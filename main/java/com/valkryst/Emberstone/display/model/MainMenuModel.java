package com.valkryst.Emberstone.display.model;

import com.valkryst.Emberstone.display.controller.MainMenuController;
import com.valkryst.Emberstone.display.view.MainMenuView;

public class MainMenuModel extends Model<MainMenuController, MainMenuView> {
	@Override
	protected MainMenuController createController() {
		return new MainMenuController(this);
	}

	@Override
	protected MainMenuView createView(final MainMenuController controller) {
		return new MainMenuView(controller);
	}
}
