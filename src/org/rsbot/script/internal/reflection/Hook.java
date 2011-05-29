package org.rsbot.script.internal.reflection;

public class Hook {
	private final String hookName, className, fieldName;

	public Hook(final String hookName, final String className,
			final String fieldName) {
		this.hookName = hookName;
		this.className = className;
		this.fieldName = fieldName;
	}

	/**
	 * Gets the field name.
	 * 
	 * @return The field name.
	 */
	public String getField() {
		return fieldName;
	}

	/**
	 * Gets the name of the hook.
	 * 
	 * @return The name of the hook.
	 */
	public String getHook() {
		return hookName;
	}

	/**
	 * Gets the class name.
	 * 
	 * @return The class name.
	 */
	public String getParentClass() {
		return className;
	}
}
