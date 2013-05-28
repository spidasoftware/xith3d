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

import org.xith3d.render.preprocessing.sorting.OrderedStateRenderBinSorter;
import org.xith3d.render.preprocessing.sorting.StatePriorities;
import org.xith3d.render.preprocessing.sorting.StateRenderBinSorter;
import org.xith3d.render.states.units.ColoringStateUnit;
import org.xith3d.render.states.units.FogStateUnit;
import org.xith3d.render.states.units.ShaderProgramStateUnit;
import org.xith3d.render.states.units.LightingStateUnit;
import org.xith3d.render.states.units.LineAttribsStateUnit;
import org.xith3d.render.states.units.MaterialStateUnit;
import org.xith3d.render.states.units.PointAttribsStateUnit;
import org.xith3d.render.states.units.PolygonAttribsStateUnit;
import org.xith3d.render.states.units.RenderingAttribsStateUnit;
import org.xith3d.render.states.units.TextureUnitStateUnit;

/**
 * This enum can be used to initialized a {@link StatePriorities} instance,
 * which must be passed to a {@link StateRenderBinSorter} or
 * {@link OrderedStateRenderBinSorter}.
 * <p>
 * The order if the fields is important, since it defines the order in which
 * states are applied to the GL, which is important.
 * </p>
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public enum StateTypes
{
    COLORING( ColoringStateUnit.STATE_TYPE ),
    SHADER_PROGRAM( ShaderProgramStateUnit.STATE_TYPE ),
    TEXTURE_UNIT0( TextureUnitStateUnit.STATE_TYPES[ 0 ] ),
    TEXTURE_UNIT1( TextureUnitStateUnit.STATE_TYPES[ 1 ] ),
    TEXTURE_UNIT2( TextureUnitStateUnit.STATE_TYPES[ 2 ] ),
    TEXTURE_UNIT3( TextureUnitStateUnit.STATE_TYPES[ 3 ] ),
    TEXTURE_UNIT4( TextureUnitStateUnit.STATE_TYPES[ 4 ] ),
    TEXTURE_UNIT5( TextureUnitStateUnit.STATE_TYPES[ 5 ] ),
    TEXTURE_UNIT6( TextureUnitStateUnit.STATE_TYPES[ 6 ] ),
    TEXTURE_UNIT7( TextureUnitStateUnit.STATE_TYPES[ 7 ] ),
    LIGHTING( LightingStateUnit.STATE_TYPE ),
    FOG( FogStateUnit.STATE_TYPE ),
    MATERIAL( MaterialStateUnit.STATE_TYPE ),
    POLYGON_ATTRIBUTES( PolygonAttribsStateUnit.STATE_TYPE ),
    LINE_ATTRIBUTES( LineAttribsStateUnit.STATE_TYPE ),
    POINT_ATTRIBUTES( PointAttribsStateUnit.STATE_TYPE ),
    RENDERING_ATTRIBUTES( RenderingAttribsStateUnit.STATE_TYPE ),
    ;
    
    private final int internalType;
    
    public final int getInternalType()
    {
        return ( internalType );
    }
    
    private StateTypes( int internalType )
    {
        this.internalType = internalType;
    }
    
    /**
     * This method can be called from anywhere to initialize the state-unit-types.
     */
    public static final void init()
    {
    }
}
