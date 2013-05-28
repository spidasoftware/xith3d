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
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.physics.collision.CollideableBase;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;

/**
 * A box collideable.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class BoxCollideable extends CollideableBase
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
    
    private org.xith3d.scenegraph.primitives.Box gfxObject;
    
    private final Tuple3f size;
    
    /**
     * {@inheritDoc}
     */
    public final String getInfo()
    {
        return ( "A box collideable. One can adjust its x,y,z size" );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getType()
    {
        return ( "Box" );
    }
    
    /**
     * {@inheritDoc}
     */
    public org.xith3d.scenegraph.primitives.Box getBaseGFX()
    {
        if ( gfxObject == null )
        {
            if ( defaultAppearance == null )
            {
                gfxObject = new org.xith3d.scenegraph.primitives.Box( size.getX(), size.getY(), size.getZ(), Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
            }
            else
            {
                gfxObject = new org.xith3d.scenegraph.primitives.Box( size.getX(), size.getY(), size.getZ(), defaultAppearance.cloneNodeComponent( false ) );
            }
        }
        
        if ( gfxObject.getAppearance( true ).getPolygonAttributes() != null )
            gfxObject.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.FILL );
        
        return ( gfxObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public org.xith3d.scenegraph.primitives.Box getDebugGFX()
    {
        org.xith3d.scenegraph.primitives.Box shape = getBaseGFX();
        shape.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.LINE );
        
        return ( shape );
    }
    
    /**
     * Changes the size of this Box collideable.
     * 
     * @param width
     * @param height
     * @param depth
     */
    public void setSize( float width, float height, float depth )
    {
        if ( gfxObject != null )
        {
            final float qx = width / this.size.getX();
            final float qy = height / this.size.getY();
            final float qz = depth / this.size.getZ();
            
            StaticTransform.scale( gfxObject, qx, qy, qz );
        }
        
        this.size.set( width, height, depth );
    }
    
    /**
     * Changes the size of this Box collideable.
     * 
     * @param size
     */
    public final void setSize( Tuple3f size )
    {
        setSize( size.getX(), size.getY(), size.getZ() );
    }
    
    /**
     * @return the size of this box along the three axis : X, Y, Z
     */
    public final Tuple3f getSize()
    {
        return ( size.getReadOnly() );
    }
    
    /**
     * Creates a new Box collideable.
     * 
     * @param eng the collision engine we belong to
     * @param xSize
     * @param ySize
     * @param zSize
     */
    public BoxCollideable( CollisionEngine eng, float xSize, float ySize, float zSize )
    {
        super( eng );
        
        this.gfxObject = null;
        
        this.size = new Tuple3f( xSize, ySize, zSize );
    }
    
    /**
     * Creates a new Box collideable.
     * 
     * @param eng the collision engine we belong to
     * @param size
     */
    public BoxCollideable( CollisionEngine eng, Tuple3f size )
    {
        this( eng, size.getX(), size.getY(), size.getZ() );
    }
}
