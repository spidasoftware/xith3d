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

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;

/**
 * Information about a collision : position,
 * normal, depth, geoms that collided
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Collision
{
    private static final CollisionPool POOL = new CollisionPool( 128 );
    
    private Collideable c1;
    private Collideable c2;
    
    private Point3f pos;
    private Vector3f normal;
    private float depth;
    
    private final Vector3f outNormal = new Vector3f();
    
    /**
     * The first collideable involved in the collision
     */
    public final Collideable getCollideable1()
    {
        return ( c1 );
    }
    
    /**
     * The second collideable involved in the collision
     */
    public final Collideable getCollideable2()
    {
        return ( c2 );
    }
    
    /**
     * The position of the contact. Of course, more than
     * a point may be in contact (between Collideables), but
     * we need an approximation for computations.
     */
    public final Point3f getPosition()
    {
        return ( pos );
    }
    
    /**
     * The "direction" of this collision. It's a vector normal
     * to the collision.
     * In other terms, it's a vector such that if you move the second
     * collideable by the (normal * depth) vector, it no longer collides
     * with the first collideable.
     */
    public final Vector3f getNormal()
    {
        return ( normal );
    }
    
    /**
     * The "direction" of this collision. It's a vector normal
     * to the collision scaled by the depth of the collision.
     */
    public final Vector3f getScaledNormal()
    {
        outNormal.scale( depth, normal );
        
        return ( outNormal );
    }
    
    /**
     * The "direction" of this collision. It's an inverted vector normal
     * to the collision.
     */
    public final Vector3f getInvertedNormal()
    {
        outNormal.negate( normal );
        
        return ( outNormal );
    }
    
    /**
     * The "direction" of this collision. It's a vector normal
     * to the collision scaled by the depth of the collision and inverted.
     */
    public final Vector3f getScaledInvertedNormal()
    {
        outNormal.scale( -depth, normal );
        
        return ( outNormal );
    }
    
    /**
     * The penetration depth of the collision. If it just grazes,
     * depth = 0.
     */
    public final float getDepth()
    {
        return ( depth );
    }
    
    @Override
    public String toString()
    {
        return ( "Collision between a " + c1.getType() + " and a " + c2.getType() +
                " at position " + pos + " with normal " + normal + " and depth " + depth );
    }
    
    /**
     * Sets this instance's a information.
     * 
     * @param pos contact position
     * @param normal normal vector
     * @param depth penetration depth
     * @param c1 the colliding object n°1
     * @param c2 the colliding object n°2
     */
    protected final void set( Point3f pos, Vector3f normal, float depth,
                              Collideable c1, Collideable c2 )
    {
        if ( pos != null )
            this.pos = pos;
        if ( normal != null )
            this.normal = normal;
        this.depth = depth;
        this.c1 = c1;
        this.c2 = c2;
    }
    
    /**
     * Creates a new collision information object.
     * 
     * @param pos contact position
     * @param normal normal vector
     * @param depth penetration depth
     * @param c1 the colliding object n°1
     * @param c2 the colliding object n°2
     */
    public Collision( Point3f pos, Vector3f normal, float depth,
                      Collideable c1, Collideable c2 )
    {
        super();
        
        this.pos = pos;
        this.normal = normal;
        this.depth = depth;
        this.c1 = c1;
        this.c2 = c2;
    }
    
    /**
     * Creates a new collision information object.
     */
    protected Collision()
    {
        super();
        
        this.pos = new Point3f();
        this.normal = new Vector3f();
        this.depth = 0f;
        this.c1 = null;
        this.c2 = null;
    }
    
    public static final Collision fromPool( Point3f pos, Vector3f normal, float depth,
                                            Collideable c1, Collideable c2 )
    {
        return ( POOL.alloc( pos, normal, depth, c1, c2 ) );
    }
    
    public static final Collision fromPool()
    {
        return ( POOL.alloc() );
    }
    
    public static final void toPool( Collision collision )
    {
        POOL.free( collision );
    }
}
