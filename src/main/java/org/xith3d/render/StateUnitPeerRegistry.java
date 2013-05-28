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
package org.xith3d.render;

import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.StateUnitPeer;

/**
 * A ShaderRegistry maintains all ShaderPeer implementations used by the
 * rendering implementation.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class StateUnitPeerRegistry
{
    protected final StateUnitPeer[] peerMap = new StateUnitPeer[ StateUnit.MAX_STATE_TYPES ];
    protected final RenderAtomPeer[] atomMap = new RenderAtomPeer[ StateUnit.MAX_ATOM_TYPES ];
    
    /**
     * Registers an API specific rendering peer. An appearance shader could be
     * implemented differently in LWJGL and JOGL.
     * 
     * @param stateUnitType
     * @param stateUnitPeer
     */
    protected void registerStateUnitPeer( int stateUnitType, StateUnitPeer stateUnitPeer )
    {
        peerMap[ stateUnitType ] = stateUnitPeer;
    }
    
    /**
     * The RenderAtomPeer implements the API specific commands necessary to
     * render the geometry. For example a GeometryArray.
     * 
     * @param atomType
     * @param atomPeer
     */
    protected void registerAtomRenderer( int atomType, RenderAtomPeer atomPeer )
    {
        atomMap[ atomType ] = atomPeer;
    }
    
    /**
     * @return the StateUnitPeer for this StateUnit. The first time we hit a stateType
     *         we have not seen yet this will need to lookup the class and
     *         assign it to the state type. Subsequent times it is a quick
     *         lookup.
     * 
     * @param stateUnitType
     */
    public final StateUnitPeer getStateUnitPeer( int stateUnitType )
    {
        return ( peerMap[ stateUnitType ] );
    }
    
    /**
     * @return the RenderAtomPeer for this atom. The first time we hit a
     *         stateType we have not seen yet this will need to lookup the class
     *         and assign it to the state type. Subsequent times it is a quick
     *         lookup.
     * 
     * @param atomStateType
     */
    public final RenderAtomPeer getRenderAtomPeer( int atomStateType )
    {
        return ( atomMap[ atomStateType ] );
    }
    
    public StateUnitPeerRegistry()
    {
    }
}
