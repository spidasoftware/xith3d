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
package org.xith3d.utility.texturing;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loaders.texture.TextureLoader.FlipMode;
import org.xith3d.scenegraph.Texture;

/**
 * This is a simple utility container for the six textures of a Cube.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CubeTextureSet
{
    public static final int FRONT_INDEX  = 0;
    public static final int RIGHT_INDEX  = 1;
    public static final int BACK_INDEX   = 2;
    public static final int LEFT_INDEX   = 3;
    public static final int TOP_INDEX    = 4;
    public static final int BOTTOM_INDEX = 5;
    
    private String[] textureNames;
    private Texture[] textures;
    private FlipMode flipMode;
    
    public void setFlipMode( FlipMode flipMode )
    {
        this.flipMode = flipMode;
    }
    
    public FlipMode getFlipMode()
    {
        return ( flipMode );
    }
    
    public String[] getTextureNames()
    {
        return ( textureNames );
    }
    
    public Texture[] getTextures()
    {
        for ( int i = 0; i < textures.length; i++ )
        {
            textures[ i ] = TextureLoader.getInstance().getTexture( textureNames[ i ], getFlipMode() );
        }
        
        return ( textures );
    }
    
    public void setFront( Texture texture )
    {
        if ( texture.getResourceName() == null )
            throw new NullPointerException( "The given Texture does not have resource name." );
        
        this.textureNames[ FRONT_INDEX ] = texture.getResourceName();
    }
    
    public void setFront( String textureName )
    {
        if ( textureName == null )
            throw new NullPointerException( "The given Texture-Name must not be null." );
        
        this.textureNames[ FRONT_INDEX ] = textureName;
    }
    
    public String getFront()
    {
        return ( this.textureNames[ FRONT_INDEX ] );
    }
    
    public Texture getFrontTexture()
    {
        return ( TextureLoader.getInstance().getTexture( getFront(), getFlipMode() ) );
    }
    
    public void setRight( Texture texture )
    {
        if ( texture.getResourceName() == null )
            throw new NullPointerException( "The given Texture does not have resource name." );
        
        this.textureNames[ RIGHT_INDEX ] = texture.getResourceName();
    }
    
    public void setRight( String textureName )
    {
        if ( textureName == null )
            throw new NullPointerException( "The given Texture-Name must not be null." );
        
        this.textureNames[ RIGHT_INDEX ] = textureName;
    }
    
    public String getRight()
    {
        return ( this.textureNames[ RIGHT_INDEX ] );
    }
    
    public Texture getRightTexture()
    {
        return ( TextureLoader.getInstance().getTexture( getRight(), getFlipMode() ) );
    }
    
    public void setBack( Texture texture )
    {
        if ( texture.getResourceName() == null )
            throw new NullPointerException( "The given Texture does not have resource name." );
        
        this.textureNames[ BACK_INDEX ] = texture.getResourceName();
    }
    
    public void setBack( String textureName )
    {
        if ( textureName == null )
            throw new NullPointerException( "The given Texture-Name must not be null." );
        
        this.textureNames[ BACK_INDEX ] = textureName;
    }
    
    public String getBack()
    {
        return ( this.textureNames[ BACK_INDEX ] );
    }
    
    public Texture getBackTexture()
    {
        return ( TextureLoader.getInstance().getTexture( getBack(), getFlipMode() ) );
    }
    
    public void setLeft( Texture texture )
    {
        if ( texture.getResourceName() == null )
            throw new NullPointerException( "The given Texture does not have resource name." );
        
        this.textureNames[ LEFT_INDEX ] = texture.getResourceName();
    }
    
    public void setLeft( String textureName )
    {
        if ( textureName == null )
            throw new NullPointerException( "The given Texture-Name must not be null." );
        
        this.textureNames[ LEFT_INDEX ] = textureName;
    }
    
    public String getLeft()
    {
        return ( this.textureNames[ LEFT_INDEX ] );
    }
    
    public Texture getLeftTexture()
    {
        return ( TextureLoader.getInstance().getTexture( getLeft(), getFlipMode() ) );
    }
    
    public void setTop( Texture texture )
    {
        if ( texture.getResourceName() == null )
            throw new NullPointerException( "The given Texture does not have resource name." );
        
        this.textureNames[ TOP_INDEX ] = texture.getResourceName();
    }
    
    public void setTop( String textureName )
    {
        if ( textureName == null )
            throw new NullPointerException( "The given Texture-Name must not be null." );
        
        this.textureNames[ TOP_INDEX ] = textureName;
    }
    
    public String getTop()
    {
        return ( this.textureNames[ TOP_INDEX ] );
    }
    
    public Texture getTopTexture()
    {
        return ( TextureLoader.getInstance().getTexture( getTop(), getFlipMode() ) );
    }
    
    public void setBottom( Texture texture )
    {
        if ( texture.getResourceName() == null )
            throw new NullPointerException( "The given Texture does not have resource name." );
        
        this.textureNames[ BOTTOM_INDEX ] = texture.getResourceName();
    }
    
    public void setBottom( String textureName )
    {
        if ( textureName == null )
            throw new NullPointerException( "The given Texture-Name must not be null." );
        
        this.textureNames[ BOTTOM_INDEX ] = textureName;
    }
    
    public String getBottom()
    {
        return ( this.textureNames[ BOTTOM_INDEX ] );
    }
    
    public Texture getBottomTexture()
    {
        return ( TextureLoader.getInstance().getTexture( getBottom(), getFlipMode() ) );
    }
    
    public CubeTextureSet( String[] textureNames, FlipMode flipMode )
    {
        if ( textureNames.length != 6 )
            throw new IllegalArgumentException( "the given array must be of length 6." );
        
        this.textureNames = new String[ 6 ];
        
        for ( int i = 0; i < 6; i++ )
        {
            if ( textureNames[ i ] == null )
                throw new NullPointerException( "None of the geven texture names must be null (#" + i + " is)." );
            
            this.textureNames[ i ] = textureNames[ i ];
        }
        
        this.textures = new Texture[ 6 ];
        
        this.flipMode = flipMode;
    }
    
    public CubeTextureSet( String[] textureNames )
    {
        this( textureNames, FlipMode.FLIPPED_VERTICALLY );
    }
    
    public CubeTextureSet( String texFront, String texRight, String texBack, String texLeft, String texTop, String texBottom, FlipMode flipMode )
    {
        this.textureNames = new String[]
        {
            texFront, texRight, texBack, texLeft, texTop, texBottom
        };
        
        for ( int i = 0; i < 6; i++ )
        {
            if ( textureNames[ i ] == null )
                throw new NullPointerException( "None of the geven texture names must be null (#" + i + " is)." );
        }
        
        this.textures = new Texture[ 6 ];
        
        this.flipMode = flipMode;
    }
    
    public CubeTextureSet( String texFront, String texRight, String texBack, String texLeft, String texTop, String texBottom )
    {
        this( texFront, texRight, texBack, texLeft, texTop, texBottom, (FlipMode)null );
    }
    
    public CubeTextureSet( Texture texFront, Texture texRight, Texture texBack, Texture texLeft, Texture texTop, Texture texBottom, FlipMode flipMode )
    {
        this.textureNames = new String[]
        {
            texFront.getResourceName(), texRight.getResourceName(), texBack.getResourceName(), texLeft.getResourceName(), texTop.getResourceName(), texBottom.getResourceName()
        };
        
        for ( int i = 0; i < 6; i++ )
        {
            if ( textureNames[ i ] == null )
                throw new NullPointerException( "All of the given textures must have names (#" + i + " doesn't)." );
        }
        
        this.textures = new Texture[ 6 ];
        
        this.flipMode = flipMode;
    }
    
    public CubeTextureSet( Texture texFront, Texture texRight, Texture texBack, Texture texLeft, Texture texTop, Texture texBottom )
    {
        this( texFront, texRight, texBack, texLeft, texTop, texBottom, (FlipMode)null );
    }
    
    public static CubeTextureSet create( String baseName, String extension, FlipMode flipMode )
    {
        if ( !extension.startsWith( "." ) )
        {
            extension = "." + extension;
        }
        
        return ( new CubeTextureSet( baseName + "front" + extension, baseName + "right" + extension, baseName + "back" + extension, baseName + "left" + extension, baseName + "top" + extension, baseName + "bottom" + extension, flipMode ) );
    }
    
    public static CubeTextureSet create( String baseName, String extension )
    {
        return ( create( baseName, extension, (FlipMode)null ) );
    }
}
