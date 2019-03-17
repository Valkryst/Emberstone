package com.valkryst.Emberstone.item;

import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class Inventory {
    /** The equipped items. */
    private final HashMap<EquipmentSlot, Equipment> equipment = new HashMap<>();

    /**
     * Equips an item.
     *
     * If another item is equipped in the same slot that the new item is being equipped to, then the currently
     * equipped item is removed and placed in the inventory before the new item is equipped.
     *
     * If there is no room in the inventory, then nothing happens.
     *
     * @param item
     *          The item to equip.
     */
    public void equip(final Equipment item) {
        if (item == null) {
            return;
        }

        final EquipmentSlot slot = item.getSlot();

        if (equipment.get(slot) != null) {
            equipment.remove(slot);
        }

        equipment.put(slot, item);
    }

    /**
     * Unequips an item and places it in the inventory.
     *
     * If there is no room in the inventory, then nothing happens.
     *
     * @param slot
     *          The slot of the item to unequip.
     */
    public void unequip(final EquipmentSlot slot) {
        if (equipment.get(slot) != null) {
            equipment.remove(slot);
        }
    }

    /**
     * Retrieves the item equipped in a specific slot.
     *
     * @param slot
     *          The slot.
     *
     * @return
     *          The item.
     *          Null if no item is equipped to the slot.
     */
    public Equipment getEquipment(final EquipmentSlot slot) {
        return equipment.get(slot);
    }
}
