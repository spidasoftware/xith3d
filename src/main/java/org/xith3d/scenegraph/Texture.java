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
package org.xith3d.scenegraph;

import org.jagatoo.datatypes.Enableable;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.jagatoo.loaders.textures.AbstractTexture;
import org.jagatoo.loaders.textures.AbstractTextureImage;
import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFilter;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureType;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.SceneGraphOpenGLReferences;

/**
 * A Texture represents an image to be applied to a Shape3D's Appearance.
 * The renderer positiones the Texture on the Shape's Geometry according
 * to its texture coordinates.<br>
 * <br>
 * One Texture instance can be reused for for an arbitrary number of Shapes.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class Texture extends NodeComponent implements AbstractTexture, Enableable
{
    public static enum MipmapMode
    {
        /**
         * Mipmap mode - Indicates that this texture only has a base-level image.
         */
        BASE_LEVEL( false ),
        
        /**
         * Mipmap mode - Indicates that this texture object has multiple images, one
         * for each mipmap level. Images for all levels must be set.
         */
        MULTI_LEVEL_MIPMAP( true );
        
        private boolean boolVal;
        
        public boolean booleanValue()
        {
            return ( boolVal );
        }
        
        private MipmapMode( boolean boolVal )
        {
            this.boolVal = boolVal;
        }
        
        public static MipmapMode valueOf( boolean boolVal )
        {
            return ( boolVal ? MULTI_LEVEL_MIPMAP : BASE_LEVEL );
        }
    }
    
    /*
     * State ID 0 reserved for null texture, State ID -1 reserved for
     * non-initialized state (of any shader)
     */
    private static long TEX_STATE_ID_SEQ = 1;
    
    private final TextureType type;
    
    /**
     * The desired mipmap mode.
     */
    private TextureFormat format = null;
    
    /**
     * The image data. Must be set with the setImage method.
     */
    private TextureImage[] images = null;
    
    /**
     * The desired boundary mode S
     */
    private TextureBoundaryMode boundaryModeS = TextureBoundaryMode.WRAP;
    
    /**
     * The desired boundary mode T
     */
    private TextureBoundaryMode boundaryModeT = TextureBoundaryMode.WRAP;
    
    /**
     * The desired boundary color.
     */
    private final Colorf boundaryColor = new Colorf( 0f, 0f, 0f, 0f );
    
    /**
     * The width of the image boundary.
     */
    private int boundaryWidth = 0;
    
    /**
     * texture mapping is enabled or not for this texture.
     */
    private boolean mappingEnabled = true;
    
    private static TextureFilter defaultFilter = null; //TextureFilter.NICER;
    
    private TextureFilter filter = defaultFilter;
    
    private long texStateId;
    
    private boolean dirty = true;
    
    private String cacheKey = null;
    private String resourceName = null;
    
    private boolean markedAsLocalDataToBeFreed = false;
    
    private final SceneGraphOpenGLReferences openGLReferences = new SceneGraphOpenGLReferences( 1 );
    
    public final TextureType getType()
    {
        return ( type );
    }
    
    /**
     * @return this Texture's format
     */
    public final TextureFormat getFormat()
    {
        return ( format );
    }
    
    /**
     * Enables disable texture mapping for this texture.
     */
    public void setEnabled( boolean enabled )
    {
        mappingEnabled = enabled;
        
        setChanged( true );
    }
    
    /**
     * Is texture mapping enabled for this texture.
     */
    public final boolean isEnabled()
    {
        return ( mappingEnabled );
    }
    
    final void setDirty( boolean dirty )
    {
        this.dirty = dirty;
    }
    
    public final boolean isDirty()
    {
        return ( dirty );
    }
    
    /**
     * This method is called by {@link #setImage(int, TextureImage)}.
     * It checks the added image's type and throws an exception,
     * if the type is not accepted.
     * 
     * @param image
     */
    protected abstract void checkImageType( TextureImage image );
    
    /**
     * Sets the image data for a specified mipmap level.
     */
    public void setImage( int level, TextureImage image )
    {
        if ( image != null )
        {
            checkImageType( image );
        }
        
        if ( images == null )
        {
            if ( image == null )
                return;
            
            images = new TextureImage[ level + 1 ];
            images[ level ] = image;
        }
        else
        {
            if ( images.length - 1 >= level )
            {
                if ( image == null )
                {
                    if ( images.length == 1 )
                    {
                        images = null;
                    }
                    else
                    {
                        TextureImage[] temp = new TextureImage[ images.length - 1 ];
                        System.arraycopy( images, 0, temp, 0, level );
                        System.arraycopy( images, level + 1, temp, level, images.length - level - 1 );
                        images = temp;
                    }
                }
                else
                {
                    images[ level ] = image;
                }
            }
            else
            {
                if ( image == null )
                    return;
                
                TextureImage[] temp = new TextureImage[ level + 1 ];
                for ( int loop = 0; loop < images.length; loop++ )
                    temp[ loop ] = images[ loop ];
                temp[ level ] = image;
                images = temp;
            }
        }
        
        dirty = true;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setImage( int level, AbstractTextureImage image )
    {
        setImage( level, (TextureImage)image );
    }
    
    public final void addImage( AbstractTextureImage image )
    {
        setImage( getImagesCount(), image );
    }
    
    /**
     * Gets the image for the specified mipmap level.
     */
    public final TextureImage getImage( int level )
    {
        return ( images[ level ] );
    }
    
    /**
     * Get the number of elements in the images array. This doesn't mean that
     * each one of the elements is non-null.
     */
    public final int getImagesCount()
    {
        if ( images == null )
            return ( 0 );
        
        return ( images.length  );
    }
    
    /**
     * Gets the mipmap mode for texture mapping for this texture.
     */
    public final MipmapMode getMipMapMode()
    {
        if ( ( images == null ) || ( images.length <= 1 ) )
            return ( MipmapMode.BASE_LEVEL );
        
        return ( MipmapMode.MULTI_LEVEL_MIPMAP );
    }
    
    private boolean sizeChanged = false;
    
    public void setSizeChanged()
    {
        sizeChanged = true;
    }
    
    public final boolean hasSizeChanged()
    {
        return ( sizeChanged );
    }
    
    final void resetSizeChanged()
    {
        sizeChanged = false;
    }
    
    public final Sized2iRO getSize()
    {
        if ( getImagesCount() == 0 )
            return ( null );
        
        return ( getImage( 0 ).getSize() );
    }
    
    public final int getWidth()
    {
        if ( getImagesCount() == 0 )
            return ( -1 );
        
        return ( getImage( 0 ).getWidth() );
    }
    
    public final int getHeight()
    {
        if ( getImagesCount() == 0 )
            return ( -1 );
        
        return ( getImage( 0 ).getHeight() );
    }
    
    public final Sized2iRO getOriginalSize()
    {
        if ( getImagesCount() == 0 )
            return ( null );
        
        return ( getImage( 0 ).getOriginalSize() );
    }
    
    public final int getOriginalWidth()
    {
        if ( getImagesCount() == 0 )
            return ( -1 );
        
        return ( getImage( 0 ).getOriginalWidth() );
    }
    
    public final int getOriginalHeight()
    {
        if ( getImagesCount() == 0 )
            return ( -1 );
        
        return ( getImage( 0 ).getOriginalHeight() );
    }
    
    /**
     * Sets the boundary mode S.
     * 
     * @param mode
     */
    public final void setBoundaryModeS( TextureBoundaryMode mode )
    {
        boundaryModeS = mode;
        setChanged( true );
    }
    
    /**
     * Gets the boundary mode S.
     */
    public final TextureBoundaryMode getBoundaryModeS()
    {
        return ( boundaryModeS );
    }
    
    /**
     * Sets the boundary mode T.
     * 
     * @param mode
     */
    public final void setBoundaryModeT( TextureBoundaryMode mode )
    {
        boundaryModeT = mode;
        setChanged( true );
    }
    
    /**
     * Gets the boundary mode T.
     */
    public final TextureBoundaryMode getBoundaryModeT()
    {
        return ( boundaryModeT );
    }
    
    /**
     * Sets the boundary modes S and T.
     * 
     * @param modeS
     * @param modeT
     */
    public final void setBoundaryModes( TextureBoundaryMode modeS, TextureBoundaryMode modeT )
    {
        setBoundaryModeS( modeS );
        setBoundaryModeT( modeT );
    }
    
    /**
     * Sets the boundary color.
     */
    public void setBoundaryColor( Colorf color )
    {
        boundaryColor.set( color );
        setChanged( true );
    }
    
    public final void setBoundaryColor( float r, float g, float b, float a )
    {
        boundaryColor.set( r, g, b, a );
        setChanged( true );
    }
    
    /**
     * Gets the boundary color.
     */
    public final Colorf getBoundaryColor()
    {
        return ( boundaryColor.getReadOnly() );
    }
    
    public final Colorf getBoundaryColor( Colorf c )
    {
        c.set( boundaryColor );
        
        return ( c );
    }
    
    public void setBoundaryWidth( int boundaryWidth )
    {
        this.boundaryWidth = boundaryWidth;
        setChanged( true );
    }
    
    public final int getBoundaryWidth()
    {
        return ( boundaryWidth );
    }
    
    /**
     * Sets the default filter for textures.
     * 
     * @param defaultFilter
     */
    public static void setDefaultFilter( TextureFilter defaultFilter )
    {
        Texture.defaultFilter = defaultFilter;
    }
    
    /**
     * @return the default filter for textures.
     */
    public static TextureFilter getDefaultFilter()
    {
        return ( defaultFilter );
    }
    
    /**
     * Sets the texture filter.
     */
    public final void setFilter( TextureFilter filter )
    {
        this.filter = filter;
        setChanged( true );
    }
    
    /**
     * @return the texture filter.
     */
    public final TextureFilter getFilter()
    {
        return ( filter );
    }
    
    /**
     * @return the state ID used to sort the item
     */
    public final long getStateId()
    {
        return ( texStateId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        Texture orgTex = (Texture)original;
        
        this.format = orgTex.format;
        
        this.mappingEnabled = orgTex.mappingEnabled;
        
        if ( orgTex.images == null )
        {
            this.images = null;
        }
        else
        {
            this.images = new TextureImage[ orgTex.images.length ];
            
            for ( int i = 0; i < orgTex.images.length; i++ )
            {
                this.images[ i ] = orgTex.images[ i ];
            }
        }
        
        this.boundaryModeS = orgTex.boundaryModeS;
        this.boundaryModeT = orgTex.boundaryModeT;
        
        this.boundaryColor.set( orgTex.boundaryColor );
        this.boundaryWidth = orgTex.boundaryWidth;
        
        this.filter = orgTex.filter;
    }
    
    /**
     * This marks this Texture to free its local (RAM) texture-data
     * after it has been sent to OpenGL.<br>
     * In most cases this will be usefull, since we don't need the
     * texture-data locally, since it will reside in the VRAM.
     */
    public void enableAutoFreeLocalData()
    {
        this.markedAsLocalDataToBeFreed = true;
    }
    
    public final boolean isMarkedAsLocalDataToBeFreed()
    {
        return ( markedAsLocalDataToBeFreed );
    }
    
    /*
    public final boolean isLocalDataFreed()
    {
        return ( localDataFreed );
    }
    */
    
    public final SceneGraphOpenGLReferences getOpenGLReferences()
    {
        return ( openGLReferences );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize()
    {
        openGLReferences.prepareObjectForDestroy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( openGLReferences.referenceExists( canvasPeer ) )
            openGLReferences.prepareObjectForDestroy( canvasPeer );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCacheKey( String cacheKey )
    {
        this.cacheKey = cacheKey;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getCacheKey()
    {
        return ( cacheKey );
    }
    
    /**
     * Removes the Texture from the Cache, so that it can be deleted in OpenGL,
     * if it is not used anymore anywhere else.
     */
    public final void removeFromCache()
    {
        TextureLoader.getInstance().getCache().remove( this );
    }
    
    public void setResourceName( String resName )
    {
        this.resourceName = resName;
    }
    
    /**
     * @return the name, this Texture has been loaded by.
     */
    public final String getResourceName()
    {
        return ( resourceName );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( this.getClass().getSimpleName() +
                "{ " +
                "name = " + ( getName() != null ? "\"" : "" ) + getName() + ( getName() != null ? "\"" : "" ) +
                //", type = " + type +
                ", size = " + getWidth() + "x" + getHeight() +
                ( !getSize().equals( getOriginalSize() ) ? ( ", orgSize = " + getOriginalWidth() + "x" + getOriginalHeight() ) : "" ) +
                ", format = " + format +
                ", mipmapMode = " + getMipMapMode() + " (" + getImagesCount() + " image" + ( getImagesCount() != 1 ? "s" : "" ) + ")" +
                ", boundaryMode = " + boundaryModeS + "/" + boundaryModeT +
                ", boundaryWidth = " + boundaryWidth +
                ", boundaryColor = ( " + boundaryColor.getRed() + ", " + boundaryColor.getGreen() + ", " + boundaryColor.getBlue() + ( boundaryColor.hasAlpha() ? ( ", " + boundaryColor.getAlpha() ) : "" ) + " )" +
                ", boundaryWidth = " + boundaryWidth +
                ", filter = " + filter +
                ", enabled = " + mappingEnabled +
                ", resourceName = " + ( resourceName != null ? "\"" : "" ) + resourceName + ( resourceName != null ? "\"" : "" ) +
                ", cacheKey = " + ( cacheKey != null ? "\"" : "" ) + cacheKey + ( cacheKey != null ? "\"" : "" ) +
                " }"
              );
    }
    
    /**
     * Constructs a new Texture object.
     * 
     * @param type
     * @param format
     * @param boundaryWidth
     */
    public Texture( TextureType type, TextureFormat format, int boundaryWidth )
    {
        super( true );
        
        texStateId = TEX_STATE_ID_SEQ++;
        
        this.type = type;
        this.format = format;
        this.boundaryWidth = boundaryWidth;
    }
    
    /**
     * Constructs a new Texture object.
     * 
     * @param type
     * @param format
     */
    public Texture( TextureType type, TextureFormat format )
    {
        this( type, format, 0 );
    }
}
