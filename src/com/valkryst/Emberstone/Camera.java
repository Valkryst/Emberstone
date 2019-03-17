package com.valkryst.Emberstone;

import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.map.Tile;
import lombok.Getter;

import java.awt.*;

public class Camera {
    /** The camera's position on the map. */
    private final Point position = new Point(0, 0);

    /** The entity followed by the camera. */
    private final Entity target;

    private final Rectangle viewBoundingBox;

    /** Width of the camera's view. */
    @Getter private final int viewWidth = Game.getInstance().getCanvasWidth();
    /** Height of the camera's view. */
    @Getter private final int viewHeight = Game.getInstance().getCanvasHeight();

    /** Width of the map, in pixels. */
    private final int mapWidth;
    /** Height of the map, in pixels. */
    private final int mapHeight;

    /** Half of the width of the target entity's sprite. */
    private final int halfTargetWidth;
    /** Half of the height of the target entity's sprite. */
    private final int halfTargetHeight;

    /**
     * Constructs a new Camera.
     *
     * @param mapWidth
     *          Width of the map, in pixels.
     *
     * @param mapHeight
     *          Height of the map, in pixels.
     *
     * @param target
     *          The entity followed by the camera.
     */
    public Camera(final int mapWidth, final int mapHeight, final Entity target) {
        this.target = target;

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        viewBoundingBox = new Rectangle(position.x, position.y, viewWidth, viewHeight);

        halfTargetWidth = target.getSpriteAnimation().getCurrentWidth() / 2;
        halfTargetHeight = target.getSpriteAnimation().getCurrentHeight() / 2;

        final Point targetPosition = target.getPosition();
        final int x = targetPosition.x - (viewWidth / 2) + halfTargetWidth;
        final int y = targetPosition.y - (viewHeight / 2) + halfTargetHeight;
        position.setLocation(x, y);
    }

    /**
     * Updates the camera's state.
     *
     * @param deltaTime
     *          The delta time.
     */
    public void update(final double deltaTime) {
        final int targetX = target.getPosition().x - (viewWidth / 2) + halfTargetWidth;
        final int targetY = target.getPosition().y - (viewHeight / 2) + halfTargetHeight;

        position.x += (targetX - position.x) * 0.1 * deltaTime;
        position.y += (targetY - position.y) * 0.1 * deltaTime;

        position.x = Math.min((mapWidth - viewWidth), Math.max(0, position.x));
        position.y = Math.min((mapHeight - viewHeight), Math.max(0, position.y));

        viewBoundingBox.setLocation(position.x, position.y);
    }

    /**
     * Determines whether an entity is within view of the camera.
     *
     * @param entity
     *          The entity.
     *
     * @return
     *          Whether the entity is within view of the camera.
     */
    public boolean isInView(final Entity entity) {
        final Rectangle entityBoundingBox = entity.getBodyBoundingBox();

        if (entityBoundingBox == null) {
            if (Settings.getInstance().isDebugModeOn()) {
                System.out.println("Encountered an animation without a bounding box.\n\tAnimation state:" + entity.getAnimationState() + "\n\tSpriteType Animation Name: " + entity.getSpriteAnimation().getName());
            }
            return true;
        }

        return viewBoundingBox.intersects(entityBoundingBox);
    }

    /**
     * Determines whether a rectangle is within view of the camera.
     * @param x
     *          Position of the top-left corner of the rectangle on the x-axis.
     *
     * @param y
     *          Position of the top-left corner of the rectangle on the y-axis.
     *
     * @param width
     *          Width of the rectangle.
     *
     * @param height
     *          Height of the rectangle.
     *
     * @return
     *          Whether the rectangle is within view of the camera.
     */
    public boolean isInView(final int x, final int y, final int width, final int height) {
        return viewBoundingBox.intersects(new Rectangle(x, y, width, height));
    }

    /**
     * Retrieves the x-axis position of the top-left corner of the camera's view.
     *
     * @return
     *          The x-axis position of the top-left corner of the camera's view.
     */
    public int getX() {
        return position.x;
    }

    /**
     * Retrieves the y-axis position of the top-left corner of the camera's view.
     *
     * @return
     *          The y-axis position of the top-left corner of the camera's view.
     */
    public int getY() {
        return position.y;
    }
}
