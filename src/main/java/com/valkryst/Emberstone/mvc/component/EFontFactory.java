package com.valkryst.Emberstone.mvc.component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class EFontFactory {
    /** Path to the font file. */
    private final static String FONT_PATH = "/fonts/Deneane.ttf";
    /** Cache of recently created fonts. */
    private final static Cache<Integer, Font> FONT_CACHE = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    /**
     * Creates a copy of the game's font at a specified size.
     *
     * @param textSize
     *          The text size.
     *
     * @return
     *          The font.
     */
    public static Font createFont(final int textSize) {
        Font font = FONT_CACHE.getIfPresent(textSize);

        if (font == null) {
            try (final InputStream is = EFontFactory.class.getResourceAsStream(FONT_PATH)) {
                font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, textSize);
                FONT_CACHE.put(textSize, font);
            } catch (final IOException | FontFormatException e) {
                e.printStackTrace();
            }
        }

        return font;
    }
}
