package com.valkryst.Emberstone.display.model;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.valkryst.Emberstone.Keyboard;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.component.VelocityComponent;
import com.valkryst.Emberstone.system.MovementSystem;

import java.awt.event.KeyEvent;

public class GameModel extends Model {
    private final Engine engine = new Engine();
    private final Entity playerEntity = new Entity();

    public GameModel() {
        engine.addSystem(new MovementSystem());

        playerEntity.add(new PositionComponent(0, 0));
        playerEntity.add(new VelocityComponent(0, 0));
        engine.addEntity(playerEntity);
    }

    public void setDeltaTime(final Float deltaTime) {
        engine.update(deltaTime);

        // todo This control code should be in the controller.
        final var keyboard = Keyboard.getInstance();
        if (keyboard.isPressed(KeyEvent.VK_W)) {
            playerEntity.getComponent(PositionComponent.class).updateY(-32 * deltaTime);
        } else if (keyboard.isPressed(KeyEvent.VK_S)) {
            playerEntity.getComponent(PositionComponent.class).updateY(32 * deltaTime);
        } else if (keyboard.isPressed(KeyEvent.VK_A)) {
            playerEntity.getComponent(PositionComponent.class).updateX(-32 * deltaTime);
        } else if (keyboard.isPressed(KeyEvent.VK_D)) {
            playerEntity.getComponent(PositionComponent.class).updateX(32 * deltaTime);
        }

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
