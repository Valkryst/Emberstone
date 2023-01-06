package com.valkryst.Emberstone.display.controller;

import com.valkryst.Emberstone.display.Display;
import com.valkryst.Emberstone.display.model.Model;
import lombok.NonNull;

import java.lang.reflect.InvocationTargetException;

public abstract class Controller<M extends Model> {
	final M model;

	public Controller(final @NonNull M model) {
		this.model = model;
	}

	public void setContentPane(final Class<? extends Model> modelClass, final Object ... modelArgs) {
		// Handle models using the singleton pattern.
		try {
			final var method = modelClass.getDeclaredMethod("getInstance");
			final var model = modelClass.cast(method.invoke(null, modelArgs));
			final var view = model.createView();
			Display.getInstance().setContentPane(view);
			return;
		} catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}

		// Handle models using a public constructor.
		try {
			final Class<?>[] modelArgTypes = new Class[modelArgs.length];
			for (int i = 0 ; i < modelArgs.length ; i++) {
				modelArgTypes[i] = modelArgs[i].getClass();
			}

			final var model = modelClass.getDeclaredConstructor(modelArgTypes).newInstance(modelArgs);
			final var view = model.createView();
			Display.getInstance().setContentPane(view);
		} catch (final NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
