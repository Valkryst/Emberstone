package com.valkryst.Emberstone.item;

import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.VDice.DiceRoller;
import lombok.Getter;

import java.util.HashMap;

public class Equipment extends Item {
    /** The rarity. */
    @Getter private final Rarity rarity;

    /** The material. */
    @Getter private final Material material;

    /** The slot in which this item can be equipped. */
    @Getter private final EquipmentSlot slot;

    /** The statistic modifiers. */
    @Getter private final EquipmentModifier modifier;

    /**
     * Constructs a new piece of Equipment.
     *
     * @param name
     *          The name.
     *
     * @param description
     *          The description.
     *
     * @param stats
     *          The stats.
     *
     * @param rarity
     *          The rarity.
     *
     * @param material
     *          The material.
     *
     * @param modifier
     *          The modifier.
     *
     * @param slot
     *          The slot in which this item can be equipped.
     */
    public Equipment(final String name, final String description, final HashMap<StatisticType, Statistic> stats, final Rarity rarity, final Material material, final EquipmentModifier modifier, final EquipmentSlot slot) {
        super(name + (modifier == null ? "" : modifier.getSuffix()), description, stats);
        this.rarity = (rarity == null ? Rarity.COMMON : rarity);
        this.material = (material == null ? Material.COPPER : material);
        this.modifier = (modifier == null ? EquipmentModifier.NONE : modifier);
        this.slot = slot;
    }

    /**
     * Uses the damage stat to perform a damage roll. The result is a value between the min and max damage
     * values of the stat.
     *
     * If this piece of equipment doesn't have a damage stat, then 0 is returned.
     *
     * @return
     *          The rolled damage value.
     */
    public int rollDamage() {
        final BoundStatistic damageStat = (BoundStatistic) super.getStat(StatisticType.DAMAGE);

        if (damageStat == null) {
            return 0;
        }

        final int minDamage = damageStat.getMinValue();
        final int maxDamage = damageStat.getMaxValue();

        final DiceRoller diceRoller = new DiceRoller();
        diceRoller.addDice(maxDamage - minDamage, 1);

        try {
            return diceRoller.roll() + minDamage;
        } catch (final IllegalArgumentException e) {
            return 0; // Shit solution, but there's a bug in the Creature creation code.
        }
    }

    /**
     * Retrieves the armor value from the armor stat.
     *
     * If this piece of equipment doesn't have an armor stat, then 0 is returned.
     *
     * @return
     *          The armor value of the armor stat.
     */
    public int getArmor() {
        final Statistic armor = super.getStat(StatisticType.ARMOR);

        if (armor == null) {
            return 0;
        }

        return armor.getValue();
    }
}
