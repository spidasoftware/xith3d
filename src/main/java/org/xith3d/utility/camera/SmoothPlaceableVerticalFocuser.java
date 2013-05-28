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

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.physics.util.Placeable;
import org.xith3d.scenegraph.View;

/**
 * Same as BasicPlaceableVerticalFocuser, but smooth
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class SmoothPlaceableVerticalFocuser extends
		BasicPlaceableVerticalFocuser {

	private Vector3f realPos;
	private float alpha;
	private Tuple3f offset = null;

	/**
	 * Create a new SmoothPlaceableVerticalFocuser
	 * 
	 * @param opSched
	 *            An op-scheduler on which to register
	 * @param view
	 *            The view on which to act
	 * @param placeable
	 *            The placeable to focus
	 * @param height
	 *            Height
	 * @param alpha
	 *            Interpolation Speed, between 0 and 1
	 */
	public SmoothPlaceableVerticalFocuser(OperationScheduler opSched,
			View view, Placeable placeable, float height, float alpha) {

		super(opSched, view, placeable, height);

		this.alpha = alpha;

	}
	
	/**
	 * Set a shifter for this focuser.
	 * If non-null, the shifter will modify the position
	 * of the camera once it's interpolated, so that you can
	 * have variety of views..
	 * @param shifter
	 */
	public void setOffset(Tuple3f offset) {
		
		this.offset = offset;
		
	}

	@Override
    public void update(long gameTime, long frameTime, TimingMode timingMode) {

		if (realPos == null) {
			realPos = new Vector3f();
			realPos.set(getFocusPoint());
		}

		Tuple3f pos = getFocusPoint();

		realPos.interpolate(pos, alpha);
		
		if (offset != null) {
		    Point3f tmp = Point3f.fromPool();
		    tmp.add(realPos, offset);
            lookAt(tmp);
		    Point3f.toPool(tmp);
		} else {
            lookAt(realPos);
		}

	}

}
