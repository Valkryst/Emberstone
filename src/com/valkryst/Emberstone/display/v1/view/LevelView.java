package com.valkryst.Emberstone.display.v1.view;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.valkryst.Emberstone.component.PositionComponent;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LevelView extends View {
    @Getter private final Canvas canvas = new Canvas();

    public LevelView(final JFrame frame, final Dimension dimensions) {
        canvas.setPreferredSize(dimensions);
        frame.add(canvas);
        frame.pack();
        frame.requestFocus();
        frame.repaint();
        canvas.requestFocusInWindow();
    }

    public void draw(final Engine engine) {
        final var bufferStrategy = canvas.getBufferStrategy();

        final var positions = ComponentMapper.getFor(PositionComponent.class);

        do {
            do {
                try {
                    final var gc = (Graphics2D) bufferStrategy.getDrawGraphics();
                    setRenderingHints(gc);

                    gc.setColor(Color.BLACK);
                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                    gc.setColor(Color.RED);
                    engine.getEntities().forEach(e -> {
                        final var position = positions.get(e);
                        gc.fillRect((int) position.getX(), (int) position.getY(), 32, 32);
                    });

                    gc.dispose();
                } catch(final NullPointerException | IllegalArgumentException e) {
                    if (bufferStrategy == null) {
                        try {
                            canvas.createBufferStrategy(2);
                        } catch(final IllegalStateException ex) {
                            return;
                        }

                        draw(engine);
                        return;
                    }
                }
            } while(bufferStrategy.contentsRestored()); // Repeat render if drawing buffer contents were restored.

            try {
                bufferStrategy.show();
            } catch(final IllegalStateException ignored) {
                // Occurs when the program is closed while the screen is rendering.
            }
        } while(bufferStrategy.contentsLost()); // Repeat render if drawing buffer was lost.
    }

    private void setRenderingHints(final Graphics2D gc) {
        // Whether to bias algorithm choices more for speed or quality when evaluating tradeoffs.
        gc.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        // Controls how closely to approximate a color when storing into a destination with limited
        // color resolution.
        gc.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        // Controls the accuracy of approximation and conversion when storing colors into a
        // destination image or surface.
        gc.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        // Controls how image pixels are filtered or resampled during an image rendering operation.
        gc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Controls whether or not the geometry rendering methods of a Graphics2D object will
        // attempt to reduce aliasing artifacts along the edges of shapes.
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // This code sets the appropriate fractional metrics/anti aliasing
        final Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        gc.setRenderingHints(desktopHints);

        // A general hint that provides a high levels recommendation as to whether to bias alpha
        // blending algorithm choices more for speed or quality when evaluating tradeoffs.
        gc.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

        // Ensure only the visible portions of the canvas are drawn on.
        gc.setClip(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
