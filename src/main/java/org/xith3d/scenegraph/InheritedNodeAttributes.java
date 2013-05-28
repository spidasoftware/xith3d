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
package org.xith3d.scenegraph;

import org.jagatoo.util.arrays.ArrayUtils;
import org.xith3d.render.ScissorRect;
import org.xith3d.render.ClipperInfo;

/**
 * the {@link InheritedNodeAttributes} class holds references to inherited
 * {@link Node}s like lights, fog, etc.
 * Each SceneGraph {@link Node} carries an instance of this class,
 * which is automatically updated and which is read-only for non-scenegraph
 * classes.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public final class InheritedNodeAttributes
{
    private static final int MAX_LIGHTS = 8;
    private static final int MAX_FOGS = 1;
    
    private Light[] lights = new Light[ 8 ];
    private int numLights = 0;
    private int numEffectiveLights = 0;
    private Fog[] fogs = new Fog[ 8 ];
    private int numFogs = 0;
    private int numEffectiveFogs = 0;
    private ScissorRect scissorRect = null;
    private ClipperInfo clipper = null;
    
    private boolean lightsDirty = false;
    private boolean fogsDirty = false;
    
    protected final void addLight( Light light )
    {
        if ( lights.length <= numLights )
        {
            Light[] tmp = new Light[ lights.length + 1 ];
            System.arraycopy( lights, 0, tmp, 0, lights.length );
            lights = tmp;
        }
        
        lights[ numLights++ ] = light;
        
        this.numEffectiveLights = Math.min( numLights, MAX_LIGHTS );
        
        lightsDirty = true;
    }
    
    protected final void removeLight( Light light )
    {
        int index = ArrayUtils.indexOf( lights, light, true );
        
        if ( index >= 0 )
        {
            System.arraycopy( lights, index + 1, lights, index, numLights - index - 1 );
            lights[ --numLights ] = null;
            this.numEffectiveLights = Math.min( numLights, MAX_LIGHTS );
        }
        
        lightsDirty = true;
    }
    
    /**
     * @return the number of (inherited) {@link Light}s in the owning Node.
     */
    public final int getLightsCount()
    {
        return ( numLights );
    }
    
    /**
     * @return the number of (inherited) {@link Light}s in the owning Node,
     * but with a maximum of 8.
     */
    public final int getEffectiveLightsCount()
    {
        return ( numEffectiveLights );
    }
    
    /**
     * @return the i-th (inherited) {@link Light} in the owning Node.
     */
    public final Light getLight( int index )
    {
        return ( lights[ index ] );
    }
    
    public final boolean getLightsDirty()
    {
        return ( lightsDirty );
    }
    
    public final void setLightsClean()
    {
        lightsDirty = false;
    }
    
    protected final void addFog( Fog fog )
    {
        if ( fogs.length <= numFogs )
        {
            Fog[] tmp = new Fog[ fogs.length + 1 ];
            System.arraycopy( fogs, 0, tmp, 0, fogs.length );
            fogs = tmp;
        }
        
        fogs[ numFogs++ ] = fog;
        
        numEffectiveFogs = Math.min( numFogs, MAX_FOGS );
        
        fogsDirty = true;
    }
    
    protected final void removeFog( Fog fog )
    {
        int index = ArrayUtils.indexOf( fogs, fog, true );
        
        if ( index >= 0 )
        {
            System.arraycopy( fogs, index + 1, fogs, index, numFogs - index - 1 );
            fogs[ --numFogs ] = null;
            this.numEffectiveFogs = Math.min( numFogs, MAX_FOGS );
        }
        
        fogsDirty = true;
    }
    
    /**
     * @return the number of (inherited) {@link Fog}s in the owning Node.
     */
    public final int getFogsCount()
    {
        return ( numFogs );
    }
    
    /**
     * @return the number of (inherited) {@link Fog}s in the owning Node,
     * but with a maximum of 1.
     */
    public final int getEffectiveFogsCount()
    {
        return ( numEffectiveFogs );
    }
    
    /**
     * @return the i-th (inherited) {@link Fog} in the owning Node.
     */
    public final Fog getFog( int index )
    {
        return ( fogs[ index ] );
    }
    
    public final boolean getFogsDirty()
    {
        return ( fogsDirty );
    }
    
    public final void setFogsClean()
    {
        fogsDirty = false;
    }
    
    protected final void setScissorRect( ScissorRect scissorRect )
    {
        this.scissorRect = scissorRect;
    }
    
    /**
     * @return the (inherited) {@link ScissorRect} of the owning Node.
     */
    public final ScissorRect getScissorRect()
    {
        return ( scissorRect );
    }
    
    protected final void setClipper( ClipperInfo clipper )
    {
        this.clipper = clipper;
    }
    
    /**
     * @return the (inherited) {@link ClipperInfo} of the owning Node.
     */
    public final ClipperInfo getClipper()
    {
        return ( clipper );
    }
    
    protected void merge( InheritedNodeAttributes toMerge )
    {
        for ( int i = 0; i < toMerge.getLightsCount(); i++ )
        {
            final Light light = toMerge.getLight( i );
            if ( !ArrayUtils.contains( lights, light, true ) )
                addLight( light );
        }
        
        for ( int i = 0; i < toMerge.getFogsCount(); i++ )
        {
            final Fog fog = toMerge.getFog( i );
            if ( !ArrayUtils.contains( fogs, fog, true ) )
                addFog( fog );
        }
        
        if ( this.scissorRect == null )
            this.scissorRect = toMerge.scissorRect;
        
        if ( this.clipper == null )
            this.clipper = toMerge.clipper;
    }
    
    protected void unmerge( InheritedNodeAttributes toUnmerge )
    {
        for ( int i = 0; i < toUnmerge.getLightsCount(); i++ )
        {
            removeLight( toUnmerge.getLight( i ) );
        }
        
        for ( int i = 0; i < toUnmerge.getFogsCount(); i++ )
        {
            removeFog( toUnmerge.getFog( i ) );
        }
        
        if ( this.scissorRect == toUnmerge.getScissorRect() )
            this.scissorRect = null;
        
        if ( this.clipper == toUnmerge.getClipper() )
            this.clipper = null;
    }
    
    public InheritedNodeAttributes()
    {
    }
}
