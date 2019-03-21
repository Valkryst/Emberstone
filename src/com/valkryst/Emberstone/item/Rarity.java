package com.valkryst.Emberstone.item;

import lombok.Getter;

import java.awt.*;

public enum Rarity {
    COMMON(Color.WHITE, 1),
    UNCOMMON(new Color(0x1EFF00), 2),
    RARE(new Color(0x0070DD), 3),
    EPIC(new Color(0xA335EE), 4),
    LEGENDARY(new Color(0xFF8000), 8),
    ARTIFACT(new Color(0xE6CC80), 12);

    /** The color associated with the rarity. */
    @Getter private final Color color;

    /** The multiplier value associated with the rarity. */
    @Getter private final double multiplier;

    /**
     * Constructs a new Rarity.
     *
     * @param color
     *          The color associated with the rarity.
     *
     * @param multiplier
     *          The multiplier value associated with the rarity.
     */
    Rarity(final Color color, final double multiplier) {
        this.color = color;
        this.multiplier = multiplier;
    }
}
