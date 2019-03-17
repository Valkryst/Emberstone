package com.valkryst.Emberstone.media;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.valkryst.Emberstone.Settings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GameAudio {
    /** The singleton instance. */
    private static final GameAudio INSTANCE = new GameAudio();

    /** The cache of recently played audio clips. */
    private final Cache<SoundEffect, AudioClip> audioClipCache = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    /** The active music tracks. */
    private final HashMap<Music, MediaPlayer> activeMusic = new HashMap<>();

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static GameAudio getInstance() {
        return INSTANCE;
    }

    /**
     * Starts playing a music track.
     *
     * If a music track is already playing, it's faded-out as the new music track is faded-in.
     *
     * @param music
     *          The music to play.
     */
    public void playMusic(final Music music) {
        if (Settings.getInstance().isMusicEnabled() == false) {
            if (Settings.getInstance().isDebugAudioOn()) {
                System.out.println("Cannot play music track, because music is disabled.\n\t");
            }

            return;
        }

        if (Settings.getInstance().isDebugAudioOn()) {
            System.out.println("Playing music track.\n\t" + music.getUri());
        }

        if (activeMusic.containsKey(music)) {
            if (Settings.getInstance().isDebugAudioOn()) {
                System.out.println("Cannot play music track, because it is already playing.\n\t" + music.getUri());
            }
        } else {
            final Media media = new Media(music.getUri());
            final MediaPlayer mediaPlayer = new MediaPlayer(media);

            activeMusic.put(music, mediaPlayer);

            fadeInAndStart(mediaPlayer);
        }
    }

    /**
     * Starts playing a media player while fading it in from 0-100% volume.
     *
     * @param mediaPlayer
     *          The media player.
     */
    private void fadeInAndStart(final MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            return;
        }

        if (Settings.getInstance().isDebugAudioOn()) {
            System.out.println("Fading-In Music: " + mediaPlayer.getMedia().getSource());
        }

        mediaPlayer.setVolume(0);

        final KeyValue keyValue = new KeyValue(mediaPlayer.volumeProperty(), 1);
        final KeyFrame keyFrame = new KeyFrame(Duration.seconds(10), keyValue);
        final Timeline timeline = new Timeline(keyFrame);

        mediaPlayer.play();
        timeline.play();
    }

    /**
     * Stops playing a media player after fading it out from 100-0%.
     *
     * @param mediaPlayer
     *          The media player.
     */
    private void fadeOutAndStop(final MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            return;
        }

        if (Settings.getInstance().isDebugAudioOn()) {
            System.out.println("Fading-Out Music: " + mediaPlayer.getMedia().getSource());
        }

        final KeyValue keyValue = new KeyValue(mediaPlayer.volumeProperty(), 0);
        final KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), keyValue);
        final Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(actionEvent -> {
            mediaPlayer.stop();
            Platform.runLater(mediaPlayer::dispose);
        });

        timeline.play();
    }

    /**
     * Stops a music track from playing.
     *
     * @param music
     *          The music to stop.
     */
    public void stopMusic(final Music music) {
        if (music == null) {
            return;
        }

        fadeOutAndStop(activeMusic.get(music));
        activeMusic.remove(music);
    }

    /** Stops all active music. */
    public void stopAllActiveMusic() {
        for (final Music music : activeMusic.keySet()) {
            stopMusic(music);
        }
    }

    /**
     * Plays a sound effect.
     *
     * @param effect
     *          The sound effect.
     */
    public void playSoundEffect(final SoundEffect effect) {
        if (Settings.getInstance().isSfxEnabled() == false) {
            if (Settings.getInstance().isDebugAudioOn()) {
                System.out.println("SFX are disabled, not playing: " + effect.getUri());
            }

            return;
        }

        if (effect == null) {
            return;
        }

        if (Settings.getInstance().isDebugAudioOn()) {
            System.out.println("Playing SFX: " + effect.getUri());
        }

        AudioClip audioClip = audioClipCache.getIfPresent(effect);
        if (audioClip != null) {
            audioClip.setVolume(0.75);
            audioClip.play();
            return;
        }

        audioClip = new AudioClip(effect.getUri());
        audioClipCache.put(effect, audioClip);
        audioClip.play();
    }
}
