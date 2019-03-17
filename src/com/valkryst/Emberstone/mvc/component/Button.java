package com.valkryst.Emberstone.mvc.component;

import com.valkryst.Emberstone.Game;
import com.valkryst.V2DSprite.Sprite;
import com.valkryst.V2DSprite.SpriteSheet;
import lombok.Getter;
import lombok.NonNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Button extends Component {
    /** Current state of the button. */
    @Getter private ButtonState state = ButtonState.NORMAL;

    /** The sprite sheet containing the visual representations of each of the button's states. */
    private final SpriteSheet spriteSheet;

    /** Functions to run when the button's state changes. */
    private final HashMap<ButtonState, List<Runnable>> stateChangeFunctions = new HashMap<>();

    /**
     * Constructs a new Button.
     *
     * @param position
     *          Location of the button within its parent.
     *
     * @param spriteSheet
     *          The sprite sheet containing the visual representations of each of the button's states.
     */
    public Button(final @NonNull Point position, final @NonNull SpriteSheet spriteSheet) {
        super(position);
        this.spriteSheet = spriteSheet;
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
        spriteSheet.getSprite(state.name()).draw(gc, super.position.x, super.position.y);
    }

    @Override
    public boolean intersects(final Point point) {
        if (point == null) {
            return false;
        }

        final Sprite sprite = spriteSheet.getSprite(state.name());

        if (sprite == null) {
            return false;
        }

        final Rectangle spriteBoundingBox = sprite.getBoundingBox("Default");

        if (spriteBoundingBox == null) {
            return false;
        }

        spriteBoundingBox.x += super.getX();
        spriteBoundingBox.y += super.getY();

        return new Rectangle(point.x, point.y, 1, 1).intersects(spriteBoundingBox);
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

        final List<Runnable> runnables = stateChangeFunctions.get(state);

        if (runnables == null || runnables.size() == 0) {
            return;
        }

        for (final Runnable runnable : runnables) {
            runnable.run();
        }
    }
}
