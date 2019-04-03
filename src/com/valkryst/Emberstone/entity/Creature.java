package com.valkryst.Emberstone.entity;

import com.valkryst.Emberstone.entity.ai.combat.AggressiveCombatAI;
import com.valkryst.Emberstone.entity.ai.combat.CombatAI;
import com.valkryst.Emberstone.entity.ai.movement.MovementAI;
import com.valkryst.Emberstone.item.*;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Creature extends Entity {
    /** The movement AI. */
    @Getter @Setter private MovementAI movementAI = new MovementAI();
    /** The combat AI. */
    @Getter @Setter private CombatAI combatAI = new AggressiveCombatAI();

    @Getter private boolean statsGenerated = false;

    @Setter private SpriteType spriteType;

    private Creature(final Point position, final SpriteSheet spriteSheet, final SpriteType spriteType) {
        super(position, spriteSheet);
        this.spriteType = spriteType;
    }

    public static Entity createCreature(final Point position) {
        // Load sprite sheet
        SpriteSheet sheet = null;
        SpriteType spriteType = null;

        try {
            final SpriteAtlas atlas;

            switch (ThreadLocalRandom.current().nextInt(6)) {
                default:
                case 0: {
                    spriteType = SpriteType.SKELETON_HEAVY;
                    atlas = SpriteType.SKELETON_HEAVY.getSpriteAtlas();
                    break;
                }
                case 1: {
                    spriteType = SpriteType.SKELETON_LIGHT;
                    atlas = SpriteType.SKELETON_LIGHT.getSpriteAtlas();
                    break;
                }
                case 2: {
                    spriteType = SpriteType.SKELETON_CULTIST;
                    atlas = SpriteType.SKELETON_CULTIST.getSpriteAtlas();
                    break;
                }
                case 3: {
                    spriteType = SpriteType.ZOMBIE_FARMER;
                    atlas = SpriteType.ZOMBIE_FARMER.getSpriteAtlas();
                    break;
                }
                case 4: {
                    spriteType = SpriteType.ZOMBIE_VILLAGER;
                    atlas = SpriteType.ZOMBIE_VILLAGER.getSpriteAtlas();
                    break;
                }
                case 5: {
                    spriteType = SpriteType.ZOMBIE_WOODCUTTER;
                    atlas = SpriteType.ZOMBIE_WOODCUTTER.getSpriteAtlas();
                    break;
                }
            }

            sheet = atlas.getSpriteSheet("Entity");
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        return new Creature(position, sheet, spriteType);
    }


    @Override
    public void update(final Map map, final double deltaTime) {
        super.update(map, deltaTime);
        combatAI.decide(map, this);
        movementAI.move(this);

        final Statistic speed = new Statistic(StatisticType.SPEED, 4);
        super.addStat(speed);
    }

    public void generateStats(final Player player) {
        if (statsGenerated) {
            return;
        }

        this.statsGenerated = true;

        // Get Player Ststs
        final BoundStatistic playerDamage = (BoundStatistic) player.getInventory().getEquipment(EquipmentSlot.MAIN_HAND).getStat(StatisticType.DAMAGE);
        final int playerHealth = 100;

        switch (spriteType) {
            default:
            case ZOMBIE_FARMER: {
                super.addStat(new Statistic(StatisticType.SPEED, 12));
                super.addStat(new BoundStatistic(StatisticType.HEALTH, 0, playerDamage.getMaxValue() * 10));

                final HashMap<StatisticType, Statistic> itemStats = new HashMap<>();

                final int minDamage = ThreadLocalRandom.current().nextInt(0, (int) (playerHealth / 12.0)) -  player.getStat(StatisticType.LEVEL).getValue();
                final int maxDamage = minDamage + ThreadLocalRandom.current().nextInt((int) (playerHealth / 9.0));
                itemStats.put(StatisticType.DAMAGE, new BoundStatistic(StatisticType.DAMAGE, minDamage, maxDamage));


                final Equipment equipment = new Equipment("Sword", "Sword", itemStats, Rarity.COMMON, Material.COPPER, EquipmentModifier.NONE, EquipmentSlot.MAIN_HAND);
                super.getInventory().equip(equipment);
                break;
            }
            case ZOMBIE_VILLAGER: {
                super.addStat(new Statistic(StatisticType.SPEED, 12));
                super.addStat(new BoundStatistic(StatisticType.HEALTH, 0, playerDamage.getMaxValue() * 10));

                final HashMap<StatisticType, Statistic> itemStats = new HashMap<>();

                final int minDamage = ThreadLocalRandom.current().nextInt(0, (int) (playerHealth / 12.0)) -  player.getStat(StatisticType.LEVEL).getValue();
                final int maxDamage = minDamage + ThreadLocalRandom.current().nextInt((int) (playerHealth / 10.0));
                itemStats.put(StatisticType.DAMAGE, new BoundStatistic(StatisticType.DAMAGE, minDamage, maxDamage));


                final Equipment equipment = new Equipment("Sword", "Sword", itemStats, Rarity.COMMON, Material.COPPER, EquipmentModifier.NONE, EquipmentSlot.MAIN_HAND);
                super.getInventory().equip(equipment);
                break;
            }
            case ZOMBIE_WOODCUTTER: {
                super.addStat(new Statistic(StatisticType.SPEED, 12));
                super.addStat(new BoundStatistic(StatisticType.HEALTH, 0, playerDamage.getMaxValue() * 8));

                final HashMap<StatisticType, Statistic> itemStats = new HashMap<>();

                final int minDamage = ThreadLocalRandom.current().nextInt(0, (int) (playerHealth / 12.0)) -  player.getStat(StatisticType.LEVEL).getValue();
                final int maxDamage = minDamage + ThreadLocalRandom.current().nextInt((int) (playerHealth / 8.0));
                itemStats.put(StatisticType.DAMAGE, new BoundStatistic(StatisticType.DAMAGE, minDamage, maxDamage));


                final Equipment equipment = new Equipment("Sword", "Sword", itemStats, Rarity.COMMON, Material.COPPER, EquipmentModifier.NONE, EquipmentSlot.MAIN_HAND);
                super.getInventory().equip(equipment);
                break;
            }
            case SKELETON_CULTIST: {
                super.addStat(new Statistic(StatisticType.SPEED, 10));
                super.addStat(new BoundStatistic(StatisticType.HEALTH, 0, playerDamage.getMaxValue() * 6));

                final HashMap<StatisticType, Statistic> itemStats = new HashMap<>();

                final int minDamage = ThreadLocalRandom.current().nextInt(0, (int) (playerHealth / 10.0)) -  player.getStat(StatisticType.LEVEL).getValue();
                final int maxDamage = minDamage + ThreadLocalRandom.current().nextInt((int) (playerHealth / 9.0));
                itemStats.put(StatisticType.DAMAGE, new BoundStatistic(StatisticType.DAMAGE, minDamage, maxDamage));


                final Equipment equipment = new Equipment("Sword", "Sword", itemStats, Rarity.COMMON, Material.COPPER, EquipmentModifier.NONE, EquipmentSlot.MAIN_HAND);
                super.getInventory().equip(equipment);
                break;
            }
            case SKELETON_HEAVY: {
                super.addStat(new Statistic(StatisticType.SPEED, 8));
                super.addStat(new BoundStatistic(StatisticType.HEALTH, 0, playerDamage.getMaxValue() * 2));

                final HashMap<StatisticType, Statistic> itemStats = new HashMap<>();

                final int minDamage = ThreadLocalRandom.current().nextInt(0, (int) (playerHealth / 8.0)) -  player.getStat(StatisticType.LEVEL).getValue();
                final int maxDamage = minDamage + ThreadLocalRandom.current().nextInt((int) (playerHealth / 2.0));
                itemStats.put(StatisticType.DAMAGE, new BoundStatistic(StatisticType.DAMAGE, minDamage, maxDamage));


                final Equipment equipment = new Equipment("Sword", "Sword", itemStats, Rarity.COMMON, Material.COPPER, EquipmentModifier.NONE, EquipmentSlot.MAIN_HAND);
                super.getInventory().equip(equipment);
                break;
            }
            case SKELETON_LIGHT: {
                super.addStat(new Statistic(StatisticType.SPEED, 8));
                super.addStat(new BoundStatistic(StatisticType.HEALTH, 0, playerDamage.getMaxValue() * 4));

                final HashMap<StatisticType, Statistic> itemStats = new HashMap<>();

                final int minDamage = ThreadLocalRandom.current().nextInt(0, (int) (playerHealth / 9.0)) -  player.getStat(StatisticType.LEVEL).getValue();
                final int maxDamage = minDamage + ThreadLocalRandom.current().nextInt((int) (playerHealth / 3.0));
                itemStats.put(StatisticType.DAMAGE, new BoundStatistic(StatisticType.DAMAGE, minDamage, maxDamage));


                final Equipment equipment = new Equipment("Sword", "Sword", itemStats, Rarity.COMMON, Material.COPPER, EquipmentModifier.NONE, EquipmentSlot.MAIN_HAND);
                super.getInventory().equip(equipment);
                break;
            }
        }
    }
}
