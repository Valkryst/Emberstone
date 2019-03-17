package com.valkryst.Emberstone.entity;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.valkryst.V2DSprite.SpriteAtlas;
import lombok.Getter;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public enum SpriteType {
    PLAYER_A("entities/player/A/Image.png", "entities/player/Data.json"),
    PLAYER_B("entities/player/B/Image.png", "entities/player/Data.json"),
    PLAYER_C("entities/player/C/Image.png", "entities/player/Data.json"),
    SKELETON_HEAVY("entities/skeleton/Heavy/Image.png", "entities/skeleton/Heavy/Data.json"),
    SKELETON_LIGHT("entities/skeleton/Light/Image.png", "entities/skeleton/Light/Data.json"),
    SKELETON_CULTIST("entities/skeleton/Cultist/Image.png", "entities/skeleton/Cultist/Data.json"),
    ZOMBIE_FARMER("entities/zombie/Farmer/Image.png", "entities/zombie/Farmer/Data.json"),
    ZOMBIE_VILLAGER("entities/zombie/Villager/Image.png", "entities/zombie/Villager/Data.json"),
    ZOMBIE_WOODCUTTER("entities/zombie/Woodcutter/Image.png", "entities/zombie/Woodcutter/Data.json");

    /** The cache of recently loaded SpriteAtlases. */
    private final static Cache<SpriteType, SpriteAtlas> ATLAS_CACHE = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Getter private final String pngPath;
    @Getter private final String jsonPath;

    SpriteType(final String pngPath, final String jsonPath) {
        // todo Add missing file and null handling
        this.pngPath = pngPath;
        this.jsonPath = jsonPath;
    }

    public SpriteAtlas getSpriteAtlas() throws IOException, ParseException {
        SpriteAtlas atlas = ATLAS_CACHE.getIfPresent(this);

        if (atlas != null) {
            return atlas;
        } else {
            atlas = SpriteAtlas.createSpriteAtlas(pngPath, jsonPath);
            ATLAS_CACHE.put(this, atlas);
            return atlas;
        }
    }
}
