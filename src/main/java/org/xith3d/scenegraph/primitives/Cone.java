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
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;

/**
 * An open cone pointing in the +z direction.
 * Has unit height and radius.
 * 
 * @see <a href="http://www.javagaming.org/cgi-bin/JGNetForums/YaBB.cgi?board=xith3d;action=display;num=1081555878">Original Announcement</a>
 * 
 * @author Daniel Herring
 * @author Marvin Froehlich (aka Qudus)
 */
public class Cone extends Shape3D
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
    
    /*
    public static GeometryConstruct createGeometryConstructITSA(int slices, int features)
    {
        if (slices < 3)
        {
            throw new IllegalArgumentException( "slices < 3" ) );
        }
        
        Point3f[] vertices = null;
        Vector3f[] normals = null;
        TexCoord2f[] texCoords = null;
        Color3f[] colors = null;
        int[] indices = null;
        int[] stripLengths = null;
        
        vertices = new Point3f[ slices + 2 ];
        
        if ((features & GeometryArray.NORMALS) > 0)
            normals = new Vector3f[ vertices.length ];
        
        if ((features & GeometryArray.TEXTURE_COORDINATE_2) > 0)
            texCoords = new TexCoord2f[ vertices.length ];
        
        int numIndices = (int)FastMath.floor( (float)slices / 3f ) * 5;
        if ((slices % 3) == 1)
            numIndices += 3;
        else if ((slices % 3) == 2)
            numIndices += 4;
        
        indices = new int[ numIndices ];
        
        if (slices % 3 == 0)
            stripLengths = new int[ slices / 3 ];
        else
            stripLengths = new int[ slices / 3 + 1 ];
        for (int i = 0; i < stripLengths.length; i++)
        {
            if (i == stripLengths.length - 1)
            {
                if (slices % 3 == 0)
                    stripLengths[ i ] = 5;
                else if (slices % 3 == 1)
                    stripLengths[ i ] = 3;
                else
                    stripLengths[ i ] = 4;
            }
            else
            {
                stripLengths[ i ] = 5;
            }
        }
        
        vertices[ 0 ] = new Point3f( 0.0f, 1.0f, 0.0f );
        if (normals != null)
            normals[ 0 ] = new Vector3f( 0.0f, 1.0f, 0.0f );
        if (texCoords != null)
            texCoords[ 0 ] = new TexCoord2f( 0.5f, 0.5f );
        
        int v = 1;
        int i = 0;
        for (int s = 0; s < slices + 1; s += 3)
        {
            for (int t = 0; (t < 3) && (s + t < slices + 1); t++)
            {
                final float a = (float)(s + t) * FastMath.TWO_PI / (float)slices;
                
                vertices[ v + t ] = new Point3f( FastMath.cos( a ), 0.0f, -FastMath.sin( a ) );
                if (normals != null)
                    normals[ v + t ] = new Vector3f( 0.0f, 1.0f, 0.0f );
                if (texCoords != null)
                    texCoords[ v + t ] = new TexCoord2f( 0.5f, 0.5f );
            }
            
            indices[ i++ ] = v;
            indices[ i++ ] = v + 1;
            indices[ i++ ] = 0;
            if (i < indices.length)
                indices[ i++ ] = v + 2;
            if (i < indices.length)
                indices[ i++ ] = v + 3;
            
            v += 3;
        }
        
        if ((features & GeometryArray.COLOR_3) > 0)
            colors = GeomFactory.generateColors3( vertices );
        
        return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, vertices, normals, texCoords, colors, indices, stripLengths ) );
    }
    */
    
    private static final Vector3f compNormal( Point3f vertex, Point3f tip, float height )
    {
        if ( height == 0.0f )
        {
            return ( new Vector3f( 0f, 1f, 0f ) );
        }
        
        Vector3f tmp1 = Vector3f.fromPool();
        Vector3f tmp2 = Vector3f.fromPool();
        
        tmp1.sub( vertex, tip );
        tmp2.sub( vertex, Point3f.ZERO );
        
        Vector3f normal = new Vector3f();
        
        normal.cross( tmp2, tmp1 );
        
        normal.cross( tmp1, normal );
        
        normal.normalize();
        
        Vector3f.toPool( tmp2 );
        Vector3f.toPool( tmp1 );
        
        return ( normal );
    }
    
    private static final float PI_FOURTH = FastMath.PI / 4f;
    
    private static final float O1 = PI_FOURTH;
    private static final float O2 = PI_FOURTH * 2f;
    private static final float O3 = PI_FOURTH * 3f;
    private static final float O4 = PI_FOURTH * 4f;
    private static final float O5 = PI_FOURTH * 5f;
    private static final float O6 = PI_FOURTH * 6f;
    private static final float O7 = PI_FOURTH * 7f;
    private static final float O8 = PI_FOURTH * 8f;
    
    private static final TexCoord2f getTexCoord( float angle )
    {
        float s = 0f;
        float t = 0f;
        
        if ( angle <= O1 )
        {
            s = 1.0f;
            
            t = FastMath.sin( angle * 2f );
            t *= 0.5f;
            t += 0.5f;
        }
        else if ( angle <= O2 )
        {
            t = 1.0f;
            
            s = FastMath.sin( ( O2 - angle ) * 2f );
            s *= 0.5f;
            s += 0.5f;
        }
        else if ( angle <= O3 )
        {
            t = 1.0f;
            
            s = FastMath.sin( ( angle - O2 ) * 2f );
            s *= -0.5f;
            s += 0.5f;
        }
        else if ( angle <= O4 )
        {
            s = 0.0f;
            
            t = FastMath.sin( ( O4 - angle ) * 2f );
            t *= 0.5f;
            t += 0.5f;
        }
        else if ( angle <= O5 )
        {
            s = 0.0f;
            
            t = FastMath.sin( ( angle - O4 ) * 2f );
            t *= -0.5f;
            t += 0.5f;
        }
        else if ( angle <= O6 )
        {
            t = 0.0f;
            
            s = FastMath.sin( ( O6 - angle ) * 2f );
            s *= -0.5f;
            s += 0.5f;
        }
        else if ( angle <= O7 )
        {
            t = 0.0f;
            
            s = FastMath.sin( ( angle - O6 ) * 2f );
            s *= 0.5f;
            s += 0.5f;
        }
        else// if ( angle <= O8 )
        {
            s = 1.0f;
            
            t = FastMath.sin( ( O8 - angle ) * 2f );
            t *= -0.5f;
            t += 0.5f;
        }
        
        return ( new TexCoord2f( s, t ) );
    }
    
    /**
     * Generate the GeometryConstruct for a TriangleArray to build
     * a Cone pointing in the +y direction (with unit radius and length).
     * 
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATES_2 ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTA( float radius, float height, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( slices < 3 )
        {
            throw new IllegalArgumentException( "slices < 3" );
        }
        
        Point3f[] vertices = new Point3f[ 3 * slices ];
        Vector3f[] normals = new Vector3f[ 3 * slices ];
        TexCoord2f[] texture = new TexCoord2f[ 3 * slices ];
        
        Point3f tip = new Point3f( 0f, height, 0f );
        
        int index = 0;
        for ( int i = 0; i < slices; i++ )
        {
            final float angle0 = ( i + 0 ) * ( FastMath.TWO_PI / slices );
            final float angle1 = ( i + 1 ) * ( FastMath.TWO_PI / slices );
            
            // first corner
            vertices[ index ] = new Point3f( FastMath.cos( angle0 ) * radius, 0f, -FastMath.sin( angle0 ) * radius );
            normals[ index ] = compNormal( vertices[ index ], tip, height );
            texture[ index ] = getTexCoord( angle0 );
            index++;
            
            // second corner (tip)
            vertices[ index ] = new Point3f( tip );
            normals[ index ] = new Vector3f( normals[ index - 1 ] );
            texture[ index ] = new TexCoord2f( 0.5f, 0.5f );
            index++;
            
            // third corner
            vertices[ index ] = new Point3f( FastMath.cos( angle1 ) * radius, 0f, -FastMath.sin( angle1 ) * radius );
            normals[ index ] = compNormal( vertices[ index ], tip, height );
            texture[ index ] = getTexCoord( angle1 );
            index++;
        }
        
        return ( new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, vertices, normals, texture ) );
    }
    
    /**
     * Generate a TriangleArray to build
     * a Cone pointing in the +y direction (with unit radius and length).
     * 
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATES_2 ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleArray createGeometryTA( float radius, float height, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct gc = createGeometryConstructTA( radius, height, slices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTriangleArray( gc ) );
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
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the sphere
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static Geometry createGeometry( float radius, float height, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( radius, height, slices, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Cone( float radius, float height, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( createGeometry( radius, height, slices, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Cone( int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( 1.0f, 1.0f, slices, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to apply to this Shape's Appearance
     */
    public Cone( float radius, float height, int slices, Texture texture )
    {
        this( radius, height, slices, Geometry.COORDINATES | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to apply to this Shape's Appearance
     */
    public Cone( int slices, Texture texture )
    {
        this( 1.0f, 1.0f, slices, Geometry.COORDINATES | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to apply to this Shape's Appearance
     */
    public Cone( float radius, float height, int slices, String texture )
    {
        this( radius, height, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to apply to this Shape's Appearance
     */
    public Cone( int slices, String texture )
    {
        this( 1.0f, 1.0f, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the cone
     * @param color the color to apply to this Shape's Appearance
     */
    public Cone( float radius, float height, int slices, Colorf color )
    {
        this( radius, height, slices, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param slices Number of vertical stripes down the cone
     * @param color the color to apply to this Shape's Appearance
     */
    public Cone( int slices, Colorf color )
    {
        this( 1.0f, 1.0f, slices, color );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param radius the cone's base-radius
     * @param height the cone's height
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     */
    public Cone( float radius, float height, int slices, Appearance app )
    {
        this( radius, height, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Generate an open cone pointing in the +z direction.
     * Has unit height and radius.
     * 
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     */
    public Cone( int slices, Appearance app )
    {
        this( 1.0f, 1.0f, slices, app );
    }
}
