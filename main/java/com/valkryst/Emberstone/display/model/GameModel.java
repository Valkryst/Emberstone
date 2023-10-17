package com.valkryst.Emberstone.display.model;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.valkryst.Emberstone.component.*;
import com.valkryst.Emberstone.display.controller.GameController;
import com.valkryst.Emberstone.display.view.GameView;
import com.valkryst.Emberstone.system.*;
import com.valkryst.VMVC.model.Model;
import lombok.Getter;

public class GameModel extends Model<GameController, GameView> {
    private final Engine engine = new Engine();
    @Getter private final Entity playerEntity = new Entity();

	@Getter private final CameraSystem cameraSystem = new CameraSystem();

    public GameModel() {
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new CollisionBoxSystem());
		engine.addSystem(cameraSystem);
		engine.addSystem(new HitboxSystem());
        engine.addSystem(new MovementSystem());
		engine.addSystem(new VisibilitySystem(cameraSystem));

		playerEntity.add(new AnimationComponent("player", AnimationComponent.Type.WALKING));
		playerEntity.add(new CollisionBoxComponent());
		playerEntity.add(new FacingComponent());
		playerEntity.add(new HitboxComponent());
        playerEntity.add(new PositionComponent(0, 0));
        playerEntity.add(new VelocityComponent(0, 0));
		playerEntity.add(new VisibilityComponent(true));
        engine.addEntity(playerEntity);

		cameraSystem.follow(playerEntity);
    }

	public void updateEngine(final double deltaTime) {
		engine.update((float) deltaTime);
	}

	@Override
	protected GameController createController() {
		return new GameController(this);
	}

	@Override
	protected GameView createView(final GameController controller) {
		return new GameView(controller, engine);
	}
}
