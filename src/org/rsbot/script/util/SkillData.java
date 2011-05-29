package org.rsbot.script.util;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.methods.Skills;

/**
 * Skill data tracker
 * 
 * @author Timer
 */
public class SkillData extends MethodProvider {
	public static double hourly(final int total, final long time) {
		return total * 3600000D / time;
	}

	private final int[] startExp = new int[25], startLvl = new int[25];

	private Timer runTimer = null;

	public SkillData(final MethodContext ctx, final Timer runTimer) {
		super(ctx);
		for (int i = 0; i < startExp.length; i++) {
			startExp[i] = exp(i);
		}
		for (int i = 0; i < startLvl.length; i++) {
			startLvl[i] = level(i);
		}
		this.runTimer = runTimer != null ? runTimer : new Timer(0);
	}

	public int exp(final int idx) {
		return methods.skills.getCurrentExp(idx);
	}

	public int expGain(final int idx) {
		return exp(idx) - startExp[idx];
	}

	public int expToLevel(final int idx) {
		return expToLevel(idx, level(idx) + 1);
	}

	public int expToLevel(final int idx, final int lvl) {
		if (lvl < 1 || lvl > 99) {
			return -1;
		}
		if (lvl == 99) {
			return 0;
		}
		return Skills.XP_TABLE[lvl] - exp(idx);
	}

	public double hourlyExp(final int idx) {
		return hourly(expGain(idx), runTimer.getElapsed());
	}

	public int level(final int idx) {
		return methods.skills.getCurrentLevel(idx);
	}

	public int levelsGained(final int idx) {
		return level(idx) - startLvl[idx];
	}

	public double percentToLevel(final int idx) {
		return percentToLevel(idx, level(idx) + 1);
	}

	public double percentToLevel(final int idx, final int lvl) {
		final int curLvl = level(idx);
		if (lvl < 1 || lvl > 99) {
			return 0;
		}
		if (curLvl == 99 || curLvl == lvl) {
			return 100;
		}
		return 100 * (exp(idx) - Skills.XP_TABLE[curLvl])
				/ (Skills.XP_TABLE[lvl] - Skills.XP_TABLE[curLvl]);
	}

	public long timeToLevel(final int idx) {
		final double hourlyExp = hourlyExp(idx);
		if (hourlyExp == 0) {
			return 0;
		}
		return 1000 * (long) (expToLevel(idx) * 3600 / hourlyExp);
	}

	public long timeToLevel(final int idx, final int level) {
		final double hourlyExp = hourlyExp(idx);
		if (hourlyExp == 0) {
			return 0;
		}
		return 1000 * (long) (expToLevel(idx, level) * 3600 / hourlyExp);
	}
}
