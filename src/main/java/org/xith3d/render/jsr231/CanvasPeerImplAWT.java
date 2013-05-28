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
package org.xith3d.render.jsr231;

import java.awt.Color;
import java.awt.Component;
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
import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesChooser;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.TraceGL;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.impl.awt.AWTCursorConverter;
import org.jagatoo.input.impl.mixed.AWTJInputInputDeviceFactory;
import org.jagatoo.input.render.Cursor;
import org.jagatoo.logging.ProfileTimer;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.RenderPass;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

import com.sun.opengl.util.Gamma;

/**
 * The CanvasPeer implementation for the official Java OpenGL Bindings (JOGL)
 * and AWT.
 * 
 * @author David Yazel [jogl]
 * @author Marvin Froehlich (aka Qudus)
 */
public class CanvasPeerImplAWT extends CanvasPeerImplBase implements GLEventListener
{
    private static final int RENDER_MODE_CLEAR = 1;
    private static final int RENDER_MODE_DESTROY = 2;
    
    private GLCapabilities gc;
    private GLCapabilitiesChooser gcc;
    private GLCanvas glCanvas;
    private GL gl;
    private GL plainGL;
    
    private View view;
    private List< RenderPass > renderPasses;
    private boolean layeredMode;
    private long frameId;
    private long nanoTime;
    private long nanoStep;
    private PickRequest pickRequest = null;
    private Object pickResult = null;
    
    private int renderMode = 0;
    private boolean isRendering = false;
    
    private Frame window;
    private boolean closeRequested = false;
    
    private AWTJInputInputDeviceFactory inputDeviceFactory = null;
    
    public AWTJInputInputDeviceFactory getInputDeviceFactory( InputSystem inputSystem )
    {
        if ( inputDeviceFactory == null )
        {
            inputDeviceFactory = new AWTJInputInputDeviceFactory( this, inputSystem.getEventQueue() );
        }
        
        return ( inputDeviceFactory );
    }
    
    public final GLCanvas getDrawable()
    {
        return ( glCanvas );
    }
    
    public void refreshCursor( org.jagatoo.input.devices.Mouse mouse )
    {
        if ( !mouse.isAbsolute() || ( getCursor() == null ) )
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
        
        try
        {
            System.setProperty( "org.xith3d.render.jsr231.displayGLInfos", String.valueOf( false ) );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        
        this.gc = new GLCapabilities();
        gc.setStencilBits( 8 );
        //gc.setAlphaBits( 8 );
        gc.setDepthBits( depthBufferSize );
        if ( fsaa != FSAA.OFF )
        {
            gc.setSampleBuffers( true );
        }
        gc.setNumSamples( fsaa.getIntValue() );
        
        this.gcc = new OldStyleGLCapabilitiesChooser();
        
        this.glCanvas = new GLCanvas( gc, gcc, null, null );
        //glCanvas.setBounds( 0, 0, getDisplayMode().getWidth(), getDisplayMode().getHeight() );
        glCanvas.setBounds( 0, 0, displayMode.getWidth(), displayMode.getHeight() );
        setVSyncEnabled( vsync );
        
        //canvas = GLDrawableFactory.getFactory().createGLCanvas( gc, gcc );
        //canvas.setNoAutoRedrawMode( true );
        glCanvas.addGLEventListener( this );
        glCanvas.setFocusable( true );
        
        this.gl = glCanvas.getGL();
        updateTraceDebug();
        clear();
        
        Point upperLeft = null;
        
        if ( owner == null )
        {
            Frame frame = new Frame( "Xith3D (JOGL)" );
            frame.setLayout( null );
            if ( fullscreen.isFullscreen() )
            {
                frame.setBackground( Color.BLACK );
            }
            frame.setUndecorated( fullscreen != FullscreenMode.WINDOWED );
            frame.setSize( displayMode.getWidth(), displayMode.getHeight() );
            setWindow( frame );
            frame.addWindowListener( new WindowAdapter()
            {
                @Override
                public void windowClosing( WindowEvent e )
                {
                    closeRequested = true;
                }
            } );
            
            final boolean exclusive = ( fullscreen.isFullscreen() && ( displayMode.getNativeMode() != null ) );
            
            frame.add( glCanvas );
            frame.setVisible( true );
            
            if ( !exclusive )
            {
                Thread.yield();
                
                final Dimension frameSize;
                if ( !fullscreen.isFullscreen() )
                {
                    Insets insets = frame.getInsets();
                    glCanvas.setLocation( insets.left, insets.top );
                    frameSize = new Dimension( displayMode.getWidth() + insets.left + insets.right, displayMode.getHeight() + insets.top + insets.bottom );
                    frame.setSize( frameSize );
                }
                else
                {
                    frameSize = new Dimension( displayMode.getWidth(), displayMode.getHeight() );
                }
                
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                upperLeft = new Point( ( screenSize.width - frameSize.width ) / 2, ( screenSize.height - frameSize.height ) / 2 );
                frame.setLocation( upperLeft );
                
                //frame.setResizable( false/* !fullscreen */);
            }
            else
            {
                final java.awt.DisplayMode awtMode = (java.awt.DisplayMode)displayMode.getNativeMode();
                
                GraphicsDevice graphDev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                
                graphDev.setFullScreenWindow( frame );
                graphDev.setDisplayMode( awtMode );
            }
        }
        else
        {
            glCanvas.setBounds( 0, 0, displayMode.getWidth(), displayMode.getHeight() );
            ( (Container)owner ).add( glCanvas );
        }
        
        Thread.yield();
        
        if ( owner == null )
        {
            /*
            java.awt.Point loc = glCanvas.getLocation();
            glCanvas.setLocation( loc.x + 1, loc.y + 1 );
            */
            
            ((Frame)window).setResizable( false );
            
            java.awt.Dimension size = window.getSize();
            window.setSize( size.width - 2, size.height - 2 );
            
            Thread.yield();
            
            if ( ( window.getLocation().x == 0 ) && ( window.getLocation().y == 0 ) && ( upperLeft != null ) )
                window.setLocation( upperLeft );
        }
    }
    
    protected void updateTraceDebug()
    {
        if ( !( gl instanceof DebugGL || gl instanceof TraceGL ) )
            plainGL = gl;
        
        final boolean needsDebugGL = getRenderPeer().getRenderOptions().areGLErrorChecksEnabled();
        final boolean needsTraceGL = getRenderPeer().getRenderOptions().isGLTracingEnabled();
        
        if ( ( needsTraceGL && needsDebugGL ) && !( gl instanceof TraceGL ) )
        {
            X3DLog.debug( "OpenGL error check and command tracing are enabled" );
            gl = new TraceGL( new DebugGL( plainGL ), System.err );
        }
        else if ( needsTraceGL && !( gl instanceof TraceGL ) )
        {
            X3DLog.debug( "OpenGL command tracing is enabled" );
            gl = new TraceGL( plainGL, System.err );
        }
        else if ( needsDebugGL && !( gl instanceof DebugGL ) )
        {
            X3DLog.debug( "OpenGL error check is enabled" );
            gl = new DebugGL( plainGL );
        }
        else
            gl = plainGL;
        
        glCanvas.setGL( gl );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void init( GLAutoDrawable drawable )
    {
        this.gl = glCanvas.getGL();
        updateTraceDebug();
        
        /*
         * The GLCanvas recreates the whole GL-context when it is reinitialized.
         * This can happen at anytime, for example during a hide/show/resize
         * on their container. We need to destroy the GL-names to make them be
         * recreated on the next render.
         */
        destroyGLNames( false );
        
        super.init();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OpenGLLayer getType()
    {
        return ( OpenGLLayer.JOGL_AWT  );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setVSyncEnabled( boolean vsync )
    {
        super.setVSyncEnabled( vsync );
        
        setSwapInterval( vsync ? 1 : 0 );
    }
    
    @Override
    public final GL getGL()
    {
        return ( gl );
    }
    
    protected final void setWindow( Frame w )
    {
        this.window = w;
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
    public final Component getComponent()
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
        
        ( (Frame)window ).setIconImage( ImageIO.read( iconResource ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTitle( String title )
    {
        if ( window != null )
        {
            assert window instanceof Frame : "Window isn't a Frame";
            ( (Frame)window ).setTitle( title );
        }
        else
        {
            // Do nothing...
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTitle()
    {
        if ( window == null )
            return ( "" );
        
        assert window instanceof Frame : "Window isn't a Frame";
        
        return ( ( (Frame)window ).getTitle() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean setLocation( int x, int y )
    {
        if ( ( window.getLocation().x == x ) && ( window.getLocation().y == y ) )
            return ( false );
        
        window.setLocation( x, y );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getLeft()
    {
        return ( glCanvas.getLocationOnScreen().x );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getTop()
    {
        return ( glCanvas.getLocationOnScreen().y );
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
            
            setDisplayMode( new DisplayMode( getType(), null, width, height, getBPP(), getFrequency() ) );
            
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
    public final boolean setFullscreen( boolean fullscreen )
    {
        final boolean oldState = super.setFullscreen( fullscreen );
        
        if ( getFullscreenSwitchRequest() != null )
        {
            final boolean newFSMode = getFullscreenSwitchRequest().booleanValue();
            final boolean exclusive = ( newFSMode && ( getDisplayMode().getNativeMode() != null ) );
            
            if ( !exclusive )
            {
                GraphicsDevice graphDev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                
                graphDev.setFullScreenWindow( null );
                
                Thread.yield();
                
                final Dimension frameSize;
                if ( !newFSMode )
                {
                    Insets insets = window.getInsets();
                    glCanvas.setLocation( insets.left, insets.top );
                    frameSize = new Dimension( getWidth() + insets.left + insets.right, getHeight() + insets.top + insets.bottom );
                    window.setSize( frameSize );
                }
                else
                {
                    frameSize = new Dimension( getWidth(), getHeight() );
                }
                
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                Point upperLeft = new Point( ( screenSize.width - frameSize.width ) / 2, ( screenSize.height - frameSize.height ) / 2 );
                window.setLocation( upperLeft );
                
                //frame.setResizable( false/* !fullscreen */);
            }
            else
            {
                final java.awt.DisplayMode awtMode = (java.awt.DisplayMode)getDisplayMode().getNativeMode();
                
                GraphicsDevice graphDev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                
                graphDev.setFullScreenWindow( window );
                graphDev.setDisplayMode( awtMode );
            }
            
            if ( !newFSMode )
            {
                final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                setLocation( ( ( screenSize.width - getWidth() ) / 2 ), ( ( screenSize.height - getHeight() ) / 2 ) );
            }
            
            resetFullscreenSwitchRequest();
        }
        
        return ( oldState );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean setDisplayModeImpl( DisplayMode displayMode )
    {
        //final boolean result = !displayMode.equals( getDisplayMode() );
        final boolean result = true;
        
        /*
        if ( result )
            displayModeChanged = true;
        */
        
        setSize( displayMode.getWidth(), displayMode.getHeight() );
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void reshape( GLAutoDrawable drawable, int x, int y, int width, int height )
    {
        this.gl = drawable.getGL();
        updateTraceDebug();
        
        /*
        ((GLCanvas)drawable).repaint();
        
        gl.glViewport( 0, 0, width, height ); // Reset The Current Viewport
         
        gl.glMatrixMode( GL.GL_PROJECTION );  // Select The Projection Matrix
        gl.glLoadIdentity();                // Reset The Projection Matrix
         
        // Calculate The Aspect Ratio Of The Window
        glu.gluPerspective( 54.0f, width / height, 0.001f, 100.0f );
        
        gl.glMatrixMode( GL.GL_MODELVIEW );   // Select The Modelview Matrix
        gl.glLoadIdentity();
        */
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
            Gamma.setDisplayGamma( gamma, brightness, contrast );
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void displayChanged( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged )
    {
    }
    
    @Override
    protected void setAutoSwapBufferMode( boolean mode )
    {
        glCanvas.setAutoSwapBufferMode( mode );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isRendering()
    {
        return ( isRendering );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeThreadChanged()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public final void display( GLAutoDrawable drawable )
    {
        synchronized ( getRenderLock() )
        {
            if ( !isInitialized() || isRendering )
                return;
            
            isRendering = true;
            /*
             * Reset the ShapeAtomPeer's TransformGroup-id indicator to setup the
             * modelview matrix at least once per frame.
             */
            ShapeAtomPeer.reset();
            
            if ( closeRequested )
            {
                closeRequested = false;
                fireClosingEvent();
            }
            
            this.gl = drawable.getGL();
            updateTraceDebug();
            
            if ( swapIntervalChanged )
            {
                swapIntervalChanged = false;
                gl.setSwapInterval( getSwapInterval() );
            }
            
            if ( renderMode != 0 )
            {
                boolean proceed = true;
                
                if ( ( renderMode & RENDER_MODE_CLEAR ) != 0 )
                {
                    super.clear();
                    
                    isRendering = false;
                    
                    proceed = false;
                }
                
                if ( ( renderMode & RENDER_MODE_DESTROY ) != 0 )
                {
                    destroy();
                    
                    isRendering = false;
                    
                    proceed = false;
                }
                
                if ( !proceed )
                    return;
            }
            
            pickResult = doRender( view, renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest );
            
            isRendering = false;
        }
    }

    @Override
    public void finish()
    {
        glCanvas.display();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object initRenderingImpl( View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "CanvasPeerImpl::initRendering" );
        
        this.renderMode = 0;
        
        this.view = view;
        this.renderPasses = renderPasses;
        this.layeredMode = layeredMode;
        this.frameId = frameId;
        this.nanoTime = nanoTime;
        this.nanoStep = nanoStep;
        this.pickRequest = pickRequest;
        this.pickResult = null;
        
//        glCanvas.display();
        
        Object result = this.pickResult;
        this.pickResult = null;
        
        ProfileTimer.endProfile();
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        renderMode |= RENDER_MODE_CLEAR;
        glCanvas.display();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy()
    {
        if ( ( renderMode & RENDER_MODE_DESTROY ) == 0 )
        {
            renderMode |= RENDER_MODE_CLEAR | RENDER_MODE_DESTROY;
            
            if ( isGammaChanged )
            {
                try
                {
                    Gamma.resetDisplayGamma();
                }
                catch ( IllegalArgumentException e )
                {
                    // if gamma has not been corrected, this method throws an IllegalArgumentException!
                    //e.printStackTrace();
                }
            }
            
            // FIXME: NVIDIA drivers don't seem to like this
            //glCanvas.getContext().destroy();
            
            /*
             * This may be a possible solution for destroying the glCanvas.
             */
            GLContext context = glCanvas.getContext();
            if ( context.makeCurrent() != GLContext.CONTEXT_NOT_CURRENT )
            {
                context.release();
                context.destroy();
            }
            
            glCanvas.display();
        }
        else
        {
            super.destroy();
            
            if ( getWindow() != null )
                getWindow().dispose();
        }
    }
}
