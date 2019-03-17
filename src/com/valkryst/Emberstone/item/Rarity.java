package com.valkryst.Emberstone.item;

import lombok.Getter;

public enum Rarity {
    COMMON(1),
    UNCOMMON(2),
    RARE(3),
    EPIC(4),
    LEGENDARY(8),
    ARTIFACT(12);

    /** The multiplier value associated with the rarity. */
    @Getter private final double multiplier;

    /**
     * Constructs a new Rarity.
     *
     * @param multiplier
     *          The multiplier value associated with the rarity.
     */
    Rarity(final double multiplier) {
        this.multiplier = multiplier;
    }
}
