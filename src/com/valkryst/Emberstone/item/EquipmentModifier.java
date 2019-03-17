package com.valkryst.Emberstone.item;

import com.valkryst.Emberstone.statistic.StatisticType;
import lombok.Getter;

public enum EquipmentModifier {
    NONE("", new StatisticType[0]),
    LEOPARD(" of the Leopard", new StatisticType[]{StatisticType.AGILITY, StatisticType.STAMINA}),
    TIGER(" of the Tiger", new StatisticType[]{StatisticType.AGILITY, StatisticType.STRENGTH}),
    RHINO(" of the Rhino", new StatisticType[]{StatisticType.STAMINA, StatisticType.STRENGTH});

    /** The text appended after an item's name, if it uses the modifier. */
    @Getter private final String suffix;
    /** The stats associated with the modifier. */
    @Getter private final StatisticType[] stats;

    /**
     *
     * @param suffix
     *          The text appended after an item's name, if it uses the modifier.
     *
     * @param stats
     *          The stats associated with the modifier.
     */
    EquipmentModifier(final String suffix, final StatisticType[] stats) {
        this.suffix = suffix;
        this.stats = stats;
    }
}
