package com.valkryst.Emberstone.action;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.entity.*;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.map.Region;
import com.valkryst.Emberstone.map.Tile;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.SoundEffect;
import com.valkryst.Emberstone.mvc.controller.menu.DeathController;
import com.valkryst.Emberstone.mvc.controller.menu.MainController;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteAtlas;
import javafx.application.Platform;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DeathAction extends Action {
    @Override
    public void perform(final Map map, final Entity self) {
        super.perform(map, self);

        if (map == null || self == null) {
            return;
        }

        self.setAnimation(AnimationState.DYING);

        if (self instanceof Player == false && ThreadLocalRandom.current().nextInt(4) == 0) {
            final int x = self.getPosition().x;
            final int y = self.getPosition().y - 1;
            map.addEntity(Chest.createChest(new Point(x, y), map.getPlayer().getStat(StatisticType.LEVEL).getValue()));
            GameAudio.getInstance().playSoundEffect(SoundEffect.CHEST_APPEAR);
        }

        if (self instanceof Player) {
            Game.getInstance().setController(new DeathController());
        }

        if (self instanceof Creature) {
            map.setShardsGathered(map.getShardsGathered() + ThreadLocalRandom.current().nextInt(1, 3));

            if (map.getShardsGathered() >= map.getShardsRequired()) {
                map.setShardsGathered(map.getShardsRequired());
            }

            if (map.isPortalSpawned() == false && map.getShardsGathered() >= map.getShardsRequired()) {
                map.setPortalSpawned(true);

                final int x = self.getPosition().x;
                final int y = self.getPosition().y - 1;
                try {
                    final SpriteAtlas atlas = SpriteType.PORTAL.getSpriteAtlas();
                    final Portal portal = new Portal(new Point(x, y), atlas.getSpriteSheet("Entity"));
                    map.addEntity(portal);
                    GameAudio.getInstance().playSoundEffect(SoundEffect.PORTAL_SPAWN);
                } catch (final ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        final Timer entityRemovalTimer = new Timer();
        entityRemovalTimer.schedule(new TimerTask() {
            @Override
            public void run() {
            map.removeEntity(self);
            this.cancel();
            }
        }, 300_000);
    }
}
