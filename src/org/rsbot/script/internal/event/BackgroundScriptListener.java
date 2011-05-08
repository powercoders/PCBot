package org.rsbot.script.internal.event;

import org.rsbot.script.BackgroundScript;
import org.rsbot.script.internal.BackgroundScriptHandler;

/**
 * A listener for a background script.
 *
 * @author Timer
 */
public interface BackgroundScriptListener {
	public void scriptStarted(BackgroundScriptHandler handler, BackgroundScript script);

	public void scriptStopped(BackgroundScriptHandler handler, BackgroundScript script);
}
