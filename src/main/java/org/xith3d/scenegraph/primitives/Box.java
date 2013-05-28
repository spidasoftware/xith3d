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
import org.openmali.vecmath2.TexCoord3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;

/**
 * A simple Box.
 * 
 * @author Yuri Vl. Gushchin
 * @author Marvin Froehlich (aka Qudus)
 */
public class Box extends Shape3D
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
                throw new UnsupportedOperationException( "A " + Box.class.getSimpleName() + " can only by constructed of " + GeometryType.TRIANGLE_ARRAY.getCorrespondingClass().getSimpleName() );
        }
    }
    
    /**
     * @return the hint for this ShapeType's Geometry to be constructed of a certain type.
     */
    public static GeometryType getGeometryConstructionTypeHint()
    {
        return ( geomConstructTypeHint );
    }
    
    public static GeometryConstruct createGeometryConstructTA( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        Point3f[] vertices = null;
        Vector3f[] normals = null;
        TexCoord2f[] texCoords2f = null;
        TexCoord3f[] texCoords3f = null;
        Colorf[] colors = null;
        
        //if (((features & GeometryArray.COORDINATES) > 0) || true) // always!
        {
            final float halfX = sizeX / 2f;
            final float halfY = sizeY / 2f;
            final float halfZ = sizeZ / 2f;
            
            vertices = new Point3f[]
            {
                // top
                new Point3f( -halfX, halfY, halfZ ),
                new Point3f( halfX, halfY, halfZ ),
                new Point3f( halfX, halfY, -halfZ ),
                new Point3f( halfX, halfY, -halfZ ),
                new Point3f( -halfX, halfY, -halfZ ),
                new Point3f( -halfX, halfY, halfZ ),
                
                // back
                new Point3f( halfX, -halfY, halfZ ),
                new Point3f( halfX, halfY, halfZ ),
                new Point3f( -halfX, halfY, halfZ ),
                new Point3f( -halfX, halfY, halfZ ),
                new Point3f( -halfX, -halfY, halfZ ),
                new Point3f( halfX, -halfY, halfZ ),
                
                // left
                new Point3f( halfX, halfY, -halfZ ),
                new Point3f( halfX, halfY, halfZ ),
                new Point3f( halfX, -halfY, halfZ ),
                new Point3f( halfX, -halfY, halfZ ),
                new Point3f( halfX, -halfY, -halfZ ),
                new Point3f( halfX, halfY, -halfZ ),
                
                // right
                new Point3f( -halfX, -halfY, halfZ ),
                new Point3f( -halfX, halfY, halfZ ),
                new Point3f( -halfX, halfY, -halfZ ),
                new Point3f( -halfX, halfY, -halfZ ),
                new Point3f( -halfX, -halfY, -halfZ ),
                new Point3f( -halfX, -halfY, halfZ ),
                
                // bottom
                new Point3f( halfX, -halfY, halfZ ),
                new Point3f( -halfX, -halfY, halfZ ),
                new Point3f( -halfX, -halfY, -halfZ ),
                new Point3f( -halfX, -halfY, -halfZ ),
                new Point3f( halfX, -halfY, -halfZ ),
                new Point3f( halfX, -halfY, halfZ ),
                
                //front
                new Point3f( halfX, halfY, -halfZ ),
                new Point3f( halfX, -halfY, -halfZ ),
                new Point3f( -halfX, -halfY, -halfZ ),
                new Point3f( -halfX, -halfY, -halfZ ),
                new Point3f( -halfX, halfY, -halfZ ),
                new Point3f( halfX, halfY, -halfZ ),
            };
            
            if ( ( offsetX != 0.0f ) || ( offsetY != 0.0f ) || ( offsetZ != 0.0f ) )
            {
                StaticTransform.translate( vertices, offsetX, offsetY, offsetZ );
            }
        }
        
        if ( ( features & Geometry.NORMALS ) > 0 )
        {
            Vector3f up = new Vector3f( 0f, 1f, 0f );
            Vector3f down = new Vector3f( 0f, -1f, 0f );
            Vector3f right = new Vector3f( -1f, 0f, 0f );
            Vector3f left = new Vector3f( 1f, 0f, 0f );
            Vector3f front = new Vector3f( 0f, 0f, -1f );
            Vector3f back = new Vector3f( 0f, 0f, 1f );
            
            normals = new Vector3f[]
            {
                new Vector3f( up ),
                new Vector3f( up ),
                new Vector3f( up ),
                new Vector3f( up ),
                new Vector3f( up ),
                new Vector3f( up ),
                
                new Vector3f( back ),
                new Vector3f( back ),
                new Vector3f( back ),
                new Vector3f( back ),
                new Vector3f( back ),
                new Vector3f( back ),
                
                new Vector3f( left ),
                new Vector3f( left ),
                new Vector3f( left ),
                new Vector3f( left ),
                new Vector3f( left ),
                new Vector3f( left ),
                
                new Vector3f( right ),
                new Vector3f( right ),
                new Vector3f( right ),
                new Vector3f( right ),
                new Vector3f( right ),
                new Vector3f( right ),
                
                new Vector3f( down ),
                new Vector3f( down ),
                new Vector3f( down ),
                new Vector3f( down ),
                new Vector3f( down ),
                new Vector3f( down ),
                
                new Vector3f( front ),
                new Vector3f( front ),
                new Vector3f( front ),
                new Vector3f( front ),
                new Vector3f( front ),
                new Vector3f( front )
            };
        }
        
        if ( ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 ) && ( texCoordsSize == 2 ) )
        {
            texCoords2f = new TexCoord2f[]
            {
                new TexCoord2f( 0f, 0f ),
                new TexCoord2f( 1f, 0f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 0f, 1f ),
                new TexCoord2f( 0f, 0f ),
                
                new TexCoord2f( 0f, 0f ),
                new TexCoord2f( 1f, 0f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 0f, 1f ),
                new TexCoord2f( 0f, 0f ),
                
                new TexCoord2f( 0f, 0f ),
                new TexCoord2f( 1f, 0f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 0f, 1f ),
                new TexCoord2f( 0f, 0f ),
                
                new TexCoord2f( 0f, 0f ),
                new TexCoord2f( 1f, 0f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 0f, 1f ),
                new TexCoord2f( 0f, 0f ),
                
                new TexCoord2f( 0f, 0f ),
                new TexCoord2f( 1f, 0f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 0f, 1f ),
                new TexCoord2f( 0f, 0f ),
                
                new TexCoord2f( 0f, 0f ),
                new TexCoord2f( 1f, 0f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 1f, 1f ),
                new TexCoord2f( 0f, 1f ),
                new TexCoord2f( 0f, 0f ),
            };
        }
        
        if ( ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 ) && ( texCoordsSize == 3 ) )
        {
            texCoords3f = new TexCoord3f[]
            {
                new TexCoord3f( 0f, 0f, 0f ),
                new TexCoord3f( 1f, 0f, 0.5f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 0f, 1f, 0.5f ),
                new TexCoord3f( 0f, 0f, 0f ),
                
                new TexCoord3f( 0f, 0f, 0f ),
                new TexCoord3f( 1f, 0f, 0.5f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 0f, 1f, 0.5f ),
                new TexCoord3f( 0f, 0f, 0f ),
                
                new TexCoord3f( 0f, 0f, 0f ),
                new TexCoord3f( 1f, 0f, 0.5f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 0f, 1f, 0.5f ),
                new TexCoord3f( 0f, 0f, 0f ),
                
                new TexCoord3f( 0f, 0f, 0f ),
                new TexCoord3f( 1f, 0f, 0.5f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 0f, 1f, 0.5f ),
                new TexCoord3f( 0f, 0f, 0f ),
                
                new TexCoord3f( 0f, 0f, 0f ),
                new TexCoord3f( 1f, 0f, 0.5f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 0f, 1f, 0.5f ),
                new TexCoord3f( 0f, 0f, 0f ),
                
                new TexCoord3f( 0f, 0f, 0f ),
                new TexCoord3f( 1f, 0f, 0.5f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 1f, 1f, 1f ),
                new TexCoord3f( 0f, 1f, 0.5f ),
                new TexCoord3f( 0f, 0f, 0f )
            };
        }
        
        if ( ( features & Geometry.COLORS ) != 0 )
        {
            colors = new Colorf[]
            {
                new Colorf( 1f, 1f, 1f ),
                new Colorf( 1f, 0f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 0f, 1f ),
                new Colorf( 1f, 1f, 1f ),
                
                new Colorf( 1f, 1f, 1f ),
                new Colorf( 1f, 0f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 0f, 1f ),
                new Colorf( 1f, 1f, 1f ),
                
                new Colorf( 1f, 1f, 1f ),
                new Colorf( 1f, 0f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 0f, 1f ),
                new Colorf( 1f, 1f, 1f ),
                
                new Colorf( 1f, 1f, 1f ),
                new Colorf( 1f, 0f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 0f, 1f ),
                new Colorf( 1f, 1f, 1f ),
                
                new Colorf( 1f, 1f, 1f ),
                new Colorf( 1f, 0f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 0f, 1f ),
                new Colorf( 1f, 1f, 1f ),
                
                new Colorf( 1f, 1f, 1f ),
                new Colorf( 1f, 0f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 1f, 0f ),
                new Colorf( 0f, 0f, 1f ),
                new Colorf( 1f, 1f, 1f ),
            };
            
            if ( colorAlpha )
            {
                for ( int i = 0; i < colors.length; i++ )
                {
                    colors[ i ].setAlpha( 0f );
                }
            }
        }
        
        if ( texCoords3f != null )
            return ( new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, vertices, normals, texCoords3f, colors ) );
        
        return ( new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, vertices, normals, texCoords2f, colors ) );
    }
    
    public static GeometryConstruct createGeometryConstructTA( float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( Box.createGeometryConstructTA( 0f, 0f, 0f, sizeX, sizeY, sizeZ, features, colorAlpha, texCoordsSize ) );
    }
    
    public static TriangleArray createGeometryTA( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        GeometryConstruct gc = Box.createGeometryConstructTA( offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTriangleArray( gc ) );
    }
    
    public static Geometry createGeometry( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    public static Geometry createGeometry( float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( Box.createGeometry( 0f, 0f, 0f, sizeX, sizeY, sizeZ, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param features GeomatryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Box( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        super();
        
        super.setGeometry( createGeometry( offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offset
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param features GeomatryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Box( Tuple3f offset, float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( offset.getX(), offset.getY(), offset.getZ(), sizeX, sizeY, sizeZ, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Builds a Box.
     * 
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param features GeomatryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Box( float sizeX, float sizeY, float sizeZ, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( 0f, 0f, 0f, sizeX, sizeY, sizeZ, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param texture
     */
    public Box( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, Texture texture )
    {
        this( offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offset
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param texture
     */
    public Box( Tuple3f offset, float sizeX, float sizeY, float sizeZ, Texture texture )
    {
        this( offset, sizeX, sizeY, sizeZ, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Builds a Box.
     * 
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param texture
     */
    public Box( float sizeX, float sizeY, float sizeZ, Texture texture )
    {
        this( 0f, 0f, 0f, sizeX, sizeY, sizeZ, texture );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param texture
     */
    public Box( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, String texture )
    {
        this( offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offset
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param texture
     */
    public Box( Tuple3f offset, float sizeX, float sizeY, float sizeZ, String texture )
    {
        this( offset, sizeX, sizeY, sizeZ, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param texture
     */
    public Box( float sizeX, float sizeY, float sizeZ, String texture )
    {
        this( 0f, 0f, 0f, sizeX, sizeY, sizeZ, texture );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param color
     */
    public Box( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, Colorf color )
    {
        this( offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offset
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param color
     */
    public Box( Tuple3f offset, float sizeX, float sizeY, float sizeZ, Colorf color )
    {
        this( offset, sizeX, sizeY, sizeZ, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Builds a Box.
     * 
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param color
     */
    public Box( float sizeX, float sizeY, float sizeZ, Colorf color )
    {
        this( 0f, 0f, 0f, sizeX, sizeY, sizeZ, color );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param app
     */
    public Box( float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, Appearance app )
    {
        this( offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        this.setAppearance( app );
    }
    
    /**
     * Builds a Box.
     * 
     * @param offset
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param app
     */
    public Box( Tuple3f offset, float sizeX, float sizeY, float sizeZ, Appearance app )
    {
        this( offset, sizeX, sizeY, sizeZ, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        this.setAppearance( app );
    }
    
    /**
     * Builds a Box.
     * 
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param app
     */
    public Box( float sizeX, float sizeY, float sizeZ, Appearance app )
    {
        this( 0f, 0f, 0f, sizeX, sizeY, sizeZ, app );
    }
    
    /**
     * Builds a Box.
     * 
     * @param box the box-body to create this scenegraph-box-primitive from
     * @param features GeomatryArray features
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static final Box createFromBoxBody( org.openmali.spatial.bodies.Box box, int features, boolean colorAlpha, int texCoordsSize )
    {
        return ( new Box( box.getCenterX(), box.getCenterY(), box.getCenterZ(), box.getSize().getX(), box.getSize().getY(), box.getSize().getZ(), features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param box the box-body to create this scenegraph-box-primitive from
     * @param texture
     */
    public static final Box createFromBoxBody( org.openmali.spatial.bodies.Box box, Texture texture )
    {
        return ( new Box( box.getCenterX(), box.getCenterY(), box.getCenterZ(), box.getSize().getX(), box.getSize().getY(), box.getSize().getZ(), texture ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param box the box-body to create this scenegraph-box-primitive from
     * @param texture
     */
    public static final Box createFromBoxBody( org.openmali.spatial.bodies.Box box, String texture )
    {
        return ( new Box( box.getCenterX(), box.getCenterY(), box.getCenterZ(), box.getSize().getX(), box.getSize().getY(), box.getSize().getZ(), texture ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param box the box-body to create this scenegraph-box-primitive from
     * @param color
     */
    public static final Box createFromBoxBody( org.openmali.spatial.bodies.Box box, Colorf color )
    {
        return ( new Box( box.getCenterX(), box.getCenterY(), box.getCenterZ(), box.getSize().getX(), box.getSize().getY(), box.getSize().getZ(), color ) );
    }
    
    /**
     * Builds a Box.
     * 
     * @param box the box-body to create this scenegraph-box-primitive from
     * @param app
     */
    public static final Box createFromBoxBody( org.openmali.spatial.bodies.Box box, Appearance app )
    {
        return ( new Box( box.getCenterX(), box.getCenterY(), box.getCenterZ(), box.getSize().getX(), box.getSize().getY(), box.getSize().getZ(), app ) );
    }
}
