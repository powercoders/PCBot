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
import java.util.ArrayList;
import java.util.List;

/**
 * A simple Miner for free on RSBot.
 * Please remember paid scripts are always better!
 */

@ScriptManifest(name = "godMinerLite", authors = {"Timer"}, description = "Bank [or power mine] copper, tin, and iron.", version = 0.1, keywords = {"copper", "tin", "iron", "power", "mine", "bank", "mining"})
public class godMinerLite extends Script implements PaintListener, MessageListener {
	private static final int[] COPPER_ROCK_IDS = {3229, 3027, 9708, 9709, 9710, 11936, 11937, 11938, 11960, 11961, 11962,
			11963, 31080, 31082, 29230, 29231, 5780, 5779, 5781}, TIN_ROCK_IDS = {3038, 3245, 9714, 9716, 11933, 11934,
			11935, 11957, 11958, 11959, 31077, 31078, 31079, 5776, 5777, 5778}, IRON_ROCK_IDS = {2092, 2093, 5773, 5774,
			5775, 9717, 9718, 9719, 11954, 11955, 11956, 14913, 14914, 31071, 31072, 31073, 37307, 37308, 37309, 29221,
			29222, 14856, 14857, 14858, 32443, 32441, 32442};
	private static final List<Integer> ROCKS = new ArrayList<Integer>();
	private RSObject object = null;
	private static final Filter<RSObject> ROCK = new Filter<RSObject>() {
		public boolean accept(RSObject rsObject) {
			return rsObject != null && ROCKS.contains(rsObject.getID());
		}
	};
	private Timer timer = new Timer(15000);
	private RSTile returnTile = null;
	private RSWeb walkWeb = null;
	private boolean powerMine = true;

	@Override
	public int loop() {
		if (getMyPlayer().isMoving() && walkWeb == null) {
			return random(500, 1280);
		}
		if (interfaces.canContinue()) {
			interfaces.clickContinue();
			return 0;
		}
		if (walking.getEnergy() > 50 && !walking.isRunEnabled()) {
			walking.setRun(true);
			return 1800;
		}
		if (timer != null && timer.isRunning() && isMining()) {
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
			if (powerMine) {
				for (RSItem item : inventory.getItems()) {
					if (item != null && item.getID() != -1 && !item.getName().contains("pickaxe")) {
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
						final int h = inventoryPickaxeID();
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
		if (!powerMine && returnTile != null) {
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
					if (calc.distanceTo(returnTile) < 4) {
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
		RSObject rock = objects.getNearest(ROCK);
		if (rock != null) {
			if (!calc.tileOnScreen(rock.getLocation())) {
				walking.walkTileMM(rock.getLocation());
				camera.turnTo(rock);
				return 1500;
			}
			rock.doAction("Mine");
			object = rock;
			timer.setEndIn(15000);
		}
		return random(1000, 3000);
	}

	@Override
	public boolean onStart() {
		int returnInt = JOptionPane.showConfirmDialog(null, "Do you wish to bank the ore?", "Powermine or Bank", JOptionPane.YES_NO_OPTION);
		switch (returnInt) {
			case JOptionPane.YES_OPTION:
				powerMine = false;
				break;
			case JOptionPane.NO_OPTION:
				powerMine = true;
				break;
			default:
				return false;
		}
		double distance = 100.0;
		int finalRock = -1;
		RSObject rock = objects.getNearest(COPPER_ROCK_IDS);
		if (rock != null && calc.distanceTo(rock) < distance) {
			distance = calc.distanceTo(rock);
			finalRock = 0;
		}
		rock = objects.getNearest(TIN_ROCK_IDS);
		if (rock != null && calc.distanceTo(rock) < distance) {
			distance = calc.distanceTo(rock);
			finalRock = 1;
		}
		rock = objects.getNearest(IRON_ROCK_IDS);
		if (rock != null && calc.distanceTo(rock) < distance) {
			distance = calc.distanceTo(rock);
			finalRock = 2;
		}
		if (finalRock != -1) {
			int[] arr = null;
			switch (finalRock) {
				case 0:
					arr = COPPER_ROCK_IDS;
					break;
				case 1:
					arr = TIN_ROCK_IDS;
					break;
				case 2:
					arr = IRON_ROCK_IDS;
					break;
			}
			if (arr != null) {
				for (int i : arr) {
					ROCKS.add(i);
				}
			} else {
				return false;
			}
		}
		return finalRock != -1;
	}

	public void onRepaint(Graphics render) {
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

	private final RSTile getCurrentTile() {
		int orient = roundOrient(getMyPlayer().getOrientation());
		RSTile ourTile = getMyPlayer().getLocation();
		switch (orient) {
			case 0:
				return new RSTile(ourTile.getX() + 1, ourTile.getY());
			case 1:
				return new RSTile(ourTile.getX(), ourTile.getY() + 1);
			case 2:
				return new RSTile(ourTile.getX() - 1, ourTile.getY());
			case 3:
				return new RSTile(ourTile.getX(), ourTile.getY() - 1);
		}
		return null;
	}

	private final boolean isMining() {
		final RSTile tile = getCurrentTile();
		RSObject object = objects.getTopAt(tile);
		if (object != null && ROCK.accept(object)) {
			this.object = object;
			return getMyPlayer().getAnimation() != -1;
		}
		this.object = null;
		return false;
	}

	private static final int roundOrient(int orientation) {
		int i = orientation % 90;
		int off;
		if (i >= 45) {
			off = 90 - i;
		} else {
			off = 0 - i;
		}
		orientation += off;
		return orientation / 90;
	}

	public void messageReceived(MessageEvent e) {
		if (e.getID() == MessageEvent.MESSAGE_SERVER) {
			if (e.getMessage().toLowerCase().contains("do not have")) {
				stopScript();
			}
		}
	}

	public int inventoryPickaxeID() {
		return inventory.getItemID("pickaxe");
	}

	@Override
	public boolean onBreakStart() {
		walkWeb = null;
		return true;
	}
}
