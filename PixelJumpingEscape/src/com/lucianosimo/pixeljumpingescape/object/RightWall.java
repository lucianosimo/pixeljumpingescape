package com.lucianosimo.pixeljumpingescape.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lucianosimo.pixeljumpingescape.manager.ResourcesManager;

public class RightWall extends Sprite{

	private Body body;
	private FixtureDef fixture;
	
	public RightWall(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_wall_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		//fixture.filter.groupIndex = -1;
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, fixture);
		body.setUserData("rightWall");
		body.setFixedRotation(true);
	}
	
	public Body getBody() {
		return body;
	}
	
}
