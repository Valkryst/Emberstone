package com.valkryst.Emberstone.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.valkryst.Emberstone.component.CollisionBoxComponent;
import com.valkryst.Emberstone.component.VisibilityComponent;
import lombok.NonNull;

public class VisibilitySystem extends EntitySystem {
	private final CameraSystem cameraSystem;

	/** All entities to be updated by the system. */
	private ImmutableArray<Entity> entities;

	/** All {@link CollisionBoxComponent}s. */
	private final ComponentMapper<CollisionBoxComponent> boundingBoxes = ComponentMapper.getFor(CollisionBoxComponent.class);

	/** All {@link VisibilityComponent}s. */
	private final ComponentMapper<VisibilityComponent> visibilities = ComponentMapper.getFor(VisibilityComponent.class);

	public VisibilitySystem(final @NonNull CameraSystem cameraSystem) {
		this.cameraSystem = cameraSystem;
	}

	@Override
	public void addedToEngine(final Engine engine) {
		entities = engine.getEntitiesFor(Family.all(
			CollisionBoxComponent.class,
			VisibilityComponent.class
		).get());
	}

	@Override
	public void update(final float deltaTime) {
		entities.forEach(entity -> {
			final var boundingBox = boundingBoxes.get(entity);
			final var visibility = visibilities.get(entity);
			visibility.setVisible(cameraSystem.isVisible(boundingBox));
		});
	}
}
