package com.valkryst.Emberstone.display.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameController extends DefaultController {
    private final static int TARGET_FPS = 60;

    private ScheduledExecutorService executorService;

    public void startGame() {
        if (executorService != null) {
            stopGame();
        }

        var timeOfLastDeltaTimeUpdate = System.currentTimeMillis();
        final var fps = new FPS();


        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            final var currentTime = System.currentTimeMillis();

            // Calculate DeltaTime
            final float deltaTime = (currentTime - timeOfLastDeltaTimeUpdate) / 1000f;

            // Calculate FPS

            super.setModelProperty("DeltaTime", deltaTime);
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
            final var currentTime = System.currentTimeMillis();
            final float deltaTime = (currentTime - timeOfLastUpdate) / 1000f;
            timeOfLastUpdate = currentTime;
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
                System.out.println("FPS: " + fps);
            } else {
                frameCount++;
            }
        }
    }
}
