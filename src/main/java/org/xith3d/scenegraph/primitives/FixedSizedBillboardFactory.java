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

import java.awt.Font;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * A factory class to create {@link FixedSizedBillboard} instances with specific
 * parameters.
 * 
 * @author Herve
 */
public class FixedSizedBillboardFactory
{
    private FixedSizedBillboardFactory()
    {
        
    }
    
    public static FixedSizedBillboard createColoredBillboard( float width, float height, Colorf color )
    {
        FixedSizedBillboard billboard = new FixedSizedBillboard( null, width, height );
        billboard.getAppearance( true ).setColor( color );
        
        return ( billboard );
    }
    
    public static FixedSizedBillboard createColoredBillboard( Tuple3f center, float width, float height, Colorf color )
    {
        FixedSizedBillboard billboard = new FixedSizedBillboard( center, width, height );
        billboard.getAppearance( true ).setColor( color );
        
        return ( billboard );
    }
    
    public static FixedSizedBillboard createTexturedBillboard( Tuple3f center, float width, float height, Texture texture )
    {
        FixedSizedBillboard billboard = new FixedSizedBillboard( center, width, height );
        billboard.setTexture( texture, null, null );
        
        return ( billboard );
    }
    
    public static FixedSizedBillboard createTexturedBillboard( Tuple3f center, float width, float height, Texture texture, Tuple2f textureLowerLeft, Tuple2f textureUpperRight )
    {
        FixedSizedBillboard billboard = new FixedSizedBillboard( center, width, height );
        billboard.setTexture( texture, textureLowerLeft, textureUpperRight );
        
        return ( billboard );
    }
    
    public static FixedSizedBillboard createTexturedBillboard( Tuple3f center, float width, float height, String textureName, Tuple2f textureLowerLeft, Tuple2f textureUpperRight )
    {
        FixedSizedBillboard billboard = new FixedSizedBillboard( center, width, height );
        final Texture texture = TextureLoader.getInstance().getTextureOrNull( textureName, MipmapMode.MULTI_LEVEL_MIPMAP );
        billboard.setTexture( texture, textureLowerLeft, textureUpperRight );
        
        return ( billboard );
    }
    
    /**
     * Creates a constant-size billboard from the given {@link Texture} and
     * texture coordinates.
     * 
     * @param screenWidth desired width on screen.
     * @param screenHeight desired height on screen.
     * @param textureName name of the texture to use.
     * @param textureLowerLeft
     * @param textureUpperRight
     */
    public static FixedSizedBillboard createConstantSizeTexturedBillboard( int screenWidth, int screenHeight, String textureName, Tuple2f textureLowerLeft, Tuple2f textureUpperRight )
    {
        final Texture texture = TextureLoader.getInstance().getTextureOrNull( textureName );
        
        return ( createConstantSizeTexturedBillboard( screenWidth, screenHeight, texture, textureLowerLeft, textureUpperRight ) );
    }
    
    /**
     * Creates a constant-size billboard from the given {@link Texture} and
     * texture coordinates.
     * 
     * @param screenWidth desired width on screen.
     * @param screenHeight desired height on screen.
     * @param texture texture to use.
     * @param textureLowerLeft
     * @param textureUpperRight
     */
    public static FixedSizedBillboard createConstantSizeTexturedBillboard( int screenWidth, int screenHeight, Texture texture, Tuple2f textureLowerLeft, Tuple2f textureUpperRight )
    {
        FixedSizedBillboard billboard = new FixedSizedBillboard( null, 1f, 1f );
        billboard.setSizeOnScreen( screenWidth, screenHeight );
        billboard.setTexture( texture, textureLowerLeft, textureUpperRight );
        
        return ( billboard );
    }
    
    /**
     * Creates a {@link FixedSizedBillboard} instance from the given {@link Texture}.
     * The billboard will be rendered as a rectangle with the specified
     * dimensions.
     * 
     * @param screenWidth desired width on screen.
     * @param screenHeight desired height on screen.
     * @param texture texture to use.
     */
    public static FixedSizedBillboard createConstantSizeTexturedBillboard( int screenWidth, int screenHeight, String texture )
    {
        return ( createConstantSizeTexturedBillboard( screenWidth, screenHeight, texture, null, null ) );
    }
    
    /**
     * Creates a {@link FixedSizedBillboard} instance from the given {@link Texture}.
     * The billboard will be rendered as a rectangle with the specified
     * dimensions.
     * 
     * @param screenWidth desired width on screen.
     * @param screenHeight desired height on screen.
     * @param texture texture to use.
     */
    public static FixedSizedBillboard createConstantSizeTexturedBillboard( int screenWidth, int screenHeight, Texture texture )
    {
        return ( createConstantSizeTexturedBillboard( screenWidth, screenHeight, texture, null, null ) );
    }
    
    /**
     * Creates a {@link FixedSizedBillboard} instance from the given {@link Texture}.
     * The billboard will be rendered as a rectangle with the same dimensions as
     * the given {@link Texture}.
     * 
     * @param texture texture to use.
     */
    public static FixedSizedBillboard createConstantSizeTexturedBillboard( Texture texture )
    {
        return ( createConstantSizeTexturedBillboard( texture.getOriginalWidth(), texture.getOriginalHeight(), texture, null, null ) );
    }
    
    /**
     * Creates a {@link FixedSizedBillboard} instance from the given texture name.
     * The billboard will be rendered as a rectangle with the same dimensions as
     * the given {@link Texture}.
     * 
     * @param texture texture to use.
     */
    public static FixedSizedBillboard createConstantSizeTexturedBillboard( String texture )
    {
        return ( createConstantSizeTexturedBillboard( TextureLoader.getInstance().getTexture( texture ) ) );
    }
    
    public static FixedSizedBillboard createConstantSizedTextBillboard( String text, int alignment, Font font, Colorf color )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        return ( createConstantSizeTexturedBillboard( texture ) );
    }
}
