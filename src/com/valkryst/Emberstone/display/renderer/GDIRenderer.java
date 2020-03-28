package com.valkryst.Emberstone.display.renderer;

import java.awt.peer.ComponentPeer;

public class GDIRenderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.awt.windows.WComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.windows.GDIWindowSurfaceData";

    public GDIRenderer(final ComponentPeer peer) throws UnsupportedOperationException {
        super(peer, GDIRenderer.class);
    }

    /**
     * Determines whether or not the GDIRenderer is supported on this machine.
     *
     * @return
     *          Whether the GDIRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(GDIRenderer.class);
    }

    @Override
    public String getName() {
        return "GDI";
    }
}
