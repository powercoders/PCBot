package org.rsbot.script.wrappers;

import org.rsbot.client.Model;
import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;

import java.awt.*;

public abstract class RSCharacter extends MethodProvider implements RSTarget {
	public RSCharacter(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Retrieves a reference to the client accessor. For internal use. The
	 * reference should be stored in a SoftReference by subclasses to allow for
	 * garbage collection when appropriate.
	 *
	 * @return The client accessor.
	 */
	protected abstract org.rsbot.client.RSCharacter getAccessor();

	/**
	 * Performs an action on a humanoid character (tall and skinny).
	 *
	 * @param action The action of the menu entry to be clicked (if available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 */
	public boolean interact(final String action) {
		return interact(action, null);
	}

	/**
	 * Performs an action on a humanoid character (tall and skinny).
	 *
	 * @param action The action of the menu entry to be clicked (if available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 * @see org.rsbot.script.wrappers.RSCharacter#interact(String)
	 */
	@Deprecated
	public boolean doAction(final String action) {
		return interact(action);
	}

	/**
	 * Performs an action on a humanoid character (tall and skinny).
	 *
	 * @param action The action of the menu entry to be clicked (if available).
	 * @param option The option of the menu entry to be clicked (if available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 */
	public boolean interact(final String action, final String option) {
		if (isValid()) {
			final RSModel model = getModel();
			if (model != null) {
				return model.interact(action, option);
			}
			try {
				Point screenLoc;
				for (int i = 0; i < 10; i++) {
					screenLoc = getScreenLocation();
					if (!isValid() || !methods.calc.pointOnScreen(screenLoc)) {
						break;
					}
					if (!methods.mouse.getLocation().equals(screenLoc) &&
							methods.menu.doAction(action, option)) {
						return true;
					}
					methods.mouse.move(screenLoc);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Performs an action on a humanoid character (tall and skinny).
	 *
	 * @param action The action of the menu entry to be clicked (if available).
	 * @param option The option of the menu entry to be clicked (if available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 * @see org.rsbot.script.wrappers.RSCharacter#interact(String, String)
	 */
	@Deprecated
	public boolean doAction(final String action, final String option) {
		return interact(action, option);
	}

	public int getAnimation() {
		return getAccessor().getAnimation();
	}

	public int getGraphic() {
		return getAccessor().getGraphicsData()[0].getID();
	}

	public int getHeight() {
		return getAccessor().getHeight();
	}

	/**
	 * @return The % of HP remaining
	 */
	public int getHPPercent() {
		return isInCombat() ? getAccessor().getHPRatio() * 100 / 255 : 100;
	}

	public RSCharacter getInteracting() {
		final int interact = getAccessor().getInteracting();
		if (interact == -1) {
			return null;
		}
		if (interact < 32768) {
			final Node node = methods.nodes.lookup(methods.client.getRSNPCNC(),
					interact);
			if (node == null || !(node instanceof RSNPCNode)) {
				return null;
			}
			return new RSNPC(methods, ((RSNPCNode) node).getRSNPC());
		} else {
			int index = interact - 32768;
			if (index == methods.client.getSelfInteracting()) {
				index = 2047;
			}
			return new RSPlayer(methods,
					methods.client.getRSPlayerArray()[index]);
		}
	}

	public int getLevel() {
		return -1; // should be overridden too
	}

	public RSTile getLocation() {
		final org.rsbot.client.RSCharacter c = getAccessor();
		if (c == null) {
			return new RSTile(-1, -1);
		}
		final int x = methods.client.getBaseX() + (c.getX() >> 9);
		final int y = methods.client.getBaseY() + (c.getY() >> 9);
		return new RSTile(x, y, methods.game.getPlane());
	}

	public String getMessage() {
		return getAccessor().getMessage();
	}

	/**
	 * Gets the minimap location, of the character. Note: This does work when
	 * it's walking!
	 *
	 * @return The location of the character on the minimap.
	 */
	public Point getMinimapLocation() {
		final org.rsbot.client.RSCharacter c = getAccessor();
		final int cX = methods.client.getBaseX() + (c.getX() / 32 - 2) / 4;
		final int cY = methods.client.getBaseY() + (c.getY() / 32 - 2) / 4;
		return methods.calc.worldToMinimap(cX, cY);
	}

	public RSModel getModel() {
		final org.rsbot.client.RSCharacter c = getAccessor();
		if (c != null) {
			final Model model = c.getModel();
			if (model != null) {
				return new RSCharacterModel(methods, model, c);
			}
		}
		return null;
	}

	public String getName() {
		return ""; // should be overridden, obviously
	}

	public int getOrientation() {
		return (int) (270 - (getAccessor().getOrientation() & 0x3fff) / 45.51) % 360;
	}

	public Point getScreenLocation() {
		final org.rsbot.client.RSCharacter c = getAccessor();
		final RSModel model = getModel();
		if (model == null) {
			return methods.calc.groundToScreen(c.getX(), c.getY(),
					c.getHeight() / 2);
		} else {
			return model.getPoint();
		}
	}

	/**
	 * Hovers this Player/NPC
	 */
	public void hover() {
		getModel().hover();
	}

	public boolean isInCombat() {
		return methods.game.isLoggedIn()
				&& methods.client.getLoopCycle() < getAccessor()
				.getLoopCycleStatus();
	}

	/**
	 * Determines whether the character is dead or dying
	 *
	 * @return <tt>true</tt> if the character is dead/dying; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isDead() {
		return !isValid() || getAnimation() == 836;
	}

	public boolean isInteractingWithLocalPlayer() {
		return getAccessor().getInteracting() - 32768 == methods.client
				.getSelfInteracting();
	}

	public boolean isMoving() {
		return getAccessor().isMoving() != 0;
	}

	public boolean isOnScreen() {
		final RSModel model = getModel();
		if (model == null) {
			return methods.calc.tileOnScreen(getLocation());
		} else {
			return methods.calc.pointOnScreen(model.getPoint());
		}
	}

	public boolean isValid() {
		return getAccessor() != null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof RSCharacter) {
			final RSCharacter cha = (org.rsbot.script.wrappers.RSCharacter) obj;
			return cha.getAccessor() == getAccessor();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(getAccessor());
	}

	@Override
	public String toString() {
		final RSCharacter inter = getInteracting();
		final String msg = getMessage();
		return "[anim=" + getAnimation()
				+ (msg != null ? ",msg=" + getMessage() : "")
				+ ",interact=" + (inter == null ? "null" :
				inter.isValid() ? inter.getName() : "Invalid") + "]";
	}

	public Point getPoint() {
		return getScreenLocation();
	}

	public boolean contains(int x, int y) {
		RSModel model = getModel();
		if (model != null) {
			return model.contains(x, y);
		}
		return getScreenLocation().distance(x, y) < random(0, 8);
	}
}