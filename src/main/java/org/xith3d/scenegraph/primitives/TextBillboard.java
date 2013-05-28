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

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.scenegraph.Billboard;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * The TextBillboard is a Rectangle with a text-Texture on it, that's always
 * facing the camera.<br>
 * The Texture is created by TextureCreator.createTexture(String, Colorf, java.awt.Font, int).
 * 
 * @see TextureCreator#createTexture(String, Colorf, java.awt.Font, int)
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextBillboard extends TextRectangle implements Billboard
{
    protected long lastFrameId = -Long.MAX_VALUE;
    
    protected Tuple3f[] zeroVertices;
    protected Point3f[] transformedVertices = new Point3f[]
    {
        new Point3f(), new Point3f(), new Point3f(), new Point3f()
    };
    
    public void updateOriginalVertexCoordinates()
    {
        this.getGeometry().getCoordinates( 0, zeroVertices );
    }
    
    /**
     * {@inheritDoc}
     */
    public Sized2iRO getSizeOnScreen()
    {
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateFaceToCamera( Matrix3f viewRotation, long frameId, long nanoTime, long nanoStep )
    {
        if ( frameId <= lastFrameId )
            return;

        for ( int i = 0; i < 4; i++ )
        {
            transformedVertices[ i ].set( zeroVertices[ i ] );
            viewRotation.transform( transformedVertices[ i ] );
        }

        this.getGeometry().setCoordinates( 0, transformedVertices );

        lastFrameId = frameId;
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param position relative Location of the Rectangle
     * @param texture the text-texture to apply
     */
    protected TextBillboard( float width, float height, Tuple3f position, Texture texture )
    {
        super( width, height, position, texture );
        
        this.zeroVertices = createVertexCoords( width, height, position );
        
        getGeometry().setOptimization( Optimization.NONE );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the text-texture to apply
     */
    protected TextBillboard( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture )
    {
        super( width, height, zpl, zOffset, texture );
        
        this.zeroVertices = createVertexCoords( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ) );
        
        getGeometry().setOptimization( Optimization.NONE );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedWidth( float width, Tuple3f position, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        final TextBillboard rect = new TextBillboard( width, height, position, texture );
        
        return ( rect );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedWidth( float width, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        final TextBillboard rect = new TextBillboard( width, height, new Vector3f( 0f, 0f, zOffset ), texture );
        
        return ( rect );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param alignment the text horizontal alignment
     * @param width (fixed) width of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedWidth( float width, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        final TextBillboard rect = new TextBillboard( width, height, (Tuple3f)null, texture );
        
        return ( rect );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedWidth( float width, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextBillboard( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedWidth( float width, ZeroPointLocation zpl, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextBillboard( width, height, zpl, 0.0f, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedWidth( float width, Tuple3f position, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        final TextBillboard rect = new TextBillboard( width, height, position, texture );
        
        return ( rect );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedWidth( float width, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        final TextBillboard rect = new TextBillboard( width, height, new Vector3f( 0f, 0f, zOffset ), texture );
        
        return ( rect );
    }
    
    /**
     * Creates a TextRectangle.
     * 
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param width (fixed) width of the Rectangle
     */
    public static TextBillboard createFixedWidth( float width, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        final TextBillboard rect = new TextBillboard( width, height, (Tuple3f)null, texture );
        
        return ( rect );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedWidth( float width, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextBillboard( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedWidth( float width, ZeroPointLocation zpl, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float height = getHeightFromFixedWidth( width, texture );
        
        return ( new TextBillboard( width, height, zpl, 0.0f, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedHeight( float height, Tuple3f position, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, position, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedHeight( float height, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedHeight( float height, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, (Tuple3f)null, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedHeight( float height, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboard createFixedHeight( float height, ZeroPointLocation zpl, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, zpl, 0.0f, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedHeight( float height, Tuple3f position, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, position, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedHeight( float height, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedHeight( float height, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, (Tuple3f)null, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedHeight( float height, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, zpl, zOffset, texture ) );
    }
    
    /**
     * Creates a TextBillboard.
     * 
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboard createFixedHeight( float height, ZeroPointLocation zpl, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );
        
        final float width = getWidthFromFixedHeight( height, texture );
        
        return ( new TextBillboard( width, height, zpl, 0.0f, texture ) );
    }
}
