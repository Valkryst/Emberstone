package com.valkryst.Emberstone.mvc.component;

import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ERadioButton extends JRadioButton {
    private static SpriteSheet spriteSheet;

    /**
     * Constructs a new ERadioButton.
     *
     * @param text
     *          The displayed text.
     *
     * @param color
     *          The size of the displayed text.
     *
     * @param buttonGroup
     *          The radio button's button group.
     */
    public ERadioButton(final String text, final int textSize, final Color color, final ButtonGroup buttonGroup) {
        this.setText(text);
        this.setForeground(color);
        buttonGroup.add(this);

        // Load the sprite sheet if necessary.
        if (spriteSheet == null) {
            try {
                final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Radio Button.png", "gui/Radio Button.json");
                spriteSheet = atlas.getSpriteSheet("Radio Button");
            } catch (final ParseException | IOException ex) {
                ex.printStackTrace();
            }
        }

        // Set the font.
        this.setFont(EFontFactory.createFont(textSize));

        // Set the size/preferred size
        final Dimension dimensions = new Dimension(text.length() * textSize, textSize);
        this.setSize(dimensions);
        this.setPreferredSize(dimensions);

        // Set the selection icons.
        updateIcon();
        this.addItemListener(e -> {
            switch (e.getStateChange()) {
                case ItemEvent.SELECTED: {
                    updateIcon();
                    break;
                }
                case ItemEvent.DESELECTED: {
                    updateIcon();
                    break;
                }
            }
        });

        // Ensure only the icon is displayed.
        this.setBorder(null);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setContentAreaFilled(false);
        this.setMargin(new Insets(0, 0, 0, 0));

        // Ensure view key listeners still work after clicking a radio button.
        this.setFocusable(false);
    }

    /** Updates the icon based on the selection state of the radio button. */
    private void updateIcon() {
        if (this.isSelected()) {
            final Sprite sprite = spriteSheet.getSprite("Selected");
            BufferedImage bufferedImage = sprite.getBufferedImage();
            this.setIcon(new ImageIcon(bufferedImage));
        } else {
            final Sprite sprite = spriteSheet.getSprite("Unselected");
            BufferedImage bufferedImage = sprite.getBufferedImage();
            this.setIcon(new ImageIcon(bufferedImage));
        }
    }
}
