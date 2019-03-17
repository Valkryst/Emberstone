package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.mvc.component.Button;
import com.valkryst.Emberstone.mvc.component.ButtonState;
import com.valkryst.Emberstone.mvc.component.Image;
import com.valkryst.Emberstone.mvc.controller.MainMenuController;
import com.valkryst.Emberstone.mvc.controller.SettingsController;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

public class SettingsView extends View {
    @Getter private Button musicButton;

    @Getter private Button sfxButton;

    /** The back button. */
    @Getter private Button backButton;

    public SettingsView() {
        final Game game = Game.getInstance();

        // Set up the background and logo.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("SpriteAtlas - Main Menu.png", "SpriteAtlas - Main Menu.json");

            // Add Background
            SpriteSheet sheet = atlas.getSpriteSheet("Main Menu");
            Sprite sprite = sheet.getSprite("Background");
            super.addComponent(new com.valkryst.Emberstone.mvc.component.Image(new Point(0, 0), sprite));

            // Add Logo
            sprite = sheet.getSprite("Logo");

            final int x = (int) ((game.getCanvasWidth() / 2.0) - (sprite.getWidth() / 2.0));
            final int y = sprite.getHeight();

            super.addComponent(new Image(new Point(x, y), sprite));
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Set up the buttons.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Buttons.png", "gui/Buttons.json");


            SpriteSheet spriteSheet = atlas.getSpriteSheet("Back");

            final Sprite sprite = spriteSheet.getSprite(ButtonState.NORMAL.name());
            final int x = (int) ((game.getCanvasWidth() / 2.0) - (sprite.getWidth() / 2.0));
            int y = (int) (game.getCanvasHeight() / 2.0);

            // Add Music Toggle Button
            spriteSheet = atlas.getSpriteSheet(Settings.getInstance().isMusicEnabled() ? "Music - On": "Music - Off");

            musicButton = new Button(new Point(x, y), spriteSheet);
            super.addComponent(musicButton);


            // Add SFX Toggle Button
            spriteSheet = atlas.getSpriteSheet(Settings.getInstance().isSfxEnabled() ? "SFX - On": "SFX - Off");
            y += sprite.getHeight() * 1.5;

            sfxButton = new Button(new Point(x, y), spriteSheet);
            super.addComponent(sfxButton);


            // Add Back Button
            spriteSheet = atlas.getSpriteSheet("Back");
            y += sprite.getHeight() * 1.5;

            backButton = new Button(new Point(x, y), spriteSheet);
            super.addComponent(backButton);
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Set up the state change functions.
        musicButton.addStateChangeFunction(ButtonState.RELEASED, () -> {
            final Settings settings = Settings.getInstance();
            settings.setMusicEnabled(!settings.isMusicEnabled());

            game.setController(new SettingsController());
        });

        sfxButton.addStateChangeFunction(ButtonState.RELEASED, () -> {
            final Settings settings = Settings.getInstance();
            settings.setSfxEnabled(!settings.isSfxEnabled());

            game.setController(new SettingsController());
        });

        backButton.addStateChangeFunction(ButtonState.RELEASED, () -> {
            game.setController(new MainMenuController());
        });
    }
}
