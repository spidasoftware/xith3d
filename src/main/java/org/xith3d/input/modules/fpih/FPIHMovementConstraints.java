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

import org.openmali.FastMath;
import org.openmali.spatial.AxisIndicator;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.input.FirstPersonInputHandler;

/**
 * {@link FPIHMovementConstraints} defines movement constraints for a
 * {@link FirstPersonInputHandler}.
 * This includes constraints for the movement as well as constraints for the
 * mouse rotation or zooming.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHMovementConstraints
{
    private float maxAngleUp = FPIHConfig.DEFAULT_MAX_ANGLE_UP_DOWN;
    private float maxAngleDown = FPIHConfig.DEFAULT_MAX_ANGLE_UP_DOWN;
    
    private boolean fixedPlaneEnabled = false;
    
    /**
     * Sets the maximum up-angle (in radians), that the view can be rotated.
     * 
     * @param angle
     */
    public void setMaxAngleUp( float angle )
    {
        this.maxAngleUp = angle;
    }
    
    /**
     * @return the maximum up-angle (in radians), that the view can be rotated.
     */
    public final float getMaxAngleUp()
    {
        return ( maxAngleUp );
    }
    
    /**
     * Sets the maximum down-angle (in radians), that the view can be rotated.
     * 
     * @param angle
     */
    public void setMaxAngleDown( float angle )
    {
        this.maxAngleDown = angle;
    }
    
    /**
     * @return the maximum down-angle (in radians), that the view can be rotated.
     */
    public final float getMaxAngleDown()
    {
        return ( maxAngleDown );
    }
    
    /**
     * Just calls {@link #setMaxAngleUp(float)} and {@link #setMaxAngleDown(float)}.
     * 
     * @param angle
     */
    public final void setMaxAngleUpDown( float angle )
    {
        setMaxAngleUp( angle );
        setMaxAngleDown( angle );
    }
    
    /**
     * If enabled, the Player will always move on the current y-plane.
     * 
     * @param enabled enabled/disabled
     */
    public void setFixedPlaneEnabled( boolean enabled )
    {
        this.fixedPlaneEnabled = enabled;
    }
    
    /**
     * If enabled, the Player will always move on the current y-plane.
     */
    public final boolean getFixedPlaneEnabled()
    {
        return ( fixedPlaneEnabled );
    }
    
    /**
     * Applies the rotational constraints to the euler-angles.
     * 
     * @param viewEuler
     * @param upAxis
     */
    public void applyRotationalConstraints( Tuple3f viewEuler, AxisIndicator upAxis )
    {
        // TODO: Fix code for x-up.
        
        float rotLeftRight = 0f;
        float rotUpDown = 0f;
        
        switch ( upAxis )
        {
            case POSITIVE_X_AXIS:
                rotLeftRight = viewEuler.getX();
                rotUpDown = viewEuler.getY();
                break;
            case NEGATIVE_X_AXIS:
                rotLeftRight = viewEuler.getX();
                rotUpDown = viewEuler.getY();
                break;
            case POSITIVE_Y_AXIS:
                rotLeftRight = viewEuler.getY();
                rotUpDown = viewEuler.getX();
                break;
            case NEGATIVE_Y_AXIS:
                rotLeftRight = viewEuler.getY();
                rotUpDown = viewEuler.getX();
                break;
            case POSITIVE_Z_AXIS:
                rotLeftRight = viewEuler.getZ();
                rotUpDown = viewEuler.getX() - FastMath.PI_HALF;
                break;
            case NEGATIVE_Z_AXIS:
                rotLeftRight = viewEuler.getZ();
                rotUpDown = viewEuler.getX() - FastMath.PI_HALF;
                break;
        }
        
        rotLeftRight %= FastMath.TWO_PI;
        
        if ( rotUpDown > maxAngleUp )
            rotUpDown = maxAngleUp;
        else if ( rotUpDown < -maxAngleDown )
            rotUpDown = -maxAngleDown;
        
        switch ( upAxis )
        {
            case POSITIVE_X_AXIS:
                viewEuler.setX( rotLeftRight );
                viewEuler.setY( rotUpDown );
                break;
            case NEGATIVE_X_AXIS:
                viewEuler.setX( rotLeftRight );
                viewEuler.setY( rotUpDown );
                break;
            case POSITIVE_Y_AXIS:
                viewEuler.setY( rotLeftRight );
                viewEuler.setX( rotUpDown );
                break;
            case NEGATIVE_Y_AXIS:
                viewEuler.setY( rotLeftRight );
                viewEuler.setX( rotUpDown );
                break;
            case POSITIVE_Z_AXIS:
                viewEuler.setZ( rotLeftRight );
                viewEuler.setX( rotUpDown + FastMath.PI_HALF );
                break;
            case NEGATIVE_Z_AXIS:
                viewEuler.setZ( rotLeftRight );
                viewEuler.setX( rotUpDown + FastMath.PI_HALF );
                break;
        }
    }
    
    public void applyMovementDeltaConstraints( Vector3f viewDeltaPosition, AxisIndicator upAxis )
    {
        if ( fixedPlaneEnabled )
        {
            final float speed = viewDeltaPosition.length();
            
            if ( ( speed == 0f ) || Float.isNaN( speed ) || Float.isInfinite( speed ) )
            {
                viewDeltaPosition.setZero();
                return;
            }
            
            switch ( upAxis )
            {
                case POSITIVE_X_AXIS:
                case NEGATIVE_X_AXIS:
                    viewDeltaPosition.setX( 0.0f );
                    break;
                case POSITIVE_Y_AXIS:
                case NEGATIVE_Y_AXIS:
                    viewDeltaPosition.setY( 0.0f );
                    break;
                case POSITIVE_Z_AXIS:
                case NEGATIVE_Z_AXIS:
                    viewDeltaPosition.setZ( 0.0f );
                    break;
            }
            
            viewDeltaPosition.normalize();
            viewDeltaPosition.scale( speed );
        }
    }
    
    /**
     * 
     * @param viewPosition
     * @param upAxis
     */
    public void applyMovementConstraints( Tuple3f viewPosition, AxisIndicator upAxis )
    {
    }
}
