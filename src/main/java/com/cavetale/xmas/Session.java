package com.cavetale.xmas;

import com.cavetale.core.util.Json;
import com.cavetale.xmas.attraction.Attraction;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public final class Session {
    protected final XmasPlugin plugin;
    protected final UUID uuid;
    protected final String name;
    protected final File saveFile;
    protected Tag tag;
    protected XmasPresent lastClickedPresent;

    protected Session(final XmasPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.saveFile = new File(plugin.getPlayersFolder(), uuid + ".json");
    }

    protected void load() {
        this.tag = Json.load(saveFile, Tag.class, Tag::new);
    }

    public void save() {
        Json.save(saveFile, tag, true);
    }

    public Duration getCooldown(Attraction attraction) {
        Long cd = tag.cooldowns.get(attraction.getName());
        if (cd == null) return null;
        long now = System.currentTimeMillis();
        if (now > cd) {
            tag.cooldowns.remove(attraction.getName());
            return null;
        }
        return Duration.ofMillis(cd - now);
    }

    public void setCooldown(Attraction attraction, Duration duration) {
        tag.cooldowns.put(attraction.getName(), duration.toMillis() + System.currentTimeMillis());
    }

    @Data
    public static final class Tag {
        protected final Map<String, Long> cooldowns = new HashMap<>();
        protected final Set<String> completed = new HashSet<>();
        protected final Map<String, RewardType> storedRewards = new HashMap<>();
        protected final Set<XmasPresent> presentsGiven = new HashSet<>();
        protected final List<XmasPresent> presentList = new ArrayList<>();
        protected int keys;
        protected int doorsOpened;
    }
}
