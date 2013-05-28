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
package org.xith3d.utility.math;

import org.jagatoo.util.timing.Time;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;

/**
 * A timed interpolator uses an Interpolator (e.g. LinearFloatInterpolator)
 * but it adjust it automatically so that it interpolates precisely between two
 * instants you define.
 * Don't worry about memory or what : when a TimedInterpolator is finished (the
 * "end time" is reached), it is destroyed automatically.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class TimedInterpolator extends ScheduledOperationImpl {
    
    private Time beginTime;
    private Time endTime;
    private Interpolator interpolator;
    private OperationScheduler opSched;
    
    /**
     * Create a new TimedInterpolator
     * @param interpolator The interpolator on which to act
     * @param beginTime The time the interpolator should begin
     * @param endTime The time the interpolator should end
     * @param opSched The OperationScheduled we should schedule to
     */
    public TimedInterpolator(Interpolator interpolator, Time beginTime, Time endTime, OperationScheduler opSched) {
        
        super(true);
        
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.interpolator = interpolator;
        this.opSched = opSched;
        opSched.scheduleOperation(this);
        
    }
    
    /**
     * Create a new TimedInterpolator
     * @param interpolator The interpolator on which to act
     * @param beginTime The time the interpolator should begin
     * @param length The length the interpolator should last
     * @param opSched The OperationScheduled we should schedule to
     */
    public TimedInterpolator(Interpolator interpolator, Time beginTime, double length, OperationScheduler opSched) {
        
        this(interpolator, beginTime, new Time(beginTime.getValue() + length, beginTime.getUnit()), opSched);
        
    }
    
    /**
     * Create a new TimedInterpolator
     * @param interpolator The interpolator on which to act
     * @param length The length the interpolator should last
     * @param opSched The OperationScheduled we should schedule to
     */
    public TimedInterpolator(Interpolator interpolator, Time length, OperationScheduler opSched) {
        
        this(interpolator, new Time(-1, length.getUnit()), length, opSched);
        
    }

    public void update(long gameTime, long frameTime, TimingMode timingMode) {

        final long millis = timingMode.getMilliSeconds(gameTime);
        
        if(beginTime.getValue() == -1) {
            beginTime = new Time(millis, Time.MILLISECOND);
            double endTimeMillis = endTime.getMilliseconds();
            double realEndTime = endTimeMillis + millis;
            endTime = new Time(realEndTime, Time.MILLISECOND);
        }
        
        if(millis > endTime.getMilliseconds()) {
            interpolator.setAlpha(1f);
            opSched.unscheduleOperation(this);
        } else if(millis > beginTime.getMilliseconds()) {
            float alpha = (float) ((millis - beginTime.getMilliseconds()) / (endTime.getMilliseconds() - beginTime.getMilliseconds()));
            interpolator.setAlpha(alpha);
        }
        
    }

    /**
     * @return the beginTime
     */
    public Time getBeginTime() {
        return beginTime;
    }

    /**
     * @param beginTime the beginTime to set
     */
    public void setBeginTime(Time beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * @return the endTime
     */
    public Time getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the interpolator
     */
    public Interpolator getInterpolator() {
        return interpolator;
    }

    /**
     * @param interpolator the interpolator to set
     */
    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }
    
}
