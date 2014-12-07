package com.lucianosimo.pixeljumpingescape.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lucianosimo.pixeljumpingescape.manager.ResourcesManager;

public abstract class Player extends AnimatedSprite{

	private Body body;
	private FixtureDef fixture;
	private boolean onRightWall = false;
	private boolean onLeftWall = true;
	
	public abstract void onDie();
	
	public Player(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_player_region, vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, fixture);
		
		this.setUserData("player");
		body.setUserData("player");
		
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				if (onLeftWall) {
					body.setLinearVelocity(new Vector2(-30, body.getLinearVelocity().y));
				} else if (onRightWall) {
					body.setLinearVelocity(new Vector2(30, body.getLinearVelocity().y));
				}
				
			}
		});
	}
	
	public void changeWall() {
		if (onLeftWall) {
			onLeftWall = false;
			onRightWall = true;
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 10));
		} else if (onRightWall) {
			onRightWall = false;
			onLeftWall = true;
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 10));
		}
	}
	
	public void stopPlayer() {
		body.setLinearVelocity(new Vector2(0, 0));
	}
	
	public Body getPlayerBody() {
		return body;
	}	
}
