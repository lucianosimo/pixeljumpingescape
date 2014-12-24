package com.lucianosimo.pixeljumpingescape.scene;

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
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
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

	//Constants
	private final static int MAX_BLOCKS = 3;
	private final static int PLAYER_INITIAL_X = 300;
	private final static int PLAYER_INITIAL_Y = 150;
	private final static int BUTTON_WIDTH = 200;
	private final static int BUTTON_HEIGHT = 1280;
	private final static int WALL_WIDTH = 100;
	private final static int WALL_HEIGHT = 128;
	private final static int SPIKES_WIDTH = 80;
	private final static int SPIKES_HEIGHT = 128;
	private final static int CENTER_SPIKES_WIDTH = 75;
	private final static int CENTER_SPIKES_HEIGHT = 75;
	private final static int CAMERA_SPEED_INCREMENT = 20;
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
		camera.setMaxVelocityY(-100);
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
				if (pSceneTouchEvent.isActionDown() && player.isOnRightWall() && !player.isOnAir()) {
					float yJumpPx = (camera.getCenterY() - screenHeight / 2) + pSceneTouchEvent.getY() - player.getY();
					float ySpeed = yJumpPx * Y_JUMP_SPEED_MULTIPLIER;
					player.goToLeftWall(ySpeed);
				}
				return false;
			}
		};
		rightButton = new Rectangle(screenWidth - (BUTTON_WIDTH / 2), screenHeight / 2, BUTTON_WIDTH, BUTTON_HEIGHT, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown() && player.isOnLeftWall() && !player.isOnAir()) {
					float yJumpPx = (camera.getCenterY() - screenHeight / 2) + pSceneTouchEvent.getY() - player.getY();
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
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -3), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createPlayer() {
		player = new Player(PLAYER_INITIAL_X, PLAYER_INITIAL_Y, vbom, camera, physicsWorld) {
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				incrementScore();
				if (player.collidesWith(moveBlocksSensor[0]) && movedBlocks > 0) {
					incrementCameraSpeed();
					moveBlocks(0);
					/*moveBlocksSensor[2].setPosition(screenWidth / 2, moveBlocksSensor[2].getY() + (1280 * (MAX_BLOCKS)));
					moveBlocksSensor[0].setPosition(5000, moveBlocksSensor[0].getY());
					for (int i = 0; i < leftWall[2].length; i++) {
						leftWall[2][i].getBody().setTransform(leftWall[2][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[2][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[2][i].getBody().getAngle());
						leftWall[2][i].setPosition(leftWall[2][i].getX(), leftWall[2][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < rightWall[2].length; i++) {
						rightWall[2][i].getBody().setTransform(rightWall[2][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[2][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[2][i].getBody().getAngle());
						rightWall[2][i].setPosition(rightWall[2][i].getX(), rightWall[2][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < leftSpikes[2].length; i++) {
						leftSpikes[2][i].getBody().setTransform(leftSpikes[2][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[2][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[2][i].getBody().getAngle());
						leftSpikes[2][i].setPosition(leftSpikes[2][i].getX(), leftSpikes[2][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < rightSpikes[2].length; i++) {
						rightSpikes[2][i].getBody().setTransform(rightSpikes[2][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[2][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[2][i].getBody().getAngle());
						rightSpikes[2][i].setPosition(rightSpikes[2][i].getX(), rightSpikes[2][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					movedBlocks++;*/
				} else if (player.collidesWith(moveBlocksSensor[1])) {
					incrementCameraSpeed();
					moveBlocks(1);
					/*moveBlocksSensor[0].setPosition(screenWidth / 2, moveBlocksSensor[0].getY() + (1280 * (MAX_BLOCKS)));
					moveBlocksSensor[1].setPosition(5000, moveBlocksSensor[1].getY());
					for (int i = 0; i < leftWall[0].length; i++) {
						leftWall[0][i].getBody().setTransform(leftWall[0][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[0][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[0][i].getBody().getAngle());
						leftWall[0][i].setPosition(leftWall[0][i].getX(), leftWall[0][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < rightWall[0].length; i++) {
						rightWall[0][i].getBody().setTransform(rightWall[0][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[0][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[0][i].getBody().getAngle());
						rightWall[0][i].setPosition(rightWall[0][i].getX(), rightWall[0][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < leftSpikes[0].length; i++) {
						leftSpikes[0][i].getBody().setTransform(leftSpikes[0][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[0][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[0][i].getBody().getAngle());
						leftSpikes[0][i].setPosition(leftSpikes[0][i].getX(), leftSpikes[0][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < rightSpikes[0].length; i++) {
						rightSpikes[0][i].getBody().setTransform(rightSpikes[0][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[0][i].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[0][i].getBody().getAngle());
						rightSpikes[0][i].setPosition(rightSpikes[0][i].getX(), rightSpikes[0][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					movedBlocks++;*/			
				} else if (player.collidesWith(moveBlocksSensor[2])) {
					incrementCameraSpeed();
					moveBlocks(2);
					/*moveBlocksSensor[1].setPosition(screenWidth / 2, moveBlocksSensor[1].getY() + (1280 * (MAX_BLOCKS)));
					moveBlocksSensor[2].setPosition(5000, moveBlocksSensor[2].getY());
					for (int i = 0; i < leftWall[1].length; i++) {
						leftWall[1][i].getBody().setTransform(leftWall[1][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[1][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[1][i].getBody().getAngle());
						leftWall[1][i].setPosition(leftWall[1][i].getX(), leftWall[1][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < rightWall[1].length; i++) {
						rightWall[1][i].getBody().setTransform(rightWall[1][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[1][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[1][i].getBody().getAngle());
						rightWall[1][i].setPosition(rightWall[1][i].getX(), rightWall[1][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < leftSpikes[1].length; i++) {
						leftSpikes[1][i].getBody().setTransform(leftSpikes[1][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[1][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[1][i].getBody().getAngle());
						leftSpikes[1][i].setPosition(leftSpikes[1][i].getX(), leftSpikes[1][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					for (int i = 0; i < rightSpikes[1].length; i++) {
						rightSpikes[1][i].getBody().setTransform(rightSpikes[1][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[1][i].getY() + (1280 * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[1][i].getBody().getAngle());
						rightSpikes[1][i].setPosition(rightSpikes[1][i].getX(), rightSpikes[1][i].getY() + (1280 * (MAX_BLOCKS)));
					}
					movedBlocks++;*/
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
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int random;
		moveBlocksSensor = new Rectangle[MAX_BLOCKS];
		leftWall = new LeftWall[MAX_BLOCKS][5];
		rightWall = new RightWall[MAX_BLOCKS][5];
		leftSpikes = new LeftSpikes[MAX_BLOCKS][5];
		rightSpikes = new RightSpikes[MAX_BLOCKS][5];
		int[] leftWallPositions;
		int[] rightWallPositions;
		int[] leftSpikesPositions;
		int[] rightSpikesPositions;

		for (int i = 0; i < MAX_BLOCKS; i++) {
			random = rand.nextInt(MAX_BLOCKS) + 1;
			if (random == 1) {
				leftWallPositions = new int[] {64, 192, 448, 704, 1088};
				rightWallPositions = new int[] {64, 320, 576, 960, 1088};
				leftSpikesPositions = new int[] {320, 576, 832, 960, 1216};
				rightSpikesPositions = new int[] {192, 448, 704, 832, 1216};
			} else if (random == 2) {
				leftWallPositions = new int[] {64, 448, 576, 1088, 1216};
				rightWallPositions = new int[] {320, 448, 704, 832, 1216};
				leftSpikesPositions = new int[] {192, 320, 704, 832, 960};
				rightSpikesPositions = new int[] {64, 192, 576, 960, 1088};
			} else if (random == 3) {
				leftWallPositions = new int[] {64, 192, 320, 704, 960};
				rightWallPositions = new int[] {64, 320, 448, 960, 1216};
				leftSpikesPositions = new int[] {448, 576, 832, 1088, 1216};
				rightSpikesPositions = new int[] {192, 576, 704, 832, 1088};
			} else {
				leftWallPositions = new int[] {0};
				rightWallPositions = new int[] {0};
				leftSpikesPositions = new int[] {0};
				rightSpikesPositions = new int[] {0};
			}
			
			moveBlocksSensor[i] = new Rectangle(screenWidth / 2, 1280 * i + screenHeight, screenWidth, 10, vbom);
			moveBlocksSensor[i].setColor(Color.RED);
			GameScene.this.attachChild(moveBlocksSensor[i]);
			
			for (int j = 0; j < leftWallPositions.length; j++) {
				leftWall[i][j] = new LeftWall(WALL_WIDTH / 2, leftWallPositions[j] + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(leftWall[i][j]);
			}
			for (int j = 0; j < rightWallPositions.length; j++) {
				rightWall[i][j] = new RightWall(screenWidth - (WALL_WIDTH / 2), rightWallPositions[j] + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightWall[i][j]);
			}
			for (int j = 0; j < leftSpikesPositions.length; j++) {
				leftSpikes[i][j] = new LeftSpikes(SPIKES_WIDTH / 2, leftSpikesPositions[j] + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(leftSpikes[i][j]);
			}
			for (int j = 0; j < rightSpikesPositions.length; j++) {
				rightSpikes[i][j] = new RightSpikes(screenWidth - (SPIKES_WIDTH / 2), rightSpikesPositions[j] + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightSpikes[i][j]);
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
			rightSpikes[indexBlockToMove][i].getBody().setTransform(rightSpikes[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[indexBlockToMove][i].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[indexBlockToMove][i].getBody().getAngle());
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
