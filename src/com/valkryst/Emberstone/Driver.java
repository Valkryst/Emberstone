package com.valkryst.Emberstone;

import com.valkryst.Emberstone.display.controller.DefaultController;
import com.valkryst.Emberstone.display.renderer.*;

public class Driver {
    public static void main(final String[] args) {
        // todo Test these settings when the game is more playable. They don't seem to affect anything.
        // Disable OpenGL and enable Direct3D is the only way Direct3D renderer can work.
        // The OpenGL renderer doesn't seem to care if it is disabled. It works just fine.
        //System.setProperty("sun.java2d.d3d", "true");
        //System.setProperty("sun.java2d.opengl", "false");

        final var preferences = Settings.getInstance();

        final var controller = new DefaultController();
        controller.displayMainMenuView(null);

        System.out.println("D3DRenderer Supported: " + D3DRenderer.isSupported());
        System.out.println("GDIRenderer Supported: " + GDIRenderer.isSupported());
        System.out.println("GLXRenderer Supported: " + GLXRenderer.isSupported());
        System.out.println("LWRenderer Supported: " + LWRenderer.isSupported());
        System.out.println("WGLRenderer Supported: " + WGLRenderer.isSupported());
        System.out.println("X11Renderer Supported: " + X11Renderer.isSupported());
        System.out.println("XRenderer Supported: " + XRenderer.isSupported());

    }
}
