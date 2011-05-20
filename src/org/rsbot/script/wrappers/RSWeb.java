package org.rsbot.script.wrappers;

import org.rsbot.script.methods.Web;
import org.rsbot.script.web.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A transportation action consisting of a list of routes.
 *
 * @author Timer
 */
public class RSWeb {

	private final LinkedList<Route> routes = new LinkedList<Route>();
	private int oldCount = 0;
	private RSTile[] path;

	public RSWeb(final Route[] routes) {
		for (Route route : routes) {
			this.routes.addLast(route);
		}
		oldCount = Web.rs_map.size();
		List<RSTile> l = new ArrayList<RSTile>();
		for (Route route : getRoutes()) {
			if (route != null) {
				l.addAll(Arrays.asList(route.getPath()));
			}
		}
		this.path = l.toArray(new RSTile[l.size()]);
	}

	public Route[] getRoutes() {
		return routes.toArray(new Route[routes.size()]);
	}

	public boolean step() {
		if (routes.size() > 0) {
			if (Web.rs_map.size() != oldCount) {
				oldCount = Web.rs_map.size();
				update();
			}
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

	@Deprecated
	public List<RSTile[]> getPaths() {
		List<RSTile[]> l = new ArrayList<RSTile[]>();
		for (Route route : getRoutes()) {
			if (route != null && route.getPaths().size() > 0) {
				for (RSTile[] rsTiles : route.getPaths()) {
					l.add(rsTiles);
				}
			}
		}
		return l;
	}

	@Deprecated
	public RSTile[] getPath() {//For scripters that wish to draw paths.
		return path;
	}

	@Deprecated
	public double getDistance() {
		List<RSTile[]> paths = getPaths();
		double d = 0.0D;
		for (RSTile[] tiles : paths) {
			RSTile last = null;
			for (RSTile tile : tiles) {
				if (last != null) {
					d += tile.getX() != last.getX() && tile.getY() != last.getY() ? 1.41421356D : 1.0D;
				}
				last = tile;
			}
		}
		return d;
	}

	public void update() {
		for (Route route : getRoutes()) {
			if (route != null) {
				route.updateRoute();
			}
		}
		List<RSTile> l = new ArrayList<RSTile>();
		for (Route route : getRoutes()) {
			if (route != null) {
				l.addAll(Arrays.asList(route.getPath()));
			}
		}
		path = l.toArray(new RSTile[l.size()]);
	}
}