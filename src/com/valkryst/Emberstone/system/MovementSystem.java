package com.valkryst.Emberstone.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.component.PositionComponent;

public class MovementSystem extends EntitySystem {
    /** All entities to be updated by the system. */
    private ImmutableArray<Entity> entities;

    /** All position components. */
    private ComponentMapper<PositionComponent> positionComponents = ComponentMapper.getFor(PositionComponent.class);
    /** All velocity components. */
    private ComponentMapper<VelocityComponent> velocityComponents = ComponentMapper.getFor(VelocityComponent.class);

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
            final var position = positionComponents.get(entity);
            final var velocity = velocityComponents.get(entity);

            position.updateX(velocity.getX() * deltaTime);
            position.updateY(velocity.getY() * deltaTime);
        });
    }
}
