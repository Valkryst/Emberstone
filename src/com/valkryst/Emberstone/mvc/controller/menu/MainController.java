package com.valkryst.Emberstone.mvc.controller.menu;

import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.Music;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.menu.MainView;

public class MainController extends Controller {
    /** Constructs a new MainController. */
    public MainController() {
        super(new MainView());
        GameAudio.getInstance().playMusic(Music.MAIN_MENU);
    }
}
