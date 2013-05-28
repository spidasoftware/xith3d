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
import org.openmali.vecmath2.TexCoord3f;
import org.openmali.vecmath2.Tuple3f;
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

/**
 * A sphere using standard specifications.
 * 
 * @author Daniel Herring
 * @author Marvin Froehlich (aka Qudus)
 */
public class Torus extends Shape3D
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
                throw new UnsupportedOperationException( "Currently " + Torus.class.getSimpleName() + " does not support " + hint );
        }
    }
    
    /**
     * @return the hint for this ShapeType's Geometry to be constructed of a certain type.
     */
    public static GeometryType getGeometryConstructionTypeHint()
    {
        return ( geomConstructTypeHint );
    }
    
    private static void rotateTupleZ( Tuple3f t, float angle )
    {
        final float sin = FastMath.sin( angle );
        final float cos = FastMath.cos( angle );
        
        //t.set( t.getX() * cos - -t.getZ() * sin, t.getY(), t.getX() * sin + -t.getZ() * cos);
        t.set( t.getX() * cos - t.getY() * sin, t.getX() * sin + t.getY() * cos, t.getZ() );
    }
    
    /**
     * Creates the GeometryConstruct for a Torus made of an IndexedTriangleStripArray.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITSA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( ( radSlices < 3 ) || ( conSlices < 3 ) )
        {
            throw new IllegalArgumentException( "radSlices and conSlices <=3" );
        }
        
        final float radius = ( 1.0f - alpha ) / 2.0f;
        
        Point3f[] vertices = null;
        int[] indices = null;
        Vector3f[] normals = null;
        TexCoord2f[] texCoords2 = null;
        TexCoord3f[] texCoords3 = null;
        
        final int stackLen = radSlices * 2 + 2;
        
        vertices = new Point3f[ ( radSlices + 1 ) * ( conSlices + 1 ) ];
        indices = new int[ stackLen * conSlices ];
        
        if ( ( features & Geometry.NORMALS ) > 0 )
            normals = new Vector3f[ vertices.length ];
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texCoordsSize == 2 )
                texCoords2 = new TexCoord2f[ vertices.length ];
            else if ( texCoordsSize == 3 )
                texCoords3 = new TexCoord3f[ vertices.length ];
        }
        
        for ( int j = 0; j < conSlices + 1; j++ )
        {
            float angleXZl = (float)j * FastMath.TWO_PI / (float)conSlices;
            
            for ( int i = 0; i < radSlices + 1; i++ )
            {
                float angleXY = (float)i * FastMath.TWO_PI / (float)radSlices;
                
                float x = FastMath.cos( angleXY );
                float y = FastMath.sin( angleXY );
                
                final int k = ( j * ( radSlices + 1 ) ) + i;
                vertices[ k ] = new Point3f( alpha + radius + x * radius, 0.0f, -y * radius );
                rotateTupleZ( vertices[ k ], angleXZl );
                
                if ( j < conSlices )
                {
                    final int idx = ( j * stackLen ) + i * 2;
                    indices[ idx + 0 ] = k;
                    indices[ idx + 1 ] = k + radSlices + 1;
                }
                
                if ( normals != null )
                {
                    normals[ k ] = new Vector3f( x * radius, 0.0f, -y * radius );
                    rotateTupleZ( normals[ k ], angleXZl );
                    normals[ k ].normalize();
                }
                
                if ( texCoords2 != null )
                {
                    final float tx = (float)i * 1.0f / (float)radSlices;
                    texCoords2[ k ] = new TexCoord2f( tx, (float)( j + 0 ) * 1.0f / (float)conSlices );
                }
                
                if ( texCoords3 != null )
                {
                    final float tx = (float)i * 1.0f / (float)radSlices;
                    texCoords3[ k ] = new TexCoord3f( tx, (float)( j + 0 ) * 1.0f / (float)conSlices, 0.0f );
                }
            }
        }
        
        Colorf[] colors = null;
        if ( ( features & Geometry.COLORS ) != 0 )
            colors = GeomFactory.generateColors( colorAlpha, vertices );
        
        int[] stripLengths = new int[ conSlices ];
        for ( int i = 0; i < conSlices; i++ )
        {
            stripLengths[ i ] = indices.length / conSlices;
        }
        
        if ( texCoords3 != null )
            return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, vertices, normals, texCoords3, colors, indices, stripLengths ) );
        
        return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, vertices, normals, texCoords2, colors, indices, stripLengths ) );
    }
    
    /**
     * Creates the IndexedTriangleStripArray for a Torus.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATES | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleStripArray createGeometryITSA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createIndexedTriangleStripArray( gcITSA ) );
    }
    
    /**
     * Creates the GeometryConstruct for a Torus made of an IndexedTriangleArray.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2ITA( gcITSA ) );
    }
    
    /**
     * Creates the IndexedTriangleArray for a Torus.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleArray createGeometryITA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createITAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates the GeometryConstruct for a Torus made of a TriangleStripArray.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTSA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TSA( gcITSA ) );
    }
    
    /**
     * Creates the TriangleStripArray for a Torus.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleStripArray createGeometryTSA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTSAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates the GeometryConstruct for a Torus made of a TriangleArray.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TA( gcITSA ) );
    }
    
    /**
     * Creates the TriangleArray for a Torus.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleArray createGeometryTA( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features = features | Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates the GeometryArray for a Torus.
     * 
     * @param alpha Ratio of ring thickness to ring diameter (alpha=.5 -> donut shape)
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static Geometry createGeometry( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
                return ( createGeometryITSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize ) );
                
            case INDEXED_TRIANGLE_ARRAY:
                return ( createGeometryITA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_STRIP_ARRAY:
                return ( createGeometryTSA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * Creates a torus.
     */
    private Torus( Geometry geom )
    {
        super( geom );
    }
    
    /**
     * Creates a torus.
     * 
     * Generating equations:
     * x = (rc + rr * cos( v )) * cos( u )
     * y = (rc + rr * cos( v )) * sin( u )
     * z = rr * sin( v )
     * 
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Torus( float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( createGeometry( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Creates a torus.
     * 
     * @param radius the outer radius of the Torus
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Torus( float radius, float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * Creates a torus.
     * 
     * Generating equations:
     * x = (rc + rr * cos( v )) * cos( u )
     * y = (rc + rr * cos( v )) * sin( u )
     * z = rr * sin( v )
     * 
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Torus( float alpha, int radSlices, int conSlices, Texture texture )
    {
        this( alpha, radSlices, conSlices, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Creates a torus.
     * 
     * @param radius the outer radius of the Torus
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Torus( float radius, float alpha, int radSlices, int conSlices, Texture texture )
    {
        this( alpha, radSlices, conSlices, texture );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * Creates a torus.
     * 
     * Generating equations:
     * x = (rc + rr * cos( v )) * cos( u )
     * y = (rc + rr * cos( v )) * sin( u )
     * z = rr * sin( v )
     * 
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Torus( float alpha, int radSlices, int conSlices, String texture )
    {
        this( alpha, radSlices, conSlices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Creates a torus.
     * 
     * @param radius the outer radius of the Torus
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Torus( float radius, float alpha, int radSlices, int conSlices, String texture )
    {
        this( radius, alpha, radSlices, conSlices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Creates a torus.
     * 
     * Generating equations:
     * x = (rc + rr * cos( v )) * cos( u )
     * y = (rc + rr * cos( v )) * sin( u )
     * z = rr * sin( v )
     * 
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param color the color to be applied to the Shape's Appearance's ColoringAttributes
     */
    public Torus( float alpha, int radSlices, int conSlices, Colorf color )
    {
        this( alpha, radSlices, conSlices, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( ( color != null ) && ( color.hasAlpha() ) )
            getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Creates a torus.
     * 
     * @param radius the outer radius of the Torus
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param color the color to be applied to the Shape's Appearance's ColoringAttributes
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Torus( float radius, float alpha, int radSlices, int conSlices, Colorf color )
    {
        this( alpha, radSlices, conSlices, color );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * Creates a torus.
     * 
     * Generating equations:
     * x = (rc + rr * cos( v )) * cos( u )
     * y = (rc + rr * cos( v )) * sin( u )
     * z = rr * sin( v )
     * 
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param app the Appearance to be applied to this Shape
     */
    public Torus( float alpha, int radSlices, int conSlices, Appearance app )
    {
        this( alpha, radSlices, conSlices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Creates a torus.
     * 
     * @param radius the outer radius of the Torus
     * @param alpha 0.5 for donut shape, 0.25 for a smaller hole
     * @param radSlices Number of radial slices (from the center). Must be >=3
     * @param conSlices Number of concentric slices in the ring. Must be >=3
     * @param app the Appearance to be applied to this Shape
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Torus( float radius, float alpha, int radSlices, int conSlices, Appearance app )
    {
        this( alpha, radSlices, conSlices, app );
        
        StaticTransform.scale( this, radius );
    }
}
