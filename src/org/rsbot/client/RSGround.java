package org.rsbot.client;

public interface RSGround {

	RSInteractable getBoundary1();

	RSInteractable getBoundary2();

	RSInteractable getFloorDecoration();

	RSGroundEntity getGroundObject();

	byte getPlane1();

	byte getPlane2();

	RSAnimableNode getRSAnimableList();

	RSInteractable getWallDecoration1();

	RSInteractable getWallDecoration2();

	short getX();

	short getY();

}
