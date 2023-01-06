package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.model.VideoModel;
import lombok.NonNull;

public class VideoController extends Controller<VideoModel> {
	public VideoController(final @NonNull VideoModel model) {
		super(model);
	}

	public void skip() {
		super.setContentPane(model.getModelOfNextView());
	}
}
