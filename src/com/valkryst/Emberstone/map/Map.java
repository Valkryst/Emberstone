package com.valkryst.Emberstone.map;

import com.valkryst.Emberstone.Camera;
import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.entity.*;
import com.valkryst.Emberstone.mvc.component.EFontFactory;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.VJSON.VJSON;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import rlforj.IBoard;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Map implements IBoard {
    private final static int MAP_DIMENSIONS = 10;

    private final Tile[][] tiles = new Tile[MAP_DIMENSIONS * Region.getRegionDimensions()][MAP_DIMENSIONS * Region.getRegionDimensions()];

    private final List<Entity> entities = new CopyOnWriteArrayList<>();

    @Getter private Player player;

    @Getter private Camera camera;

    private boolean minimapEnemiesVisible = true;

    @Getter @Setter private int shardsGathered = 0;
    @Getter @Setter private int shardsRequired = 20;

    @Getter @Setter private boolean portalSpawned = false;

    public Map(final Player player, final String mapJsonFilePath, final int maxEntities) {
        this.player = player;

        // Load regions and copy them onto the map.
        try {
            final JSONObject levelData = VJSON.loadJson(mapJsonFilePath);

            int column = 0;
            int row = 0;

            for (final Object rowArray : (JSONArray) levelData.get("Regions")) {
                for (final Object columnObject : (JSONArray) rowArray) {
                    final Region region = Region.getRegion((String) columnObject);
                    if (region != null) {
                        region.copyOntoMap(tiles, column, row);
                    }

                    column++;
                }

                column = 0;
                row++;
            }
        } catch (final IOException | ParseException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Setup player
        final int tileDimensions = Tile.getTileDimensions();
        this.player.getPosition().setLocation(21 * tileDimensions, 21 * tileDimensions);
        addEntity(player);
        camera = new Camera(tiles[0].length * tileDimensions, tiles.length * tileDimensions, player);

        // Setup entities
        final int regionDimensions = Region.getRegionDimensions();

        for (int i = 0 ; i < maxEntities ; i++) {
            while (true) {
                int x = 0;
                int y = 0;

                while (tiles[y][x] == null || tiles[y][x].isWalkable() == false) {
                    x = ThreadLocalRandom.current().nextInt(25, MAP_DIMENSIONS * regionDimensions);
                    y = ThreadLocalRandom.current().nextInt(0, MAP_DIMENSIONS * regionDimensions);
                }

                if (addEntity(Creature.createCreature(new Point(x * tileDimensions, y * tileDimensions)))) {
                    break;
                }
            }
        }
    }

    public void update(final double deltaTime) {
        for (final Entity entity : entities) {
            entity.update(this, deltaTime);
        }

        camera.update(deltaTime);
    }

    public void draw(final Graphics2D gc) {
        // Draw all tiles within a certain distance of the player.
        final int tileDimensions = Tile.getTileDimensions();

        final Point playerPosition = player.getPosition();
        final int playerX = playerPosition.x / tileDimensions;
        final int playerY = playerPosition.y / tileDimensions;

        final int minX = Math.max(0, playerX - 8); // The +/- values are all arbitrary, they work well enough.
        final int minY = Math.max(0, playerY - 6);
        final int maxX = Math.min(playerX + 15, tiles[0].length);
        final int maxY = Math.min(playerY + 9, tiles.length);

        for (int y = minY ; y < maxY ; y++) {
            for (int x = minX ; x < maxX ; x++) {
                if (tiles[y][x] == null) {
                    continue;
                }

                // The min/max calculations aren't parfect, so we should still check each tile to ensure it's
                // actually on-screen.
                if (camera.isInView(x * tileDimensions, y * tileDimensions, tileDimensions, tileDimensions)) {
                    tiles[y][x].draw(gc, x, y, camera);
                }
            }
        }

        // Sort the list of entities, so that they're rendered in the proper Z-order.
        Collections.sort(entities);
        entities.forEach(entity -> {
            // todo This could be further optimized by only iterating over the entities nearest to the player.
            // Draw only visible entities.
            if (camera.isInView(entity)) {
                // Generate an inventory for the creature, when it first comes into view.
                if (entity instanceof Creature) {
                    if (((Creature) entity).isStatsGenerated() == false) {
                        ((Creature) entity).generateStats(player);
                    }
                }

                entity.draw(gc, camera);
            }
        });

        // Draw red filter depending on the player's health.
        final BoundStatistic playerHealth = (BoundStatistic) player.getStat(StatisticType.HEALTH);
        if (playerHealth.getValue() < playerHealth.getMaxValue()) {
            gc.setColor(new Color(255, 0, 0, (int) (64 - ((playerHealth.getValue() / (double) playerHealth.getMaxValue()) * 64))));
            gc.fillRect(0, 0, 1920, 1080);
        }

        // Draw tiles on minimap
        final int minimapX = Game.getInstance().getViewWidth() - 413;
        final int minimapY = 13;

        gc.setColor(new Color(0, 0, 0, 128));
        gc.fillRect(minimapX, minimapY, 400, 400);

        for (int y = 0 ; y < tiles[0].length ; y++) {
            for (int x = 0 ; x < tiles.length ; x++) {
                if (tiles[y][x] != null) {
                    if (tiles[y][x].isWalkable()) {
                        gc.setColor(Color.GRAY);
                    } else {
                        gc.setColor(Color.DARK_GRAY);
                    }


                    gc.fillRect(minimapX + (x * 2), minimapY + (y * 2), 2, 2);
                }
            }
        }

        // Draw entities on minimap.
        if (minimapEnemiesVisible) {
            for (final Entity entity : entities) {
                if (entity.getAnimationState() == AnimationState.DYING) {
                    continue;
                }

                if (entity instanceof Player) {
                    gc.setColor(Color.CYAN);
                } else if (entity instanceof Chest) {
                    gc.setColor(Color.YELLOW);
                } else if (entity instanceof Portal) {
                    gc.setColor(Color.CYAN);
                } else {
                    gc.setColor(Color.RED);
                }

                final Point position = entity.getPosition();
                final int x = minimapX + (position.x / tileDimensions) * 2;
                final int y = minimapY + (position.y / tileDimensions) * 2;
                gc.fillRect(x, y, 2, 2);
            }
        }

        // Draw Shards Collected
        gc.setColor(Color.WHITE);
        gc.setFont(EFontFactory.createFont(28));
        gc.drawString("Phylactery Shards Collected: " + shardsGathered + "/" + shardsRequired, 1470, 450);

        if (Settings.getInstance().isDebugModeOn()) {
            gc.setColor(Color.MAGENTA);
            gc.setFont(EFontFactory.createFont(12));
            gc.drawString("Total Entities: " + entities.size(), 16, 32);
            gc.drawString("Player Position: (" + playerX + "x, " + playerY + "y)", 16, 48);
        }
    }

    public boolean addEntity(final Entity entity) {
        if (entity == null || entities.contains(entity)) {
            return false;
        }

        // Check if the entity would collide with a tile if placed.
        for (int y = 0 ; y < tiles.length ; y++) {
            for (int x = 0 ; x < tiles[0].length ; x++) {
                if (tiles[y][x] == null) {
                    continue;
                }

                Rectangle entityFeet = entity.getBoundingBox("Feet");

                if (entityFeet == null) {
                    continue;
                }

                final Rectangle tileBodyA = tiles[y][x].getBoundingBox("Body A", x, y);
                final Rectangle tileBodyB = tiles[y][x].getBoundingBox("Body B", x, y);

                if (tileBodyA != null) {
                    if (entityFeet.intersects(tileBodyA)) {
                        return false;
                    }
                }

                if (tileBodyB != null) {
                    if (entityFeet.intersects(tileBodyB)) {
                        return false;
                    }
                }
            }
        }

        entities.add(entity);
        return true;
    }

    public void removeEntity(final Entity entity) {
        entities.remove(entity);
    }

    public Iterator<Entity> getEntitiesIterator() {
        return entities.iterator();
    }

    /**
     * Retrieves a tile from the map.
     *
     * @param x
     *          The location of the tile on the x-axis.
     *
     * @param y
     *          The location of the tile on the y-axis.
     *
     * @return
     *          The tile.
     */
    public Tile getTileAt(final int x, final int y) {
        if (x < 0 || y < 0) {
            return null;
        }

        if (x > getWidth() || y > getHeight()) {
            return null;
        }

        return tiles[y][x];
    }

    /**
     * Retrieves the map's width, in tiles.
     *
     * @return
     *          The map's width, in tiles.
     */
    public int getWidth() {
        return tiles[0].length;
    }

    /**
     * Retrieves the map's height, in tiles.
     *
     * @return
     *          The map's height, in tiles.
     */
    public int getHeight() {
        return tiles.length;
    }

    @Override
    public boolean contains(final int x, final int y) {
        if (x < 0 || y < 0) {
            return false;
        }

        if (x > getWidth() || y > getWidth()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean blocksLight(final int x, final int y) {
        return false;
    }

    @Override
    public boolean blocksStep(final int x, final int y) {
        return false;
    }

    @Override
    public void visit(final int x, final int y) {}
}
