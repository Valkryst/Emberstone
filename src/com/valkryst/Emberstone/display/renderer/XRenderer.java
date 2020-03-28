package com.valkryst.Emberstone.display.renderer;

import java.awt.peer.ComponentPeer;

public class XRenderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.awt.X11ComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.xr.XRSurfaceData";

    public XRenderer(final ComponentPeer peer) throws UnsupportedOperationException {
        super(peer, XRenderer.class);
    }

    /**
     * Determines whether or not the XRenderer is supported on this machine.
     *
     * @return
     *          Whether the XRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(XRenderer.class);
    }

    @Override
    public String getName() {
        return "X Render";
    }
}
