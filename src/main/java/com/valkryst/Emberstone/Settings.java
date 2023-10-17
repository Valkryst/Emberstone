package com.valkryst.Emberstone;

import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.VJSON.VJSON;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Settings {
    /** The settings filename. */
    private static final String FILE_NAME = "Settings.json";

    /** The singleton instance. */
    private static Settings instance;

    /** The target FPS. */
    @Getter @Setter private int targetFps = 50;

    @Getter @Setter private double uiScale = 1.0;

    /** Whether music is enabled. */
    @Getter private boolean musicEnabled = true;

    /** Whether music is disabled. */
    @Getter @Setter private boolean sfxEnabled = true;

    /** Whether Direct3D hardware acceleration is enabled. */
    @Getter private boolean direct3DEnabled = true;

    /** Whether OpenGL hardware acceleration is enabled. */
    @Getter private boolean openGLEnabled = false;

    /** Whether debug mode is enabled. */
    @Getter @Setter private boolean debugModeOn = false;

    /** Whether bounding boxes are displayed when debug mode is on. */
    @Setter private boolean debugBoundingBoxesOn = false;

    /** Whether spawn points are displayed when debug mode is on. */
    @Setter private boolean debugSpawnPointsOn = false;

    /** Whether debug messages for the audio system are displayed when debug mode is on. */
    @Setter private boolean debugAudioOn = false;

    /** Constructs a new Settings object. */
    private Settings() {}

    /**
     * Constructs a new Settings object.
     *
     * @param json
     *          A JSON object containing the settings.
     */
    private Settings(final JSONObject json) {
        targetFps = VJSON.getInt(json, "Target FPS");
        uiScale = VJSON.getDouble(json, "UI Scale");

        musicEnabled = VJSON.getBoolean(json, "Music Enabled");
        sfxEnabled = VJSON.getBoolean(json, "SFX Enabled");

        direct3DEnabled = VJSON.getBoolean(json, "Direct3D Hardware Acceleration Enabled");
        openGLEnabled = VJSON.getBoolean(json, "OpenGL Hardware Acceleration Enabled");

        debugModeOn = VJSON.getBoolean(json, "Debug Mode On");
        debugBoundingBoxesOn = VJSON.getBoolean(json, "Debug Bounding Boxes On");
        debugSpawnPointsOn = VJSON.getBoolean(json, "Debug Spawn Points On");
        debugAudioOn = VJSON.getBoolean(json, "Debug Audio On");

        // Only one should be active.
        if (direct3DEnabled && openGLEnabled) {
            direct3DEnabled = false;
        }
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
                instance = new Settings(VJSON.loadJson(FILE_NAME));
            } catch (final FileNotFoundException e) {
                instance = new Settings();

                try {
                    instance.save();
                } catch (final IOException ee) {
                    ee.printStackTrace();
                }
            } catch (final IOException | ParseException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Save the settings when the game shuts down.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    instance.save();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        return instance;
    }

    /**
     * Saves the settings to an on-disk file.
     *
     * @throws IOException
     *          If the named file exists, but is a directory rather than a regular file.
     *          If the file does not exist, but cannot be created.
     *          If the file cannot be opened.
     */
    public void save() throws IOException {
        // Prepare Data
        final JSONObject json = new JSONObject();
        json.put("Target FPS", targetFps);
        json.put("UI Scale", uiScale);
        json.put("Music Enabled", musicEnabled);
        json.put("SFX Enabled", sfxEnabled);
        json.put("Direct3D Hardware Acceleration Enabled", direct3DEnabled);
        json.put("OpenGL Hardware Acceleration Enabled", openGLEnabled);
        json.put("Debug Mode On", debugModeOn);
        json.put("Debug Bounding Boxes On", debugBoundingBoxesOn);
        json.put("Debug Spawn Points On", debugSpawnPointsOn);
        json.put("Debug Audio On", debugAudioOn);

        // Write Data
        final FileWriter fw = new FileWriter(FILE_NAME);
        json.writeJSONString(fw);
        fw.flush();
        fw.close();
    }

    /**
     * Determines whether bounding boxes should be displayed.
     *
     * @return
     *          Whether bounding boxes should be displayed.
     */
    public boolean areDebugBoundingBoxesOn() {
        return debugModeOn & debugBoundingBoxesOn;
    }

    /**
     * Determines whether spawn points should be displayed.
     *
     * @return
     *          Whether spawn points should be displayed.
     */
    public boolean areDebugSpawnPointsOn() {
        return debugModeOn & debugSpawnPointsOn;
    }

    /**
     * Determines whether debug audio should be displayed.
     *
     * @return
     *          Whether debug audio should be displayed.
     */
    public boolean isDebugAudioOn() {
        return debugModeOn && debugAudioOn;
    }

    /**
     * En/disables music.
     *
     * If disabling, then all active music is stopped.
     *
     * @param musicEnabled
     *          Whether music is en/disabled.
     */
    public void setMusicEnabled(final boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        GameAudio.getInstance().stopAllActiveMusic();
    }
}
