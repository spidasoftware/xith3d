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
package org.xith3d.effects.bumpmapping;

import java.io.IOException;

import org.jagatoo.opengl.enums.TextureFormat;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.utility.geometry.TangentsFactory;
import org.xith3d.utility.geometry.TangentsFactory.TangentsStoreMode;

/**
 * The BumpmappingFactory is capable of preparing a Shape3D for Bumpmapping.
 * the underlying methods to calculate tangets/bitangets and to load the
 * appropriate shaders are also public to use them for different perposes.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class BumpMappingFactory
{
    private static final TangentsFactory tangentsFactory = TangentsFactory.getInstance();
    
    private int normalMapTextureUnit = 1;
    
    private int tangentsTextureUnit = 1;
    private int bitangentsTextureUnit = 2;
    
    private int tangentsVertexAttribute = 5;
    private int bitangentsVertexAttribute = 7;
    
    private TangentsStoreMode tangentsStoreMode = TangentsStoreMode.VERTEX_ATTRIBUTES;
    
    private Light bumpLight = null;
    
    public static final TangentsFactory getTangentsFactory()
    {
        return ( tangentsFactory );
    }
    
    /**
     * Sets the texture-unit to be used for the normal map Texture.
     * 
     * @param textureUnit
     */
    public void setNormalMapTextureUnit( int textureUnit )
    {
        this.normalMapTextureUnit = textureUnit;
    }
    
    /**
     * @return the texture-unit to be used for the normal map Texture.
     */
    public int getNormalMapTextureUnit()
    {
        return ( normalMapTextureUnit );
    }
    
    /**
     * Sets the texture-unit to be used to store the tangents.
     * 
     * @param textureUnit
     */
    public void setTangentsTextureUnit( int textureUnit )
    {
        this.tangentsTextureUnit = textureUnit;
    }
    
    /**
     * @return the texture-unit to be used to store the tangents.
     */
    public int getTangentsTextureUnit()
    {
        return ( tangentsTextureUnit );
    }
    
    /**
     * Sets the texture-unit to be used to store the bitangents.
     * 
     * @param textureUnit
     */
    public void setBiTangentsTextureUnit( int textureUnit )
    {
        this.bitangentsTextureUnit = textureUnit;
    }
    
    /**
     * @return the texture-unit to be used to store the bitangents.
     */
    public int getBiTangentsTextureUnit()
    {
        return ( bitangentsTextureUnit );
    }
    
    /**
     * Sets the vertex-attribute-index to be used to store the tangents.
     * 
     * @param vertexAttribIndex
     */
    public void setTangentsVertexAttribute( int vertexAttribIndex )
    {
        this.tangentsVertexAttribute = vertexAttribIndex;
    }
    
    /**
     * @return the vertex-attribute-index to be used to store the tangents.
     */
    public int getTangentsVertexAttribute()
    {
        return ( tangentsVertexAttribute );
    }
    
    /**
     * Sets the vertex-attribute-index to be used to store the bitangents.
     * 
     * @param vertexAttribIndex
     */
    public void setBiTangentsVertexAttribute( int vertexAttribIndex )
    {
        this.bitangentsVertexAttribute = vertexAttribIndex;
    }
    
    /**
     * @return the vertex-attribute-index to be used to store the bitangents.
     */
    public int getBiTangentsVertexAttribute()
    {
        return ( bitangentsVertexAttribute );
    }
    
    /**
     * Sets the store-mode for the tangents and bitangents.
     * 
     * @param mode
     */
    public void setTangentsStoreMode( TangentsStoreMode mode )
    {
        if ( mode == null )
            throw new NullPointerException( "mode must not be null" );
        
        this.tangentsStoreMode = mode;
    }
    
    /**
     * @return the store-mode for the tangents and bitangents.
     */
    public TangentsStoreMode getTangentsStoreMode()
    {
        return ( tangentsStoreMode );
    }
    
    protected int getStoreIndex1()
    {
        if ( getTangentsStoreMode() == TangentsStoreMode.VERTEX_ATTRIBUTES )
        {
            return ( getTangentsVertexAttribute() );
        }
        else if ( getTangentsStoreMode() == TangentsStoreMode.TEXTURE_COORDINATES )
        {
            return ( getTangentsTextureUnit() );
        }
        else
        {
            throw new Error( "Invalid store-mode." );
        }
    }
    
    protected int getStoreIndex2()
    {
        if ( getTangentsStoreMode() == TangentsStoreMode.VERTEX_ATTRIBUTES )
        {
            return ( getBiTangentsVertexAttribute() );
        }
        else if ( getTangentsStoreMode() == TangentsStoreMode.TEXTURE_COORDINATES )
        {
            return ( getBiTangentsTextureUnit() );
        }
        else
        {
            throw new Error( "Invalid store-mode." );
        }
    }
    
    public void setLight( Light light )
    {
        this.bumpLight = light;
        System.err.println( "Warning: the light-source is not yet taken into account!" );
    }
    
    public Light getLight()
    {
        return ( bumpLight );
    }
    
    /**
     * Loads the normal-map-Texture.
     * 
     * @param textureName
     * @return the normal-map-Texture.
     */
    public static Texture loadNormalMap( String textureName )
    {
        Texture tex = TextureLoader.getInstance().getTexture( textureName,
                                                              TextureFormat.RGB,
                                                              Texture.MipmapMode.BASE_LEVEL
                                                            );
        
        return ( tex );
    }
    
    /**
     * Prepares the given Shape3D for BumpMapping.<br>
     * This calculates the tangets and bitangets and stores them in the texture units 1 and 2.<br>
     * It also attaches a vertex- and fragment shader to calculate bumpmapping.
     * 
     * @param shape
     * @param normalMapTex
     */
    public abstract void prepareForBumpMapping( Shape3D shape, Texture normalMapTex ) throws IOException;
    
    /**
     * Prepares the given Shape3D for BumpMapping.<br>
     * This calculates the tangets and bitangets and stores them in the texture units 1 and 2.<br>
     * It also attaches a vertex- and fragment shader to calculate bumpmapping.
     * 
     * @param shape
     * @param normalMapTex
     */
    public final void prepareForBumpMapping( Shape3D shape, String normalMapTex ) throws IOException
    {
        prepareForBumpMapping( shape, loadNormalMap( normalMapTex ) );
    }
}
