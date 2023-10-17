package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.controller.Controller;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class VideoView extends View {
    /** The video panel. */
    @Getter private final JFXPanel videoPanel = new JFXPanel();

    /**
     * Constructs a new VideoView.
     *
     * @param video
     *          The video to play.
     *
     * @param controller
     *          The controller whose view is to be displayed after the video finishes.
     */
    public VideoView(final Video video, final Controller controller) {
        final Game game = Game.getInstance();

        videoPanel.setBackground(Color.BLACK);

        final Media media = new Media(video.getUri());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);

        // If there are any issues loading the video, this will clean things up and skip to the next view.
        final Timer faultDetectionTimer = new Timer();
        faultDetectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(mediaPlayer::dispose);
                SwingUtilities.invokeLater(() -> game.setController(controller));
            }
        }, 3000);

        // Finish setting up the media player.
        mediaPlayer.setOnReady(() -> {
            // If the media player starts playing, then there were no issues loading the video.
            SwingUtilities.invokeLater(faultDetectionTimer::cancel);

            // Place the media player within the video panel.
            final MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);

            final StackPane root = new StackPane();
            root.getChildren().add(mediaView);

            final Scene scene = new Scene(root);
            scene.setOnMouseClicked(e -> {
                if (MouseButton.SECONDARY == e.getButton()) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    SwingUtilities.invokeLater(() -> game.setController(controller));
                }
            });
            scene.setOnKeyPressed(e -> {
                if (KeyCode.ESCAPE == e.getCode()) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    SwingUtilities.invokeLater(() -> game.setController(controller));
                }
            });
            videoPanel.setScene(scene);

            // Start playing the video.
            mediaPlayer.play();
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.dispose();
            SwingUtilities.invokeLater(() -> game.setController(controller));
        });
    }
}
