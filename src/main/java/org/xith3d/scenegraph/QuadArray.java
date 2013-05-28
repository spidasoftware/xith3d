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
 * A QuadArray defines an array of vertices rendered as quads.<br>
 * The vertex order is like this:<br>
 * <br>
 * 3---2<br>
 * |   |<br>
 * |   |<br>
 * 0---1<br>
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class QuadArray extends Geometry implements WriteableTriangleContainer
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
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public int getTriangleCount()
    {
        return ( getValidVertexCount() / 4 * 2 );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setTriangle( int i, Triangle triangle )
    {
        final int idx0;
        
        if ( i % 2 == 0 )
            idx0 = ( ( i / 2 ) * 4 ) + 0;
        else
            idx0 = ( ( i / 2 ) * 4 ) + 1;
        
        return ( setTriangle( idx0 + 0, idx0 + 1, idx0 + 2, triangle ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean getTriangle( int i, Triangle triangle )
    {
        final int idx0;
        
        if ( i % 2 == 0 )
            idx0 = ( ( i / 2 ) * 4 ) + 0;
        else
            idx0 = ( ( i / 2 ) * 4 ) + 1;
        
        return ( getTriangle( idx0 + 0, idx0 + 1, idx0 + 2, triangle ) );
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
    public QuadArray cloneNodeComponent( boolean forceDuplicate )
    {
        QuadArray qa = new QuadArray( this.getCoordinatesSize(), this.getVertexCount() );
        
        qa.duplicateNodeComponent( this, forceDuplicate );
        
        return ( qa );
    }
    
    /**
     * Constructs an empty QuadArray object with the specified number of vertices, and vertex format.
     *
     * @param vertexCount
     */
    public QuadArray( int coordsSize, int vertexCount )
    {
        super( GeometryArrayType.QUADS, false, coordsSize, vertexCount, null, 0 );
    }
    
    /**
     * Constructs an empty QuadArray object with the specified number of vertices, and vertex format.
     *
     * @param vertexCount
     */
    public QuadArray( int vertexCount )
    {
        this( 3, vertexCount );
    }
}
