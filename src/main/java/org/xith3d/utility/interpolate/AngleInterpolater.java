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
package org.xith3d.utility.interpolate;

import org.openmali.FastMath;
import org.xith3d.loop.UpdatingThread.TimingMode;

/**
 * Creates a time based interpolator for angles.  The result is an angle in
 * radians appropriate to use as a euler angle.  The speed is specfied in
 * radians/second.  The interpolator can be increasing or decreasing.
 * 
 * Time values must always be in microseconds!
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class AngleInterpolater
{
    private static final int STATE_INCREASING = 1;
    private static final int STATE_DECREASING = 2;
    private static final int STATE_STOPPED = 3;
    
    private int state = STATE_STOPPED;
    
    private float start; // starting value
    private float speed; // rdians per second
    private float min; // minimum value
    private float max; // maximum value
    private boolean wraps; // wrap between min/max
    private float value; // current value
    private long lastMicros; // last time we took a reading
    
    /**
     * Constructs the AngleInterpolator.
     * 
     * @param value Current angle
     * @param speed Radians per second that we change
     * @param min   Minimum acceptable angle in radians
     * @param max   Maximum acceptable angle in radians
     * @param wraps Determines if the angle wraps to MIN when it reaches MAX and visa versa
     */
    public AngleInterpolater( float value, float speed, float min, float max, boolean wraps )
    {
        this.start = value;
        this.value = value;
        this.speed = speed;
        this.min = min;
        this.max = max;
        this.wraps = wraps;
        
        this.lastMicros = -1;
    }
    
    /**
     * Constructs the AngleInterpolator.
     * 
     * @param speed Radians per second that we change
     * @param min   Minimum acceptable angle in radians
     * @param max   Maximum acceptable angle in radians
     * @param wraps Determines if the angle wraps to MIN when it reaches MAX and visa versa
     */
    public AngleInterpolater( float speed, float min, float max, boolean wraps )
    {
        this( 0.0f, speed, min, max, wraps );
    }
    
    /**
     * Constructs the AngleInterpolator.
     * 
     * @param speed Radians per second that we change
     * @param wraps Determines if the angle wraps to MIN when it reaches MAX and visa versa
     */
    public AngleInterpolater( float speed, boolean wraps )
    {
        this( 0.0f, speed, 0.0f, FastMath.TWO_PI, wraps );
    }
    
    /**
     * Constructs the AngleInterpolator.
     * 
     * @param speed Radians per second that we change
     */
    public AngleInterpolater( float speed )
    {
        this( speed, true );
    }
    
    public AngleInterpolater()
    {
        this( 0.0f, FastMath.TWO_PI, 0.0f, FastMath.TWO_PI, true );
    }
    
    public void stop()
    {
        state = STATE_STOPPED;
    }
    
    public boolean isStopped()
    {
        return ( state == STATE_STOPPED );
    }
    
    public void startIncreasing( long startTime )
    {
        if ( state == STATE_STOPPED )
        {
            lastMicros = startTime;
        }
        
        state = STATE_INCREASING;
    }
    
    public void resetIncreasing( long startTime )
    {
        value = start;
        lastMicros = startTime;
        state = STATE_INCREASING;
    }
    
    public void resetDecreasing( long startTime )
    {
        value = start;
        lastMicros = startTime;
        state = STATE_DECREASING;
    }
    
    public void startDecreasing( long startTime )
    {
        if ( state == STATE_STOPPED )
        {
            lastMicros = startTime;
        }
        
        state = STATE_DECREASING;
    }
    
    public void setValue( float angle )
    {
        value = angle;
    }
    
    public final float getValue( long gameMicros )
    {
        if ( lastMicros == -1L )
        {
            lastMicros = gameMicros;
        }
        
        float elapsedSeconds = ( (float)( gameMicros - lastMicros ) ) / 1000000f;
        
        if ( state == STATE_INCREASING )
        {
            value = value + ( elapsedSeconds * speed );
            
            if ( value > max )
            {
                if ( wraps )
                {
                    value = min + ( value - max );
                }
                else
                {
                    state = STATE_STOPPED;
                    value = max;
                }
            }
        }
        else if ( state == STATE_DECREASING )
        {
            value = value - ( elapsedSeconds * speed );
            
            if ( value < min )
            {
                if ( wraps )
                {
                    value = max - ( min - value );
                }
                else
                {
                    state = STATE_STOPPED;
                    value = min;
                }
            }
        }
        
        lastMicros = gameMicros;
        
        return ( value );
    }
    
    public final float getValue( long gameTime, TimingMode timingMode )
    {
        return ( getValue( timingMode.getMicroSeconds( gameTime ) ) );
    }
    
    public void setStartValue( float startVal )
    {
        this.start = startVal;
    }
    
    public float getStartValue()
    {
        return ( start );
    }
    
    public void setSpeed( float speed )
    {
        this.speed = speed;
    }
    
    public float getSpeed()
    {
        return ( speed );
    }
    
    public void setMinValue( float min )
    {
        this.min = min;
    }
    
    public float getMinValue()
    {
        return ( min );
    }
    
    public void setMaxValue( float max )
    {
        this.max = max;
    }
    
    public float getMaxValue()
    {
        return ( max );
    }
    
    public void setWraps( boolean wraps )
    {
        this.wraps = wraps;
    }
    
    public boolean getWraps()
    {
        return ( wraps );
    }
}
