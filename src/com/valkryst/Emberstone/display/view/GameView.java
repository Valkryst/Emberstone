package com.valkryst.Emberstone.display.view;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.valkryst.Emberstone.component.PositionComponent;
import com.valkryst.Emberstone.display.controller.GameController;
import com.valkryst.Emberstone.display.model.SettingsModel;
import com.valkryst.Emberstone.display.renderer.Renderer;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;

public class GameView extends View {
    private Engine engine;
    private Renderer renderer;

    public GameView(final GameController controller) {
        engine = controller.getEngine();

        // Because we're specifically using active rendering with a custom
        // buffer strategy for the GameView, we can ignore repaint events sent
        // by the OS.
        this.setIgnoreRepaint(true);

        /*
         * We set up the renderer after the view has been added to a parent
         * component. If we try to set it up before the view has been added
         * to a parent, then the renderer will fail to initialize.
         */
        final var view = this;
        this.addHierarchyListener(new HierarchyListener() {
            @SneakyThrows
            @Override
            public void hierarchyChanged(final HierarchyEvent e) {
                final var parentChanged = (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0;
                if (!parentChanged) {
                    return;
                }

                final Field peerField = Component.class.getDeclaredField("peer");
                peerField.setAccessible(true);
                final var peer = peerField.get(view);

                final var renderClass = SettingsModel.getInstance().getRenderer();
                final var renderConstructor = renderClass.getDeclaredConstructor(ComponentPeer.class);
                renderer = renderClass.cast(renderConstructor.newInstance(peer));

                controller.startGame(view);

                view.removeHierarchyListener(this);
            }
        });
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent event) {

    }

    public void render() {
        do {
            final var gc = renderer.getBufferGraphics2D();

            // Ensure only the visible portions of the canvas are drawn on.
            gc.setClip(0, 0, this.getWidth(), this.getHeight());

            gc.setColor(Color.BLACK);
            gc.fillRect(0, 0, this.getWidth(), this.getHeight());


            final var positions = ComponentMapper.getFor(PositionComponent.class);
            gc.setColor(Color.RED);
            engine.getEntities().forEach(e -> {
                final var position = positions.get(e);
                gc.fillRect((int) position.getX(), (int) position.getY(), 32, 32);
            });
        } while(renderer.bufferContentsLost());

        renderer.blit();
    }
}
