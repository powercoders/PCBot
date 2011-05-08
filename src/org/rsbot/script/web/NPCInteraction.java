package org.rsbot.script.web;

/**
 * NPC interaction base functions.
 *
 * @author Timer
 */
public abstract class NPCInteraction implements Activity, Prerequisites {
	public abstract double npcCost();
}