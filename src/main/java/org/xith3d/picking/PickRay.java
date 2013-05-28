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
package org.xith3d.picking;

import org.openmali.FastMath;
import org.openmali.types.twodee.Rect2i;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Ray3f;
import org.openmali.vecmath2.Tuple2f;

import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph._SG_PrivilegedAccess;
import org.xith3d.scenegraph.View.CameraMode;
import org.xith3d.scenegraph.View.ProjectionPolicy;

/**
 * Implements a pick-ray.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class PickRay extends Ray3f
{
    private static final long serialVersionUID = 6030131242963942660L;
    
    // store these values for later use
    private ProjectionPolicy projectionPolicy;
    private float fieldOfView, screenScale, centerViewX, centerViewY;
    private Matrix4f viewMatrix;
    private float canvasWidth, canvasHeight, canvasAspect;
    private float canvasX, canvasY;
    
    private View lastView = null;
    private RenderPassConfig lastRPC = null;
    
    /**
     * Recalculates the PickRay.
     * 
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public void recalculate( float canvasX, float canvasY )
    {
        this.canvasX = canvasX;
        this.canvasY = canvasY;
        
        float x = this.canvasX;
        float y = this.canvasY;
        
        if ( ( lastRPC != null ) && ( lastRPC.getViewport() != null ) )
        {
            Rect2i viewport = lastRPC.getViewport();
            
            x -= viewport.getLeft();
            y -= viewport.getTop();

            this.canvasWidth = viewport.getWidth();
            this.canvasHeight = viewport.getHeight();
            this.canvasAspect = this.canvasWidth / this.canvasHeight;
        }
        
        if ( projectionPolicy == ProjectionPolicy.PERSPECTIVE_PROJECTION )
        {
            /*
             * Normalize the pixel location to the range [-1.0, 1.0]
             * and modify the x coordinate to take aspect ratio into account.
             */
            float rx = ( 2.0f * x / canvasWidth - 1.0f ) * canvasAspect;
            float ry = 2.0f - 2.0f * y / canvasHeight - 1.0f;
            
            // Calculate the distance between viewer and view plane.
            float vpd = 1.0f / FastMath.tan( fieldOfView );
            
            /*
             * Originate the ray at the local origin of the viewer and direct
             * it toward the local position of the click in the view plane.
             */
            setOrigin( 0.0f, 0.0f, 0.0f );
            setDirection( rx, ry, -vpd );
            getDirection().normalize();
        }
        else
        {
            /*
             * Normalize the pixel location to the range [-1.0, 1.0]
             * and modify the y coordinate to take aspect ratio into account.
             */
            float rx = 2.0f * x / canvasWidth - 1.0f;
            float ry = ( 2.0f - 2.0f * y / canvasHeight - 1.0f ) / canvasAspect;
            
            /*
             * Originate the ray at local position of the click
             * in the view plane and direct it along the -Z axis.
             */
            final float s = screenScale;
            final float cx = centerViewX;
            final float cy = centerViewY;
            
            setOrigin( -cx + s * rx, -cy + s * ry, 9999f );
            setDirection( 0f, 0f, -1f );
        }
        
        // Transform the ray into world space.
        viewMatrix.transform( getOrigin() );
        viewMatrix.transform( getDirection() );
    }
    
    /**
     * Recalculates the PickRay.
     * 
     * @param viewMatrix teh model-view-matrix
     */
    public void recalculate( Matrix4f viewMatrix )
    {
        this.viewMatrix = viewMatrix;
        
        recalculate( canvasX, canvasY );
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param projectionPolicy the View's projection policy
     * @param fieldOfView the View's field of view
     * @param screenScale the View's screen scale
     * @param centerViewX the x-coordinate of center-of-view
     * @param centerViewY the x-coordinate of center-of-view
     * @param viewMatrix the View's transform matrix
     * @param canvasWidth the Canvas3D's width
     * @param canvasHeight the Canvas3D's height
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public void recalculate( ProjectionPolicy projectionPolicy, float fieldOfView, float screenScale, float centerViewX, float centerViewY, Matrix4f viewMatrix, float canvasWidth, float canvasHeight, float canvasX, float canvasY )
    {
        this.projectionPolicy = projectionPolicy;
        this.fieldOfView = fieldOfView;
        this.screenScale = screenScale;
        this.centerViewX = centerViewX;
        this.centerViewY = centerViewY;
        this.viewMatrix = viewMatrix;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.canvasAspect = canvasWidth / canvasHeight;
        
        recalculate( canvasX, canvasY );
    }
    
    public void recalculate( PickRay template, CameraMode cameraMode )
    {
        this.lastView = template.lastView;
        this.lastRPC = template.lastRPC;
        
        this.projectionPolicy = template.projectionPolicy;
        this.fieldOfView = template.fieldOfView;
        this.screenScale = template.screenScale;
        this.centerViewX = template.centerViewX;
        this.centerViewY = template.centerViewY;
        this.canvasWidth = template.canvasWidth;
        this.canvasHeight = template.canvasHeight;
        this.canvasAspect = template.canvasAspect;
        
        if ( lastRPC != null )
            _SG_PrivilegedAccess.set( lastView, true, lastRPC );
        
        if ( cameraMode == null )
            this.viewMatrix = lastView.getTransform().getMatrix4f();
        else
            this.viewMatrix = lastView.getModelViewTransform( cameraMode, true ).getMatrix4f();
        
        if ( lastRPC != null )
            _SG_PrivilegedAccess.set( lastView, false, null );
        
        recalculate( template.canvasX, template.canvasY );
    }
    
    private static final float getCenterViewX( Tuple2f centerOfView )
    {
        return ( ( centerOfView != null ) ? centerOfView.getX() : 0f );
    }
    
    private static final float getCenterViewY( Tuple2f centerOfView )
    {
        return ( ( centerOfView != null ) ? centerOfView.getY() : 0f );
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param canvas the Canvas3D to take for calculation
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public void recalculate( Canvas3D canvas, int canvasX, int canvasY )
    {
        final View view = canvas.getView();
        this.lastView = view;
        
        final CameraMode cameraMode = ( lastRPC == null ) ? null : lastRPC.getCameraMode();
        
        _SG_PrivilegedAccess.set( view, true, lastRPC );
        
        this.projectionPolicy = view.getProjectionPolicy();
        this.fieldOfView = view.getFieldOfView();
        this.screenScale = view.getScreenScale();
        this.centerViewX = getCenterViewX( view.getCenterOfView() );
        this.centerViewY = getCenterViewY( view.getCenterOfView() );
        
        /*
         * TODO: WORKAROUND: this enables picking to work with multiple viewports,
         * but we should check why the inverse matrix (returned by getModelViewTransform(...)) does not work here
         */
        //if ( cameraMode == null ) // <- original code
        if ( ( cameraMode == null ) || ( cameraMode == View.CameraMode.VIEW_NORMAL ) )
            this.viewMatrix = view.getTransform().getMatrix4f();
        else
            this.viewMatrix = view.getModelViewTransform( cameraMode, true ).getMatrix4f();

        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        this.canvasAspect = canvasWidth / canvasHeight;
        
        _SG_PrivilegedAccess.set( view, false, null );
        
        recalculate( (float)canvasX, (float)canvasY );
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param canvas the Canvas3D to take for calculation
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public void recalculate( int canvasX, int canvasY )
    {
        Canvas3D canvas = lastView.getCanvas3D( 0 );
        
        recalculate( canvas, canvasX, canvasY );
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param rpc the RenderPassConfig to take calculation values from
     * @param canvas the Canvas3D to take for calculation
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public void recalculate( RenderPassConfig rpc, Canvas3D canvas, int canvasX, int canvasY )
    {
        this.lastRPC = rpc;
        
        recalculate( canvas, canvasX, canvasY );
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param canvas the Canvas3D to take for calculation (and the Canvas3D's
     *            View)
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public void recalculate( CameraMode cameraMode, Canvas3D canvas, int canvasX, int canvasY )
    {
        final float centerViewX = getCenterViewX( canvas.getView().getCenterOfView() );
        final float centerViewY = getCenterViewY( canvas.getView().getCenterOfView() );
        
        recalculate( canvas.getView().getProjectionPolicy(), canvas.getView().getFieldOfView(), centerViewX, centerViewY, canvas.getView().getScreenScale(), ( cameraMode == null ) ? canvas.getView().getTransform().getMatrix4f() : canvas.getView().getModelViewTransform( cameraMode, true ).getMatrix4f(), (float)canvas.getWidth(), (float)canvas.getHeight(), (float)canvasX, (float)canvasY );
        
        this.lastView = canvas.getView();
        this.lastRPC = null;
    }
    
    /**
     * Creates a pick-ray
     */
    public PickRay()
    {
        super();
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param projectionPolicy the View's projection policy
     * @param fieldOfView the View's field of view
     * @param screenScale the View's screen scale
     * @param centerViewX the x-coordinate of center-of-view
     * @param centerViewY the x-coordinate of center-of-view
     * @param viewMatrix the View's transform matrix
     * @param canvasWidth the Canvas3D's width
     * @param canvasHeight the Canvas3D's height
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public PickRay( ProjectionPolicy projectionPolicy, float fieldOfView, float screenScale, float centerViewX, float centerViewY, Matrix4f viewMatrix, float canvasWidth, float canvasHeight, float canvasX, float canvasY )
    {
        super();
        
        this.projectionPolicy = projectionPolicy;
        this.fieldOfView = fieldOfView;
        this.screenScale = screenScale;
        this.centerViewX = centerViewX;
        this.centerViewY = centerViewY;
        this.viewMatrix = viewMatrix;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.canvasAspect = canvasWidth / canvasHeight;
        
        recalculate( canvasX, canvasY );
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param view the View to take for calculation
     * @param cameraMode
     * @param canvas the Canvas3D to take for calculation
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public PickRay( View view, CameraMode cameraMode, Canvas3D canvas, int canvasX, int canvasY )
    {
        this( view.getProjectionPolicy(), view.getFieldOfView(), view.getScreenScale(), getCenterViewX( view.getCenterOfView() ), getCenterViewY( view.getCenterOfView() ), view.getModelViewTransform( cameraMode, true ).getMatrix4f(), (float)canvas.getWidth(), (float)canvas.getHeight(), (float)canvasX, (float)canvasY );
        
        this.lastView = view;
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param cameraMode
     * @param canvas the Canvas3D to take for calculation (and the Canvas3D's View)
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public PickRay( CameraMode cameraMode, Canvas3D canvas, int canvasX, int canvasY )
    {
        this( canvas.getView().getProjectionPolicy(), canvas.getView().getFieldOfView(), canvas.getView().getScreenScale(), getCenterViewX( canvas.getView().getCenterOfView() ), getCenterViewY( canvas.getView().getCenterOfView() ), canvas.getView().getModelViewTransform( cameraMode, true ).getMatrix4f(), (float)canvas.getWidth(), (float)canvas.getHeight(), (float)canvasX, (float)canvasY );
        
        this.lastView = canvas.getView();
    }
    
    /**
     * Creates a pick-ray
     * 
     * @param rpc the RenderPassConfig to take calculation values from
     * @param canvas the Canvas3D to take for calculation (and the Canvas3D's View)
     * @param canvasX the x position on the Canvas3D
     * @param canvasY the y position on the Canvas3D
     */
    public PickRay( RenderPassConfig rpc, Canvas3D canvas, int canvasX, int canvasY )
    {
        this();
        
        recalculate( rpc, canvas, canvasX, canvasY );
    }
}
