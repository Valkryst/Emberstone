package com.valkryst.Emberstone.map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
    /** The cache of recently played audio clips. */
    private final static Cache<String, Region> REGIONS = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Getter private final int width;
    @Getter private final int height;
    @Getter private final String name;
    private final Tile[][] tiles;

    private final ConnectorDirection[] connectorDirections;

    @Getter private final Rectangle[] spawnRegions;

    private Region(final int width, final int height, final JSONObject data) {
        this.width = width;
        this.height = height;
        name = VJSON.getString(data, "Name");
        tiles = new Tile[height][width];

        // Load Tilesets
        final HashMap<String, SpriteSheet> tilesets = new HashMap<>();

        for (final Object tilesetObj : (JSONArray) data.get("Tilesets")) {
            String name = (String) tilesetObj;

            try {
                final SpriteAtlas spriteAtlas = SpriteAtlas.createSpriteAtlas("tilesets/" + name + ".png", "tilesets/" + name + ".json");
                final SpriteSheet spriteSheet = spriteAtlas.getSpriteSheet("Tiles");
                tilesets.put(name, spriteSheet);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        // Load Connector Directions
        final JSONArray connectorData = (JSONArray) data.get("Connectors"); // todo Throw error if a region has no connectors.
        connectorDirections = new ConnectorDirection[connectorData.size()];

        for (int i = 0 ; i < connectorData.size() ; i++) {
            connectorDirections[i] = ConnectorDirection.valueOf((String) connectorData.get(i));
        }

        // Load Spawn Regions
        final JSONArray spawnRegionsData = (JSONArray) data.get("Spawn Regions"); // todo Add handling for when these are missing.

        if (spawnRegionsData != null) {
            spawnRegions = new Rectangle[spawnRegionsData.size()];

            for (int i = 0; i < spawnRegionsData.size(); i++) {
                final JSONObject spawnRegionData = (JSONObject) spawnRegionsData.get(i);
                final int x = VJSON.getInt(spawnRegionData, "X");
                final int y = VJSON.getInt(spawnRegionData, "Y");
                final int w = VJSON.getInt(spawnRegionData, "Width");
                final int h = VJSON.getInt(spawnRegionData, "Height");
                spawnRegions[i] = new Rectangle(x, y, w, h);
            }
        } else {
            spawnRegions = new Rectangle[0];
        }

        // Load Tiles
        for (final Object tileObj : (JSONArray) data.get("Tiles")) {
            final JSONObject tileData = (JSONObject) tileObj;

            final int x = VJSON.getInt(tileData, "X");
            final int y = VJSON.getInt(tileData, "Y");

            final String tileset = VJSON.getString(tileData, "Tileset");
            final String spriteName = VJSON.getString(tileData, "Sprite Name");

            final SpriteSheet spriteSheet = tilesets.get(tileset);
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

            final int width = VJSON.getInt(regionsJson, "Width");
            final int height = VJSON.getInt(regionsJson, "Height");

            for (final Object regionObj : (JSONArray) regionsJson.get("Regions")) {
                final JSONObject regionData = (JSONObject) regionObj;

                if (VJSON.getString(regionData, "Name").equals(name)) {
                    region = new Region(width, height, regionData);
                    REGIONS.put(region.getName(), region);
                }
            }
        } catch (final IOException | ParseException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return region;
    }

    public boolean hasConnector(final ConnectorDirection connectorDirection) {
        for (int i = 0 ; i <connectorDirections.length ; i++) {
            if (connectorDirections[i] == connectorDirection) {
                return true;
            }
        }

        return false;
    }

    public Tile getTileAt(final int x, final int y) {
        return tiles[y][x];
    }
}
