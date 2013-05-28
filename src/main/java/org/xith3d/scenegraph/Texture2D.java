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

import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.jagatoo.opengl.enums.TextureType;
import org.jagatoo.util.image.ImageUtility;

/**
 * Texture2D defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class Texture2D extends Texture
{
    private final boolean isDrawTexture;
    
    private boolean hasUpdateList = false;
    
    private Texture2DCanvas textureCanvas = null;
    private boolean hasTextureCanvas = false;
    
    /**
     * Was created as draw texture?
     * 
     * @return Was created as draw texture?
     */
    public final boolean isDrawTexture()
    {
        return ( isDrawTexture );
    }
    
    /**
     * This method must be called after the {@link TextureImage2D#update(java.awt.Rectangle)}
     * method to make the renderer apply the update list to the graphics card.
     * 
     * @param b
     */
    public void setHasUpdateList( boolean b )
    {
        this.hasUpdateList = b;
    }
    
    public final boolean hasUpdateList()
    {
        return ( hasUpdateList );
    }
    
    /**
     * @return the first
     */
    public final TextureImage2D getImage0()
    {
        if ( getImagesCount() == 0 )
            return ( null );
        
        return ( (TextureImage2D)super.getImage( 0 ) );
    }
    
    public final boolean hasTextureCanvas()
    {
        return ( hasTextureCanvas );
    }
    
    public Texture2DCanvas getTextureCanvas()
    {
        if ( textureCanvas == null )
        {
            TextureImage2D ti0 = (TextureImage2D)getImage( 0 );
            
            textureCanvas = new Texture2DCanvas( this, ti0, getOriginalWidth(), getOriginalHeight() );
            hasTextureCanvas = true;
        }
        
        return ( textureCanvas );
    }
    
    @Override
    public void setSizeChanged()
    {
        super.setSizeChanged();
        
        if ( ( getImagesCount() > 0 ) && hasTextureCanvas )
        {
            TextureImage2D ti0 = getImage0();
            
            ti0.fixUpdateListAfterSizeChange();
            textureCanvas.notifyImagesizeChanged( ti0.getOriginalWidth(), ti0.getOriginalHeight(), ti0.createGraphics2D() );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkImageType( TextureImage image )
    {
        if ( !( image instanceof TextureImage2D ) )
        {
            throw new Error( "Only TextureImage2D instances can be added to a Texture2D." );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        Texture2D orgTex = (Texture2D)original;
        
        this.hasUpdateList = orgTex.hasUpdateList;
        this.textureCanvas = orgTex.textureCanvas;
        this.hasTextureCanvas = orgTex.hasTextureCanvas;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Texture2D cloneNodeComponent( boolean forceDuplicate )
    {
        Texture2D t2d = new Texture2D( isDrawTexture(), this.getFormat() );
        
        t2d.duplicateNodeComponent( this, forceDuplicate );
        
        return ( t2d );
    }
    
    private Texture2D( boolean isDrawTexture, TextureFormat format )
    {
        super( TextureType.TEXTURE_2D, format );
        
        this.isDrawTexture = isDrawTexture;
    }
    
    private Texture2D( boolean isDrawTexture, TextureFormat format, int boundaryWidth )
    {
        super( TextureType.TEXTURE_2D, format, boundaryWidth );
        
        this.isDrawTexture = isDrawTexture;
    }
    
    /**
     * Constructs a new Texture2D object.
     * 
     * @param format
     */
    public Texture2D( TextureFormat format )
    {
        this( false, format );
    }
    
    /**
     * Constructs a new Texture2D object.
     * 
     * @param format
     * @param boundaryWidth
     */
    public Texture2D( TextureFormat format, int boundaryWidth )
    {
        this( false, format, boundaryWidth );
    }
    
    public static Texture2D createDrawTexture( TextureFormat format, int width, int height, boolean strechToPowerOfTwo, boolean useByteBuffer, boolean yUp )
    {
        int orgWidth = width;
        int orgHeight = height;
        
        if ( strechToPowerOfTwo )
        {
            width = ImageUtility.roundUpPower2( width );
            height = ImageUtility.roundUpPower2( height );
        }
        
        switch ( format )
        {
            case RGBA:
                //bi = DirectBufferedImage.makeDirectImageRGBA( width, height );
                break;
            case RGB:
                //bi = DirectBufferedImage.makeDirectImageRGB( width, height );
                break;
            case DEPTH:
                //bi = DirectBufferedImage.getDirectImageGrey( width, height );
                throw new Error( "Unsupported format " + format );
            case LUMINANCE:
            default:
                //bi = DirectBufferedImage.getDirectImageGrey( width, height );
                throw new Error( "Unsupported format " + format );
        }
        
        Texture2D tex = new Texture2D( true, format );
        
        final TextureImageFormat tiFormat = format.getDefaultTextureImageFormat();
        
        TextureImage2D ti = new TextureImage2D( tiFormat, width, height, orgWidth, orgHeight, yUp );
        ti.setReadOnly( false );
        ti.setImageData( null, width * height * ti.getPixelSize(), useByteBuffer );
        
        tex.setImage( 0, ti );
        
        return ( tex );
    }
    
    public static Texture2D createDrawTexture( TextureFormat format, int width, int height, boolean useByteBuffer, boolean yUp )
    {
        return ( createDrawTexture( format, width, height, true, useByteBuffer, yUp ) );
    }
    
    public static Texture2D createDrawTexture( TextureFormat format, int width, int height, boolean useByteBuffer )
    {
        return ( createDrawTexture( format, width, height, useByteBuffer, false ) );
    }
    
    public static Texture2D createDrawTexture( TextureFormat format, int width, int height )
    {
        return ( createDrawTexture( format, width, height, false, false ) );
    }
    
    public static Texture2D createOfflineDrawTexture( TextureFormat format, int width, int height, boolean useByteBuffer, boolean yUp )
    {
        return ( createDrawTexture( format, width, height, false, useByteBuffer, yUp ) );
    }
    
    public static Texture2D createOfflineDrawTexture( TextureFormat format, int width, int height, boolean useByteBuffer )
    {
        return ( createOfflineDrawTexture( format, width, height, useByteBuffer, false ) );
    }
    
    public static Texture2D createOfflineDrawTexture( TextureFormat format, int width, int height )
    {
        return ( createOfflineDrawTexture( format, width, height, false, false ) );
    }
}
