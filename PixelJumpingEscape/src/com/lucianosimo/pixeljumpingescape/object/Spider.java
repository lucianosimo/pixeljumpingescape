package com.lucianosimo.pixeljumpingescape.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Spider extends AnimatedSprite{

	private Body body;
	private FixtureDef fixture;
	private boolean isMoving;
	private final static int SPIDER_SPEED = -4;
	
	public Spider(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld, ITiledTextureRegion spider_region) {
		super(pX, pY, spider_region, vbom);
		isMoving = false;
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, fixture);
		body.setUserData("spider");		
		body.setFixedRotation(true);		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
	}
	
	public void startMoving() {
		body.setLinearVelocity(new Vector2(0, SPIDER_SPEED));
		isMoving = true;
	}
	
	public void stopMoving() {
		body.setLinearVelocity(new Vector2(0, 0));
		isMoving = false;
	}
	
	public Body getBody() {
		return body;
	}
	
	public boolean isMoving() {
		return isMoving;
	}
}
