package com.valkryst.Emberstone.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.valkryst.Emberstone.component.AnimationComponent;
import com.valkryst.Emberstone.component.CollisionBoxComponent;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.display.model.SettingsModel;
import lombok.NonNull;

import java.awt.geom.Rectangle2D;

public class CameraSystem extends EntitySystem {
	private final Rectangle2D boundingBox = new Rectangle2D.Float();

	private Entity followedEntity;

	@Override
	public void update(final float deltaTime) {
		if (followedEntity == null) {
			return;
		}

		final var settings = SettingsModel.getInstance();
		final int viewWidth = settings.getViewWidth();
		final int viewHeight = settings.getViewHeight();

		final var animationComponent = followedEntity.getComponent(AnimationComponent.class);
		final var entitySprite = animationComponent.getAnimation().getCurrentFrame();
		final int spriteWidth = entitySprite.width();
		final int spriteHeight = entitySprite.height();

		final var positionComponent = followedEntity.getComponent(PositionComponent.class);

		final double targetX = positionComponent.getX() - (viewWidth / 2d) + (spriteWidth / 2d);
		final double targetY = positionComponent.getY() - (viewHeight / 2d) + (spriteHeight / 2d);

		double newX = boundingBox.getX();
		double newY = boundingBox.getY();

		newX += (targetX - boundingBox.getX()) * 0.1 * deltaTime;
		newY += (targetY - boundingBox.getY()) * 0.1 * deltaTime;

		/*
		 * todo
		 *  Take into account the map width and height. See the following link.
		 *  https://github.com/Valkryst/Emberstone/blob/master/src/com/valkryst/Emberstone/Camera.java
		 */
		newX = Math.min(viewWidth, Math.max(0, newX));
		newY = Math.min(viewHeight, Math.max(0, newY));

		boundingBox.setRect(newX, newY, viewWidth, viewHeight);
	}

	public void follow(final @NonNull Entity entity) {
		if (entity.getComponent(AnimationComponent.class) == null) {
			throw new IllegalArgumentException("The specified entity does not have an AnimationComponent.");
		}

		if (entity.getComponent(PositionComponent.class) == null) {
			throw new IllegalArgumentException("The specified entity does not have a PositionComponent.");
		}

		followedEntity = entity;
	}

	public boolean isVisible(final CollisionBoxComponent collisionBoxComponent) {
		if (collisionBoxComponent == null) {
			return false;
		}

		return this.boundingBox.intersects(collisionBoxComponent.getCollisionBox());
	}

	/**
	 * Retrieves the x-axis position of the top-left corner of the camera's view.
	 *
	 * @return The x-axis position of the top-left corner of the camera's view.
	 */
	public double getX() {
		return boundingBox.getX();
	}

	/**
	 * Retrieves the y-axis position of the top-left corner of the camera's view.
	 *
	 * @return The y-axis position of the top-left corner of the camera's view.
	 */
	public double getY() {
		return boundingBox.getY();
	}
}
