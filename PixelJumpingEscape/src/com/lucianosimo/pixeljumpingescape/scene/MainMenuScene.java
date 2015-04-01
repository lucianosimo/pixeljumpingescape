package com.lucianosimo.pixeljumpingescape.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.lucianosimo.pixeljumpingescape.base.BaseScene;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene menuChildScene;
	
	private IMenuItem menuPlayItem;
	private IMenuItem menuStoreItem;
	private IMenuItem menuLeaderboardItem;
	private IMenuItem menuAchievementsItem;
	
	private Sprite menuSelectionMenuBackground;
	private ScaleMenuItemDecorator menuSelectionCloseButton;
	private ScaleMenuItemDecorator menuSelectionOpenButton;
	private ScaleMenuItemDecorator menuLeftPlayerButton;
	private ScaleMenuItemDecorator menuRightPlayerButton;
	private ScaleMenuItemDecorator menuLeftStageButton;
	private ScaleMenuItemDecorator menuRightStageButton;
	private Sprite menuSelectionPlayerLabel;
	private Sprite menuSelectionStageLabel;
	private ArrayList<Sprite> playersToSelect;
	private ArrayList<Sprite> stagesToSelect;
	private String[] playersMap;
	private String[] stagesMap;
	
	private float screenWidth;
	private float screenHeight;
	
	private Sprite menuSoundEnabledButton;
	private Sprite menuSoundDisabledButton;
	private Sprite menuExitButton;
	
	private int coins;
	
	private boolean unlockedNerd = false;
	private boolean unlockedNinja = false;
	private boolean unlockedRobot = false;
	private boolean unlockedBrick = false;
	private boolean unlockedWood = false;
	private boolean unlockedSteel = false;
	
	private boolean availableOpenSelectionMenu = true;
	private boolean availableCloseSelectionMenu = false;
	private boolean availableDisplayExitWindow = true;
	
	private final static int WALL_HEIGHT = 128;
	
	private static final IEaseFunction[][] EASEFUNCTIONS = new IEaseFunction[][] {
		new IEaseFunction[] { 
				//EaseQuadIn.getInstance()},
				EaseStrongIn.getInstance()},
	};
	
	private final static int RATEUS_REWARD_VALUE = 250;
	
	private final int MENU_PLAY = 0;
	private final int MENU_STORE = 1;
	private final int MENU_LEADERBOARD = 2;
	private final int MENU_ACHIEVEMENTS = 3;
	private final int MENU_OPEN_SELECTION = 4;
	private final int MENU_CLOSE_SELECTION = 5;
	private final int MENU_LEFT_PLAYER = 6;
	private final int MENU_RIGHT_PLAYER = 7;
	private final int MENU_LEFT_STAGE = 8;
	private final int MENU_RIGHT_STAGE = 9;
	
	private final float SELECTION_BACKGROUND_MOVE_DURATION = 1.25f;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		menuChildScene = new MenuScene(camera);
		loadCoins();
		evaluateRateUs();
		createBackground();
		createMenuChildScene();
		loadGameVariables();
	}

	@Override
	public void onBackKeyPressed() {
		if (availableDisplayExitWindow) {
			displayQuitGameWindow();
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
		createSoundButtons();
		
		menuExitButton = new Sprite(0, 0, resourcesManager.menu_exit_button_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (availableDisplayExitWindow) {
					displayQuitGameWindow();
				}
				return true;
			}
		};
		
		menuChildScene.setPosition(screenWidth/2, screenHeight/2);
		menuSelectionMenuBackground = new Sprite(0, -screenHeight/2 - 150, resourcesManager.menu_selection_menu_background_region, vbom);
		
		menuPlayItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.menu_play_button_region, vbom), 1.2f, 1);
		menuStoreItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_STORE, resourcesManager.menu_store_button_region, vbom), 1.2f, 1);
		menuLeaderboardItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEADERBOARD, resourcesManager.menu_leaderboard_button_region, vbom), 1.2f, 1);
		menuAchievementsItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_ACHIEVEMENTS, resourcesManager.menu_achievements_button_region, vbom), 1.2f, 1);
		menuSelectionOpenButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPEN_SELECTION, resourcesManager.menu_selection_open_button_region, vbom), 1.2f, 1);
		menuSelectionCloseButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_CLOSE_SELECTION, resourcesManager.menu_selection_close_button_region, vbom), 1.2f, 1);
		menuLeftPlayerButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEFT_PLAYER, resourcesManager.menu_selection_left_player_button_region, vbom), 1.2f, 1);
		menuRightPlayerButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RIGHT_PLAYER, resourcesManager.menu_selection_right_player_button_region, vbom), 1.2f, 1);
		menuLeftStageButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEFT_STAGE, resourcesManager.menu_selection_left_stage_button_region, vbom), 1.2f, 1);
		menuRightStageButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RIGHT_STAGE, resourcesManager.menu_selection_right_stage_button_region, vbom), 1.2f, 1);
		
		menuSelectionPlayerLabel = new Sprite(0, 0, resourcesManager.menu_selection_player_label_region, vbom);
		menuSelectionStageLabel = new Sprite(0, 0, resourcesManager.menu_selection_stage_label_region, vbom);
		
		menuChildScene.attachChild(menuSelectionMenuBackground);
		
		menuChildScene.attachChild(menuExitButton);
		menuChildScene.registerTouchArea(menuExitButton);
		
		menuChildScene.addMenuItem(menuPlayItem);
		menuChildScene.addMenuItem(menuStoreItem);
		menuChildScene.addMenuItem(menuLeaderboardItem);
		menuChildScene.addMenuItem(menuAchievementsItem);
		menuChildScene.addMenuItem(menuSelectionOpenButton);
		menuChildScene.addMenuItem(menuLeftPlayerButton);
		menuChildScene.addMenuItem(menuRightPlayerButton);
		menuChildScene.addMenuItem(menuLeftStageButton);
		menuChildScene.addMenuItem(menuRightStageButton);
		
		menuSelectionMenuBackground.attachChild(menuSelectionPlayerLabel);
		menuSelectionMenuBackground.attachChild(menuSelectionStageLabel);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		setMainMenuButtonsPositions();
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
	
	private void createSoundButtons() {
		menuSoundDisabledButton = new Sprite(51, 51, resourcesManager.menu_sound_disabled_button_region, vbom);
		
		//If soundEnabled = 0, enabled..if 1 disabled
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
		if (soundEnabled == 1) {
			activity.enableSound(false);
			menuSoundDisabledButton.setPosition(55f, 52.5f);
		} else if (soundEnabled == 0) {
			activity.enableSound(true);
			menuSoundDisabledButton.setPosition(1500, 1500);
		}
		
		menuSoundEnabledButton = new Sprite(0, 0, resourcesManager.menu_sound_enabled_button_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					//If soundEnabled = 0, enabled..if 1 disabled
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
					int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
					Editor editor = sharedPreferences.edit();
					if (soundEnabled == 1) {
						soundEnabled = 0;
						menuSoundDisabledButton.setPosition(1500, 1500);
						activity.enableSound(true);
						activity.enableMusic(true);
					} else if (soundEnabled == 0) {
						soundEnabled = 1;
						menuSoundDisabledButton.setPosition(55f, 52.5f);
						activity.enableSound(false);
						activity.enableMusic(false);
					}
					editor.putInt("soundEnabled", soundEnabled);
					editor.commit();	
				}
				return true;
			}
		};
		
		menuChildScene.attachChild(menuSoundEnabledButton);
		menuSoundEnabledButton.attachChild(menuSoundDisabledButton);
		
		menuChildScene.registerTouchArea(menuSoundEnabledButton);
	}
	
	private void openSelectionMenu() {
		if (availableOpenSelectionMenu) {
			resourcesManager.menu_selection_background_move_sound.play();
			availableOpenSelectionMenu = false;
			availableCloseSelectionMenu = true;
			MainMenuScene.this.activity.runOnUpdateThread(new Runnable() {
				
				@Override
				public void run() {
					final IEaseFunction[] easeFunction = EASEFUNCTIONS[0];
					menuSelectionMenuBackground.clearEntityModifiers();
					menuSelectionMenuBackground.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionMenuBackground.getX(), 
							menuSelectionMenuBackground.getY(), menuSelectionMenuBackground.getX(), menuSelectionMenuBackground.getY() + 375, easeFunction[0]));
					
					menuSelectionOpenButton.clearEntityModifiers();
					menuSelectionOpenButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionOpenButton.getX(), 
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
					menuPlayItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuPlayItem.getX(), 
							menuPlayItem.getY(), menuPlayItem.getX(), menuPlayItem.getY() + 100, easeFunction[0]));
					
					menuStoreItem.clearEntityModifiers();
					menuStoreItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuStoreItem.getX(), 
							menuStoreItem.getY(), menuStoreItem.getX(), menuStoreItem.getY() + 100, easeFunction[0]));
					
					menuLeaderboardItem.clearEntityModifiers();
					menuLeaderboardItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuLeaderboardItem.getX(), 
							menuLeaderboardItem.getY(), menuLeaderboardItem.getX(), menuLeaderboardItem.getY() + 100, easeFunction[0]));
					
					menuAchievementsItem.clearEntityModifiers();
					menuAchievementsItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuAchievementsItem.getX(), 
							menuAchievementsItem.getY(), menuAchievementsItem.getX(), menuAchievementsItem.getY() + 100, easeFunction[0]));
					
					menuLeftPlayerButton.clearEntityModifiers();
					menuLeftPlayerButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuLeftPlayerButton.getX(), 
							menuLeftPlayerButton.getY(), menuLeftPlayerButton.getX(), menuLeftPlayerButton.getY() + 375, easeFunction[0]));
					
					menuRightPlayerButton.clearEntityModifiers();
					menuRightPlayerButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuRightPlayerButton.getX(), 
							menuRightPlayerButton.getY(), menuRightPlayerButton.getX(), menuRightPlayerButton.getY() + 375, easeFunction[0]));
					
					menuLeftStageButton.clearEntityModifiers();
					menuLeftStageButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuLeftStageButton.getX(), 
							menuLeftStageButton.getY(), menuLeftStageButton.getX(), menuLeftStageButton.getY() + 375, easeFunction[0]));
					
					menuRightStageButton.clearEntityModifiers();
					menuRightStageButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuRightStageButton.getX(), 
							menuRightStageButton.getY(), menuRightStageButton.getX(), menuRightStageButton.getY() + 375, easeFunction[0]));
					
					menuSelectionPlayerLabel.clearEntityModifiers();
					menuSelectionPlayerLabel.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionPlayerLabel.getX(), 
							menuSelectionPlayerLabel.getY(), menuSelectionPlayerLabel.getX(), menuSelectionPlayerLabel.getY() + 375, easeFunction[0]));
					
					menuSelectionStageLabel.clearEntityModifiers();
					menuSelectionStageLabel.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionStageLabel.getX(), 
							menuSelectionStageLabel.getY(), menuSelectionStageLabel.getX(), menuSelectionStageLabel.getY() + 375, easeFunction[0]));
					
				}
			});
		}
	}
	
	private void closeSelectionMenu() {
		if (availableCloseSelectionMenu) {
			resourcesManager.menu_selection_background_move_sound.play();
			availableCloseSelectionMenu = false;
			availableOpenSelectionMenu = true;
			MainMenuScene.this.activity.runOnUpdateThread(new Runnable() {
				
				@Override
				public void run() {				
					final IEaseFunction[] easeFunction = EASEFUNCTIONS[0];
					menuSelectionMenuBackground.clearEntityModifiers();
					menuSelectionMenuBackground.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionMenuBackground.getX(), 
							menuSelectionMenuBackground.getY(), menuSelectionMenuBackground.getX(), menuSelectionMenuBackground.getY() - 375, easeFunction[0]));
					
					menuSelectionCloseButton.clearEntityModifiers();
					menuSelectionCloseButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionCloseButton.getX(), 
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
					menuPlayItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuPlayItem.getX(), 
							menuPlayItem.getY(), menuPlayItem.getX(), menuPlayItem.getY() - 100, easeFunction[0]));
					
					menuStoreItem.clearEntityModifiers();
					menuStoreItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuStoreItem.getX(), 
							menuStoreItem.getY(), menuStoreItem.getX(), menuStoreItem.getY() - 100, easeFunction[0]));
					
					menuLeaderboardItem.clearEntityModifiers();
					menuLeaderboardItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuLeaderboardItem.getX(), 
							menuLeaderboardItem.getY(), menuLeaderboardItem.getX(), menuLeaderboardItem.getY() - 100, easeFunction[0]));
					
					menuAchievementsItem.clearEntityModifiers();
					menuAchievementsItem.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuAchievementsItem.getX(), 
							menuAchievementsItem.getY(), menuAchievementsItem.getX(), menuAchievementsItem.getY() - 100, easeFunction[0]));
					
					menuLeftPlayerButton.clearEntityModifiers();
					menuLeftPlayerButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuLeftPlayerButton.getX(), 
							menuLeftPlayerButton.getY(), menuLeftPlayerButton.getX(), menuLeftPlayerButton.getY() - 375, easeFunction[0]));
					
					menuRightPlayerButton.clearEntityModifiers();
					menuRightPlayerButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuRightPlayerButton.getX(), 
							menuRightPlayerButton.getY(), menuRightPlayerButton.getX(), menuRightPlayerButton.getY() - 375, easeFunction[0]));
					
					menuLeftStageButton.clearEntityModifiers();
					menuLeftStageButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuLeftStageButton.getX(), 
							menuLeftStageButton.getY(), menuLeftStageButton.getX(), menuLeftStageButton.getY() - 375, easeFunction[0]));
					
					menuRightStageButton.clearEntityModifiers();
					menuRightStageButton.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuRightStageButton.getX(), 
							menuRightStageButton.getY(), menuRightStageButton.getX(), menuRightStageButton.getY() - 375, easeFunction[0]));
					
					menuSelectionPlayerLabel.clearEntityModifiers();
					menuSelectionPlayerLabel.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionPlayerLabel.getX(), 
							menuSelectionPlayerLabel.getY(), menuSelectionPlayerLabel.getX(), menuSelectionPlayerLabel.getY() - 375, easeFunction[0]));
					
					menuSelectionStageLabel.clearEntityModifiers();
					menuSelectionStageLabel.registerEntityModifier(new MoveModifier(SELECTION_BACKGROUND_MOVE_DURATION, menuSelectionStageLabel.getX(), 
							menuSelectionStageLabel.getY(), menuSelectionStageLabel.getX(), menuSelectionStageLabel.getY() - 375, easeFunction[0]));
					
				}
			});
		}
	}
	
	private void setMainMenuButtonsPositions() {
		menuPlayItem.setPosition(0, 75);
		menuStoreItem.setPosition(200, -125);
		menuLeaderboardItem.setPosition(0, -125);
		menuAchievementsItem.setPosition(-200, -125);
		menuSoundEnabledButton.setPosition(-315, screenHeight / 2 - 60);
		menuExitButton.setPosition(315, screenHeight / 2 - 60);
		menuSelectionOpenButton.setPosition(-195, -screenHeight / 2 + 35);
		menuLeftPlayerButton.setPosition(-300, -screenHeight / 2 - 250);
		menuRightPlayerButton.setPosition(-75, -screenHeight / 2 - 250);
		menuLeftStageButton.setPosition(70, -screenHeight / 2 - 250);
		menuRightStageButton.setPosition(300, -screenHeight / 2 - 250);
		menuSelectionPlayerLabel.setPosition(175, -90);
		menuSelectionStageLabel.setPosition(screenWidth - 175, -90);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_PLAY:
				resourcesManager.menu_button_sound.play();
				SceneManager.getInstance().loadGameScene(engine, this);
				return true;
			case MENU_LEADERBOARD:
				resourcesManager.menu_button_sound.play();
				if (activity.getGoogleApiClient() != null && activity.getGoogleApiClient().isConnected()) {
					activity.displayLeaderboard();
				} else {
					activity.getGoogleApiClient().connect();
				}				
				return true;
			case MENU_ACHIEVEMENTS:
				resourcesManager.menu_button_sound.play();
				if (activity.getGoogleApiClient() != null && activity.getGoogleApiClient().isConnected()) {
					activity.displayAchievements();
				} else {
					activity.getGoogleApiClient().connect();
				}				
				return true;
			case MENU_STORE:
				resourcesManager.menu_button_sound.play();
				SceneManager.getInstance().loadStoreScene(engine, this);
				return true;
			case MENU_CLOSE_SELECTION:
				closeSelectionMenu();	
				return true;
			case MENU_OPEN_SELECTION:
				openSelectionMenu();	
				return true;
			case MENU_LEFT_PLAYER:
				resourcesManager.menu_button_sound.play();
				boolean changedLeft = false;
				for (int i = 0; i < playersToSelect.size(); i++) {
					if (playersToSelect.get(i).isVisible() && !changedLeft) {
						changedLeft = true;
						playersToSelect.get(i).setVisible(false);
						if (i > 0) {
							playersToSelect.get(i - 1).setVisible(true);
							selectPlayer(playersMap[i - 1]);
						} else {
							int index = playersToSelect.size() - 1;
							playersToSelect.get(index).setVisible(true);
							selectPlayer(playersMap[index]);
						}
					}
				}
				return true;
			case MENU_RIGHT_PLAYER:
				resourcesManager.menu_button_sound.play();
				boolean changedRight = false;
				for (int i = 0; i < playersToSelect.size(); i++) {
					if (playersToSelect.get(i).isVisible() && !changedRight) {
						changedRight = true;
						playersToSelect.get(i).setVisible(false);
						if (i < playersToSelect.size() - 1) {
							playersToSelect.get(i + 1).setVisible(true);
							selectPlayer(playersMap[i + 1]);
						} else {
							playersToSelect.get(0).setVisible(true);
							selectPlayer(playersMap[0]);
						}
					}
				}
				return true;
			case MENU_LEFT_STAGE:
				resourcesManager.menu_button_sound.play();
				boolean changedLeftStage = false;
				for (int i = 0; i < stagesToSelect.size(); i++) {
					if (stagesToSelect.get(i).isVisible() && !changedLeftStage) {
						changedLeftStage = true;
						stagesToSelect.get(i).setVisible(false);
						if (i > 0) {
							stagesToSelect.get(i - 1).setVisible(true);
							selectStage(stagesMap[i - 1]);
						} else {
							int index = stagesToSelect.size() - 1;
							stagesToSelect.get(index).setVisible(true);
							selectStage(stagesMap[index]);
						}
					}
				}
				return true;
			case MENU_RIGHT_STAGE:
				resourcesManager.menu_button_sound.play();
				boolean changedRightStage = false;
				for (int i = 0; i < stagesToSelect.size(); i++) {
					if (stagesToSelect.get(i).isVisible() && !changedRightStage) {
						changedRightStage = true;
						stagesToSelect.get(i).setVisible(false);
						if (i < stagesToSelect.size() - 1) {
							stagesToSelect.get(i + 1).setVisible(true);
							selectStage(stagesMap[i + 1]);
						} else {
							stagesToSelect.get(0).setVisible(true);
							selectStage(stagesMap[0]);
						}
					}
				}
				return true;
			default:
				return false;
		}
	}

	@Override
	public void handleOnPause() {
		
	}
	
	private void loadGameVariables() {
		loadUnlockedPlayers();
		loadUnlockedStages();
		loadSelectedPlayer();
		loadPlayersStagesOnSelectionMenu();
	}
	
	private void loadPlayersStagesOnSelectionMenu() {
		int playerIndex = 0;
		int stageIndex = 0;
		playersToSelect = new ArrayList<>();
		stagesToSelect = new ArrayList<>();
		playersMap = new String[4];
		stagesMap = new String[4];
		
		playersMap[playerIndex] = "beard";
		playersToSelect.add(playerIndex++, new Sprite(175, 125, resourcesManager.menu_selection_beard_player_region, vbom));		
		if (unlockedNerd) {
			playersMap[playerIndex] = "nerd";
			playersToSelect.add(playerIndex++, new Sprite(175, 125, resourcesManager.menu_selection_nerd_player_region, vbom));
		}
		if (unlockedNinja) {
			playersMap[playerIndex] = "ninja";
			playersToSelect.add(playerIndex++, new Sprite(175, 125, resourcesManager.menu_selection_ninja_player_region, vbom));
		}
		if (unlockedRobot) {
			playersMap[playerIndex] = "robot";
			playersToSelect.add(playerIndex++, new Sprite(175, 125, resourcesManager.menu_selection_robot_player_region, vbom));
		}
		
		stagesMap[stageIndex] = "castle";
		stagesToSelect.add(stageIndex++, new Sprite(screenWidth - 175, 125, resourcesManager.menu_selection_castle_stage_region, vbom));
		if (unlockedBrick) {
			stagesMap[stageIndex] = "brick";
			stagesToSelect.add(stageIndex++, new Sprite(screenWidth - 175, 125, resourcesManager.menu_selection_brick_stage_region, vbom));
		}
		if (unlockedWood) {
			stagesMap[stageIndex] = "wood";
			stagesToSelect.add(stageIndex++, new Sprite(screenWidth - 175, 125, resourcesManager.menu_selection_wood_stage_region, vbom));
		}
		if (unlockedSteel) {
			stagesMap[stageIndex] = "steel";
			stagesToSelect.add(stageIndex++, new Sprite(screenWidth - 175, 125, resourcesManager.menu_selection_steel_stage_region, vbom));
		}
		
		for (int i = 0; i < playersToSelect.size(); i++) {
			menuSelectionMenuBackground.attachChild(playersToSelect.get(i));
			if (playersMap[i].equals(loadSelectedPlayer())) {
				playersToSelect.get(i).setVisible(true);
			} else {
				playersToSelect.get(i).setVisible(false);
			}
		}
		
		for (int i = 0; i < stagesToSelect.size(); i++) {
			menuSelectionMenuBackground.attachChild(stagesToSelect.get(i));
			if (stagesMap[i].equals(loadSelectedStage())) {
				stagesToSelect.get(i).setVisible(true);
			} else {
				stagesToSelect.get(i).setVisible(false);
			}
		}
	}
	
	//0 = beard, 1 = nerd, 2 = ninja, 3 = robot
	private void selectPlayer(String player) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putString("selectedPlayer", player);
		editor.commit();
	}
	
	//0 = beard, 1 = nerd, 2 = ninja, 3 = robot
	private void selectStage(String stage) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putString("selectedStage", stage);
		editor.commit();
	}
	
	private String loadSelectedPlayer() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String player = sharedPreferences.getString("selectedPlayer", "beard");
		return player;
	}
	
	private String loadSelectedStage() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String stage = sharedPreferences.getString("selectedStage", "castle");
		return stage;
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
	
	private void evaluateRateUs() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int played = sharedPreferences.getInt("played", 0);
		//Rated: 0 = no, 1 = yes, 2 = no and don't want to rate
		int rated = sharedPreferences.getInt("rated", 0);		
		if (rated == 0) {
			if (played == 5 || played == 15 || played == 30) {
				displayRateUsWindow();
			}
		}
	}
	
	private void displayRateUsWindow() {
		resourcesManager.menu_popup_window_sound.play();
		MainMenuScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(MainMenuScene.this.activity)
				.setMessage("Do you want to rate us. Obtain " + RATEUS_REWARD_VALUE + " coins")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int whichButton) {
				    	activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.lucianosimo.parachuteaction")));
				    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int rated = sharedPreferences.getInt("rated", 0);
						Editor editor = sharedPreferences.edit();
						rated = 1;
						editor.putInt("rated", rated);
						addCoins(RATEUS_REWARD_VALUE);
						editor.commit();
				    }})
				.setNegativeButton("Never", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int rated = sharedPreferences.getInt("rated", 0);
						Editor editor = sharedPreferences.edit();
						rated = 2;
						editor.putInt("rated", rated);
						editor.commit();						
					}
				})
				.setNeutralButton("Maybe later", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int played = sharedPreferences.getInt("played", 0);
						Editor editor = sharedPreferences.edit();
						played++;
						editor.putInt("played", played);
						editor.commit();						
					}
				})
				.show();
			}
		});
	}
	
	private void displayQuitGameWindow() {
		availableDisplayExitWindow = false;
		resourcesManager.menu_popup_window_sound.play();
		MainMenuScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(MainMenuScene.this.activity)
				.setMessage("Do you want to quit?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int whichButton) {
				    	System.exit(0);	
				    }})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						availableDisplayExitWindow = true;
					}
				})
				.setCancelable(false)
				.show();
			}
		});
	}
	
	private void loadCoins() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		coins = sharedPreferences.getInt("coins", 0);
	}
	
	private void saveCoins(int coins) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		editor.putInt("coins", coins);
		editor.commit();
	}
	
	private void addCoins(int rewardCoins) {
		coins = coins + rewardCoins;
		saveCoins(coins);
	}

}
