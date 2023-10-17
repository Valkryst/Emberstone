package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.model.MainMenuModel;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

public class MainMenuController extends Controller<MainMenuModel> {
	public MainMenuController(final @NonNull MainMenuModel model) {
		super(model);
	}
}
