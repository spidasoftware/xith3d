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

import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * This is a simple, single textured rectangle implementation. Useful for
 * loading screens and Foreground objects.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Rectangle extends Quad
{
    public static enum ZeroPointLocation
    {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        CENTER_LEFT,
        CENTER_CENTER,
        CENTER_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT;
    }
    
    private final Point3f[] coords;
    private final Vector3f offset;
    private ZeroPointLocation zpl;
    private final Dim2f size;
    
    /**
     * Sets the new texture-coordinates for this Rectangle.
     * 
     * @param texLowerLeft the Lower-left texture coordinate
     * @param texUpperRight the Upper-right texture coordinate
     */
    public void setTexturePosition( Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        TexCoord2f textureCoordinates[]  = { new TexCoord2f( 0f, 0f ),
                                             new TexCoord2f( 1f, 0f ),
                                             new TexCoord2f( 1f, 1f ),
                                             new TexCoord2f( 0f, 1f )
                                           };
        if ( texLowerLeft != null )
        {
            textureCoordinates[ 0 ].setS( texLowerLeft.getX() );
            textureCoordinates[ 0 ].setT( texLowerLeft.getY() );
            textureCoordinates[ 1 ].setT( texLowerLeft.getY() );
            textureCoordinates[ 3 ].setS( texLowerLeft.getX() );
        }
        
        if ( texUpperRight != null )
        {
            textureCoordinates[ 1 ].setS( texUpperRight.getX() );
            textureCoordinates[ 3 ].setT( texUpperRight.getY() );
            textureCoordinates[ 2 ].setS( texUpperRight.getX() );
            textureCoordinates[ 2 ].setT( texUpperRight.getY() );
        }
        
        setTextureCoordinates( textureCoordinates );
    }
    
    /**
     * Sets the new texture-coordinates for this Rectangle.
     * 
     * @param texUpperRight the Upper-right texture coordinate
     */
    public void setTexturePosition( Tuple2f texUpperRight )
    {
        setTexturePosition( null, texUpperRight );
    }
    
    /**
     * Sets the new Texture together with texture-coordinates for this Rectangle.
     * 
     * @param texture the new Texture
     * @param texLowerLeft the Lower-left texture coordinate
     * @param texUpperRight the Upper-right texture coordinate
     */
    public void setTexture( Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        if ( texture != null )
        {
            setTexture( texture );
            
            setTexturePosition( texLowerLeft, texUpperRight );
        }
        else
        {
            Appearance app = getAppearance();
            if ( app != null )
            {
                app.setTexture( (Texture)null );
            }
        }
    }
    
    /**
     * Sets the new Texture together with texture-coordinates for this Rectangle.
     * 
     * @param texture the new Texture resource
     * @param texLowerLeft the Lower-left texture coordinate
     * @param texUpperRight the Upper-right texture coordinate
     */
    public void setTexture( String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        setTexture( TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ), texLowerLeft, texUpperRight );
    }
    
    /**
     * Sets the new Texture for this Rectangle.
     * 
     * @param texture the new Texture resource
     */
    @Override
    public void setTexture( String texture )
    {
        setTexture( TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * @return this Rectangle's Texture or null
     */
    @Override
    public Texture getTexture()
    {
        if ( getAppearance() == null )
            return ( null );
        
        return ( getAppearance().getTexture() );
    }
    
    
    protected static Tuple3f createPosition( float width, float height, ZeroPointLocation zpl, float zOffset, Vector3f result )
    {
        float posX = 0f;
        
        switch ( zpl )
        {
            case TOP_LEFT:
            case CENTER_LEFT:
            case BOTTOM_LEFT:
                posX = +width / 2f;
                break;
            
            case TOP_CENTER:
            case CENTER_CENTER:
            case BOTTOM_CENTER:
                posX = 0.0f;
                break;
            
            case TOP_RIGHT:
            case CENTER_RIGHT:
            case BOTTOM_RIGHT:
                posX = -width / 2f;
                break;
        }
        
        float posY = 0f;
        
        switch ( zpl )
        {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                posY = -height / 2f;
                break;
            
            case CENTER_LEFT:
            case CENTER_CENTER:
            case CENTER_RIGHT:
                posY = 0.0f;
                break;
            
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                posY = +height / 2f;
                break;
        }
        
        result.set( posX, posY, zOffset );
        
        return ( result );
    }
    
    protected static Tuple3f createPosition( float width, float height, ZeroPointLocation zpl, Tuple3f offset )
    {
        float posX = 0f;
        
        switch ( zpl )
        {
            case TOP_LEFT:
            case CENTER_LEFT:
            case BOTTOM_LEFT:
                posX = +width / 2f;
                break;
            
            case TOP_CENTER:
            case CENTER_CENTER:
            case BOTTOM_CENTER:
                posX = 0.0f;
                break;
            
            case TOP_RIGHT:
            case CENTER_RIGHT:
            case BOTTOM_RIGHT:
                posX = -width / 2f;
                break;
        }
        
        float posY = 0f;
        
        switch ( zpl )
        {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                posY = -height / 2f;
                break;
            
            case CENTER_LEFT:
            case CENTER_CENTER:
            case CENTER_RIGHT:
                posY = 0.0f;
                break;
            
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                posY = +height / 2f;
                break;
        }
        
        return ( new Point3f( offset.getX() + posX, offset.getY() + posY, offset.getZ() ) );
    }
    
    /**
     * @return this Rectangle's size (<b>never manipulate it</b>)
     */
    public Dim2f getSize()
    {
        return ( size );
    }
    
    /**
     * @return this Rectangle's width
     */
    public final float getWidth()
    {
        return ( size.getWidth() );
    }
    
    /**
     * @return this Rectangle's height
     */
    public final float getHeight()
    {
        return ( size.getHeight() );
    }
    
    /**
     * Resizes and repositions the Rectangle.
     * 
     * @param width the new width
     * @param height the new height
     * @param xOffset relative x-Location of the Rectangle
     * @param yOffset relative y-Location of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     */
    public boolean resize( float width, float height, float xOffset, float yOffset, float zOffset )
    {
        if ( ( width == size.getWidth() ) && ( height == size.getHeight() ) && ( xOffset == offset.getX() ) && ( yOffset == offset.getY() ) && ( zOffset == offset.getZ() ) )
            return ( false );
        
        offset.set( xOffset, yOffset, zOffset );
        
        coords[ 0 ].set( xOffset - (width / 2f), yOffset - (height / 2f), zOffset );
        coords[ 1 ].set( xOffset + (width / 2f), yOffset - (height / 2f), zOffset );
        coords[ 2 ].set( xOffset + (width / 2f), yOffset + (height / 2f), zOffset );
        coords[ 3 ].set( xOffset - (width / 2f), yOffset + (height / 2f), zOffset );
        
        //getGeometry().setCoordinates( 0, coords );
        setVertexCoords( coords );
        
        this.size.set( width, height );
        
        return ( true );
    }
    
    /**
     * Resizes and repositions the Rectangle.
     * 
     * @param width the new width
     * @param height the new height
     * @param offset the new relative position
     */
    public final boolean resize( float width, float height, Tuple3f offset )
    {
        return ( resize( width, height, offset.getX(), offset.getY(), offset.getZ() ) );
    }
    
    /**
     * Resizes and repositions the Rectangle.
     * 
     * @param width the new width
     * @param height the new height
     */
    public final boolean resize( float width, float height )
    {
        if ( zpl != null )
            createPosition( width, height, zpl, offset.getZ(), offset );
        
        return ( resize( width, height, offset ) );
    }
    
    /**
     * Resizes and repositions the Rectangle.
     * 
     * @param size the new size for the Rectangle
     * @param xOffset relative x-Location of the Rectangle
     * @param yOffset relative y-Location of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     */
    public final boolean resize( Tuple2f size, float xOffset, float yOffset, float zOffset )
    {
        return ( resize( size.getX(), size.getY(), xOffset, yOffset, zOffset ) );
    }
    
    /**
     * Resizes and repositions the Rectangle.
     * 
     * @param size the new size for the Rectangle
     * @param offset the new relative position
     */
    public final boolean resize( Tuple2f size, Tuple3f offset )
    {
        return ( resize( size.getX(), size.getY(), offset.getX(), offset.getY(), offset.getZ() ) );
    }
    
    /**
     * Resizes and repositions the Rectangle.
     * 
     * @param size the new size for the Rectangle
     */
    public final boolean resize( Tuple2f size )
    {
        return ( resize( size.getX(), size.getY(), offset ) );
    }
    
    public static Point3f[] createVertexCoords( float width, float height, float offX, float offY, float offZ )
    {
        Point3f[] coords = new Point3f[] { new Point3f( offX - (width / 2f), offY - (height / 2f), offZ ),
                                           new Point3f( offX + (width / 2f), offY - (height / 2f), offZ ),
                                           new Point3f( offX + (width / 2f), offY + (height / 2f), offZ ),
                                           new Point3f( offX - (width / 2f), offY + (height / 2f), offZ )
                                         };
        
        return ( coords );
    }
    
    public static Point3f[] createVertexCoords( float width, float height, Tuple3f offset )
    {
        if ( offset == null )
            return ( createVertexCoords( width, height, 0.0f, 0.0f, 0.0f ) );
        
        return ( createVertexCoords( width, height, offset.getX(), offset.getY(), offset.getZ() ) );
    }
    
    public static TexCoord2f[] createTextureCoordinates( Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        TexCoord2f[] textureCoordinates  = { new TexCoord2f( 0f, 0f ),
                                             new TexCoord2f( 1f, 0f ),
                                             new TexCoord2f( 1f, 1f ),
                                             new TexCoord2f( 0f, 1f )
                                           };
        if ( texLowerLeft != null )
        {
            textureCoordinates[ 0 ].setS( texLowerLeft.getX() );
            textureCoordinates[ 0 ].setT( texLowerLeft.getY() );
            textureCoordinates[ 1 ].setT( texLowerLeft.getY() );
            textureCoordinates[ 3 ].setS( texLowerLeft.getX() );
        }

        if ( texUpperRight != null )
        {
            textureCoordinates[ 1 ].setS( texUpperRight.getX() );
            textureCoordinates[ 3 ].setT( texUpperRight.getY() );
            textureCoordinates[ 2 ].setS( texUpperRight.getX() );
            textureCoordinates[ 2 ].setT( texUpperRight.getY() );
        }
        
        return ( textureCoordinates );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param generateNormals
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     * @param color the color for the ColoringAttributes
     */
    private Rectangle( float width, float height, Tuple3f offset, boolean generateNormals, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        super( createVertexCoords( width, height, offset ), generateNormals, ( color == null ) ? createTextureCoordinates( texLowerLeft, texUpperRight ) : null, texture, color );
        
        this.zpl = null;
        
        this.offset = new Vector3f( 0.0f, 0.0f, 0.0f );
        if ( offset != null )
            this.offset.set( offset );
        
        this.coords = new Point3f[ getGeometry().getVertexCount() ];
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ] = new Point3f();
            getGeometry().getCoordinate( i, coords[ i ] );
        }
        
        this.size = new Dim2f( width, height );
    }
    
    private Rectangle( float width, float height, Tuple3f offset, boolean generateNormals, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, offset, generateNormals, TextureLoader.getInstance().getTexture( texture ), texLowerLeft, texUpperRight, color );
    }
    
    private Rectangle( float width, float height, ZeroPointLocation zpl, Tuple3f offset, boolean generateNormals, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, createPosition( width, height, zpl, offset ), generateNormals, texture, texLowerLeft, texUpperRight, color );
        
        this.zpl = zpl;
    }
    
    private Rectangle( float width, float height, ZeroPointLocation zpl, Tuple3f offset, boolean generateNormals, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, createPosition( width, height, zpl, offset ), generateNormals, TextureLoader.getInstance().getTexture( texture ), texLowerLeft, texUpperRight, color );
        
        this.zpl = zpl;
    }
    
    private Rectangle( float width, float height, ZeroPointLocation zpl, float zOffset, boolean generateNormals, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ), generateNormals, texture, texLowerLeft, texUpperRight, color );
        
        this.zpl = zpl;
    }
    
    private Rectangle( float width, float height, ZeroPointLocation zpl, float zOffset, boolean generateNormals, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ), generateNormals, TextureLoader.getInstance().getTexture( texture ), texLowerLeft, texUpperRight, color );
        
        this.zpl = zpl;
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     * @param color the color for the ColoringAttributes
     */
    public Rectangle( float width, float height, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, offset, true, texture, texLowerLeft, texUpperRight, color );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     * @param color the color for the ColoringAttributes
     */
    public Rectangle( float width, float height, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, offset, true, texture, texLowerLeft, texUpperRight, color );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, Texture texture )
    {
        this( width, height, (Tuple3f)null, texture, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, String texture )
    {
        this( width, height, (Tuple3f)null, texture, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, Tuple3f offset, String texture )
    {
        this( width, height, offset, texture, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, float zOffset, String texture )
    {
        this( width, height, new Tuple3f( 0f, 0f, zOffset ), true, texture, null, null, null );
    }
    
    /**
     * Creates an untextured Rectangle without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     */
    public Rectangle( float width, float height, float zOffset )
    {
        this( width, height, new Tuple3f( 0f, 0f, zOffset ), (Texture)null, null, null, null );
    }
    
    /**
     * Creates an untextured Rectangle without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset
     */
    public Rectangle( float width, float height, Tuple3f offset )
    {
        this( width, height, offset, (Texture)null, null, null, null );
    }
    
    /**
     * Creates an untextured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     */
    public Rectangle( float width, float height )
    {
        this( width, height, null, (Texture)null, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, offset, texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * 
     * @deprecated please use {@link Rectangle#Rectangle(float, float, Texture)}
     */
    @Deprecated
    public Rectangle( Texture texture, float width, float height )
    {
        this( width, height, (Tuple3f)null, texture, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, Tuple3f offset, Texture texture )
    {
        this( width, height, offset, texture, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, float zOffset, Texture texture )
    {
        this( width, height, new Tuple3f( 0f, 0f, zOffset ), texture, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, float zOffset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, new Tuple3f( 0f, 0f, zOffset ), texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, (Tuple3f)null, texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, offset, texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public Rectangle( float width, float height, Tuple3f offset, Colorf color )
    {
        this( width, height, offset, (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public Rectangle( float width, float height, float zOffset, Colorf color )
    {
        this( width, height, new Tuple3f( 0f, 0f, zOffset ), (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public Rectangle( float width, float height, Colorf color )
    {
        this( width, height, null, (Texture)null, null, null, color );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    protected Rectangle( float width, float height, boolean generateNormals, ZeroPointLocation zpl, Texture texture )
    {
        this( width, height, zpl, 0.0f, generateNormals, texture, null, null, (Colorf)null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Texture texture )
    {
        this( width, height, zpl, 0.0f, true, texture, null, null, (Colorf)null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture )
    {
        this( width, height, zpl, zOffset, true, texture, null, null, (Colorf)null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, zpl, 0.0f, true, texture, texLowerLeft, texUpperRight, (Colorf)null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, zpl, zOffset, true, texture, texLowerLeft, texUpperRight, (Colorf)null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, String texture )
    {
        this( width, height, zpl, 0.0f, true, texture, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, float zOffset, String texture )
    {
        this( width, height, zpl, zOffset, true, texture, null, null, null );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, zpl, 0.0f, true, texture, texLowerLeft, texUpperRight, (Colorf)null );
    }
    
    /**
     * Creates an untextured Rectangle without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, float zOffset )
    {
        this( width, height, zpl, zOffset, true, (Texture)null, null, null, (Colorf)null );
    }
    
    /**
     * Creates an untextured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl )
    {
        this( width, height, zpl, 0.0f, true, (Texture)null, null, null, (Colorf)null );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, float zOffset, Colorf color )
    {
        this( width, height, zpl, zOffset, true, (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param color the color to use for colorin this Rectangle
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Colorf color )
    {
        this( width, height, zpl, 0.0f, true, (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param color the color to use for coloring this Rectangle
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Tuple3f offset, Colorf color )
    {
        this( width, height, zpl, offset, true, (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Tuple3f offset, String texture )
    {
        this( width, height, zpl, offset, true, texture, null, null, null );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, zpl, offset, true, texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Tuple3f offset, Texture texture )
    {
        this( width, height, zpl, offset, true, texture, null, null, null );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public Rectangle( float width, float height, ZeroPointLocation zpl, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, zpl, offset, true, texture, texLowerLeft, texUpperRight, null );
    }
    
    
    
    
    
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     * @param color the color for the ColoringAttributes
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        return ( new Rectangle( width, height, offset, false, texture, texLowerLeft, texUpperRight, color ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     * @param color the color for the ColoringAttributes
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        return ( new Rectangle( width, height, offset, false, texture, texLowerLeft, texUpperRight, color ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, Texture texture )
    {
        return ( new Rectangle( width, height, (Tuple3f)null, false, texture, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, String texture )
    {
        return ( new Rectangle( width, height, (Tuple3f)null, false, texture, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset, String texture )
    {
        return ( new Rectangle( width, height, offset, false, texture, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, float zOffset, String texture )
    {
        return ( new Rectangle( width, height, new Tuple3f( 0f, 0f, zOffset ), false, texture, null, null, null ) );
    }
    
    /**
     * Creates an untextured Rectangle without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, float zOffset )
    {
        return ( new Rectangle( width, height, new Tuple3f( 0f, 0f, zOffset ), false, (Texture)null, null, null, null ) );
    }
    
    /**
     * Creates an untextured Rectangle without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset )
    {
        return ( new Rectangle( width, height, offset, false, (Texture)null, null, null, null ) );
    }
    
    /**
     * Creates an untextured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height )
    {
        return ( new Rectangle( width, height, null, false, (Texture)null, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, offset, false, texture, texLowerLeft, texUpperRight, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset, Texture texture )
    {
        return ( new Rectangle( width, height, offset, false, texture, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, float zOffset, Texture texture )
    {
        return ( new Rectangle( width, height, new Tuple3f( 0f, 0f, zOffset ), false, texture, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, float zOffset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, new Tuple3f( 0f, 0f, zOffset ), false, texture, texLowerLeft, texUpperRight, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, (Tuple3f)null, false, texture, texLowerLeft, texUpperRight, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, offset, false, texture, texLowerLeft, texUpperRight, null ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, Tuple3f offset, Colorf color )
    {
        return ( new Rectangle( width, height, offset, false, (Texture)null, null, null, color ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, float zOffset, Colorf color )
    {
        return ( new Rectangle( width, height, new Tuple3f( 0f, 0f, zOffset ), false, (Texture)null, null, null, color ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, Colorf color )
    {
        return ( new Rectangle( width, height, null, false, (Texture)null, null, null, color ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Texture texture )
    {
        return ( new Rectangle( width, height, zpl, 0.0f, false, texture, null, null, (Colorf)null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture )
    {
        return ( new Rectangle( width, height, zpl, zOffset, false, texture, null, null, (Colorf)null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, zpl, 0.0f, false, texture, texLowerLeft, texUpperRight, (Colorf)null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, zpl, zOffset, false, texture, texLowerLeft, texUpperRight, (Colorf)null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, String texture )
    {
        return ( new Rectangle( width, height, zpl, 0.0f, false, texture, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, float zOffset, String texture )
    {
        return ( new Rectangle( width, height, zpl, zOffset, false, texture, null, null, null ) );
    }
    
    /**
     * Creates a textured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, zpl, 0f, false, texture, texLowerLeft, texUpperRight, null ) );
    }
    
    /**
     * Creates an untextured Rectangle without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, float zOffset )
    {
        return ( new Rectangle( width, height, zpl, 0f, false, (Texture)null, null, null, null ) );
    }
    
    /**
     * Creates an untextured Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl )
    {
        return ( new Rectangle( width, height, zpl, 0f, false, (Texture)null, null, null, null ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, float zOffset, Colorf color )
    {
        return ( new Rectangle( width, height, zpl, zOffset, false, (Texture)null, null, null, color ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param color the color to use for colorin this Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Colorf color )
    {
        return ( new Rectangle( width, height, zpl, 0f, false, (Texture)null, null, null, color ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param color the color to use for coloring this Rectangle
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Tuple3f offset, Colorf color )
    {
        return ( new Rectangle( width, height, zpl, offset, false, (Texture)null, null, null, color ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Tuple3f offset, String texture )
    {
        return ( new Rectangle( width, height, zpl, offset, false, texture, null, null, null ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, offset, false, texture, texLowerLeft, texUpperRight, null ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Tuple3f offset, Texture texture )
    {
        return ( new Rectangle( width, height, zpl, offset, false, texture, null, null, null ) );
    }
    
    /**
     * Creates an untextured, but colored Rectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public static final Rectangle createWithoutNormals( float width, float height, ZeroPointLocation zpl, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        return ( new Rectangle( width, height, zpl, offset, false, texture, texLowerLeft, texUpperRight, null ) );
    }
}
