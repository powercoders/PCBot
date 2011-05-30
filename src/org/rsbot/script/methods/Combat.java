package org.rsbot.script.methods;

import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSNPC;

/**
 * Combat related operations.
 */
public class Combat extends MethodProvider {
	public Combat(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Eats at the desired HP %.
	 *
	 * @param percent The health percentage to eat at; 10%-90%
	 * @param foods   Array of Foods we can eat.
	 * @return <tt>true</tt> once we eaten to the health % (percent); otherwise
	 *         <tt>false</tt>.
	 */
	public boolean eat(final int percent, final int... foods) {
		final int firstPercent = getHealth();
		for (final int food : foods) {
			if (!methods.inventory.contains(food)) {
				continue;
			}
			if (methods.inventory.getItem(food).interact("Eat")) {
				for (int i = 0; i < 100; i++) {
					sleep(random(100, 300));
					if (firstPercent < percent) {
						break;
					}
				}
			}
			if (getHealth() >= percent) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the attack mode.
	 *
	 * @return The current fight mode setting.
	 */
	public int getFightMode() {
		return methods.settings.getSetting(Settings.SETTING_COMBAT_STYLE);
	}

	/**
     * Gets the current player's health as a percentage of full health.
     *
     * @return The current percentage health remaining.
     */
    public int getHealth() {
    	return getLifePoints() * 10 / methods.skills.getRealLevel(Skills.CONSTITUTION);
    }

	/**
     * Gets the current player's life points.
     *
     * @return The current life points if the interface is valid; otherwise 0.
     */
    public int getLifePoints() {
    	try {
    		return Integer.parseInt(methods.interfaces.get(748).getComponent(8).getText());
    	} catch (final NumberFormatException ex) {
    		return 0;
    	}
    }

	/**
     * Gets the current player's prayer points.
     *
     * @return The current prayer points if the interface is valid; otherwise 0.
     */
    public int getPrayerPoints() {
    	try {
    		return Integer.parseInt(methods.interfaces.get(Game.INTERFACE_PRAYER_ORB).getComponent(4).getText().trim());
    	} catch (final NumberFormatException ex) {
    		return 0;
    	}
    }

	/**
     * Gets the special bar energy amount.
     *
     * @return The current spec energy.
     */
    public int getSpecialBarEnergy() {
    	return methods.settings.getSetting(300) / 10;
    }

	/**
	 * Gets the current Wilderness Level. Written by Speed.
	 *
	 * @return The current wilderness level otherwise, 0.
	 */
	public int getWildernessLevel() {
		return methods.interfaces.get(381).getComponent(2).isValid() ? Integer.parseInt(methods.interfaces.get(381).getComponent(2).getText().replace("Level: ", "").trim()) : 0;
	}

	/**
     * Checks if your character is interacting with an Npc.
     *
     * @param npc The Npc we want to fight.
     * @return <tt>true</tt> if interacting; otherwise <tt>false</tt>.
     */
    public boolean isAttacking(final RSNPC npc) {
    	final RSCharacter interact = methods.players.getMyPlayer().getInteracting();
    	return interact != null && interact.equals(npc);
    }

	/**
     * Returns whether or not the auto-retaliate option is enabled.
     *
     * @return <tt>true</tt> if retaliate is enabled; otherwise <tt>false</tt>.
     */
    public boolean isAutoRetaliateEnabled() {
    	return methods.settings.getSetting(Settings.SETTING_AUTO_RETALIATE) == 0;
    }

	/**
	 * Returns whether or not we're poisoned.
	 *
	 * @return <tt>true</tt> if poisoned; otherwise <tt>false</tt>.
	 */
	public boolean isPoisoned() {
		return methods.settings.getSetting(102) > 0 || methods.interfaces.getComponent(748, 4).getBackgroundColor() == 1801;
	}

	/**
	 * Returns whether or not the special-attack option is enabled.
	 *
	 * @return <tt>true</tt> if special is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isSpecialEnabled() {
		return methods.settings.getSetting(Settings.SETTING_SPECIAL_ATTACK_ENABLED) == 1;
	}

	/**
     * Turns auto-retaliate on or off in the combat tab.
     *
     * @param enable <tt>true</tt> to enable; <tt>false</tt> to disable.
     */
    public void setAutoRetaliate(final boolean enable) {
    	if (isAutoRetaliateEnabled() != enable && methods.game.openTab(Game.Tab.ATTACK)) {
        	final RSComponent autoRetal = methods.interfaces.getComponent(884, 15);    		
    		if (autoRetal != null) {
    			autoRetal.doClick();
    		}
    	}
    }

	/**
     * Sets the attack mode.
     *
     * @param fightMode The fight mode to set it to. From 0-3 corresponding to the 4
     *                  attacking modes; Else if there is only 3 attacking modes then,
     *                  from 0-2 corresponding to the 3 attacking modes
     * @return <tt>true</tt> if the interface was clicked; otherwise
     *         <tt>false</tt>.
     * @see #getFightMode()
     */
    public boolean setFightMode(final int fightMode) {
    	if (fightMode != getFightMode()) {
    		methods.game.openTab(Game.Tab.ATTACK);
    		if (fightMode == 0) {
    			return methods.interfaces.getComponent(884, 11).doClick();
    		} else if (fightMode == 1) {
    			return methods.interfaces.getComponent(884, 12).doClick();
    		} else if (fightMode == 2 || fightMode == 3 && methods.interfaces.getComponent(884, 14).getActions() == null) {
    			return methods.interfaces.getComponent(884, 13).doClick();
    		} else if (fightMode == 3) {
    			return methods.interfaces.getComponent(884, 14).doClick();
    		}
    	}
    	return false;
    }

	/**
	 * Sets the special attack option on or off.
	 *
	 * @param enabled <tt>true</tt> enable; <tt>false</tt> to disable.
	 * @return <tt>true</tt> if the special bar was clicked; otherwise <tt>false</tt>.
	 */
	public boolean setSpecialAttack(final boolean enabled) {
		if (isSpecialEnabled() != enabled) {
			methods.game.openTab(Game.Tab.ATTACK);
			final RSComponent specBar = methods.interfaces.getComponent(884, 4);
			if (specBar != null && isSpecialEnabled() != enabled) {
				return specBar.doClick();
			}
		}
		return false;
	}
}
