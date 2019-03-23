package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.entity.AnimationState;
import com.valkryst.Emberstone.entity.Entity;
import com.valkryst.Emberstone.entity.SpriteType;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.Music;
import com.valkryst.Emberstone.mvc.view.LevelView;
import com.valkryst.Emberstone.statistic.StatisticType;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class LevelController extends Controller {
    /** The event listeners. */
    private final List<EventListener> eventListeners = new ArrayList<>();

    @Getter private Map map;

    /**
     * Constructs a new LevelController.
     *
     * @param playerSpriteType
     *          The player's sprite type.
     */
    public LevelController(final SpriteType playerSpriteType) {
        super(new LevelView());
        map = new Map(playerSpriteType);

        final LevelController levelController = this;

        // Add player controls
        final Timer autoAttackTimer = new Timer(500, actionEvent -> {
            final Entity player = map.getPlayer();

            if (player.getAnimationState() == AnimationState.IDLE) {
                player.setAnimation(AnimationState.ATTACKING_IDLE);
            }

            if (player.getAnimationState() == AnimationState.RUNNING) {
                player.setAnimation(AnimationState.ATTACKING_RUNNING);
            }
        });
        autoAttackTimer.setInitialDelay(0);

        eventListeners.add(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {}

            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    if (map.getPlayer().getAnimationState() == AnimationState.DEFENDING) {
                        map.getPlayer().setAnimation(AnimationState.IDLE);
                    }

                    autoAttackTimer.start();
                } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    map.getPlayer().setAnimation(AnimationState.DEFENDING);
                }
            }

            @Override
            public void mouseReleased(final MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    autoAttackTimer.stop();
                } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    if (map.getPlayer().getAnimationState() == AnimationState.DEFENDING) {
                        map.getPlayer().setAnimation(AnimationState.IDLE);
                    }
                }
            }

            @Override
            public void mouseEntered(final MouseEvent mouseEvent) {}

            @Override
            public void mouseExited(final MouseEvent mouseEvent) {}
        });

        eventListeners.add(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent keyEvent) {}

            @Override
            public void keyPressed(final KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A: {
                        final Entity player = map.getPlayer();
                        player.setHorizontalSpeed(-player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D: {
                        final Entity player = map.getPlayer();
                        player.setHorizontalSpeed(player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W: {
                        final Entity player = map.getPlayer();
                        player.setVerticalSpeed(-player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S: {
                        final Entity player = map.getPlayer();
                        player.setVerticalSpeed(player.getStat(StatisticType.SPEED).getValue());
                        break;
                    }
                    case KeyEvent.VK_I: {
                        final InventoryController inventoryController = new InventoryController(levelController, map.getPlayer().getInventory());
                        Game.getInstance().setController(inventoryController);
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(final KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D: {
                        map.getPlayer().setHorizontalSpeed(0);
                        break;
                    }
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S: {
                        map.getPlayer().setVerticalSpeed(0);
                        break;
                    }
                }
            }
        });
    }

    //@Override
    public void update(final double deltaTime) {
        //super.update(deltaTime);
        map.update(deltaTime);
    }

    //@Override
    public void draw(final Graphics2D gc) {
        map.draw(gc);
        //super.draw(gc);
    }

    @Override
    public void addToCanvas() {
        super.addToCanvas();

        GameAudio.getInstance().stopMusic(Music.MAIN_MENU);
        GameAudio.getInstance().playMusic(Music.GAME);

        final Game game = Game.getInstance();

        for (final EventListener eventListener : eventListeners) {
            game.addEventListener(eventListener);
        }
    }

    @Override
    public void removeFromCanvas() {
        super.removeFromCanvas();

        final Game game = Game.getInstance();

        for (final EventListener eventListener : eventListeners) {
            game.removeEventListener(eventListener);
        }
    }
}
