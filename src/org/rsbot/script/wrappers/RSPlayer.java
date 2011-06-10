package org.rsbot.script.wrappers;

import org.rsbot.client.RSPlayerComposite;
import org.rsbot.script.methods.MethodContext;

import java.lang.ref.SoftReference;

/**
 * Represents a player.
 */
public class RSPlayer extends RSCharacter {
	private final SoftReference<org.rsbot.client.RSPlayer> p;
	private static final int EQUIPMENT_CONSTANT = 1073741824;

	public RSPlayer(final MethodContext ctx, final org.rsbot.client.RSPlayer p) {
		super(ctx);
		this.p = new SoftReference<org.rsbot.client.RSPlayer>(p);
	}

	@Override
	protected org.rsbot.client.RSCharacter getAccessor() {
		return p.get();
	}

	public int getCombatLevel() {
		return p.get().getLevel();
	}

	public int getNPCID() {
		final RSPlayerComposite comp = p.get().getComposite();
		if (comp != null) {
			return comp.getNPCID();
		}
		return -1;
	}

	public int[] getEquipment() {
		final RSPlayerComposite comp = p.get().getComposite();
		if (comp != null) {
			final int[] equip = comp.getEquipment();
			for (int i = 0; i < equip.length; i++) {
				equip[i] = equip[i] - EQUIPMENT_CONSTANT;
				if (equip[i] < 0 || equip[i] > 1000000000) {
					equip[i] = -1;
				}
			}
			return equip;
		}
		return null;
	}

	public int getTeam() {
		return p.get().getTeam();
	}

	public boolean isIdle() {
		return !isMoving() && getAnimation() == -1 && !isInCombat();
	}

	@Override
	public String getName() {
		return p.get().getName();
	}

	@Override
	public int getLevel() {
		return getCombatLevel();
	}

	@Override
	public String toString() {
		return "Player[" + getName() + "]" + super.toString();
	}
}