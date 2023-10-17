package com.valkryst.Emberstone.mvc.view.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.entity.Player;
import com.valkryst.Emberstone.item.Equipment;
import com.valkryst.Emberstone.item.EquipmentSlot;
import com.valkryst.Emberstone.item.Inventory;
import com.valkryst.Emberstone.mvc.component.EButton;
import com.valkryst.Emberstone.mvc.component.ELabel;
import com.valkryst.Emberstone.mvc.component.ERadioButton;
import com.valkryst.Emberstone.mvc.controller.Controller;
import com.valkryst.Emberstone.mvc.view.View;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryView extends View {
    private final static int RADIO_BUTTON_HEIGHT = 40;
    private final static int RADIO_BUTTON_TEXT_SIZE = 32;

    private Equipment selectedItem = null;
    private Inventory playerInventory = null;
    private Inventory lootInventory = null;
    private Player player = null;

    private final List<Component> playerInventoryComponents = new ArrayList<>();
    private final List<Component> lootInventoryComponents = new ArrayList<>();
    private final List<Component> playerInventoryStatComponents = new ArrayList<>();
    private final List<Component> lootInventoryStatComponents = new ArrayList<>();
    private final List<Component> playerStatComponents = new ArrayList<>();


    /**
     * Constructs a new InventoryView.
     *
     * @param backgroundImage
     *          The background image.
     *
     * @param previousController
     *          The controller whose view was displayed before this view.
     */
    public InventoryView(final BufferedImage backgroundImage, final Controller previousController) {
        // Load the static GUI and add it to the background image.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Inventory.png", "gui/Inventory.json");
            final SpriteSheet sheet = atlas.getSpriteSheet("Inventory");
            final BufferedImage staticGui = sheet.getSprite("Background").getBufferedImage();

            // Combine the background image and static GUI image.
            final Graphics2D gc = (Graphics2D) backgroundImage.getGraphics();
            gc.drawImage(staticGui, 0, 0, null);
            gc.dispose();
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Set the background image.
        super.setBackgroundImage(backgroundImage);

        // Create the equip button
        final JButton equipButton = new EButton("Equip Selected Loot", 38);
        final JButton backButton = new EButton("Back", 38);

        final Game game = Game.getInstance();
        final int x = (int) ((game.getViewWidth() / 2.0) - (equipButton.getWidth() / 2.0));
        int y = (int) (game.getViewHeight() / 2.0) + equipButton.getHeight() * 2;
        equipButton.setLocation(x, y);
        backButton.setLocation(x, y + (equipButton.getHeight() * 2));

        equipButton.addActionListener(e -> {
            if (selectedItem == null || playerInventory == null || lootInventory == null || player == null) {
                return;
            }

            final EquipmentSlot slot = selectedItem.getSlot();
            final Equipment otherItem = playerInventory.getEquipment(slot);

            lootInventory.unequip(slot);
            lootInventory.equip(otherItem);

            playerInventory.equip(selectedItem);

            displayPlayerInventory(playerInventory);
            displayLootInventory(lootInventory);
            displayPlayerStats(player);
        });
        backButton.addActionListener(e -> {
            Game.getInstance().setController(previousController);
        });
        super.add(equipButton);
        super.add(backButton);
    }

    public void displayPlayerInventory(final Inventory playerInventory) {
        // If the display is being refreshed, we need to clear-out the previous components.
        playerInventoryComponents.forEach(super::remove);
        playerInventoryComponents.clear();

        if (playerInventory == null) {
            super.repaint();
            return;
        }

        this.playerInventory = playerInventory;

        int x = 50;
        int y = 50;

        int row = 0;

        final ButtonGroup buttonGroup = new ButtonGroup();

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            final Equipment item = playerInventory.getEquipment(slot);

            final String text = (item == null ? "Empty" : item.getName());
            final Point position = new Point(x, y + (row * RADIO_BUTTON_HEIGHT));
            final Color color = (item == null ? Color.GRAY : item.getRarity().getColor());

            final JRadioButton radioButton = new ERadioButton(text, RADIO_BUTTON_TEXT_SIZE, color, buttonGroup);
            radioButton.setLocation(position);
            radioButton.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // Clear the previously displayed stats.
                    playerInventoryStatComponents.forEach(super::remove);
                    playerInventoryStatComponents.clear();

                    if (item == null) {
                        super.repaint();
                        return;
                    }

                    // Display stats.
                    int statX = 130;
                    int statY = 110 + (8 * RADIO_BUTTON_HEIGHT);
                    int statRow = 0;

                    JLabel label = new ELabel("Item Stats", RADIO_BUTTON_TEXT_SIZE, Color.WHITE);
                    label.setLocation(x + 37, statY - 40);
                    super.add(label);
                    playerInventoryStatComponents.add(label);

                    if (item.getStat(StatisticType.DAMAGE) != null) {
                        label = item.getStat(StatisticType.DAMAGE).getLabel(RADIO_BUTTON_TEXT_SIZE);
                        label.setLocation(statX, statY + (statRow * RADIO_BUTTON_HEIGHT));
                        super.add(label);
                        playerInventoryStatComponents.add(label);

                        statRow++;
                    } else {
                        label = item.getStat(StatisticType.ARMOR).getLabel(RADIO_BUTTON_TEXT_SIZE);
                        label.setLocation(statX, statY + (statRow * RADIO_BUTTON_HEIGHT));
                        super.add(label);
                        playerInventoryStatComponents.add(label);

                        statRow++;
                    }

                    for (final StatisticType statType : item.getModifier().getStats()) {
                        final Statistic stat = item.getStat(statType);

                        label = stat.getLabel(RADIO_BUTTON_TEXT_SIZE);
                        label.setLocation(statX, statY + (statRow * RADIO_BUTTON_HEIGHT));
                        super.add(label);
                        playerInventoryStatComponents.add(label);

                        statRow++;
                    }

                    super.repaint();
                }
            });

            super.add(radioButton);
            playerInventoryComponents.add(radioButton);

            if (row == 0) {
                //selectedEquipmentSlot = slot;
                radioButton.setSelected(true);
            }

            row++;
        }
    }

    public void displayLootInventory(final Inventory lootInventory) {
        // If the display is being refreshed, we need to clear-out the previous components.
        lootInventoryComponents.forEach(super::remove);
        lootInventoryComponents.clear();

        if (lootInventory == null) {
            super.repaint();
            return;
        }

        this.lootInventory = lootInventory;

        int x = 1330;
        int y = 50;

        int row = 0;

        final ButtonGroup buttonGroup = new ButtonGroup();

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            final Equipment item = lootInventory.getEquipment(slot);

            final String text = (item == null ? "Empty" : item.getName());
            final Point position = new Point(x, y + (row * RADIO_BUTTON_HEIGHT));
            final Color color = (item == null ? Color.GRAY : item.getRarity().getColor());

            final JRadioButton radioButton = new ERadioButton(text, RADIO_BUTTON_TEXT_SIZE, color, buttonGroup);
            radioButton.setLocation(position);
            radioButton.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // Clear the previously displayed stats.
                    lootInventoryStatComponents.forEach(super::remove);
                    lootInventoryStatComponents.clear();

                    if (item == null) {
                        selectedItem = null;
                        super.repaint();
                        return;
                    }

                    selectedItem = item;

                    // Display stats.
                    int statX = 1410;
                    int statY = 110 + (8 * RADIO_BUTTON_HEIGHT);
                    int statRow = 0;

                    JLabel label = new ELabel("Item Stats", RADIO_BUTTON_TEXT_SIZE, Color.WHITE);
                    label.setLocation(x + 37, statY - 40);
                    super.add(label);
                    lootInventoryStatComponents.add(label);

                    if (item.getStat(StatisticType.DAMAGE) != null) {
                        label = item.getStat(StatisticType.DAMAGE).getLabel(RADIO_BUTTON_TEXT_SIZE);
                        label.setLocation(statX, statY + (statRow * RADIO_BUTTON_HEIGHT));
                        super.add(label);
                        lootInventoryStatComponents.add(label);

                        statRow++;
                    } else {
                        label = item.getStat(StatisticType.ARMOR).getLabel(RADIO_BUTTON_TEXT_SIZE);
                        label.setLocation(statX, statY + (statRow * RADIO_BUTTON_HEIGHT));
                        super.add(label);
                        lootInventoryStatComponents.add(label);

                        statRow++;
                    }

                    for (final StatisticType statType : item.getModifier().getStats()) {
                        final Statistic stat = item.getStat(statType);

                        label = stat.getLabel(RADIO_BUTTON_TEXT_SIZE);
                        label.setLocation(statX, statY + (statRow * RADIO_BUTTON_HEIGHT));
                        super.add(label);
                        lootInventoryStatComponents.add(label);

                        statRow++;
                    }

                    super.repaint();
                }
            });

            super.add(radioButton);
            lootInventoryComponents.add(radioButton);

            if (row == 0) {
                //selectedEquipmentSlot = slot;
                radioButton.setSelected(true);
            }

            row++;
        }
    }

    public void displayPlayerStats(final Player player) {
        if (player == null) {
            return;
        }

        this.player = player;

        final Inventory playerInventory = player.getInventory();

        // If the display is being refreshed, we need to clear-out the previous components.
        playerStatComponents.forEach(super::remove);
        playerStatComponents.clear();

        // Calculate the values of each stat.
        final HashMap<StatisticType, Statistic> statTotals = new HashMap<>();

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            final Equipment item = playerInventory.getEquipment(slot);

            if (item != null) {
                for (final StatisticType statType : StatisticType.values()) {
                    if (statType == StatisticType.DAMAGE) {
                        continue; // Damage is a bound stat, do it separately.
                    }

                    final Statistic stat = item.getStat(statType);

                    if (stat != null) {
                        Statistic current = statTotals.getOrDefault(statType, new Statistic(statType, 0));
                        current.setValue(current.getValue() + stat.getValue());
                        statTotals.put(statType, current);
                    }
                }
            }
        }

        // Calculate the min/max damage;
        final Equipment weapon = playerInventory.getEquipment(EquipmentSlot.MAIN_HAND);
        final BoundStatistic damageStat = (weapon == null ? new BoundStatistic(StatisticType.DAMAGE, 0, 0) : (BoundStatistic) weapon.getStat(StatisticType.DAMAGE));

        // Display the stat labels.
        int statX = 130;
        int statY = 110 + (8 * RADIO_BUTTON_HEIGHT) + RADIO_BUTTON_HEIGHT + (3 * RADIO_BUTTON_HEIGHT);


        JLabel label = new ELabel("Player Stats", RADIO_BUTTON_TEXT_SIZE, Color.WHITE);
        label.setLocation(statX - 40, statY);
        super.add(label);
        playerStatComponents.add(label);
        statY += RADIO_BUTTON_HEIGHT;

        label = damageStat.getLabel(RADIO_BUTTON_TEXT_SIZE);
        label.setLocation(statX, statY);
        super.add(label);
        playerStatComponents.add(label);
        statY += RADIO_BUTTON_HEIGHT;

        for (final StatisticType statisticType : StatisticType.values()) {
            final Statistic stat = statTotals.get(statisticType);

            if (stat == null) {
                continue;
            }

            label = stat.getLabel(RADIO_BUTTON_TEXT_SIZE);
            label.setLocation(statX, statY);
            super.add(label);
            playerStatComponents.add(label);

            statY += RADIO_BUTTON_HEIGHT;
        }

        final BoundStatistic health = (BoundStatistic) player.getStat(StatisticType.HEALTH);
        if (health != null) {
            label = health.getLabel(RADIO_BUTTON_TEXT_SIZE);
            label.setText("Health: " + (health.getValue()));
            label.setLocation(statX, statY);
            super.add(label);
            playerStatComponents.add(label);
            statY += RADIO_BUTTON_HEIGHT;
        }

        final Statistic level = player.getStat(StatisticType.LEVEL);
        if (level != null) {
            label = level.getLabel(RADIO_BUTTON_TEXT_SIZE);
            label.setLocation(statX, statY);
            super.add(label);
            playerStatComponents.add(label);
        }
    }
}
