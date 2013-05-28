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

import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureType;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.utility.texturing.CubeTextureSet;

/**
 * @author Yuri Vl. Gushchin
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureCubeMap extends Texture
{
    public static final int  POSITIVE_X  = 0,
                             NEGATIVE_X  = 1,
                             POSITIVE_Y  = 2,
                             NEGATIVE_Y  = 3,
                             POSITIVE_Z  = 4,
                             NEGATIVE_Z  = 5;
    
    private TextureImage[][] images = new TextureImage[ 6 ][ 1 ];
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkImageType( TextureImage image )
    {
        /*
        if ( !( image instanceof TextureImage2D ) )
        {
            throw new Error( "Only TextureImage2D instances can be added to a Texture2D." );
        }
        */
    }
    
    public void setImage( int level, int face, TextureImage image )
    {
        if ( face == 0 )
            super.setImage( level, image );
        
        if ( images[ face ] == null )
        {
            images[ face ] = new TextureImage[ level + 1 ];
            images[ face ][ level ] = image;
        }
        else
        {
            if ( images[ face ].length - 1 >= level )
                images[ face ][ level ] = image;
            else
            {
                TextureImage[] temp = new TextureImage[ level + 1 ];
                
                for ( int loop = 0; loop < images[ face ].length; loop++ )
                    temp[ loop ] = images[ face ][ loop ];
                
                temp[ level ] = image;
                images[ face ] = temp;
            }
        }
        
        setDirty( true );
    }
    
    public void setImage( int level, int face, String textureName )
    {
        Texture tex = TextureLoader.getInstance().getTexture( textureName, getFormat(), MipmapMode.BASE_LEVEL );
        
        setImage( level, face, tex.getImage( level ) );
    }
    
    public void setImages( int face, TextureImage[] images )
    {
        if ( face == 0 )
        {
            for ( int i = 0; i < images.length; i++ )
            {
                super.setImage( i, images[ i ] );
            }
        }
        
        this.images[ face ] = images;
    }
    
    public void setImages( int face, String[] images )
    {
        for ( int i = 0; i < images.length; i++ )
        {
            setImage( face, i, images[ i ] );
        }
    }
    
    public TextureImage getImage( int mipmap, int face )
    {
        return ( images[ face ][ mipmap ] );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        assert original instanceof TextureCubeMap;
        
        final TextureCubeMap tcmOrig = (TextureCubeMap)original;
        
        for ( int i = 0; i < images.length; i++ )
        {
            for ( int j = 0; j < images[ i ].length; j++ )
            {
                //this.images[ i ][ j ] = ( (TextureCubeMap)original ).images[ i ][ j ];
                setImages( i, tcmOrig.images[ i ] );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TextureCubeMap cloneNodeComponent( boolean forceDuplicate )
    {
        TextureCubeMap tcm = new TextureCubeMap( this.getFormat() );
        
        tcm.duplicateNodeComponent( this, forceDuplicate );
        
        return ( tcm );
    }
    
    private TextureCubeMap( TextureFormat format )
    {
        super( TextureType.TEXTURE_CUBE_MAP, format );
    }
    
    public TextureCubeMap( TextureFormat format, int face, String[] textureNames )
    {
        super( TextureType.TEXTURE_CUBE_MAP, format );
        
        setImages( face, textureNames );
    }
    
    public TextureCubeMap( TextureFormat format, int face, CubeTextureSet textureSet )
    {
        super( TextureType.TEXTURE_CUBE_MAP, format );
        
        setImage( face, 0, textureSet.getRight() );
        setImage( face, 1, textureSet.getLeft() );
        setImage( face, 2, textureSet.getTop() );
        setImage( face, 3, textureSet.getBottom() );
        setImage( face, 4, textureSet.getFront() );
        setImage( face, 5, textureSet.getBack() );
        
        this.setBoundaryModeS( TextureBoundaryMode.CLAMP_TO_EDGE );
        this.setBoundaryModeT( TextureBoundaryMode.CLAMP_TO_EDGE );
    }
}
