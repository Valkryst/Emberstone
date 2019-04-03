package com.valkryst.Emberstone.mvc.view.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.mvc.component.EButton;
import com.valkryst.Emberstone.mvc.controller.menu.MainController;
import com.valkryst.Emberstone.mvc.view.View;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

public class DeathView extends View {
    public DeathView() {
        // Set up the background and logo.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Death.png", "gui/Death.json");
            final SpriteSheet sheet = atlas.getSpriteSheet("Death");

            super.setBackgroundImage(sheet.getSprite("Background").getBufferedImage());
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        final JButton mainMenuButton = new EButton("Main Menu", 48);


        final Game game = Game.getInstance();
        final int x = (int) ((game.getViewWidth() / 2.0) - (mainMenuButton.getWidth() / 2.0));
        int y = (int) (game.getViewHeight() / 2.0) + 300;

        mainMenuButton.setLocation(x, y);
        mainMenuButton.addActionListener(e -> {
            GameAudio.getInstance().stopAllActiveMusic();
            game.setController(new MainController());
        });
        this.add(mainMenuButton);
    }
}
