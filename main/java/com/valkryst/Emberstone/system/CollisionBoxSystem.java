package com.valkryst.Emberstone.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.valkryst.Emberstone.component.AnimationComponent;
import com.valkryst.Emberstone.component.CollisionBoxComponent;
import com.valkryst.Emberstone.component.FacingComponent;
import com.valkryst.Emberstone.component.PositionComponent;

public class CollisionBoxSystem extends EntitySystem {

	/** All entities to be updated by the system. */
	private ImmutableArray<Entity> entities;

	/** All {@link AnimationComponent}s. */
	private final ComponentMapper<AnimationComponent> animations = ComponentMapper.getFor(AnimationComponent.class);

	/** All {@link FacingComponent}s. */
	private final ComponentMapper<FacingComponent> facings = ComponentMapper.getFor(FacingComponent.class);

	/** All {@link CollisionBoxComponent}s. */
	private final ComponentMapper<CollisionBoxComponent> collisionBoxes = ComponentMapper.getFor(CollisionBoxComponent.class);

	/** All {@link PositionComponent}s. */
	private final ComponentMapper<PositionComponent> positions = ComponentMapper.getFor(PositionComponent.class);

	@Override
	public void addedToEngine(final Engine engine) {
		entities = engine.getEntitiesFor(Family.all(
			AnimationComponent.class,
			PositionComponent.class
		).get());
	}

	@Override
	public void update(final float deltaTime) {
		entities.forEach(entity -> {
			final var animation = animations.get(entity);
			final var box = collisionBoxes.get(entity);
			final var facing = facings.get(entity);
			final var position = positions.get(entity);

			box.update(animation, facing, position);
		});
	}
}
