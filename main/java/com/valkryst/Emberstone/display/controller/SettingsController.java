package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.model.SettingsModel;
import lombok.NonNull;

import java.io.IOException;

public class SettingsController extends Controller<SettingsModel> {
	public SettingsController(final @NonNull SettingsModel model) {
		super(model);
	}

	public String changeAlphaInterpolation(final String newValue) {
		return super.model.setAlphaInterpolation(newValue);
    }

    public String changeAntialiasing(final String newValue) {
		return super.model.setAntialiasing(newValue);
    }

    public String changeColorRendering(final String newValue) {
        return super.model.setColorRendering(newValue);
    }

    public String changeDithering(final String newValue) {
        return super.model.setDithering(newValue);
    }

    public String changeInterpolation(final String newValue) {
        return super.model.setInterpolation(newValue);
    }

    public String changeRendering(final String newValue) {
        return super.model.setRendering(newValue);
    }

	public boolean changeWindowed(final boolean newValue) {
		return super.model.setWindowed(newValue);
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
