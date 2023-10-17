package com.valkryst.Emberstone.mvc.model;

import com.valkryst.Emberstone.item.Inventory;
import lombok.Getter;

public class InventoryModel {
    /** The player's inventory. */
    @Getter private Inventory playerInventory;
    /** The inventory being looted. */
    @Getter private Inventory lootInventory;

    /**
     * Sets the player inventory.
     *
     * @param playerInventory
     *          The new player inventory.
     */
    public void setPlayerInventory(final Inventory playerInventory) {
        if (playerInventory == null) {
            this.playerInventory = new Inventory();
        } else {
            this.playerInventory = playerInventory;
        }
    }

    /**
     * Sets the loot inventory.
     *
     * @param lootInventory
     *          The new loot inventory.
     */
    public void setLootInventory(final Inventory lootInventory) {
        if (lootInventory == null) {
            this.lootInventory = new Inventory();
        } else {
            this.lootInventory = lootInventory;
        }
    }
}
