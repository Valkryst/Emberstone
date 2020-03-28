package com.valkryst.Emberstone.display.renderer;

import sun.java2d.d3d.D3DGraphicsDevice;
import sun.java2d.pipe.hw.ContextCapabilities;

import java.awt.*;
import java.awt.peer.ComponentPeer;

public class D3DRenderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.awt.windows.WComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.d3d.D3DSurfaceData";

    public D3DRenderer(final ComponentPeer peer) throws UnsupportedOperationException {
        super(peer, D3DRenderer.class);
    }

    /**
     * Determines whether or not the D3DRenderer is supported on this machine.
     *
     * @return
     *          Whether the D3DRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(D3DRenderer.class);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            // We use reflection so that this class loads on linux.
            final var d3dGraphicsDevice = Class.forName("sun.java2d.d3d.D3DGraphicsDevice");
            final var d3dGraphicsConfig = Class.forName("sun.java2d.d3d.D3DGraphicsConfig");
            // final var contextCapabilities = Class.forName("sun.java2d.pipe.hw.ContextCapabilities");

            // Find the screen id.
            final var getScreenMethod = d3dGraphicsDevice.getMethod("getScreen");
            final int screen = (int) getScreenMethod.invoke(super.getGraphicsConfig().getDevice());

            // Find the device capability.
            final var getDeviceCapsMethod = d3dGraphicsDevice.getDeclaredMethod("getDeviceCaps", int.class);
            getDeviceCapsMethod.setAccessible(true);
            final var d3dDeviceCaps = getDeviceCapsMethod.invoke(null, screen);

            // Check Direct3D pipeline support.
            final var getCapsMethod = ContextCapabilities.class.getMethod("getCaps");
            final int CAPS_OK = 1 << 18;
            if ((((int) getCapsMethod.invoke(d3dDeviceCaps)) & CAPS_OK) == 0) {
                throw new RuntimeException("Could not enable Direct3D pipeline on " + "screen " + screen);
            }

            // Find constructors for Direct3D graphics device and configuration.
            final var newD3DGraphicsDevice = d3dGraphicsDevice.getDeclaredConstructor(int.class, ContextCapabilities.class);
            newD3DGraphicsDevice.setAccessible(true);

            final var newD3DGraphicsConfig = d3dGraphicsConfig.getDeclaredConstructor(D3DGraphicsDevice.class);
            newD3DGraphicsConfig.setAccessible(true);

            // Create the Direct3D graphics device.
            final var device = newD3DGraphicsDevice.newInstance(screen, d3dDeviceCaps);
            return (GraphicsConfiguration) newD3DGraphicsConfig.newInstance(device);
        } catch (final Exception e) {
            // todo Tons of exceptions can be thrown in the above code, catch it or something.
            // Otherwise, we fall back to the default and hope for the best.
            e.printStackTrace();
            return super.getGraphicsConfig();
        }
    }


    @Override
    public String getName() {
        return "OpenGL";
    }
}
