import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(authors = {"SupahScripts"}, keywords = {"WillowPowerCutter"}, name = "WillowPwner", version = 1.06, description = "Universal Willow Tree Cutter. Only PowerCuts (Supports most hatchets)", website = "http://www.powerbot.org/vb/showthread.php?t=772773")
public class WillowPowerCutter extends Script implements PaintListener,
		MessageListener {

	private final int willowLogID = 1519;
	private final int willowTree[] = {5551, 5552, 5553, 1308, 8481, 8482,
			8483, 8484, 8485, 8486, 8487, 8488, 38627, 38616, 38627, 2210, 142,
			2372, 139};
	private final int hatchets[] = {1349, 1351, 1353, 1355, 1357, 1359, 1361,
			6739, 13470};
	private int startXP;
	public long startTime = System.currentTimeMillis();
	public int xpGained;
	public int logsCut;
	String status = "";
	private final Color color1 = new Color(0, 0, 0);

	private final Color color2 = new Color(0, 255, 0, 122);

	private final Color color3 = new Color(0, 51, 204);

	private final Font font1 = new Font("Impact", 0, 16);

	private final Image img1 = getImage("http://therunehell.webs.com/Woodcutting_capes.png");

	public void antiban() {
		final int r = random(1, 10);

		if (r == 1) {
			camera.setAngle(random(0, 200));
			sleep(random(400, 800));
		}

		if (r == 2) {
			camera.setPitch(random(0, 85));
			sleep(random(400, 800));
		}

		if (r == 3) {
			mouse.moveSlightly();
			sleep(random(400, 800));
		}

		if (r == 4) {
			mouse.moveSlightly();
			sleep(random(100, 200));
			mouse.moveSlightly();
			sleep(random(100, 200));
			mouse.moveSlightly();
			sleep(random(400, 800));
		}

		if (r == 5) {
			mouse.moveSlightly();
			mouse.moveSlightly();
			mouse.moveSlightly();
			sleep(random(400, 800));
		}

		if (r == 6) {
			skills.doHover(Skills.INTERFACE_WOODCUTTING);
			sleep(random(0, 1500));
		}

		if (r == 7) {
			skills.doHover(Skills.INTERFACE_CONSTITUTION);
			sleep(random(0, 1500));
		}

		if (r == 8) {
			skills.doHover(Skills.INTERFACE_STRENGTH);
			sleep(random(0, 1500));
		}

		if (r == 9) {
			skills.doHover(Skills.INTERFACE_ATTACK);
			sleep(random(0, 1500));
		}
	}

	public void chop() {

		if (getMyPlayer().getAnimation() == -1) {
			final RSObject tree = objects.getNearest(willowTree);
			if (tree != null) {
				status = "Cutting tree";
				if (!tree.isOnScreen() && tree != null) {
					camera.turnTo(tree.getLocation());
					if (calc.distanceTo(tree) > 6) {
						walking.walkTileMM(tree.getLocation());
					}
				}
				tree.doAction("Chop down Willow");
				sleep(random(2000, 3000));
			}
		} else {
			antiban();
		}
	}

	private Image getImage(final String url) {
		try {

			return ImageIO.read(new URL(url));

		} catch (final IOException e) {
			return null;
		}
	}

	@Override
	public int loop() {
		mouse.setSpeed(random(0, 10));

		if (walking.getEnergy() > random(50, 100)) {
			walking.setRun(true);
			sleep(random(700, 800));
		}

		if (inventory.isFull()) {

			status = "Dropping logs";
			inventory.dropAllExcept(hatchets);

		} else {

			chop();

		}
		return 0;
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		if (e.getMessage().contains("some willow logs")) {
			logsCut++;
		}
	}

	@Override
	public void onFinish() {
		log("Thank you for using my script. Please post a progress report.");
	}

	@Override
	public void onRepaint(final Graphics g1) {

		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		final int percent = skills.getPercentToNextLevel(8);

		xpGained = skills.getCurrentExp(Skills.WOODCUTTING) - startXP;

		final Graphics2D g = (Graphics2D) g1;
		g.setColor(color1);
		g.fillRect(723, 27, 18, 15);
		g.fillRect(743, 67, 13, 14);
		g.fillRect(5, 457, 127, 17);
		g.setColor(color2);
		g.fillRoundRect(556, 213, 173, 144, 16, 16);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString("Percent TNL: " + percent + "%", 562, 327);
		g.drawString("Time Running: " + hours + ":" + minutes + ":" + seconds, 562, 347);
		g.drawString("XP Gained: " + xpGained, 562, 305);
		g.drawString("Logs Cut: " + logsCut, 562, 284);
		g.drawString("Status: " + status, 562, 264);
		g.drawString("~SupahScripts~", 590, 236);
		g.drawImage(img1, 595, 360, null);

		g.setColor(Color.green);
		g.drawLine(0, (int) mouse.getLocation().getY(), 800, (int) mouse.getLocation().getY());
		g.drawLine((int) mouse.getLocation().getX(), 0, (int) mouse.getLocation().getX(), 800);
		g.setColor(Color.green);
		g.drawLine(0, (int) mouse.getLocation().getY() + 1, 800, (int) mouse.getLocation().getY() + 1);
		g.drawLine((int) mouse.getLocation().getX() + 1, 0, (int) mouse.getLocation().getX() + 1, 800);

	}

	@Override
	public boolean onStart() {
		grandExchange.lookup(willowLogID).getGuidePrice();
		startXP = skills.getCurrentExp(Skills.WOODCUTTING);
		return true;
	}
}
