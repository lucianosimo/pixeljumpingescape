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

public class RightSpikes extends Sprite{

	private Body body;
	private FixtureDef fixture;
	
	public RightSpikes(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_right_spikes_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		final float width = 100 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 128 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] vector = {
			new Vector2(-0.50150f*width, -0.19635f*height),
			new Vector2(-0.11293f*width, -0.49969f*height),
			new Vector2(+0.49945f*width, -0.49926f*height),
			new Vector2(+0.49999f*width, +0.49985f*height),
			new Vector2(-0.10500f*width, +0.49960f*height),
			new Vector2(-0.49917f*width, +0.22700f*height),
		};
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, vector, BodyType.StaticBody, fixture);
		body.setUserData("rightSpikes");
		body.setFixedRotation(true);
	}
	
	public Body getBody() {
		return body;
	}
}
