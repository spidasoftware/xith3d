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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.util.image.ImageUtility;
import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.TexCoord2f;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.loaders.texture.TextureCreator.TextMetrics;
import org.xith3d.loaders.texture.TextureLoader.FlipMode;
import org.xith3d.utility.characters.CharIndex;

/**
 * This is a Texture with all printable ASCII characters on it.<br>
 * For each character a set of texture coordinates is provided in a Map.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ASCIITexture extends Texture2D
{
    private final CharIndex charIndex;
    private final TexCoord2f[][] texCoords;
    private final Sized2iRO[] charSizes;
    
    public final CharIndex getCharIndex()
    {
        return ( charIndex );
    }
    
    public TexCoord2f[] getTextureCoordinates( char ch )
    {
        final int i = charIndex.getIndex( ch );
        
        if ( ( i < 0 ) || ( i >= texCoords.length ) )
            return ( null );
        
        return ( texCoords[ i ] );
    }
    
    public Sized2iRO getCharSize( char ch )
    {
        final int i = charIndex.getIndex( ch );
        
        if ( ( i < 0 ) || ( i >= texCoords.length ) )
            return ( null );
        
        return ( charSizes[ i ] );
    }
    
    private ASCIITexture( Sized2iRO[] charSizes, TexCoord2f[][] texCoords, CharIndex charIndex )
    {
        super( TextureFormat.RGBA );
        
        this.charIndex = charIndex;
        this.charSizes = charSizes;
        this.texCoords = texCoords;
    }
    
    private static Graphics2D metricsGraphics = null;
    
    private static class CharMetrics extends TextMetrics
    {
        private static final long serialVersionUID = 1L;
        
        private final char ch;
        
        public final char getChar()
        {
            return ( ch );
        }
        
        public CharMetrics( char ch, int width, int height, int ascend, int descend )
        {
            super( width, height, new int[]
            {
                width
            }, height, ascend, descend );
            
            this.ch = ch;
        }
    }
    
    /**
     * Creates a Tuple2f containing the size of the text on a Texture.
     * 
     * @param text
     * @param font
     */
    private static CharMetrics getCharMetrics( char ch, Font font )
    {
        if ( metricsGraphics == null )
        {
            BufferedImage image = new BufferedImage( 16, 16, BufferedImage.TYPE_4BYTE_ABGR );
            metricsGraphics = image.createGraphics();
        }
        
        final FontMetrics metrics = metricsGraphics.getFontMetrics( font );
        final int ascend = metrics.getAscent();
        final int descend = metrics.getDescent();
        final int lineWidth = metrics.stringWidth( String.valueOf( ch ) );
        final int lineHeight = ascend + descend;
        
        return ( new CharMetrics( ch, lineWidth, lineHeight, ascend, descend ) );
    }
    
    private static CharMetrics[] createCharMetricsMap( Font font, CharIndex charIndex )
    {
        CharMetrics[] map = new CharMetrics[ charIndex.getNumberOfPrintableChars() ];
        
        final int n = charIndex.getTotalNumberOfCharacters();
        for ( char ch = 0; ch < n; ch++ )
        {
            final int i = charIndex.getIndex( ch );
            if ( i >= 0 )
            {
                map[ i ] = getCharMetrics( ch, font );
            }
        }
        
        return ( map );
    }
    
    /**
     * Creates an ASCIITexture with all ASCII chars on it and the appropriate
     * Texture coordiantes.
     * 
     * @param color
     * @param font
     * @param charIndex
     * 
     * @return the created ASCIITexture
     */
    public static ASCIITexture create( Colorf color, Font font, CharIndex charIndex )
    {
        CharMetrics[] map = createCharMetricsMap( font, charIndex );
        
        final int sqrSize = (int)Math.ceil( Math.sqrt( (double)charIndex.getNumberOfPrintableChars() ) );
        int sqrW = 0;
        int sqrH = 0;
        int w = 0;
        int h = 0;
        
        int x = 0;
        
        // find texture min size
        for ( int i = 0; i < map.length; i++ )
        {
            x = i % sqrSize;
            
            if ( x == 0 )
            {
                w = 0;
                h = 0;
            }
            
            w += map[ i ].width;
            h = Math.max( h, map[ i ].height );
            
            if ( x == sqrSize - 1 )
            {
                sqrW = Math.max( sqrW, w );
                sqrH += h;
            }
        }
        
        final int width = ImageUtility.roundUpPower2( sqrW );
        final int height = ImageUtility.roundUpPower2( sqrH );
        
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
        
        // Graphics from BufferedImage
        Graphics2D g2 = image.createGraphics();
        
        // enable anti-aliasing
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        // fill background to transparent
        g2.setColor( new Color( 1.0f, 0.0f, 1.0f, 0.0f ) );
        g2.fillRect( 0, 0, width, height );
        
        // set the color
        if ( color == null )
            g2.setColor( new Color( 1.0f, 1.0f, 1.0f, 1.0f ) );
        else
            g2.setColor( color.getAWTColor() );
        
        // set Font
        g2.setFont( font );
        
        Sized2iRO[] charSizes = new Sized2iRO[ map.length ];
        TexCoord2f[][] texCoords = new TexCoord2f[ map.length ][];
        
        // draw the chars on the BufferedImage
        h = 0;
        int left = 0, top = 0;
        int lineHeight = 0;
        final float texPx = ( 1.0f / (float)width );
        final float texPy = ( 1.0f / (float)height );
        for ( int i = 0; i < map.length; i++ )
        {
            x = i % sqrSize;
            
            if ( x == 0 )
            {
                left = 0;
                lineHeight = 0;
            }
            
            g2.drawString( String.valueOf( map[ i ].getChar() ), left, top + map[ i ].getLineHeight() - map[ i ].getDescend() );
            
            // create appropriate Texture Coordinates
            charSizes[ i ] = new Dim2i( map[ i ].width, map[ i ].height );
            texCoords[ i ] = new TexCoord2f[]
            {
                new TexCoord2f( (float)left * texPx, 1.0f - (float)( top + map[ i ].height ) * texPy ), new TexCoord2f( (float)( left + map[ i ].width ) * texPx, 1.0f - (float)top * texPy )
            };
            
            left += 0 + map[ i ].width;
            lineHeight = Math.max( lineHeight, map[ i ].height );
            
            if ( x == sqrSize - 1 )
            {
                top += 0 + lineHeight;
            }
        }
        
        ASCIITexture tex = new ASCIITexture( charSizes, texCoords, charIndex );
        
        TextureCreator.createTexture( image, FlipMode.FLIPPED_VERTICALLY, TextureFormat.RGBA, Texture.MipmapMode.BASE_LEVEL, width, height, tex );
        
        tex.setName( "ASCIITexture: " + font + ", " + color );
        
        return ( tex );
    }
}
