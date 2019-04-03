package com.valkryst.Emberstone;

import com.valkryst.Emberstone.mvc.controller.menu.MainController;
import javafx.application.Platform;

public class Driver {
    public static void main(final String[] args) {
        final Settings settings = Settings.getInstance();
        System.setProperty("sun.java2d.uiScale", String.valueOf(settings.getUiScale()));
        System.setProperty("sun.java2d.d3d", String.valueOf(settings.isDirect3DEnabled()));
        System.setProperty("sun.java2d.opengl", String.valueOf(settings.isOpenGLEnabled()));

        /*
         * Prevents the JavaFX runtime from shutting down when all of the JFXPanels are closed.
         *
         * If we allow it to shut down, then the GameAudio class won't be able to make use of the Media
         * and MediaPlayer JavaFX classes. The program will just throw NPEs when you try to create new
         * MediaPlayers.
         */
        Platform.setImplicitExit(false);
        Platform.startup(() -> {});

        final Game game = Game.getInstance();
        game.setController(new MainController());
    }
}
