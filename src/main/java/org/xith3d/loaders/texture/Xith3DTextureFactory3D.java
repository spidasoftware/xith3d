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

import java.nio.ByteBuffer;

import org.jagatoo.loaders.textures.TextureFactory;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.jagatoo.opengl.enums.TextureImageInternalFormat;
import org.jagatoo.opengl.enums.TextureType;
import org.xith3d.scenegraph.Texture3D;
import org.xith3d.scenegraph.TextureImage3D;

/**
 * Xith3D implementation of {@link TextureFactory} for 3D-textures.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Xith3DTextureFactory3D extends TextureFactory
{
    private final int depth;
    
    private TextureImage3D textureImage = null;
    
    private int imageCount = 0;
    
    public void skipOneImage()
    {
        imageCount++;
        
        if ( textureImage != null )
        {
            ByteBuffer bb = textureImage.getDataBuffer();
            bb.limit( bb.capacity() );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TextureImage3D createTextureImageImpl( int width, int height, int orgWidth, int orgHeight, int pixelSize, int dataSize, TextureImageInternalFormat internalFormat, TextureImageFormat format )
    {
        if ( textureImage == null )
        {
            this.textureImage = new TextureImage3D( format, width, height, depth );
        }
        else
        {
            if ( ( width != textureImage.getWidth() ) || ( width != textureImage.getWidth() ) )
            {
                throw new Error( "All images must have the same size." );
            }
        }
        
        int imageSize = textureImage.initImageData();
        ByteBuffer bb = textureImage.getDataBuffer();
        
        bb.position( imageSize * imageCount );
        bb.limit( bb.capacity() );
        
        imageCount++;
        
        return ( textureImage );
    }
    
    @Override
    protected Texture3D createTextureImpl( TextureType type, TextureFormat format )
    {
        return ( new Texture3D( format ) );
    }
    
    public Xith3DTextureFactory3D( int depth )
    {
        this.depth = depth;
    }
}
