import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Wei Su" }, name = "Ultimate Cannon ball smelter", version = 0.1, description = "Balls deep", keywords = {
		"cannon", "balls", "money" }, website = "http://www.powerbot.org/vb/showthread.php?t=783447")
/* Save as script.java - who could get that wrong! :) */
public class UltimateAmmo extends Script implements PaintListener,
		MessageListener, ActionListener, MouseMotionListener, MouseListener {

	public int steelBar = 2353;
	public int mould = 4;
	public int bankBoothID = 26972;
	public int furnaceID = 26814;

	public int nullCheck;

	RSArea bankarea = new RSArea(3094, 3495, 3098, 3499);
	RSTile bankTile = new RSTile(3097, 3496);

	RSArea furnace = new RSArea(3106, 3498, 3110, 3502);
	RSTile furnaceTile = new RSTile(3108, 3501);

	public String status;

	public long startTime, millis, hours, minutes, seconds, last;
	public int startExp;
	public int currExp;
	public int gainedExp;

	private final Color color1 = new Color(0, 0, 0);

	private final Color color2 = new Color(255, 255, 255);

	private final Font font1 = new Font("Arial", 0, 12);

	private final Font font2 = new Font("Arial", 0, 11);

	private final Image img1 = getImage("http://imageshack.us/m/705/37/cannonpaint.png");

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public int antibanlist() {
		switch (random(0, 350)) {
		case 0:
			chooserandomAFK();
			break;
		case 1:
		case 2:
		case 3:
			chooserandomAFK();
			break;
		case 4:
			mouse.moveSlightly();
			break;
		case 5:
			chooserandomAFK();
			break;
		case 6:
		case 7:

			break;
		case 8:
			superAntiMoveMouse();
			break;
		case 9:
			randomXPcheck();
			break;
		case 10:
			randomtab();
			break;
		case 11:
		case 12:
			randomtab();
			break;
		case 13:
			superAntiMoveMouse();
			break;
		case 14:
			randomXPcheck();
			break;
		case 15:
		case 16:
		case 17:
			break;
		default:
			break;
		}
		return 100;
	}

	public void chooserandomAFK() {
		switch (random(0, 4)) {
		case 0:
			sleep(random(500, 900));
			break;
		case 1:
			sleep(random(400, 1000));
			break;
		case 2:
			sleep(random(1000, 2000));
			break;
		case 3:
			sleep(random(1000, 3000));
			break;
		case 4:
			log("Not doing AFK");
			break;
		}
	}

	public int getCase() {
		if (needToFurnace() && !inFurnace()) {
			return 1;
		}

		if (inventory.contains(steelBar) && inFurnace()) {
			return 2;
		}

		if (needToBank() && !inBank()) {
			return 3;
		}

		if (needToBank() && inBank()) {
			return 4;
		}

		return -1;
	}

	// START: Code generated using Enfilade's Easel
	private Image getImage(final String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (final IOException e) {
			return null;
		}
	}

	public boolean inBank() {
		if (bankarea.contains(getMyPlayer().getLocation())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean inFurnace() {
		if (furnace.contains(getMyPlayer().getLocation())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int loop() {
		try {
			if (getMyPlayer().getAnimation() == -1) {
				nullCheck++;
			} else {
				nullCheck = 0;
			}

			if (!inFurnace()) {
				nullCheck = 50;
			}

			switch (getCase()) {
			case 1:
				walkToFurnace();
				status = "Walking to the furnace";
				return random(50, 100);

			case 2:
				useFurnace();
				status = "Using furnace";
				return random(50, 100);

			case 3:
				walkToBank();
				status = "Walking to bank";
				return random(50, 100);

			case 4:
				useBank();
				status = "Using bank";
			}
		} catch (final Exception ignore) {
		}

		return random(50, 100);

	}

	@Override
	public void messageReceived(final MessageEvent arg0) {

	}

	@Override
	public void mouseClicked(final MouseEvent e) {// this is the mouse listener,
													// it listen's for the
													// click.

	}

	// END: Code generated using Enfilade's Easel

	@Override
	public void mouseDragged(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	// START: Code generated using Enfilade's Easel

	@Override
	public void mouseExited(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean needToBank() {
		if (inventory.contains(steelBar)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean needToFurnace() {
		if (inventory.isFull()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onFinish() {

	}

	@Override
	public void onRepaint(final Graphics g1) {
		double ballsMade;
		currExp = skills.getCurrentExp(Skills.SMITHING);
		gainedExp = currExp - startExp;
		millis = System.currentTimeMillis() - startTime;
		hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		seconds = millis / 1000;
		ballsMade = gainedExp / 25.6 * 4;

		final long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
		if (totalSeconds == 0) {

		} else {
		}
		float xpsec = 0;

		if ((minutes > 0 || hours > 0 || seconds > 0) && gainedExp > 0) {
			xpsec = (float) gainedExp
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
		}

		float glasssec = 0;
		if ((minutes > 0 || hours > 0 || seconds > 0) && ballsMade > 0) {

			glasssec = (float) ballsMade
					/ (seconds + minutes * 60 + hours * 60 * 60);
		}

		final float xpmin = xpsec * 60;
		final float xphour = xpmin * 60;
		final float glassmin = glasssec * 60;
		final float glasshour = glassmin * 60;

		final Graphics2D g = (Graphics2D) g1;
		g.drawImage(img1, 520, 164, null);
		g.setFont(font1);
		g.setColor(color1);
		g.drawString("Time elapse: " + hours + ":" + minutes + ":" + seconds, 552, 285);
		g.drawString("Balls made: " + ballsMade, 552, 305);
		g.drawString("Balls/hour: " + glasshour, 552, 325);
		g.drawString("Exp gained: " + gainedExp, 553, 345);
		g.drawString("Exp/hour: " + xphour, 551, 365);
		g.setFont(font2);
		g.setColor(color2);
		g.drawString("If you have a suggestion feel free to email me on wei@powerbot.org or visit me at links.powerbot.org/wei", 4, 333);
	}

	// END: Code generated using Enfilade's Easel

	@Override
	public boolean onStart() {
		startTime = System.currentTimeMillis();
		startExp = skills.getCurrentExp(Skills.SMITHING);

		return true;
	}

	public void randomtab() {
		switch (random(0, 12)) {
		case 0:
			game.openTab(Game.TAB_STATS);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 1:
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 2:
			game.openTab(Game.TAB_CLAN);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 3:
			game.openTab(Game.TAB_FRIENDS);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 4:

		case 5:
			game.openTab(Game.TAB_EQUIPMENT);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 6:
			game.openTab(Game.TAB_MAGIC);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 7:
			game.openTab(Game.TAB_QUESTS);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 8:

		case 9:
			game.openTab(Game.TAB_NOTES);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 10:
			game.openTab(Game.TAB_PRAYER);
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 11:
			game.openTab(Game.TAB_MUSIC);
			game.openTab(Game.TAB_INVENTORY);
			break;
		}
	}

	public void randomXPcheck() {
		game.openTab(Game.TAB_STATS);
		switch (random(0, 20)) {
		case 0:
			skills.doHover(Skills.INTERFACE_FISHING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 1:
			skills.doHover(Skills.INTERFACE_WOODCUTTING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 2:
			skills.doHover(Skills.INTERFACE_ATTACK);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 3:
			skills.doHover(Skills.INTERFACE_STRENGTH);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 4:
			skills.doHover(Skills.INTERFACE_COOKING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 5:
			skills.doHover(Skills.INTERFACE_RANGE);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 6:
			skills.doHover(Skills.INTERFACE_FIREMAKING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 7:
			skills.doHover(Skills.INTERFACE_CONSTRUCTION);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 8:
			skills.doHover(Skills.INTERFACE_RUNECRAFTING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 9:
			skills.doHover(Skills.INTERFACE_SUMMONING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 10:
			skills.doHover(Skills.INTERFACE_SLAYER);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 11:
			skills.doHover(Skills.INTERFACE_SMITHING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 12:
			skills.doHover(Skills.INTERFACE_FARMING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 13:
			skills.doHover(Skills.INTERFACE_AGILITY);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 14:
			skills.doHover(Skills.INTERFACE_THIEVING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 15:
			skills.doHover(Skills.INTERFACE_HUNTER);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 16:
			skills.doHover(Skills.INTERFACE_MINING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 17:
			skills.doHover(Skills.INTERFACE_SMITHING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 18:
			skills.doHover(Skills.INTERFACE_MAGIC);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 19:
			skills.doHover(Skills.INTERFACE_FLETCHING);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		case 20:
			skills.doHover(Skills.INTERFACE_PRAYER);
			sleep(random(200, 300));
			game.openTab(Game.TAB_INVENTORY);
			break;
		}
	}

	public void superAntiMoveMouse() {
		switch (random(0, 10)) {
		case 0:
			log("Doing superAnti! Wiggling mouse a lot");
			mouse.setSpeed(random(6, 9));
			mouse.moveSlightly();
			mouse.moveSlightly();
			mouse.moveSlightly();
			break;
		case 1:
			log("Doing superAnti! Wiggling mouse ");
			mouse.setSpeed(random(6, 9));
			mouse.moveSlightly();
			mouse.moveSlightly();
			break;
		case 2:
			log("Doing superAnti! Wiggling mouse a lot");
			mouse.setSpeed(random(6, 9));
			mouse.moveSlightly();
			mouse.moveSlightly();
			mouse.moveSlightly();
			mouse.moveSlightly();
			mouse.moveSlightly();
			mouse.moveSlightly();
			break;
		}
	}

	public void useBank() {
		final RSObject bankBooth = objects.getNearest(bankBoothID);
		camera.turnTo(bankBooth);
		if (bank.isOpen()) {
			if (bank.getCount(steelBar) < 27) {
				env.saveScreenshot(true);
				stopScript(true);
			}
			if (inventory.contains(mould)) {
				if (inventory.getCount(steelBar) < 27) {
					bank.depositAllExcept(mould);
					bank.withdraw(steelBar, 27);
				}
			} else {
				bank.depositAll();
				bank.withdraw(mould, 1);
			}
		} else {
			bankBooth.doAction("Use-q");
			sleep(random(1200, 1300));
		}
	}

	public void useFurnace() {
		Point test;
		final RSObject furnaces = objects.getNearest(furnaceID);
		final RSObject bankBooth = objects.getNearest(bankBoothID);
		camera.turnTo(bankBooth);
		camera.setPitch(100);
		if (nullCheck > 25) {
			test = furnaces.getModel().getCentralPoint();
			inventory.selectItem(steelBar);
			mouse.move(test);
			mouse.click(true);
			interfaces.getComponent(905, 14).getComponent(57).doClick();
			nullCheck = 0;
			antibanlist();
			sleep(random(1200, 1400));
		}

	}

	public void walkToBank() {
		RSPath path = null;
		if (path == null) {
			path = walking.getPath(bankTile);
		}
		path.traverse();
	}

	public void walkToFurnace() {
		RSPath path = null;
		if (path == null) {
			path = walking.getPath(furnaceTile);
		}
		path.traverse();
	}

}
