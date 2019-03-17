package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.component.Button;
import com.valkryst.Emberstone.mvc.component.ButtonState;
import com.valkryst.Emberstone.mvc.component.Image;
import com.valkryst.Emberstone.mvc.controller.CharacterSelectionController;
import com.valkryst.Emberstone.mvc.controller.LevelController;
import com.valkryst.Emberstone.mvc.controller.SettingsController;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

public class MainMenuView extends View {
    /** The new game button. */
    @Getter private Button newGameButton;
    /** The load game button. */
    @Getter private Button loadGameButton;
    /** The settings button. */
    @Getter private Button settingsButton;
    /** The quit button. */
    @Getter private Button quitButton;

    public MainMenuView() {
        final Game game = Game.getInstance();

        // Set up the background and logo.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("SpriteAtlas - Main Menu.png", "SpriteAtlas - Main Menu.json");

            // Add Background
            SpriteSheet sheet = atlas.getSpriteSheet("Main Menu");
            Sprite sprite = sheet.getSprite("Background");
            super.addComponent(new Image(new Point(0, 0), sprite));

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

            SpriteSheet spriteSheet = atlas.getSpriteSheet("New Game");

            final Sprite sprite = spriteSheet.getSprite(ButtonState.NORMAL.name());
            final int x = (int) ((game.getCanvasWidth() / 2.0) - (sprite.getWidth() / 2.0));
            int y = (int) (game.getCanvasHeight() / 2.0);


            // Add New Game Button
            newGameButton = new Button(new Point(x, y), spriteSheet);
            super.addComponent(newGameButton);


            // Add Load Game Button
            spriteSheet = atlas.getSpriteSheet("Load Game");
            y += sprite.getHeight() * 1.5;

            loadGameButton = new Button(new Point(x, y), spriteSheet);
            super.addComponent(loadGameButton);


            // Add Settings Button
            spriteSheet = atlas.getSpriteSheet("Settings");
            y += sprite.getHeight() * 1.5;

            settingsButton = new Button(new Point(x, y), spriteSheet);
            super.addComponent(settingsButton);


            // Add Quit Button
            spriteSheet = atlas.getSpriteSheet("Quit");
            y += sprite.getHeight() * 1.5;

            quitButton = new Button(new Point(x, y), spriteSheet);
            super.addComponent(quitButton);
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Set up the state change functions.
        newGameButton.addStateChangeFunction(ButtonState.RELEASED, () -> game.setController(new CharacterSelectionController()));
        settingsButton.addStateChangeFunction(ButtonState.RELEASED, () -> game.setController(new SettingsController()));
        quitButton.addStateChangeFunction(ButtonState.RELEASED, () -> System.exit(0));
    }
}
