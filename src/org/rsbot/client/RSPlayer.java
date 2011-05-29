package org.rsbot.client;

public interface RSPlayer extends RSCharacter {

	RSPlayerComposite getComposite();

	int getLevel();

	String getName();

	int getTeam();

}
