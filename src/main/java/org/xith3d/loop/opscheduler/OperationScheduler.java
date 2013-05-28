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

import org.xith3d.loop.Updatable;
import org.xith3d.loop.Updater;
import org.xith3d.loop.UpdatingThread.TimingMode;

/**
 * An OperationScheduler is capable of handling ScheduledOperations, which are
 * to be executed by the render thread.<br>
 * This way SceneGraph manipulations can be done thread safely.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface OperationScheduler extends Animator, Updater, Updatable
{
    /**
     * Adds a ScheduledOperation to the queue.
     * The loop works off the queue each iteration.
     * 
     * @param schedOp the Operation to schedule
     */
    public void scheduleOperation( ScheduledOperation schedOp );
    
    /**
     * Removes a ScheduledOperation from the queue.
     * 
     * @param schedOp the Operation to unschedule
     */
    public void unscheduleOperation( ScheduledOperation schedOp );
    
    /**
     * Adds a new Interval to the scheduler, which will be checked each
     * iteration.
     * When the check() method returns true, all IntervalListeners are notified.
     * This method immediately invokes the revive() method of the given Interval.
     * 
     * @param interval the new Interval
     */
    public void addInterval( Interval interval );
    
    /**
     * Removes all Intervals from the scheduler.
     */
    public void removeAllIntervals();
    
    /**
     * Registers a new IntervalListner to this OperationScheduler.
     * 
     * @param l the new IntervalListener
     */
    public void addIntervalListener( IntervalListener l );
    
    /**
     * Unregisters an IntervalListner from this OperationScheduler.
     * 
     * @param l the IntervalListener to remove
     */
    public IntervalListener removeIntervalListener( IntervalListener l );
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode );
}
