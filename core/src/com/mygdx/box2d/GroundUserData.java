package com.mygdx.box2d;

import com.mygdx.enums.UserDataType;

public class GroundUserData extends UserData {

	public GroundUserData(float width, float height) {
		super(width, height);
		userDataType = UserDataType.GROUND;
	}
}
