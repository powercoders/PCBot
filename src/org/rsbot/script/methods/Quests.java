package org.rsbot.script.methods;

import org.rsbot.script.wrappers.RSComponent;

/**
 * Quest methods.
 *
 * @author Timer
 */
public class Quests extends MethodProvider {
	public static final int QUESTS = 190;
	public static final int POINTS_COMPONENT = 2;
	public static final int QUESTS_COMPONENT = 18;
	public static final int SCROLL_COMPONENT = 17;

	public Quests(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * The state of a quest.
	 */
	public enum State {
		NOT_STARTED, IN_PROGRESS, FINISHED
	}

	/**
	 * Gets the player's current number of quest points.
	 *
	 * @return Player's current number of quest points.
	 */
	public int getPoints() {
		openTab();
		String s = methods.interfaces.getComponent(QUESTS, POINTS_COMPONENT).getText().replace("Quest Points: ", "");
		return Integer.parseInt(s.split(" / ")[0]);
	}

	/**
	 * Gets the maximum number of quest points possible.
	 *
	 * @return Maximum number of quest points possible.
	 */
	public int getMaxPoints() {
		openTab();
		String s = methods.interfaces.getComponent(QUESTS, POINTS_COMPONENT).getText().replace("Quest Points: ", "");
		return Integer.parseInt(s.split(" / ")[1]);
	}

	/**
	 * Checks if the specified quest is filtered out or not.
	 *
	 * @param name name of the quest to retrieve Component of.
	 * @return Whether the Quest is filtered out or not.
	 */
	public boolean isFiltered(final String name) {
		if (getComponent(name) != null) {
			return getComponent(name).getTextColor() == 2236962;
		}
		return false;
	}

	/**
	 * Checks if a Quest is finished. Only works if not hidden.
	 *
	 * @param name name of the quest to check completion status of.
	 * @return Whether the specified quest is completed or not.
	 */
	public boolean isCompleted(final String name) {
		if (!isFiltered(name)) {
			return getProgress(name).equals(State.FINISHED);
		}
		return false;
	}

	/**
	 * Returns a Quest.Progress object to reflect completion status of quest.
	 * Only works if not hidden.
	 *
	 * @param name name of the quest to check progress of.
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
	 * Returns the component of the specified quest.
	 *
	 * @param name name of the quest to retrieve component of.
	 * @return Component of specified quest.
	 */
	public RSComponent getComponent(final String name) {
		openTab();
		for (RSComponent i : getComponents()) {
			if (i.getText().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Opens a quest.
	 *
	 * @param name The name of the quest.
	 * @return <tt>true</tt> if the quest was opened.
	 */
	public boolean openQuest(final String name) {
		RSComponent quest = getComponent(name);
		RSComponent scrollBar = methods.interfaces.getComponent(QUESTS, SCROLL_COMPONENT);
		if (quest != null && scrollBar != null) {
			if (methods.interfaces.scrollTo(quest, scrollBar)) {
				return quest.doClick();
			}
		}
		return false;
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
	 * Selects the quests tab if not already selected.
	 */
	public void openTab() {
		if (methods.game.getCurrentTab() != Game.TAB_QUESTS) {
			methods.game.openTab(Game.TAB_QUESTS);
		}
	}

}
