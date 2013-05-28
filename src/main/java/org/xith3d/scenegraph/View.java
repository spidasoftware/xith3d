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
package org.xith3d.scenegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmali.FastMath;
import org.openmali.spatial.bodies.Frustum;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderPassConfig;

/**
 * The View defines a Camera or an Eye to tell OpenGL of the perspective to
 * render from.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public class View extends SceneGraphObject implements Transformable
{
    /**
     * Camera mode for a View.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static enum CameraMode
    {
        /**
         * Geometry will be rendered like normal geometry with the camera in its
         * current location. This field is set in Background and Foreground
         * NodeS.
         */
        VIEW_NORMAL,
        /**
         * Geometry will be rendered with the camera in the same location and
         * facing the same directrion This field is set in Background and
         * Foreground NodeS.
         */
        VIEW_FIXED,
        /**
         * Geometry will be rendered with the camera in the same location but
         * with the camera facing its current direction. This field is set in
         * Background and Foreground NodeS.
         */
        VIEW_FIXED_POSITION;
    }
    
    /**
     * Projection policy to render from.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static enum ProjectionPolicy
    {
        /**
         * render the scene in parallel projection mode
         */
        PARALLEL_PROJECTION,
        
        /**
         * render the scene in perspective projection mode
         */
        PERSPECTIVE_PROJECTION,
        
        /**
         * Uses the custom projection matrix.
         */
        CUSTOM_PROJECTION,
        ;
    }
    
    /**
     * Render the scene in parallel projection mode
     */
    public static final ProjectionPolicy PARALLEL_PROJECTION = ProjectionPolicy.PARALLEL_PROJECTION;
    
    /**
     * Render the scene in perspective projection mode
     */
    public static final ProjectionPolicy PERSPECTIVE_PROJECTION = ProjectionPolicy.PERSPECTIVE_PROJECTION;
    
    /**
     * The default field of view (FOV)
     */
    public static final float DEFAULT_FIELD_OF_VIEW = FastMath.toRad( 35.0f );
    
    /**
     * The default front clip distance (near clip plane) for perspective projection mode.
     */
    public static final float DEFAULT_FRONT_CLIP_DISTANCE_PERSPECTIVE = 1.0f;
    
    /**
     * The default front clip distance (near clip plane) for parallel projection mode.
     */
    public static final float DEFAULT_FRONT_CLIP_DISTANCE_PARALLEL = -1.0f;
    
    /**
     * The default back clip distance (far clip plane) for perspective projection mode.
     */
    public static final float DEFAULT_BACK_CLIP_DISTANCE_PERSPECTIVE = 2000.0f;
    
    /**
     * The default back clip distance (far clip plane) for parallel projection mode.
     */
    public static final float DEFAULT_BACK_CLIP_DISTANCE_PARALLEL = 100.0f;
    
    private final ArrayList< Canvas3D > canvasList;
    private final List< Canvas3D > unmodCanvasList;
    
    private ProjectionPolicy projectionPolicy = PERSPECTIVE_PROJECTION;
    private float fieldOfView = DEFAULT_FIELD_OF_VIEW;
    private float frontClipDistance;
    private float backClipDistance;
    private boolean isFrontClipSet = false;
    private boolean isBackClipSet = false;
    private float screenScale = 1.0f;
    private Tuple2f centerOfView = null;
    private RenderPassConfig[] rpcStack = new RenderPassConfig[ 16 ];
    private int rpcStackSize = 0;
    
    private final Transform3D transform = new Transform3D();
    private final Transform3D modelViewTransform = new Transform3D();
    private final Transform3D modelViewTransform2 = new Transform3D();
    private final Point3f position = new Point3f();
    private final Transform3D projection = new Transform3D();
    private Transform3D customProjection = null;
    private final Matrix4f projViewMatrix = new Matrix4f();
    private final Matrix3f tempMatrix = new Matrix3f();
    private final Matrix4f IDENTITY = new Matrix4f();
    private final Frustum frustum = new Frustum();
    private final Matrix3f rotMat = new Matrix3f();
    
    private static final Vector3f TO_BACK = new Vector3f( 0.0f, 0.0f, -1.0f );
    private static final Vector3f TO_RIGHT = new Vector3f( 1.0f, 0.0f, 0.0f );
    private static final Vector3f TO_UP = new Vector3f( 0.0f, 1.0f, 0.0f );
    
    private float soundActivationRadius = 40f;
    
    private PointLight attachedLight = null;
    
    /**
     * Attaches a {@link PointLight}/{@link SpotLight} to this View (or detaches if for <code>null</code>).
     * 
     * @param light
     */
    public final void setAttachedLight( PointLight light )
    {
        this.attachedLight = light;
    }
    
    /**
     * @return the View-attached {@link PointLight}/{@link SpotLight}.
     */
    public final PointLight getAttachedLight()
    {
        return ( attachedLight );
    }
    
    /**
     * Sets the projection policy.
     */
    public final void setProjectionPolicy( ProjectionPolicy policy )
    {
        this.projectionPolicy = policy;
    }
    
    /**
     * @return the projection policy.
     */
    public final ProjectionPolicy getProjectionPolicy()
    {
        if ( rpcStackSize > 0 )
        {
            for ( int i = rpcStackSize - 1; i >= 0; i-- )
            {
                if ( ( rpcStack[ i ] != null ) && ( rpcStack[ i ].getProjectionPolicy() != null ) )
                    return ( rpcStack[ i ].getProjectionPolicy() );
            }
        }
        
        return ( projectionPolicy );
    }
    
    /**
     * Sets the center of the View.
     * 
     * @param cov values range from 1 (left) to -1 (right) a value of null
     *            sets the center to (0|0) - the default
     */
    public final void setCenterOfView( Tuple2f cov )
    {
        this.centerOfView = cov;
    }
    
    /**
     * @return the center of this View - values range from 1 (left) to -1 (right)
     */
    public final Tuple2f getCenterOfView()
    {
        if ( rpcStackSize > 0 )
        {
            for ( int i = rpcStackSize - 1; i >= 0; i-- )
            {
                if ( ( rpcStack[ i ] != null ) && ( rpcStack[ i ].getCenterOfView() != null ) )
                    return ( rpcStack[ i ].getCenterOfView() );
            }
        }
        
        return ( centerOfView );
    }
    
    /**
     * Sets the screen scale.
     */
    public final void setScreenScale( float screenScale )
    {
        this.screenScale = screenScale;
    }
    
    /**
     * @return the screen scale.
     */
    public final float getScreenScale()
    {
        if ( rpcStackSize > 0 )
        {
            for ( int i = rpcStackSize - 1; i >= 0; i-- )
            {
                if ( ( rpcStack[ i ] != null ) && ( rpcStack[ i ].getScreenScale() > -9998.0f ) )
                    return ( rpcStack[ i ].getScreenScale() );
            }
        }
        
        return ( screenScale );
    }
    
    /**
     * Sets the front clip distance (near clip plane) for this View.
     */
    public final void setFrontClipDistance( float value )
    {
        this.frontClipDistance = value;
        this.isFrontClipSet = true;
    }
    
    /**
     * @return the front clip distance (near clip plane) for this View.
     */
    public final float getFrontClipDistance()
    {
        if ( rpcStackSize > 0 )
        {
            for ( int i = rpcStackSize - 1; i >= 0; i-- )
            {
                if ( ( rpcStack[ i ] != null ) && ( rpcStack[ i ].getFrontClipDistance() > -9998.0f ) )
                    return ( rpcStack[ i ].getFrontClipDistance() );
            }
        }
        
        if ( isFrontClipSet )
            return ( frontClipDistance );
        else if ( getProjectionPolicy() == ProjectionPolicy.PARALLEL_PROJECTION )
            return ( DEFAULT_FRONT_CLIP_DISTANCE_PARALLEL );
        else// if ( getProjectionPolicy() == ProjectionPolicy.PERSPECTIVE_PROJECTION )
            return ( DEFAULT_FRONT_CLIP_DISTANCE_PERSPECTIVE );
        
        //throw new Error( "Mission impossible!" );
    }
    
    /**
     * Sets the back clip distance (far clip plane) for this View.
     */
    public final void setBackClipDistance( float value )
    {
        this.backClipDistance = value;
        this.isBackClipSet = true;
    }
    
    /**
     * @return the back clip distance (far clip plane) for this View.
     */
    public final float getBackClipDistance()
    {
        if ( rpcStackSize > 0 )
        {
            for ( int i = rpcStackSize - 1; i >= 0; i-- )
            {
                if ( ( rpcStack[ i ] != null ) && ( rpcStack[ i ].getBackClipDistance() > -9998.0f ) )
                    return ( rpcStack[ i ].getBackClipDistance() );
            }
        }
        
        if ( isBackClipSet )
            return ( backClipDistance );
        else if ( getProjectionPolicy() == ProjectionPolicy.PARALLEL_PROJECTION )
            return ( DEFAULT_BACK_CLIP_DISTANCE_PARALLEL );
        else// if ( getProjectionPolicy() == ProjectionPolicy.PERSPECTIVE_PROJECTION )
            return ( DEFAULT_BACK_CLIP_DISTANCE_PERSPECTIVE );
        
        //throw new Error( "Mission impossible!" );
    }
    
    /**
     * Sets the field of view (viewing angle).
     */
    public final void setFieldOfView( float value )
    {
        this.fieldOfView = value;
    }
    
    /**
     * @return the field of view (viewing angle).
     */
    public final float getFieldOfView()
    {
        if ( rpcStackSize > 0 )
        {
            for ( int i = rpcStackSize - 1; i >= 0; i-- )
            {
                if ( ( rpcStack[ i ] != null ) && ( rpcStack[ i ].getFieldOfView() > -9998.0f ) )
                    return ( rpcStack[ i ].getFieldOfView() );
            }
        }
        
        return ( fieldOfView );
    }
    
    /**
     * <b>Never use this method. It is just for internal use!</b>
     * 
     * @param b
     * @param rpc
     */
    final void set( boolean b, RenderPassConfig rpc )
    {
        if ( b )
            this.rpcStack[ rpcStackSize++ ] = rpc;
        else
            this.rpcStack[ --rpcStackSize ] = null;
    }
    
    /**
     * Set the sound activation radius.<br>
     * (Sound Nodes, which are farer from the point of view than this radius
     * aren't heard.)
     * 
     * @param radius the new sound activation radius
     */
    public final void setSoundActivationRadius( float radius )
    {
        this.soundActivationRadius = radius;
    }
    
    /**
     * @return the sound activation radius.<br>
     * (Sound Nodes, which are farer from the point of view than this radius
     * aren't heard.)
     */
    public final float getSoundActivationRadius()
    {
        return ( soundActivationRadius );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setTransform( Transform3D transform )
    {
        this.transform.set( transform );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Transform3D getTransform()
    {
        if ( rpcStackSize > 0 )
        {
            for ( int i = rpcStackSize - 1; i >= 0; i-- )
            {
                if ( ( rpcStack[ i ] != null ) && ( rpcStack[ i ].getViewTransform() != null ) )
                    return ( rpcStack[ i ].getViewTransform() );
            }
        }
        
        return ( transform );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getTransform( Transform3D transform )
    {
        getTransform().get( transform );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPosition( float posX, float posY, float posZ )
    {
        getTransform().setTranslation( posX, posY, posZ );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setPosition( Tuple3f position )
    {
        setPosition( position.getX(), position.getY(), position.getZ() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getPosition( Tuple3f position )
    {
        getTransform().getTranslation( position );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Point3f getPosition()
    {
        getTransform().getTranslation( position );
        
        return ( position );
    }
    
    /**
     * Helper function that specifies the position and orientation of a view
     * matrix.
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
    public final void lookAt( float eyePositionX, float eyePositionY, float eyePositionZ, float viewFocusX, float viewFocusY, float viewFocusZ, float vecUpX, float vecUpY, float vecUpZ )
    {
        transform.lookAt( eyePositionX, eyePositionY, eyePositionZ, viewFocusX, viewFocusY, viewFocusZ, vecUpX, vecUpY, vecUpZ );
    }
    
    /**
     * Helper function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param eyePosition the center of the eye
     * @param viewFocus the point the view looks at
     * @param vecUp the vector pointing up
     */
    public final void lookAt( Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f vecUp )
    {
        transform.lookAt( eyePosition, viewFocus, vecUp );
    }
    
    /**
     * Helper function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param viewFocus the point the view looks at
     */
    public final void lookAt( Tuple3f viewFocus )
    {
        this.getPosition( position );
        Vector3f up = Vector3f.fromPool();
        this.getUpDirection( up );
        
        this.lookAt( position, viewFocus, up );
        
        Vector3f.toPool( up );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewDirectionX the direction the view looks along
     * @param viewDirectionY the direction the view looks along
     * @param viewDirectionZ the direction the view looks along
     * @param vecUpX the vector pointing up
     * @param vecUpY the vector pointing up
     * @param vecUpZ the vector pointing up
     */
    public final void lookAlong( float eyePositionX, float eyePositionY, float eyePositionZ, float viewDirectionX, float viewDirectionY, float viewDirectionZ, float vecUpX, float vecUpY, float vecUpZ )
    {
        transform.lookAlong( eyePositionX, eyePositionY, eyePositionZ, viewDirectionX, viewDirectionY, viewDirectionZ, vecUpX, vecUpY, vecUpZ );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param eyePosition the center of the eye
     * @param viewDirection the direction the view looks along
     * @param vecUp the vector pointing up
     */
    public final void lookAlong( Tuple3f eyePosition, Tuple3f viewDirection, Tuple3f vecUp )
    {
        transform.lookAlong( eyePosition, viewDirection, vecUp );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.<br>
     * <br>
     * This method assumes Y-up.
     * 
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewDirectionX the direction the view looks along
     * @param viewDirectionY the direction the view looks along
     * @param viewDirectionZ the direction the view looks along
     */
    public final void lookAlong( float eyePositionX, float eyePositionY, float eyePositionZ, float viewDirectionX, float viewDirectionY, float viewDirectionZ )
    {
        transform.lookAlong( eyePositionX, eyePositionY, eyePositionZ, viewDirectionX, viewDirectionY, viewDirectionZ );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.<br>
     * <br>
     * This method assumes Y-up.
     * 
     * @param eyePosition the center of the eye
     * @param viewDirection the direction the view looks along
     */
    public final void lookAlong( Tuple3f eyePosition, Tuple3f viewDirection )
    {
        transform.lookAlong( eyePosition, viewDirection );
    }
    
    /**
     * Sets the direction in which the view looks.
     * 
     * @param direction
     */
    public final void setFacingDirection( Vector3f direction )
    {
        this.getPosition( position );
        Vector3f up = Vector3f.fromPool();
        this.getUpDirection( up );
        
        this.lookAlong( position, direction, up );
        
        Vector3f.toPool( up );
    }
    
    /**
     * Calculates the direction, the view faces and fills the values into the
     * given Tuple3f.
     * 
     * @param direction the Tuple3f to be filled up with the result
     */
    public final <T extends Tuple3f> T getFacingDirection( T direction )
    {
        transform.getRotation( rotMat );
        rotMat.mul( TO_BACK, direction );
        
        return ( direction );
    }
    
    /**
     * Calculates and returns the direction, the view faces.
     */
    public final Vector3f getFacingDirection()
    {
        final Vector3f direction = new Vector3f();
        
        getFacingDirection( direction );
        
        return ( direction );
    }
    
    /**
     * Calculates the direction to the right and fills the values into the given
     * Tuple3f.
     * 
     * @param direction the Tuple3f to be filled up with the result
     */
    public final <T extends Tuple3f> T getRightDirection( T direction )
    {
        transform.getRotation( rotMat );
        rotMat.mul( TO_RIGHT, direction );
        
        return ( direction );
    }
    
    /**
     * Calculates and returns the direction to the right.
     */
    public final Vector3f getRightDirection()
    {
        final Vector3f direction = new Vector3f();
        
        getRightDirection( direction );
        
        return ( direction );
    }
    
    /**
     * Calculates the direction heading up and fills the values into the given
     * Tuple3f.
     * 
     * @param direction the Tuple3f to be filled up with the result
     */
    public final <T extends Tuple3f> T getUpDirection( T direction )
    {
        transform.getRotation( rotMat );
        rotMat.mul( TO_UP, direction );
        
        return ( direction );
    }
    
    /**
     * Calculates and returns the direction heading up.
     */
    public final Vector3f getUpDirection()
    {
        final Vector3f direction = new Vector3f();
        
        getUpDirection( direction );
        
        return ( direction );
    }
    
    /**
     * Gets model-view transform adjusting it depending on the camera mode.
     * 
     * @param mode VIEW_NORMAL will return the standard view,
     *            VIEW_FIXED_POSITION will return only the rotational component
     *            of the standard view (the position is left as the identity),
     *            VIEW_FIXED returns the identity matrix.
     * @param forceRecomputation if true, the model-view-transform is guaranteed to be recalculated
     */
    public final Transform3D getModelViewTransform( CameraMode mode, boolean forceRecomputation )
    {
        if ( !forceRecomputation )
            return ( modelViewTransform );
        
        switch ( mode )
        {
            case VIEW_NORMAL:
                modelViewTransform.set( getTransform() );
                modelViewTransform.invert();
                
                return ( modelViewTransform );
                
            case VIEW_FIXED_POSITION:
                getTransform().getRotation( tempMatrix );
                
                // Applies the rotational component to the model-view-transform.
                // Note: translation is not applied
                modelViewTransform.set( tempMatrix );
                
                modelViewTransform.invert();
                
                return ( modelViewTransform );
                
            case VIEW_FIXED:
                modelViewTransform.setIdentity();
                
                return ( modelViewTransform );
                
            default:
                throw new AssertionError( "Unknown CameraMode encountered" );
        }
    }
    
    /**
     * Gets model-view transform for the NORMAL camera mode.
     * 
     * @param forceRecomputation if true, the model-view-transform is guaranteed to be recalculated
     */
    public final Transform3D getModelViewTransform( boolean forceRecomputation )
    {
        return ( getModelViewTransform( CameraMode.VIEW_NORMAL, forceRecomputation ) );
    }
    
    public Transform3D calculatePerspective( float viewportWidth, float viewportHeight )
    {
        if ( getProjectionPolicy() == ProjectionPolicy.CUSTOM_PROJECTION )
            return ( customProjection );
        
        final Tuple2f cov = getCenterOfView();
        final float zFar = getBackClipDistance();
        final float zNear = getFrontClipDistance();
        
        if ( getProjectionPolicy() == PARALLEL_PROJECTION )
        {
            final float screenScale = getScreenScale();
            final float aspect = viewportWidth / viewportHeight;
            
            projection.ortho( -screenScale, screenScale, -screenScale / aspect, screenScale / aspect, zNear, zFar );
            
            if ( cov != null )
            {
                projection.getMatrix4f().add( 0, 3, cov.getX() / ( viewportWidth / 2.0f ) );
                projection.getMatrix4f().add( 1, 3, cov.getY() / ( viewportHeight / 2.0f ) );
            }
        }
        else
        {
            final float fovy = getFieldOfView();
            final float aspect = viewportWidth / viewportHeight;
            
            if ( cov == null )
            {
                //projection.perspective( fovy, aspect, zNear, zFar );
                projection.perspectiveMesa( fovy, aspect, zNear, zFar );
            }
            else
            {
                // same as perspectiveMesa
                float ymax = zNear * FastMath.tan( fovy );
                float ymin = -ymax;
                float xmin = ymin * aspect;
                float xmax = ymax * aspect;
                
                // now transform with centerOfView
                ymax = ymax + ymax * cov.getY();
                ymin = ymin - ymin * cov.getY();
                xmax = xmax + xmax * cov.getX();
                xmin = xmin - xmin * cov.getX();
                
                //projection.frustum( xmin, xmax, ymin, ymax, zNear, zFar );
                projection.frustumMesa( xmin, xmax, ymin, ymax, zNear, zFar );
            }
            
            projection.transpose();
        }
        
        return ( projection );
    }
    
    public final Transform3D calculatePerspective( Sized2iRO viewport )
    {
        return ( calculatePerspective( viewport.getWidth(), viewport.getHeight() ) );
    }
    
    /**
     * Sets the custom projection transform.
     * This also executes setProjectionPolicy( ( customProjection == null ) ? ProjectionPolicy.PERSPECTIVE_PROJECTION : ProjectionPolicy.PERSPECTIVE_PROJECTION ).
     * 
     * @param customProjection
     */
    public void setProjection( Transform3D customProjection )
    {
        this.customProjection = customProjection;
        
        setProjectionPolicy( ( this.customProjection == null ) ? ProjectionPolicy.PERSPECTIVE_PROJECTION : ProjectionPolicy.PERSPECTIVE_PROJECTION );
    }
    
    /**
     * @return the projection Transform3D. calculatePerspective must be called
     *         before the result of this method is valid, if projection policy is not custom.
     */
    public final Transform3D getProjection()
    {
        if ( getProjectionPolicy() == ProjectionPolicy.CUSTOM_PROJECTION )
            return ( customProjection );
        
        return ( projection );
    }
    
    /**
     * Calculates and returns the View's frustum.
     * 
     * @param viewportWidth the canvas width the take for calculation
     * @param viewportHeight the canvas height the take for calculation
     * 
     * @return the View's frustum
     */
    public final Frustum getFrustum( float viewportWidth, float viewportHeight )
    {
        modelViewTransform2.set( getTransform() );
        modelViewTransform2.invert();
        final Transform3D projection = calculatePerspective( viewportWidth, viewportHeight );
        projViewMatrix.mulTransposeBoth( modelViewTransform2.getMatrix4f(), projection.getMatrix4f() );
        
        frustum.compute( projViewMatrix, IDENTITY );
        
        return ( frustum );
    }
    
    /**
     * Calculates and returns the View's frustum.
     * 
     * @param viewport the viewport size the take for calculation
     * 
     * @return the View's frustum
     */
    public final Frustum getFrustum( Sized2iRO viewport )
    {
        return ( getFrustum( viewport.getWidth(), viewport.getHeight() ) );
    }
    
    /**
     * Adds a Canvas3D to this view.
     * 
     * @param canvas3D the canvas to be added
     */
    public void addCanvas3D( Canvas3D canvas3D )
    {
        canvas3D.setView( this );
        canvasList.add( canvas3D );
    }
    
    /**
     * Adds a Canvas3D at the specified index.
     * 
     * @param canvas the Canvas3D to add
     * @param index the new index of the Canvas3D
     */
    public final void addCanvas3D( Canvas3D canvas, int index )
    {
        canvas.setView( this );
        canvasList.add( canvas );
    }
    
    public final Canvas3D removeCanvas3D( int index )
    {
        final Canvas3D removedCanvas = canvasList.remove( index );
        removedCanvas.setView( null );
        
        return ( removedCanvas );
    }
    
    public void removeCanvas3D( Canvas3D canvas )
    {
        canvasList.remove( canvas );
    }
    
    public final Canvas3D getCanvas3D( int index )
    {
        return ( canvasList.get( index ) );
    }
    
    public final int indexOfCanvas3D( Canvas3D canvas )
    {
        return ( canvasList.indexOf( canvas ) );
    }
    
    public final int numCanvas3Ds()
    {
        return ( canvasList.size() );
    }
    
    /**
     * @return a read-only List of all Canvas3Ds registered to this View.
     */
    public final List< Canvas3D > getCanvas3Ds()
    {
        return ( unmodCanvasList );
    }
    
    /**
     * Creates a new View.
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
    public View( float eyePositionX, float eyePositionY, float eyePositionZ, float viewFocusX, float viewFocusY, float viewFocusZ, float vecUpX, float vecUpY, float vecUpZ )
    {
        this.IDENTITY.setIdentity();
        this.lookAt( eyePositionX, eyePositionY, eyePositionZ, viewFocusX, viewFocusY, viewFocusZ, vecUpX, vecUpY, vecUpZ );
        
        this.canvasList = new ArrayList< Canvas3D >();
        this.unmodCanvasList = Collections.unmodifiableList( canvasList );
    }
    
    /**
     * Creates a new View.
     * 
     * @param eyePosition the environment's view's location
     * @param viewFocus the environment's view's center (where to look at)
     * @param vecUp the environment's view's normal which is pointing up
     */
    public View( Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f vecUp )
    {
        this( eyePosition.getX(), eyePosition.getY(), eyePosition.getZ(),
              viewFocus.getX(), viewFocus.getY(), viewFocus.getZ(),
              vecUp.getX(), vecUp.getY(), vecUp.getZ()
            );
    }
    
    /**
     * Constructs a new View.
     */
    public View()
    {
        this( 0.0f, 0.0f, 5.0f,
              0.0f, 0.0f, 0.0f,
              0.0f, 1.0f, 0.0f
            );
    }
}
