import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.Filter;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.*;

import javax.swing.*;
import java.awt.*;

/**
 * A simple Ivy Chopper for free on PCBot.
 * Please remember paid scripts are always better!
 */

@ScriptManifest(name = "godChopperLite", authors = {"Timer"}, description = "Cuts and banks trees, oaks, willows, maples, yews, and magics.", version = 0.1, keywords = {"tree", "oak", "willow", "maple"})
public class godChopperLite extends Script implements PaintListener, MessageListener {
	private RSObject object = null;
	private static String treeName = "";
	private static final String[] TREES = {"Tree", "Oak", "Willow", "Maple", "Yew", "Magic"};
	private static final Filter<RSObject> TREE = new Filter<RSObject>() {
		public boolean accept(RSObject rsObject) {
			return rsObject != null && rsObject.getName() != null && rsObject.getName().equals(treeName) && containsAction(rsObject.getDef().getActions(), "Chop");
		}
	};

	private static boolean containsAction(String[] actions, String str) {
		for (String a : actions) {
			if (a != null) {
				if (a.contains(str)) {
					return true;
				}
			}
		}
		return false;
	}

	private Timer timer = new Timer(60000);
	private RSTile returnTile = null;
	private RSWeb walkWeb = null;
	private boolean powerChop = true;
	private static final Timer timeRunning = new Timer(0);
	private int cutCount = 0;

	@Override
	public int loop() {
		if (getMyPlayer().isMoving() && walkWeb == null) {
			return random(500, 1280);
		}
		if (interfaces.canContinue()) {
			interfaces.clickContinue();
			return 200;
		}
		if (walking.getEnergy() > 50 && !walking.isRunEnabled()) {
			walking.setRun(true);
			return 1800;
		}
		if (timer != null && timer.isRunning() && getMyPlayer().getAnimation() != -1 && (object == null || (verify(object) && TREE.accept(object)))) {
			if (random(0, 10) == 0) {
				mouse.moveRandomly(300);
			} else if (random(0, 5) == 0) {
				if (calc.pointOnScreen(mouse.getLocation())) {
					mouse.moveOffScreen();
				}
			}
			return 80;
		}
		if (inventory.isFull()) {
			if (powerChop) {
				for (RSItem item : inventory.getItems()) {
					if (item != null && item.getID() != -1 && !item.getName().contains("hatchet")) {
						item.doAction(random(0, 150) == 0 ? "Examine" : "Drop");
						sleep(random(20, 480));
					}
				}
			} else {
				final RSTile tile = getMyPlayer().getLocation();
				if (returnTile == null) {
					returnTile = tile;
				}
				final RSTile bankTile = web.getNearestBank(tile);
				if (bankTile != null) {
					if (calc.distanceTo(bankTile) < 4) {
						final int h = inventoryHatchetID();
						if (bank.isOpen() || bank.open()) {
							for (int i = 0; i < 10; i++) {
								if (inventory.getCount() > 0) {
									if (h != -1) {
										bank.depositAllExcept(h);
									} else {
										bank.depositAll();
									}
									sleep(250);
								}
							}
							walkWeb = null;
						}
					} else {
						if (walkWeb == null) {
							walkWeb = web.getWeb(tile, bankTile);
						}
						if (!walkWeb.finished()) {
							if (!walkWeb.step()) {
								walkWeb = null;
								return 0;
							}
						} else {
							walkWeb = null;
						}
					}
				}
			}
			return 200;
		}
		if (!powerChop && returnTile != null) {
			final RSTile tile = getMyPlayer().getLocation();
			if (calc.distanceTo(returnTile) > 4) {
				if (walkWeb == null) {
					walkWeb = web.getWeb(tile, returnTile);
				}
				if (!walkWeb.finished()) {
					if (!walkWeb.step()) {
						walkWeb = null;
						return 0;
					}
				} else {
					walkWeb = null;
					if (calc.distanceTo(returnTile) < 6) {
						returnTile = null;
					}
				}
			} else {
				walkWeb = null;
				returnTile = null;
			}
			return 0;
		}
		if (bank.isOpen()) {
			bank.close();
		}
		RSObject tree = objects.getNearest(TREE);
		if (tree == null) {
			return 2000;
		}
		if (!calc.tileOnScreen(tree.getLocation())) {
			walking.walkTileMM(tree.getLocation());
			camera.turnTo(tree);
			return 1500;
		}
		tree.doAction("Chop down " + treeName);
		object = tree;
		timer.setEndIn(60000);
		return random(1000, 3000);
	}

	@Override
	public boolean onStart() {
		int returnInt = JOptionPane.showConfirmDialog(null, "Do you wish to bank the logs?", "Powerchop or Bank", JOptionPane.YES_NO_OPTION);
		switch (returnInt) {
			case JOptionPane.YES_OPTION:
				powerChop = false;
				break;
			case JOptionPane.NO_OPTION:
				powerChop = true;
				break;
			default:
				return false;
		}
		double distance = 100.0;
		String finalTree = null;
		for (String tree : TREES) {
			treeName = tree;
			RSObject test = objects.getNearest(TREE);
			if (TREE != null && verify(test)) {
				if (calc.distanceTo(test) < distance) {
					distance = calc.distanceTo(test);
					finalTree = treeName;
				}
			}
		}
		if (finalTree != null) {
			treeName = finalTree;
			log("Tree: " + treeName);
		} else {
			treeName = "";
		}
		return !treeName.equals("");
	}

	public void onRepaint(Graphics render) {
		render.drawString("Running for: " + timeRunning.toElapsedString(), 15, 50);
		render.drawString("Chopped Logs: " + cutCount, 15, 65);
		if (object != null) {
			RSModel model = object.getModel();
			if (model != null) {
				render.setColor(new Color(0, 0, 255, 75));
				Polygon[] modelArray = model.getTriangles();
				for (Polygon modelIndex : modelArray) {
					render.fillPolygon(modelIndex);
				}
			}
		}
	}

	public int inventoryHatchetID() {
		return inventory.getItemID("hatchet");
	}

	public void messageReceived(MessageEvent e) {
		if (e.getID() == MessageEvent.MESSAGE_SERVER) {
			if (e.getMessage().toLowerCase().contains("do not have")) {
				stopScript();
			}
		} else if (e.getID() == MessageEvent.MESSAGE_ACTION) {
			if (e.getMessage().toLowerCase().contains("ou get some")) {
				cutCount++;
			}
		}
	}

	@Override
	public boolean onBreakStart() {
		walkWeb = null;
		return true;
	}
}
