import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.util.Filter;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.*;

import java.awt.*;

/**
 * A simple Ivy Chopper for free on RSBot.
 * Please remember paid scripts are always better!
 */

@ScriptManifest(name = "godIvy", authors = {"Timer"}, description = "Cuts down ivy.", website = "http://www.powerbot.org/vb/showthread.php?t=783713", version = 0.1, keywords = {"bank", "ivy", "nest", "woodcutting"}, requiresVersion = 242)
public class godIvy extends Script implements PaintListener {
	private static final Filter<RSGroundItem> BIRD_NEST = new Filter<RSGroundItem>() {
		public boolean accept(RSGroundItem rsGroundItem) {
			return rsGroundItem != null && rsGroundItem.getItem().getName() != null && rsGroundItem.getItem().getName().toLowerCase().contains("nest");
		}
	};
	private static final Filter<RSObject> IVY = new Filter<RSObject>() {
		public boolean accept(RSObject rsObject) {
			return rsObject != null && rsObject.getName() != null && rsObject.getName().toLowerCase().equalsIgnoreCase("ivy");
		}
	};

	private RSTile returnTile = null;
	private RSWeb walkWeb = null;
	private RSObject ivy = null;

	@Override
	public int loop() {
		if (game.getPlane() != 0) {
			return -1;
		}
		if ((getMyPlayer().isMoving() && walkWeb == null) || getMyPlayer().getAnimation() != -1) {
			if (random(0, 10) == 0) {
				mouse.moveRandomly(300);
			} else if (random(0, 5) == 0) {
				if (calc.pointOnScreen(mouse.getLocation())) {
					mouse.moveOffScreen();
				}
			} else if (random(0, 10) == 0) {
				camera.moveRandomly(5000);
			} else if (random(0, 4) == 0) {
				game.openTab(Game.TAB_STATS);
				sleep(150);
				skills.doHover(Skills.WOODCUTTING);
				sleep(random(0, 5000));
				game.openTab(Game.TAB_INVENTORY);
			}
			return random(2500, 4000);
		}
		if (inventory.isFull()) {
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
						returnTile = null;
					}
				}
			}
			return 250;
		} else {
			final RSGroundItem groundItem = groundItems.getNearest(BIRD_NEST);
			if (groundItem != null) {
				groundItem.doAction("Take");
				return 800;
			}
		}
		if (returnTile != null) {
			final RSTile tile = getMyPlayer().getLocation();
			if (calc.distanceTo(returnTile) < 4) {
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
				}
			} else {
				returnTile = null;
			}
			return 0;
		}
		RSObject ivyObject = objects.getNearest(IVY);
		if (ivyObject != null) {
			if (ivyObject.isOnScreen()) {
				RSModel ivyModel = ivyObject.getModel();
				if (ivyModel != null) {
					Timer timer = new Timer(10000);
					while (timer.isRunning()) {
						Point p = ivyModel.getPoint();
						if (calc.pointOnScreen(p)) {
							mouse.move(p);
							Timer waitTimer = new Timer(250);
							while (waitTimer.isRunning()) {
								if (mouse.getLocation().equals(p)) {
									break;
								}
								sleep(random(25, 70));
							}
							sleep(random(150, 180));
							if (menu.doAction("Chop")) {
								ivy = ivyObject;
								camera.turnTo(ivy);
								return 250;
							}
						}
					}
				}
			} else {
				if (calc.canReach(ivyObject.getLocation(), false)) {
					walking.walkTileMM(ivyObject.getLocation());
				}
				return 2500;
			}
		}
		return 0;
	}

	public int inventoryHatchetID() {
		return inventory.getItemID("hatchet");
	}

	public void onRepaint(Graphics render) {
		if (ivy != null) {
			RSModel model = ivy.getModel();
			if (model != null) {
				render.setColor(new Color(0, 0, 255, 75));
				Polygon[] modelArray = model.getTriangles();
				for (Polygon modelIndex : modelArray) {
					render.fillPolygon(modelIndex);
				}
			}
		}
	}

	@Override
	public boolean onBreakStart() {
		walkWeb = null;
		return true;
	}
}