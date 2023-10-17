package com.valkryst.Emberstone.display.model;

import com.valkryst.Emberstone.Video;
import com.valkryst.Emberstone.display.controller.VideoController;
import com.valkryst.Emberstone.display.view.VideoView;
import com.valkryst.VMVC.model.Model;
import lombok.Getter;
import lombok.NonNull;

public class VideoModel extends Model<VideoController, VideoView> {
	private final Video video;
	@Getter private final Class<? extends Model> modelOfNextView;

	public VideoModel(final @NonNull Video video, final @NonNull Class<? extends Model> modelOfNextView) {
		this.video = video;
		this.modelOfNextView = modelOfNextView;
	}

	@Override
	protected VideoController createController() {
		return new VideoController(this);
	}

	@Override
	protected VideoView createView(final VideoController controller) {
		return new VideoView(controller, video);
	}
}
