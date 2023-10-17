package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.mvc.view.View;
import lombok.Getter;

import java.awt.*;

public class Controller {
    /** The view. */
    @Getter public final View view;

    /**
     * Constructs a new Controller.
     *
     * @param view
     *          The view.
     */
    public Controller(final View view) {
        this.view = view;
    }
}
