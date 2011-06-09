package org.rsbot.script.web;

import java.util.LinkedList;

public class Route {
	private final LinkedList<RouteStep> subRoutes = new LinkedList<RouteStep>();
	public Route parent = null;

	public Route(final RouteStep[] steps) {
		for (RouteStep step : steps) {
			subRoutes.addLast(step);
		}
	}

	public boolean execute() {
		if (subRoutes.size() > 0) {
			RouteStep routeStep = subRoutes.poll();
			if (!routeStep.execute()) {
				return false;
			} else {
				if (!routeStep.finished()) {
					subRoutes.addFirst(routeStep);
				}
			}
		}
		return true;
	}

	public RouteStep[] getSteps() {
		return subRoutes.toArray(new RouteStep[subRoutes.size()]);
	}

	public boolean finished() {
		return subRoutes.size() == 0;
	}

	public void add(final RouteStep step) {
		subRoutes.addLast(step);
	}

	public void updateRoute() {
		for (RouteStep route : subRoutes) {
			route.update();
		}
	}

	public double getDistance() {
		return 0;//TODO distancing.
	}
}
