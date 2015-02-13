package com.lucianosimo.pixeljumpingescape.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lucianosimo.pixeljumpingescape.manager.ResourcesManager;

public class LeftSpikes extends Sprite{

	private Body body;
	private FixtureDef fixture;
	
	public LeftSpikes(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_left_spikes_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		final float width = 100 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 128 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] vector = {
			new Vector2(-0.50036f*width, -0.49965f*height),
			new Vector2(+0.15963f*width, -0.49972f*height),
			new Vector2(+0.50315f*width, -0.21870f*height),
			new Vector2(+0.49980f*width, +0.27841f*height),
			new Vector2(+0.15834f*width, +0.49844f*height),
			new Vector2(-0.50266f*width, +0.50100f*height),
		};
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, vector, BodyType.StaticBody, fixture);
		body.setUserData("leftSpikes");
		body.setFixedRotation(true);
	}
	
	public Body getBody() {
		return body;
	}
}
