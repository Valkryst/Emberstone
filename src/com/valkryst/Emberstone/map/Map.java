package com.valkryst.Emberstone.map;

import com.valkryst.Emberstone.Camera;
import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.entity.Creature;
import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.entity.Player;
import com.valkryst.Emberstone.entity.SpriteType;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.VJSON.VJSON;
import lombok.Getter;
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
    private final static int REGION_DIMENSIONS = 10;
    private final static int MAP_DIMENSIONS = 20;

    private final Tile[][] tiles;

    private final List<Entity> entities = new CopyOnWriteArrayList<>();

    @Getter private Player player;

    @Getter private Camera camera;

    private boolean minimapEnemiesVisible = true;

    public Map(final SpriteType playerSpriteType) {
        tiles = new Tile[REGION_DIMENSIONS * MAP_DIMENSIONS][REGION_DIMENSIONS * MAP_DIMENSIONS];

        // Load Regions
        final Region[][] regions = new Region[REGION_DIMENSIONS][REGION_DIMENSIONS];
        try {
            final JSONObject levelData = VJSON.loadJson("levels/Level2.json");
            final JSONArray rowData = (JSONArray) levelData.get("Regions");

            int y = 0;
            for (final Object columnObj : rowData) {
                final JSONArray columnData = (JSONArray) columnObj;

                for (int x = 0 ; x < columnData.size() ; x++) {
                    final String name = (String) columnData.get(x);
                    regions[y][x] = Region.getRegion(name);
                }

                y++;
            }
        } catch (final IOException | ParseException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Copy Region Tiles onto the Map.
        for (int regionY = 0 ; regionY < REGION_DIMENSIONS ; regionY++) {
            for (int regionX = 0 ; regionX < REGION_DIMENSIONS ; regionX++) {
                final Region region = regions[regionY][regionX];

                if (region == null) {
                    continue;
                }

                for (int tileY = 0 ; tileY < region.getHeight() ; tileY++) {
                    for (int tileX = 0 ; tileX < region.getWidth() ; tileX++) {
                        final int placementX = (regionX * MAP_DIMENSIONS) + tileX;
                        final int placementY = (regionY * MAP_DIMENSIONS) + tileY;
                        tiles[placementY][placementX] = region.getTileAt(tileX, tileY);
                    }
                }
            }
        }

        // Setup player
        try {
            final int tileDimensions = Tile.getTileDimensions();

            final SpriteAtlas atlas = playerSpriteType.getSpriteAtlas();
            player = new Player(new Point(35 * tileDimensions, 35 * tileDimensions), atlas.getSpriteSheet("Entity"));
            addEntity(player);
            camera = new Camera(tiles[0].length * tileDimensions, tiles.length * tileDimensions, player);
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Setup entities
        try {
            final int tileDimensions = Tile.getTileDimensions();

            for (int i = 0 ; i < 200 ; i++) {
                int x = ThreadLocalRandom.current().nextInt(0, REGION_DIMENSIONS * MAP_DIMENSIONS);
                int y = ThreadLocalRandom.current().nextInt(0, REGION_DIMENSIONS * MAP_DIMENSIONS);

                while (tiles[y][x] == null || tiles[y][x].isValidSpawnPoint() == false) {
                    x = ThreadLocalRandom.current().nextInt(0, REGION_DIMENSIONS * MAP_DIMENSIONS);
                    y = ThreadLocalRandom.current().nextInt(0, REGION_DIMENSIONS * MAP_DIMENSIONS);
                }

                switch (ThreadLocalRandom.current().nextInt(0, 6)) {
                    case 0: {
                        addEntity(new Creature(new Point(x * tileDimensions, y * tileDimensions), SpriteType.SKELETON_HEAVY.getSpriteAtlas().getSpriteSheet("Entity")));
                        break;
                    }
                    case 1: {
                        addEntity(new Creature(new Point(x * tileDimensions, y * tileDimensions), SpriteType.SKELETON_LIGHT.getSpriteAtlas().getSpriteSheet("Entity")));
                        break;
                    }
                    case 2: {
                        addEntity(new Creature(new Point(x * tileDimensions, y * tileDimensions), SpriteType.SKELETON_CULTIST.getSpriteAtlas().getSpriteSheet("Entity")));
                        break;
                    }
                    case 3: {
                        addEntity(new Creature(new Point(x * tileDimensions, y * tileDimensions), SpriteType.ZOMBIE_FARMER.getSpriteAtlas().getSpriteSheet("Entity")));
                        break;
                    }
                    case 4: {
                        addEntity(new Creature(new Point(x * tileDimensions, y * tileDimensions), SpriteType.ZOMBIE_VILLAGER.getSpriteAtlas().getSpriteSheet("Entity")));
                        break;
                    }
                    case 5: {
                        addEntity(new Creature(new Point(x * tileDimensions, y * tileDimensions), SpriteType.ZOMBIE_WOODCUTTER.getSpriteAtlas().getSpriteSheet("Entity")));
                        break;
                    }
                }
            }
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
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
                entity.draw(gc, camera);
            }
        });

        // Draw tiles on minimap
        final int minimapX = Game.getInstance().getCanvasWidth() - 213;
        final int minimapY = 13;

        gc.setColor(Color.BLACK);
        gc.fillRect(minimapX, minimapY, 200, 200);

        for (int y = 0 ; y < tiles[0].length ; y++) {
            for (int x = 0 ; x < tiles.length ; x++) {
                if (tiles[y][x] != null) {
                    if (tiles[y][x].isValidSpawnPoint()) {
                        gc.setColor(Color.GRAY);
                    } else {
                        gc.setColor(Color.DARK_GRAY);
                    }


                    gc.fillRect(minimapX + x, minimapY + y, 2, 2);
                }
            }
        }

        // Draw entities on minimap.
        if (minimapEnemiesVisible) {
            for (final Entity entity : entities) {
                if (entity instanceof Player) {
                    gc.setColor(Color.CYAN);
                } else {
                    gc.setColor(Color.RED);
                }

                final Point position = entity.getPosition();
                final int x = minimapX + (position.x / tileDimensions);
                final int y = minimapY + (position.y / tileDimensions);
                gc.fillRect(x, y, 1, 1);
            }
        }

        if (Settings.getInstance().isDebugModeOn()) {
            gc.setColor(Color.MAGENTA);
            gc.drawString("Total Entities: " + entities.size(), 16, 32);
            gc.drawString("Player Position: (" + playerX + "x, " + playerY + "y)", 16, 48);
        }
    }

    public boolean addEntity(final Entity entity) {
        if (entity == null || entities.contains(entity)) {
            return false;
        }

        // Check if the entity would collide with a tile if placed.
        final int tileDimensions = Tile.getTileDimensions();

        for (int y = 0 ; y < tiles.length ; y++) {
            for (int x = 0 ; x < tiles[0].length ; x++) {
                if (tiles[y][x] == null) {
                    continue;
                }

                Rectangle entityFeet = entity.getFeetBoundingBox();

                if (entityFeet == null) {
                    continue;
                }

                final Rectangle tileBodyA = tiles[y][x].getBoundingBoxA(x * tileDimensions, y * tileDimensions);
                final Rectangle tileBodyB = tiles[y][x].getBoundingBoxB(x * tileDimensions, y * tileDimensions);

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
