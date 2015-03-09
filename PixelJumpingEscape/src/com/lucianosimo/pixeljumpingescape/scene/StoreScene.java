package com.lucianosimo.pixeljumpingescape.scene;

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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.lucianosimo.pixeljumpingescape.base.BaseScene;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager.SceneType;

public class StoreScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene storeScene;
	
	private IMenuItem storeMenuItem;
	
	private float screenWidth;
	private float screenHeight;
	
	final Rectangle fade = new Rectangle(360, 640, 720, 1280, vbom);
	
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
	
	private final static int NERD_UNLOCK_VALUE = 25;
	private final static int NINJA_UNLOCK_VALUE = 100;
	private final static int ROBOT_UNLOCK_VALUE = 250;
	private final static int BRICK_UNLOCK_VALUE = 25;
	private final static int WOOD_UNLOCK_VALUE = 100;
	private final static int STEEL_UNLOCK_VALUE = 250;

	private final int STORE_BACK = 0;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		storeScene = new MenuScene(camera);
		createBackground();
		createStoreChildScene();
		loadGameVariables();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine, this);	
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_STORE;
	}

	@Override
	public void disposeScene() {
	}
	
	public void createBackground() {
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 0);
		ParallaxEntity backgroundParallaxEntity = new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.store_background_region, vbom));
		background.attachParallaxEntity(backgroundParallaxEntity);
		this.setBackground(background);
	}

	private void createStoreChildScene() {
		storeMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(STORE_BACK, resourcesManager.store_back_button_region, vbom), 1.2f, 1);
		storeScene.addMenuItem(storeMenuItem);
		storeScene.buildAnimations();
		
		coinsText = new Text(screenWidth / 2 + 40, screenHeight - 110 , resourcesManager.store_coins_font, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		playAdButton = new Sprite(362, 200, resourcesManager.store_play_ad_button_region, vbom);
		
		storeMenuItem.setPosition(100, 100);
		
		storeScene.setOnMenuItemClickListener(this);
		
		storeScene.setBackgroundEnabled(false);
		
		storeScene.attachChild(coinsText);
		storeScene.attachChild(playAdButton);
		
		setChildScene(storeScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case STORE_BACK:
				SceneManager.getInstance().loadMenuScene(engine, this);	
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
		coins = 10000;
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
		StoreScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(StoreScene.this.activity)
				.setMessage("Do you want to unlock " + player + " for " + coins + " coins")
				.setPositiveButton("Of course!!!", new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int whichButton) {
				    	StoreScene.this.activity.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
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
							}
						});
				        Toast.makeText(activity, player + " unlocked", Toast.LENGTH_LONG).show();
				        loadUnlockedPlayers();
				    }})
				 .setNegativeButton("Mmmm, not really", null).show();	
			}
		});
	}
	
	private void noEnoughCoins(final String player, final int coins) {
		StoreScene.this.activity.runOnUiThread(new Runnable() {
			
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
