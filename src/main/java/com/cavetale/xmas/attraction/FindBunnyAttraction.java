package com.cavetale.xmas.attraction;

import com.cavetale.area.struct.Cuboid;
import com.cavetale.area.struct.Vec3i;
import com.cavetale.core.font.Unicode;
import com.cavetale.xmas.Booth;
import com.cavetale.xmas.XmasPlugin;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class FindBunnyAttraction extends Attraction<FindBunnyAttraction.SaveTag> {
    @Setter protected Duration searchTime = Duration.ofSeconds(40);
    protected static final int MAX_BUNNIES = 10;
    protected final Set<Vec3i> possibleBunnyBlocks;
    protected Rabbit currentBunny;
    protected int secondsLeft;
    protected static final List<Rabbit.Type> rabbitTypes = List.of(Rabbit.Type.BLACK,
                                                                   Rabbit.Type.BLACK_AND_WHITE,
                                                                   Rabbit.Type.BROWN,
                                                                   Rabbit.Type.GOLD,
                                                                   Rabbit.Type.SALT_AND_PEPPER,
                                                                   Rabbit.Type.WHITE);

    protected FindBunnyAttraction(final XmasPlugin plugin, final String name, final List<Cuboid> areaList, final Booth booth) {
        super(plugin, name, areaList, booth, SaveTag.class, SaveTag::new);
        Set<Vec3i> bunnySet = new HashSet<>();
        for (Cuboid cuboid : areaList) {
            if ("bunny".equals(cuboid.name)) {
                bunnySet.addAll(cuboid.enumerate());
            }
        }
        this.possibleBunnyBlocks = Set.copyOf(bunnySet);
        this.displayName = Component.text("Missing Bunnies", NamedTextColor.DARK_RED);
        this.description = Component.text("My bunnies escaped! Please find them quicky.");
    }

    @Override
    public boolean isPlaying() {
        return saveTag.state != State.IDLE;
    }

    @Override
    protected void start(Player player) {
        saveTag.currentPlayer = player.getUniqueId();
        makeBunnyBlocks();
        startingGun(player);
        changeState(State.SEARCH);
    }

    @Override
    protected void stop() {
        changeState(State.IDLE);
    }

    @Override
    public void onTick() {
        State newState = saveTag.state.tick(this);
        if (newState != null) changeState(newState);
    }

    @Override
    public void onLoad() {
        if (saveTag.state == State.SEARCH) {
            spawnBunny();
        }
    }

    @Override
    public void onDisable() {
        clearBunny();
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().equals(currentBunny)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (saveTag.state != State.SEARCH) return;
        Player player = getCurrentPlayer();
        if (player == null) return;
        if (event.getEntity().equals(currentBunny) && event.getDamager().equals(player)) {
            bunnyFound(player);
        }
    }

    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (saveTag.state != State.SEARCH) return;
        Player player = getCurrentPlayer();
        if (player == null) return;
        if (event.getRightClicked().equals(currentBunny) && event.getPlayer().equals(player)) {
            bunnyFound(player);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (saveTag.state != State.SEARCH) return;
        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK:
        case LEFT_CLICK_BLOCK:
            break;
        default: return;
        }
        Player player = getCurrentPlayer();
        if (player == null) return;
        Vec3i bunnyBlock = saveTag.bunnyBlocks.get(saveTag.bunnyBlockIndex);
        if (event.getPlayer().equals(player) && Vec3i.of(event.getClickedBlock()).equals(bunnyBlock)) {
            bunnyFound(player);
        }
    }

    public void bunnyFound(Player player) {
        confetti(player, currentBunny.getLocation().add(0, currentBunny.getHeight() * 0.5, 0));
        clearBunny();
        saveTag.bunnyBlockIndex += 1;
        if (saveTag.bunnyBlockIndex >= saveTag.bunnyBlocks.size()) {
            perfectCompletion(player, true);
            changeState(State.IDLE);
        } else {
            progress(player);
            player.sendActionBar(makeProgressComponent(secondsLeft, Unicode.HEART.string,
                                                       saveTag.bunnyBlockIndex + 1, saveTag.bunnyBlocks.size()));
            changeState(State.SEARCH);
        }
    }

    protected void makeBunnyBlocks() {
        List<Vec3i> bunnyBlocks = new ArrayList<>(possibleBunnyBlocks);
        World w = plugin.getWorld();
        bunnyBlocks.removeIf(it -> {
                Block block = it.toBlock(w);
                if (block.isEmpty()) return false;
                Material mat = block.getType();
                if (Tag.CARPETS.isTagged(mat)) return false;
                if (block.getType() == Material.COBWEB) return false;
                return true;
            });
        Collections.shuffle(bunnyBlocks);
        saveTag.bunnyBlocks = new ArrayList<>(bunnyBlocks.subList(0, Math.min(MAX_BUNNIES, bunnyBlocks.size())));
        saveTag.bunnyBlockIndex = 0;
    }

    protected void spawnBunny() {
        Vec3i vector = saveTag.bunnyBlocks.get(saveTag.bunnyBlockIndex);
        World w = plugin.getWorld();
        currentBunny = plugin.getWorld().spawn(vector.toLocation(w), Rabbit.class, b -> {
                b.setPersistent(false);
                b.setRemoveWhenFarAway(false);
                b.setGravity(false);
                b.setSilent(true);
                b.setRabbitType(rabbitTypes.get(random.nextInt(rabbitTypes.size())));
                Bukkit.getMobGoals().removeAllGoals(b);
            });
        currentBunny.setMetadata("nomap", new FixedMetadataValue(plugin, true));
    }

    protected void clearBunny() {
        if (currentBunny != null) {
            currentBunny.remove();
            currentBunny = null;
        }
    }

    protected State tickSearch() {
        Player player = getCurrentPlayer();
        if (player == null || currentBunny == null) return State.IDLE;
        long now = System.currentTimeMillis();
        long timeout = saveTag.searchStarted + searchTime.toMillis();
        if (now > timeout) {
            timeout(player);
            return State.IDLE;
        }
        int seconds = (int) ((timeout - now - 1) / 1000L) + 1;
        if (seconds != secondsLeft) {
            secondsLeft = seconds;
            player.sendActionBar(makeProgressComponent(seconds, Unicode.HEART.string,
                                                       saveTag.bunnyBlockIndex + 1, saveTag.bunnyBlocks.size()));
            currentBunny.getWorld().playSound(currentBunny.getLocation(), Sound.ENTITY_RABBIT_HURT, SoundCategory.MASTER, 1.0f, 0.5f);
        }
        Location location = currentBunny.getLocation();
        location.setYaw(location.getYaw() + 18.0f);
        currentBunny.teleport(location);
        return null;
    }

    protected void changeState(State newState) {
        State oldState = saveTag.state;
        saveTag.state = newState;
        oldState.exit(this);
        newState.enter(this);
    }

    enum State {
        IDLE,
        SEARCH {
            @Override protected State tick(FindBunnyAttraction instance) {
                return instance.tickSearch();
            }

            @Override protected void enter(FindBunnyAttraction instance) {
                instance.saveTag.searchStarted = System.currentTimeMillis();
                instance.spawnBunny();
            }

            @Override protected void exit(FindBunnyAttraction instance) {
                instance.clearBunny();
            }
        };

        protected void enter(FindBunnyAttraction instance) { }

        protected void exit(FindBunnyAttraction instance) { }

        protected State tick(FindBunnyAttraction instance) {
            return null;
        }
    }

    protected static final class SaveTag extends Attraction.SaveTag {
        protected State state = State.IDLE;
        protected List<Vec3i> bunnyBlocks;
        protected int bunnyBlockIndex;
        protected long searchStarted;
    }
}
