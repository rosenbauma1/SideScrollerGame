package com.mygdx.game;

import com.badlogic.gdx.Game;

public class SideScroller extends Game {

	@Override
	public void create () {
		setScreen(new GameScreen());
	}
}