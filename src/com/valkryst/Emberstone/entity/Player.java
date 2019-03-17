package com.valkryst.Emberstone.entity;

import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.SoundEffect;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteSheet;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends Entity {
    private int killsToLevel = 1;

    /**
     * Constructs a new Player.
     *
     * @param position
     *          Location within the level.
     *
     * @param spriteSheet
     *          The sprite sheet.
     */
    public Player(final Point position, final SpriteSheet spriteSheet) {
        super(position, spriteSheet);

        final BoundStatistic health = new BoundStatistic(StatisticType.HEALTH, 0, 100);
        final BoundStatistic level = new BoundStatistic(StatisticType.LEVEL, 1, 1, 60);
        final Statistic speed = new Statistic(StatisticType.SPEED, 6);

        super.addStat(health);
        super.addStat(level);
        super.addStat(speed);

        final Player player = this;
        final Timer healthRegenTimer = new Timer();
        healthRegenTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final BoundStatistic health = (BoundStatistic) player.getStat(StatisticType.HEALTH);

                if (health.getValue() < health.getMaxValue()) {
                    health.setValue(health.getValue() + 10);
                }
            }
        }, 0, 20_000);
    }

    public void levelUp() {
        killsToLevel--;

        if (killsToLevel == 0) {
            final BoundStatistic level = (BoundStatistic) super.getStat(StatisticType.LEVEL);

            if (level.getValue() < level.getMaxValue()) {
                GameAudio.getInstance().playSoundEffect(SoundEffect.LEVEL_UP);

                level.setValue(level.getValue() + 1);
                killsToLevel = level.getValue();

                final BoundStatistic health = (BoundStatistic) super.getStat(StatisticType.HEALTH);
                health.setValue(health.getMaxValue());
            }
        }
    }
}
