package com.valkryst.Emberstone.action;

import com.valkryst.Emberstone.entity.AnimationState;
import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.map.Map;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeathAction extends Action {
    @Override
    public void perform(final Map map, final Entity self) {
        super.perform(map, self);

        if (map == null || self == null) {
            return;
        }

        self.setAnimation(AnimationState.DYING);

        final ScheduledExecutorService entityRemovalService = Executors.newSingleThreadScheduledExecutor();
        entityRemovalService.schedule(() -> {
            map.removeEntity(self);
            entityRemovalService.shutdown();
        }, 30, TimeUnit.SECONDS);
    }
}
