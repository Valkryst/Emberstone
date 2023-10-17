package com.valkryst.Emberstone.display.view;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.valkryst.Emberstone.component.*;
import com.valkryst.Emberstone.display.controller.GameController;
import com.valkryst.Emberstone.display.model.SettingsModel;
import com.valkryst.Emberstone.system.CameraSystem;
import com.valkryst.VMVC.view.View;

import java.awt.*;

public class GameView extends View<GameController> {
	private final Engine engine;

	/** All {@link AnimationComponent}s. */
	private final ComponentMapper<AnimationComponent> animations = ComponentMapper.getFor(AnimationComponent.class);

	/** All {@link CollisionBoxComponent}s. */
	private final ComponentMapper<CollisionBoxComponent> collisionBoxes = ComponentMapper.getFor(CollisionBoxComponent.class);

	/** All {@link FacingComponent}s. */
	private final ComponentMapper<FacingComponent> facings = ComponentMapper.getFor(FacingComponent.class);

	/** All {@link HitboxComponent}s. */
	private final ComponentMapper<HitboxComponent> hitboxes = ComponentMapper.getFor(HitboxComponent.class);

	/** All {@link PositionComponent}s. */
	private final ComponentMapper<PositionComponent> positions = ComponentMapper.getFor(PositionComponent.class);

	/** All {@link VisibilityComponent}s. */
	private final ComponentMapper<VisibilityComponent> visibilities = ComponentMapper.getFor(VisibilityComponent.class);

    public GameView(final GameController controller, final Engine engine) {
		super(controller);
		this.engine = engine;
		this.addKeyListener(controller);
        controller.startGame(this);
    }

	@Override
	public void paintComponent(final Graphics gc) {
		final var g2d = (Graphics2D) gc;
		SettingsModel.getInstance().applyRenderingHints(g2d);

		super.paintComponent(g2d);
		super.paintComponents(g2d);

		final var entities = engine.getEntitiesFor(Family.all(
			AnimationComponent.class,
			PositionComponent.class,
			VisibilityComponent.class
		).get());

		final var cameraSystem = engine.getSystem(CameraSystem.class);

		entities.forEach(entity -> {
			final var visibility = visibilities.get(entity);
			if (!visibility.isVisible()) {
				return;
			}

			final var position = positions.get(entity);
			final var animation = animations.get(entity);
			final var frame = animation.getAnimation().getCurrentFrame();
			g2d.setClip(
				(int) (position.getX() - cameraSystem.getX()),
				(int) (position.getY() - cameraSystem.getY()),
				frame.width(),
				frame.height()
			);

			animation.paint(g2d, facings.get(entity));

			/*
			 * Because the collision and hit boxes use real coordinates (i.e.
			 * they're affected by the entity's position), we need to apply
			 * a translation to ensure they're drawn in the correct position.
			 *
			 * We also need to translate from the top-right coordinates, when
			 * the facing is flipped.
			 */
			g2d.translate(-cameraSystem.getX(), -cameraSystem.getY());
			hitboxes.get(entity).paint(g2d);
			collisionBoxes.get(entity).paint(g2d);
			g2d.translate(cameraSystem.getX(), cameraSystem.getY());
		});
	}
}
