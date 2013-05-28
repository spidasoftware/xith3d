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

/**
 * A class implementing this interface holds all information
 * necessary to configure a render pass.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface RenderPassConfig
{
    public static final View.ProjectionPolicy DEFAULT_PROJECTION_POLICY = View.ProjectionPolicy.PERSPECTIVE_PROJECTION;
    public static final View.CameraMode DEFAULT_CAMERA_MODE = View.CameraMode.VIEW_NORMAL;
    
    /**
     * Sets the projection policy.
     */
    void setProjectionPolicy( View.ProjectionPolicy policy );
    
    /**
     * @return the projection policy.
     */
    View.ProjectionPolicy getProjectionPolicy();
    
    /**
     * Sets the camera mode for this RenderPass.
     * 
     * @param cameraMode
     */
    void setCameraMode( View.CameraMode cameraMode );
    
    /**
     * @return the camera mode for this RenderPass.
     */
    View.CameraMode getCameraMode();
    
    /**
     * Sets the opaque RenderBinSorter for this RenderPass.<br>
     * This automatically sets the sorting policy to CUSTOM.
     * 
     * @see #setOpaqueSortingPolicy(org.xith3d.render.Renderer.OpaqueSortingPolicy)
     * 
     * @param sorter the RenderBinSorter to use for opaque shapes
     */
    void setOpaqueSorter( RenderBinSorter sorter );
    
    /**
     * @return sorter the RenderBinSorter to use for opaque shapes
     * 
     * @see #getOpaqueSortingPolicy()
     */
    RenderBinSorter getOpaqueSorter();
    
    /**
     * Sets the transparent RenderBinSorter for this RenderPass.<br>
     * This automatically sets the sorting policy to CUSTOM.
     * 
     * @param sorter the RenderBinSorter to use for transparent shapes
     */
    void setTransparentSorter( RenderBinSorter sorter );
    
    /**
     * @return sorter the RenderBinSorter to use for transparent shapes
     * 
     * @see #getTransparentSortingPolicy()
     */
    RenderBinSorter getTransparentSorter();
    
    /**
     * Sets the opaque sorting policy for this RenderPass. 
     * 
     * @param policy the new policy
     */
    void setOpaqueSortingPolicy( OpaqueSortingPolicy policy );
    
    /**
     * @return the current opaque sorting policy for this RenderPass
     */
    OpaqueSortingPolicy getOpaqueSortingPolicy();
    
    /**
     * Sets the transparency sorting policy for this RenderPass. 
     *
     * @param policy the new policy
     */
    void setTransparentSortingPolicy( TransparentSortingPolicy policy );
    
    /**
     * @return the current transparency sorting policy for this RenderPass.
     */
    TransparentSortingPolicy getTransparentSortingPolicy();
    
    /**
     * Sets the front clip distance.
     * All polygons which are closer to the camera than this value
     * will be culled.
     * 
     * @param frontClipDistance the new front clip distance
     */
    void setFrontClipDistance( float frontClipDistance );
    
    /**
     * @return the back clip distance.
     * All polygons which are farer from the camera than this value
     * will be culled.
     */
    float getFrontClipDistance();
    
    /**
     * Sets the back clip distance.
     * All polygons which are farer from the camera than this value
     * will be culled.
     * 
     * @param backClipDistance the new back clip distance
     */
    void setBackClipDistance( float backClipDistance );
    
    /**
     * @return the back clip distance.
     * All polygons which are farer from the camera than this value
     * will be culled.
     */
    float getBackClipDistance();
    
    /**
     * Sets the screen scale.
     */
    void setScreenScale( float screenScale );
    
    /**
     * @return the screen scale
     */
    float getScreenScale();
    
    /**
     * Sets the field of view.
     */
    void setFieldOfView( float fov );
    
    /**
     * @return the field of view
     */
    float getFieldOfView();
    
    /**
     * Sets the center of the View.
     * 
     * @param cov values range from 1 (left) to -1 (right) a value of null
     *            sets the center to (0|0) - the default
     */
    void setCenterOfView( Tuple2f cov );
    
    /**
     * @return the center of this View - values range from 1 (left) to -1
     *         (right)
     */
    Tuple2f getCenterOfView();
    
    /**
     * Sets the viewport of this RenderPass.
     * If the viewport is <i>null</i> the default viewport is assumed.
     */
    void setViewport( Rect2i rect );
    
    /**
     * @return the viewport of this RenderPass.
     *         If the viewport is <i>null</i> the default viewport is assumed.
     */
    Rect2i getViewport();
    
    /**
     * Sets the View Transform3D for this RenderPass.
     * If the transform is <i>null</i> the default is taken from the View.
     * 
     * @param viewTransform
     */
    void setViewTransform( Transform3D viewTransform );
    
    /**
     * @return the View Transform3D for this RenderPass.
     * If the transform is <i>null</i> the default is taken from the View.
     */
    Transform3D getViewTransform();
    
    /**
     * Sets the RenderOptions for this RenderPass.
     * If the RenderOptions are <i>null</i> the default is taken from the Canvas.
     * 
     * @param renderOptions
     */
    void setRenderOptions( RenderOptions renderOptions );
    
    /**
     * @return the RenderOptions for this RenderPass.
     * If the RenderOptions are <i>null</i> the default is taken from the Canvas.
     */
    RenderOptions getRenderOptions();
    
    /**
     * Sets the color-mask as a bit-mask (red = 1/1, green = 2/2, blue = 3/4, alpha = 4/8).
     * 
     * @param colorMask
     */
    void setColorMask( int colorMask );
    
    /**
     * Sets the color-mask.
     * 
     * @param enableRed
     * @param enableGreen
     * @param enableBlue
     * @param enableAlpha
     */
    void setColorMask( boolean enableRed, boolean enableGreen, boolean enableBlue, boolean enableAlpha );
    
    /**
     * @return this RenderPass'es color-mask (red = 1/1, green = 2/2, blue = 3/4, alpha = 4/8).
     */
    int getColorMask();
    
    /**
     * Sets this RenderPassConfigProvider to the values of the template.
     * 
     * @param template the RenderPassConfigProvider to take the values from
     */
    void set( RenderPassConfig template );
}
