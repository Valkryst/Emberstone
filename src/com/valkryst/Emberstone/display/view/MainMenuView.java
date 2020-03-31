package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.display.controller.DefaultController;

import java.awt.*;
import java.beans.PropertyChangeEvent;

public class MainMenuView extends View {
    /**
     * Constructs a new MainMenuView.
     *
     * @param controller
     *          The controller for this view.
     */
    public MainMenuView(final DefaultController controller) {
        final var innerPanel = new Panel(new GridLayout(0, 1));
        this.add(innerPanel);

        final var newGameButton = new Button("New");
        final var loadGameButton = new Button("Load");
        final var settingsButton = new Button("Settings");
        final var creditsButton = new Button("Credits");
        final var exitButton = new Button("Exit");

        newGameButton.addActionListener(e -> controller.displayNewGameView(this));
        loadGameButton.addActionListener(e -> controller.displayLoadGameView(this));
        settingsButton.addActionListener(e -> controller.displaySettingsView(this));
        creditsButton.addActionListener(e -> controller.displayCreditsView());
        exitButton.addActionListener(e -> controller.exitGame());

        innerPanel.add(newGameButton);
        innerPanel.add(loadGameButton);
        innerPanel.add(settingsButton);
        innerPanel.add(creditsButton);
        innerPanel.add(exitButton);

        this.setBackground(Color.BLACK);
        this.setLayout(new GridBagLayout());
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent event) {
        // Ignored, there are no properties to listen to at the moment.
    }
}
