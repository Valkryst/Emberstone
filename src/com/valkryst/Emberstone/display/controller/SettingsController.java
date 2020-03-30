package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.model.SettingsModel;

import java.io.IOException;

public class SettingsController extends DefaultController {
    public void changeAlphaInterpolation(final String newValue) {
        super.setModelProperty("AlphaInterpolation", newValue);
    }

    public void changeAntialiasing(final String newValue) {
        super.setModelProperty("Antialiasing", newValue);
    }

    public void changeColorRendering(final String newValue) {
        super.setModelProperty("ColorRendering", newValue);
    }

    public void changeDithering(final String newValue) {
        super.setModelProperty("Dithering", newValue);
    }

    public void changeInterpolation(final String newValue) {
        super.setModelProperty("Interpolation", newValue);
    }

    public void changeRendering(final String newValue) {
        super.setModelProperty("Rendering", newValue);
    }

    public void changeRenderer(final String newValue) {
        super.setModelProperty("Renderer", newValue);
    }

    public void saveSettings() {
        try {
            SettingsModel.getInstance().save();
        } catch (IOException e) {
            // todo Something better than just printing the trace.
            e.printStackTrace();
        }
    }
}
