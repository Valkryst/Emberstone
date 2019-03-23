package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.entity.SpriteType;
import com.valkryst.Emberstone.item.Equipment;
import com.valkryst.Emberstone.item.EquipmentSlot;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.component.*;
import com.valkryst.Emberstone.mvc.component.Button;
import com.valkryst.Emberstone.mvc.component.Component;
import com.valkryst.Emberstone.mvc.component.Label;
import com.valkryst.Emberstone.mvc.controller.LevelController;
import com.valkryst.Emberstone.mvc.controller.MainMenuController;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import lombok.NonNull;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryView extends View {
    private final static int RADIO_BUTTON_HEIGHT = 40;
    private final static int RADIO_BUTTON_WIDTH = 500;

    private Inventory playerInventory;
    private Inventory lootInventory;

    private EquipmentSlot selectedPlayerEquipmentSlot = null;
    private EquipmentSlot selectedLootEquipmentSlot= null;

    private List<Component> playerInventoryComponents = new ArrayList<>();
    private List<Component> playerItemInspectionComponents = new ArrayList<>();
    private List<Component> lootInventoryComponents = new ArrayList<>();
    private List<Component> lootItemInspectionComponents = new ArrayList<>();
    private List<Component> playerStatsComponents = new ArrayList<>();

    @Getter private Button quitButton;

    /**
     * Constructs a new InventoryView, displaying the player's inventory and stats.
     *
     * @param playerInventory
     *          The player's inventory.
     */
    public InventoryView(final Inventory playerInventory) {
        displayMisc();
        displayPlayerInventory(playerInventory);
        displayPlayerStats(playerInventory);
    }

    /**
     * Constructs a new InventoryView, displaying the player's inventory and a lootable inventory.
     *
     * @param playerInventory
     *          The player's inventory.
     *
     * @param lootInventory
     *          The lootable inventory.
     */
    public InventoryView(final Inventory playerInventory, final Inventory lootInventory) {
        displayMisc();
        displayPlayerInventory(playerInventory);
        displayLootInventory(lootInventory);
    }

    private void displayMisc() {
        // Set up the buttons.
        final Game game = Game.getInstance();

        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Buttons.png", "gui/Buttons.json");
            SpriteSheet spriteSheet = atlas.getSpriteSheet("Select");

            // Add small quit button.
            spriteSheet = atlas.getSpriteSheet("Quit - Small");
            quitButton = new Button(new Point(1850, 0), spriteSheet);
            super.addComponent(quitButton);
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        quitButton.addStateChangeFunction(ButtonState.RELEASED, () -> game.setController(new MainMenuController()));
    }

    private void displayPlayerInventory(final @NonNull Inventory inventory) {
        // Remove any existing components from the parent, for when we refresh the inventory.
        if (playerInventoryComponents.size() > 0) {
            playerInventoryComponents.forEach(super::removeComponent);
        }

        playerInventoryComponents.clear();

        // Top-left coordinates of the display area.
        int x = 50;
        int y = 50;


        // Add a radio button for each equipment slot.
        final RadioButtonGroup radioButtonGroup = new RadioButtonGroup();
        int itemIndex = 0;

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            final Equipment item = inventory.getEquipment(slot);

            // Ensure the first item in the inventory is selected.
            if (itemIndex == 0) {
                selectedPlayerEquipmentSlot = slot;
            }

            final Point position = new Point(x, y+ (itemIndex * RADIO_BUTTON_HEIGHT));
            final String text = (item == null ? "Empty": item.getName());
            final Color color = (item == null ? Color.GRAY : item.getRarity().getColor());
            final Dimension dimensions = new Dimension(RADIO_BUTTON_WIDTH, RADIO_BUTTON_HEIGHT);
            final RadioButton button = new RadioButton(position, text, color, dimensions, radioButtonGroup);

            radioButtonGroup.addRadioButton(button);
            playerInventoryComponents.add(button);
            super.addComponent(button);

            // Add runnable to display the item's information.
            final Runnable runnable = () -> {
                // Top-left coordinates of the display area
                int informationX = 50;
                int informationY = 100 + (EquipmentSlot.values().length * RADIO_BUTTON_HEIGHT);

                selectedPlayerEquipmentSlot = slot;

                // Remove any existing components from the parent, for when we refresh the inspection components.
                if (playerItemInspectionComponents.size() > 0) {
                    playerItemInspectionComponents.forEach(super::removeComponent);
                }

                playerItemInspectionComponents.clear();

                // Display nothing if there is no item to display.
                final Equipment equipment = inventory.getEquipment(slot);
                if (equipment == null) {
                    return;
                }

                // Add stat information labels
                if (equipment.getStat(StatisticType.DAMAGE) != null) {
                    final Label label = equipment.getStat(StatisticType.DAMAGE).getLabel();
                    label.setX(informationX);
                    label.setY(informationY);

                    playerItemInspectionComponents.add(label);
                    super.addComponent(label);
                } else {
                    final Label label = equipment.getStat(StatisticType.ARMOR).getLabel();
                    label.setX(informationX);
                    label.setY(informationY);

                    playerItemInspectionComponents.add(label);
                    super.addComponent(label);
                }

                informationY += RADIO_BUTTON_HEIGHT;

                for (int row = 0 ; row < equipment.getModifier().getStats().length ; row++) {
                    final Statistic stat = equipment.getStat(equipment.getModifier().getStats()[row]);

                    if (stat != null) {
                        final Label label = stat.getLabel();
                        label.setX(informationX);
                        label.setY(informationY);

                        informationY += RADIO_BUTTON_HEIGHT;

                        playerItemInspectionComponents.add(label);
                        super.addComponent(label);
                    }
                }
            };
            button.addStateChangeFunction(ButtonState.PRESSED, runnable);

            if (itemIndex == 0) {
                runnable.run();
            }

            itemIndex++;
        }
    }

    private void displayLootInventory(final @NonNull Inventory inventory) {
        // Remove any existing components from the parent, for when we refresh the inventory.
        if (lootInventoryComponents.size() > 0) {
            lootInventoryComponents.forEach(super::removeComponent);
        }

        lootInventoryComponents.clear();

        // Top-left coordinates of the display area.
        int x = 800;
        int y = 50;

        // Add a radio button for each equipment slot.
        final RadioButtonGroup radioButtonGroup = new RadioButtonGroup();
        int itemIndex = 0;

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            final Equipment item = inventory.getEquipment(slot);

            // Ensure the first item in the inventory is selected.
            if (itemIndex == 0) {
                selectedLootEquipmentSlot = slot;
            }

            final Point position = new Point(x, y+ (itemIndex * RADIO_BUTTON_HEIGHT));
            final String text = (item == null ? "Empty": item.getName());
            final Color color = (item == null ? Color.GRAY : item.getRarity().getColor());
            final Dimension dimensions = new Dimension(RADIO_BUTTON_WIDTH, RADIO_BUTTON_HEIGHT);
            final RadioButton button = new RadioButton(position, text, color, dimensions, radioButtonGroup);

            radioButtonGroup.addRadioButton(button);
            lootInventoryComponents.add(button);
            super.addComponent(button);

            // Add runnable to display the item's information.
            final Runnable runnable = () -> {
                // Top-left coordinates of the display area
                int informationX = 800;
                int informationY = 100 + (EquipmentSlot.values().length * RADIO_BUTTON_HEIGHT);

                selectedPlayerEquipmentSlot = slot;

                // Remove any existing components from the parent, for when we refresh the inspection components.
                if (lootItemInspectionComponents.size() > 0) {
                    lootItemInspectionComponents.forEach(super::removeComponent);
                }

                lootItemInspectionComponents.clear();

                // Display nothing if there is no item to display.
                final Equipment equipment = inventory.getEquipment(slot);
                if (equipment == null) {
                    return;
                }

                // Add stat information labels
                if (equipment.getStat(StatisticType.DAMAGE) != null) {
                    final Label label = equipment.getStat(StatisticType.DAMAGE).getLabel();
                    label.setX(informationX);
                    label.setY(informationY);

                    lootItemInspectionComponents.add(label);
                    super.addComponent(label);
                } else {
                    final Label label = equipment.getStat(StatisticType.ARMOR).getLabel();
                    label.setX(informationX);
                    label.setY(informationY);

                    lootItemInspectionComponents.add(label);
                    super.addComponent(label);
                }

                informationY += RADIO_BUTTON_HEIGHT;

                for (int row = 0 ; row < equipment.getModifier().getStats().length ; row++) {
                    final Statistic stat = equipment.getStat(equipment.getModifier().getStats()[row]);

                    if (stat != null) {
                        final Label label = stat.getLabel();
                        label.setX(informationX);
                        label.setY(informationY);

                        informationY += RADIO_BUTTON_HEIGHT;

                        lootItemInspectionComponents.add(label);
                        super.addComponent(label);
                    }
                }
            };
            button.addStateChangeFunction(ButtonState.PRESSED, runnable);

            if (itemIndex == 0) {
                runnable.run();
            }

            itemIndex++;
        }
    }

    private void displayPlayerStats(final @NonNull Inventory inventory) {
        // Remove any existing components from the parent, for when we refresh the stats.
        if (playerStatsComponents.size() > 0) {
            playerStatsComponents.forEach(super::removeComponent);
        }

        playerStatsComponents.clear();
    }
}
