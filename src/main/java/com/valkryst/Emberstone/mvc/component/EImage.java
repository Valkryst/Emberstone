package com.valkryst.Emberstone.mvc.component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EImage extends JLabel {
    /**
     * Constructs a new EImage.
     *
     * @param image
     *          The displayed image.
     */
    public EImage(final BufferedImage image) {
        this.setIcon(new ImageIcon(image));

        // Set the size/preferred size
        final Dimension dimensions = new Dimension(image.getWidth(), image.getHeight());
        this.setSize(dimensions);
        this.setPreferredSize(dimensions);
    }
}
