package com.valkryst.Emberstone.display;

import com.valkryst.Emberstone.display.model.SettingsModel;
import com.valkryst.VMVC.view.View;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Display {
    /** Singleton instance. */
    private final static Display INSTANCE = new Display();

    @Getter private final JFrame frame = new JFrame();

    /** Constructs a new Display. */
    private Display() {
        final var environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var device = environment.getDefaultScreenDevice();

        final var settings = SettingsModel.getInstance();
        final int width = settings.getViewWidth();
        final int height = settings.getViewHeight();
        boolean isFullscreen = !settings.isWindowed() && device.isFullScreenSupported();

		if (isFullscreen) {
			frame.setUndecorated(true);
		}

        frame.setBackground(Color.BLACK);
        frame.setTitle("Emberstone");
        frame.setResizable(false);
        frame.setIgnoreRepaint(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                if (settings.isWindowed()) {
                    device.setFullScreenWindow(null);
                }

                frame.dispose();
                System.exit(0);
            }
        });
        frame.setVisible(true);
        frame.setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setLocationRelativeTo(null); // Must be called after pack()

        if (isFullscreen) {
            device.setFullScreenWindow(frame);

            /*
             * The initial view will appear black, and blank if we don't repaint
             * after setting the frame to full-screen.
             */
            frame.repaint();
        }

        // todo Load, from the saved settings which renderer to initialize.
        // todo Init a renderer.
    }


	public void setContentPane(final @NonNull View view) {
		frame.setContentPane(view);
		frame.revalidate();

		view.requestFocusInWindow();
	}

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static Display getInstance() {
        return INSTANCE;
    }
}
