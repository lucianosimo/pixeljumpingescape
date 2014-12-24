package com.lucianosimo.pixeljumpingescape.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.lucianosimo.pixeljumpingescape.base.BaseScene;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager.SceneType;
import com.lucianosimo.pixeljumpingescape.object.CenterSpikes;
import com.lucianosimo.pixeljumpingescape.object.LeftSpikes;
import com.lucianosimo.pixeljumpingescape.object.LeftWall;
import com.lucianosimo.pixeljumpingescape.object.Player;
import com.lucianosimo.pixeljumpingescape.object.RightSpikes;
import com.lucianosimo.pixeljumpingescape.object.RightWall;
import com.lucianosimo.pixeljumpingescape.object.Spider;

public class GameScene extends BaseScene implements IOnSceneTouchListener{
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//HUD sprites
	private Sprite fire;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	
	//Instances
	private Player player;
	private LeftWall[][] leftWall;
	private RightWall[][] rightWall;
	private LeftSpikes[][] leftSpikes;
	private RightSpikes[][] rightSpikes;
	private CenterSpikes centerSpikes;
	private Spider spider;
	
	//Booleans

	//Integers
	
	//Windows
	private Sprite gameOverWindow;

	//Buttons
	
	//Rectangles
	private Rectangle leftButton;
	private Rectangle rightButton;
	
	//Counters
	private int score;
	private int movedBlocks;
	
	//Pools
	
	//Explosions
	
	//Text
	private Text scoreText;
	
	//Sensors
	private Rectangle[] moveBlocksSensor;
	private Rectangle incrementSpeedSensor;

	//Constants
	private final static int MAX_BLOCKS = 10;
	private final static int PLAYER_INITIAL_X = 360;
	private final static int PLAYER_INITIAL_Y = 300;
	private final static int BUTTON_WIDTH = 200;
	private final static int BUTTON_HEIGHT = 1280;
	private final static int FLOOR_HEIGHT = 256;
	private final static int WALL_WIDTH = 100;
	private final static int WALL_HEIGHT = 128;
	private final static int SPIKES_WIDTH = 80;
	private final static int SPIKES_HEIGHT = 128;
	private final static int CENTER_SPIKES_WIDTH = 75;
	private final static int CENTER_SPIKES_HEIGHT = 75;
	private final static int CAMERA_INITIAL_SPEED = -150;
	private final static int CAMERA_SPEED_INCREMENT = 10;
	private final static float Y_JUMP_SPEED_MULTIPLIER = 0.069444444444f;
	
	//If negative, never collides between groups, if positive yes
	//private static final int GROUP_ENEMY = -1;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		setCameraProperties();
		createHud();
		createBackground();
		createPhysics();
		createPlayer();
		createWindows();
		createWalls();
		//createEnemies();
		GameScene.this.setOnSceneTouchListener(this);
		//DebugRenderer debug = new DebugRenderer(physicsWorld, vbom);
        //GameScene.this.attachChild(debug);
	}
	
	private void setCameraProperties() {
		camera.setMaxVelocityY(CAMERA_INITIAL_SPEED);
		//camera.setMaxVelocityY(0);
		camera.setChaseEntity(this);
		camera.setBoundsEnabled(false);
		moveCameraToOrigin();
	}
	
	private void moveCameraToOrigin() {
		camera.setCenterDirect(screenWidth / 2, screenHeight / 2);
        camera.setZoomFactorDirect(1.0f);
        camera.setCenterDirect(screenWidth / 2, screenHeight / 2);
	}
	
	private void createHud() {
		gameHud = new HUD();
		
		scoreText = new Text(screenWidth / 2, 1200, resourcesManager.game_score_font, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setText("0");
		leftButton = new Rectangle(BUTTON_WIDTH / 2, screenHeight / 2, BUTTON_WIDTH, BUTTON_HEIGHT, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown() && player.isOnRightWall() && !player.isOnAir() || player.isInitial()) {
					float yJumpPx = (camera.getCenterY() - screenHeight / 2) + pSceneTouchEvent.getY() - player.getY();
					if (player.isInitial()) {
						yJumpPx = (camera.getCenterY() - screenHeight / 2) + pSceneTouchEvent.getY() - player.getY() + FLOOR_HEIGHT;
					}
					float ySpeed = yJumpPx * Y_JUMP_SPEED_MULTIPLIER;
					player.goToLeftWall(ySpeed);
				}
				return false;
			}
		};
		rightButton = new Rectangle(screenWidth - (BUTTON_WIDTH / 2), screenHeight / 2, BUTTON_WIDTH, BUTTON_HEIGHT, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown() && player.isOnLeftWall() && !player.isOnAir() || player.isInitial()) {
					float yJumpPx = (camera.getCenterY() - screenHeight / 2) + pSceneTouchEvent.getY() - player.getY();
					if (player.isInitial()) {
						yJumpPx = (camera.getCenterY() - screenHeight / 2) + pSceneTouchEvent.getY() - player.getY() + FLOOR_HEIGHT;
					}
					float ySpeed = yJumpPx * Y_JUMP_SPEED_MULTIPLIER;
					player.goToRightWall(ySpeed);
				}
				return false;
			}
		};
		fire = new Sprite(screenWidth / 2, 50, resourcesManager.game_fire_region, vbom);
		leftButton.setAlpha(0);
		rightButton.setAlpha(0);
		gameHud.attachChild(scoreText);
		gameHud.attachChild(leftButton);
		gameHud.attachChild(rightButton);
		gameHud.attachChild(fire);
		gameHud.registerTouchArea(leftButton);
		gameHud.registerTouchArea(rightButton);
		camera.setHUD(gameHud);
	}
	
	private void createBackground() {
		ParallaxBackground background = new ParallaxBackground(0, 0, 0);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_background_region, vbom)));
		this.setBackground(background);
	}
	
	private void createWindows() {
		gameOverWindow = new Sprite(10000, 10000, resourcesManager.game_over_window_region, vbom);
		GameScene.this.attachChild(gameOverWindow);
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -1), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createPlayer() {
		player = new Player(PLAYER_INITIAL_X, PLAYER_INITIAL_Y, vbom, camera, physicsWorld) {
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				incrementScore();
				if (player.collidesWith(incrementSpeedSensor)) {
					incrementSpeedSensor.setPosition(incrementSpeedSensor.getX(), incrementSpeedSensor.getY() + screenHeight / 2);
					incrementCameraSpeed();
				}
				if (player.collidesWith(moveBlocksSensor[0]) && movedBlocks > 0) {
					moveBlocks(0);
				} else if (player.collidesWith(moveBlocksSensor[1])) {
					moveBlocks(1);	
				} else if (player.collidesWith(moveBlocksSensor[2])) {
					moveBlocks(2);
				} else if (player.collidesWith(moveBlocksSensor[3])) {
					moveBlocks(3);
				} else if (player.collidesWith(moveBlocksSensor[4])) {
					moveBlocks(4);
				} else if (player.collidesWith(moveBlocksSensor[5])) {
					moveBlocks(5);
				} else if (player.collidesWith(moveBlocksSensor[6])) {
					moveBlocks(6);
				} else if (player.collidesWith(moveBlocksSensor[7])) {
					moveBlocks(7);
				} else if (player.collidesWith(moveBlocksSensor[8])) {
					moveBlocks(8);
				} else if (player.collidesWith(moveBlocksSensor[9])) {
					moveBlocks(9);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
			
			@Override
			public void onDie() {
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						GameScene.this.setIgnoreUpdate(true);
				        camera.setChaseEntity(null);
				        camera.setMaxVelocityY(0);
				        gameOverWindow.setPosition(camera.getCenterX(), camera.getCenterY());
					    final Sprite retryButton = new Sprite(125, 110, resourcesManager.game_retry_button_region, vbom){
					    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					    		if (pSceneTouchEvent.isActionDown()) {
					    			moveCameraToOrigin();
					    			gameHud.dispose();
									gameHud.setVisible(false);
									detachChild(gameHud);
									myGarbageCollection();
									SceneManager.getInstance().loadGameScene(engine, GameScene.this);
								}
					    		return true;
					    	};
					    };
					    final Sprite quitButton = new Sprite(350, 110, resourcesManager.game_quit_button_region, vbom){
					    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					    		if (pSceneTouchEvent.isActionDown()) {
					    			moveCameraToOrigin();
					    			gameHud.dispose();
									gameHud.setVisible(false);
									detachChild(gameHud);
									myGarbageCollection();
									SceneManager.getInstance().loadMenuScene(engine, GameScene.this);
					    		}
					    		return true;
					    	};
					    };
					    GameScene.this.registerTouchArea(retryButton);
					    GameScene.this.registerTouchArea(quitButton);
					    gameOverWindow.attachChild(quitButton);
					    gameOverWindow.attachChild(retryButton);
					}
				});
			}
		};
		
		GameScene.this.attachChild(player);
	}
	
	private void createWalls() {
		long seed;
		moveBlocksSensor = new Rectangle[MAX_BLOCKS];
		leftWall = new LeftWall[MAX_BLOCKS][5];
		rightWall = new RightWall[MAX_BLOCKS][5];
		leftSpikes = new LeftSpikes[MAX_BLOCKS][5];
		rightSpikes = new RightSpikes[MAX_BLOCKS][5];
		
		ArrayList<Integer> leftPositions = new ArrayList<>();
		ArrayList<Integer> rightPositions = new ArrayList<>();
		
		ArrayList<Integer> leftWallPositions = new ArrayList<>();
		ArrayList<Integer> rightWallPositions = new ArrayList<>();
		ArrayList<Integer> leftSpikesPositions = new ArrayList<>();
		ArrayList<Integer> rightSpikesPositions = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			leftPositions.add(WALL_HEIGHT/2 + (WALL_HEIGHT * i));
			rightPositions.add(WALL_HEIGHT/2 + (WALL_HEIGHT * i));
		}

		Sprite floor = new Sprite(camera.getCenterX(), 128, resourcesManager.game_floor_region, vbom);
		FixtureDef floor_fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		final Body floor_body = PhysicsFactory.createBoxBody(physicsWorld, floor, BodyType.StaticBody, floor_fixture);
		floor_body.setUserData("floor_body");
		GameScene.this.attachChild(floor);
		
		incrementSpeedSensor = new Rectangle(screenWidth / 2, screenHeight / 2, screenWidth, 10, vbom);
		incrementSpeedSensor.setColor(Color.GREEN);
		GameScene.this.attachChild(incrementSpeedSensor);

		for (int i = 0; i < MAX_BLOCKS; i++) {
			moveBlocksSensor[i] = new Rectangle(screenWidth / 2, 1280 * i + screenHeight, screenWidth, 10, vbom);
			moveBlocksSensor[i].setColor(Color.RED);
			GameScene.this.attachChild(moveBlocksSensor[i]);
			
			seed = System.nanoTime();
			Collections.shuffle(leftPositions, new Random(seed));
			seed = System.nanoTime();
			Collections.shuffle(rightPositions, new Random(seed));
			
			for (int j = 0; j < leftPositions.size(); j++) {
				if (j < 5) {
					leftWallPositions.add(leftPositions.get(j));
					rightWallPositions.add(rightPositions.get(j));
				} else {
					leftSpikesPositions.add(leftPositions.get(j));
					rightSpikesPositions.add(rightPositions.get(j));
				}
			}
			
			for (int j = 0; j < leftWallPositions.size(); j++) {
				leftWall[i][j] = new LeftWall(WALL_WIDTH / 2, leftWallPositions.get(j) + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(leftWall[i][j]);
			}
			for (int j = 0; j < rightWallPositions.size(); j++) {
				rightWall[i][j] = new RightWall(screenWidth - (WALL_WIDTH / 2), rightWallPositions.get(j) + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightWall[i][j]);
			}
			for (int j = 0; j < leftSpikesPositions.size(); j++) {
				leftSpikes[i][j] = new LeftSpikes(SPIKES_WIDTH / 2, leftSpikesPositions.get(j) + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(leftSpikes[i][j]);
			}
			for (int j = 0; j < rightSpikesPositions.size(); j++) {
				rightSpikes[i][j] = new RightSpikes(screenWidth - (SPIKES_WIDTH / 2), rightSpikesPositions.get(j) + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightSpikes[i][j]);
			}

			for (int j = 0; j < leftPositions.size(); j++) {
				if (j < 5) {
					leftWallPositions.remove(0);
					rightWallPositions.remove(0);
				} else {
					leftSpikesPositions.remove(0);
					rightSpikesPositions.remove(0);
				}
			}
		}
	}
	
	private void createEnemies() {
		for (int i = 0; i < 5; i++) {
			if ((i%2) == 0) {
				spider = new Spider(150, 2500 * i, vbom, camera, physicsWorld);
			} else {
				spider = new Spider(570, 2500 * i, vbom, camera, physicsWorld);
			}
			GameScene.this.attachChild(spider);
		}
	}
	
	private void incrementScore() {
		score = ((int) camera.getCenterY() - 640) / 100;
		scoreText.setText("" + score);
	}
	
	private void incrementCameraSpeed() {
		camera.setMaxVelocityY(camera.getMaxVelocityY() - CAMERA_SPEED_INCREMENT);
	}
	
	private void moveBlocks(int index) {
		int indexBlockToMove = 0;
		if (index == 0) {
			indexBlockToMove = MAX_BLOCKS - 1;
		} else {
			indexBlockToMove = index - 1;
		}
		moveBlocksSensor[indexBlockToMove].setPosition(screenWidth / 2, moveBlocksSensor[indexBlockToMove].getY() + (1280 * (MAX_BLOCKS)));
		moveBlocksSensor[index].setPosition(5000, moveBlocksSensor[index].getY());
		for (int i = 0; i < leftWall[indexBlockToMove].length; i++) {
			leftWall[indexBlockToMove][i].getBody().setTransform(leftWall[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[indexBlockToMove][i].getBody().getAngle());
			leftWall[indexBlockToMove][i].setPosition(leftWall[indexBlockToMove][i].getX(), leftWall[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS)));
		}
		for (int i = 0; i < rightWall[indexBlockToMove].length; i++) {
			rightWall[indexBlockToMove][i].getBody().setTransform(rightWall[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[indexBlockToMove][i].getBody().getAngle());
			rightWall[indexBlockToMove][i].setPosition(rightWall[indexBlockToMove][i].getX(), rightWall[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS)));
		}
		for (int i = 0; i < leftSpikes[indexBlockToMove].length; i++) {
			leftSpikes[indexBlockToMove][i].getBody().setTransform(leftSpikes[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[indexBlockToMove][i].getBody().getAngle());
			leftSpikes[indexBlockToMove][i].setPosition(leftSpikes[indexBlockToMove][i].getX(), leftSpikes[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS)));
		}
		for (int i = 0; i < rightSpikes[indexBlockToMove].length; i++) {
			rightSpikes[indexBlockToMove][i].getBody().setTransform(rightSpikes[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[indexBlockToMove][i].getBody().getAngle());
			rightSpikes[indexBlockToMove][i].setPosition(rightSpikes[indexBlockToMove][i].getX(), rightSpikes[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS)));
		}
		movedBlocks++;	
	}
	
	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
	
			}
			
			@Override
			public void endContact(Contact contact) {
				
			}
			
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("leftWall")) {
					player.stopPlayer();
					player.setOnAirFalse();
				}
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("rightWall")) {
					player.stopPlayer();
					player.setOnAirFalse();
				}
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("leftSpikes")) {
					player.killPlayer();
				}
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("rightSpikes")) {
					player.killPlayer();
				}
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("centerSpikes")) {
					player.killPlayer();
				}
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("spider")) {
					player.killPlayer();
				}
				
				if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("fire")) {
					player.killPlayer();
				}
			}
		};
		return contactListener;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		
	}
	
	private void myGarbageCollection() {
		Iterator<Body> allMyBodies = physicsWorld.getBodies();
        while(allMyBodies.hasNext()) {
        	try {
        		final Body myCurrentBody = allMyBodies.next();
                	physicsWorld.destroyBody(myCurrentBody);                
            } catch (Exception e) {
            	Debug.e(e);
            }
        }
               
        this.clearChildScene();
        this.detachChildren();
        this.reset();
        this.detachSelf();
        physicsWorld.clearForces();
        physicsWorld.clearPhysicsConnectors();
        physicsWorld.reset();
 
        System.gc();
	}
	
	@Override
	public void handleOnPause() {
		
	}
	
	@Override
	public void onBackKeyPressed() {

	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				/*if (pSceneTouchEvent.isActionDown()) {
					player.changeWall();
				}*/
			}
		});
		return true;
	}
	
}
