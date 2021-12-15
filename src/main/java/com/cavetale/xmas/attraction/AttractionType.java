package com.cavetale.xmas.attraction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttractionType {
    FIND_BUNNY(FindBunnyAttraction.class),
    REPEAT_MELODY(RepeatMelodyAttraction.class),
    MUSIC_HERO(MusicHeroAttraction.class),
    POSTER(PosterAttraction.class),
    PET_PILE(PetPileAttraction.class);

    public final Class<? extends Attraction> type;

    public static AttractionType forName(String name) {
        for (AttractionType it : AttractionType.values()) {
            if (name.toUpperCase().equals(it.name())) return it;
        }
        return null;
    }

    public static AttractionType of(Attraction attraction) {
        for (AttractionType it : AttractionType.values()) {
            if (it.type.isInstance(attraction)) return it;
        }
        return null;
    }
}
