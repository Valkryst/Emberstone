package com.valkryst.Emberstone.display.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;

public abstract class View extends Panel {
    public abstract void modelPropertyChange(final PropertyChangeEvent event);
}
