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

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;

/**
 * Transformable specifies a single spatial transformation,
 * via a Transform3D object, that can be positioned, oriented and scaled.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface Transformable
{
    /**
     * Sets the transform for this object.
     */
    void setTransform( Transform3D t );
    
    /**
     * Gets the transform for this object.
     */
    Transform3D getTransform();
    
    /**
     * Gets the transform for this object.
     */
    void getTransform( Transform3D t );
    
    /**
     * Repositions the TransformNode
     * 
     * @param posX the new x-position
     * @param posY the new y-position
     * @param posZ the new z-position
     */
    void setPosition( float posX, float posY, float posZ );
    
    /**
     * Repositions the TransformNode
     * 
     * @param position the new position
     */
    void setPosition( Tuple3f position );
    
    /**
     * Retrieves the View's position from its Transform3D and writes it into position.
     * 
     * @param position the tuple to write the positional data into.
     */
    void getPosition( Tuple3f position );
    
    /**
     * Retrieves and returns the View's position from its Transform3D.
     */
    Point3f getPosition();
    
    /**
     * @return this Node's name
     */
    String getName();
    
    /**
     * Sets this Node's name
     */
    void setName( String name );
}
