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

import java.util.Arrays;
import java.util.TreeSet;

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.utility.geometry.nvtristrip.PrimitiveGroup;
import org.xith3d.utility.geometry.nvtristrip.TriStrip;

/**
 * This class allows for optimalization of indexed geometry. In most cases it will be filled with 
 * GeometryCreator.fillGeometryInfo, processed and result geometry will be retrieved. Exact way for
 * creating renderer-specific geometry should be implemented in subclasses of this class.
 * 
 * @author YVG
 */
public class GeometryInfo
{
    protected static final int STATE_UNKNOWN = 0;
    protected static final int STATE_SPLIT = 1;
    protected static final int STATE_MERGED = 2;
    
    protected VertexData[] vertices;
    protected int[] triangles;
    protected int[] smoothGroups;
    
    private int vertexCacheSize = TriStrip.CACHESIZE_GEFORCE1_2;
    
    protected int state = STATE_UNKNOWN;
    
    /**
     * Constructs new GeometryInfo
     */
    public GeometryInfo()
    {
        super();
    }
    
    /**
     * This method computes normal for each face in flat mode - every vertex is duplicated for
     * each face and assigned separate normal belonging to this face.
     * As side effect of this method, indices are 'split'. Merging them will probably not do much,
     * as vertices with same coords will have different normals because of flat shading.
     */
    public void recalculateFlatNormals()
    {
        
        unweldVertices();
        
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f faceNormal = new Vector3f();
        
        int numFaces = triangles.length / 3;
        
        // step through all the faces
        for ( int face = 0; face < numFaces; face++ )
        {
            
            Point3f a = vertices[ triangles[ face * 3 ] ].coord;
            Point3f b = vertices[ triangles[ face * 3 + 1 ] ].coord;
            Point3f c = vertices[ triangles[ face * 3 + 2 ] ].coord;
            
            v1.sub( b, a );
            v2.sub( c, a );
            faceNormal.cross( v1, v2 );
            faceNormal.normalize();
            
            vertices[ triangles[ face * 3 ] ].normal = new Vector3f( faceNormal );
            vertices[ triangles[ face * 3 + 1 ] ].normal = new Vector3f( faceNormal );
            vertices[ triangles[ face * 3 + 2 ] ].normal = new Vector3f( faceNormal );
        }
    }
    
    /**
     * This method computes smoothened normals for each vertex. Normal of each face from 
     * same smoothing group is averaged to compute value of vertex. This means that
     * vertex will be duplicated if it belongs to faces belonging to more than one smooth group.
     * After this operation, vertices will be mostly welded - unless there is a case where two neighbour faces
     * from different smoothing group have same normal. 
     */
    public void recalculateSmoothGroupNormals()
    {
        if ( smoothGroups == null )
            throw new IllegalStateException( "Missing smooth group data" );
        
        for ( int i = 0; i < vertices.length; i++ )
        {
            vertices[ i ].normal = null;
        }
        
        unweldVertices();
        
        for ( int i = 0; i < smoothGroups.length; i++ )
        {
            vertices[ triangles[ i * 3 ] ].smoothGroup = smoothGroups[ i ];
            vertices[ triangles[ i * 3 + 1 ] ].smoothGroup = smoothGroups[ i ];
            vertices[ triangles[ i * 3 + 2 ] ].smoothGroup = smoothGroups[ i ];
        }
        weldVertices();
        
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f faceNormal = new Vector3f();
        
        int numFaces = triangles.length / 3;
        
        for ( int i = 0; i < vertices.length; i++ )
        {
            vertices[ i ].normal = new Vector3f();
        }
        
        // step through all the faces
        for ( int face = 0; face < numFaces; face++ )
        {
            
            Point3f a = vertices[ triangles[ face * 3 ] ].coord;
            Point3f b = vertices[ triangles[ face * 3 + 1 ] ].coord;
            Point3f c = vertices[ triangles[ face * 3 + 2 ] ].coord;
            
            v1.sub( b, a );
            v2.sub( c, a );
            faceNormal.cross( v1, v2 );
            
            vertices[ triangles[ face * 3 ] ].normal.add( faceNormal );
            vertices[ triangles[ face * 3 + 1 ] ].normal.add( faceNormal );
            vertices[ triangles[ face * 3 + 2 ] ].normal.add( faceNormal );
        }
        
        for ( int i = 0; i < vertices.length; i++ )
        {
            vertices[ i ].normal.normalize();
            vertices[ i ].smoothGroup = -1;
        }
        
        state = STATE_UNKNOWN;
    }
    
    /**
     * Find vertices with same parameters and merge them into one. Allows sharing of data between faces,
     * which is a requirement for performance benefit from GPU vertex cache.
     * 
     * @see #unweldVertices
     */
    public void weldVertices()
    {
        if ( state == STATE_MERGED )
            return;
        
        TreeSet< VertexData > nverts = new TreeSet< VertexData >();
        for ( int i = 0; i < vertices.length; i++ )
        {
            nverts.add( vertices[ i ] );
        }
        
        VertexData[] ndata = nverts.toArray( new VertexData[ nverts.size() ] );
        int[] nTriangles = new int[ triangles.length ];
        for ( int i = 0; i < nTriangles.length; i++ )
        {
            nTriangles[ i ] = Arrays.binarySearch( ndata, vertices[ triangles[ i ] ] );
        }
        vertices = ndata;
        triangles = nTriangles;
        state = STATE_MERGED;
    }
    
    /**
     * Duplicate vertex data for each face, so it is not shared between them. Allows for allocating
     * per-face normal (flat shading).
     * 
     * @see #weldVertices
     */
    public void unweldVertices()
    {
        if ( state == STATE_SPLIT )
            return;
        VertexData[] ndata = new VertexData[ triangles.length ];
        for ( int i = 0; i < triangles.length; i++ )
        {
            ndata[ i ] = new VertexData( vertices[ triangles[ i ] ] );
            triangles[ i ] = i;
        }
        vertices = ndata;
        state = STATE_SPLIT;
    }
    
    /**
     * This method reorders face indices to fit well into vertex cache of GPU. This operation is meaningful
     * if you plan to use non-strip triangle array later - in other case, use one of strip generation methods,
     * which are cache-aware by default. 
     * Note: this method destroys smoothGroups info and it cannot be recovered, as order of faces is changed.
     * If you want to use smoothing group info for generating normals, you need to do it _before_ you call this
     * method.
     * 
     * @see #setVertexCacheSize
     */
    public void optimizeTrianglesForCache()
    {
        TriStrip ts = new TriStrip();
        ts.setCacheSize( getVertexCacheSize() );
        ts.setListsOnly( true );
        PrimitiveGroup[] pg = ts.generateStrips( triangles );
        
        assert ( pg.length == 1 );
        assert ( pg[ 0 ].type == PrimitiveGroup.PT_LIST );
        smoothGroups = null;
        triangles = pg[ 0 ].getTrimmedIndices();
    }
    
    /**
     * Create continous triangle strip with separate substrips connected by degenerate triangles.
     * Please note that unless vertices are welded before (explicitly by weldVertices or implictly by 
     * recalculateSmoothGroupNormals) stripification will not help with perfomance, as same vertex will
     * have different index for different face. 
     * 
     * @return indices of triangle strip
     */
    public int[] createContinousStrip()
    {
        TriStrip ts = new TriStrip();
        ts.setCacheSize( getVertexCacheSize() );
        ts.setListsOnly( false );
        ts.setMinStripSize( 0 );
        ts.setStitchStrips( true );
        PrimitiveGroup[] pg = ts.generateStrips( triangles );
        
        assert ( pg.length == 1 );
        assert ( pg[ 0 ].type == PrimitiveGroup.PT_STRIP );
        
        return pg[ 0 ].getTrimmedIndices();
    }
    
    /**
     * @return the vertexCacheSize
     */
    public int getVertexCacheSize()
    {
        return vertexCacheSize;
    }
    
    /**
     * This method sets size of vertex cache on gpu for which strips/triangle lists should be optimized.
     * This is the "actual" cache size, so 24 for GeForce3 and 16 for GeForce1/2 You may
     * want to play around with this number to tweak performance. Default value: 16
     * In case of doubt it is better to underestimate size of cache.
     * If you don't care about vertex cache and want strips as long as possible, put very high value here -
     * but be warned, because stripification algorithm is O(n^2*m) [doublecheck - is it true?], where n
     * is number of indices, and m is size of vertexCacheSize. On the other hand, with too small value, you will
     * get too many small strips and cost of degenerate triangles will rise.
     * 
     * @param vertexCacheSize The vertexCacheSize to set.
     */
    public void setVertexCacheSize( int vertexCacheSize )
    {
        this.vertexCacheSize = vertexCacheSize;
    }
}
