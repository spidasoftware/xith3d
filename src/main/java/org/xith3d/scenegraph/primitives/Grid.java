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
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleStripArray;

/**
 * This is a simple implementation of a homogenous vertex/triangle grid.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Grid extends Shape3D
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
                throw new UnsupportedOperationException( "Currently " + Cylinder.class.getSimpleName() + " does not support " + hint );
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
     * Creates a GeometryConstruct for an IndexedTriangleStripArray
     * for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITSA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( ( resX < 1 ) || ( resY < 1 ) )
        {
            throw new IllegalArgumentException( "insufficient resolution" );
        }
        
        Point3f[] vertices = null;
        Vector3f[] normals = null;
        TexCoord2f[] texCoords = null;
        
        vertices = new Point3f[ ( resX + 1 ) * ( resY + 1 ) ];
        
        if ( ( features & Geometry.NORMALS ) > 0 )
            normals = new Vector3f[ vertices.length ];
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) > 0 )
        {
            if ( texCoordsSize == 2 )
                texCoords = new TexCoord2f[ vertices.length ];
        }
        
        int k = 0;
        for ( int j = 0; j < resY + 1; j++ )
        {
            for ( int i = 0; i < resX + 1; i++ )
            {
                vertices[ k ] = new Point3f( width * ( -0.5f + (float)i / (float)resX ), height * ( -0.5f + (float)j / (float)resY ), 0.0f );
                
                if ( normals != null )
                {
                    normals[ k ] = new Vector3f( 0f, 0f, 1f );
                }
                
                if ( texCoords != null )
                {
                    final float tx = (float)i * 1.0f / (float)resX;
                    final float ty = (float)j * 1.0f / (float)resY;
                    texCoords[ k ] = new TexCoord2f( tx, ty );
                }
                
                k++;
            }
        }
        
        final int stripLength = ( 2 + ( resX * 2 ) );
        final int[] indices = new int[ stripLength * resY ];
        
        for ( int j = 0; j < resY; j++ )
        {
            for ( int i = 0; i < resX + 1; i++ )
            {
                k = j * stripLength + i * 2;
                
                // Flip each second strip to avoid visible connections!
                if ( ( j % 2 ) == 0 )
                {
                    indices[ k + 0 ] = ( j + 0 ) * ( resX + 1 ) + i;
                    indices[ k + 1 ] = ( j + 1 ) * ( resX + 1 ) + i;
                }
                else
                {
                    indices[ k + 0 ] = ( j + 0 ) * ( resX + 1 ) + ( resX - i );
                    indices[ k + 1 ] = ( j + 1 ) * ( resX + 1 ) + ( resX - i );
                }
            }
        }
        
        Colorf[] colors = null;
        if ( ( features & Geometry.COLORS ) != 0 )
            colors = GeomFactory.generateColors( colorAlpha, vertices );
        
        int[] stripLengths = new int[ resY ];
        for ( int i = 0; i < resY; i++ )
        {
            stripLengths[ i ] = stripLength;
        }
        //stripLengths = new int[] { vertices.length };
        
        return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, vertices, normals, texCoords, colors, indices, stripLengths ) );
    }
    
    /**
     * Creates an IndexedTriangleStripArray for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleStripArray createGeometryITSA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createIndexedTriangleStripArray( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for an IndexedTriangleArray
     * for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2ITA( gcITSA ) );
    }
    
    /**
     * Creates an IndexedTriangleArray for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleArray createGeometryITA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createITAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for a TriangleStripArray
     * for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTSA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TSA( gcITSA ) );
    }
    
    /**
     * Creates a TriangleStripArray
     * for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleStripArray createGeometryTSA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTSAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for a TriangleArray
     * for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TA( gcITSA ) );
    }
    
    /**
     * Creates a TriangleArray for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleArray createGeometryTA( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates a GeometryArray for a Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static Geometry createGeometry( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
                return ( createGeometryITSA( width, height, resX, resY, features, colorAlpha, texCoordsSize ) );
                
            case INDEXED_TRIANGLE_ARRAY:
                return ( createGeometryITA( width, height, resX, resY, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_STRIP_ARRAY:
                return ( createGeometryTSA( width, height, resX, resY, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( width, height, resX, resY, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * Creates a new Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Grid( float width, float height, int resX, int resY, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( createGeometry( width, height, resX, resY, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Creates a new Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param texture the texture to be applied
     */
    public Grid( float width, float height, int resX, int resY, Texture texture )
    {
        this( width, height, resX, resY, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Creates a new Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param texture the texture to be applied
     */
    public Grid( float width, float height, int resX, int resY, String texture )
    {
        this( width, height, resX, resY, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Creates a new Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param color the color to be applied to the shape's ColoringAttributes
     */
    public Grid( float width, float height, int resX, int resY, Colorf color )
    {
        this( width, height, resX, resY, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Creates a new Raster Shape3D.
     * 
     * @param width the absolute width of the whole Raster
     * @param height the absolute height of the Whole Raster
     * @param resX the x-resolution of the Raster (number of rectangles)
     * @param resY the y-resolution of the Raster (number of rectangles)
     * @param app the Appearance to be applied to the shape
     */
    public Grid( float width, float height, int resX, int resY, Appearance app )
    {
        this( width, height, resX, resY, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
}
