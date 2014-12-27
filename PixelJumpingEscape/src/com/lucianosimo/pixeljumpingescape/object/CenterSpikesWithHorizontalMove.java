package com.lucianosimo.pixeljumpingescape.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lucianosimo.pixeljumpingescape.manager.ResourcesManager;

public class CenterSpikesWithHorizontalMove extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private boolean moveToLeft = true;
	private boolean moveToRight = false;
	private final static int LEFT_BOUND = 200;
	private final static int RIGHT_BOUND = 520;
	
	public CenterSpikesWithHorizontalMove(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_center_spikes_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		//fixture.filter.groupIndex = -1;
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, fixture);
		body.setUserData("centerSpikes");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				if (moveToLeft) {
					body.setLinearVelocity(new Vector2(-10, body.getLinearVelocity().y));
				} else if (moveToRight) {
					body.setLinearVelocity(new Vector2(10, body.getLinearVelocity().y));
				}
				if (getX() < LEFT_BOUND) {
					moveToRight = true;
					moveToLeft = false;
				}
				if (getX() > RIGHT_BOUND) {
					moveToLeft = true;
					moveToRight = false;
				}
			}
		});
	}
	
	public Body getBody() {
		return body;
	}
	
}
