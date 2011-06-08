import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

//end
@ScriptManifest(authors = { "Ryan", "Truemenskeet" }, name = "GoofyScriptsMultiCooker", version = 1.0, description = "Cooks food in Al Kharid and Rogues Den", website = "http://www.powerbot.org/vb/showthread.php?t=768104")
public class GoofyScriptsMultiCooker extends Script implements PaintListener,
		MessageListener, MouseListener {

	String antiban = "Idle";
	String status = "Idle";
	String location = "null";
	String runtime = "00:00:00";

	Point fireloc;
	Point rangeloc;

	int rawID = 0;
	public long startTime = 0;
	public long millis = 0;
	public long hours = 0;
	public long minutes = 0;
	public long seconds = 0;
	private int startExp = 0;
	public int expGained = 0;
	public int expHour = 0;
	public int expTNL = 0;
	public int currLevel;
	public int startLevel;
	public int lvlsGained = 0;
	private int percentTNL;
	public int cooked = 0;
	public int burned = 0;
	public int cookedphr = 0;
	public int count = 0;

	public boolean showPaint;
	public boolean fishSelected;

	private final Color color1 = new Color(210, 210, 195);
	private final Color color2 = new Color(0, 0, 0);
	private final Color color3 = new Color(1, 1, 0);

	private final Color color4 = new Color(241, 17, 17);

	private final Color color5 = new Color(77, 194, 40);

	private final Color color6 = new Color(0, 1, 0);

	private final Color color8 = new Color(255, 255, 255, 129);
	private final BasicStroke stroke1 = new BasicStroke(1);
	private final Font font1 = new Font("Arial", 1, 16);

	private final Font font2 = new Font("Arial", 1, 12);
	private final Image img1 = getImage("http://i56.tinypic.com/29mx5ll.png");

	// START: Code generated using Enfilade's Easel
	private Image getImage(final String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (final IOException e) {
			return null;
		}
	}

	private boolean isInterfaceValid(final int interfaceId) {
		return interfaces.getComponent(interfaceId, 0).isValid();
	}

	@Override
	public int loop() {

		if (game.getCurrentTab() == 4) {

			if (isInterfaceValid(740)) {
				interfaces.clickContinue();
				sleep(3000);
			}

			if (objects.getNearest(25730) != null
					|| npcs.getNearest(2271) != null) {
				if (fishSelected == true) {

					if (location == "kharid") {
						getMyPlayer().getLocation();
						final RSItem rawfood = inventory.getItem(rawID);

						antiban = "Idle";

						if (camera.getPitch() < 100) {
							camera.setPitch(100);
						}
						if (!walking.isRunEnabled()
								&& walking.getEnergy() > random(60, 75)) {
							walking.setRun(true);
						}

						if (players.getMyPlayer().isMoving()) {
							sleep(500);
						} else if (inventory.getCount(rawID) > 0
								&& count > inventory.getCount(rawID)) {
							count = inventory.getCount(rawID);
							sleep(3500); // 2000 is an example if your return is
											// 500
							mouseAntiban();
						} else {
							final RSObject booth = objects.getNearest(35648);
							final RSObject range = objects.getTopAt(new RSTile(3271, 3181));

							if (inventory.contains(rawID)) {
								if (range != null) {
									if (range.isOnScreen()) {
										if (isInterfaceValid(905)) {
											interfaces.getComponent(905, 14).doAction("Cook All");
											count = inventory.getCount(rawID);
											sleep(2500);
										} else {
											if (game.getCurrentTab() == 4) {
												status = "Cooking";
												if (!inventory.isItemSelected()) {
													rawfood.doAction("Use");
												} else {
													env.setUserInput(0);
													if (rangeloc != null) {
														if (calc.pointOnScreen(rangeloc)) {
															mouse.move(rangeloc.x, rangeloc.y);
															mouse.click(false);
															sleep(200, 250);
														}
													}
													if (menu.isOpen()) {
														if (menu.contains("-> Range")) {
															menu.clickIndex(menu.getIndex("-> Range"));
															sleep(2500);
															env.setUserInput(2);
														} else {
															int x, y;
															x = random(500, 650);
															y = random(300, 420);
															mouse.move(x, y);
														}
													}
												}
											} else {
												keyboard.pressKey((char) KeyEvent.VK_F1);
												sleep(100, 200);
												keyboard.releaseKey((char) KeyEvent.VK_F1);
											}
										}
									} else {
										status = "Walking to Range";
										walking.walkTileMM(new RSTile(3273, 3180));
										sleep(2250);
									}
								}
							} else {
								if (bank.isOpen()) {
									if (bank.getCount(rawID) == 0) {
										bank.close();
										sleep(1000);
										stopScript(true);
									}
									bank.depositAll();
									sleep(500);
									bank.withdraw(rawID, 0);
									sleep(500);
								} else if (booth != null) {
									if (booth.isOnScreen()) {
										status = "Banking";
										bank.open();
									} else {
										status = "Walking to bank";
										walking.walkTileMM(new RSTile(3267, 3167));
										sleep(2000);
									}
								}
							}

						}

					} else if (location == "den") {

						getMyPlayer().getLocation();
						final RSItem rawfood = inventory.getItem(rawID);

						antiban = "Idle";

						if (camera.getPitch() < 100) {
							camera.setPitch(100);
						}
						if (!walking.isRunEnabled()
								&& walking.getEnergy() > random(60, 75)) {
							walking.setRun(true);
						}

						if (players.getMyPlayer().isMoving()) {
							sleep(500);
						} else if (inventory.getCount(rawID) > 0
								&& count > inventory.getCount(rawID)) {
							count = inventory.getCount(rawID);
							sleep(3500); // 2000 is an example if your return is
											// 500
							mouseAntiban();
						} else {

							objects.getTopAt(new RSTile(3043, 4973));
							final RSNPC bankguy = npcs.getNearest(2271);

							if (inventory.contains(rawID)) {
								if (calc.pointOnScreen(fireloc)) {
									if (isInterfaceValid(905)) {
										interfaces.getComponent(905, 14).doAction("Cook All");
										count = inventory.getCount(rawID);
										sleep(2500);
									} else {
										if (game.getCurrentTab() == 4) {
											status = "Cooking";
											if (!inventory.isItemSelected()) {
												rawfood.doAction("Use");
											} else {
												env.setUserInput(0);
												if (fireloc != null) {
													if (calc.pointOnScreen(fireloc)) {
														mouse.move(fireloc.x, fireloc.y);
														mouse.click(false);
														sleep(200, 250);
													}
												}
												if (menu.isOpen()) {
													if (menu.contains("-> Fire")) {
														menu.clickIndex(menu.getIndex("-> Fire"));
														sleep(2500);
														env.setUserInput(2);
													} else {
														int x, y;
														x = random(500, 650);
														y = random(300, 420);
														mouse.move(x, y);
													}
												}
											}
										} else {
											keyboard.pressKey((char) KeyEvent.VK_F1);
											sleep(100, 200);
											keyboard.releaseKey((char) KeyEvent.VK_F1);
										}
									}
								} else {
									status = "Walking to Fire";
									walking.walkTileMM(new RSTile(3043, 4972));
									sleep(2000);
								}
							} else {
								if (bank.isOpen()) {
									if (bank.getCount(rawID) == 0) {
										bank.close();
										sleep(1000);
										stopScript(true);
									}
									bank.depositAll();
									sleep(500);
									bank.withdraw(rawID, 0);
									sleep(500);
									bank.close();
								} else {
									status = "Banking";
									if (bankguy != null) {
										if (calc.pointOnScreen(bankguy.getScreenLocation())) {
											if (bankguy.doAction("Bank")) {
												sleep(2000);
											}
										} else {
											final RSTile bankguytile = bankguy.getLocation();
											if (bankguytile != null) {
												walking.walkTileMM(bankguytile);
												sleep(2000);
											}
										}
									}
								}
							}
						}

					}

				} else {
					mouseinput();
				}
			}

		} else {
			keyboard.pressKey((char) KeyEvent.VK_F1);
			sleep(100, 200);
			keyboard.releaseKey((char) KeyEvent.VK_F1);
		}

		return random(100, 200);
	}

	// END: Code generated using Enfilade's Easel
	@Override
	public void messageReceived(final MessageEvent e) {
		if (e.getMessage().contains("You manage to cook")) {
			cooked++;
		}

		if (e.getMessage().contains("You accidentally burn")) {
			burned++;
		}
		if (e.getMessage().contains("You successfully cook")) {
			cooked++;
		}
		if (e.getMessage().contains("You roast a")) {
			cooked++;
		}
	}

	public void mouseAntiban() {
		final int r = random(1, 28);
		if (r == 4) {
			final int t = random(1, 4);
			if (t == 2) {
				antiban = "Friend's List";
				game.openTab(9);
				wiggle();
				sleep(random(2100, 3400));
			}
		} else if (r == 6 || r == 8 || r == 10) {
			antiban = "Mouse Wiggle";
			wiggle();
		} else if (r == 12 || r == 14 || r == 13) {
			antiban = "Angle Adjustment";
			camera.setAngle(random(1, 359));
		} else if (r == 16 || r == 18) {
			antiban = "Mouse off screen";
			mouse.moveOffScreen();
		} else if (r == 20) {
			final int t = random(1, 4);
			if (t == 2) {
				antiban = "Checking Exp";
				game.openTab(2);
				skills.doHover(Skills.INTERFACE_COOKING);
				sleep(random(2100, 3400));
			}
		} else if (r == 24 || r == 26) {
			antiban = "Mouse Speed";
			MouseSpeed();
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (fishSelected == true && location != "null") {
			final RSComponent inter = interfaces.get(137).getComponent(0);
			if (inter.getArea().contains(e.getPoint())) {
				showPaint = !showPaint;
			}
			if (showPaint == true) {
				if (e.getPoint().x > 4 && e.getPoint().x < 79
						&& e.getPoint().y > 323 && e.getPoint().y < 337) {
					fishSelected = false;
					mouseinput();
				}
			}
		} else if (fishSelected == false) {

			// fish1
			if (e.getPoint().x > 35 && e.getPoint().x < 135
					&& e.getPoint().y > 373 && e.getPoint().y < 393) {
				fishSelected = true;
				rawID = 317;
			}
			if (e.getPoint().x > 150 && e.getPoint().x < 250
					&& e.getPoint().y > 373 && e.getPoint().y < 393) {
				fishSelected = true;
				rawID = 13435;
			}
			if (e.getPoint().x > 265 && e.getPoint().x < 365
					&& e.getPoint().y > 373 && e.getPoint().y < 393) {
				fishSelected = true;
				rawID = 321;
			}
			if (e.getPoint().x > 380 && e.getPoint().x < 480
					&& e.getPoint().y > 373 && e.getPoint().y < 393) {
				fishSelected = true;
				rawID = 327;
			}

			// fish2
			if (e.getPoint().x > 35 && e.getPoint().x < 135
					&& e.getPoint().y > 398 && e.getPoint().y < 418) {
				fishSelected = true;
				rawID = 345;
			}
			if (e.getPoint().x > 150 && e.getPoint().x < 250
					&& e.getPoint().y > 398 && e.getPoint().y < 418) {
				fishSelected = true;
				rawID = 335;
			}
			if (e.getPoint().x > 265 && e.getPoint().x < 365
					&& e.getPoint().y > 398 && e.getPoint().y < 418) {
				fishSelected = true;
				rawID = 331;
			}
			if (e.getPoint().x > 380 && e.getPoint().x < 480
					&& e.getPoint().y > 398 && e.getPoint().y < 418) {
				fishSelected = true;
				rawID = 359;
			}

			// fish3
			if (e.getPoint().x > 15 && e.getPoint().x < 115
					&& e.getPoint().y > 423 && e.getPoint().y < 443) {
				fishSelected = true;
				rawID = 377;
			}
			if (e.getPoint().x > 130 && e.getPoint().x < 230
					&& e.getPoint().y > 423 && e.getPoint().y < 443) {
				fishSelected = true;
				rawID = 363;
			}
			if (e.getPoint().x > 245 && e.getPoint().x < 345
					&& e.getPoint().y > 423 && e.getPoint().y < 443) {
				fishSelected = true;
				rawID = 371;
			}
			if (e.getPoint().x > 360 && e.getPoint().x < 460
					&& e.getPoint().y > 423 && e.getPoint().y < 443) {
				fishSelected = true;
				rawID = 7944;
			}

			// fish4

			if (e.getPoint().x > 130 && e.getPoint().x < 230
					&& e.getPoint().y > 448 && e.getPoint().y < 468) {
				fishSelected = true;
				rawID = 383;
			}
			if (e.getPoint().x > 245 && e.getPoint().x < 345
					&& e.getPoint().y > 448 && e.getPoint().y < 468) {
				fishSelected = true;
				rawID = 15270;
			}

		} else if (location == "null") {

			if (e.getPoint().x > 150 && e.getPoint().x < 250
					&& e.getPoint().y > 373 && e.getPoint().y < 393) {
				location = "kharid";
				showPaint = true;
				env.setUserInput(2);
			}
			if (e.getPoint().x > 275 && e.getPoint().x < 375
					&& e.getPoint().y > 373 && e.getPoint().y < 393) {
				location = "den";
				showPaint = true;
				env.setUserInput(2);
			}

		}

	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	private void mouseinput() {
		env.setUserInput(1);
	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void MouseSpeed() {
		final int rand = random(1, 5);
		if (rand == 1) {
			mouse.setSpeed(random(7, 10));
		}
	}

	@Override
	public void onFinish() {
		log("Thanks for using Goofy Script's Multi Location Cooker");
		log("You cooked " + cooked + " fish" + " and gained " + expGained
				+ "exp");
		env.saveScreenshot(false);
	}

	@Override
	public void onRepaint(final Graphics g1) {

		final Graphics2D g = (Graphics2D) g1;
		g.setColor(color8);

		fireloc = calc.tileToScreen(new RSTile(3043, 4973), 0.5, 0.5, -50);
		rangeloc = calc.tileToScreen(new RSTile(3271, 3180), 0.5, 0.35, -200);
		if (calc.pointOnScreen(fireloc)) {
			g.drawOval(fireloc.x - 7, fireloc.y - 7, 14, 14);
			g.fillOval(fireloc.x - 5, fireloc.y - 5, 10, 10);
		} else if (calc.pointOnScreen(rangeloc)) {
			g.drawOval(rangeloc.x - 7, rangeloc.y - 7, 14, 14);
			g.fillOval(rangeloc.x - 5, rangeloc.y - 5, 10, 10);
		}

		if (fishSelected == true && location != "null") {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
				sleep(100);
			}

			if (showPaint) {

				millis = System.currentTimeMillis() - startTime;
				hours = millis / (1000 * 60 * 60);
				millis -= hours * 1000 * 60 * 60;
				minutes = millis / (1000 * 60);
				millis -= minutes * 1000 * 60;
				seconds = millis / 1000;

				runtime = Timer.format(System.currentTimeMillis() - startTime);

				expGained = skills.getCurrentExp(Skills.COOKING) - startExp;
				expHour = (int) (expGained * 3600000D / (System.currentTimeMillis() - startTime));
				cookedphr = (int) (cooked * 3600000D / (System.currentTimeMillis() - startTime));
				expTNL = skills.getExpToNextLevel(Skills.COOKING);
				currLevel = skills.getCurrentLevel(Skills.COOKING);
				lvlsGained = currLevel - startLevel;
				percentTNL = skills.getPercentToNextLevel(Skills.COOKING);
				final int percentBar = (int) (skills.getPercentToNextLevel(Skills.COOKING) * 3.38);
				g.setColor(color1);
				g.fillRect(6, 344, 505, 129);
				g.setColor(color2);
				g.setStroke(stroke1);
				g.drawRect(6, 344, 505, 129);
				g.drawLine(7, 370, 511, 370);
				g.drawImage(img1, 363, 299, null);
				g.setFont(font1);
				g.setColor(color3);
				g.drawString("Goofy Scripts Multi Cooker", 9, 365);
				g.setFont(font2);
				g.drawString("Running Time: " + runtime, 9, 385);
				g.drawString("Current Level: " + currLevel + " (+" + lvlsGained
						+ ")", 9, 402);
				g.drawString("Exp Gained: " + expGained, 9, 419);
				g.drawString("Exp P/Hr: " + expHour, 9, 436);
				g.drawString("Exp TNL: " + expTNL, 9, 453);
				g.drawString("Status: " + status, 184, 385);
				g.drawString("Antiban: " + antiban, 184, 402);
				g.drawString("Total Cooked: " + cooked, 184, 419);
				g.drawString("Cooked P/Hr: " + cookedphr, 184, 436);
				g.drawString("Total Burned: " + burned, 184, 453);
				g.setColor(color4);
				g.fillRect(6, 456, 338, 16);
				g.setColor(color2);
				g.drawRect(6, 456, 338, 16);
				g.setColor(color5);
				g.fillRect(6, 456, percentBar, 16);
				g.setColor(color2);
				g.drawRect(6, 456, percentBar, 16);
				g.setColor(color6);
				g.drawString(percentTNL + "%", 158, 470);

				g.setColor(color1);
				g.fillRect(4, 323, 75, 14);
				g.setColor(color3);
				g.drawString("Change Fish", 7, 335);

			} else {

			}
		} else if (fishSelected == false) {
			g.setColor(color1);
			g.fillRect(6, 344, 505, 129);
			g.setColor(color2);
			g.setStroke(stroke1);
			g.drawRect(6, 344, 505, 129);
			g.setFont(font1);
			g.setColor(color3);
			g.drawString("Please select a fish.", 190, 365);

			if (mouse.getLocation().getX() > 35
					&& mouse.getLocation().getX() < 135
					&& mouse.getLocation().getY() > 373
					&& mouse.getLocation().getY() < 393) {
				g.setColor(color5);
				g.fillRect(35, 373, 100, 20);
			} else if (mouse.getLocation().getX() > 150
					&& mouse.getLocation().getX() < 250
					&& mouse.getLocation().getY() > 373
					&& mouse.getLocation().getY() < 393) {
				g.setColor(color5);
				g.fillRect(150, 373, 100, 20);
			} else if (mouse.getLocation().getX() > 265
					&& mouse.getLocation().getX() < 365
					&& mouse.getLocation().getY() > 373
					&& mouse.getLocation().getY() < 393) {
				g.setColor(color5);
				g.fillRect(265, 373, 100, 20);
			} else if (mouse.getLocation().getX() > 380
					&& mouse.getLocation().getX() < 480
					&& mouse.getLocation().getY() > 373
					&& mouse.getLocation().getY() < 393) {
				g.setColor(color5);
				g.fillRect(380, 373, 100, 20);

			} else if (mouse.getLocation().getX() > 35
					&& mouse.getLocation().getX() < 135
					&& mouse.getLocation().getY() > 398
					&& mouse.getLocation().getY() < 418) {
				g.setColor(color5);
				g.fillRect(35, 398, 100, 20);
			} else if (mouse.getLocation().getX() > 150
					&& mouse.getLocation().getX() < 250
					&& mouse.getLocation().getY() > 398
					&& mouse.getLocation().getY() < 418) {
				g.setColor(color5);
				g.fillRect(150, 398, 100, 20);
			} else if (mouse.getLocation().getX() > 265
					&& mouse.getLocation().getX() < 365
					&& mouse.getLocation().getY() > 398
					&& mouse.getLocation().getY() < 418) {
				g.setColor(color5);
				g.fillRect(265, 398, 100, 20);
			} else if (mouse.getLocation().getX() > 380
					&& mouse.getLocation().getX() < 480
					&& mouse.getLocation().getY() > 398
					&& mouse.getLocation().getY() < 418) {
				g.setColor(color5);
				g.fillRect(380, 398, 100, 20);

			} else if (mouse.getLocation().getX() > 35
					&& mouse.getLocation().getX() < 135
					&& mouse.getLocation().getY() > 423
					&& mouse.getLocation().getY() < 443) {
				g.setColor(color5);
				g.fillRect(35, 423, 100, 20);
			} else if (mouse.getLocation().getX() > 150
					&& mouse.getLocation().getX() < 250
					&& mouse.getLocation().getY() > 423
					&& mouse.getLocation().getY() < 443) {
				g.setColor(color5);
				g.fillRect(150, 423, 100, 20);
			} else if (mouse.getLocation().getX() > 265
					&& mouse.getLocation().getX() < 365
					&& mouse.getLocation().getY() > 423
					&& mouse.getLocation().getY() < 443) {
				g.setColor(color5);
				g.fillRect(265, 423, 100, 20);
			} else if (mouse.getLocation().getX() > 380
					&& mouse.getLocation().getX() < 480
					&& mouse.getLocation().getY() > 423
					&& mouse.getLocation().getY() < 443) {
				g.setColor(color5);
				g.fillRect(380, 423, 100, 20);

			} else if (mouse.getLocation().getX() > 150
					&& mouse.getLocation().getX() < 250
					&& mouse.getLocation().getY() > 448
					&& mouse.getLocation().getY() < 468) {
				g.setColor(color5);
				g.fillRect(150, 448, 100, 20);
			} else if (mouse.getLocation().getX() > 265
					&& mouse.getLocation().getX() < 365
					&& mouse.getLocation().getY() > 448
					&& mouse.getLocation().getY() < 468) {
				g.setColor(color5);
				g.fillRect(265, 448, 100, 20);
			}

			g.setColor(color3);
			g.drawString("Shrimps", 54, 389);
			g.drawRect(35, 373, 100, 20);
			g.drawString("Crayfish", 170, 389);
			g.drawRect(150, 373, 100, 20);
			g.drawString("Anchovies", 275, 389);
			g.drawRect(265, 373, 100, 20);
			g.drawString("Sardine", 400, 389);
			g.drawRect(380, 373, 100, 20);

			g.drawString("Herring", 59, 414);
			g.drawRect(35, 398, 100, 20);
			g.drawString("Trout", 180, 414);
			g.drawRect(150, 398, 100, 20);
			g.drawString("Salmon", 290, 414);
			g.drawRect(265, 398, 100, 20);
			g.drawString("Tuna", 410, 414);
			g.drawRect(380, 398, 100, 20);

			g.drawString("Lobster", 59, 439);
			g.drawRect(35, 423, 100, 20);
			g.drawString("Bass", 180, 439);
			g.drawRect(150, 423, 100, 20);
			g.drawString("Swordfish", 277, 439);
			g.drawRect(265, 423, 100, 20);
			g.drawString("Monkfish", 400, 439);
			g.drawRect(380, 423, 100, 20);

			g.drawString("Shark", 180, 464);
			g.drawRect(150, 448, 100, 20);
			g.drawString("Rocktail", 285, 464);
			g.drawRect(265, 448, 100, 20);
		} else if (location == "null") {
			g.setColor(color1);
			g.fillRect(6, 344, 505, 129);
			g.setColor(color2);
			g.setStroke(stroke1);
			g.drawRect(6, 344, 505, 129);
			g.setFont(font1);
			g.setColor(color3);
			g.drawString("Please select a location.", 170, 365);

			if (mouse.getLocation().getX() > 150
					&& mouse.getLocation().getX() < 250
					&& mouse.getLocation().getY() > 373
					&& mouse.getLocation().getY() < 393) {
				g.setColor(color5);
				g.fillRect(150, 373, 100, 20);
			} else if (mouse.getLocation().getX() > 275
					&& mouse.getLocation().getX() < 375
					&& mouse.getLocation().getY() > 373
					&& mouse.getLocation().getY() < 393) {
				g.setColor(color5);
				g.fillRect(275, 373, 100, 20);
			}

			g.setColor(color3);
			g.drawString("Al Kharid", 170, 389);
			g.drawRect(150, 373, 100, 20);
			g.drawString("Rogues Den", 280, 389);
			g.drawRect(275, 373, 100, 20);

		}
		g.setColor(color8);
		final int[] X = new int[] { 515, (int) mouse.getLocation().getX(), 800,
				800 };
		final int[] Y = new int[] { 0, (int) mouse.getLocation().getY(), 170, 0 };
		g.fillPolygon(X, Y, X.length);
	}

	@Override
	public boolean onStart() {
		log("Welcome to Goofy's Scripts Multi Cooker");
		fishSelected = false;
		startExp = skills.getCurrentExp(Skills.COOKING);
		startLevel = skills.getCurrentLevel(Skills.COOKING);
		mouse.setSpeed(8);
		return true;
	}

	public void wiggle() {
		int x, y;
		x = random(500, 650);
		y = random(300, 420);
		mouse.move(x, y);
		sleep(25, 674);
		x = random(500, 650);
		y = random(300, 420);
		mouse.move(x, y);
		sleep(1, 240);
	}

}
