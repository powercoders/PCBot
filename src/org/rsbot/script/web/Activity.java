package org.rsbot.script.web;

/**
 * An activity base of functions.
 */
public interface Activity {
	boolean isApplicable();

	boolean preform();
}