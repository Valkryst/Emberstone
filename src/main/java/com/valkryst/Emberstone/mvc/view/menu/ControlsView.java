package com.valkryst.Emberstone.mvc.view.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.mvc.component.EButton;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.View;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

public class ControlsView extends View {
    /**
     * Constructs a new ControlsView.
     *
     * @param previousController
     *          The controller whose view was displayed before this view.
     */
    public ControlsView(final Controller previousController) {
        // Set up the background.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Controls.png", "gui/Controls.json");
            final SpriteSheet sheet = atlas.getSpriteSheet("Controls");

            // Add Background
            super.setBackgroundImage(sheet.getSprite("Background").getBufferedImage());
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        final JButton back = new EButton("Back", 48);
        back.setLocation(725, 900);
        back.addActionListener(e -> Game.getInstance().setController(previousController));
        this.add(back);
    }
}
