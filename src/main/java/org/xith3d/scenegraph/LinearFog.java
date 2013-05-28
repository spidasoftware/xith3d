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

import org.openmali.vecmath2.Colorf;

/**
 * LinearFog extends the Fog leaf node by adding a pair of
 * distance values, in Z, at which fog should start obscuring
 * the scene and should maximally obscure the scene.
 * 
 * @author David Yazel
 */
public class LinearFog extends Fog
{
    /**
     * The front distance for this object.
     */
    private float frontDistance = 0.1f;
    
    /**
     * The back distance for this object.
     */
    private float backDistance = 1f;
    
    /**
     * Sets the front distance for this object.
     */
    public final void setFrontDistance( float distance )
    {
        frontDistance = distance;
    }
    
    /**
     * Gets the front distance for this object.
     */
    public final float getFrontDistance()
    {
        return ( frontDistance );
    }
    
    /**
     * Sets the back distance for this object.
     */
    public final void setBackDistance( float distance )
    {
        backDistance = distance;
    }
    
    /**
     * Gets the back distance for this object.
     */
    public final float getBackDistance()
    {
        return ( backDistance );
    }
    
    /**
     * Constructs a new LinearFog object with a default color of black,
     * front distance of 0.1 and back distance of 1.
     */
    public LinearFog()
    {
        super();
    }
    
    /**
     * Constructs a new LinearFog object with the specified color,
     * front distance of 0.1 and back distance of 1.
     */
    public LinearFog( Colorf color )
    {
        super( color );
    }
    
    /**
     * Constructs a new LinearFog object with the specified color,
     * front distance and back distance.
     */
    public LinearFog( Colorf color, float front, float back )
    {
        super( color );
        
        frontDistance = front;
        backDistance = back;
    }
}
