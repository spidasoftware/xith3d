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
import org.openmali.vecmath2.TexCoord2f;
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
 * A simple Capsule Shape3D.<br>
 * <br>
 * The length of the Capsule is the length of the body (without the caps).<br>
 * The radius is the radius of the body and the caps.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Capsule extends Shape3D
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
     * Creates the GeometryConstruct for a Capsule.
     * 
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param radius the radius of the capsule
     * @param length the length of the body Cylinder
     * @param features the Geometry features
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @return the GeometryConstruct for a Hemisphere
     */
    public static GeometryConstruct createGeometryConstructTA( int slices, int stacks, float radius, float length, int features, boolean colorAlpha, int texCoordsSize )
    {
        GeometryConstruct gcCapTop = Hemisphere.createGeometryConstructTA( slices, stacks, features, colorAlpha, texCoordsSize );
        GeometryConstruct gcCapBottom = Hemisphere.createGeometryConstructTA( slices, stacks, features, colorAlpha, texCoordsSize );
        GeometryConstruct gcBody = Cylinder.createGeometryConstructTA( 1f, 1f, 1f, false, slices,1, features, colorAlpha, texCoordsSize );
        
        // transform geometry parts to build a Capsule
        {
            if ( radius != 1.0f )
                StaticTransform.scale( gcCapTop.getCoordinates(), radius, radius, radius );
            StaticTransform.translate( gcCapTop.getCoordinates(), 0.0f, +( length / 2.0f ), 0.0f );
            
            if ( ( radius != 1.0f ) || ( length != 1.0f ) )
                StaticTransform.scale( gcBody.getCoordinates(), radius, length, radius );
            
            //StaticTransform.mirrorZX( gcCapBottom.getCoordinates() );
            //StaticTransform.mirrorZX( gcCapBottom.getNormals() );
            if ( radius != 1.0f )
                StaticTransform.scale( gcCapBottom.getCoordinates(), radius, radius, radius );
            StaticTransform.rotateX( gcCapBottom.getCoordinates(), -FastMath.PI );
            StaticTransform.rotateX( gcCapBottom.getNormals(), -FastMath.PI );
            StaticTransform.translate( gcCapBottom.getCoordinates(), 0.0f, -( length / 2.0f ), 0.0f );
        }
        
        // fix texture-coordinates to slide smoothly into each others
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texCoordsSize == 2 )
            {
                final float relTopSize = ( 1.0f / ( radius + radius + length ) ) * radius;
                final float relBodySize = ( 1.0f / ( radius + radius + length ) ) * length;
                final float relBottomSize = ( 1.0f / ( radius + radius + length ) ) * radius;
                
                for ( int i = 0; i < gcCapTop.getTextureCoordinates2f().length; i++ )
                {
                    TexCoord2f tc = gcCapTop.getTextureCoordinates2f()[ i ];
                    
                    tc.mulT( relTopSize );
                    tc.addT( relBottomSize + relBodySize );
                }
                
                for ( int i = 0; i < gcBody.getTextureCoordinates2f().length; i++ )
                {
                    TexCoord2f tc = gcBody.getTextureCoordinates2f()[ i ];
                    
                    tc.mulT( relBodySize );
                    tc.addT( relBottomSize );
                }
                
                for ( int i = 0; i < gcCapBottom.getTextureCoordinates2f().length; i++ )
                {
                    TexCoord2f tc = gcCapBottom.getTextureCoordinates2f()[ i ];
                    
                    tc.mulT( relBottomSize );
                    tc.addT( relBottomSize - tc.getT() ); // TODO add or set?
                    
                    tc.setS( 1.0f - tc.getS() );
                }
            }
        }
        
        Tuple3f[] vertices = null;
        Vector3f[] normals = null;
        TexCoord2f[] texCoords = null;
        
        //if ((features & GeometryArray.COORDINATES) > 0)
        {
            vertices = new Tuple3f[ gcCapTop.numVertices() + gcBody.numVertices() + gcCapBottom.numVertices() ];
            
            int j = 0;
            for ( int i = 0; i < gcCapTop.numVertices(); i++ )
            {
                vertices[ j++ ] = gcCapTop.getCoordinates()[ i ];
            }
            for ( int i = 0; i < gcBody.numVertices(); i++ )
            {
                vertices[ j++ ] = gcBody.getCoordinates()[ i ];
            }
            for ( int i = 0; i < gcCapBottom.numVertices(); i++ )
            {
                vertices[ j++ ] = gcCapBottom.getCoordinates()[ i ];
            }
        }
        
        if ( ( features & Geometry.NORMALS ) > 0 )
        {
            normals = new Vector3f[ gcCapTop.numNormals() + gcBody.numNormals() + gcCapBottom.numNormals() ];
            
            int j = 0;
            for ( int i = 0; i < gcCapTop.numNormals(); i++ )
            {
                normals[ j++ ] = gcCapTop.getNormals()[ i ];
            }
            for ( int i = 0; i < gcBody.numNormals(); i++ )
            {
                normals[ j++ ] = gcBody.getNormals()[ i ];
            }
            for ( int i = 0; i < gcCapBottom.numNormals(); i++ )
            {
                normals[ j++ ] = gcCapBottom.getNormals()[ i ];
            }
        }
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texCoordsSize == 2 )
            {
                texCoords = new TexCoord2f[ gcCapTop.numTextureCoordinates2f() + gcBody.numTextureCoordinates2f() + gcCapBottom.numTextureCoordinates2f() ];
                
                int j = 0;
                for ( int i = 0; i < gcCapTop.numTextureCoordinates2f(); i++ )
                {
                    texCoords[ j++ ] = gcCapTop.getTextureCoordinates2f()[ i ];
                }
                for ( int i = 0; i < gcBody.numTextureCoordinates2f(); i++ )
                {
                    texCoords[ j++ ] = gcBody.getTextureCoordinates2f()[ i ];
                }
                for ( int i = 0; i < gcCapBottom.numTextureCoordinates2f(); i++ )
                {
                    texCoords[ j++ ] = gcCapBottom.getTextureCoordinates2f()[ i ];
                }
            }
        }
        
        return ( new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, vertices, normals, texCoords ) );
    }
    
    /**
     * Creates the Geometry for a Capsule.
     * 
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param radius the radius of the capsule
     * @param length the length of the body Cylinder
     * @param features the Geometry features
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @return the GeometryConstruct for a Hemisphere
     */
    public static TriangleArray createGeometryTA( int slices, int stacks, float radius, float length, int features, boolean colorAlpha, int texCoordsSize )
    {
        features |= Geometry.COORDINATES;
        
        GeometryConstruct data = createGeometryConstructTA( slices, stacks, radius, length, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTriangleArray( data ) );
    }
    
    public static Geometry createGeometry( int slices, int stacks, float radius, float length, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( slices, stacks, radius, length, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Capsule( int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( createGeometry( slices, stacks, 1.0f, 1.0f, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param radius the radius of the capsule
     * @param length the length of the body Cylinder
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Capsule( float radius, float length, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( createGeometry( slices, stacks, radius, length, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param radius the radius of the capsule
     * @param length the length of the body Cylinder
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Capsule( float radius, float length, int slices, int stacks, Texture texture )
    {
        this( radius, length, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Capsule( int slices, int stacks, Texture texture )
    {
        this( slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param radius the radius of the capsule
     * @param length the length of the body Cylinder
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Capsule( float radius, float length, int slices, int stacks, String texture )
    {
        this( radius, length, slices, stacks, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param texture the texture to apply to the Sphere's Appearance
     */
    public Capsule( int slices, int stacks, String texture )
    {
        this( slices, stacks, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param radius the radius of the capsule
     * @param length the length of the body Cylinder
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param color the color to apply to the Sphere's Appearance
     */
    public Capsule( float radius, float length, int slices, int stacks, Colorf color )
    {
        this( radius, length, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param color the color to apply to the Sphere's Appearance
     */
    public Capsule( int slices, int stacks, Colorf color )
    {
        this( 1.0f, 1.0f, slices, stacks, color );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param radius the radius of the capsule
     * @param length the length of the body Cylinder
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param app the Appearance to be applied to this Shape
     */
    public Capsule( float radius, float length, int slices, int stacks, Appearance app )
    {
        this( radius, length, slices, stacks, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Creates a capsule using standard specifications.
     * 
     * @param slices the slices of the cap Hemispheres and the body Cylinder
     * @param stacks the stacks of <b>each</b> cap Hemisphere.
     * @param app the Appearance to be applied to this Shape
     */
    public Capsule( int slices, int stacks, Appearance app )
    {
        this( 1.0f, 1.0f, slices, stacks, app );
    }
}
