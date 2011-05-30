package org.rsbot.client;

import java.awt.Canvas;
import java.awt.Rectangle;

import org.rsbot.client.input.Keyboard;
import org.rsbot.client.input.Mouse;

public interface Client {

	int getBaseX();

	int getBaseY();

	Callback getCallBack();

	int getCameraPitch();

	int getCameraYaw();

	int getCamPosX();

	int getCamPosY();

	int getCamPosZ();

	Canvas getCanvas();

	ChatLine[] getChatLines();

	NodeSubQueue getCollapsedMenuItems();

	MenuGroupNode getCurrentMenuGroupNode();

	String getCurrentPassword();

	String getCurrentUsername();

	int getDestX();

	int getDestY();

	DetailInfoNode getDetailInfoNode();

	byte[][][] getGroundByteArray();

	int getGUIRSInterfaceIndex();

	int getIdleTime();

	Keyboard getKeyboard();

	int getLoginIndex();

	int getLoopCycle();

	NodeDeque getMenuItems();

	int getMenuOptionsCount();

	int getMenuX();

	int getMenuY();

	int getMinimapAngle();

	float getMinimapOffset();

	int getMinimapScale();

	int getMinimapSetting();

	Mouse getMouse();

	RSPlayer getMyRSPlayer();

	int getPlane();

	int getPublicChatMode();

	RSGround[][][] getRSGroundArray();

	// MouseWheel getMouseWheel();

	RSGroundData[] getRSGroundDataArray();

	StatusNodeListLoader getRSInteractableDefListLoader();

	StatusNodeList getRSInteractingDefList();

	Rectangle[] getRSInterfaceBoundsArray();

	RSInterface[][] getRSInterfaceCache();

	HashTable getRSInterfaceNC();

	RSItemDefLoader getRSItemDefLoader();

	HashTable getRSItemHashTable();

	int getRSNPCCount();

	int[] getRSNPCIndexArray();

	HashTable getRSNPCNC();

	RSObjectDefLoader getRSObjectDefLoader();

	RSPlayer[] getRSPlayerArray();

	int getRSPlayerCount();

	int[] getRSPlayerIndexArray();

	String getSelectedItemName();

	int getSelfInteracting();

	Settings getSettingArray();

	Signlink getSignlink();

	Signlink getSignLink();

	int[] getSkillExperiences();

	int[] getSkillExperiencesMax();

	int[] getSkillLevelMaxes();

	int[] getSkillLevels();

	int getSubMenuWidth();

	int getSubMenuX();

	int getSubMenuY();

	TileData[] getTileData();

	boolean[] getValidRSInterfaceArray();

	ServerData getWorldData();

	boolean isFlagged();

	int isItemSelected();

	boolean isMenuCollapsed();

	boolean isMenuOpen();

	boolean isSpellSelected();

	void setCallback(Callback cb);

}
