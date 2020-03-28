package com.valkryst.Emberstone.display.v1.controller;

import com.valkryst.Emberstone.display.v1.view.View;
import lombok.Getter;
import lombok.NonNull;

public abstract class Controller<V extends View> {
    @Getter private final V view;

    public Controller(final @NonNull V view) {
        this.view = view;
    }
}
