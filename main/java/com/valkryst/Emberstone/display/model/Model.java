package com.valkryst.Emberstone.display.model;

import com.valkryst.Emberstone.display.controller.Controller;
import com.valkryst.Emberstone.display.view.View;

public abstract class Model<C extends Controller, V extends View> {
	/**
	 * Constructs a controller for the view.
	 *
	 * @return A controller.
	 */
	protected abstract C createController();

	/**
	 * Constructs a view of this model.
	 *
	 * @param controller A controller for the view.
	 * @return A view.
	 */
	protected abstract V createView(final C controller);

	/**
	 * Constructs a view of this model.
	 *
	 * @return A view.
	 */
	public V createView() {
		return createView(createController());
	}
}
