package com.valkryst.Emberstone.display.controller;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.valkryst.Emberstone.Keyboard;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.display.view.GameView;
import com.valkryst.Emberstone.system.MovementSystem;
import lombok.Getter;

import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameController extends DefaultController {
    private final static int TARGET_FPS = 60;

    private ScheduledExecutorService executorService;

    @Getter private Engine engine = new Engine();

    public void startGame(final GameView view) {
        if (executorService != null) {
            stopGame();
        }

        engine.addSystem(new MovementSystem());

        final Entity entity = new Entity();
        entity.add(new PositionComponent(0, 0));
        entity.add(new VelocityComponent(0, 0));
        engine.addEntity(entity);

        final var fps = new FPS();
        final var deltaTime = new DeltaTime();

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
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

            view.render();

            fps.update();
        }, 0, 1000 / TARGET_FPS, TimeUnit.MILLISECONDS);
    }

    public void stopGame() {
        if (executorService == null) {
            return;
        }

        executorService.shutdown();
        executorService = null;
    }

    // todo Refactor these classes
    private static class DeltaTime {
        private static long timeOfLastUpdate = System.currentTimeMillis();

        public float getDeltaTime() {
            final float deltaTime = (timeOfLastUpdate - System.currentTimeMillis()) / 1_000_000f;
            timeOfLastUpdate = System.currentTimeMillis();
            return deltaTime;
        }
    }

    private static class FPS {
        private static long timeOfLastUpdate = System.currentTimeMillis();
        private static int frameCount = 0;
        private static int fps = 60;

        public void update() {
            final var currentTime = System.currentTimeMillis();
            if (currentTime - timeOfLastUpdate >= 1000) {
                fps = frameCount;
                timeOfLastUpdate = currentTime;
                frameCount = 0;
            } else {
                frameCount++;
            }
        }
    }
}
