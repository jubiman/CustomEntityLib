package com.jubiman.customentitylib.api;

import necesse.engine.save.SaveData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class CustomDataHandler<I, T extends CustomData> {
	protected final HashMap<I, T> userMap = new HashMap<>();
	protected final Constructor<T> ctor;
	/**
	 * The name of the handler (usually tied to the mod name)
	 */
	public final String handlerName;
	public CustomDataHandler(Class<T> clazz, Class<?>[] parameterTypes, String handlerName) {
		this.handlerName = handlerName;
		try {
			this.ctor = clazz.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * A null safe way to get a player from the map, adds player if they don't exist yet
	 * @param identifier the identifier of the CustomData object
	 * @return the object belonging to the player
	 */
	public T get(I identifier) {
		try {
			if (!userMap.containsKey(identifier))
				userMap.put(identifier, ctor.newInstance(identifier));
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) { // should only happen when people develop
			throw new RuntimeException(e);
		}
		return userMap.get(identifier);
	}

	/**
	 * Save player's data.
	 * @param saveData the parent save object (usually ServerClient)
	 * @param identifier the identifier of the CustomData object to save
	 */
	public void save(SaveData saveData, I identifier) {
		T p = get(identifier);
		if (!(p instanceof Savable)) return;

		SaveData save = new SaveData(this.handlerName);
		((Savable) p).addSaveData(save);
		saveData.addSaveData(save);
	}
}
