package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.view.VideoView;

public class VideoController extends Controller {
    /**
     * Constructs a new VideoController.
     *
     * @param video
     *          The video to play.
     *
     * @param controller
     *          The controller whose view is to be displayed after the video finishes.
     */
    public VideoController(final Video video, final Controller controller) {
        super(new VideoView(video, controller));
    }
}
