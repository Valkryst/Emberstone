package com.valkryst.Emberstone.mvc.component;

import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EButton extends JButton {
    /**
     * Constructs a new EButton.
     *
     * @param text
     *          The displayed text.
     *
     * @param textSize
     *          The size of the displayed text.
     */
    public EButton(final String text, final int textSize) {
        this.setText(text);
        this.setForeground(new Color(255, 216, 0));

        this.setHorizontalTextPosition(JButton.CENTER);
        this.setVerticalTextPosition(JButton.CENTER);

        // Set the font.
        this.setFont(EFontFactory.createFont(textSize));

        // Attempt to load and set the button's background
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Button.png", "gui/Button.json");
            final SpriteSheet sheet = atlas.getSpriteSheet("Button");

            Sprite sprite = sheet.getSprite("Normal");
            BufferedImage bufferedImage = sprite.getBufferedImage();
            this.setIcon(new ImageIcon(bufferedImage));

            sprite = sheet.getSprite("Hover");
            bufferedImage = sprite.getBufferedImage();
            this.setRolloverIcon(new ImageIcon(bufferedImage));

            sprite = sheet.getSprite("Pressed");
            bufferedImage = sprite.getBufferedImage();
            this.setPressedIcon(new ImageIcon(bufferedImage));

            // Set the size/preferred size
            final Dimension dimensions = new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
            this.setSize(dimensions);
            this.setPreferredSize(dimensions);

            // Ensure only the icon is displayed.
            this.setBorder(null);
            this.setBorderPainted(false);
            this.setContentAreaFilled(false);
            this.setFocusPainted(false);
            this.setMargin(new Insets(0, 0, 0, 0));


            this.setFocusable(false);
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
