package com.valkryst.Emberstone;

import com.valkryst.Emberstone.display.Display;
import com.valkryst.Emberstone.display.model.MainMenuModel;
import javafx.application.Platform;

import javax.swing.*;

public class Driver {
    public static void main(final String[] args) {
		/*
		 * This ensures that the JavaFX thread is kept alive, even when there
		 * are no more JavaFX scenes.
		 */
		Platform.setImplicitExit(false);

		SwingUtilities.invokeLater(() -> {
			Display.getInstance().setContentPane(new MainMenuModel().createView());
		});
    }
}
