package com.shilcomm;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.shape.IShape;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 18:51:22 - 05.07.2010
 */

// extends physics connector

public class PhysicsJointConnector implements IUpdateHandler, PhysicsConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// protected final IShape mShape;
	protected final Line mLine;
	// protected final Body mBody;
	protected final Joint mJoint;

	// protected final float mShapeHalfBaseWidth;
	// protected final float mShapeHalfBaseHeight;

	protected boolean mUpdatePosition;
	protected boolean mUpdateRotation;
	protected final float mPixelToMeterRatio;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PhysicsJointConnector(final Line line, final Joint joint) {
		this(line, joint, true, true);
	}

	public PhysicsJointConnector(final Line line, final Joint joint,
			final float pPixelToMeterRatio) {
		this(line, joint, true, true, pPixelToMeterRatio);
	}

	public PhysicsJointConnector(final Line line, final Joint joint,
			final boolean pUdatePosition, final boolean pUpdateRotation) {
		this(line, joint, pUdatePosition, pUpdateRotation,
				PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public PhysicsJointConnector(final Line line, final Joint joint,
			final boolean pUdatePosition, final boolean pUpdateRotation,
			final float pPixelToMeterRatio) {
		// super(line, mBody);
		this.mLine = line;
		this.mJoint = joint;

		this.mUpdatePosition = pUdatePosition;
		this.mUpdateRotation = pUpdateRotation;
		this.mPixelToMeterRatio = pPixelToMeterRatio;

		// this.mShapeHalfBaseWidth = line.getLineWidth()*0.5f;
		// this.mShapeHalfBaseHeight = line.
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public IShape getLine() {
		return this.mLine;
	}

	public Joint getJoint() {
		return this.mJoint;
	}

	public boolean isUpdatePosition() {
		return this.mUpdatePosition;
	}

	public boolean isUpdateRotation() {
		return this.mUpdateRotation;
	}

	public void setUpdatePosition(final boolean pUpdatePosition) {
		this.mUpdatePosition = pUpdatePosition;
	}

	public void setUpdateRotation(final boolean pUpdateRotation) {
		this.mUpdateRotation = pUpdateRotation;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onUpdate(final float pSecondsElapsed) {
		// final IShape shape = this.mShape;
		// final Body body = this.mBody;

		Vector2 A = this.mJoint.getAnchorA();
		Vector2 B = this.mJoint.getAnchorB();

//		if (this.mUpdatePosition) {
//			 final Vector2 position = body.getPosition();
//			 final float pixelToMeterRatio = this.mPixelToMeterRatio;
//			 shape.setPosition(position.x * pixelToMeterRatio -
//			 this.mShapeHalfBaseWidth, position.y * pixelToMeterRatio -
//			 this.mShapeHalfBaseHeight);
//		}
//
//		if (this.mUpdateRotation) {
//			 final float angle = body.getAngle();
//			 shape.setRotation(MathUtils.radToDeg(angle));
//		}
		this.mLine.setPosition(A.x * this.mPixelToMeterRatio, A.y
				* this.mPixelToMeterRatio, B.x * this.mPixelToMeterRatio, B.y
				* this.mPixelToMeterRatio);
	}

	@Override
	public void reset() {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
