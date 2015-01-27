package com.lucianosimo.pixeljumpingescape.manager;

import java.util.Random;

import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
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
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

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
	
	//Menu fonts
	
	//Menu audio
	
	//Menu items
	public ITextureRegion loading_background_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion menu_play_button_region;

	private BuildableBitmapTextureAtlas loadingBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas menuBackgroundTextureAtlas;
	
	
	//Game audio
	
	//Game fonts
	public Font game_score_font;
	
	//Game HUD
	public ITiledTextureRegion game_fire_region;
	public ITextureRegion game_score_sign_region;
	public ITextureRegion game_blood_region;
	
	//Objects
	public ITextureRegion game_wall_region;
	public ITextureRegion game_left_spikes_region;
	public ITextureRegion game_right_spikes_region;
	public ITextureRegion game_center_spikes_region;
	public ITextureRegion game_spider_web_region;
	
	//Platforms
	public ITextureRegion game_floor_region;
	
	//Backgrounds
	public ITextureRegion game_background_region;
	
	//Animated
	public ITiledTextureRegion game_player_region;
	public ITiledTextureRegion game_spider_region;
	public ITiledTextureRegion game_spider_2_region;
	
	//Countdown
	
	//Windows
	public ITextureRegion game_over_window_region;
	public ITextureRegion game_retry_button_region;
	public ITextureRegion game_quit_button_region;

	//Buttons
	
	//Score tiles;
	
	//Game Textures
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private BuildableBitmapTextureAtlas gameBloodTextureAtlas;
	private BuildableBitmapTextureAtlas gameHudTextureAtlas;
	private BuildableBitmapTextureAtlas gameWindowsTextureAtlas;
	private BuildableBitmapTextureAtlas gameAnimatedTextureAtlas;
	private BuildableBitmapTextureAtlas gameFireTextureAtlas;
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
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR);
		menuBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		loadingBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		
		loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingBackgroundTextureAtlas, activity, "loading_background.png");
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background.png");
		menu_play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_play_button.png");
		
		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.loadingBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
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
		MusicFactory.setAssetBasePath("music/menu/");
	}
	
	private void unloadMenuTextures() {
		this.menuTextureAtlas.unload();
		this.menuBackgroundTextureAtlas.unload();
	}
	
	private void unloadMenuFonts() {
		
	}
	
	private void unloadMenuAudio() {
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
		Random rand = new Random();
		
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameBloodTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameHudTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameWindowsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameAnimatedTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameFireTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1440, 100, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 720, 1280, TextureOptions.BILINEAR);
		
		//Game HUD
		game_fire_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameFireTextureAtlas, activity, "game_fire.png", 2, 1);
		
		//Blood
		game_blood_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBloodTextureAtlas, activity, "game_blood.png");
		
		//Game animated atlas
		int player = rand.nextInt(2) + 1;
		player = 1;
		if (player == 1) {
			game_player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "player_ninja.png", 6, 1);
		} else if (player == 2) {
			game_player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "player_beard.png", 2, 1);
		}
		
		game_spider_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_spider.png", 2, 1);
		game_spider_2_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_spider_2.png", 2, 1);		
		
		//Game texture atlas
		int stage = rand.nextInt(4) + 1;
		//Castle
		if (stage == 1) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_castle.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_castle.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_castle.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_castle.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_castle.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_castle.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_castle.png");
		//Red bricks
		} else if (stage == 2) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_brick.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_brick.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_brick.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_brick.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_brick.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_brick.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_brick.png");
		//Steel
		} else if (stage == 3) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_steel.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_steel.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_steel.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_steel.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_steel.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_steel.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_steel.png");
		//Wood
		} else if (stage == 4) {
			game_wall_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_wall_wood.png");
			game_left_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_left_spikes_wood.png");
			game_right_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_right_spikes_wood.png");
			game_center_spikes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_center_spikes_wood.png");
			game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background_wood.png");
			game_score_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHudTextureAtlas, activity, "game_score_sign_wood.png");
			game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_wood.png");
		}
		game_spider_web_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_spider_web.png");
		
		
		//Game windows atlas
		game_over_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_over_window.png"); 
		game_retry_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_retry_button.png");
		game_quit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_quit_button.png");
		
		try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameBloodTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameHudTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameWindowsTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameAnimatedTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameFireTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
			this.gameBloodTextureAtlas.load();
			this.gameHudTextureAtlas.load();
			this.gameWindowsTextureAtlas.load();
			this.gameBackgroundTextureAtlas.load();
			this.gameAnimatedTextureAtlas.load();
			this.gameFireTextureAtlas.load();
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
		
		final ITexture game_score_texture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		game_score_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), game_score_texture, activity.getAssets(), "karmaticArcade.ttf", 50, false, Color.WHITE_ARGB_PACKED_INT, 0.5f, Color.BLACK_ARGB_PACKED_INT);
		game_score_font.load();
	}
	
	private void unloadGameTextures() {
		this.gameTextureAtlas.unload();
		this.gameBloodTextureAtlas.unload();
		this.gameHudTextureAtlas.unload();
		this.gameWindowsTextureAtlas.unload();
		this.gameBackgroundTextureAtlas.unload();
		this.gameAnimatedTextureAtlas.unload();
		this.gameFireTextureAtlas.load();
	}
	
	private void unloadGameFonts() {
		game_score_font.unload();
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


