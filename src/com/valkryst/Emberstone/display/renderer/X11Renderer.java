package com.valkryst.Emberstone.display.renderer;

import java.awt.*;
import java.awt.peer.ComponentPeer;

public class X11Renderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.awt.X11ComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.opengl.GLXSurfaceData";

    public X11Renderer(final ComponentPeer peer) throws UnsupportedOperationException {
        super(peer, X11Renderer.class);
    }

    /**
     * Determines whether or not the X11Renderer is supported on this machine.
     *
     * @return
     *          Whether the X11Renderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(X11Renderer.class);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var x11GraphicsDevice = Class.forName("sun.awt.X11GraphicsDevice");
            final var x11GraphicsConfig = Class.forName("sun.awt.X11GraphicsConfig");

            final var getVisualMethod = x11GraphicsConfig.getMethod("getVisual");
            final int visual = (int) getVisualMethod.invoke(currentGraphicsConfiguration);

            final var getConfigMethod = x11GraphicsConfig.getMethod("getConfig", x11GraphicsDevice, int.class, int.class, int.class, boolean.class);

            // I pulled these values out of nowhere, may be wrong.
            // 24 = depth, 0 = colormap, false= doublebuffer
            return (GraphicsConfiguration) getConfigMethod.invoke(null, currentGraphicsConfiguration.getDevice(), visual, 24, 0, true);
        } catch (final Exception e) {
            // todo Tons of exceptions can be thrown in the above code, catch it or something.
            // Otherwise, we fall back to the default and hope for the best.
            e.printStackTrace();
            return super.getGraphicsConfig();
        }
    }

    @Override
    public String getName() {
        return "X11";
    }
}
