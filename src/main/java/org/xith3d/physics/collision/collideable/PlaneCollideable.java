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
import org.jagatoo.opengl.enums.TextureFilter;
import org.openmali.FastMath;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.physics.collision.CollideableBase;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;

/**
 * A plane collideable.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class PlaneCollideable extends CollideableBase
{
    private static Appearance defaultAppearance = null;
    
    public static void setDefaultAppearance( Appearance app )
    {
        defaultAppearance = app;
    }
    
    public static void setDefaultAppearance( Texture texture )
    {
        if ( defaultAppearance == null )
        {
            defaultAppearance = new Appearance();
        }
        
        texture.setFilter( TextureFilter.TRILINEAR );
        
        defaultAppearance.setTexture( texture );
    }
    
    public static void setDefaultAppearance( String texture )
    {
        setDefaultAppearance( TextureLoader.getInstance().getTexture( texture ) );
    }
    
    public static Appearance getDefaultAppearance()
    {
        return ( defaultAppearance );
    }
    
    private org.xith3d.scenegraph.primitives.Quad gfxObject;
    
    private Vector3f normal;
    private float d;
    
    public final String getInfo()
    {
        return ( "A plane is like a rectangle that extends infinitely in all directions.." +
                "it is not placeable but has a normal and a point which it should contain" );
    }
    
    public final String getType()
    {
        return ( "Plane" );
    }
    
    public org.xith3d.scenegraph.primitives.Quad getBaseGFX()
    {
        if ( gfxObject == null )
        {
            //final float INF = 65536.0f;
            final float INF = 100.0f;
            
            final org.xith3d.scenegraph.primitives.Rectangle shape;
            
            if ( defaultAppearance != null )
            {
                shape = new org.xith3d.scenegraph.primitives.Rectangle( INF, INF, defaultAppearance.getTexture() );
                shape.setAppearance( defaultAppearance.cloneNodeComponent( false ) );
                shape.setTexturePosition( new Tuple2f( shape.getWidth(), shape.getHeight() ) );
            }
            else
            {
                shape = new org.xith3d.scenegraph.primitives.Rectangle( INF, INF, new Colorf( 1.0f, 0.7f, 0.6f, 0.0f ) );
            }
            
            StaticTransform.translate( shape, 0.0f, 0.0f, this.getD() );
            
            Vector3f n0 = new Vector3f( 0f, 0f, 1f );
            Vector3f normal = getNormal();
            Vector3f axis = new Vector3f();
            axis.cross( n0, normal );
            float angle = n0.angle( normal );
            
            if ( angle > FastMath.PI - 0.001f )
                axis.set( Vector3f.POSITIVE_Y_AXIS );
            else if ( angle < 0.001f )
                axis.set( Vector3f.NEGATIVE_Y_AXIS );
            
            StaticTransform.rotate( shape, axis, angle );
            
            gfxObject = shape;
        }
        
        if ( gfxObject.getAppearance( true ).getPolygonAttributes() != null )
            gfxObject.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.FILL );
        
        return ( gfxObject );
    }
    
    public org.xith3d.scenegraph.primitives.Quad getDebugGFX()
    {
        org.xith3d.scenegraph.primitives.Quad shape = getBaseGFX();
        shape.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.LINE );
        
        return ( shape );
    }
    
    /**
     * Changes the normal of this plane collideable.
     * 
     * @param normal
     */
    public void setNormal( Vector3f normal )
    {
        this.normal = normal;
    }
    
    /**
     *
     * @return the normal of this plane
     */
    public final Vector3f getNormal()
    {
        return ( normal.getReadOnly() );
    }
    
    /**
     * Sets the d-parameter of the plane.
     * 
     * @param d
     */
    public void setD( float d )
    {
        this.d = d;
    }
    
    /**
     * @return the d-parameter of the plane.
     */
    public final float getD()
    {
        return ( d );
    }
    
    /**
     * Creates a new Plane Collideable.
     * 
     * @param eng
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public PlaneCollideable( CollisionEngine eng, float a, float b, float c, float d )
    {
        super( eng );
        
        this.normal = new Vector3f( a, b, c );
        this.d = d;
        
        this.gfxObject = null;
    }
    
    /**
     * Creates a new Plane Collideable.
     * 
     * @param eng
     * @param normal
     * @param d
     */
    public PlaneCollideable( CollisionEngine eng, Vector3f normal, float d )
    {
        this( eng, normal.getX(), normal.getY(), normal.getZ(), d );
    }
    
    /**
     * Creates a new Plane Collideable.
     * 
     * @param eng
     * @param normal
     * @param point
     */
    public PlaneCollideable( CollisionEngine eng, Vector3f normal, Point3f point )
    {
        this( eng, normal, point.getX() * normal.getX() + point.getY() * normal.getY() + point.getZ() * normal.getZ() );
    }
}
