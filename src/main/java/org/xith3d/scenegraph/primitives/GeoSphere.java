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
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;

/**
 * A geodesic sphere based on an icosahedron.
 * 
 * @author Daniel Herring
 */
public class GeoSphere extends Shape3D
{
    private static GeometryType geomConstructTypeHint = GeometryType.TRIANGLE_ARRAY;
    
    /**
     * Sets the hint for this ShapeType's Geometry to be constructed of a certain type.
     * 
     * @param hint
     */
    public static void setGeometryConstructionTypeHint( GeometryType hint )
    {
        switch ( hint )
        {
            case TRIANGLE_ARRAY:
                geomConstructTypeHint = hint;
                break;
            
            default:
                throw new UnsupportedOperationException( "Currently " + GeoSphere.class.getSimpleName() + " does not support " + hint );
        }
    }
    
    /**
     * @return the hint for this ShapeType's Geometry to be constructed of a certain type.
     */
    public static GeometryType getGeometryConstructionTypeHint()
    {
        return ( geomConstructTypeHint );
    }
    
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
    private static void geodesate( Vector3f a, Vector3f b, Vector3f c, int N, int offset, Vector3f[] vertices )
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
    
    public static GeometryConstruct createGeometryConstructTA( int N, int features, boolean colorAlpha, int texcoordsSize, boolean hemisphere )
    {
        if ( N < 0 )
        {
            throw new IllegalArgumentException( "require N>=0" );
        }
        
        features |= Geometry.COORDINATES;
        
        // Recurse on the 20 triangles of (semi-)icosahedron
        final int strips = hemisphere ? 2 : 4;
        // there are 5 "sides" around the pole of (semi-)icosahedron
        final int triangles = strips * 5;
        Vector3f[] coords = new Vector3f[ 3 * triangles * N * N ];
        Vector3f a, b, c; // corners of each base triangle
        for ( int i = 0; i < 5; i++ )
        {
            // top pole
            a = new Vector3f( 0f, 0f, 1f );
            b = new Vector3f();
            b.setX( FastMath.cos( ( i + 0 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3 ) );
            b.setY( FastMath.sin( ( i + 0 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3 ) );
            b.setZ( FastMath.cos( FastMath.PI / 3f ) );
            c = new Vector3f();
            c.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
            c.setZ( FastMath.cos( FastMath.PI / 3f ) );
            geodesate( a, b, c, N, 3 * ( strips * i + 0 ) * N * N, coords );
            
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
            c.setZ( FastMath.cos( FastMath.PI / 3f ) );
            geodesate( a, b, c, N, 3 * ( strips * i + 1 ) * N * N, coords );
            
            if ( !hemisphere )
            {
                // bottom ring
                a = new Vector3f();
                a.setX( FastMath.cos( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                a.setY( FastMath.sin( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                a.setZ( FastMath.cos( 2f * FastMath.PI / 3f ) );
                b = new Vector3f();
                b.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                b.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                b.setZ( FastMath.cos( FastMath.TWO_PI / 3f ) );
                c = new Vector3f();
                c.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
                c.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f ) * FastMath.sin( FastMath.PI / 3f ) );
                c.setZ( FastMath.cos( FastMath.PI / 3f ) );
                geodesate( a, b, c, N, 3 * ( strips * i + 2 ) * N * N, coords );
                
                // bottom pole
                a = new Vector3f();
                a.setX( FastMath.cos( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                a.setY( FastMath.sin( i * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                a.setZ( FastMath.cos( 2f * FastMath.PI / 3f ) );
                b = new Vector3f( 0f, 0f, -1f );
                c = new Vector3f();
                c.setX( FastMath.cos( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                c.setY( FastMath.sin( ( i + 1 ) * FastMath.TWO_PI / 5f + FastMath.PI / 5f ) * FastMath.sin( FastMath.TWO_PI / 3f ) );
                c.setZ( FastMath.cos( FastMath.TWO_PI / 3f ) );
                geodesate( a, b, c, N, 3 * ( strips * i + 3 ) * N * N, coords );
            }
        }
        
        // Convert to vertices
        Point3f[] vertices = new Point3f[ coords.length ];
        for ( int i = 0; i < coords.length; i++ )
        {
            vertices[ i ] = new Point3f( coords[ i ] );
        }
        
        StaticTransform.rotateX( vertices, FastMath.PI_HALF );
        
        Vector3f[] normals = null;
        if ( ( features & Geometry.NORMALS ) > 0 )
        {
            normals = GeomFactory.generateNaiveNormals( vertices );
        }
        
        Colorf[] colors = null;
        if ( ( features & Geometry.COLORS ) != 0 )
        {
            colors = new Colorf[ vertices.length ];
            for ( int i = 0; i < vertices.length; i++ )
            {
                if ( colorAlpha )
                    colors[ i ] = new Colorf( vertices[ i ].getX(), vertices[ i ].getY(), vertices[ i ].getZ(), 0f );
                else
                    colors[ i ] = new Colorf( vertices[ i ].getX(), vertices[ i ].getY(), vertices[ i ].getZ() );
                colors[ i ].mul( 2f ); // Why that?
            }
        }
        
        TexCoord2f[] texCoords = null;
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texcoordsSize == 2 )
                texCoords = GeomFactory.generateTexCoords2( vertices );
        }
        
        return ( new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, vertices, normals, texCoords, colors ) );
    }
    
    public static TriangleArray createGeometryTA( int N, int features, boolean colorAlpha, int texCoordsSize, boolean hemisphere )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcTA = createGeometryConstructTA( N, features, colorAlpha, texCoordsSize, hemisphere );
        
        return ( GeomFactory.createTriangleArray( gcTA ) );
    }
    
    /**
     * Creates the GeometryArray for a GeoSphere.
     * 
     * @param N Split each edge of the icosahedron N times
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static Geometry createGeometry( int N, int features, boolean colorAlpha, int texCoordsSize, boolean hemisphere )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( N, features, colorAlpha, texCoordsSize, hemisphere ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public GeoSphere( int N, int features, boolean colorAlpha, int texCoordsSize, boolean hemisphere )
    {
        super( createGeometry( N, features, colorAlpha, texCoordsSize, hemisphere ) );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * @param hemisphere
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( float radius, int N, int features, boolean colorAlpha, int texCoordsSize, boolean hemisphere )
    {
        this( N, features, colorAlpha, texCoordsSize, hemisphere );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public GeoSphere( int N, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( N, features, colorAlpha, texCoordsSize, false );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( float radius, int N, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( radius, N, features, colorAlpha, texCoordsSize, false );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to this shape's Appearance
     */
    public GeoSphere( int N, boolean hemisphere, Texture texture )
    {
        this( N, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2, hemisphere );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param hemisphere
     * @param texture the Texture to be applied to this shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( float radius, int N, boolean hemisphere, Texture texture )
    {
        this( radius, N, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2, hemisphere );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to this shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( int N, Texture texture )
    {
        this( N, false, texture );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to this shape's Appearance
     */
    public GeoSphere( float radius, int N, Texture texture )
    {
        this( radius, N, false, texture );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to this shape's Appearance
     */
    public GeoSphere( int N, boolean hemisphere, String texture )
    {
        this( N, hemisphere, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param hemisphere
     * @param texture the Texture to be applied to this shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( float radius, int N, boolean hemisphere, String texture )
    {
        this( radius, N, hemisphere, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to this shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( int N, String texture )
    {
        this( N, false, texture );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param texture the Texture to be applied to this shape's Appearance
     */
    public GeoSphere( float radius, int N, String texture )
    {
        this( radius, N, false, texture );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param color the color to be applied to this shape's Appearance's ColoringAttributes
     */
    public GeoSphere( int N, boolean hemisphere, Colorf color )
    {
        this( N, Geometry.COORDINATES | Geometry.NORMALS, false, 2, hemisphere );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param hemisphere
     * @param color the color to be applied to this shape's Appearance's ColoringAttributes
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( float radius, int N, boolean hemisphere, Colorf color )
    {
        this( radius, N, Geometry.COORDINATES | Geometry.NORMALS, false, 2, hemisphere );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param color the color to be applied to this shape's Appearance's ColoringAttributes
     */
    public GeoSphere( int N, Colorf color )
    {
        this( N, false, color );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param color the color to be applied to this shape's Appearance's ColoringAttributes
     */
    public GeoSphere( float radius, int N, Colorf color )
    {
        this( radius, N, false, color );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param app the Appearance to be applied to this shape
     */
    public GeoSphere( int N, boolean hemisphere, Appearance app )
    {
        this( N, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ), hemisphere );
        
        setAppearance( app );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param hemisphere
     * @param app the Appearance to be applied to this shape
     * 
     * @see StaticTransform#scale(Shape3D, float)    
     */
    public GeoSphere( float radius, int N, boolean hemisphere, Appearance app )
    {
        this( radius, N, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ), hemisphere );
        
        setAppearance( app );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param N Split each edge of the icosahedron N times
     * @param app the Appearance to be applied to this shape
     */
    public GeoSphere( int N, Appearance app )
    {
        this( N, false, app );
    }
    
    /**
     * Create a geodesic sphere based on an icosahedron.
     * 
     * This geodesic sphere gives one of the best "roundness"/triangles ratios available; 
     * much better than a sphere generated from the slices/stacks specification.
     * 
     * old param: addColor Set true to add color information
     * 
     * @param radius amount to enlarge the sphere by
     * @param N Split each edge of the icosahedron N times
     * @param app the Appearance to be applied to this shape
     */
    public GeoSphere( float radius, int N, Appearance app )
    {
        this( radius, N, false, app );
    }
}
