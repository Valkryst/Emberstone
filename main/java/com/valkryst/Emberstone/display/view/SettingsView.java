package com.valkryst.Emberstone.display.view;

import com.valkryst.Emberstone.display.controller.SettingsController;
import com.valkryst.Emberstone.display.model.MainMenuModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SettingsView extends View<SettingsController> {
    private final JComboBox<String> alphaInterpolationComboBox = new JComboBox<>(new String[]{ "Auto", "Quality", "Speed" });
    private final JComboBox<String> antialiasingComboBox = new JComboBox<>(new String[] { "Auto", "On", "Off" });
    private final JComboBox<String> colorRenderingComboBox = new JComboBox<>(new String[]{ "Auto", "Quality", "Speed" });
    private final JComboBox<String> ditheringComboBox = new JComboBox<>(new String[] { "Auto", "Enable", "Disable" });
    private final JComboBox<String> interpolationComboBox = new JComboBox<>(new String[] { "Bicubic", "Bilinear", "Nearest Neighbor" });
    private final JComboBox<String> renderingComboBox = new JComboBox<>(new String[]{ "Auto", "Quality", "Speed" });
	private final JTextField viewHeightTextField = new JTextField();
	private final JTextField viewWidthTextField = new JTextField();
	private final JComboBox<Boolean> windowedComboBox = new JComboBox<>(new Boolean[]{ true, false});

    public SettingsView(final SettingsController controller) {
		super(controller);

        this.setLayout(new GridBagLayout());

        final var innerPanel = new JPanel(new GridLayout(0, 2));
        innerPanel.setPreferredSize(new Dimension(512, 512));
        this.add(innerPanel);

        innerPanel.add(new JLabel("Alpha Interpolation"));
        alphaInterpolationComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeAlphaInterpolation((String) e.getItem());
            }
        });
        innerPanel.add(alphaInterpolationComboBox, "span");

        innerPanel.add(new JLabel("Antialiasing"));
        antialiasingComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeAntialiasing((String) e.getItem());
            }
        });
        innerPanel.add(antialiasingComboBox, "span");

        innerPanel.add(new JLabel("Color Rendering"));
        colorRenderingComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeColorRendering((String) e.getItem());
            }
        });
        innerPanel.add(colorRenderingComboBox, "span");

        innerPanel.add(new JLabel("Dithering"));
        ditheringComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeDithering((String) e.getItem());
            }
        });
        innerPanel.add(ditheringComboBox, "span");

        innerPanel.add(new JLabel("Interpolation"));
        interpolationComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeInterpolation((String) e.getItem());
            }
        });
        innerPanel.add(interpolationComboBox, "span");

        innerPanel.add(new JLabel("Rendering"));
        renderingComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.changeRendering((String) e.getItem());
            }
        });
        innerPanel.add(renderingComboBox, "span");

        innerPanel.add(new JLabel("View Width"));
        innerPanel.add(viewHeightTextField, "span");

        innerPanel.add(new JLabel("View Height"));
        innerPanel.add(viewWidthTextField, "span");

        innerPanel.add(new JLabel("Windowed"));
		windowedComboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				controller.changeWindowed((Boolean) e.getItem());
			}
		});
        innerPanel.add(windowedComboBox, "span");

        var button = new JButton("Save");
        button.addActionListener(e -> {
            controller.saveSettings();
			controller.setContentPane(MainMenuModel.class);
        });
        innerPanel.add(button);

        button = new JButton("Cancel");
        button.addActionListener(e -> {
			controller.setContentPane(MainMenuModel.class);
        });
        innerPanel.add(button);
    }

	public void setAlphaInterpolation(final String value) {
		alphaInterpolationComboBox.setSelectedItem(value);
	}

	public void setAntialiasing(final String value) {
		antialiasingComboBox.setSelectedItem(value);
	}

	public void setColorRendering(final String value) {
		colorRenderingComboBox.setSelectedItem(value);
	}

	public void setDithering(final String value) {
		ditheringComboBox.setSelectedItem(value);
	}

	public void setInterpolation(final String value) {
		interpolationComboBox.setSelectedItem(value);
	}

	public void setRendering(final String value) {
		renderingComboBox.setSelectedItem(value);
	}

	public void setViewHeight(final int value) {
		viewHeightTextField.setText(String.valueOf(value));
	}

	public void setViewWidth(final int value) {
		viewWidthTextField.setText(String.valueOf(value));
	}

	public void setWindowed(final boolean windowed) {
		windowedComboBox.setSelectedItem(windowed);
	}
}
