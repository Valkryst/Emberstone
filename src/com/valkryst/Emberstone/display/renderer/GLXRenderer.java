package com.valkryst.Emberstone.display.renderer;

import java.awt.*;
import java.awt.peer.ComponentPeer;

public class GLXRenderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.awt.X11ComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.x11.X11SurfaceData";

    public GLXRenderer(final ComponentPeer peer) throws UnsupportedOperationException {
        super(peer, GLXRenderer.class);
    }

    /**
     * Determines whether or not the GLXRenderer is supported on this machine.
     *
     * @return
     *          Whether the GLXRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        boolean isSupported = Renderer.isSupported(GLXRenderer.class);

        try {
            final var x11GraphicsEnvironment = Class.forName("sun.awt.X11GraphicsEnvironment");
            final var isGLXAvailableMethod = x11GraphicsEnvironment.getMethod("isGLXAvailable");
            isSupported &= (boolean) isGLXAvailableMethod.invoke(null);
        } catch (final Exception e) {
            isSupported = false;
        }

        return isSupported;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var glxGraphicsConfig = Class.forName("sun.java2d.opengl.GLXGraphicsConfig");
            final var x11GraphicsDevice = Class.forName("sun.awt.X11GraphicsDevice");
            final var x11GraphicsConfig = Class.forName("sun.awt.X11GraphicsConfig");

            final var getVisualMethod = x11GraphicsConfig.getMethod("getVisual");
            final int visual = (int) getVisualMethod.invoke(currentGraphicsConfiguration);

            final var getConfigMethod = glxGraphicsConfig.getMethod("getConfig", x11GraphicsDevice, int.class);
            return (GraphicsConfiguration) getConfigMethod.invoke(null, currentGraphicsConfiguration.getDevice(), visual);
        } catch (final Exception e) {
            // todo Tons of exceptions can be thrown in the above code, catch it or something.
            // Otherwise, we fall back to the default and hope for the best.
            e.printStackTrace();
            return super.getGraphicsConfig();
        }
    }

    @Override
    public String getName() {
        return "GLX";
    }
}
