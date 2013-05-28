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
import org.openmali.spatial.LineContainer;
import org.openmali.vecmath2.Tuple3f;

/**
 * @author David J. Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class IndexedLineStripArray extends IndexedGeometryStripArray implements LineContainer
{
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isTriangulatable()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isTriangulated()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getLinesCount()
    {
        int lc = 0;
        for ( int i = 0; i < getNumStrips(); i++ )
        {
            lc += getStripVertexCounts()[i];
            //lc += getStripIndexCounts()[i];
        }
        
        return ( lc );
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean getLineCoordinates( int i, Tuple3f start, Tuple3f end )
    {
        //if ( n * 2 >= getIndexCount() - 1 )
        //    return ( false );
        
        final int[] stripLengths = getStripVertexCounts();
        
        int offset = 0;
        for ( int j = 0; j < stripLengths.length; j++ )
        {
            final int stripLineCount = stripLengths[j] - 1;
            
            if ( i < offset + stripLineCount )
            {
                final int idx0 = i + ( j * 1 );
                
                getCoordinate( getIndex( idx0 ), start );
                getCoordinate( getIndex( idx0 + 1 ), end );
                
                return ( true );
            }
            
            offset += stripLineCount;
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
    public IndexedLineStripArray cloneNodeComponent( boolean forceDuplicate )
    {
        IndexedLineStripArray ilsa = new IndexedLineStripArray( this.getCoordinatesSize(), this.getVertexCount(), this.getIndexCount(), this.getStripVertexCounts() );
        
        ilsa.duplicateNodeComponent( this, forceDuplicate );
        
        return ( ilsa );
    }
    
    /**
     * Constructs an empty IndexedTriangleStripArray object with the specified
     * number of vertices, vertex format, and number of indices.
     */
    public IndexedLineStripArray( int coordsSize, int vertexCount, int indexCount, int[] stripIndexCounts )
    {
        super( GeometryArrayType.LINE_STRIP, coordsSize, vertexCount, indexCount, stripIndexCounts );
    }
    
    /**
     * Constructs an empty IndexedTriangleStripArray object with the specified
     * number of vertices, vertex format, and number of indices.
     */
    public IndexedLineStripArray( int vertexCount, int indexCount, int[] stripIndexCounts )
    {
        this( 3, vertexCount, indexCount, stripIndexCounts );
    }
    
    public IndexedLineStripArray( int coordsSize, int vertexCount, int indexCount )
    {
        this( coordsSize, vertexCount, indexCount, new int[] { indexCount } );
    }
    
    public IndexedLineStripArray( int vertexCount, int indexCount )
    {
        this( vertexCount, indexCount, new int[] { indexCount } );
    }
}
