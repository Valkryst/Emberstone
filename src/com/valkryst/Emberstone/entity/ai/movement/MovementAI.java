package com.valkryst.Emberstone.entity.ai.movement;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.map.Tile;
import com.valkryst.Emberstone.statistic.StatisticType;
import rlforj.math.Point;
import rlforj.pathfinding.AStar;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MovementAI {
    private static Cache<Integer, Point[]> PATH_CACHE = Caffeine.newBuilder() .expireAfterAccess(1, TimeUnit.MINUTES).build();

    private Point[] path = new Point[0];
    private int pathPosition = 0;

    public void move(final Entity entity) {
        if (path == null || path.length == 0) {
            return;
        }

        final int startX = entity.getPosition().x / Tile.getTileDimensions();
        final int startY = entity.getPosition().y / Tile.getTileDimensions();
        final Point destination = path[pathPosition];

        if (startX == destination.x && startY == destination.y) {
            if (pathPosition < path.length - 1) {
                pathPosition++;
                move(entity);
                return;
            } else {
                entity.setHorizontalSpeed(0);
                entity.setVerticalSpeed(0);
                return;
            }
        }

        final boolean moveLeft = startX > destination.x;
        final boolean moveRight = startX < destination.x;
        final boolean moveUp = startY > destination.y;
        final boolean moveDown = startY < destination.y;

        if (moveLeft) {
            entity.setHorizontalSpeed(-entity.getStat(StatisticType.SPEED).getValue());
        } else if (moveRight) {
            entity.setHorizontalSpeed(entity.getStat(StatisticType.SPEED).getValue());
        } else if (moveUp) {
            entity.setVerticalSpeed(-entity.getStat(StatisticType.SPEED).getValue());
        } else if (moveDown) {
            entity.setVerticalSpeed(entity.getStat(StatisticType.SPEED).getValue());
        } else {
            entity.setHorizontalSpeed(0);
            entity.setVerticalSpeed(0);
        }
    }

    public void findAndSetPath(final Map map, final java.awt.Point selfPosition, final java.awt.Point targetPosition) {
        final int tileDimensions = Tile.getTileDimensions();

        final int startX = selfPosition.x / tileDimensions;
        final int startY = selfPosition.y / tileDimensions;
        final int endX = targetPosition.x / tileDimensions;
        final int endY = targetPosition.y / tileDimensions ;

        final int hash = Objects.hash(startX, startY, endX, endY);
        Point[] path = PATH_CACHE.getIfPresent(hash);

        if (path != null) {
            if (this.path == path) {
                return;
            } else {
                this.path = path;
            }
        } else {
            final AStar pathFinder = new AStar(map, map.getWidth(), map.getHeight(), false);
            path = pathFinder.findPath(startX, startY, endX, endY, 10);

            if (path == null) {
                this.path = new Point[0];
            } else {
                this.path = path;
            }
        }

        pathPosition = 0;
    }
}
