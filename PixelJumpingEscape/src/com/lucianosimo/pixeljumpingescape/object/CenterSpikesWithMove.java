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

public class CenterSpikesWithMove extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private float initialY;
	private boolean moveToLeft = false;
	private boolean moveToRight = false;
	private boolean moveToTop = false;
	private boolean moveToDown = false;
	private boolean horizontal = false;
	private final static int LEFT_BOUND = 200;
	private final static int RIGHT_BOUND = 520;
	private final static int TOP_BOUND = 125;
	private final static int DOWN_BOUND = -125;
	private final static int LEFT_SPEED = -11;
	private final static int RIGHT_SPEED = 11;
	private final static int TOP_SPEED = 11;
	private final static int DOWN_SPEED = -11;
	
	public CenterSpikesWithMove(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld,boolean hor) {
		super(pX, pY, ResourcesManager.getInstance().game_center_spikes_region.deepCopy(), vbom);
		if (hor) {
			horizontal = true;
			moveToLeft = true;
		} else {
			horizontal = false;
			moveToTop = true;
		}
		initialY = pY;
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
					body.setLinearVelocity(new Vector2(LEFT_SPEED, body.getLinearVelocity().y));
				} else if (moveToRight) {
					body.setLinearVelocity(new Vector2(RIGHT_SPEED, body.getLinearVelocity().y));
				} else if (moveToTop) {
					body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, TOP_SPEED));
				} else if (moveToDown) {
					body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, DOWN_SPEED));
				}
				if (horizontal) {
					if (getX() < LEFT_BOUND) {
						moveToRight = true;
						moveToLeft = false;
					}
					if (getX() > RIGHT_BOUND) {
						moveToLeft = true;
						moveToRight = false;
					}
				} else {
					if (getY() > (initialY + TOP_BOUND)) {
						moveToDown = true;
						moveToTop = false;
					}
					if (getY() < (initialY + DOWN_BOUND)) {
						moveToTop = true;
						moveToDown = false;
					}
				}
				
			}
		});
	}
	
	public Body getBody() {
		return body;
	}
	
	public void setInitialY(float y) {
		initialY = y;
	}
	
}
