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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.LinkedList;
import org.jagatoo.util.image.ImageUtility;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Colorf;
import org.jagatoo.loaders.textures.MipmapGenerator;
import org.jagatoo.loaders.textures.pixelprocessing.PixelProcessor;
import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFilter;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.xith3d.loaders.texture.TextureLoader.FlipMode;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture2D;

/**
 * The TextureCreator is capable of creating (empty) Textures.
 * 
 * @author Matthias Mann
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureCreator
{
    private static TextureCreator singletonInstance = null;
    
    private static Graphics2D metricsGraphics = null;
    
    // Simplistic LRUCache for BufferedImages used for text writing
    private static class BICache
    {
        // TODO: use SoftReference once I know how to circumvent the timestamp update for constraint checking
        //        private static class BIEntry extends SoftReference<BufferedImage>
        private static class BIEntry
        {
            final BufferedImage image;
            long lastUsed;
            public BIEntry( BufferedImage referent )
            {
                //super( referent );
                this.image = referent;
            }
            
            public void touch()
            {
                lastUsed = System.currentTimeMillis();
            }
            
            //@Override
            //@Deprecated
            //public BufferedImage get()
            //{
            //    throw new IllegalStateException( "Don't call get!" );
            //}
        }
        
        // TODO: check performance against LinkedList because of the Iterator.remove()-calls in getImage()
        private LinkedList<BIEntry> images = new LinkedList<BIEntry>();
        private int capacity;
        private long maxAge;
        
        public BICache( int capacity, long maxAge )
        {
            this.capacity = capacity;
            this.maxAge = maxAge;
        }
        
        public BufferedImage getImage( int width, int height )
        {
            BIEntry candidate = null;
            BIEntry match = null;
            
            // Try to find a matching cache entry
            final int size = images.size();
            Iterator<BIEntry> it = images.iterator();
            for ( int i = 0; i < size; i++ )
            {
                candidate = it.next();
                if ( ( candidate.image.getWidth() == width ) && ( candidate.image.getHeight() == height ) )
                {
                    match = candidate;
                    // remove the candidate from the cache to reinsert it at first position later
                    it.remove();
                    break;
                }
            }
            
            // If no match was found create a new BufferedImage
            if ( match == null )
                match = new BIEntry( new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR ) );
            
            // update lastUsed, so we can check against this to get rid of old cache entries
            match.touch();
            images.addFirst( match );
            housekeep();
            
            return ( match.image );
        }
        
        /**
         * does some housekeeping by keeping the cache within its limits and removing too old entries
         */
        public void housekeep()
        {
            if ( ( images.size() > capacity ) || ( System.currentTimeMillis() > images.getLast().lastUsed + maxAge ) )
                images.getLast();
        }
    }
    
    // Hold up to 64 images as long as their age is not greater than 30 seconds
    private static BICache imageCache = new BICache( 64, 30 * 1000 );
    
    /**
     * @return the singleton instance of TextureCreator
     * 
     * @deprecated this class has static method only now!
     */
    @Deprecated
    public static TextureCreator getInstance()
    {
        if ( singletonInstance == null )
        {
            singletonInstance = new TextureCreator();
        }
        
        return ( singletonInstance );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param format The desired Texture format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param width The desired texture width.
     * @param height The desired texture height.
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode, int width, int height, Texture2D tex )
    {
        final int orgWidth = img.getWidth();
        final int orgHeight = img.getHeight();
        
        if ( orgWidth != width || orgHeight != height )
        {
            img = ImageUtility.scaleImage( img, width, height, format.hasAlpha() );
        }
        
        boolean flip = ( flipVertically != null ) ? flipVertically.getBooleanValue() : true;
        final PixelProcessor pp = PixelProcessor.selectPixelProcessor( img, format );
        TextureImage2D ic = (TextureImage2D)pp.createTextureImage( img, orgWidth, orgHeight, format, flip, Xith3DTextureFactory2D.getInstance() );
        tex.setImage( 0, ic );
        
        if ( ( mipmapMode == null ) || ( mipmapMode.booleanValue() ) )
        {
            MipmapGenerator.createMipMaps( ic, tex, Xith3DTextureFactory2D.getInstance() );
        }
        
        return ( tex );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param format The desired Texture format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param width The desired texture width.
     * @param height The desired texture height.
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode, int width, int height )
    {
        Texture2D tex = new Texture2D( format );
        
        return ( createTexture( img, flipVertically, format, mipmapMode, width, height, tex ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param flipVertically flip the image vertically or not
     * @param format The desired Texture format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param allowStreching
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode, boolean allowStreching )
    {
        final int width;
        final int height;
        
        if ( allowStreching )
        {
            width = ImageUtility.roundUpPower2( img.getWidth() );
            height = ImageUtility.roundUpPower2( img.getHeight() );
        }
        else
        {
            width = img.getWidth();
            height = img.getHeight();
        }
        
        return ( createTexture( img, flipVertically, format, mipmapMode, width, height ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param flipVertically flip the image vertically or not
     * @param format The desired Texture format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( createTexture( img, flipVertically, format, mipmapMode, true ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param format The desired Texture format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( createTexture( img, (FlipMode)null, format, mipmapMode ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param flipVertically flip the image vertically or not
     * @param mipmapMode Should the texture contain mipmaps ?
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, FlipMode flipVertically, Texture.MipmapMode mipmapMode )
    {
        return ( createTexture( img, flipVertically, TextureFormat.RGBA, mipmapMode ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, Texture.MipmapMode mipmapMode )
    {
        return ( createTexture( img, (FlipMode)null, mipmapMode ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param format The desired Texture format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param width The desired texture width.
     * @param height The desired texture height.
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, TextureFormat format, Texture.MipmapMode mipmapMode, int width, int height )
    {
        return ( createTexture( img, (FlipMode)null, format, mipmapMode, width, height ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param flipVertically flip the image vertically or not
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param width The desired texture width.
     * @param height The desired texture height.
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, FlipMode flipVertically, Texture.MipmapMode mipmapMode, int width, int height )
    {
        return ( createTexture( img, flipVertically, TextureFormat.RGBA, mipmapMode, width, height ) );
    }
    
    /**
     * Creates a Texture from the given BufferedImage. The generated Texture is
     * not cached.
     * 
     * @param img The BufferedImage which should be converted.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param width The desired texture width.
     * @param height The desired texture height.
     * @return Texture A Texture object that is based on the current content of
     *         the given image (NOT byRef!)
     */
    public static Texture2D createTexture( BufferedImage img, Texture.MipmapMode mipmapMode, int width, int height )
    {
        return ( createTexture( img, (FlipMode)null, mipmapMode, width, height ) );
    }
    
    /**
     * Creates a new Texture2D with the given color.
     * 
     * @param format
     * @param width
     * @param height
     * @param color
     * 
     * @return the new Texture2D
     */
    public static Texture2D createTexture( TextureFormat format, int width, int height, Colorf color )
    {
        if ( format == TextureFormat.LUMINANCE )
        {
            throw new UnsupportedOperationException( "Creating Luminance Textures is not yet supported." );
        }
        
        final TextureImageFormat tiFormat = format.getDefaultTextureImageFormat();
        
        if ( color == null )
            color = new Colorf( 0f, 0f, 0f, 1f );
        
        Texture2D tex = new Texture2D( format );
        
        TextureImage2D ic = new TextureImage2D( tiFormat, width, height, width, height, false );
        ic.setImageData( null, ic.getDataSize() );
        ic.clear( color );
        
        tex.setImage( 0, ic );
        
        return ( tex );
    }
    
    /**
     * Creates a new Texture2D with the given color.
     * 
     * @param format
     * @param width
     * @param height
     * 
     * @return the new Texture2D
     */
    public static Texture2D createTexture( TextureFormat format, int width, int height )
    {
        return ( createTexture( format, width, height, null ) );
    }
    
    public static class TextMetrics extends Dimension
    {
        private static final long serialVersionUID = 1L;
        
        private int lineHeight;
        private int ascend;
        private int descend;
        private int[] lineWidths;
        
        public final int getLineHeight()
        {
            return ( lineHeight );
        }
        
        public final int getAscend()
        {
            return ( ascend );
        }
        
        public final int getDescend()
        {
            return ( descend );
        }
        
        public TextMetrics( int width, int height, int[] lineWidths, int lineHeight, int ascend, int descend )
        {
            super( width, height );
            
            this.lineWidths = lineWidths;
            this.lineHeight = lineHeight;
            this.ascend = ascend;
            this.descend = descend;
        }
    }
    
    /**
     * Creates a Tuple2f containing the size of the text on a Texture.
     * 
     * @param text
     * @param font
     */
    private static TextMetrics getTextMetrics( String[] lines, Font font )
    {
        if ( metricsGraphics == null )
        {
            BufferedImage image = new BufferedImage( 1024, 128, BufferedImage.TYPE_4BYTE_ABGR );
            metricsGraphics = image.createGraphics();
        }
        
        int maxWidth = 0;
        int totalHeight = 0;
        
        final FontMetrics metrics = metricsGraphics.getFontMetrics( font );
        final int ascend = metrics.getAscent();
        final int descend = metrics.getDescent();
        final int lineHeight = ascend + descend;
        int[] lineWidths = new int[ lines.length ];
        
        for ( int i = 0; i < lines.length; i++ )
        {
            lineWidths[ i ] = metrics.stringWidth( lines[ i ] );
            
            if ( lineWidths[ i ] > maxWidth )
                maxWidth = lineWidths[ i ];
            totalHeight += lineHeight;
        }
        
        return ( new TextMetrics( maxWidth, totalHeight, lineWidths, lineHeight, ascend, descend ) );
    }
    
    public static final int TEXT_ALIGNMENT_HORIZONTAL_LEFT = 0;
    public static final int TEXT_ALIGNMENT_HORIZONTAL_CENTER = 1;
    public static final int TEXT_ALIGNMENT_HORIZONTAL_RIGHT = 2;
    
    /**
     * Creates a new transparent Texture with a String drawn on it.<br>
     * Call texture.getUserData( "EFFECTIVE_SIZE" ) to retrieve the effective size when padded.
     * 
     * @see #TEXT_ALIGNMENT_HORIZONTAL_LEFT
     * @see #TEXT_ALIGNMENT_HORIZONTAL_CENTER
     * @see #TEXT_ALIGNMENT_HORIZONTAL_RIGHT
     * 
     * @param text the text to draw on the Texture (inline '\n' for line wrapping).
     * @param color text color (null for transparent)
     * @param font the font to use
     * @param horizontalAlignment horizontal alignment indicator
     * @param paddSizetoPower2 if true, the Texture's size is padded up to the next power of 2 to avoid resizings
     * @return returns a Texture (should be casted to Character)
     */
    public static Texture2D createTexture( String text, Colorf color, Font font, int horizontalAlignment, boolean paddSizetoPower2 )
    {
        final String[] lines = text.split( "\\n" );
        
        TextMetrics metrics = getTextMetrics( lines, font );
        
        // BufferedImage to draw on
        final int width;
        final int height;
        if ( paddSizetoPower2 )
        {
            width = ImageUtility.roundUpPower2( metrics.width );
            height = ImageUtility.roundUpPower2( metrics.height );
        }
        else
        {
            width = metrics.width;
            height = metrics.height;
        }
//        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
        BufferedImage image = imageCache.getImage(width, height);
        
        // Graphics from BufferedImage
        Graphics2D g2 = image.createGraphics();
        
        // enable anti-aliasing
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        // fill background to transparent
        g2.setBackground(new Color( 0.0f, 0.0f, 0.0f, 0.0f ));
        g2.clearRect(0, 0, width, height);
        
        // set the color
        if ( color == null )
            g2.setColor( new Color( 1.0f, 1.0f, 1.0f, 1.0f ) );
        else
            g2.setColor( color.getAWTColor() );
        
        // set Font
        g2.setFont( font );
        
        // draw the text
        int left = 0;
        for ( int i = 0; i < lines.length; i++ )
        {
            if ( horizontalAlignment == TEXT_ALIGNMENT_HORIZONTAL_CENTER )
                left = ( metrics.width - metrics.lineWidths[ i ] ) / 2;
            else if ( horizontalAlignment == TEXT_ALIGNMENT_HORIZONTAL_RIGHT )
                left = metrics.width - metrics.lineWidths[ i ];
            else
                left = 0;
            
            g2.drawString( lines[ i ], left, ( metrics.lineHeight * i ) + metrics.ascend );
        }
        
        // --- convert the String to a texture
        Texture2D texture = (Texture2D)createTexture( image, TextureFormat.RGBA, Texture.MipmapMode.BASE_LEVEL );
        
        texture.setBoundaryModeS( TextureBoundaryMode.CLAMP );
        texture.setBoundaryModeT( TextureBoundaryMode.CLAMP );
        
        texture.setFilter( TextureFilter.TRILINEAR );
        
        if ( paddSizetoPower2 )
            texture.setUserData( "EFFECTIVE_SIZE", new Dim2i( metrics.width, metrics.height ) );
        
        if ( text.length() > 30 )
            texture.setName( "Text: \"" + text.substring( 0, 27 ) + "...\"" );
        else
            texture.setName( "Text: \"" + text + "\"" );
        
        return ( texture );
    }
    
    /**
     * Creates a new transparent Texture with a String drawn on it.
     * The Texture's size is padded up to the next power of 2 to avoid resizings.<br>
     * Call texture.getUserData( "EFFECTIVE_SIZE" ) to retrieve the effective size.
     * 
     * @see #TEXT_ALIGNMENT_HORIZONTAL_LEFT
     * @see #TEXT_ALIGNMENT_HORIZONTAL_CENTER
     * @see #TEXT_ALIGNMENT_HORIZONTAL_RIGHT
     * 
     * @param text the text to draw on the Texture (inline '\n' for line wrapping).
     * @param color text color (null for transparent)
     * @param font the font to use
     * @param horizontalAlignment horizontal alignment indicator
     * @return returns a Texture (should be casted to Character)
     */
    public static Texture2D createTexture( String text, Colorf color, Font font, int horizontalAlignment )
    {
        return ( createTexture( text, color, font, horizontalAlignment, true ) );
    }
}
