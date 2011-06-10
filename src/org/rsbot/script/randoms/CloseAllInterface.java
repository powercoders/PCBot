package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSComponent;

import java.util.LinkedList;
import java.util.List;


/**
 * Closes interfaces that scripts may open by mistake.
 */
@ScriptManifest(authors = {"Jacmob", "HeyyamaN", "Pervy Shuya"}, name = "InterfaceCloser", version = 1.9)
public class CloseAllInterface extends Random {

	static class ComponentDef {

		final int parent;
		final int child;
		final boolean text;

		public ComponentDef(final int parent, final int child, final boolean text) {
			this.parent = parent;
			this.child = child;
			this.text = text;
		}

	}

	private final List<ComponentDef> components = new LinkedList<ComponentDef>();

	{
		addChild(743, 20); // Audio
		addChild(767, 10); // Bank of RuneScape - Help
		addChild(499, 29); // Stats
		addChild(594, 48); // Report Abuse
		addChild(275, 8); // Quest
		addChild(206, 13); // Price check
		addChild(266, 1); // Tombstone
		addChild(266, 11); // Grove
		addChild(102, 13); // Death items
		addChild(14, 34, true); // New pin
		addChild(14, 11); // Pin settings
		addChild(157, 13); // Quick chat help
		addChild(764, 18); // Objectives
		addChild(895, 19); // Advisor
		addChild(109, 14); // Grand exchange collection
		addChild(667, 74); // Equipment Bonus
		addChild(742, 18); // Graphic
		addChild(917, 73); // Task List
		addChild(1107, 174); // Clan Vexillum
		addChild(276, 76); // Soul Wars Rewards
		addChild(1011, 51); // Pest Control Rewards ( Commendation Rewards )
		addChild(732, 208); // Fist of Guthx Reward Shop
		addChild(1083, 181); // Livid Farm Rewards
	}

	private void addChild(final int parent, final int idx) {
		components.add(new ComponentDef(parent, idx, false));
	}

	private void addChild(final int parent, final int idx, final boolean text) {
		components.add(new ComponentDef(parent, idx, text));
	}

	@Override
	public boolean activateCondition() {
		if (game.isLoggedIn()) {
			if (interfaces.get(755).getComponent(44).isValid()) { // World map
				if (interfaces.getComponent(755, 0).getComponents().length > 0) {
					return true;
				}
			}
			for (final ComponentDef c : components) {
				final RSComponent comp = interfaces.getComponent(c.parent, c.child);
				if (comp.isValid() && !(c.text && (comp.getText() == null || comp.getText().isEmpty()))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int loop() {
		sleep(random(500, 900));
		if (interfaces.get(755).isValid() && interfaces.getComponent(755, 0).getComponents().length > 0) {
			interfaces.getComponent(755, 44).doClick();
			return random(500, 900);
		}
		for (final ComponentDef c : components) {
			if (interfaces.getComponent(c.parent, c.child).isValid()) {
				interfaces.getComponent(c.parent, c.child).doClick();
				sleep(random(500, 900));
				if (random(0, 3) == 0) {
					mouse.moveSlightly();
				}
				break;
			}
		}
		return -1;
	}
}