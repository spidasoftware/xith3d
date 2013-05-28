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
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;

/**
 * A disk created by crushing/transforming a cone.<br>
 * The disk will be parallel to the x/y plane.
 * 
 * @author Daniel Herring
 * @author Marvin Froehlich (aka Qudus)
 */
public class Disk extends Shape3D
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
                throw new UnsupportedOperationException( "Currently " + Disk.class.getSimpleName() + " does not support " + hint );
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
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit radius.
     * 
     * @param radius the Disk's radius
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTA( float radius, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        GeometryConstruct gc = Cone.createGeometryConstructTA( radius, 0.0f, slices, features, colorAlpha, texCoordsSize );
        
        //StaticTransform.scale( gc.getCoordinates(), 1.0f, 0.0f, 1.0f );
        StaticTransform.rotateX( gc.getCoordinates(), FastMath.PI_HALF );
        
        if ( gc.numNormals() > 0 )
        {
            for ( int i = 0; i < gc.numNormals(); i++ )
            {
                gc.getNormals()[ i ].set( 0.0f, 0.0f, 1.0f );
            }
        }
        
        return ( gc );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     * 
     * @param radius the Disk's radius
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleArray createGeometryTA( float radius, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        GeometryConstruct gc = createGeometryConstructTA( radius, slices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTriangleArray( gc ) );
    }
    
    public static Geometry createGeometry( float radius, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( radius, slices, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * A disk is created by crushing a cone.
     * 
     * @param radius the Disk's radius
     * @param slices number of slices for the Cone (e.g. 36)
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Disk( float radius, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( createGeometry( radius, slices, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * 
     * @param slices number of slices for the Cone (e.g. 36)
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Disk( int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( 1.0f, slices, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param radius the Disk's radius
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to apply to this Shape's Appearance
     */
    public Disk( float radius, int slices, Texture texture )
    {
        this( radius, slices, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param texture the Texture to apply to this Shape's Appearance
     * @param slices Number of vertical stripes down the cone
     */
    public Disk( int slices, Texture texture )
    {
        this( slices, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param radius the Disk's radius
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to apply to this Shape's Appearance
     */
    public Disk( float radius, int slices, String texture )
    {
        this( radius, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param texture the Texture to apply to this Shape's Appearance
     * @param slices Number of vertical stripes down the cone
     */
    public Disk( int slices, String texture )
    {
        this( slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param radius the Disk's radius
     * @param slices Number of vertical stripes down the cone
     * @param color the color to apply to this Shape's Appearance
     */
    public Disk( float radius, int slices, Colorf color )
    {
        this( radius, slices, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param slices Number of vertical stripes down the cone
     * @param color the color to apply to this Shape's Appearance
     */
    public Disk( int slices, Colorf color )
    {
        this( 1.0f, slices, color );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param radius the Disk's radius
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     */
    public Disk( float radius, int slices, Appearance app )
    {
        this( radius, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Generates the Geometry of a disk parallel to the x/y plane.
     * Has unit height and radius.
     *
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     */
    public Disk( int slices, Appearance app )
    {
        this( 1.0f, slices, app );
    }
}
