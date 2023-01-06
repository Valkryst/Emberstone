package com.valkryst.Emberstone.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.valkryst.Emberstone.component.AnimationComponent;
import com.valkryst.Emberstone.component.FacingComponent;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.component.VisibilityComponent;

public class AnimationSystem extends EntitySystem {
	/** All entities to be updated by the system. */
	private ImmutableArray<Entity> entities;

	/** All {@link AnimationComponent}s. */
	private final ComponentMapper<AnimationComponent> animations = ComponentMapper.getFor(AnimationComponent.class);

	/** All {@link FacingComponent}s. */
	private final ComponentMapper<FacingComponent> facings = ComponentMapper.getFor(FacingComponent.class);

	/** All {@link VelocityComponent}s. */
	private final ComponentMapper<VelocityComponent> velocities = ComponentMapper.getFor(VelocityComponent.class);

	/** All {@link VisibilityComponent}s. */
	private final ComponentMapper<VisibilityComponent> visibilities = ComponentMapper.getFor(VisibilityComponent.class);

	@Override
	public void addedToEngine(final Engine engine) {
		entities = engine.getEntitiesFor(
			Family.one(AnimationComponent.class).get()
		);
	}

	@Override
	public void update(final float deltaTime) {
		entities.forEach(entity -> {
			// We only want to animate visible entities.
			final var visibility = visibilities.get(entity);
			if (!visibility.isVisible()) {
				return;
			}

			final var animation = animations.get(entity);
			final var facing = facings.get(entity);
			final var velocity = velocities.get(entity);

			if (velocity.getX() < 0) {
				facing.setDirection(FacingComponent.Direction.LEFT);
			} else if (velocity.getX() > 0) {
				facing.setDirection(FacingComponent.Direction.RIGHT);
			}

			if (velocity.getX() != 0 || velocity.getY() != 0) {
				animation.setAnimation("player", AnimationComponent.Type.WALKING);
			} else {
				animation.setAnimation("player", AnimationComponent.Type.IDLE);
			}

			animations.get(entity)
					  .getAnimation()
					  .update(deltaTime);
		});
	}
}
