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
package org.xith3d.utility.math;

import org.openmali.FastMath;

/**
 * Bunch of functions for generating noise in different forms.
 * 
 * @author David Yazel
 */
public final class NoiseFactory
{
    private NoiseFactory()
    {
    }
    
    /**
     * Fairly bad noise function.  But it is predictable and quick.  Good for testing.
     */
    public static int intNoise( int x )
    {
        x = ( x << 13 ) ^ x;
        
        return ( 1 - ( ( ( ( x * ( ( x * x * 15731 ) + 789221 ) ) + 1376312589 ) & 0x7fffffff ) / 1073741824 ) );
    }
    
    /**
     * linear interpolation between two values.
     * 
     * @param a start point
     * @param b end point
     * @param x value between 0 and 1 representing the distance from a to b you want a new point
     */
    public static float linearInterpolate( float a, float b, float x )
    {
        return ( a * ( 1 - x ) ) + ( b * x );
    }
    
    /**
     * smother interpolation between two values.
     * 
     * @param a start point
     * @param b end point
     * @param x value between 0 and 1 representing the distance from a to b you want a new point
     */
    public static float cosineInterpolate( float a, float b, float x )
    {
        float ft = x * FastMath.PI;
        float f = ( 1 - FastMath.cos( ft ) ) * 0.5f;
        
        return ( a * ( 1 - f ) ) + ( b * f );
    }
}
