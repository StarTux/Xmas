package com.cavetale.xmas;

import com.cavetale.mytems.Mytems;
import com.cavetale.xmas.attraction.Attraction;
import com.cavetale.xmas.attraction.AttractionType;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;

/**
 * Static information for Attractions.
 */
public enum Booth {
    MOUNTAIN_HERO(AttractionType.MUSIC_HERO, 1,
                  null, // DisplayName
                  null, // Description
                  XmasPresent.PAINTBRUSH,
                  null);

    public final String name; // Corresponds with area.name
    public final int dayOfChristmas;
    public final AttractionType type;
    public final Component displayName;
    public final Component description;
    public final XmasPresent xmasPresent;
    public final Consumer<Attraction> consumer;

    Booth(final AttractionType type,
          final int dayOfChristmas,
          final Component displayName,
          final Component description,
          final XmasPresent xmasPresent,
          final Consumer<Attraction> consumer) {
        this.name = Stream.of(name().split("_"))
            .map(s -> s.substring(0, 1) + s.substring(1).toLowerCase())
            .collect(Collectors.joining(""));
        this.type = type;
        this.dayOfChristmas = dayOfChristmas;
        this.displayName = displayName;
        this.description = description;
        this.xmasPresent = xmasPresent;
        this.consumer = consumer;
    }

    public static Booth forName(String n) {
        for (Booth booth : Booth.values()) {
            if (n.equals(booth.name)) return booth;
        }
        return null;
    }
}
