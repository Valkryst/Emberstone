package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.display.controller.SettingsController;
import com.valkryst.Emberstone.display.model.SettingsModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;

public class SettingsView extends View {
    private final JComboBox<String> alphaInterpolationComboBox = new JComboBox<>(new String[]{ "Auto", "Quality", "Speed" });
    private final JComboBox<String> antialiasingComboBox = new JComboBox<>(new String[] { "Auto", "On", "Off" });
    private final JComboBox<String> colorRenderingComboBox = new JComboBox<>(new String[]{ "Auto", "Quality", "Speed" });
    private final JComboBox<String> ditheringComboBox = new JComboBox<>(new String[] { "Auto", "Enable", "Disable" });
    private final JComboBox<String> interpolationComboBox = new JComboBox<>(new String[] { "Bicubic", "Bilinear", "Nearest Neighbor" });
    private final JComboBox<String> renderingComboBox = new JComboBox<>(new String[]{ "Auto", "Quality", "Speed" });

    public SettingsView(final SettingsController controller) {
        final var model = SettingsModel.getInstance();

        controller.addModel(model);

        this.setLayout(new GridBagLayout());

        final var innerPanel = new JPanel(new GridLayout(0, 2));
        innerPanel.setPreferredSize(new Dimension(512, 512));
        this.add(innerPanel);

        innerPanel.add(new JLabel("Alpha Interpolation"));
        alphaInterpolationComboBox.setSelectedItem(model.getAlphaInterpolation());
        alphaInterpolationComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeAlphaInterpolation((String) e.getItem());
            }
        });
        innerPanel.add(alphaInterpolationComboBox, "span");

        innerPanel.add(new JLabel("Antialiasing"));
        antialiasingComboBox.setSelectedItem(model.getAntialiasing());
        antialiasingComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeAntialiasing((String) e.getItem());
            }
        });
        innerPanel.add(antialiasingComboBox, "span");

        innerPanel.add(new JLabel("Color Rendering"));
        colorRenderingComboBox.setSelectedItem(model.getColorRendering());
        colorRenderingComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeColorRendering((String) e.getItem());
            }
        });
        innerPanel.add(colorRenderingComboBox, "span");

        innerPanel.add(new JLabel("Dithering"));
        ditheringComboBox.setSelectedItem(model.getDithering());
        ditheringComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeDithering((String) e.getItem());
            }
        });
        innerPanel.add(ditheringComboBox, "span");

        innerPanel.add(new JLabel("Interpolation"));
        interpolationComboBox.setSelectedItem(model.getInterpolation());
        interpolationComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeInterpolation((String) e.getItem());
            }
        });
        innerPanel.add(interpolationComboBox, "span");

        innerPanel.add(new JLabel("Rendering"));
        renderingComboBox.setSelectedItem(model.getRendering());
        renderingComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeRendering((String) e.getItem());
            }
        });
        innerPanel.add(renderingComboBox, "span");

        innerPanel.add(new JLabel("View Width"));
        innerPanel.add(new JTextField(model.getViewWidth()), "span");

        innerPanel.add(new JLabel("View Height"));
        innerPanel.add(new JTextField(model.getViewHeight()), "span");

        innerPanel.add(new JLabel("Windowed"));
        innerPanel.add(new JTextField(String.valueOf(model.isWindowed())), "span");

        var button = new JButton("Save");
        button.addActionListener(e -> {
            controller.saveSettings();
            controller.displayMainMenuView(this);
        });
        innerPanel.add(button);

        button = new JButton("Cancel");
        button.addActionListener(e -> {
            controller.displayMainMenuView(this);
        });
        innerPanel.add(button);
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "AlphaInterpolation": {
                alphaInterpolationComboBox.setSelectedItem(event.getNewValue());
                break;
            }
            case "Antialiasing": {
                antialiasingComboBox.setSelectedItem(event.getNewValue());
                break;
            }
            case "ColorRendering": {
                colorRenderingComboBox.setSelectedItem(event.getNewValue());
                break;
            }
            case "Dithering": {
                ditheringComboBox.setSelectedItem(event.getNewValue());
                break;
            }
            case "Interpolation": {
                interpolationComboBox.setSelectedItem(event.getNewValue());
                break;
            }
            case "Rendering": {
                renderingComboBox.setSelectedItem(event.getNewValue());
                break;
            }
        }
    }
}
