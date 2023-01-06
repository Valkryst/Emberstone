package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.display.controller.Controller;
import lombok.NonNull;

import javax.swing.*;

public abstract class View<C extends Controller<?>> extends JPanel {
	final C controller;

	public View(final @NonNull C controller) {
		this.controller = controller;
	}
}
