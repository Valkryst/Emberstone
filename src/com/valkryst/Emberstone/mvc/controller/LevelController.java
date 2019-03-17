package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.entity.*;
import com.valkryst.Emberstone.item.EquipmentSlot;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.item.generator.EquipmentGenerator;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.Music;
import com.valkryst.Emberstone.media.SoundEffect;
import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.controller.menu.EscapeController;
import com.valkryst.Emberstone.mvc.controller.menu.InventoryController;
import com.valkryst.Emberstone.mvc.controller.menu.MainController;
import com.valkryst.Emberstone.mvc.view.LevelView;
import com.valkryst.Emberstone.statistic.StatisticType;
import lombok.Getter;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LevelController extends Controller {
    private ScheduledExecutorService gameLoopService;

    @Getter private com.valkryst.Emberstone.map.Map map;

    int currMapId = 0;

    public LevelController(final Player player) {
        super(new LevelView());

        map = new com.valkryst.Emberstone.map.Map(player, "levels/Level1.json", 60);
        map.setShardsRequired(20);

        GameAudio.getInstance().stopAllActiveMusic();
        GameAudio.getInstance().playMusic(Music.GAME);

        // Add player controls
        final Timer autoAttackTimer = new Timer(500, actionEvent -> {
            final Entity mapPlayer = map.getPlayer();

            if (mapPlayer.getAnimationState() == AnimationState.IDLE) {
                mapPlayer.setAnimation(AnimationState.ATTACKING_IDLE);
            }

            if (mapPlayer.getAnimationState() == AnimationState.RUNNING) {
                mapPlayer.setAnimation(AnimationState.ATTACKING_RUNNING);
            }
        });
        autoAttackTimer.setInitialDelay(0);


        final Canvas canvas = ((LevelView) super.getView()).getCanvas();
        final Controller controller = this;

        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {}

            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    if (map.getPlayer().getAnimationState() == AnimationState.DEFENDING) {
                        map.getPlayer().setAnimation(AnimationState.IDLE);
                    }

                    autoAttackTimer.start();
                } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    map.getPlayer().setAnimation(AnimationState.DEFENDING);
                }
            }

            @Override
            public void mouseReleased(final MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    autoAttackTimer.stop();
                } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    if (map.getPlayer().getAnimationState() == AnimationState.DEFENDING) {
                        map.getPlayer().setAnimation(AnimationState.IDLE);
                    }
                }
            }

            @Override
            public void mouseEntered(final MouseEvent mouseEvent) {}

            @Override
            public void mouseExited(final MouseEvent mouseEvent) {}
        });

        canvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent keyEvent) {}

            @Override
            public void keyPressed(final KeyEvent keyEvent) {
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
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A: {
                        final Entity player = map.getPlayer();
                        player.setHorizontalSpeed(-player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D: {
                        final Entity player = map.getPlayer();
                        player.setHorizontalSpeed(player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W: {
                        final Entity player = map.getPlayer();
                        player.setVerticalSpeed(-player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S: {
                        final Entity player = map.getPlayer();
                        player.setVerticalSpeed(player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_ESCAPE: {
                        Game.getInstance().setController(new EscapeController(getScreenshot(), controller));
                        break;
                    }
                    case KeyEvent.VK_Q: {
                        // Find the chest closest to the player and open it if it's within the player's combat radius.
                        final Point playerPosition = map.getPlayer().getPosition();
                        final int playerCombatRaidus = map.getPlayer().getCombatRadius();

                        Chest chest = null;
                        double chestDist = Double.POSITIVE_INFINITY;

                        final Iterator<Entity> it = map.getEntitiesIterator();
                        while (it.hasNext()) {
                            final Entity entity = it.next();

                            if (entity instanceof Chest) {
                                final Point entityPosition = entity.getPosition();
                                final double distance = Math.sqrt(Math.pow(entityPosition.x - playerPosition.x, 2) + Math.pow(entityPosition.y - playerPosition.y, 2));

                                if (distance <= playerCombatRaidus && distance <= chestDist) {
                                    chest = (Chest) entity;
                                    chestDist = distance;
                                }
                            }
                        }

                        final InventoryController inventoryController = new InventoryController(getScreenshot(), controller);
                        inventoryController.displayPlayerInventory(map.getPlayer().getInventory());
                        inventoryController.displayLootInventory((chest == null ? null : chest.getInventory()));
                        inventoryController.displayPlayerStats(map.getPlayer());
                        Game.getInstance().setController(inventoryController);
                        break;
                    }
                    case KeyEvent.VK_E: {
                        final Point playerPosition = map.getPlayer().getPosition();
                        final int playerCombatRaidus = map.getPlayer().getCombatRadius();

                        // Find the portal closest to the player and enter it if it's within the player's combat radius.
                        Portal portal = null;
                        double dist = Double.POSITIVE_INFINITY;

                        Iterator<Entity> it = map.getEntitiesIterator();
                        while (it.hasNext()) {
                            final Entity entity = it.next();

                            if (entity instanceof Portal) {
                                final Point entityPosition = entity.getPosition();
                                final double distance = Math.sqrt(Math.pow(entityPosition.x - playerPosition.x, 2) + Math.pow(entityPosition.y - playerPosition.y, 2));

                                if (distance <= playerCombatRaidus && distance <= dist) {
                                    portal = (Portal) entity;
                                    dist = distance;
                                }
                            }
                        }

                        if (portal != null) {
                            final Player mapPlayer = map.getPlayer();

                            switch (currMapId) {
                                case 0: {
                                    map = new com.valkryst.Emberstone.map.Map(mapPlayer, "levels/Level2.json", 300);
                                    map.setShardsRequired(60);
                                    break;
                                }
                                case 1: {
                                    Game.getInstance().setController(new VideoController(Video.OUTRO, new MainController()));
                                    break;
                                }
                            }

                            currMapId++;
                        }

                        // Find the chest closest to the player and open it if it's within the player's combat radius.
                        Chest chest = null;
                        dist = Double.POSITIVE_INFINITY;

                        it = map.getEntitiesIterator();
                        while (it.hasNext()) {
                            final Entity entity = it.next();

                            if (entity instanceof Chest) {
                                final Point entityPosition = entity.getPosition();
                                final double distance = Math.sqrt(Math.pow(entityPosition.x - playerPosition.x, 2) + Math.pow(entityPosition.y - playerPosition.y, 2));

                                if (distance <= playerCombatRaidus && distance <= dist) {
                                    chest = (Chest) entity;
                                    dist = distance;
                                }
                            }
                        }

                        if (chest == null) {
                            break;
                        }

                        final InventoryController inventoryController = new InventoryController(getScreenshot(), controller);
                        inventoryController.displayPlayerInventory(map.getPlayer().getInventory());
                        inventoryController.displayLootInventory((chest == null ? null : chest.getInventory()));
                        inventoryController.displayPlayerStats(map.getPlayer());
                        Game.getInstance().setController(inventoryController);
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(final KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D: {
                        map.getPlayer().setHorizontalSpeed(0);
                        break;
                    }
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S: {
                        map.getPlayer().setVerticalSpeed(0);
                        break;
                    }
                }
            }
        });
    }

    /** Starts the update-render loop. */
    public void start() {
        final AtomicInteger frameCounter = new AtomicInteger(0);
        final AtomicInteger fps = new AtomicInteger(0);
        final AtomicLong lastFPSDisplayTime = new AtomicLong(0);

        final double targetFps = Settings.getInstance().getTargetFps();

        gameLoopService = Executors.newSingleThreadScheduledExecutor();
        gameLoopService.scheduleAtFixedRate(() -> {
            try {
                map.update(targetFps / (fps.get() <= 1 ? targetFps : fps.get()));
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
        }, 0, (long) (1000 / targetFps), TimeUnit.MILLISECONDS);
    }

    /** Stops the update-render loop. */
    public void stop() {
        if (gameLoopService == null) {
            return;
        }

        gameLoopService.shutdown();
        gameLoopService = null;
        ((LevelView) super.getView()).getCanvas().getBufferStrategy().dispose();
    }

    public void draw(final int fps) {
        if (gameLoopService == null || gameLoopService.isShutdown() || gameLoopService.isTerminated()) {
            return;
        }

        final Canvas canvas = ((LevelView) super.getView()).getCanvas();
        final BufferStrategy bs = canvas.getBufferStrategy();

        do {
            do {
                final Graphics2D gc;

                try {
                    gc = (Graphics2D) bs.getDrawGraphics();
                    draw(gc, canvas);

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
     * Retrieves a screenshot of the canvas.
     *
     * @return
     *          The screenshot.
     */
    public BufferedImage getScreenshot() {
        final Canvas canvas = ((LevelView) super.getView()).getCanvas();

        final BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gc = (Graphics2D) image.getGraphics();

        draw(gc, canvas);

        gc.dispose();

        return image;
    }

    private void draw(final Graphics2D gc, final Canvas canvas) {
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

        map.draw(gc);
    }
}
