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
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * A sphere using standard specifications.
 * 
 * @author Daniel Herring
 * @author Marvin Froehlich (aka Qudus)
 */
public class Hemisphere extends Shape3D
{
    private static GeometryType geomConstructTypeHint = GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY;
    
    /**
     * Sets the hint for this ShapeType's Geometry to be constructed of a certain type.
     * 
     * @param hint
     */
    public static void setGeometryConstructionTypeHint( GeometryType hint )
    {
        switch ( hint )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
            case INDEXED_TRIANGLE_ARRAY:
            case TRIANGLE_STRIP_ARRAY:
            case TRIANGLE_ARRAY:
                geomConstructTypeHint = hint;
                break;
            
            default:
                throw new UnsupportedOperationException( "Currently " + Sphere.class.getSimpleName() + " does not support " + hint );
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
     * Creates the GemetryConstruct for a Hemisphere.
     * 
     * <pre>
     * Parametric equations:
     * x = r * cos( theta ) * sin( phi )
     * y = r * sin( theta ) * sin( phi )
     * z = r * cos( phi )
     * over theta in [ 0, 2 * PI ] and phi in [ 0, PI ]
     * </pre>
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITSA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( ( stacks < 2 ) || ( slices < 3 ) )
        {
            throw new IllegalArgumentException( "insufficient stacks or slices" );
        }
        
        Point3f[] vertices = null;
        int[] indices = null;
        Vector3f[] normals = null;
        TexCoord2f[] texCoords = null;
        
        final int stackLen = slices * 2 + 2;
        
        if ( ( features & Geometry.NORMALS ) > 0 )
            normals = new Vector3f[ ( slices + 1 ) * ( stacks + 1 ) ];
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texCoordsSize == 2 )
                texCoords = new TexCoord2f[ ( slices + 1 ) * ( stacks + 1 ) ];
        }
        
        vertices = new Point3f[ ( slices + 1 ) * ( stacks + 1 ) ];
        indices = new int[ stackLen * stacks ];
        for ( int j = 0; j < stacks + 1; j++ )
        {
            float angleXZl = (float)j * FastMath.PI_HALF / (float)stacks;
            
            final float low = FastMath.sin( angleXZl );
            
            for ( int i = 0; i < slices + 1; i++ )
            {
                float angleXY = (float)i * FastMath.TWO_PI / (float)slices;
                
                float x = FastMath.cos( angleXY );
                float y = FastMath.sin( angleXY );
                
                float cl = FastMath.cos( angleXZl );
                
                final int k = ( j * ( slices + 1 ) ) + i;
                vertices[ k ] = new Point3f( x * cl, low, -y * cl );
                
                if ( j < stacks )
                {
                    final int idx = ( j * stackLen ) + i * 2;
                    indices[ idx + 0 ] = k;
                    indices[ idx + 1 ] = k + slices + 1;
                }
                
                if ( normals != null )
                {
                    normals[ k ] = new Vector3f( vertices[ k ] );
                    //normals[ k ].normalize(); // it's already unit-length
                }
                
                if ( texCoords != null )
                {
                    final float tx = (float)i * 1.0f / (float)slices;
                    texCoords[ k ] = new TexCoord2f( tx, (float)( j + 0 ) * 1.0f / (float)stacks );
                }
            }
        }
        
        Colorf[] colors = null;
        if ( ( features & Geometry.COLORS ) != 0 )
            colors = GeomFactory.generateColors( colorAlpha, vertices );
        
        int[] stripLengths = new int[ stacks ];
        for ( int i = 0; i < stacks; i++ )
        {
            stripLengths[ i ] = indices.length / stacks;
        }
        
        return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, vertices, normals, texCoords, colors, indices, stripLengths ) );
    }
    
    public static IndexedTriangleStripArray createGeometryITSA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createIndexedTriangleStripArray( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for an IndexedTriangleArray
     * for a Raster Shape3D.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2ITA( gcITSA ) );
    }
    
    /**
     * Creates an IndexedTriangleArray for a Hemisphere Shape3D.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleArray createGeometryITA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createITAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for a TriangleStripArray
     * for a Raster Shape3D.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTSA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TSA( gcITSA ) );
    }
    
    /**
     * Creates a TriangleStripArray for a Hemisphere Shape3D.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleStripArray createGeometryTSA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTSAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for a TriangleArray
     * for a Raster Shape3D.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TA( gcITSA ) );
    }
    
    /**
     * Creates a TriangleArray for a Hemisphere Shape3D.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleArray createGeometryTA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates the GeometryArray for a Sphere.
     * 
     * <pre>
     * Parametric equations:
     * x = r * cos( theta ) * sin( phi )
     * y = r * sin( theta ) * sin( phi )
     * z = r * cos( phi )
     * over theta in [ 0, 2 * PI ] and phi in [ 0, PI ]
     * </pre>
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static Geometry createGeometry( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
                return ( createGeometryITSA( slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            case INDEXED_TRIANGLE_ARRAY:
                return ( createGeometryITA( slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_STRIP_ARRAY:
                return ( createGeometryTSA( slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * Creates the GemetryConstruct for the upper hemisphere.
     * 
     * <pre>
     * Parametric equations:
     * x = r * cos( theta ) * sin( phi )
     * y = r * sin( theta ) * sin( phi )
     * z = r * cos( phi )
     * over theta in [ 0, 2 * PI ] and phi in [ 0, PI ]
     * </pre>
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    /*
    public static GeometryConstruct createGeometryConstructTA( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ((stacks < 2) || (slices < 3))
        {
            throw new IllegalArgumentException( "insufficient stacks or slices" ) );
        }
        
        final Point3f[][] buffer = new Point3f[ 2 ][ stacks + 1 ]; // Store the vertices in buffers
        final TexCoord2f[][] texbuf = new TexCoord2f[ 2 ][ stacks + 1 ];
        
        stacks *= 2;
        
        // Init the left buffers
        for (int j = 0; j <= stacks / 2; j++)
        {
            buffer[ 0 ][ j ] = new Point3f();
            buffer[ 0 ][ j ].x = FastMath.sin( j * FastMath.PI / stacks );
            buffer[ 0 ][ j ].y = FastMath.cos( j * FastMath.PI / stacks );
            buffer[ 0 ][ j ].z = 0.0f;
            
            texbuf[ 0 ][ j ] = new TexCoord2f();
            texbuf[ 0 ][ j ].x = 1.0f;
            texbuf[ 0 ][ j ].y = 1f - 1f * j / (stacks / 2f);
        }
        
        final Point3f[] vertices = new Point3f[ 3 * slices + 3 * slices * 2 * ((stacks / 2) - 1) ];
        final Vector3f[] normals = new Vector3f[ 3 * slices + 3 * slices * 2 * ((stacks / 2) - 1) ];
        final TexCoord2f[] texCoords = new TexCoord2f[ 3 * slices + 3 * slices * 2 * ((stacks / 2) - 1) ];
        
        int left, right; // select the appropriate buffer
        int index = 0;
        for (int i = 0; i < slices; i++)
        {
            // auto-swap buffers
            left = i % 2;
            right = (i + 1) % 2;
            
            // Start the right column
            buffer[ right ][ 0 ] = new Point3f( 0f, 1f, 0f );
            texbuf[ right ][ 0 ] = new TexCoord2f( 1f * (i + 1) / slices, 1 );
            
            for (int j = 0; j < stacks / 2; j++)
            {
                // next right point
                buffer[ right ][ j + 1 ] = new Point3f();
                buffer[ right ][ j + 1 ].x = FastMath.cos( 2f * (i + 1) * FastMath.PI / slices ) *
                                             FastMath.sin( (j + 1) * FastMath.PI / stacks);
                buffer[ right ][ j + 1 ].y = buffer[ left ][ j + 1 ].y;
                buffer[ right ][ j + 1 ].z = FastMath.sin( 2f * (i + 1) * FastMath.PI / slices) *
                                             FastMath.sin( (j + 1) * FastMath.PI / stacks );
                texbuf[ right ][ j + 1 ] = new TexCoord2f( 1f - (1f * (i + 1) / slices), 1 - 1f * (j + 1) / (stacks / 2f) );
                
                if (j > 0)
                {
                    vertices[ index ] = new Point3f( buffer[ left ][ j ] );
                    normals[ index ] = new Vector3f( vertices[ index ] );
                    normals[ index ].normalize();
                    texCoords[ index++ ] = new TexCoord2f( texbuf[ left ][ j ] );
                    
                    vertices[ index ] = new Point3f( buffer[ left ][ j + 1 ] );
                    normals[ index ] = new Vector3f( vertices[ index ] );
                    normals[ index ].normalize();
                    texCoords[ index++ ] = new TexCoord2f( texbuf[ left ][ j + 1 ] );
                    
                    vertices[ index ] = new Point3f( buffer[ right ][ j ] );
                    normals[ index ] = new Vector3f( vertices[ index ] );
                    normals[ index ].normalize();
                    texCoords[ index++ ] = new TexCoord2f( texbuf[ right ][ j ] );
                }
                
                vertices[ index ] = new Point3f( buffer[ right ][ j ] );
                normals[ index ] = new Vector3f( vertices[ index ] );
                normals[ index ].normalize();
                texCoords[ index++ ] = new TexCoord2f( texbuf[ right ][ j ] );
                
                vertices[ index ] = new Point3f( buffer[ left ][ j + 1 ] );
                normals[ index ] = new Vector3f( vertices[ index ] );
                normals[ index ].normalize();
                texCoords[ index++ ] = new TexCoord2f( texbuf[ left ][ j + 1 ] );
                
                vertices[ index ] = new Point3f( buffer[ right ][ j + 1 ] );
                normals[ index ] = new Vector3f( vertices[ index ] );
                normals[ index ].normalize();
                texCoords[ index++ ] = new TexCoord2f( texbuf[ right ][ j + 1 ] );
            }
        }
        
        return ( new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, vertices, normals, texCoords ) );
    }
    
    public static TriangleArray createGeometryTA(int slices, int stacks, int features)
    {
        features = features | GeometryArray.COORDINATES;
        
        GeometryConstruct gc = createGeometryConstructTA( slices, stacks, features );
        
        return ( GeomFactory.createTriangleArray( gc ) );
    }
    
    public static GeometryArray createGeometry(int slices, int stacks, int features)
    {
        switch (getGeometryConstructionTypeHint())
        {
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( slices, stacks, features ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName()  + " creation is not yet implemented." ) );
        }
    }
    */

    /**
     * Creates a hemisphere using standard specifications.
     * 
     * <pre>
     * Parametric equations:
     * x=r*cos(theta)*sin(phi)
     * y=r*sin(theta)*sin(phi)
     * z=r*cos(phi)
     * over theta in [0,2*PI] and phi in [0,PI]
     * </pre>
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Hemisphere( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( createGeometry( slices, stacks, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Hemisphere( float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( slices, stacks, features, colorAlpha, texCoordsSize );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Hemisphere( float radius, int slices, int stacks, Texture texture )
    {
        this( radius, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Hemisphere( float radius, int slices, int stacks, String texture )
    {
        this( radius, slices, stacks, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Hemisphere( int slices, int stacks, Texture texture )
    {
        this( slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Hemisphere( int slices, int stacks, String texture )
    {
        this( slices, stacks, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param color the color to apply to the Sphere's Appearance
     */
    public Hemisphere( int slices, int stacks, Colorf color )
    {
        this( slices, stacks, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param color the color to apply to the Sphere's Appearance
     */
    public Hemisphere( float radius, int slices, int stacks, Colorf color )
    {
        this( slices, stacks, color );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param app the Appearance to be applied to this Shape
     */
    public Hemisphere( int slices, int stacks, Appearance app )
    {
        this( slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Creates a hemisphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param app the Appearance to be applied to this Shape
     */
    public Hemisphere( float radius, int slices, int stacks, Appearance app )
    {
        this( slices, stacks, app );
        
        StaticTransform.scale( this, radius );
    }
}
