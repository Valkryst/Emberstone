package com.valkryst.Emberstone.mvc.component;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import lombok.Getter;
import lombok.NonNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RadioButton extends Component {
    /** Current state of the radio button. */
    @Getter private ButtonState state = ButtonState.NORMAL;

    private final Label label;

    private final Dimension dimensions;

    /** Functions to run when the button's state changes. */
    private final HashMap<ButtonState, List<Runnable>> stateChangeFunctions = new HashMap<>();

    @Getter private boolean isChecked = false;

    /** The radio button group that the radio button belongs to. */
    @Getter private RadioButtonGroup group;

    private Color color;

    public RadioButton(final @NonNull Point position, final @NonNull String text, final Color color, final Dimension dimensions, final RadioButtonGroup group) {
        super(position);
        this.color = color;
        this.label = new Label(position, "}" + text, 32, color);
        this.dimensions = dimensions;
        this.group = group;
        group.addRadioButton(this);
    }

    @Override
    protected void createEventListeners() {
        super.createEventListeners();

        final Game game = Game.getInstance();

        final MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent e) {}

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (state == ButtonState.PRESSED) {
                        return;
                    }

                    if (intersects(game.getMousePosition())) {
                        setState(ButtonState.PRESSED);
                    }
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    switch (state) {
                        case NORMAL:
                        case HOVERED: {
                            return;
                        }
                        case PRESSED: {
                            setState(ButtonState.RELEASED);
                            break;
                        }
                    }

                    if (intersects(game.getMousePosition())) {
                        setState(ButtonState.HOVERED);
                    } else {
                        setState(ButtonState.NORMAL);
                    }
                }
            }

            @Override
            public void mouseEntered(final MouseEvent e) {}

            @Override
            public void mouseExited(final MouseEvent e) {}
        };

        final MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(final MouseEvent e) {}

            @Override
            public void mouseMoved(final MouseEvent e) {
                if (intersects(game.getMousePosition())) {
                    setState(ButtonState.HOVERED);
                } else {
                    setState(ButtonState.NORMAL);
                }
            }
        };

        super.addEventListener(mouseListener);
        super.addEventListener(mouseMotionListener);
    }

    @Override
    public void draw(final Graphics2D gc) {
        label.draw(gc);

        if (Settings.getInstance().isDebugModeOn()) {
            gc.drawRect(label.getX(), label.getY() - dimensions.height, dimensions.width, dimensions.height);
        }
    }

    @Override
    public boolean intersects(final Point point) {
        if (point == null) {
            return false;
        }

        return new Rectangle(point.x, point.y, 1, 1).intersects(new Rectangle(super.position.x, super.position.y - dimensions.height, dimensions.width, dimensions.height));
    }

    /**
     * Adds a state change function, to be run when the button's state changes.
     *
     * @param state
     *          The state which when entered will run the function.
     *
     * @param runnable
     *          The function to run.
     */
    public void addStateChangeFunction(final ButtonState state, final Runnable runnable) {
        stateChangeFunctions.putIfAbsent(state, new LinkedList<>());
        stateChangeFunctions.get(state).add(runnable);
    }

    /**
     * Changes the button's state.
     *
     * @param state
     *          The new state.
     */
    public void setState(final ButtonState state) {
        this.state = state;

        switch (state) {
            case NORMAL:
            case RELEASED:{
                label.setColor(color);
                break;
            }
            case HOVERED: {
                label.setColor(tint(color, 0.2));
                break;
            }
            case PRESSED: {
                label.setColor(shade(color, 0.2));
                group.setCheckedButton(this);
                break;
            }
        }


        final List<Runnable> runnables = stateChangeFunctions.get(state);

        if (runnables == null || runnables.size() == 0) {
            return;
        }

        for (final Runnable runnable : runnables) {
            runnable.run();
        }
    }

    public void setChecked(final boolean ischecked) {
        label.setText((ischecked ? "{" : "}") + label.getText().substring(1));
    }


    /**
     * Shades a color by some factor, where a higher factor results in a darker
     * shade.
     *
     * @param color
     *        The color.
     *
     * @param shadeFactor
     *        The factor.
     *
     *        Values should range from 0.0 to 1.0.
     *
     * @return
     *        The shaded color.
     *
     * @throws NullPointerException
     *         If the color is null.
     */
    public static Color shade(final @NonNull Color color, double shadeFactor) {
        if (shadeFactor > 1.0) {
            shadeFactor = 1.0;
        }

        if (shadeFactor < 0.0) {
            shadeFactor = 0.0;
        }

        final int a = color.getAlpha();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        r *= (1 - shadeFactor);
        g *= (1 - shadeFactor);
        b *= (1 - shadeFactor);

        return new Color(r, g, b, a);
    }

    /**
     * Tints a color by some factor, where a higher factor results in a lighter
     * tint.
     *
     * @param color
     *        the color.
     *
     * @param tintFactor
     *        The factor.
     *
     *        Values should range from 0.0 to 1.0.
     *
     * @return
     *        The tinted color.
     *
     * @throws NullPointerException
     *         If the color is null.
     */
    public static Color tint(final @NonNull Color color, double tintFactor) {
        if (tintFactor > 1.0) {
            tintFactor = 1.0;
        }

        if (tintFactor < 0.0) {
            tintFactor = 0.0;
        }

        final int a = color.getAlpha();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        r += (255 - r) * tintFactor;
        g += (255 - g) * tintFactor;
        b += (255 - b) * tintFactor;

        return new Color(r, g, b, a);
    }
}
