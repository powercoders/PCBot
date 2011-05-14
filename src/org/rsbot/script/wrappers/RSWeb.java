package org.rsbot.script.wrappers;

import org.rsbot.script.web.Route;
import org.rsbot.script.methods.*;

import java.util.LinkedList;

/**
 * A transportation action consisting of a list of routes.
 *
 * @author Timer
 */
public class RSWeb extends MethodProvider {
	private final LinkedList<Route> routes = new LinkedList<Route>();

	public RSWeb(final MethodContext ctx, final Route[] routes) {
		super(ctx);
		for (final Route route : routes) {
			this.routes.addLast(route);
		}
	}

	public Route[] getRoutes() {
		return routes.toArray(new Route[routes.size()]);
	}

	public boolean step() {
		if (routes.size() > 0) {
			Route route = routes.poll();
			return route.execute();
		}
		final Route[] routes = methods.web.getWeb(getStart(), getDestination()).getRoutes();
		if (routes != null) {
			this.routes.clear();
			for (final Route route : routes) {
				this.routes.addLast(route);
			}
		}
		return finished();
	}

	public boolean finished() {
		return routes.size() == 0;
	}

	public RSTile getDestination() {
		return routes.getLast().getDestination();
	}

	public RSTile getStart() {
		return routes.getFirst().getStart();
	}
}