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
package org.xith3d.scenegraph.primitives;

import org.openmali.FastMath;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * Geodesic sphere, warped into an ellipsoid.
 * 
 * @author Daniel Herring
 */
public class GeoEllipsoid extends Shape3D
{
    /**
     * This method can be used to geodesate a triangle.
     * In mathspeak, project the nth-order tessellation onto the unit circle.
     * 
     * In English, subdivide the triangle into N^2 smaller triangles of the
     * same proportions, and then map all the new vertices to the unit circle.
     * 
     * (This routine assumes a, b, and c are already unit vectors.)
     * 
     * @param a First corner (should be a unit vector)
     * @param b Second corner (should be a unit vector)
     * @param c Third corner (should be a unit vector)
     * @param N Number of splits in each edge
     * @param offset Offset to base vertex index
     * @param vertices Target vertex array
     */
    public static void geodesate( Vector3f a, Vector3f b, Vector3f c, int N, int offset, Vector3f[] vertices )
    {
        Vector3f[][] buffer = new Vector3f[ 2 ][ N + 1 ]; // Store the vertex buffers
        buffer[ 0 ][ 0 ] = new Vector3f( a ); // Seed the top row
        int top, bottom;
        for ( int i = 0; i < N; i++ )
        {
            // auto-swap buffer rows
            top = i % 2;
            bottom = ( i + 1 ) % 2;
            
            // Start the bottom row
            buffer[ bottom ][ 0 ] = new Vector3f();
            buffer[ bottom ][ 0 ].setX( ( N - i - 1 ) * a.getX() + ( i + 1 ) * b.getX() );
            buffer[ bottom ][ 0 ].setY( ( N - i - 1 ) * a.getY() + ( i + 1 ) * b.getY() );
            buffer[ bottom ][ 0 ].setZ( ( N - i - 1 ) * a.getZ() + ( i + 1 ) * b.getZ() );
            buffer[ bottom ][ 0 ].normalize();
            
            for ( int j = 0; j <= i; j++ )
            {
                // next bottom point
                buffer[ bottom ][ j + 1 ] = new Vector3f();
                buffer[ bottom ][ j + 1 ].setX( ( N - i - 1 ) * a.getX() + ( i - j ) * b.getX() + ( j + 1 ) * c.getX() );
                buffer[ bottom ][ j + 1 ].setY( ( N - i - 1 ) * a.getY() + ( i - j ) * b.getY() + ( j + 1 ) * c.getY() );
                buffer[ bottom ][ j + 1 ].setZ( ( N - i - 1 ) * a.getZ() + ( i - j ) * b.getZ() + ( j + 1 ) * c.getZ() );
                buffer[ bottom ][ j + 1 ].normalize();
                
                vertices[ offset++ ] = buffer[ top ][ j ];
                vertices[ offset++ ] = buffer[ bottom ][ j ];
                vertices[ offset++ ] = buffer[ bottom ][ j + 1 ];
                
                if ( j < i )
                {
                    vertices[ offset++ ] = buffer[ top ][ j ];
                    vertices[ offset++ ] = buffer[ bottom ][ j + 1 ];
                    vertices[ offset++ ] = buffer[ top ][ j + 1 ];
                }
            }
        }
    }
    
    /**
     * Geodesic sphere, warped into an ellipsoid.
     * Creates 20*N^2 triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param N Split each edge of the icosahedron N times
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public GeoEllipsoid( float rx, float ry, float rz, int N, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( N < 0 )
        {
            throw new IllegalArgumentException( "requires N>=0" );
        }
        
        features |= Geometry.COORDINATES;
        
        // Recurse on the 20 triangles of an icosahedron
        Vector3f[] coords = new Vector3f[ 3 * 20 * N * N ];
        Vector3f a, b, c; // corners of each base triangle
        for ( int i = 0; i < 5; i++ )
        {
            // top pole
            a = new Vector3f( 0f, 0f, 1f );
            b = new Vector3f();
            b.setX( FastMath.cos( i * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            b.setY( FastMath.sin( i * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            b.setZ( FastMath.cos( FastMath.PI / 3f ) );
            c = new Vector3f();
            c.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setZ( FastMath.cos( FastMath.PI / 3f ) );
            geodesate( a, b, c, N, 3 * ( 4 * i + 0 ) * N * N, coords );
            
            // top ring
            a = new Vector3f();
            a.setX( FastMath.cos( i * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            a.setY( FastMath.sin( i * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            a.setZ( FastMath.cos( FastMath.PI / 3f ) );
            b = new Vector3f();
            b.setX( FastMath.cos( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            b.setY( FastMath.sin( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            b.setZ( FastMath.cos( FastMath.TWO_PI / 3f ) );
            c = new Vector3f();
            c.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setZ( FastMath.cos( FastMath.PI / 3 ) );
            geodesate( a, b, c, N, 3 * ( 4 * i + 1 ) * N * N, coords );
            
            // bottom ring
            a = new Vector3f();
            a.setX( FastMath.cos( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            a.setY( FastMath.sin( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            a.setZ( FastMath.cos( FastMath.TWO_PI / 3f ) );
            b = new Vector3f();
            b.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            b.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            b.setZ( FastMath.cos( FastMath.TWO_PI / 3f ) );
            c = new Vector3f();
            c.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setZ( FastMath.cos( FastMath.PI / 3f ) );
            geodesate( a, b, c, N, 3 * ( 4 * i + 2 ) * N * N, coords );
            
            // bottom pole
            a = new Vector3f();
            a.setX( FastMath.cos( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            a.setY( FastMath.sin( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            a.setZ( FastMath.cos( FastMath.TWO_PI / 3f ) );
            b = new Vector3f( 0f, 0f, -1f );
            c = new Vector3f();
            c.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            c.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
            c.setZ( FastMath.cos( FastMath.TWO_PI / 3f ) );
            geodesate( a, b, c, N, 3 * ( 4 * i + 3 ) * N * N, coords );
        }
        
        // Sew everything up
        Point3f[] vertices = new Point3f[ coords.length ];
        Colorf[] colors = null;
        if ( ( features & Geometry.COLORS ) != 0 )
            colors = new Colorf[ vertices.length ];
        for ( int i = 0; i < vertices.length; i++ )
        {
            vertices[ i ] = new Point3f( coords[ i ] );
            vertices[ i ].mul( rx, ry, rz );
            if ( ( colors != null ) && colorAlpha )
                colors[ i ] = new Colorf( coords[ i ].getX(), coords[ i ].getY(), coords[ i ].getZ(), 0f );
            else
                colors[ i ] = new Colorf( coords[ i ].getX(), coords[ i ].getY(), coords[ i ].getZ() );
        }
        
        TriangleArray geom = new TriangleArray( vertices.length );
        geom.makeInterleaved( features, colorAlpha, new int[] { texCoordsSize }, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        geom.setCoordinates( 0, vertices );
        if ( colors != null )
            geom.setColors( 0, colors );
        
        super.setGeometry( geom );
    }
    
    /**
     * Geodesic sphere, warped into an ellipsoid.
     * Creates 20*N^2 triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to the shape's Appearance
     */
    public GeoEllipsoid( float rx, float ry, float rz, int N, Texture texture )
    {
        this( rx, ry, rz, N, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Geodesic sphere, warped into an ellipsoid.
     * Creates 20*N^2 triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to the shape's Appearance
     */
    public GeoEllipsoid( float rx, float ry, float rz, int N, String texture )
    {
        this( rx, ry, rz, N, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Geodesic sphere, warped into an ellipsoid.
     * Creates 20*N^2 triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param N Split each edge of the icosahedron N times
     * @param color color to be applied to this shape's ColoringAttributes
     */
    public GeoEllipsoid( float rx, float ry, float rz, int N, Colorf color )
    {
        this( rx, ry, rz, N, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Geodesic sphere, warped into an ellipsoid.
     * Creates 20*N^2 triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param N Split each edge of the icosahedron N times
     * @param app the Appearance to be applied to this shape
     */
    public GeoEllipsoid( float rx, float ry, float rz, int N, Appearance app )
    {
        this( rx, ry, rz, N, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
}
