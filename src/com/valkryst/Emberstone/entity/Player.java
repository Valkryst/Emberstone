package com.valkryst.Emberstone.entity;

import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;

import java.awt.*;

public class Player extends Entity {
    /** The player's inventory. */
    @Getter private final Inventory inventory = new Inventory();

    /**
     * Constructs a new Player.
     *
     * @param position
     *          Location within the levels.
     *
     * @param spriteSheet
     *          The sprite sheet.
     */
    public Player(final Point position, final SpriteSheet spriteSheet) {
        super(position, spriteSheet);

        final BoundStatistic experience = new BoundStatistic(StatisticType.EXPERIENCE, 0, 0, 100);
        final Statistic speed = new Statistic(StatisticType.SPEED, 6);

        super.addStat(experience);
        super.addStat(speed);
    }
}
