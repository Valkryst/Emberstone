package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.display.controller.SettingsController;
import com.valkryst.Emberstone.display.model.SettingsModel;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;

public class SettingsView extends View {
    private final Choice alphaInterpolationChoice = new Choice();
    private final Choice antialiasingChoice = new Choice();
    private final Choice colorRenderingChoice = new Choice();
    private final Choice ditheringChoice = new Choice();
    private final Choice interpolationChoice = new Choice();
    private final Choice renderingChoice = new Choice();
    private final Choice rendererChoice = new Choice();

    public SettingsView(final SettingsController controller) {
        final var model = SettingsModel.getInstance();

        controller.addModel(model);

        this.setLayout(new GridBagLayout());
        this.setBackground(Color.WHITE);

        final var innerPanel = new Panel(new GridLayout(0, 2));
        innerPanel.setPreferredSize(new Dimension(512, 512));
        this.add(innerPanel);

        innerPanel.add(new Label("Alpha Interpolation"));
        alphaInterpolationChoice.add("Auto");
        alphaInterpolationChoice.add("Quality");
        alphaInterpolationChoice.add("Speed");
        alphaInterpolationChoice.select(model.getAlphaInterpolation());
        alphaInterpolationChoice.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeAlphaInterpolation((String) e.getItem());
            }
        });
        innerPanel.add(alphaInterpolationChoice, "span");

        innerPanel.add(new Label("Antialiasing"));
        antialiasingChoice.add("Auto");
        antialiasingChoice.add("On");
        antialiasingChoice.add("Off");
        antialiasingChoice.select(model.getAntialiasing());
        antialiasingChoice.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeAntialiasing((String) e.getItem());
            }
        });
        innerPanel.add(antialiasingChoice, "span");

        innerPanel.add(new Label("Color Rendering"));
        colorRenderingChoice.add("Auto");
        colorRenderingChoice.add("Quality");
        colorRenderingChoice.add("Speed");
        colorRenderingChoice.select(model.getColorRendering());
        colorRenderingChoice.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeColorRendering((String) e.getItem());
            }
        });
        innerPanel.add(colorRenderingChoice, "span");

        innerPanel.add(new Label("Dithering"));
        ditheringChoice.add("Auto");
        ditheringChoice.add("Enable");
        ditheringChoice.add("Disable");
        ditheringChoice.select(model.getDithering());
        ditheringChoice.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeDithering((String) e.getItem());
            }
        });
        innerPanel.add(ditheringChoice, "span");

        innerPanel.add(new Label("Interpolation"));
        interpolationChoice.add("Bicubic");
        interpolationChoice.add("Bilinear");
        interpolationChoice.add("Nearest Neighbor");
        interpolationChoice.select(model.getInterpolation());
        interpolationChoice.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeInterpolation((String) e.getItem());
            }
        });
        innerPanel.add(interpolationChoice, "span");

        innerPanel.add(new Label("Rendering"));
        renderingChoice.add("Auto");
        renderingChoice.add("Quality");
        renderingChoice.add("Speed");
        renderingChoice.select(model.getRendering());
        renderingChoice.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeRendering((String) e.getItem());
            }
        });
        innerPanel.add(renderingChoice, "span");

        innerPanel.add(new Label("Renderer"));
        model.getSupportedRendererNames().forEach(rendererChoice::add);
        rendererChoice.select(model.getRendererName());
        rendererChoice.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeRenderer((String) e.getItem());
            }
        });
        innerPanel.add(rendererChoice, "span");

        innerPanel.add(new Label("View Width"));
        innerPanel.add(new TextField(model.getViewWidth()), "span");

        innerPanel.add(new Label("View Height"));
        innerPanel.add(new TextField(model.getViewHeight()), "span");

        innerPanel.add(new Label("Windowed"));
        innerPanel.add(new TextField(String.valueOf(model.isWindowed())), "span");

        var button = new Button("Save");
        button.addActionListener(e -> {
            controller.saveSettings();
            controller.displayMainMenuView(this);
        });
        innerPanel.add(button);

        button = new Button("Cancel");
        button.addActionListener(e -> {
            controller.displayMainMenuView(this);
        });
        innerPanel.add(button);
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "AlphaInterpolation": {
                alphaInterpolationChoice.select((String) event.getNewValue());
                break;
            }
            case "Antialiasing": {
                antialiasingChoice.select((String) event.getNewValue());
                break;
            }
            case "ColorRendering": {
                colorRenderingChoice.select((String) event.getNewValue());
                break;
            }
            case "Dithering": {
                ditheringChoice.select((String) event.getNewValue());
                break;
            }
            case "Interpolation": {
                interpolationChoice.select((String) event.getNewValue());
                break;
            }
            case "Rendering": {
                renderingChoice.select((String) event.getNewValue());
                break;
            }
            case "Renderer": {
                rendererChoice.select((String) event.getNewValue());
                break;
            }
        }
    }
}
