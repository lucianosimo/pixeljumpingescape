package com.lucianosimo.pixeljumpingescape.object;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lucianosimo.pixeljumpingescape.manager.ResourcesManager;

public abstract class Player extends AnimatedSprite{

	private Body body;
	private FixtureDef fixture;
	private boolean onAir = true;
	private boolean onRightWall = false;
	private boolean onLeftWall = false;
	private boolean initial = true;
	private boolean showBlood = false;
	private boolean isDead = false;
	private final static int LEFT_SPEED = -30;
	private final static int RIGHT_SPEED = 30;
	
	public abstract void onDie();
	
	public Player(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_player_region, vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0.9f);
		final float width = 50 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 100 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] vector = {
			new Vector2(-0.36342f*width, -0.49824f*height),
			new Vector2(+0.48695f*width, -0.49905f*height),
			new Vector2(+0.48856f*width, +0.30551f*height),
			new Vector2(-0.36342f*width, +0.30551f*height),

		};
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, vector, BodyType.DynamicBody, fixture);
		
		this.setUserData("player");
		body.setUserData("player");
		
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				if (getY() < (camera.getCenterY() - 640)) {
					onDie();
				}
				if (isDead) {
					isDead = false;
					if (getX() < 360) {
						body.setLinearVelocity(new Vector2(3, 0));
					} else {
						body.setLinearVelocity(new Vector2(-3, 0));
					}					
				}
				if (onLeftWall) {
					body.setLinearVelocity(new Vector2(LEFT_SPEED, body.getLinearVelocity().y));
				} else if (onRightWall) {
					body.setLinearVelocity(new Vector2(RIGHT_SPEED, body.getLinearVelocity().y));
				} else if (initial) {
					body.setLinearVelocity(new Vector2(0, body.getLinearVelocity().y));
				}
				
			}
		});
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public boolean showBlood() {
		return showBlood;
	}
	
	public void hideBlood() {
		showBlood = false;
	}
	
	public boolean isOnLeftWall() {
		return onLeftWall;
	}
	
	public boolean isOnRightWall() {
		return onRightWall;
	}
	
	public boolean isOnAir() {
		return onAir;
	}
	
	public boolean isInitial() {
		return initial;
	}
	
	public void setOnAirFalse() {
		onAir = false;
	}
	
	public void goToLeftWall(float ySpeed) {
		onRightWall = false;
		onAir = true;
		onLeftWall = true;
		initial = false;
		body.setLinearVelocity(new Vector2(LEFT_SPEED, ySpeed));
	}
	
	public void goToRightWall(float ySpeed) {
		onLeftWall = false;
		onAir = true;
		onRightWall = true;
		initial = false;
		body.setLinearVelocity(new Vector2(RIGHT_SPEED, ySpeed));
	}
	
	public void stopPlayer() {
		body.setLinearVelocity(new Vector2(0, 0));
	}
	
	public Body getPlayerBody() {
		return body;
	}
	
	public void killPlayer(SmoothCamera camera, AutoParallaxBackground background) {
		onLeftWall = false;
		onRightWall = false;
		showBlood = true;
		isDead = true;
		camera.setMaxVelocityY(0);
		background.setParallaxChangePerSecond(0);
		if (this.getX() < 320) {
			this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(5, 0, 540)));
		} else {
			this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(5, 0, -540)));
		}
	}
}
