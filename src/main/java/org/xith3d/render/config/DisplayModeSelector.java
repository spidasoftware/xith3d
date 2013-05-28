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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.xith3d.utility.comparator.Sorter;

/**
 * DisplayModeSelector base.<br>
 * If you want to know, which DisplayModes are awailable on your System and
 * for a specific OpenGLLayer, make use of it.<br>
 * <br>
 * Instantiate it by invoking the static getImplementation() method.
 * 
 * @see #getImplementation(org.xith3d.render.config.OpenGLLayer)
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class DisplayModeSelector
{
    private static class ModesComparator implements Comparator< DisplayMode >
    {
        public int compare( DisplayMode mode1, DisplayMode mode2 )
        {
            if ( mode1.getWidth() > mode2.getWidth() )
                return ( 1 );
            if ( mode1.getWidth() < mode2.getWidth() )
                return ( -1 );
            
            if ( mode1.getHeight() > mode2.getHeight() )
                return ( 1 );
            if ( mode1.getHeight() < mode2.getHeight() )
                return ( -1 );
            
            if ( mode1.getBPP() > mode2.getBPP() )
                return ( 1 );
            if ( mode1.getBPP() < mode2.getBPP() )
                return ( -1 );
            
            if ( mode1.getFrequency() > mode2.getFrequency() )
                return ( 1 );
            if ( mode1.getFrequency() < mode2.getFrequency() )
                return ( -1 );
            
            return ( 0 );
        }
    }
    
    private static final ModesComparator modesComparator = new ModesComparator();
    
    public static boolean debug = false;
    
    private static final HashMap< OpenGLLayer, DisplayModeSelector > selectorCache = new HashMap< OpenGLLayer, DisplayModeSelector >();
    
    /**
     * Sorts the DisplayModes in ascending order.
     * 
     * @param modes the array of DisplayModes
     */
    protected void sortModes( DisplayMode[] modes )
    {
        for ( int i = 0; i < modes.length; i++ )
        {
            Sorter.quickSort( modes, 0, modes.length - 1, modesComparator );
        }
    }
    
    /**
     * @return an Array of all available DisplayModes
     */
    public abstract DisplayMode[] getAvailableModes();
    
    /**
     * @return the DisplayMode for the current Desktop setting.
     */
    public DisplayMode getDesktopMode()
    {
        GraphicsDevice graphDev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        java.awt.DisplayMode dm = graphDev.getDisplayMode();
        
        return ( new DisplayMode( null, dm.getWidth(), dm.getHeight(), dm.getBitDepth(), dm.getRefreshRate() ) );
    }
    
    /**
     * Returns the DisplayMode matching best the given parameters.
     * 
     * @param width
     * @param height
     * @param optimalBPP
     * @param optimalFreq
     * 
     * @return the best possible DisplayMode
     */
    public DisplayMode getBestMode( int width, int height, int optimalBPP, int optimalFreq )
    {
        DisplayMode[] modes = getAvailableModes();
        
        if ( modes.length == 0 )
        {
            // What's this? This cannot be! (falling back to desktop)
            modes = new DisplayMode[] { getDesktopMode() };
            
            if ( debug )
            {
                System.out.println( "No DisplayMode available! Falling back to desktop mode!" );
            }
        }
        
        if ( debug )
            System.out.println( "Trying to find best mode matching (Width = " + width + ", Height = " + height + " , BPP = " + optimalBPP + ")" );
        
        ArrayList< DisplayMode > foundModes = new ArrayList< DisplayMode >();
        for ( int i = 0; i < modes.length; i++ )
        {
            if ( debug )
                System.out.println( "  Found Mode " + i + "... Width = " + modes[ i ].getWidth() + ", Height = " + modes[ i ].getHeight() + ", BPP = " + modes[ i ].getBPP() + ", frequency = " + modes[ i ].getFrequency() );
            
            if ( modes[ i ].getWidth() == width && modes[ i ].getHeight() == height && modes[ i ].getBPP() == optimalBPP )
            {
                foundModes.add( modes[ i ] );
            }
        }
        
        if ( foundModes.size() == 0 )
        {
            // Try to satisfy width and height
            for ( int i = 0; i < modes.length; i++ )
            {
                if ( ( modes[ i ].getWidth() == width ) && ( modes[ i ].getHeight() == height ) )
                {
                    foundModes.add( modes[ i ] );
                }
            }
        }
        
        if ( foundModes.size() == 0 )
        {
            if ( debug )
                System.out.println( "No matching mode found so far. Trying to find a similar one..." );
            
            Arrays.sort( modes, new ModesComparator() );
            
            DisplayMode bestMode = modes[ 0 ];
            
            for ( int i = 1; i < modes.length; i++ )
            {
                final int diffW0 = Math.abs( bestMode.getWidth() - width );
                final int diffW1 = Math.abs( modes[ i ].getWidth() - width );
                final int diffH0 = Math.abs( bestMode.getHeight() - height );
                final int diffH1 = Math.abs( modes[ i ].getHeight() - height );
                
                if ( ( diffW1 <= diffW0 ) && ( diffH1 <= diffH0 ) )
                {
                    bestMode = modes[ i ];
                }
            }
            
            for ( int i = 0; i < modes.length; i++ )
            {
                if ( ( modes[ i ].getWidth() == bestMode.getWidth() ) && ( modes[ i ].getHeight() == bestMode.getHeight() ) )
                {
                    foundModes.add( modes[ i ] );
                }
            }
            
            if ( debug )
            {
                System.out.println( "Found " + foundModes.size() + " similar mode(s) with (Width = " + bestMode.getWidth() + ", Height = " + bestMode.getHeight() + ")." );
            }
        }
        
        DisplayMode bestMode = null;
        
        if ( foundModes.size() > 0 )
        {
            bestMode = foundModes.get( 0 );
            
            // select mode with the best frequency
            for ( int i = 1; i < foundModes.size(); i++ )
            {
                final int diff0 = ( bestMode.getFrequency() - optimalFreq );
                final int diff1 = ( foundModes.get( i ).getFrequency() - optimalFreq );
                
                if ( ( diff1 >= 0 ) && ( ( diff1 < diff0 ) || ( diff0 < 0 ) ) )
                {
                    bestMode = foundModes.get( i );
                }
            }
        }
        
        if ( debug )
        {
            if ( bestMode == null )
                System.out.println( "No DisplayMode available!" );
            else
                System.out.println( "Using mode: Width = " + bestMode.getWidth() + ", Height = " + bestMode.getHeight() + ", BPP = " + bestMode.getBPP() + ", frequency = " + bestMode.getFrequency() );
        }
        
        return ( bestMode );
    }
    
    /**
     * Returns the DisplayMode matching best the given parameters.
     * 
     * @param width
     * @param height
     * @param optimalBPP
     * 
     * @return the best possible DisplayMode
     */
    public DisplayMode getBestMode( int width, int height, int optimalBPP )
    {
        return ( getBestMode( width, height, optimalBPP, DisplayMode.getDefaultFrequency() ) );
    }
    
    /**
     * Returns the DisplayMode matching best the given parameters.
     * 
     * @param width
     * @param height
     * 
     * @return the best possible DisplayMode
     */
    public DisplayMode getBestMode( int width, int height )
    {
        return ( getBestMode( width, height, DisplayMode.getDefaultBPP() ) );
    }
    
    /**
     * @param layer
     * 
     * @return the DisplayModeSelector implementation for the given OpenGLLayer.
     */
    public static DisplayModeSelector getImplementation( OpenGLLayer layer )
    {
        final DisplayModeSelector cached = selectorCache.get( layer );
        if ( cached != null )
        {
            return ( cached );
        }
        
        try
        {
            DisplayModeSelector selector = (DisplayModeSelector)Class.forName( layer.getDisplayModeSelectorClassName() ).newInstance();
            selectorCache.put( layer, selector );
            
            return ( selector );
        }
        catch ( Exception e )
        {
            Error error = new Error( e );
            
            throw error;
        }
        
        //throw new IllegalArgumentException( layer.toString() + "currently has no implementation for DisplayModeSelector" ) );
    }
}
