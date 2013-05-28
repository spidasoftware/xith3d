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
package org.xith3d.input;

import java.util.ArrayList;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.InputState;
import org.jagatoo.input.events.MouseMovedEvent;
import org.jagatoo.input.handlers.InputHandler;
import org.jagatoo.input.listeners.MouseAdapter;
import org.jagatoo.input.managers.InputBindingsManager;
import org.openmali.FastMath;
import org.openmali.spatial.AxisIndicator;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.input.modules.fpih.FPIHConfig;
import org.xith3d.input.modules.fpih.FPIHInputAction;
import org.xith3d.input.modules.fpih.FPIHInputBindingsManager;
import org.xith3d.input.modules.fpih.FPIHInputStatesManager;
import org.xith3d.input.modules.fpih.FPIHMovementConstraints;
import org.xith3d.input.modules.fpih.FPIHPhysics;
import org.xith3d.input.modules.fpih.MovementListener;
import org.xith3d.scenegraph.Transformable;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.avatar.AvatarTransform;

/**
 * This class handles the Keyboard and Mouse input for first person shooter like Views (EGO-perspective).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FirstPersonInputHandler extends InputHandler< FPIHInputAction >
{
    public static final float DEFAULT_MOUSE_X_SPEED = FPIHConfig.DEFAULT_MOUSE_X_SPEED;
    public static final float DEFAULT_MOUSE_Y_SPEED = FPIHConfig.DEFAULT_MOUSE_Y_SPEED;
    public static final boolean DEFAULT_MOUSE_Y_INVERTED = FPIHConfig.DEFAULT_MOUSE_Y_INVERTED;
    public static final float DEFAULT_MOVEMENT_FORWARD_SPEED = FPIHConfig.DEFAULT_MOVEMENT_FORWARD_SPEED;
    public static final float DEFAULT_MOVEMENT_BACKWARD_SPEED = FPIHConfig.DEFAULT_MOVEMENT_BACKWARD_SPEED;
    public static final float DEFAULT_MOVEMENT_SIDEWARD_SPEED = FPIHConfig.DEFAULT_MOVEMENT_SIDEWARD_SPEED;
    public static final float DEFAULT_MAX_ANGLE_UP_DOWN = FPIHConfig.DEFAULT_MAX_ANGLE_UP_DOWN;
    
    private AxisIndicator upAxis = AxisIndicator.POSITIVE_Y_AXIS;
    
    private Matrix3f rotMat = new Matrix3f();
    
    private MouseLstnr mouseListener;
    
    private int canvasWidth;
    private int canvasHeight;
    
    private float mouseXSpeed = DEFAULT_MOUSE_X_SPEED;
    private float mouseYSpeed = DEFAULT_MOUSE_Y_SPEED;
    
    private float movementSpeedForward = DEFAULT_MOVEMENT_FORWARD_SPEED;
    private float movementSpeedBackward = DEFAULT_MOVEMENT_BACKWARD_SPEED;
    private float movementSpeedSideward = DEFAULT_MOVEMENT_SIDEWARD_SPEED;
    
    private float crouchSizeRegression = 2.0f;
    
    private int   discreteZoomDelta = 0;
    
    private final Transformable view;
    
    private final Vector3f DEFAULT_TP_OFFSET = new Vector3f( 0f, 0f, 1.0f );
    private Vector3f org_tpOffset = null;
    private Vector3f tpOffset = null;
    private final Vector3f tpOffset_transformed = new Vector3f();
    
    private float minTPDistance = 0.0f;
    private float maxTPDistance = 7.0f;
    private float discreteTPStepSize = 0.5f;
    
    private final Point3f  viewPosition = new Point3f();
    private final Tuple3f  viewEuler;
    private int            mouseDX = 0;
    private int            mouseDY = 0;
    private long           lastMouseQueryTime = -1L;
    
    private FPIHPhysics physicsObject = null;
    
    private final ArrayList<AvatarTransform> avatarTransforms = new ArrayList<AvatarTransform>();
    
    
    private boolean mouseMoved = false;
    
    private ArrayList<MovementListener> movementListeners;
    
    private boolean isFirst = true;
    
    private FPIHConfig config = null;
    
    private FPIHMovementConstraints constraints = new FPIHMovementConstraints();
    
    /**
     * Sets the up-axis to use.<br>
     * Default is {@link AxisIndicator#POSITIVE_Y_AXIS}.
     * 
     * @param upAxis
     */
    public void setUpAxis( AxisIndicator upAxis )
    {
        if ( upAxis == null )
            throw new IllegalArgumentException( "upAxis must not be null!" );
        
        this.upAxis = upAxis;
    }
    
    /**
     * @return the up-axis to use.<br>
     * Default is {@link AxisIndicator#POSITIVE_Y_AXIS}.
     */
    public final AxisIndicator getUpAxis()
    {
        return ( upAxis );
    }
    
    /**
     * @return the View used by this FirstPersonInputAdapter
     */
    public final Transformable getTransformNode()
    {
        return ( view );
    }
    
    /**
     * @return the View used by this FirstPersonInputAdapter
     */
    public final View getView()
    {
        return ( (View)view );
    }
    
    /**
     * Adds a MovementListener to the List.
     * 
     * @param l the new MovmentListener to add
     */
    public void addMovementListener( MovementListener l )
    {
        movementListeners.add( l );
    }
    
    /**
     * Removes a MovementListener from the List.
     * 
     * @param l the MovmentListener to be removed
     */
    public void removeMovementListener( MovementListener l )
    {
        movementListeners.remove( l );
    }
    
    /**
     * Called when the player starts to move into any direction.
     * This method will never contain any code and can easily been
     * overridden.
     * 
     * @param command the KeyCommand, that invoked this event
     */
    public void startMovement( FPIHInputAction command )
    {
    }
    
    /**
     * Called when the player stopps to move into any direction.
     * This method will never contain any code and can easily been
     * overridden.
     * 
     * @param command the KeyCommand, that invoked this event
     */
    public void stopMovement( FPIHInputAction command )
    {
    }
    
    /**
     * Makes the player crouch.
     */
    public void startCrouch()
    {
        if ( crouchSizeRegression > 0.0f )
        {
            final Tuple3f currPos = view.getPosition();
            
            view.setPosition( new Vector3f( currPos.getX(),
                                            currPos.getY() - crouchSizeRegression,
                                            currPos.getZ()
                                          )
                            );
        }
    }
    
    /**
     * Makes the player stand up from crouch.
     */
    public void stopCrouch()
    {
        if ( crouchSizeRegression > 0.0f )
        {
            final Tuple3f currPos = view.getPosition();
            
            view.setPosition( new Vector3f( currPos.getX(),
                                            currPos.getY() + crouchSizeRegression,
                                            currPos.getZ()
                                          )
                            );
        }
    }
    
    /**
     * Called, when the player jumped.
     * This method will never contain any code and can easily been
     * overridden.
     */
    public void startJump()
    {
        if ( getPhysicsObject() != null )
        {
            getPhysicsObject().startJump( getUpAxis() );
        }
    }
    
    /**
     * Calculates internal angle-values from the current View-rotation.
     * Invoke this method, when the view has been rotated from something else
     * than this class instance.
     */
    public void updateViewInverse()
    {
        view.getTransform().getEuler( viewEuler );
        view.getTransform().getTranslation( viewPosition );
        
        if ( getPhysicsObject() != null )
        {
            getPhysicsObject().updateFromView( viewPosition );
        }
        
        if ( tpOffset != null )
        {
            view.getTransform().getMatrix4f().transform( tpOffset, tpOffset_transformed );
            viewPosition.add( tpOffset_transformed );
        }
        else
        {
            tpOffset_transformed.set( 0f, 0f, 0f );
        }
        
        //updateAvatars( view.getTransform(), viewEuler.x, viewEuler.y, tpOffset2 );
        isFirst = true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final FPIHInputBindingsManager getBindingsManager()
    {
        return ( (FPIHInputBindingsManager)super.getBindingsManager() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final FPIHInputStatesManager getStatesManager()
    {
        return ( (FPIHInputStatesManager)super.getStatesManager() );
    }
    
    /**
     * Sets the third-person-offset in view-local coordinates.
     * 
     * @param tpDirectionX
     * @param tpDirectionY
     * @param tpDirectionZ
     * @param distance
     */
    public void setThirdPersonOffset( float tpDirectionX, float tpDirectionY, float tpDirectionZ, float distance )
    {
        if ( (tpDirectionX == 0f) && (tpDirectionY == 0f) && (tpDirectionZ == 0f) )
        {
            this.org_tpOffset = null;
            this.tpOffset = null;
            updateViewInverse();
            return;
        }
        
        if ( this.tpOffset == null )
            this.tpOffset = new Vector3f( tpDirectionX, tpDirectionY, tpDirectionZ );
        else
            this.tpOffset.set( tpDirectionX, tpDirectionY, tpDirectionZ );
        
        if ( distance >= 0f )
        {
            this.tpOffset.normalize();
            this.tpOffset.scale( distance );
        }
        
        if ( this.org_tpOffset == null )
            this.org_tpOffset = new Vector3f( this.tpOffset );
        else
            this.org_tpOffset.set( this.tpOffset );
        
        updateViewInverse();
    }
    
    public void setThirdPersonDistance( float dist )
    {
        discreteZoomDelta = (int)( dist / (float)discreteTPStepSize );
    }
    
    /**
     * Sets the third-person-offset in view-local coordinates.
     * 
     * @param tpOffsetX
     * @param tpOffsetY
     * @param tpOffsetZ
     */
    public void setThirdPersonOffset( float tpOffsetX, float tpOffsetY, float tpOffsetZ )
    {
        setThirdPersonOffset( tpOffsetX, tpOffsetY, tpOffsetZ, -1f );
    }
    
    /**
     * Sets the third-person-offset in view-local coordinates.
     * 
     * @param tpDirection
     */
    public void setThirdPersonOffset( Tuple3f tpDirection, float distance )
    {
        if ( tpDirection == null )
        {
            this.org_tpOffset = null;
            this.tpOffset = null;
            updateViewInverse();
            return;
        }
        
        setThirdPersonOffset( tpDirection.getX(), tpDirection.getY(), tpDirection.getZ(), distance );
    }
    
    /**
     * Sets the third-person-offset in view-local coordinates.
     * 
     * @param tpOffset
     */
    public void setThirdPersonOffset( Tuple3f tpOffset )
    {
        setThirdPersonOffset( tpOffset, -1f );
    }
    
    /**
     * @return the third-person-offset in view-local coordinates.
     */
    public final Vector3f getThirdPersonOffset()
    {
        return ( tpOffset );
    }
    
    /**
     * Sets the minimum third-person distance.
     * 
     * @param minDist
     */
    public void setMinThirdPersonDistance( float minDist )
    {
        this.minTPDistance = minDist;
    }
    
    /**
     * @return the minimum third-person distance.
     */
    public final float getMinThirdPersonDistance()
    {
        return ( minTPDistance );
    }
    
    /**
     * Sets the maximum third-person distance.
     * 
     * @param maxDist
     */
    public void setMaxThirdPersonDistance( float maxDist )
    {
        this.maxTPDistance = maxDist;
    }
    
    /**
     * @return the maximum third-person distance.
     */
    public final float getMaxThirdPersonDistance()
    {
        return ( maxTPDistance );
    }
    
    /**
     * Sets the stepsize of discrete third-person offset manipulation.
     * 
     * @param stepSize
     */
    public void setDiscreteThirdPersonStepSize( float stepSize )
    {
        this.discreteTPStepSize = stepSize;
    }
    
    /**
     * @return the stepsize of discrete third-person offset manipulation.
     */
    public final float getDiscreteThirdPersonStepSize()
    {
        return ( discreteTPStepSize );
    }
    
    /**
     * Sets the mouse movement speed for the x-axis.
     * 
     * @param speedX the new speed for the x-axis
     */
    public void setMouseXSpeed( float speedX )
    {
        this.mouseXSpeed = speedX;
        
        if ( config != null )
        {
            config.setMouseXSpeed( speedX );
        }
    }
    
    /**
     * @return the mouse movement speed for the x-axis
     */
    public final float getMouseXSpeed()
    {
        return ( mouseXSpeed );
    }
    
    /**
     * Sets the mouse movement speed for the y-axis.
     * 
     * @param speedY the new speed for the y-axis
     */
    public void setMouseYSpeed( float speedY )
    {
        this.mouseYSpeed = speedY;
        
        if ( config != null )
        {
            config.setMouseYSpeed( speedY );
        }
    }
    
    /**
     * @return the mouse movement speed for the y-axis
     */
    public final float getMouseYSpeed()
    {
        return ( mouseYSpeed );
    }
    
    /**
     * Flips the mouse-y-axis movement.<br>
     * This is the same as<br>
     * <code>
     * setMouseSpeedY( -getMouseSpeedY() );
     * </code>
     */
    public void flipMouseYAxis()
    {
        setMouseYSpeed( -getMouseYSpeed() );
    }
    
    /**
     * Sets the speed the player moves by forward and backward.
     * 
     * @param speed the new moving speed
     */
    public void setMovementSpeed( float speed )
    {
        this.movementSpeedForward = speed;
        this.movementSpeedBackward = speed;
        this.movementSpeedSideward = speed;
        
        if ( config != null )
        {
            config.setMovementSpeed( speed );
        }
    }
    
    /**
     * @return the speed the player moves by forward and backward.
     */
    public final float getMovementSpeed()
    {
        return ( (movementSpeedForward + movementSpeedBackward + movementSpeedSideward) / 3.0f );
    }
    
    /**
     * Sets the speed the player moves by forward.
     * 
     * @param speed the new (forward) moving speed
     */
    public void setMovementSpeedForward( float speed )
    {
        this.movementSpeedForward = speed;
        
        if ( config != null )
        {
            config.setMovementSpeedForward( speed );
        }
    }
    
    /**
     * @return the speed the player moves by forward.
     */
    public final float getMovementSpeedForward()
    {
        return ( movementSpeedForward );
    }
    
    /**
     * Sets the speed the player moves by backward.
     * 
     * @param speed the new (backward) moving speed
     */
    public void setMovementSpeedBackward( float speed )
    {
        this.movementSpeedBackward = speed;
        
        if ( config != null )
        {
            config.setMovementSpeedBackward( speed );
        }
    }
    
    /**
     * @return the speed the player moves by backward.
     */
    public final float getMovementSpeedBackward()
    {
        return ( movementSpeedBackward );
    }
    
    /**
     * Sets the speed the player moves by sideward.
     * 
     * @param speed the new (sideward) moving speed
     */
    public void setMovementSpeedSideward( float speed )
    {
        this.movementSpeedSideward = speed;
        
        if ( config != null )
        {
            config.setMovementSpeedSideward( speed );
        }
    }
    
    /**
     * @return the speed the player moves by sideward.
     */
    public final float getMovementSpeedSideward()
    {
        return ( movementSpeedSideward );
    }
    
    /**
     * Applies the given {@link FPIHConfig} to this {@link FirstPersonInputHandler}.
     * The config instance is stored in this object and is notified of any change.
     * 
     * @param config
     */
    public void applyConfig( FPIHConfig config )
    {
        this.config = null;
        
        this.setMouseXSpeed( config.getMouseXSpeed() );
        this.setMouseYSpeed( config.getMouseYSpeed() );
        this.setMovementSpeedForward( config.getMovementSpeedForward() );
        this.setMovementSpeedBackward( config.getMovementSpeedBackward() );
        this.setMovementSpeedSideward( config.getMovementSpeedSideward() );
        
        this.config = config;
    }
    
    /**
     * Extracts a {@link FPIHConfig} from this {@link FirstPersonInputHandler}.
     * 
     * @param config
     */
    public void extractConfig( FPIHConfig config )
    {
        this.config = null;
        
        this.setMouseXSpeed( config.getMouseXSpeed() );
        this.setMouseYSpeed( config.getMouseYSpeed() );
        this.setMovementSpeedForward( config.getMovementSpeedForward() );
        this.setMovementSpeedBackward( config.getMovementSpeedBackward() );
        this.setMovementSpeedSideward( config.getMovementSpeedSideward() );
        
        this.config = config;
    }
    
    /**
     * Sets the value the view will we lowered with the player crouches.
     * 
     * @param sizeDelta the value the view will be lowered on crouch
     */
    public void setPlayerCrouchSizeRegression( float sizeDelta )
    {
        this.crouchSizeRegression = sizeDelta;
    }
    
    /**
     * @return value the view will we lowered with the player crouches.
     */
    public float getPlayerCrouchSizeRegression()
    {
        return ( crouchSizeRegression );
    }
    
    /**
     * Sets the constraints to be used by this {@link FirstPersonInputHandler}.
     * 
     * @param constraints
     */
    public void setMovementConstraints( FPIHMovementConstraints constraints )
    {
        if ( constraints == null )
            throw new IllegalArgumentException( "constraints must not be null" );
        
        this.constraints = constraints;
    }
    
    /**
     * @return the constraints to be used by this {@link FirstPersonInputHandler}.
     */
    public final FPIHMovementConstraints getMovementConstraints()
    {
        return ( constraints );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setSuspendMask( int suspendMask )
    {
        boolean imms = isMouseMovementSuspended();
        
        if ( super.setSuspendMask( suspendMask ) )
        {
            if ( getInputSystem() != null )
            {
                try
                {
                    if ( getInputSystem().hasMouse() )
                        getInputSystem().getMouse().setAbsolute( isMouseMovementSuspended() );
                }
                catch ( InputSystemException e )
                {
                    throw new RuntimeException( e );
                }
            }
            
            if ( imms && !isMouseMovementSuspended() )
            {
                updateViewInverse();
            }
            
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * Adds an Avatar to the FPIH,<br>
     * An Avatar always follows the main Transformable (View) possibly with an offset.
     * 
     * @param at
     */
    public void addAvatar( AvatarTransform at )
    {
        if ( at == null )
            throw new NullPointerException( "at must not be null" );
        
        this.avatarTransforms.add( at );
        
        updateViewInverse();
    }
    
    /**
     * Removes an Avatar from the FPIH,<br>
     * An Avatar always follows the main Transformable (View) possibly with an offset.
     * 
     * @param at the avatar Transformable
     */
    public void removeAvatar( AvatarTransform at )
    {
        if ( at == null )
            throw new NullPointerException( "at must not be null" );
        
        avatarTransforms.remove( at );
    }
    
    public void setPhysicsObject( FPIHPhysics physicsObject )
    {
        this.physicsObject = physicsObject;
        
        if ( physicsObject != null )
        {
            physicsObject.init( getTransformNode(), getThirdPersonOffset() );
            
            if ( !isSuspended() )
            {
                updateViewInverse();
            }
        }
    }
    
    public final FPIHPhysics getPhysicsObject()
    {
        return ( physicsObject );
    }
    
    /**
     * This transforms additional Transformables to follow the main Transformable (View).
     * 
     * @param gameMicros
     * @param view
     * @param rotX
     * @param rotY
     * @param thirdPersonOffset
     */
    protected void updateAvatars( long gameMicros, long frameMicros, Transformable view, Vector3f viewTranslation, float rotX, float rotY, Vector3f thirdPersonOffset )
    {
        if ( getPhysicsObject() != null )
        {
            getPhysicsObject().update( gameMicros, frameMicros, view, viewTranslation, rotX, rotY, thirdPersonOffset );
            
            isFirst = true;
        }
        
        for ( int i = 0; i < avatarTransforms.size(); i++ )
        {
            avatarTransforms.get( i ).transform( view.getTransform(), rotX, rotY, thirdPersonOffset );
        }
    }
    
    private class MouseLstnr extends MouseAdapter
    {
        @Override
        public void onMouseMoved( MouseMovedEvent e, int x, int y, int dx, int dy )
        {
            if ( isMouseMovementSuspended() )
                return;
            
            mouseDX += dx;
            mouseDY += dy;
            
            mouseMoved = true;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update( long nanoSeconds, float seconds, long nanoFrame, float frameSeconds ) throws InputSystemException
    {
        final long gameMicros = nanoSeconds / 1000L;
        final long frameMicros = nanoFrame / 1000L;
        
        if ( getPhysicsObject() != null )
        {
            getPhysicsObject().updateGameTime( gameMicros );
        }
        
        final FPIHInputStatesManager statesManager = getStatesManager();
        
        if ( statesManager.getInputState( FPIHInputAction.DISCRETE_ZOOM_IN ) == InputState.MADE_POSITIVE )
            discreteZoomDelta--;
        if ( statesManager.getInputState( FPIHInputAction.DISCRETE_ZOOM_OUT ) == InputState.MADE_POSITIVE )
            discreteZoomDelta++;
        
        final boolean wasZooming = (discreteZoomDelta != 0) || statesManager.isZooming();
        
        Vector3f viewTranslation = Vector3f.fromPool();
        
        if ( statesManager.isMoving() || statesManager.isTurning() || mouseMoved || wasZooming )
        {
            view.getTransform().getTranslation( viewPosition );
            viewTranslation.setZero();
        }
        
        if ( (tpOffset != null) && ( statesManager.isMoving() || statesManager.isTurning() || mouseMoved || wasZooming ) )
        {
            view.getTransform().getMatrix4f().transform( tpOffset, tpOffset_transformed );
            viewPosition.sub( tpOffset_transformed );
        }
        
        /*
         * If we're turning (not by mouse movevent), we need to apply
         * a rotation to the view, which depends on the passed time.
         */
        if ( statesManager.isTurning() )
        {
            final float turnH = mouseXSpeed * 5f * frameSeconds;
            final float turnV = mouseYSpeed * 5f * frameSeconds;
            
            if ( statesManager.isTurningLeft() )
            {
                viewEuler.addY( turnH );
            }
            
            if ( statesManager.isTurningRight() )
            {
                viewEuler.subY( turnH );
            }
            
            if ( statesManager.isAimingUp() )
            {
                viewEuler.addX( turnV );
            }
            
            if ( statesManager.isAimingDown() )
            {
                viewEuler.subX( turnV );
            }
            
            constraints.applyRotationalConstraints( viewEuler, getUpAxis() );
            
            view.getTransform().setEuler( viewEuler );
        }
        
        final boolean mouseWasMoved;
        if ( !isMouseMovementSuspended() )
        {
            if ( mouseMoved )
            {
                mouseWasMoved = true;
                
                float dx = (float)mouseDX * 1f * FastMath.TWO_PI / ( mouseXSpeed * (float)canvasWidth );
                float dy = (float)mouseDY * 1f * FastMath.TWO_PI / ( mouseYSpeed * (float)canvasHeight );
                
                if ( isMouseSmoothingEnabled() )
                {
                    if ( lastMouseQueryTime != -1L )
                    {
                        float dt = 100f * Math.min( 0.3f, ( nanoSeconds - lastMouseQueryTime ) / 1000000000f );
                        
                        dx /= dt;
                        dy /= dt;
                    }
                    
                    lastMouseQueryTime = nanoSeconds;
                }
                
                switch ( getUpAxis() )
                {
                    case POSITIVE_X_AXIS:
                        viewEuler.sub( dx, dy, 0f );
                        break;
                    case NEGATIVE_X_AXIS:
                        viewEuler.add( dx, dy, 0f );
                        break;
                    case POSITIVE_Y_AXIS:
                        viewEuler.sub( dy, dx, 0f );
                        break;
                    case NEGATIVE_Y_AXIS:
                        viewEuler.add( dy, dx, 0f );
                        break;
                    case POSITIVE_Z_AXIS:
                        viewEuler.sub( dy, 0f, dx );
                        break;
                    case NEGATIVE_Z_AXIS:
                        viewEuler.add( dy, 0f, dx );
                        break;
                }
                
                constraints.applyRotationalConstraints( viewEuler, getUpAxis() );
                
                view.getTransform().setEuler( viewEuler );
                
                mouseDX = 0;
                mouseDY = 0;
                
                mouseMoved = false;
            }
            else
            {
                mouseWasMoved = false;
            }
        }
        else
        {
            mouseWasMoved = false;
        }
        
        // if a movement key is pressed --> calculate the new translation
        if ( statesManager.isMoving() && ( nanoFrame > 0L ) )
        {
            Vector3f viewFacingDirection = Vector3f.fromPool();
            Vector3f viewRightDirection = Vector3f.fromPool();
            
            // calculate facing-direction
            //view.getFacingDirection( viewFacingDirection );
            view.getTransform().getRotation( rotMat );
            rotMat.mul( Vector3f.NEGATIVE_Z_AXIS, viewFacingDirection );
            
            //calculate right-direction
            //view.getRightDirection( viewRightDirection );
            view.getTransform().getRotation( rotMat );
            rotMat.mul( Vector3f.POSITIVE_X_AXIS, viewRightDirection );
            
            final boolean isMovingF = statesManager.isMovingForward();
            final boolean isMovingB = statesManager.isMovingBackward();
            final boolean isMovingL = statesManager.isMovingLeft();
            final boolean isMovingR = statesManager.isMovingRight();
            
            /*
             * This code will prevent the player from moving faster
             * when moving forward/backward and sideward at the same time.
             */
            final float distF;
            if ( isMovingF && ( isMovingL || isMovingR ) )
                distF = movementSpeedForward * 7.072135785007072135f * frameSeconds;
            else
                distF = movementSpeedForward * 10f * frameSeconds;
            final float distB;
            if ( isMovingB && ( isMovingL || isMovingR ) )
                distB = movementSpeedBackward * 7.072135785007072135f * frameSeconds;
            else
                distB = movementSpeedBackward * 10f * frameSeconds;
            final float distS ;
            if ( ( isMovingL || isMovingR ) && ( isMovingF || isMovingB ) )
                distS = movementSpeedSideward * 7.072135785007072135f * frameSeconds;
            else
                distS = movementSpeedSideward * 10f * frameSeconds;
            
            Vector3f tmpVec = Vector3f.fromPool();
            
            /*
             * Apply the scaled forward-movement to the delta-translation.
             * The scale is defined by the mouse-speed-settings.
             */
            if ( isMovingF )
            {
                tmpVec.set( viewFacingDirection );
                tmpVec.normalize();
                
                tmpVec.mul( distF );
                
                viewTranslation.add( tmpVec );
            }
            
            /*
             * Apply the scaled backward-movement to the delta-translation.
             * The scale is defined by the mouse-speed-settings.
             */
            if ( isMovingB )
            {
                tmpVec.set( viewFacingDirection );
                tmpVec.normalize();
                
                tmpVec.mul( -distB );
                
                viewTranslation.add( tmpVec );
            }
            
            /*
             * Apply the scaled right-movement to the delta-translation.
             * The scale is defined by the mouse-speed-settings.
             */
            if ( isMovingR )
            {
                tmpVec.set( viewRightDirection );
                tmpVec.normalize();
                
                tmpVec.mul( distS );
                
                viewTranslation.add( tmpVec );
            }
            
            /*
             * Apply the scaled left-movement to the delta-translation.
             * The scale is defined by the mouse-speed-settings.
             */
            if ( isMovingL )
            {
                tmpVec.set( viewRightDirection );
                tmpVec.normalize();
                
                tmpVec.mul( -distS );
                
                viewTranslation.add( tmpVec );
            }
            
            Vector3f.toPool( tmpVec );
            Vector3f.toPool( viewRightDirection );
            Vector3f.toPool( viewFacingDirection );
            
            constraints.applyMovementDeltaConstraints( viewTranslation, getUpAxis() );
            
            /*
             * Add the delta-movement to the absolute one.
             */
            viewPosition.add( viewTranslation );
            
            constraints.applyMovementConstraints( viewPosition, getUpAxis() );
        }
        
        if ( wasZooming )
        {
            /*
             * Calculate the third-person-offset delta
             * from discrete steps and smooth zooming...
             */
            float delta = (float)discreteZoomDelta * discreteTPStepSize;
            discreteZoomDelta = 0;
            
            if ( statesManager.isZoomingIn() )
                delta -= discreteTPStepSize * 8f * frameSeconds;
            if ( statesManager.isZoomingOut() )
                delta += discreteTPStepSize * 8f * frameSeconds;
            
            /*
             * If the new delta is non-zero or the current TP-offset is used...
             * (A null-tpOffset means, that it has a zero distance.)
             */
            if ( (delta > 0) || (tpOffset != null) )
            {
                // Set the tpOffset to the initial value...
                if ( tpOffset == null )
                {
                    if ( org_tpOffset == null )
                        org_tpOffset = new Vector3f( DEFAULT_TP_OFFSET );
                    
                    tpOffset = new Vector3f( 0f, 0f, 0f );
                }
                
                /*
                 * Constraint to minimum and maximum...
                 */
                float dist = tpOffset.length();
                if ( Float.isNaN( dist ) )
                    dist = minTPDistance;
                dist += delta;
                
                if ( dist < minTPDistance )
                    dist = minTPDistance;
                else if ( dist > maxTPDistance )
                    dist = maxTPDistance;
                
                /*
                 * Apply the new distance to the tpOffset,
                 * if it is greater than zero or set it to null,
                 * if it has zero distance.
                 */
                if ( dist > 0f )
                {
                    tpOffset.normalize( org_tpOffset );
                    tpOffset.scale( dist );
                }
                else
                {
                    tpOffset = null;
                }
            }
        }
        
        /*
         * If the tpOffset is currently used and we're moving or turning or zooming, etc.,
         * we need to transform the tpOffset by the current view-matrix
         * and apply it to the new view-position.
         */
        if ( (tpOffset != null) && (statesManager.isMoving() || statesManager.isTurning() || mouseWasMoved || wasZooming) )
        {
            view.getTransform().getMatrix4f().transform( tpOffset, tpOffset_transformed );
            viewPosition.add( tpOffset_transformed );
        }
        
        /*
         * If the current tpOffset is zero (instance is null),
         * we need to set the transformed value to zero, too,
         * since it is used below.
         */
        if ( tpOffset == null )
        {
            tpOffset_transformed.set( 0f, 0f, 0f );
        }
        
        /*
         * If we're moving, zooming or turning, we need to apply the translation.
         */
        if ( statesManager.isMoving() || statesManager.isTurning() || mouseWasMoved || wasZooming )
        {
            view.getTransform().setTranslation( viewPosition );
            view.setTransform( view.getTransform() );
        }
        
        /*
         * Update avatars, if necessary.
         */
        if ( statesManager.isMoving() || statesManager.isTurning() || mouseWasMoved || isFirst || ( getPhysicsObject() != null ) )
        {
            updateAvatars( gameMicros, frameMicros, view, viewTranslation, viewEuler.getX(), viewEuler.getY(), tpOffset_transformed );
        }
        
        Vector3f.toPool( viewTranslation );
        
        isFirst = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setInputSystem( InputSystem inputSystem )
    {
        try
        {
            if ( inputSystem != this.getInputSystem() )
            {
                if ( ( this.getInputSystem() != null ) && ( mouseListener != null ) )
                {
                    if ( !isMouseMovementSuspended() )
                        this.getInputSystem().getMouse().setAbsolute( true );
                    this.getInputSystem().getMouse().removeMouseListener( mouseListener );
                }
                
                if ( inputSystem != null )
                {
                    super.setInputSystem( inputSystem );
                    
                    if ( mouseListener == null )
                        mouseListener = new MouseLstnr();
                    inputSystem.getMouse().addMouseListener( mouseListener );
                    inputSystem.getMouse().setAbsolute( isMouseMovementSuspended() );
                }
            }
        }
        catch ( Throwable t )
        {
            Error e = new Error( "You must register a mouse before the FirstPersonInputHandler can be added to the InputHandler." );
            e.initCause( t );
            
            throw e;
        }
    }
    
    @Override
    protected FPIHInputStatesManager createInputStatesManager( InputBindingsManager< FPIHInputAction > bindingsManager )
    {
        this.movementListeners = new ArrayList< MovementListener >( 1 );
        
        return ( new FPIHInputStatesManager( this, movementListeners ) );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeedForeward the new foreward movement speed
     * @param movementSpeedBackward the new backward movement speed
     * @param movementSpeedSideward the new sideward movement speed
     */
    public FirstPersonInputHandler( Transformable view, int resolutionX, int resolutionY, float mouseXSpeed, float mouseYSpeed, float movementSpeedForeward, float movementSpeedBackward, float movementSpeedSideward )
    {
        super( new FPIHInputBindingsManager() );
        
        this.view = view;
        
        this.viewEuler = new Tuple3f( 0f, 0f, 0f );
        updateViewInverse();
        
        this.canvasWidth = resolutionX;
        this.canvasHeight = resolutionY;
        
        this.setMouseXSpeed( mouseXSpeed );
        this.setMouseYSpeed( mouseYSpeed );
        this.setMovementSpeedForward( movementSpeedForeward );
        this.setMovementSpeedBackward( movementSpeedBackward );
        this.setMovementSpeedSideward( movementSpeedSideward );
        
        this.setMouseSmoothingEnabled( true );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeed the new movement speed
     */
    public FirstPersonInputHandler( Transformable view, int resolutionX, int resolutionY, float mouseXSpeed, float mouseYSpeed, float movementSpeed )
    {
        this( view, resolutionX, resolutionY, mouseXSpeed, mouseYSpeed, movementSpeed, movementSpeed, movementSpeed );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param mouseYInverted
     * @param movementSpeed the new movement speed
     */
    public FirstPersonInputHandler( Transformable view, int resolutionX, int resolutionY, float mouseXSpeed, float mouseYSpeed, boolean mouseYInverted, float movementSpeed )
    {
        this( view, resolutionX, resolutionY, mouseXSpeed, mouseYInverted ? -mouseYSpeed : mouseYSpeed, movementSpeed, movementSpeed, movementSpeed );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas3D to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeedForeward the new foreward movement speed
     * @param movementSpeedBackward the new backward movement speed
     * @param movementSpeedSideward the new sideward movement speed
     */
    public FirstPersonInputHandler( Transformable view, Sized2iRO resolution, float mouseXSpeed, float mouseYSpeed, float movementSpeedForeward, float movementSpeedBackward, float movementSpeedSideward )
    {
        this( view, resolution.getWidth(), resolution.getHeight(), mouseXSpeed, mouseYSpeed, movementSpeedForeward, movementSpeedBackward, movementSpeedSideward );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas3D to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeed the new movement speed
     */
    public FirstPersonInputHandler( Transformable view, Sized2iRO resolution, float mouseXSpeed, float mouseYSpeed, float movementSpeed )
    {
        this( view, resolution.getWidth(), resolution.getHeight(), mouseXSpeed, mouseYSpeed, movementSpeed );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas3D to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param yInverted
     * @param movementSpeed the new movement speed
     */
    public FirstPersonInputHandler( Transformable view, Sized2iRO resolution, float mouseXSpeed, float mouseYSpeed, boolean yInverted, float movementSpeed )
    {
        this( view, resolution.getWidth(), resolution.getHeight(), mouseXSpeed, mouseYSpeed, yInverted, movementSpeed );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     */
    public FirstPersonInputHandler( Transformable view, int resolutionX, int resolutionY )
    {
        this( view, resolutionX, resolutionY, DEFAULT_MOUSE_X_SPEED, DEFAULT_MOUSE_Y_SPEED, DEFAULT_MOUSE_Y_INVERTED, DEFAULT_MOVEMENT_FORWARD_SPEED );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas to take as calculation basis for resolution-independent turn speed
     */
    public FirstPersonInputHandler( Transformable view, Sized2iRO resolution )
    {
        this( view, resolution.getWidth(), resolution.getHeight() );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param config
     */
    public FirstPersonInputHandler( Transformable view, int resolutionX, int resolutionY, FPIHConfig config )
    {
        this( view, resolutionX, resolutionY );
        
        applyConfig( config );
    }
    
    /**
     * Creates a new FirstPersonInputHandler.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas to take as calculation basis for resolution-independent turn speed
     * @param config
     */
    public FirstPersonInputHandler( Transformable view, Sized2iRO resolution, FPIHConfig config )
    {
        this( view, resolution.getWidth(), resolution.getHeight(), config );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeedForeward the new foreward movement speed
     * @param movementSpeedBackward the new backward movement speed
     * @param movementSpeedSideward the new sideward movement speed
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, int resolutionX, int resolutionY, float mouseXSpeed, float mouseYSpeed, float movementSpeedForeward, float movementSpeedBackward, float movementSpeedSideward )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolutionX, resolutionY, mouseXSpeed, mouseYSpeed, movementSpeedForeward, movementSpeedBackward, movementSpeedSideward );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeed the new movement speed
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, int resolutionX, int resolutionY, float mouseXSpeed, float mouseYSpeed, float movementSpeed )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolutionX, resolutionY, mouseXSpeed, mouseYSpeed, movementSpeed, movementSpeed, movementSpeed );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas3D to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeedForeward the new foreward movement speed
     * @param movementSpeedBackward the new backward movement speed
     * @param movementSpeedSideward the new sideward movement speed
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, Sized2iRO resolution, float mouseXSpeed, float mouseYSpeed, float movementSpeedForeward, float movementSpeedBackward, float movementSpeedSideward )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolution.getWidth(), resolution.getHeight(), mouseXSpeed, mouseYSpeed, movementSpeedForeward, movementSpeedBackward, movementSpeedSideward );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas3D to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeed the new movement speed
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, Sized2iRO resolution, float mouseXSpeed, float mouseYSpeed, float movementSpeed )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolution.getWidth(), resolution.getHeight(), mouseXSpeed, mouseYSpeed, movementSpeed );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas3D to take as calculation basis for resolution-independent turn speed
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param mouseYInverted
     * @param movementSpeed the new movement speed
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, Sized2iRO resolution, float mouseXSpeed, float mouseYSpeed, boolean mouseYInverted, float movementSpeed )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolution.getWidth(), resolution.getHeight(), mouseXSpeed, mouseYSpeed, mouseYInverted, movementSpeed );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param mouseYInverted
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, int resolutionX, int resolutionY, boolean mouseYInverted )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolutionX, resolutionY, DEFAULT_MOUSE_X_SPEED, DEFAULT_MOUSE_Y_SPEED, mouseYInverted, DEFAULT_MOVEMENT_FORWARD_SPEED );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, int resolutionX, int resolutionY )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolutionX, resolutionY, DEFAULT_MOUSE_X_SPEED, DEFAULT_MOUSE_Y_SPEED, DEFAULT_MOUSE_Y_INVERTED, DEFAULT_MOVEMENT_FORWARD_SPEED );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas to take as calculation basis for resolution-independent turn speed
     * @param mouseYInverted
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, Sized2iRO resolution, boolean mouseYInverted )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolution.getWidth(), resolution.getHeight(), DEFAULT_MOUSE_X_SPEED, DEFAULT_MOUSE_Y_SPEED, mouseYInverted, DEFAULT_MOVEMENT_FORWARD_SPEED );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas to take as calculation basis for resolution-independent turn speed
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, Sized2iRO resolution )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolution.getWidth(), resolution.getHeight() );
        fpHandler.getBindingsManager().createDefaultBindings();
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolutionX the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param resolutionY the Canvas3D-width to take as calculation basis for resolution-independent turn speed
     * @param config
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, int resolutionX, int resolutionY, FPIHConfig config )
    {
        final FirstPersonInputHandler fpHandler = new FirstPersonInputHandler( view, resolutionX, resolutionY, DEFAULT_MOUSE_X_SPEED, DEFAULT_MOUSE_Y_SPEED, DEFAULT_MOUSE_Y_INVERTED, DEFAULT_MOVEMENT_FORWARD_SPEED );
        fpHandler.getBindingsManager().createDefaultBindings();
        
        fpHandler.applyConfig( config );
        
        return ( fpHandler );
    }
    
    /**
     * Creates a new FirstPersonInputHandler and applies default key-bindings.
     * 
     * @param view the View to be used.
     * @param resolution the Canvas to take as calculation basis for resolution-independent turn speed
     * @param config
     */
    public static final FirstPersonInputHandler createDefault( Transformable view, Sized2iRO resolution, FPIHConfig config )
    {
        return ( createDefault( view, resolution.getWidth(), resolution.getHeight(), config) );
    }
}
