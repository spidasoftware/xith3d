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
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.physics.collision.CollideableBase;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;

/**
 * A triangle mesh collideable.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class TriMeshCollideable extends CollideableBase
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
    
    private org.xith3d.scenegraph.Shape3D gfxObject;
    
    /**
     * {@inheritDoc}
     */
    public final String getInfo()
    {
        return ( "A triangle mesh collideable. Its shape is defined by a set of coordinates (and optionally indices)" );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getType()
    {
        return ( "TriMesh" );
    }
    
    /**
     * {@inheritDoc}
     */
    public Shape3D getBaseGFX()
    {
        if ( gfxObject == null )
        {
            IndexedTriangleArray geom = new IndexedTriangleArray( getVertexCount(), getIndexCount() );
            geom.setCoordinates( 0, getVerticesFloats() );
            geom.setIndex( getIndices() );
            
            if ( defaultAppearance == null )
            {
                gfxObject = new Shape3D( geom, null );
            }
            else
            {
                gfxObject = new Shape3D( geom, defaultAppearance.cloneNodeComponent( false ) );
            }
        }
        
        if ( gfxObject.getAppearance( true ).getPolygonAttributes() != null )
            gfxObject.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.FILL );
        
        return ( gfxObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public Shape3D getDebugGFX()
    {
        Shape3D shape = getBaseGFX();
        shape.getAppearance().getPolygonAttributes().setDrawMode( DrawMode.LINE );
        
        return ( shape );
    }
    
    /**
     * @return the vertex data with which this TriMesh has been initialized,
     * or null if the init() method hasn't been called yet.
     */
    public abstract float[] getVerticesFloats();
    
    public abstract int getVertexCount();
    
    public abstract void getVertex( int i, Tuple3f coord );
    
    /**
     * @return the vertex data with which this TriMesh has been initialized,
     * or null if the init() method hasn't been called yet.
     * Note that these vertices are just like a "library of vertices" that are
     * referred by indices. If you wanted the list of vertices that are really
     * detected for collision, you need to loop through the indices array and take
     * the referred vertices.
     */
    public abstract Point3f[] getVertices();
    
    /**
     * @return the number of vertices
     */
    public abstract int getIndexCount();
    
    public abstract int getIndex( int i );
    
    /**
     * @return the index data with which this TriMesh has been initialized,
     * or null if the init() method hasn't been called yet.
     */
    public abstract int[] getIndices();
    
    /**
     * Creates a new TriMesh from vertex data. Three points (referenced by their indices)
     * make a triangle.
     * 
     * @param eng
     */
    public TriMeshCollideable( CollisionEngine eng )
    {
        super( eng );
        
        // To avoid lots of casts
        this.gfxObject = null;
    }
}
