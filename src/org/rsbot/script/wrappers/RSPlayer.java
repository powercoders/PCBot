package org.rsbot.script.wrappers;

import java.lang.ref.SoftReference;

import org.rsbot.client.RSPlayerComposite;
import org.rsbot.script.methods.MethodContext;

/**
 * Represents a player.
 */
public class RSPlayer extends RSCharacter {

	private final SoftReference<org.rsbot.client.RSPlayer> p;

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

	@Override
	public int getLevel() {
		return getCombatLevel();
	}

	@Override
	public String getName() {
		return p.get().getName();
	}

	public int getNPCID() {
		final RSPlayerComposite comp = p.get().getComposite();
		if (comp != null) {
			return comp.getNPCID();
		}
		return -1;
	}

	public int getTeam() {
		return p.get().getTeam();
	}

	public boolean isIdle() {
		return !isMoving() && getAnimation() == -1 && !isInCombat();
	}

	@Override
	public String toString() {
		return "Player[" + getName() + "]" + super.toString();
	}
}
