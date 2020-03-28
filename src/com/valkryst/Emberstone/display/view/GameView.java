package com.valkryst.Emberstone.display.view;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.valkryst.Emberstone.Keyboard;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.display.renderer.Renderer;
import com.valkryst.Emberstone.system.MovementSystem;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameView extends View {
    private Renderer renderer;

    public GameView() {
        // Because we're specifically using active rendering with a custom
        // buffer strategy for the GameView, we can ignore repaint events sent
        // by the OS.
        this.setIgnoreRepaint(true);
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent event) {

    }

    @SneakyThrows
    public void setRenderer(final Class<? extends Renderer> rendererClass) {
        if (rendererClass == null) {
            return;
        }

        if (!Renderer.class.isAssignableFrom(rendererClass)) {
            // If the given class is not a subclass of Renderer.
            return;
        }

        final Field peerField = Component.class.getDeclaredField("peer");
        peerField.setAccessible(true);

        final var gameViewPeer = peerField.get(this);

        this.renderer = rendererClass.cast(rendererClass.getDeclaredConstructor(ComponentPeer.class)
                                                        .newInstance(gameViewPeer));
    }


    private final static int TARGET_FPS = 60;
    private ScheduledExecutorService executor;

    private final Engine engine = new Engine();

    public void start() {
        if (executor != null) {
            stop();
        }

        // TEMP CODE
        engine.addSystem(new MovementSystem());

        final Entity entity = new Entity();
        entity.add(new PositionComponent(0, 0));
        entity.add(new VelocityComponent(0, 0));
        engine.addEntity(entity);

        final var fps = new FPS();
        final var deltaTime = new DeltaTime();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            // UPDATE
            engine.update(deltaTime.getDeltaTime());

            final var keyboard = Keyboard.getInstance();
            if (keyboard.isPressed(KeyEvent.VK_W)) {
                entity.getComponent(PositionComponent.class).updateY(-32);
            } else if (keyboard.isPressed(KeyEvent.VK_S)) {
                entity.getComponent(PositionComponent.class).updateY(32);
            } else if (keyboard.isPressed(KeyEvent.VK_A)) {
                entity.getComponent(PositionComponent.class).updateX(-32);
            } else if (keyboard.isPressed(KeyEvent.VK_D)) {
                entity.getComponent(PositionComponent.class).updateX(32);
            }

            // RENDER
            final var gc = (Graphics2D) renderer.getGraphics();

            // Ensure only the visible portions of the canvas are drawn on.
            gc.setClip(0, 0, this.getWidth(), this.getHeight());

            gc.setColor(Color.BLACK);
            gc.fillRect(0, 0, this.getWidth(), this.getHeight());


            final var positions = ComponentMapper.getFor(PositionComponent.class);
            gc.setColor(Color.RED);
            engine.getEntities().forEach(e -> {
                final var position = positions.get(e);
                gc.fillRect((int) position.getX(), (int) position.getY(), 32, 32);
            });

            fps.update();
        }, 0, 1000 / TARGET_FPS, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (executor == null) {
            return;
        }

        executor.shutdown();
        executor = null;
    }

    // todo Refactor these classes
    private static class DeltaTime {
        private long timeOfLastUpdate = System.currentTimeMillis();

        public float getDeltaTime() {
            final float deltaTime = (timeOfLastUpdate - System.currentTimeMillis()) / 1_000_000f;
            timeOfLastUpdate = System.currentTimeMillis();
            return deltaTime;
        }
    }

    private static class FPS {
        private long timeOfLastUpdate = System.currentTimeMillis();
        private int frameCount = 0;

        public void update() {
            final var currentTime = System.currentTimeMillis();
            if (currentTime - timeOfLastUpdate >= 1000) {
                System.out.println("FPS: " + frameCount);
                timeOfLastUpdate = currentTime;
                frameCount = 0;
            } else {
                frameCount++;
            }
        }
    }
}
