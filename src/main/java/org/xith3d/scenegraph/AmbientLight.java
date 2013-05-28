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
 * AmbientLight defines an ambient light source.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class AmbientLight extends Light
{
    /**
     * Constructs a new AmbientLight object with the specified color.
     * 
     * @param enabled if true, the light will be enabled, else it will be disabled.
     * @param color the ambient light color
     */
    public AmbientLight( boolean enabled, Colorf color )
    {
        super( enabled, getCol( color ).getRed(), getCol( color ).getGreen(), getCol( color ).getBlue() );
    }
    
    /**
     * Constructs a new AmbientLight object with the specified color.
     * 
     * @param color The ambient light color
     */
    public AmbientLight( Colorf color )
    {
        this( true, color );
    }
    
    /**
     * Constructs a new AmbientLight object with a default color of white.
     * 
     * @param enabled
     */
    public AmbientLight( boolean enabled )
    {
        super( enabled );
    }
    
    /**
     * Constructs a new AmbientLight object with a default color of white.
     */
    public AmbientLight()
    {
        this( true );
    }
}
