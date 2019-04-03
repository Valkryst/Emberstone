package com.valkryst.Emberstone.entity;

import com.valkryst.Emberstone.item.EquipmentSlot;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.item.Rarity;
import com.valkryst.Emberstone.item.generator.EquipmentGenerator;
import com.valkryst.Emberstone.map.Map;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Chest extends Entity {
    /**
     * Constructs a new Chest.
     *
     * @param position
     *          Location within the level.
     *
     * @param spriteSheet
     *          The sprite sheet.
     *
     * @param inventory
     *          The chest's inventory.
     */
    private Chest(final Point position, final SpriteSheet spriteSheet, final Inventory inventory) {
        super(position, spriteSheet);

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            super.getInventory().equip(inventory.getEquipment(slot));
        }
    }

    /**
     * Creates a new chest.
     *
     * @param position
     *          Location within the level.
     *
     * @param lootLevel
     *          The level of loot ot generate.
     *
     * @return
     *          The chest.
     */
    public static Chest createChest(final Point position, final int lootLevel) {
        // Generate a random inventory.
        Rarity highestRarity = Rarity.COMMON;

        final Inventory inventory = new Inventory();
        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                final EquipmentGenerator equipmentGenerator = new EquipmentGenerator(lootLevel, slot);
                inventory.equip(equipmentGenerator.generate());

                switch (highestRarity) {
                    case COMMON: {
                        switch (inventory.getEquipment(slot).getRarity()) {
                            case UNCOMMON: {
                                highestRarity = Rarity.UNCOMMON;
                                break;
                            }
                            case RARE: {
                                highestRarity = Rarity.RARE;
                                break;
                            }
                            case EPIC: {
                                highestRarity = Rarity.EPIC;
                                break;
                            }
                            case LEGENDARY: {
                                highestRarity = Rarity.LEGENDARY;
                                break;
                            }
                            case ARTIFACT: {
                                highestRarity = Rarity.ARTIFACT;
                                break;
                            }
                        }
                        break;
                    }
                    case UNCOMMON: {
                        switch (inventory.getEquipment(slot).getRarity()) {
                            case RARE: {
                                highestRarity = Rarity.RARE;
                                break;
                            }
                            case EPIC: {
                                highestRarity = Rarity.EPIC;
                                break;
                            }
                            case LEGENDARY: {
                                highestRarity = Rarity.LEGENDARY;
                                break;
                            }
                            case ARTIFACT: {
                                highestRarity = Rarity.ARTIFACT;
                                break;
                            }
                        }
                        break;
                    }
                    case RARE: {
                        switch (inventory.getEquipment(slot).getRarity()) {
                            case EPIC: {
                                highestRarity = Rarity.EPIC;
                                break;
                            }
                            case LEGENDARY: {
                                highestRarity = Rarity.LEGENDARY;
                                break;
                            }
                            case ARTIFACT: {
                                highestRarity = Rarity.ARTIFACT;
                                break;
                            }
                        }
                        break;
                    }
                    case EPIC: {
                        switch (inventory.getEquipment(slot).getRarity()) {
                            case LEGENDARY: {
                                highestRarity = Rarity.LEGENDARY;
                                break;
                            }
                            case ARTIFACT: {
                                highestRarity = Rarity.ARTIFACT;
                                break;
                            }
                        }
                        break;
                    }
                    case LEGENDARY: {
                        switch (inventory.getEquipment(slot).getRarity()) {
                            case ARTIFACT: {
                                highestRarity = Rarity.ARTIFACT;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        SpriteSheet sheet = null;

        // Load sprite sheet.
        try {
            // Find the item of the highest rarity within the chest and set it to use the appropriate sprite.
            final SpriteAtlas atlas;

            switch (highestRarity) {
                default:
                case COMMON: {
                    atlas = SpriteType.CHEST_COMMON.getSpriteAtlas();
                    break;
                }
                case UNCOMMON: {
                    atlas = SpriteType.CHEST_UNCOMMON.getSpriteAtlas();
                    break;
                }
                case RARE: {
                    atlas = SpriteType.CHEST_RARE.getSpriteAtlas();
                    break;
                }
                case EPIC: {
                    atlas = SpriteType.CHEST_EPIC.getSpriteAtlas();
                    break;
                }
                case LEGENDARY: {
                    atlas = SpriteType.CHEST_LEGENDARY.getSpriteAtlas();
                    break;
                }
                case ARTIFACT: {
                    atlas = SpriteType.CHEST_ARTIFACT.getSpriteAtlas();
                    break;
                }
            }

            sheet = atlas.getSpriteSheet("Entity");
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Create the chest.
        return new Chest(position, sheet, inventory);
    }

    @Override
    public void update(final Map map, final double deltaTime) {}
}
