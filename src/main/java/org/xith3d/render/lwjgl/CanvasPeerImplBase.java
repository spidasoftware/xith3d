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

import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
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
import org.xith3d.utility.logging.X3DLog;

/**
 * The CanvasPeer base implementation for the LightWeight Java Game Library (LWJGL)
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class CanvasPeerImplBase extends CanvasPeer
{
    protected static Boolean DEBUG_GL = null;
    
    private Rect2i currentViewport = null;
    private boolean isDefaultViewport = true;
    
    private Rect2i maxViewport = new Rect2i( -1, -1, -1, -1 );
    
    protected Thread renderingThread = null;
    protected boolean vsyncSwitched = false;
    
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
    
    protected void init() throws Throwable
    {
        this.oglInfo = new OpenGLInfo( GL11.glGetString( GL11.GL_RENDERER ), GL11.glGetString( GL11.GL_VERSION ), GL11.glGetString( GL11.GL_VENDOR ), GL11.glGetString( GL11.GL_EXTENSIONS ) );
        OpenGlExtensions.setExtensions( oglInfo );
        
        IntBuffer intBuffer = BufferUtils.createIntBuffer( 16 );
        GL11.glGetInteger( GL11.GL_MAX_TEXTURE_SIZE, intBuffer );
        final int maxTexSize = intBuffer.get( 0 );
        
        intBuffer.clear();
        GL11.glGetInteger( GL13.GL_MAX_TEXTURE_UNITS, intBuffer );
        final int maxTUs = intBuffer.get( 0 );
        
        final int maxVAs;
        if ( oglInfo.getVersionMajor() >= 2 )
        {
            intBuffer.clear();
            GL11.glGetInteger( GL20.GL_MAX_VERTEX_ATTRIBS, intBuffer );
            maxVAs = intBuffer.get( 0 );
        }
        else
        {
            maxVAs = 0;
        }
        
        intBuffer.clear();
        GL11.glGetInteger( GL11.GL_MAX_VIEWPORT_DIMS, intBuffer );
        this.maxViewport.set( 0, 0, intBuffer.get( 0 ), intBuffer.get( 1 ) );
        
        setOpenGLCapabilities( new OpenGLCapabilities( maxTexSize, maxTUs, TextureUnitStateUnitPeer.getMaxAnisotropicLevel(), maxVAs, oglInfo ) );
        
        getRenderPeer().getStatesCache().update( null, getOpenGLCapabilities() );
        
        IntBuffer ib = BufferUtils.createIntBuffer( 16 );
        GL11.glGetInteger( GL11.GL_DEPTH_BITS, ib );
        setDepthBufferSize( ib.get( 0 ) );
        
        boolean showInfos = true;
        try
        {
            showInfos = System.getProperty( "org.xith3d.render.lwjgl.displayGLInfos", "true" ).equals( "true" );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        
        if ( showInfos )
        {
//            System.out.println( "Init GL is " + GL11.class.getName() );
            oglInfo.dump();
        }
        
        isInitialized = true;
    }
    
    protected final boolean isInitialized()
    {
        return ( isInitialized );
    }
    
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
    
    @Override
    protected Class< ? > getExpectedNativeDisplayModeClass()
    {
        return ( org.lwjgl.opengl.DisplayMode.class );
    }
    
    public final org.lwjgl.opengl.DisplayMode getNativeDisplayMode()
    {
        if ( getDisplayMode() == null )
            return ( null );
        
        return ( (org.lwjgl.opengl.DisplayMode)getDisplayMode().getNativeMode() );
    }
    
    protected abstract void applyVSync();
    
    protected abstract Thread makeCurrent();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addDestroyableObject( SceneGraphOpenGLReference ref )
    {
        super.addDestroyableObject( ref );
    }
    
    protected void beforeRenderStart()
    {
        if ( renderingThread == null )
        {
            renderingThread = makeCurrent();
        }
        
        if ( vsyncSwitched )
        {
            applyVSync();
            vsyncSwitched = false;
        }
        
        //setNoSwapBuffers( pickRequest != null );
        
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
    
    public void updateViewport( Rect2i viewport )
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
            
            GL11.glViewport( currentViewport.getLeft(), y, currentViewport.getWidth(), currentViewport.getHeight() );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        getRenderPeer().clearViewport();
    }
    
    /**
     * Uses the current render frame to draw all the graphics for the frame.
     * If there is not one defined then nothing wil be drawm.
     */
    protected final Object doRender( View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        Object result = getRenderPeer().render( null, view, renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest );
        
        renderedFrames++;
        
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
            GL11.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
            
            if ( Mouse.isCreated() )
                Mouse.destroy();
            
            if ( Keyboard.isCreated() )
                Keyboard.destroy();
        }
        catch ( Throwable t )
        {
            X3DLog.print( t );
        }
    }
}
