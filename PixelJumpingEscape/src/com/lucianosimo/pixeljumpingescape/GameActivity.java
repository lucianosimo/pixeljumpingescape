package com.lucianosimo.pixeljumpingescape;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.lucianosimo.pixeljumpingescape.manager.ResourcesManager;
import com.lucianosimo.pixeljumpingescape.manager.SceneManager;

public class GameActivity extends BaseGameActivity {

	private SmoothCamera camera;
	public static float mGravityX = 0;
	//private int score = 0;
	
	private final static float SPLASH_DURATION = 5f;
	
	//private final static int SWARM_APP_ID = 12987;
	//private final static String SWARM_APP_KEY = "27b45b3507f2daea1c39203e523c00cf";
	//private final static int SWARM_LEADERBOARD_ID = 17629;
	
	//private final static String CHARTBOOST_APP_ID = "5404aa5cc26ee42f745be480";
	//private final static String CHARTBOOST_APP_SIGNATURE = "91043a4b46e9cbb87172ca5e675c8d0183825734";
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		/*Chartboost.startWithAppId(this, CHARTBOOST_APP_ID, CHARTBOOST_APP_SIGNATURE);
	    Chartboost.onCreate(this);
		Swarm.setActive(this);
		Swarm.preload(this, SWARM_APP_ID, SWARM_APP_KEY);*/
	}
    
	/*public void showLeaderboard() {
		Swarm.setAllowGuests(true);
		if (!Swarm.isInitialized() ) {
    		Swarm.init(this, SWARM_APP_ID, SWARM_APP_KEY);
        }
		this.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			SwarmLeaderboard.showLeaderboard(SWARM_LEADERBOARD_ID);
    		}
    	});
	}
	
    public void submitScore(int submitScore) {
    	score = submitScore;
    	Swarm.setAllowGuests(true);
    	Swarm.init(this, SWARM_APP_ID, SWARM_APP_KEY);
    	this.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			SwarmLeaderboard.submitScore(SWARM_LEADERBOARD_ID, score);
    		}
    	});
    }*/
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new SmoothCamera(0, 0, 720, 1280, 0, 0, 0);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		//Chartboost.onPause(this);
		SceneManager.getInstance().getCurrentScene().handleOnPause();
		//Swarm.setInactive(this);
		mEngine.getSoundManager().setMasterVolume(0);
		mEngine.getMusicManager().setMasterVolume(0);
	}
	
	@Override
	protected synchronized void onResume() {
		super.onResume();
		//Chartboost.onResume(this);
		//Swarm.setActive(this);
		/*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
		int musicEnabled = sharedPreferences.getInt("musicEnabled", 0);
		if (soundEnabled == 1) {
			enableSound(false);
		} else if (soundEnabled == 0) {
			enableSound(true);
		}
		if (musicEnabled == 1) {
			enableMusic(false);
		} else if (musicEnabled == 0) {
			enableMusic(true);
		}*/
	}
	
	/*public void enableSound(boolean enable) {
		if (enable) {
			mEngine.getSoundManager().setMasterVolume(1);
		} else {
			mEngine.getSoundManager().setMasterVolume(0);
		}
	}
	
	public void enableMusic(boolean enable) {
		if (enable) {
			mEngine.getMusicManager().setMasterVolume(1);
		} else {
			mEngine.getMusicManager().setMasterVolume(0);
		}
	}*/

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws IOException {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)	throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);		
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(SPLASH_DURATION, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().createMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Chartboost.onDestroy(this);
		System.exit(0);
	}
	
	public void tweetScore(Intent intent) {
		startActivity(Intent.createChooser(intent, "Protect the town"));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed(); 
			/*if (Chartboost.onBackPressed()) {
				 return false;
			 } else {
				 SceneManager.getInstance().getCurrentScene().onBackKeyPressed(); 
			 }*/
		}
		return false;
	}

    @Override
	protected void onStart() {
		super.onStart();
		//Chartboost.onStart(this);
		//Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//Chartboost.onStop(this);
	}	
}
