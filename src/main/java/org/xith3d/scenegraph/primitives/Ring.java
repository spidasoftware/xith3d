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
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;

/**
 * A ring created by crushing a cylinder.
 * 
 * @author Daniel Herring
 * @author Marvin Froehlich (aka Qudus)
 */
public class Ring extends Cylinder
{
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Ring( float alpha, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        super( alpha, slices, features, colorAlpha, texCoordsSize );
        
        StaticTransform.scale( this, 1f, 0f, 1f );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param radius the ring's radius
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Ring( float radius, float alpha, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( alpha, slices, features, colorAlpha, texCoordsSize );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Ring( float alpha, int slices, Texture texture )
    {
        this( alpha, slices, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2 );
        
        getAppearance( true ).setTexture( texture );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param radius the ring's radius
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Ring( float radius, float alpha, int slices, Texture texture )
    {
        this( alpha, slices, texture );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Ring( float alpha, int slices, String texture )
    {
        this( alpha, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param radius the ring's radius
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Ring( float radius, float alpha, int slices, String texture )
    {
        this( alpha, slices, texture );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param color the color to be applied to the ColoringAttributes of the Shape's Appearance
     */
    public Ring( float alpha, int slices, Colorf color )
    {
        this( alpha, slices, Geometry.COORDINATES | Geometry.NORMALS, false, 2 );
        
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param radius the ring's radius
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param color the color to be applied to the ColoringAttributes of the Shape's Appearance
     */
    public Ring( float radius, float alpha, int slices, Colorf color )
    {
        this( alpha, slices, color );
        
        StaticTransform.scale( this, radius );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param app the Appearance to be applied to this shape
     */
    public Ring( float alpha, int slices, Appearance app )
    {
        this( alpha, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * A ring is created by crushing a cylinder.
     * 
     * @param radius the ring's radius
     * @param alpha ratio of inner radius to outer radius
     * @param slices number of slices for the Cylinder (e.g. 36)
     * @param app the Appearance to be applied to this shape
     */
    public Ring( float radius, float alpha, int slices, Appearance app )
    {
        this( alpha, slices, app );
        
        StaticTransform.scale( this, radius );
    }
}
