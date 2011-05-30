package org.rsbot.client;

public interface RSItemDef {

	String[] getActions();

	int getCertID();

	int getCertTemplateID();

	String[] getGroundActions();

	int getID();

	RSItemDefLoader getLoader();

	String getName();

	boolean isMembersObject();
}
