package org.rsbot.script.internal.reflection;

import org.rsbot.bot.Bot;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Hooks {
	private static final HashMap<String, Hook> hooks = new HashMap<String, Hook>();
	private final Bot bot;

	public Hooks(Bot bot) {
		this.bot = bot;
	}

	/**
	 * Gets the Object value of an object in (a) class[es].
	 *
	 * @param parent   The parent class.
	 * @param hookName The object's name.
	 * @return The object.
	 */
	Object getValue(final Object parent, final String hookName) {
		Field field = getField(hookName);
		if (field == null) {
			return null;
		}
		field.setAccessible(true);
		try {
			return field.get(parent);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Checks if the hook exists in the map yet.
	 *
	 * @param hookName The hook's name.
	 * @return <tt>true</tt> if it exists, otherwise <tt>false</tt>.
	 */
	public static boolean isValid(final String hookName) {
		return Hooks.hooks.containsKey(hookName);
	}

	/**
	 * Adds a hook to the map.
	 *
	 * @param hook The hook to add.
	 */
	public static void add(final Hook hook) {
		if (!isValid(hook.getHook())) {
			Hooks.hooks.put(hook.getHook(), hook);
		}
	}

	/**
	 * Gets the hook's field.
	 *
	 * @param className The class.
	 * @param fieldName The field.
	 * @return The field from the class.
	 */
	private Field getField(final String className, final String fieldName) {
		try {
			if (className == null || fieldName == null) {
				return null;
			}
			Class<?> clazz = this.bot.getLoader().getClassLoader().loadClass(className);
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the hook field.
	 *
	 * @param hookName The name of the hook.
	 * @return The field from the class.
	 */
	private Field getField(final String hookName) {
		if (hookName == null || !isValid(hookName)) {
			return null;
		}
		Hook hook = Hooks.hooks.get(hookName);
		if (hook == null) {
			return null;
		}
		return getField(hook.getParentClass(), hook.getField());
	}
}