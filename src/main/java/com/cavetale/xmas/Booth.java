package com.cavetale.xmas;

import com.cavetale.xmas.attraction.Attraction;
import com.cavetale.xmas.attraction.AttractionType;
import com.cavetale.xmas.attraction.FindBunnyAttraction;
import com.cavetale.xmas.attraction.MusicHeroAttraction;
import com.cavetale.xmas.attraction.PetPileAttraction;
import com.cavetale.xmas.attraction.PosterAttraction;
import com.cavetale.xmas.attraction.RepeatMelodyAttraction;
import java.time.Duration;
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
                  a -> ((RepeatMelodyAttraction) a).set(Instrument.BANJO, 0)),
    TRAZAO_RUDOLPH_HERO(AttractionType.MUSIC_HERO, 7,
                        Component.text("Rudolph"),
                        Component.text("Learn a song about the reindeer with a red shiny nose?"),
                        XmasPresent.ARMCHAIR,
                        a -> ((MusicHeroAttraction) a).setMusic(Music.RUDOLPH)),
    HOTEL_BUNNIES(AttractionType.FIND_BUNNY, 8,
                  Component.text("Home Alone with Bunnies"),
                  Component.text("My manor is overrun with bunnies,"
                                 + " and they are scaring my guests!"),
                  XmasPresent.CHRISTMAS_BALL, null),
    SANTA_HAT_MELODY(AttractionType.REPEAT_MELODY, 9,
                     Component.text("Musical Balcony"),
                     Component.text("Oh hi, friend!"
                                    + " Looking for some musical exercise?"),
                     XmasPresent.SNOW_SHOVEL,
                     a -> ((RepeatMelodyAttraction) a).set(Instrument.BELL, 0)),
    TANNENBAUM_HERO(AttractionType.MUSIC_HERO, 10,
                    Component.text("O Tannenbaum"),
                    Component.text("Learn this song about the Christmas Tree?"),
                    XmasPresent.PRESENT,
                    a -> ((MusicHeroAttraction) a).setMusic(Music.TANNENBAUM)),
    ADIS_BUNNIES(AttractionType.FIND_BUNNY, 11,
                 Component.text("Bunny Mansion"),
                 Component.text("There are bunnies everywhere! Please help!!!"),
                 XmasPresent.SANTAS_LIST,
                 a -> ((FindBunnyAttraction) a).setSearchTime(Duration.ofSeconds(80))),
    BEAMY_COTTAGE_MUSIC(AttractionType.REPEAT_MELODY, 12,
                        Component.text("Cottage Jam Session"),
                        Component.text("A good melody is sweet like a sugar cane."
                                       + " Repeat after me!"),
                        XmasPresent.ONION,
                        a -> ((RepeatMelodyAttraction) a).set(Instrument.COW_BELL, 0)),
    FROEHLICHE(AttractionType.MUSIC_HERO, 13,
               Component.text("O Du Fröhliche"),
               Component.text("This is one of my favorites! Do you speak German?"),
               XmasPresent.DODO_EGG,
               a -> ((MusicHeroAttraction) a).setMusic(Music.O_DU_FROEHLICHE)),
    DMS_BUNNIES(AttractionType.FIND_BUNNY, 14,
                Component.text("Carrot Enthusiast"),
                Component.text("This guy over there filled my house with carrots."
                               + " It attracts the bunnies. Can you catch them?"),
                XmasPresent.SCARY_PUMPKIN,
                a -> ((FindBunnyAttraction) a).setSearchTime(Duration.ofSeconds(60))),
    BLACKOUT_POSTER(AttractionType.POSTER, 15,
                    Component.text("Mosaic"),
                    Component.text("My drawing got all messed up. Can you put it in the right order?"),
                    XmasPresent.CHOCOLATE,
                    a -> ((PosterAttraction) a).setPoster("XmasBlackoutPoster")),
    SHEEP_CAT_PILE(AttractionType.PET_PILE, 16,
                   Component.text("Sorting Cats"),
                   Component.text("Quick, close the doors! I need to sort out my cats."
                                  + " Can you find the one I'm looking for?"),
                   XmasPresent.PALETTE,
                   a -> ((PetPileAttraction) a).setCats()),
    SNOWMAN_TOWER(AttractionType.SNOWBALL_FIGHT, 17,
                  Component.text("Snowman Tower"),
                  Component.text("Snowmen in this tower taunt everyone with their snowball"
                                 + " skills. Can you hit them all without being hit?"),
                  XmasPresent.GLOBE, null),
    SILENT_NIGHT_HERO(AttractionType.MUSIC_HERO, 18,
                      Component.text("Silent Night"),
                      Component.text("This Christmas song is so relaxing..."),
                      XmasPresent.TRAFFIC_LIGHT,
                      a -> ((MusicHeroAttraction) a).setMusic(Music.SILENT_NIGHT)),
    EASTER_POSTER(AttractionType.POSTER, 19,
                  Component.text("Picture Puzzle"),
                  Component.text("My Family Picture got all scrambled up."
                                 + " Can you put it in order?"),
                  XmasPresent.GOBLET,
                  a -> ((PosterAttraction) a).setPoster("XmasEasterPoster")),
    UKZ_BUNNIES(AttractionType.FIND_BUNNY, 20,
                Component.text("Bunnies, Back to Bed"),
                Component.text("It's bedtime for my bunnies. Please catch them all!"),
                XmasPresent.SHADES, null),
    CAT_PILE_HUT(AttractionType.PET_PILE, 21,
                 Component.text("Kittens in the Hut"),
                 Component.text("Close the door and help me sort out my cats!"
                                + " I need to give them a bath..."),
                 XmasPresent.CLAMSHELL,
                 a -> ((PetPileAttraction) a).setCats()),
    KINDERLEIN_HERO(AttractionType.MUSIC_HERO, 22,
                    Component.text("Childrens' Song"),
                    Component.text("All aboard the Christmas train!"),
                    XmasPresent.LOLLY,
                    a -> ((MusicHeroAttraction) a).setMusic(Music.KINDERLEIN));

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
