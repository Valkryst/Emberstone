package com.valkryst.Emberstone.media;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

public enum Video {
    INTRO("/video/Intro.mp4"),
    CREDITS("/video/Credits.mp4");

    /** The URI of the video file. */
    @Getter private final String uri;

    /**
     * Constructs a new Video enum.
     *
     * @param filePath
     *          The path of the video file.
     */
    Video(final String filePath) {
        URI uri = null;

        // Search the Jar for the file.
        try {
            uri = Video.class.getResource(filePath).toURI();
        } catch (final URISyntaxException ignored) {}

        if (uri != null) {
            this.uri = uri.toString();
            return;
        }

        // Search the filesystem for the file.
        final File file = new File(filePath);

        try {
            if (file.exists() == false) {
                throw new FileNotFoundException("The file '" + filePath + "' could not be found within the Jar or on the filesystem.");
            }

            if (file.isDirectory()) {
                throw new FileNotFoundException("The file '" + filePath + "' points to a directory on the filesystem.");
            }
        } catch (final FileNotFoundException e) {
            // Throw an error instantly, so we know if one of the music files is missing.
            e.printStackTrace();
        }

        this.uri = file.toURI().toString();
    }
}
