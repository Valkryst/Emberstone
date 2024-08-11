package com.valkryst.Emberstone.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.component.VelocityComponent;

public class MovementSystem extends EntitySystem {
    /** All entities to be updated by the system. */
    private ImmutableArray<Entity> entities;

    /** All {@link PositionComponent}s. */
    private final ComponentMapper<PositionComponent> positions = ComponentMapper.getFor(PositionComponent.class);
    /** All {@link VelocityComponent}s. */
    private final ComponentMapper<VelocityComponent> velocities = ComponentMapper.getFor(VelocityComponent.class);

    @Override
    public void addedToEngine(final Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
			PositionComponent.class,
			VelocityComponent.class
        ).get());
    }

    @Override
    public void update(final float deltaTime) {
        entities.forEach(entity -> {
            final var position = positions.get(entity);
            final var velocity = velocities.get(entity);
            position.updateX(velocity.getX() * deltaTime);
            position.updateY(velocity.getY() * deltaTime);
        });
    }
}
