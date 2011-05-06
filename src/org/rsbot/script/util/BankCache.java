package org.rsbot.script.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rsbot.script.wrappers.RSItem;

public class BankCache {
	private static final Map<String, List<BankItem>> banks = new HashMap<String, List<BankItem>>();

	/**
	 * Saves a bank cache for a user.
	 * 
	 * @param name
	 *            The name of the character.
	 * @param items
	 *            The array of items in the bank.
	 */
	public static void Save(final String name, final RSItem[] items) {
		if (banks.get(name) == null)
			banks.put(name, new ArrayList<BankItem>());
		banks.get(name).clear();
		for (RSItem item : items)
			banks.get(name).add(new BankItem(item));
	}

	/**
	 * Gets an item in the bank cache
	 * 
	 * @param name
	 *            The account name
	 * @param o
	 *            The item name
	 * @return The bank item or <tt>null</tt>
	 */
	public static BankItem getItem(final String name, final String o) {
		if (banks.containsKey(name)) {
			List<BankItem> items = banks.get(name);
			for (BankItem b : items) {
				if (b.getName().equalsIgnoreCase(o))
					return b;
			}
		}
		return null;
	}

	/**
	 * Gets an item in the bank cache
	 * 
	 * @param name
	 *            The account name
	 * @param id
	 *            The item id
	 * @return The bank item or <tt>null</tt>
	 */
	public static BankItem getItem(final String name, final int id) {
		if (banks.containsKey(name)) {
			List<BankItem> items = banks.get(name);
			for (BankItem b : items) {
				if (b.getID() == id)
					return b;
			}
		}
		return null;
	}

	/**
	 * Lightweight version of an RSItem without the ability to do actions. (No
	 * component reference)
	 */
	public static class BankItem {
		private final int id;
		private final String name;
		private final int count;

		public BankItem(RSItem base) {
			this.id = base.getID();
			this.count = base.getStackSize();
			this.name = base.getName();
		}

		public int getID() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getStackSize() {
			return count;
		}
	}
}