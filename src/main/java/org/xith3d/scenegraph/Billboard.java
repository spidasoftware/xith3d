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
package org.xith3d.scenegraph;

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Matrix3f;

/**
 * A BillBoard is someting, that always faces the View.
 * Implement this interface in Shape3D extensions to make the renderer use it.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface Billboard
{
    /**
     * Returns the Billboard desired on screen dimensions. 
     * 
     * For the default Billboard implementation,
     * this method should return {@code null}, as the on-screen dimension is dependant on the 
     * distance between the Billboard and the camera.
     * 
     * If the desired effect is a Billboard having a constant size on screen (usefull for icons, 
     * or textual information that should be readable at any distance), then this method should
     * return the desired size in pixels. In this case, the {@code Sized2iRO} returned will
     * generally be a constant.
     * 
     * This method is called by the renderer each frame the Billboard is rendered.
     * 
     * Note to implementers: for this behaviour to work properly, the unmodified geometry 
     * should of course have a unit size, ie: the width and the height should should be 
     * equal to 1f. Else the effective desired size on screen will be scaled accordingly.
     * 
     * @return the desired size on screen, or {@code null} for a standard billboard.
     */
    public Sized2iRO getSizeOnScreen();
    
    /**
     * This method is called by the renderer each frame, the BillBoard is
     * rendered.
     * 
     * @param viewRotation the camera's rotation
     * @param frameId the current rendered frame's id
     * @param nanoTime
     * @param nanoStep
     */
    public void updateFaceToCamera( Matrix3f viewRotation, long frameId, long nanoTime, long nanoStep );
}
