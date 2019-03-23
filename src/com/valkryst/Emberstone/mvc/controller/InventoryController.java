package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.mvc.component.ButtonState;
import com.valkryst.Emberstone.mvc.view.InventoryView;
import lombok.NonNull;

public class InventoryController extends Controller {
    private final LevelController levelController;

    /**
     * Constructs a new InventoryController.
     *
     * @param levelController
     *          The level controller to restore when exiting from the inventory.
     */
    public InventoryController(final @NonNull LevelController levelController) {
        super(new InventoryView(levelController.getMap().getPlayer().getInventory()));
        this.levelController = levelController;

        ((InventoryView) super.getView()).getQuitButton().addStateChangeFunction(ButtonState.RELEASED, () -> Game.getInstance().setController(levelController));
    }

    public InventoryController(final @NonNull LevelController levelController, final @NonNull Inventory lootInventory) {
        super(new InventoryView(levelController.getMap().getPlayer().getInventory(), lootInventory));
        this.levelController = levelController;

        ((InventoryView) super.getView()).getQuitButton().addStateChangeFunction(ButtonState.RELEASED, () -> Game.getInstance().setController(levelController));
    }
}
