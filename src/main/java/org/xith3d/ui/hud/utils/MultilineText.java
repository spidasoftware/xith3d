/**
 * Copyright (c) 2003-2008, Xith3D Project Group all rights reserved.
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
package org.xith3d.ui.hud.utils;

import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * {@link MultilineText} splits an input string at the newline character
 * and calculates sizes and offsets in pixels.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MultilineText
{
    private String text = null;
    private String[] lines = new String[ 0 ];
    private int numLines = 0;
    private Rectangle2D[] bounds = null;
    private int offset_y = 0;
    private int totalWidth = 0;
    private int totalHeight = 0;
    private int min_pos_x = 0;
    private int[] pos_x = null;
    private int[] pos_y = null;
    
    private int dirty = ~0;
    
    public final int getNumLines()
    {
        return ( numLines );
    }
    
    public final String getLine( int index )
    {
        return ( lines[index] );
    }
    
    public final int getMinPosX()
    {
        return ( min_pos_x );
    }
    
    public final int getPosX( int lineIndex )
    {
        return ( pos_x[lineIndex] );
    }
    
    public final int getPosY( int lineIndex )
    {
        return ( pos_y[lineIndex] );
    }
    
    public final int getLineOffsetY( int lineIndex )
    {
        return ( (int)bounds[lineIndex].getY() );
    }
    
    public final int getWidth( int lineIndex )
    {
        return ( (int)bounds[lineIndex].getWidth() );
    }
    
    public final int getHeight( int lineIndex )
    {
        return ( (int)bounds[lineIndex].getHeight() );
    }
    
    public final int getTotalWidth()
    {
        return ( totalWidth );
    }
    
    public final int getTotalHeight()
    {
        return ( totalHeight );
    }
    
    public final int getOffsetY()
    {
        return ( offset_y );
    }
    
    private static final String getLine( String text, int fromIdx, int toIdx )
    {
        if ( ( toIdx - fromIdx >= 1 ) && ( text.charAt( fromIdx ) == '\r' ) )
        {
            if ( ( toIdx - fromIdx >= 2 ) && ( text.charAt( toIdx - 1 ) == '\r' ) )
                return ( text.substring( fromIdx + 1, toIdx - 1 ) );
            
            return ( text.substring( fromIdx + 1, toIdx ));
        }
        
        if ( ( toIdx - fromIdx >= 2 ) && ( text.charAt( toIdx - 1 ) == '\r' ) )
        {
            return ( text.substring( fromIdx, toIdx - 1 ) );
        }
        
        if ( ( fromIdx == 0 ) && ( toIdx == text.length() ) )
            return ( text );
        
        return ( text.substring( fromIdx, toIdx ) );
    }
    
    private int splitText()
    {
        int newNumLines = 0;
        int off = 0;
        int idx = text.indexOf( '\n', off );
        if ( idx < 0 )
        {
            // Single-line text
            
            if ( lines.length < 1 )
                lines = new String[ 1 ];
            
            lines[0] = getLine( text, 0, text.length() );
            newNumLines = 1;
        }
        else
        {
            // Multi-line text
            
            do
            {
                if ( idx < 0 )
                    idx = text.length();
                
                if ( lines.length < newNumLines + 1 )
                {
                    String[] tmp = new String[ lines.length + 1 ];
                    System.arraycopy( lines, 0, tmp, 0, lines.length );
                    lines = tmp;
                }
                
                lines[ newNumLines++ ] = getLine( text, off, idx );
                
                off = idx + 1;
            }
            while ( ( ( idx = text.indexOf( '\n', off ) ) >= 0 ) || ( off < text.length() - 1 ) );
        }
        
        for ( int i = newNumLines; i < numLines; i++ )
        {
            lines[i] = null;
            bounds[i] = null;
            //lineMetrics[i] = null;
        }
        
        numLines = newNumLines;
        
        return ( numLines );
    }
    
    /**
     * Sets the new text and instantly splits it.
     * {@link #update(Texture2DCanvas, int, int, int, int, int, int, TextAlignment)}
     * must be called to update the sizes and offsets.
     * 
     * @param text
     */
    public void setText( String text )
    {
        this.text = text;
        
        splitText();
        
        //this.dirty |= 1;
        this.dirty = ~0;
    }
    
    /**
     * Marks sizes and offsets dirty.
     */
    public void setPositionDirty()
    {
        this.dirty |= 2;
    }
    
    /**
     * Updates all dirty content.
     * 
     * @param texCanvas
     * @param width
     * @param height
     * @param paddingLeft
     * @param paddingRight
     * @param paddingTop
     * @param paddingBottom
     * @param alignment
     */
    public void update( Texture2DCanvas texCanvas, int width, int height, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, TextAlignment alignment )
    {
        if ( dirty == 0 )
            return;
        
        if ( ( dirty & 1 ) != 0 )
        {
            FontMetrics metrics = texCanvas.getFontMetrics();
            
            if ( numLines == 0 )
            {
                min_pos_x = 0;
                totalWidth = 0;
                totalHeight = 0;
                offset_y = 0;
            }
            else
            {
                if ( ( bounds == null ) || ( bounds.length < numLines ) )
                    bounds = new Rectangle2D[ numLines ];
                totalHeight = 0;
                for ( int i = 0; i < numLines; i++ )
                {
                    bounds[i] = metrics.getStringBounds( lines[i], texCanvas );
                    totalHeight += bounds[i].getHeight();
                }
                offset_y = (int)bounds[0].getY();
            }
            
            if ( ( pos_x == null ) || ( pos_x.length < numLines ) )
                pos_x = new int[ numLines ];
            if ( ( pos_y == null ) || ( pos_y.length < numLines ) )
                pos_y = new int[ numLines ];
            
            dirty |= 2;
        }
        
        if ( ( dirty & 2 ) != 0 )
        {
            int y = paddingTop - offset_y;
            
            if ( alignment.isVCenterAligned() )
                y += (int)( ( height - paddingTop - paddingBottom - totalHeight ) / 2.0 );
            else if ( alignment.isBottomAligned() )
                y += (int)( height - paddingTop - paddingBottom - totalHeight );
            
            for ( int i = 0; i < numLines; i++ )
            {
                pos_x[i] = paddingLeft;
                if ( alignment.isHCenterAligned() )
                    pos_x[i] += (int)( ( width - paddingLeft - paddingRight - bounds[i].getWidth() ) / 2.0 );
                else if ( alignment.isRightAligned() )
                    pos_x[i] += (int)( width - paddingLeft - paddingRight - bounds[i].getWidth() );
                
                pos_y[i] = y;
                y += bounds[i].getHeight();
            }
        }
        
        if ( ( dirty & ( 1 | 2 ) ) != 0 )
        {
            if ( numLines == 0 )
            {
                min_pos_x = 0;
                totalWidth = 0;
            }
            else
            {
                min_pos_x = Integer.MAX_VALUE;
                totalWidth = 0;
                for ( int i = 0; i < numLines; i++ )
                {
                    min_pos_x = Math.min( min_pos_x, pos_x[i] );
                    totalWidth = Math.max( totalWidth, pos_x[i] + (int)bounds[i].getWidth() );
                }
                totalWidth -= min_pos_x;
            }
        }
        
        dirty = 0;
    }
    
    public MultilineText()
    {
    }
}
