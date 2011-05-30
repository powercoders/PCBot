package org.rsbot.client;

public interface RSCharacter extends RSAnimable {

	int getAnimation();

	Graphic[] getGraphicsData();

	int getHeight();

	int getHPRatio();

	int getInteracting();

	int[] getLocationX();

	int[] getLocationY();

	int getLoopCycleStatus();

	String getMessage();

	Model getModel();

	int getOrientation();

	int isMoving();

}
