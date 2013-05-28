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

import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Sized2iRO;
import org.jagatoo.loaders.textures.AbstractTextureImage;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.jagatoo.opengl.enums.TextureImageInternalFormat;
import org.xith3d.render.CanvasPeer;
import org.xith3d.utility.logging.X3DLog;

/**
 * {@link TextureImage} defines attributes that apply to one
 * mipmap-level of a {@link Texture}.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class TextureImage extends NodeComponent implements AbstractTextureImage
{
    /**
     * The image data format.
     */
    private final TextureImageFormat format;
    
    /**
     * The desired image format for the graphics card. This is only a hint.
     */
    private TextureImageInternalFormat internalFormat;
    
    /**
     * The image's size.
     */
    private final Dim2i size = new Dim2i();
    
    /**
     * The image's original size (before scaling to powers of two).
     */
    private final Dim2i orgSize = new Dim2i();
    
    private boolean hasData = true;
    
    protected void setHasData( boolean hasData )
    {
        this.hasData = hasData;
    }
    
    public final boolean hasData()
    {
        return ( hasData );
    }
    
    /**
     * Get the image format. This is the format in which the data is specified.
     */
    public final TextureImageFormat getFormat()
    {
        return ( format );
    }
    
    /**
     * @return true if the {@link TextureImage} contains explicit alpha data.
     * This does not count for compressed formats.
     */
    public final boolean hasAlpha()
    {
        return ( getFormat().hasAlpha() );
    }
    
    /**
     * @return true if the {@link TextureImage} contains compressed data.
     */
    public final boolean isCompressed()
    {
        return ( getFormat().isCompressed() );
    }
    
    /**
     * Sets the desired internal format. Setting a internal format that is incompatible
     * is no error, this method ensures that the internal format is always compatible
     * with the data format.
     * 
     * @param internalFormat
     */
    public final void setInternalFormat( TextureImageInternalFormat internalFormat )
    {
        switch ( format )
        {
            case INTENSITY:
                switch ( internalFormat )
                {
                    case INTENSITY:
                    case INTENSITY4:
                    case INTENSITY8:
                        this.internalFormat = internalFormat;
                        break;
                    
                    default:
                        this.internalFormat = TextureImageInternalFormat.INTENSITY;
                }
                break;
            
            case LUMINANCE:
                switch ( internalFormat )
                {
                    case LUMINANCE:
                    case LUMINANCE4:
                    case LUMINANCE8:
                        this.internalFormat = internalFormat;
                        break;
                    
                    default:
                        this.internalFormat = TextureImageInternalFormat.LUMINANCE;
                }
                break;
            
            case ALPHA:
                switch ( internalFormat )
                {
                    case ALPHA:
                    case ALPHA4:
                    case ALPHA8:
                        this.internalFormat = internalFormat;
                        break;
                    
                    default:
                        this.internalFormat = TextureImageInternalFormat.ALPHA;
                }
                break;
            
            case LUMINANCE_ALPHA:
                switch ( internalFormat )
                {
                    case LUM_ALPHA:
                    case LUM4_ALPHA4:
                    case LUM8_ALPHA8:
                        this.internalFormat = internalFormat;
                        break;
                    
                    default:
                        this.internalFormat = TextureImageInternalFormat.LUM_ALPHA;
                }
                break;
            
            case RGB:
                switch ( internalFormat )
                {
                    case RGB:
                    case RGB4:
                    case RGB5:
                    case RGB8:
                    case R3_G3_B2:
                    case RGB_DXT1:
                    case RGBA_DXT1:
                    case RGBA_DXT3:
                    case RGBA_DXT5:
                        this.internalFormat = internalFormat;
                        break;
                    
                    default:
                        this.internalFormat = TextureImageInternalFormat.RGB;
                }
                break;
            
            case RGBA:
                switch ( internalFormat )
                {
                    case RGBA:
                    case RGBA4:
                    case RGB5_A1:
                    case RGBA8:
                    case RGBA_DXT1:
                    case RGBA_DXT3:
                    case RGBA_DXT5:
                        this.internalFormat = internalFormat;
                        break;
                    
                    default:
                        this.internalFormat = TextureImageInternalFormat.RGBA;
                }
                break;
            
            case RGB_DXT1:
                this.internalFormat = TextureImageInternalFormat.RGB_DXT1;
                break;
            
            case RGBA_DXT1:
                this.internalFormat = TextureImageInternalFormat.RGBA_DXT1;
                break;
            
            case RGBA_DXT3:
                this.internalFormat = TextureImageInternalFormat.RGBA_DXT3;
                break;
            
            case RGBA_DXT5:
                this.internalFormat = TextureImageInternalFormat.RGBA_DXT5;
                break;
            
            case DEPTH:
                if ( internalFormat != null )
                {
                    switch ( internalFormat )
                    {
                        case DEPTH16:
                        case DEPTH24:
                        case DEPTH32:
                            this.internalFormat = internalFormat;
                            break;
                        
                        default:
                            this.internalFormat = TextureImageInternalFormat.DEPTH24;
                    }
                }
                else
                {
                    this.internalFormat = null;
                }
                break;
            
            default:
                throw new IllegalArgumentException( "Invalid data format: " + format );
        }
    }
    
    /**
     * Get the internal image format. This is a format hint for the graphic driver.
     */
    public final TextureImageInternalFormat getInternalFormat()
    {
        return ( internalFormat );
    }
    
    /**
     * @return true if the image should be stroed in compressed form on the graphics card.
     */
    public final boolean isInternalFormatCompressed()
    {
        return ( getInternalFormat().isCompressed() );
    }
    
    protected void setSize( int width, int height )
    {
        this.size.set( width, height );
    }
    
    /**
     * @return the image's size.
     */
    public final Sized2iRO getSize()
    {
        return ( size );
    }
    
    /**
     * @return the image's width.
     */
    public final int getWidth()
    {
        return ( size.getWidth() );
    }
    
    /**
     * @return the image's height.
     */
    public final int getHeight()
    {
        return ( size.getHeight() );
    }
    
    protected void setOriginalSize( int orgWidth, int orgHeight )
    {
        this.orgSize.set( orgWidth, orgHeight );
    }
    
    /**
     * @return the image's original size (before scaling to powers of two).
     */
    public Sized2iRO getOriginalSize()
    {
        return ( orgSize );
    }
    
    /**
     * @return the image's original width (before scaling to powers of two).
     */
    public int getOriginalWidth()
    {
        return ( orgSize.getWidth() );
    }
    
    /**
     * @return the image's original height (before scaling to powers of two).
     */
    public int getOriginalHeight()
    {
        return ( orgSize.getHeight() );
    }
    
    protected final int calculateNeededImageSize()
    {
        int sizeMultiplier = 0;
        
        switch ( format )
        {
            case RGB_DXT1:
            case RGBA_DXT1:
                // DXT1 has a block size of 8 and 4x4 pixel blocks
                return ( ( ( getWidth() + 3 ) / 4 ) * ( ( getHeight() + 3 ) / 4 ) * 8 );
                
            case RGBA_DXT3:
            case RGBA_DXT5:
                // DXT3 & DXT5 have a block sizes of 16 and 4x4 pixel blocks
                return ( ( ( getWidth() + 3 ) / 4 ) * ( ( getHeight() + 3 ) / 4 ) * 16 );
                
            case RGB:
                //each pixel contains three eight bit channels, one each for red, green and blue.
                sizeMultiplier = 3;
                break;
            
            case RGBA:
                // each pixel contains four eight bit channels, one each for red, green, blue and alpha.
                sizeMultiplier = 4;
                break;
            
            case LUMINANCE_ALPHA:
                // each pixel contains two eight bit channels, one each for luminance and alpha.
                sizeMultiplier = 2;
                break;
            
            case ALPHA:
            case INTENSITY:
            case LUMINANCE:
                // each pixel contains one eight bit channel, the channel can be luminance, alpha or intensity.
                sizeMultiplier = 1;
                break;
            
            case DEPTH:
                //each pixel contains 24 bit depth.
                sizeMultiplier = 3;
                break;
            
            default:
                X3DLog.exception( "TextureImage2D.setImage(): The image object contains a value, ", format, ", in the format that is unknown." );
                break;
        }
        
        return ( sizeMultiplier * getWidth() * getHeight() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    public TextureImage( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, TextureImageInternalFormat internalFormat )
    {
        super( false );
        
        this.format = format;
        this.size.set( width, height );
        this.orgSize.set( orgWidth, orgHeight );
        setInternalFormat( internalFormat );
    }
    
    /**
     * Constructs a new {@link TextureImage} object.
     */
    public TextureImage( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight )
    {
        // just a dummy internal format, will be automatically corrected by setInternalFormat()
        this( format, width, height, orgWidth, orgHeight, TextureImageInternalFormat.getFallbackInternalFormat( format ) );
    }
}
