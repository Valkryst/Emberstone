package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.display.controller.DefaultController;

import javax.swing.*;
import java.beans.PropertyChangeEvent;

public class LoadGameView extends View {
    /**
     * Constructs a new LoadGameView.
     *
     * @param controller
     *          The controller for this view.
     */
    public LoadGameView(final DefaultController controller) {
        final var backButton = new JButton("Back");

        backButton.addActionListener(e -> controller.displayMainMenuView(this));

        this.add(backButton);
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent event) {
        // Ignored, there are no properties to listen to at the moment.
    }
}
