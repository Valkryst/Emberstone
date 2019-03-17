package com.valkryst.Emberstone.mvc.view;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class View extends JPanel {
    /** The background image. */
    @Setter private BufferedImage backgroundImage;

    /** Constructs a new View. */
    public View() {
        this.setLayout(null);
        backgroundImage = null;
    }

    /**
     * Constructs a new View.
     *
     * @param backgroundImage
     *          The background image.
     */
    public View(final BufferedImage backgroundImage) {
        this.setLayout(null);
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(final Graphics gc) {
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, null);
        }
    }
}
