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
package org.xith3d.utility.input;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.Keyboard;
import org.jagatoo.input.devices.components.Keys;
import org.openmali.vecmath2.Point3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.ScheduledOperation;
import org.xith3d.physics.util.Placeable;

/**
 * Eight directions movement, controlled by
 * the arrow keys, adjustable speed.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class EightDirectionsMovement implements ScheduledOperation {

    private final Placeable placeable;
    private final Keyboard keyboard;
    private float speed;
    
    // TODO : Add constructor for different key combinations
    
    /**
     * New EightDirectionsMovement
     */
    public EightDirectionsMovement(OperationScheduler opScheder,
                                   Placeable placeable,
                                   float speed) {
        
        this(null, opScheder, placeable, speed);
        
    }
    
    /**
     * New EightDirectionsMovement
     */
    public EightDirectionsMovement(Keyboard keyboard,
            OperationScheduler opScheder, Placeable placeable, float speed) {
        
        super();
        
        if (keyboard == null) {
            if (!InputSystem.hasInstance()) {
                throw new Error("No InputSystem registered.");
            }
            
            keyboard = InputSystem.getInstance().getKeyboard();
            
            if (keyboard == null) {
                throw new Error("No Keyboard registered at the InputSystem.");
            }
        }
        
        this.placeable = placeable;
        this.keyboard = keyboard;
        this.speed = speed;
        
        opScheder.scheduleOperation(this);
        
    }
    
    public void update(long gameTime, long frameTime, TimingMode timingMode) {

        float realSpeed = speed * timingMode.getSecondsAsFloat(frameTime);
        
        float xDiff = 0;
        float yDiff = 0;
        
        if(keyboard.isKeyPressed(Keys.LEFT)) {
            xDiff -= realSpeed;
        }
        if(keyboard.isKeyPressed(Keys.RIGHT)) {
            xDiff += realSpeed;
        }
        if(keyboard.isKeyPressed(Keys.DOWN)) {
            yDiff -= realSpeed;
        }
        if(keyboard.isKeyPressed(Keys.UP)) {
            yDiff += realSpeed;
        }
        
        Point3f pos = Point3f.fromPool();
        
        placeable.getPosition(pos);
        pos.addX( xDiff );
        pos.addY( yDiff );
        placeable.setPosition(pos);
        
        Point3f.toPool(pos);
        
    }
    
    public boolean isAlive() {
        return true;
    }
    
    public boolean isPersistent() {
        return true;
    }
    
    public void setAlive(boolean alive) {
        // Blaaaaaah !
    }
    
    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    /**
     * @return the speed
     */
    public final float getSpeed() {
        return speed;
    }
    
}
