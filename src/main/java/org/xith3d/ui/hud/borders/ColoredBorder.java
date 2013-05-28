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

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.base.Border;
import org.xith3d.ui.hud.base.Widget;

/**
 * A ColoredBorder is a Border implementation with no Textures but only a
 * color.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ColoredBorder extends Border
{
    private final Colorf color = new Colorf();
    
    /**
     * Sets the border's color.
     * 
     * @param color
     */
    public void setColor( Colorf color )
    {
        this.color.set( color );
    }
    
    /**
     * Returns the border's color.
     * 
     * @return the border's color.
     */
    public final Colorf getColor()
    {
        return ( color.getReadOnly() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBorder( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, Widget hostWidget )
    {
        Colorf oldColor = texCanvas.getColorf();
        texCanvas.setColor( getColor() );
        
        // bottom
        if ( getBottomHeight() > 0 )
            texCanvas.fillRect( offsetX, offsetY + height - getBottomHeight(), width + 0, getBottomHeight() + 0 );
        
        // right
        if ( getRightWidth() > 0 )
            texCanvas.fillRect( offsetX + width - getRightWidth(), offsetY + getTopHeight(), getRightWidth() + 0, height - getTopHeight() - getBottomHeight() + 0 );
        
        // top
        if ( getTopHeight() > 0 )
            texCanvas.fillRect( offsetX, offsetY, width + 0, getTopHeight() + 0 );
        
        // left
        if ( getLeftWidth() > 0 )
            texCanvas.fillRect( offsetX, offsetY + getTopHeight(), getLeftWidth() + 0, height - getTopHeight() - getBottomHeight() + 0 );
        
        texCanvas.setColor( oldColor );
    }
    
    /**
     * Creates a new ColoredBorder with the given side widths.
     * 
     * @param bottomHeight
     * @param rightWidth
     * @param topHeight
     * @param leftWidth
     * @param color
     */
    public ColoredBorder( int bottomHeight, int rightWidth, int topHeight, int leftWidth, Colorf color )
    {
        super( bottomHeight, rightWidth, topHeight, leftWidth );
        
        if ( color == null )
        {
            throw new NullPointerException( "color parameter MUST NOT be null." );
        }
        
        setColor( color );
    }
    
    /**
     * Creates a new ColoredBorder with all sides of the same width.
     * 
     * @param width
     * @param color
     */
    public ColoredBorder( int width, Colorf color )
    {
        this( width, width, width, width, color );
    }
}
