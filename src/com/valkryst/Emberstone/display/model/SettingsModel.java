package com.valkryst.Emberstone.display.model;

import com.valkryst.Emberstone.display.renderer.Renderer;
import com.valkryst.Emberstone.display.renderer.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

public class SettingsModel extends Model {
    /*
     * Automatically initialize the settings singleton instance when the class
     * is loaded.
     */
    static {
        SettingsModel.getInstance();
    }

    /** Name of the settings file. */
    private final static String SETTINGS_FILE = "settings.ini";

    /** Singleton instance. */
    private static SettingsModel instance;

    @Getter @Setter private boolean rendererDebuggingEnabled;

    /**
     * Whether alpha blending algorithm choices should be biased for more speed
     * or quality.
     */
    @Getter @Setter private String alphaInterpolation;

    /**
     * Whether geometry rendering methods of a Graphics2D object should attempt
     * to reduce aliasing artifacts along the edges of shapes.
     */
    @Getter @Setter private String antialiasing;

    /**
     * Whether color conversion algorithms should be biased for more accurate
     * approximations/conversions when storing colors into a destination image
     * or surface with limited color resolution.
     */
    @Getter @Setter private String colorRendering;

    /**
     * Whether color conversion algorithms should be biased to use dithering
     * when storing colors into a destination image or surface with limited
     * color resolution.
     */
    @Getter @Setter private String dithering;

    /**
     * Controls how image pixels are filtered, or resamples, during image
     * rendering operations.
     */
    @Getter @Setter private String interpolation;

    /**
     * Whether rendering algorithm choices should be biased for more speed or
     * quality.
     */
    @Getter @Setter private String rendering;

    @Getter @Setter private Class<? extends Renderer> renderer;

    @Getter @Setter private int viewWidth;

    @Getter @Setter private int viewHeight;

    @Getter @Setter private boolean windowed;

    /**
     * Constructs a new SettingsModel.
     *
     * @throws IOException
     *          If the settings file doesn't exist and can't be created.
     *          If the settings file is a directory.
     *          If the user doesn't have permissions to read the settings file.
     *          If the settings file can't be opened for any other reason.
     */
    private SettingsModel() throws IOException {
        final var file = new File(SETTINGS_FILE);

        if (!file.exists()) {
            System.err.printf("There is no settings file at '%s'. Generating default settings file.\n", file.getAbsoluteFile());
            generateDefaultSettingsFile();
        }

        if (file.isDirectory()) {
            throw new IOException(String.format("The settings file at '%s' is a directory.\n", file.getAbsoluteFile()));
        }

        if (!file.canRead()) {
            throw new IOException(String.format("Unable to read the settings file at '%s'.", file.getAbsoluteFile()));
        }

        Config.getGlobal().setEscape(false);
        final var settings = new IniPreferences(new Ini(file));

        // Load Debugging Settings
        final var debuggingSettings = settings.node("DEBUGGING");
        loadString(debuggingSettings, "RendererDebuggingEnabled").ifPresent(s -> {
            rendererDebuggingEnabled = Boolean.parseBoolean(s);
        });

        // Load Graphics Settings
        final var graphicsSettings = settings.node("GRAPHICS");
        loadString(graphicsSettings, "AlphaInterpolation").ifPresent(this::setAlphaInterpolation);
        loadString(graphicsSettings, "Antialiasing").ifPresent(this::setAntialiasing);
        loadString(graphicsSettings, "ColorRendering").ifPresent(this::setColorRendering);
        loadString(graphicsSettings, "Dithering").ifPresent(this::setDithering);
        loadString(graphicsSettings, "Interpolation").ifPresent(this::setInterpolation);
        loadString(graphicsSettings, "Rendering").ifPresent(this::setRendering);
        loadString(graphicsSettings, "Renderer").ifPresent(this::setRenderer);
        loadInteger(graphicsSettings, "ViewWidth").ifPresent(this::setViewWidth);
        loadInteger(graphicsSettings, "ViewHeight").ifPresent(this::setViewHeight);
        loadBoolean(graphicsSettings, "Windowed").ifPresent(this::setWindowed);
    }

    private void generateDefaultSettingsFile() throws IOException {
        rendererDebuggingEnabled = true;

        setAlphaInterpolation("Auto");
        setAntialiasing("Auto");
        setColorRendering("Auto");
        setDithering("Auto");
        setInterpolation("Nearest Neighbor");
        setRendering("Auto");
        setRenderer(GDIRenderer.getName());
        setViewWidth(1920);
        setViewHeight(1080);
        setWindowed(true);

        save();
    }

    public void save() throws IOException {
        final var fileWriter = new FileWriter(new File(SETTINGS_FILE));
        final var printWriter = new PrintWriter(fileWriter);

        printWriter.println("[DEBUGGING]");
        printWriter.println("RendererDebuggingEnabled = " + rendererDebuggingEnabled);
        printWriter.println();
        printWriter.println("[GRAPHICS]");
        printWriter.println("AlphaInterpolation = " + alphaInterpolation);
        printWriter.println("Antialiasing = " + antialiasing);
        printWriter.println("ColorRendering = " + colorRendering);
        printWriter.println("Dithering = " + dithering);
        printWriter.println("Interpolation = " + interpolation);
        printWriter.println("Rendering = " + rendering);
        printWriter.println("Renderer = " + getRendererName());
        printWriter.println("ViewWidth = " + viewWidth);
        printWriter.println("ViewHeight = " + viewHeight);
        printWriter.println("Windowed = " + windowed);

        printWriter.close();
        fileWriter.close();
    }

    private Optional<Boolean> loadBoolean(final Preferences preferences, final String key) {
        return loadString(preferences, key).map(Boolean::parseBoolean);

    }

    private Optional<String> loadString(final Preferences preferences, final String key) {
        final var data = preferences.get(key, null);

        if (data == null || data.isEmpty()) {
            final var message = String.format("There is no value associated with the '%s' key in the '%s' section.\n", key, preferences.name());
            JOptionPane.showMessageDialog(null, message, "Settings Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        return Optional.of(data);
    }

    private Optional<Integer> loadInteger(final Preferences preferences, final String key) {
        final var string = loadString(preferences, key);

        if (string.isPresent()) {
            try {
                return Optional.of(Integer.parseInt(string.get()));
            } catch(final NumberFormatException e) {
                final var message = String.format("Unable to parse the value '%s', associated with the '%s' key in the '%s' section, as an integer.", string.get(), key, preferences.name());
                JOptionPane.showMessageDialog(null, message, "Settings Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        return Optional.empty();
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static SettingsModel getInstance() {
        if (instance == null) {
            try {
                instance = new SettingsModel();
            } catch(final IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Settings Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        return instance;
    }

    public Object getAlphaInterpolationHint() {
        switch (alphaInterpolation) {
            default:
            case "Auto": {
                return RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
            }
            case "Quality": {
                return RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
            }
            case "Speed": {
                return RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED;
            }
        }
    }

    public Object getAntialiasingHint() {
        switch (antialiasing) {
            default:
            case "Auto": {
                return RenderingHints.VALUE_ANTIALIAS_DEFAULT;
            }
            case "Quality": {
                return RenderingHints.VALUE_ANTIALIAS_ON;
            }
            case "Speed": {
                return RenderingHints.VALUE_ANTIALIAS_OFF;
            }
        }
    }

    public Object getColorRenderingHint() {
        switch (colorRendering) {
            default:
            case "Auto": {
                return RenderingHints.VALUE_COLOR_RENDER_DEFAULT;
            }
            case "Quality": {
                return RenderingHints.VALUE_COLOR_RENDER_QUALITY;
            }
            case "Speed": {
                return RenderingHints.VALUE_COLOR_RENDER_SPEED;
            }
        }
    }

    public Object getDitheringHint() {
        switch (dithering) {
            default:
            case "Auto": {
                return RenderingHints.VALUE_DITHER_DEFAULT;
            }
            case "Enable": {
                return RenderingHints.VALUE_DITHER_ENABLE;
            }
            case "Disable": {
                return RenderingHints.VALUE_DITHER_DISABLE;
            }
        }
    }

    public Object getInterpolationHint() {
        switch (interpolation) {
            default:
            case "Bicubic": {
                return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            }
            case "Bilinear": {
                return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            }
            case "Nearest Neighbor": {
                return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            }
        }
    }

    public Object getRenderingHint() {
        switch (rendering) {
            default:
            case "Auto": {
                return RenderingHints.VALUE_RENDER_DEFAULT;
            }
            case "Quality": {
                return RenderingHints.VALUE_RENDER_QUALITY;
            }
            case "Speed": {
                return RenderingHints.VALUE_RENDER_SPEED;
            }
        }
    }

    public String getRendererName() {
        if (renderer.equals(D3DRenderer.class)) {
            return D3DRenderer.getName();
        }

        if (renderer.equals(GDIRenderer.class)) {
            return GDIRenderer.getName();
        }

        if (renderer.equals(GLXRenderer.class)) {
            return GLXRenderer.getName();
        }

        if (renderer.equals(WGLRenderer.class)) {
            return WGLRenderer.getName();
        }

        if (renderer.equals(X11GLRenderer.class)) {
            return X11GLRenderer.getName();
        }

        if (renderer.equals(X11Renderer.class)) {
            return X11Renderer.getName();
        }

        return ""; // todo Returning nothing is an issue.
    }

    public List<String> getSupportedRendererNames() {
        final var names = new ArrayList<String>();

        if (D3DRenderer.isSupported()) {
            names.add(D3DRenderer.getName());
        }

        if (GDIRenderer.isSupported()) {
            names.add(GDIRenderer.getName());
        }

        if (GLXRenderer.isSupported()) {
            names.add(GLXRenderer.getName());
        }

        if (WGLRenderer.isSupported()) {
            names.add(WGLRenderer.getName());
        }

        if (X11GLRenderer.isSupported()) {
            names.add(X11GLRenderer.getName());
        }

        if (X11Renderer.isSupported()) {
            names.add(X11Renderer.getName());
        }

        return names;
    }

    public void setAlphaInterpolation(final String alphaInterpolation) {
        switch (alphaInterpolation) {
            case "Auto":
            case "Quality":
            case "Speed": {
                super.firePropertyChange("AlphaInterpolation", this.alphaInterpolation, alphaInterpolation);
                this.alphaInterpolation = alphaInterpolation;
                break;
            }
            default: {
                // todo Throw an error, or warning, because the value isn't supported.
            }
        }
    }

    public void setAntialiasing(final @NonNull String antialiasing) {
        switch (antialiasing) {
            case "Auto":
            case "On":
            case "Off": {
                super.firePropertyChange("Antialiasing", this.antialiasing, antialiasing);
                this.antialiasing = antialiasing;
                break;
            }
            default: {
                // todo Throw an error, or warning, because the value isn't supported.
            }
        }
    }

    public void setColorRendering(final @NonNull String colorRendering) {
        switch (colorRendering) {
            case "Auto":
            case "Quality":
            case "Speed": {
                super.firePropertyChange("ColorRendering", this.colorRendering, colorRendering);
                this.colorRendering = colorRendering;
                break;
            }
            default: {
                // todo Throw an error, or warning, because the value isn't supported.
            }
        }
    }

    public void setDithering(final @NonNull String dithering) {
        switch (dithering) {
            case "Auto":
            case "Enable":
            case "Disable": {
                super.firePropertyChange("Dithering", this.dithering, dithering);
                this.dithering = dithering;
                break;
            }
            default: {
                // todo Throw an error, or warning, because the value isn't supported.
            }
        }
    }

    public void setInterpolation(final @NonNull String interpolation) {
        switch (interpolation) {
            case "Bicubic":
            case "Bilinear":
            case "Nearest Neighbor": {
                super.firePropertyChange("Interpolation", this.interpolation, interpolation);
                this.interpolation = interpolation;
                break;
            }
            default: {
                // todo Throw an error, or warning, because the value isn't supported.
            }
        }
    }

    public void setRendering(final @NonNull String rendering) {
        switch (rendering) {
            case "Auto":
            case "Quality":
            case "Speed": {
                super.firePropertyChange("Rendering", this.rendering, rendering);
                this.rendering = rendering;
                break;
            }
            default: {
                // todo Throw an error, or warning, because the value isn't supported.
            }
        }
    }

    public void setRenderer(final @NonNull String renderer) {
        final var unsupportedRendererMessage = "This machine can't use the selected renderer: " + renderer;

        if (D3DRenderer.getName().equals(renderer)) {
            if (!D3DRenderer.isSupported()) {
                // todo Throw an error, or warning, because this machine can't use the renderer.
                System.err.println(unsupportedRendererMessage);
            }

            super.firePropertyChange("Renderer", this.renderer, renderer);
            this.renderer = D3DRenderer.class;
            return;
        }

        if (GDIRenderer.getName().equals(renderer)) {
            if (!GDIRenderer.isSupported()) {
                // todo Throw an error, or warning, because this machine can't use the renderer.
                System.err.println(unsupportedRendererMessage);
            }

            super.firePropertyChange("Renderer", this.renderer, renderer);
            this.renderer = GDIRenderer.class;
            return;
        }

        if (GLXRenderer.getName().equals(renderer)) {
            if (!GLXRenderer.isSupported()) {
                // todo Throw an error, or warning, because this machine can't use the renderer.
                System.err.println(unsupportedRendererMessage);
            }

            super.firePropertyChange("Renderer", this.renderer, renderer);
            this.renderer = GLXRenderer.class;
            return;
        }

        if (WGLRenderer.getName().equals(renderer)) {
            if (!WGLRenderer.isSupported()) {
                // todo Throw an error, or warning, because this machine can't use the renderer.
                System.err.println(unsupportedRendererMessage);
            }

            super.firePropertyChange("Renderer", this.renderer, renderer);
            this.renderer = WGLRenderer.class;
            return;
        }

        if (X11GLRenderer.getName().equals(renderer)) {
            if (!X11GLRenderer.isSupported()) {
                // todo Throw an error, or warning, because this machine can't use the renderer.
                System.err.println(unsupportedRendererMessage);
            }

            super.firePropertyChange("Renderer", this.renderer, renderer);
            this.renderer = X11GLRenderer.class;
            return;
        }

        if (X11Renderer.getName().equals(renderer)) {
            if (!X11Renderer.isSupported()) {
                // todo Throw an error, or warning, because this machine can't use the renderer.
                System.err.println(unsupportedRendererMessage);
            }

            super.firePropertyChange("Renderer", this.renderer, renderer);
            this.renderer = X11Renderer.class;
            return;
        }

        // todo Throw an error, or warning, because the value isn't supported.
    }

    public void setViewWidth(final @NonNull int viewWidth) {
        // todo Ensure only appropriate values can be set.
        this.viewWidth = viewWidth;
    }

    public void setViewHeight(final @NonNull int viewHeight) {
        // todo Ensure only appropriate values can be set.
        this.viewHeight = viewHeight;
    }

    public void setWindowed(final @NonNull boolean windowed) {
        this.windowed = windowed;
    }
}
