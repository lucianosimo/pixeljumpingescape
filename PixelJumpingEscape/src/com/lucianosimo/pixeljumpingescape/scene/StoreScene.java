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
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.google.android.gms.games.Games;
import com.lucianosimo.pixeljumpingescape.base.BaseScene;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager.SceneType;

public class StoreScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene storeScene;
	
	private IMenuItem storeMenuItem;
	private IMenuItem playAdButton;
	private IMenuItem rateUsButton;
	
	private float screenWidth;
	private float screenHeight;
	
	final Rectangle fade = new Rectangle(360, 640, 720, 1280, vbom);
	
	private boolean unlockedNerd = false;
	private boolean unlockedNinja = false;
	private boolean unlockedRobot = false;
	private boolean unlockedBrick = false;
	private boolean unlockedWood = false;
	private boolean unlockedSteel = false;
	
	private int isRated = 0;
	
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
	
	private int coins;
	
	private TiledSprite[] storeCoins;
	
	private final static int NERD_UNLOCK_VALUE = 25;
	private final static int NINJA_UNLOCK_VALUE = 100;
	private final static int ROBOT_UNLOCK_VALUE = 250;
	private final static int BRICK_UNLOCK_VALUE = 25;
	private final static int WOOD_UNLOCK_VALUE = 100;
	private final static int STEEL_UNLOCK_VALUE = 250;
	
	private final static int RATEUS_REWARD_VALUE = 250;
	//private final static int PLAY_AD_REWARD_VALUE = 500;

	private final int STORE_BACK = 0;
	private final int STORE_PLAY_AD = 1;
	private final int STORE_RATEUS = 2;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		storeScene = new MenuScene(camera);
		Chartboost.cacheRewardedVideo(CBLocation.LOCATION_GAMEOVER);
		loadGameVariables();
		createBackground();
		createStoreChildScene();
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
		
		storeMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(STORE_BACK, resourcesManager.store_back_button_region, vbom), 1, 1);
		playAdButton = new ScaleMenuItemDecorator(new SpriteMenuItem(STORE_PLAY_AD, resourcesManager.store_play_ad_button_region, vbom), 1, 1);
		rateUsButton = new ScaleMenuItemDecorator(new SpriteMenuItem(STORE_RATEUS, resourcesManager.store_rate_us_button_region, vbom), 1, 1);
		
		storeScene.addMenuItem(storeMenuItem);
		storeScene.addMenuItem(playAdButton);
		
		isRated();
		
		if (isRated == 0) {
			storeScene.addMenuItem(rateUsButton);			
		}
		
		storeScene.buildAnimations();
		
		storeCoins = new TiledSprite[5];
        
		storeCoins[0] = new TiledSprite(565, screenHeight - 110, resourcesManager.store_coins_tiled_region.deepCopy(), vbom);
		storeCoins[1] = new TiledSprite(510, screenHeight - 110, resourcesManager.store_coins_tiled_region.deepCopy(), vbom);
		storeCoins[2] = new TiledSprite(455, screenHeight - 110, resourcesManager.store_coins_tiled_region.deepCopy(), vbom);
		storeCoins[3] = new TiledSprite(400, screenHeight - 110, resourcesManager.store_coins_tiled_region.deepCopy(), vbom);
		storeCoins[4] = new TiledSprite(345, screenHeight - 110, resourcesManager.store_coins_tiled_region.deepCopy(), vbom);
		
		for (int i = 0; i < storeCoins.length; i++) {
			storeScene.attachChild(storeCoins[i]);
		}
		
		createStoreCoinsTiledSprites();

		storeMenuItem.setPosition(100, 100);
		playAdButton.setPosition(275, 170);
		rateUsButton.setPosition(440, 170);
		
		storeScene.setOnMenuItemClickListener(this);
		
		storeScene.setBackgroundEnabled(false);

		setChildScene(storeScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case STORE_BACK:
				SceneManager.getInstance().loadMenuScene(engine, this);	
				return true;
			case STORE_RATEUS:
				activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.lucianosimo.pixeljumpingescape")));
				saveRateState();
				addCoins(RATEUS_REWARD_VALUE);
				return true;
			case STORE_PLAY_AD:
				resourcesManager.store_popup_window_sound.play();
				Chartboost.showRewardedVideo(CBLocation.LOCATION_DEFAULT);
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
		verifyAchievements();
	}
	
	private void createStoreCoinsTiledSprites() {
        StoreScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {				
				int coinsIndex;
				
				if (coins == 0) {
					storeCoins[0].setPosition(400, storeCoins[0].getY());
				}
				
				if (coins > 0) {
					coinsIndex = coins % 10;
					storeCoins[0].setCurrentTileIndex(coinsIndex);
					storeCoins[0].setPosition(400, storeCoins[0].getY());
				} else if (coins == 0) {
					storeCoins[0].setCurrentTileIndex(0);
				}
				
				if (coins > 9) {
					storeCoins[1].setVisible(true);
					coinsIndex = (coins/10) % 10;
					storeCoins[1].setCurrentTileIndex(coinsIndex);
					
					storeCoins[0].setPosition(419, storeCoins[0].getY());
					storeCoins[1].setPosition(363, storeCoins[0].getY());
				} else {
					storeCoins[1].setVisible(false);
				}
				
				if (coins > 99) {
					storeCoins[2].setVisible(true);
					coinsIndex = (coins/100) % 10;
					storeCoins[2].setCurrentTileIndex(coinsIndex);
					
					storeCoins[0].setPosition(437, storeCoins[0].getY());
					storeCoins[1].setPosition(386, storeCoins[0].getY());
					storeCoins[2].setPosition(335, storeCoins[0].getY());
				} else {
					storeCoins[2].setVisible(false);
				}
				
				if (coins > 999) {
					storeCoins[3].setVisible(true);
					coinsIndex = (coins/1000) % 10;
					storeCoins[3].setCurrentTileIndex(coinsIndex);
					
					storeCoins[0].setPosition(478, storeCoins[0].getY());
					storeCoins[1].setPosition(427, storeCoins[0].getY());
					storeCoins[2].setPosition(376, storeCoins[0].getY());
					storeCoins[3].setPosition(325, storeCoins[0].getY());
				} else {
					storeCoins[3].setVisible(false);
				}
				
				if (coins > 9999) {
					storeCoins[4].setVisible(true);
					coinsIndex = (coins/10000) % 10;
					storeCoins[4].setCurrentTileIndex(coinsIndex);
					
					storeCoins[0].setPosition(504, storeCoins[0].getY());
					storeCoins[1].setPosition(453, storeCoins[0].getY());
					storeCoins[2].setPosition(402, storeCoins[0].getY());
					storeCoins[3].setPosition(351, storeCoins[0].getY());
					storeCoins[4].setPosition(300, storeCoins[0].getY());
				} else {
					storeCoins[4].setVisible(false);
				}
				
				for (int i = 0; i < storeCoins.length; i++) {
					storeScene.detachChild(storeCoins[i]);
					storeScene.attachChild(storeCoins[i]);
				}
			}
		});		
	}
	
	private void loadCoins() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		coins = sharedPreferences.getInt("coins", 0);
		coins = 10000;
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
						resourcesManager.store_popup_window_sound.play();
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
						resourcesManager.store_popup_window_sound.play();
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
						resourcesManager.store_popup_window_sound.play();
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
						resourcesManager.store_popup_window_sound.play();
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
						resourcesManager.store_popup_window_sound.play();
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
						resourcesManager.store_popup_window_sound.play();
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
				    	resourcesManager.store_unlock_sound.play();
				    	StoreScene.this.activity.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								if (player.equals("nerd")) {
									unlockedNerd = true;
						    		unlockNerd();
						    		storeScene.detachChild(lockedBarsNerd);
						    		storeScene.detachChild(unlockNerdButton);
						    		storeScene.unregisterTouchArea(unlockNerdButton);
						    		storeScene.detachChild(lightNerd);
						    		lightNerd = new Sprite(280, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
						    		storeScene.attachChild(lightNerd);
						    	}
						    	if (player.equals("ninja")) {
						    		unlockedNinja = true;
						    		unlockNinja();
						    		storeScene.detachChild(lockedBarsNinja);
						    		storeScene.detachChild(unlockNinjaButton);
						    		storeScene.unregisterTouchArea(unlockNinjaButton);
						    		storeScene.detachChild(lightNinja);
						    		lightNinja = new Sprite(450, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
						    		storeScene.attachChild(lightNinja);
						    	}
						    	if (player.equals("robot")) {
						    		unlockedRobot = true;
						    		unlockRobot();
						    		storeScene.detachChild(lockedBarsRobot);
						    		storeScene.detachChild(unlockRobotButton);
						    		storeScene.unregisterTouchArea(unlockRobotButton);
						    		storeScene.detachChild(lightRobot);
						    		lightRobot = new Sprite(620, 1075, resourcesManager.store_unlocked_player_light_region, vbom);
						    		storeScene.attachChild(lightRobot);
						    	}
						    	if (player.equals("brick")) {
						    		unlockedBrick = true;
						    		unlockBrick();
						    		storeScene.detachChild(unlockBrickButton);
						    		storeScene.unregisterTouchArea(unlockBrickButton);
						    	}
						    	if (player.equals("wood")) {
						    		unlockedWood = true;
						    		unlockWood();
						    		storeScene.detachChild(unlockWoodButton);
						    		storeScene.unregisterTouchArea(unlockWoodButton);
						    	}
						    	if (player.equals("steel")) {
						    		unlockedSteel = true;
						    		unlockSteel();
						    		storeScene.detachChild(unlockSteelButton);
						    		storeScene.unregisterTouchArea(unlockSteelButton);
						    	}
							}
						});
				        Toast.makeText(activity, player + " unlocked", Toast.LENGTH_LONG).show();
				        loadUnlockedPlayers();
				        loadUnlockedStages();
				        verifyAchievements();
				    }})
				 .setNegativeButton("Mmmm, not really", null).show();	
			}
		});
	}
	
	private void verifyAchievements() {
		Log.d("pixel", "*****************************");
		Log.d("pixel", "nerd: " + unlockedNerd);
		Log.d("pixel", "ninja: " + unlockedNinja);
		Log.d("pixel", "robot: " + unlockedRobot);
		Log.d("pixel", "brick: " + unlockedBrick);
		Log.d("pixel", "wood: " + unlockedWood);
		Log.d("pixel", "steel: " + unlockedSteel);
		//Achievements
		if (activity.getGoogleApiClient() != null && activity.getGoogleApiClient().isConnected()) {
			if (unlockedNerd && unlockedNinja && unlockedRobot) {
				Log.d("pixel", "players achievements 1");
				Games.Achievements.unlock(activity.getGoogleApiClient(), activity.getThanksForLiberatingUsAchievementID());
			}
			if (unlockedBrick && unlockedWood && unlockedSteel) {
				Log.d("pixel", "stage achievements 1");
				Games.Achievements.unlock(activity.getGoogleApiClient(), activity.getTripWhereverYouWantAchievementID());
			}
		} else {
			activity.getGoogleApiClient().connect();
			if (activity.getGoogleApiClient() != null && activity.getGoogleApiClient().isConnected()) {
				if (unlockedNerd && unlockedNinja && unlockedRobot) {
					Log.d("pixel", "players achievements 2");
					Games.Achievements.unlock(activity.getGoogleApiClient(), activity.getThanksForLiberatingUsAchievementID());
				}
				if (unlockedBrick && unlockedWood && unlockedSteel) {
					Log.d("pixel", "stage achievements 2");
					Games.Achievements.unlock(activity.getGoogleApiClient(), activity.getTripWhereverYouWantAchievementID());
				}
			}
		}
	}
	
	private void noEnoughCoins(final String player, final int coins) {
		resourcesManager.store_unlock_sound.play();
		StoreScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(activity, "No enough coins. Collect " + coins + " more to unlock " + player, Toast.LENGTH_SHORT).show();	
			}
		});
	}
	
	private void unlockNerd() {
		unlockedNerd = true;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedNerd", true);
		editor.commit();
		coins = coins - NERD_UNLOCK_VALUE;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
	}
	
	private void unlockNinja() {
		unlockedNinja = true;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedNinja", true);
		editor.commit();
		coins = coins - NINJA_UNLOCK_VALUE;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
	}
	
	private void unlockRobot() {
		unlockedRobot = true;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedRobot", true);
		editor.commit();
		coins = coins - ROBOT_UNLOCK_VALUE;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
	}
	
	private void unlockBrick() {
		unlockedBrick = true;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedBrick", true);
		editor.commit();
		coins = coins - BRICK_UNLOCK_VALUE;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
	}
	
	private void unlockWood() {
		unlockedWood = true;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedWood", true);
		editor.commit();
		coins = coins - WOOD_UNLOCK_VALUE;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
	}
	
	private void unlockSteel() {
		unlockedSteel = true;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("unlockedSteel", true);
		editor.commit();
		coins = coins - STEEL_UNLOCK_VALUE;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
	}
	
	private void saveCoins(int coins) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putInt("coins", coins);
		editor.commit();
	}
	
	private void addCoins(int rewardCoins) {
		coins = coins + rewardCoins;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
	}
	
	public void addRewardedVideoCoins(int rewardCoins) {
		coins = coins + rewardCoins;
		createStoreCoinsTiledSprites();
		saveCoins(coins);
		resourcesManager.store_coins_reward_sound.play();
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
	
	private void saveRateState() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putInt("rated", 1);
		editor.commit();
	}
	
	private void isRated() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		//Rated: 0 = no, 1 = yes, 2 = no and don't want to rate
		isRated = sharedPreferences.getInt("rated", 0);
	}

}
