package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.component.FacingComponent;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.display.Display;
import com.valkryst.Emberstone.display.model.GameModel;
import com.valkryst.Emberstone.display.model.MainMenuModel;
import com.valkryst.Emberstone.display.model.SettingsModel;
import com.valkryst.Emberstone.display.view.GameView;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameController extends Controller<GameModel> implements KeyListener {
    private final static int TARGET_FPS = 60;

    private ScheduledExecutorService executorService;

	public GameController(final @NonNull GameModel model) {
		super(model);
	}

	public void startGame(final GameView view) {
		if (executorService != null) {
			stopGame();
		}

		final var fps = new FPS();
		final var deltaTime = new DeltaTime();

		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			super.model.updateEngine(deltaTime.update());
			view.repaint();
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

	@Override
	public void keyTyped(final KeyEvent e) {}

	@Override
	public void keyPressed(final KeyEvent e) {
		final var player = super.model.getPlayerEntity();
		final var velocity = player.getComponent(VelocityComponent.class);

		if (velocity.getY() == 0) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_W -> velocity.setY(-64);
				case KeyEvent.VK_S -> velocity.setY(64);
			}
		}

		if (velocity.getX() == 0) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_A -> {
					player.getComponent(FacingComponent.class).setDirection(FacingComponent.Direction.LEFT);
					velocity.setX(-64);
				}
				case KeyEvent.VK_D -> {
					player.getComponent(FacingComponent.class).setDirection(FacingComponent.Direction.RIGHT);
					velocity.setX(64);
				}
			}
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			stopGame();
			Display.getInstance().setContentPane(new MainMenuModel().createView());
			return;
		}

		if (e.getKeyCode() == KeyEvent.VK_F1) {
			final var settings = SettingsModel.getInstance();
			settings.setRendererDebuggingEnabled(
				!settings.isRendererDebuggingEnabled()
			);

			try {
				settings.save();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}

			return;
		}

		final var player = super.model.getPlayerEntity();
		final var velocity = player.getComponent(VelocityComponent.class);

		if (velocity.getY() != 0) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_W, KeyEvent.VK_S -> velocity.setY(0);
			}
		}

		if (velocity.getX() != 0) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_A, KeyEvent.VK_D -> velocity.setX(0);
			}
		}
	}

	// todo Refactor these classes
    private static class DeltaTime {
        private static long timeOfLastUpdate = System.currentTimeMillis();

        public double update() {
            final var currentTime = System.currentTimeMillis();
            final double deltaTime = (currentTime - timeOfLastUpdate) / 1000d;
            timeOfLastUpdate = currentTime;
            return deltaTime;
        }
    }

    private static class FPS {
        private static long timeOfLastUpdate = System.currentTimeMillis();
        private static int frameCount = 0;

        public void update() {
            final var currentTime = System.currentTimeMillis();
            if (currentTime - timeOfLastUpdate >= 1000) {
				Display.getInstance().getFrame().setTitle("Emberstone - FPS " + frameCount);
                timeOfLastUpdate = currentTime;
                frameCount = 0;
            } else {
                frameCount++;
            }
        }
    }
}
