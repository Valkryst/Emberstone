package com.valkryst.Emberstone.statistic;

import lombok.Getter;

public enum StatisticType {
    LEVEL("Level"),
    EXPERIENCE("Experience"),
    SPEED("Speed"),
    HEALTH("Health"),
    DAMAGE("Damage"),
    ARMOR("Armor"),
    AGILITY("Agility"),
    STAMINA("Stamina"),
    STRENGTH("Strength");

    /** The name. */
    @Getter private final String name;

    /**
     * Constructs a new StatisticType.
     *
     * @param name
     *          The name.
     */
    StatisticType(final String name) {
        this.name = name;
    }
}
