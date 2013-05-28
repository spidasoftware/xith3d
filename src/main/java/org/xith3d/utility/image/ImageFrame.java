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

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;

/**
 * A frame that displays an image. Create an ImageFrame, then use one
 * of the setImage() methods to show the image.
 * 
 * @author <a href="http://www.gurge.com/amd/">Adam Doppelt</a>
 * @author David Yazel
 */
public class ImageFrame extends Frame
{
    private static final long serialVersionUID = 2095764397849922116L;
    
    private int left = -1;
    private int top;
    private Image image;
    
    public ImageFrame()
    {
        setLayout( null );
        setSize( 100, 100 );
    }
    
    /**
     * Set the image from a file.
     */
    public void setImage( File file ) throws IOException
    {
        // load the image
        Image image = getToolkit().getImage( file.getAbsolutePath() );
        
        // wait for the image to entirely load
        MediaTracker tracker = new MediaTracker( this );
        tracker.addImage( image, 0 );
        
        try
        {
            tracker.waitForID( 0 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        if ( tracker.statusID( 0, true ) != MediaTracker.COMPLETE )
        {
            throw new IOException( "Could not load: " + file + " " + tracker.statusID( 0, true ) );
        }
        
        setTitle( file.getName() );
        setImage( image );
    }
    
    /**
     * Set the image from an AWT image object.
     */
    public void setImage( Image image )
    {
        this.image = image;
        setVisible( true );
    }
    
    /**
     * Set the image from an indexed color array.
     */
    public void setImage( int[] palette, int[][] pixels )
    {
        int w = pixels.length;
        int h = pixels[ 0 ].length;
        int[] pix = new int[ w * h ];
        
        // convert to RGB
        for ( int x = w; x-- > 0; )
        {
            for ( int y = h; y-- > 0; )
            {
                pix[ ( y * w ) + x ] = palette[ pixels[ x ][ y ] ];
            }
        }
        
        setImage( w, h, pix );
    }
    
    /**
     * Set the image from a 2D RGB pixel array.
     */
    public void setImage( int[][] pixels )
    {
        int w = pixels.length;
        int h = pixels[ 0 ].length;
        int[] pix = new int[ w * h ];
        
        // convert to RGB
        for ( int x = w; x-- > 0; )
        {
            for ( int y = h; y-- > 0; )
            {
                pix[ ( y * w ) + x ] = pixels[ x ][ y ];
            }
        }
        
        setImage( w, h, pix );
    }
    
    /**
     * Set the image from a 1D RGB pixel array.
     */
    public void setImage( int w, int h, int[] pix )
    {
        setImage( createImage( new MemoryImageSource( w, h, pix, 0, w ) ) );
    }
    
    /**
     * Get the image.
     */
    public Image getImage()
    {
        return image;
    }
    
    /**
     * Overridden for double buffering.
     */
    @Override
    public void update( Graphics g )
    {
        paint( g );
    }
    
    /**
     * Paint the image.
     */
    @Override
    public void paint( Graphics g )
    {
        // the first time through, figure out where to draw the image
        if ( left == -1 )
        {
            Insets insets = getInsets();
            left = insets.left;
            top = insets.top;
            
            setSize( image.getWidth( null ) + left + insets.right, image.getHeight( null ) + top + insets.bottom );
        }
        
        g.drawImage( image, left, top, this );
    }
    
    /*
    public static void main( String[] args ) throws IOException
    {
        ImageFrame f = new ImageFrame();
        f.setImage( new File( args[ 0 ] ) );
    }
    */
}
