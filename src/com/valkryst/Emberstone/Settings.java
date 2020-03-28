package com.valkryst.Emberstone;

import com.valkryst.Emberstone.display.renderer.*;
import com.valkryst.Emberstone.display.renderer.Renderer;
import lombok.Getter;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.prefs.Preferences;

public class Settings {
    /** Name of the settings file. */
    private final static String SETTINGS_FILE = "settings.ini";

    /** Singleton instance. */
    private static Settings instance;

    /**
     * Whether alpha blending algorithm choices should be biased for more speed
     * or quality.
     */
    @Getter private Object alphaInterpolation;

    /**
     * Whether geometry rendering methods of a Graphics2D object should attempt
     * to reduce aliasing artifacts along the edges of shapes.
     */
    @Getter private Object antialiasing;

    /**
     * Whether color conversion algorithms should be biased for more accurate
     * approximations/conversions when storing colors into a destination image
     * or surface with limited color resolution.
     */
    @Getter private Object colorRendering;

    /**
     * Whether color conversion algorithms should be biased to use dithering
     * when storing colors into a destination image or surface with limited
     * color resolution.
     */
    @Getter private Object dithering;

    /**
     * Controls how image pixels are filtered, or resamples, during image
     * rendering operations.
     */
    @Getter private Object interpolation;

    /**
     * Whether rendering algorithm choices should be biased for more speed or
     * quality.
     */
    @Getter private Object rendering;

    @Getter private Class<? extends Renderer> renderer;

    /**
     * Constructs a new Settings object.
     *
     * @throws IOException
     *          If the settings file doesn't exist and can't be created.
     *          If the settings file is a directory.
     *          If the user doesn't have permissions to read the settings file.
     *          If the settings file can't be opened for any other reason.
     */
    private Settings() throws IOException {
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

        // Load Graphics Settings
        final var graphicsSettings = settings.node("graphics");
        loadString(graphicsSettings, "alphaInterpolation").ifPresent(s -> {
            switch (s) {
                case "default": {
                    alphaInterpolation = RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
                    break;
                }
                case "quality": {
                    alphaInterpolation = RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
                    break;
                }
                case "speed": {
                    alphaInterpolation = RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED;
                    break;
                }
                default: {
                    // todo value not supported
                }
            }
        });
        loadString(graphicsSettings, "antialiasing").ifPresent(s -> {
            switch (s) {
                case "default": {
                    antialiasing = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
                    break;
                }
                case "on": {
                    antialiasing = RenderingHints.VALUE_ANTIALIAS_ON;
                    break;
                }
                case "off": {
                    antialiasing = RenderingHints.VALUE_ANTIALIAS_OFF;
                    break;
                }
                default: {
                    // todo value not supported
                }
            }
        });
        loadString(graphicsSettings, "colorRendering").ifPresent(s -> {
            switch (s) {
                case "default": {
                    colorRendering = RenderingHints.VALUE_COLOR_RENDER_DEFAULT;
                    break;
                }
                case "quality": {
                    colorRendering = RenderingHints.VALUE_COLOR_RENDER_QUALITY;
                    break;
                }
                case "speed": {
                    colorRendering = RenderingHints.VALUE_COLOR_RENDER_SPEED;
                    break;
                }
                default: {
                    // todo value not supported
                }
            }
        });
        loadString(graphicsSettings, "dithering").ifPresent(s -> {
            switch (s) {
                case "default": {
                    dithering = RenderingHints.VALUE_DITHER_DEFAULT;
                    break;
                }
                case "enable": {
                    dithering = RenderingHints.VALUE_DITHER_ENABLE;
                    break;
                }
                case "disable": {
                    dithering = RenderingHints.VALUE_DITHER_DISABLE;
                    break;
                }
                default: {
                    // todo value not supported
                }
            }
        });
        loadString(graphicsSettings, "interpolation").ifPresent(s -> {
            switch (s) {
                case "bicubic": {
                    interpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                    break;
                }
                case "bilinear": {
                    interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                    break;
                }
                case "nearestNeighbor": {
                    interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                    break;
                }
                default: {
                    // todo value not supported
                }
            }
        });
        loadString(graphicsSettings, "rendering").ifPresent(s -> {
            switch (s) {
                case "default": {
                    rendering = RenderingHints.VALUE_RENDER_DEFAULT;
                    break;
                }
                case "quality": {
                    rendering = RenderingHints.VALUE_RENDER_QUALITY;
                    break;
                }
                case "speed": {
                    rendering = RenderingHints.VALUE_RENDER_SPEED;
                    break;
                }
                default: {
                    // todo value not supported
                }
            }
        });
        loadString(graphicsSettings, "renderer").ifPresent(s -> {
            switch (s) {
                case "d3d": {
                    renderer = D3DRenderer.class;
                    break;
                }
                case "gdi": {
                    renderer = GDIRenderer.class;
                    break;
                }
                case "glx": {
                    renderer = GLXRenderer.class;
                    break;
                }
                case "lw": {
                    renderer = LWRenderer.class;
                    break;
                }
                case "wgl": {
                    renderer = WGLRenderer.class;
                    break;
                }
                case "x11": {
                    renderer = X11Renderer.class;
                    break;
                }
                case "x": {
                    renderer = XRenderer.class;
                    break;
                }
                default: {
                    // todo value not supported
                }
            }
        });
    }

    private void generateDefaultSettingsFile() throws IOException {
        final var fileWriter = new FileWriter(new File(SETTINGS_FILE));
        final var printWriter = new PrintWriter(fileWriter);

        printWriter.println("[graphics]");
        printWriter.println("alphaInterpolation = default");
        printWriter.println("antialiasing = default");
        printWriter.println("colorRendering = default");
        printWriter.println("dithering = default");
        printWriter.println("interpolation = nearestNeighbor");
        printWriter.println("rendering = default");
        printWriter.println("renderer = lw");

        printWriter.close();
        fileWriter.close();
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
    public static Settings getInstance() {
        if (instance == null) {
            try {
                instance = new Settings();
            } catch(final IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Settings Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        return instance;
    }
}
