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
 * ExponentialFog extends the Fog leaf node by adding a fog density.
 * 
 * @author David Yazel
 */
public class ExponentialFog extends Fog
{
    /**
     * Chooses one of the available FogModes.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static enum FogMode
    {
        /**
         * Basic rendered fog which fogs out all of the screen.<br>
         * It doesn't give much of a fog effect,
         * but gets the job done on older PCs.
         */
        EXP,
        
        /**
         * Is the next step up from GL_EXP.<br>
         * This will fog out all of the screen,
         * however it will give more depth to the scene.
         */
        EXP2;
    }
    
    /**
     * @see FogMode#EXP
     */
    public static final FogMode FOG_MODE_EXP = FogMode.EXP;
    
    /**
     * @see FogMode#EXP2
     */
    public static final FogMode FOG_MODE_EXP2 = FogMode.EXP2;
    
    private FogMode fogMode = FogMode.EXP;
    
    /**
     * The density for this object.
     */
    private float density = 1f;
    
    /**
     * Sets the FogMode for this Fog.
     * 
     * @param fogMode
     */
    public void setFogMode( FogMode fogMode )
    {
        this.fogMode = fogMode;
    }
    
    /**
     * @return the FogMode for this Fog.
     */
    public final FogMode getFogMode()
    {
        return ( fogMode );
    }
    
    /**
     * Sets the density for this object.
     */
    public final void setDensity( float density )
    {
        this.density = density;
    }
    
    /**
     * Gets the density for this object.
     */
    public final float getDensity()
    {
        return ( density );
    }
    
    /**
     * Constructs a new ExponentialFog object with a default color of black
     * and density of 1.0.
     */
    public ExponentialFog()
    {
        super();
    }
    
    /**
     * Constructs a new ExponentialFog object with the specified color and density of 1.0 and FogMode.EXP.
     */
    public ExponentialFog( Colorf color )
    {
        super( color );
    }
    
    /**
     * Constructs a new ExponentialFog object with the specified color and density and FogMode.EXP.
     */
    public ExponentialFog( Colorf color, float density )
    {
        this( color );
        
        this.density = density;
    }
    
    /**
     * Constructs a new ExponentialFog object with the specified color and density of 1.0.
     */
    public ExponentialFog( Colorf color, FogMode fogMode )
    {
        this( color );
        
        this.fogMode = fogMode;
    }
    
    /**
     * Constructs a new ExponentialFog object with the specified color and density.
     */
    public ExponentialFog( Colorf color, float density, FogMode fogMode )
    {
        this( color, density );
        
        this.fogMode = fogMode;
    }
}
