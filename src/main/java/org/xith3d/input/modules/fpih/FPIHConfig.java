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

import org.openmali.FastMath;
import org.xith3d.input.FirstPersonInputHandler;

/**
 * This config can be used to fully setup a {@link FirstPersonInputHandler}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHConfig
{
    public static final float DEFAULT_MOUSE_X_SPEED = 1.0f;
    public static final float DEFAULT_MOUSE_Y_SPEED = 1.0f;
    public static final boolean DEFAULT_MOUSE_Y_INVERTED = true;
    public static final float DEFAULT_MOVEMENT_FORWARD_SPEED = 1.0f;
    public static final float DEFAULT_MOVEMENT_BACKWARD_SPEED = 1.0f;
    public static final float DEFAULT_MOVEMENT_SIDEWARD_SPEED = 1.0f;
    public static final float DEFAULT_MAX_ANGLE_UP_DOWN = FastMath.toRad( 80.0f );
    
    private float mouseXSpeed = DEFAULT_MOUSE_X_SPEED;
    private float mouseYSpeed = DEFAULT_MOUSE_Y_SPEED;
    
    private float movementSpeedForward = DEFAULT_MOVEMENT_FORWARD_SPEED;
    private float movementSpeedBackward = DEFAULT_MOVEMENT_BACKWARD_SPEED;
    private float movementSpeedSideward = DEFAULT_MOVEMENT_SIDEWARD_SPEED;
    
    private final ArrayList< FPIHConfigListener > listeners = new ArrayList< FPIHConfigListener >();
    
    public void addListener( FPIHConfigListener l )
    {
        listeners.add( l );
    }
    
    public void removeListener( FPIHConfigListener l )
    {
        listeners.remove( l );
    }
    
    protected void onSettingChanged( String setting, float oldValue, float value )
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onSettingChanged( setting, oldValue, value );
        }
    }
    
    /**
     * Sets the mouse movement speed for the x-axis.
     * 
     * @param speedX the new speed for the x-axis
     */
    public void setMouseXSpeed( float speedX )
    {
        final float oldValue = this.mouseXSpeed;
        
        this.mouseXSpeed = speedX;
        
        if ( speedX != oldValue )
        {
            onSettingChanged( "MouseXSpeed", oldValue, speedX );
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
        final float oldValue = this.mouseYSpeed;
        
        this.mouseYSpeed = speedY;
        
        if ( speedY != oldValue )
        {
            onSettingChanged( "MouseYSpeed", oldValue, speedY );
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
        final float oldValue = this.movementSpeedForward;
        
        this.movementSpeedForward = speed;
        
        if ( speed != oldValue )
        {
            onSettingChanged( "MovementSpeedForward", oldValue, speed );
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
        final float oldValue = this.movementSpeedBackward;
        
        this.movementSpeedBackward = speed;
        
        if ( speed != oldValue )
        {
            onSettingChanged( "MovementSpeedBackward", oldValue, speed );
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
        final float oldValue = this.movementSpeedSideward;
        
        this.movementSpeedSideward = speed;
        
        if ( speed != oldValue )
        {
            onSettingChanged( "MovementSpeedSideward", oldValue, speed );
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
     * Sets the speed the player moves by forward and backward.
     * 
     * @param speed the new moving speed
     */
    public void setMovementSpeed( float speed )
    {
        final float oldValueForward = this.movementSpeedForward;
        final float oldValueBackward = this.movementSpeedBackward;
        final float oldValueSideward = this.movementSpeedSideward;
        
        this.movementSpeedForward = speed;
        this.movementSpeedBackward = speed;
        this.movementSpeedSideward = speed;
        
        if ( ( speed != oldValueForward ) || ( speed != oldValueBackward ) || ( speed != oldValueSideward ) )
        {
            onSettingChanged( "MovementSpeed", ( oldValueForward + oldValueBackward + oldValueSideward ) / 3f, speed );
        }
    }
    
    /**
     * Creates a new FPIHConfig.
     * 
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeedForeward the new foreward movement speed
     * @param movementSpeedBackward the new backward movement speed
     * @param movementSpeedSideward the new sideward movement speed
     */
    public FPIHConfig( float mouseXSpeed, float mouseYSpeed, float movementSpeedForeward, float movementSpeedBackward, float movementSpeedSideward )
    {
        this.setMouseXSpeed( mouseXSpeed );
        this.setMouseYSpeed( mouseYSpeed );
        this.setMovementSpeedForward( movementSpeedForeward );
        this.setMovementSpeedBackward( movementSpeedBackward );
        this.setMovementSpeedSideward( movementSpeedSideward );
    }
    
    /**
     * Creates a new FPIHConfig.
     * 
     * @param mouseXSpeed the new x-axis mouse speed
     * @param mouseYSpeed the new y-axis mouse speed
     * @param movementSpeed the new movement speed
     */
    public FPIHConfig( float mouseXSpeed, float mouseYSpeed, float movementSpeed )
    {
        this( mouseXSpeed, mouseYSpeed, movementSpeed, movementSpeed, movementSpeed );
    }
    
    /**
     * Creates a new FPIHConfig.
     */
    public FPIHConfig()
    {
        this( DEFAULT_MOUSE_X_SPEED, DEFAULT_MOUSE_Y_INVERTED ? -DEFAULT_MOUSE_Y_SPEED : DEFAULT_MOUSE_Y_SPEED,
              DEFAULT_MOVEMENT_FORWARD_SPEED, DEFAULT_MOVEMENT_BACKWARD_SPEED, DEFAULT_MOVEMENT_SIDEWARD_SPEED
            );
    }
}
