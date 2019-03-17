package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.entity.SpriteType;
import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.component.Button;
import com.valkryst.Emberstone.mvc.component.ButtonState;
import com.valkryst.Emberstone.mvc.component.Image;
import com.valkryst.Emberstone.mvc.component.Label;
import com.valkryst.Emberstone.mvc.controller.LevelController;
import com.valkryst.Emberstone.mvc.controller.MainMenuController;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import com.valkryst.generator.MarkovGenerator;
import com.valkryst.generator.NameGenerator;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

public class CharacterSelectionView extends View {
    private Button selectionButtonA;
    private Button selectionButtonB;
    private Button selectionButtonC;
    private Button quitButton;

    public CharacterSelectionView() {
        // Set up the background.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Character Selection.png", "gui/Character Selection.json");

            // Add Background
            SpriteSheet sheet = atlas.getSpriteSheet("Character Selection");
            Sprite sprite = sheet.getSprite("Background");
            super.addComponent(new Image(new Point(0, 0), sprite));
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Set up the labels.
        try {
            final String[] trainingNames = NameGenerator.loadLinesFromJar("Human/Welsh/Male.txt");
            final MarkovGenerator generator = new MarkovGenerator(trainingNames);

            super.addComponent(new Label(new Point(200, 450), generator.generateName(7), Color.BLACK));
            super.addComponent(new Label(new Point(850, 450), generator.generateName(7), Color.BLACK));
            super.addComponent(new Label(new Point(1495, 450), generator.generateName(7), Color.BLACK));
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // Set up the buttons.
        final Game game = Game.getInstance();

        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Buttons.png", "gui/Buttons.json");
            SpriteSheet spriteSheet = atlas.getSpriteSheet("Select");

            // Add first selection button.
            selectionButtonA = new Button(new Point(120, 900), spriteSheet);
            super.addComponent(selectionButtonA);


            // Add second selection button.
            selectionButtonB = new Button(new Point(760, 900), spriteSheet);
            super.addComponent(selectionButtonB);


            // Add third selection button.
            selectionButtonC = new Button(new Point(1400, 900), spriteSheet);
            super.addComponent(selectionButtonC);

            // Add small quit button.
            spriteSheet = atlas.getSpriteSheet("Quit - Small");
            quitButton = new Button(new Point(1850, 0), spriteSheet);
            super.addComponent(quitButton);
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        selectionButtonA.addStateChangeFunction(ButtonState.RELEASED, () -> game.playVideo(Video.INTRO, new LevelController(SpriteType.PLAYER_A)));
        selectionButtonB.addStateChangeFunction(ButtonState.RELEASED, () -> game.playVideo(Video.INTRO, new LevelController(SpriteType.PLAYER_B)));
        selectionButtonC.addStateChangeFunction(ButtonState.RELEASED, () -> game.playVideo(Video.INTRO, new LevelController(SpriteType.PLAYER_C)));
        quitButton.addStateChangeFunction(ButtonState.RELEASED, () -> game.setController(new MainMenuController()));
    }
}
