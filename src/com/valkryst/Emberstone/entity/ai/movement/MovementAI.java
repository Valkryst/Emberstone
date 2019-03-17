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

        if (entity.getStat(StatisticType.SPEED) == null) {
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

    public void constructPath(final Map map, final java.awt.Point selfPosition, final java.awt.Point targetPosition) {
        final int tileDimensions = Tile.getTileDimensions();

        final int startX = selfPosition.x / tileDimensions;
        final int startY = selfPosition.y / tileDimensions;
        final int endX = targetPosition.x / tileDimensions;
        final int endY = targetPosition.y / tileDimensions;

        pathPosition = 0;

        final int hash = Objects.hash(startX, startY, endX, endY);
        Point[] path = PATH_CACHE.getIfPresent(hash);

        if (path != null) {
            if (this.path == path) {
                return;
            } else {
                this.path = path;
            }
        }

        /*
         * To save processing power, we can do a quick path without using A*.
         *
         * If the destination is above the starting position and all rows of tiles from the starting position to the
         * destination are free, then the path will say to move directly to the starting position.
         *
         * A similar process is repeated for left, right, and down.
         */

        // Destination directly below current position
        if (startX == endX && startY < endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int y = startY ; y != endY + 1 ; y++) {
                if (map.getTileAt(startX, y).isValidSpawnPoint() == false) {
                    allTilesWalkable = false;
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Destination directly above current position
        if (startX == endX && startY > endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int y = endY ; y != startY + 1 ; y++) {
                if (map.getTileAt(startX, y).isValidSpawnPoint() == false) {
                    allTilesWalkable = false;
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Destination directly left of current position.
        if (startX > endX && startY == endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int x = endX ; x != startX + 1 ; x++) {
                if (map.getTileAt(x, startY).isValidSpawnPoint() == false) {
                    allTilesWalkable = false;
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Destination directly right of current position.
        if (startX < endX && startY == endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int x = startX ; x != endX ; x++) {
                if (map.getTileAt(x, startY).isValidSpawnPoint() == false) {
                    allTilesWalkable = false;
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Destination to the top-left of current position.
        if (startX > endX && startY > endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int y = endY ; y != startY + 1 ; y++) {
                for (int x = endX ; x != startX + 1 ; x++) {
                    if (map.getTileAt(x, y).isValidSpawnPoint() == false) {
                        allTilesWalkable = false;
                        break;
                    }
                }

                if (allTilesWalkable == false) {
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Destination to the top-right of current position.
        if (startX < endX && startY > endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int y = endY ; y != startY + 1 ; y++) {
                for (int x = startX ; x != endX + 1 ; x++) {
                    if (map.getTileAt(x, y).isValidSpawnPoint() == false) {
                        allTilesWalkable = false;
                        break;
                    }
                }

                if (allTilesWalkable == false) {
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Destination to the bottom-left of current position.
        if (startX > endX && startY < endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int y = startY ; y != endY + 1 ; y++) {
                for (int x = endX ; x != startX + 1 ; x++) {
                    if (map.getTileAt(x, y).isValidSpawnPoint() == false) {
                        allTilesWalkable = false;
                        break;
                    }
                }

                if (allTilesWalkable == false) {
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Destination to the bottom-right of current position.
        if (startX < endX && startY < endY) {
            // Check all tiles along the path to ensure there are no obstructions.
            boolean allTilesWalkable = true;

            for (int y = startY ; y != endY + 1 ; y++) {
                for (int x = startX ; x != endX + 1 ; x++) {
                    if (map.getTileAt(x, y).isValidSpawnPoint() == false) {
                        allTilesWalkable = false;
                        break;
                    }
                }

                if (allTilesWalkable == false) {
                    break;
                }
            }

            // Construct path if there are no obstructions.
            if (allTilesWalkable) {
                this.path = new Point[]{new Point(endX, endY)};
                PATH_CACHE.put(hash, this.path);
                return;
            } else {
                this.path = new Point[0];
            }
        }

        // Generate an A* path if all else fails.
        final AStar pathFinder = new AStar(map, map.getWidth(), map.getHeight(), false);
        path = pathFinder.findPath(startX, startY, endX, endY, 5);

        if (path == null) {
            this.path = new Point[0];
        } else {
            this.path = path;
            PATH_CACHE.put(hash, this.path);
        }
    }
}
