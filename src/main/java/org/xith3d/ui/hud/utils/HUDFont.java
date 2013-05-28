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
package org.xith3d.ui.hud.utils;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.xith3d.ui.hud.HUD;

/**
 * <p>
 * The {@link HUDFont} abstracts fonts usable on the HUD.
 * </p>
 * 
 * <p>
 * TODO: The font size should be as euqually sized on each Canvas3D resolution.
 * For this to work, the underlying AWT-Font must be selected appropriately to avoid scaling.
 * </p> 
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HUDFont
{
    public static enum FontStyle
    {
        PLAIN( java.awt.Font.PLAIN ),
        BOLD( java.awt.Font.BOLD ),
        ITALIC( java.awt.Font.ITALIC ),
        BOLD_ITALIC( java.awt.Font.BOLD | java.awt.Font.ITALIC ),
        ;
        
        private final int awtStyle;
        
        public final int getAWTStyle()
        {
            return ( awtStyle );
        }
        
        public final FontStyle makeItalic()
        {
            if ( this == PLAIN )
                return ( ITALIC );
            
            if ( this == BOLD )
                return ( BOLD_ITALIC );
            
            return ( this );
        }
        
        public final FontStyle makeNonItalic()
        {
            if ( this == ITALIC )
                return ( PLAIN );
            
            if ( this == BOLD_ITALIC )
                return ( BOLD );
            
            return ( this );
        }
        
        public final FontStyle makeBold()
        {
            if ( this == PLAIN )
                return ( BOLD );
            
            if ( this == BOLD )
                return ( BOLD_ITALIC );
            
            return ( this );
        }
        
        public final FontStyle makeNonBold()
        {
            if ( this == BOLD )
                return ( PLAIN );
            
            if ( this == BOLD_ITALIC )
                return ( ITALIC );
            
            return ( this );
        }
        
        private FontStyle( int awtStyle )
        {
            this.awtStyle = awtStyle;
        }
        
        public static final FontStyle getFromAWTStyle( int awtStyle )
        {
            switch ( awtStyle )
            {
                case java.awt.Font.PLAIN:
                    return ( PLAIN );
                case java.awt.Font.BOLD:
                    return ( BOLD );
                case java.awt.Font.ITALIC:
                    return ( ITALIC );
                case java.awt.Font.BOLD | java.awt.Font.ITALIC:
                    return ( BOLD_ITALIC );
            }
            
            throw new IllegalArgumentException( "Unknown awt font style " + awtStyle );
        }
    }
    
    /**
     * @see FontStyle#PLAIN
     */
    public static final FontStyle PLAIN = FontStyle.PLAIN;
    
    /**
     * @see FontStyle#BOLD
     */
    public static final FontStyle BOLD = FontStyle.BOLD;
    
    /**
     * @see FontStyle#ITALIC
     */
    public static final FontStyle ITALIC = FontStyle.ITALIC;
    
    /**
     * @see FontStyle#BOLD_ITALIC
     */
    public static final FontStyle BOLD_ITALIC = FontStyle.BOLD_ITALIC;
    
    private static final Graphics2D GRAPHICS = new BufferedImage( 16, 16, BufferedImage.TYPE_3BYTE_BGR ).createGraphics();
    
    private static final HashMap< String, HUDFont > MAP = new HashMap< String, HUDFont >();
    
    private static boolean useFontScaling = true;
    
    private final URL url;
    private final String name;
    private final FontStyle style;
    private final int size;
    
    private int lastHUDHeight = -1;
    
    private java.awt.Font awtFont = null;
    private java.awt.FontMetrics metrics = null;
    
    /**
     * Sets whether fonts are scaled to match the selected resolution or are simply passed through (different optical size for different resolutions.
     * 
     * @param useFontScaling
     */
    public static final void setUseFontScaling( boolean useFontScaling )
    {
        HUDFont.useFontScaling = useFontScaling;
    }
    
    /**
     * Gets whether fonts are scaled to match the selected resolution or are simply passed through (different optical size for different resolutions.
     * 
     * @return font-scaling?
     */
    public static final boolean getUseFontScaling()
    {
        return ( useFontScaling );
    }
    
    /**
     * Returns the font's name.
     * 
     * @return the font's name.
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * Returns the style.
     * 
     * @return the style.
     */
    public final FontStyle getStyle()
    {
        return ( style );
    }
    
    /**
     * Returns the size.
     * 
     * @return the size.
     */
    public final int getSize()
    {
        return ( size );
    }
    
    /**
     * Returns a derived font instance.
     * 
     * @param style
     * @param size
     * 
     * @return a derived font instance.
     */
    public final HUDFont derive( FontStyle style, int size )
    {
        if ( url != null )
        {
            try
            {
                return ( getFont( url, style, size ) );
            }
            catch ( IOException e )
            {
                // Cannot happen!
            }
        }
        
        return ( getFont( this.getName(), style, size ) );
    }
    
    /**
     * Returns a derived font instance.
     * 
     * @param style
     * 
     * @return a derived font instance.
     */
    public final HUDFont derive( FontStyle style )
    {
        if ( url != null )
        {
            try
            {
                return ( getFont( url, style, getSize() ) );
            }
            catch ( IOException e )
            {
                // Cannot happen!
            }
        }
        
        return ( getFont( this.getName(), style, getSize() ) );
    }
    
    /**
     * Returns a derived font instance.
     * 
     * @param size
     * 
     * @return a derived font instance.
     */
    public final HUDFont derive( int size )
    {
        if ( url != null )
        {
            try
            {
                return ( getFont( url, getStyle(), size ) );
            }
            catch ( IOException e )
            {
                // Cannot happen!
            }
        }
        
        return ( getFont( this.getName(), getStyle(), size ) );
    }
    
    /**
     * Returns the underlying {@link java.awt.Font}.
     * 
     * @param hud
     * 
     * @return the underlying {@link java.awt.Font}
     */
    public final java.awt.Font getAWTFont( HUD hud )
    {
        boolean useScaling = useFontScaling && hud.hasCustomResolution();
        
        if ( useScaling )
        {
            if ( ( this.awtFont == null ) || ( this.lastHUDHeight != (int)hud.getResY() ) )
            {
                if ( url == null )
                {
                    this.awtFont = new java.awt.Font( name, style.getAWTStyle(), Math.round( size * hud.getHeight() / hud.getResY() ) );
                }
                else if ( this.awtFont == null )
                {
                    try
                    {
                        this.awtFont = java.awt.Font.createFont( java.awt.Font.TRUETYPE_FONT, url.openStream() ).deriveFont( Math.round( size * hud.getHeight() / hud.getResY() ) ).deriveFont( style.getAWTStyle(), size );
                    }
                    catch ( Throwable t )
                    {
                        throw new Error( t );
                    }
                }
                else
                {
                    this.awtFont = this.awtFont.deriveFont( style.getAWTStyle(), size );
                }
                
                this.metrics = null;
                
                this.lastHUDHeight = (int)hud.getResY();
            }
        }
        else
        {
            if ( awtFont == null )
            {
                if ( url == null )
                {
                    this.awtFont = new java.awt.Font( name, style.getAWTStyle(), size );
                }
                else
                {
                    try
                    {
                        this.awtFont = java.awt.Font.createFont( java.awt.Font.TRUETYPE_FONT, url.openStream() ).deriveFont( style.getAWTStyle(), size );
                    }
                    catch ( Throwable t )
                    {
                        throw new Error( t );
                    }
                }
                
                this.metrics = null;
            }
        }
        
        return ( awtFont );
    }
    
    /**
     * Returns a matching {@link FontMetrics}.
     * 
     * @param hud
     * 
     * @return a matching {@link FontMetrics}.
     */
    public final FontMetrics getFontMetrics( HUD hud )
    {
        if ( ( metrics == null ) || ( useFontScaling && hud.hasCustomResolution() && ( this.lastHUDHeight != (int)hud.getResY() ) ) )
        {
            this.metrics = GRAPHICS.getFontMetrics( getAWTFont( hud ) );
        }
        
        return ( metrics );
    }
    
    private static final String getString( String name, FontStyle style, int size )
    {
        return ( name + "-" + style.name() + "-" + size );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( getString( getName(), getStyle(), getSize() ) );
    }
    
    
    private HUDFont( URL url, String name, FontStyle style, int size )
    {
        this.url = url;
        this.name = name;
        this.style = style;
        this.size = size;
    }
    
    /**
     * Returns the corresponding HUDFont instance.
     * 
     * @param name
     * @param style
     * @param size
     * 
     * @return the corresponding HUDFont instance.
     */
    public static final HUDFont getFont( String name, FontStyle style, int size )
    {
        HUDFont font = MAP.get( getString( name, style, size ) );
        
        if ( font == null )
        {
            font = new HUDFont( null, name, style, size );
            MAP.put( getString( name, style, size ), font );
        }
        
        return ( font );
    }
    
    /**
     * Returns the corresponding HUDFont instance.
     * 
     * @param url
     * @param style
     * @param size
     * 
     * @return the corresponding HUDFont instance.
     */
    public static final HUDFont getFont( URL url, FontStyle style, int size ) throws IOException
    {
        if ( url == null )
            throw new IllegalArgumentException( "url must not be null" );
        
        String name = url.toString();
        
        HUDFont font = MAP.get( getString( name, style, size ) );
        
        if ( font == null )
        {
            // Try to access the URL to make sure, it exists.
            InputStream is = url.openStream();
            is.close();
            
            font = new HUDFont( url, name, style, size );
            MAP.put( getString( name, style, size ), font );
        }
        
        return ( font );
    }
}
