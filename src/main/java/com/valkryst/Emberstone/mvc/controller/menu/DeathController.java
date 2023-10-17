package com.valkryst.Emberstone.mvc.controller.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.Music;
import com.valkryst.Emberstone.media.SoundEffect;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.menu.DeathView;
import com.valkryst.Emberstone.mvc.view.menu.SettingsView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_I;

public class DeathController extends Controller {
    /** Constructs a new DeathController. */
    public DeathController() {
        super(new DeathView());
        super.getView().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {}

            @Override
            public void keyPressed(final KeyEvent e) {
                switch (e.getKeyCode()) {
                    case VK_ESCAPE: {
                        Game.getInstance().setController(new MainController());
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {}
        });

        GameAudio.getInstance().stopAllActiveMusic();
        GameAudio.getInstance().playMusic(Music.DEATH);
        GameAudio.getInstance().playSoundEffect(SoundEffect.EVIL_LAUGH);
    }
}
