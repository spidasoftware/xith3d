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
package org.xith3d.physics.collision.collideable;

import org.jagatoo.opengl.enums.DrawMode;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.physics.collision.CollideableBase;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.scenegraph.Appearance;

/**
 * A ray collideable.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class RayCollideable extends CollideableBase
{
    private static Appearance defaultAppearance = null;
    
    public static void setDefaultAppearance( Appearance app )
    {
        defaultAppearance = app;
    }
    
    public static void setDefaultAppearance( Colorf color )
    {
        if ( defaultAppearance == null )
        {
            defaultAppearance = new Appearance();
        }
        
        defaultAppearance.setColor( color );
    }
    
    public static Appearance getDefaultAppearance()
    {
        return ( defaultAppearance );
    }
    
    private org.xith3d.scenegraph.primitives.Line gfxObject;
    
    private final Point3f origin;
    private final Vector3f direction;
    private final Point3f end;
    private float length;
    
    /**
     * {@inheritDoc}
     */
    public final String getInfo()
    {
        return ( "A ray collideable. One can adjust its origin and direction" );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getType()
    {
        return ( "Ray" );
    }
    
    /**
     * {@inheritDoc}
     */
    public org.xith3d.scenegraph.primitives.Line getBaseGFX()
    {
        if ( gfxObject == null )
        {
            Point3f end = Point3f.fromPool( 0f, 0f, 1f );
            end.scale( getLength() );
            
            /*
            LineArray lineGeom = new LineArray( 2 );
            lineGeom.setCoordinate( 0, getOrigin() );
            lineGeom.setCoordinate( 1, end );
            
            gfxObject = new Shape3D( lineGeom, defaultAppearance.cloneNodeComponent( false ) );
            */
            
            if ( defaultAppearance == null )
            {
                gfxObject = new org.xith3d.scenegraph.primitives.Line( end, Colorf.RED );
            }
            else
            {
                //gfxObject = new org.xith3d.scenegraph.primitives.Line( end, defaultAppearance.cloneNodeComponent( false ) );
                gfxObject = new org.xith3d.scenegraph.primitives.Line( end, defaultAppearance.getColoringAttributes( true ).getColor() );
            }
            
            Point3f.toPool( end );
        }
        
        if ( gfxObject.getAppearance( true ).getPolygonAttributes() != null )
            gfxObject.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.FILL );
        
        return ( gfxObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public org.xith3d.scenegraph.primitives.Line getDebugGFX()
    {
        org.xith3d.scenegraph.primitives.Line shape = getBaseGFX();
        shape.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.LINE );
        
        return ( shape );
    }
    
    /**
     * Changes the origin of this RayCollideable.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setOrigin( float x, float y, float z )
    {
        if ( gfxObject != null )
        {
            gfxObject.setCoordinates( x, y, z, end.getX(), end.getY(), end.getZ() );
        }
        
        this.origin.set( x, y, z );
    }
    
    /**
     * Changes the origin of this RayCollideable.
     * 
     * @param origin
     */
    public final void setOrigin( Tuple3f origin )
    {
        setOrigin( origin.getX(), origin.getY(), origin.getZ() );
    }
    
    /**
     * @return the origin of this ray.
     */
    public final Point3f getOrigin()
    {
        return ( origin.getReadOnly() );
    }
    
    /**
     * Changes the direction of this RayCollideable.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setDirection( float x, float y, float z )
    {
        if ( gfxObject != null )
        {
            gfxObject.setCoordinates( origin.getX(), origin.getY(), origin.getZ(), x, y, z );
        }
        
        this.direction.set( x, y, z );
        this.length = this.direction.length();
        this.end.add( this.origin, this.direction );
    }
    
    /**
     * Changes the direction of this RayCollideable.
     * 
     * @param direction
     */
    public final void setDirection( Tuple3f direction )
    {
        setDirection( direction.getX(), direction.getY(), direction.getZ() );
    }
    
    /**
     * @return the direction of this ray.
     */
    public final Vector3f getDirection()
    {
        return ( direction.getReadOnly() );
    }
    
    /**
     * @return the end of this ray.
     */
    public final Point3f getEnd()
    {
        return ( end.getReadOnly() );
    }
    
    /**
     * Sets the ray's length.
     * 
     * @param length
     */
    public void setLength( float length )
    {
        direction.normalize();
        direction.scale( length );
        setDirection( direction );
    }
    
    public final float getLength()
    {
        return ( length );
    }
    
    /**
     * Creates a new Box collideable.
     * 
     * @param eng the collision engine we belong to
     * @param originX
     * @param originY
     * @param originZ
     * @param directionX
     * @param directionY
     * @param directionZ
     */
    public RayCollideable( CollisionEngine eng, float originX, float originY, float originZ, float directionX, float directionY, float directionZ )
    {
        super( eng );
        
        this.gfxObject = null;
        
        this.origin = new Point3f( originX, originY, originZ );
        this.direction = new Vector3f( directionX, directionY, directionZ );
        this.end = new Point3f( origin );
        end.add( direction );
        this.length = direction.length();
    }
    
    /**
     * Creates a new Box collideable.
     * 
     * @param eng the collision engine we belong to
     * @param origin
     * @param direction
     */
    public RayCollideable( CollisionEngine eng, Tuple3f origin, Tuple3f direction )
    {
        this( eng, origin.getX(), origin.getY(), origin.getZ(), direction.getX(), direction.getY(), direction.getZ() );
    }
}
