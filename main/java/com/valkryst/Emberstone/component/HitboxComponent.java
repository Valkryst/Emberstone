package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import com.valkryst.Emberstone.display.model.SettingsModel;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HitboxComponent implements Component {
	private final static Color COLOR = new Color(0, 255, 0, 128);

	@Getter
	private final Rectangle2D hitbox = new Rectangle2D.Float();

	public void paint(final Graphics2D graphics2D) {
		if (!SettingsModel.getInstance().isRendererDebuggingEnabled()) {
			return;
		}

		final var originalColor = graphics2D.getColor();

		graphics2D.setColor(COLOR);
		graphics2D.fill(hitbox);
		graphics2D.setColor(originalColor);
	}

	public void update(final AnimationComponent animation, final FacingComponent facing, final PositionComponent position) {
		final var hitbox = animation.getAnimation().getCurrentHitbox();
		final var frame = animation.getAnimation().getCurrentFrame();

		float x;
		if (facing.getDirection() == FacingComponent.Direction.LEFT) {
			x = frame.width() - (hitbox.x() + hitbox.width());
		} else {
			x = hitbox.x();
		}

		this.hitbox.setRect(
			position.getX() + x,
			position.getY() + hitbox.y(),
			hitbox.width(),
			hitbox.height()
		);
	}
}
