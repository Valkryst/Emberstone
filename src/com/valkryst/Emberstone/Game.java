package com.valkryst.Emberstone;

import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.controller.LevelController;
import com.valkryst.Emberstone.mvc.controller.VideoController;
import com.valkryst.Emberstone.mvc.view.LevelView;
import com.valkryst.Emberstone.mvc.view.VideoView;
import com.valkryst.Emberstone.mvc.view.View;
import javafx.embed.swing.JFXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class Game extends JFrame {
    /** The singleton instance. */
    private final static Game INSTANCE = new Game();

    private final static Dimension VIEW_DIMENSIONS = new Dimension(1920, 1080);

    /** The active MVC controller. */
    private Controller controller;

    /** Constructs a new Game. */
    private Game() {
        this.setBackground(Color.BLACK);
        this.setTitle("Emberstone");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIgnoreRepaint(false);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });

        try {
            this.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResource("icon/Icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * Retrieves the view's width.
     *
     * @return
     *          The view's width.
     */
    public int getViewWidth() {
        return VIEW_DIMENSIONS.width;
    }

    /**
     * Retrieves the view's height.
     *
     * @return
     *          The view's height.
     */
    public int getViewHeight() {
        return VIEW_DIMENSIONS.height;
    }

    public void setController(final Controller controller) {
        if (controller == null) {
            return;
        }

        // Remove previous controller.
        if (this.controller != null) {
            if (this.controller instanceof VideoController) {
                this.remove(((VideoView) this.controller.getView()).getVideoPanel());
            } else if (this.controller instanceof LevelController) {
                ((LevelController) this.controller).stop();
                this.remove(((LevelView) this.controller.getView()).getCanvas());

                // Do a delayed repaint to ensure the Canvas' last draw doesn't affect the new view.
                final JFrame frame = this;
                final CountDownLatch countDownLatch = new CountDownLatch(10);

                final Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (countDownLatch.getCount() > 0) {
                            frame.repaint();
                            countDownLatch.countDown();
                        } else {
                            this.cancel();
                        }
                    }
                }, 0, 32);
            } else {
                this.remove(this.controller.getView());
            }
        }

        // Add new controller.
        this.controller = controller;

        if (controller instanceof VideoController) {
            final VideoView view = (VideoView) controller.getView();

            final JFXPanel videoPanel = view.getVideoPanel();
            videoPanel.setSize(VIEW_DIMENSIONS);
            videoPanel.setPreferredSize(VIEW_DIMENSIONS);

            this.add(videoPanel);

            this.requestFocus();
            videoPanel.requestFocus();
            videoPanel.requestFocusInWindow();
        } else if (controller instanceof LevelController) {
            final Canvas canvas = ((LevelView) controller.getView()).getCanvas();
            canvas.setSize(VIEW_DIMENSIONS);
            canvas.setPreferredSize(VIEW_DIMENSIONS);

            this.add(canvas);

            this.requestFocus();
            canvas.requestFocus();
            canvas.requestFocusInWindow();

            ((LevelController) controller).start();
        } else {
            final View view = controller.getView();

            view.setSize(VIEW_DIMENSIONS);
            view.setPreferredSize(VIEW_DIMENSIONS);
            this.add(view);

            this.requestFocus();
            view.requestFocus();
            view.requestFocusInWindow();
        }

        // Refresh the frame.
        this.pack();
        this.revalidate();
        this.repaint();

        // Show the frame if it hasn't yet been displayed.
        if (this.isVisible() == false) {
            // Center frame on screen.
            final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
            int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
            this.setLocation(x, y);

            this.setVisible(true);
        }
    }
}
