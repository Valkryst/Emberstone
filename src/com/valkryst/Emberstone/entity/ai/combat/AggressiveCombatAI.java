package com.valkryst.Emberstone.entity.ai.combat;

import com.valkryst.Emberstone.action.SwapFacing;
import com.valkryst.Emberstone.entity.*;
import com.valkryst.Emberstone.map.Map;
import javafx.concurrent.Task;
import lombok.NonNull;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class AggressiveCombatAI implements CombatAI {
    private final int millisecondsBetweenAttacks = ThreadLocalRandom.current().nextInt(500, 1250);
    private final int millisecondsBetweenMovements = millisecondsBetweenAttacks / 4;

    private long lastAttackTime = 0;

    private long lastMovementTime = 0;

    @Override
    public void decide(final @NonNull Map map, final @NonNull Creature self) {
        final long currentTime = System.currentTimeMillis();

        if (currentTime - lastAttackTime >= millisecondsBetweenAttacks) {
            lastAttackTime = currentTime;
        } else {
            return;
        }

        // Check nearby entities and attack any within range.
        boolean foundValidTargetBehindSelf = false;

        boolean attackedTarget = false;
        Entity closestEnemy = null;
        double closestEnemyDistance = Double.POSITIVE_INFINITY;

        final Iterator<Entity> it = map.getEntitiesIterator();
        while (it.hasNext()) {
            final Entity target = it.next();

            // Creatures ignore creatures
            if (target instanceof Creature) {
                continue;
            }

            // Creatures ignore chests.
            if (target instanceof Chest) {
                continue;
            }



            // Creatures ignore portals.
            if (target instanceof Portal) {
                continue;
            }

            // Ignore dead entities.
            if (target.getAnimationState() == AnimationState.DYING) {
                continue;
            }

            // Ignore any far-away entities.
            final Point selfPosition = self.getPosition();
            final Point targetPosition = target.getPosition();

            if (Math.abs(selfPosition.x - targetPosition.x) >= 600) {
                continue;
            }

            if (Math.abs(selfPosition.y - targetPosition.y) >= 600) {
                continue;
            }

            final double distance = Math.sqrt(Math.pow(targetPosition.x - selfPosition.x, 2) + Math.pow(targetPosition.y - selfPosition.y, 2));

            // Find the closest enemy.
            if (closestEnemy == null) {
                closestEnemy = target;
                closestEnemyDistance = distance;
            } else {
                if (distance < closestEnemyDistance) {
                    closestEnemy = target;
                    closestEnemyDistance = distance;
                }
            }

            if (distance <= self.getCombatRadius()) {
                // Target to the left of entity, but entity facing right.
                if (targetPosition.x < selfPosition.x && self.isFacingRight()) {
                    foundValidTargetBehindSelf = true;
                    continue;
                }

                // Target to right of entity, but entity facing left.
                if (targetPosition.x > selfPosition.x && self.isFacingLeft()) {
                    foundValidTargetBehindSelf = true;
                    continue;
                }

                if (self.getAnimationState() == AnimationState.IDLE) {
                    self.setAnimation(AnimationState.ATTACKING_IDLE);
                    attackedTarget = true;
                    break;
                }

                if (self.getAnimationState() == AnimationState.RUNNING) {
                    self.setAnimation(AnimationState.ATTACKING_RUNNING);
                    attackedTarget = true;
                    break;
                }
            }
        }

        if (foundValidTargetBehindSelf) {
            self.addAction(new SwapFacing());
        }

        if (closestEnemy == null) {
            self.setHorizontalSpeed(0);
            self.setVerticalSpeed(0);
            self.getMovementAI().clearPath();
        }

        if (attackedTarget == false && closestEnemy != null) {
            if (currentTime - lastMovementTime >= millisecondsBetweenMovements) {
                lastMovementTime = currentTime;
                self.getMovementAI().constructPath(map, self.getPosition(), closestEnemy.getPosition());
            }
        }
    }
}
