package com.mygdx.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.mygdx.actors.Background;
import com.mygdx.actors.Enemy;
import com.mygdx.actors.Ground;
import com.mygdx.actors.Runner;
import com.mygdx.game.SideScroller;
import com.mygdx.utils.BodyUtils;
import com.mygdx.utils.Constants;
import com.mygdx.utils.WorldUtils;

public class GameStage extends Stage implements ContactListener{
	
	// This will be our viewport measurements while working with the debug renderer
	private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
	private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
	
	private World world;
	private Ground ground;
	private Runner runner;


	
	private final float TIME_STEP = 1 / 300f;
	private float accumulator = 0f;
	
	private OrthographicCamera camera;
	
	private Rectangle screenLeftSide;
	private Rectangle screenRightSide;
	
	private Vector3 touchPoint; 	
	
	public GameStage(){
		super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));
		setUpWorld();
		setupCamera();
		setupTouchControlAreas();
	}
	
	private void setUpWorld() {
		world = WorldUtils.createWorld();
		// Let the world know you are handling contacts
		world.setContactListener(this);
		setUpBackground();
		setUpGround();
		setUpRunner();
		createEnemy();
	}
	private void setUpBackground() {
		addActor(new Background());
	}
	
	private void setUpRunner() {
		runner = new Runner(WorldUtils.createRunner(world));
		addActor(runner);
	}

	private void setUpGround() {
		ground = new Ground(WorldUtils.createGround(world));
		addActor(ground);
		
	}
	
	
	private void setupCamera(){
		camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
		camera.update();
	}
	
	private void setupTouchControlAreas() {
		touchPoint = new Vector3();
		screenLeftSide = new Rectangle(0, 0, getCamera().viewportWidth / 2, getCamera().viewportHeight);
		screenRightSide = new Rectangle(getCamera().viewportWidth / 2, 0, getCamera().viewportWidth / 2, getCamera().viewportHeight);
		Gdx.input.setInputProcessor(this);
	}
	
	@Override 
	public void act(float delta){
		super.act(delta);
		
		Array<Body> bodies = new Array<Body>(world.getBodyCount());
		world.getBodies(bodies);
		
		for (Body body : bodies) {
			update(body);
		}
		
		// Fixed timestep
		accumulator += delta;
		
		while (accumulator >= delta){
			world.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;	
		}
		
		
		//TODO: Implement interpolation
	}
	
	private void update(Body body) {
		if (!BodyUtils.bodyInBounds(body)) {
			if(BodyUtils.bodyIsEnemy(body) && !runner.isHit()) {
				createEnemy();
			}
			world.destroyBody(body);
			
		}
	}
	
	private void createEnemy() {
		Enemy enemy = new Enemy(WorldUtils.createEnemy(world));
		addActor(enemy);
	}
	
	@Override
	public void act() {
		// TODO Auto-generated method stub
		super.act();
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		
		// Need to get the actual coordinates
		translateScreenToWorldCoordinates(x, y);
		
		if(rightSideTouched(touchPoint.x, touchPoint.y)){
			runner.jump();			
		} else if (leftSideTouched(touchPoint.x, touchPoint.y)) {
			runner.dodge();
		}
		
		return super.touchDown(x, y, pointer, button);
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button ) {
		
		if (runner.isDodging()) {
			runner.stopDodge();
		}
		
		return super.touchUp(screenX, screenY, pointer, button);
	}
	private boolean rightSideTouched(float x, float y){
		return screenRightSide.contains(x, y);
	}
	
	private boolean leftSideTouched(float x, float y) {
		return screenLeftSide.contains(x, y);
		
	}
	/**
     * Helper function to get the actual coordinates in my world
     * @param x
     * @param y
     */
	
	private void translateScreenToWorldCoordinates(int x, int y){
		getCamera().unproject(touchPoint.set(x, y, 0));
		
	}
	
	@Override
	public void beginContact(Contact contact) {
		
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
		
		if((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsEnemy(b) || BodyUtils.bodyIsEnemy(a) && BodyUtils.bodyIsRunner(b))) {
				runner.hit();
		} else if ((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsGround(b)) || (BodyUtils.bodyIsGround(a) && BodyUtils.bodyIsRunner(b))) {
			runner.landed();
		}
	}
	
	@Override
	public void endContact(Contact contact){
		
	}
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
	
}
