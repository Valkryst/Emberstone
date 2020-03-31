package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.Keyboard;

import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class GameController extends DefaultController {
    private final static int TARGET_FPS = 60;

    private ScheduledExecutorService executorService;

    public void startGame() {
        if (executorService != null) {
            stopGame();
        }

        final var lastTime = new AtomicLong(System.currentTimeMillis());

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            final var currentTime = System.currentTimeMillis();
            final var deltaTime = (currentTime - lastTime.get()) / 1000.0;
            lastTime.set(currentTime);

            final var keyboard = Keyboard.getInstance();
            if (keyboard.isPressed(KeyEvent.VK_W)) {
                super.setModelProperty("PlayerVelocityY", -96);
            } else if (keyboard.isPressed(KeyEvent.VK_S)) {
                super.setModelProperty("PlayerVelocityY", 96);
            } else {
                super.setModelProperty("PlayerVelocityY", 0);
            }

            if (keyboard.isPressed(KeyEvent.VK_A)) {
                super.setModelProperty("PlayerVelocityX", -96);
            } else if (keyboard.isPressed(KeyEvent.VK_D)) {
                super.setModelProperty("PlayerVelocityX", 96);
            } else {
                super.setModelProperty("PlayerVelocityX", 0);
            }

            super.setModelProperty("DeltaTime", deltaTime);
        }, 0, 1000 / TARGET_FPS, TimeUnit.MILLISECONDS);
    }

    public void stopGame() {
        if (executorService == null) {
            return;
        }

        executorService.shutdown();
        executorService = null;
    }
}
