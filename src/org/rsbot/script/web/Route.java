package org.rsbot.script.web;

import java.util.LinkedList;

import org.rsbot.script.wrappers.RSTile;

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

	public RSTile getDestination() {
		return subRoutes.getLast().getDestination();
	}

	public RSTile getStart() {
		return subRoutes.getFirst().getStart();
	}

}
