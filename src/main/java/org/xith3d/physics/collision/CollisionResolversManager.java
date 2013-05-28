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
package org.xith3d.physics.collision;

import java.util.ArrayList;
import java.util.Vector;

import org.xith3d.physics.simulation.SurfaceParameters;

/**
 * Manages the {@link CollisionResolver}s.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CollisionResolversManager
{
    private final CollisionEngine   collEngine;
    private       SurfaceParameters defaultSurfaceParameters = null;
    private final Vector<CollisionResolver> collisionResolverList = new Vector<CollisionResolver>();
    private final ArrayList<CollisionResolveListener> collisionResolveListeners = new ArrayList<CollisionResolveListener>();
    
    public final CollisionEngine getCollisionEngine()
    {
        return ( collEngine );
    }
    
    public final void setDefaultSurfaceParameters( SurfaceParameters parameters )
    {
        this.defaultSurfaceParameters = parameters;
    }
    
    public final SurfaceParameters getDefaultSurfaceParameters()
    {
        return ( defaultSurfaceParameters );
    }
    
    public final void addCollisionResolver( CollisionResolver cr )
    {
        collisionResolverList.add( cr );
        cr.setManager( this );
    }
    
    public final void removeCollisionResolver( CollisionResolver cr )
    {
        collisionResolverList.remove( cr );
        cr.setManager( null );
    }
    
    protected Vector< CollisionResolver > getCollisionResolvers()
    {
        return ( collisionResolverList );
    }
    
    public final void addCollisionResolveListener( CollisionResolveListener l )
    {
        collisionResolveListeners.add( l );
    }
    
    public final void removeCollisionResolveListener( CollisionResolveListener l )
    {
        collisionResolveListeners.remove( l );
    }
    
    protected ArrayList< CollisionResolveListener > getCollisionResolveListeners()
    {
        return ( collisionResolveListeners );
    }
    
    public final void update()
    {
        for ( int i = 0; i < collisionResolverList.size(); i++ )
        {
            final CollisionResolver cr = collisionResolverList.get( i );
            cr.update();
        }
    }
    
    public CollisionResolversManager( CollisionEngine collEngine, SurfaceParameters defaultSurfaceParameters )
    {
        this.collEngine = collEngine;
        this.defaultSurfaceParameters = defaultSurfaceParameters;
    }
}
