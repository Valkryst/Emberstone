package com.valkryst.Emberstone.mvc.controller.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.menu.EscapeView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class EscapeController extends Controller {
    /**
     * Constructs a new VideoController.
     *
     * @param backgroundImage
     *          The background image.
     *
     * @param previousController
     *          The controller whose view was displayed before this controller's view.
     */
    public EscapeController(final BufferedImage backgroundImage, final Controller previousController) {
        super(new EscapeView(backgroundImage, previousController));
        super.getView().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {}

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Game.getInstance().setController(previousController);
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {}
        });
    }
}
