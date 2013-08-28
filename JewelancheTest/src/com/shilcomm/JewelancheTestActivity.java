package com.shilcomm;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
//import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.ui.activity.BaseGameActivity;
//import org.andengine.util.color.Color;
import org.andengine.util.adt.color.*;

import android.graphics.Point;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;

public class JewelancheTestActivity extends BaseGameActivity implements
		IOnMenuItemClickListener, IAccelerationListener {

	static int CAMERA_WIDTH = 500;
	static int CAMERA_HEIGHT = 400;

	protected Camera mCamera;
	protected Scene mMainScene;
	protected MenuScene mMenuScene;
	protected Level1 lvl1;

	protected static final int MENU_PLAY = 1;
	protected static final int MENU_QUIT = 2;

	protected Font mFont;
	BitmapTextureAtlas mFontTexture;

	void returnToMenu() {
		// lvl1.mPhysicsWorld.dispose();
		mMainScene.detachChildren();
		lvl1 = null;
		this.createMenuScene();
		// mMainScene = new Scene();
	
		// mMainScene.d
		
		mMainScene.setChildScene(mMenuScene, false, true, true);
		// this.mEngine.unregisterUpdateHandler(lvl1.tm);
	
		
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		Display disp = getWindowManager().getDefaultDisplay();
		Point outSize = new Point();
		disp.getSize(outSize);
		CAMERA_WIDTH = (int) (outSize.x / 2.1);
		CAMERA_HEIGHT = (int) (outSize.y / 2.1);
		// CAMERA_WIDTH = 480;
		// CAMERA_HEIGHT = 800;
		// TODO Auto-generated method stub
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engOp = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		// engOp.getTouchOptions().setNeedsMultiTouch(true);

		return engOp;

	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_MENU
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			pauseAndUnpauseGame();
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	public void pauseAndUnpauseGame() {
		if (lvl1 != null) {
			if (lvl1.isIgnoreUpdate()) {
				lvl1.setIgnoreUpdate(false); // unpause
				this.mEngine.start();
			} else {
				lvl1.setIgnoreUpdate(true); // pause
				this.mEngine.stop();
			}
		}
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		// TODO Auto-generated method stub

		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.mFont = new Font(this.getFontManager(), this.mFontTexture,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true,
				Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.getFontManager().loadFont(this.mFont);

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		// TODO Auto-generated method stub

		this.mMainScene = new Scene();

		pOnCreateSceneCallback.onCreateSceneFinished(mMainScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
		// TODO Auto-generated method stub
		this.createMenuScene();
		mMainScene.setChildScene(mMenuScene, false, true, true);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO Auto-generated method stub

		if (pMenuItem.getID() == MENU_PLAY) {
			Log.w("sahil", "CLICKED");
			lvl1 = new Level1(this);
			lvl1.loadResources();
			mMainScene.setChildScene(lvl1);
			return true;
		}
		return false;
	}

	protected void createMenuScene() {
		this.mMenuScene = new MenuScene(this.mCamera);
		final IMenuItem playMenuItem = new TextMenuItem(MENU_PLAY, mFont,
				"Play Game", this.getVertexBufferObjectManager());
		playMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(playMenuItem);
		this.mMenuScene.buildAnimations();
		this.mMenuScene.setBackgroundEnabled(true);
		this.mMenuScene.setOnMenuItemClickListener(this);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
		pauseAndUnpauseGame();
		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		pauseAndUnpauseGame();
		this.disableAccelerationSensor();
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		// final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(),
		// pAccelerationData.getY());
		// if(lvl1!= null && lvl1.mPhysicsWorld!=null)
		// lvl1.mPhysicsWorld.setGravity(gravity);
		// Vector2Pool.recycle(gravity);
	}

	public void registerFPSLogger() {
		// TODO Auto-generated method stub
		mEngine.registerUpdateHandler(new FPSLogger());
	}

	public void loadFont(Font myFont) {
		// TODO Auto-generated method stub
		mEngine.getTextureManager().loadTexture(mFontTexture);
		getFontManager().loadFont(myFont);
	}
}