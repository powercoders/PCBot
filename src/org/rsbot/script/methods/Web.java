package org.rsbot.script.methods;

import org.rsbot.script.internal.wrappers.TileFlags;
import org.rsbot.script.web.WebTile;
import org.rsbot.script.wrappers.RSTile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * The web class.
 *
 * @author Timer
 */
public class Web extends MethodProvider {
	public static final HashMap<RSTile, TileFlags> map = new HashMap<RSTile, TileFlags>();
	public static boolean loaded = false;

	Web(final MethodContext ctx) {
		super(ctx);
	}

	public static final RSTile[] generateNodePath(final RSTile start, final RSTile end) {
		if (start.getZ() != end.getZ()) {
			return null;
		}
		HashSet<Node> open = new HashSet<Node>();
		HashSet<Node> closed = new HashSet<Node>();
		Node curr = new Node(start.getX(), start.getY(), start.getZ());
		Node dest = new Node(end.getX(), end.getY(), end.getZ());
		curr.f = Heuristic(curr, dest);
		open.add(curr);
		while (!open.isEmpty()) {
			curr = Lowest_f(open);
			if (curr.equals(dest)) {
				return Path(curr);
			}
			open.remove(curr);
			closed.add(curr);
			for (Node next : Successors(curr)) {
				if (!closed.contains(next)) {
					double t = curr.g + Dist(curr, next);
					boolean use_t = false;
					if (!open.contains(next)) {
						open.add(next);
						use_t = true;
					} else if (t < next.g) {
						use_t = true;
					}
					if (use_t) {
						next.prev = curr;
						next.g = t;
						next.f = t + Heuristic(next, dest);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Node class.
	 *
	 * @author Jacmob
	 */
	private static class Node extends TileFlags {
		public int x, y, z;
		public Node prev;
		public double g, f;

		public Node(int x, int y, int z) {
			super(new RSTile(x, y, z), getTileFlags(new RSTile(x, y, z)));
			this.x = x;
			this.y = y;
			this.z = z;
			g = f = 0;
		}

		@Override
		public int hashCode() {
			return (x << 4) | y;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Node) {
				Node n = (Node) o;
				return x == n.x && y == n.y;
			}
			return false;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

		public WebTile toWebTile() {
			WebTile t = new WebTile(new RSTile(x, y, z), getKeys());
			return t;
		}
	}

	private static double Heuristic(Node start, Node end) {
		double dx = start.x - end.x;
		double dy = start.y - end.y;
		if (dx < 0) {
			dx = -dx;
		}
		if (dy < 0) {
			dy = -dy;
		}
		return dx < dy ? dy : dx;
	}

	private static double Dist(Node start, Node end) {
		if (start.x != end.x && start.y != end.y) {
			return 1.41421356;
		} else {
			return 1.0;
		}
	}

	private static Node Lowest_f(Set<Node> open) {
		Node best = null;
		for (Node t : open) {
			if (best == null || t.f < best.f) {
				best = t;
			}
		}
		return best;
	}

	private static WebTile[] Path(Node end) {
		LinkedList<WebTile> path = new LinkedList<WebTile>();
		Node p = end;
		while (p != null) {
			path.addFirst(p.toWebTile());
			p = p.prev;
		}
		return path.toArray(new WebTile[path.size()]);
	}

	private static java.util.List<Node> Successors(Node t) {
		LinkedList<Node> tiles = new LinkedList<Node>();
		int x = t.x, y = t.y;
		final WebTile here = t.toWebTile();
		if (!Flag(here, TileFlags.Keys.WALL_SOUTH) && !Flag(new RSTile(here.getX(), here.getY() - 1), TileFlags.Keys.BLOCKED)) {
			tiles.add(new Node(x, y - 1, t.getZ()));
		}
		if (!Flag(here, TileFlags.Keys.WALL_WEST) && !Flag(new RSTile(here.getX() - 1, here.getY()), TileFlags.Keys.BLOCKED)) {
			tiles.add(new Node(x - 1, y, t.getZ()));
		}
		if (!Flag(here, TileFlags.Keys.WALL_NORTH) && !Flag(new RSTile(here.getX(), here.getY() + 1), TileFlags.Keys.BLOCKED)) {
			tiles.add(new Node(x, y + 1, t.getZ()));
		}
		if (!Flag(here, TileFlags.Keys.WALL_EAST) && !Flag(new RSTile(here.getX() + 1, here.getY()), TileFlags.Keys.BLOCKED)) {
			tiles.add(new Node(x + 1, y, t.getZ()));
		}
		if (!Flag(here, TileFlags.Keys.WALL_SOUTH_WEST, TileFlags.Keys.WALL_SOUTH, TileFlags.Keys.WALL_WEST) &&
				!Flag(new RSTile(here.getX() - 1, here.getY() - 1), TileFlags.Keys.BLOCKED) &&
				!Flag(new RSTile(here.getX(), here.getY() - 1), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_WEST) &&
				!Flag(new RSTile(here.getX() - 1, here.getY()), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_SOUTH)) {
			tiles.add(new Node(x - 1, y - 1, t.getZ()));
		}
		if (!Flag(here, TileFlags.Keys.WALL_NORTH_WEST, TileFlags.Keys.WALL_NORTH, TileFlags.Keys.WALL_WEST) &&
				!Flag(new RSTile(here.getX() - 1, here.getY() + 1), TileFlags.Keys.BLOCKED) &&
				!Flag(new RSTile(here.getX(), here.getY() + 1), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_WEST) &&
				!Flag(new RSTile(here.getX() - 1, here.getY()), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_NORTH)) {
			tiles.add(new Node(x - 1, y + 1, t.getZ()));
		}
		if (!Flag(here, TileFlags.Keys.WALL_SOUTH_EAST, TileFlags.Keys.WALL_SOUTH, TileFlags.Keys.WALL_EAST) &&
				!Flag(new RSTile(here.getX() + 1, here.getY() - 1), TileFlags.Keys.BLOCKED) &&
				!Flag(new RSTile(here.getX(), here.getY() - 1), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_EAST) &&
				!Flag(new RSTile(here.getX() + 1, here.getY()), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_SOUTH)) {
			tiles.add(new Node(x + 1, y - 1, t.getZ()));
		}
		if (!Flag(here, TileFlags.Keys.WALL_NORTH_EAST, TileFlags.Keys.WALL_NORTH, TileFlags.Keys.WALL_EAST) &&
				!Flag(new RSTile(here.getX() + 1, here.getY() + 1), TileFlags.Keys.BLOCKED) &&
				!Flag(new RSTile(here.getX(), here.getY() + 1), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_EAST) &&
				!Flag(new RSTile(here.getX() + 1, here.getY()), TileFlags.Keys.BLOCKED, TileFlags.Keys.WALL_NORTH)) {
			tiles.add(new Node(x + 1, y + 1, t.getZ()));
		}
		return tiles;
	}

	private static Integer[] getTileFlags(final RSTile tile) {
		if (Web.map.containsKey(tile)) {
			return Web.map.get(tile).getKeys();
		}
		return new Integer[]{};
	}

	private static boolean Flag(final RSTile tile, final int... key) {
		if (Web.map.containsKey(tile)) {
			TileFlags theTile = Web.map.get(tile);
			return theTile.containsKey(key);
		}
		return false;
	}
}
