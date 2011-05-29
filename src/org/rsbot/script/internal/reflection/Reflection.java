package org.rsbot.script.internal.reflection;

import java.awt.Rectangle;

import org.rsbot.bot.Bot;

public class Reflection {
	private final Hooks hooks;

	public Reflection(final Bot bot) {
		hooks = new Hooks(bot);
	}

	/**
	 * Gets a boolean from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The boolean.
	 */
	public boolean invokeBoolean(final Object parent, final String hookName) {
		try {
			return (Boolean) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Gets a boolean array from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The boolean array.
	 */
	public boolean[] invokeBooleanArray(final Object parent,
			final String hookName) {
		try {
			return (boolean[]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets a byte from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The byte.
	 */
	public byte invokeByte(final Object parent, final String hookName) {
		try {
			return (Byte) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return -1;
		}
	}

	/**
	 * Gets a float from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The float.
	 */
	public float invokeFloat(final Object parent, final String hookName) {
		try {
			return (Float) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return -1;
		}
	}

	/**
	 * Gets an integer from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The integer.
	 */
	public int invokeInt(final Object parent, final String hookName) {
		try {
			return (Integer) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return -1;
		}
	}

	/**
	 * Gets an integer[][] from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The integer[][].
	 */
	public int[][] invokeInt2DArray(final Object parent, final String hookName) {
		try {
			return (int[][]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets an integer array from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The integer array.
	 */
	public int[] invokeIntArray(final Object parent, final String hookName) {
		try {
			return (int[]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets long from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The long.
	 */
	public long invokeLong(final Object parent, final String hookName) {
		try {
			return (Long) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return -1;
		}
	}

	/**
	 * Gets an object from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The object.
	 */
	public Object invokeObject(final Object parent, final String hookName) {
		try {
			return hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets an object[][] from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The object[][].
	 */
	public Object[][] invokeObject2DArray(final Object parent,
			final String hookName) {
		try {
			return (Object[][]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets an object[][][] from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The object[][][].
	 */
	public Object[][][] invokeObject3DArray(final Object parent,
			final String hookName) {
		try {
			return (Object[][][]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets an object array from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The object array.
	 */
	public Object[] invokeObjectArray(final Object parent, final String hookName) {
		try {
			return (Object[]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets a rectangle array from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The rectange array.
	 */
	public Rectangle[] invokeRectangleArray(final Object parent,
			final String hookName) {
		try {
			return (Rectangle[]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets a short from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The short.
	 */
	public short invokeShort(final Object parent, final String hookName) {
		try {
			return (Short) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return -1;
		}
	}

	/**
	 * Gets a short array from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The short.
	 */
	public short[] invokeShortArray(final Object parent, final String hookName) {
		try {
			return (short[]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets a string from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The string.
	 */
	public String invokeString(final Object parent, final String hookName) {
		try {
			return (String) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return "null";
		}
	}

	/**
	 * Gets a string array from the runescape client.
	 * 
	 * @param parent
	 *            The parent class.
	 * @param hookName
	 *            The name of the field.
	 * @return The string array.
	 */
	public String[] invokeStringArray(final Object parent, final String hookName) {
		try {
			return (String[]) hooks.getValue(parent, hookName);
		} catch (final Exception e) {
			return null;
		}
	}
}
