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
package org.xith3d.loop;

import java.util.Vector;

import org.xith3d.loop.UpdatingThread.TimingMode;

/**
 * This {@link Updater} implementation manages cares about the frameTime
 * posted to the Updatables not being greater than a specific value.
 * If a frame took more than this value, the {@link Updatable}s are invoked
 * in several small steps of (up to) maxFrameTime each.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class LimitedUpdater implements Updater, Updatable
{
    private final Vector< Updatable > updatableList = new Vector< Updatable >();
    
    private long maxFrameTime = Long.MAX_VALUE;
    private boolean gameTimeStepped = true;
    
    /**
     * {@inheritDoc}
     */
    public void addUpdatable( Updatable updatable )
    {
        updatableList.add( updatable );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeUpdatable( Updatable updatable )
    {
        updatableList.remove( updatable );
    }
    
    /**
     * Sets the time-interval, one frame is allowed to take at maximum.
     * If a frame took more than this value, the {@link Updatable}s are invoked
     * in several small steps of (up to) maxFrameTime each.
     * 
     * @param maxFrameTime
     */
    public void setMaxFrameTime( long maxFrameTime )
    {
        this.maxFrameTime = maxFrameTime;
    }
    
    /**
     * @return the time-interval, one frame is allowed to take at maximum.
     * If a frame took more than this value, the {@link Updatable}s are invoked
     * in several small steps of (up to) maxFrameTime each.
     */
    public final long getMaxFrameTime()
    {
        return ( maxFrameTime );
    }
    
    /**
     * Sets, if the game-time is stepped with the maximum frame-time.
     * 
     * @param stepped
     */
    public void setGameTimeStepped( boolean stepped )
    {
        this.gameTimeStepped = stepped;
    }
    
    /**
     * @return if the game-time is stepped with the maximum frame-time.
     */
    public final boolean isGameTimeStepped()
    {
        return ( gameTimeStepped );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( !updatableList.isEmpty() )
        {
            final long maxStep = getMaxFrameTime();
            long rest = frameTime;
            final boolean gameTimeStepped = isGameTimeStepped();
            
            do
            {
                if ( rest > maxStep )
                {
                    if ( gameTimeStepped )
                    {
                        for ( int i = 0; i < updatableList.size(); i++ )
                            updatableList.get( i ).update( gameTime - rest, maxStep, timingMode );
                    }
                    else
                    {
                        for ( int i = 0; i < updatableList.size(); i++ )
                            updatableList.get( i ).update( gameTime, maxStep, timingMode );
                    }
                }
                else
                {
                    for ( int i = 0; i < updatableList.size(); i++ )
                        updatableList.get( i ).update( gameTime, rest, timingMode );
                }
                
                rest -= maxStep;
            }
            while ( rest > 0L );
        }
    }
    
    public LimitedUpdater( long maxFrameTime, boolean gameTimeStepped )
    {
        this.maxFrameTime = maxFrameTime;
        this.gameTimeStepped = gameTimeStepped;
    }
    
    public LimitedUpdater( long maxFrameTime )
    {
        this( maxFrameTime, true );
    }
}
