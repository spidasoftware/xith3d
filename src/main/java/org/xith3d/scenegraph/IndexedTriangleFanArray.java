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
 * (Indexed)TriangleFanArray defines a triangle array, where the first vertex
 * is part of any triangle. The second and thirtvertex form the first triangle
 * (together with the first vertex) and the fourth and fifth triangle form the
 * second triangle (together with the first vertex) and so forth.<br>
 * <br>
 * In the Indexed version the first vertex is the one referenced by the number
 * in the first element of the index array.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class IndexedTriangleFanArray extends IndexedGeometryArray implements WriteableTriangleContainer
{
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
        return ( getValidIndexCount() - 2 );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setTriangle( int i, Triangle triangle )
    {
        final int[] index = getIndex();
        
        final int idx0 = index[ 0 ];
        final int idx1 = index[ i + 1 ];
        final int idx2 = index[ i + 2 ];
        
        return ( setTriangle( idx0, idx1, idx2, triangle ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean getTriangle( int i, Triangle triangle )
    {
        final int[] index = getIndex();
        
        final int idx0 = index[ 0 ];
        final int idx1 = index[ i + 1 ];
        final int idx2 = index[ i + 2 ];
        
        return ( getTriangle( idx0, idx1, idx2, triangle ) );
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
    public IndexedTriangleFanArray cloneNodeComponent( boolean forceDuplicate )
    {
        // TODO: Check the null!
        IndexedTriangleFanArray itfa = new IndexedTriangleFanArray( this.getCoordinatesSize(), this.getVertexCount(), this.getIndexCount(), null );
        
        itfa.duplicateNodeComponent( this, forceDuplicate );
        
        return ( itfa );
    }
    
    /**
     * Constructs an empty IndexedTriangleArray object with the specified
     * number of vertices, vertex format, and number of indices.
     */
    // TODO: Why isn't stripIndexCounts used?
    public IndexedTriangleFanArray( int coordsSize, int vertexCount, int indexCount, int[] stripIndexCounts )
    {
        super( GeometryArrayType.TRIANGLE_FAN, coordsSize, vertexCount, null, indexCount );
    }
    
    /**
     * Constructs an empty IndexedTriangleArray object with the specified
     * number of vertices, vertex format, and number of indices.
     */
    public IndexedTriangleFanArray( int vertexCount, int indexCount, int[] stripIndexCounts )
    {
        this( 3, vertexCount, indexCount, stripIndexCounts );
    }
}
