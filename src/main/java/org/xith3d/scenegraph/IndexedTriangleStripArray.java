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
package org.xith3d.scenegraph;

import org.jagatoo.opengl.enums.GeometryArrayType;
import org.openmali.spatial.WriteableTriangleContainer;
import org.openmali.spatial.polygons.Triangle;

/**
 * @author David J. Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class IndexedTriangleStripArray extends IndexedGeometryStripArray implements WriteableTriangleContainer
{
    private final int triangleCount;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isTriangulatable()
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isTriangulated()
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int getTriangleCount()
    {
        return ( triangleCount );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setTriangle( int i, Triangle triangle )
    {
        final int[] stripLengths = getStripVertexCounts();
        
        int offset = 0;
        for ( int j = 0; j < stripLengths.length; j++ )
        {
            final int stripTriangCount = stripLengths[ j ] - 2;
            if ( i < offset + stripTriangCount )
            {
                final int[] index = getIndex();
                
                final int index_index0 = ( j * 2 ) + i;
                
                final int idx0 = index[ index_index0 + 0 ];
                final int idx1 = index[ index_index0 + 1 ];
                final int idx2 = index[ index_index0 + 2 ];
                
                return ( setTriangle( idx0, idx1, idx2, triangle ) );
            }
            
            offset += stripTriangCount;
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean getTriangle( int i, Triangle triangle )
    {
        final int[] stripLengths = getStripVertexCounts();
        
        int offset = 0;
        for ( int j = 0; j < stripLengths.length; j++ )
        {
            final int stripTriangCount = stripLengths[ j ] - 2;
            if ( i < offset + stripTriangCount )
            {
                final int[] index = getIndex();
                
                final int index_index0 = i;
                
                final int idx0;
                final int idx1;
                final int idx2;
                if ( ( i % 2 ) == 0 )
                {
                    idx0 = index[ index_index0 + 0 ];
                    idx1 = index[ index_index0 + 1 ];
                    idx2 = index[ index_index0 + 2 ];
                }
                else
                {
                    idx0 = index[ index_index0 + 1 ];
                    idx1 = index[ index_index0 + 0 ];
                    idx2 = index[ index_index0 + 2 ];
                }
                
                return ( getTriangle( idx0, idx1, idx2, triangle ) );
            }
            
            offset += stripTriangCount;
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexedTriangleStripArray cloneNodeComponent( boolean forceDuplicate )
    {
        IndexedTriangleStripArray itsa = new IndexedTriangleStripArray( this.getCoordinatesSize(), this.getVertexCount(), this.getIndexCount(), this.getStripVertexCounts() );
        
        itsa.duplicateNodeComponent( this, forceDuplicate );
        
        return ( itsa );
    }
    
    private int computeTriangleCount( int[] stripIndexCounts )
    {
        if ( stripIndexCounts != null )
        {
            int triangleCount = 0;
            
            for ( int i = 0; i < stripIndexCounts.length; i++ )
            {
                triangleCount += stripIndexCounts[ i ] - 2;
            }
            
            return ( triangleCount );
        }
        
        return ( getValidVertexCount() );
    }
    
    /**
     * Constructs an empty IndexedTriangleStripArray object with the specified
     * number of vertices, vertex format, and number of indices.
     */
    public IndexedTriangleStripArray( int coordsSize, int vertexCount, int indexCount, int[] stripIndexCounts )
    {
        super( GeometryArrayType.TRIANGLE_STRIP, coordsSize, vertexCount, indexCount, stripIndexCounts );
        
        this.triangleCount = computeTriangleCount( stripIndexCounts );
    }
    
    /**
     * Constructs an empty IndexedTriangleStripArray object with the specified
     * number of vertices, vertex format, and number of indices.
     */
    public IndexedTriangleStripArray( int vertexCount, int indexCount, int[] stripIndexCounts )
    {
        this( 3, vertexCount, indexCount, stripIndexCounts );
    }
    
    public IndexedTriangleStripArray( int coordsSize, int vertexCount, int indexCount )
    {
        this( coordsSize, vertexCount, indexCount, new int[] { indexCount } );
    }
    
    public IndexedTriangleStripArray( int vertexCount, int indexCount )
    {
        this( 3, vertexCount, indexCount, new int[] { indexCount } );
    }
}
