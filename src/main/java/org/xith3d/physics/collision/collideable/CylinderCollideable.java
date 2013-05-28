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
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.physics.collision.CollideableBase;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;

/**
 * A cylinder collideable.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class CylinderCollideable extends CollideableBase
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
    
    private org.xith3d.scenegraph.primitives.Cylinder gfxObject;
    
    private float radius;
    private float length;
    
    /**
     * {@inheritDoc}
     */
    public final String getInfo()
    {
        return ( "A cylinder collideable. One can adjust its radius and length" );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getType()
    {
        return ( "Cylinder" );
    }
    
    /**
     * {@inheritDoc}
     */
    public org.xith3d.scenegraph.primitives.Cylinder getBaseGFX()
    {
        if ( gfxObject == null )
        {
            if ( defaultAppearance == null )
            {
                gfxObject = new org.xith3d.scenegraph.primitives.Cylinder( radius, length, 16, 16,
                                                                           Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
            }
            else
            {
                gfxObject = new org.xith3d.scenegraph.primitives.Cylinder( radius, length, 16, 16, defaultAppearance.cloneNodeComponent( false ) );
            }
            
            StaticTransform.rotateX( gfxObject, org.openmali.FastMath.PI_HALF );
        }
        
        if ( gfxObject.getAppearance( true ).getPolygonAttributes() != null )
            gfxObject.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.FILL );
        
        return ( gfxObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public org.xith3d.scenegraph.primitives.Cylinder getDebugGFX()
    {
        org.xith3d.scenegraph.primitives.Cylinder shape = getBaseGFX();
        shape.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.LINE );
        
        return ( shape );
    }
    
    /**
     * Changes the radius of this Cylinder.
     * 
     * @param radius the new radius
     */
    public void setRadius( float radius )
    {
        if ( gfxObject != null )
        {
            final float q = radius / this.radius;
            StaticTransform.scale( gfxObject, q, q, 1.0f );
        }
        
        this.radius = radius;
    }
    
    /**
     * @return the radius of the Cylinder
     */
    public final float getRadius()
    {
        return ( radius );
    }
    
    /**
     * Changes the length of this Cylinder.
     * 
     * @param length the new length
     */
    public void setLength( float length )
    {
        if ( gfxObject != null )
        {
            final float q = length / this.length;
            StaticTransform.scale( gfxObject, 1.0f, 1.0f, q );
        }
        
        this.length = length;
    }
    
    /**
     * @return the length of the Cylinder
     */
    public final float getLength()
    {
        return ( length );
    }
    
    /**
     * Creates a new CylinderCollideable.
     * 
     * @param eng
     * @param radius
     * @param length
     */
    public CylinderCollideable( CollisionEngine eng, float radius, float length )
    {
        super( eng );
        
        this.gfxObject = null;
        
        this.radius = radius;
        this.length = length;
    }
}
