package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.mvc.view.SettingsView;

public class SettingsController extends Controller {
    /** Constructs a new SettingsController. */
    public SettingsController() {
        super(new SettingsView());
    }

    @Override
    public void addToCanvas() {
        super.addToCanvas();
    }

    @Override
    public void removeFromCanvas() {
        super.removeFromCanvas();
    }
}
