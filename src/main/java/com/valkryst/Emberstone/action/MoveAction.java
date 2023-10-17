package com.valkryst.Emberstone.action;

import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.map.Tile;

import java.awt.*;

public class MoveAction extends Action {
    /** The change applied to the original x-axis position. */
    private final double dx;
    /** The change applied to the original y-axis position. */
    private final double dy;

    /**
     * Constructs a new MoveAction.
     *  @param dx
     *        The change to apply to the x-axis position.
     *
     * @param dy
     */
    public MoveAction(final double dx, final double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void perform(final Map map, final Entity self) {
        if (map == null || self == null) {
            return;
        }

        final Point position = self.getPosition();
        self.getPosition().setLocation(position.x + dx, position.y + dy);

        // Check if the entity collides with any tiles at it's new position.
        final int tileDimensions = Tile.getTileDimensions();

        final int entityX = position.x / tileDimensions;
        final int entityY = position.y / tileDimensions;

        final int minX = Math.max(0, entityX - 2);
        final int minY = Math.max(0, entityY - 2);
        final int maxX = Math.min(entityX + 2, map.getWidth());
        final int maxY = Math.min(entityY + 2, map.getHeight());

        for (int y = minY ; y < maxY ; y++) {
            for (int x = minX ; x < maxX ; x++) {
                final Tile tile = map.getTileAt(x, y);

                // Many of the map's tiles are null, so we can ignore them.
                if (tile == null) {
                    continue;
                }

                // Not all entities are set-up with feet bounding boxes.
                Rectangle entityFeet = self.getBoundingBox("Feet");
                if (entityFeet == null) {
                    continue;
                }

                // Tiles can have multiple body bounding boxes, so we need to check against all of them.
                final Rectangle tileBodyA = tile.getBoundingBox("Body A", x, y);
                final Rectangle tileBodyB = tile.getBoundingBox("Body B", x, y);

                if (tileBodyA != null) {
                    if (entityFeet.intersects(tileBodyA)) {
                        self.getPosition().setLocation(position.x - dx, position.y - dy);
                    }
                } else if (tileBodyB != null) {
                    if (entityFeet.intersects(tileBodyB)) {
                        self.getPosition().setLocation(position.x - dx, position.y - dy);
                    }
                }
            }
        }
    }
}
