package org.rsbot.script.internal.reflection;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.rsbot.bot.Bot;

public class Hooks {
	private static final HashMap<String, Hook> hooks = new HashMap<String, Hook>();

	/**
	 * Adds a hook to the map.
	 * 
	 * @param hook
	 *            The hook to add.
	 */
	public static void add(final Hook hook) {
		if (!isValid(hook.getHook())) {
			Hooks.hooks.put(hook.getHook(), hook);
		}
	}

	/**
	 * Checks if the hook exists in the map yet.
	 * 
	 * @param hookName
	 *            The hook's name.
	 * @return <tt>true</tt> if it exists, otherwise <tt>false</tt>.
	 */
	public static boolean isValid(final String hookName) {
		return Hooks.hooks.containsKey(hookName);
	}

	private final Bot bot;

	public Hooks(final Bot bot) {
		this.bot = bot;
	}

	/**
	 * Gets the hook field.
	 * 
	 * @param hookName
	 *            The name of the hook.
	 * @return The field from the class.
	 */
	private Field getField(final String hookName) {
		if (hookName == null || !isValid(hookName)) {
			return null;
		}
		final Hook hook = Hooks.hooks.get(hookName);
		if (hook == null) {
			return null;
		}
		return getField(hook.getParentClass(), hook.getField());
	}

	/**
	 * Gets the hook's field.
	 * 
	 * @param className
	 *            The class.
	 * @param fieldName
	 *            The field.
	 * @return The field from the class.
	 */
	private Field getField(final String className, final String fieldName) {
		try {
			if (className == null || fieldName == null) {
				return null;
			}
			final Class<?> clazz = bot.getLoader().getClassLoader().loadClass(className);
			final Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets the Object value of an object in (a) class[es].
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The object's name.
	 * @return The object.
	 */
	Object getValue(final Object parent, final String hookName) {
		final Field field = getField(hookName);
		if (field == null) {
			return null;
		}
		field.setAccessible(true);
		try {
			return field.get(parent);
		} catch (final Exception e) {
			return null;
		}
	}
}
