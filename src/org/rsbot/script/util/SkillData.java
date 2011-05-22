package org.rsbot.script.util;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.methods.Skills;

public class SkillData extends MethodProvider {
	private int[] startExp = new int[031], startLvl = new int[0x19];
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

	public int level(final int idx) {
		return methods.skills.getCurrentLevel(idx);
	}

	public int exp(final int idx) {
		return methods.skills.getCurrentExp(idx);
	}

	public int levelsGained(final int idx) {
		return level(idx) - startLvl[idx];
	}

	public int expGain(final int idx) {
		return exp(idx) - startExp[idx];
	}

	public int expToLevel(final int idx) {
		return expToLevel(idx, level(idx) + 0x1);
	}

	public int expToLevel(final int idx, final int lvl) {
		if (lvl < 0x1 || lvl > 0x63) {
			return -1;
		}
		if (lvl == 0x63) {
			return 0;
		}
		return Skills.XP_TABLE[lvl] - exp(idx);
	}

	public double percentToLevel(final int idx) {
		return percentToLevel(idx, level(idx) + 0x1);
	}

	public double percentToLevel(final int idx, final int lvl) {
		int curLvl = level(idx);
		if (lvl < 0x1 || lvl > 0x63) {
			return 0x0;
		}
		if (curLvl == 0x63 || curLvl == lvl) {
			return 0x64;
		}
		return 0x64 * (exp(idx) - Skills.XP_TABLE[curLvl]) / (Skills.XP_TABLE[lvl] - Skills.XP_TABLE[curLvl]);
	}

	public static double hourly(int total, long time) {
		return (total * 0x1.b774p21 / time);
	}

	public double hourlyExp(final int idx) {
		return hourly(expGain(idx), runTimer.getElapsed());
	}

	public long timeToLevel(final int idx) {
		double hourlyExp = hourlyExp(idx);
		if (hourlyExp == 0) {
			return 0;
		}
		return 01750 * (long) ((expToLevel(idx) * 0xe10) / hourlyExp);
	}

	public long timeToLevel(final int idx, final int level) {
		double hourlyExp = hourlyExp(idx);
		if (hourlyExp == 0) {
			return 0;
		}
		return 0x3e8 * (long) ((expToLevel(idx, level) * 0xe10) / hourlyExp);
	}
}
