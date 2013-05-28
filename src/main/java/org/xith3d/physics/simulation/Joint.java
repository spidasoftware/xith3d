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
package org.xith3d.physics.simulation;

/**
 * A Joint is a constraint which is applied between Bodies.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Joint
{
    /** The first body constrained by this Joint or null if it's the static environment */
    private final Body body1;
    
    /** The second body constrained by this Joint or null if it's the static environment */
    private final Body body2;
    
    /**
     * @return the name of the Joint, that is, the one which is used to refer to
     *         it in CollisionEngine. All camel-case, please ! (e.g. : "Sphere")
     */
    public abstract String getType();
    
    /**
     * @return a small description of how is this joint, its exact
     * shape, a brief human-readable description of what can be adjusted
     */
    public abstract String getInfo();
    
    /**
     * @return the {@link SimulationWorld}, this Joint belongs to.
     */
    public abstract SimulationWorld getWorld();
    
    /**
     * @return the body1
     */
    public final Body getBody1()
    {
        return ( body1 );
    }
    
    /**
     * @return the body2
     */
    public final Body getBody2()
    {
        return ( body2 );
    }
    
    /**
     * Refreshes this {@link Joint}'s anchors, etc. from the implementation.
     */
    protected void refresh()
    {
    }
    
    /**
     * Creates a new Joint.
     * 
     * @param body1 The first body constrained by this Joint or null if it's the static environment
     * @param body2 The second body constrained by this Joint or null if it's the static environment
     */
    public Joint( Body body1, Body body2 )
    {
        this.body1 = body1;
        this.body2 = body2;
    }
}
