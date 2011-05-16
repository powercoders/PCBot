package org.rsbot.script.wrappers;

import org.rsbot.script.web.Route;

import java.util.LinkedList;

/**
 * A transportation action consisting of a list of routes.
 *
 * @author Timer
 */
public class RSWeb {
	private final LinkedList<Route> routes = new LinkedList<Route>();

	public RSWeb(final Route[] routes) {
		for (Route route : routes) {
			this.routes.addLast(route);
		}
	}

	public Route[] getRoutes() {
		return routes.toArray(new Route[routes.size()]);
	}

	public boolean step() {
		if (routes.size() > 0) {
			Route route = routes.poll();
			if (route.execute()) {
				if (!route.finished()) {
					routes.addFirst(route);
				}
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean finished() {
		return routes.size() == 0;
	}
}