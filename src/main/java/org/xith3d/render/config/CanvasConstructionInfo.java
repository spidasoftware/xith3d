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

import java.awt.Dimension;
import java.awt.Toolkit;

import org.xith3d.render.config.DisplayMode.FullscreenMode;

/**
 * A CanvasConstructionInfo holds all information to construct a new instance
 * of Canvas3DWrapper. It can be used to directly being pass to the
 * Canvas3DWrapper's constructor.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CanvasConstructionInfo
{
    private static DisplayMode createDisplayMode( OpenGLLayer layer, int width, int height, int bpp )
    {
        assert ( layer != null );
        
        DisplayModeSelector modeSelector = DisplayModeSelector.getImplementation( layer );
        
        DisplayMode displayMode = modeSelector.getBestMode( width, height, bpp, DisplayMode.getDefaultFrequency() );
        
        if ( displayMode == null )
        {
            //throw new RuntimeException( "No DisplayMode found!" ) );
            displayMode = new DisplayMode( null, width, height, DisplayMode.getDefaultBPP(), DisplayMode.getDefaultFrequency() );
        }
        
        return ( displayMode );
    }
    
    /*
    private static DisplayMode createDisplayMode(OpenGLLayer layer, int width, int height)
    {
        return ( createDisplayMode( layer, width, height, DisplayMode.getDefaultBPP() ) );
    }
    */

    private static final Dimension DESKTOP_RESOLUTION = Toolkit.getDefaultToolkit().getScreenSize();
    
    private OpenGLLayer layer;
    private DisplayMode dspMode;
    private FullscreenMode fullscreenMode;
    private boolean vsync;
    private FSAA fsaa;
    private int depthBufferSize = 16;
    private String title;
    
    public void setOpenGLLayer( OpenGLLayer layer )
    {
        if ( layer == null )
            throw new IllegalArgumentException( "layer MUST NOT be null" );
        
        this.layer = layer;
    }
    
    public final OpenGLLayer getOpenGLLayer()
    {
        return ( layer );
    }
    
    public void setDisplayMode( DisplayMode mode )
    {
        if ( mode == null )
            throw new IllegalArgumentException( "mode MUST NOT be null" );
        
        this.dspMode = mode;
    }
    
    public final DisplayMode getDisplayMode()
    {
        return ( dspMode );
    }
    
    public void setFullscreenMode( FullscreenMode fullscreenMode )
    {
        this.fullscreenMode = fullscreenMode;
    }
    
    public final FullscreenMode getFullscreenMode()
    {
        return ( fullscreenMode );
    }
    
    public void setVSyncEnabled( boolean vsync )
    {
        this.vsync = vsync;
    }
    
    public final boolean isVSyncEnabled()
    {
        return ( vsync );
    }
    
    public void setFSAAMode( FSAA fsaa )
    {
        this.fsaa = fsaa;
    }
    
    public final FSAA getFSAAMode()
    {
        return ( fsaa );
    }
    
    public void setDepthBufferSize( int depthBufferSize )
    {
        this.depthBufferSize = depthBufferSize;
    }
    
    public final int getDepthBufferSize()
    {
        return ( depthBufferSize );
    }
    
    public void setTitle( String title )
    {
        if ( title == null )
            setFullscreenMode( DisplayMode.FULLSCREEN );
        else
            setFullscreenMode( DisplayMode.WINDOWED );
        
        this.title = title;
    }
    
    public final String getTitle()
    {
        return ( title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        setOpenGLLayer( layer );
        setDisplayMode( dspMode );
        setTitle( title );
        setFullscreenMode( fullscreen );
        setVSyncEnabled( vsync );
        setFSAAMode( fsaa );
        
        /*
        if (dspMode.getNativeMode() == null)
        {
            setDisplayMode( createDisplayMode( layer, dspMode.getWidth(), dspMode.getWidth() ) );
        }
        */
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( layer, dspMode, fullscreen, DisplayMode.VSYNC_ENABLED, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( layer, dspMode, fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, String title )
    {
        this( layer, dspMode, fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        this( extractOpenGLLayer( dspMode ), dspMode, fullscreen, vsync, fsaa, title );
    }
    
    private static final OpenGLLayer extractOpenGLLayer( DisplayMode dspMode )
    {
        if ( dspMode.getNativeMode() == null )
            return ( OpenGLLayer.getDefault() );
        
        if ( dspMode.getNativeMode().getClass().getName().contains( "lwjgl" ) )
            return ( OpenGLLayer.LWJGL  );
        
        return ( OpenGLLayer.getDefault() );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( DisplayMode dspMode, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( extractOpenGLLayer( dspMode ), dspMode, fullscreen, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( dspMode, fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( DisplayMode dspMode, FullscreenMode fullscreen, String title )
    {
        this( dspMode, fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        this( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, vsync, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( layer, width, height, bpp, fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, String title )
    {
        this( layer, width, height, bpp, fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        this( OpenGLLayer.getDefault(), width, height, bpp, fullscreen, vsync, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, int bpp, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( OpenGLLayer.getDefault(), width, height, bpp, fullscreen, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( width, height, bpp, fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param bpp the bits per pixel (color depth)
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, int bpp, FullscreenMode fullscreen, String title )
    {
        this( width, height, bpp, fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        this( layer, width, height, DisplayMode.getDefaultBPP(), fullscreen, vsync, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( layer, width, height, DisplayMode.getDefaultBPP(), fullscreen, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( layer, width, height, fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, String title )
    {
        this( layer, width, height, fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        this( OpenGLLayer.getDefault(), width, height, fullscreen, vsync, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( OpenGLLayer.getDefault(), width, height, fullscreen, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( width, height, fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param width the x-resolution
     * @param height the y-resolution
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( int width, int height, FullscreenMode fullscreen, String title )
    {
        this( width, height, fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        this( layer, DESKTOP_RESOLUTION.width, DESKTOP_RESOLUTION.height, DisplayMode.getDefaultBPP(), fullscreen, vsync, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( layer, DESKTOP_RESOLUTION.width, DESKTOP_RESOLUTION.height, DisplayMode.getDefaultBPP(), fullscreen, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( layer, fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( OpenGLLayer layer, FullscreenMode fullscreen, String title )
    {
        this( layer, fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        this( OpenGLLayer.getDefault(), fullscreen, vsync, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public CanvasConstructionInfo( FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        this( OpenGLLayer.getDefault(), fullscreen, fsaa, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public CanvasConstructionInfo( FullscreenMode fullscreen, boolean vsync, String title )
    {
        this( fullscreen, vsync, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param fullscreen DisplayMode.FULLSCREEN or DisplayMode.WINDOWED
     * @param title the new window's title
     */
    public CanvasConstructionInfo( FullscreenMode fullscreen, String title )
    {
        this( fullscreen, FSAA.OFF, title );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public CanvasConstructionInfo( OpenGLLayer layer, boolean vsync, FSAA fsaa )
    {
        this( layer, DisplayMode.WINDOWED, vsync, fsaa, "Powered by Xith3D" );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param fsaa full scene anti aliasing mode
     */
    public CanvasConstructionInfo( OpenGLLayer layer, FSAA fsaa )
    {
        this( layer, DisplayMode.WINDOWED, fsaa, "Powered by Xith3D" );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param vsync v-sync enabled or not
     */
    public CanvasConstructionInfo( OpenGLLayer layer, boolean vsync )
    {
        this( layer, vsync, FSAA.OFF );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     */
    public CanvasConstructionInfo( OpenGLLayer layer )
    {
        this( layer, FSAA.OFF );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public CanvasConstructionInfo( boolean vsync, FSAA fsaa )
    {
        this( OpenGLLayer.getDefault(), vsync, fsaa );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param fsaa full scene anti aliasing mode
     */
    public CanvasConstructionInfo( FSAA fsaa )
    {
        this( OpenGLLayer.getDefault(), fsaa );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     * 
     * @param vsync v-sync enabled or not
     */
    public CanvasConstructionInfo( boolean vsync )
    {
        this( vsync, FSAA.OFF );
    }
    
    /**
     * Creates as construction info to construct a new Canvas3D.
     */
    public CanvasConstructionInfo()
    {
        this( FSAA.OFF );
    }
}
