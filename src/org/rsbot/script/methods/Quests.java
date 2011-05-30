package org.rsbot.script.methods;

import org.rsbot.script.wrappers.RSComponent;

/**
 * Quest methods.
 * 
 * @author Timer
 */
public class Quests extends MethodProvider {
	/**
	 * The state of a quest.
	 */
	public enum State {
		NOT_STARTED, IN_PROGRESS, FINISHED
	}

	public static final int QUESTS = 190;
	public static final int POINTS_COMPONENT = 2;
	public static final int QUESTS_COMPONENT = 18;

	public static final int SCROLL_COMPONENT = 17;

	public Quests(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Returns the component of the specified quest.
	 * 
	 * @param name
	 *            name of the quest to retrieve component of.
	 * @return Component of specified quest.
	 */
	public RSComponent getComponent(final String name) {
		openTab();
		for (final RSComponent i : getComponents()) {
			if (i.getText().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Returns an array of all the individual quest components.
	 * 
	 * @return Array of all individual quest components.
	 */
	public RSComponent[] getComponents() {
		openTab();
		return methods.interfaces.getComponent(QUESTS, QUESTS_COMPONENT).getComponents();
	}

	/**
	 * Gets the maximum number of quest points possible.
	 * 
	 * @return Maximum number of quest points possible.
	 */
	public int getMaxPoints() {
		openTab();
		final String s = methods.interfaces.getComponent(QUESTS, POINTS_COMPONENT).getText().replace("Quest Points: ", "");
		return Integer.parseInt(s.split(" / ")[1]);
	}

	/**
	 * Gets the player's current number of quest points.
	 * 
	 * @return Player's current number of quest points.
	 */
	public int getPoints() {
		openTab();
		final String s = methods.interfaces.getComponent(QUESTS, POINTS_COMPONENT).getText().replace("Quest Points: ", "");
		return Integer.parseInt(s.split(" / ")[0]);
	}

	/**
	 * Returns a Quest.Progress object to reflect completion status of quest.
	 * Only works if not hidden.
	 * 
	 * @param name
	 *            name of the quest to check progress of.
	 * @return Correct Quest.Progress object.
	 */
	public State getProgress(final String name) {
		if (!isFiltered(name)) {
			switch (getComponent(name).getTextColor()) {
			case 16711680:
				return State.NOT_STARTED;
			case 16776960:
				return State.IN_PROGRESS;
			case 65280:
				return State.FINISHED;
			}
		}
		return null;
	}

	/**
	 * Checks if a Quest is finished. Only works if not hidden.
	 * 
	 * @param name
	 *            name of the quest to check completion status of.
	 * @return Whether the specified quest is completed or not.
	 */
	public boolean isCompleted(final String name) {
		if (!isFiltered(name)) {
			return getProgress(name).equals(State.FINISHED);
		}
		return false;
	}

	/**
	 * Checks if the specified quest is filtered out or not.
	 * 
	 * @param name
	 *            name of the quest to retrieve Component of.
	 * @return Whether the Quest is filtered out or not.
	 */
	public boolean isFiltered(final String name) {
		if (getComponent(name) != null) {
			return getComponent(name).getTextColor() == 2236962;
		}
		return false;
	}

	/**
	 * Opens a quest.
	 * 
	 * @param name
	 *            The name of the quest.
	 * @return <tt>true</tt> if the quest was opened.
	 */
	public boolean openQuest(final String name) {
		final RSComponent quest = getComponent(name);
		final RSComponent scrollBar = methods.interfaces.getComponent(QUESTS, SCROLL_COMPONENT);
		if (quest != null && scrollBar != null) {
			if (methods.interfaces.scrollTo(quest, scrollBar)) {
				return quest.doClick();
			}
		}
		return false;
	}

	/**
	 * Selects the quests tab if not already selected.
	 */
	public void openTab() {
		methods.game.openTab(Game.Tab.QUESTS);
	}

}
