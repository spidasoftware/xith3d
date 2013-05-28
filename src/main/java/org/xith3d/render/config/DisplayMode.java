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
package org.xith3d.render.config;

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Tuple2i;

/**
 * Represents an abstract display mode.<br>
 * All available DisplayModes can be retrieved by a DisplayModeSelector
 * implementation.
 * 
 * @see DisplayModeSelector
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DisplayMode implements Sized2iRO
{
    public static enum FullscreenMode
    {
        FULLSCREEN,
        WINDOWED,
        WINDOWED_UNDECORATED,
        ;
        
        public final boolean isFullscreen()
        {
            return ( this == FULLSCREEN );
        }
        
        public final boolean isWindowed()
        {
            return ( ( this == WINDOWED ) || ( this == WINDOWED_UNDECORATED ) );
        }
        
        public final boolean isUndecorated()
        {
            return ( this == WINDOWED_UNDECORATED );
        }
    }
    
    public static FullscreenMode FULLSCREEN = FullscreenMode.FULLSCREEN;
    public static FullscreenMode WINDOWED = FullscreenMode.WINDOWED;
    public static FullscreenMode WINDOWED_UNDECORATED = FullscreenMode.WINDOWED_UNDECORATED;
    
    public static boolean VSYNC_ENABLED = true;
    public static boolean VSYNC_DISABLED = false;
    
    private final Object nativeMode;
    
    private final OpenGLLayer oglLayer;
    
    private final int width;
    private final int height;
    private final int bpp;
    private final int freq;
    
    /**
     * @return the default bits per pixel (color depth)
     */
    public static int getDefaultBPP()
    {
        return ( 24 );
    }
    
    /**
     * @return the default bits per pixel (color depth)
     */
    public static int getDefaultFrequency()
    {
        return ( 75 );
    }
    
    public final OpenGLLayer getOpenGLLayer()
    {
        return ( oglLayer );
    }
    
    /**
     * @return the native OpenGLLayer-dependent DisplayMode object used by the
     *         CanvasPeer implementations to set the display mode.
     */
    public Object getNativeMode()
    {
        return ( nativeMode );
    }
    
    /**
     * @return the width of the display mode
     */
    public final int getWidth()
    {
        return ( width );
    }
    
    /**
     * @return the height of the display mode
     */
    public final int getHeight()
    {
        return ( height );
    }
    
    /**
     * {@inheritDoc}
     */
    public Tuple2i getSize()
    {
        return ( new Tuple2i( getWidth(), getHeight() ) );
    }
    
    /**
     * @return this DisplayMode's aspect ratio
     */
    public float getAspect()
    {
        return ( (float)getWidth() / (float)getHeight() );
    }
    
    /**
     * @return the bits per pixel of the display mode
     */
    public final int getBPP()
    {
        return ( bpp );
    }
    
    /**
     * @return the frequency of the display mode
     */
    public final int getFrequency()
    {
        return ( freq );
    }
    
    /**
     * Creates a DisplayMode instance from the String, if possible.<br>
     * The String must be of format 800x600x24x85
     * 
     * @param layer the OpenGLLayer to use for native mode retieval
     * @param value the String to create a DisplayMode from
     * 
     * @return the created DisplayMode
     * 
     * @throws IllegalArgumentException if the String was not of the right format
     */
    public static DisplayMode parseDisplayMode( OpenGLLayer layer, String value ) throws IllegalArgumentException
    {
        final DisplayModeSelector selector = DisplayModeSelector.getImplementation( layer );
        
        try
        {
            final String[] modeParts = value.split( "x" );
            final int width = Integer.parseInt( modeParts[ 0 ] );
            final int height = Integer.parseInt( modeParts[ 1 ] );
            final int bpp;
            if ( modeParts[ 2 ].equals( "?" ) )
                bpp = 24;
            else
                bpp = Integer.parseInt( modeParts[ 2 ] );
            final int freq;
            if ( modeParts[ 3 ].equals( "?" ) )
                freq = 75;
            else
                freq = Integer.parseInt( modeParts[ 3 ] );
            
            return ( selector.getBestMode( width, height, bpp, freq ) );
        }
        catch ( Throwable t )
        {
            throw new IllegalArgumentException( "The String could not be parsed to a DisplayMode", t );
        }
    }
    
    /**
     * @return a String in the format 800x600x24x85
     */
    public String toLightString()
    {
        final String bpp;
        if ( getBPP() <= 0 )
            bpp = "?";
        else
            bpp = String.valueOf( getBPP() );
        
        final String freq;
        if ( getFrequency() <= 0 )
            freq = "?";
        else
            freq = String.valueOf( getFrequency() );
        
        return ( getWidth() + "x" + getHeight() + "x" + bpp + "x" + freq );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        /*
        final String bpp;
        if ( getBPP() <= 0 )
            bpp = "?";
        else
            bpp = String.valueOf( getBPP() );
        
        final String freq;
        if ( getFrequency() <= 0 )
            freq = "?";
        else
            freq = String.valueOf( getFrequency() );
        
        return ( getClass().getName() + " ( Width = " + getWidth() + ", Height = " + getHeight() + ", BPP = " + bpp + ", Frequency = " + freq + ", Native-Mode: " + ( ( nativeMode == null ) ? "invalid" : "valid" ) + " )" );
        */
        
        return ( toLightString() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object other )
    {
        if ( !( other instanceof DisplayMode ) )
            return ( false );
        
        final DisplayMode otherDM = (DisplayMode)other;
        
        if ( otherDM.width != this.width )
            return ( false );
        
        if ( otherDM.height != this.height )
            return ( false );
        
        if ( otherDM.bpp != this.bpp )
            return ( false );
        
        if ( otherDM.freq != this.freq )
            return ( false );
        
        return ( true );
    }
    
    /**
     * Creates a new DisplayMode object.
     * 
     * @param oglLayer
     * @param nativeMode the OpenGLLayer dependent DisplayMode object
     * @param width the width
     * @param height the height
     * @param bpp the pits per pixel
     * @param freq the frequency
     */
    public DisplayMode( OpenGLLayer oglLayer, Object nativeMode, int width, int height, int bpp, int freq )
    {
        this.oglLayer = oglLayer;
        this.nativeMode = nativeMode;
        this.width = width;
        this.height = height;
        this.bpp = bpp;
        this.freq = freq;
    }
    
    /**
     * Creates a new DisplayMode object.
     * 
     * @param nativeMode the OpenGLLayer dependent DisplayMode object
     * @param width the width
     * @param height the height
     * @param bpp the pits per pixel
     * @param freq the frequency
     */
    public DisplayMode( Object nativeMode, int width, int height, int bpp, int freq )
    {
        this( null, nativeMode, width, height, bpp, freq );
    }
}
