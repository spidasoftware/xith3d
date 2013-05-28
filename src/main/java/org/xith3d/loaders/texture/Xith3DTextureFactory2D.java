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
package org.xith3d.loaders.texture;

import org.jagatoo.loaders.textures.TextureFactory;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.jagatoo.opengl.enums.TextureImageInternalFormat;
import org.jagatoo.opengl.enums.TextureType;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture3D;

/**
 * Xith3D implementation of {@link TextureFactory} for 2D-textures.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Xith3DTextureFactory2D extends TextureFactory
{
    private static final Xith3DTextureFactory2D instance = new Xith3DTextureFactory2D();
    
    public static final Xith3DTextureFactory2D getInstance()
    {
        return ( instance );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TextureImage2D createTextureImageImpl( int width, int height, int orgWidth, int orgHeight, int pixelSize, int dataSize, TextureImageInternalFormat internalFormat, TextureImageFormat format )
    {
        TextureImage2D ic = new TextureImage2D( format, width, height, orgWidth, orgHeight, false );
        
        //ic.initImageData( true );
        ic.setImageData( null, dataSize );
        
        return ( ic );
    }
    
    @Override
    protected Texture createTextureImpl( TextureType type, TextureFormat format )
    {
        switch ( type )
        {
            case TEXTURE_1D:
                throw new Error( "1D-textures are not yet implemented." );
            case TEXTURE_2D:
                return ( new Texture2D( format ) );
            case TEXTURE_3D:
                return ( new Texture3D( format ) );
            case TEXTURE_CUBE_MAP:
                throw new Error( "CubeMaps can not yet be created by this factory." );
        }
        
        throw new Error( "Unsupported type " + type );
    }
    
    protected Xith3DTextureFactory2D()
    {
    }
}
