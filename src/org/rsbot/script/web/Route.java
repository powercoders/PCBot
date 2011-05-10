package org.rsbot.script.web;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;

import java.util.LinkedList;

public class Route {
	private LinkedList<RouteStep> subRoutes = new LinkedList<RouteStep>();

	public Route(final RouteStep[] steps) {
		for (RouteStep step : steps) {
			subRoutes.addLast(step);
		}
	}

	public boolean execute() {
		while (subRoutes.size() > 0) {
			if (!subRoutes.poll().execute()) {
				return false;
			}
		}
		return true;
	}
}
