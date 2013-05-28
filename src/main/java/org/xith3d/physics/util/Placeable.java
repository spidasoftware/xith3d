/**
 * Copyright (c) 2007-2008, JAGaToo Project Group all rights reserved.
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
package org.xith3d.physics.util;

import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Tuple3f;

/**
 * Placeable object : you can get/set Position and Rotation
 * 
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public interface Placeable
{
    /**
     * Sets the position of this Placeable object.
     */
    public void setPosition( float posX, float posY, float posZ );
    
    /**
     * Sets the position of this Placeable object.
     * 
     * @param pos
     */
    public void setPosition( Tuple3f pos );
    
    /**
     * @return The position of this Placeable object
     */
    public Tuple3f getPosition();
    
    /**
     * Writes the position of this Placeable object to the
     * given Tuple3f.
     * 
     * @param pos The Tuple3f to put the pos into
     */
    public void getPosition( Tuple3f pos );
    
    /**
     * Sets the rotation of this object, in Euler angles.
     * 
     * @param rotX The x-rotation of this object
     * @param rotY The y-rotation of this object
     * @param rotZ The z-rotation of this object
     */
    public void setRotation( float rotX, float rotY, float rotZ );
    
    /**
     * Sets the rotation of this object, in Euler angles
     * (degrees)
     * 
     * @param rot The rotation, in Euler angles (degrees) of
     * this Placeable object
     */
    public void setRotation( Tuple3f rot );
    
    /**
     * @return The rotation, in Euler angles (degrees) of
     * this Placeable object.
     */
    public Tuple3f getRotation();
    
    /**
     * Writes the rotation, in Euler angles (degrees) of
     * this Placeable object in the given Tuple3f.
     * 
     * @param rot
     */
    public void getRotation( Tuple3f rot );
    
    /**
     * Sets the rotation Matrix of this object.
     * 
     * @param rot The rotation, as a 3x3 rotation Matrix,
     * of this Placeable object
     */
    public void setRotationMatrix( Matrix3f rot );
    
    /**
     * @return The rotation, as a 3x3 rotation Matrix,
     * of this Placeable object.
     */
    public Matrix3f getRotationMatrix();
    
    /**
     * Writes the rotation, as a 3x3 rotation Matrix,
     * of this Placeable object in the given Matrix3f.
     * 
     * @param rot
     */
    public void getRotationMatrix( Matrix3f rot );
}
