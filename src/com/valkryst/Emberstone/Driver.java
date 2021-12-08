package com.valkryst.Emberstone;

import com.valkryst.Emberstone.display.controller.DefaultController;

import javax.swing.*;

public class Driver {
    public static void main(final String[] args) {
        // todo Test these settings when the game is more playable. They don't seem to affect anything.
        // Disable OpenGL and enable Direct3D is the only way Direct3D renderer can work.
        // The OpenGL renderer doesn't seem to care if it is disabled. It works just fine.
        System.setProperty("sun.java2d.d3d", "true");
        System.setProperty("sun.java2d.opengl", "false");

		SwingUtilities.invokeLater(() -> {
			final var controller = new DefaultController();
			controller.displayMainMenuView(null);
		});
    }
}
