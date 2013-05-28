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
package org.xith3d.utility.commandline;

import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Sized2iRO;
import org.xith3d.render.config.CanvasConstructionInfo;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;

/**
 * An instance of this class can hold the basic application arguments for any Xith3D application.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class BasicApplicationArguments
{
    private static final int DEFAULT_COLOR_DEPTH = 32;
    private static final int DEFAULT_FREQUENCY = 75;
    
    private OpenGLLayer oglLayer = OpenGLLayer.JOGL_SWING;
    int[] displayMode = { 800, 600, DEFAULT_COLOR_DEPTH, DEFAULT_FREQUENCY };
    private boolean fullscreen = false;
    private boolean undecorated = false;
    private boolean vsync = false;
    private FSAA fsaa = FSAA.OFF;
    private boolean invertMouseY = true;
    private Float maxFPS = 120f;
    
    private CanvasConstructionInfo canvasInfo = null;
    
    /**
     * Sets the selected OpenGLLayer.
     *  
     * @param oglLayer
     */
    public void setOpenGLLayer( OpenGLLayer oglLayer )
    {
        this.oglLayer = oglLayer;
    }
    
    /**
     * Returns the selected or the default OpenGLLayer.
     *  
     * @return the OpenGLLayer.
     */
    public final OpenGLLayer getOpenGLLayer()
    {
        return ( oglLayer );
    }
    
    /**
     * Sets the selected resolution.
     *  
     * @param resX
     * @param resY
     */
    public void setResolution( int resX, int resY )
    {
        this.displayMode[0] = resX;
        this.displayMode[1] = resY;
    }
    
    /**
     * Returns the selected or the default resolution.
     *  
     * @return the resolution.
     */
    public final Sized2iRO getResolution()
    {
        return ( new Dim2i( displayMode[0], displayMode[1] ) );
    }
    
    /**
     * Returns the selected or the default resolution width.
     *  
     * @return the resolution width.
     */
    public final int getResolutionWidth()
    {
        return ( displayMode[0] );
    }
    
    /**
     * Returns the selected or the default resolution height.
     *  
     * @return the resolution height.
     */
    public final int getResolutionHeight()
    {
        return ( displayMode[1] );
    }
    
    /**
     * Sets the selected color depth.
     *  
     * @param bpp
     */
    public void setColorDepth( int bpp )
    {
        if ( displayMode.length < 3 )
        {
            int[] tmp = new int[ 3 ];
            System.arraycopy( displayMode, 0, tmp, 0, displayMode.length );
            displayMode = tmp;
        }
        
        displayMode[2] = bpp;
    }
    
    /**
     * Returns the selected or the default color depth.
     *  
     * @return the color depth.
     */
    public final int getColorDepth()
    {
        if ( ( displayMode.length >= 3 ) && ( displayMode[2] > 0 ) )
            return ( displayMode[2] );
        
        return ( DEFAULT_COLOR_DEPTH );
    }
    
    /**
     * Sets the selected frequency.
     *  
     * @param freq
     */
    public void setFrequency( int freq )
    {
        if ( displayMode.length < 4 )
        {
            int[] tmp = new int[ 4 ];
            System.arraycopy( displayMode, 0, tmp, 0, displayMode.length );
            if ( displayMode.length < 3 )
                tmp[2] = DEFAULT_COLOR_DEPTH;
            displayMode = tmp;
        }
        
        displayMode[3] = freq;
    }
    
    /**
     * Returns the selected or the default frequency.
     *  
     * @return the frequency.
     */
    public final int getFrequency()
    {
        if ( ( displayMode.length >= 4 ) && ( displayMode[3] > 0 ) )
            return ( displayMode[3] );
        
        return ( DEFAULT_FREQUENCY );
    }
    
    /**
     * Returns the selected or the default DisplayMode.
     *  
     * @return the DisplayMode.
     */
    public final DisplayMode getDisplayMode()
    {
        return ( DisplayModeSelector.getImplementation( getOpenGLLayer() ).getBestMode( getResolutionWidth(), getResolutionHeight(), getColorDepth(), getFrequency() ) );
    }
    
    /**
     * Sets the selected FullscreenMode.
     *  
     * @param fullscreenMode.
     */
    public void setFullscreenMode( FullscreenMode fullscreenMode )
    {
        this.fullscreen = fullscreenMode.isFullscreen();
        this.undecorated = fullscreenMode.isUndecorated();
    }
    
    /**
     * Returns the selected or the default FullscreenMode.
     *  
     * @return the FullscreenMode.
     */
    public final FullscreenMode getFullscreenMode()
    {
        if ( fullscreen )
            return ( FullscreenMode.FULLSCREEN );
        
        if ( undecorated )
            return ( FullscreenMode.WINDOWED_UNDECORATED );
        
        return ( FullscreenMode.WINDOWED );
    }
    
    /**
     * Sets the selected setting for vertical-sync.
     *  
     * @param vsync.
     */
    public void setVSync( boolean vsync )
    {
        this.vsync = vsync;
    }
    
    /**
     * Returns the selected or the default setting for vertical-sync.
     *  
     * @return the vertical-sync.
     */
    public final boolean getVSync()
    {
        return ( vsync );
    }
    
    /**
     * Sets the selected {@link FSAA}.
     *  
     * @param fsaa
     */
    public void setFSAA( FSAA fsaa )
    {
        this.fsaa = fsaa;
    }
    
    /**
     * Returns the selected or the default {@link FSAA}.
     *  
     * @return the {@link FSAA}.
     */
    public final FSAA getFSAA()
    {
        return ( fsaa );
    }
    
    /**
     * Sets the selected mouse-y-inverted flag.
     *  
     * @param inverted
     */
    public void setMouseYInverted( boolean inverted )
    {
        this.invertMouseY = inverted;
    }
    
    /**
     * Returns the selected or the default mouse-y-inverted flag.
     *  
     * @return the mouse-y-inverted flag.
     */
    public final boolean getMouseYInverted()
    {
        return ( invertMouseY );
    }
    
    /**
     * Sets the selected maximum frames per second (FPS).
     * 
     * @param maxFPS (null for no limit)
     */
    public void setMaxFPS( Float maxFPS )
    {
        this.maxFPS = maxFPS;
    }
    
    /**
     * Returns the selected or default maximum frames per second (FPS).
     * 
     * @return the maximum FPS or null if unlimited.
     */
    public final Float getMaxFPS()
    {
        return ( maxFPS );
    }
    
    /**
     * Returns the selected or default maximum frames per second (FPS).
     * 
     * @return the maximum FPS or null if unlimited.
     */
    public final float getConcreteMaxFPS()
    {
        return ( maxFPS == null ? Float.MAX_VALUE : maxFPS );
    }
    
    /**
     * Constructs and returns a {@link CanvasConstructionInfo} from the selected values.
     * 
     * @return a {@link CanvasConstructionInfo}.
     */
    public final CanvasConstructionInfo getCanvasConstructionInfo()
    {
        if ( canvasInfo == null )
        {
            canvasInfo = new CanvasConstructionInfo( OpenGLLayer.JOGL_SWING, getDisplayMode(), getFullscreenMode(), getVSync(), getFSAA(), "Xith3D Application"  );
        }
        
        return ( canvasInfo );
    }
    
    public BasicApplicationArguments()
    {
    }
    
    public BasicApplicationArguments( OpenGLLayer layer, DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, boolean mouseYInverted )
    {
        this.oglLayer = layer;
        this.displayMode = new int[] { displayMode.getWidth(), displayMode.getHeight(), displayMode.getBPP(), displayMode.getFrequency() };
        this.fullscreen = fullscreen.isFullscreen();
        this.undecorated = fullscreen == FullscreenMode.WINDOWED_UNDECORATED;
        this.vsync = vsync;
        this.fsaa = fsaa;
        this.invertMouseY = mouseYInverted;
    }
}
