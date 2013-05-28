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
package org.xith3d.resources;

import org.jagatoo.loaders.textures.locators.TextureStreamLocator;
import org.jagatoo.opengl.enums.TextureFormat;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loaders.texture.TextureLoader.FlipMode;
import org.xith3d.scenegraph.Texture;

/**
 * This is a ResourceRequest for a Texture resource to be loaded with
 * TextureLoader.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureResourceRequest implements ResourceRequest
{
    private final String name;
    private final String bagName;
    private final TextureFormat format;
    private final boolean flipped;
    private final Texture.MipmapMode mipmapMode;
    private final boolean fallbackTextureAccepted;
    private final TextureStreamLocator tsl;
    
    /**
     * {@inheritDoc}
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getBagName()
    {
        return ( bagName );
    }
    
    /**
     * @return if the Texture resource is to be flipped vertically
     */
    public final boolean flipped()
    {
        return ( flipped );
    }
    
    /**
     * @return the basic format of the Texture (RGB, RGBA, ...)
     */
    public final TextureFormat format()
    {
        return ( format );
    }
    
    /**
     * @return if the Texture resource is to be loaded with mipmap
     */
    public final Texture.MipmapMode mipmapMode()
    {
        return ( mipmapMode );
    }
    
    /**
     * @return if false and the fallback Texture was loaded, <i>null</i> is put
     *         into the ResourceBag
     */
    public final boolean fallbackTextureAccepted()
    {
        return ( fallbackTextureAccepted );
    }
    
    /**
     * @return the TextureStreamLocator used to load this Texture resource (or <i>null</i>)
     */
    public final TextureStreamLocator getTSL()
    {
        return ( tsl );
    }
    
    /**
     * {@inheritDoc}
     */
    public Object loadResource( ResourceLocator resLoc, ResourceBag resBag )
    {
        if ( getTSL() != null )
            TextureLoader.getInstance().addTextureStreamLocator( getTSL() );
        
        Texture tex = TextureLoader.getInstance().getTexture( getName(), flipped() ? FlipMode.FLIPPED_VERTICALLY : FlipMode.NOT_FLIPPED, format(), mipmapMode() );
        
        if ( ( tex == TextureLoader.getFallbackTexture() ) && !fallbackTextureAccepted() )
            tex = null;
        
        if ( getTSL() != null )
            TextureLoader.getInstance().removeTextureStreamLocator( getTSL() );
        
        resBag.addTexture( getBagName(), tex );
        
        return ( tex );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     * @param mipmapMode load mipmap?
     * @param flipped flip the texture vertically?
     * @param fallbackTextureAccepted if false and the fallback Texture was loaded, <i>null</i> is put
     *                                into the ResourceBag
     * @param tsl the TextureStreamLocator to be used to locate the resource
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format, Texture.MipmapMode mipmapMode, boolean flipped, boolean fallbackTextureAccepted, TextureStreamLocator tsl )
    {
        this.name = name;
        this.bagName = bagName;
        this.format = format;
        this.flipped = flipped;
        this.mipmapMode = mipmapMode;
        this.fallbackTextureAccepted = fallbackTextureAccepted;
        this.tsl = tsl;
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     * @param mipmapMode load mipmap?
     * @param flipped flip the texture vertically?
     * @param tsl the TextureStreamLocator to be used to locate the resource
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format, Texture.MipmapMode mipmapMode, boolean flipped, TextureStreamLocator tsl )
    {
        this( name, bagName, format, mipmapMode, flipped, true, tsl );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     * @param mipmapMode load mipmap?
     * @param tsl the TextureStreamLocator to be used to locate the resource
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format, Texture.MipmapMode mipmapMode, TextureStreamLocator tsl )
    {
        this( name, bagName, format, mipmapMode, true, true, tsl );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     * @param tsl the TextureStreamLocator to be used to locate the resource
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format, TextureStreamLocator tsl )
    {
        this( name, bagName, format, Texture.MipmapMode.MULTI_LEVEL_MIPMAP, true, true, tsl );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param tsl the TextureStreamLocator to be used to locate the resource
     */
    public TextureResourceRequest( String name, String bagName, TextureStreamLocator tsl )
    {
        this( name, bagName, (TextureFormat)null, Texture.MipmapMode.MULTI_LEVEL_MIPMAP, true, true, tsl );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     * @param mipmapMode load mipmap?
     * @param flipped flip the texture vertically?
     * @param fallbackTextureAccepted if false and the fallback Texture was loaded, <i>null</i> is put
     *                                into the ResourceBag
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format, Texture.MipmapMode mipmapMode, boolean flipped, boolean fallbackTextureAccepted )
    {
        this( name, bagName, format, mipmapMode, flipped, true, (TextureStreamLocator)null );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     * @param mipmapMode load mipmap?
     * @param flipped flip the texture vertically?
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format, Texture.MipmapMode mipmapMode, boolean flipped )
    {
        this( name, bagName, format, mipmapMode, flipped, true, (TextureStreamLocator)null );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     * @param mipmapMode load mipmap?
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        this( name, bagName, format, mipmapMode, true, true, (TextureStreamLocator)null );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     * @param format load alpha channel?
     */
    public TextureResourceRequest( String name, String bagName, TextureFormat format )
    {
        this( name, bagName, format, Texture.MipmapMode.MULTI_LEVEL_MIPMAP, true, true, (TextureStreamLocator)null );
    }
    
    /**
     * Creates a new TextureResourceRequest.
     * 
     * @param name the name of the Texture resource to use for loading
     * @param bagName the name, that the Texture resource will carry in the ResourceBag
     */
    public TextureResourceRequest( String name, String bagName )
    {
        this( name, bagName, (TextureFormat)null, Texture.MipmapMode.MULTI_LEVEL_MIPMAP, true, true, (TextureStreamLocator)null );
    }
}
