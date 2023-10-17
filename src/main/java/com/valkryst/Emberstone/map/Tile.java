package com.valkryst.Emberstone.map;

import com.valkryst.Emberstone.Camera;
import com.valkryst.Emberstone.Settings;
import com.valkryst.V2DSprite.AnimatedSprite;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.VJSON.VJSON;
import lombok.Getter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Tile {
    /** The width/height of a tile. */
    private final static int TILE_DIMENSIONS = 128;

    /** The tile's sprite. */
    private final Sprite sprite;

    /** Whether the tile is walkable. */
    @Getter private final boolean isWalkable;

    /**
     * Constructs a new Tile.
     *
     * @param sprite
     *          The sprite.
     *
     * @param data
     *          The JSON data.
     */
    public Tile(final Sprite sprite, final JSONObject data) {
        this.sprite = sprite;
        isWalkable = VJSON.getBoolean(data, "Is Valid Spawn Point");
    }

    /**
     * Constructs a new Tile.
     *
     * @param sprite
     *          The animated sprite.
     *
     * @param data
     *          The JSON data.
     */
    public Tile(final AnimatedSprite sprite, final JSONObject data) {
        // Randomize the sprite.
        final int frame = ThreadLocalRandom.current().nextInt(sprite.getTotalFrames());
        for (int i = 0 ; i < frame ; i++) {
            sprite.toNextFrame();
        }

        this.sprite = sprite.getCurrentSprite();
        isWalkable = VJSON.getBoolean(data, "Is Valid Spawn Point");
    }

    /**
     * Draws the tile on a graphics context.
     *
     * @param gc
     *          The graphics context.
     *
     * @param x
     *          The tile's x-axis position on the map.
     *
     * @param y
     *          The tile's y-axis position on the map.
     *
     * @param camera
     *          The map's active camera.
     */
    public void draw(final Graphics2D gc, int x, int y, final Camera camera) {
        x = (x * TILE_DIMENSIONS) - camera.getX();
        y = (y * TILE_DIMENSIONS) - camera.getY();
        sprite.draw(gc, x, y);

        final Settings settings = Settings.getInstance();

        // Display bounding boxes.
        if (settings.areDebugBoundingBoxesOn()) {
            gc.setColor(Color.MAGENTA);
            sprite.drawBoundingBox(gc, "Body A", x, y);
            sprite.drawBoundingBox(gc, "Body B", x, y);
        }

        // Display Spawn Points
        if (isWalkable && settings.areDebugSpawnPointsOn()) {
            gc.setColor(Color.BLUE);
            gc.drawRect(x, y, TILE_DIMENSIONS - 1, TILE_DIMENSIONS - 1);
        }
    }

    /**
     * Retrieves the width/height of a tile.
     *
     * @return
     *          The width/height of a tile.
     */
    public static int getTileDimensions() {
        return TILE_DIMENSIONS;
    }

    /**
     * Retrieves a specific bounding box from the sprite.
     *
     * @param name
     *          The bounding box's name.
     *
     * @param x
     *          The tile's x-axis position on the map.
     *
     * @param y
     *          The tile's y-axis position on the map.
     *
     * @return
     *          The bounding box, or null if no bounding box with a matching name was found.
     */
    public Rectangle getBoundingBox(final String name, final int x, final int y) {
        final Rectangle boundingBox = sprite.getBoundingBox(name);

        if (boundingBox != null) {
            boundingBox.x += x * TILE_DIMENSIONS;
            boundingBox.y += y * TILE_DIMENSIONS;
        }

        return boundingBox;
    }
}
