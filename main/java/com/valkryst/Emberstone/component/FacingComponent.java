package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString
public class FacingComponent implements Component {
	private Direction direction;

	/** Constructs a new {@code FacingComponent}. */
	public FacingComponent() {
		this(Direction.RIGHT);
	}

	/**
	 * Constructs a new {@code FacingComponent}.
	 *
	 * @param direction The direction.
	 */
	public FacingComponent(final @NonNull FacingComponent.Direction direction) {
		this.direction = direction;
	}

	public enum Direction {
		LEFT,
		RIGHT
	}
}
