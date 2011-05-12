package org.rsbot.script.util;

import org.rsbot.script.methods.Skills;

/**
 * Tracks skills, providing experience rates and experience gains
 * 
 * @author Equinox_
 * 
 */
public class SkillTracker {
    private Skills skills;
    private int[] startExp;
    private int[] newExp;
    private long[] lastTrained;
    private long start;

    /**
     * Constructs a SkillTracker using the provided Skills instance
     * 
     * @param skills
     */
    public SkillTracker(Skills skills) {
	this.skills = skills;
	this.start = System.currentTimeMillis();
	this.startExp = new int[Skills.SKILL_NAMES.length];
	this.lastTrained = new long[Skills.SKILL_NAMES.length];
	this.newExp = new int[Skills.SKILL_NAMES.length];
	for (int i = 0; i < Skills.SKILL_NAMES.length; i++) {
	    this.startExp[i] = skills.getCurrentExp(i);
	    this.newExp[i] = this.startExp[i];
	}
    }

    /**
     * Gets the elapsed time in milliseconds
     * 
     * @return the elapsed time
     */
    public long getElapsedTime() {
	return System.currentTimeMillis() - start;
    }

    /**
     * Gets the total experience gained for the specific skill
     * 
     * @param skill
     *            the skill
     * @return the experience gain
     */
    public int getExpGain(int skill) {
	return skills.getCurrentExp(skill) - startExp[skill];
    }

    /**
     * Gets the total level gained for the specific skill
     * 
     * @param skill
     *            the skill
     * @return the level gain
     */
    public int getLevelGain(int skill) {
	return getCurrentLevel(skill) - getStartLevel(skill);
    }

    private int getStartLevel(int skill) {
	int sLvl = Skills.getLevelAt(startExp[skill]);
	if (sLvl > getMaxLevel(skill))
	    sLvl = getMaxLevel(skill);
	return sLvl;
    }

    private int getMaxLevel(int skill) {
	return skill == Skills.DUNGEONEERING ? 120 : 99;
    }

    private int getCurrentLevel(int skill) {
	int sLvl = skills.getRealLevel(skill);
	if (sLvl > getMaxLevel(skill))
	    sLvl = getMaxLevel(skill);
	return sLvl;
    }

    /**
     * Gets the experience gain per millisecond for the specified skill
     * 
     * @param skill
     *            the skill
     * @return the experience gain per millisecond
     */
    public double getExpPerMilli(int skill) {
	double gain = getExpGain(skill);
	double time = getElapsedTime();
	return gain / time;
    }

    /**
     * Gets the experience gain per hour for the specified skill
     * 
     * @param skill
     *            the skill
     * @return the experience gain per hour
     */
    public double getExpPerHour(int skill) {
	return getExpPerMilli(skill) * 3600000d;
    }

    /**
     * Gets the number of milliseconds to the specified experience in the
     * specified skill
     * 
     * @param skill
     *            the skill
     * @param exp
     *            the experience
     * @return the time in milliseconds
     */
    public long getMillisToExp(int skill, int exp) {
	int expNeed = exp - skills.getCurrentExp(skill);
	double expPerMilli = getExpPerMilli(skill);
	if (expNeed <= 0 || getExpGain(skill) <= 0 || expPerMilli <= 0)
	    return 0;
	return (long) (((double) expNeed) / expPerMilli);
    }

    /**
     * Gets the number of milliseconds to the specified level in the specified
     * skill
     * 
     * @param skill
     *            the skill
     * @param exp
     *            the experience
     * @return the time in milliseconds
     */
    public long getMillisToLevel(int skill, int level) {
	if (level > getMaxLevel(skill) || level < 0)
	    return 0;
	return getMillisToExp(skill, Skills.XP_TABLE[level]);
    }

    /**
     * Checks if the specified skill has been trained
     * 
     * @param skill
     *            the skill
     * @return if the skill has been trained
     */
    public boolean hasTrained(int skill) {
	return skills.getCurrentExp(skill) > startExp[skill];
    }

    /**
     * Checks if the specified skill has been trained within the last minute
     * 
     * @param skill
     *            the skill
     * @return if the skill has been trained in the last minute
     */
    public boolean hasRecentlyTrained(int skill) {
	int cExp = skills.getCurrentExp(skill);
	if (newExp[skill] < cExp) {
	    lastTrained[skill] = System.currentTimeMillis();
	}
	newExp[skill] = cExp;
	return lastTrained[skill] + 60000 > System.currentTimeMillis();
    }

    /**
     * Checks if the specified skill has been trained within the amount of time
     * specified
     * 
     * @param skill
     *            the skill
     * @param millis
     *            the time in milliseconds
     * @return if the skill has been recently trained
     */
    public boolean hasRecentlyTrained(int skill, long millis) {
	int cExp = skills.getCurrentExp(skill);
	if (newExp[skill] < cExp) {
	    lastTrained[skill] = System.currentTimeMillis();
	}
	newExp[skill] = cExp;
	return lastTrained[skill] + millis > System.currentTimeMillis();
    }
}
