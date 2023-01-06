package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.Video;
import com.valkryst.Emberstone.display.controller.MainMenuController;
import com.valkryst.Emberstone.display.model.GameModel;
import com.valkryst.Emberstone.display.model.MainMenuModel;
import com.valkryst.Emberstone.display.model.SettingsModel;
import com.valkryst.Emberstone.display.model.VideoModel;

import javax.swing.*;
import java.awt.*;

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
			controller.setContentPane(VideoModel.class, Video.INTRO, GameModel.class);
		});
        loadGameButton.addActionListener(e -> {
			System.err.println("Load Game View Not Implemented");
		});
        settingsButton.addActionListener(e -> {
			controller.setContentPane(SettingsModel.class);
		});
        creditsButton.addActionListener(e -> {
			controller.setContentPane(VideoModel.class, Video.CREDITS, MainMenuModel.class);
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
