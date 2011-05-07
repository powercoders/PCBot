package org.rsbot.script.wrappers;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.web.WebTile;

/**
 * A path consisting of a list of tile waypoints.
 *
 * @author Timer
 */
public class RSWeb extends MethodProvider {
	public RSWeb(MethodContext ctx, WebTile[] tiles) {
		super(ctx);
	}
}