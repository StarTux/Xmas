package com.cavetale.xmas.attraction;

import com.cavetale.area.struct.Area;
import com.cavetale.core.font.Unicode;
import com.cavetale.core.struct.Vec3i;
import com.cavetale.mytems.Mytems;
import com.cavetale.mytems.event.music.PlayerBeatEvent;
import com.cavetale.mytems.event.music.PlayerCloseMusicalInstrumentEvent;
import com.cavetale.mytems.event.music.PlayerMelodyCompleteEvent;
import com.cavetale.mytems.event.music.PlayerOpenMusicalInstrumentEvent;
import com.cavetale.mytems.item.font.Glyph;
import com.cavetale.mytems.item.music.Beat;
import com.cavetale.mytems.item.music.Melody;
import com.cavetale.mytems.item.music.Semitone;
import com.cavetale.xmas.Booth;
import com.cavetale.xmas.Music;
import com.cavetale.xmas.XmasPlugin;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.Note.Tone;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public final class MusicHeroAttraction extends Attraction<MusicHeroAttraction.SaveTag> {
    protected static final Duration WARMUP_TIME = Duration.ofSeconds(30);
    protected int secondsLeft;
    @Setter protected Music music = Music.JINGLE_BELLS;
    protected Vec3i lecternBlock = null;
    protected ItemStack melodyBook;

    protected MusicHeroAttraction(final XmasPlugin plugin, final String name, final List<Area> areaList, final Booth booth) {
        super(plugin, name, areaList, booth, SaveTag.class, SaveTag::new);
        for (Area area : areaList) {
            if ("lectern".equals(area.name)) {
                lecternBlock = area.min;
            }
        }
        this.displayName = Component.text("Music Hero", NamedTextColor.RED);
        this.description = Component.text("Play the notes while they're on your instrument."
                                          + " Don't miss a single note for the prize!");
        this.doesRequireInstrument = true;
    }

    @Override
    public boolean isPlaying() {
        return saveTag.state != State.IDLE;
    }

    @Override
    public void start(Player player) {
        saveTag.currentPlayer = player.getUniqueId();
        startingGun(player);
        changeState(State.WARMUP);
        if (lecternBlock != null && lecternBlock.toBlock(plugin.getWorld()).isEmpty()) {
            lecternBlock.toBlock(plugin.getWorld()).setType(Material.LECTERN);
        }
    }

    @Override
    protected void stop() {
        changeState(State.IDLE);
    }

    @Override
    public void onTick() {
        if (saveTag.state == State.IDLE) return;
        Player player = getCurrentPlayer();
        if (player == null) return;
        State newState = saveTag.state.tick(this, player);
        if (newState != null) changeState(newState);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK: break;
        default: return;
        }
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        if (block.getType() == Material.LECTERN || Vec3i.of(block).equals(lecternBlock)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            Player player = event.getPlayer();
            if (melodyBook == null) makeMelodyBook();
            player.openBook(melodyBook);
        }
    }

    public void onPlayerOpenMusicalInstrument(PlayerOpenMusicalInstrumentEvent event) {
        Player player = getCurrentPlayer();
        if (saveTag.state != State.IDLE && Objects.equals(player, event.getPlayer())) {
            List<Beat> beats = new ArrayList<>(music.melody.getBeats());
            beats.removeIf(b -> b.instrument != null || b.ticks == 0 || b.isPause());
            List<Long> ticks = new ArrayList<>(beats.size());
            for (Beat beat : beats) ticks.add((long) beat.ticks);
            Collections.sort(ticks);
            long median = ticks.get(ticks.size() / 2);
            long desired = 900L;
            long speed = ((desired - 1) / median) + 1;
            event.setHeroMelody(new Melody(music.melody.getInstrument(), music.keys, beats, speed, null));
            changeState(State.PLAY);
        }
    }

    public void onPlayerCloseMusicalInstrument(PlayerCloseMusicalInstrumentEvent event) {
        Player player = getCurrentPlayer();
        if (saveTag.state == State.PLAY && Objects.equals(player, event.getPlayer()) && !saveTag.completed) {
            fail(player);
            stop();
        }
    }

    public void onPlayerBeat(PlayerBeatEvent event) { }

    public void onPlayerMelodyComplete(PlayerMelodyCompleteEvent event) {
        Player player = getCurrentPlayer();
        if (saveTag.state != State.PLAY || !Objects.equals(player, event.getPlayer())) return;
        saveTag.completed = true;
        int finalScore = event.getScore();
        int maximumScore = event.getMaxScore();
        if (finalScore >= maximumScore) {
            player.closeInventory();
            music.melody.play(plugin, player.getLocation());
            perfectCompletion(player, false);
        } else {
            player.closeInventory();
            player.showTitle(Title.title(Component.text(finalScore + "/" + maximumScore, NamedTextColor.DARK_RED),
                                         Component.text("Try again!", NamedTextColor.DARK_RED)));
            plugin.sessionOf(player).setCooldown(this, Duration.ofSeconds(20));
        }
        changeState(State.IDLE);
    }

    protected State tickWarmup(Player player) {
        long now = System.currentTimeMillis();
        long timeout = saveTag.warmupStarted + WARMUP_TIME.toMillis();
        if (now > timeout) {
            timeout(player);
            return State.IDLE;
        }
        int seconds = (int) ((timeout - now - 1) / 1000L) + 1;
        if (seconds != secondsLeft) {
            secondsLeft = seconds;
            player.sendActionBar(Component.join(JoinConfiguration.noSeparators(), new Component[] {
                        Component.text(Unicode.WATCH.string + seconds, NamedTextColor.GOLD),
                        Component.space(),
                        Mytems.ANGELIC_HARP.component,
                        Component.text("Open your Instrument", NamedTextColor.WHITE),
                    }));
        }
        return null;
    }

    protected State tickPlay(Player player) {
        return null;
    }

    protected void changeState(State newState) {
        State oldState = saveTag.state;
        saveTag.state = newState;
        oldState.exit(this);
        newState.enter(this);
    }

    protected void makeMelodyBook() {
        List<Component> keys = new ArrayList<>();
        for (Tone tone : Tone.values()) {
            Semitone semitone = music.keys.get(tone);
            if (semitone == null) continue;
            keys.add(Glyph.toComponent(tone.toString().toLowerCase() + semitone.symbol));
        }
        List<Component> notes = new ArrayList<>();
        for (Beat beat : music.melody.getBeats()) {
            if (beat.ticks == 0 || beat.isPause()) continue;
            if (beat.instrument != null) continue;
            notes.add(Component.text(beat.toString(), NamedTextColor.BLUE));
        }
        List<List<Component>> notePages = new ArrayList<>();
        final int pageLen = 72;
        for (int i = 0; i < notes.size(); i += pageLen) {
            notePages.add(List.copyOf(notes.subList(i, Math.min(i + pageLen, notes.size()))));
        }
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        List<Component> pages = new ArrayList<>();
        pages.add(!keys.isEmpty()
                  ? Component.join(JoinConfiguration.separator(Component.newline()), new Component[] {
                          Component.join(JoinConfiguration.separator(Component.space()), keys),
                          Component.empty(),
                          Component.join(JoinConfiguration.separator(Component.space()), notePages.get(0)),
                      })
                  : Component.join(JoinConfiguration.separator(Component.space()), notePages.get(0)));
        for (int i = 1; i < notePages.size(); i += 1) {
            pages.add(Component.join(JoinConfiguration.separator(Component.space()),
                                     notePages.get(i)));
        }
        book.editMeta(m -> {
                BookMeta meta = (BookMeta) m;
                meta.pages(pages);
                meta.setAuthor("Cavetale");
                meta.title(displayName);
            });
        melodyBook = book;
    }

    enum State {
        IDLE {
            @Override protected void enter(MusicHeroAttraction instance) {
                instance.saveTag = new SaveTag();
            }
        },
        WARMUP {
            @Override protected void enter(MusicHeroAttraction instance) {
                instance.saveTag.warmupStarted = System.currentTimeMillis();
            }

            @Override protected State tick(MusicHeroAttraction instance, Player player) {
                return instance.tickWarmup(player);
            }
        },
        PLAY {
            @Override protected void enter(MusicHeroAttraction instance) {
                instance.saveTag.playStarted = System.currentTimeMillis();
                instance.saveTag.completed = false;
            }

            @Override protected State tick(MusicHeroAttraction instance, Player player) {
                return instance.tickPlay(player);
            }

            @Override protected void exit(MusicHeroAttraction instance) {
            }
        };

        protected void enter(MusicHeroAttraction instance) { }

        protected void exit(MusicHeroAttraction instance) { }

        protected State tick(MusicHeroAttraction instance, Player player) {
            return null;
        }
    }

    protected static final class SaveTag extends Attraction.SaveTag {
        protected State state = State.IDLE;
        protected long playStarted;
        protected long warmupStarted;
        protected boolean completed;
    }
}
