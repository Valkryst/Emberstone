package com.valkryst.Emberstone.mvc.view.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.mvc.component.EButton;
import com.valkryst.Emberstone.mvc.component.EImage;
import com.valkryst.Emberstone.mvc.controller.menu.MainController;
import com.valkryst.Emberstone.mvc.view.View;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

public class SettingsView extends View {
    /** Constructs a new SettingsView. */
    public SettingsView() {
        final Game game = Game.getInstance();
        final Settings settings = Settings.getInstance();

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
        final JButton musicButton = new EButton("Music: " + (settings.isMusicEnabled() ? "On" : "Off"), 48);
        final JButton sfxButton = new EButton("SFX: " + (settings.isMusicEnabled() ? "On" : "Off"), 48);
        final JButton backButton = new EButton("Back", 48);

        // Position buttons.
        final int x = (int) ((game.getViewWidth() / 2.0) - (musicButton.getWidth() / 2.0));
        int y = (int) (game.getViewHeight() / 2.0);

        musicButton.setLocation(x, y);
        sfxButton.setLocation(x, (int) (y + (musicButton.getHeight() * 1.5)));
        backButton.setLocation(x, (int) (y + (musicButton.getHeight() * 3.0)));

        // Add button listeners.
        musicButton.addActionListener(e -> {
            settings.setMusicEnabled(!settings.isMusicEnabled());
            musicButton.setText("Music: " + (settings.isMusicEnabled() ? "On" : "Off"));
        });
        sfxButton.addActionListener(e -> {
            settings.setSfxEnabled(!settings.isSfxEnabled());
            sfxButton.setText("SFX: " + (settings.isSfxEnabled() ? "On" : "Off"));
        });
        backButton.addActionListener(e -> game.setController(new MainController()));

        // Add buttons to view.
        this.add(musicButton);
        this.add(sfxButton);
        this.add(backButton);
    }
}
