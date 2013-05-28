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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.InputDeviceFactory;
import org.jagatoo.input.render.Cursor;
import org.jagatoo.input.render.InputSourceWindow;
import org.openmali.types.twodee.Rect2i;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.render.util.WindowClosingListener;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * All CanvasPeer implementations must extend this class.<br>
 * It offers the base methods of all implementations.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class CanvasPeer implements InputSourceWindow
{
    private Canvas3D canvas3D;
    private final RenderPeer renderPeer;
    private DisplayMode displayMode;
    private boolean isFullscreen;
    private final boolean isUndecorated;
    private Boolean fullscreenSwitchRequest = null;
    private boolean vsync;
    private FSAA fsaa;
    private int depthBufferSize;
    
    private Cursor cursor = Cursor.DEFAULT_CURSOR;
    
    private final Object renderLock = new Object();
    
    private final ArrayList< WindowClosingListener > closingListeners = new ArrayList< WindowClosingListener >();
    private boolean isClosingListenerRegistered = false;
    
    private OpenGLCapabilities openGLCapabilities = null;
    protected OpenGLInfo oglInfo = null;
    
    private long triangles = 0;
    
    private float gamma = 1.0f;
    private float brightness = 0.0f;
    private float contrast = 1.0f;
    protected boolean isGammaChanged = false;
    
    private Rect2i viewport = null;
    
    private static int nextCanvasID = 1;
    private static final HashSet< Integer > freedCanvasIDs = new HashSet< Integer >();
    private final int canvasID;
    private boolean destroyed;
    
    private final HashSet< SceneGraphOpenGLReference > destroyableReferences = new HashSet< SceneGraphOpenGLReference >();
    
    protected abstract RenderPeer createRenderPeer();
    
    public CanvasPeer( DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, int depthBufferSize )
    {
        this.renderPeer = createRenderPeer();
        this.displayMode = checkDisplayMode( displayMode, this.getType() );
        this.isFullscreen = fullscreen.isFullscreen();
        this.isUndecorated = ( fullscreen == FullscreenMode.WINDOWED_UNDECORATED );
        this.vsync = vsync;
        this.fsaa = fsaa;
        this.depthBufferSize = depthBufferSize;
        
        synchronized ( this )
        {
            int newCanvasID = nextCanvasID++;
            for ( int i = newCanvasID - 1; i >= 1; i-- )
            {
                if ( freedCanvasIDs.contains( i ) )
                {
                    newCanvasID = i;
                    freedCanvasIDs.remove( i );
                    nextCanvasID--;
                    break;
                }
            }
            this.canvasID = newCanvasID;
        }
        this.destroyed = false;
    }

    public void finish()
    {
    
    }
    
    public RenderPeer getRenderPeer()
    {
        return ( renderPeer );
    }
    
    protected void setCanvas3D( Canvas3D canvas )
    {
        this.canvas3D = canvas;
    }
    
    public Canvas3D getCanvas3D()
    {
        return ( canvas3D );
    }
    
    /**
     * @return an element of the OpenGLLayer enum.
     * 
     * @see OpenGLLayer
     */
    public abstract OpenGLLayer getType();
    
    /**
     * @return unique, one-based canvas ID assigned to this instance of canvas peer.
     * It can be used to ensure that some cached resources have been created for specific canvas, and
     * support multiple cached resources for specific object (for example, Texture) on different canvases.
     */
    public final int getCanvasID()
    {
        return ( canvasID );
    }
    
    /**
     * @return a structure holding the textual information about the OpenGLLayer
     */
    public final OpenGLInfo getOpenGLInfo()
    {
        return ( oglInfo );
    }
    
    public Object getRenderLock()
    {
        return ( renderLock );
    }
    
    protected final Boolean getFullscreenSwitchRequest()
    {
        return ( fullscreenSwitchRequest );
    }
    
    protected final void resetFullscreenSwitchRequest()
    {
        this.fullscreenSwitchRequest = null;
    }
    
    /**
     * Switches this Canvas3D's fullscreen flag.<br>
     * Depending on the implementation this flag may be applied
     * on the next render-frame, but not instantly.
     * 
     * @param fullscreen
     * 
     * @return the previous fullscreen flag.
     */
    public boolean setFullscreen( boolean fullscreen )
    {
        final boolean oldState = isFullscreen;
        
        if ( oldState == fullscreen )
            return ( oldState );
        
        this.fullscreenSwitchRequest = fullscreen;
        this.isFullscreen = fullscreen;
        
        return ( oldState );
    }
    
    /**
     * @return true, if the rendering is done in fullscreen mode
     */
    public final boolean isFullscreen()
    {
        return ( isFullscreen );
    }
    
    /**
     * @return true, if this Canvas'es window is undecorated.
     */
    public final boolean isUndecorated()
    {
        return ( isUndecorated );
    }
    
    protected abstract Class< ? > getExpectedNativeDisplayModeClass();
    
    private final DisplayMode checkDisplayMode( DisplayMode displayMode, OpenGLLayer oglLayer )
    {
        if ( displayMode == null )
            throw new IllegalArgumentException( "displayMode must not be null" );
        
        if ( ( displayMode.getNativeMode() == null ) || ( displayMode.getNativeMode().getClass() != getExpectedNativeDisplayModeClass() ) )
        {
            displayMode = DisplayModeSelector.getImplementation( oglLayer ).getBestMode( displayMode.getWidth(), displayMode.getHeight(), displayMode.getBPP(), displayMode.getFrequency() );
            if ( displayMode == null )
                throw new RuntimeException( "No DisplayMode found!" );
        }
        
        return ( displayMode );
    }
    
    /**
     * Sets the new DisplayMode in an implementation independant manner.
     * 
     * @param displayMode
     */
    protected abstract boolean setDisplayModeImpl( DisplayMode displayMode );
    
    protected final void setDisplayModeRef( DisplayMode displayMode )
    {
        this.displayMode = displayMode;
    }
    
    /**
     * Sets the new DisplayMode in an implementation independant manner
     */
    public final boolean setDisplayMode( DisplayMode displayMode )
    {
        displayMode = checkDisplayMode( displayMode, this.getType() );
        
        final boolean result = setDisplayModeImpl( displayMode );
        
        if ( result )
            setDisplayModeRef( displayMode );
        
        return ( result );
    }
    
    /**
     * @return the current DisplayMode.
     */
    public final DisplayMode getDisplayMode()
    {
        return ( displayMode );
    }
    
    /**
     * @return the color depth.
     */
    public final int getBPP()
    {
        return ( displayMode.getBPP() );
    }
    
    /**
     * @return the displaymode's frequency.
     */
    public final int getFrequency()
    {
        return ( displayMode.getFrequency() );
    }
    
    /**
     * Enables or disables V-Sync.
     */
    public void setVSyncEnabled( boolean vsync )
    {
        this.vsync = vsync;
    }
    
    /**
     * @return true, if vsync has been enabled at creation time.
     */
    public final boolean isVSyncEnabled()
    {
        return ( vsync );
    }
    
    /**
     * @return the full scene anti aliasing mode
     */
    public final FSAA getFSAA()
    {
        return ( fsaa );
    }
    
    protected void setDepthBufferSize( int size )
    {
        this.depthBufferSize = size;
    }
    
    /**
     * @return the bit-depth of the depth-buffer.
     */
    public final int getDepthBufferSize()
    {
        return ( depthBufferSize );
    }
    
    protected final boolean isClosingListenerRegistered()
    {
        return ( isClosingListenerRegistered );
    }
    
    /**
     * Adds a new WindowClosingListener to be notified, when a non-fullscreen
     * Canvas3D's Window is to be closed by clicking the X-button.
     * 
     * @param l
     */
    protected void addWindowClosingListener( WindowClosingListener l )
    {
        this.closingListeners.add( l );
        this.isClosingListenerRegistered = true;
    }
    
    /**
     * Removes a WindowClosingListener.
     * 
     * @param l
     */
    protected void removeWindowClosingListener( WindowClosingListener l )
    {
        this.closingListeners.remove( l );
        this.isClosingListenerRegistered = ( closingListeners.size() > 0 );
    }
    
    protected void fireClosingEvent()
    {
        for ( int i = 0; i < closingListeners.size(); i++ )
        {
            closingListeners.get( i ).onWindowCloseRequested( canvas3D );
        }
    }
    
    protected final void setOpenGLCapabilities( OpenGLCapabilities caps )
    {
        this.openGLCapabilities = caps;
    }
    
    public final OpenGLCapabilities getOpenGLCapabilities()
    {
        return ( openGLCapabilities );
    }
    
    /**
     * @return the maximum texture size.<br>
     */
    public final int getMaxTextureSize()
    {
        return ( openGLCapabilities.getMaxTextureSize() );
    }
    
    /**
     * @return the maximum number of texture units.<br>
     */
    public final int getMaxTextureUnits()
    {
        return ( openGLCapabilities.getMaxTextureUnits() );
    }
    
    /**
     * @return the maximum number of vertex attributes.<br>
     */
    public final int getMaxVertexAttributes()
    {
        return ( openGLCapabilities.getMaxVertexAttributes() );
    }
    
    public final void setTriangles( long num )
    {
        triangles = num;
    }
    
    public final void addTriangles( long num )
    {
        triangles += num;
    }
    
    /**
     * @return the number of triangles currently being rendered.
     */
    public final long getTriangles()
    {
        return ( triangles );
    }
    
    /**
     * @return the number of frames already rendered
     */
    public abstract long getRenderedFrames();
    
    /**
     * @return the Window holding the Canvas.
     */
    public abstract Object getWindow();
    
    /**
     * @return the Component holding the Canvas.
     */
    public abstract Object getComponent();
    
    /**
     * Changes the window icon.
     * 
     * @param iconResource the resource of the image for the new icon
     */
    public abstract void setIcon( URL iconResource ) throws IOException;
    
    /**
     * This method is called when a NEW {@link Cursor} is applied to this Mouse.
     * 
     * @param cursor
     */
    protected void setCursorImpl( Cursor cursor )
    {
        if ( !InputSystem.hasInstance() || !InputSystem.getInstance().hasMouse() )
            return;
        
        InputSystem is = InputSystem.getInstance();
        int n = is.getMousesCount();
        for ( int i = 0; i < n; i++ )
        {
            org.jagatoo.input.devices.Mouse mouse = is.getMouse( i );
            
            if ( mouse.getSourceWindow() == this )
            {
                refreshCursor( mouse );
            }
        }
    }
    
    /**
     * Sets the new Cursor for this Mouse.
     * Use <code>null</code> for an invisible Cursor.
     * Use {@link Cursor#DEFAULT_CURSOR} for the system's default Cursor.
     * 
     * @param cursor
     */
    public final void setCursor( Cursor cursor )
    {
        if ( this.cursor == cursor )
        {
            if ( ( cursor == null ) || !cursor.isDirty() )
                return;
        }
        
        this.cursor = cursor;
        
        setCursorImpl( cursor );
    }
    
    public final Cursor getCursor()
    {
        return ( cursor );
    }
    
    /**
     * Sets the title in an implementation independant manner.
     * 
     * @param title
     */
    public abstract void setTitle( String title );
    
    /**
     * Gets the title in an implementation independant manner
     */
    public abstract String getTitle();
    
    /**
     * @return the InputDeviceFactory, that provides methods to retrieve
     * InputDevices for the specified implementation.<br>
     * Using this method is equal to calling:<br>
     * getInputDeviceFactory( InputSystem.getInstance() );
     */
    public final InputDeviceFactory getInputDeviceFactory()
    {
        return ( getInputDeviceFactory( InputSystem.getInstance() ) );
    }
    
    /**
     * Sets the location in an implementation independant manner.
     * 
     * @param x
     * @param y
     */
    public abstract boolean setLocation( int x, int y );
    
    /**
     * Gets the left-location in an implementation independant manner
     */
    public abstract int getLeft();
    
    /**
     * Gets the top-location in an implementation independant manner
     */
    public abstract int getTop();
    
    /**
     * Sets the size in an implementation independant manner.
     * 
     * @param width
     * @param height
     */
    public abstract boolean setSize( int width, int height );
    
    /**
     * Gets the width in an implementation independant manner
     */
    public abstract int getWidth();
    
    /**
     * Gets the width in an implementation independant manner
     */
    public abstract int getHeight();
    
    /**
     * @return the maximum size of the viewport.<br>
     *         The values will be invalid before the first frame is rendered.
     */
    public abstract Rect2i getMaxViewport();
    
    /**
     * Sets this CanvasPeer's Viewport.
     * 
     * @param viewport
     */
    public void setViewport( Rect2i viewport )
    {
        this.viewport = viewport;
    }
    
    /**
     * @return this CanvasPeer's Viewport
     */
    public Rect2i getViewport()
    {
        return ( viewport );
    }
    
    /**
     * Changes the gamma-, brightness- and contrast values for this CanvasPeer.
     * 
     * @param gamma the gamma value
     * @param brightness the brightness value [-1.0, +1.0]
     * @param contrast the contrast value [0, +1.0]
     */
    public void setGamma( float gamma, float brightness, float contrast )
    {
        /*
        if ((gamma < -1.0f) || (gamma > +1.0f))
            throw new IllegalArgumentException( "gamma must be in range [-1.0, +1.0]" ) );
        */
        if ( ( brightness < -1.0f ) || ( brightness > +1.0f ) )
            throw new IllegalArgumentException( "brightness must be in range [-1.0, +1.0]" );
        if ( ( contrast < 0.0f ) || ( contrast > +1.0f ) )
            throw new IllegalArgumentException( "contrast must be in range [0.0, +1.0]" );
        
        this.gamma = gamma;
        this.brightness = brightness;
        this.contrast = contrast;
        
        this.isGammaChanged = true;
    }
    
    /**
     * @return the current gamma value for this CanvasPeer.
     */
    public float getGamma()
    {
        return ( gamma );
    }
    
    /**
     * @return the current brightness value for this CanvasPeer.
     */
    public float getBrightness()
    {
        return ( brightness );
    }
    
    /**
     * @return the current contrast value for this CanvasPeer.
     */
    public float getContrast()
    {
        return ( contrast );
    }
    
    /**
     * This flag is set by the Rendering system triggered by xith
     * or by the CanvasPeer implementation itself (e.g when the CanvasPeer is auto-redrawn).
     */
    public abstract boolean isRendering();
    
    /**
     * This method should be executed by the Thread that starts the actual
     * rendering Thread right before it starts it. (LWJGL needs this)
     */
    public abstract void beforeThreadChanged();
    
    /**
     * Initializes the rendering. The CanvasPeer implementation may decide when to actually do.
     * 
     * @param view the View used to render
     * @param renderPasses the List of RenderPasses to iterate and render
     * @param layeredMode if true, the RenderPasses are handled in layered mode
     * @param frameId the current frame's id
     * @param pickRequest <code>null</code> for normal rendering
     */
    protected abstract Object initRenderingImpl( View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest );
    
    /**
     * Clears the screen to BLACK.
     */
    public abstract void clear();
    
    public final boolean isDestroyed()
    {
        return ( destroyed );
    }
    
    protected void addDestroyableObject( SceneGraphOpenGLReference ref )
    {
        synchronized ( destroyableReferences )
        {
            destroyableReferences.add( ref );
        }
    }
    
    protected final void destroyGLNames( boolean onlyGCed )
    {
        if ( !onlyGCed )
        {
            SceneGraphOpenGLReferences.destroyObjects( this );
        }
        
        if ( destroyableReferences.size() > 0 )
        {
            synchronized ( destroyableReferences )
            {
                for ( SceneGraphOpenGLReference ref: destroyableReferences )
                {
                    ref.destroyObject();
                }
                
                destroyableReferences.clear();
            }
        }
    }
    
    /**
     * Clears the screen (BLACK) and destroys the display.
     */
    public void destroy()
    {
        if ( !isDestroyed() )
        {
            synchronized ( this )
            {
                setCursor( Cursor.DEFAULT_CURSOR );
                
                if ( InputSystem.hasInstance() )
                {
                    try
                    {
                        InputSystem.getInstance().destroy( this );
                    }
                    catch ( InputSystemException e )
                    {
                        e.printStackTrace();
                        X3DLog.print( e );
                    }
                }
                
                
                destroyGLNames( false );
                
                this.destroyed = true;
                freedCanvasIDs.add( this.getCanvasID() );
            }
        }
    }
    
    /**
     * Search a new free filename for the next screenshot
     * 
     * @param filenameBase the namebase
     * @param forceOverwrite if true, the current date is not appended
     * @return die File with the new filenname
     */
    private final File getNewScreenshotFilename( String filenameBase, boolean forceOverwrite )
    {
        final String postfix;
        File file;
        
        if ( filenameBase.endsWith( ".png" ) )
            postfix = "";
        else
            postfix = ".png";
        
        file = new File( filenameBase + postfix );
        
        while ( ( !forceOverwrite ) && ( file.exists() ) )
        {
            file = new File( filenameBase + ( new SimpleDateFormat( "_yyyy-MM-dd-HHmmss" ) ).format( new GregorianCalendar().getTime() ) + postfix );
        }
        
        return ( file );
    }
    
    /**
     * Takes a screenshot of the current rendering
     * 
     * @param file the file to save the screenshot to
     * @param alpha with alpha channel?
     */
    public void takeScreenshot( File file, boolean alpha )
    {
        getRenderPeer().takeScreenshot( file, alpha );
    }
    
    /**
     * Takes a screenshot of the current rendering
     * 
     * @param filenameBase the filenameBase to save the screenshot to (e.g. "screens/shot")
     *                     The current date and ".png" are appended.
     * @param alpha with alpha channel?
     * @return the file where the screenshot has been saved
     */
    public final File takeScreenshot( String filenameBase, boolean alpha )
    {
        final File file;
        
        if ( filenameBase.endsWith( ".png" ) )
            file = getNewScreenshotFilename( filenameBase, true );
        else
            file = getNewScreenshotFilename( filenameBase, false );
        
        takeScreenshot( file, alpha );
        
        return ( file );
    }
    
    /**
     * Takes a screenshot of the current rendering of the first added Canvas3D.
     * 
     * @param alpha with alpha channel?
     * @return the file where the screenshot has been saved
     */
    public final File takeScreenshot( boolean alpha )
    {
        return ( takeScreenshot( "screenshot", alpha ) );
    }
}
