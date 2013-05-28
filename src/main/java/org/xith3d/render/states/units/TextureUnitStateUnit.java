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

import org.xith3d.render.states.StateMap;
import org.xith3d.render.states.StateUnit;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.TexCoordGeneration;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TextureUnit;

/**
 * This {@link StateUnit} encapsulates a whole {@link TextureUnit}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureUnitStateUnit extends StateUnit
{
    public static final int[] STATE_TYPES = new int[ 8 ];
    
    //public static final StateMap texStateMap = new StateMap();
    public static final StateMap texAttribsStateMap = new StateMap();
    public static final StateMap texCoorGenStateMap = new StateMap();
    
    public static final TextureAttributes DEFAULT_TEX_ATTR = new TextureAttributes();
    public static final TexCoordGeneration DEFAULT_TEXCOORD_GEN = new TexCoordGeneration( false );
    public static final TextureUnitStateUnit[] DEFAULT_UNIT = new TextureUnitStateUnit[ 8 ];
    static
    {
        for ( int i = 0; i < STATE_TYPES.length; i++ )
        {
            STATE_TYPES[ i ] = StateMap.newStateType();
            DEFAULT_UNIT[ i ] = new TextureUnitStateUnit( i, null, DEFAULT_TEX_ATTR, DEFAULT_TEXCOORD_GEN, true );
        }
    }
    
    private final int unit;
    
    private Texture texture;
    private TextureAttributes texAttribs;
    private TexCoordGeneration texCoordGen;
    
    public final int getUnit()
    {
        return ( unit );
    }
    
    public final Texture getTexture()
    {
        return ( texture );
    }
    
    public final TextureAttributes getTextureAttributes()
    {
        return ( texAttribs );
    }
    
    public final TexCoordGeneration getTexCoordGeneration()
    {
        return ( texCoordGen );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColoringAttributes getNodeComponent()
    {
        throw new Error();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isTranslucent()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long getStateId()
    {
        if ( texture == null )
            return ( ( texAttribs.getStateId() << 16 ) | texCoordGen.getStateId() );
        
        return ( ( texture.getStateId() << 50 ) | ( texAttribs.getStateId() << 16 ) | texCoordGen.getStateId() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( this.getClass().getSimpleName() + "( unit: " + getUnit() + ", state-type: " + getStateType() + ", " + "state-ID: " + getStateId() + " )" );
    }
    
    public final void update( Texture texture, TextureAttributes texAttribs, TexCoordGeneration texCoordGen )
    {
        this.texture = texture;
        this.texAttribs = ( texAttribs != null ) ? texAttribs : DEFAULT_TEX_ATTR;
        this.texCoordGen = ( texCoordGen != null ) ? texCoordGen : DEFAULT_TEXCOORD_GEN;
        
        //texStateMap.assignState( this.texture );
        texAttribsStateMap.assignState( this.texAttribs );
        texCoorGenStateMap.assignState( this.texCoordGen );
    }
    
    private TextureUnitStateUnit( int unit, Texture texture, TextureAttributes texAttribs, TexCoordGeneration texCoordGen, boolean isDefault )
    {
        super( STATE_TYPES[ unit ], isDefault );
        
        this.unit = unit;
        
        update( texture, texAttribs, texCoordGen );
    }
    
    public static TextureUnitStateUnit makeTextureUnitStateUnit( int unit, TextureUnit tu, StateUnit[] cache )
    {
        if ( tu == null )
        {
            return ( DEFAULT_UNIT[ unit ] );
        }
        
        final int stateType = STATE_TYPES[ unit ];
        
        if ( cache[ stateType ] != null )
        {
            final TextureUnitStateUnit stateUnit = ( (TextureUnitStateUnit)cache[ STATE_TYPES[ unit ] ] );
            stateUnit.update( tu.getTexture(), tu.getTextureAttributes(), tu.getTexCoordGeneration() );
            
            return ( stateUnit );
        }
        
        TextureUnitStateUnit stateUnit = new TextureUnitStateUnit( unit, tu.getTexture(), tu.getTextureAttributes(), tu.getTexCoordGeneration(), false );
        cache[ stateType ] = stateUnit;
        
        return ( stateUnit );
    }
}
