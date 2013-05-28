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
package org.xith3d.loop.opscheduler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.xith3d.loop.GameTimeHost;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.Updater;
import org.xith3d.loop.UpdatingThread;
import org.xith3d.loop.opscheduler.Animatable;
import org.xith3d.loop.opscheduler.Interval;
import org.xith3d.loop.opscheduler.IntervalListener;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.ScheduledOperation;

/**
 * If you're not using RenderLoop, but want to benefit from the
 * OperationScheduler functionality, just use this one.<br>
 * This class also implements the {@link Updater} interface.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OperationSchedulerImpl extends UpdatingThread implements OperationScheduler
{
    private List< ScheduledOperation > scheduledOps = Collections.synchronizedList( new ArrayList< ScheduledOperation >() );
    private ConcurrentLinkedQueue< ScheduledOperation > oneTimeOps = new ConcurrentLinkedQueue< ScheduledOperation >();
    
    private List< Interval > intervals = Collections.synchronizedList( new ArrayList< Interval >() );
    
    private Vector< IntervalListener > intervalListeners = new Vector< IntervalListener >();
    
    private final Vector< Updatable > updatableList = new Vector< Updatable >();
    
    /**
     * @return the number of {@link ScheduledOperation}s.
     */
    public final int getNumberOfSchedOps()
    {
        return ( scheduledOps.size() + oneTimeOps.size() );
    }
    
    /**
     * @return the number of {@link Interval}s.
     */
    public final int getNumberOfIntevals()
    {
        return ( intervals.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void scheduleOperation( ScheduledOperation schedOp )
    {
        if ( schedOp.isPersistent() )
            scheduledOps.add( schedOp );
        else
            oneTimeOps.add( schedOp );
    }
    
    /**
     * {@inheritDoc}
     */
    public void unscheduleOperation( ScheduledOperation schedOp )
    {
        scheduledOps.remove( schedOp );
    }
    
    /**
     * {@inheritDoc}
     */
    public void addInterval( Interval interval )
    {
        interval.revive();
        intervals.add( interval );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAllIntervals()
    {
        intervals.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public void addIntervalListener( IntervalListener il )
    {
        intervalListeners.add( il );
    }
    
    /**
     * {@inheritDoc}
     */
    public IntervalListener removeIntervalListener( IntervalListener il )
    {
        if ( intervalListeners.remove( il ) )
            return ( il );
        
        return ( null );
    }
    
    public void addAnimatableObject( Animatable animObj, long gameTime, TimingMode timingMode )
    {
        scheduleOperation( animObj );
        
        if ( gameTime >= 0L )
            animObj.startAnimation( gameTime, timingMode );
    }
    
    /**
     * {@inheritDoc}
     */
    public void addAnimatableObject( Animatable animObj, boolean startAnimation )
    {
        if ( startAnimation )
            addAnimatableObject( animObj, getGameTime(), getTimingMode() );
        else
            addAnimatableObject( animObj, -1L, getTimingMode() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void addAnimatableObject( Animatable anumObj )
    {
        addAnimatableObject( anumObj, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAnimatableObject( Animatable animObj )
    {
        unscheduleOperation( animObj );
    }
    
    /**
     * This method is executed each time an interval is hit.
     * 
     * @param interval the hit Interval
     * @param gameTime the current gameTime
     * @param frameTime the time needed to render the last frame
     * @param timingMode
     */
    protected void callIntervalListeners( Interval interval, long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( !intervalListeners.isEmpty() )
        {
            for ( int i = 0; i < intervalListeners.size(); i++ )
            {
                intervalListeners.get( i ).onIntervalHit( interval, gameTime, frameTime, timingMode );
            }
        }
    }
    
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
     * {@inheritDoc}
     */
    @Override
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        // check intervals
        if ( !intervals.isEmpty() )
        {
            int i = 0;
            while ( i < intervals.size() )
            {
                final Interval interval = intervals.get( i );
                
                if ( ( interval.isAlive() ) && ( interval.check( gameTime, frameTime, timingMode ) ) )
                    callIntervalListeners( interval, gameTime, frameTime, timingMode );
                
                if ( interval.isAlive() )
                    i++;
                else
                    intervals.remove( i );
            }
        }
        
        // execute persistent scheduled operations
        if ( !scheduledOps.isEmpty() )
        {
            int i = 0;
            while ( i < scheduledOps.size() )
            {
                final ScheduledOperation schedOp = scheduledOps.get( i );
                
                if ( schedOp.isAlive() )
                {
                    schedOp.update( gameTime, frameTime, timingMode );
                    i++;
                }
                else
                {
                    scheduledOps.remove( i );
                }
            }
        }
        
        // execute nonpersistent scheduled operations
        if ( !oneTimeOps.isEmpty() )
        {
            ScheduledOperation schedOp;
            while ( ( schedOp = oneTimeOps.poll() ) != null )
            {
                schedOp.update( gameTime, frameTime, timingMode );
            }
        }
        
        if ( !updatableList.isEmpty() )
        {
            for ( int i = 0; i < updatableList.size(); i++ )
                updatableList.get( i ).update( gameTime, frameTime, timingMode );
        }
    }
    
    /**
     * Checks the list of operations and intervals and calls
     * necessary methods if the time is right.
     * 
     * @param gameTime the current game time
     */
    public void update( long gameTime )
    {
        update( gameTime, -1L, TimingMode.MILLISECONDS );
    }
    
    /**
     * Checks the list of operations and intervals and calls
     * necessary methods if the time is right.
     */
    public void update()
    {
        update( -1L, -1L, TimingMode.MILLISECONDS );
    }
    
    public OperationSchedulerImpl( long minItTime, GameTimeHost gameTimeHost )
    {
        super( minItTime, gameTimeHost );
    }
    
    public OperationSchedulerImpl( long minItTime )
    {
        this( minItTime, null );
    }
    
    public OperationSchedulerImpl( GameTimeHost gameTimeHost )
    {
        this( 0L, gameTimeHost );
    }
    
    public OperationSchedulerImpl()
    {
        this( 0L, null );
    }
}
