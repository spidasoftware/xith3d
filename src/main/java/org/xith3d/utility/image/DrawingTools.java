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
package org.xith3d.utility.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * A bunch of static functions meant to be used as helpers in the various
 * control paint functions.
 *
 * @author David Yazel
 */
public class DrawingTools
{
    public DrawingTools()
    {
    }
    
    public static void centerText( Component c, Graphics g, String text )
    {
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        
        g.setFont( c.getFont() );
        
        int w = g.getFontMetrics().stringWidth( text );
        int h = g.getFontMetrics().getMaxAscent() + g.getFontMetrics().getMaxDescent();
        int tx = ( c.getWidth() / 2 ) - ( w / 2 );
        int ty = ( c.getHeight() / 2 ) + ( h / 3 );
        
        g.setColor( c.getForeground() );
        g.drawString( text, tx, ty );
    }
    
    public static void centerText( Rectangle r, Graphics g, String text )
    {
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        
        int w = g.getFontMetrics().stringWidth( text );
        int h = g.getFontMetrics().getMaxAscent() + g.getFontMetrics().getMaxDescent();
        int tx = r.x + ( r.width / 2 ) - ( w / 2 );
        int ty = r.y + ( r.height / 2 ) + ( h / 3 );
        
        g.drawString( text, tx, ty );
    }
    
    public static void drawText( Rectangle r, Graphics g, String text )
    {
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        
        //int w = g.getFontMetrics().stringWidth(text);
        int h = g.getFontMetrics().getMaxAscent() + g.getFontMetrics().getMaxDescent();
        int ty = r.y + ( r.height / 2 ) + ( h / 3 );
        
        g.drawString( text, r.x, ty );
    }
    
    public static void centerShadowedText( Component c, Graphics g, String text )
    {
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        
        g.setFont( c.getFont() );
        
        int w = g.getFontMetrics().stringWidth( text );
        int h = g.getFontMetrics().getMaxAscent() + g.getFontMetrics().getMaxDescent();
        int tx = ( c.getWidth() / 2 ) - ( w / 2 );
        int ty = ( c.getHeight() / 2 ) + ( h / 4 );
        
        g.setColor( Color.black );
        g.drawString( text, tx + 1, ty + 1 );
        
        g.setColor( c.getForeground() );
        g.drawString( text, tx, ty );
    }
    
    public static void drawImageOnCanvas( Graphics g, BufferedImage back, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2 )
    {
        if ( sx2 > back.getWidth() )
        {
            sx2 = back.getWidth();
        }
        
        if ( sy2 > back.getHeight() )
        {
            sy2 = back.getHeight();
        }
        
        if ( sx1 >= sx2 )
        {
            return;
        }
        
        if ( sy1 >= sy2 )
        {
            return;
        }
        
        if ( sx1 < 0 )
        {
            return;
        }
        
        if ( sy1 < 0 )
        {
            return;
        }
        
        if ( ( dx2 - dx1 ) != ( sx2 - sx1 ) )
        {
            dx2 = dx1 + ( sx2 - sx1 );
        }
        
        if ( ( dy2 - dy1 ) != ( sy2 - sy1 ) )
        {
            dy2 = dy1 + ( sy2 - sy1 );
        }
        
        g.drawImage( back, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null );
    }
    
    /**
     * tiles an image horizonatally in 3 segments, the first segment is the left side, the second segment (repeated)
     * is the middle segment and the final segment is the right hand side.
     * 
     * @param g
     * @param image Image containing the segment to be tiled
     * @param leftWidth The width of the first segment
     * @param rightWidth The width of the thrid segment
     * @param dy The destination Y value
     * @param sy The source Y value
     * @param width The width of the target area
     * @param height The height of the target area
     */
    public static void tileSegmentsHorizontally( Graphics g, BufferedImage image, int leftWidth, int rightWidth, int dy, int sy, int width, int height )
    {
        int middleWidth = image.getWidth() - leftWidth - rightWidth;
        int middleTarget = width - leftWidth - rightWidth;
        int imageWidth = image.getWidth();
        
        // draw left hand side
        drawImageOnCanvas( g, image, 0, dy, leftWidth, dy + height, 0, sy, leftWidth, sy + height );
        
        // draw right hand side
        drawImageOnCanvas( g, image, width - rightWidth, dy, width, dy + height, imageWidth - rightWidth, sy, imageWidth, sy + height );
        
        // now we need to draw as many segments as needed to fill in remainder
        int amountLeft = middleTarget;
        int x = leftWidth;
        int check = 0;
        
        while ( amountLeft > 0 )
        {
            // calculate amounts
            int amount = middleWidth;
            
            if ( amount > amountLeft )
            {
                amount = amountLeft;
            }
            
            drawImageOnCanvas( g, image, x, dy, amount, dy + height, leftWidth, sy, leftWidth + amount, sy + height );
            
            // adjust counters
            x += amount;
            amountLeft -= amount;
            
            if ( ( check++ ) > 10 )
            {
                break;
            }
        }
    }
    
    public static void drawSegmentedImage( Graphics g, BufferedImage image, int height, int width, int topWidth, int bottomWidth, int leftWidth, int rightWidth )
    {
        int borderHeight = image.getHeight();
        
        DrawingTools.tileSegmentsHorizontally( g, image, leftWidth, rightWidth, 0, 0, width, topWidth );
        DrawingTools.tileSegmentsHorizontally( g, image, leftWidth, rightWidth, height - bottomWidth, borderHeight - bottomWidth, width, bottomWidth );
        
        // now draw any addition bands needed
        int middleHeight = borderHeight - topWidth - bottomWidth;
        int middleTarget = height - topWidth - bottomWidth;
        
        int amountLeft = middleTarget;
        int y = topWidth;
        int check = 0;
        
        while ( amountLeft > 0 )
        {
            // calculate amounts
            int amount = middleHeight;
            
            if ( amount > amountLeft )
            {
                amount = amountLeft;
            }
            
            DrawingTools.tileSegmentsHorizontally( g, image, leftWidth, rightWidth, y, topWidth, width, amount );
            
            // adjust counters
            y += amount;
            amountLeft -= amount;
            
            if ( ( check++ ) > 10 )
            {
                break;
            }
        }
    }
    
    public static void drawImageOnCanvas( Graphics g, int x, int y, Rectangle r, BufferedImage back )
    {
        int ux = r.x + (int)r.getWidth();
        int uy = r.y + (int)r.getHeight();
        
        int amountX = ( back.getWidth() );
        
        if ( ( amountX + x ) >= ux )
        {
            amountX = ux - x;
        }
        
        int amountY = ( back.getHeight() );
        
        if ( ( amountY + y ) >= uy )
        {
            amountY = uy - y;
        }
        
        g.drawImage( back, x, y, x + amountX, y + amountY, 0, 0, amountX, amountY, null );
    }
    
    public static void fillWithBackground( Graphics g, Rectangle r, BufferedImage back )
    {
        if ( ( r.x >= back.getWidth() ) || ( r.y >= back.getHeight() ) || ( r.x < 0 ) || ( r.y < 0 ) )
        {
            throw new Error( "Illegal attempt to place an image at " + r.x + "," + r.y + " when image is " + back.getWidth() + "x" + back.getHeight() );
        }
        
        try
        {
            drawImageOnCanvas( g, r.x, r.y, r, back );
        }
        catch ( Exception e )
        {
            throw new Error( "Exception attempting to place an image at " + r.x + "," + r.y + " (" + r.width + "x" + r.height + ") " + " when image is " + back.getWidth() + "x" + back.getHeight() );
        }
        
        // now loop through and copy the location to the rest of the area assuming that
        // not all of it is covered.  This should be faster than drawing the image
        // multiple times.
        int y = r.y;
        int x = 0;
        
        while ( y < ( r.y + r.height ) )
        {
            if ( y == r.y )
            {
                x = r.x + back.getWidth();
            }
            else
            {
                x = r.x;
            }
            
            // now loop through the X values
            while ( x < ( r.x + r.width ) )
            {
                drawImageOnCanvas( g, x, y, r, back );
                x += back.getWidth();
            }
            
            y += back.getHeight();
        }
    }
    
    public static void drawBorderedBox( Graphics g, Rectangle r, BufferedImage back, BufferedImage front )
    {
        fillWithBackground( g, r, back );
        g.setClip( 4, 4, r.width - 8, r.height - 8 );
        fillWithBackground( g, r, front );
        g.setClip( 0, 0, r.width, r.height );
        
        Color shadow = new Color( 0f, 0f, 0f, 0.8f );
        g.setColor( shadow );
        g.drawRect( 0, 0, r.width - 1, r.height - 1 );
    }
    
    /**
     * Draws a shadowed rectagle on the supplied graphics context.  This is a utility
     * function to be used in drawing panels and windows.
     */
    public static void drawShadowedBox( Graphics g, Rectangle r, Color highlight, Color shadow, Color internal )
    {
        g.setColor( shadow );
        
        // draw the shadows for the border
        int tx = r.x + r.width;
        int ty = r.y + r.height;
        g.drawLine( r.x + 1, r.y + 1, tx - 3, r.y + 1 );
        g.drawLine( r.x, ty - 1, tx - 1, ty - 1 );
        g.drawLine( r.x + 1, r.y + 2, r.x + 1, ty - 3 );
        g.drawLine( tx - 1, r.y, tx - 1, ty - 1 );
        
        // lower the intensity of the border itself
        g.setColor( highlight );
        g.drawRect( r.x, r.y, r.width - 2, r.height - 2 );
        
        // now drop the intensity of the inside
        g.setColor( internal );
        g.fillRect( r.x + 2, r.y + 2, r.width - 4, r.height - 4 );
    }
    
    public static void drawBox( Graphics g, Rectangle r, Color highlight, Color shadow )
    {
        g.setColor( shadow );
        
        // draw the shadows for the border
        int tx = r.x + r.width;
        int ty = r.y + r.height;
        g.drawLine( r.x + 1, r.y + 1, tx - 3, r.y + 1 );
        g.drawLine( r.x, ty - 1, tx - 1, ty - 1 );
        g.drawLine( r.x + 1, r.y + 2, r.x + 1, ty - 3 );
        g.drawLine( tx - 1, r.y, tx - 1, ty - 1 );
        
        // lower the intensity of the border itself
        g.setColor( highlight );
        g.drawRect( r.x, r.y, r.width - 2, r.height - 2 );
    }
    
    public static void drawShadowedBox( Graphics g, Rectangle r, float alpha )
    {
        Color shadow = new Color( 0f, 0f, 0f, 0.8f );
        Color highlight = new Color( 0f, 0f, 0f, 0.1f );
        Color inside = new Color( 0f, 0f, 0f, alpha );
        
        drawShadowedBox( g, r, highlight, shadow, inside );
    }
    
    public static void drawShadowedBox( Graphics g, Rectangle r, float highlightAlpha, float shadowAlpha, float insideAlpha )
    {
        Color shadow = new Color( 0f, 0f, 0f, shadowAlpha );
        Color highlight = new Color( 1f, 1f, 1f, highlightAlpha );
        Color inside = new Color( 0f, 0f, 0f, insideAlpha );
        
        drawShadowedBox( g, r, highlight, shadow, inside );
    }
    
    public static void adjustAlpha( BufferedImage image, float alpha )
    {
        int n = image.getWidth() * image.getHeight();
        int[] pixels = new int[ n ];
        image.getRGB( 0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth() );
        
        int a = (int)( 255.0f * alpha );
        
        for ( int i = 0; i < n; i++ )
        {
            pixels[ i ] = ( a << 24 ) | ( pixels[ i ] & 0xffffff );
        }
        
        image.setRGB( 0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth() );
    }
}
