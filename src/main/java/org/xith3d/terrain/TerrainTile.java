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
package org.xith3d.terrain;

import static java.lang.Math.abs;

import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.AbstractLODShape3D;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * @author Mathias 'cylab' Henze
 * @since 1.0
 */
public class TerrainTile extends GroupNode
{
    private GridTriangulator triangulator;
    private GridSampler sampler;
    private GridSurface shader;
    private Appearance appearance;
    private int surfaceDetail;
    //private IndexedTriangleArray geometry;
    private IndexedTriangleStripArray geometry;
    
    public TerrainTile( GridSampler sampler,GridSurface surface, GridTriangulator triangulator ,int surfaceDetail  )
    {
        float x1 = triangulator.getX1();
        float z1 = triangulator.getZ1();
        float x2 = triangulator.getX2();
        float z2 = triangulator.getZ2();
        float s1 = triangulator.getS1();
        float t1 = triangulator.getT1();
        float s2 = triangulator.getS2();
        float t2 = triangulator.getT2();
        this.triangulator = triangulator;
        this.sampler = sampler;
        this.shader = surface;
        this.surfaceDetail = surfaceDetail;
        this.appearance = surface.getAppearance();
        
        //System.out.println("Number of vertices: " + triangulator.getSize() * triangulator.getSize());
        Tuple3f[] coordinates = triangulator.getCoordinates();
        //geometry = new IndexedTriangleArray(coordinates.length, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2, index.length);
        int[] index = triangulator.getIndex();
        
        int units = surface.getTextureUnits();
        
        geometry = new IndexedTriangleStripArray( coordinates.length, index.length );
        geometry.setCoordinates( 0, coordinates );
        geometry.setIndex( index );
        geometry.setValidIndexCount( index.length );
        geometry.setNormals(0, triangulator.getNormals() );
        
        for ( int u = 0; u < units; u++ )
        {
            TexCoord2f[] texCoordinates = new TexCoord2f[ coordinates.length ];
            for ( int i = 0; i < coordinates.length; i++ )
            {
                texCoordinates[ i ] = surface.map( s1 + ( coordinates[ i ].getX() - x1 ) / abs( x2 - x1 ) * abs( s2 - s1 ), t1 + ( coordinates[ i ].getZ() - z1 ) / abs( z2 - z1 ) * abs( t2 - t1 ), u  );
            }
            geometry.setTextureCoordinates( u, 0, texCoordinates );
        }
        
        addChild( new TerrainShape() );
    }
    
    @Override
    protected GroupNode newInstance()
    {
        return new TerrainTile( sampler, shader, triangulator ,surfaceDetail );
    }
    
    @Override
    public boolean traverse( DetailedTraversalCallback callback )
    {
        if ( !callback.traversalOperationCommon( this ) )
            return ( false );
        if ( !callback.traversalOperation( this ) )
            return ( false );
        
        if ( callback.traversalCheckGroupCommon( this ) )
        {
            final int num = numChildren();
            for ( int i = 0; i < num; i++ )
            {
                if ( !getChild( i ).traverse( callback ) )
                    return ( false );
            }
        }
        
        return ( callback.traversalOperationCommonAfter( this ) && callback.traversalOperationAfter( this ) );
    }
    
    private class TerrainShape extends AbstractLODShape3D
    {
        int[][] lods = new int[ 8 ][ 0 ];
        
        public TerrainShape()
        {
            super( geometry, appearance );
            
            float tolerance = triangulator.getBaseTolerance();
            // TODO cylab 2007-MAR-11: make this dependent of the view distance
            float size = triangulator.getX2() - triangulator.getX1();
            float step = size/4;
            float normRange= triangulator.getS2() - triangulator.getS1();
            for ( int i = 0; i < 8; i++ )
            {
                lods[ i ] = i == 0 ? triangulator.getIndex() : triangulator.getIndex( tolerance + normRange * i );
                addLOD( size + step * i, i == 7 ? 1e20f : size+step * ( i + 1 ) );
            }
        }
        
        @Override
        protected void onLODChanged( int oldLOD, int newLOD, String name )
        {
            //if( newLOD < 2 )
            //{
            //    System.out.println("LOD-Level: "+newLOD+" indicies: "+lods[newLOD].length);
            //}
            geometry.setIndex( lods[ newLOD ] );
            geometry.setValidIndexCount( lods[ newLOD ].length );
            geometry.setStripVertexCounts( new int[]
            {
                lods[ newLOD ].length
            } );
        }
        
        @Override
        protected TerrainShape newInstance()
        {
            boolean gib = Node.globalIgnoreBounds;
            Node.globalIgnoreBounds = this.isIgnoreBounds();
            TerrainShape newShape = new TerrainShape();
            Node.globalIgnoreBounds = gib;
            
            return ( newShape );
        }
        
        @Override
        public boolean traverse( DetailedTraversalCallback callback )
        {
            return super.traverse( callback ); //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
