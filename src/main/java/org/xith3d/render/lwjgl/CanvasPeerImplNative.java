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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import org.jagatoo.image.SharedBufferedImage;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.impl.lwjgl.LWJGLCursorConverter;
import org.jagatoo.input.impl.lwjgl.LWJGLMessageProcessor;
//import org.jagatoo.input.impl.lwjgl.LWJGLInputDeviceFactory;
import org.jagatoo.input.impl.mixed.LWJGLJInputInputDeviceFactory;
import org.jagatoo.input.render.Cursor;
import org.jagatoo.logging.ProfileTimer;
import org.jagatoo.util.image.ImageUtility;
import org.jagatoo.util.nio.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.RenderPass;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
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
 * @author Amos Wenger (aka BlueSky)
 */
public class CanvasPeerImplNative extends CanvasPeerImplBase
{
    private static final boolean NON_INTERACTIVE_MODE = false;
    
    private int left = 0;
    private int top = 0;
    private int width;
    private int height;
    private boolean displayModeChanged = false;
    
    private boolean isRendering = false;
    
    private static final long PROCESS_MESSAGE_TIME_INTERVAL = 100000000L; // 100 milli-seconds
    
    private long lastIsCloseRequestedQueryTime = 0L;
    
    private LWJGLJInputInputDeviceFactory inputDeviceFactory = null;
    
    public LWJGLJInputInputDeviceFactory getInputDeviceFactory( InputSystem inputSystem )
    {
        if ( inputDeviceFactory == null )
        {
            //inputDeviceFactory = new LWJGLInputDeviceFactory( this, inputSystem.getEventQueue() );
            inputDeviceFactory = new LWJGLJInputInputDeviceFactory( this, inputSystem.getEventQueue() );
        }
        
        return ( inputDeviceFactory );
    }
    
    public final Object getDrawable()
    {
        return ( null );
    }
    
    public void refreshCursor( org.jagatoo.input.devices.Mouse mouse )
    {
        try
        {
            if ( !org.lwjgl.input.Mouse.isCreated() )
                org.lwjgl.input.Mouse.create();
            
            if ( getCursor() == null )
            {
                if ( mouse.isAbsolute() )
                    org.lwjgl.input.Mouse.setGrabbed( true );
            }
            else
            {
                if ( mouse.isAbsolute() )
                    org.lwjgl.input.Mouse.setGrabbed( false );
            }
            
            if ( getCursor() == Cursor.DEFAULT_CURSOR )
            {
                org.lwjgl.input.Mouse.setNativeCursor( null );
            }
            else if ( getCursor() != null )
            {
                LWJGLCursorConverter.convertCursor( getCursor() );
                
                org.lwjgl.input.Mouse.setNativeCursor( (org.lwjgl.input.Cursor)getCursor().getCursorObject() );
            }
        }
        catch ( org.lwjgl.LWJGLException e )
        {
            throw new Error( e );
        }
    }
    
    public final boolean receivesInputEvents()
    {
        return ( true );
    }
    
    /**
     * 
     * @param owner
     * @param displayMode
     * @param fullscreen
     * @param vsync
     * @param fsaa
     * @param depthBufferSize
     */
    public CanvasPeerImplNative( Object owner, DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, int depthBufferSize )
    {
        super( displayMode, fullscreen, vsync, fsaa, depthBufferSize );
        
        try
        {
            if ( this.isUndecorated() )
                System.setProperty( "org.lwjgl.opengl.Window.undecorated", String.valueOf( true ) );
            else
                System.setProperty( "org.lwjgl.opengl.Window.undecorated", String.valueOf( false ) );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        
        try
        {
            System.setProperty( "org.xith3d.render.lwjgl.displayGLInfos", String.valueOf( false ) );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        
        this.width = getDisplayMode().getWidth();
        this.height = getDisplayMode().getHeight();
        
        try
        {
            Display.setDisplayMode( getNativeDisplayMode() );
            Display.setFullscreen( fullscreen.isFullscreen() );
            Display.setTitle( "Xith3D (LWJGL)" );
            Display.setVSyncEnabled( vsync );
            Display.create( new PixelFormat( 0, depthBufferSize, 8, fsaa.getIntValue() ) );
            
            if ( !fullscreen.isFullscreen() )
            {
                final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                setLocation( ( ( screenSize.width - width ) / 2 ), ( ( screenSize.height - height ) / 2 ) );
            }
            
            clear();
            
            beforeThreadChanged();
            
            init();
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
        
        if ( NON_INTERACTIVE_MODE )
        {
            LWJGLMessageProcessor.allowOneUpdate();
        }
    }
    
    @Override
    protected final void init() throws Throwable
    {
        Display.makeCurrent();
        
        super.init();
        
        Display.releaseContext();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OpenGLLayer getType()
    {
        return ( OpenGLLayer.LWJGL  );
    }
    
    @Override
    protected void applyVSync()
    {
        Display.setVSyncEnabled( isVSyncEnabled() );
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
    public final Object getWindow()
    {
        //throw new UnsupportedOperationException( "LWJGL does not create a Window." );
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Object getComponent()
    {
        //throw new UnsupportedOperationException( "LWJGL does not create a Component." ) );
        return ( null );
    }
    
    private static ByteBuffer[] createIcon( URL url ) throws IOException
    {
        BufferedImage srcImg = ImageIO.read( url );
        
        String osName = System.getProperty( "os.name" ).toLowerCase();
        
        ByteBuffer[] bbs;
        if ( osName.indexOf( "windows" ) >= 0 )
        {
            SharedBufferedImage trgImg0 = SharedBufferedImage.create( 16, 16, 4, true, new int[] { 0, 1, 2, 3 }, null );
            
            ImageUtility.scaleImage( srcImg, trgImg0 );
            
            ByteBuffer bb0 = BufferUtils.createByteBuffer( trgImg0.getWidth() * trgImg0.getHeight() * 4 );
            bb0.put( trgImg0.getSharedData() );
            bb0.flip();
            
            SharedBufferedImage trgImg1 = SharedBufferedImage.create( 32, 32, 4, true, new int[] { 0, 1, 2, 3 }, null );
            
            ImageUtility.scaleImage( srcImg, trgImg1 );
            
            ByteBuffer bb1 = BufferUtils.createByteBuffer( trgImg1.getWidth() * trgImg1.getHeight() * 4 );
            bb1.put( trgImg1.getSharedData() );
            bb1.flip();
            
            bbs = new ByteBuffer[] { bb0, bb1 };
        }
        else if ( osName.indexOf( "mac" ) >= 0 )
        {
            SharedBufferedImage trgImg0 = SharedBufferedImage.create( 128, 128, 4, true, new int[] { 0, 1, 2, 3 }, null );
            
            ImageUtility.scaleImage( srcImg, trgImg0 );
            
            ByteBuffer bb0 = BufferUtils.createByteBuffer( trgImg0.getWidth() * trgImg0.getHeight() * 4 );
            bb0.put( trgImg0.getSharedData() );
            bb0.flip();
            
            bbs = new ByteBuffer[] { bb0 };
        }
        else // expect linux or similar platform
        {
            SharedBufferedImage trgImg0 = SharedBufferedImage.create( 32, 32, 4, true, new int[] { 0, 1, 2, 3 }, null );
            
            ImageUtility.scaleImage( srcImg, trgImg0 );
            
            ByteBuffer bb0 = BufferUtils.createByteBuffer( trgImg0.getWidth() * trgImg0.getHeight() * 4 );
            bb0.put( trgImg0.getSharedData() );
            bb0.flip();
            
            bbs = new ByteBuffer[] { bb0 };
        }
        
        return ( bbs );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setIcon( URL iconResource ) throws IOException
    {
        Display.setIcon( createIcon( iconResource ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTitle( String title )
    {
        /*
        try
        {
            Display.releaseContext();
            System.err.println( Thread.currentThread() );
            Thread.dumpStack();
            Display.makeCurrent();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        */
        
        Display.setTitle( title );
        
        /*
        try
        {
            Display.releaseContext();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        */
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTitle()
    {
        return ( Display.getTitle() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean setLocation( int x, int y )
    {
        if ( ( this.left == x ) && ( this.top == y ) )
        {
            return ( false );
        }
        
        Display.setLocation( x, y );
        
        this.left = x;
        this.top = y;
        
        return ( true );
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
        if ( ( width == this.width ) && ( height == this.height ) )
            return ( false );
        
        DisplayMode displayMode = DisplayModeSelector.getImplementation( OpenGLLayer.LWJGL ).getBestMode( width, height, getBPP(), getFrequency() );
        
        if ( displayMode == null )
            return ( false );
        
        setDisplayModeRef( displayMode );
        displayModeChanged = true;
        
        this.width = getDisplayMode().getWidth();
        this.height = getDisplayMode().getHeight();
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getWidth()
    {
        return ( width );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getHeight()
    {
        return ( height );
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
        {
            displayModeChanged = true;
            this.width = displayMode.getWidth();
            this.height = displayMode.getHeight();
        }
        
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Thread makeCurrent()
    {
        try
        {
            Display.makeCurrent();
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
                Display.releaseContext();
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
        if ( !isInitialized() )
            return ( null );
        
        if ( getFullscreenSwitchRequest() != null )
        {
            try
            {
                final boolean newFSMode = getFullscreenSwitchRequest().booleanValue();
                
                //destroyGLNames( false );
                
                //Display.destroy();
                Display.setFullscreen( newFSMode );
                //Display.create( new PixelFormat( 0, getDepthBufferSize(), 8, getFSAA().getIntValue() ) );
                
                if ( newFSMode )
                {
                    this.left = 0;
                    this.top = 0;
                }
                else
                {
                    this.left = Display.getDisplayMode().getWidth() / 2;
                    this.top = 0;
                    
                    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    Display.setLocation( ( ( screenSize.width - width ) / 2 ), ( ( screenSize.height - height ) / 2 ) );
                }
            }
            catch ( LWJGLException e )
            {
                e.printStackTrace();
            }
            
            resetFullscreenSwitchRequest();
        }
        
        if ( displayModeChanged )
        {
            try
            {
                final int currentCenterX = left + ( Display.getDisplayMode().getWidth() / 2 );
                final int currentCenterY = top + ( Display.getDisplayMode().getHeight() / 2 );
                
                Display.setDisplayMode( getNativeDisplayMode() );
                
                if ( !isFullscreen() )
                {
                    this.left = currentCenterX - ( getNativeDisplayMode().getWidth() / 2 );
                    this.top = currentCenterY - ( getNativeDisplayMode().getHeight() / 2 );
                    Display.setLocation( this.left, this.top );
                }
            }
            catch ( LWJGLException e )
            {
                e.printStackTrace();
            }
            
            displayModeChanged = false;
        }
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "CanvasPeerImpl::render" );
        
        boolean pmTimeHit = ( nanoTime - lastIsCloseRequestedQueryTime ) >= PROCESS_MESSAGE_TIME_INTERVAL;
        
        if ( isClosingListenerRegistered() && pmTimeHit )
        {
            if ( Display.isCloseRequested() )
            {
                fireClosingEvent();
            }
        }
        
        isRendering = true;
        Object result = doRender( view, renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest );
        isRendering = false;
        
        if ( ( ( frameId % 100 ) == 0 ) || pmTimeHit )
        {
            Display.processMessages();
            
            if ( NON_INTERACTIVE_MODE )
            {
                LWJGLMessageProcessor.allowOneUpdate();
            }
        }
        
        if ( pmTimeHit )
        {
            lastIsCloseRequestedQueryTime = nanoTime;
        }
        
        // updates LWJGL display
        //Display.update();
        //if ( Display.isVisible() || Display.isDirty() )
        //if ( Display.isVisible() )
        {
            try
            {
                Display.swapBuffers();
            }
            catch ( LWJGLException e )
            {
                X3DLog.print( e );
                e.printStackTrace();
            }
        }
        
        ProfileTimer.endProfile();
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy()
    {
        super.destroy();
        
        try
        {
            Display.releaseContext();
            Display.makeCurrent();
            
            if ( Display.isCreated() && Display.isActive() )
                Display.destroy();
        }
        catch ( Throwable t )
        {
            //t.printStackTrace();
        }
    }
}
