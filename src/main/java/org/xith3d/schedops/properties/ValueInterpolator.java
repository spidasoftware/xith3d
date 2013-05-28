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
package org.xith3d.schedops.properties;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Interval;

/**
 * This Interval interpolates a value over a period of time.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ValueInterpolator extends Interval
{
    private       long startTime;
    private final long totalTime;
    private final float startVal;
    private final float endVal;
    private final float deltaVal;
    
    public final long getStartTime()
    {
        return ( startTime );
    }
    
    public final long getTotalTime()
    {
        return ( totalTime );
    }
    
    public final float getStartValue()
    {
        return ( startVal );
    }
    
    public final float getEndValue()
    {
        return ( endVal );
    }
    
    public final float getDeltaValue()
    {
        return ( deltaVal );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onIntervalStarted( long gameTime, long frameTime, TimingMode timingMode )
    {
        this.startTime = gameTime;
    }
    
    /**
     * This method is invoked each time slice.
     * Override it to do someting useful with the normalized value.
     * 
     * @param value
     */
    protected abstract void applyValue( float value );
    
    /**
     * This method is invoked each time slice.
     * Override it to do someting useful with the normalized value.
     * 
     * @param normValue the interpolation factor (0.0 .. 1.0)
     */
    protected void applyNormValue( float normValue )
    {
        applyValue( startVal + deltaVal * normValue );
    }
    
    /**
     * This method is invoked, when the interpolation has reached its final value.
     */
    protected void onInterpolationFinished()
    {
        this.kill();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onIntervalHit( long gameTime, long frameTime, TimingMode timingMode )
    {
        float normValue = (float)( gameTime - startTime ) / (float)totalTime;
        
        boolean isEnd = false;
        if ( normValue >= 1.0f )
        {
            isEnd = true;
            normValue = 1.0f;
        }
        
        applyNormValue( normValue );
        
        if ( isEnd )
        {
            onInterpolationFinished();
        }
    }
    
    public ValueInterpolator( long totalTime, long resolution, float startVal, float endVal )
    {
        super( resolution );
        
        this.totalTime = totalTime;
        this.startVal = startVal;
        this.endVal = endVal;
        this.deltaVal = endVal - startVal;
    }
    
    public ValueInterpolator( long totalTime, float startVal, float endVal )
    {
        this( totalTime, 30L, startVal, endVal );
    }
    
    public ValueInterpolator( long totalTime, long resolution )
    {
        this( totalTime, resolution, 0.0f, 1.0f );
    }
    
    public ValueInterpolator( long totalTime )
    {
        this( totalTime, 30L, 0.0f, 1.0f );
    }
}
