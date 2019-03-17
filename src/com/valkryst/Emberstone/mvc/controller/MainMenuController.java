package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.Music;
import com.valkryst.Emberstone.mvc.view.MainMenuView;

public class MainMenuController extends Controller {
    /** Constructs a new MainMenuController. */
    public MainMenuController() {
        super(new MainMenuView());
    }

    @Override
    public void addToCanvas() {
        super.addToCanvas();
        GameAudio.getInstance().playMusic(Music.MAIN_MENU);
    }

    @Override
    public void removeFromCanvas() {
        super.removeFromCanvas();
    }
}
