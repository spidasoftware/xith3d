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
package org.xith3d.loaders.texture;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import org.jagatoo.loaders.textures.AbstractTexture;
import org.jagatoo.loaders.textures.AbstractTextureImage;
import org.jagatoo.loaders.textures.AbstractTextureLoader;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.xith3d.scenegraph.Texture3D;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TextureImage3D;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * Loads Textures from various image resources.<br>
 * <br>
 * Loading (<b>by name</b>) works in the following order:<br>
 * If the texture was already loaded and is still in the cache then the
 * existing texture is returned. So don't modify loaded nad cached textures!<br>
 * All {@link TextureStreamLocator}s are tried in the order, in which they are
 * registered.<br>
 * If a stream was found then the following is tried on the stream:<br>
 * All {@link TextureImageFormatLoader}s are tried in the order, in which they are
 * registered. A texture can then be created from this {@link AbstractTextureImage}.<br>
 * All {@link TextureFormatLoader}s are tried in the order, in which they are
 * registered.<br>
 * At last the fallback {@link TextureImageFormatLoader} is used to load the
 * Texture. It will most probably use ImageIO.<br>
 * The loading stops as soon as a Texture is created.
 * 
 * @author Matthias Mann
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public class TextureLoader extends AbstractTextureLoader
{
    /**
     * The FlipMode defines, if Textures are to be vertically flipped at load
     * time.
     */
    public enum FlipMode
    {
        /**
         * The Texture is loaded as is (not flipped).
         */
        NOT_FLIPPED,

        /**
         * The Texture is flipped vertically
         */
        FLIPPED_VERTICALLY;
        
        public final boolean getBooleanValue()
        {
            return ( this == FLIPPED_VERTICALLY );
        }
    }
    
    protected static Texture2D fallbackTexture = null;
    
    private static final TextureLoader instance = new TextureLoader();
    
    /**
     * @return the fallback Texture 
     */
    public static Texture2D getFallbackTexture()
    {
        if ( fallbackTexture == null )
        {
            fallbackTexture = new Texture2D( TextureFormat.RGB );
            fallbackTexture.setImage( 0, new TextureImage2D( TextureImageFormat.LUMINANCE, 2, 2, new byte[] { 0, -1, -1, 0 } ) );
            fallbackTexture.setName( "Fallback-Texture" );
        }
        
        return ( fallbackTexture );
    }
    
    /**
     * @param tex the sample Texture to test
     * 
     * @return true, if the sample Texture IS the fallback Texture
     */
    public static boolean isFallbackTexture( Texture tex )
    {
        return ( tex == getFallbackTexture() );
    }
    
    /**
     * @return the singleton instance of the TextureLoader.
     */
    public static TextureLoader getInstance()
    {
        return ( instance );
    }
    
    /*
     * @return another instance of the TextureLoader.
     */
    /*
    public static TextureLoader newInstance()
    {
        return ( new TextureLoader() );
    }
    */
    
    
    public TextureImage2D loadTextureImage( String name, FlipMode flipVertically, TextureFormat format, boolean allowStreching )
    {
        final boolean flip = ( flipVertically == null ) || flipVertically.getBooleanValue();
        final boolean acceptAlpha = ( format == null ) || format.hasAlpha();
        
        AbstractTextureImage texImg = loadTextureImage( name, flip, acceptAlpha, allowStreching, Xith3DTextureFactory2D.getInstance() );
        
        if ( texImg == null )
            return ( (TextureImage2D)getFallbackTexture().getImage( 0 ) );
        
        return ( (TextureImage2D)texImg );
    }
    
    public final TextureImage2D loadTextureImage( String name, FlipMode flipVertically, TextureFormat format )
    {
        return ( loadTextureImage( name, flipVertically, format, true ) );
    }
    
    public final TextureImage2D loadTextureImage( String name, TextureFormat format )
    {
        return ( loadTextureImage( name, (FlipMode)null, format, true ) );
    }
    
    public final TextureImage2D loadTextureImage( String name )
    {
        return ( loadTextureImage( name, (FlipMode)null, (TextureFormat)null, true ) );
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onTextureLoaded( AbstractTexture texture, String resourceName )
    {
        ( (Texture)texture ).setResourceName( resourceName );
        //_SG_PrivilegedAccess.setResourceName( (Texture)texture, resourceName );
    }
    
    /**
     * Retrieves the requested Texture resource either from the cache
     * or loads it and stires it to the cache.
     * 
     * @param name The name of the texture.
     * @param flipVertically
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param allowStreching
     * @param useCache
     * @param writeToCache ignored, if useCache is false
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode, boolean allowStreching, boolean useCache, boolean writeToCache )
    {
        final boolean flip = ( flipVertically == null ) || flipVertically.getBooleanValue();
        final boolean acceptAlpha = ( format == null ) || format.hasAlpha();
        final boolean loadMipmaps = ( mipmapMode == null ) || mipmapMode.booleanValue();
        
        AbstractTexture tex = loadOrGetTexture( name, flip, acceptAlpha, loadMipmaps, allowStreching, Xith3DTextureFactory2D.getInstance(), useCache, writeToCache );
        
        if ( tex == null )
            return ( getFallbackTexture() );
        
        return ( (Texture2D)tex );
    }
    
    /**
     * Retrieves the requested Texture resource either from the cache
     * or loads it and stires it to the cache.
     * 
     * @param name The name of the texture.
     * @param flipVertically
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * @param allowStreching
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode, boolean allowStreching )
    {
        return ( getTexture( name, flipVertically, format, mipmapMode, allowStreching, true, true ) );
    }
    
    /**
     * Retrieves the requested Texture resource either from the cache
     * or loads it and stires it to the cache.
     * 
     * @param name The name of the texture.
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public final Texture2D getTexture( String name, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( getTexture( name, flipVertically, format, mipmapMode, true ) );
    }
    
    /**
     * Retrieves the texture with the given name, with mipmap.
     * 
     * @param name the name of the texture.
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name, FlipMode flipVertically )
    {
        return ( getTexture( name, flipVertically, (TextureFormat)null, Texture.MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * Retrieves the Texture with the given name, with mipmap.
     * 
     * @param name the name of the texture.
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name )
    {
        return ( getTexture( name, (FlipMode)null ) );
    }
    
    /**
     * Retrieves the texture with the given name.
     * 
     * @param name The name of the texture.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name, FlipMode flipVertically, Texture.MipmapMode mipmapMode )
    {
        return ( getTexture( name, flipVertically, (TextureFormat)null, mipmapMode ) );
    }
    
    /**
     * Retrieves the texture with the given name.
     * 
     * @param name The name of the texture.
     * @param format Texture.RGB or Texture.RGBA
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name, TextureFormat format )
    {
        return ( getTexture( name, (FlipMode)null, format, (Texture.MipmapMode)null ) );
    }
    
    /**
     * Retrieves the texture with the given name.
     * 
     * @param name The name of the texture.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name, Texture.MipmapMode mipmapMode )
    {
        return ( getTexture( name, (FlipMode)null, mipmapMode ) );
    }
    /**
     * Retrives the texture with the given name. Loading works in the following
     * order:<br>
     * If the texture was already loaded and is still in the cache then the
     * existing texture is returned. So don't modify textures returned by this
     * method.<br>
     * All TextureLocator are tried in the order in which they are registered.<br>
     * All TextureStreamLocator are tried in the order in which they are
     * registered.<br>
     * If a stream was found then the following is tried on the stream:<br>
     * All TextureStreamLoader are tried in the order in which they are
     * registered.<br>
     * All {@link TextureImageFormatLoader} are tried in the order in which they are
     * registered. A texture is then created with this {@link AbstractTextureImage}
     * (mipmaps currently not implemented).<br>
     * At last ImageIO.read() is tried. From this a Texture with optional
     * mipmaps is created.<br>
     * The loading stops as soon as a Texture is created.
     * 
     * @param name The name of the texture.
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D getTexture( String name, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( getTexture( name, (FlipMode)null, format, mipmapMode ) );
    }
    
    
    
    /**
     * This is a convenience method needed many times.<br>
     * It loads the Texture by resource name and with or without alpha channel.
     * If the given texture-name is null or an empty string, null is returned.
     * 
     * @param textureName the requested Texture's name or null
     * @param mipmapMode
     * 
     * @return the loaded Texture or <i>null</i>
     */
    public Texture2D getTextureOrNull( String textureName, MipmapMode mipmapMode )
    {
        final Texture2D texture = getTexture( textureName, mipmapMode );
        
        if ( isFallbackTexture( texture ) )
            return ( null );
        
        return ( texture );
    }
    
    /**
     * This is a convenience method needed many times.<br>
     * It loads the Texture by resource name and with or without alpha channel.
     * If the given texture-name is null or an empty string, null is returned.
     * 
     * @param textureName the requested Texture's name or null
     * 
     * @return the loaded Texture or <i>null</i>
     */
    public Texture2D getTextureOrNull( String textureName )
    {
        return ( getTextureOrNull( textureName, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    
    
    /**
     * Loads the requested Texture resource. (Doesn't check the cache!)
     * 
     * @param imageURL The name of the texture.
     * @param flipVertically
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode, boolean allowStreching )
    {
        final boolean flip = ( flipVertically == null ) || flipVertically.getBooleanValue();
        final boolean acceptAlpha = ( format == null ) || format.hasAlpha();
        final boolean loadMipmaps = ( mipmapMode == null ) || mipmapMode.booleanValue();
        
        AbstractTexture tex = loadTextureFromURL( imageURL, flip, acceptAlpha, loadMipmaps, allowStreching, Xith3DTextureFactory2D.getInstance() );
        
        if ( tex == null )
            return ( getFallbackTexture() );
        
        return ( (Texture2D)tex );
    }
    
    /**
     * Loads the requested Texture resource. (Doesn't check the cache!)
     * 
     * @param imageURL The name of the texture.
     * @param flipVertically
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( imageURL, flipVertically, format, mipmapMode, true ) );
    }
    
    /**
     * Loads the texture with the given name, with mipmap
     * 
     * @param imageURL the texture image resource
     * @param flipVertically
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL, FlipMode flipVertically )
    {
        return ( loadTexture( imageURL, flipVertically, (TextureFormat)null, (Texture.MipmapMode)null ) );
    }
    
    /**
     * Loads the texture with the given name, with mipmap
     * 
     * @param imageURL the texture image resource
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL )
    {
        return ( loadTexture( imageURL, (FlipMode)null ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param imageURL the texture image resource
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( imageURL, (FlipMode)null, mipmapMode ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param imageURL the texture image resource
     * @param format Texture.RGB or Texture.RGBA
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL, TextureFormat format )
    {
        return ( loadTexture( imageURL, (FlipMode)null, format, (Texture.MipmapMode)null ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param imageURL the texture image resource
     * @param flipVertically
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL, FlipMode flipVertically, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( imageURL, flipVertically, (TextureFormat)null, mipmapMode ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param imageURL the texture image resource
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( URL imageURL, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( imageURL, (FlipMode)null, format, mipmapMode ) );
    }
    
    
    
    /**
     * Loads the requested Texture resource. (Doesn't check the cache!)
     * 
     * @param in An InputStream for the texture resource.
     * @param flipVertically
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode, boolean allowStreching )
    {
        if ( !( in instanceof BufferedInputStream ) )
        {
            in = new BufferedInputStream( in );
        }
        
        final boolean flip = ( flipVertically == null ) || flipVertically.getBooleanValue();
        final boolean acceptAlpha = ( format == null ) || format.hasAlpha();
        final boolean loadMipmaps = ( mipmapMode == null ) || mipmapMode.booleanValue();
        
        AbstractTexture tex = loadTextureFromStream( (BufferedInputStream)in, flip, acceptAlpha, loadMipmaps, allowStreching, Xith3DTextureFactory2D.getInstance() );
        
        if ( tex == null )
            return ( getFallbackTexture() );
        
        return ( (Texture2D)tex );
    }
    
    /**
     * Loads the requested Texture resource. (Doesn't check the cache!)
     * 
     * @param in An InputStream for the texture resource.
     * @param flipVertically
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in, FlipMode flipVertically, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( in, flipVertically, format, mipmapMode, true ) );
    }
    
    /**
     * Loads the texture with the given name, with mipmap
     * 
     * @param in An InputStream for the texture resource.
     * @param flipVertically
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in, FlipMode flipVertically )
    {
        return ( loadTexture( in, flipVertically, (TextureFormat)null, (Texture.MipmapMode)null ) );
    }
    
    /**
     * Loads the texture with the given name, with mipmap
     * 
     * @param in An InputStream for the texture resource.
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in )
    {
        return ( loadTexture( in, (FlipMode)null ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param in An InputStream for the texture resource.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( in, (FlipMode)null, mipmapMode ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param in An InputStream for the texture resource.
     * @param format Texture.RGB or Texture.RGBA
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in, TextureFormat format )
    {
        return ( loadTexture( in, (FlipMode)null, format, (Texture.MipmapMode)null ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param in An InputStream for the texture resource.
     * @param flipVertically
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in, FlipMode flipVertically, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( in, flipVertically, (TextureFormat)null, mipmapMode ) );
    }
    
    /**
     * Loads the texture with the given name.
     * 
     * @param in An InputStream for the texture resource.
     * @param format The desired texture format. The returned texture format may
     *            differ from this format.
     * @param mipmapMode Should the texture contain mipmaps ?
     * 
     * @return The Texture object (or a dummy texture if it was not found).
     */
    public Texture2D loadTexture( InputStream in, TextureFormat format, Texture.MipmapMode mipmapMode )
    {
        return ( loadTexture( in, (FlipMode)null, format, mipmapMode ) );
    }
    
    
    
    public Texture3D loadTexture3D( String[] names, FlipMode flipVertically, TextureFormat format )
    {
        if ( ( names == null ) || ( names.length == 0 ) )
        {
            throw new IllegalArgumentException( "names must not be null or of length 0." );
        }
        
        final int depth = names.length;
        
        final boolean flip = ( flipVertically == null ) || flipVertically.getBooleanValue();
        final boolean acceptAlpha = ( format == null ) || format.hasAlpha();
        
        Xith3DTextureFactory3D texFactory = new Xith3DTextureFactory3D( depth );
        
        TextureImage3D texImg = null;
        for ( int i = 0; i < depth; i++ )
        {
            AbstractTextureImage absTexImg = loadTextureImage( names[ i ], flip, acceptAlpha, true, texFactory );
            
            if ( absTexImg == null )
            {
                texFactory.skipOneImage();
            }
            else
            {
                texImg = (TextureImage3D)absTexImg;
            }
        }
        
        if ( texImg == null )
        {
            return ( null );
        }
        
        //Texture.Format actualFormat = Texture.Format.getFormat( tis2D[0].getFormat() );
        TextureFormat actualFormat = format;
        
        Texture3D tex = new Texture3D( actualFormat );
        tex.setImage( 0, texImg );
        
        return ( tex );
    }
    
    public final Texture3D loadTexture3D( String[] names, TextureFormat format )
    {
        return ( loadTexture3D( names, (FlipMode)null, format ) );
    }
    
    public final Texture3D loadTexture3D( String[] names )
    {
        return ( loadTexture3D( names, (FlipMode)null, (TextureFormat)null ) );
    }
    
    
    
    /**
     * Creates a new instance of TextureLoader.
     */
    private TextureLoader()
    {
        super();
    }
}
