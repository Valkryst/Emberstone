package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.display.view.GameView;
import com.valkryst.Emberstone.display.view.LoadGameView;
import com.valkryst.Emberstone.display.view.MainMenuView;
import com.valkryst.Emberstone.display.view.View;

public class DefaultController extends Controller {
    public void displayMainMenuView(final View view) {
        super.removeView(view);
        this.addView(new MainMenuView(this));

        System.out.println("Main Menu");
    }

    public void displayNewGameView(View view) {
        super.removeView(view);

        // The renderer MUST be set after the view is added to the Frame, or
        // else a whole whack of errors will be thrown.
        final var gameView = new GameView();
        this.addView(gameView);
        gameView.setRenderer(Settings.getInstance().getRenderer());
        gameView.start();

        System.out.println("New Game");
    }

    public void displayLoadGameView(final View view) {
        super.removeView(view);
        this.addView(new LoadGameView(this));

        System.out.println("Load Game");
    }

    public void displayCreditsView(final View view) {
        //super.removeView(view);

        System.out.println("Credits");
    }

    public void exitGame() {
        System.out.println("Exit");
        System.exit(0);
    }
}
