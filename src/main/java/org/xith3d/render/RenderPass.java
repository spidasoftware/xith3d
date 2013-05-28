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

import java.util.ArrayList;

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.datatypes.NamableObject;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.shadows.ShadowFactory;
import org.xith3d.render.Renderer.OpaqueSortingPolicy;
import org.xith3d.render.Renderer.TransparentSortingPolicy;
import org.xith3d.render.preprocessing.RenderBinProvider;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View.CameraMode;
import org.xith3d.scenegraph.View.ProjectionPolicy;

/**
 * This class holds all information for a render pass.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class RenderPass implements Enableable, NamableObject
{
    private RenderPassConfig config;
    private final RenderBinProvider binProvider;
    private BranchGroup branchGroup;
    private final ArrayList< RenderCallback > callbacks = new ArrayList< RenderCallback >( 8 );
    private final RenderCallback.RenderCallbackNotifier callbackNotifier = new RenderCallback.RenderCallbackNotifier( callbacks );
    private RenderTarget renderTarget = null;
    private boolean isClipperEnabled = true;
    private boolean isScissorEnabled = false;
    private boolean isFrustumCullingEnabled = true;
    private boolean isLayeredForced = false;
    private boolean isUnlayeredForced = false;
    private boolean isEnabled = true;
    
    private Light shadowCasterLight = null;
    
    private String name = "";
    
    /**
     * {@inheritDoc}
     */
    public void setName( String name )
    {
        this.name = name;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * Sets this RenderPass'es BranchGroup.
     */
    public void setBranchGroup( BranchGroup branchGroup )
    {
        if ( branchGroup == null )
            throw new IllegalArgumentException( "branchGroup must not be null." );
        
        this.branchGroup = branchGroup;
    }
    
    /**
     * @return this RenderPass'es BranchGroup.
     */
    public BranchGroup getBranchGroup()
    {
        return ( branchGroup );
    }
    
    /**
     * Sets the RenderPassConfig for this RenderPass.
     */
    public void setConfig( RenderPassConfig config )
    {
        this.config = config;
    }
    
    /**
     * @return the RenderPassConfig for this RenderPass
     */
    public RenderPassConfig getConfig()
    {
        return ( config );
    }
    
    /**
     * This is a simple conveniece mode to switch wireframe-mode
     * (enabled/disabled) on the attached RenderPassConfig's RenderOptions.
     * 
     * @return the new state.
     */
    public boolean switchWireframeMode()
    {
        return ( getConfig().getRenderOptions().switchWireframeMode() );
    }
    
    /**
     * @return this RenderPass'es RenderBinProvider
     */
    public RenderBinProvider getRenderBinProvider()
    {
        return ( binProvider );
    }
    
    /**
     * Adds a RenderCallback to this RenderPass, which is notified in
     * different render stages.
     * 
     * @param callback
     */
    public final void addRenderCallback( RenderCallback callback )
    {
        callbacks.add( callback );
    }
    
    /**
     * Removes a RenderCallback from this RenderPass.
     * 
     * @param callback
     */
    public final void removeRenderCallback( RenderCallback callback )
    {
        callbacks.remove( callback );
    }
    
    /**
     * @return a notifier for the {@link RenderCallback}s.
     */
    public final RenderCallback.RenderCallbackNotifier getRenderCallbackNotifier()
    {
        return ( callbackNotifier );
    }
    
    /**
     * Sets the RenderTarget for this RenderPass.<br>
     * Use <code>null</code> for the default frame buffer.
     * 
     * @param renderTarget
     */
    public void setRenderTarget( RenderTarget renderTarget )
    {
        this.renderTarget = renderTarget;
    }
    
    /**
     * @return the RenderTarget for this RenderPass.<br>
     * This is <code>null</code> for the default frame buffer.
     */
    public RenderTarget getRenderTarget()
    {
        return ( renderTarget );
    }
    
    /**
     * Enables or disables Clipper for this RenderPass.
     * 
     * @param enabled
     */
    public void setClipperEnabled( boolean enabled )
    {
        this.isClipperEnabled = enabled;
    }
    
    /**
     * @return if Clipper is enabled for this RenderPass
     */
    public boolean isClipperEnabled()
    {
        return ( isClipperEnabled );
    }
    
    /**
     * Enables or disables scissor for this RenderPass.
     * 
     * @param enabled
     */
    public void setScissorEnabled( boolean enabled )
    {
        this.isScissorEnabled = enabled;
    }
    
    /**
     * @return if scissor is enabled for this RenderPass
     */
    public boolean isScissorEnabled()
    {
        return ( isScissorEnabled );
    }
    
    /**
     * Enables or disables Frustum culling for this RenderPass.
     * 
     * @param enabled
     */
    public void setFrustumCullingEnabled( boolean enabled )
    {
        this.isFrustumCullingEnabled = enabled;
    }
    
    /**
     * @return if Frustum culling is enabled for this RenderPass
     */
    public boolean isFrustumCullingEnabled()
    {
        return ( isFrustumCullingEnabled );
    }
    
    /**
     * Sets this RenderPass to be forced to be rendered in layered mode.<br>
     * If the Renderer is set up to render in layered mode, this flag is
     * ignored.
     * 
     * @param forced
     */
    public void setLayeredModeForced( boolean forced )
    {
        this.isLayeredForced = forced;
    }
    
    /**
     * @return if this RenderPass is forced to be rendered in layered mode.<br>
     * If the Renderer is set up to render in layered mode, this flag is
     * ignored.
     */
    public boolean isLayeredModeForced()
    {
        return ( isLayeredForced );
    }
    
    /**
     * Sets this RenderPass to be forced to be rendered in non-layered mode.<br>
     * If the Renderer is set up to render in non-layered mode, this flag is
     * ignored.
     * 
     * @param forced
     */
    public void setUnlayeredModeForced( boolean forced )
    {
        this.isUnlayeredForced = forced;
    }
    
    /**
     * @return if this RenderPass is forced to be rendered in non-layered mode.<br>
     * If the Renderer is set up to render in non-layered mode, this flag is
     * ignored.
     */
    public boolean isUnlayeredModeForced()
    {
        return ( isUnlayeredForced );
    }
    
    /**
     * Enables or disables this RenderPass.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled )
    {
        this.isEnabled = enabled;
    }
    
    /**
     * @return if this RenderPass is enabled
     */
    public boolean isEnabled()
    {
        return ( isEnabled );
    }
    
    /**
     * Sets the {@link Light}, which is capable of casting shadows for this RenderPass.
     */
    public void setShadowCasterLight( Light light )
    {
        final EffectFactory effFact = EffectFactory.getInstance();
        if ( effFact == null )
            throw new IllegalStateException( "No EffectFactory found!" );
        
        final ShadowFactory shadowFact = effFact.getShadowFactory();
        if ( shadowFact == null )
            throw new IllegalStateException( "No ShadowFactory registered!" );
        
        shadowFact.verifyLight( light );
        
        this.shadowCasterLight = light;
    }
    
    /**
     * @return the {@link Light}, which is capable of casting shadows for this RenderPass.
     */
    public final Light getShadowCasterLight()
    {
        return ( shadowCasterLight );
    }
    
    /**
     * This method frees OpenGL resources (names) for all Nodes in the traversal
     * of this Node(-Group).
     * 
     * @param canvasPeer
     */
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( getBranchGroup() != null )
            getBranchGroup().freeOpenGLResources( canvasPeer );
    }
    
    /**
     * This method frees OpenGL resources (names) for all Nodes in the traversal
     * of this Node(-Group).
     * 
     * @param canvas
     */
    public final void freeOpenGLResources( Canvas3D canvas )
    {
        if ( canvas.getPeer() == null )
            throw new Error( "The given Canvas3D is not linked to a CanvasPeer." );
        
        freeOpenGLResources( canvas.getPeer() );
    }
    
    protected RenderBinProvider createRenderBinProvider()
    {
        return ( new RenderBinProvider() );
    }
    
    /**
     * Creates a new RenderPass assotiated with the given BranchGroup.
     * 
     * @param branchGroup the BranchGroup assotiated with this RenderPass
     * @param config this RenderPass'es configuration
     */
    public RenderPass( BranchGroup branchGroup, RenderPassConfig config )
    {
        if ( branchGroup == null )
            throw new IllegalArgumentException( "branchGroup must not be null." );
        
        this.branchGroup = branchGroup;
        this.config = config;
        this.binProvider = createRenderBinProvider();
    }
    
    /**
     * Creates a new RenderPass assotiated with a new BranchGroup.
     * 
     * @param config this RenderPass'es configuration
     */
    public RenderPass( RenderPassConfig config )
    {
        this( new BranchGroup(), config );
    }
    
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( branchGroup, config ) );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( branchGroup, config ) );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( new BranchGroup(), config ) );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( new BranchGroup(), config ) );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createPerspective( branchGroup, cameraMode,
                                   (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                   frontClipDistance, backClipDistance,
                                   screenScale, fieldOfView
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createPerspective( branchGroup,
                                   (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                   frontClipDistance, backClipDistance,
                                   screenScale, fieldOfView
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createPerspective( new BranchGroup(), cameraMode,
                                   (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                   frontClipDistance, backClipDistance,
                                   screenScale, fieldOfView
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createPerspective( float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createPerspective( new BranchGroup(),
                                   (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                   frontClipDistance, backClipDistance,
                                   screenScale, fieldOfView
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createPerspective( branchGroup, cameraMode,
                                   frontClipDistance, backClipDistance,
                                   screenScale, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createPerspective( branchGroup,
                                   frontClipDistance, backClipDistance,
                                   screenScale, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createPerspective( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createPerspective( new BranchGroup(), cameraMode,
                                   frontClipDistance, backClipDistance,
                                   screenScale, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createPerspective( float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createPerspective( new BranchGroup(),
                                   frontClipDistance, backClipDistance,
                                   screenScale, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance )
    {
        return ( createPerspective( branchGroup, cameraMode,
                                   frontClipDistance, backClipDistance,
                                   -9999.0f, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, float frontClipDistance, float backClipDistance )
    {
        return ( createPerspective( branchGroup,
                                   frontClipDistance, backClipDistance,
                                   -9999.0f, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createPerspective( CameraMode cameraMode, float frontClipDistance, float backClipDistance )
    {
        return ( createPerspective( new BranchGroup(), cameraMode,
                                   frontClipDistance, backClipDistance,
                                   -9999.0f, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createPerspective( float frontClipDistance, float backClipDistance )
    {
        return ( createPerspective( new BranchGroup(),
                                   frontClipDistance, backClipDistance,
                                   -9999.0f, -9999.0f
                                 )
              );
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createPerspective( branchGroup, cameraMode,
                                   opaqueSortingPolicy,
                                   transparentSortingPolicy,
                                   -9999.0f, -9999.0f,
                                   -9999.0f, -9999.0f
                                 )
              );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createPerspective( branchGroup,
                                   opaqueSortingPolicy,
                                   transparentSortingPolicy,
                                   -9999.0f, -9999.0f,
                                   -9999.0f, -9999.0f
                                 )
              );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createPerspective( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createPerspective( new BranchGroup(), cameraMode,
                                   opaqueSortingPolicy,
                                   transparentSortingPolicy,
                                   -9999.0f, -9999.0f, -9999.0f, -9999.0f
                                 )
              );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createPerspective( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createPerspective( new BranchGroup(),
                                   opaqueSortingPolicy,
                                   transparentSortingPolicy,
                                   -9999.0f, -9999.0f, -9999.0f, -9999.0f
                                 )
              );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     */
    public static RenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode )
    {
        return ( createPerspective( branchGroup, cameraMode, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     */
    public static RenderPass createPerspective( BranchGroup branchGroup )
    {
        return ( createPerspective( branchGroup, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     */
    public static RenderPass createPerspective( CameraMode cameraMode )
    {
        return ( createPerspective( new BranchGroup(), cameraMode,
                                   -9999.0f, -9999.0f,
                                   -9999.0f, -9999.0f
                                 )
              );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     */
    public static RenderPass createPerspective()
    {
        return ( createPerspective( new BranchGroup(),
                                   -9999.0f, -9999.0f,
                                   -9999.0f, -9999.0f
                                 )
              );
    }
    
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( branchGroup, config ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( branchGroup, config ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( new BranchGroup(), config ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new RenderPass( new BranchGroup(), config ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createParallel( branchGroup, cameraMode,
                                (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                frontClipDistance, backClipDistance,
                                screenScale, fieldOfView
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createParallel( branchGroup,
                                (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                frontClipDistance, backClipDistance,
                                screenScale, fieldOfView
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createParallel( new BranchGroup(), cameraMode,
                                (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                frontClipDistance, backClipDistance,
                                screenScale, fieldOfView
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public static RenderPass createParallel( float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        return ( createParallel( new BranchGroup(),
                                (OpaqueSortingPolicy)null, (TransparentSortingPolicy)null,
                                frontClipDistance, backClipDistance,
                                screenScale, fieldOfView
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createParallel( branchGroup, cameraMode,
                                frontClipDistance, backClipDistance,
                                screenScale, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createParallel( branchGroup,
                                frontClipDistance, backClipDistance,
                                screenScale, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createParallel( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createParallel( new BranchGroup(), cameraMode,
                                frontClipDistance, backClipDistance,
                                screenScale, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public static RenderPass createParallel( float frontClipDistance, float backClipDistance, float screenScale )
    {
        return ( createParallel( new BranchGroup(),
                                frontClipDistance, backClipDistance,
                                screenScale, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance )
    {
        return ( createParallel( branchGroup, cameraMode,
                                frontClipDistance, backClipDistance,
                                -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createParallel( BranchGroup branchGroup, float frontClipDistance, float backClipDistance )
    {
        return ( createParallel( branchGroup,
                                frontClipDistance, backClipDistance,
                                -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createParallel( CameraMode cameraMode, float frontClipDistance, float backClipDistance )
    {
        return ( createParallel( new BranchGroup(),
                                cameraMode,
                                frontClipDistance, backClipDistance,
                                -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public static RenderPass createParallel( float frontClipDistance, float backClipDistance )
    {
        return ( createParallel( new BranchGroup(),
                                frontClipDistance, backClipDistance,
                                -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createParallel( branchGroup,
                                cameraMode,
                                opaqueSortingPolicy,
                                transparentSortingPolicy,
                                -9999.0f, -9999.0f, -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createParallel( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createParallel( branchGroup,
                                opaqueSortingPolicy,
                                transparentSortingPolicy,
                                -9999.0f, -9999.0f, -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createParallel( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createParallel( new BranchGroup(),
                                cameraMode,
                                opaqueSortingPolicy,
                                transparentSortingPolicy,
                                -9999.0f, -9999.0f, -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public static RenderPass createParallel( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        return ( createParallel( new BranchGroup(),
                                opaqueSortingPolicy,
                                transparentSortingPolicy,
                                -9999.0f, -9999.0f, -9999.0f, -9999.0f
                              )
              );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode )
    {
        return ( createParallel( branchGroup, cameraMode, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     */
    public static RenderPass createParallel( BranchGroup branchGroup )
    {
        return ( createParallel( branchGroup, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     */
    public static RenderPass createParallel( CameraMode cameraMode )
    {
        return ( createParallel( new BranchGroup(), cameraMode, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     */
    public static RenderPass createParallel()
    {
        return ( createParallel( new BranchGroup(), -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     * 
     * @param branchGroup
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param screenWidth
     * @param screenHeight
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
    {
        final Tuple2f centerOfView;
        if ( moveCenterToUpperLeft )
        {
            centerOfView = new Tuple2f( -(float)screenWidth / 2.0f, (float)screenHeight / 2.0f );
        }
        else
        {
            centerOfView = null;
        }
        
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                (OpaqueSortingPolicy)null, TransparentSortingPolicy.SORT_BY_Z_VALUE,
                                                                -9999.0f, -9999.0f,
                                                                (float)screenWidth / 2.0f, -9999.0f
                                                              );
        config.setCenterOfView( centerOfView );
        
        return ( new RenderPass( branchGroup, config ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     *     
     * @param branchGroup
     * @param screenWidth
     * @param screenHeight
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( BranchGroup branchGroup, int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( branchGroup, RenderPassConfig.DEFAULT_CAMERA_MODE, screenWidth, screenHeight, moveCenterToUpperLeft ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     * 
     * @param branchGroup
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param screenSize
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, Sized2iRO screenSize, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( branchGroup, cameraMode, screenSize.getWidth(), screenSize.getHeight(), moveCenterToUpperLeft ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     * 
     * @param branchGroup
     * @param screenSize
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( BranchGroup branchGroup, Sized2iRO screenSize, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( branchGroup, screenSize.getWidth(), screenSize.getHeight(), moveCenterToUpperLeft ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param screenWidth
     * @param screenHeight
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( CameraMode cameraMode, int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( new BranchGroup(), cameraMode, screenWidth, screenHeight, moveCenterToUpperLeft ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     * 
     * @param screenWidth
     * @param screenHeight
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( new BranchGroup(), screenWidth, screenHeight, moveCenterToUpperLeft ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param screenSize
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( CameraMode cameraMode, Sized2iRO screenSize, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( new BranchGroup(), cameraMode, screenSize.getWidth(), screenSize.getHeight(), moveCenterToUpperLeft ) );
    }
    
    /**
     * Creates a new RenderPass for parallel projection of real-scaled shapes.
     * 
     * @param screenSize
     * @param moveCenterToUpperLeft
     * 
     * @return the new RenderPass
     */
    public static RenderPass createParallel( Sized2iRO screenSize, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( new BranchGroup(), screenSize.getWidth(), screenSize.getHeight(), moveCenterToUpperLeft ) );
    }
    
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, int resolutionX, int resolutionY )
    {
        RenderPass pass = createParallel( branchGroup, cameraMode, opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolutionX / 2f, -9999.0f );
        
        final RenderPassConfig passConfig = pass.getConfig();
        
        /*
         * Setup view-transform for this RenderPass,
         * so that good clipping-planes can be used.
         */
        final float zPos = frontClipDistance + ( ( backClipDistance - frontClipDistance ) / 2f );
        Transform3D viewTrans = new Transform3D();
        viewTrans.lookAlong( 0f, 0f, zPos, 0f, 0f, -1f );
        passConfig.setViewTransform( viewTrans );
        
        passConfig.setCenterOfView( new Point2f( -resolutionX / 2.0f, resolutionY / 2.0f ) );
        
        return ( pass );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, Sized2iRO resolution )
    {
        return ( create2D( branchGroup, cameraMode, opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, int resolutionX, int resolutionY )
    {
        return ( create2D( branchGroup, cameraMode, opaqueSortingPolicy, transparentSortingPolicy, -1f, 1f, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, Sized2iRO resolution )
    {
        return ( create2D( branchGroup, cameraMode, opaqueSortingPolicy, transparentSortingPolicy, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, CameraMode cameraMode, int resolutionX, int resolutionY )
    {
        return ( create2D( branchGroup, cameraMode, OpaqueSortingPolicy.SORT_BY_Z_VALUE, TransparentSortingPolicy.SORT_BY_Z_VALUE, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, CameraMode cameraMode, Sized2iRO resolution )
    {
        return ( create2D( branchGroup, cameraMode, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, int resolutionX, int resolutionY )
    {
        return ( create2D( branchGroup, RenderPassConfig.DEFAULT_CAMERA_MODE, opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, Sized2iRO resolution )
    {
        return ( create2D( branchGroup, opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, int resolutionX, int resolutionY )
    {
        return ( create2D( branchGroup, opaqueSortingPolicy, transparentSortingPolicy, -1f, 1f, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, Sized2iRO resolution )
    {
        return ( create2D( branchGroup, opaqueSortingPolicy, transparentSortingPolicy, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, int resolutionX, int resolutionY )
    {
        return ( create2D( branchGroup, OpaqueSortingPolicy.SORT_BY_Z_VALUE, TransparentSortingPolicy.SORT_BY_Z_VALUE, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( BranchGroup branchGroup, Sized2iRO resolution )
    {
        return ( create2D( branchGroup, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, int resolutionX, int resolutionY )
    {
        return ( create2D( new BranchGroup(), cameraMode, opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, Sized2iRO resolution )
    {
        return ( create2D( cameraMode, opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, int resolutionX, int resolutionY )
    {
        return ( create2D( cameraMode, opaqueSortingPolicy, transparentSortingPolicy, -1f, 1f, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, Sized2iRO resolution )
    {
        return ( create2D( cameraMode, opaqueSortingPolicy, transparentSortingPolicy, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( CameraMode cameraMode, int resolutionX, int resolutionY )
    {
        return ( create2D( cameraMode, OpaqueSortingPolicy.SORT_BY_Z_VALUE, TransparentSortingPolicy.SORT_BY_Z_VALUE, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( CameraMode cameraMode, Sized2iRO resolution )
    {
        return ( create2D( cameraMode, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, int resolutionX, int resolutionY )
    {
        return ( create2D( RenderPassConfig.DEFAULT_CAMERA_MODE, opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, Sized2iRO resolution )
    {
        return ( create2D( opaqueSortingPolicy, transparentSortingPolicy, frontClipDistance, backClipDistance, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, int resolutionX, int resolutionY )
    {
        return ( create2D( opaqueSortingPolicy, transparentSortingPolicy, -1f, 1f, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, Sized2iRO resolution )
    {
        return ( create2D( opaqueSortingPolicy, transparentSortingPolicy, resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param resolutionX the x-resolution of the screen
     * @param resolutionY the y-resolution of the screen
     */
    public static RenderPass create2D( int resolutionX, int resolutionY )
    {
        return ( create2D( OpaqueSortingPolicy.SORT_BY_Z_VALUE, TransparentSortingPolicy.SORT_BY_Z_VALUE, resolutionX, resolutionY ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param resolution the resolution of the screen
     */
    public static RenderPass create2D( Sized2iRO resolution )
    {
        return ( create2D( resolution.getWidth(), resolution.getHeight() ) );
    }
}
