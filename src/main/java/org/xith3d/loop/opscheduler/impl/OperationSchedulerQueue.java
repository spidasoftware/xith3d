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
import java.util.Vector;

import org.xith3d.loop.GameTimeHost;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread;
import org.xith3d.loop.opscheduler.Animatable;
import org.xith3d.loop.opscheduler.Interval;
import org.xith3d.loop.opscheduler.IntervalListener;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.ScheduledOperation;

/**
 * This is a queue, that takes instances of OperationScheduler.
 * The Update
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OperationSchedulerQueue extends UpdatingThread implements OperationScheduler
{
    private class IntervalAdapter implements IntervalListener
    {
        private Vector< IntervalListener > intervalListeners = new Vector< IntervalListener >();
        
        public void addIntervalListener( IntervalListener l )
        {
            intervalListeners.add( l );
        }
        
        public IntervalListener removeIntervalListener( IntervalListener l )
        {
            if ( intervalListeners.remove( l ) )
                return ( l );
            
            return ( null );
        }
        
        public void onIntervalHit( Interval interval, long gameTime, long frameTime, TimingMode timingMode )
        {
            for ( int i = 0; i < intervalListeners.size(); i++ )
            {
                intervalListeners.get( i ).onIntervalHit( interval, gameTime, frameTime, timingMode );
            }
        }
    }
    
    private final ArrayList< OperationScheduler > opScheders = new ArrayList< OperationScheduler >();
    private OperationScheduler defaultOpScheder = null;
    private final IntervalAdapter intervalAdapter = new IntervalAdapter();
    
    /**
     * Adds a new OperationScheduler to the queue.
     * 
     * @param opScheder
     * @param index the index, at which the OperationScheduler will be added
     */
    public void addOperationScheduler( OperationScheduler opScheder, int index )
    {
        opScheders.add( index, opScheder );
        
        opScheder.addIntervalListener( intervalAdapter );
    }
    
    /**
     * Adds a new OperationScheduler to the queue.
     * 
     * @param opScheder
     */
    public void addOperationScheduler( OperationScheduler opScheder )
    {
        opScheders.add( opScheder );
        
        opScheder.addIntervalListener( intervalAdapter );
    }
    
    /**
     * Removes an OperationScheduler from the queue.
     * 
     * @param index
     */
    public void removeOperationScheduler( int index )
    {
        opScheders.get( index ).removeIntervalListener( intervalAdapter );
        
        opScheders.remove( index );
    }
    
    /**
     * Removes an OperationScheduler from the queue.
     * 
     * @param opScheder
     */
    public void removeOperationScheduler( OperationScheduler opScheder )
    {
        opScheder.removeIntervalListener( intervalAdapter );
        
        opScheders.remove( opScheder );
    }
    
    /**
     * Sets the default OperationScheduler, that takes the calls to
     * scheduleOperation(), etc. of this OperationSchedulerQueue.
     * 
     * @param defaultOpScheder
     */
    public void setDefaultOperationScheduler( OperationScheduler defaultOpScheder )
    {
        this.defaultOpScheder = defaultOpScheder;
    }
    
    /**
     * @return the default OperationScheduler, that takes the calls to
     * scheduleOperation(), etc. of this OperationSchedulerQueue.
     */
    public OperationScheduler getDefaultOperationScheduler()
    {
        return ( defaultOpScheder );
    }
    
    /**
     * {@inheritDoc}
     */
    public void scheduleOperation( ScheduledOperation schedOp )
    {
        getDefaultOperationScheduler().scheduleOperation( schedOp );
    }
    
    /**
     * {@inheritDoc}
     */
    public void unscheduleOperation( ScheduledOperation schedOp )
    {
        getDefaultOperationScheduler().unscheduleOperation( schedOp );
    }
    
    /**
     * {@inheritDoc}
     */
    public void addInterval( Interval interval )
    {
        getDefaultOperationScheduler().addInterval( interval );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAllIntervals()
    {
        getDefaultOperationScheduler().removeAllIntervals();
    }
    
    /**
     * {@inheritDoc}
     */
    public void addIntervalListener( IntervalListener l )
    {
        intervalAdapter.addIntervalListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    public IntervalListener removeIntervalListener( IntervalListener l )
    {
        return ( intervalAdapter.removeIntervalListener( l ) );
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
            addAnimatableObject( animObj, 0L, getTimingMode() );
        else
            addAnimatableObject( animObj, -1L, null );
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
     * {@inheritDoc}
     */
    public void addUpdatable( Updatable updatable )
    {
        getDefaultOperationScheduler().addUpdatable( updatable );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeUpdatable( Updatable updatable )
    {
        getDefaultOperationScheduler().removeUpdatable( updatable );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        for ( int i = 0; i < opScheders.size(); i++ )
        {
            opScheders.get( i ).update( gameTime, frameTime, timingMode );
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
    
    public OperationSchedulerQueue( long minItTime, GameTimeHost gameTimeHost )
    {
        super( minItTime, gameTimeHost );
    }
    
    public OperationSchedulerQueue( long minItTime )
    {
        this( minItTime, null );
    }
    
    public OperationSchedulerQueue( GameTimeHost gameTimeHost )
    {
        this( 0L, gameTimeHost );
    }
    
    public OperationSchedulerQueue()
    {
        this( 0L, null );
    }
}
