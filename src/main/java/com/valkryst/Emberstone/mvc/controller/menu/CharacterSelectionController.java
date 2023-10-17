package com.valkryst.Emberstone.mvc.controller.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.menu.CharacterSelectionView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CharacterSelectionController extends Controller {
    /** Constructs a new CharacterSelectionController. */
    public CharacterSelectionController() {
        super(new CharacterSelectionView());
        super.getView().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {}

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Game.getInstance().setController(new MainController());
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {}
        });
    }
}
