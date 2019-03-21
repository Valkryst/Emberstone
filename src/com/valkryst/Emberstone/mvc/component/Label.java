package com.valkryst.Emberstone.mvc.component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class Label extends Component {
    private final static Cache<Integer, Font> FONT_CACHE = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Getter @Setter private String text;
    private Font font;
    @Setter private Color color;

    public Label(final @NonNull Point position, final String text, final int size, final Color color) {
        super(position);
        this.text = (text == null ? "NO TEXT SET" : text);
        this.color = (color == null ? new Color(255, 216, 0) : color);

        Font font = FONT_CACHE.getIfPresent(size);

        if (font == null) {
            try (final InputStream is = Component.class.getResourceAsStream("/fonts/Deneane.ttf")) {
                this.font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, size);
                FONT_CACHE.put(size, this.font);
            } catch (final IOException | FontFormatException e) {
                e.printStackTrace();
            }
        } else {
            this.font = font;
        }
    }

    public Label(final @NonNull Point position, final String text, final int size) {
        this(position, text, size, new Color(255, 216, 6));
    }

    public Label(final @NonNull Point position, final String text, final Color color) {
        this(position, text, 56, color);
    }

    public Label(final @NonNull Point position, final String text) {
        this(position, text, 56);
    }

    @Override
    public void draw(final Graphics2D gc) {
        final Font previousFont = gc.getFont();

        gc.setColor(color);
        gc.setFont(font);
        gc.drawString(text, super.getX(), super.getY());
        gc.setFont(previousFont);
    }

    @Override
    public boolean intersects(final Point point) {
        return false;
    }
}
