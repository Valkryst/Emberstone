package com.valkryst.Emberstone;

import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.controller.Controller;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.EventListener;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Game {
    /** The singleton instance. */
    private final static Game INSTANCE = new Game();

    private final Dimension canvasDimensions = new Dimension(1920, 1080);

    /** The scheduled executor service that handles the game's update-render loop. */
    private ScheduledExecutorService gameLoopExecutorService;

    /** The frame in which the game is drawn. */
    @Getter private final JFrame frame;

    /** The canvas on which the game is rendered. */
    private final Canvas canvas;

    /** The component in which videos are played. */
    @Getter private JFXPanel videoPlayer;

    /** The active MVC controller. */
    private Controller controller;

    /** Constructs a new Game. */
    private Game() {
        // Initialize video player
        videoPlayer = new JFXPanel();
        videoPlayer.setBackground(Color.BLACK);
        videoPlayer.setPreferredSize(canvasDimensions);

        // Initialize Canvas
        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        canvas.setPreferredSize(canvasDimensions);

        frame = new JFrame();
        frame.setBackground(Color.BLACK);
        frame.setTitle("Emberstone");
        frame.add(canvas);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setIgnoreRepaint(false);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });

        try {
            frame.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResource("icon/Icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);

        if (Settings.getInstance().isDebugModeOn()) {
            System.out.println("Adding event listeners for the Game class.");
        }

        addEventListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent keyEvent) {}

            @Override
            public void keyPressed(final KeyEvent keyEvent) {}

            @Override
            public void keyReleased(final KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_F1: {
                        final Settings settings = Settings.getInstance();
                        settings.setDebugModeOn(!settings.isDebugModeOn());
                        break;
                    }
                    case KeyEvent.VK_F2: {
                        final Settings settings = Settings.getInstance();
                        settings.setDebugBoundingBoxesOn(!settings.areDebugBoundingBoxesOn());
                        break;
                    }
                    case KeyEvent.VK_F3: {
                        final Settings settings = Settings.getInstance();
                        settings.setDebugSpawnPointsOn(!settings.areDebugSpawnPointsOn());
                        break;
                    }
                    case KeyEvent.VK_F4: {
                        final Settings settings = Settings.getInstance();
                        settings.setDebugAudioOn(!settings.isDebugAudioOn());
                        break;
                    }
                }
            }
        });
    }

    /** Starts the game loop. */
    private void startGameLoop() {
        if (gameLoopExecutorService != null) {
            return;
        }

        if (Settings.getInstance().isDebugModeOn()) {
            System.out.println(Thread.currentThread().getName() + ": Starting the game loop.");
        }

        canvas.setBackground(new Color(47, 47, 46));
        canvas.requestFocus();
        canvas.requestFocusInWindow();

        /*
         * When switching from the JFXPanel video player to the Canvas, there's an issue where the draw
         * function freezes whenever "gc = (Graphics2D) bs.getDrawGraphics();" is run. To fix this, although
         * I don't fully understand how this fixes it, you have to re-create the canvas' buffer strategy.
         */
        canvas.createBufferStrategy(2);

        final AtomicInteger frameCounter = new AtomicInteger(0);
        final AtomicInteger fps = new AtomicInteger(0);
        final AtomicLong lastFPSDisplayTime = new AtomicLong(0);

        gameLoopExecutorService = Executors.newSingleThreadScheduledExecutor();
        gameLoopExecutorService.scheduleAtFixedRate(() -> {
            try {
                update((double) Settings.getInstance().getTargetFps() / (fps.get() <= 1 ? 60.0 : fps.get()));
                draw(fps.get());

                final long curr = System.currentTimeMillis();
                frameCounter.incrementAndGet();

                if (curr - lastFPSDisplayTime.get() > 1000) {
                    fps.set(frameCounter.get());
                    frameCounter.set(0);
                    lastFPSDisplayTime.set(System.currentTimeMillis());
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }, 0, 1000 / Settings.getInstance().getTargetFps(), TimeUnit.MILLISECONDS);
    }

    /** Stops the game loop. */
    private void stopGameLoop() {
        if (gameLoopExecutorService == null) {
            return;
        }

        if (Settings.getInstance().isDebugModeOn()) {
            System.out.println(Thread.currentThread().getName() + ": Stopping the game loop.");
        }

        canvas.setBackground(Color.BLACK);

        gameLoopExecutorService.shutdown();
        gameLoopExecutorService = null;
    }

    /**
     * Updates the game's state.
     *
     * @param deltaTime
     *          The delta time.
     */
    private void update(final double deltaTime) {
        if (controller == null) {
            return;
        }

        controller.update(deltaTime);
    }

    /**
     * Renders the current state of the game.
     *
     * @param fps
     *          The game's FPS.
     */
    private void draw(final int fps) {
        if (controller == null) {
            return;
        }

        final BufferStrategy bs = canvas.getBufferStrategy();

        do {
            do {
                final Graphics2D gc;

                try {
                    gc = (Graphics2D) bs.getDrawGraphics();

                    // Whether to bias algorithm choices more for speed or quality when evaluating tradeoffs.
                    gc.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                    // Controls how closely to approximate a color when storing into a destination with limited
                    // color resolution.
                    gc.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

                    // Controls the accuracy of approximation and conversion when storing colors into a
                    // destination image or surface.
                    gc.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

                    // Controls how image pixels are filtered or resampled during an image rendering operation.
                    gc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                    // FontType characters are pre-rendered images, so no need for AA.
                    //
                    // Controls whether or not the geometry rendering methods of a Graphics2D object will
                    // attempt to reduce aliasing artifacts along the edges of shapes.
                    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                    // Everything is done VIA images, so there's no need for text rendering options.
                    //gc.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    //gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    // This code sets the appropriate fractional metrics/anti aliasing
                    final Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
                    gc.setRenderingHints(desktopHints);

                    // It's possible that users will be using a large amount of transparent layers, so we want
                    // to ensure that rendering is as quick as we can get it.
                    //
                    // A general hint that provides a high levels recommendation as to whether to bias alpha
                    // blending algorithm choices more for speed or quality when evaluating tradeoffs.
                    gc.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

                    // Ensure only the visible portions of the canvas are drawn on.
                    gc.setClip(0, 0, canvas.getWidth(), canvas.getHeight());

                    // Clear the canvas.
                    gc.setColor(canvas.getBackground());
                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                    controller.draw(gc);

                    if (Settings.getInstance().isDebugModeOn()) {
                        gc.setColor(Color.MAGENTA);
                        gc.drawString("FPS: " + fps, 16, 16);
                    }

                    gc.dispose();
                } catch (final NullPointerException | IllegalStateException e) {
                    if (bs == null) {
                        try {
                            canvas.createBufferStrategy(2);
                        } catch (final IllegalStateException ex) {
                            return;
                        }

                        draw(fps);
                        return;
                    }
                }
            } while (bs.contentsRestored()); // Repeat render if drawing buffer contents were restored.

            try {
                bs.show();
            } catch (final IllegalStateException ignored) {
                // Occurs when the program is closed while the screen is rendering.
            }
        } while (bs.contentsLost()); // Repeat render if drawing buffer was lost.
    }

    /**
     * Adds an event listener to the canvas.
     *
     * @param eventListener
     *          The event listener.
     */
    public void addEventListener(final EventListener eventListener) {
        if (eventListener == null) {
            return;
        }

        if (eventListener instanceof KeyListener) {
            if (Settings.getInstance().isDebugModeOn()) {
                System.out.println("\t\tAdded KeyListener.");
            }

            canvas.addKeyListener((KeyListener) eventListener);
            return;
        }

        if (eventListener instanceof MouseListener) {
            if (Settings.getInstance().isDebugModeOn()) {
                System.out.println("\t\tAdded MouseListener.");
            }

            canvas.addMouseListener((MouseListener) eventListener);
            return;
        }

        if (eventListener instanceof MouseMotionListener) {
            if (Settings.getInstance().isDebugModeOn()) {
                System.out.println("\t\tAdded MouseMotionListener.");
            }

            canvas.addMouseMotionListener((MouseMotionListener) eventListener);
            return;
        }

        throw new IllegalStateException("The " + eventListener.getClass().getSimpleName() + " is not supported.");
    }

    /**
     * Removes an event listener from the canvas.
     *
     * @param eventListener
     *          The event listener.
     */
    public void removeEventListener(final EventListener eventListener) {
        if (eventListener == null) {
            return;
        }

        if (eventListener instanceof KeyListener) {
            if (Settings.getInstance().isDebugModeOn()) {
                System.out.println("\t\tRemoved KeyListener.");
            }

            canvas.removeKeyListener((KeyListener) eventListener);
            return;
        }

        if (eventListener instanceof MouseListener) {
            if (Settings.getInstance().isDebugModeOn()) {
                System.out.println("\t\tRemoved MouseListener.");
            }

            canvas.removeMouseListener((MouseListener) eventListener);
            return;
        }

        if (eventListener instanceof MouseMotionListener) {
            if (Settings.getInstance().isDebugModeOn()) {
                System.out.println("\t\tRemoved MouseMotionListener.");
            }

            canvas.removeMouseMotionListener((MouseMotionListener) eventListener);
            return;
        }

        throw new IllegalStateException("The " + eventListener.getClass().getSimpleName() + " is not supported.");
    }

    /**
     * Plays a video, then displays a view after the video is finished.
     *
     * @param video
     *          The video.
     *
     * @param controller
     *          The controller of the view to display after the video.
     */
    public void playVideo(final Video video, final Controller controller) {
        final Settings settings = Settings.getInstance();
        final Media media = new Media(video.getUri());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);

        if (settings.isDebugModeOn()) {
            System.out.println(Thread.currentThread().getName() + ": Loading video at " + System.currentTimeMillis() + "ms: " + media.getSource());
        }

        final Runnable switchToCanvas = () -> {
            try {
                Platform.runLater(mediaPlayer::dispose);

                SwingUtilities.invokeLater(() -> {
                    if (settings.isDebugModeOn()) {
                        System.out.println(Thread.currentThread().getName() + ": Removing video player from the frame.");
                    }

                    frame.remove(videoPlayer);
                    frame.add(canvas);
                    frame.revalidate();

                    if (settings.isDebugModeOn()) {
                        System.out.println(Thread.currentThread().getName() + ": Removed video player from the frame.");
                    }

                    setController(controller);

                    // AWT doesn't play nicely with JavaFX, so we have to start/stop the game loop when playing
                    // video.
                    Game.getInstance().stopGameLoop();
                    Game.getInstance().startGameLoop();
                });
            } catch (final Exception e) {
                e.printStackTrace();
            }
        };

        // Sometimes the video fails to load, so we need to skip it.
        final Timer faultDetectionTimer = new Timer();
        faultDetectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                switchToCanvas.run();
            }
        }, 3000);

        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnReady(() -> {
            try {
                faultDetectionTimer.cancel();
                if (settings.isDebugModeOn()) {
                    System.out.println(Thread.currentThread().getName() + ": Video loaded at " + System.currentTimeMillis() + "ms.");
                }

                final MediaView mediaView = new MediaView(mediaPlayer);
                mediaView.setPreserveRatio(true);

                final StackPane root = new StackPane();
                root.getChildren().add(mediaView);

                final Scene scene = new Scene(root);
                scene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        if (settings.isDebugModeOn()) {
                            System.out.println(Thread.currentThread().getName() + ": Video skipped by user.");
                        }

                        switchToCanvas.run();
                    }
                });

                videoPlayer.setScene(scene);

                SwingUtilities.invokeLater(() -> {
                    if (controller != null) {
                        this.controller.removeFromCanvas();
                        this.controller = null;
                    }

                    if (settings.isDebugModeOn()) {
                        System.out.println(Thread.currentThread().getName() + ": Adding video player to the frame.");
                    }

                    frame.remove(canvas);
                    frame.add(videoPlayer);
                    frame.revalidate();

                    videoPlayer.requestFocus();

                    if (settings.isDebugModeOn()) {
                        System.out.println(Thread.currentThread().getName() + ": Added video player to the frame.");
                    }

                    // AWT doesn't play nicely with JavaFX, so we have to start/stop the game loop when playing
                    // video.
                    this.stopGameLoop();
                });

                mediaPlayer.play();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
        mediaPlayer.setOnEndOfMedia(switchToCanvas);
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static Game getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves the canvas' width.
     *
     * @return
     *          The canvas' width.
     */
    public int getCanvasWidth() {
        return canvasDimensions.width;
    }

    /**
     * Retrieves the canvas' height.
     *
     * @return
     *          The canvas' height.
     */
    public int getCanvasHeight() {
        return canvasDimensions.height;
    }

    /**
     * Retrieves the mouse's position on the canvas.
     *
     * @return
     *          The mouse's position on the canvas.
     */
    public Point getMousePosition() {
        return canvas.getMousePosition();
    }

    /**
     * Sets the active MVC controller whose view is drawn on the canvas.
     *
     * @param controller
     *          The controller.
     */
    public void setController(final Controller controller) {
        if (controller == null) {
            return;
        }

        if (this.controller != null) {
            this.controller.removeFromCanvas();
        }

        final Settings settings = Settings.getInstance();

        if (settings.isDebugModeOn()) {
            System.out.println(Thread.currentThread().getName() + ": Setting new controller.");
        }

        this.controller = controller;
        controller.addToCanvas();

        if (settings.isDebugModeOn()) {
            System.out.println(Thread.currentThread().getName() + ": Set new controller.");
        }

        if (gameLoopExecutorService == null) {
            if (settings.isDebugModeOn()) {
                System.out.println(Thread.currentThread().getName() + ": The game loop wasn't running when a new controller was set. Starting the game loop.");
            }

            startGameLoop();
        }
    }
}
