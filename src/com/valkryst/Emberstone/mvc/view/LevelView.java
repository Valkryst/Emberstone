package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.mvc.component.Image;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

public class LevelView extends View {
    /** Constructs a new LevelView. */
    public LevelView() {
        // Set up the UI.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Game UI.png", "gui/Game UI.json");

            // Add Background
            SpriteSheet sheet = atlas.getSpriteSheet("Game UI");
            Sprite sprite = sheet.getSprite("Background");
            super.addComponent(new Image(new Point(0, 0), sprite));
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
