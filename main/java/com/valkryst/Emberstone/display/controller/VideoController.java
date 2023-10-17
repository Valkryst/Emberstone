package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.Display;
import com.valkryst.Emberstone.display.model.VideoModel;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

public class VideoController extends Controller<VideoModel> {
	public VideoController(final @NonNull VideoModel model) {
		super(model);
	}

	public void skip() {
		try {
			Display.getInstance().setContentPane(model.getModelOfNextView().getDeclaredConstructor().newInstance().createView());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
