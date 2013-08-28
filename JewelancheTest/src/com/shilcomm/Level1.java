package com.shilcomm;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.util.ArrayList;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

public class Level1 extends Level implements IOnSceneTouchListener, IOnAreaTouchListener {

	JewelancheTestActivity main;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mCircleFaceTextureRegion;
	private TiledTextureRegion mHexagonFaceTextureRegion;

	Body groundbody;
	TimerHandler tm;
	Text txtScore;
	int score = 0;

	Font scoreFont;

	int[] fibonacci = { 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 150 };

	Vibrator vib;
	ArrayList<PhysicsConnector> activeShapes = new ArrayList<PhysicsConnector>();
	PhysicsJointConnectorManager jointManager = new PhysicsJointConnectorManager();
	// ArrayList<DistanceJoint> activeJoints = new ArrayList<DistanceJoint>();
	ArrayList<PhysicsJointConnector> activeJoints = new ArrayList<PhysicsJointConnector>();

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.4f, 0.5f);
	PhysicsWorld mPhysicsWorld;

	public Level1(JewelancheTestActivity parent) {
		super();
		main = parent;

		main.registerFPSLogger();

		vib = (Vibrator) main.getSystemService(Context.VIBRATOR_SERVICE);

		ITimerCallback itc = new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// TODO Auto-generated method stub
				main.lvl1.dropRandomShape();

				if (pTimerHandler.getTimerSeconds() > 0.2)
					pTimerHandler.setTimerSeconds(pTimerHandler.getTimerSeconds() * 0.97f);
			}

		};
		tm = new TimerHandler(1f, true, itc);
		main.getEngine().registerUpdateHandler(tm);

		this.setBackground(new Background(0, 0, 0));
		this.setOnSceneTouchListener(this);
		this.setOnAreaTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, -SensorManager.GRAVITY_EARTH), false);

		final VertexBufferObjectManager vertexBufferObjectManager = main.getVertexBufferObjectManager();
		//final Rectangle ground = new Rectangle(0, JewelancheTestActivity.CAMERA_HEIGHT - 2, JewelancheTestActivity.CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle ground = new Rectangle(JewelancheTestActivity.CAMERA_WIDTH/2, 1, JewelancheTestActivity.CAMERA_WIDTH, 2, vertexBufferObjectManager);
		// final Rectangle roof = new Rectangle(0, 0, main.CAMERA_WIDTH, 2,
		// vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, JewelancheTestActivity.CAMERA_HEIGHT/2, 2, JewelancheTestActivity.CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(JewelancheTestActivity.CAMERA_WIDTH - 1, JewelancheTestActivity.CAMERA_HEIGHT/2, 2, JewelancheTestActivity.CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.4f, 0.5f);
		groundbody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		groundbody.setUserData(ground);
		// PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,
		// BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.attachChild(ground);
		// this.attachChild(roof);
		this.attachChild(left);
		this.attachChild(right);

		this.registerUpdateHandler(this.mPhysicsWorld);
		this.registerUpdateHandler(this.jointManager);
	
		

		scoreFont = new Font(main.getFontManager(), main.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, true, Color.WHITE);

		main.loadFont(scoreFont);

		txtScore = new Text(100, JewelancheTestActivity.CAMERA_HEIGHT-10, scoreFont, "Score: 0", 14, main.getVertexBufferObjectManager());
		this.attachChild(txtScore);

	}

	protected void dropRandomShape() {
		// TODO Auto-generated method stub
		float x = (float) (Math.random() * JewelancheTestActivity.CAMERA_WIDTH * .8 + 10);

		switch ((int) Math.round(Math.random() * 3)) {
		case 1:
			this.createCircle(x, JewelancheTestActivity.CAMERA_HEIGHT + 50);
			break;

		case 2:
			this.createSquare(x, JewelancheTestActivity.CAMERA_HEIGHT + 50);
			break;

		case 3:
			this.createHexagon(x, JewelancheTestActivity.CAMERA_HEIGHT + 50);
			break;

		}

		if (this.mPhysicsWorld.getBodyCount() > 70) {
			this.detachChild((IEntity) groundbody.getUserData());
			this.mPhysicsWorld.destroyBody(groundbody);
			txtScore.setText("LOSE!!");
			Thread lose = new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
						youLose();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//

				}
			};
		
			lose.start();
			

		}
	}

	private void youLose() {
		// TODO Auto-generated method stub

		// main.mMainScene.setChildSceneModal(main.mMenuScene);
		// this.mPhysicsWorld.dispose();
		// this.detachChildren();
	//	this.detachChild(txtScore);

		//txtScore = null;
		main.getEngine().unregisterUpdateHandler(tm);

		main.returnToMenu();

	}

	@Override
	public void loadResources() {
		// TODO Auto-generated method stub
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 64, 128, TextureOptions.BILINEAR);
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, main, "face_box_tiled.png", 0, 0, 2, 1); // 64x32
		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, main, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
		BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, main, "face_triangle_tiled.png", 0, 64, 2, 1);
		this.mHexagonFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, main, "face_hexagon_tiled.png", 0, 96, 2, 1); // 64x32
		this.mBitmapTextureAtlas.load();

		this.createCircle(JewelancheTestActivity.CAMERA_WIDTH / 6 - 15, JewelancheTestActivity.CAMERA_HEIGHT / 2 - 200);// left
		this.createCircle(JewelancheTestActivity.CAMERA_WIDTH / 5.1f - 15, JewelancheTestActivity.CAMERA_HEIGHT / 2.2f - 200);
		this.createCircle(JewelancheTestActivity.CAMERA_WIDTH / 4.2f - 15, JewelancheTestActivity.CAMERA_HEIGHT / 2 - 200);

		this.createSquare(JewelancheTestActivity.CAMERA_WIDTH - JewelancheTestActivity.CAMERA_WIDTH / 6 - 15, JewelancheTestActivity.CAMERA_HEIGHT / 2 - 200);// right
		this.createSquare(JewelancheTestActivity.CAMERA_WIDTH - JewelancheTestActivity.CAMERA_WIDTH / 5 - 15, JewelancheTestActivity.CAMERA_HEIGHT / 2 - 200);
		this.createSquare(JewelancheTestActivity.CAMERA_WIDTH - JewelancheTestActivity.CAMERA_WIDTH / 4 - 15, JewelancheTestActivity.CAMERA_HEIGHT / 2 - 200);

		this.createHexagon(JewelancheTestActivity.CAMERA_WIDTH / 2 - 15, JewelancheTestActivity.CAMERA_HEIGHT / 3 - 200);// middle
																															// and
																															// higher
		this.createHexagon(JewelancheTestActivity.CAMERA_WIDTH / 2.2f - 15, JewelancheTestActivity.CAMERA_HEIGHT / 3 - 200);
		this.createHexagon(JewelancheTestActivity.CAMERA_WIDTH / 1.8f - 15, JewelancheTestActivity.CAMERA_HEIGHT / 3 - 200);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (this.mPhysicsWorld != null) {
			if (pSceneTouchEvent.isActionUp()) {
				// this.addFace(pSceneTouchEvent.getX(),
				// pSceneTouchEvent.getY());

				// destroy all bodies as well as joints if there are three or
				// more shapes in the chain and empty array
				// otherwise destroy the joint and detach em and empty array
				for (PhysicsJointConnector pjc : activeJoints) {
					this.mPhysicsWorld.destroyJoint(pjc.getJoint());
					jointManager.remove(pjc);
					this.detachChild(pjc.getLine());
				}
				activeJoints.clear();
				if (activeShapes.size() > 2) {
					for (PhysicsConnector pc : activeShapes) {
						this.mPhysicsWorld.unregisterPhysicsConnector(pc);
						this.mPhysicsWorld.destroyBody(pc.getBody());
						this.unregisterTouchArea(pc.getEntity());
						this.detachChild(pc.getEntity());
					}
					vibrate(activeShapes.size());
					updateScore(activeShapes.size());
				}
				for (PhysicsConnector pc : activeShapes) {
					AnimatedSprite tmp = (AnimatedSprite) pc.getEntity();
					tmp.setCurrentTileIndex(0);
				}
				activeShapes.clear();

				return true;
			}
		}
		return false;
	}

	private void vibrate(int i) {
		// TODO Auto-generated method stub
		vib.vibrate(i * 33);

	}

	private PhysicsConnector createCircle(float pX, float pY) {
		AnimatedSprite face = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion, main.getVertexBufferObjectManager());

		Body body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		body.setUserData(ShapeType.Circle);
		// face.animate(200);
		this.registerTouchArea(face);
		this.attachChild(face);
		PhysicsConnector pc = new PhysicsConnector(face, body, true, true);
		this.mPhysicsWorld.registerPhysicsConnector(pc);

		return pc;

	}

	private PhysicsConnector createSquare(float pX, float pY) {
		AnimatedSprite face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, main.getVertexBufferObjectManager());

		Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		body.setUserData(ShapeType.Square);
		// face.animate(200);
		this.registerTouchArea(face);
		this.attachChild(face);
		PhysicsConnector pc = new PhysicsConnector(face, body, true, true);
		this.mPhysicsWorld.registerPhysicsConnector(pc);

		return pc;

	}

	private PhysicsConnector createHexagon(float pX, float pY) {
		AnimatedSprite face = new AnimatedSprite(pX, pY, this.mHexagonFaceTextureRegion, main.getVertexBufferObjectManager());

		Body body = createHexagonBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		body.setUserData(ShapeType.Hexagon);
		// face.animate(200);
		// face.
		// face.setCurrentTileIndex(pCurrentTileIndex)
		this.registerTouchArea(face);
		this.attachChild(face);
		PhysicsConnector pc = new PhysicsConnector(face, body, true, true);
		this.mPhysicsWorld.registerPhysicsConnector(pc);

		return pc;

	}

	private static Body createHexagonBody(final PhysicsWorld pPhysicsWorld, final IShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		/*
		 * Remember that the vertices are relative to the center-coordinates of
		 * the Shape.
		 */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		/*
		 * The top and bottom vertex of the hexagon are on the bottom and top of
		 * hexagon-sprite.
		 */
		final float top = -halfHeight;
		final float bottom = halfHeight;

		final float centerX = 0;

		/*
		 * The left and right vertices of the heaxgon are not on the edge of the
		 * hexagon-sprite, so we need to inset them a little.
		 */
		final float left = -halfWidth + 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float right = halfWidth - 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float higher = top + 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float lower = bottom - 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;

		final Vector2[] vertices = { new Vector2(centerX, top), new Vector2(right, higher), new Vector2(right, lower), new Vector2(centerX, bottom), new Vector2(left, lower),
				new Vector2(left, higher) };

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		PhysicsConnector temp = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape((IShape) pTouchArea);

		switch (pSceneTouchEvent.getAction()) {
		case TouchEvent.ACTION_DOWN:
			// add shape to array
			activeShapes.add(temp);
			AnimatedSprite tmp1 = (AnimatedSprite) temp.getEntity();
			tmp1.setCurrentTileIndex(1);
			return true;
		case TouchEvent.ACTION_MOVE:
			// add shape to array if it doesn't exist already, it is the same
			// type as the other shape, and it is close to the other shape
			if (activeShapes.isEmpty()) {
				activeShapes.add(temp);
				AnimatedSprite tmp = (AnimatedSprite) temp.getEntity();
				tmp.setCurrentTileIndex(1);
			} else if (!activeShapes.contains(temp) && activeShapes.size() >= 1) {
				PhysicsConnector oldShape = activeShapes.get(activeShapes.size() - 1);
				if (temp.getBody().getUserData() == oldShape.getBody().getUserData()
						&& temp.getBody().getWorldCenter().dst(oldShape.getBody().getWorldCenter()) * 32 < JewelancheTestActivity.CAMERA_WIDTH / 3.8) {
					activeShapes.add(temp);
					AnimatedSprite tmp = (AnimatedSprite) temp.getEntity();
					tmp.setCurrentTileIndex(1);
					this.createJoint(temp, oldShape);
				}
			}

			// create joint

			return true;
		case TouchEvent.ACTION_UP:
			// this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());

			// destroy all bodies as well as joints if there are three or more
			// shapes in the chain and empty array
			// otherwise destroy the joint and detach em and empty array
			for (PhysicsJointConnector pjc : activeJoints) {
				this.mPhysicsWorld.destroyJoint(pjc.getJoint());
				jointManager.remove(pjc);
				this.detachChild(pjc.getLine());
			}
			activeJoints.clear();
			if (activeShapes.size() > 2) {

				for (PhysicsConnector pc : activeShapes) {
					this.mPhysicsWorld.unregisterPhysicsConnector(pc);
					this.mPhysicsWorld.destroyBody(pc.getBody());
					this.unregisterTouchArea(pc.getEntity());
					this.detachChild(pc.getEntity());
				}
				vibrate(activeShapes.size());
				updateScore(activeShapes.size());
			} else {
				for (PhysicsConnector pc : activeShapes) {
					AnimatedSprite tmp = (AnimatedSprite) pc.getEntity();
					tmp.setCurrentTileIndex(0);
				}
			}
			activeShapes.clear();

			return true;
		}

		return false;
	}

	private void updateScore(int numShapes) {
		// TODO Auto-generated method stub

		if (numShapes < fibonacci.length) {
			score += fibonacci[numShapes] * 8;
		} else {
			score += 150 * 8;
		}
		txtScore.setText("Score: " + score);
		// tm.setTimerSeconds((float) (tm.getTimerSeconds()/Math.pow(0.9f,
		// numShapes)));
		// if(tm.getTimerSeconds()>1f) tm.setTimerSeconds(0.7f);

	}

	public void createJoint(PhysicsConnector physicsConnector, PhysicsConnector physicsConnector2) {

		Body bodyA = physicsConnector.getBody();
		Body bodyB = physicsConnector2.getBody();
		Line line = new Line(bodyA.getWorldCenter().x * 32, bodyB.getWorldCenter().y * 32, bodyB.getWorldCenter().x * 32, bodyB.getWorldCenter().y * 32, main.getVertexBufferObjectManager());
		line.setLineWidth(3);
		line.setColor(Color.WHITE);

		DistanceJointDef djd = new DistanceJointDef();
		djd.initialize(bodyA, bodyB, bodyA.getWorldCenter(), bodyB.getWorldCenter());

		DistanceJoint dj = (DistanceJoint) this.mPhysicsWorld.createJoint(djd);
		PhysicsJointConnector pjc = new PhysicsJointConnector(line, dj);
		this.attachChild(pjc.getLine());
		activeJoints.add(pjc);
		jointManager.add(pjc);

	}

	public void pauseAndUnpauseGame() {
		if (main.getEngine().isRunning()) {
			main.getEngine().stop();
		} else {
			main.getEngine().start();
		}// yo
	}

}
