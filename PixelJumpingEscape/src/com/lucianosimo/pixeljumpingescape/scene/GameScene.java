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
	private LeftWall[] leftWall;
	private RightWall[] rightWall;
	private LeftSpikes[] leftSpikes;
	private RightSpikes[] rightSpikes;
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
		createEnemies();
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
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createPlayer() {
		player = new Player(PLAYER_INITIAL_X, PLAYER_INITIAL_Y, vbom, camera, physicsWorld) {
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				incrementScore();
				if (this.collidesWith(moveBlocksSensor[0]) && movedBlocks > 0) {
					Log.d("pixel", "move sensor 0");
					moveBlocksSensor[0].setPosition(moveBlocksSensor[0].getX(), moveBlocksSensor[0].getY() + (1280 * (MAX_BLOCKS + movedBlocks)));
					for (int i = 0; i < leftWall.length; i++) {
						leftWall[2].getBody().setTransform(leftWall[2].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[2].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[2].getBody().getAngle());
					}
					for (int i = 0; i < rightWall.length; i++) {
						rightWall[2].getBody().setTransform(rightWall[2].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[2].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[2].getBody().getAngle());
					}
					for (int i = 0; i < leftSpikes.length; i++) {
						leftSpikes[2].getBody().setTransform(leftSpikes[2].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[2].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[2].getBody().getAngle());
					}
					for (int i = 0; i < rightSpikes.length; i++) {
						rightSpikes[2].getBody().setTransform(rightSpikes[2].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[2].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[2].getBody().getAngle());
					}
					movedBlocks++;
				} else if (this.collidesWith(moveBlocksSensor[1])) {
					Log.d("pixel", "move sensor 1");
					moveBlocksSensor[1].setPosition(moveBlocksSensor[1].getX(), moveBlocksSensor[1].getY() + (1280 * (MAX_BLOCKS + movedBlocks)));
					for (int i = 0; i < leftWall.length; i++) {
						leftWall[0].getBody().setTransform(leftWall[0].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[0].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[0].getBody().getAngle());
					}
					for (int i = 0; i < rightWall.length; i++) {
						rightWall[0].getBody().setTransform(rightWall[0].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[0].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[0].getBody().getAngle());
					}
					for (int i = 0; i < leftSpikes.length; i++) {
						leftSpikes[0].getBody().setTransform(leftSpikes[0].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[0].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[0].getBody().getAngle());
					}
					for (int i = 0; i < rightSpikes.length; i++) {
						rightSpikes[0].getBody().setTransform(rightSpikes[0].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[0].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[0].getBody().getAngle());
					}
					movedBlocks++;					
				} else if (this.collidesWith(moveBlocksSensor[2])) {
					Log.d("pixel", "move sensor 2");
					moveBlocksSensor[2].setPosition(moveBlocksSensor[2].getX(), moveBlocksSensor[2].getY() + (1280 * (MAX_BLOCKS + movedBlocks)));
					for (int i = 0; i < leftWall.length; i++) {
						leftWall[1].getBody().setTransform(leftWall[1].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftWall[1].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftWall[1].getBody().getAngle());
					}
					for (int i = 0; i < rightWall.length; i++) {
						rightWall[1].getBody().setTransform(rightWall[1].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightWall[1].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightWall[1].getBody().getAngle());
					}
					for (int i = 0; i < leftSpikes.length; i++) {
						leftSpikes[1].getBody().setTransform(leftSpikes[1].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (leftSpikes[1].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, leftSpikes[1].getBody().getAngle());
					}
					for (int i = 0; i < rightSpikes.length; i++) {
						rightSpikes[1].getBody().setTransform(rightSpikes[1].getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (rightSpikes[1].getY() + (1280 * (MAX_BLOCKS + movedBlocks))) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rightSpikes[1].getBody().getAngle());
					}
					movedBlocks++;
				}
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
						//GameScene.this.attachChild(gameOverWindow);
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
		leftWall = new LeftWall[MAX_BLOCKS];
		rightWall = new RightWall[MAX_BLOCKS];
		leftSpikes = new LeftSpikes[MAX_BLOCKS];
		rightSpikes = new RightSpikes[MAX_BLOCKS];
		int[] leftWallPositions;
		int[] rightWallPositions;
		int[] leftSpikesPositions;
		int[] rightSpikesPositions;

		for (int i = 0; i < MAX_BLOCKS; i++) {
			random = rand.nextInt(MAX_BLOCKS) + 1;
			if (random == 1) {
				leftWallPositions = new int[] {64, 192, 448, 704, 1088, 1216};
				rightWallPositions = new int[] {64, 320, 576, 960};
				leftSpikesPositions = new int[] {320, 576, 832, 960};
				rightSpikesPositions = new int[] {192, 448, 704, 832, 1088, 1216};
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
			
			moveBlocksSensor[i] = new Rectangle(screenWidth / 2, 1280 * i + screenHeight / 2, screenWidth, 10, vbom);
			moveBlocksSensor[i].setColor(Color.RED);
			GameScene.this.attachChild(moveBlocksSensor[i]);
			
			for (int j = 0; j < leftWallPositions.length; j++) {
				leftWall[i] = new LeftWall(WALL_WIDTH / 2, leftWallPositions[j] + (1280 * i), vbom, camera, physicsWorld) {
					@Override
					protected void onManagedUpdate(float pSecondsElapsed) {
						super.onManagedUpdate(pSecondsElapsed);
					}
				};
				GameScene.this.attachChild(leftWall[i]);
				
			}
			for (int j = 0; j < rightWallPositions.length; j++) {
				rightWall[i] = new RightWall(screenWidth - (WALL_WIDTH / 2), rightWallPositions[j] + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightWall[i]);
			}
			for (int j = 0; j < leftSpikesPositions.length; j++) {
				leftSpikes[i] = new LeftSpikes(SPIKES_WIDTH / 2, leftSpikesPositions[j] + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(leftSpikes[i]);
			}
			for (int j = 0; j < rightSpikesPositions.length; j++) {
				rightSpikes[i] = new RightSpikes(screenWidth - (SPIKES_WIDTH / 2), rightSpikesPositions[j] + (1280 * i), vbom, camera, physicsWorld);
				GameScene.this.attachChild(rightSpikes[i]);
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
