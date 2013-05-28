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
package org.xith3d.terrain;

import java.io.IOException;
import java.net.URL;

import org.jagatoo.opengl.enums.TextureMode;

/**
 * @author Mathias 'cylab' Henze
 */
public class SimpleGridResourceProvider implements GridResourceProvider
{
    private GridSampler sampler;
    private GridSurface surface;

    public SimpleGridResourceProvider( GridSampler sampler, GridSurface surface )
    {
        this.sampler = sampler;
        this.surface = surface;
    }

    public SimpleGridResourceProvider( URL heightMapLocation, URL textureLocation ) throws IOException
    {
        this(new HeightMapSampler( heightMapLocation ), new TextureSurface( textureLocation ));
    }

    public SimpleGridResourceProvider( URL heightMapLocation, int xDim, int yDim, HeightMapSampler.Type type, URL textureLocation ) throws IOException
    {
        this(new HeightMapSampler( heightMapLocation, xDim, yDim, type ), new TextureSurface( textureLocation ));
    }

    public SimpleGridResourceProvider( URL heightMapLocation, URL textureLocation, URL detailTextureLocation, float detailRepeat, TextureMode detailBlendMode ) throws IOException
    {
        this( new HeightMapSampler( heightMapLocation ), new TextureSurface( textureLocation, detailTextureLocation, detailRepeat, detailBlendMode ));
    }

    public SimpleGridResourceProvider( URL heightMapLocation, int xDim, int yDim, HeightMapSampler.Type type, URL textureLocation, URL detailTextureLocation, float detailRepeat, TextureMode detailBlendMode ) throws IOException
    {
        this( new HeightMapSampler( heightMapLocation, xDim, yDim, type ), new TextureSurface( textureLocation, detailTextureLocation, detailRepeat, detailBlendMode ));
    }

    public GridSampler findSampler( float s1, float t1, float s2, float t2, int detail )
    {
        return sampler;
    }

    public GridSurface findSurface( float s1, float t1, float s2, float t2, int detail )
    {
        return surface;
    }

    public void releaseSampler( GridSampler sampler )
    {
    }

    public void releaseSurface( GridSurface surface )
    {
    }
    
}
