package com.valkryst.Emberstone.entity;

import com.valkryst.Emberstone.map.Map;
import com.valkryst.V2DSprite.SpriteSheet;

import java.awt.*;

public class Portal extends Entity {
    /**
     * Constructs a new Portal.
     *
     * @param position
     *          Location within the level.
     *
     * @param spriteSheet
     *          The sprite sheet.
     */
    public Portal(final Point position, final SpriteSheet spriteSheet) {
        super(position, spriteSheet);
    }

    @Override
    public void update(final Map map, final double deltaTime) {
        // Handle Animation Increment
        super.frameTime += deltaTime;

        if (super.frameTime >= 6) {
            super.spriteAnimation.toNextFrame();
            super.frameTime -= 6;
        }
    }
}
