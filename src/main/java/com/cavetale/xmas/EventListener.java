package com.cavetale.xmas;

import com.cavetale.area.struct.Vec3i;
import com.cavetale.core.event.player.PluginPlayerEvent;
import com.cavetale.magicmap.event.MagicMapCursorEvent;
import com.cavetale.magicmap.util.Cursors;
import com.cavetale.mytems.event.music.PlayerBeatEvent;
import com.cavetale.mytems.event.music.PlayerCloseMusicalInstrumentEvent;
import com.cavetale.mytems.event.music.PlayerMelodyCompleteEvent;
import com.cavetale.mytems.event.music.PlayerOpenMusicalInstrumentEvent;
import com.cavetale.resident.PluginSpawn;
import com.cavetale.xmas.attraction.Attraction;
import com.cavetale.xmas.attraction.MusicHeroAttraction;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.map.MapCursor;

@RequiredArgsConstructor
public final class EventListener implements Listener {
    private final XmasPlugin plugin;
    protected boolean enabled = true;

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    protected void onPlayerQuit(PlayerQuitEvent event) {
        for (Attraction attraction : plugin.attractionsMap.values()) {
            attraction.onPlayerQuit(event);
        }
        plugin.clearSession(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    protected void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        plugin.clearSession(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    protected void onPluginPlayer(PluginPlayerEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals(XmasPlugin.WORLD)) return;
        if (!plugin.getWorld().equals(player.getWorld())) return;
        for (Attraction attraction : plugin.attractionsMap.values()) {
            if (!attraction.isInArea(player.getLocation())) continue;
            attraction.onPluginPlayer(event);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    protected void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        for (Attraction attraction : plugin.attractionsMap.values()) {
            if (!attraction.isInArea(location)) continue;
            attraction.onEntityDamage(event);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    protected void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Location location = event.getEntity().getLocation();
        for (Attraction attraction : plugin.attractionsMap.values()) {
            if (!attraction.isInArea(location)) continue;
            attraction.onEntityDamageByEntity(event);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    protected void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Location location = event.getRightClicked().getLocation();
        for (Attraction attraction : plugin.attractionsMap.values()) {
            if (!attraction.isInArea(location)) continue;
            attraction.onPlayerInteractEntity(event);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    protected void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        if (!block.getWorld().getName().equals(XmasPlugin.WORLD)) return;
        Vec3i vec = Vec3i.of(block);
        if (vec.equals(plugin.calendarBlock)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            plugin.openCalendar(event.getPlayer());
            return;
        }
        if (vec.equals(plugin.presentBlock)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            plugin.openPresentInventory(event.getPlayer());
            return;
        }
        Location location = block.getLocation();
        for (Attraction attraction : plugin.attractionsMap.values()) {
            if (!attraction.isInArea(location)) continue;
            attraction.onPlayerInteract(event);
        }
    }

    @EventHandler
    protected void onPlayerOpenMusicalInstrument(PlayerOpenMusicalInstrumentEvent event) {
        plugin.applyActiveAttraction(MusicHeroAttraction.class, m -> m.onPlayerOpenMusicalInstrument(event));
    }

    @EventHandler
    protected void onPlayerCloseMusicalInstrument(PlayerCloseMusicalInstrumentEvent event) {
        plugin.applyActiveAttraction(MusicHeroAttraction.class, m -> m.onPlayerCloseMusicalInstrument(event));
    }

    @EventHandler
    protected void onPlayerBeat(PlayerBeatEvent event) {
        plugin.applyActiveAttraction(MusicHeroAttraction.class, m -> m.onPlayerBeat(event));
    }

    @EventHandler
    protected void onPlayerMelodyComplete(PlayerMelodyCompleteEvent event) {
        plugin.applyActiveAttraction(MusicHeroAttraction.class, m -> m.onPlayerMelodyComplete(event));
    }

    @EventHandler
    protected void onMagicMapCursor(MagicMapCursorEvent event) {
        Player player = event.getPlayer();
        if (!event.getPlayer().getWorld().getName().equals(XmasPlugin.WORLD)) return;
        Session session = plugin.sessionOf(player);
        for (Attraction attraction : plugin.attractionsMap.values()) {
            if (!attraction.isAwake()) continue;
            Vec3i vec = attraction.getNpcVector();
            if (vec == null) continue;
            if (vec.x < event.getMinX() || vec.x > event.getMaxX()) continue;
            if (vec.z < event.getMinZ() || vec.z > event.getMaxZ()) continue;
            boolean completed = session.tag.completed.contains(attraction.getName());
            RewardType storedReward = session.tag.storedRewards.getOrDefault(attraction.getName(), RewardType.NONE);
            boolean pickedUp = storedReward == RewardType.NONE;
            MapCursor.Type cursorType;
            if (completed && pickedUp) {
                cursorType = MapCursor.Type.MANSION;
            } else if (completed && !pickedUp) {
                cursorType = MapCursor.Type.RED_MARKER;
            } else {
                cursorType = MapCursor.Type.RED_X;
            }
            MapCursor mapCursor = Cursors.make(cursorType,
                                               vec.x - event.getMinX(),
                                               vec.z - event.getMinZ(),
                                               8);
            event.getCursors().addCursor(mapCursor);
        }
        for (XmasPresent xmasPresent : XmasPresent.values()) {
            int index = xmasPresent.ordinal();
            if (plugin.traderSpawns.size() <= index) break;
            PluginSpawn traderSpawn = plugin.traderSpawns.get(index);
            int x = traderSpawn.loc.getBlockX();
            int z = traderSpawn.loc.getBlockZ();
            if (x < event.getMinX() || x > event.getMaxX()) continue;
            if (z < event.getMinZ() || z > event.getMaxZ()) continue;
            MapCursor.Type cursorType = session.tag.presentsGiven.contains(xmasPresent)
                ? MapCursor.Type.SMALL_WHITE_CIRCLE
                : MapCursor.Type.WHITE_CROSS;
                MapCursor mapCursor = Cursors.make(cursorType,
                                               x - event.getMinX(),
                                               z - event.getMinZ(),
                                               8);
            event.getCursors().addCursor(mapCursor);
        }
    }
}
