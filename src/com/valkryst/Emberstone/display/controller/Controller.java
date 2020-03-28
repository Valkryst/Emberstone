package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.Display;
import com.valkryst.Emberstone.display.model.Model;
import com.valkryst.Emberstone.display.view.View;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class Controller implements PropertyChangeListener {
    /** The views. */
    private final List<View> views = new ArrayList<>();
    /** The models. */
    private final List<Model> models = new ArrayList<>();

    /**
     * Adds a view to the controller.
     *
     * @param view
     *          The view to add.
     */
    public void addView(final View view) {
        if (view != null) {
            views.add(view);
            Display.getInstance().addView(view);
        }
    }

    /**
     * Removes a view from the controller.
     *
     * @param view
     *          The view to remove.
     */
    public void removeView(final View view) {
        if (view != null) {
            views.remove(view);
            Display.getInstance().removeView(view);
        }
    }

    /**
     * Adds a model to the controller.
     *
     * @param model
     *          The model to add.
     */
    public void addModel(final Model model) {
        if (model != null) {
            models.add(model);
            model.addPropertyChangeListener(this);
        }
    }

    /**
     * Removes a model from the controller.
     *
     * @param model
     *          The model to remove.
     */
    public void removeModel(final Model model) {
        if (model != null) {
            models.remove(model);
            model.removePropertyChangeListener(this);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        for (final View view : views) {
            view.modelPropertyChange(event);
        }
    }

    /**
     * This is a convenience method that subclasses can call upon to fire
     * property changes back to the models. This method uses reflection to
     * inspect each of the model classes to determine whether it's the owner of
     * the property in question. If it isn't, a NoSuchMethodException is thrown,
     * which the method ignores.
     *
     * @param propertyName
     *          Name of the property.
     *
     * @param newValue
     *          An object that represents the property's new value.
     */
    protected void setModelProperty(final String propertyName, final Object newValue) {
        for (final Model model : models) {
            try {
                final var methodName = "set" + propertyName;
                final var method = model.getClass().getMethod(methodName, newValue.getClass());

                method.invoke(model, newValue);
            } catch (final NoSuchMethodException ignored) {
                /*
                 * We're iterating over the models, trying to find the model
                 * that has the 'setPropertyName' method, so this could be
                 * thrown a few times.
                 *
                 * Because this is intended behaviour, we ignore the exception.
                 */
            } catch (final IllegalAccessException e) {
                /*
                 * If we found the model with the 'setPropertyName' method,
                 * this can be thrown if the model is enforcing the Java
                 * language access control and the 'setPropertyName' method is
                 * inaccessible.
                 */
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                // if the underlying method throws an exception.
                /*
                 * If we found the model with the 'setPropertyName' method, this
                 * can be thrown if the 'setPropertyName' method throws an
                 * exception.
                 */
                e.printStackTrace();
            }
        }
    }
}
