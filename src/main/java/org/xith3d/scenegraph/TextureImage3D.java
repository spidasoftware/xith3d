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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.jagatoo.loaders.textures.pixelprocessing.PixelProcessor;
import org.jagatoo.opengl.enums.TextureImageFormat;

/**
 * {@link TextureImage3D} is the 3D implementation to {@link TextureImage}
 * (used in 3D texturing, see Texture3D(Coord)Test)
 * 
 * @author David Yazel
 */
public class TextureImage3D extends TextureImage
{
    /**
     * The desired depth.
     */
    private final int depth;
    
    /**
     * The byte buffers for the image data.
     */
    private ByteBuffer dataBuffer = null;
    
    /**
     * gets the depth
     */
    public final int getDepth()
    {
        return ( depth );
    }
    
    /**
     * {@inheritDoc}
     */
    public final ByteBuffer getDataBuffer()
    {
        return ( dataBuffer );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPixelSize()
    {
        return ( getDataBuffer().limit() / ( getWidth() * getHeight() * getDepth() ) );
    }
    
    public int initImageData()
    {
        int imageSize = calculateNeededImageSize();
        
        if ( this.dataBuffer == null )
            this.dataBuffer = ByteBuffer.allocateDirect( imageSize * depth );
        
        return ( imageSize );
    }
    
    /**
     * Sets the data for the image
     */
    public final void setImageData( byte[][] data )
    {
        int imageSize = initImageData();
        
        for ( int loop = 0; loop < data.length; loop++ )
        {
            dataBuffer.put( data[ loop ], 0, imageSize );
        }
        
        dataBuffer.flip();
    }
    
    public void setImageData( BufferedImage[] images )
    {
        initImageData();
        
        PixelProcessor pp = PixelProcessor.selectPixelProcessor( this.getFormat() );
        
        int dataOffset = 0;
        for ( int i = 0; i < images.length; i++ )
        {
            dataOffset += pp.readImageData( images[ i ], 0, 0, getWidth(), getHeight(), dataBuffer, dataOffset, false );
        }
    }
    
    public void setImageData( TextureImage2D[] images )
    {
        initImageData();
        
        byte[] bytes = null;
        
        for ( int i = 0; i < images.length; i++ )
        {
            ByteBuffer srcBuffer = images[i].getDataBuffer();
            
            if ( srcBuffer == null )
            {
                if ( bytes == null )
                    bytes = new byte[ images[i].getDataSize() ];
                
                images[i].getData( bytes );
                
                dataBuffer.put( bytes );
            }
            else
            {
                int pos = srcBuffer.position();
                srcBuffer.position( 0 );
                dataBuffer.put( images[i].getDataBuffer() );
                srcBuffer.position( pos );
            }
        }
        
        dataBuffer.flip();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        //super.duplicateNodeComponent( original, forceDuplicate );
        throw new UnsupportedOperationException( "Not implemented yet" );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TextureImage3D cloneNodeComponent( boolean forceDuplicate )
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }
    
    /**
     * Constructs a new {@link TextureImage3D} object.
     */
    public TextureImage3D( TextureImageFormat format, int width, int height, int depth )
    {
        super( format, width, height, width, height );
        
        this.depth = depth;
    }
    
    /**
     * Constructs a new {@link TextureImage3D} object.
     */
    public TextureImage3D( TextureImageFormat format, int width, int height, int depth, byte[][] data )
    {
        this( format, width, height, depth );
        
        setImageData( data );
    }
    
    /**
     * Constructs a new {@link TextureImage3D} object.
     */
    public TextureImage3D( TextureImageFormat format, int width, int height, int depth, BufferedImage[] image )
    {
        this( format, width, height, depth );
        
        setImageData( image );
    }
    
    /**
     * Constructs a new {@link TextureImage3D} object.
     */
    public TextureImage3D( TextureImageFormat format, int width, int height, int depth, TextureImage2D[] image )
    {
        this( format, width, height, depth );
        
        setImageData( image );
    }
}
