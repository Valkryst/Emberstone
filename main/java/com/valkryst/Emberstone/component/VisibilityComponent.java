package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VisibilityComponent implements Component {
	private boolean isVisible = false;

	/**
	 * Constructs a new {@code VisibilityComponent}.
	 *
	 * @param isVisible The visibility.
	 */
	public VisibilityComponent(final boolean isVisible) {
		this.isVisible = isVisible;
	}
}
