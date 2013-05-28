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
import org.openmali.vecmath2.Vector3f;

import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.atmosphere.AtmosphereFactory;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loop.Updater;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.PointLight;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * A sphere using standard specifications.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Sphere extends Shape3D
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
    
    // hehe, I got this by accident. A McDonald's head ;-)
    /*
    final int stackLen = slices * 2 + 2;
    vertices = new Point3f[ stackLen * stacks ];
    for (int j = 0; j < stacks; j++)
    {
        float angleXZl = ((float)j - ((float)stacks / 2.0f)) * FastMath.PI / (float)stacks;
        float angleXZh = ((float)j + 1.0f - ((float)stacks / 2.0f)) * FastMath.PI / (float)stacks;
        
        final float low  = FastMath.sin( angleXZl );
        final float high = FastMath.sin( angleXZh );
        
        for (int i = 0; i < slices + 1; i++)
        {
            float angleXY = (float)i * FastMath.TWO_PI / (float)slices;
            
            float x = FastMath.cos( angleXY );
            float y = FastMath.sin( angleXY );
            
            float sl = FastMath.sin( angleXZl );
            float cl = FastMath.cos( angleXZl );
            float sh = FastMath.sin( angleXZh );
            float ch = FastMath.cos( angleXZh );
            
            final int k = (j * stackLen) + i * 2;
            vertices[ k + 0 ] = new Point3f( x, low * sl, -y * cl );
            vertices[ k + 1 ] = new Point3f( x, high * sh, -y * ch );
        }
    }
    */

    /**
     * Creates the GemetryConstruct for a sphere.
     * 
     * <pre>
     * Parametric equations:
     * x = r * cos( theta ) * sin( phi )
     * y = r * sin( theta ) * sin( phi )
     * z = r * cos( phi )
     * over theta in [ 0, 2 * PI ] and phi in [ 0, PI ]
     * </pre>
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITSA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( ( stacks < 2 ) || ( slices < 3 ) )
        {
            throw new IllegalArgumentException( "insufficient stacks or slices" );
        }
        
        Point3f[] coords = null;
        int[] indices = null;
        Vector3f[] normals = null;
        TexCoord2f[] texCoords2 = null;
        TexCoord3f[] texCoords3 = null;
        
        final int stackLen = slices * 2 + 2;
        
        if ( ( features & Geometry.NORMALS ) > 0 )
            normals = new Vector3f[ ( slices + 1 ) * ( stacks + 1 ) ];
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texCoordsSize == 2 )
                texCoords2 = new TexCoord2f[ ( slices + 1 ) * ( stacks + 1 ) ];
            else if ( texCoordsSize == 3 )
                texCoords3 = new TexCoord3f[ ( slices + 1 ) * ( stacks + 1 ) ];
        }
        
        coords = new Point3f[ ( slices + 1 ) * ( stacks + 1 ) ];
        indices = new int[ stackLen * stacks ];
        for ( int j = 0; j < stacks + 1; j++ )
        {
            float angleXZl = ( (float)j - ( (float)stacks / 2.0f ) ) * FastMath.PI / (float)stacks;
            
            final float low = FastMath.sin( angleXZl );
            
            for ( int i = 0; i < slices + 1; i++ )
            {
                float angleXY = (float)i * FastMath.TWO_PI / (float)slices;
                
                float x = FastMath.cos( angleXY );
                float y = FastMath.sin( angleXY );
                
                float cl = FastMath.cos( angleXZl );
                
                final int k = ( j * ( slices + 1 ) ) + i;
                coords[ k ] = new Point3f( x * cl * radius, low * radius, -y * cl * radius );
                
                if ( j < stacks )
                {
                    final int idx = ( j * stackLen ) + i * 2;
                    indices[ idx + 0 ] = k;
                    indices[ idx + 1 ] = k + slices + 1;
                }
                
                if ( normals != null )
                {
                    normals[ k ] = new Vector3f( coords[ k ] );
                    normals[ k ].normalize();
                }
                
                if ( texCoords2 != null )
                {
                    final float tx = (float)i * 1.0f / (float)slices;
                    texCoords2[ k ] = new TexCoord2f( tx, (float)( j + 0 ) * 1.0f / (float)stacks );
                }
                
                if ( texCoords3 != null )
                {
                    final float tx = (float)i * 1.0f / (float)slices;
                    texCoords3[ k ] = new TexCoord3f( tx, (float)( j + 0 ) * 1.0f / (float)stacks, 0.0f );
                }
            }
        }
        
        Colorf[] colors = null;
        if ( ( features & Geometry.COLORS ) != 0 )
            colors = GeomFactory.generateColors( colorAlpha, coords );
        
        int[] stripLengths = new int[ stacks ];
        for ( int i = 0; i < stacks; i++ )
        {
            stripLengths[ i ] = indices.length / stacks;
        }
        
        if ( ( centerX != 0f ) || ( centerY != 0f ) || ( centerZ != 0f ) )
        {
            for ( int i = 0; i < coords.length; i++ )
            {
                coords[ i ].add( centerX, centerY, centerZ );
            }
        }
        
        if ( texCoords3 != null )
            return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, coords, normals, texCoords3, colors, indices, stripLengths ) );
        
        return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, coords, normals, texCoords2, colors, indices, stripLengths ) );
    }
    
    /**
     * Creates an IndexedTriangleStripArray for a Sphere Shape3D.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleStripArray createGeometryITSA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createIndexedTriangleStripArray( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for an IndexedTriangleArray
     * for a Raster Shape3D.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2ITA( gcITSA ) );
    }
    
    /**
     * Creates an IndexedTriangleArray for a Sphere Shape3D.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleArray createGeometryITA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createITAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for a TriangleStripArray
     * for a Raster Shape3D.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTSA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TSA( gcITSA ) );
    }
    
    /**
     * Creates a TriangleStripArray for a Sphere Shape3D.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleStripArray createGeometryTSA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTSAfromITSA( gcITSA ) );
    }
    
    /**
     * Creates a GeometryConstruct for a TriangleArray
     * for a Raster Shape3D.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.convertGeometryConstructITSA2TA( gcITSA ) );
    }
    
    /**
     * Creates a TriangleArray for a Sphere Shape3D.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleArray createGeometryTA( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gcITSA = createGeometryConstructITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize );
        
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
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static Geometry createGeometry( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
                return ( createGeometryITSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            case INDEXED_TRIANGLE_ARRAY:
                return ( createGeometryITA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_STRIP_ARRAY:
                return ( createGeometryTSA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    private float radius;
    
    public void setRadius( float radius, boolean scaleGeometry )
    {
        if ( radius == this.radius )
            return;
        
        float scale = radius / this.radius;
        
        this.radius = radius;
        
        if ( scaleGeometry )
            StaticTransform.scale( this, scale );
    }
    
    public final void setRadius( float radius )
    {
        setRadius( radius, true );
    }
    
    public final float getRadius()
    {
        return ( radius );
    }
    
    /**
     * Prepares this Sphere to have an atmosphere.
     * 
     * @param percentalAtmosphereRadius the additional radius of the atmosphere in percent to the sphere's radius.
     * this could be something like 1.05f.
     * @param light
     * @param updater
     */
    public void addAtmosphere( float percentalAtmosphereRadius, PointLight light, Updater updater )
    {
        AtmosphereFactory atmosFact = EffectFactory.getInstance().getAtmosphereFactory();
        
        if ( atmosFact == null )
            throw new Error( "No AtmosphereFactory registered at org.xith3d.effects.EffectFactory!" );
        
        atmosFact.prepareAtmosphere( this, getRadius() * percentalAtmosphereRadius, light, updater );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * <pre>
     * Parametric equations:
     * x=r*cos(theta)*sin(phi)
     * y=r*sin(theta)*sin(phi)
     * z=r*cos(phi)
     * over theta in [0,2*PI] and phi in [0,PI]
     * </pre>
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Sphere( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( createGeometry( centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize ) );
        
        this.radius = radius;
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Sphere( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, Texture texture )
    {
        this( centerX, centerY, centerZ, radius, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Sphere( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, String texture )
    {
        this( centerX, centerY, centerZ, radius, slices, stacks, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param app the new Sphere's Appearance
     */
    public Sphere( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, Appearance app )
    {
        this( centerX, centerY, centerZ, radius, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        this.setAppearance( app );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param color the color to apply to the Sphere's Appearance
     */
    public Sphere( float centerX, float centerY, float centerZ, float radius, int slices, int stacks, Colorf color )
    {
        this( centerX, centerY, centerZ, radius, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Creates a sphere using standard specifications.
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
    public Sphere( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( 0f, 0f, 0f, 1f, slices, stacks, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Creates a sphere using standard specifications.
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
    public Sphere( float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( 0f, 0f, 0f, radius, slices, stacks, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Sphere( float radius, int slices, int stacks, Texture texture )
    {
        this( 0f, 0f, 0f, radius, slices, stacks, texture );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Sphere( float radius, int slices, int stacks, String texture )
    {
        this( 0f, 0f, 0f, radius, slices, stacks, texture );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param app the new Sphere's Appearance
     */
    public Sphere( float radius, int slices, int stacks, Appearance app )
    {
        this( 0f, 0f, 0f, radius, slices, stacks, app );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Sphere( int slices, int stacks, Texture texture )
    {
        this( 0f, 0f, 0f, 1f, slices, stacks, texture );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Sphere( int slices, int stacks, String texture )
    {
        this( 0f, 0f, 0f, 1f, slices, stacks, texture );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param app the new Sphere's Appearance
     */
    public Sphere( int slices, int stacks, Appearance app )
    {
        this( 0f, 0f, 0f, 1f, slices, stacks, app );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param color the color to apply to the Sphere's Appearance
     */
    public Sphere( int slices, int stacks, Colorf color )
    {
        this( 0f, 0f, 0f, 1f, slices, stacks, color );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param radius amount to enlarge the sphere by
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param color the color to apply to the Sphere's Appearance
     */
    public Sphere( float radius, int slices, int stacks, Colorf color )
    {
        this( 0f, 0f, 0f, radius, slices, stacks, color );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param sampleSphere the sphere-body to create this scenegraph-sphere-primitive from
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public static Sphere createFromSphereBody( org.openmali.spatial.bodies.Sphere sampleSphere, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( new Sphere( sampleSphere.getCenterX(), sampleSphere.getCenterY(), sampleSphere.getCenterZ(), sampleSphere.getRadius(), slices, stacks, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param sampleSphere the sphere-body to create this scenegraph-sphere-primitive from
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public static Sphere createFromSphereBody( org.openmali.spatial.bodies.Sphere sampleSphere, int slices, int stacks, Texture texture )
    {
        return ( new Sphere( sampleSphere.getCenterX(), sampleSphere.getCenterY(), sampleSphere.getCenterZ(), sampleSphere.getRadius(), slices, stacks, texture ) );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param sampleSphere the sphere-body to create this scenegraph-sphere-primitive from
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public static Sphere createFromSphereBody( org.openmali.spatial.bodies.Sphere sampleSphere, int slices, int stacks, String texture )
    {
        return ( new Sphere( sampleSphere.getCenterX(), sampleSphere.getCenterY(), sampleSphere.getCenterZ(), sampleSphere.getRadius(), slices, stacks, texture ) );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param sampleSphere the sphere-body to create this scenegraph-sphere-primitive from
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param app the new Sphere's Appearance
     */
    public static Sphere createFromSphereBody( org.openmali.spatial.bodies.Sphere sampleSphere, int slices, int stacks, Appearance app )
    {
        return ( new Sphere( sampleSphere.getCenterX(), sampleSphere.getCenterY(), sampleSphere.getCenterZ(), sampleSphere.getRadius(), slices, stacks, app ) );
    }
    
    /**
     * Creates a sphere using standard specifications.
     * 
     * @param sampleSphere the sphere-body to create this scenegraph-sphere-primitive from
     * @param slices Number of vertical stripes down the sphere
     * @param stacks Number of stacked rings around the sphere
     * @param color the color to apply to the Sphere's Appearance
     */
    public static Sphere createFromSphereBody( org.openmali.spatial.bodies.Sphere sampleSphere, int slices, int stacks, Colorf color )
    {
        return ( new Sphere( sampleSphere.getCenterX(), sampleSphere.getCenterY(), sampleSphere.getCenterZ(), sampleSphere.getRadius(), slices, stacks, color ) );
    }
}
