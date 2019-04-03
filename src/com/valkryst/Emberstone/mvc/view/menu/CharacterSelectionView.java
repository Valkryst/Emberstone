package com.valkryst.Emberstone.mvc.view.menu;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.entity.Player;
import com.valkryst.Emberstone.entity.SpriteType;
import com.valkryst.Emberstone.item.EquipmentSlot;
import com.valkryst.Emberstone.item.generator.EquipmentGenerator;
import com.valkryst.Emberstone.media.GameAudio;
import com.valkryst.Emberstone.media.Video;
import com.valkryst.Emberstone.mvc.component.EButton;
import com.valkryst.Emberstone.mvc.component.ELabel;
import com.valkryst.Emberstone.mvc.controller.LevelController;
import com.valkryst.Emberstone.mvc.controller.VideoController;
import com.valkryst.Emberstone.mvc.view.View;
import com.valkryst.Emberstone.statistic.BoundStatistic;
import com.valkryst.Emberstone.statistic.Statistic;
import com.valkryst.Emberstone.statistic.StatisticType;
import com.valkryst.V2DSprite.SpriteAtlas;
import com.valkryst.V2DSprite.SpriteSheet;
import com.valkryst.generator.MarkovGenerator;
import com.valkryst.generator.NameGenerator;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CharacterSelectionView extends View {
    /** Constructs a new CharacterSelectionView. */
    public CharacterSelectionView() {
        final Game game = Game.getInstance();

        // Set up the background.
        try {
            final SpriteAtlas atlas = SpriteAtlas.createSpriteAtlas("gui/Character Selection.png", "gui/Character Selection.json");
            final SpriteSheet sheet = atlas.getSpriteSheet("Character Selection");

            // Add Background
            super.setBackgroundImage(sheet.getSprite("Background").getBufferedImage());
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        // Generate and display names.
        try {
            final String[] trainingNames = NameGenerator.loadLinesFromJar("Human/Welsh/Male.txt");
            final MarkovGenerator generator = new MarkovGenerator(trainingNames);

            // Create labels.
            final JLabel labelA = new ELabel(generator.generateName(7), 64, Color.BLACK);
            final JLabel labelB = new ELabel(generator.generateName(7), 64, Color.BLACK);
            final JLabel labelC = new ELabel(generator.generateName(7), 64, Color.BLACK);

            // Position labels.
            labelA.setLocation(200, 400);
            labelB.setLocation(850, 400);
            labelC.setLocation(1495, 400);

            // Add labels to view.
            this.add(labelA);
            this.add(labelB);
            this.add(labelC);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // Generate and display bonuses.
        // Create labels.
        final JLabel labelA = new ELabel("400% Health", 32, Color.BLACK);
        final JLabel labelB = new ELabel("125% Speed", 32, Color.BLACK);
        final JLabel labelC = new ELabel("100% Health", 32, Color.BLACK);
        final JLabel labelD = new ELabel("100% Speed", 32, Color.BLACK);
        final JLabel labelE = new ELabel("100% Health", 32, Color.BLACK);
        final JLabel labelF = new ELabel("100% Speed", 32, Color.BLACK);


        // Position labels.
        labelA.setLocation(100, 500);
        labelD.setLocation(100, 550);
        labelB.setLocation(750, 500);
        labelE.setLocation(750, 550);
        labelC.setLocation(1395, 500);
        labelF.setLocation(1395, 550);

        // Add labels to view.
        this.add(labelA);
        this.add(labelD);
        this.add(labelB);
        this.add(labelE);
        this.add(labelC);
        this.add(labelF);

        // Create buttons.
        final JButton selectionA = new EButton("Select", 48);
        final JButton selectionB = new EButton("Select", 48);
        final JButton selectionC = new EButton("Select", 48);

        // Position buttons.
        selectionA.setLocation(120, 900);
        selectionB.setLocation(760, 900);
        selectionC.setLocation(1400, 900);

        // Add button listeners
        selectionA.addActionListener(e -> {
            GameAudio.getInstance().stopAllActiveMusic();

            final Player player = createPlayer(SpriteType.PLAYER_A);
            player.addStat(new BoundStatistic(StatisticType.HEALTH, 0, 400));
            game.setController(new VideoController(Video.INTRO, new LevelController(player)));
        });
        selectionB.addActionListener(e -> {
            GameAudio.getInstance().stopAllActiveMusic();

            final Player player = createPlayer(SpriteType.PLAYER_B);
            player.addStat(new Statistic(StatisticType.SPEED, 8));
            game.setController(new VideoController(Video.INTRO, new LevelController(player)));
        });
        selectionC.addActionListener(e -> {
            GameAudio.getInstance().stopAllActiveMusic();

            final Player player = createPlayer(SpriteType.PLAYER_C);
            game.setController(new VideoController(Video.INTRO, new LevelController(player)));
        });

        // Add buttons to view.
        this.add(selectionA);
        this.add(selectionB);
        this.add(selectionC);
    }

    private Player createPlayer(final SpriteType spriteType) {
        Player player = null;

        try {
            player = new Player(new Point(0, 0), SpriteType.PLAYER_A.getSpriteAtlas().getSpriteSheet("Entity"));

            // Generate Equipment for Player
            for (final EquipmentSlot slot : EquipmentSlot.values()) {
                final EquipmentGenerator equipmentGenerator = new EquipmentGenerator(1, slot);
                player.getInventory().equip(equipmentGenerator.generate());
            }
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
        }

        return player;
    }
}
