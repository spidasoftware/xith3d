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

import java.util.ArrayList;
import java.util.List;

import org.jagatoo.util.arrays.ArrayUtils;
import org.openmali.spatial.TriangleContainer;
import org.openmali.spatial.VertexContainer;
import org.openmali.spatial.polygons.Triangle;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedGeometryArray;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TriangleArray;

/**
 * This class provides static utility methods for Geometries.
 * 
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public class GeometryUtils
{
    /**
     * Retrieves the vertex coordinates from a VertexContainer.
     * 
     * @param vc the source Geometry
     * @param coords the coords array (must be of correct length)
     */
    public static final void getVertexCoords( VertexContainer vc, Tuple3f[] coords )
    {
        final int n = vc.getVertexCount();
        
        for ( int i = 0; i < n; i++ )
        {
            if ( coords[ i ] == null )
                coords[ i ] = new Point3f();
            
            vc.getVertex( i, coords[ i ] );
        }
    }
    
    /**
     * Retrieves the vertex coordinates from a VertexContainer.
     * 
     * @param vc the source Geometry
     * 
     * @return the Point3f array of vertex coords
     */
    public static final Point3f[] getVertexCoords( VertexContainer vc )
    {
        final Point3f[] coords = new Point3f[ vc.getVertexCount() ];
        
        getVertexCoords( vc, coords );
        
        return ( coords );
    }
    
    /**
     * Retrieves the vertices from a Shape3D.
     * 
     * @param shape the source Shape3D
     * 
     * @return the Point3f array of vertex coords
     */
    public static final Point3f[] getVertexCoords( Shape3D shape )
    {
        if ( shape.getGeometry() == null )
            return ( null );
        
        return ( getVertexCoords( shape.getGeometry() ) );
    }
    
    /**
     * Retrieves the normals from a Geometry.
     * 
     * @param geom the source Geometry
     * @param normals the Vector3f array to store normals in
     */
    public static final void getNormals( Geometry geom, Vector3f[] normals )
    {
        final int n = geom.getVertexCount();
        
        for ( int i = 0; i < n; i++ )
        {
            if ( normals[ i ] == null )
                normals[ i ] = new Vector3f();
            
            geom.getNormal( i, normals[ i ] );
        }
    }
    
    /**
     * Retrieves the normals from a Geometry.
     * 
     * @param geom the source Geometry
     * @return the Vector3f array of normals
     */
    public static final Vector3f[] getNormals( Geometry geom )
    {
        final Vector3f[] normals = new Vector3f[ geom.getVertexCount() ];
        
        getNormals( geom, normals );
        
        return ( normals );
    }
    
    /**
     * Retrieves the normals from a Shape3D.
     * 
     * @param shape the source Shape3D
     * 
     * @return the Vector3f array of normals
     */
    public static final Vector3f[] getNormals( Shape3D shape )
    {
        if ( shape.getGeometry() == null )
            return ( null );
        
        return ( getNormals( shape.getGeometry() ) );
    }
    
    /**
     * Merges multiple Geometries into one big TriangleArray.
     * 
     * @param geoms
     * 
     * @return the new TriangleArray.
     */
    public static TriangleArray mergeGeometriesTA( Geometry... geoms )
    {
        if ( geoms.length == 0 )
            return ( null );
        
        int numTriangles = 0;
        int features = ~0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            TriangleContainer tc = (TriangleContainer)geoms[i];
            numTriangles += tc.getTriangleCount();
            
            features &= geoms[i].getVertexFormat();
        }
        
        int numVertices = numTriangles * 3;
        
        TriangleArray targetTA = new TriangleArray( numVertices );
        
        Triangle triangle = new Triangle( features, 2 );
        
        int offset = 0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            TriangleContainer tc = (TriangleContainer)geoms[i];
            numTriangles = tc.getTriangleCount();
            
            for ( int t = 0; t < numTriangles; t++ )
            {
                tc.getTriangle( t, triangle );
                targetTA.setTriangle( offset + t, triangle );
            }
            
            offset += numTriangles;
        }
        
        return ( targetTA );
    }
    
    private static int findVertex( Point3f[] coords, Vector3f[] normals, Colorf[] colors, TexCoord2f[] texCoords, int searchStart, int searchEnd, int sampleIndex )
    {
        int index = ArrayUtils.indexOf( coords, searchStart, searchEnd, coords[ sampleIndex ], false );
        
        if ( index < 0 )
            return ( index );
        
        if ( normals != null )
        {
            if ( !normals[index].equals( normals[sampleIndex] ) )
            {
                if ( index < searchEnd )
                    return ( findVertex( coords, normals, colors, texCoords, index + 1, searchEnd, sampleIndex ) );
                
                return ( -1 );
            }
        }
        
        if ( colors != null )
        {
            if ( !colors[index].equals( colors[sampleIndex] ) )
            {
                if ( index < searchEnd )
                    return ( findVertex( coords, normals, colors, texCoords, index + 1, searchEnd, sampleIndex ) );
                
                return ( -1 );
            }
        }
        
        if ( texCoords != null )
        {
            if ( !texCoords[index].equals( texCoords[sampleIndex] ) )
            {
                if ( index < searchEnd )
                    return ( findVertex( coords, normals, colors, texCoords, index + 1, searchEnd, sampleIndex ) );
                
                return ( -1 );
            }
        }
        
        return ( index );
    }
    
    /**
     * Merges multiple Geometries into one big IndexedTriangleArray.
     * 
     * @param geoms
     * 
     * @return the new IndexedTriangleArray.
     */
    public static IndexedTriangleArray mergeGeometriesITA( Geometry... geoms )
    {
        if ( geoms.length == 0 )
            return ( null );
        
        int numTriangles = 0;
        int features = ~0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            TriangleContainer tc = (TriangleContainer)geoms[i];
            numTriangles += tc.getTriangleCount();
            
            features &= geoms[i].getVertexFormat();
        }
        
        int numVertices = numTriangles * 3;
        
        Point3f[] coords = ( ( features & Geometry.COORDINATES ) != 0 ) ? new Point3f[ numVertices ] : null;
        Vector3f[] normals = ( ( features & Geometry.NORMALS ) != 0 ) ? new Vector3f[ numVertices ] : null;
        Colorf[] colors = ( ( features & Geometry.COLORS ) != 0 ) ? new Colorf[ numVertices ] : null;
        TexCoord2f[] texCoords = ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 ) ? new TexCoord2f[ numVertices ] : null;
        
        Triangle triangle = new Triangle( features, 2 );
        
        int offset = 0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            TriangleContainer tc = (TriangleContainer)geoms[i];
            numTriangles = tc.getTriangleCount();
            
            for ( int t = 0; t < numTriangles; t++ )
            {
                tc.getTriangle( t, triangle );
                
                coords[offset + t * 3 + 0] = new Point3f( triangle.getVertexCoordA() );
                coords[offset + t * 3 + 1] = new Point3f( triangle.getVertexCoordB() );
                coords[offset + t * 3 + 2] = new Point3f( triangle.getVertexCoordC() );
                
                if ( normals != null )
                {
                    normals[offset + t * 3 + 0] = new Vector3f( triangle.getVertexNormalA() );
                    normals[offset + t * 3 + 1] = new Vector3f( triangle.getVertexNormalB() );
                    normals[offset + t * 3 + 2] = new Vector3f( triangle.getVertexNormalC() );
                }
                
                if ( colors != null )
                {
                    colors[offset + t * 3 + 0] = new Colorf( triangle.getVertexColorA() );
                    colors[offset + t * 3 + 1] = new Colorf( triangle.getVertexColorB() );
                    colors[offset + t * 3 + 2] = new Colorf( triangle.getVertexColorC() );
                }
                
                if ( texCoords != null )
                {
                    texCoords[offset + t * 3 + 0] = new TexCoord2f( triangle.getVertexTexCoordA() );
                    texCoords[offset + t * 3 + 1] = new TexCoord2f( triangle.getVertexTexCoordB() );
                    texCoords[offset + t * 3 + 2] = new TexCoord2f( triangle.getVertexTexCoordC() );
                }
            }
            
            offset += geoms[i].getVertexCount();
        }
        
        int[] index = new int[ numVertices ];
        
        index[0] = 0;
        index[1] = 1;
        index[2] = 2;
        
        int numUniqueVertices = 3;
        
        for ( int i = 3; i < numVertices; i++ )
        {
            int j = findVertex( coords, normals, colors, texCoords, 0, i - 1, i );
            
            if ( j < 0 )
            {
                index[i] = i;
                numUniqueVertices++;
            }
            else
            {
                index[i] = index[j];
            }
        }
        
        Point3f[] uniqueCoords = ( ( features & Geometry.COORDINATES ) != 0 ) ? new Point3f[ numUniqueVertices ] : null;
        Vector3f[] uniqueNormals = ( ( features & Geometry.NORMALS ) != 0 ) ? new Vector3f[ numUniqueVertices ] : null;
        Colorf[] uniqueColors = ( ( features & Geometry.COLORS ) != 0 ) ? new Colorf[ numUniqueVertices ] : null;
        TexCoord2f[] uniqueTexCoords = ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 ) ? new TexCoord2f[ numUniqueVertices ] : null;
        
        int j = 0;
        for ( int i = 0; i < numVertices; i++ )
        {
            if ( index[i] == i )
            {
                for ( int k = 0; k < numVertices; k++ )
                {
                    if ( index[k] == i )
                        index[k] = j;
                }
                
                uniqueCoords[j] = coords[i];
                if ( normals != null )
                    uniqueNormals[j] = normals[i];
                if ( colors != null )
                    uniqueColors[j] = colors[i];
                if ( normals != null )
                    uniqueTexCoords[j] = texCoords[i];
                j++;
            }
        }
        
        IndexedTriangleArray targetITA = new IndexedTriangleArray( numUniqueVertices, numVertices );
        targetITA.setCoordinates( 0, uniqueCoords );
        if ( uniqueNormals != null )
            targetITA.setNormals( 0, uniqueNormals );
        if ( uniqueColors != null )
            targetITA.setColors( 0, uniqueColors );
        if ( uniqueTexCoords != null )
            targetITA.setTextureCoordinates( 0, 0, uniqueTexCoords );
        
        targetITA.setIndex( index );
        
        return ( targetITA );
    }
    
    /**
     * Splits a shape into several parts.
     * 
     * @param shape
     * @param maxFacesPerPart
     * 
     * @return the List of resulting Shape3Ds
     */
    public static List< Shape3D > split( Shape3D shape, int maxFacesPerPart/*, int max*/)
    {
        Geometry src = shape.getGeometry();
        
        if ( src == null )
            return ( null );
        
        int vertexCount = src.getVertexCount();
        
        float[] origCoords = ( (Geometry)src ).getCoordRefFloat();
        float[] origNormals = ( (Geometry)src ).getNormalRefFloat();
        //float[] origUVs = ( (Geometry)src ).getTexCoordRefFloat( 0 );
        
        ArrayList< Shape3D > parts = new ArrayList< Shape3D >();
        
        /*divide : */for ( int start = 0; start < vertexCount; start += ( maxFacesPerPart * 3 ) )
        {
            /*if (start > max)
            {
                break divide;
            }*/

            if ( start + ( maxFacesPerPart * 3 ) > vertexCount )
            {
                maxFacesPerPart = ( start + ( maxFacesPerPart * 3 ) ) - vertexCount;
            }
            
            float[] coords = new float[ maxFacesPerPart * 3 ];
            System.arraycopy( origCoords, start, coords, 0, coords.length );
            float[] normals = new float[ maxFacesPerPart * 3 ];
            System.arraycopy( origNormals, start, normals, 0, normals.length );
            /*float[] UVs = new float[ maxFacesPerPart * 2 ];
            System.arraycopy( origUVs, start, UVs, 0, UVs.length );*/

            TriangleArray ta = new TriangleArray( coords.length );
            ta.setOptimization( shape.getGeometry().getOptimization() );
            ta.setCoordinates( 0, coords );
            ta.setNormals( 0, normals );
            //ta.setTextureCoordinates( 0, 0, UVs );
            parts.add( new Shape3D( ta, shape.getAppearance() ) );
        }
        
        return ( parts );
    }
    
    /**
     * Subdivides a Shape's Geometry into n pieces.
     * Total number of faces after subdivision will be :<br>
     * initial number of faces * n * number of vertices per face
     * 
     * @param shape the shape to split
     * @param n The number of times to subdivide the shape
     */
    public static void subdivide( Shape3D shape, int n )
    {
        // While we have still work, subdivide
        if ( n > 1 )
        {
            subdivide( shape, --n );
        }
        
        Geometry geom = shape.getGeometry();
        
        // Here we have the real work
        if ( geom instanceof TriangleArray )
        {
            TriangleArray src = (TriangleArray)geom;
            TriangleArray dst = new TriangleArray( src.getVertexCount() * 3 );
            
            float x1 = 0;
            float x2 = 0;
            float x3 = 0;
            float xc = 0;
            float y1 = 0;
            float y2 = 0;
            float y3 = 0;
            float yc = 0;
            float z1 = 0;
            float z2 = 0;
            float z3 = 0;
            float zc = 0;
            
            // 1st step : vertices
            float[] srcVerts = src.getCoordRefFloat();
            float[] dstVerts = new float[ srcVerts.length * 3 ];
            int j = 0;
            for ( int i = 0; i < srcVerts.length; i += 3 * 3 )
            {
                x1 = srcVerts[ i ];
                x2 = srcVerts[ i + 3 ];
                x3 = srcVerts[ i + 6 ];
                xc = ( x1 + x2 + x3 ) * 0.333f;
                
                y1 = srcVerts[ i + 1 ];
                y2 = srcVerts[ i + 4 ];
                y3 = srcVerts[ i + 7 ];
                yc = ( y1 + y2 + y3 ) * 0.333f;
                
                z1 = srcVerts[ i + 2 ];
                z2 = srcVerts[ i + 5 ];
                z3 = srcVerts[ i + 8 ];
                zc = ( z1 + z2 + z3 ) * .333f;
                
                dstVerts[ j++ ] = x1;
                dstVerts[ j++ ] = y1;
                dstVerts[ j++ ] = z1;
                
                dstVerts[ j++ ] = x2;
                dstVerts[ j++ ] = y2;
                dstVerts[ j++ ] = z2;
                
                dstVerts[ j++ ] = xc;
                dstVerts[ j++ ] = yc;
                dstVerts[ j++ ] = zc;
                
                dstVerts[ j++ ] = x2;
                dstVerts[ j++ ] = y2;
                dstVerts[ j++ ] = z2;
                
                dstVerts[ j++ ] = x3;
                dstVerts[ j++ ] = y3;
                dstVerts[ j++ ] = z3;
                
                dstVerts[ j++ ] = xc;
                dstVerts[ j++ ] = yc;
                dstVerts[ j++ ] = zc;
                
                dstVerts[ j++ ] = x3;
                dstVerts[ j++ ] = y3;
                dstVerts[ j++ ] = z3;
                
                dstVerts[ j++ ] = x1;
                dstVerts[ j++ ] = y1;
                dstVerts[ j++ ] = z1;
                
                dstVerts[ j++ ] = xc;
                dstVerts[ j++ ] = yc;
                dstVerts[ j++ ] = zc;
            }
            
            dst.setCoordinates( 0, dstVerts );
            
            // 2nd step : normals
            if ( ( src.getVertexFormat() & Geometry.NORMALS ) != 0 )
            {
                float[] srcNormals = src.getNormalRefFloat();
                float[] dstNormals = new float[ srcNormals.length * 3 ];
                j = 0;
                for ( int i = 0; i < srcNormals.length; i += 3 * 3 )
                {
                    x1 = srcNormals[ i ];
                    x2 = srcNormals[ i + 3 ];
                    x3 = srcNormals[ i + 6 ];
                    xc = ( x1 + x2 + x3 ) * 0.333f;
                    
                    y1 = srcNormals[ i + 1 ];
                    y2 = srcNormals[ i + 4 ];
                    y3 = srcNormals[ i + 7 ];
                    yc = ( y1 + y2 + y3 ) * 0.333f;
                    
                    z1 = srcNormals[ i + 2 ];
                    z2 = srcNormals[ i + 5 ];
                    z3 = srcNormals[ i + 8 ];
                    zc = ( z1 + z2 + z3 ) * 0.333f;
                    
                    dstNormals[ j++ ] = x1;
                    dstNormals[ j++ ] = y1;
                    dstNormals[ j++ ] = z1;
                    
                    dstNormals[ j++ ] = x2;
                    dstNormals[ j++ ] = y2;
                    dstNormals[ j++ ] = z2;
                    
                    dstNormals[ j++ ] = xc;
                    dstNormals[ j++ ] = yc;
                    dstNormals[ j++ ] = zc;
                    
                    dstNormals[ j++ ] = x2;
                    dstNormals[ j++ ] = y2;
                    dstNormals[ j++ ] = z2;
                    
                    dstNormals[ j++ ] = x3;
                    dstNormals[ j++ ] = y3;
                    dstNormals[ j++ ] = z3;
                    
                    dstNormals[ j++ ] = xc;
                    dstNormals[ j++ ] = yc;
                    dstNormals[ j++ ] = zc;
                    
                    dstNormals[ j++ ] = x3;
                    dstNormals[ j++ ] = y3;
                    dstNormals[ j++ ] = z3;
                    
                    dstNormals[ j++ ] = x1;
                    dstNormals[ j++ ] = y1;
                    dstNormals[ j++ ] = z1;
                    
                    dstNormals[ j++ ] = xc;
                    dstNormals[ j++ ] = yc;
                    dstNormals[ j++ ] = zc;
                }
                
                dst.setNormals( 0, dstNormals );
            }
            
            // 3rd step : UVs
            if ( ( ( src.getVertexFormat() & Geometry.TEXTURE_COORDINATES ) != 0 ) && ( src.getTexCoordSize( 0 ) == 2 ) )
            {
                for ( int tu = 0; tu < src.getNumTextureUnits(); tu++ )
                {
                    float[] srcTexCoords = src.getTexCoordRefFloat( src.getTexCoordSetMap()[ tu ] );
                    float[] dstTexCoords = new float[ srcTexCoords.length * 3 ];
                    j = 0;
                    for ( int i = 0; i < srcTexCoords.length; i += 3 * 2 )
                    {
                        x1 = srcTexCoords[ i ];
                        x2 = srcTexCoords[ i + 2 ];
                        x3 = srcTexCoords[ i + 4 ];
                        xc = ( x1 + x2 + x3 ) * 0.333f;
                        
                        y1 = srcTexCoords[ i + 1 ];
                        y2 = srcTexCoords[ i + 3 ];
                        y3 = srcTexCoords[ i + 5 ];
                        yc = ( y1 + y2 + y3 ) * 0.333f;
                        
                        dstTexCoords[ j++ ] = x1;
                        dstTexCoords[ j++ ] = y1;
                        
                        dstTexCoords[ j++ ] = x2;
                        dstTexCoords[ j++ ] = y2;
                        
                        dstTexCoords[ j++ ] = xc;
                        dstTexCoords[ j++ ] = yc;
                        
                        dstTexCoords[ j++ ] = x2;
                        dstTexCoords[ j++ ] = y2;
                        
                        dstTexCoords[ j++ ] = x3;
                        dstTexCoords[ j++ ] = y3;
                        
                        dstTexCoords[ j++ ] = xc;
                        dstTexCoords[ j++ ] = yc;
                        
                        dstTexCoords[ j++ ] = x3;
                        dstTexCoords[ j++ ] = y3;
                        
                        dstTexCoords[ j++ ] = x1;
                        dstTexCoords[ j++ ] = y1;
                        
                        dstTexCoords[ j++ ] = xc;
                        dstTexCoords[ j++ ] = yc;
                    }
                    
                    dst.setTextureCoordinates( src.getTexCoordSetMap()[ tu ], 0, 2, dstTexCoords );
                }
            }
            
            // 4th step : Color
            if ( ( ( src.getVertexFormat() & Geometry.COLORS ) != 0 ) && ( !src.hasColorAlpha() ) )
            {
                float[] srcColors = src.getColorRefFloat();
                float[] dstColors = new float[ srcColors.length * 3 ];
                j = 0;
                for ( int i = 0; i < srcColors.length; i += 3 * 3 )
                {
                    x1 = srcColors[ i ];
                    x2 = srcColors[ i + 3 ];
                    x3 = srcColors[ i + 6 ];
                    xc = ( x1 + x2 + x3 ) * 0.333f;
                    
                    y1 = srcColors[ i + 1 ];
                    y2 = srcColors[ i + 4 ];
                    y3 = srcColors[ i + 7 ];
                    yc = ( y1 + y2 + y3 ) * 0.333f;
                    
                    z1 = srcColors[ i + 2 ];
                    z2 = srcColors[ i + 5 ];
                    z3 = srcColors[ i + 8 ];
                    zc = ( z1 + z2 + z3 ) * 0.333f;
                    
                    dstColors[ j++ ] = x1;
                    dstColors[ j++ ] = y1;
                    dstColors[ j++ ] = z1;
                    
                    dstColors[ j++ ] = x2;
                    dstColors[ j++ ] = y2;
                    dstColors[ j++ ] = z2;
                    
                    dstColors[ j++ ] = xc;
                    dstColors[ j++ ] = yc;
                    dstColors[ j++ ] = zc;
                    
                    dstColors[ j++ ] = x2;
                    dstColors[ j++ ] = y2;
                    dstColors[ j++ ] = z2;
                    
                    dstColors[ j++ ] = x3;
                    dstColors[ j++ ] = y3;
                    dstColors[ j++ ] = z3;
                    
                    dstColors[ j++ ] = xc;
                    dstColors[ j++ ] = yc;
                    dstColors[ j++ ] = zc;
                    
                    dstColors[ j++ ] = x3;
                    dstColors[ j++ ] = y3;
                    dstColors[ j++ ] = z3;
                    
                    dstColors[ j++ ] = x1;
                    dstColors[ j++ ] = y1;
                    dstColors[ j++ ] = z1;
                    
                    dstColors[ j++ ] = xc;
                    dstColors[ j++ ] = yc;
                    dstColors[ j++ ] = zc;
                }
                
                dst.setColors( 0, 3, dstColors );
            }
            
            // Final step : swap the geoms
            shape.setGeometry( dst );
        }
        else
        {
            throw new Error( "Error! Cannot subdivide a Geometry of type : " + geom.getClass().getName() + "." + " Sorry :( (it's just not implemented yet)" );
        }
    }
    
    /**
     * Generates (flat) TextureCoordinates from the x/y coordinates of the Geometry's vertex-coords.
     * The TextureCoordinates are directly applied the the Geometry.
     * 
     * @param geom the Geometry to get the vertex-coords from and to store the tex-coords to
     * @param texCoords the reference array for the texcoords. Can be <code>null</code>.
     */
    public static final void generateTexCoordsXY( Geometry geom, TexCoord2f[] texCoords )
    {
        if ( ( !geom.hasTextureCoordinates() ) || ( geom.getTexCoordSize( 0 ) != 2 ) )
        {
            throw new UnsupportedOperationException( "The Geometry cannot receive 2D-texture-coords" );
        }
        
        Point3f coord = Point3f.fromPool();
        
        // Find the range of z values
        float xmin = +Float.MAX_VALUE;
        float xmax = -Float.MAX_VALUE;
        float ymin = +Float.MAX_VALUE;
        float ymax = -Float.MAX_VALUE;
        final float n = geom.getVertexCount();
        for ( int i = 0; i < n; i++ )
        {
            geom.getVertex( i, coord );
            
            xmin = Math.min( xmin, coord.getX() );
            xmax = Math.max( xmax, coord.getX() );
            ymin = Math.min( ymin, coord.getY() );
            ymax = Math.max( ymax, coord.getY() );
        }
        final float xscale = 1.0f / ( xmax - xmin );
        final float yscale = 1.0f / ( ymax - ymin );
        
        TexCoord2f texCoord = new TexCoord2f();
        
        // Generate the coordinates
        for ( int i = 0; i < n; i++ )
        {
            geom.getVertex( i, coord );
            
            texCoord.setS( ( coord.getX() - xmin ) * xscale );
            texCoord.setT( ( coord.getY() - ymin ) * yscale );
            
            if ( texCoords != null )
            {
                if ( texCoords[ i ] == null )
                    texCoords[ i ] = new TexCoord2f();
                
                texCoords[ i ].set( texCoord );
            }
            
            geom.setTextureCoordinate( 0, i, texCoord );
        }
        
        Point3f.toPool( coord );
    }
    
    /**
     * Generates (flat) TextureCoordinates from the x/z coordinates of the Geometry's vertex-coords.
     * The TextureCoordinates are directly applied the the Geometry.
     * 
     * @param geom the Geometry to get the vertex-coords from and to store the tex-coords to
     * @param texCoords the reference array for the texcoords. Can be <code>null</code>.
     */
    public static final void generateTexCoordsXZ( Geometry geom, TexCoord2f[] texCoords )
    {
        if ( ( !geom.hasTextureCoordinates() ) || ( geom.getTexCoordSize( 0 ) != 2 ) )
        {
            throw new UnsupportedOperationException( "The Geometry cannot receive texture-coords" );
        }
        
        Point3f coord = Point3f.fromPool();
        
        // Find the range of z values
        float xmin = +Float.MAX_VALUE;
        float xmax = -Float.MAX_VALUE;
        float zmin = +Float.MAX_VALUE;
        float zmax = -Float.MAX_VALUE;
        final float n = geom.getVertexCount();
        for ( int i = 0; i < n; i++ )
        {
            geom.getVertex( i, coord );
            
            xmin = Math.min( xmin, coord.getX() );
            xmax = Math.max( xmax, coord.getX() );
            zmin = Math.min( zmin, coord.getZ() );
            zmax = Math.max( zmax, coord.getZ() );
        }
        final float xscale = 1.0f / ( xmax - xmin );
        final float zscale = 1.0f / ( zmax - zmin );
        
        TexCoord2f texCoord = new TexCoord2f();
        
        // Generate the coordinates
        for ( int i = 0; i < n; i++ )
        {
            geom.getVertex( i, coord );
            
            texCoord.setS( ( coord.getX() - xmin ) * xscale );
            texCoord.setT( ( coord.getY() - zmin ) * zscale );
            
            if ( texCoords != null )
            {
                if ( texCoords[ i ] == null )
                    texCoords[ i ] = new TexCoord2f();
                
                texCoords[ i ].set( texCoord );
            }
            
            geom.setTextureCoordinate( 0, i, texCoord );
        }
        
        Point3f.toPool( coord );
    }
    
    /**
     * Generates (flat) TextureCoordinates from the x/y coordinates of the Geometry's vertex-coords.
     * The TextureCoordinates are directly applied the the Geometry.
     * 
     * @param geom the Geometry to get the vertex-coords from and to store the tex-coords to
     * @param texCoords the reference array for the texcoords. Can be <code>null</code>.
     */
    public static final void generateTexCoordsZY( Geometry geom, TexCoord2f[] texCoords )
    {
        if ( ( !geom.hasTextureCoordinates() ) || ( geom.getTexCoordSize( 0 ) != 2 ) )
        {
            throw new UnsupportedOperationException( "The Geometry cannot receive texture-coords" );
        }
        
        Point3f coord = Point3f.fromPool();
        
        // Find the range of z values
        float zmin = +Float.MAX_VALUE;
        float zmax = -Float.MAX_VALUE;
        float ymin = +Float.MAX_VALUE;
        float ymax = -Float.MAX_VALUE;
        final float n = geom.getVertexCount();
        for ( int i = 0; i < n; i++ )
        {
            geom.getVertex( i, coord );
            
            zmin = Math.min( zmin, coord.getZ() );
            zmax = Math.max( zmax, coord.getZ() );
            ymin = Math.min( ymin, coord.getY() );
            ymax = Math.max( ymax, coord.getY() );
        }
        final float zscale = 1.0f / ( zmax - zmin );
        final float yscale = 1.0f / ( ymax - ymin );
        
        TexCoord2f texCoord = new TexCoord2f();
        
        // Generate the coordinates
        for ( int i = 0; i < n; i++ )
        {
            geom.getVertex( i, coord );
            
            texCoord.setS( ( coord.getZ() - zmin ) * zscale );
            texCoord.setT( ( coord.getY() - ymin ) * yscale );
            
            if ( texCoords != null )
            {
                if ( texCoords[ i ] == null )
                    texCoords[ i ] = new TexCoord2f();
                
                texCoords[ i ].set( texCoord );
            }
            
            geom.setTextureCoordinate( 0, i, texCoord );
        }
        
        Point3f.toPool( coord );
    }
    
    /**
     * Computes smooth vertex-indices and writes them into the vertex-coord-data.
     * 
     * TODO: optimize!
     * 
     * @param geom
     * @param initialIndex
     * @param indexCount
     * @param flipNormals
     */
    public static void computeVertexNormals( Geometry geom, int initialIndex, int indexCount, boolean flipNormals )
    {
        final float EPSILON = 0.001f;
        
        Point3f[] coords = new Point3f[ geom.getValidVertexCount() ];
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ geom.getInitialVertexIndex() + i ] = new Point3f();
        }
        
        geom.getCoordinates( 0, coords );
        
        final int[] indices;
        if ( geom instanceof IndexedGeometryArray )
        {
            final IndexedGeometryArray idxGeom = (IndexedGeometryArray)geom;
            
            indices = idxGeom.getIndex();
            if ( initialIndex < 0 )
                initialIndex = idxGeom.getInitialIndexIndex();
            if ( indexCount < 0 )
                indexCount = idxGeom.getValidIndexCount();
        }
        else
        {
            indices = new int[ coords.length ];
            if ( initialIndex < 0 )
                initialIndex = 0;
            if ( indexCount < 0 )
                indexCount = indices.length;
            
            for ( int i = 0; i < indices.length; i++ )
            {
                indices[ i ] = i;
            }
            
            /*
            for ( int i = 0; i < coords.length; i++ )
            {
                if ( indices[ i ] == i )
                {
                    for ( int j = i + 1; j < coords.length; j++ )
                    {
                        if ( indices[ j ] == j )
                        {
                            if ( coords[ i ].distance( coords[ j ] ) <= EPSILON )
                            {
                                indices[ j ] = indices[ i ];
                            }
                        }
                    }
                }
            }
            */
        }
        
        Vector3f[] normals = new Vector3f[ coords.length ];
        for ( int i = 0; i < normals.length; i++ )
        {
            normals[ i ] = new Vector3f();
        }
        geom.getNormals( 0, normals );
        for ( int i = 0; i < normals.length; i++ )
        {
            normals[ i ].normalize();
        }
        
        
        
        Vector3f[] newNormals = new Vector3f[ coords.length ];
        for ( int i = 0; i < newNormals.length; i++ )
        {
            newNormals[ i ] = new Vector3f( 0f, 0f, 0f );
        }
        
        TriangleContainer trianCont = (TriangleContainer)geom;
        Triangle[] triangles = new Triangle[ trianCont.getTriangleCount() ];
        for ( int i = 0; i < triangles.length; i++ )
        {
            triangles[ i ] = new Triangle();
            trianCont.getTriangle( i, triangles[ i ] );
        }
        
        Vector3f normal = new Vector3f();
        for ( int i = initialIndex; i < initialIndex + indexCount; i++ )
        {
            int idx = indices[ i ];
            
            float angleSum = 0f;
            
            for ( int j = 0; j < triangles.length; j++ )
            {
                if ( coords[ idx ].distance( triangles[ j ].getVertexCoordA() ) <= EPSILON )
                {
                    angleSum += triangles[ j ].getAngleA();
                }
                else if ( coords[ idx ].distance( triangles[ j ].getVertexCoordB() ) <= EPSILON )
                {
                    angleSum += triangles[ j ].getAngleB();
                }
                else if ( coords[ idx ].distance( triangles[ j ].getVertexCoordC() ) <= EPSILON )
                {
                    angleSum += triangles[ j ].getAngleC();
                }
            }
            
            for ( int j = 0; j < triangles.length; j++ )
            {
                if ( coords[ idx ].distance( triangles[ j ].getVertexCoordA() ) <= EPSILON )
                {
                    triangles[ j ].getFaceNormal( normal );
                    final float angle = triangles[ j ].getAngleA();
                    normal.scale( angle / angleSum );
                    
                    newNormals[ idx ].add( normal );
                }
                else if ( coords[ idx ].distance( triangles[ j ].getVertexCoordB() ) <= EPSILON )
                {
                    triangles[ j ].getFaceNormal( normal );
                    final float angle = triangles[ j ].getAngleB();
                    normal.scale( angle / angleSum );
                    
                    newNormals[ idx ].add( normal );
                }
                else if ( coords[ idx ].distance( triangles[ j ].getVertexCoordC() ) <= EPSILON )
                {
                    triangles[ j ].getFaceNormal( normal );
                    final float angle = triangles[ j ].getAngleC();
                    normal.scale( angle / angleSum );
                    
                    newNormals[ idx ].add( normal );
                }
            }
        }
        
        if ( flipNormals )
        {
            for ( int i = 0; i < newNormals.length; i++ )
            {
                newNormals[ i ].normalize();
                newNormals[ i ].negate();
            }
        }
        else
        {
            for ( int i = 0; i < newNormals.length; i++ )
            {
                //System.out.println( newNormals[ i ] );
                newNormals[ i ].normalize();
            }
        }
        
        geom.setNormals( 0, newNormals );
    }
    
    /**
     * Computes smooth vertex-indices and writes them into the vertex-coord-data.
     * 
     * @param geom
     * @param initialIndex
     * @param indexCount
     */
    public static void computeVertexNormals( Geometry geom, int initialIndex, int indexCount )
    {
        computeVertexNormals( geom, initialIndex, indexCount, false );
    }
    
    /**
     * Computes smooth vertex normals and writes them into the vertex-coord-data.
     * 
     * @param geom
     * @param initialIndex
     * @param indexCount
     * @param flipNormals
     */
    @SuppressWarnings( "unused" )
    private static void _computeVertexNormals( Geometry geom, int initialIndex, int indexCount, boolean flipNormals )
    {
        final float EPSILON = 0.001f;
        
        Point3f[] coords = new Point3f[ geom.getValidVertexCount() ];
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ geom.getInitialVertexIndex() + i ] = new Point3f();
        }
        
        geom.getCoordinates( 0, coords );
        
        final int[] indices;
        if ( geom instanceof IndexedGeometryArray )
        {
            final IndexedGeometryArray idxGeom = (IndexedGeometryArray)geom;
            
            indices = idxGeom.getIndex();
            if ( initialIndex < 0 )
                initialIndex = idxGeom.getInitialIndexIndex();
            if ( indexCount < 0 )
                indexCount = idxGeom.getValidIndexCount();
        }
        else
        {
            indices = new int[ coords.length ];
            if ( initialIndex < 0 )
                initialIndex = 0;
            if ( indexCount < 0 )
                indexCount = indices.length;
            for ( int i = 0; i < indices.length; i++ )
            {
                indices[ i ] = i;
            }
            
            for ( int i = 0; i < coords.length; i++ )
            {
                if ( indices[ i ] == i )
                {
                    for ( int j = i + 1; j < coords.length; j++ )
                    {
                        if ( indices[ j ] == j )
                        {
                            if ( coords[ i ].distance( coords[ j ] ) <= EPSILON )
                            {
                                indices[ j ] = indices[ i ];
                            }
                        }
                    }
                }
            }
        }
        
        Vector3f[] normals = new Vector3f[ coords.length ];
        for ( int i = 0; i < normals.length; i++ )
        {
            normals[ i ] = new Vector3f();
        }
        geom.getNormals( 0, normals );
        for ( int i = 0; i < normals.length; i++ )
        {
            normals[ i ].normalize();
        }
        
        Vector3f[] newNormals = new Vector3f[ coords.length ];
        for ( int i = 0; i < newNormals.length; i++ )
        {
            newNormals[ i ] = new Vector3f( 0f, 0f, 0f );
        }
        
        TriangleContainer trianCont = (TriangleContainer)geom;
        Triangle[] triangles = new Triangle[ trianCont.getTriangleCount() ];
        for ( int i = 0; i < triangles.length; i++ )
        {
            triangles[ i ] = new Triangle();
            trianCont.getTriangle( i, triangles[ i ] );
        }
        
        for ( int i = initialIndex; i < initialIndex + indexCount; i++ )
        {
            int idx= indices[ i ];
            for ( int j = 0; j < triangles.length; j++ )
            {
                if ( coords[ idx ].distance( triangles[ j ].getVertexCoordA() ) <= EPSILON )
                {
                    triangles[ j ].getFaceNormalACAB( newNormals[ idx ] );
                }
                else if ( coords[ idx ].distance( triangles[ j ].getVertexCoordB() ) <= EPSILON )
                {
                    triangles[ j ].getFaceNormalBABC( newNormals[ idx ] );
                }
                else if ( coords[ idx ].distance( triangles[ j ].getVertexCoordC() ) <= EPSILON )
                {
                    triangles[ j ].getFaceNormalCBCA( newNormals[ idx ] );
                }
            }
        }
        
        if ( flipNormals )
        {
            for ( int i = 0; i < newNormals.length; i++ )
            {
                newNormals[ i ].negate();
            }
        }
        
        geom.setNormals( 0, newNormals );
    }
    
    /**
     * Computes smooth vertex-indices and writes them into the vertex-coord-data.
     * 
     * @param geom
     * @param flipNormals
     */
    public static void computeVertexNormals( Geometry geom, boolean flipNormals )
    {
        computeVertexNormals( geom, -1, -1, flipNormals );
    }
    
    /**
     * Computes smooth vertex-indices and writes them into the vertex-coord-data.
     * 
     * @param geom
     */
    public static void computeVertexNormals( Geometry geom )
    {
        computeVertexNormals( geom, -1, -1 );
    }
}
