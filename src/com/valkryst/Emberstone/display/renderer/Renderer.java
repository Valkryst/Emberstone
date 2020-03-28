package com.valkryst.Emberstone.display.renderer;

import com.valkryst.Emberstone.Settings;
import lombok.SneakyThrows;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

import java.awt.*;
import java.awt.peer.ComponentPeer;
import java.util.Map;

public abstract class Renderer {
    /** The native peer of our {@link java.awt.Panel}, upon which we render. */
    protected final ComponentPeer peer;

    /** This is the class for our peer. */
    protected Class<?> peerClass;

    /** This is the surface that would be used by this render manager to render. */
    protected Class<?> surfaceClass;

    /** A cached instance of this renderer's graphics context. */
    protected Graphics2D graphics2D;


    @SneakyThrows
    public Renderer(final ComponentPeer peer, final Class<? extends Renderer> rendererClass) throws UnsupportedOperationException {
        if (!isSupported(rendererClass)) {
            throw new UnsupportedOperationException("The " + rendererClass.getName() + " is not supported on this machine.");
        }

        /*
         * The following lines can throw exceptions if the PEER_CLASS_NAME or
         * SURFACE_CLASS_NAME don't exist in the Renderer, but we use the
         * @SneakyThrows annotation to ignore them.
         *
         * The renderers were written for this project and it's guaranteed that
         * they do have them.
         */
        var classField = rendererClass.getDeclaredField("PEER_CLASS_NAME");
        var className = (String) classField.get(null);
        this.peerClass = Class.forName(className);

        classField = rendererClass.getDeclaredField("SURFACE_CLASS_NAME");
        className = (String) classField.get(null);
        this.surfaceClass = Class.forName(className);

        this.peer = peer;

        peer.updateGraphicsData(getGraphicsConfig());
    }

    /**
     * Determines whether the specified renderer is supported on this machine.
     *
     * @param rendererClass
     *          The class of the renderer to check.
     *
     * @return
     *          Whether the renderer is supported on this machine.
     */
    @SneakyThrows
    public static boolean isSupported(final Class<? extends Renderer> rendererClass) {
        /*
         * The following lines can throw exceptions if the PEER_CLASS_NAME or
         * SURFACE_CLASS_NAME don't exist in the Renderer, but we use the
         * @SneakyThrows annotation to ignore them.
         *
         * The renderers were written for this project and it's guaranteed that
         * they do have them.
         */
        final var peerClassField = rendererClass.getDeclaredField("PEER_CLASS_NAME");
        final var surfaceClassField = rendererClass.getDeclaredField("SURFACE_CLASS_NAME");

        final var peerClassName = (String) peerClassField.get(null);
        final var surfaceClassName = (String) surfaceClassField.get(null);

        try {
            Class.forName(peerClassName);
            Class.forName(surfaceClassName);
        } catch (final ClassNotFoundException ignored) {
            ignored.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Attempt to create a surface and convert it to a {@link java.awt.Graphics2D} object.
     * <p/>
     * In doing so, this method temporarily switches to the new {@link java.awt.GraphicsConfiguration}.
     * This method does not cache the newly created {@link java.awt.Graphics2D} object.
     *
     * #method
     *
     * @return a {@link java.awt.Graphics2D} object.
     */
    protected Graphics2D createGraphics() {
        try {
            // Creates the surface.
            final var createDataMethod = surfaceClass.getDeclaredMethod("createData", peerClass);
            final var surfaceData = (SurfaceData) createDataMethod.invoke(null, peer);

            // Fail if the surface is not created.
            if (surfaceData == null) {
                return null;
            }

            // Use unofficial APi to convert the surface to Graphics2D.
            return new SunGraphics2D(surfaceData, Color.BLACK, Color.BLACK, null);
        } catch (Exception e) {
            // #error If anything bad happens, we fail.
            e.printStackTrace(); // todo Remove this and do something diff.
            return null;
        }
    }

    /**
     * Retrieves the graphics context.
     *
     * @return
     *          The graphics context.
     */
    public Graphics2D getGraphics() {
        if (graphics2D == null) {
            graphics2D = createGraphics();
            setRenderingHints();
            return graphics2D;
        }

        if (((SunGraphics2D) graphics2D).surfaceData.isSurfaceLost()) {
            graphics2D = createGraphics();
            setRenderingHints();
            return graphics2D;
        }

        setRenderingHints();
        return graphics2D;
    }

    /**
     * Retrieves renderer's {@link java.awt.GraphicsConfiguration}.
     *
     * @return
     *          This renderer's {@link java.awt.GraphicsConfiguration}.
     */
    public GraphicsConfiguration getGraphicsConfig() {
        return peer.getGraphicsConfiguration();
    }

    /**
     * Retrieves this renderer's name.
     *
     * @return
     *          This renderer's name.
     */
    public abstract String getName();

    private void setRenderingHints() {
        final var settings = Settings.getInstance();

        // Automatically detect the best text rendering settings and apply them.
        final var desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        graphics2D.setRenderingHints(desktopHints);

        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, settings.getAlphaInterpolation());
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, settings.getAntialiasing());
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, settings.getColorRendering());
        graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, settings.getDithering());
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, settings.getInterpolation());
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, settings.getRendering());
        graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    }
}
