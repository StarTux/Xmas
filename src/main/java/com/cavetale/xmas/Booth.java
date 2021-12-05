package com.cavetale.xmas;

import com.cavetale.xmas.attraction.Attraction;
import com.cavetale.xmas.attraction.AttractionType;
import com.cavetale.xmas.attraction.MusicHeroAttraction;
import com.cavetale.xmas.attraction.RepeatMelodyAttraction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;

/**
 * Static information for Attractions.
 */
public enum Booth {
    MERRY_CHRISTMAS_HERO(AttractionType.MUSIC_HERO, 1,
                         Component.text("Merry Christmas"),
                         Component.text("I'll teach you my favorite Christmas song!"),
                         XmasPresent.PAINTBRUSH,
                         a -> ((MusicHeroAttraction) a).setMusic(Music.MERRY_CHRISTMAS)),
    DOKIEE_BUNNY(AttractionType.FIND_BUNNY, 2,
                 Component.text("Bunny Escape"),
                 Component.text("My bunnies got out and are all over the house!"),
                 XmasPresent.BROOM, null),
    CCRIS_MUSIC(AttractionType.REPEAT_MELODY, 3,
                Component.text("Simon Says"),
                Component.text("Play the notes right after me!"),
                XmasPresent.DICE,
                a -> ((RepeatMelodyAttraction) a).set(Instrument.CHIME, 0)),
    SHEEP_JINGLE_BELLS_MUSIC(AttractionType.MUSIC_HERO, 4,
                             Component.text("Jingle Bells"),
                             Component.text("Jingle All the Way! Wanna learn a new song?"),
                             XmasPresent.WARM_SOCKS,
                             a -> ((MusicHeroAttraction) a).setMusic(Music.JINGLE_BELLS)),
    GNOME_BUNNY(AttractionType.FIND_BUNNY, 5,
                Component.text("Rabbit Invasion"),
                Component.text("Help! I'm scared of rabbits, but they got into my house."
                               + " They're just everywhere!"),
                XmasPresent.DIAMOND_RING, null),
    PIRATE_MELODY(AttractionType.REPEAT_MELODY, 6,
                  Component.text("Music Piracy"),
                  Component.text("Repeat after me, but don't steal my music!"),
                  XmasPresent.KNITTED_HAT,
                  a -> ((RepeatMelodyAttraction) a).set(Instrument.BANJO, 0));

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
