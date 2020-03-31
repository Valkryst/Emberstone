package com.valkryst.Emberstone.display.v1.controller;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.display.v1.view.LevelView;
import com.valkryst.Emberstone.system.MovementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LevelController extends Controller<LevelView> {
    private final static int TARGET_FPS = 60;
    private ScheduledExecutorService executor;

    private final Engine engine = new Engine();

    public LevelController(final JFrame frame, final Dimension dimensions) {
        super(new LevelView(frame, dimensions));

        engine.addSystem(new MovementSystem());

        final Entity entity = new Entity();
        entity.add(new VelocityComponent(0, 0));
        entity.add(new PositionComponent(0, 0));
        engine.addEntity(entity);

        super.getView().getCanvas().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {}

            @Override
            public void keyPressed(final KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: {
                        entity.getComponent(PositionComponent.class).updateY(-32);
                        break;
                    }
                    case KeyEvent.VK_S: {
                        entity.getComponent(PositionComponent.class).updateY(32);
                        break;
                    }
                    case KeyEvent.VK_A: {
                        entity.getComponent(PositionComponent.class).updateX(-32);
                        break;
                    }
                    case KeyEvent.VK_D: {
                        entity.getComponent(PositionComponent.class).updateX(32);
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {}
        });
    }

    public void start() {
        if (executor != null) {
            stop();
        }

        final var fps = new FPS();
        final var deltaTime = new DeltaTime();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            engine.update(deltaTime.getDeltaTime());

            super.getView().draw(engine);

            fps.update();
        }, 0, 1000 / TARGET_FPS, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (executor == null) {
            return;
        }

        executor.shutdown();
        executor = null;
        super.getView().getCanvas().getBufferStrategy().dispose();
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
