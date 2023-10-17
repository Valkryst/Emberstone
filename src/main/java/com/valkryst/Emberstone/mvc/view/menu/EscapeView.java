package com.valkryst.Emberstone.mvc.view.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.mvc.component.EButton;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.controller.menu.ControlsController;
import com.valkryst.Emberstone.mvc.controller.menu.EscapeController;
import com.valkryst.Emberstone.mvc.controller.menu.MainController;
import com.valkryst.Emberstone.mvc.view.View;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class EscapeView extends View {
    /**
     * Constructs a new EscapeView.
     *
     * @param backgroundImage
     *          The background image.
     *
     * @param previousController
     *          The controller whose view was displayed before this view.
     */
    public EscapeView(BufferedImage backgroundImage, final Controller previousController) {
        final Game game = Game.getInstance();

        // Set the background image.
        super.setBackgroundImage(backgroundImage);

        // Create buttons.
        final JButton continueButton = new EButton("Continue", 48);
        final JButton controlsButton = new EButton("Controls", 48);
        final JButton exitToMainMenuButton = new EButton("Exit to Main Menu", 36);

        // Position buttons.
        final int x = (int) ((game.getViewWidth() / 2.0) - (continueButton.getWidth() / 2.0));
        int y = (int) (game.getViewHeight() / 2.0);

        continueButton.setLocation(x, y);
        controlsButton.setLocation(x, (int) (y + (continueButton.getHeight() * 1.5)));
        exitToMainMenuButton.setLocation(x, (int) (y + (continueButton.getHeight() * 3.0)));

        // Add button listeners.
        continueButton.addActionListener(e -> game.setController(previousController));
        controlsButton.addActionListener(e -> game.setController(new ControlsController(previousController)));
        exitToMainMenuButton.addActionListener(e -> game.setController(new MainController()));

        // Add buttons to view.
        this.add(continueButton);
        this.add(controlsButton);
        this.add(exitToMainMenuButton);
    }
}
