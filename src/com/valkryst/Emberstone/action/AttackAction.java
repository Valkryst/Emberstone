package com.valkryst.Emberstone.action;

import com.valkryst.Emberstone.entity.AnimationState;
import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.item.Equipment;
import com.valkryst.Emberstone.item.EquipmentSlot;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.VDice.DiceRoller;

public class AttackAction extends Action {
    /** The target. */
    private final Entity target;

    /** The result of the roll which determines the type of attack to perform. */
    private final int actionRoll;

    /**
     * Constructs a new AttackAction.
     *
     * @param target
     *          The target.
     */
    public AttackAction(final Entity target) {
        this.target = target;

        final DiceRoller diceRoller = new DiceRoller();
        diceRoller.addDice(20, 1);
        actionRoll = diceRoller.roll();
    }

    @Override
    public void perform(final Map map, final Entity self) {
        if (map == null || self == null || target == null) {
            return;
        }

        super.perform(map, self);

        int damage = 0;

        // Miss
        if (actionRoll < 5) {
            new MissAction().perform(map, self);
        }

        // Normal Attack
        if (actionRoll >= 5 && actionRoll <= 16) {
            damage = calculateDamage(self, target);
        }

        // Double Attack
        if (actionRoll > 16 && actionRoll < 20) {
            damage = calculateDamage(self, target) * 2;
        }

        // Critical Attack
        if (actionRoll == 20) {
            damage = calculateDamage(self, target) * 3;
        }

        // If the target is defending, then halve the damage dealt.
        if (target.getAnimationState() == AnimationState.DEFENDING) {
            damage /= 2;
        }

        final BoundStatistic health = (BoundStatistic) target.getStat(StatisticType.HEALTH);

        if (damage > 0) {
            health.setValue(health.getValue() - damage);
        }

        if (health.getValue() <= health.getMinValue()) {
            new DeathAction().perform(map, target);
        }
    }

    /**
     * Calculates the damage to deal.
     *
     * @param self
     *        The attacking creature.
     *
     * @param target
     *        The creature being attacked.
     *
     * @return
     *        The damage dealt.
     */
    private int calculateDamage(final Entity self, final Entity target) {
        // Calculate Target Armor
        final Inventory targetInventory = target.getInventory();

        int armor = 0;

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            final Equipment equipment = targetInventory.getEquipment(slot);
            armor += (equipment == null ? 0 : equipment.getArmor());
        }

        // Calculate Damage
        final Inventory selfInventory = self.getInventory();
        final Equipment mainHand = selfInventory.getEquipment(EquipmentSlot.MAIN_HAND);
        final Equipment offHand = selfInventory.getEquipment(EquipmentSlot.OFF_HAND);

        int damage = 0;
        damage+= (mainHand == null ? 0 : mainHand.rollDamage());
        damage+= (offHand == null ? 0 : offHand.rollDamage());

        // Calculate Result
        final int result = damage - armor;
        return result > 0 ? result : 0;
    }
}
