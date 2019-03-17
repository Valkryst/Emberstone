package com.valkryst.Emberstone.media;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public enum SoundEffect {
    IMPACT_1("/sfx/combat/Impact - 1.mp3"),
    IMPACT_2("/sfx/combat/Impact - 2.mp3"),
    IMPACT_3("/sfx/combat/Impact - 3.mp3"),
    IMPACT_4("/sfx/combat/Impact - 4.mp3"),
    IMPACT_5("/sfx/combat/Impact - 5.mp3"),
    IMPACT_6("/sfx/combat/Impact - 6.mp3"),
    IMPACT_7("/sfx/combat/Impact - 7.mp3"),
    IMPACT_8("/sfx/combat/Impact - 8.mp3"),
    IMPACT_9("/sfx/combat/Impact - 9.mp3"),
    IMPACT_10("/sfx/combat/Impact - 10.mp3"),
    ATTACK_1("/sfx/combat/Attack - 1.mp3"),
    ATTACK_2("/sfx/combat/Attack - 2.mp3"),
    ATTACK_3("/sfx/combat/Attack - 3.mp3"),
    ATTACK_4("/sfx/combat/Attack - 4.mp3"),
    ATTACK_5("/sfx/combat/Attack - 5.mp3"),
    ATTACK_6("/sfx/combat/Attack - 6.mp3"),
    ATTACK_7("/sfx/combat/Attack - 7.mp3"),
    ATTACK_8("/sfx/combat/Attack - 8.mp3"),
    ATTACK_9("/sfx/combat/Attack - 9.mp3"),
    ATTACK_10("/sfx/combat/Attack - 10.mp3"),
    ATTACK_11("/sfx/combat/Attack - 11.mp3"),
    MISS_1("/sfx/combat/Miss - 1.mp3"),
    MISS_2("/sfx/combat/Miss - 2.mp3"),
    MISS_3("/sfx/combat/Miss - 3.mp3"),
    MISS_4("/sfx/combat/Miss - 4.mp3"),
    MISS_5("/sfx/combat/Miss - 5.mp3"),
    MISS_6("/sfx/combat/Miss - 6.mp3"),
    MISS_7("/sfx/combat/Miss - 7.mp3"),
    MISS_8("/sfx/combat/Miss - 8.mp3"),
    MISS_9("/sfx/combat/Miss - 9.mp3");

    /** The URI of the sound effect file. */
    @Getter private final String uri;

    /**
     * Constructs a new SoundEffect enum.
     *
     * @param filePath
     *          The path of the sound effect file.
     */
    SoundEffect(final String filePath) {
        URI uri = null;

        // Search the Jar for the file.
        try {
            uri = GameAudio.class.getResource(filePath).toURI();
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

    /**
     * Retrieves a random attack sound effect.
     *
     * @return
     *          The attack sound.
     */
    public static SoundEffect getAttackSound() {
        switch (ThreadLocalRandom.current().nextInt(0, 11)) {
            default:
            case 0: {
                return ATTACK_1;
            }
            case 1: {
                return ATTACK_2;
            }
            case 2: {
                return ATTACK_3;
            }
            case 3: {
                return ATTACK_4;
            }
            case 4: {
                return ATTACK_5;
            }
            case 5: {
                return ATTACK_6;
            }
            case 6: {
                return ATTACK_7;
            }
            case 7: {
                return ATTACK_8;
            }
            case 8: {
                return ATTACK_9;
            }
            case 9: {
                return ATTACK_10;
            }
            case 10: {
                return ATTACK_11;
            }
        }
    }

    /**
     * Retrieves a random impact sound effect.
     *
     * @return
     *          The impact sound.
     */
    public static SoundEffect getImpactSound() {
        switch (ThreadLocalRandom.current().nextInt(0, 10)) {
            default:
            case 0: {
                return IMPACT_1;
            }
            case 1: {
                return IMPACT_2;
            }
            case 2: {
                return IMPACT_3;
            }
            case 3: {
                return IMPACT_4;
            }
            case 4: {
                return IMPACT_5;
            }
            case 5: {
                return IMPACT_6;
            }
            case 6: {
                return IMPACT_7;
            }
            case 7: {
                return IMPACT_8;
            }
            case 8: {
                return IMPACT_9;
            }
            case 9: {
                return IMPACT_10;
            }
        }
    }

    /**
     * Retrieves a random miss sound effect.
     *
     * @return
     *          The miss sound.
     */
    public static SoundEffect getMissSound() {
        switch (ThreadLocalRandom.current().nextInt(0, 9)) {
            default:
            case 0: {
                return MISS_1;
            }
            case 1: {
                return MISS_2;
            }
            case 2: {
                return MISS_3;
            }
            case 3: {
                return MISS_4;
            }
            case 4: {
                return MISS_5;
            }
            case 5: {
                return MISS_6;
            }
            case 6: {
                return MISS_7;
            }
            case 7: {
                return MISS_8;
            }
            case 8: {
                return MISS_9;
            }
        }
    }
}
