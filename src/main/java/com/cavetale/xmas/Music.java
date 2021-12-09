package com.cavetale.xmas;

import com.cavetale.mytems.item.music.Melody;
import com.cavetale.mytems.item.music.MelodyBuilder;
import com.cavetale.mytems.item.music.Semitone;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Instrument;
import org.bukkit.Note.Tone;
import static com.cavetale.mytems.item.music.Semitone.*;
import static org.bukkit.Instrument.*;
import static org.bukkit.Note.Tone.*;

public enum Music {
    GRINCH(DIDGERIDOO, 100L,
           Map.of(),
           b -> b
           .beat(0, F, SHARP, 0).beat(2, F, SHARP, 1)
           .beat(0, G, 0).beat(2, G, 1)
           .beat(0, G, SHARP, 0).beat(2, G, SHARP, 1)
           .beat(0, A, 1).beat(2, A, 0)),

    DECK_THE_HALLS(BELL, 50L,
                   Map.of(B, FLAT),
                   b -> b
                   .beat(6, C, 1)
                   .beat(2, B, 1)
                   .beat(4, A, 1)
                   .beat(4, G, 1)

                   .beat(4, F, 0)
                   .beat(4, G, 1)
                   .beat(4, A, 1)
                   .beat(4, F, 0)

                   .beat(2, G, 1)
                   .beat(2, A, 1)
                   .beat(2, B, 1)
                   .beat(2, G, 1)
                   .beat(6, A, 1)
                   .beat(2, G, 1)

                   .beat(4, F, 0)
                   .beat(4, E, 0)
                   .beat(8, F, 0)

                   .extra(CHIME)
                   .beat(0, A, 1).beat(6, C, 1)
                   .beat(0, G, 1).beat(2, B, 1)
                   .beat(0, F, 0).beat(4, A, 1)
                   .beat(0, C, 0).beat(4, G, 1)

                   .beat(0, A, 0).beat(4, F, 0)
                   .beat(0, C, 0).beat(4, G, 1)
                   .beat(0, F, 0).beat(4, A, 1)
                   .beat(0, A, 0).beat(4, F, 0)

                   .beat(0, D, 0).beat(2, G, 1)
                   .beat(2, A, 1)
                   .beat(0, D, 0).beat(2, B, 1)
                   .beat(2, G, 1)
                   .beat(0, F, 0).beat(0, C, 0).beat(4, A, 1)
                   .beat(2, D, 0)
                   .beat(2, G, 1)

                   .beat(0, A, 0).beat(4, F, 0)
                   .beat(0, C, 0).beat(2, E, 0)
                   .beat(2, B, 0)
                   .beat(0, A, 0).beat(8, F, 0)

                   .parent().extra(GUITAR)
                   .beat(4, F, 1).beat(4, F, 1).beat(4, F, 1).beat(4, C, 1)
                   .beat(4, F, 1).beat(4, C, 1).beat(8, F, 1)
                   .beat(4, B, 1).beat(4, G, 1).beat(4, C, 1).beat(4, B, 1)
                   .beat(4, C, 1).beat(4, C, 0).beat(8, F, 1)),

    MERRY_CHRISTMAS(PIANO, 100L,
                    Map.of(),
                    b -> b
                    .beat(4, G, 1) // We

                    .beat(4, C, 1) // wish
                    .beat(2, C, 1) // you
                    .beat(2, D, 1) // a
                    .beat(2, C, 1) // mer-
                    .beat(2, B, 1) // ry

                    .beat(4, A, 1) // Christ-
                    .beat(4, A, 1) // mas,
                    .beat(4, A, 1) // we

                    .beat(4, D, 1) // wish
                    .beat(2, D, 1) // you
                    .beat(2, E, 1) // a
                    .beat(2, D, 1) // mer-
                    .beat(2, C, 1) // ry

                    .beat(4, B, 1) // Christ-
                    .beat(4, G, 1) // mas,
                    .beat(4, G, 1) // we

                    .beat(4, E, 1) // wish
                    .beat(2, E, 1) // you
                    .beat(2, F, 1) // a
                    .beat(2, E, 1) // mer-
                    .beat(2, D, 1) // ry

                    .beat(4, C, 1) // Christ-
                    .beat(4, A, 1) // mas
                    .beat(2, G, 1) // and
                    .beat(2, G, 1) // a

                    .beat(4, A, 1) // happ-
                    .beat(4, D, 1) // py
                    .beat(4, B, 1) // New

                    .beat(8, C, 1)), // Year.

    JINGLE_BELLS(PIANO, 50L,
                 Map.of(F, SHARP),
                 b -> b
                 // Line 1
                 .beat(4, D, 0).beat(4, B, 1).beat(4, A, 1).beat(4, G, 1)
                 .beat(12, D, 0).beat(2, D, 0).beat(2, D, 0)
                 .beat(4, D, 0).beat(4, B, 1).beat(4, A, 1).beat(4, G, 1)
                 .beat(16, E, 0)
                 // Line 2
                 .beat(4, E, 0).beat(4, C, 1).beat(4, B, 1).beat(4, A, 1)
                 .beat(16, F, 0)
                 .beat(4, D, 1).beat(4, D, 1).beat(4, C, 1).beat(4, A, 1)
                 .beat(16, B, 1)
                 // Line 3
                 .beat(4, D, 0).beat(4, B, 1).beat(4, A, 1).beat(4, G, 1)
                 .beat(16, D, 0)
                 .beat(4, D, 0).beat(4, B, 1).beat(4, A, 1).beat(4, G, 1)
                 .beat(12, E, 0)
                 .beat(12, E, 0).beat(4, E, 0)
                 // Line 4
                 .beat(4, E, 0).beat(4, B, 1).beat(4, A, 1).beat(4, G, 1)
                 .beat(4, D, 1).beat(4, D, 1).beat(4, D, 1).beat(4, D, 1)
                 .beat(4, F, 1).beat(4, D, 1).beat(4, C, 1).beat(4, A, 1)
                 .beat(8, G, 1)
                 .pause(8)
                 // Line 5
                 .beat(4, B, 1).beat(4, B, 1).beat(8, B, 1)
                 .beat(4, B, 1).beat(4, B, 1).beat(8, B, 1)
                 .beat(4, B, 1).beat(4, D, 1).beat(6, G, 1).beat(2, A, 1)
                 .beat(16, B, 1)
                 // Line 6
                 .beat(4, C, 1).beat(4, C, 1).beat(6, C, 1).beat(2, C, 1)
                 .beat(4, C, 1).beat(4, B, 1).beat(4, B, 1).beat(2, B, 1).beat(2, B, 1)
                 .beat(4, B, 1).beat(4, A, 1).beat(4, A, 1).beat(4, B, 1)
                 .beat(8, A, 1).beat(8, D, 1)
                 // Line 7
                 .beat(4, B, 1).beat(4, B, 1).beat(8, B, 1)
                 .beat(4, B, 1).beat(4, B, 1).beat(8, B, 1)
                 .beat(4, B, 1).beat(4, D, 1).beat(6, G, 1).beat(2, A, 1)
                 .beat(16, B, 1)
                 // Line 8
                 .beat(4, C, 1).beat(4, C, 1).beat(6, C, 1).beat(2, C, 1)
                 .beat(4, C, 1).beat(4, B, 1).beat(4, B, 1).beat(2, B, 1).beat(2, B, 1)
                 .beat(4, D, 1).beat(4, D, 1).beat(4, C, 1).beat(4, A, 1)
                 .beat(16, G, 1)),

    RUDOLPH(Instrument.PIANO, 50L, Map.of(), b -> b
            .beat(2, G, 1).beat(4, A, 1).beat(2, G, 1).beat(4, E, 0).beat(4, C, 1)
            .beat(4, A, 1).beat(0, E, 0).beat(12, G, 1)
            .beat(2, G, 1).beat(2, A, 1).beat(2, G, 1).beat(2, A, 1).beat(4, G, 1).beat(4, C, 1)
            .beat(0, F, 0).beat(16, B, 1)
            .beat(2, F, 0).beat(4, G, 1).beat(2, F, 0).beat(4, D, 0).beat(4, B, 1)
            .beat(4, A, 1).beat(12, G, 1)
            // Line 2
            .beat(2, G, 1).beat(2, A, 1).beat(2, G, 1).beat(2, A, 1).beat(4, G, 1).beat(4, A, 1)
            .beat(16, E, 0)
            .beat(2, G, 1).beat(4, A, 1).beat(2, G, 1).beat(4, E, 0).beat(4, C, 1)
            .beat(4, A, 1).beat(12, G, 1)
            .beat(2, G, 1).beat(2, A, 1).beat(2, G, 1).beat(2, A, 1).beat(4, G, 1).beat(4, C, 1)
            .beat(0, F, 0).beat(16, B, 1)
            // Line 3
            .beat(2, F, 0).beat(4, G, 1).beat(2, F, 0).beat(4, D, 0).beat(4, B, 1)
            .beat(4, A, 1).beat(12, G, 1)
            .beat(2, G, 1).beat(2, A, 1).beat(2, G, 1).beat(2, A, 1).beat(4, G, 1).beat(4, D, 1)
            .beat(0, E, 0).beat(16, C, 1)
            .beat(4, A, 1).beat(4, A, 1).beat(4, C, 1).beat(4, A, 1)
            .beat(4, G, 1).beat(4, E, 0).beat(8, G, 1)
            // Line 4
            .beat(4, F, 0).beat(4, A, 1).beat(4, G, 1).beat(4, F, 0)
            .beat(0, C, 0).beat(16, E, 0)
            .beat(4, D, 0).beat(4, E, 0).beat(4, G, 1).beat(4, A, 1)
            .beat(4, B, 1).beat(4, B, 1).beat(8, B, 1)
            .beat(4, D, 1).beat(4, C, 1).beat(4, B, 1).beat(4, A, 1)
            .beat(4, G, 1).beat(4, F, 0).beat(8, D, 0)
            .beat(2, G, 1).beat(4, A, 1).beat(2, G, 1).beat(4, E, 0).beat(4, C, 1)
            // Line 5
            .beat(4, A, 1).beat(0, E, 0).beat(12, G, 1)
            .beat(2, G, 1).beat(2, A, 1).beat(2, G, 1).beat(2, A, 1).beat(4, G, 1).beat(4, C, 1)
            .beat(0, F, 0).beat(16, B, 1)
            .beat(2, F, 0).beat(4, G, 1).beat(2, F, 0).beat(4, D, 0).beat(4, B, 1)
            .beat(4, A, 1).beat(12, G, 1)
            .beat(2, G, 1).beat(2, A, 1).beat(2, G, 1).beat(2, A, 1).beat(4, G, 1).beat(4, D, 1)
            .beat(0, E, 0).beat(16, C, 1)
            // Left Hand
            .extra(Instrument.GUITAR)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            // Line 2
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(8, C, 0)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            // Line 3
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(8, C, 0)
            .beat(16, F, 0)
            .beat(12, C, 0).beat(4, C, SHARP, 0)
            // Line 4
            .beat(8, D, 0).beat(8, G, 1)
            .beat(0, C, 0).beat(16, G, 1)
            .beat(16, G, 0)
            .beat(12, G, 1).beat(4, G, SHARP, 1)
            .beat(0, D, 0).beat(16, A, 1)
            .beat(0, G, 1).beat(16, B, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            // Line 6
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(4, C, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, D, 0).beat(4, G, 1).beat(4, G, 0).beat(4, G, 1)
            .beat(4, C, 0).beat(4, G, 1).beat(8, C, 0)),

    TANNENBAUM(PLING, 100L, Map.of(B, FLAT), b -> b
                 .beat(2, C, 0) // O

                 .beat(3, F, 0) // Tan
                 .beat(1, F, 0) // nen
                 .beat(3, F, 0) // baum,
                 .beat(2, G, 1) // o

                 .beat(3, A, 1) // Tan
                 .beat(1, A, 1) // nen
                 .beat(3, A, 1) // baum,
                 .beat(2, A, 1) // wie

                 .beat(2, G, 1) // treu
                 .beat(2, A, 1) // sind
                 .beat(4, B, 1) // dei
                 .beat(4, E, 0) // ne

                 .beat(4, G, 1) // Blaet
                 .beat(4, F, 0) // ter
                 .pause(4)
                 .beat(2, C, 1) // Du

                 .beat(2, C, 1) // gruenst
                 .beat(2, A, 1) // nicht
                 .beat(6, D, 1) // nur
                 .beat(2, C, 1) // zur

                 .beat(2, C, 1) // Som
                 .beat(2, B, 1) // mer
                 .beat(6, B, 1) // zeit,
                 .beat(2, B, 1) // nein,

                 .beat(2, B, 1) // auch
                 .beat(2, G, 1) // im
                 .beat(6, C, 1) // Win
                 .beat(2, B, 1) // ter,

                 .beat(2, B, 1) // wenn
                 .beat(2, A, 1) // es
                 .beat(4, A, 1) // schneit.
                 .pause(4)
                 .beat(2, C, 0) // O

                 .beat(3, F, 0) // Tan
                 .beat(1, F, 0) // nen
                 .beat(3, F, 0) // baum,
                 .beat(2, G, 1) // o

                 .beat(3, A, 1) // Tan
                 .beat(1, A, 1) // nen
                 .beat(3, A, 1) // baum,
                 .beat(2, A, 1) // wie

                 .beat(2, G, 1) // treu
                 .beat(2, A, 1) // sind
                 .beat(4, B, 1) // dei
                 .beat(4, E, 0) // ne

                 .beat(4, G, 1) // Blaet
                 .beat(4, F, 0)); // ter

    public final Instrument instrument;
    public final long speed;
    public final Map<Tone, Semitone> keys;
    public final Melody melody;

    Music(final Instrument instrument, final long speed, final Map<Tone, Semitone> keys, final Consumer<MelodyBuilder> build) {
        this.instrument = instrument;
        this.speed = speed;
        this.keys = keys;
        MelodyBuilder builder = Melody.builder(instrument, speed).keys(keys);
        build.accept(builder);
        this.melody = builder.build();
    }
}
