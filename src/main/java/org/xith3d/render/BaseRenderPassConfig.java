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

import org.openmali.types.twodee.Rect2i;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.render.Renderer.OpaqueSortingPolicy;
import org.xith3d.render.Renderer.TransparentSortingPolicy;
import org.xith3d.render.preprocessing.sorting.RenderBinSorter;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.View.ProjectionPolicy;

/**
 * This class holds all information necessary to configure a render pass.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class BaseRenderPassConfig implements RenderPassConfig
{
    private View.ProjectionPolicy projectionPolicy;
    private View.CameraMode cameraMode;
    private RenderBinSorter opaqueRenderBinSorter;
    private RenderBinSorter transparentRenderBinSorter;
    private OpaqueSortingPolicy opaqueSortingPolicy;
    private TransparentSortingPolicy transparentSortingPolicy;
    private float frontClipDistance;
    private float backClipDistance;
    private float screenScale;
    private float fieldOfView;
    private Tuple2f centerOfView = null;
    private Rect2i viewport;
    private Transform3D viewTransform;
    private RenderOptions renderOptions;
    private int colorMask;
    
    /**
     * {@inheritDoc}
     */
    public View.ProjectionPolicy getProjectionPolicy()
    {
        return ( projectionPolicy );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setProjectionPolicy( View.ProjectionPolicy policy )
    {
        if ( cameraMode == null )
            throw new IllegalArgumentException( "ProjectionPolicy must not be null" );
        
        projectionPolicy = policy;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCameraMode( View.CameraMode cameraMode )
    {
        this.cameraMode = cameraMode;
    }
    
    /**
     * @return the camera mode for this RenderPass.
     */
    public View.CameraMode getCameraMode()
    {
        return ( cameraMode );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setOpaqueSorter( RenderBinSorter sorter )
    {
        this.opaqueRenderBinSorter = sorter;
        this.opaqueSortingPolicy = OpaqueSortingPolicy.CUSTOM;
    }
    
    /**
     * {@inheritDoc}
     */
    public final RenderBinSorter getOpaqueSorter()
    {
        return ( opaqueRenderBinSorter );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setTransparentSorter( RenderBinSorter sorter )
    {
        this.transparentRenderBinSorter = sorter;
        this.transparentSortingPolicy = TransparentSortingPolicy.CUSTOM;
    }
    
    /**
     * {@inheritDoc}
     */
    public final RenderBinSorter getTransparentSorter()
    {
        return ( transparentRenderBinSorter );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setOpaqueSortingPolicy( OpaqueSortingPolicy policy )
    {
        if ( policy != null )
            this.opaqueRenderBinSorter = policy.getSorter();
        else
            this.opaqueRenderBinSorter = null;
        this.opaqueSortingPolicy = policy;
    }
    
    /**
     * {@inheritDoc}
     */
    public final OpaqueSortingPolicy getOpaqueSortingPolicy()
    {
        return ( opaqueSortingPolicy );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setTransparentSortingPolicy( TransparentSortingPolicy policy )
    {
        if ( policy != null )
            this.transparentRenderBinSorter = policy.getSorter();
        else
            this.transparentRenderBinSorter = null;
        this.transparentSortingPolicy = policy;
    }
    
    /**
     * {@inheritDoc}
     */
    public final TransparentSortingPolicy getTransparentSortingPolicy()
    {
        return ( transparentSortingPolicy );
    }
    
    /**
     * {@inheritDoc}
     */
    public float getFrontClipDistance()
    {
        return ( frontClipDistance );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFrontClipDistance( float frontClipDistance )
    {
        this.frontClipDistance = frontClipDistance;
    }
    
    /**
     * {@inheritDoc}
     */
    public float getBackClipDistance()
    {
        return ( backClipDistance );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBackClipDistance( float backClipDistance )
    {
        this.backClipDistance = backClipDistance;
    }
    
    /**
     * {@inheritDoc}
     */
    public float getScreenScale()
    {
        return ( screenScale );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setScreenScale( float screenScale )
    {
        this.screenScale = screenScale;
    }
    
    /**
     * {@inheritDoc}
     */
    public float getFieldOfView()
    {
        return ( fieldOfView );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFieldOfView( float fov )
    {
        this.fieldOfView = fov;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setCenterOfView( Tuple2f cov )
    {
        this.centerOfView = cov;
    }
    
    /**
     * {@inheritDoc}
     */
    public final Tuple2f getCenterOfView()
    {
        return ( centerOfView );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setViewport( Rect2i rect )
    {
        this.viewport = rect;
    }
    
    /**
     * {@inheritDoc}
     */
    public Rect2i getViewport()
    {
        return ( viewport );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setViewTransform( Transform3D viewTransform )
    {
        this.viewTransform = viewTransform;
    }
    
    /**
     * {@inheritDoc}
     */
    public Transform3D getViewTransform()
    {
        return ( viewTransform );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setRenderOptions( RenderOptions renderOptions )
    {
        this.renderOptions = renderOptions;
    }
    
    /**
     * {@inheritDoc}
     */
    public RenderOptions getRenderOptions()
    {
        return ( renderOptions );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setColorMask( int colorMask )
    {
        this.colorMask = colorMask;
    }
    
    /**
     * {@inheritDoc}
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
     * @return this RenderPass'es color-mask (red = 1/1, green = 2/2, blue = 3/4, alpha = 4/8).
     */
    public final int getColorMask()
    {
        return ( colorMask );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void set( RenderPassConfig template )
    {
        this.projectionPolicy = template.getProjectionPolicy();
        this.cameraMode = template.getCameraMode();
        this.setOpaqueSortingPolicy( template.getOpaqueSortingPolicy() );
        this.opaqueRenderBinSorter = template.getOpaqueSorter();
        this.setTransparentSortingPolicy( template.getTransparentSortingPolicy() );
        this.transparentRenderBinSorter = template.getTransparentSorter();
        this.frontClipDistance = template.getFrontClipDistance();
        this.backClipDistance = template.getBackClipDistance();
        this.screenScale = template.getScreenScale();
        this.fieldOfView = template.getFieldOfView();
        this.centerOfView = template.getCenterOfView();
        this.viewport = template.getViewport();
        this.viewTransform = template.getViewTransform();
        this.renderOptions = template.getRenderOptions();
        this.colorMask = template.getColorMask();
    }
    
    /**
     * Clone constructor
     * 
     * @param template the RenderPassConfigProvider to take the values from
     */
    public BaseRenderPassConfig( RenderPassConfig template )
    {
        this.set( template );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     * @param viewTransform the View Transform3D for this RenderPass
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView, Rect2i viewport, Transform3D viewTransform )
    {
        this.projectionPolicy = projectionPolicy;
        this.setCameraMode( cameraMode );
        this.setOpaqueSortingPolicy( opaqueSortingPolicy );
        this.setTransparentSortingPolicy( transparentSortingPolicy );
        this.frontClipDistance = frontClipDistance;
        this.backClipDistance = backClipDistance;
        this.screenScale = screenScale;
        this.fieldOfView = fieldOfView;
        this.centerOfView = null;
        this.viewport = viewport;
        this.viewTransform = viewTransform;
        this.renderOptions = null;
        this.colorMask = -1;
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     * @param viewTransform the View Transform3D for this RenderPass
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView, Rect2i viewport, Transform3D viewTransform )
    {
        this( projectionPolicy,
              DEFAULT_CAMERA_MODE,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              viewport,
              null
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView, Rect2i viewport )
    {
        this( projectionPolicy,
              cameraMode,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              viewport,
              null
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView, Rect2i viewport )
    {
        this( projectionPolicy,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              viewport,
              null
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        this( projectionPolicy,
              cameraMode,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              null
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        this( projectionPolicy,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              null
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView, Rect2i viewport )
    {
        this( projectionPolicy,
              cameraMode,
              null,
              null,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView, Rect2i viewport )
    {
        this( projectionPolicy,
              null,
              null,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        this( projectionPolicy,
              cameraMode,
              null,
              null,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              null
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param fieldOfView the field of view to set
     */
    public BaseRenderPassConfig( ProjectionPolicy projectionPolicy, float frontClipDistance, float backClipDistance, float screenScale, float fieldOfView )
    {
        this( projectionPolicy,
              null,
              null,
              frontClipDistance,
              backClipDistance,
              screenScale,
              fieldOfView,
              null
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale, Rect2i viewport )
    {
        this( projectionPolicy,
              cameraMode,
              frontClipDistance,
              backClipDistance,
              screenScale,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, float frontClipDistance, float backClipDistance, float screenScale, Rect2i viewport )
    {
        this( projectionPolicy,
              frontClipDistance,
              backClipDistance,
              screenScale,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, float frontClipDistance, float backClipDistance, float screenScale )
    {
        this( projectionPolicy,
              cameraMode,
              frontClipDistance,
              backClipDistance,
              screenScale,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param screenScale The screen scale to set
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, float frontClipDistance, float backClipDistance, float screenScale )
    {
        this( projectionPolicy,
              frontClipDistance,
              backClipDistance,
              screenScale,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, float frontClipDistance, float backClipDistance, Rect2i viewport )
    {
        this( projectionPolicy,
              cameraMode,
              frontClipDistance,
              backClipDistance,
              -9999.0f,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, float frontClipDistance, float backClipDistance, Rect2i viewport )
    {
        this( projectionPolicy,
              frontClipDistance,
              backClipDistance,
              -9999.0f,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, float frontClipDistance, float backClipDistance )
    {
        this( projectionPolicy,
              cameraMode,
              frontClipDistance,
              backClipDistance,
              -9999.0f,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param frontClipDistance The front clip distance to set
     * @param backClipDistance The back clip distance to set
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, float frontClipDistance, float backClipDistance )
    {
        this( projectionPolicy,
              frontClipDistance,
              backClipDistance,
              -9999.0f,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, Rect2i viewport )
    {
        this( projectionPolicy,
              cameraMode,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy, Rect2i viewport )
    {
        this( projectionPolicy,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        this( projectionPolicy,
              cameraMode,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param opaqueSortingPolicy The sorting policy for opaque shapes
     * @param transparentSortingPolicy The sorting policy for transparent shapes
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, OpaqueSortingPolicy opaqueSortingPolicy, TransparentSortingPolicy transparentSortingPolicy )
    {
        this( projectionPolicy,
              opaqueSortingPolicy,
              transparentSortingPolicy,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode, Rect2i viewport )
    {
        this( projectionPolicy,
              cameraMode,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, Rect2i viewport )
    {
        this( projectionPolicy,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              viewport
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     * @param cameraMode
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy, View.CameraMode cameraMode )
    {
        this( projectionPolicy,
              cameraMode,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param projectionPolicy The projection policy to set
     */
    public BaseRenderPassConfig( View.ProjectionPolicy projectionPolicy )
    {
        this( projectionPolicy,
              -9999.0f,
              -9999.0f,
              -9999.0f,
              -9999.0f
            );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param cameraMode
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( View.CameraMode cameraMode, Rect2i viewport )
    {
        this( (ProjectionPolicy)null, cameraMode, viewport );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param viewport the viewport for this renderpass (<i>null</i> for default viewport)
     */
    public BaseRenderPassConfig( Rect2i viewport )
    {
        this( (ProjectionPolicy)null, viewport );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     * 
     * @param cameraMode
     */
    public BaseRenderPassConfig( View.CameraMode cameraMode )
    {
        this( (ProjectionPolicy)null, cameraMode );
    }
    
    /**
     * Creates new RenderPassConfig with the given parameters.
     */
    public BaseRenderPassConfig()
    {
        this( (ProjectionPolicy)null );
    }
}
