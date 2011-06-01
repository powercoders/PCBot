package org.rsbot.client;

/**
 */
public interface MenuGroupNode extends NodeSub {

	NodeSubQueue getItems();

	String getOption();

	int size();

}
