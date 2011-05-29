package org.rsbot.script.web;

import org.rsbot.script.wrappers.RSTile;

/**
 * An activity base of functions.
 * 
 * @author Timer
 */
public interface Transportation {
	double getDistance(final RSTile destination);

	boolean isApplicable(final RSTile base, final RSTile destination);

	boolean perform();
}
