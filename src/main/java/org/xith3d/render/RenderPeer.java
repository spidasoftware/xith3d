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
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import org.xith3d.picking.PickRequest;
import org.xith3d.picking.PickResult;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.preprocessing.RenderBinProvider;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.StateUnitPeer;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.general.SortableList;

/**
 * The RenderPeer is used to abstract the layer that does the actual drawing
 * from the architecture of the renderer.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class RenderPeer
{
    public static enum RenderMode
    {
        NORMAL,
        PICKING,
        SHADOW_MAP_GENERATION;
    }
    
    private CanvasPeer canvasPeer;
    private StateUnitPeerRegistry stateUnitRegistry;
    private RenderOptions renderOptions;
    
    protected boolean forceNoSwap = false;
    /** disableClearBuffer set to true if application does not want renderer to clear any buffers */
    protected boolean disableClearBuffer = false;
    /** fullOverpaint set to true if client guarantees that all the screen always completely painted and no reason to clear color buffer */
    protected boolean fullOverpaint = false;
    
    protected float[] clearColor = new float[]
    {
        0.2f, // red
        0.2f, // green
        0.2f, // blue
        0.0f // alpha
    };
    
    protected int colorMask = 15;
    
    protected boolean backgroundCachingEnabled = false;
    
    private static final long[] nulledStates = new long[ StateUnit.MAX_STATE_TYPES ];
    
    static
    {
        Arrays.fill( nulledStates, -1 );
    }
    
    private final long[] stateIDs = new long[ nulledStates.length ];
    
    protected IntBuffer selectBuffer = null;
    private SortableList< PickResult > pickResults = new SortableList< PickResult >();
    private PickResult pickResult = null;
    
    private final OpenGLStatesCache statesCache;
    
    private static boolean gcRequested = false;
    
    protected static void setGCRequested( boolean gcReq )
    {
        gcRequested = gcReq;
    }
    
    protected static final void checkGCRequested()
    {
        if ( gcRequested )
        {
            //System.gc(); // TODO: This is not good. But memory needs to be freed some day...
            
            gcRequested = false;
        }
    }
    
    protected void setCanvasPeer( CanvasPeer canvasPeer )
    {
        this.canvasPeer = canvasPeer;
    }
    
    public final CanvasPeer getCanvasPeer()
    {
        return ( canvasPeer );
    }
    
    /**
     * @return the ShaderRegistry, which is responsible for the Shader handling.
     */
    public final StateUnitPeerRegistry getShaderRegistry()
    {
        return ( stateUnitRegistry );
    }
    
    /**
     * Sets the rendering options that this RenderPeer will abide by.
     *
     * @param renderOptions the rendering options, that this RenderPeer will abide by.
     */
    public void setRenderOptions( RenderOptions renderOptions )
    {
        this.renderOptions = renderOptions;
    }
    
    /**
     * @return the rendering options that this CanvasPeer abides by.
     */
    public final RenderOptions getRenderOptions()
    {
        return ( renderOptions );
    }
    
    /**
     * @return the {@link OpenGLStatesCache} of this render context.
     */
    public final OpenGLStatesCache getStatesCache()
    {
        return ( statesCache );
    }
    
    /**
     * Sets the color with which to clear the screen before each frame.
     * 
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     */
    public final void setClearColor( float r, float g, float b, float a )
    {
        clearColor[ 0 ] = r;
        clearColor[ 1 ] = g;
        clearColor[ 2 ] = b;
        clearColor[ 3 ] = a;
    }
    
    /** 
     * Disables or enables buffer clear operations.
     *
     * If DisableClearBuffer set to true, client guarantees that all it will take care of correct frame buffer
     * clearing/filling. This may provide speedups for example in cases where multiple transparent objects
     * rebdered over opaque background, and depth buffer testing/writing disabled.
     *
     * Default value for DisableClearBuffer flag is false.
     *
     * @param val New value for DisableClearBuffer flag
     */
    public final void setDisableClearBuffer( boolean val )
    {
        disableClearBuffer = val;
    }
    
    /** 
     * Sets "Full Overpaint" flag.
     *
     * If Full Overpaint set to true, client guarantees that all the screen always completely painted 
     * and no reason to clear color buffer 
     *
     * Default value for "Full Overpaint" flag is false.
     *
     * @param val New value for "Full Overpaint" flag
     */
    public final void setFullOverpaint( boolean val )
    {
        fullOverpaint = val;
    }
    
    public final void setForceNoSwap( boolean forceNoSwap )
    {
        this.forceNoSwap = forceNoSwap;
    }
    
    /**
     * Sets the colormask for the rendering
     * 
     * @param enableRed
     * @param enableGreen
     * @param enableBlue
     * @param enableAlpha
     */
    public final void setColorMask( boolean enableRed, boolean enableGreen, boolean enableBlue, boolean enableAlpha )
    {
        this.colorMask = 0;
        if ( enableRed )
            colorMask |= 1;
        if ( enableGreen )
            colorMask |= 2;
        if ( enableBlue )
            colorMask |= 4;
        if ( enableAlpha )
            colorMask |= 8;
    }
    
    /**
     * Sets flag that enables caching of the background in buffer region.
     *
     * @param enabled New value for background cache enable/disable flag
     */
    public void setBackgroundCachingEnabled( boolean enabled )
    {
        backgroundCachingEnabled = enabled;
    }
    
    /**
     * Called when begining a new frame to draw.
     * It fills up the state and shader arrays.
     */
    protected final void resetStateUnitStateArrays()
    {
        System.arraycopy( nulledStates, 0, stateIDs, 0, stateIDs.length );
    }
    
    protected void renderStart( PickRequest pickRequest )
    {
        resetStateUnitStateArrays();
        
        if ( pickRequest != null )
        {
            if ( pickRequest.getPickAll() )
                pickResults.clear();
            else
                pickResult = null;
        }
    }
    
    public final void forceState( int stateType )
    {
        stateIDs[ stateType ] = -1;
    }
    
    /**
     * Renders an Atom.
     * 
     * @param options
     * @param atom
     * @param view
     * @param frameId
     * 
     * @return the number of rendered Triangles
     */
    public final int renderAtom( RenderAtom< ? > atom, Object glObj, CanvasPeer canvasPeer, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache, View view, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        if ( atom.getNode().isBillboard() )
        {
            BillboardManager.updateBillboardGeometry( atom, view, getCanvasPeer().getWidth(), getCanvasPeer().getHeight(), nanoTime, nanoStep, frameId );
        }
        
        final int atomType = atom.getStateType();
        
        if ( ( atomType != org.xith3d.render.preprocessing.BoundsAtom.STATE_TYPE ) && ( renderMode == RenderMode.NORMAL ) )
        {
            final StateUnit[] stateUnits = atom.getStateUnits();
            StateUnit stateUnit;
            for ( int stateType = 0; stateType < StateUnit.MAX_STATE_TYPES; stateType++ )
            {
                stateUnit = stateUnits[ stateType ];
                
                final long stateId = stateUnit.getCachedStateId();
                if ( stateIDs[ stateType ] != stateId )
                {
                    final StateUnitPeer stateUnitPeer = stateUnitRegistry.getStateUnitPeer( stateType );
                    stateUnitPeer.apply( atom, stateUnit, glObj, canvasPeer, this, glCaps, view, statesCache, options, nanoTime, nanoStep, renderMode, frameId );
                    stateIDs[ stateType ] = stateId;
                }
            }
        }
        
        return ( stateUnitRegistry.getRenderAtomPeer( atomType ).renderAtom( atom, glObj, this, glCaps, view, options, nanoTime, nanoStep, renderMode, frameId ) );
    }
    
    public final List< PickResult > getPickResults()
    {
        return ( pickResults );
    }
    
    public final PickResult getPickResult()
    {
        return ( pickResult );
    }
    
    protected final RenderAtom< ? > getAtomByGlobalIndex( int index, List< RenderPass > renderPasses )
    {
        int offset = 0;
        
        for ( int i = 0; i < renderPasses.size(); i++ )
        {
            final RenderPass pass = renderPasses.get( i );
            final RenderBinProvider binProvider = pass.getRenderBinProvider();
            
            if ( pass.isEnabled() )
            {
                // check main opaque bin
                if ( index < ( offset + binProvider.getOpaqueBin().size() ) )
                    return ( binProvider.getOpaqueBin().getAtom( index - offset ) );
                
                offset += binProvider.getOpaqueBin().size();
                
                // check main transparent bin
                if ( index < ( offset + binProvider.getTransparentBin().size() ) )
                    return ( binProvider.getTransparentBin().getAtom( index - offset ) );
                
                offset += binProvider.getTransparentBin().size();
            }
            else
            {
                offset += binProvider.getAtomsCount();
            }
        }
        
        return ( null );
    }
    
    /**
     * Convert select buffer to List<PickResult>.
     */
    protected final Object convertSelectBuffer( int hits, List< RenderPass > renderPasses, boolean pickAll )
    {
        PickResult result;
        
        int position = 0;
        for ( int i = 0; i < hits; i++ )
        {
            int namesCount = selectBuffer.get( position++ );
            
            final float zMin = (float)selectBuffer.get( position++ ) / 0x7fffffff;
            final float zMax = (float)selectBuffer.get( position++ ) / 0x7fffffff;
            final float zMed = zMin + ( ( zMax - zMin ) / 2.0f );
            
            for ( int j = 0; j < namesCount; j++ )
            {
                final int selectName = selectBuffer.get( position++ );
                
                try
                {
                    RenderAtom< ? > atom = getAtomByGlobalIndex( selectName, renderPasses );
                    Shape3D shape = (Shape3D)atom.getNode();
                    
                    result = new PickResult( shape, zMin, zMax, zMed );
                    
                    if ( pickAll )
                        pickResults.add( result );
                    else if ( ( pickResult == null ) || ( result.getMinimumDistance() < pickResult.getMinimumDistance() ) )
                        pickResult = result;
                }
                catch ( Throwable t )
                {
                    t.printStackTrace();
                }
            }
        }
        
        if ( pickAll )
        {
            pickResults.sort();
            
            return ( pickResults );
        }
        
        return ( pickResult );
    }
    
    /**
     * The frame is complete.  The implementation should return as fast
     * as possible, so possibly another thread should be used to wait for
     * the drawing to be complete.
     */
    //protected final void renderDone( Object glObj, OpenGLCapabilities glCaps, View view, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    protected final void renderDone( long frameId )
    {
        final long[] lastFrameIDs = getStatesCache().lastFrameId;
        
        for ( int i = 0; i < lastFrameIDs.length; i++ )
            lastFrameIDs[i] = frameId;
    }
    
    /**
     * Renders a single frame using the View and RenderBins provided in
     * the RenderPasses object.
     * 
     * @param glObj the OpenGL handle object
     * @param view the View used to render
     * @param renderPasses the List of RenderPasses to iterate and render
     * @param layeredMode if true, the RenderPasses are handled in layered mode
     * @param frameId the current frame's id
     * @param pickRequest <code>null</code> for normal rendering
     */
    public abstract Object render( Object glObj, View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest );
    
    /**
     * Takes a screenshot of the current rendering
     * 
     * @param file the file to save the screenshot to
     * @param alpha with alpha channel?
     */
    public abstract void takeScreenshot( File file, boolean alpha );
    
    public RenderPeer( CanvasPeer canvasPeer, StateUnitPeerRegistry shaderRegistry, OpenGLStatesCache statesCache, RenderOptions renderOptions )
    {
        this.canvasPeer = canvasPeer;
        this.stateUnitRegistry = shaderRegistry;
        this.statesCache = statesCache;
        this.renderOptions = renderOptions;
    }
    
    public RenderPeer( CanvasPeer canvasPeer, StateUnitPeerRegistry shaderRegistry, OpenGLStatesCache statesCache )
    {
        this( canvasPeer, shaderRegistry, statesCache, new RenderOptions() );
    }
}
