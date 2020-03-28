package com.valkryst.Emberstone.display.renderer;

import java.awt.*;
import java.awt.peer.ComponentPeer;

// OSX Specific
// https://github.com/AdoptOpenJDK/openjdk-jdk11/tree/master/src/java.desktop/macosx/classes/sun/lwawt
public class LWRenderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.lwawt.LWComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.OSXOffScreenSurfaceData";

    public LWRenderer(final ComponentPeer peer, final Class<? extends Renderer> rendererClass) throws UnsupportedOperationException {
        super(peer, LWRenderer.class);
    }

    /**
     * Determines whether or not the LWRenderer is supported on this machine.
     *
     * @return
     *          Whether the LWRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(LWRenderer.class);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var cGraphicsDevice = Class.forName("sun.awt.CGraphicsDevice");
            final var cGraphicsConfig = Class.forName("sun.awt.CGraphicsConfig");
            final var lwGraphicsConfig = Class.forName("sun.lwawt.LWGraphicsConfig");

            // get the visual id
            final int visual = (int) cGraphicsConfig.getMethod("getVisual").invoke(currentGraphicsConfiguration);

            final var getConfigMethod = lwGraphicsConfig.getMethod("getConfig", lwGraphicsConfig, int.class);

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
        return "LW";
    }
}
