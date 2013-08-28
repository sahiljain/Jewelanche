package com.shilcomm;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;


import com.badlogic.gdx.physics.box2d.Joint;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 15:52:27 - 15.07.2010
 */
public class PhysicsJointConnectorManager extends ArrayList<PhysicsJointConnector> implements IUpdateHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8006772092628639834L;

	// ===========================================================
	// Constants
	// ===========================================================

	//private static final long serialVersionUID = 412969510084261799L;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	PhysicsJointConnectorManager() {

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onUpdate(final float pSecondsElapsed) {
		final ArrayList<PhysicsJointConnector> physicsJointConnectors = this;
		for (int i = physicsJointConnectors.size() - 1; i >= 0; i--) {
			physicsJointConnectors.get(i).onUpdate(pSecondsElapsed);
		}
	}

	@Override
	public void reset() {
		final ArrayList<PhysicsJointConnector> physicsJointConnectors = this;
		for (int i = physicsJointConnectors.size() - 1; i >= 0; i--) {
			physicsJointConnectors.get(i).reset();
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public Joint findJointByLine(final Line line) {
		final ArrayList<PhysicsJointConnector> physicsJointConnectors = this;
		for (int i = physicsJointConnectors.size() - 1; i >= 0; i--) {
			final PhysicsJointConnector physicsJointConnector = physicsJointConnectors.get(i);
			if (physicsJointConnector.getLine() == line) {
				return physicsJointConnector.getJoint();
			}
		}
		return null;
	}

	public PhysicsJointConnector findPhysicsJointConnectorByLine(final Line line) {
		final ArrayList<PhysicsJointConnector> physicsJointConnectors = this;
		for (int i = physicsJointConnectors.size() - 1; i >= 0; i--) {
			final PhysicsJointConnector physicsJointConnector = physicsJointConnectors.get(i);
			if (physicsJointConnector.getLine() == line) {
				return physicsJointConnector;
			}
		}
		return null;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
