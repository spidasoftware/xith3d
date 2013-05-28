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
package org.xith3d.input.modules;

import java.util.ArrayList;

import org.openmali.vecmath2.Point3f;
import org.xith3d.physics.collision.Collideable;
import org.xith3d.physics.collision.CollideableGroup;
import org.xith3d.physics.collision.Collision;
import org.xith3d.physics.collision.CollisionEngine;

/**
 * This ColliderCheckCallback make the avatar slide at walls.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class SlidingColliderCheckCallback implements ColliderCheckCallback
{
    private final CollisionEngine collEngine;
    private       CollideableGroup collGroup;
    
    private final ArrayList< Collision > collisions = new ArrayList< Collision >();
    private final ArrayList< Collideable > walls = new ArrayList< Collideable >();
    private final ArrayList< Collision > collisions2 = new ArrayList< Collision >();
    
    public final void setCollideableGroup( CollideableGroup collGroup )
    {
        this.collGroup = collGroup;
    }
    
    public final CollideableGroup getCollideableGroup()
    {
        return ( collGroup );
    }
    
    private static final void resolveCollision( final Collision collision, Collideable avatarCollider )
    {
        Point3f pos = Point3f.fromPool();
        
        avatarCollider.getPosition( pos );
        pos.add( collision.getScaledNormal() );
        avatarCollider.setPosition( pos );
        
        Point3f.toPool( pos );
    }
    
    public boolean checkCollision( final Collideable avatarCollider )
    {
        if ( ( avatarCollider == null ) || ( collGroup == null ) || !collEngine.isEnabled() )
        {
            return ( false );
        }
        
        collEngine.checkCollisions( avatarCollider, collGroup, false, collisions );
        
        if ( collisions.size() == 0 )
        {
            return ( false );
        }
        
        resolveCollision( collisions.get( 0 ), avatarCollider );
        
        if ( collisions.size() == 1 )
        {
            return ( true );
        }
        
        walls.clear();
        for ( int i = 1; i < collisions.size(); i++ )
        {
            walls.add( collisions.get( i ).getCollideable2() );
        }
        
        for ( int i = 0; i < walls.size(); i++ )
        {
            collEngine.checkCollisions( avatarCollider, walls.get( i ), false, collisions2 );
            
            if ( collisions2.size() > 0 )
            {
                resolveCollision( collisions2.get( 0 ), avatarCollider );
            }
        }
        
        return ( true );
    }
    
    public SlidingColliderCheckCallback( CollisionEngine collEngine, CollideableGroup collGroup )
    {
        this.collEngine = collEngine;
        this.collGroup = collGroup;
    }
}
