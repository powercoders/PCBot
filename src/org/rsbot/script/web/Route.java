package org.rsbot.script.web;

import org.rsbot.script.wrappers.RSTile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Route {
	private final LinkedList<RouteStep> subRoutes = new LinkedList<RouteStep>();

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

	public boolean finished() {
		return subRoutes.size() == 0;
	}

	public List<RSTile[]> getPaths() {
		List<RSTile[]> l = new ArrayList<RSTile[]>();
		for (RouteStep route : subRoutes) {
			if (route != null && route.getPath() != null) {
				l.add(route.getPath());
			}
		}
		return l;
	}

	public void updateRoute() {
		for (RouteStep route : subRoutes) {
			route.update();
		}
	}
}
