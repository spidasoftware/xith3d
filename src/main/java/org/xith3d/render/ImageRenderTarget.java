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
package org.xith3d.render;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.jagatoo.util.nio.BufferUtils;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.GroupNode;

/**
 * This type of RenderTarget is used to render (copy) the scene
 * to a BufferedImage.
 * Apply it to a RenderPass to bring it into effect.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ImageRenderTarget implements RenderTarget
{
    private GroupNode group;
    private ByteBuffer byteBuffer;
    private BufferedImage image;
    private Colorf backgroundColor = null;
    private boolean backgroundRenderingEnabled = false;
    
    /**
     * Sets the Group to be rendered to this RenderTarget.
     */
    public final void setGroup( GroupNode group )
    {
        if ( group == null )
            throw new NullPointerException( "group must not be null" );
        
        this.group = group;
    }
    
    /**
     * {@inheritDoc}
     */
    public final GroupNode getGroup()
    {
        return ( group );
    }
    
    /**
     * Sets the assotiated Texture instance.
     * 
     * @param image
     */
    public final void setImage( BufferedImage image )
    {
        if ( image == null )
            throw new NullPointerException( "image must not be null" );
        
        this.image = image;
        this.byteBuffer = BufferUtils.createByteBuffer(  image.getWidth() * image.getHeight() * 4  );
    }
    
    /**
     * @return the assotiated BufferedImage instance.
     */
    public final BufferedImage getImage()
    {
        return ( image );
    }
    
    public final ByteBuffer getByteBuffer()
    {
        return ( byteBuffer );
    }
    
    /**
     * Sets the color, the texture is to be cleared before the Renderer renders to it.
     * Set this to <code>null</code> to do no clearing.
     */
    public final void setBackgroundColor( Colorf color )
    {
        this.backgroundColor = color;
    }
    
    /**
     * @return the color, the texture is to be cleared before the Renderer renders to it.
     * This is <code>null</code> to do no clearing.
     */
    public final Colorf getBackgroundColor()
    {
        return ( backgroundColor );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBackgroundRenderingEnabled( boolean enabled )
    {
        this.backgroundRenderingEnabled = enabled;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isBackgroundRenderingEnabled()
    {
        return ( backgroundRenderingEnabled );
    }
    
    public final void copyBufferToImage()
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        
        // Convert RGB bytes to ARGB ints with no transparency. Flip 
        // image vertically by reading the rows of pixels in the byte 
        // buffer in reverse - (0,0) is at bottom left in OpenGL.
        
        final int bytesPerRow = width * 4; // Number of bytes in each row
        int p = width * height * 4; // Points to first byte (red) in each row.
        int q; // Index into ByteBuffer
        
        for ( int row = 0; row < height; row++ )
        {
            p = row * bytesPerRow;
            q = p;
            for ( int col = 0; col < width; col++ )
            {
                int iR = byteBuffer.get( q++ );
                int iG = byteBuffer.get( q++ );
                int iB = byteBuffer.get( q++ );
                int iA = byteBuffer.get( q++ );
                
                int pixelInt = ( ( 0xFF000000 ) | ( ( iA & 0xFF ) << 24 ) | ( ( iR & 0xFF ) << 16 ) | ( ( iG & 0xFF ) << 8 ) | ( iB & 0xFF ) );
                
                image.setRGB( col, height - row - 1, pixelInt );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public final void freeOpenGLResources( Canvas3D canvas )
    {
        /*
        if ( canvas.getPeer() == null )
            throw new Error( "The given Canvas3D is not linked to a CanvasPeer." ) );
        
        freeOpenGLResources( canvas.getPeer() );
        */
    }
    
    public ImageRenderTarget( GroupNode group, Colorf backgroundColor )
    {
        if ( group == null )
            throw new NullPointerException( "group must not be null" );
        
        /*
        if ( image == null )
            throw new NullPointerException( "image must not be null" ) );
        */
        
        this.group = group;
        //setImage( image );
        this.backgroundColor = backgroundColor;
    }
    
    public ImageRenderTarget( GroupNode group )
    {
        this( group, null );
    }
}
