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
package org.xith3d.render.jsr231;

import org.xith3d.render.StateUnitPeerRegistry;

/**
 * ShaderRegistry implementation base for JOGL (JSR-231).
 * 
 * @author David Yazel
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 * @author Florian Hofmann (aka Goliat) GLSL Shader support
 */
class StateUnitPeerRegistryImpl extends StateUnitPeerRegistry
{
    public StateUnitPeerRegistryImpl()
    {
        super();
        
        // register the atom and shader renderers
        
        this.registerStateUnitPeer( org.xith3d.render.states.units.ColoringStateUnit.STATE_TYPE, new ColoringStateUnitPeer() );
        this.registerStateUnitPeer( org.xith3d.render.states.units.ShaderProgramStateUnit.STATE_TYPE, new ShaderProgramStateUnitPeer() );
        TextureUnitStateUnitPeer tusup = new TextureUnitStateUnitPeer();
        for ( int i = 0; i < org.xith3d.render.states.units.TextureUnitStateUnit.STATE_TYPES.length; i++ )
        {
            this.registerStateUnitPeer( org.xith3d.render.states.units.TextureUnitStateUnit.STATE_TYPES[i], tusup );
        }
        this.registerStateUnitPeer( org.xith3d.render.states.units.LightingStateUnit.STATE_TYPE, new LightingStateUnitPeer() );
        this.registerStateUnitPeer( org.xith3d.render.states.units.FogStateUnit.STATE_TYPE, new FogStateUnitPeer() );
        this.registerStateUnitPeer( org.xith3d.render.states.units.MaterialStateUnit.STATE_TYPE, new MaterialStateUnitPeer() );
        this.registerStateUnitPeer( org.xith3d.render.states.units.PolygonAttribsStateUnit.STATE_TYPE, new PolygonAttribsStateUnitPeer() );
        this.registerStateUnitPeer( org.xith3d.render.states.units.LineAttribsStateUnit.STATE_TYPE, new LineAttribsStateUnitPeer() );
        this.registerStateUnitPeer( org.xith3d.render.states.units.PointAttribsStateUnit.STATE_TYPE, new PointAttribsStateUnitPeer() );
        this.registerStateUnitPeer( org.xith3d.render.states.units.RenderingAttribsStateUnit.STATE_TYPE, new RenderingAttribsStateUnitPeer() );
        
        this.registerAtomRenderer( org.xith3d.render.preprocessing.ShapeAtom.STATE_TYPE, new ShapeAtomPeer() );
        this.registerAtomRenderer( org.xith3d.render.preprocessing.BoundsAtom.STATE_TYPE, new BoundsAtomPeer() );
    }
}
