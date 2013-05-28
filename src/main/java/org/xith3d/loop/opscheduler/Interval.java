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
package org.xith3d.loop.opscheduler;

import org.jagatoo.datatypes.NamableObject;
import org.xith3d.loop.UpdatingThread.TimingMode;

/**
 * You can register an instance of this class to ExtRenderLoop.
 * Each time this interval is hit the appropriate method and
 * listeners will be called.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Interval implements NamableObject
{
    private long lastGameTime;
    private long timeInterval;
    private String name;
    
    private boolean isAlive;
    
    /**
     * Sets the time interval (in microseconds) for this instance.
     * 
     * @param timeInterval the new time interval (in microseconds)
     */
    public void setInterval( long timeInterval )
    {
        this.timeInterval = timeInterval;
    }
    
    /**
     * @return the time interval (in microseconds) for this instance.
     */
    public long getInterval()
    {
        return ( timeInterval );
    }
    
    /**
     * Sets this instance's name.
     * 
     * @param name the new name
     */
    public void setName( String name )
    {
        this.name = name;
    }
    
    /**
     * @return this instance's name.
     */
    public String getName()
    {
        return ( name );
    }
    
    /**
     * @return a String representation of this instance.
     */
    @Override
    public String toString()
    {
        return ( this.getClass().getName() + ", name=" + getName() + ", timeInterval=" + getInterval() );
    }
    
    /**
     * If false, the interval will be removed from the ExtRenderLoop
     */
    public boolean isAlive()
    {
        return ( isAlive );
    }
    
    /**
     * Marks this instance to be removed from the ExtRenderLoop.
     */
    public void kill()
    {
        isAlive = false;
    }
    
    /**
     * Revives the Interval to be reused.
     */
    public void revive()
    {
        lastGameTime = -1L;
        isAlive = true;
    }
    
    /**
     * This even is fired internally, when the Interval is started.
     * 
     * @param gameTime
     * @param frameTime
     * @param timingMode
     */
    protected void onIntervalStarted( long gameTime, long frameTime, TimingMode timingMode )
    {
    }
    
    /**
     * This even is fired internally, when the Interval was hit.
     * 
     * @param gameTime
     * @param frameTime
     * @param timingMode
     */
    protected void onIntervalHit( long gameTime, long frameTime, TimingMode timingMode )
    {
    }
    
    /**
     * Checks wheather this interval is hit.
     * 
     * @param gameTime the current gameTime
     * @param frameTime the time needed to render the last frame
     * @param timingMode
     * 
     * @return true, if this interval is hit
     */
    public boolean check( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( lastGameTime == -1L )
        {
            lastGameTime = timingMode.getMicroSeconds( gameTime - frameTime );
            
            onIntervalStarted( gameTime, frameTime, timingMode );
        }
        
        if ( ( lastGameTime + getInterval() ) <= timingMode.getMicroSeconds( gameTime ) )
        {
            lastGameTime = timingMode.getMicroSeconds( gameTime );
            
            onIntervalHit( gameTime, frameTime, timingMode );
            
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * Creates a new Interval instance.
     * 
     * @param timeInterval the time interval for this instance (in microseconds)
     * @param name this instance's name
     */
    public Interval( long timeInterval, String name )
    {
        this.lastGameTime = -1L;
        this.timeInterval = timeInterval;
        this.name = name;
        this.isAlive = true;
    }
    
    /**
     * Creates a new Interval instance.
     * 
     * @param timeInterval the time interval for this instance (in microseconds)
     */
    public Interval( long timeInterval )
    {
        this.lastGameTime = -1L;
        this.timeInterval = timeInterval;
        this.name = Integer.toHexString( hashCode() );
        this.isAlive = true;
    }
}
