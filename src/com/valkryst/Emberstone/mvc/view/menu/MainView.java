package com.valkryst.Emberstone.mvc.view.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.component.EButton;
import com.valkryst.Emberstone.mvc.component.EImage;
import com.valkryst.Emberstone.mvc.controller.VideoController;
import com.valkryst.Emberstone.mvc.controller.menu.CharacterSelectionController;
import com.valkryst.Emberstone.mvc.controller.menu.ControlsController;
import com.valkryst.Emberstone.mvc.controller.menu.MainController;
import com.valkryst.Emberstone.mvc.controller.menu.SettingsController;
import com.valkryst.Emberstone.mvc.view.View;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

public class MainView extends View {
    /** Constructs a new MainView. */
    public MainView() {
        final Game game = Game.getInstance();

        // Set up the background and logo.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Main Menu.png", "gui/Main Menu.json");
            final SpriteSheet sheet = atlas.getSpriteSheet("Main Menu");

            // Add Background
            super.setBackgroundImage(sheet.getSprite("Background").getBufferedImage());

            // Add Logo
            final JLabel logo = new EImage(sheet.getSprite("Logo").getBufferedImage());
            logo.setLocation((int) ((game.getViewWidth() / 2.0) - (logo.getWidth() / 2.0)), logo.getHeight());
            this.add(logo);
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Create buttons.
        final JButton newGameButton = new EButton("New Game", 48);
        final JButton controlsButton = new EButton("Controls", 48);
        final JButton settingsButton = new EButton("Settings", 48);
        final JButton creditsButton = new EButton("Credits", 48);
        final JButton quitButton = new EButton("Quit", 48);

        // Position buttons.
        final int x = (int) ((game.getViewWidth() / 2.0) - (newGameButton.getWidth() / 2.0));
        int y = (int) (game.getViewHeight() / 2.0) - newGameButton.getHeight();

        newGameButton.setLocation(x, y);
        controlsButton.setLocation(x, (int) (y + (newGameButton.getHeight() * 1.5)));
        settingsButton.setLocation(x, (int) (y + (newGameButton.getHeight() * 3.0)));
        creditsButton.setLocation(x, (int) (y + (newGameButton.getHeight() * 4.5)));
        quitButton.setLocation(x, (int) (y + (newGameButton.getHeight() * 6.0)));

        // Add button listeners
        newGameButton.addActionListener(e -> game.setController(new CharacterSelectionController()));
        controlsButton.addActionListener(e -> game.setController(new ControlsController(new MainController())));
        settingsButton.addActionListener(e -> game.setController(new SettingsController()));
        creditsButton.addActionListener(e -> {
            GameAudio.getInstance().stopAllActiveMusic();
            game.setController(new VideoController(Video.CREDITS, new MainController()));
        });
        quitButton.addActionListener(e -> System.exit(0));

        // Add buttons to view.
        this.add(newGameButton);
        this.add(controlsButton);
        this.add(settingsButton);
        this.add(creditsButton);
        this.add(quitButton);
    }
}
