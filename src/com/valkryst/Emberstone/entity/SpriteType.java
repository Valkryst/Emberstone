package com.valkryst.Emberstone.entity;

import com.valkryst.V2DSprite.SpriteAtlas;
import lombok.Getter;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public enum SpriteType {
    PLAYER_A("entities/player/A/Image.png", "entities/player/Data.json"),
    PLAYER_B("entities/player/B/Image.png", "entities/player/Data.json"),
    PLAYER_C("entities/player/C/Image.png", "entities/player/Data.json"),
    SKELETON_HEAVY("entities/skeleton/Heavy/Image.png", "entities/skeleton/Heavy/Data.json");

    @Getter private final String pngPath;
    @Getter private final String jsonPath;

    SpriteType(final String pngPath, final String jsonPath) {
        // todo Add missing file and null handling
        this.pngPath = pngPath;
        this.jsonPath = jsonPath;
    }

    public SpriteAtlas getSpriteAtlas() throws IOException, ParseException {
        return SpriteAtlas.createSpriteAtlas(pngPath, jsonPath);
    }
}
