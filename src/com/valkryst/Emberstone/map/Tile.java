package com.valkryst.Emberstone.map;

import com.valkryst.Emberstone.Camera;
import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import com.valkryst.V2DSprite.AnimatedSprite;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.VJSON.VJSON;
import lombok.Getter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Tile {
    private final static int TILE_DIMENSIONS = 128;

    private final Sprite sprite;

    @Getter private final boolean isValidSpawnPoint;

    public Tile(final Sprite sprite, final JSONObject data) {
        this.sprite = sprite;
        isValidSpawnPoint = VJSON.getBoolean(data, "Is Valid Spawn Point");
    }

    public Tile(final AnimatedSprite sprite, final JSONObject data) {
        // Randomize the sprite.
        final int frame = ThreadLocalRandom.current().nextInt(sprite.getTotalFrames());
        for (int i = 0 ; i < frame ; i++) {
            sprite.toNextFrame();
        }

        this.sprite = sprite.getCurrentSprite();
        isValidSpawnPoint = VJSON.getBoolean(data, "Is Valid Spawn Point");
    }

    public void draw(final Graphics2D gc, int x, int y, final Camera camera) {
        x = (x * TILE_DIMENSIONS) - camera.getX();
        y = (y * TILE_DIMENSIONS) - camera.getY();
        sprite.draw(gc, x, y);

        // Display bounding boxes.
        if (Settings.getInstance().areDebugBoundingBoxesOn()) {
            gc.setColor(Color.MAGENTA);
            sprite.drawBoundingBox(gc, "Body A", x, y);
            sprite.drawBoundingBox(gc, "Body B", x, y);
        }

        // Display Spawn Points
        if (isValidSpawnPoint && Settings.getInstance().areDebugSpawnPointsOn()) {
            gc.setColor(Color.BLUE);
            gc.drawRect(x, y, TILE_DIMENSIONS - 1, TILE_DIMENSIONS - 1);
        }
    }

    public static int getTileDimensions() {
        return TILE_DIMENSIONS;
    }

    public Rectangle getBoundingBoxA(final int x, final int y) {
        final Rectangle boundingBox = sprite.getBoundingBox("Body A");

        if (boundingBox != null) {
            boundingBox.x += x;
            boundingBox.y += y;
        }

        return boundingBox;
    }

    public Rectangle getBoundingBoxB(final int x, final int y) {
        final Rectangle boundingBox = sprite.getBoundingBox("Body B");

        if (boundingBox != null) {
            boundingBox.x += x;
            boundingBox.y += y;
        }

        return boundingBox;
    }
}
