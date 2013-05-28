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
package org.xith3d.utility.classes.beans;

import java.lang.reflect.Method;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;

/**
 * Link two members so that (member1 == member2)
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class MemberConnection extends ScheduledOperationImpl implements Connection {
    
    private Method setter;
    private Method getter;
    private final Object object1;
    private final Object object2;
    
    /**
     * Create a new member link so that the first member always has the value of
     * the second one
     */
    public MemberConnection(Object object1, String member1, Object object2,
            String member2, OperationScheduler opSched) {
        
        super(true);
        
        this.object1 = object1;
        this.object2 = object2;
        try {
            this.setter = BeanUtil.getSetter(object1, member1);
            this.getter = BeanUtil.getGetter(object2, member2);
        } catch (Exception e) {
            throw new Error(e);
        }
        
        opSched.scheduleOperation(this);
        
    }
    
    public void update(long gameTime, long frameTime, TimingMode timingMode) {
        
        try {
            this.setter.invoke(object1, this.getter.invoke(object2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void breakConnection() {
        
        this.setAlive(false); // Die
        
    }
    
}
