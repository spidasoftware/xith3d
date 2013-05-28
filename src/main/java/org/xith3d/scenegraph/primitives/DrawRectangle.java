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

import org.jagatoo.opengl.enums.FaceCullMode;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.render.RenderPass;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;

/**
 * This a simple {@link Rectangle} extension for pixel-perfect drawing.
 * Use it in a {@link RenderPass} created through
 * RenderPass.create2D( ... ).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DrawRectangle extends Rectangle
{
    private boolean allowBiggerTexture = false;
    
    /**
     * If set to true, the underlying TextureImage2D is not shinked,
     * if a smaller size would be sufficient, when the rectangle is shrinked.
     * 
     * @param allow
     */
    public void setAllowBiggerTexture( boolean allow )
    {
        this.allowBiggerTexture = allow;
    }
    
    /**
     * If set to true, the underlying TextureImage2D is not shinked,
     * if a smaller size would be sufficient, when the rectangle is shrinked.
     */
    public final boolean getAllowBiggerTexture()
    {
        return ( allowBiggerTexture );
    }
    
    /**
     * @return this Rectangle's Texture or null
     */
    @Override
    public Texture2D getTexture()
    {
        return ( (Texture2D)getAppearance().getTexture() );
    }
    
    public final Texture2DCanvas getTextureCanvas()
    {
        return ( getTexture().getTextureCanvas() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resize( float width, float height, float xOffset, float yOffset, float zOffset )
    {
        if ( ( width == getWidth() ) && ( height == getHeight() ) )
            return ( false );
        
        if ( !super.resize( width, height, xOffset, yOffset, zOffset ) )
            return ( false );
        
        int texWidth = Math.max( 1, (int)Math.ceil( width ) );
        int texHeight = Math.max( 1, (int)Math.ceil( height ) );
        
        Texture2D texture = getTexture();
        TextureImage2D ti0 = texture.getImage0();
        if ( ti0.initImageData( texWidth, texHeight, allowBiggerTexture ) )
        {
            texture.setSizeChanged();
            
            //System.out.println( width + ", " + height + ", " + ti0.getWidth() + ", " + ti0.getHeight() );
        }
        else if ( texture.hasTextureCanvas() )
        {
            texture.getTextureCanvas().notifyImagesizeChanged( ti0.getOriginalWidth(), ti0.getOriginalHeight(), null );
            
            //System.out.println( width + ", " + height );
        }
        
        Tuple2f tmpTC = Tuple2f.fromPool();
        ti0.getTextureCoordinateUR( tmpTC );
        setTexturePosition( tmpTC );
        Tuple2f.toPool( tmpTC );
        
        return ( true );
    }
    
    private static Texture2D createDrawTexture( int width, int height, boolean withAlpha, boolean useByteBuffer )
    {
        TextureFormat format = withAlpha ? TextureFormat.RGBA : TextureFormat.RGB;
        
        width = Math.max( 1, width );
        height = Math.max( 1, height );
        
        Texture2D texture = Texture2D.createDrawTexture( format, width, height, useByteBuffer );
        
        return ( texture );
    }
    
    public DrawRectangle( float width, float height, boolean generateNormals, boolean withAlpha, boolean useByteBuffer )
    {
        super( width, height, generateNormals, ZeroPointLocation.TOP_LEFT, createDrawTexture( (int)width, (int)height, withAlpha, useByteBuffer ) );
        
        this.getAppearance().getPolygonAttributes( true ).setFaceCullMode( FaceCullMode.BACK );
        
        Tuple2f tmpTC = Tuple2f.fromPool();
        getTexture().getImage0().getTextureCoordinateUR( tmpTC );
        setTexturePosition( tmpTC );
        Tuple2f.toPool( tmpTC );
    }
    
    public DrawRectangle( float width, float height, boolean withAlpha, boolean useByteBuffer )
    {
        this( width, height, false, withAlpha, useByteBuffer );
    }
    
    public DrawRectangle( float width, float height, boolean useByteBuffer )
    {
        this( width, height, false, true, useByteBuffer );
    }
}
