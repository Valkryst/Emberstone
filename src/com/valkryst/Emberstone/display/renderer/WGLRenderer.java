package com.valkryst.Emberstone.display.renderer;

import java.awt.*;
import java.awt.peer.ComponentPeer;

public class WGLRenderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.awt.windows.WComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.opengl.WGLSurfaceData";

    public WGLRenderer(final ComponentPeer peer) throws UnsupportedOperationException {
        super(peer, WGLRenderer.class);
    }

    /**
     * Determines whether or not the WGLRenderer is supported on this machine.
     *
     * @return
     *          Whether the WGLRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(WGLRenderer.class);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var win32GraphicsDevice = Class.forName("sun.awt.Win32GraphicsDevice");
            final var win32GraphicsConfig = Class.forName("sun.awt.Win32GraphicsConfig");
            final var wglGraphicsConfig = Class.forName("sun.java2d.opengl.WGLGraphicsConfig");

            // Get the visual id.
            final int visualId = (int) win32GraphicsConfig.getMethod("getVisual").invoke(currentGraphicsConfiguration);

            // Gets the method to get the GraphicsConfiguration for Windows OpenGL
            final var getConfigMethod = wglGraphicsConfig.getMethod("getConfig", win32GraphicsDevice, int.class);

            // Get the graphics configuration for the graphics device and visual id.
            return (GraphicsConfiguration) getConfigMethod.invoke(null, currentGraphicsConfiguration.getDevice(), visualId);
        } catch (final Exception e) {
            // todo Tons of exceptions can be thrown in the above code, catch it or something.
            // Otherwise, we fall back to the default and hope for the best.
            e.printStackTrace();
            return super.getGraphicsConfig();
        }
    }

    @Override
    public String getName() {
        return "DirectDraw";
    }
}
