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
package org.xith3d.ui.text2d;

import java.awt.Font;
import java.util.HashMap;

import org.openmali.types.twodee.Sized2iRO;
import org.xith3d.scenegraph.ASCIITexture;
import org.xith3d.utility.characters.CharIndex;

/**
 * Abstraction of UI fonts.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Font2D
{
    private static final String CHARSET = "ISO-8859-1";
    
    private static final CharIndex cachedCharIndex = CharIndex.createHashOptimizedCharIndex( CHARSET );
    //private static final HashMap< java.awt.Font, Font2D > font2DCache = new HashMap< Font, Font2D >();
    private static HashMap< String, ASCIITexture > textureCache = new HashMap< String, ASCIITexture >();
    
    public static final ASCIITexture getFontTexture( Font font )
    {
        //final String texKey = String.valueOf( font.hashCode() ) + "--" + color.hashCode();
        final String texKey = String.valueOf( font.hashCode() );
        
        if ( textureCache.containsKey( texKey ) )
        {
            return ( textureCache.get( texKey ) );
        }
        
        final ASCIITexture texture = ASCIITexture.create( null, font, cachedCharIndex );
        textureCache.put( texKey, texture );
        
        return ( texture );
    }
    
    private final FontStyle style;
    private final Font font;
    private final ASCIITexture texture;
    private final boolean isMonospace;
    
    public final Font getFont()
    {
        return ( font );
    }
    
    public final String getFamily()
    {
        return ( font.getFamily() );
    }
    
    public final String getName()
    {
        return ( font.getName() );
    }
    
    public final boolean isPlain()
    {
        return ( font.isPlain() );
    }
    
    public final boolean isItalic()
    {
        return ( font.isItalic() );
    }
    
    public final boolean isBold()
    {
        return ( font.isBold() );
    }
    
    public final boolean isMonospace()
    {
        return ( isMonospace );
    }
    
    public final FontStyle getStyle()
    {
        return ( style );
    }
    
    public final int getSize()
    {
        return ( font.getSize() );
    }
    
    public ASCIITexture getTexture()
    {
        return ( texture );
    }
    
    public final CharIndex getCharIndex()
    {
        return ( getTexture().getCharIndex() );
    }
    
    public final java.nio.charset.Charset getCharset()
    {
        return ( getCharIndex().getCharset() );
    }
    
    public final Sized2iRO getCharSize( char ch )
    {
        return ( getTexture().getCharSize( ch ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        //int hc = font.hashCode() >> 16;
        
        return ( font.hashCode() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( o == this )
            return ( true );
        
        if ( o == null )
            return ( false );
        
        if ( !( o instanceof Font2D ) )
            return ( false );
        
        final Font2D font2 = (Font2D)o;
        
        return ( font2.getFont().equals( this.getFont() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( this.getClass().getSimpleName() + " { family = \"" + getFamily() + "\", name = \"" + getName() + "\"" + ( isMonospace() ? " (monospace)" : "" ) + ", style = " + getStyle() + ", size = " + getSize() + " }" );
    }
    
    public Font2D( Font font )
    {
        this.font = font;
        this.style = FontStyle.getFromAWTStyle( font.getStyle() );
        
        this.texture = getFontTexture( font );
        
        this.isMonospace = ( texture.getCharSize( '.' ).getWidth() == texture.getCharSize( 'O' ).getWidth() );
    }
    
    public Font2D( String fontName, FontStyle style, int size )
    {
        this( new Font( fontName, style.getAWTStyle(), size ) );
    }
}
