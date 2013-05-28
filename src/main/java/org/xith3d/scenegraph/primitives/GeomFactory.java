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

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.TextureImage3D;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * Geometry Util class
 * 
 * @author William Denniss
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class GeomFactory
{
    /**
     * Swaps vertex 0 and 2 of each following 3.
     * 
     * @param ga
     */
    public static void reverseWinding( Geometry ga )
    {
        final float[] coords = ga.getCoordinatesData().getData();
        
        for ( int i = 0; i < coords.length / 9; i++ )
        {
            final int j = i * 9;
            
            float tempx = coords[ j + 6 ];
            float tempy = coords[ j + 7 ];
            float tempz = coords[ j + 8 ];
            
            coords[ j + 6 ] = coords[ j + 0 ];
            coords[ j + 7 ] = coords[ j + 1 ];
            coords[ j + 8 ] = coords[ j + 2 ];
            coords[ j + 0 ] = tempx;
            coords[ j + 1 ] = tempy;
            coords[ j + 2 ] = tempz;
            
        }
        
        ga.setCoordinate( 0, coords );
    }
    
    /**
     * Retrieves the GeometryArray features from the Appearance, that are at
     * least necessary to handle this Appearance.
     * 
     * @param app
     * 
     * @return the collected GoometryArray features
     */
    public static int getFeaturesFromAppearance( Appearance app )
    {
        int features = 0;
        
        if ( app == null )
        {
            return ( features );
        }
        
        if ( app.getTexture() != null )
        {
            if ( app.getTexture().getImage( 0 ) instanceof TextureImage3D )
                features |= Geometry.TEXTURE_COORDINATES;
            else //if (app.getTexture().getImage( 0 ) instanceof TextureImage2D)
                features |= Geometry.TEXTURE_COORDINATES;
        }
        
        return ( features );
    }
    
    /**
     * Retrieves the GeometryArray features from the Appearance, that are at
     * least necessary to handle this Appearance.
     * 
     * @param app
     * 
     * @return the collected GoometryArray features
     */
    public static int getTexCoordsSize( Appearance app )
    {
        if ( app.getTexture() != null )
        {
            if ( app.getTexture().getImage( 0 ) instanceof TextureImage3D )
                return ( 3 );
            //else if (app.getTexture().getImage( 0 ) instanceof TextureImage2D)
                return ( 2 );
        }
        
        return ( 0 );
    }
    
    /**
     * Auto-generates the normals.
     * 
     * @param vertices
     * 
     * @return the normals-array
     */
    public static Vector3f[] generateNaiveNormals( Tuple3f[] vertices )
    {
        final Vector3f[] normals = new Vector3f[ vertices.length ];
        
        for ( int i = 0; i < vertices.length; i++ )
        {
            normals[ i ] = new Vector3f( vertices[ i ] );
            normals[ i ].normalize();
        }
        
        return ( normals );
    }
    
    /**
     * Auto-generates the normals.
     * 
     * @param vertices
     * 
     * @return the normals-array
     */
    private static void generateNaiveNormals( Tuple3f[] vertices, Geometry trg )
    {
        Vector3f normal = new Vector3f();
        
        for ( int i = 0; i < vertices.length; i++ )
        {
            normal.set( vertices[ i ] );
            normal.normalize();
            
            trg.setNormal( i, normal );
        }
    }
    
    /**
     * Auto-generates the texture coordinates.
     * 
     * @param vertices
     * 
     * @return the tex-coords-array
     */
    public static TexCoord2f[] generateTexCoords2( Tuple3f[] vertices )
    {
        // Find the range of z values
        float zmin = Float.MAX_VALUE;
        float zmax = Float.MIN_VALUE;
        for ( int i = 0; i < vertices.length; i++ )
        {
            zmin = Math.min( zmin, vertices[ i ].getZ() );
            zmax = Math.max( zmax, vertices[ i ].getZ() );
        }
        final float zscale = 1 / ( zmax - zmin );
        
        // Generate the coordinates
        TexCoord2f[] texCoords = new TexCoord2f[ vertices.length ];
        for ( int i = 0; i < vertices.length; i++ )
        {
            texCoords[ i ] = new TexCoord2f();
            texCoords[ i ].setS( ( FastMath.atan2( vertices[ i ].getY(), vertices[ i ].getX() ) + FastMath.PI ) / FastMath.TWO_PI );
            texCoords[ i ].setT( zscale * ( vertices[ i ].getZ() - zmin ) );
        }
        
        return ( texCoords );
    }
    
    /**
     * Auto-generates the texture coordinates.
     * 
     * @param vertices
     * 
     * @return the tex-coords-array
     */
    public static TexCoord3f[] generateTexCoords3( Tuple3f[] vertices )
    {
        // Find the range of z values
        float zmin = Float.MAX_VALUE;
        float zmax = Float.MIN_VALUE;
        for ( int i = 0; i < vertices.length; i++ )
        {
            zmin = Math.min( zmin, vertices[ i ].getZ() );
            zmax = Math.max( zmax, vertices[ i ].getZ() );
        }
        final float zscale = 1.0f / ( zmax - zmin );
        
        // Generate the coordinates
        TexCoord3f[] texCoords = new TexCoord3f[ vertices.length ];
        for ( int i = 0; i < vertices.length; i++ )
        {
            texCoords[ i ] = new TexCoord3f();
            texCoords[ i ].setS( ( FastMath.atan2( vertices[ i ].getY(), vertices[ i ].getX() ) + FastMath.PI ) / FastMath.TWO_PI );
            texCoords[ i ].setT( zscale * ( vertices[ i ].getZ() - zmin ) );
            texCoords[ i ].setP( 0.0f ); // ?
        }
        
        return ( texCoords );
    }
    
    /**
     * Auto-generates the colors.
     * 
     * @param alpha
     * @param coords
     * @param scale
     * 
     * @return the colors-array
     */
    public static Colorf[] generateColors( boolean alpha, Tuple3f[] coords )
    {
        Colorf[] colors = new Colorf[ coords.length ];
        
        // Find the range of z values
        float xmin = +Float.MAX_VALUE;
        float xmax = -Float.MAX_VALUE;
        float ymin = +Float.MAX_VALUE;
        float ymax = -Float.MAX_VALUE;
        float zmin = +Float.MAX_VALUE;
        float zmax = -Float.MAX_VALUE;
        
        for ( int i = 0; i < coords.length; i++ )
        {
            if ( alpha )
                colors[ i ] = new Colorf( coords[ i ].getX(), coords[ i ].getY(), coords[ i ].getZ(), 0f );
            else
                colors[ i ] = new Colorf( coords[ i ].getX(), coords[ i ].getY(), coords[ i ].getZ() );
            
            xmin = Math.min( xmin, coords[ i ].getX() );
            xmax = Math.max( xmax, coords[ i ].getX() );
            ymin = Math.min( ymin, coords[ i ].getY() );
            ymax = Math.max( ymax, coords[ i ].getY() );
            zmin = Math.min( zmin, coords[ i ].getZ() );
            zmax = Math.max( zmax, coords[ i ].getZ() );
        }
        
        final float scaleX = ( xmax == xmin ) ? 1.0f : 1f / ( xmax - xmin );
        final float scaleY = ( ymax == ymin ) ? 1.0f : 1f / ( ymax - ymin );
        final float scaleZ = ( zmax == zmin ) ? 1.0f : 1f / ( zmax - zmin );
        
        for ( int i = 0; i < colors.length; i++ )
        {
            colors[ i ].setRed( ( colors[ i ].getRed() + xmin ) * scaleX );
            colors[ i ].setGreen( ( colors[ i ].getGreen() + ymin ) * scaleY );
            colors[ i ].setBlue( ( colors[ i ].getBlue() + zmin ) * scaleZ );
        }
        
        return ( colors );
    }
    
    /**
     * Creates the full-featured TriangleArray.
     * 
     * @param vertices
     * @param normals
     * @param colors
     * @param texCoords2
     * @param features
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @return the TriangleArray
     */
    public static TriangleArray createFullFeaturedTriangleArray( Tuple3f[] vertices, Vector3f[] normals, Colorf[] colors, TexCoord2f[] texCoords2, int features, boolean colorAlpha, int texCoordsSize )
    {
        final TriangleArray geom = new TriangleArray( vertices.length );
        
        geom.setCoordinates( 0, vertices );
        
        if ( normals != null )
        {
            geom.setNormals( 0, normals );
        }
        else if ( ( features & Geometry.NORMALS ) > 0 )
        {
            generateNaiveNormals( vertices, geom );
        }
        
        if ( colors != null )
        {
            geom.setColors( 0, colors );
        }
        else if ( ( ( features & Geometry.COLORS ) != 0 ) && !colorAlpha )
        {
            colors = new Colorf[ vertices.length ];
            for ( int i = 0; i < vertices.length; i++ )
            {
                colors[ i ] = new Colorf( vertices[ i ].getX(), vertices[ i ].getY(), vertices[ i ].getZ() );
                colors[ i ].mul( 2f ); // Why that?
            }
            
            geom.setColors( 0, colors );
        }
        else if ( ( ( features & Geometry.COLORS ) != 0 ) && colorAlpha )
        {
            colors = new Colorf[ vertices.length ];
            for ( int i = 0; i < vertices.length; i++ )
            {
                colors[ i ] = new Colorf( vertices[ i ].getX(), vertices[ i ].getY(), vertices[ i ].getZ(), 0f );
                colors[ i ].mul( 2f ); // Why that?
            }
            
            geom.setColors( 0, colors );
        }
        
        if ( texCoords2 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords2 );
        }
        else if ( ( features & Geometry.TEXTURE_COORDINATES ) > 0 )
        {
            if ( texCoordsSize == 2 )
                geom.setTextureCoordinates( 0, 0, generateTexCoords2( vertices ) );
        }
        
        return ( geom );
    }
    
    /**
     * Creates the full-featured TriangleArray.
     * 
     * @param geoms
     * 
     * @return the TriangleArray
     */
    protected static TriangleArray createFullFeaturedTriangleArray( GeometryConstruct[] geoms )
    {
        int numVertices = 0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            numVertices += geoms[ i ].getCoordinates().length;
        }
        
        Tuple3f[] vertices = new Tuple3f[ numVertices ];
        
        {
            int k = 0;
            for ( int i = 0; i < geoms.length; i++ )
            {
                final Tuple3f[] vs = geoms[ i ].getCoordinates();
                for ( int j = 0; j < vs.length; j++ )
                {
                    vertices[ k++ ] = vs[ j ];
                }
            }
        }
        
        int numNormals = 0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            if ( geoms[ i ].getNormals() != null )
                numNormals += geoms[ i ].getNormals().length;
        }
        
        Vector3f[] normals = null;
        if ( numNormals > 0 )
        {
            normals = new Vector3f[ numVertices ];
            
            int k = 0;
            for ( int i = 0; i < geoms.length; i++ )
            {
                final Vector3f[] ns = geoms[ i ].getNormals();
                if ( ns == null )
                {
                    for ( int j = 0; j < geoms[ i ].getCoordinates().length; j++ )
                    {
                        normals[ k++ ] = new Vector3f( geoms[ i ].getCoordinates()[ j ] );
                        normals[ k++ ].normalize();
                    }
                }
                else
                {
                    for ( int j = 0; j < geoms[ i ].getCoordinates().length; j++ )
                    {
                        normals[ k++ ] = ns[ j ];
                    }
                }
            }
        }
        
        int numTexCoords2 = 0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            if ( geoms[ i ].getTextureCoordinates2f() != null )
                numTexCoords2 += geoms[ i ].getTextureCoordinates2f().length;
        }
        
        TexCoord2f[] texCoords2 = null;
        if ( numTexCoords2 > 0 )
        {
            texCoords2 = new TexCoord2f[ numVertices ];
            
            int k = 0;
            for ( int i = 0; i < geoms.length; i++ )
            {
                TexCoord2f[] ts = geoms[ i ].getTextureCoordinates2f();
                if ( ts == null )
                {
                    ts = generateTexCoords2( vertices );
                }
                
                for ( int j = 0; j < geoms[ i ].getCoordinates().length; j++ )
                {
                    texCoords2[ k++ ] = ts[ j ];
                }
            }
        }
        
        int numTexCoords3 = 0;
        for ( int i = 0; i < geoms.length; i++ )
        {
            if ( geoms[ i ].getTextureCoordinates2f() != null )
                numTexCoords3 += geoms[ i ].getTextureCoordinates2f().length;
        }
        
        TexCoord3f[] texCoords3 = null;
        if ( numTexCoords3 > 0 )
        {
            texCoords3 = new TexCoord3f[ numVertices ];
            
            int k = 0;
            for ( int i = 0; i < geoms.length; i++ )
            {
                TexCoord3f[] ts = geoms[ i ].getTextureCoordinates3f();
                if ( ts == null )
                {
                    ts = generateTexCoords3( vertices );
                }
                
                for ( int j = 0; j < geoms[ i ].getCoordinates().length; j++ )
                {
                    texCoords3[ k++ ] = ts[ j ];
                }
            }
        }
        
        int numColors = 0;
        boolean alphaColors = false;
        for ( int i = 0; i < geoms.length; i++ )
        {
            if ( geoms[ i ].getColors() != null )
            {
                numColors += geoms[ i ].getColors().length;
                
                alphaColors = ( ( geoms[ i ].getColors()[ 0 ] != null ) && ( geoms[ i ].getColors()[ 0 ].hasAlpha() ) );
            }
        }
        
        Colorf[] colors = null;
        if ( numColors > 0 )
        {
            colors = new Colorf[ numVertices ];
            
            int k = 0;
            for ( int i = 0; i < geoms.length; i++ )
            {
                Colorf[] cs = geoms[ i ].getColors();
                if ( cs == null )
                {
                    cs = generateColors( alphaColors, vertices );
                }
                
                for ( int j = 0; j < geoms[ i ].getCoordinates().length; j++ )
                {
                    colors[ k++ ] = cs[ j ];
                }
            }
        }
        
        return ( createTriangleArray( vertices, normals, colors, texCoords2, texCoords3 ) );
    }
    
    /**
     * Calculates the GeometryArray features.
     * 
     * @param coords
     * @param normals
     * @param texCoords2
     * @param texCoords3
     * @param colors
     * 
     * @return the GeometryArray features
     */
    protected static int calculateFeatures( Tuple3f[] coords, Vector3f[] normals, Colorf[] colors, TexCoord2f[] texCoords2, TexCoord3f[] texCoords3 )
    {
        int features = Geometry.COORDINATES;
        
        if ( normals != null )
            features |= Geometry.NORMALS;
        
        if ( ( texCoords2 != null ) || ( texCoords3 != null ) )
            features |= Geometry.TEXTURE_COORDINATES;
        
        if ( colors != null )
            features |= Geometry.COLORS;
        
        return ( features );
    }
    
    /**
     * Creates a TriangleArray.
     * 
     * @param vertices
     * @param normals
     * @param texCoords2
     * @param texCoords3
     * @param colors
     * 
     * @return the TriangleArray
     */
    public static TriangleArray createTriangleArray( Tuple3f[] vertices, Vector3f[] normals, Colorf[] colors, TexCoord2f[] texCoords2, TexCoord3f[] texCoords3 )
    {
        final int features = calculateFeatures( vertices, normals, colors, texCoords2, texCoords3 );
        final boolean colorAlpha = ( colors != null ) ? colors[ 0 ].hasAlpha() : false;
        final int[] texCoordsSizes;
        if ( texCoords2 != null )
            texCoordsSizes = new int[] { 2 };
        else if ( texCoords3 != null )
            texCoordsSizes = new int[] { 3 };
        else
            texCoordsSizes = null;
        
        final TriangleArray geom = new TriangleArray( vertices.length );
        geom.makeInterleaved( features, colorAlpha, texCoordsSizes, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        
        geom.setCoordinates( 0, vertices );
        
        if ( normals != null )
        {
            geom.setNormals( 0, normals );
        }
        
        if ( texCoords2 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords2 );
        }
        
        if ( texCoords3 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords3 );
        }
        
        if ( colors != null )
        {
            geom.setColors( 0, colors );
        }
        
        return ( geom );
    }
    
    /**
     * Creates a TriangleArray.
     * 
     * @param geomConstruct
     * 
     * @return the TriangleArray
     */
    public static TriangleArray createTriangleArray( GeometryConstruct geomConstruct )
    {
        if ( geomConstruct.getGeometryTypeHint() != GeometryType.TRIANGLE_ARRAY )
            throw new IllegalArgumentException( "The GeometryConstruct is not made " + geomConstruct.getGeometryTypeHint().getCorrespondingClass().getSimpleName() + ", but " + GeometryType.TRIANGLE_ARRAY.getCorrespondingClass().getSimpleName() + " is expected." );
        
        return ( createTriangleArray( geomConstruct.getCoordinates(), geomConstruct.getNormals(), geomConstruct.getColors(), geomConstruct.getTextureCoordinates2f(), geomConstruct.getTextureCoordinates3f() ) );
    }
    
    /**
     * Creates an IndexedTriangleArray.
     * 
     * @param vertices
     * @param indices
     * @param normals
     * @param colors
     * @param texCoords2
     * @param texCoords3
     * 
     * @return the IndexedTriangleArray
     */
    public static IndexedTriangleArray createIndexedTriangleArray( Tuple3f[] vertices, int[] indices, Vector3f[] normals, Colorf[] colors, TexCoord2f[] texCoords2, TexCoord3f[] texCoords3 )
    {
        final int features = calculateFeatures( vertices, normals, colors, texCoords2, texCoords3 );
        final boolean colorAlpha = ( colors != null ) ? colors[ 0 ].hasAlpha() : false;
        final int[] texCoordsSizes;
        if ( texCoords2 != null )
            texCoordsSizes = new int[] { 2 };
        else if ( texCoords3 != null )
            texCoordsSizes = new int[] { 3 };
        else
            texCoordsSizes = null;
        
        final IndexedTriangleArray geom = new IndexedTriangleArray( vertices.length, indices.length );
        geom.makeInterleaved( features, colorAlpha, texCoordsSizes, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        
        geom.setCoordinates( 0, vertices );
        geom.setIndex( indices );
        
        if ( normals != null )
        {
            geom.setNormals( 0, normals );
        }
        
        if ( texCoords2 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords2 );
        }
        
        if ( texCoords3 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords3 );
        }
        if ( colors != null )
        {
            geom.setColors( 0, colors );
        }
        
        return ( geom );
    }
    
    /**
     * Creates a IndexedTriangleArray.
     * 
     * @param geomConstruct
     * 
     * @return the IndexedTriangleArray
     */
    public static IndexedTriangleArray createIndexedTriangleArray( GeometryConstruct geomConstruct )
    {
        if ( geomConstruct.getGeometryTypeHint() != GeometryType.INDEXED_TRIANGLE_ARRAY )
            throw new IllegalArgumentException( "The GeometryConstruct is not made " + geomConstruct.getGeometryTypeHint().getCorrespondingClass().getSimpleName() + ", but " + GeometryType.INDEXED_TRIANGLE_ARRAY.getCorrespondingClass().getSimpleName() + " is expected." );
        
        return ( createIndexedTriangleArray( geomConstruct.getCoordinates(), geomConstruct.getIndices(), geomConstruct.getNormals(), geomConstruct.getColors(), geomConstruct.getTextureCoordinates2f(), geomConstruct.getTextureCoordinates3f() ) );
    }
    
    /**
     * Creates a TriangleStripArray.
     * 
     * @param vertices
     * @param normals
     * @param colors
     * @param texCoords2
     * @param texCoords3
     * @param stripLengths
     * 
     * @return the TriangleStripArray
     */
    public static TriangleStripArray createTriangleStripArray( Tuple3f[] vertices, Vector3f[] normals, Colorf[] colors, TexCoord2f[] texCoords2, TexCoord3f[] texCoords3, int[] stripLengths )
    {
        final int features = calculateFeatures( vertices, normals, colors, texCoords2, texCoords3 );
        final boolean colorAlpha = ( colors != null ) ? colors[ 0 ].hasAlpha() : false;
        final int[] texCoordsSizes;
        if ( texCoords2 != null )
            texCoordsSizes = new int[] { 2 };
        else if ( texCoords3 != null )
            texCoordsSizes = new int[] { 3 };
        else
            texCoordsSizes = null;
        
        final TriangleStripArray geom = new TriangleStripArray( vertices.length, stripLengths );
        geom.makeInterleaved( features, colorAlpha, texCoordsSizes, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        
        geom.setCoordinates( 0, vertices );
        
        if ( normals != null )
        {
            geom.setNormals( 0, normals );
        }
        
        if ( texCoords2 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords2 );
        }
        
        if ( texCoords3 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords3 );
        }
        
        if ( colors != null )
        {
            geom.setColors( 0, colors );
        }
        
        return ( geom );
    }
    
    /**
     * Creates a TriangleStripArray.
     * 
     * @param geomConstruct
     * 
     * @return the TriangleStripArray
     */
    public static TriangleStripArray createTriangleStripArray( GeometryConstruct geomConstruct )
    {
        if ( geomConstruct.getGeometryTypeHint() != GeometryType.TRIANGLE_STRIP_ARRAY )
            throw new IllegalArgumentException( "The GeometryConstruct is not made " + geomConstruct.getGeometryTypeHint().getCorrespondingClass().getSimpleName() + ", but " + GeometryType.TRIANGLE_STRIP_ARRAY.getCorrespondingClass().getSimpleName() + " is expected." );
        
        return ( createTriangleStripArray( geomConstruct.getCoordinates(), geomConstruct.getNormals(), geomConstruct.getColors(), geomConstruct.getTextureCoordinates2f(), geomConstruct.getTextureCoordinates3f(), geomConstruct.getStripLengths() ) );
    }
    
    /**
     * Creates an IndexedTriangleStripArray.
     * 
     * @param vertices
     * @param indices
     * @param normals
     * @param colors
     * @param texCoords2
     * @param texCoords3
     * @param stripLengths
     * 
     * @return the IndexedTriangleStripArray
     */
    public static IndexedTriangleStripArray createIndexedTriangleStripArray( Tuple3f[] vertices, int[] indices, Vector3f[] normals, Colorf[] colors, TexCoord2f[] texCoords2, TexCoord3f[] texCoords3, int[] stripLengths )
    {
        final int features = calculateFeatures( vertices, normals, colors, texCoords2, texCoords3 );
        final boolean colorAlpha = ( colors != null ) ? colors[ 0 ].hasAlpha() : false;
        final int[] texCoordsSizes;
        if ( texCoords2 != null )
            texCoordsSizes = new int[] { 2 };
        else if ( texCoords3 != null )
            texCoordsSizes = new int[] { 3 };
        else
            texCoordsSizes = null;
        
        final IndexedTriangleStripArray geom = new IndexedTriangleStripArray( vertices.length, indices.length, stripLengths );
        geom.makeInterleaved( features, colorAlpha, texCoordsSizes, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        
        geom.setCoordinates( 0, vertices );
        geom.setIndex( indices );
        
        if ( normals != null )
        {
            geom.setNormals( 0, normals );
        }
        
        if ( texCoords2 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords2 );
        }
        
        if ( texCoords3 != null )
        {
            geom.setTextureCoordinates( 0, 0, texCoords3 );
        }
        
        if ( colors != null )
        {
            geom.setColors( 0, colors );
        }
        
        return ( geom );
    }
    
    /**
     * Creates a IndexedTriangleStripArray.
     * 
     * @param geomConstruct
     * 
     * @return the IndexedTriangleStripArray
     */
    public static IndexedTriangleStripArray createIndexedTriangleStripArray( GeometryConstruct geomConstruct )
    {
        if ( geomConstruct.getGeometryTypeHint() != GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY )
            throw new IllegalArgumentException( "The GeometryConstruct is made for " + geomConstruct.getGeometryTypeHint().getCorrespondingClass().getSimpleName() + ", but " + GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY.getCorrespondingClass().getSimpleName() + " is expected." );
        
        return ( createIndexedTriangleStripArray( geomConstruct.getCoordinates(), geomConstruct.getIndices(), geomConstruct.getNormals(), geomConstruct.getColors(), geomConstruct.getTextureCoordinates2f(), geomConstruct.getTextureCoordinates3f(), geomConstruct.getStripLengths() ) );
    }
    
    public static Geometry createGeometryArray( GeometryConstruct gc )
    {
        if ( gc.getGeometryTypeHint() == GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY )
        {
            return ( createIndexedTriangleStripArray( gc ) );
        }
        else if ( gc.getGeometryTypeHint() == GeometryType.INDEXED_TRIANGLE_ARRAY )
        {
            return ( createIndexedTriangleArray( gc ) );
        }
        else if ( gc.getGeometryTypeHint() == GeometryType.TRIANGLE_STRIP_ARRAY )
        {
            return ( createTriangleStripArray( gc ) );
        }
        else if ( gc.getGeometryTypeHint() == GeometryType.TRIANGLE_ARRAY )
        {
            return ( createTriangleArray( gc ) );
        }
        else
        {
            throw new UnsupportedOperationException( "The type " + gc.getGeometryTypeHint() + " cannot currently be created. (Must be implemented)" );
        }
    }
    
    /**
     * Converts a GeometryConstruct made for IndexedTriangleStripArray to one made for IndexedTriangleArrays.<br>
     * <b>The data instances will be the same for both GeometryConstructs!</b>
     * 
     * @param gcITSA the source GeometryConstruct made for IndexedTriangleStripArrays
     * 
     * @return the new GeometryConstruct made for IndexedTriangleArrays
     */
    public static GeometryConstruct convertGeometryConstructITSA2ITA( GeometryConstruct gcITSA )
    {
        final int numStrips = gcITSA.getStripLengths().length;
        
        int[] indices = new int[ ( gcITSA.numIndices() - 2 * numStrips ) * 3 ];
        
        int o = 0;
        int k = 0;
        for ( int s = 0; s < numStrips; s++ )
        {
            for ( int i = o; i < o + gcITSA.getStripLengths()[ s ] - 2; i++ )
            {
                for ( int j = 0; j < 3; j++ )
                {
                    indices[ k++ ] = gcITSA.getIndices()[ i + j ];
                }
            }
            
            o += gcITSA.getStripLengths()[ s ];
        }
        
        GeometryConstruct gcITA = new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_ARRAY, gcITSA.getCoordinates(), gcITSA.getNormals(), gcITSA.getTextureCoordinates2f(), indices, null );
        
        return ( gcITA );
    }
    
    /**
     * Converts a GeometryConstruct made for IndexedTriangleStripArray to one made for TriangleStripArrays.<br>
     * <b>The data instances will be the same for both GeometryConstructs!</b>
     * 
     * @param gcITSA the source GeometryConstruct made for IndexedTriangleStripArrays
     * 
     * @return the new GeometryConstruct made for TriangleStripArrays
     */
    public static GeometryConstruct convertGeometryConstructITSA2TSA( GeometryConstruct gcITSA )
    {
        final int numStrips = gcITSA.getStripLengths().length;
        int[] stripLengths = new int[ numStrips ];
        for ( int i = 0; i < numStrips; i++ )
        {
            stripLengths[ i ] = gcITSA.getIndices().length / numStrips;
        }
        
        Point3f[] vertices = new Point3f[ gcITSA.numIndices() ];
        Vector3f[] normals = null;
        TexCoord2f[] texCoords = null;
        
        if ( gcITSA.numNormals() > 0 )
            normals = new Vector3f[ vertices.length ];
        
        if ( gcITSA.numTextureCoordinates2f() > 0 )
            texCoords = new TexCoord2f[ vertices.length ];
        
        for ( int i = 0; i < gcITSA.getIndices().length; i++ )
        {
            final int idx = gcITSA.getIndices()[ i ];
            
            vertices[ i ] = new Point3f( gcITSA.getCoordinates()[ idx ] );
            
            if ( gcITSA.numNormals() > 0 )
                normals[ i ] = new Vector3f( gcITSA.getNormals()[ idx ] );
            
            if ( gcITSA.numTextureCoordinates2f() > 0 )
                texCoords[ i ] = new TexCoord2f( gcITSA.getTextureCoordinates2f()[ idx ] );
        }
        
        GeometryConstruct gcTSA = new GeometryConstruct( GeometryType.TRIANGLE_STRIP_ARRAY, vertices, normals, texCoords, null, stripLengths );
        
        return ( gcTSA );
    }
    
    /**
     * Converts a GeometryConstruct made for IndexedTriangleStripArray to one made for TriangleArrays.<br>
     * <b>The data instances will be the same for both GeometryConstructs!</b>
     * 
     * @param gcITSA the source GeometryConstruct made for IndexedTriangleStripArrays
     * 
     * @return the new GeometryConstruct made for TriangleArrays
     */
    public static GeometryConstruct convertGeometryConstructITSA2TA( GeometryConstruct gcITSA )
    {
        final int numStrips = gcITSA.getStripLengths().length;
        
        Point3f[] coords = new Point3f[ ( gcITSA.numIndices() - 2 * numStrips ) * 3 ];
        Vector3f[] normals = null;
        TexCoord2f[] texCoords2 = null;
        TexCoord3f[] texCoords3 = null;
        Colorf[] colors = null;
        
        if ( gcITSA.numNormals() > 0 )
            normals = new Vector3f[ coords.length ];
        
        if ( gcITSA.numTextureCoordinates2f() > 0 )
            texCoords2 = new TexCoord2f[ coords.length ];
        
        if ( gcITSA.numTextureCoordinates3f() > 0 )
            texCoords3 = new TexCoord3f[ coords.length ];
        
        if ( gcITSA.numColors() > 0 )
            colors = new Colorf[ coords.length ];
        
        int o = 0;
        int k = 0;
        for ( int s = 0; s < gcITSA.getStripLengths().length; s++ )
        {
            for ( int i = o; i < o + gcITSA.getStripLengths()[ s ] - 2; i++ )
            {
                for ( int j = 0; j < 3; j++ )
                {
                    final int idx = gcITSA.getIndices()[ i + j ];
                    
                    coords[ k ] = new Point3f( gcITSA.getCoordinates()[ idx ] );
                    
                    if ( gcITSA.numNormals() > 0 )
                        normals[ k ] = new Vector3f( gcITSA.getNormals()[ idx ] );
                    
                    if ( gcITSA.numTextureCoordinates2f() > 0 )
                        texCoords2[ k ] = new TexCoord2f( gcITSA.getTextureCoordinates2f()[ idx ] );
                    
                    if ( gcITSA.numTextureCoordinates3f() > 0 )
                        texCoords3[ k ] = new TexCoord3f( gcITSA.getTextureCoordinates3f()[ idx ] );
                    
                    if ( gcITSA.numColors() > 0 )
                        colors[ k ] = gcITSA.getColors()[ idx ];
                    
                    k++;
                }
            }
            
            o += gcITSA.getStripLengths()[ s ];
        }
        
        GeometryConstruct gcTA;
        
        if ( ( texCoords3 != null ) && ( colors != null ) )
            gcTA = new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, coords, normals, texCoords3, colors );
        else
            gcTA = new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, coords, normals, texCoords2, colors );
        
        return ( gcTA );
    }
    
    /**
     * Creates an IndexedTriangleArray for a Shape3D.
     * 
     * @param gcITSA the GeometryConstruct to build it from
     */
    public static IndexedTriangleArray createITAfromITSA( GeometryConstruct gcITSA )
    {
        if ( gcITSA.getGeometryTypeHint() != GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY )
            throw new IllegalArgumentException( "The given GeometryConstruct must be of type " + GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY + ", but is of type " + gcITSA.getGeometryTypeHint() );
        
        final int numStrips = gcITSA.getStripLengths().length;
        
        final int features = gcITSA.calculateFeatures();
        final boolean colorAlpha = ( gcITSA.getColors() != null ) ? gcITSA.getColors()[ 0 ].hasAlpha() : false;
        final int[] texCoordsSizes;
        if ( gcITSA.getTextureCoordinates2f() != null )
            texCoordsSizes = new int[] { 2 };
        else if ( gcITSA.getTextureCoordinates3f() != null )
            texCoordsSizes = new int[] { 3 };
        else
            texCoordsSizes = null;
        
        IndexedTriangleArray geom = new IndexedTriangleArray( gcITSA.numVertices(), ( gcITSA.numIndices() - 2 * numStrips ) * 3 );
        geom.makeInterleaved( features, colorAlpha, texCoordsSizes, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        
        geom.setCoordinates( 0, gcITSA.getCoordinates() );
        
        if ( gcITSA.numNormals() > 0 )
            geom.setNormals( 0, gcITSA.getNormals() );
        
        if ( gcITSA.numTextureCoordinates2f() > 0 )
            geom.setTextureCoordinates( 0, 0, gcITSA.getTextureCoordinates2f() );
        
        if ( gcITSA.numTextureCoordinates3f() > 0 )
            geom.setTextureCoordinates( 0, 0, gcITSA.getTextureCoordinates3f() );
        
        if ( gcITSA.numColors() > 0 )
            geom.setColors( 0, gcITSA.getColors() );
        
        int o = 0;
        int k = 0;
        for ( int s = 0; s < numStrips; s++ )
        {
            for ( int i = o; i < o + gcITSA.getStripLengths()[ s ] - 2; i++ )
            {
                for ( int j = 0; j < 3; j++ )
                {
                    geom.setIndex( k++, gcITSA.getIndices()[ i + j ] );
                }
            }
            
            o += gcITSA.getStripLengths()[ s ];
        }
        
        return ( geom );
    }
    
    /**
     * Creates a TriangleStripArray for a Shape3D.
     * 
     * @param gcITSA the GeometryConstruct to build it from
     */
    public static TriangleStripArray createTSAfromITSA( GeometryConstruct gcITSA )
    {
        if ( gcITSA.getGeometryTypeHint() != GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY )
            throw new IllegalArgumentException( "The given GeometryConstruct must be of type " + GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY + ", but is of type " + gcITSA.getGeometryTypeHint() );
        
        final int numStrips = gcITSA.getStripLengths().length;
        int[] stripLengths = new int[ numStrips ];
        for ( int i = 0; i < numStrips; i++ )
        {
            stripLengths[ i ] = gcITSA.getIndices().length / numStrips;
        }
        
        final int features = gcITSA.calculateFeatures();
        final boolean colorAlpha = ( gcITSA.getColors() != null ) ? gcITSA.getColors()[ 0 ].hasAlpha() : false;
        final int[] texCoordsSizes;
        if ( gcITSA.getTextureCoordinates2f() != null )
            texCoordsSizes = new int[] { 2 };
        else if ( gcITSA.getTextureCoordinates3f() != null )
            texCoordsSizes = new int[] { 3 };
        else
            texCoordsSizes = null;
        
        TriangleStripArray geom = new TriangleStripArray( gcITSA.numIndices(), stripLengths );
        geom.makeInterleaved( features, colorAlpha, texCoordsSizes, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        
        for ( int i = 0; i < gcITSA.getIndices().length; i++ )
        {
            final int idx = gcITSA.getIndices()[ i ];
            
            geom.setCoordinate( i, gcITSA.getCoordinates()[ idx ] );
            
            if ( gcITSA.numNormals() > 0 )
                geom.setNormal( i, gcITSA.getNormals()[ idx ] );
            
            if ( gcITSA.numTextureCoordinates2f() > 0 )
                geom.setTextureCoordinate( 0, i, gcITSA.getTextureCoordinates2f()[ idx ] );
        }
        
        return ( geom );
    }
    
    /**
     * Creates a TriangleArray for a Shape3D.
     * 
     * @param gcITSA the GeometryConstruct to build it from
     */
    public static TriangleArray createTAfromITSA( GeometryConstruct gcITSA )
    {
        if ( gcITSA.getGeometryTypeHint() != GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY )
            throw new IllegalArgumentException( "The given GeometryConstruct must be of type " + GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY + ", but is of type " + gcITSA.getGeometryTypeHint() );
        
        final int numStrips = gcITSA.getStripLengths().length;
        
        final int features = gcITSA.calculateFeatures();
        final boolean colorAlpha = ( gcITSA.getColors() != null ) ? gcITSA.getColors()[ 0 ].hasAlpha() : false;
        final int[] texCoordsSizes;
        if ( gcITSA.getTextureCoordinates2f() != null )
            texCoordsSizes = new int[] { 2 };
        else if ( gcITSA.getTextureCoordinates3f() != null )
            texCoordsSizes = new int[] { 3 };
        else
            texCoordsSizes = null;
        
        TriangleArray geom = new TriangleArray( ( gcITSA.numIndices() - 2 * numStrips ) * 3 );
        geom.makeInterleaved( features, colorAlpha, texCoordsSizes, null );
        geom.setOptimization( Optimization.USE_DISPLAY_LISTS );
        
        int o = 0;
        int k = 0;
        for ( int s = 0; s < gcITSA.getStripLengths().length; s++ )
        {
            for ( int i = o; i < o + gcITSA.getStripLengths()[ s ] - 2; i++ )
            {
                for ( int j = 0; j < 3; j++ )
                {
                    final int idx = gcITSA.getIndices()[ i + j ];
                    
                    geom.setCoordinate( k, gcITSA.getCoordinates()[ idx ] );
                    
                    if ( gcITSA.numNormals() > 0 )
                        geom.setNormal( k, gcITSA.getNormals()[ idx ] );
                    
                    if ( gcITSA.numTextureCoordinates2f() > 0 )
                        geom.setTextureCoordinate( 0, k, gcITSA.getTextureCoordinates2f()[ idx ] );
                    
                    if ( gcITSA.numTextureCoordinates3f() > 0 )
                        geom.setTextureCoordinate( 0, k, gcITSA.getTextureCoordinates3f()[ idx ] );
                    
                    if ( gcITSA.numColors() > 0 )
                        geom.setColor( k, gcITSA.getColors()[ idx ] );
                    
                    k++;
                }
            }
            
            o += gcITSA.getStripLengths()[ s ];
        }
        
        return ( geom );
    }
}
