package com.lucianosimo.pixeljumpingescape.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.lucianosimo.pixeljumpingescape.base.BaseScene;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene menuChildScene;
	private MenuScene storeScene;
	
	private IMenuItem menuPlayItem;
	private IMenuItem menuStoreItem;
	private IMenuItem storeMenuItem;
	
	private Sprite menuSelectionMenuBackground;
	private ScaleMenuItemDecorator menuSelectionCloseButton;
	private ScaleMenuItemDecorator menuSelectionOpenButton;
	
	private float screenWidth;
	private float screenHeight;
	
	final Rectangle fade = new Rectangle(360, 640, 720, 1280, vbom);
	
	private boolean isInMainMenu = true;
	private boolean unlockedNerd = false;
	private boolean unlockedNinja = false;
	private boolean unlockedRobot = false;
	private boolean unlockedBrick = false;
	private boolean unlockedWood = false;
	private boolean unlockedSteel = false;
	
	private Sprite lightBeard;
	private Sprite lockedBarsNerd;
	private Sprite unlockNerdButton;
	private Sprite lightNerd;
	private Sprite lockedBarsNinja;
	private Sprite unlockNinjaButton;
	private Sprite lightNinja;
	private Sprite lockedBarsRobot;
	private Sprite unlockRobotButton;
	private Sprite lightRobot;
	private Sprite unlockBrickButton;
	private Sprite unlockWoodButton;
	private Sprite unlockSteelButton;
	private Sprite playAdButton;
	
	private int coins;
	private Text coinsText;
	
	private final static int WALL_HEIGHT = 128;
	private final static int NERD_UNLOCK_VALUE = 25;
	private final static int NINJA_UNLOCK_VALUE = 100;
	private final static int ROBOT_UNLOCK_VALUE = 250;
	private final static int BRICK_UNLOCK_VALUE = 25;
	private final static int WOOD_UNLOCK_VALUE = 100;
	private final static int STEEL_UNLOCK_VALUE = 250;
	
	private static final IEaseFunction[][] EASEFUNCTIONS = new IEaseFunction[][] {
		new IEaseFunction[] { 
				//EaseQuadIn.getInstance()},
				EaseStrongIn.getInstance()},
	};
	
	private final int MENU_PLAY = 0;
	private final int MENU_STORE = 1;
	private final int MENU_OPEN_SELECTION = 2;
	private final int MENU_CLOSE_SELECTION = 3;
	private final int STORE_BACK = 5;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		menuChildScene = new MenuScene(camera);
		storeScene = new MenuScene(camera);
		createBackground();
		createMenuChildScene();
		createStoreChildScene();
		loadGameVariables();
	}

	@Override
	public void onBackKeyPressed() {
		if (isInMainMenu) {
			System.exit(0);
		} else {
			isInMainMenu = true;
			setMainMenuButtonsPositions();
			storeScene.back();
		}		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
	}
	
	public void createBackground() {
		ArrayList<Float> leftPositions = new ArrayList<>();
		ArrayList<Float> rightPositions = new ArrayList<>();	
		Sprite title = new Sprite(0, screenHeight / 2 - 200, resourcesManager.menu_title_region, vbom);
		Long seed;
		Random rand = new Random();
		int left;
		int right;
		
		//Initialize the laterals positions array for walls and spikes
		for (int i = 0; i < 10; i++) {
			leftPositions.add(WALL_HEIGHT/2 + (WALL_HEIGHT * i) - screenHeight / 2);
			rightPositions.add(WALL_HEIGHT/2 + (WALL_HEIGHT * i) - screenHeight / 2);
		}
		
		seed = System.nanoTime();
		Collections.shuffle(leftPositions, new Random(seed));
		seed = System.nanoTime();
		Collections.shuffle(rightPositions, new Random(seed));
		
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 5);
		ParallaxEntity backgroundParallaxEntity = new ParallaxEntity(-10f, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.menu_background_region, vbom));
		background.attachParallaxEntity(backgroundParallaxEntity);
		this.setBackground(background);
		
		for (int i = 0; i < leftPositions.size(); i++) {
			left = rand.nextInt(2) + 1;
			right = rand.nextInt(2) + 1;
			if (left == 1) {
				Sprite leftWall = new Sprite(50 - screenWidth / 2, leftPositions.get(i), resourcesManager.menu_wall_region, vbom);
				menuChildScene.attachChild(leftWall);
			} else {
				Sprite leftSpike = new Sprite(50 - screenWidth / 2, leftPositions.get(i), resourcesManager.menu_left_spikes_region, vbom);
				menuChildScene.attachChild(leftSpike);
			}
			if (right == 1) {
				Sprite rightWall = new Sprite(screenWidth/2 - 50, leftPositions.get(i), resourcesManager.menu_wall_region, vbom);
				menuChildScene.attachChild(rightWall);
			} else {
				Sprite rightSpike = new Sprite(screenWidth/2 -  50, leftPositions.get(i), resourcesManager.menu_right_spikes_region, vbom);
				menuChildScene.attachChild(rightSpike);
			}
		}
		
		menuChildScene.attachChild(title);
	}
	
	private void createMenuChildScene() {
		
		menuChildScene.setPosition(screenWidth/2, screenHeight/2);
		
		menuPlayItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.menu_play_button_region, vbom), 1.2f, 1);
		menuStoreItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_STORE, resourcesManager.menu_store_button_region, vbom), 1.2f, 1);
		//menuSelectionMenuBackground = new Sprite(0, -screenHeight/2 + 200, resourcesManager.menu_selection_menu_background_region, vbom);
		menuSelectionMenuBackground = new Sprite(0, -screenHeight/2 - 150, resourcesManager.menu_selection_menu_background_region, vbom);
		menuSelectionOpenButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPEN_SELECTION, resourcesManager.menu_selection_open_button_region, vbom), 1.2f, 1);
		menuSelectionCloseButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_CLOSE_SELECTION, resourcesManager.menu_selection_close_button_region, vbom), 1.2f, 1);
		
		menuChildScene.attachChild(menuSelectionMenuBackground);
		
		menuChildScene.addMenuItem(menuPlayItem);
		menuChildScene.addMenuItem(menuStoreItem);
		menuChildScene.addMenuItem(menuSelectionOpenButton);
		//menuChildScene.addMenuItem(menuSelectionCloseButton);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		setMainMenuButtonsPositions();
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
	
	private void createStoreChildScene() {
		storeMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(STORE_BACK, resourcesManager.store_back_button_region, vbom), 1.2f, 1);
		storeScene.addMenuItem(storeMenuItem);
		storeScene.buildAnimations();
		
		coinsText = new Text(screenWidth / 2 + 40, screenHeight - 110, resourcesManager.store_coins_font, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		playAdButton = new Sprite(362, 200, resourcesManager.store_play_ad_button_region, vbom);
		
		storeMenuItem.setPosition(10000, 10000);
		
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 5);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.store_background_region, vbom)));
		storeScene.setBackground(background);
		
		storeScene.setBackgroundEnabled(true);
		
		storeScene.setOnMenuItemClickListener(this);
		
		storeScene.attachChild(coinsText);
		storeScene.attachChild(playAdButton);
	}
	
	private void setStoreButtonsPositions() {
		storeMenuItem.setPosition(100, 100);
	}
	
	private void openSelectionMenu() {
		MainMenuScene.this.activity.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				final IEaseFunction[] easeFunction = EASEFUNCTIONS[0];
				menuSelectionMenuBackground.clearEntityModifiers();
				menuSelectionMenuBackground.registerEntityModifier(new MoveModifier(1, menuSelectionMenuBackground.getX(), 
						menuSelectionMenuBackground.getY(), menuSelectionMenuBackground.getX(), menuSelectionMenuBackground.getY() + 375, easeFunction[0]));
				
				menuSelectionOpenButton.clearEntityModifiers();
				menuSelectionOpenButton.registerEntityModifier(new MoveModifier(1, menuSelectionOpenButton.getX(), 
						menuSelectionOpenButton.getY(), menuSelectionOpenButton.getX(), menuSelectionOpenButton.getY() + 370, easeFunction[0]) {
					@Override
					protected void onModifierFinished(IEntity pItem) {
						super.onModifierFinished(pItem);
						menuChildScene.addMenuItem(menuSelectionCloseButton);
						menuSelectionCloseButton.setPosition(menuSelectionOpenButton.getX(), menuSelectionOpenButton.getY());
						menuChildScene.detachChild(menuSelectionOpenButton);						
					}
				});
				
				menuPlayItem.clearEntityModifiers();
				menuPlayItem.registerEntityModifier(new MoveModifier(1, menuPlayItem.getX(), 
						menuPlayItem.getY(), menuPlayItem.getX(), menuPlayItem.getY() + 100, easeFunction[0]));
				
				menuStoreItem.clearEntityModifiers();
				menuStoreItem.registerEntityModifier(new MoveModifier(1, menuStoreItem.getX(), 
						menuStoreItem.getY(), menuStoreItem.getX(), menuStoreItem.getY() + 100, easeFunction[0]));				
			}
		});
	}
	
	private void closeSelectionMenu() {
		MainMenuScene.this.activity.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				final IEaseFunction[] easeFunction = EASEFUNCTIONS[0];
				menuSelectionMenuBackground.clearEntityModifiers();
				menuSelectionMenuBackground.registerEntityModifier(new MoveModifier(1, menuSelectionMenuBackground.getX(), 
						menuSelectionMenuBackground.getY(), menuSelectionMenuBackground.getX(), menuSelectionMenuBackground.getY() - 375, easeFunction[0]));
				
				menuSelectionCloseButton.clearEntityModifiers();
				menuSelectionCloseButton.registerEntityModifier(new MoveModifier(1, menuSelectionCloseButton.getX(), 
						menuSelectionCloseButton.getY(), menuSelectionCloseButton.getX(), menuSelectionCloseButton.getY() - 370, easeFunction[0]) {
					@Override
					protected void onModifierFinished(IEntity pItem) {
						super.onModifierFinished(pItem);
						menuChildScene.addMenuItem(menuSelectionOpenButton);
						menuSelectionOpenButton.setPosition(menuSelectionCloseButton.getX(), menuSelectionCloseButton.getY());
						menuChildScene.detachChild(menuSelectionCloseButton);
					}
				});
				
				menuPlayItem.clearEntityModifiers();
				menuPlayItem.registerEntityModifier(new MoveModifier(1, menuPlayItem.getX(), 
						menuPlayItem.getY(), menuPlayItem.getX(), menuPlayItem.getY() - 100, easeFunction[0]));
				
				menuStoreItem.clearEntityModifiers();
				menuStoreItem.registerEntityModifier(new MoveModifier(1, menuStoreItem.getX(), 
						menuStoreItem.getY(), menuStoreItem.getX(), menuStoreItem.getY() - 100, easeFunction[0]));
				
			}
		});
	}
	
	private void setMainMenuButtonsPositions() {
		menuPlayItem.setPosition(0, 75);
		menuStoreItem.setPosition(0, -125);
		//menuSelectionCloseButton.setPosition(-195, -260);
		menuSelectionOpenButton.setPosition(-195, -screenHeight / 2 + 35);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_PLAY:
				SceneManager.getInstance().loadGameScene(engine, this);
				return true;
			case MENU_STORE:
				menuPlayItem.setPosition(10000, 10000);
				menuStoreItem.setPosition(10000, 10000);
				isInMainMenu = false;
				menuChildScene.setChildScene(storeScene);
				setStoreButtonsPositions();
				return true;
			case MENU_CLOSE_SELECTION:
				closeSelectionMenu();	
				return true;
			case MENU_OPEN_SELECTION:
				openSelectionMenu();	
				return true;
			case STORE_BACK:
				setMainMenuButtonsPositions();
				isInMainMenu = true;
				storeScene.back();
				return true;
			default:
				return false;
		}
	}

	@Override
	public void handleOnPause() {
		
	}
	
	private void loadGameVariables() {
		loadCoins();
		loadUnlockedPlayers();
		loadUnlockedStages();
		loadPlayers();
		loadStages();
	}
	
	private void loadCoins() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		coins = sharedPreferences.getInt("coins", 0);
		coinsText.setText("" + coins);
	}
	
	private void loadPlayers() {
		lightBeard = new Sprite(110, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
		if (!unlockedNerd) {
			lockedBarsNerd = new Sprite(280, 940, resourcesManager.store_locked_player_bars_region, vbom);
			lightNerd = new Sprite(280, 1075, resourcesManager.store_locked_player_light_region, vbom);
			unlockNerdButton = new Sprite(280, 750, resourcesManager.store_unlock_player_button_region, vbom)  {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						if (coins >= NERD_UNLOCK_VALUE) {
							confirmMessage("nerd", NERD_UNLOCK_VALUE);						
						} else {
							int coinsToUnlock = NERD_UNLOCK_VALUE - coins;
							noEnoughCoins("nerd", coinsToUnlock);
						}
					}
					return true;
				}
			};
			storeScene.attachChild(unlockNerdButton);
			storeScene.attachChild(lockedBarsNerd);
			storeScene.registerTouchArea(unlockNerdButton);
		} else {
			lightNerd = new Sprite(280, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
		}
		if (!unlockedNinja) {
			lockedBarsNinja = new Sprite(450, 940, resourcesManager.store_locked_player_bars_region, vbom);
			lightNinja = new Sprite(450, 1075, resourcesManager.store_locked_player_light_region, vbom);
			unlockNinjaButton = new Sprite(450, 750, resourcesManager.store_unlock_player_button_region, vbom)  {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						if (coins >= NINJA_UNLOCK_VALUE) {
							confirmMessage("ninja", NINJA_UNLOCK_VALUE);						
						} else {
							int coinsToUnlock = NINJA_UNLOCK_VALUE - coins;
							noEnoughCoins("ninja", coinsToUnlock);
						}
					}
					return true;
				}
			};
			storeScene.attachChild(unlockNinjaButton);
			storeScene.attachChild(lockedBarsNinja);
			storeScene.registerTouchArea(unlockNinjaButton);
		} else {
			lightNinja = new Sprite(450, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
		}
		if (!unlockedRobot) {
			lockedBarsRobot = new Sprite(620, 940, resourcesManager.store_locked_player_bars_region, vbom);
			lightRobot = new Sprite(620, 1075, resourcesManager.store_locked_player_light_region, vbom);
			unlockRobotButton = new Sprite(620, 750, resourcesManager.store_unlock_player_button_region, vbom)  {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						if (coins >= ROBOT_UNLOCK_VALUE) {
							confirmMessage("robot", ROBOT_UNLOCK_VALUE);						
						} else {
							int coinsToUnlock = ROBOT_UNLOCK_VALUE - coins;
							noEnoughCoins("robot", coinsToUnlock);
						}
					}
					return true;
				}
			};
			storeScene.attachChild(unlockRobotButton);
			storeScene.attachChild(lockedBarsRobot);
			storeScene.registerTouchArea(unlockRobotButton);
		} else {
			lightRobot = new Sprite(620, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
		}
		storeScene.attachChild(lightBeard);
		storeScene.attachChild(lightNerd);
		storeScene.attachChild(lightNinja);
		storeScene.attachChild(lightRobot);
	}
	
	private void loadStages() {
		if (!unlockedBrick) {
			unlockBrickButton = new Sprite(272, 435, resourcesManager.store_unlock_stage_button_region, vbom)  {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						if (coins >= BRICK_UNLOCK_VALUE) {
							confirmMessage("brick", BRICK_UNLOCK_VALUE);						
						} else {
							int coinsToUnlock = BRICK_UNLOCK_VALUE - coins;
							noEnoughCoins("brick", coinsToUnlock);
						}
					}
					return true;
				}
			};
			storeScene.attachChild(unlockBrickButton);
			storeScene.registerTouchArea(unlockBrickButton);
		}
		if (!unlockedWood) {
			unlockWoodButton = new Sprite(439, 435, resourcesManager.store_unlock_stage_button_region, vbom)  {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						if (coins >= WOOD_UNLOCK_VALUE) {
							confirmMessage("wood", WOOD_UNLOCK_VALUE);						
						} else {
							int coinsToUnlock = WOOD_UNLOCK_VALUE - coins;
							noEnoughCoins("wood", coinsToUnlock);
						}
					}
					return true;
				}
			};
			storeScene.attachChild(unlockWoodButton);
			storeScene.registerTouchArea(unlockWoodButton);
		}
		if (!unlockedSteel) {
			unlockSteelButton = new Sprite(605, 435, resourcesManager.store_unlock_stage_button_region, vbom)  {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						if (coins >= STEEL_UNLOCK_VALUE) {
							confirmMessage("steel", STEEL_UNLOCK_VALUE);						
						} else {
							int coinsToUnlock = STEEL_UNLOCK_VALUE - coins;
							noEnoughCoins("steel", coinsToUnlock);
						}
					}
					return true;
				}
			};
			storeScene.attachChild(unlockSteelButton);
			storeScene.registerTouchArea(unlockSteelButton);
		}
	}
	
	private void confirmMessage(final String player, final int coins) {
		MainMenuScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(MainMenuScene.this.activity)
				.setMessage("Do you want to unlock " + player + " for " + coins + " coins")
				.setPositiveButton("Of course!!!", new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int whichButton) {
				    	if (player.equals("nerd")) {
				    		unlockNerd();
				    		storeScene.detachChild(lockedBarsNerd);
				    		storeScene.detachChild(unlockNerdButton);
				    		storeScene.detachChild(lightNerd);
				    		lightNerd = new Sprite(280, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
				    		storeScene.attachChild(lightNerd);
				    	}
				    	if (player.equals("ninja")) {
				    		unlockNinja();
				    		storeScene.detachChild(lockedBarsNinja);
				    		storeScene.detachChild(unlockNinjaButton);
				    		storeScene.detachChild(lightNinja);
				    		lightNinja = new Sprite(450, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
				    		storeScene.attachChild(lightNinja);
				    	}
				    	if (player.equals("robot")) {
				    		unlockRobot();
				    		storeScene.detachChild(lockedBarsRobot);
				    		storeScene.detachChild(unlockRobotButton);
				    		storeScene.detachChild(lightRobot);
				    		lightRobot = new Sprite(620, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
				    		storeScene.attachChild(lightRobot);
				    	}
				    	if (player.equals("brick")) {
				    		unlockBrick();
				    		storeScene.detachChild(unlockBrickButton);
				    	}
				    	if (player.equals("wood")) {
				    		unlockWood();
				    		storeScene.detachChild(unlockWoodButton);
				    	}
				    	if (player.equals("steel")) {
				    		unlockSteel();
				    		storeScene.detachChild(unlockSteelButton);
				    	}
				        Toast.makeText(activity, player + " unlocked", Toast.LENGTH_LONG).show();
				        loadUnlockedPlayers();
				    }})
				 .setNegativeButton("Mmmm, not really", null).show();	
			}
		});
	}
	
	private void noEnoughCoins(final String player, final int coins) {
		MainMenuScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(activity, "No enough coins. Collect " + coins + " more to unlock " + player, Toast.LENGTH_SHORT).show();	
			}
		});
	}
	
	private void unlockNerd() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedNerd", true);
		editor.commit();
		coins = coins - NERD_UNLOCK_VALUE;
		coinsText.setText("" + coins);
		saveCoins(coins);
	}
	
	private void unlockNinja() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedNinja", true);
		editor.commit();
		coins = coins - NINJA_UNLOCK_VALUE;
		coinsText.setText("" + coins);
		saveCoins(coins);
	}
	
	private void unlockRobot() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedRobot", true);
		editor.commit();
		coins = coins - ROBOT_UNLOCK_VALUE;
		coinsText.setText("" + coins);
		saveCoins(coins);
	}
	
	private void unlockBrick() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedBrick", true);
		editor.commit();
		coins = coins - BRICK_UNLOCK_VALUE;
		coinsText.setText("" + coins);
		saveCoins(coins);
	}
	
	private void unlockWood() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedWood", true);
		editor.commit();
		coins = coins - WOOD_UNLOCK_VALUE;
		coinsText.setText("" + coins);
		saveCoins(coins);
	}
	
	private void unlockSteel() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedSteel", true);
		editor.commit();
		coins = coins - STEEL_UNLOCK_VALUE;
		coinsText.setText("" + coins);
		saveCoins(coins);
	}
	
	private void saveCoins(int coins) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putInt("coins", coins);
		editor.commit();
	}
	
	private void loadUnlockedPlayers() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		unlockedNerd = sharedPreferences.getBoolean("unlockedNerd", false);
		unlockedNinja = sharedPreferences.getBoolean("unlockedNinja", false);
		unlockedRobot = sharedPreferences.getBoolean("unlockedRobot", false);
	}
	
	private void loadUnlockedStages() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		unlockedBrick = sharedPreferences.getBoolean("unlockedBrick", false);
		unlockedWood = sharedPreferences.getBoolean("unlockedWood", false);
		unlockedSteel = sharedPreferences.getBoolean("unlockedSteel", false);
	}

}
