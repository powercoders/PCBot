package org.rsbot.script.internal.reflection;

import org.rsbot.bot.Bot;

import java.awt.*;

public class Reflection {
	private final Hooks hooks;

	public Reflection(Bot bot) {
		hooks = new Hooks(bot);
	}

	/**
	 * Gets an integer from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The integer.
	 */
	public int invokeInt(Object parent, String hookName) {
		try {
			return (Integer) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Gets long from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The long.
	 */
	public long invokeLong(Object parent, String hookName) {
		try {
			return (Long) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Gets a float from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The float.
	 */
	public float invokeFloat(Object parent, String hookName) {
		try {
			return (Float) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Gets an integer array from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The integer array.
	 */
	public int[] invokeIntArray(Object parent, String hookName) {
		try {
			return (int[]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets an integer[][] from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The integer[][].
	 */
	public int[][] invokeInt2DArray(Object parent, String hookName) {
		try {
			return (int[][]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a string from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The string.
	 */
	public String invokeString(Object parent, String hookName) {
		try {
			return (String) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return "null";
		}
	}

	/**
	 * Gets a string array from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The string array.
	 */
	public String[] invokeStringArray(Object parent, String hookName) {
		try {
			return (String[]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a short from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The short.
	 */
	public short invokeShort(Object parent, String hookName) {
		try {
			return (Short) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Gets a short array from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The short.
	 */
	public short[] invokeShortArray(Object parent, String hookName) {
		try {
			return (short[]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a byte from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The byte.
	 */
	public byte invokeByte(Object parent, String hookName) {
		try {
			return (Byte) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Gets a boolean from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The boolean.
	 */
	public boolean invokeBoolean(Object parent, String hookName) {
		try {
			return (Boolean) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Gets a boolean array from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The boolean array.
	 */
	public boolean[] invokeBooleanArray(Object parent, String hookName) {
		try {
			return (boolean[]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets an object from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The object.
	 */
	public Object invokeObject(Object parent, String hookName) {
		try {
			return hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets an object array from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The object array.
	 */
	public Object[] invokeObjectArray(Object parent, String hookName) {
		try {
			return (Object[]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets an object[][] from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The object[][].
	 */
	public Object[][] invokeObject2DArray(Object parent, String hookName) {
		try {
			return (Object[][]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets an object[][][] from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The object[][][].
	 */
	public Object[][][] invokeObject3DArray(Object parent, String hookName) {
		try {
			return (Object[][][]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a rectangle array from the runescape client.
	 *
	 * @param parent   The parent class.
	 * @param hookName The name of the field.
	 * @return The rectange array.
	 */
	public Rectangle[] invokeRectangleArray(Object parent, String hookName) {
		try {
			return (Rectangle[]) hooks.getValue(parent, hookName);
		} catch (Exception e) {
			return null;
		}
	}
}