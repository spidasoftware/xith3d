/**
 * Copyright (c) 2003-2009, Xith3D Project Group all rights reserved.
 * 
 * Portions based on the Java3D interface, Copyright by Sun Microsystems.
 * Many thanks to the developers of Java3D and Sun Microsystems for their
 * innovation and design.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the 'Xith3D Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package org.xith3d.utility.camera;

import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.ScheduledOperation;
import org.xith3d.physics.util.Placeable;
import org.xith3d.scenegraph.View;

/**
 * A Camera which follows a Placeable object, so
 * that it is always at the center of the screen.
 * The height of the camera is adjustable, up
 * vector is always +Y
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class BasicPlaceableVerticalFocuser implements ScheduledOperation {

	private float height;
	private View view;
	private Placeable placeable;

	// For improvements of the class
	protected Vector3f move = null;
	
	public BasicPlaceableVerticalFocuser(OperationScheduler opSched, View view, Placeable placeable,
			float height) {

		this.height = height;
		this.view = view;
		this.placeable = placeable;
		opSched.scheduleOperation(this);
		
	}

	public void update(long gameTime, long frameTime, TimingMode timingMode) {
		
		Tuple3f pos = getFocusPoint();
		
		lookAt(pos);
		
	}

	protected void lookAt(Tuple3f pos) {
		view.lookAt(new Vector3f(pos.getX(), pos.getY(), height),
        		new Vector3f(pos.getX(), pos.getY(), 0f),
        		new Vector3f(0f, 1f, 0f));
	}

	protected Tuple3f getFocusPoint() {
		return placeable.getPosition();
	}

	public boolean isAlive() {
		return true;
	}

	public boolean isPersistent() {
		return true;
	}

	public void setAlive(boolean alive) {
		// Wazaa ?
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}	
	
}
