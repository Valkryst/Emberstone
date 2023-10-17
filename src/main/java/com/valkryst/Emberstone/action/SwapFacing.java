package com.valkryst.Emberstone.action;

import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.map.Map;

public class SwapFacing extends Action {
    @Override
    public void perform(final Map map, final Entity self) {
        super.perform(map, self);

        if (map == null || self == null) {
            return;
        }

        self.swapFacing();
    }
}
