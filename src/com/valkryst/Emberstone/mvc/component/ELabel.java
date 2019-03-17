package com.valkryst.Emberstone.mvc.component;

import javax.swing.*;
import java.awt.*;

public class ELabel extends JLabel {
    /**
     * Constructs a new ELabel.
     *
     * @param text
     *          The displayed text.
     *
     * @param color
     *          The size of the displayed text.
     */
    public ELabel(final String text, final int textSize, final Color color) {
        this.setText(text);
        this.setForeground(color);

        // Set the font.
        this.setFont(EFontFactory.createFont(textSize));

        // Set the size/preferred size
        final Dimension dimensions = new Dimension(text.length() * textSize, textSize);
        this.setSize(dimensions);
        this.setPreferredSize(dimensions);

        // Ensure only the icon is displayed.
        this.setBorder(null);
    }
}
