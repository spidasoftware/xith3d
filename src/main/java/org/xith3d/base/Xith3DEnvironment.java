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
package org.xith3d.base;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.loop.RenderLoop;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.PickScheduler;
import org.xith3d.physics.PhysicsEngine;
import org.xith3d.render.Canvas3DWrapper;

import org.xith3d.render.Canvas3D;
import org.xith3d.render.DefaultRenderer;
import org.xith3d.render.RenderPass;
import org.xith3d.render._RNDR_PrivilegedAccess;
import org.xith3d.scenegraph.RenderableSceneGraph;
import org.xith3d.scenegraph.SceneGraph;
import org.xith3d.scenegraph.View;
import org.xith3d.sound.SoundDriver;
import org.xith3d.sound.SoundProcessor;
import org.xith3d.ui.hud.__HUD_PrivilegedAccess;
import org.xith3d.utility.logging.X3DLog;

/**
 * This class offers the common objects needed for Xith3D rendering.
 * 
 * Link it with an instance of RenderLoop
 * "renderLoop.addRenderEngine(RenderEngine)" to let the scene be rendered in a
 * separate thread.
 * 
 * @see org.xith3d.loop.RenderLoop
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Xith3DEnvironment extends SceneGraph implements RenderableSceneGraph
{
    private RenderLoop renderLoop;
    private EnvScreenshotEngine screenshotEngine = null;
    private PickScheduler pickScheduler = null;
    private PhysicsEngine physicsEngine = null;
    
    private final ConcurrentHashMap< Canvas3D, Boolean > canvasEnabledMap;
    public boolean canvasAliveCheckSuppressed = true;
    private Canvas3D firstCanvas;
    
    private HashMap<Canvas3D, List<RenderPass>> canvasRenderPassMap = null;
    
    public void setRenderLoop( RenderLoop renderLoop )
    {
        this.renderLoop = renderLoop;
    }
    
    public final RenderLoop getRenderLoop()
    {
        return ( renderLoop );
    }
    
    /**
     * {@inheritDoc}
     */
    public final OperationScheduler getOperationScheduler()
    {
        if ( renderLoop == null )
            return ( null );
        
        return ( renderLoop.getOperationScheduler() );
    }
    
    /**
     * Sets this environment's ScreenshotEngine.
     * 
     * @param engine
     */
    public void setScreenshotEngine( EnvScreenshotEngine engine )
    {
        this.screenshotEngine = engine;
    }
    
    /**
     * @return this environment's ScreenshotEngine
     */
    public EnvScreenshotEngine getScreenshotEngine()
    {
        return ( screenshotEngine );
    }
    
    /**
     * Sets this environment's PickScheduler.
     * 
     * @param picker
     */
    public void setPickScheduler( PickScheduler picker )
    {
        this.pickScheduler = picker;
    }
    
    /**
     * @return this environment's PickScheduler
     */
    public PickScheduler getPickScheduler()
    {
        return ( pickScheduler );
    }
    
    /**
     * Sets the PhysicsEngine, which is automatically updated by the RenderLoop.
     *  
     * @param physEngine
     */
    public void setPhysicsEngine( PhysicsEngine physEngine )
    {
        this.physicsEngine = physEngine;
    }
    
    /**
     * @return the PhysicsEngine, which is automatically updated by the RenderLoop.
     */
    public PhysicsEngine getPhysicsEngine()
    {
        return ( physicsEngine );
    }
    
    public final void updatePhysicsEngine( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( physicsEngine != null )
        {
            physicsEngine.update( gameTime, frameTime, timingMode );
        }
    }
    
    public final void updateInputSystem( long gameTime, TimingMode timingMode )
    {
        if ( !InputSystem.hasInstance() )
            return;
        
        final long nanoGameTime = timingMode.getNanoSeconds( gameTime );
        final InputSystem inputSystem = InputSystem.getInstance();
        
        try
        {
            inputSystem.update( nanoGameTime );
        }
        catch ( InputSystemException ex )
        {
            ex.printStackTrace();
            X3DLog.print( ex );
        }
    }
    
    /**
     * @deprecated use {@link SoundProcessor#getInstance()} instead to set the sound driver.
     */
    @Deprecated
    public void setSoundDriver( SoundDriver soundDriver )
    {
        SoundProcessor.getInstance().setSoundDriver( soundDriver );
    }
    
    /**
     * @deprecated use {@link SoundProcessor#getInstance()} instead to set the sound driver.
     */
    @Deprecated
    public SoundDriver getSoundDriver()
    {
        return ( SoundProcessor.getInstance().getSoundDriver() );
    }
    
    /**
     * {@inheritDoc}
     */
    public Canvas3D addCanvas( Canvas3D canvas, View view )
    {
        _RNDR_PrivilegedAccess.addCanvas3D( canvas, getRenderer() );
        
        view.addCanvas3D( canvas );
        
        if ( firstCanvas == null )
        {
            firstCanvas = canvas;
            for ( int i = 0; i < huds.size(); i++ )
            {
                huds.get( i ).connect( firstCanvas );
            }
        }
        
        canvasEnabledMap.put( canvas, true );
        
        return ( canvas );
    }
    
    /**
     * {@inheritDoc}
     */
    public Canvas3D addCanvas( Canvas3D canvas )
    {
        addCanvas( canvas, getView() );
        
        return ( canvas );
    }
    
    /**
     * {@inheritDoc}
     */
    public Canvas3DWrapper addCanvas( Canvas3DWrapper canvasWrapper, View view )
    {
        addCanvas( canvasWrapper.getCanvas(), view );
        
        return ( canvasWrapper );
    }
    
    /**
     * {@inheritDoc}
     */
    public Canvas3DWrapper addCanvas( Canvas3DWrapper canvasWrapper )
    {
        addCanvas( canvasWrapper.getCanvas() );
        
        return ( canvasWrapper );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeCanvas( Canvas3D canvas )
    {
        _RNDR_PrivilegedAccess.removeCanvas3D( canvas, getRenderer() );
        
        if ( canvas.getView() != null )
            canvas.getView().removeCanvas3D( canvas );
        
        canvasEnabledMap.remove( canvas );
        
        if (canvasEnabledMap.isEmpty())
            firstCanvas = null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeCanvas( Canvas3DWrapper canvasWrapper )
    {
        _RNDR_PrivilegedAccess.removeCanvas3D( canvasWrapper.getCanvas(), getRenderer() );
        
        if ( canvasWrapper.getCanvas().getView() != null )
            canvasWrapper.getCanvas().getView().removeCanvas3D( canvasWrapper.getCanvas() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAllCanvas3Ds()
    {
        final int nc = getRenderer().getNumberOfCanvas3Ds();
        for ( int i = 0; i < nc; i++ )
        {
            final Canvas3D canvas = _RNDR_PrivilegedAccess.removeCanvas3D( i, getRenderer() );
            
            canvas.getView().removeCanvas3D( canvas );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Canvas3D getCanvas()
    {
        if ( firstCanvas == null )
            //throw new NullPointerException( "No Canvas3D added to the environment!" );
            return ( null );
        
        return ( firstCanvas );
    }
    
    /**
     * {@inheritDoc}
     */
    public Canvas3D getCanvas( int index )
    {
        Canvas3D canvas;
        
        int k = 0;
        
        final int n = getNumberOfViews();
        for ( int i = 0; i < n; i++ )
        {
            final View view = getView( i );
            final int m = view.numCanvas3Ds();
            for ( int j = 0; j < m; j++ )
            {
                canvas = view.getCanvas3D( j );
                
                if ( k == index )
                    return ( canvas );
                
                k++;
            }
        }
        
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    public void suspendCanvas( Canvas3D canvas )
    {
        if ( canvasEnabledMap.containsKey( canvas ) )
            canvasEnabledMap.put( canvas, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void suspendCanvas( Canvas3DWrapper canvasWrapper )
    {
        suspendCanvas( canvasWrapper.getCanvas() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void reviveCanvas( Canvas3D canvas )
    {
        if ( !canvasEnabledMap.containsKey( canvas ) )
        {
            addCanvas( canvas );
        }
        else
            canvasEnabledMap.put( canvas, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void reviveCanvas( Canvas3DWrapper canvasWrapper )
    {
        reviveCanvas( canvasWrapper.getCanvas() );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isCanvasAlive( Canvas3D canvas )
    {
        if ( canvasEnabledMap.containsKey( canvas ) )
            return ( canvasEnabledMap.get( canvas ).booleanValue() );
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isCanvasAlive( Canvas3DWrapper canvasWrapper )
    {
        return ( isCanvasAlive( canvasWrapper.getCanvas() ) );
    }
    
    /**
     * Sets a map, that defines a list of RenderPasses to be rendered to each mapped canvas.
     * The default value is null, which means, that all RenderPasses are rendered to each canvas.
     * 
     * @param canvasRenderPassMap
     */
    public void setCanvasRenderPassMap( HashMap<Canvas3D, List<RenderPass>> canvasRenderPassMap )
    {
        this.canvasRenderPassMap = canvasRenderPassMap;
    }
    
    /**
     * {@inheritDoc}
     */
    public void checkRenderPreferences()
    {
        final int nv = getNumberOfViews();
        for ( int i = 0; i < nv; i++ )
        {
            final View view = getView( i );
            
            final int nc = view.numCanvas3Ds();
            for ( int j = 0; j < nc; j++ )
            {
                final Canvas3D canvas = view.getCanvas3D( j );
                
                canvas.getPeer().beforeThreadChanged();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void render( long nanoGameTime, long nanoFrameTime )
    {
        /*
         * Since a HUD uses its own OperationScheduler now,
         * we need to update it.
         */
        for ( int i = 0; i < huds.size(); i++ )
        {
            __HUD_PrivilegedAccess.updateOperations( huds.get( i ), nanoGameTime, nanoFrameTime );
        }
        
        int canvasesCount = 0;
        
        long frameId = -1L;
        
        SoundProcessor sp = SoundProcessor.getInstance();
        List<RenderPass> renderPasses = getRenderer().getRenderPasses();
        int numRP = renderPasses.size();
        
        final int nv = getNumberOfViews();
        for ( int v = 0; v < nv; v++ )
        {
            final View view = getView( v );
            final List< Canvas3D > canvases = view.getCanvas3Ds();
            for ( int c = 0; c < canvases.size(); c++ )
            {
                Canvas3D canvas = canvases.get( c );
                if ( canvasAliveCheckSuppressed || isCanvasAlive( canvas ) )
                {
                    if ( canvasRenderPassMap == null )
                        frameId = getRenderer().renderOnce( canvas, nanoGameTime, nanoFrameTime );
                    else
                        frameId = getRenderer().renderOnce( canvasRenderPassMap.get( canvas ), null, canvas, nanoGameTime, nanoFrameTime );
                    
                    canvasesCount++;
                }
            }
            
            // Play sounds
            for ( int i = 0; i < numRP; i++ )
            {
                sp.processAll( renderPasses.get( i ).getBranchGroup(), view, frameId );
            }
        }
        
        if ( canvasesCount == 0 )
        {
            System.err.println( "No Canvas3D added to the environment!" );
        }
    }
    
    /**
     * Renderes all Canvas3Ds.
     * This method is usually called by the RenderLoop thread.
     * 
     * It simply invokes render( -1L, -1L ).
     */
    public void render()
    {
        render( System.nanoTime(), -1L );
    }
    
    /**
     * {@inheritDoc}
     */
    public void destroy()
    {
        final int nv = getNumberOfViews();
        for ( int i = 0; i < nv; i++ )
        {
            final View view = getView( i );
            
            final int nc = view.numCanvas3Ds();
            for ( int j = 0; j < nc; j++ )
            {
                final Canvas3D canvas = view.getCanvas3D( j );
                
                canvas.getPeer().destroy();
            }
        }
        
        SoundDriver soundDriver = SoundProcessor.getInstance().getSoundDriver();
        if ( soundDriver != null )
        {
            soundDriver.shutdown();
        }
    }
    
    /**
     * Creates a new Xith3DEnvironment.
     * 
     * @param headless true for no View creation
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewFocusX the point the view looks at
     * @param viewFocusY the point the view looks at
     * @param viewFocusZ the point the view looks at
     * @param vecUpX the vector pointing up
     * @param vecUpY the vector pointing up
     * @param vecUpZ the vector pointing up
     * @param renderLoop the RenderLoop instance to link this environment with.
     */
    private Xith3DEnvironment( boolean headless,
                               float eyePositionX, float eyePositionY, float eyePositionZ,
                               float viewFocusX, float viewFocusY, float viewFocusZ,
                               float vecUpX, float vecUpY, float vecUpZ,
                               RenderLoop renderLoop )
    {
        super( new DefaultRenderer() );
        
        // enable layered rendering mode
        getRenderer().setLayeredMode( true );
        
        // create a view
        if ( !headless )
        {
            addView( new View( eyePositionX, eyePositionY, eyePositionZ,
                               viewFocusX, viewFocusY, viewFocusZ,
                               vecUpX, vecUpY, vecUpZ ) );
        }
        
        this.firstCanvas = null;
        this.canvasEnabledMap = new ConcurrentHashMap<Canvas3D, Boolean>();
        
        if ( renderLoop != null )
        {
            renderLoop.setXith3DEnvironment( this );
        }
        
        this.renderLoop = renderLoop;
        
        this.screenshotEngine = new EnvScreenshotEngineImpl( this );
        this.pickScheduler = new PickSchedulerImpl( this );
    }
    
    /**
     * Creates a new Xith3DEnvironment.
     * 
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewFocusX the point the view looks at
     * @param viewFocusY the point the view looks at
     * @param viewFocusZ the point the view looks at
     * @param vecUpX the vector pointing up
     * @param vecUpY the vector pointing up
     * @param vecUpZ the vector pointing up
     * @param renderLoop the RenderLoop instance to link this environment with.
     */
    public Xith3DEnvironment( float eyePositionX, float eyePositionY, float eyePositionZ,
                              float viewFocusX, float viewFocusY, float viewFocusZ,
                              float vecUpX, float vecUpY, float vecUpZ,
                              RenderLoop renderLoop )
    {
        this( false,
              eyePositionX, eyePositionY, eyePositionZ,
              viewFocusX, viewFocusY, viewFocusZ,
              vecUpX, vecUpY, vecUpZ,
              renderLoop
            );
    }
    
    /**
     * Creates a new Xith3DEnvironment.
     * 
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewFocusX the point the view looks at
     * @param viewFocusY the point the view looks at
     * @param viewFocusZ the point the view looks at
     * @param vecUpX the vector pointing up
     * @param vecUpY the vector pointing up
     * @param vecUpZ the vector pointing up
     */
    public Xith3DEnvironment( float eyePositionX, float eyePositionY, float eyePositionZ,
                              float viewFocusX, float viewFocusY, float viewFocusZ,
                              float vecUpX, float vecUpY, float vecUpZ )
    {
        this( false,
              eyePositionX, eyePositionY, eyePositionZ,
              viewFocusX, viewFocusY, viewFocusZ,
              vecUpX, vecUpY, vecUpZ,
              null
            );
    }
    
    /**
     * Creates a new Xith3DEnvironment.
     * 
     * @param eyePosition the environment's view's location (or <i>null</i> for no View creation)
     * @param viewFocus the environment's view's center (where to look at) (or <i>null</i> for no View creation)
     * @param vecUp the environment's view's normal which is pointing up (or <i>null</i> for no View creation)
     * @param renderLoop the RenderLoop instance to link this environment with.
     */
    public Xith3DEnvironment( Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f vecUp, RenderLoop renderLoop )
    {
        this( (eyePosition == null) || (viewFocus == null) || (vecUp == null),
              eyePosition != null ? eyePosition.getX() : 0f, eyePosition != null ? eyePosition.getY() : 0f, eyePosition != null ? eyePosition.getZ() : 0f,
              viewFocus != null ? viewFocus.getX() : 0f, viewFocus != null ? viewFocus.getY() : 0f, viewFocus != null ? viewFocus.getZ() : 0f,
              vecUp != null ? vecUp.getX() : 0f, vecUp != null ? vecUp.getY() : 0f, vecUp != null ? vecUp.getZ() : 0f,
              renderLoop
            );
    }
    
    /**
     * Creates a new Xith3DEnvironment.
     * 
     * @param eyePosition the environment's view's location (or <i>null</i> for no View creation)
     * @param viewFocus the environment's view's center (where to look at) (or <i>null</i> for no View creation)
     * @param vecUp the environment's view's normal which is pointing up (or <i>null</i> for no View creation)
     */
    public Xith3DEnvironment( Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f vecUp )
    {
        this( eyePosition, viewFocus, vecUp, null );
    }
    
    /**
     * Creates a new Xith3DEnvironment.
     * 
     * eyePosition is (0, 0, 5)
     * viewFocus is (0, 0, 0)
     * vecUp is (0, 1, 0)
     * 
     * @param renderLoop the RenderLoop instance to link this environment with.
     */
    public Xith3DEnvironment( RenderLoop renderLoop )
    {
        this( 0f, 0f, 5f,
              0f, 0f, 0f,
              0f, 1f, 0f,
              renderLoop
            );
    }
    
    /**
     * Creates a new Xith3DEnvironment.
     * 
     * eyePosition is (0, 0, 5)
     * viewFocus is (0, 0, 0)
     * vecUp is (0, 1, 0)
     */
    public Xith3DEnvironment()
    {
        this( 0f, 0f, 5f,
              0f, 0f, 0f,
              0f, 1f, 0f
            );
    }
    
    /**
     * Creates a new headless (without a View) Xith3DEnvironment.
     * 
     * @param renderLoop the RenderLoop instance to link this environment with.
     */
    public static final Xith3DEnvironment createHeadless( RenderLoop renderLoop )
    {
        return ( new Xith3DEnvironment( null, null, null, renderLoop ) );
    }
    
    /**
     * Creates a new headless (without a View) Xith3DEnvironment.
     */
    public static final Xith3DEnvironment createHeadless()
    {
        return ( new Xith3DEnvironment( null, null, null, null ) );
    }
}
