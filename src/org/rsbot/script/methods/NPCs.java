package org.rsbot.script.methods;

import java.util.HashSet;
import java.util.Set;

import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSNPC;

/**
 * Provides access to non-player characters.
 */
public class NPCs extends MethodProvider {
	/**
	 * A filter that accepts all matches.
	 */
	public static final Filter<RSNPC> ALL_FILTER = new Filter<RSNPC>() {
		@Override
		public boolean accept(final RSNPC npc) {
			return true;
		}
	};

	NPCs(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Returns an array of all loaded RSNPCs.
	 * 
	 * @return An array of the loaded RSNPCs.
	 */
	public RSNPC[] getAll() {
		return getAll(NPCs.ALL_FILTER);
	}

	/**
	 * Returns an array of all loaded RSNPCs that are accepted by the provided
	 * Filter
	 * 
	 * @param filter
	 *            Filters out unwanted matches.
	 * @return An array of the loaded RSNPCs.
	 */
	public RSNPC[] getAll(final Filter<RSNPC> filter) {
		final int[] indices = methods.client.getRSNPCIndexArray();
		final Set<RSNPC> npcs = new HashSet<RSNPC>();
		for (final int index : indices) {
			final Node node = methods.nodes.lookup(methods.client.getRSNPCNC(), index);
			if (node instanceof RSNPCNode) {
				final RSNPC npc = new RSNPC(methods, ((RSNPCNode) node).getRSNPC());
				if (npc != null && filter.accept(npc)) {
					npcs.add(npc);
				}
			}
		}
		return npcs.toArray(new RSNPC[npcs.size()]);
	}

	/**
	 * Returns an array of all loaded RSNPCs with the provided ID(s).
	 * 
	 * @param ids
	 *            Allowed NPC IDs.
	 * @return An array of the loaded RSNPCs matching the provided ID(s).
	 */
	public RSNPC[] getAll(final int... ids) {
		return getAll(new Filter<RSNPC>() {
			@Override
			public boolean accept(final RSNPC npc) {
				if (npc != null) {
					for (final int id : ids) {
						if (npc.getID() == id) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns an array of all loaded RSNPCs with the provided name(s).
	 * 
	 * @param names
	 *            Allowed NPC names.
	 * @return An array of the loaded RSNPCs matching the provided name(s).
	 */
	public RSNPC[] getAll(final String... names) {
		return getAll(new Filter<RSNPC>() {
			@Override
			public boolean accept(final RSNPC npc) {
				final String name = npc != null ? npc.getName() : null;
				if (name != null) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the RSNPC that is nearest out of all of loaded RSNPCs accepted by
	 * the provided Filter.
	 * 
	 * @param filter
	 *            Filters out unwanted matches.
	 * @return An RSNPC object representing the nearest RSNPC accepted by the
	 *         provided Filter; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public RSNPC getNearest(final Filter<RSNPC> filter) {
		int min = 20;
		RSNPC closest = null;
		final int[] indices = methods.client.getRSNPCIndexArray();

		for (final int index : indices) {
			final Node node = methods.nodes.lookup(methods.client.getRSNPCNC(), index);
			if (node instanceof RSNPCNode) {
				final RSNPC npc = new RSNPC(methods, ((RSNPCNode) node).getRSNPC());
				if (npc != null && filter.accept(npc)) {
					final int distance = methods.calc.distanceTo(npc);
					if (distance < min) {
						min = distance;
						closest = npc;
					}
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest out of all of the RSNPCs with the
	 * provided ID(s). Can return null.
	 * 
	 * @param ids
	 *            Allowed NPC IDs.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public RSNPC getNearest(final int... ids) {
		return getNearest(new Filter<RSNPC>() {
			@Override
			public boolean accept(final RSNPC npc) {
				if (npc != null) {
					for (final int id : ids) {
						if (npc.getID() == id) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the RSNPC that is nearest out of all of the RSNPCs with the
	 * provided name(s). Can return null.
	 * 
	 * @param names
	 *            Allowed NPC names.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided names; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public RSNPC getNearest(final String... names) {
		return getNearest(new Filter<RSNPC>() {
			@Override
			public boolean accept(final RSNPC npc) {
				final String name = npc != null ? npc.getName() : null;
				if (name != null) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}
}
