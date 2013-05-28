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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * A powerful implementation of the border interface. This creates
 * an alpha blended image using a series of supplied images.
 * 
 * @author David Yazel
 */
public class MaskedBackgroundBuilder
{
    private BufferedImage border;
    private BufferedImage borderMask;
    private int leftWidth = 20;
    private int rightWidth = 20;
    private int topWidth = 20;
    private int bottomWidth = 20;
    private BufferedImage center;
    
    public MaskedBackgroundBuilder( BufferedImage border, BufferedImage borderMask, BufferedImage center )
    {
        this.border = border;
        this.borderMask = borderMask;
        this.center = center;
    }
    
    public void setCornerSize( int size )
    {
        leftWidth = size;
        rightWidth = size;
        bottomWidth = size;
        topWidth = size;
    }
    
    public BufferedImage getImage( int width, int height )
    {
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)image.getGraphics();
        
        // check for various error conditions
        if ( ( center == null ) || ( border == null ) || ( borderMask == null ) )
        {
            if ( center == null )
            {
                g.setColor( Color.blue );
                g.fillRect( 0, 0, width, height );
            }
            else
            {
                g.setColor( Color.white );
                g.fillRect( 0, 0, width, height );
            }
            
            if ( border == null )
            {
                g.setColor( Color.red );
                g.drawRect( 0, 0, width, height );
            }
            
            if ( borderMask == null )
            {
                g.setColor( Color.green );
                g.drawRect( 1, 1, width - 2, height - 2 );
            }
            
            return image;
        }
        
        try
        {
            g.setClip( 0, 0, width, height );
            
            // lay down the mask
            g.setComposite( AlphaComposite.Src );
            
            DrawingTools.drawSegmentedImage( g, borderMask, height, width, topWidth, bottomWidth, leftWidth, rightWidth );
            
            // tile back ground only within mask
            g.setComposite( AlphaComposite.SrcIn );
            DrawingTools.fillWithBackground( g, new Rectangle( width, height ), center );
            
            g.setComposite( AlphaComposite.SrcOver );
            DrawingTools.drawSegmentedImage( g, border, height, width, topWidth, bottomWidth, leftWidth, rightWidth );
            
        }
        catch ( Exception error )
        {
            g.setColor( Color.white );
            g.fillRect( 0, 0, width, height );
            g.setColor( Color.black );
            
            StackTraceElement[] stack = error.getStackTrace();
            g.setFont( new Font( "dialog", Font.PLAIN, 10 ) );
            g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            
            for ( int i = 0; i < stack.length; i++ )
            {
                g.drawString( stack[ i ].toString(), 10, i * 15 );
            }
        }
        
        return image;
    }
}
