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

import org.openmali.spatial.TriangleContainer;
import org.openmali.spatial.polygons.Triangle;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Geometry;

/**
 * The TangentsFactory generates tanget data (tangents and bitangents)
 * for arbitrary meshes.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TangentsFactory
{
    public static enum TangentsStoreMode
    {
        TEXTURE_COORDINATES,
        VERTEX_ATTRIBUTES;
    }
    
    public static final TangentsFactory INSTANCE = new TangentsFactory();
    
    public static TangentsFactory getInstance()
    {
        return ( INSTANCE );
    }
    
    private static final void fixNegativeZero( Vector3f vec )
    {
        if ( vec.getX() == -0f )
            vec.setX( 0f );
        if ( vec.getY() == -0f )
            vec.setY( 0f );
        if ( vec.getZ() == -0f )
            vec.setZ( 0f );
    }
    
    /**
     * Calculates the tangents and bitangents for the given GeomContainer.
     * The GeometryArray must have at least three texture units where the first one (0) must be filled.
     * It also has to have normals.<br>
     * If both of the two array parameters are <code>null</code>,
     * the tanget data is directly applied to the 2nd and 3rd (1, 2) texture units.
     * 
     * @param geom
     * @param storeMode use null to only store in the arrays (index1 and index2 are ignored then)
     * @param storeIndex1
     * @param storeIndex2
     * @param tangents
     * @param bitangents
     */
    public void calculateTangents( Geometry geom, TangentsStoreMode storeMode, int storeIndex1, int storeIndex2, Vector3f[] tangents, Vector3f[] bitangents )
    {
        if ( !geom.hasFeature( Geometry.NORMALS ) )
            throw new IllegalArgumentException( "The GeometryArray must have normals." );
        
        if ( ( tangents == null ) && ( bitangents == null ) && ( storeMode == null ) )
        {
            throw new IllegalArgumentException( "storeMode must not be null, if both arrays are null." );
        }
        
        final int vertexCount = geom.getVertexCount();
        final TriangleContainer triangCont = (TriangleContainer)geom;
        
        Vector3f[] tan1 = new Vector3f[ vertexCount * 2 ];
        Vector3f[] tan2 = new Vector3f[ vertexCount * 2 ];
        
        for ( int i = 0; i < tan1.length; i++ )
        {
            tan1[ i ] = new Vector3f();
            tan2[ i ] = new Vector3f();
        }
        
        final int triangleCount = triangCont.getTriangleCount();
        Triangle triangle = new Triangle();
        
        final float[] w1 = new float[ 4 ];
        final float[] w2 = new float[ 4 ];
        final float[] w3 = new float[ 4 ];
        
        for ( int i = 0; i < triangleCount; i++ )
        {
            triangCont.getTriangle( i, triangle );
            
            int i1 = triangle.getVertexIndexA();
            int i2 = triangle.getVertexIndexB();
            int i3 = triangle.getVertexIndexC();
            
            final Point3f v1 = triangle.getVertexCoordA();
            final Point3f v2 = triangle.getVertexCoordB();
            final Point3f v3 = triangle.getVertexCoordC();
            
            geom.getTextureCoordinate( 0, i1, w1 );
            geom.getTextureCoordinate( 0, i2, w2 );
            geom.getTextureCoordinate( 0, i3, w3 );
            
            float x1 = v2.getX() - v1.getX();
            float x2 = v3.getX() - v1.getX();
            float y1 = v2.getY() - v1.getY();
            float y2 = v3.getY() - v1.getY();
            float z1 = v2.getZ() - v1.getZ();
            float z2 = v3.getZ() - v1.getZ();
            
            float s1 = w2[ 0 ] - w1[ 0 ];
            float s2 = w3[ 0 ] - w1[ 0 ];
            float t1 = w2[ 1 ] - w1[ 1 ];
            float t2 = w3[ 1 ] - w1[ 1 ];
            
            final float r = 1.0f / ( s1 * t2 - s2 * t1 );
            Vector3f sdir = new Vector3f( ( t2 * x1 - t1 * x2 ) * r, ( t2 * y1 - t1 * y2 ) * r, ( t2 * z1 - t1 * z2 ) * r );
            Vector3f tdir = new Vector3f( ( s1 * x2 - s2 * x1 ) * r, ( s1 * y2 - s2 * y1 ) * r, ( s1 * z2 - s2 * z1 ) * r );
            
            tan1[ i1 ].add( sdir );
            tan1[ i2 ].add( sdir );
            tan1[ i3 ].add( sdir );
            
            tan2[ i1 ].add( tdir );
            tan2[ i2 ].add( tdir );
            tan2[ i3 ].add( tdir );
        }
        
        final Vector3f normal = new Vector3f();
        Vector3f tmp = new Vector3f();
        Vector3f tmp2 = new Vector3f();
        Vector3f tangent = new Vector3f();
        Vector3f bitangent = new Vector3f();
        TexCoord3f tex = new TexCoord3f();
        Vector3f attrib = new Vector3f();
        for ( int i = 0; i < vertexCount; i++ )
        {
            geom.getNormal( i, normal );
            final Vector3f t = tan1[ i ];
            
            // Gram-Schmidt orthogonalize
            final float d = normal.dot( t );
            tmp.set( normal );
            tmp.scale( d );
            tmp2.set( t );
            tmp2.sub( tmp );
            tangent.set( tmp2 );
            tangent.normalize();
            
            // Calculate handedness
            //tmp.cross( normal, t );
            //tangent.w = (tmp.dot( tan2[ i ] ) < 0.0f) ? -1.0f : 1.0f;
            
            fixNegativeZero( tangent );
            
            // calculate bitangent
            bitangent.cross( tangent, normal );
            fixNegativeZero( bitangent );
            //System.out.println( tangent );
            //System.out.println( bitangent );
            
            if ( storeMode == TangentsStoreMode.VERTEX_ATTRIBUTES )
            {
                attrib.set( tangent.getX(), tangent.getY(), tangent.getZ() );
                geom.setVertexAttribute( storeIndex1, i, attrib );
                attrib.set( bitangent.getX(), bitangent.getY(), bitangent.getZ() );
                geom.setVertexAttribute( storeIndex2, i, attrib );
            }
            else if ( storeMode == TangentsStoreMode.TEXTURE_COORDINATES )
            {
                tex.set( tangent.getX(), tangent.getY(), tangent.getZ() );
                geom.setTextureCoordinate( storeIndex1, i, tex );
                tex.set( bitangent.getX(), bitangent.getY(), bitangent.getZ() );
                geom.setTextureCoordinate( storeIndex2, i, tex );
            }
            
            if ( tangents != null )
            {
                if ( tangents[ i ] == null )
                    tangents[ i ] = new Vector3f( tangent );
                else
                    tangents[ i ].set( tangent );
            }
            
            if ( bitangents != null )
            {
                if ( bitangents[ i ] == null )
                    bitangents[ i ] = new Vector3f( bitangent );
                else
                    bitangents[ i ].set( bitangent );
            }
        }
    }
    
    /**
     * Calculates the tangents and bitangents for the given GeomContainer.
     * The GeometryArray must have at least three texture units where the first one (0) must be filled.
     * It also has to have normals.<br>
     * 
     * @param geom
     * @param storeMode
     * @param storeIndex1
     * @param storeIndex2
     */
    public void calculateTangents( Geometry geom, TangentsStoreMode storeMode, int storeIndex1, int storeIndex2 )
    {
        if ( storeMode == null )
            throw new IllegalArgumentException( "storeMode must not be null." );
        
        calculateTangents( geom, storeMode, storeIndex1, storeIndex2, null, null );
    }
    
    protected TangentsFactory()
    {
    }
}
