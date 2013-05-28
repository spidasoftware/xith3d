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

import java.util.HashMap;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.scenegraph.Transformable;

/**
 * The Great ConnectionManager is the master of all modularity :)
 * It permits you to "bind" different members of your objects,
 * for example imagine you want to make a door open progressively
 * in your game. Well, create a float interpolator (and configure it
 * correctly). Then connect the angle of your door with this
 * interpolator. Now every time you adjust your interpolator
 * (setAlpha method) your door will have the corresponding angle..
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class ConnectionManager extends ScheduledOperationImpl {
    
    public final HashMap<Object, Connection> connections;
    private final OperationScheduler opSched;
    
    public ConnectionManager(OperationScheduler opSched) {
        
        super(true);
        this.opSched = opSched;
        this.opSched.scheduleOperation(this);
        this.connections = new HashMap<Object, Connection>();
        
    }

    public void update(long gameTime, long frameTime, TimingMode timingMode) {
        
        // Still don't know if it'll be useful one day :)
        
    }
    
    /**
     * Connect two objects, so that the specified member of the first
     * object will (nearly) always be equal to the specified member
     * of the second object
     * @param object1 The first object
     * @param member1 The member of the first object
     * @param object2 The second object
     * @param member2 The member of the second object
     */
    public void connect(Object object1, String member1, Object object2, String member2) {
        
        connections.put(object1, new MemberConnection(object1, member1, object2,
                member2, opSched));
        
    }
    
    /**
     * Connect two placeable, so that the first placeable will have exactly
     * the same position/angle as the second placeable
     * @param p1 The first placeable
     * @param p2 The second placeable
     */
    public void connect(Transformable p1, Transformable p2) {
        
        connections.put(p1, new PlaceableConnection(p1, p2));
        
    }
}
