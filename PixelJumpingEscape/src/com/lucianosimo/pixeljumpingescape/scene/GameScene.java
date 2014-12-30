package com.lucianosimo.pixeljumpingescape.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import android.util.Log;

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
import com.lucianosimo.pixeljumpingescape.manager.ResourcesManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager.SceneType;
import com.lucianosimo.pixeljumpingescape.object.CenterSpikes;
import com.lucianosimo.pixeljumpingescape.object.CenterSpikesWithMove;
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
	private ArrayList<CenterSpikes> centerSpikes;
	private ArrayList<CenterSpikesWithMove> centerSpikesWithMove;
	private Spider spider;
	
	//Decoration
	private Sprite spider_web;
	private Rectangle spider_web_line;
	
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
	private Rectangle centerBlocksSensor;
	private Rectangle centerMovingBlocksSensor;
	private Rectangle incrementSpeedSensor;
	private Rectangle spiderMoveSensor;

	//Constants
	
	//CAMERA VARIABLES
	private final static int CAMERA_INITIAL_SPEED = -200;
	private final static int CAMERA_SPEED_INCREMENT = 10;
	
	//PLAYER VARIABLES
	private final static int PLAYER_INITIAL_X = 360;
	private final static int PLAYER_INITIAL_Y = 300;
	private final static float Y_JUMP_SPEED_MULTIPLIER = 0.069444444444f;
	
	//NUMBER OF BLOCKS
	private final static int MAX_BLOCKS = 10;
	private final static int MAX_CENTER_BLOCKS = 8;
	private final static int MAX_MOVING_CENTER_BLOCKS = 8;
	private final static int WALLS_PER_BLOCKS = 6;
	
	//SPIDER VARIABLES
	private final static int SPIDERS_BLOCKS_TO_REAPPEAR = 10;
	private final static int SPIDER_INITIAL_X_LEFT = 150;
	private final static int SPIDER_INITIAL_X_RIGHT = 570;
	private final static int SPIDER_INITIAL_Y = 6400;
	
	//STATIC SPIKES VARIABLES
	private final static int CENTER_SPIKES_INITIAL_BLOCKS_TO_APPEAR = 10;
	private final static int CENTER_SPIKES_BLOCKS_TO_REAPPEAR = 20;
	private final static int SPIKES_WIDTH = 80;
	private final static int CENTER_SPIKES_MAX_OFFSET_LEFT = -200;
	private final static int CENTER_SPIKES_MAX_OFFSET_RIGHT = 200;
	//private final static int SPIKES_HEIGHT = 128;
	//private final static int CENTER_SPIKES_WIDTH = 75;
	//private final static int CENTER_SPIKES_HEIGHT = 75;
	
	//MOVING SPIKES VARIABLES
	private final static int CENTER_MOVING_SPIKES_INITIAL_BLOCKS_TO_APPEAR = 20;
	private final static int CENTER_MOVING_SPIKES_BLOCKS_TO_REAPPEAR = 20;
	
	//WALL AND FLOOR VARIABLES
	private final static int WALL_WIDTH = 100;
	private final static int WALL_HEIGHT = 128;
	private final static int FLOOR_HEIGHT = 256;
	
	//BUTTONS VARIABLES
	private final static int BUTTON_WIDTH = 200;
	private final static int BUTTON_HEIGHT = 1280;
		
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
		createWalls();
		createEnemies();
		createWindows();
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
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 10);
		background.attachParallaxEntity(new ParallaxEntity(-10f, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_background_region, vbom)));
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
				if (player.isOnLeftWall()) {
					player.setCurrentTileIndex(1);
				}
				if (player.isOnRightWall()) {
					player.setCurrentTileIndex(0);
				}
				if (player.collidesWith(incrementSpeedSensor)) {
					incrementSpeedSensor.setPosition(incrementSpeedSensor.getX(), incrementSpeedSensor.getY() + screenHeight / 2);
					incrementCameraSpeed();
				}
				if (player.collidesWith(centerBlocksSensor)) {
					centerBlocksSensor.setPosition(centerBlocksSensor.getX(), centerBlocksSensor.getY() + screenHeight * CENTER_SPIKES_BLOCKS_TO_REAPPEAR);
					moveCenterBlocks();
				}
				if (player.collidesWith(centerMovingBlocksSensor)) {
					centerMovingBlocksSensor.setPosition(centerMovingBlocksSensor.getX(), centerMovingBlocksSensor.getY() + screenHeight * CENTER_MOVING_SPIKES_BLOCKS_TO_REAPPEAR);
					moveMovingCenterBlocks();
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
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int blockOrNo;
		int centerBlocksOffset;
		int centerBlockHorizontalMove;
		boolean horizontalMove;
		long seed;
		moveBlocksSensor = new Rectangle[MAX_BLOCKS];
		leftWall = new LeftWall[MAX_BLOCKS][WALLS_PER_BLOCKS];
		rightWall = new RightWall[MAX_BLOCKS][WALLS_PER_BLOCKS];
		leftSpikes = new LeftSpikes[MAX_BLOCKS][MAX_BLOCKS - WALLS_PER_BLOCKS];
		rightSpikes = new RightSpikes[MAX_BLOCKS][MAX_BLOCKS - WALLS_PER_BLOCKS];
		centerSpikes = new ArrayList<>();
		centerSpikesWithMove = new ArrayList<>();
		
		ArrayList<Integer> leftPositions = new ArrayList<>();
		ArrayList<Integer> rightPositions = new ArrayList<>();
		ArrayList<Float> centerPositions = new ArrayList<>();
		ArrayList<Float> centerMovingPositions = new ArrayList<>();
		
		ArrayList<Integer> leftWallPositions = new ArrayList<>();
		ArrayList<Integer> rightWallPositions = new ArrayList<>();
		ArrayList<Integer> leftSpikesPositions = new ArrayList<>();
		ArrayList<Integer> rightSpikesPositions = new ArrayList<>();
		
		for (int i = 0; i < MAX_BLOCKS; i++) {
			leftPositions.add(WALL_HEIGHT/2 + (WALL_HEIGHT * i));
			rightPositions.add(WALL_HEIGHT/2 + (WALL_HEIGHT * i));
		}
		
		for (int i = 0; i < MAX_CENTER_BLOCKS; i++) {
			blockOrNo = rand.nextInt(3) + 1;
			if (blockOrNo == 1) {
				float position = (screenHeight * 3 / 2) + ((CENTER_SPIKES_INITIAL_BLOCKS_TO_APPEAR + i) * screenHeight);
				centerPositions.add(position);
			}
		}
		
		for (int i = 0; i < MAX_MOVING_CENTER_BLOCKS; i++) {
			blockOrNo = rand.nextInt(3) + 1;
			if (blockOrNo == 1) {
				float position = (screenHeight * 3 / 2) + ((CENTER_MOVING_SPIKES_INITIAL_BLOCKS_TO_APPEAR + i) * screenHeight);
				centerMovingPositions.add(position);
			}
		}

		Sprite floor = new Sprite(camera.getCenterX(), FLOOR_HEIGHT / 2, resourcesManager.game_floor_region, vbom);
		FixtureDef floor_fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		final Body floor_body = PhysicsFactory.createBoxBody(physicsWorld, floor, BodyType.StaticBody, floor_fixture);
		floor_body.setUserData("floor_body");
		GameScene.this.attachChild(floor);
		
		incrementSpeedSensor = new Rectangle(screenWidth / 2, screenHeight / 2, screenWidth, 0.01f, vbom);
		incrementSpeedSensor.setAlpha(1f);
		
		centerBlocksSensor = new Rectangle(screenWidth / 2, screenHeight * (CENTER_SPIKES_INITIAL_BLOCKS_TO_APPEAR + MAX_BLOCKS), screenWidth, 0.01f, vbom);
		centerBlocksSensor.setAlpha(1f);
		
		centerMovingBlocksSensor = new Rectangle(screenWidth / 2, screenHeight * (CENTER_MOVING_SPIKES_INITIAL_BLOCKS_TO_APPEAR + MAX_BLOCKS), screenWidth, 0.01f, vbom);
		centerMovingBlocksSensor.setAlpha(1f);
		
		for (int i = 0; i < centerPositions.size(); i++) {
			//n = rand.nextInt(max - min + 1) + min;
			centerBlocksOffset = rand.nextInt(CENTER_SPIKES_MAX_OFFSET_RIGHT - CENTER_SPIKES_MAX_OFFSET_LEFT + 1) + CENTER_SPIKES_MAX_OFFSET_LEFT;
			CenterSpikes centerSpike = new CenterSpikes(screenWidth/2 + centerBlocksOffset, centerPositions.get(i), vbom, camera, physicsWorld);
			centerSpikes.add(centerSpike);
			GameScene.this.attachChild(centerSpike);
		}
		
		for (int i = 0; i < centerMovingPositions.size(); i++) {
			//n = rand.nextInt(max - min + 1) + min;
			centerBlockHorizontalMove = rand.nextInt(2) + 1;
			centerBlocksOffset = rand.nextInt(CENTER_SPIKES_MAX_OFFSET_RIGHT - CENTER_SPIKES_MAX_OFFSET_LEFT + 1) + CENTER_SPIKES_MAX_OFFSET_LEFT;
			if (centerBlockHorizontalMove == 1) {
				horizontalMove = true;
				centerBlocksOffset = 0;
			} else {
				horizontalMove = false;
			}
			CenterSpikesWithMove centerSpike = new CenterSpikesWithMove(screenWidth / 2 + centerBlocksOffset, centerMovingPositions.get(i), vbom, camera, physicsWorld, horizontalMove);
			centerSpikesWithMove.add(centerSpike);
			GameScene.this.attachChild(centerSpike);
		}

		for (int i = 0; i < MAX_BLOCKS; i++) {
			moveBlocksSensor[i] = new Rectangle(screenWidth / 2, 1280 * i + screenHeight, screenWidth, 0.01f, vbom);
			moveBlocksSensor[i].setAlpha(1f);
			
			seed = System.nanoTime();
			Collections.shuffle(leftPositions, new Random(seed));
			seed = System.nanoTime();
			Collections.shuffle(rightPositions, new Random(seed));
			
			for (int j = 0; j < leftPositions.size(); j++) {
				if (j < WALLS_PER_BLOCKS) {
					leftWallPositions.add(leftPositions.get(j));
					rightWallPositions.add(rightPositions.get(j));
				} else {
					leftSpikesPositions.add(leftPositions.get(j));
					rightSpikesPositions.add(rightPositions.get(j));
				}
			}
			
			for (int j = 0; j < leftWallPositions.size(); j++) {
				leftWall[i][j] = new LeftWall(WALL_WIDTH / 2, leftWallPositions.get(j) + (screenHeight * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(leftWall[i][j]);
			}
			for (int j = 0; j < rightWallPositions.size(); j++) {
				rightWall[i][j] = new RightWall(screenWidth - (WALL_WIDTH / 2), rightWallPositions.get(j) + (screenHeight * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightWall[i][j]);
			}
			for (int j = 0; j < leftSpikesPositions.size(); j++) {
				leftSpikes[i][j] = new LeftSpikes(SPIKES_WIDTH / 2, leftSpikesPositions.get(j) + (screenHeight * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(leftSpikes[i][j]);
			}
			for (int j = 0; j < rightSpikesPositions.size(); j++) {
				rightSpikes[i][j] = new RightSpikes(screenWidth - (SPIKES_WIDTH / 2), rightSpikesPositions.get(j) + (screenHeight * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightSpikes[i][j]);
			}

			for (int j = 0; j < leftPositions.size(); j++) {
				if (j < WALLS_PER_BLOCKS) {
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
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int spiderLeftOrRight;
		int whichSpider;
		int initialX = SPIDER_INITIAL_X_LEFT;
		ITiledTextureRegion spider_region;
		whichSpider = rand.nextInt(2) + 1;
		spiderLeftOrRight = rand.nextInt(2) + 1;
		
		switch (whichSpider) {
		case 1:
			spider_region = resourcesManager.game_spider_region;
			break;
		case 2:
			spider_region = resourcesManager.game_spider_2_region;
			break;
		default:
			spider_region = resourcesManager.game_spider_region;
			break;
		}
		
		spiderMoveSensor = new Rectangle(screenWidth / 2, SPIDER_INITIAL_Y - screenHeight, screenWidth, 10f, vbom);
		spiderMoveSensor.setColor(Color.RED);
		if (spiderLeftOrRight == 1) {
			initialX = SPIDER_INITIAL_X_LEFT;
		} else {
			initialX = SPIDER_INITIAL_X_RIGHT;
		}
		spider = new Spider(initialX, SPIDER_INITIAL_Y, vbom, camera, physicsWorld, spider_region) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (player.collidesWith(spiderMoveSensor)) {
					this.startMoving();
					spiderMoveSensor.setPosition(spiderMoveSensor.getX(), spiderMoveSensor.getY() + SPIDERS_BLOCKS_TO_REAPPEAR * screenWidth);
				}
				if (this.isMoving()) {
					spider_web_line.setHeight(spider_web.getY() - this.getY());
					spider_web_line.setPosition(spider_web.getX(), spider_web.getY() - spider_web_line.getHeight() / 2);
				}
				if (this.getY() < (camera.getCenterY() - screenHeight / 2)) {
					moveSpider();
				}
				if (spider_web.getY() < (camera.getCenterY() - screenHeight / 2)) {
					spider_web.setPosition(this.getX(), this.getY());
				}
			}
		};
		
		spider_web = new Sprite(spider.getX(), spider.getY(), ResourcesManager.getInstance().game_spider_web_region, vbom);
		spider_web_line = new Rectangle(spider.getX(), spider.getY(), 3, 1, vbom);
		
		GameScene.this.attachChild(spider_web_line);
		GameScene.this.attachChild(spider);
		GameScene.this.attachChild(spider_web);		
	}
	
	private void incrementScore() {
		score = ((int) camera.getCenterY() - 640) / 320;
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
		moveBlocksSensor[indexBlockToMove].setPosition(screenWidth / 2, moveBlocksSensor[indexBlockToMove].getY() + (screenHeight * (MAX_BLOCKS)));
		moveBlocksSensor[index].setPosition(5000, moveBlocksSensor[index].getY());
		for (int i = 0; i < leftWall[indexBlockToMove].length; i++) {
			leftWall[indexBlockToMove][i].getBody().setTransform(leftWall[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[indexBlockToMove][i].getBody().getAngle());
			leftWall[indexBlockToMove][i].setPosition(leftWall[indexBlockToMove][i].getX(), leftWall[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS)));
		}
		for (int i = 0; i < rightWall[indexBlockToMove].length; i++) {
			rightWall[indexBlockToMove][i].getBody().setTransform(rightWall[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[indexBlockToMove][i].getBody().getAngle());
			rightWall[indexBlockToMove][i].setPosition(rightWall[indexBlockToMove][i].getX(), rightWall[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS)));
		}
		for (int i = 0; i < leftSpikes[indexBlockToMove].length; i++) {
			leftSpikes[indexBlockToMove][i].getBody().setTransform(leftSpikes[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[indexBlockToMove][i].getBody().getAngle());
			leftSpikes[indexBlockToMove][i].setPosition(leftSpikes[indexBlockToMove][i].getX(), leftSpikes[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS)));
		}
		for (int i = 0; i < rightSpikes[indexBlockToMove].length; i++) {
			rightSpikes[indexBlockToMove][i].getBody().setTransform(rightSpikes[indexBlockToMove][i].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[indexBlockToMove][i].getBody().getAngle());
			rightSpikes[indexBlockToMove][i].setPosition(rightSpikes[indexBlockToMove][i].getX(), rightSpikes[indexBlockToMove][i].getY() + (screenHeight * (MAX_BLOCKS)));
		}
		movedBlocks++;	
	}
	
	private void moveCenterBlocks() {
		for (int i = 0; i < centerSpikes.size(); i++) {
			centerSpikes.get(i).getBody().setTransform(centerSpikes.get(i).getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (centerSpikes.get(i).getY() + (screenHeight * (CENTER_SPIKES_BLOCKS_TO_REAPPEAR))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, centerSpikes.get(i).getBody().getAngle());
			centerSpikes.get(i).setPosition(centerSpikes.get(i).getX(), centerSpikes.get(i).getY() + (screenHeight * (CENTER_SPIKES_BLOCKS_TO_REAPPEAR)));
		}	
	}
	
	private void moveMovingCenterBlocks() {
		for (int i = 0; i < centerSpikesWithMove.size(); i++) {
			centerSpikesWithMove.get(i).getBody().setTransform(centerSpikesWithMove.get(i).getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (centerSpikesWithMove.get(i).getY() + (screenHeight * (CENTER_MOVING_SPIKES_BLOCKS_TO_REAPPEAR))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, centerSpikesWithMove.get(i).getBody().getAngle());
			centerSpikesWithMove.get(i).setPosition(centerSpikesWithMove.get(i).getX(), centerSpikesWithMove.get(i).getY() + (screenHeight * (CENTER_MOVING_SPIKES_BLOCKS_TO_REAPPEAR)));
		}	
	}
	
	private void moveSpider() {
		Random r = new Random();
		int x = 0;
		int leftOrRight = r.nextInt(2) + 1;
		if (leftOrRight == 1) {
			x = SPIDER_INITIAL_X_LEFT;
		} else {
			x = SPIDER_INITIAL_X_RIGHT;
		}
		spider.getBody().setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (spider.getY() + (screenHeight * SPIDERS_BLOCKS_TO_REAPPEAR)) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, spider.getBody().getAngle());
		spider.setPosition(x, spider.getY() + (screenHeight * SPIDERS_BLOCKS_TO_REAPPEAR));
		spider.stopMoving();
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
