package com.lucianosimo.pixeljumpingescape.manager;

import java.io.IOException;
import java.util.Random;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lucianosimo.pixeljumpingescape.GameActivity;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public SmoothCamera camera;
	public GameActivity activity;
	public VertexBufferObjectManager vbom;
	
	//Splash items
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	//Menu audio
	public Sound menu_button_sound;
	public Sound menu_popup_window_sound;
	public Sound menu_selection_background_move_sound;
	
	//Menu items
	public ITextureRegion loading_background_region;
	
	public ITextureRegion menu_selection_menu_background_region;
	public ITextureRegion menu_selection_close_button_region;
	public ITextureRegion menu_selection_open_button_region;
	public ITextureRegion menu_selection_left_player_button_region;
	public ITextureRegion menu_selection_right_player_button_region;
	public ITextureRegion menu_selection_left_stage_button_region;
	public ITextureRegion menu_selection_right_stage_button_region;
	public ITextureRegion menu_selection_beard_player_region;
	public ITextureRegion menu_selection_nerd_player_region;
	public ITextureRegion menu_selection_ninja_player_region;
	public ITextureRegion menu_selection_robot_player_region;
	public ITextureRegion menu_selection_castle_stage_region;
	public ITextureRegion menu_selection_brick_stage_region;
	public ITextureRegion menu_selection_wood_stage_region;
	public ITextureRegion menu_selection_steel_stage_region;
	public ITextureRegion menu_selection_player_label_region;
	public ITextureRegion menu_selection_stage_label_region;
	
	public ITextureRegion menu_background_region;
	public ITextureRegion menu_title_region;
	public ITextureRegion menu_wall_region;
	public ITextureRegion menu_left_spikes_region;
	public ITextureRegion menu_right_spikes_region;
	public ITextureRegion menu_play_button_region;
	public ITextureRegion menu_store_button_region;
	public ITextureRegion menu_leaderboard_button_region;

	private BuildableBitmapTextureAtlas loadingBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas menuSelectionTextureAtlas;
	private BuildableBitmapTextureAtlas menuBackgroundTextureAtlas;
	
	//Store fonts
	
	//Store audio
	public Sound store_coins_reward_sound;
	public Sound store_popup_window_sound;
	public Sound store_unlock_sound;
	public Sound store_error_sound;
		
	//Store items
	public ITextureRegion store_background_region;
	public ITextureRegion store_back_button_region;
	public ITextureRegion store_unlock_player_button_region;
	public ITextureRegion store_locked_player_bars_region;
	public ITextureRegion store_locked_player_light_region;
	public ITextureRegion store_unlocked_player_light_region;
	public ITextureRegion store_unlock_stage_button_region;
	public ITextureRegion store_play_ad_button_region;
	public ITextureRegion store_rate_us_button_region;
	
	//Score tiles
	public ITiledTextureRegion store_coins_tiled_region;
	
	private BuildableBitmapTextureAtlas storeBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas storeCoinsTextureAtlas;
	private BuildableBitmapTextureAtlas storeTextureAtlas;
	
	//Game audio
	
	//Game fonts
	
	//Game HUD
	public ITiledTextureRegion game_fire_region;
	public ITextureRegion game_score_sign_region;
	public ITextureRegion game_blood_region;
	public ITextureRegion game_tap_text_region;
	public ITextureRegion game_tap_block_region;
	
	//Objects
	public ITextureRegion game_wall_region;
	public ITextureRegion game_left_spikes_region;
	public ITextureRegion game_right_spikes_region;
	public ITextureRegion game_left_moving_spikes_region;
	public ITextureRegion game_right_moving_spikes_region;
	public ITextureRegion game_center_spikes_region;
	public ITextureRegion game_spider_web_region;
	
	//Platforms
	public ITextureRegion game_floor_region;
	
	//Backgrounds
	public ITextureRegion game_background_region;
	
	//Score tiles
	public ITiledTextureRegion game_score_tiled_region;
	
	//Animated
	public ITiledTextureRegion game_player_region;
	public ITiledTextureRegion game_coin_region;
	public ITiledTextureRegion game_spider_region;
	public ITiledTextureRegion game_spider_2_region;
	
	//Windows
	public ITextureRegion game_over_window_region;
	public ITextureRegion game_pause_window_region;
	public ITextureRegion game_resume_button_region;
	public ITextureRegion game_retry_button_region;
	public ITextureRegion game_quit_button_region;
	public ITextureRegion game_twitter_button_region;
	public ITextureRegion game_gpg_button_region;
	public ITextureRegion game_new_record_region;

	//Buttons
	
	//Score tiles;
	
	//Game Textures
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private BuildableBitmapTextureAtlas gameBloodTextureAtlas;
	private BuildableBitmapTextureAtlas gameHudTextureAtlas;
	private BuildableBitmapTextureAtlas gameWindowsTextureAtlas;
	private BuildableBitmapTextureAtlas gameAnimatedTextureAtlas;
	private BuildableBitmapTextureAtlas gameScoreTextureAtlas;
	private BuildableBitmapTextureAtlas gameFireTextureAtlas;
	private BuildableBitmapTextureAtlas gameMovingSpikeTextureAtlas;
	private BuildableBitmapTextureAtlas gameBackgroundTextureAtlas;
	
	//Splash Methods
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	//Menu methods
	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuFonts();
		loadMenuAudio();		
	}
	
	public void unloadMenuResources() {
		unloadMenuTextures();
		unloadMenuFonts();
		unloadMenuAudio();
	}
	
	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		Random rand = new Random();
		
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 1280, TextureOptions.BILINEAR);
		menuSelectionTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 1280, TextureOptions.BILINEAR);
		menuBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 1280, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		loadingBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		
		loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingBackgroundTextureAtlas, activity, "loading_background.png");
		menu_play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_play_button.png");
		menu_store_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_store_button.png");
		menu_leaderboard_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_leaderboard_button.png");
		menu_title_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_title.png");
		
		menu_selection_close_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_close_button.png");
		menu_selection_open_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_open_button.png");
		menu_selection_left_player_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_left_player_button.png");
		menu_selection_right_player_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_right_player_button.png");
		menu_selection_left_stage_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_left_stage_button.png");
		menu_selection_right_stage_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_right_stage_button.png");
		menu_selection_beard_player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_beard_player.png");
		menu_selection_nerd_player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_nerd_player.png");
		menu_selection_ninja_player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_ninja_player.png");
		menu_selection_robot_player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_robot_player.png");
		menu_selection_castle_stage_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_castle_stage.png");
		menu_selection_brick_stage_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_brick_stage.png");
		menu_selection_wood_stage_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_wood_stage.png");
		menu_selection_steel_stage_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_steel_stage.png");
		menu_selection_player_label_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_player_label.png");
		menu_selection_stage_label_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_stage_label.png");

		int background = rand.nextInt(4) + 1;
		//Castle
		if (background == 1) {
			menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background_castle.png");
			menu_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_wall_castle.png");
			menu_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_left_spikes_castle.png");
			menu_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_right_spikes_castle.png");
			menu_selection_menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_menu_castle_background.png");
		//Red bricks
		} else if (background == 2) {
			menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background_brick.png");
			menu_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_wall_brick.png");
			menu_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_left_spikes_brick.png");
			menu_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_right_spikes_brick.png");
			menu_selection_menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_menu_brick_background.png");
		//Steel
		} else if (background == 3) {
			menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background_steel.png");
			menu_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_wall_steel.png");
			menu_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_left_spikes_steel.png");
			menu_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_right_spikes_steel.png");
			menu_selection_menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_menu_steel_background.png");
		//Wood
		} else if (background == 4) {
			menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background_wood.png");
			menu_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_wall_wood.png");
			menu_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_left_spikes_wood.png");
			menu_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_right_spikes_wood.png");
			menu_selection_menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuSelectionTextureAtlas, activity, "menu_selection_menu_wood_background.png");
		}
		
		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuSelectionTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.loadingBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
			this.menuSelectionTextureAtlas.load();
			this.loadingBackgroundTextureAtlas.load();
			this.menuBackgroundTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			org.andengine.util.debug.Debug.e(e);
		}
	}
	
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("fonts/menu/");
	}
	
	private void loadMenuAudio() {
		SoundFactory.setAssetBasePath("sound/menu/");
		try {
			menu_button_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "menu_button_sound.mp3");
			menu_popup_window_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "menu_popup_window_sound.mp3");
			menu_selection_background_move_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "menu_selection_background_move_sound.mp3");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void unloadMenuTextures() {
		this.menuTextureAtlas.unload();
		this.menuSelectionTextureAtlas.unload();
		this.menuBackgroundTextureAtlas.unload();
	}
	
	private void unloadMenuFonts() {	
	}
	
	private void unloadMenuAudio() {
		menu_button_sound.stop();
		menu_popup_window_sound.stop();
		menu_selection_background_move_sound.stop();
		activity.getSoundManager().remove(menu_button_sound);
		activity.getSoundManager().remove(menu_popup_window_sound);
		activity.getSoundManager().remove(menu_selection_background_move_sound);
		System.gc();
	}
	
	//Store Methods
	public void loadStoreResources() {
		loadStoreGraphics();
		loadStoreFonts();
		loadStoreAudio();		
	}
	
	public void unloadStoreResources() {
		unloadStoreTextures();
		unloadStoreFonts();
		unloadStoreAudio();
	}
	
	private void loadStoreGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/store/");
		
		storeTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		storeBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		storeCoinsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 550, 55, TextureOptions.NEAREST);

		store_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeBackgroundTextureAtlas, activity, "store_background.png");
		store_back_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_back_button.png");
		store_unlock_player_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_unlock_player_button.png");
		store_locked_player_bars_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_locked_player_bars.png");
		store_locked_player_light_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_locked_player_light.png");
		store_unlocked_player_light_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_unlocked_player_light.png");
		store_unlock_stage_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_unlock_stage_button.png");
		store_play_ad_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_play_ad_button.png");
		store_rate_us_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(storeTextureAtlas, activity, "store_rateus_button.png");
		
		store_coins_tiled_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(storeCoinsTextureAtlas, activity, "store_score_tiles.png", 10, 1);
		
		try {
			this.storeBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.storeCoinsTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.storeTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.storeBackgroundTextureAtlas.load();
			this.storeCoinsTextureAtlas.load();
			this.storeTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			org.andengine.util.debug.Debug.e(e);
		}
	}
	
	private void loadStoreFonts() {
		FontFactory.setAssetBasePath("fonts/store/");
	}
	
	private void loadStoreAudio() {
		SoundFactory.setAssetBasePath("sound/store/");
		try {
			store_coins_reward_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "store_coins_reward_sound.mp3");
			store_popup_window_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "store_popup_window_sound.mp3");
			store_unlock_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "store_unlock_sound.mp3");
			store_error_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "store_error_sound.mp3");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void unloadStoreTextures() {
		this.storeBackgroundTextureAtlas.unload();
		this.storeCoinsTextureAtlas.unload();
		this.storeTextureAtlas.unload();
	}
	
	private void unloadStoreFonts() {
		
	}
	
	private void unloadStoreAudio() {
		store_coins_reward_sound.stop();
		store_popup_window_sound.stop();
		store_unlock_sound.stop();
		store_error_sound.stop();
		activity.getSoundManager().remove(store_coins_reward_sound);
		activity.getSoundManager().remove(store_popup_window_sound);
		activity.getSoundManager().remove(store_unlock_sound);
		activity.getSoundManager().remove(store_error_sound);
		System.gc();
	}
	
	//Game Methods
	public void loadGameResources() {
		loadGameGraphics();
		loadGameAudio();
		loadGameFonts();
	}
	
	public void unloadGameResources() {
		unloadGameTextures();
		unloadGameFonts();	
		unloadGameAudio();
	}
	
	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameBloodTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameHudTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameWindowsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameAnimatedTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameScoreTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 550, 55, TextureOptions.DEFAULT);
		gameMovingSpikeTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameFireTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1440, 100, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		
		//Game HUD
		game_fire_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameFireTextureAtlas, activity, "game_fire.png", 2, 1);
		
		//Blood
		game_blood_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBloodTextureAtlas, activity, "game_blood.png");
		
		game_tap_text_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_tap_text.png");
		game_tap_block_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_tap_block.png");
		
		game_score_tiled_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameScoreTextureAtlas, activity, "game_score_tiles.png", 10, 1);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String player = sharedPreferences.getString("selectedPlayer", "beard");
			
		if (player.equals("beard")) {
			game_player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "player_beard.png", 6, 1);
		} else if (player.equals("nerd")) {
			game_player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "player_nerd.png", 6, 1);
		} else if (player.equals("ninja")) {
			game_player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "player_ninja.png", 6, 1);
		} else if (player.equals("robot")) {
			game_player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "player_robot.png", 6, 1);
		}
		
		game_coin_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_coin.png", 4, 1);
		game_spider_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_spider.png", 2, 1);
		game_spider_2_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_spider_2.png", 2, 1);		
		
		String stage = sharedPreferences.getString("selectedStage", "castle");
		//Castle
		if (stage.equals("castle")) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_castle.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_castle.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_castle.png");
			game_left_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_moving_spikes_castle.png");
			game_right_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_moving_spikes_castle.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_castle.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_castle.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_castle.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_castle.png");
		//Red bricks
		} else if (stage.equals("brick")) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_brick.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_brick.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_brick.png");
			game_left_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_moving_spikes_brick.png");
			game_right_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_moving_spikes_brick.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_brick.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_brick.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_brick.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_brick.png");
		//Wood
		}  else if (stage.equals("wood")) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_wood.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_wood.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_wood.png");
			game_left_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_moving_spikes_wood.png");
			game_right_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_moving_spikes_wood.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_wood.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_wood.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_wood.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_wood.png");
		//Steel
		} else if (stage.equals("steel")) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_steel.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_steel.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_steel.png");
			game_left_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_moving_spikes_steel.png");
			game_right_moving_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_moving_spikes_steel.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_steel.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_steel.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_steel.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_steel.png");
		}
		game_spider_web_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_spider_web.png");
		
		
		//Game windows atlas
		game_over_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_over_window.png"); 
		game_pause_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_pause_window.png");
		game_resume_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_resume_button.png");
		game_retry_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_retry_button.png");
		game_quit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_quit_button.png");
		game_twitter_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_twitter_button.png");
		game_gpg_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_gpg_button.png");
		game_new_record_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_new_record.png");
		
		try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameBloodTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameHudTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameWindowsTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameAnimatedTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameScoreTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameFireTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameMovingSpikeTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
			this.gameBloodTextureAtlas.load();
			this.gameHudTextureAtlas.load();
			this.gameWindowsTextureAtlas.load();
			this.gameBackgroundTextureAtlas.load();
			this.gameAnimatedTextureAtlas.load();
			this.gameScoreTextureAtlas.load();
			this.gameFireTextureAtlas.load();
			this.gameMovingSpikeTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadGameAudio() {
		MusicFactory.setAssetBasePath("music/game/");
	}
	
	public void unloadGameAudio() {

		System.gc();
	}
	
	private void loadGameFonts() {
		FontFactory.setAssetBasePath("fonts/game/");
	}
	
	private void unloadGameTextures() {
		this.gameTextureAtlas.unload();
		this.gameBloodTextureAtlas.unload();
		this.gameHudTextureAtlas.unload();
		this.gameWindowsTextureAtlas.unload();
		this.gameBackgroundTextureAtlas.unload();
		this.gameAnimatedTextureAtlas.unload();
		this.gameScoreTextureAtlas.unload();
		this.gameFireTextureAtlas.unload();
		this.gameMovingSpikeTextureAtlas.unload();
	}
	
	private void unloadGameFonts() {
		
	}
	
	
	//Manager Methods
	public static void prepareManager(Engine engine, GameActivity activity, SmoothCamera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;		
	}
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}

}


