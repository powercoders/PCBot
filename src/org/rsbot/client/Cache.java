package org.rsbot.client;

public interface Cache {

	int getInitialCount();

	NodeSubQueue getList();

	int getSpaceLeft();

	HashTable getTable();
}
