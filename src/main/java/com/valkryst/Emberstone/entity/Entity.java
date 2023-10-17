package com.valkryst.Emberstone.entity;

import com.valkryst.Emberstone.Camera;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.action.Action;
import com.valkryst.Emberstone.action.AttackAction;
import com.valkryst.Emberstone.action.MoveAction;
import com.valkryst.Emberstone.item.Equipment;
import com.valkryst.Emberstone.item.EquipmentSlot;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.item.Rarity;
import com.valkryst.Emberstone.item.generator.EquipmentGenerator;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.SoundEffect;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.AnimatedSprite;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Entity implements Comparable<Entity> {
    /** The inventory. */
    @Getter private final Inventory inventory = new Inventory();

    /** Location within the map. */
    @Getter private final Point position;
    /** Previous location within the map. */
    @Getter private final Point previousPosition = new Point(0, 0);

    /** The stats. */
    private final HashMap<StatisticType, Statistic> stats = new HashMap<>();

    /** The actions to perform. */
    private final Queue<Action> actions = new ConcurrentLinkedQueue<>();
    /**
     * Whether the entity's attack has been processed.
     *
     * It's possible for the animation frame, that contains a weapon bounding box, to be visible across
     * multiple updates, so we need to make sure that the attack is only registered once. If we fail to do this,
     * then the attack will occur for every update that the animation frame is visible.
     */
    private boolean hasAttacked = false;

    /** The horizontal speed. */
    @Getter @Setter private int horizontalSpeed = 0;
    /** The vertical speed. */
    @Getter @Setter private int verticalSpeed = 0;

    /** The sprite sheet. */
    private final SpriteSheet spriteSheet;
    /** The active sprite animation. */
    @Getter protected AnimatedSprite spriteAnimation;
    /** The animation state. */
    @Getter private AnimationState animationState;
    /** The amount of time the current frame has been displayed. */
    protected double frameTime = 0;

    /**
     * Constructs a new Entity.
     *
     * @param position
     *          Location within the levels.
     *
     * @param spriteSheet
     *          The sprite sheet.
     */
    public Entity(final Point position, final SpriteSheet spriteSheet) {
        this.position = position;
        this.spriteSheet = spriteSheet;
        setAnimation(AnimationState.IDLE);

        final BoundStatistic health = new BoundStatistic(StatisticType.HEALTH, 0, 100);
        final BoundStatistic level = new BoundStatistic(StatisticType.LEVEL, 1, 1, 60);
        addStat(health);
        addStat(level);
    }

    @Override
    public int compareTo(final Entity otherEntity) {
        final Rectangle entityFeet = getBoundingBox("Feet");
        final Rectangle otherEntityFeet = otherEntity.getBoundingBox("Feet");

        if (entityFeet != null && otherEntityFeet != null) {
            return Integer.compare(entityFeet.y, otherEntityFeet.y);
        } else {
            return Integer.compare(position.y, otherEntity.getPosition().y);
        }
    }

    /**
     * Updates the entity's state.
     *
     * @param map
     *          The map.
     *
     * @param deltaTime
     *          The delta time.
     */
    public void update(final Map map, final double deltaTime) {
        // Handle Animation Increment
        frameTime += deltaTime;

        if (frameTime >= 2) {
            if (animationState == AnimationState.DYING) {
                if (spriteAnimation.isOnLastFrame() == false) {
                    spriteAnimation.toNextFrame();
                }
            } else {
                spriteAnimation.toNextFrame();
            }


            frameTime -= 2;
        }

        // Handle Animation Switching
        if (horizontalSpeed == 0 && verticalSpeed == 0) {
            switch (animationState) {
                // Animation: RUNNING -> IDLE
                case RUNNING: {
                    setAnimation(AnimationState.IDLE);
                    break;
                }
                // Animation: ATTACKING_IDLE -> IDLE
                case ATTACKING_IDLE: {
                    if (spriteAnimation.isOnLastFrame()) {
                        setAnimation(AnimationState.IDLE);
                    }
                    break;
                }
                // Animation: ATTACKING_RUNNING -> IDLE
                case ATTACKING_RUNNING: {
                    if (spriteAnimation.isOnLastFrame()) {
                        setAnimation(AnimationState.IDLE);
                    }
                    break;
                }
                // Animation: DYING -> N/A
                case DYING: {
                    break;
                }
            }
        } else {
            switch (animationState) {
                // Animation: IDLE -> RUNNING
                case IDLE: {
                    setAnimation(AnimationState.RUNNING);
                    break;
                }

                // Animation: ATTACKING_IDLE -> RUNNING
                case ATTACKING_IDLE: {
                    if (spriteAnimation.isOnLastFrame()) {
                        setAnimation(AnimationState.RUNNING);
                    }
                    break;
                }
                // Animation: ATTACKING_RUNNING -> RUNNING
                case ATTACKING_RUNNING: {
                    if (spriteAnimation.isOnLastFrame()) {
                        setAnimation(AnimationState.RUNNING);
                    }
                    break;
                }
                // Animation: DYING -> N/A
                case DYING: {
                    break;
                }
            }
        }

        // Check weapon collision against all other entities.
        if (map.getCamera().isInView(this) && hasAttacked == false) {
            final Rectangle weaponBoundingBox = getBoundingBox("Weapon");

            if (weaponBoundingBox != null) {
                hasAttacked = true;

                GameAudio.getInstance().playSoundEffect(SoundEffect.getMissSound());

                final Iterator<Entity> it = map.getEntitiesIterator();
                while (it.hasNext()) {
                    final Entity target = it.next();

                    // An entity can't attack itself.
                    if (this == target) {
                        continue;
                    }

                    // A creature cannot attack another creature.
                    if (this instanceof Creature && target instanceof Creature) {
                        continue;
                    }

                    // Nothing can attack a chest.
                    if (target instanceof Chest) {
                        continue;
                    }

                    // Nothing can attack a portal.
                    if (target instanceof Portal) {
                        continue;
                    }

                    // A dying entity cannot be attacked.
                    if (target.getAnimationState() == AnimationState.DYING) {
                        continue;
                    }

                    // Entities outside of the camera's bounds cannot be attacked.
                    if (map.getCamera().isInView(target) == false) {
                        continue;
                    }

                    final Rectangle bodyBoundingBox = target.getBoundingBox("Body");
                    if (bodyBoundingBox == null) {
                        continue;
                    }

                    if (weaponBoundingBox.intersects(bodyBoundingBox)) {
                        addAction(new AttackAction(target));

                        GameAudio.getInstance().playSoundEffect(SoundEffect.getAttackSound());
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                GameAudio.getInstance().playSoundEffect(SoundEffect.getImpactSound());
                            }
                        }, 300);
                    }
                }
            }
        }

        // Prevent movement during idle attacks.
        if (animationState == AnimationState.ATTACKING_IDLE) {
            return;
        }

        // Prevent movement during defending animation.
        if (animationState == AnimationState.DEFENDING) {
            return;
        }

        // Prevent movement during dying animation.
        if (animationState == AnimationState.DYING) {
            return;
        }

        // Add move action.
        if (horizontalSpeed != 0 || verticalSpeed != 0) {
            addAction(new MoveAction(horizontalSpeed * deltaTime, verticalSpeed * deltaTime));
        }

        // Perform queued actions.
        if (actions.size() > 0) {
            for (final Action action : actions) {
                action.perform(map, this);
            }
            actions.clear();
        }

        // Handle horizontal/vertical flips. Ensure this is done after the MoveAction is performed, so that the
        // flipped sprite's foot bounding box doesn't get stuck in a wall.
        if (horizontalSpeed < 0) {
            spriteAnimation.setFlippedHorizontally(true);
        } else if (horizontalSpeed > 0) {
            spriteAnimation.setFlippedHorizontally(false);
        }
    }

    /**
     * Draws the entity on a graphics context.
     *
     * @param gc
     *          The graphics context.
     *
     * @param camera
     *          The game's camera.
     */
    public void draw(final Graphics2D gc, final Camera camera) {
        if (spriteAnimation != null) {
            // Draw SpriteType
            int x = position.x - camera.getX();
            int y = position.y - camera.getY();

            spriteAnimation.draw(gc, x, y);

            // Draw Health Bar
            final BoundStatistic health = (BoundStatistic) getStat(StatisticType.HEALTH);
            if (health != null && health.getValue() < health.getMaxValue() && animationState != AnimationState.DYING) {
                // todo This code can be very slightly optimized by calculating the actual positions, widths, and
                // todo heights of each section of the bar (border, green, and red sections).
                final int halfSpriteWidth = spriteAnimation.getCurrentSprite().getWidth() / 2;
                final double healthPercent = health.getValue() / (float) health.getMaxValue();

                x += (halfSpriteWidth / 2);
                y -= (spriteAnimation.getCurrentSprite().getHeight() / 8);

                // Draw Red Rect
                gc.setColor(Color.RED);
                gc.fillRect(x, y, halfSpriteWidth, 12);

                // Draw Green Rect
                gc.setColor(Color.GREEN);
                gc.fillRect(x, y, (int) (halfSpriteWidth * healthPercent), 12);

                // Draw Border
                gc.setColor(Color.BLACK);
                gc.setStroke(new BasicStroke(3));
                gc.drawRect(x, y, halfSpriteWidth, 12);
                gc.setStroke(new BasicStroke(1));
            }

            // Draw Collision Boxes
            if (Settings.getInstance().areDebugBoundingBoxesOn()) {
                x = position.x - camera.getX();
                y = position.y - camera.getY();

                // Display bounding boxes.
                final Sprite sprite = spriteAnimation.getCurrentSprite();

                gc.setColor(Color.GREEN);
                sprite.drawBoundingBox(gc, "Body", x, y);

                gc.setColor(Color.MAGENTA);
                sprite.drawBoundingBox(gc, "Feet", x, y);

                gc.setColor(Color.YELLOW);
                sprite.drawBoundingBox(gc, "Weapon", x, y);
            }
        }
    }

    /**
     * Adds an action to the entity.
     *
     * @param action
     *        The action.
     */
    public void addAction(final Action action) {
        if (action == null) {
            return;
        }

        actions.add(action);
    }

    /**
     * Adds a stat to the entity.
     *
     * @param stat
     *          The stat.
     */
    public void addStat(final Statistic stat) {
        if (stat == null) {
            return;
        }

        stats.put(stat.getType(), stat);
    }

    /**
     * Removes a stat, by type, from the entity.
     *
     * @param type
     *          The type of the stat.
     */
    public void removeStat(final StatisticType type) {
        if (type == null) {
            return;
        }

        stats.remove(type);
    }

    /**
     * Retrieves a stat, by type, from the entity.
     *
     * @param type
     *          The type of the stat.
     *
     * @return
     *          The stat.
     *          If the type is null, then null is returned.
     *          If the entity has no stat that uses the specified type, then null is returned.
     */
    public Statistic getStat(final StatisticType type) {
        if (type == null) {
            return null;
        }

        return stats.get(type);
    }

    /**
     * Retrieves a specific bounding box from the current sprite.
     *
     * @param name
     *          The bounding box's name.
     *
     * @return
     *          The bounding box, or null if no bounding box with a matching name was found.
     */
    public Rectangle getBoundingBox(final String name) {
        Rectangle boundingBox = spriteAnimation.getCurrentSprite().getBoundingBox(name);

        if (boundingBox != null) {
            boundingBox.x += position.x;
            boundingBox.y += position.y;
        }

        return boundingBox;
    }

    public boolean isFacingLeft() {
        return spriteAnimation.isFlippedHorizontally();
    }

    public boolean isFacingRight() {
        return !spriteAnimation.isFlippedHorizontally();
    }

    /**
     * Determines the radius within which the entity can attack an enemy entity.
     *
     * @return
     *          The radius within which the entity can attack an enemy entity.
     */
    public int getCombatRadius() {
        return spriteAnimation.getCurrentWidth();
    }

    /**
     * Changes the animation state.
     *
     * @param animationState
     *          The new state.
     */
    public void setAnimation(final AnimationState animationState) {
        if (animationState == null) {
            return;
        }

        hasAttacked = false;

        final boolean isFlippedHorizontally = spriteAnimation != null && spriteAnimation.isFlippedHorizontally();
        final boolean isFlippedVertically = spriteAnimation != null && spriteAnimation.isFlippedVertically();

        this.animationState = animationState;
        spriteAnimation = spriteSheet.getAnimatedSprite(animationState.name());

        spriteAnimation.setFlippedHorizontally(isFlippedHorizontally);
        spriteAnimation.setFlippedVertically(isFlippedVertically);
    }

    public void swapFacing() {
        spriteAnimation.setFlippedHorizontally(!spriteAnimation.isFlippedHorizontally());
    }
}
