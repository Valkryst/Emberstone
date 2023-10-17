package com.valkryst.Emberstone.display.view;

import com.sun.scenario.Settings;
import com.valkryst.Emberstone.Video;
import com.valkryst.Emberstone.display.Display;
import com.valkryst.Emberstone.display.controller.MainMenuController;
import com.valkryst.Emberstone.display.model.GameModel;
import com.valkryst.Emberstone.display.model.MainMenuModel;
import com.valkryst.Emberstone.display.model.SettingsModel;
import com.valkryst.Emberstone.display.model.VideoModel;
import com.valkryst.VMVC.view.View;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainMenuView extends View<MainMenuController> {
    public MainMenuView(final MainMenuController controller) {
		super(controller);

		final var innerPanel = new JPanel(new GridLayout(0, 1));
        this.add(innerPanel);

        final var newGameButton = new JButton("New");
        final var loadGameButton = new JButton("Load");
        final var settingsButton = new JButton("Settings");
        final var creditsButton = new JButton("Credits");
        final var exitButton = new JButton("Exit");

        newGameButton.addActionListener(e -> {
            Display.getInstance().setContentPane(new VideoModel(Video.INTRO, GameModel.class).createView());
		});
        loadGameButton.addActionListener(e -> {
			System.err.println("Load Game View Not Implemented");
		});
        settingsButton.addActionListener(e -> {
            Display.getInstance().setContentPane(SettingsModel.getInstance().createView());
        });
        creditsButton.addActionListener(e -> {
            Display.getInstance().setContentPane(new VideoModel(Video.CREDITS, MainMenuModel.class).createView());
		});
        exitButton.addActionListener(e -> {
			System.exit(0);
		});

        innerPanel.add(newGameButton);
        innerPanel.add(loadGameButton);
        innerPanel.add(settingsButton);
        innerPanel.add(creditsButton);
        innerPanel.add(exitButton);

        this.setLayout(new GridBagLayout());
    }
}
