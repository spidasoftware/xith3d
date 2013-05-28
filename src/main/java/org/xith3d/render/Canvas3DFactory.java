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
package org.xith3d.render;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.xith3d.render.config.CanvasConstructionInfo;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;

/**
 * A Canvas3DFactory is capable of creating Canvas3D instance.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public final class Canvas3DFactory
{
    private static DisplayMode createDisplayMode( OpenGLLayer layer, int width, int height, int bpp, int frequency )
    {
        DisplayModeSelector modeSelector = DisplayModeSelector.getImplementation( layer );
        
        DisplayMode displayMode = modeSelector.getBestMode( width, height, bpp, frequency );
        
        if ( displayMode == null )
        {
            //throw new RuntimeException( "No DisplayMode found!" ) );
            displayMode = new DisplayMode( null, null, width, height, DisplayMode.getDefaultBPP(), DisplayMode.getDefaultFrequency() );
        }
        
        return ( displayMode );
    }
    
    private static DisplayMode createDisplayMode( OpenGLLayer layer, int width, int height, int bpp )
    {
        return ( createDisplayMode( layer, width, height, bpp, DisplayMode.getDefaultFrequency() ) );
    }
    
    private static DisplayMode createDisplayMode( OpenGLLayer layer, int width, int height )
    {
        return ( createDisplayMode( layer, width, height, DisplayMode.getDefaultBPP() ) );
    }
    
    private static OpenGLLayer getOpenGLLayer( DisplayMode dspMode )
    {
        if ( dspMode.getOpenGLLayer() == null )
            return ( OpenGLLayer.getDefault() );
        
        return ( dspMode.getOpenGLLayer() );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param depthbufferSize
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, int depthbufferSize, Object owner )
    {
        assert ( layer != null );
        assert ( dspMode != null );
        assert ( fsaa != null );
        
        /*
        if (dspMode.getNativeMode() == null)
        {
            dspMode = createDisplayMode( layer, dspMode.getWidth(), dspMode.getHeight(), dspMode.getBPP(), dspMode.getFrequency() );
        }
        */

        Class< ? > canvasPeerClass;
        try
        {
            canvasPeerClass = Class.forName( layer.getCanvasPeerImplClassName() );
        }
        catch ( Throwable t )
        {
            throw new Error( t );
        }
        
        Object[] params = new Object[ 6 ];
        
        params[ 0 ] = owner;
        params[ 1 ] = dspMode;
        params[ 2 ] = fullscreen;
        params[ 3 ] = vsync;
        params[ 4 ] = fsaa;
        params[ 5 ] = depthbufferSize;
        
        CanvasPeer canvasPeer;
        try
        {
            canvasPeer = (CanvasPeer)canvasPeerClass.getConstructors()[ 0 ].newInstance( params );
        }
        catch ( Throwable t )
        {
            if ( t.getCause() != null )
                t = t.getCause();
            
            if ( t instanceof Error )
                throw (Error)t;
            else if ( t instanceof RuntimeException )
                throw (RuntimeException)t;
            else
                throw new Error( t.getMessage(), t );
        }
        
        final Canvas3D canvas = new Canvas3D( canvasPeer );
        
        canvas.enableLighting();
        
        return ( canvas );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, Object owner )
    {
        return ( create( layer, dspMode, fullscreen, vsync, fsaa, 16, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, FSAA fsaa, Object owner )
    {
        return ( create( layer, dspMode, fullscreen, DisplayMode.VSYNC_ENABLED, fsaa, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param owner the container to hold this Canvas3D or null
     */
    public Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, Object owner )
    {
        return ( create( layer, dspMode, fullscreen, vsync, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param owner the container to hold this Canvas3D or null
     */
    public Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, Object owner )
    {
        return ( create( layer, dspMode, fullscreen, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param canvasInfo the CanvasConstructionInfo holding all necessary information to create the new Canvas3DWrapper
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( CanvasConstructionInfo canvasInfo, Object owner )
    {
        final Canvas3D canvas = create( canvasInfo.getOpenGLLayer(), canvasInfo.getDisplayMode(), canvasInfo.getFullscreenMode(), canvasInfo.isVSyncEnabled(), canvasInfo.getFSAAMode(), owner );
        
        if ( !canvasInfo.getFullscreenMode().isFullscreen() )
            canvas.setTitle( canvasInfo.getTitle() );
        
        return ( canvas );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param canvasInfo the CanvasConstructionInfo holding all necessary information to create the new Canvas3DWrapper
     * @param windowTitle overrules the title setting of canvasInfo, if non-null
     */
    public static Canvas3D create( CanvasConstructionInfo canvasInfo, String windowTitle )
    {
        final Canvas3D canvas = create( canvasInfo.getOpenGLLayer(), canvasInfo.getDisplayMode(), canvasInfo.getFullscreenMode(), canvasInfo.isVSyncEnabled(), canvasInfo.getFSAAMode(), (Object)null );
        
        if ( !canvasInfo.getFullscreenMode().isFullscreen() )
        {
            if ( windowTitle == null )
                windowTitle = canvasInfo.getTitle();
            
            if ( windowTitle == null )
                canvas.setTitle( "Powered by Xith3D" );
            else
                canvas.setTitle( windowTitle );
        }
        
        return ( canvas );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param canvasInfo the CanvasConstructionInfo holding all necessary information to create the new Canvas3DWrapper
     */
    public static Canvas3D create( CanvasConstructionInfo canvasInfo )
    {
        return ( create( canvasInfo, canvasInfo.getTitle() ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        final Canvas3D canvas = create( layer, dspMode, fullscreen, vsync, fsaa, (Object)null );
        
        canvas.setTitle( title );
        
        return ( canvas );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        final Canvas3D canvas = create( layer, dspMode, fullscreen, fsaa, (Object)null );
        
        canvas.setTitle( title );
        
        return ( canvas );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, boolean vsync, String title )
    {
        return ( create( layer, dspMode, fullscreen, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the chosen DisplayMode
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, DisplayMode dspMode, FullscreenMode fullscreen, String title )
    {
        return ( create( layer, dspMode, fullscreen, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, Object owner )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, vsync, fsaa, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, FSAA fsaa, Object owner )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, fsaa, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, Object owner )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, vsync, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, Object owner )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, Object owner )
    {
        return ( create( layer, createDisplayMode( layer, width, height ), fullscreen, vsync, fsaa, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, FSAA fsaa, Object owner )
    {
        return ( create( layer, createDisplayMode( layer, width, height ), fullscreen, fsaa, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, boolean vsync, Object owner )
    {
        return ( create( layer, createDisplayMode( null, width, height ), fullscreen, vsync, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, Object owner )
    {
        return ( create( layer, createDisplayMode( null, width, height ), fullscreen, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, fsaa, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, int bpp, FullscreenMode fullscreen, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height, bpp ), fullscreen, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height ), fullscreen, vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height ), fullscreen, fsaa, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, boolean vsync, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height ), fullscreen, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width
     * @param height the desired height
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param title the new window's title
     */
    public static Canvas3D create( OpenGLLayer layer, int width, int height, FullscreenMode fullscreen, String title )
    {
        return ( create( layer, createDisplayMode( layer, width, height ), fullscreen, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, Object owner )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, vsync, fsaa, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, FSAA fsaa, Object owner )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, fsaa, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, Object owner )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, vsync, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param owner the container to hold this Canvas3D or null
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, Object owner )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, FSAA.OFF, owner ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, String title )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, FSAA fsaa, String title )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, fsaa, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, boolean vsync, String title )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new Canvas3D.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param bpp the color depth to use
     * @param fullscreen FULLSCREEN, WINDOWED or WINDOWED_UNDECORATED
     * @param title the new window's title
     */
    public static Canvas3D create( int width, int height, int bpp, FullscreenMode fullscreen, String title )
    {
        return ( create( OpenGLLayer.getDefault(), createDisplayMode( OpenGLLayer.getDefault(), width, height, bpp ), fullscreen, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, DisplayMode dspMode, boolean vsync, FSAA fsaa, String title )
    {
        return ( create( layer, dspMode, DisplayMode.WINDOWED, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, DisplayMode dspMode, FSAA fsaa, String title )
    {
        return ( create( layer, dspMode, DisplayMode.WINDOWED, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, DisplayMode dspMode, boolean vsync, String title )
    {
        return ( createWindowed( layer, dspMode, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, DisplayMode dspMode, String title )
    {
        return ( createWindowed( layer, dspMode, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, int bpp, boolean vsync, FSAA fsaa, String title )
    {
        return ( create( layer, width, height, bpp, DisplayMode.WINDOWED, vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, int bpp, FSAA fsaa, String title )
    {
        return ( create( layer, width, height, bpp, DisplayMode.WINDOWED, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, int bpp, boolean vsync, String title )
    {
        return ( createWindowed( layer, width, height, bpp, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, int bpp, String title )
    {
        return ( createWindowed( layer, width, height, bpp, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, boolean vsync, FSAA fsaa, String title )
    {
        return ( createWindowed( layer, width, height, DisplayMode.getDefaultBPP(), vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, FSAA fsaa, String title )
    {
        return ( createWindowed( layer, width, height, DisplayMode.getDefaultBPP(), fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, boolean vsync, String title )
    {
        return ( createWindowed( layer, width, height, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( OpenGLLayer layer, int width, int height, String title )
    {
        return ( createWindowed( layer, width, height, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param fsaa full scene anti aliasing mode
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, int bpp, boolean vsync, FSAA fsaa, String title )
    {
        return ( createWindowed( OpenGLLayer.getDefault(), width, height, bpp, vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, int bpp, FSAA fsaa, String title )
    {
        return ( createWindowed( OpenGLLayer.getDefault(), width, height, bpp, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, int bpp, boolean vsync, String title )
    {
        return ( createWindowed( width, height, bpp, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, int bpp, String title )
    {
        return ( createWindowed( width, height, bpp, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, boolean vsync, FSAA fsaa, String title )
    {
        return ( createWindowed( width, height, DisplayMode.getDefaultBPP(), vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, FSAA fsaa, String title )
    {
        return ( createWindowed( width, height, DisplayMode.getDefaultBPP(), fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, boolean vsync, String title )
    {
        return ( createWindowed( width, height, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( int width, int height, String title )
    {
        return ( createWindowed( width, height, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the container window's title
     */
    public static Canvas3D createWindowed( DisplayMode dspMode, boolean vsync, FSAA fsaa, String title )
    {
        return ( createWindowed( getOpenGLLayer( dspMode ), dspMode, vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param fsaa full scene anti aliasing mode
     * @param title the container window's title
     */
    public static Canvas3D createWindowed( DisplayMode dspMode, FSAA fsaa, String title )
    {
        return ( createWindowed( getOpenGLLayer( dspMode ), dspMode, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     * @param title the container window's title
     */
    public static Canvas3D createWindowed( DisplayMode dspMode, boolean vsync, String title )
    {
        return ( createWindowed( getOpenGLLayer( dspMode ), dspMode, vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param title the container window's title
     */
    public static Canvas3D createWindowed( DisplayMode dspMode, String title )
    {
        return ( createWindowed( getOpenGLLayer( dspMode ), dspMode, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D with desktop resolution.
     * 
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( boolean vsync, FSAA fsaa, String title )
    {
        final Dimension dtsSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        return ( createWindowed( createDisplayMode( OpenGLLayer.getDefault(), dtsSize.width, dtsSize.height ), vsync, fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D with desktop resolution.
     * 
     * @param fsaa full scene anti aliasing mode
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( FSAA fsaa, String title )
    {
        final Dimension dtsSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        return ( createWindowed( createDisplayMode( OpenGLLayer.getDefault(), dtsSize.width, dtsSize.height ), fsaa, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D with desktop resolution.
     * 
     * @param vsync v-sync enabled or not
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( boolean vsync, String title )
    {
        return ( createWindowed( vsync, FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new WINDOWED Canvas3D with desktop resolution.
     * 
     * @param title the new window's title
     */
    public static Canvas3D createWindowed( String title )
    {
        return ( createWindowed( FSAA.OFF, title ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, DisplayMode dspMode, boolean vsync, FSAA fsaa )
    {
        return ( create( layer, dspMode, DisplayMode.FULLSCREEN, vsync, fsaa, (Object)null ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, DisplayMode dspMode, FSAA fsaa )
    {
        return ( create( layer, dspMode, DisplayMode.FULLSCREEN, fsaa, (Object)null ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, DisplayMode dspMode, boolean vsync )
    {
        return ( createFullscreen( layer, dspMode, vsync, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param dspMode the DisplayMode to use
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, DisplayMode dspMode )
    {
        return ( createFullscreen( layer, dspMode, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height, int bpp, boolean vsync, FSAA fsaa )
    {
        return ( create( layer, width, height, bpp, DisplayMode.FULLSCREEN, vsync, fsaa, (Object)null ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height, int bpp, FSAA fsaa )
    {
        return ( create( layer, width, height, bpp, DisplayMode.FULLSCREEN, fsaa, (Object)null ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     * @param vsync v-sync enabled or not
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height, int bpp, boolean vsync )
    {
        return ( createFullscreen( layer, width, height, bpp, vsync, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param bpp the color depth to use
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height, int bpp )
    {
        return ( createFullscreen( layer, width, height, bpp, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height, boolean vsync, FSAA fsaa )
    {
        return ( createFullscreen( layer, width, height, DisplayMode.getDefaultBPP(), vsync, fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height, FSAA fsaa )
    {
        return ( createFullscreen( layer, width, height, DisplayMode.getDefaultBPP(), fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height, boolean vsync )
    {
        return ( createFullscreen( layer, width, height, vsync, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param layer the OpenGL layer to use
     * @param width the desired width to use
     * @param height the desired height to use
     */
    public static Canvas3D createFullscreen( OpenGLLayer layer, int width, int height )
    {
        return ( createFullscreen( layer, width, height, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( int width, int height, boolean vsync, FSAA fsaa )
    {
        return ( createFullscreen( OpenGLLayer.getDefault(), width, height, vsync, fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( int width, int height, FSAA fsaa )
    {
        return ( createFullscreen( OpenGLLayer.getDefault(), width, height, fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     * @param vsync v-sync enabled or not
     */
    public static Canvas3D createFullscreen( int width, int height, boolean vsync )
    {
        return ( createFullscreen( width, height, vsync, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param width the desired width to use
     * @param height the desired height to use
     */
    public static Canvas3D createFullscreen( int width, int height )
    {
        return ( createFullscreen( width, height, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( DisplayMode dspMode, boolean vsync, FSAA fsaa )
    {
        return ( createFullscreen( getOpenGLLayer( dspMode ), dspMode, vsync, fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( DisplayMode dspMode, FSAA fsaa )
    {
        return ( createFullscreen( getOpenGLLayer( dspMode ), dspMode, fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     * @param vsync v-sync enabled or not
     */
    public static Canvas3D createFullscreen( DisplayMode dspMode, boolean vsync )
    {
        return ( createFullscreen( getOpenGLLayer( dspMode ), dspMode, vsync, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D.
     * 
     * @param dspMode the DisplayMode to use
     */
    public static Canvas3D createFullscreen( DisplayMode dspMode )
    {
        return ( createFullscreen( getOpenGLLayer( dspMode ), dspMode, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D with desktop resolution.
     * 
     * @param vsync v-sync enabled or not
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( boolean vsync, FSAA fsaa )
    {
        final Dimension dtsSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        return ( createFullscreen( createDisplayMode( OpenGLLayer.getDefault(), dtsSize.width, dtsSize.height ), vsync, fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D with desktop resolution.
     * 
     * @param fsaa full scene anti aliasing mode
     */
    public static Canvas3D createFullscreen( FSAA fsaa )
    {
        final Dimension dtsSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        return ( createFullscreen( createDisplayMode( OpenGLLayer.getDefault(), dtsSize.width, dtsSize.height ), fsaa ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D with desktop resolution.
     * 
     * @param vsync v-sync enabled or not
     */
    public static Canvas3D createFullscreen( boolean vsync )
    {
        return ( createFullscreen( vsync, FSAA.OFF ) );
    }
    
    /**
     * Creates a new FULLSCREEN Canvas3D with desktop resolution.
     */
    public static Canvas3D createFullscreen()
    {
        return ( createFullscreen( FSAA.OFF ) );
    }
    
    private Canvas3DFactory()
    {
    }
}
