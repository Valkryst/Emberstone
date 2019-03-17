package com.valkryst.Emberstone.mvc.component;

import com.valkryst.V2DSprite.Sprite;
import lombok.NonNull;

import java.awt.*;

public class Image extends Component {
    private final Sprite sprite;

    public Image(final @NonNull Point position, final Sprite sprite) {
        super(position);
        this.sprite = sprite;
    }

    @Override
    public void draw(final Graphics2D gc) {
        sprite.draw(gc, super.position.x, super.position.y);
    }

    @Override
    public boolean intersects(final Point point) {
        return false;
    }
}
