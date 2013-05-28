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
package org.xith3d.input.modules.fpih;

import java.util.ArrayList;

import org.jagatoo.input.managers.InputStatesManager;
import org.xith3d.input.FirstPersonInputHandler;

/**
 * This is a special key-states manager for the {@link FirstPersonInputHandler}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHInputStatesManager extends InputStatesManager
{
    private final FirstPersonInputHandler fpih;
    
    private boolean isMovingForward = false;
    private boolean isMovingBackward = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isTurningLeft = false;
    private boolean isTurningRight = false;
    private boolean isAimingUp = false;
    private boolean isAimingDown = false;
    private boolean isJumping = false;
    private boolean isCrouching = false;
    private boolean isZoomingIn = false;
    private boolean isZoomingOut = false;
    
    private final ArrayList<MovementListener> movementListeners;
    
    private final void notifyMovementStarted( FPIHInputAction command )
    {
        if ( movementListeners.size() > 0 )
        {
            switch ( command )
            {
                case WALK_FORWARD:
                case WALK_BACKWARD:
                case STRAFE_LEFT:
                case STRAFE_RIGHT:
                case JUMP:
                case CROUCH:
                    for ( int i = 0; i < movementListeners.size(); i++ )
                    {
                        movementListeners.get( i ).onPlayerMovementStarted( command );
                    }
                    
                    break;
            }
        }
    }
    
    private final void notifyMovementStopped( FPIHInputAction command )
    {
        if ( movementListeners.size() > 0 )
        {
            switch ( command )
            {
                case WALK_FORWARD:
                case WALK_BACKWARD:
                case STRAFE_LEFT:
                case STRAFE_RIGHT:
                case JUMP:
                case CROUCH:
                    for ( int i = 0; i < movementListeners.size(); i++ )
                    {
                        movementListeners.get( i ).onPlayerMovementStopped( command );
                    }
                    
                    break;
            }
        }
    }
    
    private final void notifyJumped()
    {
        for ( int i = 0; i < movementListeners.size(); i++ )
        {
            movementListeners.get( i ).onPlayerJumped();
        }
    }
    
    private final void notifyCrouchStarted()
    {
        for ( int i = 0; i < movementListeners.size(); i++ )
        {
            movementListeners.get( i ).onPlayerCrouched();
        }
    }
    
    private final void notifyCrouchStopped()
    {
        for ( int i = 0; i < movementListeners.size(); i++ )
        {
            movementListeners.get( i ).onPlayerStoodUp();
        }
    }
    
    
    /**
     * Called when the player starts to move into any direction.
     * This method will never contain any code and can easily been
     * overridden.
     * 
     * @param command the KeyCommand, that invoked this event
     */
    protected void startMovement( FPIHInputAction command )
    {
        notifyMovementStarted( command );
    }
    
    /**
     * Called when the player stopps to move into any direction.
     * This method will never contain any code and can easily been
     * overridden.
     * 
     * @param command the KeyCommand, that invoked this event
     */
    protected void stopMovement( FPIHInputAction command )
    {
        notifyMovementStopped( command );
    }
    
    /**
     * Called, when the player jumped.
     * This method will never contain any code and can easily been
     * overridden.
     */
    protected void startJump()
    {
        fpih.startJump();
        
        notifyJumped();
    }
    
    /**
     * Makes the player crouch.
     */
    protected void startCrouch()
    {
        fpih.startCrouch();
        
        notifyCrouchStarted();
    }
    
    /**
     * Makes the player stand up from crouch.
     */
    protected void stopCrouch()
    {
        fpih.startCrouch();
        
        notifyCrouchStopped();
    }
    
    
    public final boolean isMovingForward()
    {
        return ( isMovingForward && getSimpleInputState( FPIHInputAction.WALK_FORWARD ) > 0 );
    }
    
    public final boolean isMovingBackward()
    {
        return ( isMovingBackward && getSimpleInputState( FPIHInputAction.WALK_BACKWARD ) > 0 );
    }
    
    public final boolean isMovingLeft()
    {
        return ( isMovingLeft && getSimpleInputState( FPIHInputAction.STRAFE_LEFT ) > 0 );
    }
    
    public final boolean isMovingRight()
    {
        return ( isMovingRight && getSimpleInputState( FPIHInputAction.STRAFE_RIGHT ) > 0 );
    }
    
    /**
     * @return true, if the player is currently moving into any direction
     */
    public final boolean isMoving()
    {
        return ( isMovingForward() || isMovingBackward() || isMovingLeft() || isMovingRight() );
    }
    
    public final boolean isTurningLeft()
    {
        return ( isTurningLeft && getSimpleInputState( FPIHInputAction.TURN_LEFT ) > 0 );
    }
    
    public final boolean isTurningRight()
    {
        return ( isTurningRight && getSimpleInputState( FPIHInputAction.TURN_RIGHT ) > 0 );
    }
    
    /**
     * @return true, if the player is currently moving into any direction
     */
    public final boolean isTurning()
    {
        return ( isTurningLeft() || isTurningRight() || isAimingUp() || isAimingDown() );
    }
    
    public final boolean isAimingUp()
    {
        return ( isAimingUp && getSimpleInputState( FPIHInputAction.AIM_UP ) > 0 );
    }
    
    public final boolean isAimingDown()
    {
        return ( isAimingDown && getSimpleInputState( FPIHInputAction.AIM_DOWN ) > 0 );
    }
    
    public final boolean isJumping()
    {
        return ( isJumping && getSimpleInputState( FPIHInputAction.JUMP ) > 0 );
    }
    
    public final boolean isCrouching()
    {
        return ( isCrouching && getSimpleInputState( FPIHInputAction.CROUCH ) > 0 );
    }
    
    public final boolean isZoomingIn()
    {
        return ( isZoomingIn && getSimpleInputState( FPIHInputAction.ZOOM_IN ) > 0 );
    }
    
    public final boolean isZoomingOut()
    {
        return ( isZoomingOut && getSimpleInputState( FPIHInputAction.ZOOM_OUT ) > 0 );
    }
    
    /**
     * @return true, if view is currently zooming
     */
    public final boolean isZooming()
    {
        return ( isZoomingIn() || isZoomingOut() );
    }
    
    @Override
    public void update( long nanoTime )
    {
        super.update( nanoTime );
        
        switch ( getInputState( FPIHInputAction.WALK_FORWARD ) )
        {
            case MADE_POSITIVE:
                isMovingForward = true;
                startMovement( FPIHInputAction.WALK_FORWARD );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isMovingForward = false;
                stopMovement( FPIHInputAction.WALK_FORWARD );
                break;
            case NEGATIVE:
                if ( isMovingForward )
                    stopMovement( FPIHInputAction.WALK_FORWARD );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.WALK_BACKWARD ) )
        {
            case MADE_POSITIVE:
                isMovingBackward = true;
                startMovement( FPIHInputAction.WALK_BACKWARD );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isMovingBackward = false;
                stopMovement( FPIHInputAction.WALK_BACKWARD );
                break;
            case NEGATIVE:
                if ( isMovingBackward )
                    stopMovement( FPIHInputAction.WALK_BACKWARD );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.STRAFE_LEFT ) )
        {
            case MADE_POSITIVE:
                isMovingLeft = true;
                startMovement( FPIHInputAction.STRAFE_LEFT );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isMovingLeft = false;
                stopMovement( FPIHInputAction.STRAFE_LEFT );
                break;
            case NEGATIVE:
                if ( isMovingLeft )
                    stopMovement( FPIHInputAction.STRAFE_LEFT );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.STRAFE_RIGHT ) )
        {
            case MADE_POSITIVE:
                isMovingRight = true;
                startMovement( FPIHInputAction.STRAFE_RIGHT );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isMovingRight = false;
                stopMovement( FPIHInputAction.STRAFE_RIGHT );
                break;
            case NEGATIVE:
                if ( isMovingRight )
                    stopMovement( FPIHInputAction.STRAFE_RIGHT );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.TURN_LEFT ) )
        {
            case MADE_POSITIVE:
                isTurningLeft = true;
                startMovement( FPIHInputAction.TURN_LEFT );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isTurningLeft = false;
                stopMovement( FPIHInputAction.TURN_LEFT );
                break;
            case NEGATIVE:
                if ( isTurningLeft )
                    stopMovement( FPIHInputAction.TURN_LEFT );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.TURN_RIGHT ) )
        {
            case MADE_POSITIVE:
                isTurningRight = true;
                startMovement( FPIHInputAction.TURN_RIGHT );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isTurningRight = false;
                stopMovement( FPIHInputAction.TURN_RIGHT );
                break;
            case NEGATIVE:
                if ( isTurningRight )
                    stopMovement( FPIHInputAction.TURN_RIGHT );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.AIM_UP ) )
        {
            case MADE_POSITIVE:
                isAimingUp = true;
                startMovement( FPIHInputAction.AIM_UP );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isAimingUp = false;
                stopMovement( FPIHInputAction.AIM_UP );
                break;
            case NEGATIVE:
                if ( isAimingUp )
                    stopMovement( FPIHInputAction.AIM_UP );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.AIM_DOWN ) )
        {
            case MADE_POSITIVE:
                isAimingDown = true;
                startMovement( FPIHInputAction.AIM_DOWN );
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isAimingDown = false;
                stopMovement( FPIHInputAction.AIM_DOWN );
                break;
            case NEGATIVE:
                if ( isAimingDown )
                    stopMovement( FPIHInputAction.AIM_DOWN );
                break;
        }
        
        switch ( getInputState( FPIHInputAction.JUMP ) )
        {
            case MADE_POSITIVE:
                isJumping = true;
                startJump();
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isJumping = false;
                break;
            case NEGATIVE:
                break;
        }
        
        switch ( getInputState( FPIHInputAction.CROUCH ) )
        {
            case MADE_POSITIVE:
                isCrouching = true;
                startCrouch();
                break;
            case POSITIVE:
                break;
            case MADE_NEGATIVE:
                isCrouching = false;
                stopCrouch();
                break;
            case NEGATIVE:
                if ( isCrouching )
                    stopCrouch();
                break;
        }
    }
    
    public FPIHInputStatesManager( FirstPersonInputHandler fpih, ArrayList< MovementListener > movementListeners )
    {
        super( fpih.getBindingsManager() );
        
        this.fpih = fpih;
        this.movementListeners = movementListeners;
    }
}
