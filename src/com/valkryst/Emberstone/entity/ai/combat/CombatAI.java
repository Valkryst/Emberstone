package com.valkryst.Emberstone.entity.ai.combat;

import com.valkryst.Emberstone.entity.Creature;
import com.valkryst.Emberstone.map.Map;
import lombok.NonNull;

public interface CombatAI {
    /**
     * Decides what a creature should do in it's current combat situation and acts upon the decision.
     *
     * @param map
     *        The map.
     *
     * @param self
     *        The attacking entity.
     */
    void decide(final @NonNull Map map, final @NonNull Creature self);
}
