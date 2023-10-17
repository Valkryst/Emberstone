package com.valkryst.Emberstone.mvc.controller.menu;

import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.menu.SettingsView;

public class SettingsController extends Controller {
    /** Constructs a new SettingsController. */
    public SettingsController() {
        super(new SettingsView());
    }
}
