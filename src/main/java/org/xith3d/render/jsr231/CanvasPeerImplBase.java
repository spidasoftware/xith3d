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

import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL;

import org.openmali.types.twodee.Rect2i;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLInfo;
import org.xith3d.render.OpenGlExtensions;
import org.xith3d.render.RenderPass;
import org.xith3d.render.SceneGraphOpenGLReference;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.scenegraph.View;

import com.sun.opengl.util.BufferUtil;

/**
 * The base for CanvasPeer implementation for the official Java OpenGL Bindings (JOGL)
 * 
 * @author David Yazel [jogl]
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus) [major clean-up / speed-up, multipass rendering]
 */
public abstract class CanvasPeerImplBase extends CanvasPeer
{
    private Rect2i maxViewport = new Rect2i( -1, -1, -1, -1 );
    private Rect2i currentViewport = null;
    private boolean isDefaultViewport = true;
    
    /** The GL swapInterval (uses vsync or not)
     *  0 means : disable VSync.
     *  -1 means : unspecified (default value)
     * > 0 means : should wait before swapping buffers.
     */
    private int swapInterval = -1;
    protected boolean swapIntervalChanged = false;
    
    private long renderedFrames = 0L;
    
    private boolean isInitialized = false;
    
    @Override
    protected RenderPeerImpl createRenderPeer()
    {
        return ( new RenderPeerImpl( this, new StateUnitPeerRegistryImpl(), new OpenGLStatesCacheImpl() ) );
    }
    
    public CanvasPeerImplBase( DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, int depthBufferSize )
    {
        super( displayMode, fullscreen, vsync, fsaa, depthBufferSize );
    }
    
    protected final void init()
    {
        this.oglInfo = new OpenGLInfo( getGL().glGetString( GL.GL_RENDERER ), getGL().glGetString( GL.GL_VERSION ), getGL().glGetString( GL.GL_VENDOR ), getGL().glGetString( GL.GL_EXTENSIONS ) );
        OpenGlExtensions.setExtensions( oglInfo );
        
        //if ( !( this instanceof CanvasPeerImplSwing ) )
        {
            final IntBuffer intBuffer = BufferUtil.newIntBuffer( 2 );
            getGL().glGetIntegerv( GL.GL_MAX_TEXTURE_SIZE, intBuffer );
            final int maxTexSize = intBuffer.get( 0 );
            
            intBuffer.rewind();
            getGL().glGetIntegerv( GL.GL_MAX_TEXTURE_UNITS, intBuffer );
            final int maxTUs = intBuffer.get( 0 );
            
            final int maxVAs;
            if ( oglInfo.getVersionMajor() >= 2 )
            {
                intBuffer.rewind();
                getGL().glGetIntegerv( GL.GL_MAX_VERTEX_ATTRIBS, intBuffer );
                maxVAs = intBuffer.get( 0 );
            }
            else
            {
                maxVAs = 0;
            }
            
            intBuffer.rewind();
            getGL().glGetIntegerv( GL.GL_MAX_VIEWPORT_DIMS, intBuffer );
            this.maxViewport.set( 0, 0, intBuffer.get( 0 ), intBuffer.get( 1 ) );
            
            setOpenGLCapabilities( new OpenGLCapabilities( maxTexSize, maxTUs, TextureUnitStateUnitPeer.getMaxAnisotropicLevel( getGL() ), maxVAs, oglInfo ) );
        }
        
        getRenderPeer().getStatesCache().update( getGL(), getOpenGLCapabilities() );
        
        int[] depth_bits = new int[ 1 ];
        getGL().glGetIntegerv( GL.GL_DEPTH_BITS, depth_bits, 0 );
        setDepthBufferSize( depth_bits[ 0 ] );
        
        getGL().glViewport( 0, 0, getWidth(), getHeight() );
        
        boolean showInfos = true;
        try
        {
            showInfos = System.getProperty( "org.xith3d.render.jsr231.displayGLInfos", "true" ).equals( "true" );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        
        if ( showInfos )
        {
            System.out.println( "Init GL is " + getGL().getClass().getName() );
            oglInfo.dump();
        }
        
        isInitialized = true;
    }
    
    protected final boolean isInitialized()
    {
        return ( isInitialized );
    }
    
    @Override
    protected final Class< ? > getExpectedNativeDisplayModeClass()
    {
        return ( java.awt.DisplayMode.class );
    }

    
    public abstract GL getGL();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final RenderPeerImpl getRenderPeer()
    {
        return ( (RenderPeerImpl)super.getRenderPeer() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getRenderedFrames()
    {
        return ( renderedFrames );
    }
    
    /**
     * Updates the swapInterval (used to enable/disable vsync).
     *  
     * @param swapInterval the new swap interval (>=0) or -1 to use platform default.
     */
    public final void setSwapInterval( int swapInterval )
    {
        this.swapInterval = swapInterval;
        this.swapIntervalChanged = true;
    }
    
    /**
     * @return the swap interval in use (or -1 if not specified).
     * 
     * @see #setSwapInterval(int)
     */
    public final int getSwapInterval()
    {
        return ( swapInterval );
    }
    
    protected abstract void setAutoSwapBufferMode( boolean mode );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addDestroyableObject( SceneGraphOpenGLReference ref )
    {
        super.addDestroyableObject( ref );
    }
    
    protected void beforeRenderStart( PickRequest pickRequest, boolean forceNoSwap )
    {
        setAutoSwapBufferMode( !( ( pickRequest != null ) | forceNoSwap ) );
        
        // reset triangles count
        setTriangles( 0 );
        
        destroyGLNames( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Rect2i getMaxViewport()
    {
        return ( maxViewport );
    }
    
    public Rect2i getCurrentViewport()
    {
        return ( currentViewport );
    }
    
    public final void updateViewport( GL gl, Rect2i viewport )
    {
        // get the current viewport
        boolean viewportChanged = false;
        if ( currentViewport == null )
        {
            if ( viewport == null )
            {
                if ( getViewport() == null )
                {
                    currentViewport = new Rect2i( 0, 0, getWidth(), getHeight() );
                }
                else
                {
                    currentViewport = new Rect2i( getViewport() );
                    getViewport().setClean();
                }
                isDefaultViewport = true;
            }
            else
            {
                currentViewport = new Rect2i( viewport );
                isDefaultViewport = false;
            }
            
            viewportChanged = true;
        }
        else if ( viewport != null )
        {
            if ( ( getViewport() != null ) && ( getViewport().isDirty() ) )
            {
                currentViewport.set( getViewport() );
                getViewport().setClean();
            }
            
            if ( !currentViewport.equals( viewport ) )
            {
                currentViewport.set( viewport );
                isDefaultViewport = false;
                
                viewportChanged = true;
            }
        }
        else if ( !isDefaultViewport )
        {
            if ( getViewport() == null )
            {
                currentViewport.set( 0, 0, getWidth(), getHeight() );
            }
            else
            {
                currentViewport.set( getViewport() );
                getViewport().setClean();
            }
            isDefaultViewport = true;
            
            viewportChanged = true;
        }
        else if ( ( isDefaultViewport ) && ( !currentViewport.equals( ( getViewport() == null ) ? this : getViewport() ) ) )
        {
            if ( getViewport() == null )
            {
                currentViewport.set( 0, 0, getWidth(), getHeight() );
            }
            else
            {
                currentViewport.set( getViewport() );
                getViewport().setClean();
            }
            
            viewportChanged = true;
        }
        
        if ( viewportChanged )
        {
            // OpenGL wants it flipped!
            final int y = getHeight() - currentViewport.getTop() - currentViewport.getHeight();
            
            gl.glViewport( currentViewport.getLeft(), y, currentViewport.getWidth(), currentViewport.getHeight() );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        getRenderPeer().clearViewport( getGL() );
    }
    
    /**
     * Uses the current render frame to draw all the graphics for the frame.
     * If there is not one defined then nothing wil be drawm.
     */
    protected final Object doRender( View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        Object result = null;
        synchronized ( getRenderLock() )
        {
             result = getRenderPeer().render( getGL(), view, renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest );
        }
        renderedFrames++;
        
        return ( result );
    }
}
