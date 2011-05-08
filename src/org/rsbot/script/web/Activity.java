package org.rsbot.script.web;

/**
 * An activity base of functions.
 *
 * @author Timer
 */
public interface Activity {
	boolean isApplicable();

	boolean preform();
}