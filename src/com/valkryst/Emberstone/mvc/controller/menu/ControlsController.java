package com.valkryst.Emberstone.mvc.controller.menu;

import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.menu.ControlsView;

public class ControlsController extends Controller {
    /**
     * Constructs a new ControlsController.
     *
     * @param previousController
     *          The controller whose view was displayed before this view.
     */
    public ControlsController(final Controller previousController) {
        super(new ControlsView(previousController));
    }
}
