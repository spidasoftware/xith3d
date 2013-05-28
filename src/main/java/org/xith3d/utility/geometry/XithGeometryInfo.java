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
package org.xith3d.utility.geometry;

import org.xith3d.utility.geometry.nvtristrip.PrimitiveGroup;
import org.xith3d.utility.geometry.nvtristrip.TriStrip;

import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.TriangleArray;

/**
 * Subclass of GeometryInfo which support creation of xith3d specific geometry types. For methods
 * concerning modification/optimalization of geometry see GeometryInfo
 * @see Geometry
 * 
 * @author YVG
 */
public class XithGeometryInfo extends GeometryInfo
{
    /**
     * Creates new empty XithGeometryInfo
     *
     */
    public XithGeometryInfo()
    {
        super();
    }
    
    private void fillData( Geometry geom, int vertexFormat )
    {
        int count = vertices.length;
        if ( ( vertexFormat & Geometry.COORDINATES ) != 0 )
        {
            for ( int i = 0; i < count; i++ )
            {
                geom.setCoordinate( i, vertices[ i ].coord );
            }
        }
        
        if ( ( vertexFormat & Geometry.NORMALS ) != 0 )
        {
            for ( int i = 0; i < count; i++ )
            {
                geom.setNormal( i, vertices[ i ].normal );
            }
        }
        
        if ( ( vertexFormat & Geometry.COLORS ) != 0 )
        {
            for ( int i = 0; i < count; i++ )
            {
                geom.setColor( i, vertices[ i ].color );
            }
        }
        
        if ( ( vertexFormat & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            for ( int i = 0; i < count; i++ )
            {
                for ( int tc = 0; tc < vertices[ i ].texCoords.length; tc++ )
                {
                    geom.setTextureCoordinate( tc, i, vertices[ i ].texCoords[ tc ] );
                }
            }
        }
    }
    
    /**
     * Create non-indexed, vertex-based triangle array with geometry contained in this object.
     * 
     * @param vertexFormat
     * 
     * @return the created TriangleArray
     */
    public TriangleArray createRawTriangleArray( int vertexFormat )
    {
        unweldVertices();
        TriangleArray ta = new TriangleArray( vertices.length );
        fillData( ta, vertexFormat );
        
        return ( ta );
    }
    
    /**
     * Create indexed triangle array with geometry contained in this object. It is best to call 
     * weldVertices (if needed) and then optimizeTrianglesForCache before calling this method.
     * 
     * @param vertexFormat
     * 
     * @return the created IndexedTriangleArray
     */
    public IndexedTriangleArray createIndexedTriangleArray( int vertexFormat )
    {
        IndexedTriangleArray ita = new IndexedTriangleArray( vertices.length, triangles.length );
        fillData( ita, vertexFormat );
        ita.setIndex( triangles );
        ita.setValidIndexCount( triangles.length );
        
        return ( ita );
    }
    
    /**
     * Create indexed triangle strip array with geometry contained in this object. It is best to call 
     * weldVertices (if needed) before calling this method. Strip array will have one long strip,
     * with substrips connected by degenerate triangles. It uses createContinousStrip method.
     * 
     * @param vertexFormat
     * 
     * @return the created IndexedTriangleStripArray
     * 
     * @see GeometryInfo#createContinousStrip
     */
    public IndexedTriangleStripArray createContinousStripArray( int vertexFormat )
    {
        int[] flow = createContinousStrip();
        IndexedTriangleStripArray itsa = new IndexedTriangleStripArray( vertices.length, flow.length );
        fillData( itsa, vertexFormat );
        itsa.setValidIndexCount( flow.length );
        itsa.setIndex( flow );
        
        return ( itsa );
    }
    
    /**
     * Create indexed triangle strip array with geometry contained in this object. It is best to call 
     * weldVertices (if needed) before calling this method. Strip array will have multiple short strips, 
     * possibly even single-triangle, so it is not very fast solution. It is better to create continous strip
     * array or mixed array (when it will be implemented).
     * 
     * @param vertexFormat
     * 
     * @return the created IndexedTriangleStripArray
     * 
     * @see GeometryInfo#createContinousStrip
     */
    public IndexedTriangleStripArray createChunkedStripArray( int vertexFormat )
    {
        TriStrip ts = new TriStrip();
        ts.setCacheSize( getVertexCacheSize() );
        ts.setListsOnly( false );
        ts.setMinStripSize( 0 );
        ts.setStitchStrips( false );
        PrimitiveGroup[] pg = ts.generateStrips( triangles );
        
        int[] strips = new int[ pg.length ];
        int totalCount = 0;
        for ( int i = 0; i < strips.length; i++ )
        {
            assert ( pg[ i ].type == PrimitiveGroup.PT_STRIP );
            strips[ i ] = pg[ i ].numIndices;
            totalCount += pg[ i ].numIndices;
        }
        
        int[] flow = new int[ totalCount ];
        int current = 0;
        for ( int i = 0; i < strips.length; i++ )
        {
            System.arraycopy( pg[ i ].indices, 0, flow, current, pg[ i ].numIndices );
            current += pg[ i ].numIndices;
        }
        
        assert ( current == totalCount );
        
        IndexedTriangleStripArray itsa = new IndexedTriangleStripArray( vertices.length, triangles.length, strips );
        fillData( itsa, vertexFormat );
        itsa.setIndex( flow );
        itsa.setValidIndexCount( triangles.length );
        
        return ( itsa );
    }
}
