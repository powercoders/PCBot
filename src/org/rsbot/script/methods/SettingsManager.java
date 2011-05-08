package org.rsbot.script.methods;

import org.rsbot.script.util.SettingsManager;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.methods.MethodContext;

/**
* @author icnhzabot
*/

public class SettingsManager extends MethodProvider {

	private final MethodContext ctx;

	public SettingsManager(final MethodContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}
	
	public org.rsbot.script.util.SettingsManager getManager(String scriptname) {
		return new org.rsbot.script.util.SettingsManager(ctx, scriptname);
	}

}