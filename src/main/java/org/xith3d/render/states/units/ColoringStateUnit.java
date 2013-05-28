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
package org.xith3d.render.states.units;

import org.jagatoo.opengl.enums.BlendMode;
import org.xith3d.render.states.StateMap;
import org.xith3d.render.states.StateUnit;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.TransparencyAttributes;

/**
 * The ColoringShader encapsulates the Java3D standard coloring which are
 * attached to an Appearance.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class ColoringStateUnit extends StateUnit
{
    public static final int STATE_TYPE = StateMap.newStateType();
    
    public static final StateMap coloringStateMap = new StateMap();
    public static final StateMap transparencyStateMap = new StateMap();
    
    public static final ColoringAttributes DEFAULT_COLOR_ATTR = new ColoringAttributes();
    public static final TransparencyAttributes DEFAULT_TRANS_ATTR = new TransparencyAttributes( BlendMode.NONE, 0f );
    
    private ColoringAttributes coloringAttribs;
    private TransparencyAttributes transparencyAttribs;
    
    public final ColoringAttributes getColoringAttributes()
    {
        return ( coloringAttribs );
    }
    
    public final TransparencyAttributes getTransparencyAttributes()
    {
        return ( transparencyAttribs );
    }
    
    public static boolean isDefault( ColoringStateUnit cs )
    {
        return ( ( cs.coloringAttribs == DEFAULT_COLOR_ATTR ) && ( cs.transparencyAttribs == DEFAULT_TRANS_ATTR ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColoringAttributes getNodeComponent()
    {
        return ( coloringAttribs );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isTranslucent()
    {
        if ( !transparencyAttribs.isSortEnabled() )
            return ( false );
        
        if ( transparencyAttribs.getMode() == TransparencyAttributes.BLENDED )
            return ( true );
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long getStateId()
    {
        return ( ( transparencyAttribs.getStateId() << 20 ) | coloringAttribs.getStateId() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( this.getClass().getSimpleName() + "( state-type: " + getStateType() + ", " + "state-ID: " + getStateId() + " )" );
    }
    
    public final void update( ColoringAttributes ca, TransparencyAttributes ta )
    {
        this.coloringAttribs = ( ca != null ) ? ca : DEFAULT_COLOR_ATTR;
        this.transparencyAttribs = ( ta != null ) ? ta : DEFAULT_TRANS_ATTR;
        
        coloringStateMap.assignState( coloringAttribs );
        transparencyStateMap.assignState( transparencyAttribs );
    }
    
    private ColoringStateUnit( ColoringAttributes ca, TransparencyAttributes ta )
    {
        super( STATE_TYPE, false );
        
        update( ca, ta );
    }
    
    public static ColoringStateUnit makeColoringStateUnit( ColoringAttributes ca, TransparencyAttributes ta, StateUnit[] cache )
    {
        if ( cache[ STATE_TYPE ] != null )
        {
            final ColoringStateUnit stateUnit = ( (ColoringStateUnit)cache[ STATE_TYPE ] );
            stateUnit.update( ca, ta );
            
            return ( stateUnit );
        }
        
        ColoringStateUnit stateUnit = new ColoringStateUnit( ca, ta );
        cache[ STATE_TYPE ] = stateUnit;
        
        return ( stateUnit );
    }
}
