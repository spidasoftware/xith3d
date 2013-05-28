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
package org.xith3d.render.states;

import java.util.TreeMap;

/**
 * A state map is used to map objects to state id's. This is based on the
 * assumption that many state objects (like materials) are different objects but
 * are actually the same values. This class provides a way to assign unique id's
 * to unique combinations of objects and then map all the other objects to the
 * same id. This allows for efficient state sorting and state changes. The
 * weakness is that it will keep a pointer one more frame than it would normally
 * be kept, since reference count would not go to zero until the next frame
 * render.
 * 
 * @author David Yazel
 */
public class StateMap
{
    private static int nextStateType = 0;
    private long nextId = 0;
    
    private TreeMap< StateTrackable< ? >, StateNode > states;
    
    /**
     * Used to get an integer ID which uniquely defined a state type. Shaders
     * should initialize their state type in a static constant in their class
     * definitions.
     * 
     * @return the state type ID
     */
    public static int newStateType()
    {
        return ( nextStateType++ );
    }
    
    public StateMap()
    {
        states = new TreeMap< StateTrackable< ? >, StateNode >();
    }
    
    /**
     * Ok this is a little tricky. When a state trackable item comes in, we
     * check to see if there is a state node already assigned. If there is then
     * we are all set. If there isn't then what we have is this item which is a
     * unique object, but probably is the same as one we have already seen.
     * <p>
     * So we take the item and use it as a key into a TreeMap. The state
     * trackable items have to implement Comparable, so we we can use it as a
     * key. Now this can be expensive since some shader components have many
     * sub-items which will have to be compared. But if the comparators are
     * written properly we can quickly match against the tree state in log(n)
     * compares, which is very few.
     * <p>
     * If we find and entry then we just assign that state node to the state
     * trackable item. If we do not then we create a new node. This node copies
     * the statetrackable item, assigns it a unique id and inserts it into the
     * tree.
     * 
     * @param trackable
     */
    public void assignState( StateTrackable< ? > trackable )
    {
        StateNode sn = trackable.getStateNode();
        
        /*
         * Commenting out null check will enforce existing StateNode lookup on
         * every shader creation, since we have to get new (or existing) id
         * on change made.
         * But this is not a big performance impact, because of the
         * atom/shader caching handled now on higher level.
         */
        // if ( sn == null ) {
        StateNode existing = states.get( trackable );
        if ( existing != null )
        {
            trackable.setStateNode( existing );
        }
        else
        {
            sn = new StateNode( trackable );
            sn.id = nextId++;
            states.put( sn.masterCopy, sn );
            trackable.setStateNode( sn );
        }
    }
}
