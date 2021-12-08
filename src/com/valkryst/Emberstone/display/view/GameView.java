package com.valkryst.Emberstone.display.view;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.display.controller.GameController;
import com.valkryst.Emberstone.display.model.SettingsModel;

import java.awt.*;
import java.beans.PropertyChangeEvent;

public class GameView extends View {
	private Engine engine;

    public GameView(final GameController controller) {
        controller.startGame();
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("Engine")) {
			if (engine == null) {
				engine = (Engine) event.getNewValue();
			}

			repaint();
        }
    }

	@Override
	public void paintComponent(final Graphics gc) {
		final var gc2d = (Graphics2D) gc;
		SettingsModel.getInstance().applyRenderingHints(gc2d);

		super.paintComponents(gc2d);

		if (engine == null) {
			return;
		}

		// Ensure only the visible portions of the canvas are drawn on.
		gc2d.setClip(0, 0, this.getWidth(), this.getHeight());

		gc2d.setColor(Color.BLACK);
		gc2d.fillRect(0, 0, this.getWidth(), this.getHeight());


		final var positions = ComponentMapper.getFor(PositionComponent.class);
		gc2d.setColor(Color.RED);
		engine.getEntities().forEach(e -> {
			final var position = positions.get(e);
			gc2d.fillRect((int) position.getX(), (int) position.getY(), 32, 32);
		});
	}

}
