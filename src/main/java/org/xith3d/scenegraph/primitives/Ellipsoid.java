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

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * An ellipsoid using the stacks/slices specification.
 * 
 * @author Daniel Herring
 */
public class Ellipsoid extends Shape3D
{
    /**
     * Create an ellipsoid using the stacks/slices specification
     * 
     * Creates 2*slices*(stacks-1) triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param slices Number of vertical stripes down the ellipsoid
     * @param stacks Number of stacked rings around the ellipsoid
     * @param features the GeometryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Ellipsoid( float rx, float ry, float rz, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( ( stacks < 2 ) || ( slices < 3 ) )
        {
            throw new IllegalArgumentException( "insufficient stacks or slices" );
        }
        
        features |= Geometry.COORDINATES;
        
        Point3f[] coords = new Point3f[ 3 * slices * 2 * ( stacks - 1 ) ];
        
        Point3f[][] buffer = new Point3f[ 2 ][ stacks + 1 ]; // Store the vertices in buffers
        // Init the left buffer
        for ( int j = 0; j <= stacks; j++ )
        {
            buffer[ 0 ][ j ] = new Point3f();
            buffer[ 0 ][ j ].setX( FastMath.sin( j * FastMath.PI / stacks ) );
            buffer[ 0 ][ j ].setY( 0f );
            buffer[ 0 ][ j ].setZ( FastMath.cos( j * FastMath.PI / stacks ) );
        }
        
        int left, right; // select the appropriate buffer
        int index = 0;
        for ( int i = 0; i < slices; i++ )
        {
            // auto-swap buffers
            left = i % 2;
            right = ( i + 1 ) % 2;
            
            // Start the right column
            buffer[ right ][ 0 ] = new Point3f( 0f, 0f, 1f );
            
            for ( int j = 0; j < stacks; j++ )
            {
                // next right point
                buffer[ right ][ j + 1 ] = new Point3f();
                buffer[ right ][ j + 1 ].setX( FastMath.cos( 2f * ( i + 1 ) * FastMath.PI / slices ) * FastMath.sin( ( j + 1 ) * FastMath.PI / stacks ) );
                buffer[ right ][ j + 1 ].setY( FastMath.sin( 2f * ( i + 1 ) * FastMath.PI / slices ) * FastMath.sin( ( j + 1 ) * FastMath.PI / stacks ) );
                buffer[ right ][ j + 1 ].setZ( buffer[ left ][ j + 1 ].getZ() );
                
                if ( j > 0 )
                {
                    coords[ index++ ] = buffer[ left ][ j ];
                    coords[ index++ ] = buffer[ left ][ j + 1 ];
                    coords[ index++ ] = buffer[ right ][ j ];
                }
                if ( j < ( stacks - 1 ) )
                {
                    coords[ index++ ] = buffer[ right ][ j ];
                    coords[ index++ ] = buffer[ left ][ j + 1 ];
                    coords[ index++ ] = buffer[ right ][ j + 1 ];
                }
            }
        }
        
        Point3f[] vertices = new Point3f[ coords.length ];
        for ( int i = 0; i < coords.length; i++ )
        {
            vertices[ i ] = new Point3f( coords[ i ] );
            vertices[ i ].mul( rx, ry, rz );
        }
        
        Colorf[] colors = null;
        if ( ( features & Geometry.COLORS ) != 0 )
        {
            colors = new Colorf[ vertices.length ];
            for ( int i = 0; i < colors.length; i++ )
            {
                if ( colorAlpha )
                    colors[ i ] = new Colorf( coords[ i ].getX(), coords[ i ].getY(), coords[ i ].getZ(), 0f );
                else
                    colors[ i ] = new Colorf( coords[ i ].getX(), coords[ i ].getY(), coords[ i ].getZ() );
            }
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
     * Create an ellipsoid using the stacks/slices specification
     * 
     * Creates 2*slices*(stacks-1) triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param slices Number of vertical stripes down the ellipsoid
     * @param stacks Number of stacked rings around the ellipsoid
     * @param texture the Texture to be applied to this Shape's Appearance
     */
    public Ellipsoid( float rx, float ry, float rz, int slices, int stacks, Texture texture )
    {
        this( rx, ry, rz, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Create an ellipsoid using the stacks/slices specification
     * 
     * Creates 2*slices*(stacks-1) triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param slices Number of vertical stripes down the ellipsoid
     * @param stacks Number of stacked rings around the ellipsoid
     * @param texture the Texture to be applied to this Shape's Appearance
     */
    public Ellipsoid( float rx, float ry, float rz, int slices, int stacks, String texture )
    {
        this( rx, ry, rz, slices, stacks, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Create an ellipsoid using the stacks/slices specification
     * 
     * Creates 2*slices*(stacks-1) triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param slices Number of vertical stripes down the ellipsoid
     * @param stacks Number of stacked rings around the ellipsoid
     * @param color the color to be applied to this Shape's Appearance's ColoringAttributes
     */
    public Ellipsoid( float rx, float ry, float rz, int slices, int stacks, Colorf color )
    {
        this( rx, ry, rz, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Create an ellipsoid using the stacks/slices specification
     * 
     * Creates 2*slices*(stacks-1) triangles
     * 
     * @param rx Radius along x axis
     * @param ry Radius along y axis
     * @param rz Radius along z axis
     * @param slices Number of vertical stripes down the ellipsoid
     * @param stacks Number of stacked rings around the ellipsoid
     * @param app the Appearance to be applide to this Shape
     */
    public Ellipsoid( float rx, float ry, float rz, int slices, int stacks, Appearance app )
    {
        this( rx, ry, rz, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
}
