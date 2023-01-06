package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import com.valkryst.V2DSprite.Animation;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Locale;

@Data
@ToString
public class AnimationComponent implements Component {
	private Animation animation;
	private Type type;

	/**
	 * Constructs a new {@code AnimationComponent}.
	 *
	 * @param spriteName
	 * 			<p>The name of the sprite.</p>
	 *
	 *			<p>
	 *			    See  {@link Animation#Animation(String, String)} for more
	 *			    information.
	 *			</p>
	 *
	 * @param type The type of animation.
	 */
	public AnimationComponent(final @NonNull String spriteName, final @NonNull Type type) {
		setAnimation(spriteName, type);
	}

	public void paint(final Graphics2D graphics2D, final FacingComponent facing) {
		if (facing.getDirection() == FacingComponent.Direction.LEFT) {
			final var transform = AffineTransform.getScaleInstance(-1, 1);
			animation.draw(graphics2D, transform);
		} else {
			animation.draw(graphics2D);
		}
	}

	public void setAnimation(final @NonNull String spriteName, final @NonNull Type type) {
		if (this.type == type) {
			return;
		}

		animation = new Animation(spriteName, type.name().toLowerCase(Locale.ROOT));
		this.type = type;
	}

	public enum Type {
		IDLE,
		WALKING
	}
}
