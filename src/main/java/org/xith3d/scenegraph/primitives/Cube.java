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
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * A simple Cube.
 * 
 * @author Yuri Vl. Gushchin
 * @author Marvin Froehlich (aka Qudus)
 */
public class Cube extends Box
{


    public static GeometryConstruct createGeometryConstructTA( float offsetX, float offsetY, float offsetZ, float size, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( Box.createGeometryConstructTA( offsetX, offsetY, offsetZ, size, size, size, features, colorAlpha, texCoordsSize ) );
    }
    
    public static GeometryConstruct createGeometryConstructTA( float size, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( Cube.createGeometryConstructTA( 0f, 0f, 0f, size, features, colorAlpha, texCoordsSize ) );
    }
    
    public static TriangleArray createGeometryTA( float offsetX, float offsetY, float offsetZ, float size, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( Box.createGeometryTA( offsetX, offsetY, offsetZ, size, size, size, features, colorAlpha, texCoordsSize ) );
    }
    
    public static TriangleArray createGeometryTA( float size, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( Cube.createGeometryTA( 0f, 0f, 0f, size, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param size
     * @param features GeomatryArray features
     */
    public Cube( float size, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( size, size, size, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param size
     */
    public Cube( float size )
    {
        this( size, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param size
     * @param texture
     */
    public Cube( float size, Texture texture )
    {
        this( size, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param texture
     */
    public Cube( Texture texture )
    {
        this( 1.0f, texture );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param size
     * @param texture
     */
    public Cube( float size, String texture )
    {
        this( size, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param texture
     */
    public Cube( String texture )
    {
        this( TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param size
     * @param color
     */
    public Cube( float size, Colorf color )
    {
        this( size, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param color
     */
    public Cube( Colorf color )
    {
        this( 1.0f, color );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param size
     * @param app
     */
    public Cube( float size, Appearance app )
    {
        this( size, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        this.setAppearance( app );
    }
    
    /**
     * Builds a Cube.
     * 
     * @param app
     */
    public Cube( Appearance app )
    {
        this( 1.0f, app );
    }
}
