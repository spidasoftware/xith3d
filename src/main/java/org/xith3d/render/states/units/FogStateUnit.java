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
import org.xith3d.scenegraph.Fog;
import org.xith3d.scenegraph.InheritedNodeAttributes;
import org.xith3d.scenegraph.NodeComponent;

/**
 * Insert package comments here
 * 
 * Originally Coded by Benjamin Winters on Jul 24, 2004 at 7:56:34 PM. Based
 * upon LightingShader class by David Yazel.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FogStateUnit extends StateUnit
{
    public static final int STATE_TYPE = StateMap.newStateType();
    
    private final InheritedNodeAttributes inheritedNodeAttribs;
    
    public final int numFogs()
    {
        return ( inheritedNodeAttribs.getFogsCount() );
    }
    
    public final Fog getFog( int index )
    {
        return ( inheritedNodeAttribs.getFog( index ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NodeComponent getNodeComponent()
    {
        return ( null );
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
        long stateId = 0L;
        
        for ( int i = 0; i < inheritedNodeAttribs.getEffectiveFogsCount(); i++ )
        {
            stateId |= ( inheritedNodeAttribs.getFog( i ).getFogID() << ( i * 16 ) );
        }
        
        return ( stateId );
    }
    
    @Override
    public String toString()
    {
        String s = "FogStateUnit: ";
        for ( int i = 0; i < numFogs(); i++ )
            s = s + getFog( i ).getName() + " ";
        
        return ( s );
    }
    
    private final void update( boolean forced )
    {
        if ( inheritedNodeAttribs.getFogsDirty() || forced )
        {
            updateCachedStateId();
            
            inheritedNodeAttribs.setFogsClean();
        }
    }
    
    public final void update()
    {
        update( false );
    }
    
    private FogStateUnit( InheritedNodeAttributes inheritedNodeAttribs )
    {
        super( STATE_TYPE, false );
        
        this.inheritedNodeAttribs = inheritedNodeAttribs;
    }
    
    public static FogStateUnit makeFogStateUnit( InheritedNodeAttributes inheritedNodeAttribs )
    {
        FogStateUnit stateUnit = new FogStateUnit( inheritedNodeAttribs );
        
        stateUnit.update( true );
        
        return ( stateUnit );
    }
}
