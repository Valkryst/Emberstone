package com.valkryst.Emberstone.display.model;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.system.MovementSystem;

public class GameModel extends Model {
    private final Engine engine = new Engine();
    private final Entity playerEntity = new Entity();

    public GameModel() {
        engine.addSystem(new MovementSystem());

        playerEntity.add(new PositionComponent(0, 0));
        playerEntity.add(new VelocityComponent(0, 0));
        engine.addEntity(playerEntity);
    }

    public void setPlayerVelocityX(final Integer velocity) {
        playerEntity.getComponent(VelocityComponent.class).setX(velocity);
    }

    public void setPlayerVelocityY(final Integer velocity) {
        playerEntity.getComponent(VelocityComponent.class).setY(velocity);
    }

    public void setDeltaTime(final Double deltaTime) {
        engine.update(deltaTime.floatValue());

        /*
         * Normally, this should fire a property change for "DeltaTime", but
         * we're firing one for "Engine" as a hacky way to pass the engine
         * to the view for rendering.
         *
         * Nothing outside of the model relies on delta time.
         */
        super.firePropertyChange("Engine", null, engine);
    }
}
