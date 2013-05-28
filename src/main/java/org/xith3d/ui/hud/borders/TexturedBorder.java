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
package org.xith3d.ui.hud.borders;

import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.ui.hud.base.Border;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.utils.HUDTextureUtils;

/**
 * A ColoredBorder is a Border implementation with no Textures but only a
 * color.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TexturedBorder extends Border
{
    private Texture2D texture;
    private Border.Description desc;
    
    /**
     * Sets the border's texture.
     * 
     * @param color
     */
    public void setTexture( Texture2D texture )
    {
        if ( texture == null )
        {
            throw new NullPointerException( "texture parameter MUST NOT be null." );
        }
        
        this.texture = texture;
    }
    
    /**
     * Returns the border's texture.
     * 
     * @return the border's texture.
     */
    public final Texture2D getTexture()
    {
        return ( texture );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBorder( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, Widget hostWidget )
    {
        Texture2D texture = getTexture();
        TextureImage2D image = texture.getImage0();
        
        int srcW = HUDTextureUtils.getTextureWidth( texture );
        int srcH = HUDTextureUtils.getTextureHeight( texture );
        
        int srcBottomH = getBottomHeight();
        int srcRightW = getRightWidth();
        int srcTopH = getTopHeight();
        int srcLeftW = getLeftWidth();
        
        int ll_upper = desc.getLLupperHeight();
        int ll_right = desc.getLLrightWidth();
        int lr_left = desc.getLRleftWidth();
        int lr_upper = desc.getLRupperHeight();
        int ur_lower = desc.getURlowerHeight();
        int ur_left = desc.getURleftWidth();
        int ul_right = desc.getULrightWidth();
        int ul_lower = desc.getULlowerHeight();
        
        int srcRightLeft = srcW - srcRightW;
        int srcBottomTop = srcH - srcBottomH;
        int trgRightLeft = width - srcRightW;
        int trgBottomTop = height - srcBottomH;
        
        final TextureImage2D ti = texCanvas.getImage();
        
        /*
        if ( srcBottomH > 0 )
        {
            ti.update( offsetX, offsetY + texHeight - srcBottomH, width, height );
        }
        
        if ( srcTopH > 0 )
        {
            ti.update( offsetX, offsetY, width, srcTopH );
        }
        
        if ( ( srcLeftW > 0 ) || ( srcRightW > 0 ) )
        {
            int t = 0;
            int h = height;
            if ( srcTopH > 0 )
            {
                t += srcTopH;
                h -= srcTopH;
            }
            if ( srcBottomH > 0 )
            {
                h -= srcBottomH;
            }
            
            if ( srcLeftW > 0 )
                ti.update( offsetX, offsetY + t, srcLeftW, h );
            
            if ( srcRightW > 0 )
                ti.update( offsetX + width - srcRightW, offsetY + t, srcRightW, h );
        }
        */
        
        // render corners...
        if ( ( srcLeftW > 0 ) && ( srcBottomH > 0 ) )
            ti.drawImage( image, 0, srcBottomTop, srcLeftW, srcBottomH, offsetX + 0, offsetY + trgBottomTop );
        if ( ( srcRightW > 0 ) && ( srcBottomH > 0 ) )
            ti.drawImage( image, srcRightLeft, srcBottomTop, srcRightW, srcBottomH, offsetX + trgRightLeft, offsetY + trgBottomTop );
        if ( ( srcRightW > 0 ) && ( srcTopH > 0 ) )
            ti.drawImage( image, srcRightLeft, 0, srcRightW, srcTopH, offsetX + trgRightLeft, offsetY );
        if ( ( srcLeftW > 0 ) && ( srcTopH > 0 ) )
            ti.drawImage( image, 0, 0, srcLeftW, srcTopH, offsetX, offsetY );
        
        // render extended corners...
        if ( ll_right > 0 )
            ti.drawImage( image, srcLeftW, srcBottomTop, ll_right, srcBottomH, offsetX + srcLeftW, offsetY + trgBottomTop, ll_right, srcBottomH );
        if ( lr_left > 0 )
            ti.drawImage( image, srcRightLeft - lr_left, srcBottomTop, lr_left, srcBottomH, offsetX + trgRightLeft - lr_left, offsetY + trgBottomTop, lr_left, srcBottomH );
        if ( lr_upper > 0 )
            ti.drawImage( image, srcRightLeft, srcBottomTop - lr_upper, srcRightW, lr_upper, offsetX + trgRightLeft, offsetY + trgBottomTop - lr_upper, srcRightW, lr_upper );
        if ( ur_lower > 0 )
            ti.drawImage( image, srcRightLeft, srcTopH, srcRightW, ul_lower, offsetX + trgRightLeft, offsetY + srcTopH, srcRightW, ul_lower );
        if ( ur_left > 0 )
            ti.drawImage( image, srcRightLeft - ur_left, 0, ur_left, srcTopH, offsetX + trgRightLeft - ur_left, offsetY, ur_left, srcTopH );
        if ( ul_right > 0 )
            ti.drawImage( image, srcLeftW, 0, ul_right, srcTopH, offsetX + srcLeftW, offsetY, ul_right, srcTopH );
        if ( ul_lower > 0 )
            ti.drawImage( image, 0, srcTopH, srcLeftW, ul_lower, offsetX, offsetY + srcTopH, srcLeftW, ul_lower );
        if ( ll_upper > 0 )
            ti.drawImage( image, 0, srcBottomTop - ll_upper, srcLeftW, ll_upper, offsetX, offsetY + trgBottomTop - ll_upper, srcLeftW, ll_upper );
        
        // render edges...
        if ( srcBottomH > 0 )
            ti.drawImage( image, srcLeftW + ll_right, srcBottomTop, srcW - srcLeftW - ll_right - lr_left - srcRightW, srcBottomH, offsetX + srcLeftW + ll_right, offsetY + trgBottomTop, width - srcLeftW - ll_right - lr_left - srcRightW, srcBottomH );
        if ( srcRightW > 0 )
            ti.drawImage( image, srcRightLeft, srcTopH + ur_lower, srcRightW, srcH - srcTopH - ur_lower - lr_upper - srcBottomH, offsetX + trgRightLeft, offsetY + srcTopH + ur_lower, srcRightW, height - srcTopH - ur_lower - lr_upper - srcBottomH );
        if ( srcTopH > 0 )
            ti.drawImage( image, srcLeftW + ul_right, 0, srcW - ul_right - ur_left - srcLeftW - srcRightW, srcTopH, offsetX + srcLeftW + ul_right, offsetY, width - srcLeftW - ul_right - ur_left - srcRightW, srcTopH );
        if ( srcLeftW > 0 )
            ti.drawImage( image, 0, srcTopH + ul_lower, srcLeftW, srcH - srcTopH - ul_lower - ll_upper - srcBottomH, offsetX, offsetY + srcTopH + ul_lower, srcLeftW, height - srcTopH - ul_lower - ll_upper - srcBottomH );
    }
    
    /**
     * Creates a new TexturedBorder with the given side widths.
     * 
     * @param description
     * @param bottomHeight
     * @param rightWidth
     * @param topHeight
     * @param leftWidth
     * @param texture
     */
    private TexturedBorder( Border.Description description, int bottomHeight, int rightWidth, int topHeight, int leftWidth, Texture2D texture )
    {
        super( bottomHeight, rightWidth, topHeight, leftWidth );
        
        setTexture( texture );
        
        if ( description == null )
            this.desc = new Border.Description( bottomHeight, rightWidth, topHeight, leftWidth, texture );
        else
            this.desc = description.clone();
        
        this.desc.setTexture( texture );
    }
    
    /**
     * Creates a new TexturedBorder with the given side widths.
     * 
     * @param bottomHeight
     * @param rightWidth
     * @param topHeight
     * @param leftWidth
     * @param texture
     */
    public TexturedBorder( int bottomHeight, int rightWidth, int topHeight, int leftWidth, Texture2D texture )
    {
        this( null, bottomHeight, rightWidth, topHeight, leftWidth, texture );
    }
    
    /**
     * Creates a new TexturedBorder with all sides of the same width.
     * 
     * @param width
     * @param texture
     */
    public TexturedBorder( int width, Texture2D texture )
    {
        this( width, width, width, width, texture );
    }
    
    /**
     * Creates a new TexturedBorder with the given side widths.
     * 
     * @param description
     */
    public TexturedBorder( Border.Description description )
    {
        this( description, description.getBottomHeight(), description.getRightWidth(), description.getTopHeight(), description.getLeftWidth(), description.getTexture() );
    }
    
    /**
     * Creates a new TexturedBorder with the given side widths.
     * 
     * @param description
     * @param bottomHeight
     * @param rightWidth
     * @param topHeight
     * @param leftWidth
     * @param texture
     */
    private TexturedBorder( Border.Description description, int bottomHeight, int rightWidth, int topHeight, int leftWidth, String texture )
    {
        this( description, bottomHeight, rightWidth, topHeight, leftWidth, HUDTextureUtils.getTexture( texture ) );
    }
    
    /**
     * Creates a new TexturedBorder with the given side widths.
     * 
     * @param bottomHeight
     * @param rightWidth
     * @param topHeight
     * @param leftWidth
     * @param texture
     */
    public TexturedBorder( int bottomHeight, int rightWidth, int topHeight, int leftWidth, String texture )
    {
        this( null, bottomHeight, rightWidth, topHeight, leftWidth, texture );
    }
    
    /**
     * Creates a new TexturedBorder with all sides of the same width.
     * 
     * @param width
     * @param texture
     */
    public TexturedBorder( int width, String texture )
    {
        this( width, width, width, width, texture );
    }
}
