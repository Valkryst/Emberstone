package com.valkryst.Emberstone.item;

import lombok.Getter;

public enum Material {
    COPPER("Copper", 1),
    BRONZE("Bronze", 1.2),
    IRON("Iron", 1.3),
    STEEL("Steel", 1.4),
    MITHRIL("Mithril", 1.8),
    ADAMANTINE("Adamantine", 2.0);

    /** The name. */
    @Getter private final String name;

    /** The multiplier value associated with the material. */
    @Getter private final double multiplier;

    /**
     * Constructs a new Material.
     *
     * @param name
     *          The name.
     *
     * @param multiplier
     *          The multiplier value associated with the material.
     */
    Material(final String name, final double multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }
}
