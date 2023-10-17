package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.mvc.component.EImage;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

public class LevelView extends View {
    @Getter private final Canvas canvas;

    /** Constructs a new LevelView. */
    public LevelView() {
        canvas = new Canvas();
        canvas.setBackground(new Color(47, 47, 46));

        // Set up the UI.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Game UI.png", "gui/Game UI.json");

            // Add Background
            SpriteSheet sheet = atlas.getSpriteSheet("Game UI");
            Sprite sprite = sheet.getSprite("Background");
            super.add(new EImage(sprite.getBufferedImage()));
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
