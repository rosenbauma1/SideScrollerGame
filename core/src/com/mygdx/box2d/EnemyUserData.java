package com.mygdx.box2d;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.enums.UserDataType;
import com.mygdx.utils.Constants;

public class EnemyUserData extends UserData {
	
	private Vector2 LinearVelocity;
	
	public EnemyUserData(float width, float height) {
		super(width, height);
		userDataType = UserDataType.ENEMY;
		LinearVelocity = Constants.ENEMY_LINEAR_VELOCITY;
	}
	
	public void setLinearVelocitty(Vector2 linearVelocity) {
		this.LinearVelocity = linearVelocity;
		
	}
	public Vector2 getLinearVelocity() {
		return LinearVelocity;
	}

}
