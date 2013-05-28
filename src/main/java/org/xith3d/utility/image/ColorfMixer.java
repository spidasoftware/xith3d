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
package org.xith3d.utility.image;

import org.openmali.vecmath2.Colorf;

/**
 * Mixes colors.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class ColorfMixer
{
    /**
     * Mixes two colors.
     * 
     * @param c1 First color
     * @param c2 Second color
     * @param ratio If 0, only the first color, if 1 only the second color
     * 
     * @return The mixed color
     */
    public static Colorf mix( Colorf c1, Colorf c2, float ratio )
    {
        if ( ratio < 0f )
        {
            ratio = 0f;
        }
        else if ( ratio > 1f )
        {
            ratio = 1f;
        }
        
        final float ratio2 = 1f - ratio;
        
        Colorf mixed = new Colorf();
        
        mixed.setRed( c1.getRed() * ratio2 + c2.getRed() * ratio );
        mixed.setGreen( c1.getGreen() * ratio2 + c2.getGreen() * ratio );
        mixed.setBlue( c1.getBlue() * ratio2 + c2.getBlue() * ratio );
        if ( c1.hasAlpha() || c2.hasAlpha() )
            mixed.setAlpha( c1.getAlpha() * ratio2 + c2.getAlpha() * ratio );
        
        return ( mixed );
    }
}
