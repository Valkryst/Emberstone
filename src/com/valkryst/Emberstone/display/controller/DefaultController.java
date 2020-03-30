package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.view.*;

public class DefaultController extends Controller {
    public void displayMainMenuView(final View previousView) {
        super.removeView(previousView);

        final var controller = new DefaultController();
        final var view = new MainMenuView(controller);
        controller.addView(view);
    }

    public void displayNewGameView(final View previousView) {
        super.removeView(previousView);

        final var controller = new GameController();
        final var view = new GameView(controller);
        controller.addView(view);
    }

    public void displayLoadGameView(final View previousView) {
        super.removeView(previousView);

        final var controller = new DefaultController();
        final var view = new LoadGameView(controller);
        controller.addView(view);
    }

    public void displaySettingsView(final View previousView) {
        super.removeView(previousView);

        final var controller = new SettingsController();
        final var view = new SettingsView(controller);
        controller.addView(view);
    }

    public void displayCreditsView() {
        //super.removeView(view);

        System.out.println("Credits");
    }

    public void exitGame() {
        System.out.println("Exit");
        System.exit(0);
    }
}
