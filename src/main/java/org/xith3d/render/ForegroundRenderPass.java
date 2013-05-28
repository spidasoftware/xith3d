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

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.render.Renderer.OpaqueSortingPolicy;
import org.xith3d.render.Renderer.TransparentSortingPolicy;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View.CameraMode;
import org.xith3d.scenegraph.View.ProjectionPolicy;

/**
 * This is a simple {@link RenderPass} extension, that automatically sets up
 * itself to be rendered in the foreground.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ForegroundRenderPass extends RenderPass
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void setClipperEnabled( boolean enabled )
    {
        throw new Error( "You cannot set this on a ForegroundRenderPass." );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isClipperEnabled()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrustumCullingEnabled( boolean enabled )
    {
        throw new Error( "You cannot set this on a ForegroundRenderPass." );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isFrustumCullingEnabled()
    {
        return ( false );
    }
    
    /**
     * Creates a new RenderPass assotiated with the given BranchGroup.
     * 
     * @param branchGroup the BranchGroup assotiated with this RenderPass
     * @param config this RenderPass'es configuration
     */
    public ForegroundRenderPass( BranchGroup branchGroup, RenderPassConfig config )
    {
        super( branchGroup, config );
    }
    
    /**
     * Creates a new RenderPass assotiated with a new BranchGroup.
     * 
     * @param config this RenderPass'es configuration
     */
    public ForegroundRenderPass( RenderPassConfig config )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( branchGroup, config ) );
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( branchGroup, config ) );
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
    public static ForegroundRenderPass createPerspective( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( new BranchGroup(), config ) );
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
    public static ForegroundRenderPass createPerspective( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( new BranchGroup(), config ) );
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createPerspective( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createPerspective( float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createPerspective( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createPerspective( float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createPerspective( CameraMode cameraMode, float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createPerspective( float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createPerspective( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createPerspective( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup, CameraMode cameraMode )
    {
        return ( createPerspective( branchGroup, cameraMode, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     */
    public static ForegroundRenderPass createPerspective( BranchGroup branchGroup )
    {
        return ( createPerspective( branchGroup, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
        
    }
    
    /**
     * Creates new perspective RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     */
    public static ForegroundRenderPass createPerspective( CameraMode cameraMode )
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
    public static ForegroundRenderPass createPerspective()
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( branchGroup, config ) );
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( branchGroup, config ) );
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
    public static ForegroundRenderPass createParallel( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                cameraMode,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( new BranchGroup(), config ) );
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
    public static ForegroundRenderPass createParallel( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        BaseRenderPassConfig config = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION,
                                                                opaqueSortingPolicy, transparentSortingPolicy,
                                                                frontClipDistance, backClipDistance,
                                                                screenScale, fieldOfView
                                                              );
        
        return ( new ForegroundRenderPass( new BranchGroup(), config ) );
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createParallel( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createParallel( float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createParallel( CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createParallel( float frontClipDistance, float backClipDistance, float screenScale )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createParallel( CameraMode cameraMode, float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createParallel( float frontClipDistance, float backClipDistance )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createParallel( CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createParallel( OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode )
    {
        return ( createParallel( branchGroup, cameraMode, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.
     * 
     * @param branchGroup the BranchGroup the use for the new RenderPass
     */
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup )
    {
        return ( createParallel( branchGroup, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     * 
     * @param cameraMode the CameraMode to use for the new RenderPass
     */
    public static ForegroundRenderPass createParallel( CameraMode cameraMode )
    {
        return ( createParallel( new BranchGroup(), cameraMode, -9999.0f, -9999.0f, -9999.0f, -9999.0f ) );
    }
    
    /**
     * Creates new parallel RenderPass with the given config parameters.<br>
     * A new BranchGroup is created and assotiated.
     */
    public static ForegroundRenderPass createParallel()
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
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
        
        return ( new ForegroundRenderPass( branchGroup, config ) );
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, CameraMode cameraMode, Sized2iRO screenSize, boolean moveCenterToUpperLeft )
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
    public static ForegroundRenderPass createParallel( BranchGroup branchGroup, Sized2iRO screenSize, boolean moveCenterToUpperLeft )
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
    public static ForegroundRenderPass createParallel( CameraMode cameraMode, int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
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
    public static ForegroundRenderPass createParallel( int screenWidth, int screenHeight, boolean moveCenterToUpperLeft )
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
    public static ForegroundRenderPass createParallel( CameraMode cameraMode, Sized2iRO screenSize, boolean moveCenterToUpperLeft )
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
    public static ForegroundRenderPass createParallel( Sized2iRO screenSize, boolean moveCenterToUpperLeft )
    {
        return ( createParallel( new BranchGroup(), screenSize.getWidth(), screenSize.getHeight(), moveCenterToUpperLeft ) );
    }
}
