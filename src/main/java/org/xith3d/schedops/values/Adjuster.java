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
package org.xith3d.schedops.values;

import org.jagatoo.datatypes.NamedObject;
import org.xith3d.loop.opscheduler.ScheduledOperation;

/**
 * Add comment here...
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class Adjuster implements ScheduledOperation, NamedObject {
    
    public enum Mode {
        SEQUENTIAL, CONTINUOUS
    }
    
    // Timer resolution
    public static final float RESOLUTION = 1000;
    
    // Alive flag
    private boolean alive = true;
    
    // Changed flag
    protected boolean changed = false;
    
    // Name
    protected String name;
    
    /**
     * @return true if the adjuster has changed since the last frame
     */
    public final boolean hasChanged() {
        
        return changed;
        
    }
    
    /**
     * Get the state of this adjuster
     */
    public abstract String getState();
    
    /**
     * Print the value of this adjuster
     * 
     * @see #getState()
     */
    public void print() {
        
        System.out.println("[Adjuster " + name + "] = " + getState());
        
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isAlive() {
        
        return alive;
        
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isPersistent() {
        
        return true;
        
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setAlive(boolean alive) {
        
        this.alive = alive;
        
    }
    
    /**
     * Return the name of this adjuster
     */
    public String getName() {
        
        return name;
        
    }
    
}
