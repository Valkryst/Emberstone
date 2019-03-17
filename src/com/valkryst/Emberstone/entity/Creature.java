package com.valkryst.Emberstone.entity;

import com.valkryst.Emberstone.entity.ai.combat.AggressiveCombatAI;
import com.valkryst.Emberstone.entity.ai.combat.CombatAI;
import com.valkryst.Emberstone.entity.ai.movement.MovementAI;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class Creature extends Entity {
    @Getter @Setter private MovementAI movementAI = new MovementAI();
    @Getter @Setter private CombatAI combatAI = new AggressiveCombatAI();

    /**
     * Constructs a new Creature.
     *
     * @param position
     *          Location within the levels.
     *
     * @param spriteSheet
     *          The sprite sheet.
     */
    public Creature(final Point position, final SpriteSheet spriteSheet) {
        super(position, spriteSheet);
    }

    @Override
    public void update(final Map map, final double deltaTime) {
        super.update(map, deltaTime);
        combatAI.decide(map, this);
        movementAI.move(this);

        final Statistic speed = new Statistic(StatisticType.SPEED, 4);
        super.addStat(speed);
    }
}
