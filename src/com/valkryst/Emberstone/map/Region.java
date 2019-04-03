package com.valkryst.Emberstone.map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.entity.SpriteType;
import com.valkryst.V2DSprite.AnimatedSprite;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import com.valkryst.VJSON.VJSON;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Region {
    private final static int REGION_DIMENSIONS = 20;

    private final static Cache<String, Region> REGIONS = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    /** The cache of recently loaded SpriteAtlases. */
    private final static Cache<String, SpriteAtlas> ATLAS_CACHE = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Getter private final String name;
    private final Tile[][] tiles;

    private Region(final JSONObject data) {
        name = VJSON.getString(data, "Name");
        tiles = new Tile[REGION_DIMENSIONS][REGION_DIMENSIONS];

        // Load Tileset
        final String tilesetName = VJSON.getString(data, "Tileset");

        if (ATLAS_CACHE.getIfPresent(tilesetName) == null) {
            try {
                final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("tilesets/" + tilesetName + ".png", "tilesets/" + tilesetName + ".json");
                ATLAS_CACHE.put(tilesetName, atlas);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        // Load Tiles
        for (final Object tileObj : (JSONArray) data.get("Tiles")) {
            final JSONObject tileData = (JSONObject) tileObj;

            final int x = VJSON.getInt(tileData, "X");
            final int y = VJSON.getInt(tileData, "Y");

            final String spriteName = VJSON.getString(tileData, "Sprite Name");

            final SpriteAtlas spriteAtlas = ATLAS_CACHE.getIfPresent(tilesetName);
            if (spriteAtlas == null) {
                if (Settings.getInstance().isDebugModeOn()) {
                    System.out.println("The tile (" + x + ", " + y + ") of region " + name + " is using an unknown '" + tilesetName + "' tileset.");
                }
            }

            final SpriteSheet spriteSheet = spriteAtlas.getSpriteSheet("Tiles");
            final Sprite sprite = spriteSheet.getSprite(spriteName);
            final AnimatedSprite animatedSprite = spriteSheet.getAnimatedSprite(spriteName);

            if (sprite != null) {
                tiles[y][x] = new Tile(sprite, tileData);
            }

            if (animatedSprite != null) {
                tiles[y][x] = new Tile(animatedSprite, tileData);
            }
        }
    }

    public static Region getRegion(final String name) {
        Region region = REGIONS.getIfPresent(name);

        if (region != null) {
            return region;
        }

        try {
            final JSONObject regionsJson = VJSON.loadJson("levels/Regions.json");

            for (final Object regionObj : (JSONArray) regionsJson.get("Regions")) {
                final JSONObject regionData = (JSONObject) regionObj;

                if (VJSON.getString(regionData, "Name").equals(name)) {
                    region = new Region(regionData);
                    REGIONS.put(region.getName(), region);
                }
            }
        } catch (final IOException | ParseException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return region;
    }

    /**
     * Copies the region's tiles onto a map's tiles.
     *
     * @param mapTiles
     *          The map's tiles.
     *
     * @param startX
     *          The x-axis position, on the map, of the region's top-left corner.
     *
     * @param startY
     *          The y-axis position, on the map, of the region's top-left corner.
     */
    public void copyOntoMap(final Tile[][] mapTiles, int startX, int startY) {
        startX *= REGION_DIMENSIONS;
        startY *= REGION_DIMENSIONS;
        final int endX = startX + REGION_DIMENSIONS;
        final int endY = startY + REGION_DIMENSIONS;

        for (int y = startY ; y < endY ; y++) {
            if (endX - startX >= 0) {
                System.arraycopy(this.tiles[y - startY], 0, mapTiles[y], startX, endX - startX);
            }
        }
    }

    public static int getRegionDimensions() {
        return REGION_DIMENSIONS;
    }
}
