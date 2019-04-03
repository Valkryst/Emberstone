package com.valkryst.Emberstone.mvc.controller.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.entity.Player;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.SoundEffect;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.menu.InventoryView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_I;

public class InventoryController extends Controller {
    /**
     * Constructs a new InventoryController.
     *
     * @param backgroundImage
     *          The background image.
     *
     * @param previousController
     *          The controller whose view was displayed before this controller's view.
     */
    public InventoryController(final BufferedImage backgroundImage, final Controller previousController) {
        super(new InventoryView(backgroundImage, previousController));
        super.getView().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {}

            @Override
            public void keyPressed(final KeyEvent e) {
                switch (e.getKeyCode()) {
                    case VK_I:
                    case VK_ESCAPE: {
                        GameAudio.getInstance().playSoundEffect(SoundEffect.CHEST_CLOSE);
                        Game.getInstance().setController(previousController);
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {}
        });

        GameAudio.getInstance().playSoundEffect(SoundEffect.CHEST_OPEN);
    }

    public void displayPlayerInventory(final Inventory playerInventory) {
        ((InventoryView) super.getView()).displayPlayerInventory(playerInventory);
    }

    public void displayLootInventory(final Inventory lootInventory) {
        ((InventoryView) super.getView()).displayLootInventory(lootInventory);
    }

    public void displayPlayerStats(final Player player) {
        ((InventoryView) super.getView()).displayPlayerStats(player);
    }
}
