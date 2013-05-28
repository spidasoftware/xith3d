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

import org.xith3d.loop.UpdatingThread.TimingMode;

/**
 * Handles changes in distance over time.
 * 
 * Time values must always be in microseconds!
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class DistanceInterpolater
{
    private static final int STATE_INCREASING = 1;
    private static final int STATE_DECREASING = 2;
    private static final int STATE_STOPPED = 3;
    
    private int state = STATE_STOPPED;
    
    private float start; // current value
    private float speed; // meters per second
    private float min; // minimum value
    private float max; // maximum value
    private boolean turnsBack;
    private float value; // current value
    private long lastTime; // last time we took a reading
    
    /**
     * Constructs the DistanceInterpolator.
     * 
     * @param value Current angle
     * @param speed meters per second that we change
     * @param min   Minimum acceptable angle in radians
     * @param max   Maximum acceptable angle in radians
     * @param turnsBack if true, the interpolation will invert when max or min is hit
     */
    public DistanceInterpolater( float value, float speed, float min, float max, boolean turnsBack )
    {
        this.start = value;
        this.value = value;
        this.speed = speed;
        this.min = min;
        this.max = max;
        this.turnsBack = turnsBack;
        
        this.lastTime = -1;
    }
    
    /**
     * Constructs the DistanceInterpolator.
     * 
     * @param speed meters per second that we change
     * @param min   Minimum acceptable angle in radians
     * @param max   Maximum acceptable angle in radians
     * @param turnsBack if true, the interpolation will invert when max or min is hit
     */
    public DistanceInterpolater( float speed, float min, float max, boolean turnsBack )
    {
        this( 0.0f, speed, min, max, turnsBack );
    }
    
    /**
     * Constructs the DistanceInterpolator.
     * 
     * @param speed meters per second that we change
     * @param min   Minimum acceptable angle in radians
     * @param max   Maximum acceptable angle in radians
     */
    public DistanceInterpolater( float value, float speed, float min, float max )
    {
        this( value, speed, min, max, false );
    }
    
    /**
     * Constructs the DistanceInterpolator.
     * 
     * @param speed meters per second that we change
     * @param min   Minimum acceptable angle in radians
     * @param max   Maximum acceptable angle in radians
     */
    public DistanceInterpolater( float speed, float min, float max )
    {
        this( 0.0f, speed, min, max, false );
    }
    
    public DistanceInterpolater()
    {
        this( 0.0f, 1.0f, 0.0f, 100.0f, false );
    }
    
    public void stop()
    {
        state = STATE_STOPPED;
    }
    
    public void startIncreasing( long startTime )
    {
        if ( state == STATE_STOPPED )
        {
            lastTime = startTime;
        }
        
        state = STATE_INCREASING;
    }
    
    public void resetIncreasing( long startTime )
    {
        lastTime = startTime;
        value = start;
        state = STATE_INCREASING;
    }
    
    public void resetDecreasing( long startTime )
    {
        lastTime = startTime;
        value = start;
        state = STATE_DECREASING;
    }
    
    public void startDecreasing( long startTime )
    {
        if ( state == STATE_STOPPED )
        {
            lastTime = startTime;
        }
        
        state = STATE_DECREASING;
    }
    
    public void setValue( float value )
    {
        this.value = value;
    }
    
    public final float getValue( long gameMicros )
    {
        if ( lastTime == -1L )
        {
            lastTime = gameMicros;
        }
        
        float elapsedSeconds = ( (float)( gameMicros - lastTime ) ) / 1000000f;
        
        if ( turnsBack )
        {
            if ( state == STATE_INCREASING )
            {
                value = value + ( elapsedSeconds * speed );
            }
            else if ( state == STATE_DECREASING )
            {
                value = value - ( elapsedSeconds * speed );
            }
            
            while ( ( value < min ) || ( value > max ) )
            {
                if ( state == STATE_INCREASING )
                {
                    if ( value > max )
                    {
                        final float carry = value - max;
                        value = max - carry;
                    }
                    
                    //speed *= -1;
                    state = STATE_DECREASING;
                }
                else if ( state == STATE_DECREASING )
                {
                    if ( value < min )
                    {
                        final float carry = value - min;
                        value = min - carry;
                    }
                    
                    //speed *= -1;
                    state = STATE_INCREASING;
                }
            }
        }
        else
        {
            if ( state == STATE_INCREASING )
            {
                value = value + ( elapsedSeconds * speed );
                
                if ( value > max )
                {
                    value = max;
                    state = STATE_STOPPED;
                }
            }
            else if ( state == STATE_DECREASING )
            {
                value = value - ( elapsedSeconds * speed );
                
                if ( value < min )
                {
                    value = min;
                    state = STATE_STOPPED;
                }
            }
        }
        
        lastTime = gameMicros;
        
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
    
    public void setTurnsBack( boolean turnsBack )
    {
        this.turnsBack = turnsBack;
    }
    
    public boolean getTurnsBack()
    {
        return ( turnsBack );
    }
    
    public boolean isStarted()
    {
        return ( ( state == STATE_INCREASING ) || ( state == STATE_DECREASING ) );
    }
    
    public boolean isIncreasing()
    {
        return ( state == STATE_INCREASING );
    }
    
    public boolean isDecreasing()
    {
        return state == STATE_DECREASING;
    }
    
    public long getTicks( long curTime )
    {
        return ( curTime - lastTime );
    }
}
