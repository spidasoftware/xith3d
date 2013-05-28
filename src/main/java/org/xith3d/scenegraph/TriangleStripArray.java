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
 * TriangleStripArray defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class TriangleStripArray extends GeometryStripArray implements WriteableTriangleContainer
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
                final int idx0 = i + ( j * 2 );
                final int idx1 = idx0 + 1;
                final int idx2 = idx0 + 2;
                
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
                final int idx0 = i + ( j * 2 );
                final int idx1 = idx0 + 1;
                final int idx2 = idx0 + 2;
                
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
    public TriangleStripArray cloneNodeComponent( boolean forceDuplicate )
    {
        TriangleStripArray tsa = new TriangleStripArray( this.getCoordinatesSize(), this.getVertexCount(), this.getStripVertexCounts() );
        
        tsa.duplicateNodeComponent( this, forceDuplicate );
        
        return ( tsa );
    }
    
    private int computeTriangleCount( int[] stripVertexCounts )
    {
        if ( stripVertexCounts != null )
        {
            int triangleCount = 0;
            
            for ( int i = 0; i < stripVertexCounts.length; i++ )
            {
                triangleCount += stripVertexCounts[ i ] - 2;
            }
            
            return ( triangleCount );
        }
        
        return ( getValidVertexCount() );
    }
    
    /**
     * Constructs a new TriangleStripArray object with the
     * specified number of vertices and the specified
     * format.
     */
    public TriangleStripArray( int coordsSize, int vertexCount, int[] stripVertexCounts )
    {
        super( GeometryArrayType.TRIANGLE_STRIP, coordsSize, vertexCount, stripVertexCounts );
        
        this.triangleCount = computeTriangleCount( stripVertexCounts );
    }
    
    /**
     * Constructs a new TriangleStripArray object with the
     * specified number of vertices and the specified
     * format.
     */
    public TriangleStripArray( int vertexCount, int[] stripVertexCounts )
    {
        this( 3, vertexCount, stripVertexCounts );
    }
    
    public TriangleStripArray( int coordsSize, int vertexCount )
    {
        this( coordsSize, vertexCount, new int[] { vertexCount } );
    }
    
    public TriangleStripArray( int vertexCount )
    {
        this( 3, vertexCount, new int[] { vertexCount } );
    }
}
