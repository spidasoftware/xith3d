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
package org.xith3d.terrain;

import java.net.URL;

import org.jagatoo.opengl.enums.FaceCullMode;
import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFilter;
import org.jagatoo.opengl.enums.TextureMode;
import org.openmali.vecmath2.TexCoord2f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.*;

/**
 * This is a basic example GridShader implementation to show,
 * how to improve terrain rendering by utilizing a detail texture.
 */
public class TextureSurface implements GridSurface
{
    private Appearance appearance;
    private float detailRepeat = 4;
    private boolean hasDetailTexture;
    private float s1 = 0.0f;
    private float t1 = 0.0f;
    private float s2 = 1.0f;
    private float t2 = 1.0f;
    private float as=  1.0f;
    private float at = 1.0f;
    
    /**
     * Constructs a GridShader with a single texture. 
     * 
     * @param colorTexture the name/path of the main color texture for the grid. 
     */
    public TextureSurface( URL mainTexture)
    {
        this( mainTexture, null, 0, null );
    }
    
    /**
     * Constructs a GridShader with a single texture. 
     * 
     * @param colorTexture the name/path of the main color texture for the grid. 
     * @param material the material properties of this surface
     */
    public TextureSurface( URL mainTexture, Material material )
    {
        this( mainTexture, null, 0, null, material );
    }
    
    public TextureSurface( GridResourceSpec<GridSurface> spec )
    {
        this( spec.getLocations()[0], ( spec.getLocations().length < 2 ) ? null : spec.getLocations()[1], 16, TextureMode.BLEND );
        
        s1 = spec.getS1();
        s2 = spec.getS2();
        t1 = spec.getT1();
        t2 = spec.getT2();
        as = 1f / ( s2 - s1 );
        at = 1f / ( t2 - t1 );
    }

    /**
     * Constructs a GridShader with detail texture support. 
     * 
     * @param mainTexture the name/path of the main color texture for the grid. 
     * @param detailTexture the name/path of the repeating detail texture.
     * @param detailRepeat the amount of repetitions (per side) of the detail texture 
     * @param blendMode the blend mode of the detail texture (see TextureAttributes)
     */
    public TextureSurface( URL mainTexture, URL detailTexture, float detailRepeat, TextureMode blendMode )
    {
        this( mainTexture, detailTexture, detailRepeat, blendMode, new Material() );
        
        getAppearance().getMaterial().setLightingEnabled( false );
    }
    
    /**
     * Constructs a GridShader with detail texture support. 
     * 
     * @param mainTexture the name/path of the main color texture for the grid. 
     * @param detailTexture the name/path of the repeating detail texture.
     * @param detailRepeat the amount of repetitions (per side) of the detail texture 
     * @param blendMode the blend mode of the detail texture (see TextureAttributes)
     * @param material the material properties of this surface
     */
    public TextureSurface( URL mainTexture, URL detailTexture, float detailRepeat, TextureMode blendMode, Material material )
    {
        // Set up a basic appearance to hold the textures
        Appearance a = new Appearance();
        a.setMaterial( material );
        
        // Store the repeat value for later use in map()
        this.detailRepeat = detailRepeat;
        
        // We need to set up a list of texture unit states as TextureAttributes to assign multiple textures 
        TextureLoader tl = TextureLoader.getInstance();
        Texture texture = tl.loadTexture( mainTexture, Texture.MipmapMode.BASE_LEVEL );
        texture.removeFromCache();
        texture.setBoundaryModes( TextureBoundaryMode.CLAMP_TO_EDGE, TextureBoundaryMode.CLAMP_TO_EDGE );
        texture.setFilter( TextureFilter.TRILINEAR );
        TextureAttributes colorAttrs = new TextureAttributes( TextureAttributes.REPLACE, null, null, TextureAttributes.NICEST );
        TextureUnit color = new TextureUnit( texture, colorAttrs );

        hasDetailTexture = ( detailTexture != null );
        if ( hasDetailTexture )
        {
            // Use the given blend mode to blend the detail texture into the maintexture 
            texture = tl.loadTexture( detailTexture, Texture.MipmapMode.MULTI_LEVEL_MIPMAP );
            texture.setBoundaryModes( TextureBoundaryMode.WRAP, TextureBoundaryMode.WRAP );
            texture.setFilter( TextureFilter.TRILINEAR );
            TextureAttributes detailAttrs = new TextureAttributes( blendMode, null, null, TextureAttributes.NICEST );
            TextureUnit details = new TextureUnit( texture, detailAttrs );

            // Add the created unit states to the appearance 
            a.setTextureUnits( color, details );
        }
        else
        {
            a.setTextureUnits( color );
        }

        // TODO: check why the cull mode (or my geometry) is screwed...
        a.setPolygonAttributes( new PolygonAttributes( FaceCullMode.FRONT ) );
        
        this.appearance = a;
    }
    
   
    public void release()
    {
    }
    
    /**
     * We only set up one appearance for our grid. 
     */
    public Appearance getAppearance()
    {
        return ( this.appearance );
    }
    
    public int getTextureUnits()
    {
        return ( hasDetailTexture ? 2 : 1 );
    }
    
    public TexCoord2f map( float s, float t, int unit )
    {
        float s_ = as * ( s - s1 );
        float t_ = at * ( t - t1 );
        
        return ( ( unit == 0 ) ? new TexCoord2f( s_, t_ ) : new TexCoord2f( s_ * detailRepeat, t_ * detailRepeat ) );
    }
}
