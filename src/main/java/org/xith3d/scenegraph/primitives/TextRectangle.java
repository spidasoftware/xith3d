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

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.scenegraph.Texture;

import java.awt.*;

/**
 * The TextRectangle is a Rectangle with a text-Texture on it.<br>
 * The Texture is created by TextureCreator.createTexture(String, Colorf, java.awt.Font, int).
 * 
 * @see TextureCreator#createTexture(String, Colorf, java.awt.Font, int)
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextRectangle extends Rectangle
{
    public static final int TEXT_ALIGNMENT_HORIZONTAL_LEFT = TextureCreator.TEXT_ALIGNMENT_HORIZONTAL_LEFT;
    public static final int TEXT_ALIGNMENT_HORIZONTAL_CENTER = TextureCreator.TEXT_ALIGNMENT_HORIZONTAL_CENTER;
    public static final int TEXT_ALIGNMENT_HORIZONTAL_RIGHT = TextureCreator.TEXT_ALIGNMENT_HORIZONTAL_RIGHT;
    
    private String text = null;
    private Colorf textColor = null;
    private Font textFont = null;
    private int textAlignment = TEXT_ALIGNMENT_HORIZONTAL_LEFT;
    
    /**
     * Sets the new text for this TextRectangle.
     * 
     * @param text new new text
     * @param color the new text-color
     * @param font the new font to use for the text
     * @param alignment the text horizontal alignment
     */
    public void setText( String text, Colorf color, Font font, int alignment )
    {
        Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        /*
        Tuple2f texLowerLeft = null;
        Tuple2f texUpperRight = null;
        
        Dim2i dim = (Dim2i)texture.getUserData( "EFFECTIVE_SIZE" );
        final float qw = getWidth() / (float)texture.getWidth();
        final float qh = getHeight() / (float)texture.getHeight();
        
        texUpperRight = new Point2f( qw, 1.0f );
        texLowerLeft = new Point2f( 0.0f, 1.0f - qh );
        */

        setTexture( texture );
        
        /*
        setTexturePosition( texLowerLeft, texUpperRight );
        */

        this.text = text;
        this.textColor = color;
        this.textFont = font;
        this.textAlignment = alignment;
    }
    
    /**
     * Sets the new text for this TextRectangle.
     * 
     * @param text new new text
     */
    public void setText( String text )
    {
        setText( text, getTextColor(), getTextFont(), getTextAlignment() );
    }
    
    /**
     * @return this TextRectangle's text
     */
    public String getText()
    {
        return ( text );
    }
    
    /**
     * @return this text's color
     */
    public Colorf getTextColor()
    {
        return ( textColor );
    }
    
    /**
     * @return this text's Font
     */
    public Font getTextFont()
    {
        return ( textFont );
    }
    
    /**
     * @return this text's horizontal alignment
     */
    public int getTextAlignment()
    {
        return ( textAlignment );
    }
    
    protected static float getHeightFromFixedWidth( float width, Texture texture )
    {
//        Dim2i dim = (Dim2i)texture.getUserData( "EFFECTIVE_SIZE" );
//        float q = (float)dim.getWidth() / (float)dim.getHeight();
//
		float q  = (float)texture.getWidth()/(float)texture.getHeight();
        return ( width / q );
    }
    
    protected static float getWidthFromFixedHeight( float height, Texture texture )
    {
//        Dim2i dim = (Dim2i)texture.getUserData( "EFFECTIVE_SIZE" );
//        float q = (float)dim.getWidth() / (float)dim.getHeight();
		float q  = (float)texture.getWidth()/(float)texture.getHeight();


		return ( height * q );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param texture the text-texture to apply
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     */
    protected TextRectangle( float width, float height, Tuple3f offset, Texture texture )
    {
        super( width, height, offset, texture, null, null, null );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the text-texture to apply
     */
    protected TextRectangle( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture )
    {
        super( width, height, zpl, zOffset, texture );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedWidth( float width, Tuple3f offset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, offset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedWidth( float width, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedWidth( float width, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, (Tuple3f)null, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedWidth( float width, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedWidth( float width, ZeroPointLocation zpl, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, zpl, 0.0f, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedWidth( float width, Tuple3f offset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, offset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedWidth( float width, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedWidth( float width, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, (Tuple3f)null, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedWidth( float width, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedWidth( float width, ZeroPointLocation zpl, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextRectangle( width, height, zpl, 0.0f, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedHeight( float height, Tuple3f offset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, offset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedHeight( float height, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedHeight( float height, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, (Tuple3f)null, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedHeight( float height, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextRectangle createFixedHeight( float height, ZeroPointLocation zpl, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, zpl, 0.0f, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedHeight( float height, Tuple3f offset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, offset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedHeight( float height, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedHeight( float height, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, (Tuple3f)null, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedHeight( float height, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextRectangle createFixedHeight( String text, Colorf color, Font font, float height, ZeroPointLocation zpl )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextRectangle( width, height, zpl, 0.0f, texture ) );
    }
}
