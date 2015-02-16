package com.lucianosimo.pixeljumpingescape.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
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

public class RightMovingSpikes extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private static final int SPEED = -3;
	
	public RightMovingSpikes(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_right_moving_spikes_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		final float width = 720 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 128 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] vector = {
			new Vector2(-0.49172f*width, -0.11096f*height),
			new Vector2(-0.43124f*width, -0.49976f*height),
			new Vector2(+0.48172f*width, -0.48356f*height),
			new Vector2(+0.48172f*width, +0.48844f*height),
			new Vector2(-0.43700f*width, +0.48844f*height),
			new Vector2(-0.49172f*width, +0.13204f*height),
		};
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, vector, BodyType.KinematicBody, fixture);
		body.setUserData("rightSpikes");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				if (getX() < (camera.getCenterX() + 360)) {
					stopMoving();
				}
			}
		});
	}
	
	public Body getBody() {
		return body;
	}
	
	public void startMoving() {
		body.setLinearVelocity(new Vector2(SPEED, body.getLinearVelocity().y));
	}
	
	public void stopMoving() {
		body.setLinearVelocity(new Vector2(0, 0));
	}
}
