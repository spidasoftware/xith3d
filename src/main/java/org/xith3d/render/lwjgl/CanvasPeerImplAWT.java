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
package org.xith3d.render.lwjgl;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.impl.awt.AWTCursorConverter;
import org.jagatoo.input.impl.mixed.AWTJInputInputDeviceFactory;
import org.jagatoo.input.render.Cursor;
import org.jagatoo.logging.ProfileTimer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.RenderPass;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * The CanvasPeer implementation for the LightWeight Java Game Library (LWJGL)
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class CanvasPeerImplAWT extends CanvasPeerImplBase
{
    private Frame window;
    private ContextGLCanvas glCanvas;
    
    private int left = 0;
    private int top = 0;
    
    private long lastKnownFrameId = -1L;
    
    private boolean closeRequested = false;
    private boolean isRendering = false;
    private boolean displayModeChanged = false;
    
    private AWTJInputInputDeviceFactory inputDeviceFactory = null;
    
    public AWTJInputInputDeviceFactory getInputDeviceFactory( InputSystem inputSystem )
    {
        if ( inputDeviceFactory == null )
        {
            inputDeviceFactory = new AWTJInputInputDeviceFactory( this, inputSystem.getEventQueue() );
        }
        
        return ( inputDeviceFactory );
    }
    
    public final ContextGLCanvas getDrawable()
    {
        return ( glCanvas );
    }
    
    public void refreshCursor( org.jagatoo.input.devices.Mouse mouse )
    {
        if ( getCursor() == null )
        {
            glCanvas.setCursor( AWTCursorConverter.HIDDEN_CURSOR );
        }
        else if ( getCursor() == Cursor.DEFAULT_CURSOR )
        {
            glCanvas.setCursor( java.awt.Cursor.getDefaultCursor() );
        }
        else// if ( getCursor() != null )
        {
            AWTCursorConverter.convertCursor( getCursor() );
            
            glCanvas.setCursor( (java.awt.Cursor)getCursor().getCursorObject() );
        }
    }
    
    public final boolean receivesInputEvents()
    {
        return ( glCanvas.hasFocus() );
    }
    
    public CanvasPeerImplAWT( Object owner, DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, int depthBufferSize )
    {
        super( displayMode, fullscreen, vsync, fsaa, depthBufferSize );
        
        if ( owner == null )
        {
            String message = "The " + this.getClass().getSimpleName() + " must be used with an owner (integrated into an AWT/Swing environment).";
            
            X3DLog.error( message );
            throw new Error( message );
        }
        
        assert ( displayMode != null );
        
        displayMode = getDisplayMode();
        
        try
        {
            System.setProperty( "org.xith3d.render.lwjgl.displayGLInfos", String.valueOf( false ) );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        
        try
        {
            this.glCanvas = new ContextGLCanvas( new PixelFormat( 0, depthBufferSize, 8, fsaa.getIntValue() ) );
            this.setVSyncEnabled( vsync );
            glCanvas.setBounds( 0, 0, displayMode.getWidth(), displayMode.getHeight() );
            
            glCanvas.setFocusable( true );
            glCanvas.requestFocus();
        }
        catch ( Throwable t )
        {
            if ( t instanceof Error )
                throw (Error)t;
            else if ( t instanceof RuntimeException )
                throw (RuntimeException)t;
            else
                throw new Error( t.getMessage(), t );
        }
        
        // boomschakalacka:
        // FIXME: this code is unreachable as Exception is thrown in line 135 if owner is null!
        if ( owner == null )
        {
            this.window = new Frame( "Xith3D (LWJGL)" );
            window.setLayout( null );
            if ( fullscreen.isFullscreen() )
            {
                window.setBackground( Color.BLACK );
            }
            window.setUndecorated( fullscreen != FullscreenMode.WINDOWED );
            window.setSize( displayMode.getWidth(), displayMode.getHeight() );
            window.addWindowListener( new WindowAdapter()
            {
                @Override
                public void windowClosing( WindowEvent e )
                {
                    closeRequested = true;
                }
            } );
            
            final boolean exclusive = ( fullscreen.isFullscreen() && ( displayMode.getNativeMode() != null ) );
            
            window.add( glCanvas );
            window.setVisible( true );
            
            if ( !exclusive )
            {
                Thread.yield();
                
                final Dimension frameSize;
                if ( !fullscreen.isFullscreen() )
                {
                    Insets insets = window.getInsets();
                    glCanvas.setLocation( insets.left, insets.top );
                    frameSize = new Dimension( displayMode.getWidth() + insets.left + insets.right, displayMode.getHeight() + insets.top + insets.bottom );
                    window.setSize( frameSize );
                }
                else
                {
                    frameSize = new Dimension( displayMode.getWidth(), displayMode.getHeight() );
                }
                
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                Point upperLeft = new Point( ( screenSize.width - frameSize.width ) / 2, ( screenSize.height - frameSize.height ) / 2 );
                window.setLocation( upperLeft );
                
                //frame.setResizable( false/* !fullscreen */);
            }
            else
            {
                final java.awt.DisplayMode awtMode = (java.awt.DisplayMode)displayMode.getNativeMode();
                
                GraphicsDevice graphDev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                
                graphDev.setFullScreenWindow( window );
                graphDev.setDisplayMode( awtMode );
            }
        }
        else
        {
            glCanvas.setBounds( 0, 0, displayMode.getWidth(), displayMode.getHeight() );
            ( (Container)owner ).add( glCanvas );
        }
        
        Thread.yield();
        
        // boomschakalacka:
        // FIXME: this code is unreachable as Exception is thrown in line 135 if owner is null!
        if ( owner == null )
        {
            /*
            java.awt.Point loc = glCanvas.getLocation();
            glCanvas.setLocation( loc.x + 1, loc.y + 1 );
            */
            
            (window).setResizable( false );
            
            java.awt.Dimension size = window.getSize();
            window.setSize( size.width - 2, size.height - 2 );
        }
    }
    
    private class ContextGLCanvas extends AWTGLCanvas
    {
        private static final long serialVersionUID = 521768945921758405L;
        
        private View view;
        private List< RenderPass > renderPasses;
        private boolean layeredMode;
        private long frameId = -1L;
        private long nanoTime = 0L;
        private long nanoStep = -1L;
        private PickRequest pickRequest;
        private Object pickResult;
        
        @Override
        protected void initGL()
        {
            synchronized ( getRenderLock() )
            {
                isRendering = true;
                
                clear();
                
                try
                {
                    CanvasPeerImplAWT.this.init();
                }
                catch ( Throwable t )
                {
                    t.printStackTrace();
                }
                
                isRendering = false;
            }
        }

        public void finish()
        {
            try {
                this.swapBuffers();
            } catch (LWJGLException ex) {

            }
        }
        
        @Override
        protected void paintGL()
        {
            if ( !isInitialized() )
                return;
            
            synchronized ( getRenderLock() )
            {
                isRendering = true;
                
                if ( closeRequested )
                {
                    closeRequested = false;
                    fireClosingEvent();
                }
                
                if ( ( frameId < 0L ) || ( frameId <= lastKnownFrameId ) )
                {
                    isRendering = false;
                    return;
                }
                
                lastKnownFrameId = frameId;
                
                pickResult = doRender( view, renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest );
                
                /*
                this.view = null;
                this.bgCache = null;
                this.renderPasses = null;
                this.layeredMode = true;
                this.frameId = -1L;
                */
                
                try
                {
                    swapBuffers();
                }
                catch ( LWJGLException e )
                {
                    e.printStackTrace();
                }
                
                isRendering = false;
            }
        }
        
        public void repaint( View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
        {
            this.view = view;
            this.renderPasses = renderPasses;
            this.layeredMode = layeredMode;
            this.frameId = frameId;
            this.nanoTime = nanoTime;
            this.nanoStep = nanoStep;
            this.pickRequest = pickRequest;
            this.pickResult = null;
            
            repaint();
        }
        
        /*
        public ContextGLCanvas( GraphicsDevice graphDev, PixelFormat pixelFormat ) throws LWJGLException
        {
            super( graphDev, pixelFormat );
        }
        */
        
        public ContextGLCanvas( PixelFormat pixelFormat ) throws LWJGLException
        {
            super( pixelFormat );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OpenGLLayer getType()
    {
        return ( OpenGLLayer.LWJGL_AWT  );
    }
    
    @Override
    protected final Class< ? > getExpectedNativeDisplayModeClass()
    {
        return ( java.awt.DisplayMode.class );
    }
    
    @Override
    protected void applyVSync()
    {
        glCanvas.setVSyncEnabled( isVSyncEnabled() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setVSyncEnabled( boolean vsync )
    {
        super.setVSyncEnabled( vsync );
        
        vsyncSwitched = true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Frame getWindow()
    {
        return ( window );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final AWTGLCanvas getComponent()
    {
        return ( glCanvas );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setIcon( URL iconResource ) throws IOException
    {
        assert ( window instanceof Frame ) : "Window isn't a Frame";
        
        window.setIconImage( ImageIO.read( iconResource ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTitle( String title )
    {
        window.setTitle( title );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTitle()
    {
        return ( window.getTitle() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean setLocation( int x, int y )
    {
        if ( ( window.getLocation().x != x ) || ( window.getLocation().y != y ) )
        {
            window.setLocation( x, y );
            
            this.left = x;
            this.top = y;
            
            return ( true );
        }
        
        return ( false );
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getLeft()
    {
        return ( left );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getTop()
    {
        return ( top );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean setSize( int width, int height )
    {
        if ( ( glCanvas.getSize().width != width ) || ( glCanvas.getSize().height != height ) )
        {
            glCanvas.setSize( width, height );
            
            if ( getWindow() != null )
            {
                Insets insets = getWindow().getInsets();
                glCanvas.setLocation( insets.left, insets.top );
                Dimension frameSize = new Dimension( width + insets.left + insets.right, height + insets.top + insets.bottom );
                getWindow().setSize( frameSize );
            }
            
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getWidth()
    {
        return ( glCanvas.getWidth() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getHeight()
    {
        return ( glCanvas.getHeight() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean setDisplayModeImpl( DisplayMode displayMode )
    {
        //final boolean result = !displayMode.equals( getDisplayMode() );
        final boolean result = true;
        
        if ( result )
            displayModeChanged = true;
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setGamma( float gamma, float brightness, float contrast )
    {
        super.setGamma( gamma, brightness, contrast );
        
        try
        {
            Display.setDisplayConfiguration( gamma, brightness, contrast );
        }
        catch ( LWJGLException e )
        {
            e.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isRendering()
    {
        return ( isRendering );
    }
    
    @Override
    protected Thread makeCurrent()
    {
        try
        {
            //glCanvas.makeCurrent();
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }
        
        return ( Thread.currentThread() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeThreadChanged()
    {
        if ( ( renderingThread != null ) || ( getRenderedFrames() == 0L ) )
        {
            try
            {
                //glCanvas.releaseContext();
            }
            catch ( Throwable e )
            {
                e.printStackTrace();
            }
            renderingThread = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object initRenderingImpl( View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        if ( displayModeChanged )
        {
            try
            {
                Display.setDisplayMode( getNativeDisplayMode() );
            }
            catch ( LWJGLException e )
            {
                e.printStackTrace();
            }
            
            displayModeChanged = false;
        }
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "CanvasPeerImpl::render" );
        
        glCanvas.repaint( view, renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest );
        
        Object result = glCanvas.pickResult;
        glCanvas.pickResult = null;
        
        ProfileTimer.endProfile();
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy()
    {
        //super.destroy();
        
        if ( window != null )
            window.dispose();
    }
}
