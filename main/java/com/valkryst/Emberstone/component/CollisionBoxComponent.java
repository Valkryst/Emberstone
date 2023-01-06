package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import com.valkryst.Emberstone.display.model.SettingsModel;
import lombok.Getter;
import lombok.ToString;

import java.awt.*;
import java.awt.geom.Rectangle2D;

@ToString
public class CollisionBoxComponent implements Component {
	private final static Color COLOR = new Color(255, 0, 220, 128);

	@Getter private final Rectangle2D collisionBox = new Rectangle2D.Float();

	public void paint(final Graphics2D graphics2D) {
		if (!SettingsModel.getInstance().isRendererDebuggingEnabled()) {
			return;
		}

		final var originalColor = graphics2D.getColor();

		graphics2D.setColor(COLOR);
		graphics2D.fill(collisionBox);
		graphics2D.setColor(originalColor);
	}

	public void update(final AnimationComponent animation, final FacingComponent facing, final PositionComponent position) {
		final var collisionBox = animation.getAnimation().getCurrentCollisionBox();
		final var frame = animation.getAnimation().getCurrentFrame();

		float x;
		if (facing.getDirection() == FacingComponent.Direction.LEFT) {
			x = frame.width() - (collisionBox.x() + collisionBox.width());
		} else {
			x = collisionBox.x();
		}

		this.collisionBox.setRect(
			position.getX() + x,
			position.getY() + collisionBox.y(),
			collisionBox.width(),
			collisionBox.height()
		);
	}
}
