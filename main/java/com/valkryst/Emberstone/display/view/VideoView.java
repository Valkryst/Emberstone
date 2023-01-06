package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.Video;
import com.valkryst.Emberstone.display.controller.VideoController;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class VideoView extends View<VideoController> {
	private MediaPlayer player;

	public VideoView(final @NonNull VideoController controller, final Video video) {
		super(controller);

		this.setBackground(Color.BLACK);
		this.setLayout(new BorderLayout());

		final var videoPanel = new JFXPanel();
		this.add(videoPanel);

		Platform.runLater(() -> {
			player = new MediaPlayer(new Media(video.getFilePath()));
			player.setAutoPlay(true);

			/*
			 * If there are any issues while loading the video, this will
			 * ensure the user is advanced to the next view.
			 */
			final var errorTimer = new Timer();
			SwingUtilities.invokeLater(() -> {
				errorTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						skip().run();
					}
				}, 3000);
			});

			player.setOnReady(() -> {
				SwingUtilities.invokeLater(errorTimer::cancel);

				final var mediaView = new MediaView(player);

				final var root = new StackPane();
				root.getChildren().add(mediaView);

				final var scene = new Scene(root, javafx.scene.paint.Color.BLACK);
				videoPanel.setScene(scene);

				// Ensure the video is always resized to fit within the scene.
				mediaView.fitWidthProperty().bind(scene.widthProperty());
				mediaView.fitHeightProperty().bind(scene.heightProperty());

				root.requestFocus();

				scene.setOnKeyReleased(e -> {
					if (e.getCode() == KeyCode.ESCAPE) {
						player.stop();
						skip().run();
					}
				});
			});

			player.setOnEndOfMedia(skip());
		});
	}

	private Runnable skip() {
		return () -> {
			Platform.runLater(() -> player.dispose());
			SwingUtilities.invokeLater(controller::skip);
		};
	}
}
