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
package org.xith3d.utility.screenshots;

import org.jagatoo.util.nio.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This class creates a screenshot image from the frame buffer's content.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ScreenshotCreator implements Runnable
{
    private static final int GL_RGB = 6407;
    private static final int GL_RGBA = 6408;
    
    /**
     * This format indicates if the resulting image
     * will have an alpha channel
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public enum Format
    {
        RGB( GL_RGB ),
        RGBA( GL_RGBA );
        
        private int int_GL;
        
        public int getIntGL()
        {
            return ( int_GL );
        }
        
        public int getBufferedImageFormat()
        {
            if ( this == RGB )
                return ( BufferedImage.TYPE_INT_RGB );
            
            return ( BufferedImage.TYPE_INT_ARGB );
        }
        
        public static Format getFromGL( int intGL )
        {
            if ( intGL == GL_RGB )
                return ( RGB );
            else if ( intGL == GL_RGBA )
                return ( RGBA );
            else
                throw new IllegalArgumentException( "Either RGB or RGBA are allowed as format." );
        }
        
        private Format( int int_GL )
        {
            this.int_GL = int_GL;
        }
    }
    
    private Format format;
    private ByteBuffer byteBuffer;
    private BufferedImage image;
    private File targetFile;
    
    /**
     * @return the image-format
     */
    public Format getFormat()
    {
        return ( format );
    }
    
    /**
     * @return the OpenGL-image-format
     */
    public int getGLFormat()
    {
        return ( format.getIntGL() );
    }
    
    /**
     * @return the ByteBuffer to use for FrameBuffer capture
     */
    public ByteBuffer getBuffer()
    {
        return ( byteBuffer );
    }
    
    private BufferedImage convertRGB()
    {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Allocate space for the converted pixels
        int[] pixelInts = new int[ width * height ];
        
        // Convert RGB bytes to ARGB ints with no transparency. Flip 
        // image vertically by reading the rows of pixels in the byte 
        // buffer in reverse - (0,0) is at bottom left in OpenGL.
        
        int p = width * height * 3; // Points to first byte (red) in each row.
        int q; // Index into ByteBuffer
        int i = 0; // Index into target int[]
        int bytesPerRow = width * 3; // Number of bytes in each row
        
        for ( int row = height - 1; row >= 0; row-- )
        {
            p = row * bytesPerRow;
            q = p;
            for ( int col = 0; col < width; col++ )
            {
                int iR = byteBuffer.get( q++ );
                int iG = byteBuffer.get( q++ );
                int iB = byteBuffer.get( q++ );
                
                pixelInts[ i++ ] = ( ( 0xFF000000 ) | ( ( iR & 0xFF ) << 16 ) | ( ( iG & 0xFF ) << 8 ) | ( iB & 0xFF ) );
            }
        }
        
        // Set the data for the BufferedImage
        image.setRGB( 0, 0, image.getWidth(), image.getHeight(), pixelInts, 0, image.getWidth() );
        
        return ( image );
    }
    
    private BufferedImage convertRGBA()
    {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Allocate space for the converted pixels
        //int[] pixelInts = new int[ width * height ];
        
        // Convert RGB bytes to ARGB ints with no transparency. Flip 
        // image vertically by reading the rows of pixels in the byte 
        // buffer in reverse - (0,0) is at bottom left in OpenGL.
        
        final int bytesPerRow = width * 4; // Number of bytes in each row
        int p = width * height * 4; // Points to first byte (red) in each row.
        int q; // Index into ByteBuffer
        //int i = 0; // Index into target int[]
        
        for ( int row = height - 1; row >= 0; row-- )
        {
            p = row * bytesPerRow;
            q = p;
            for ( int col = 0; col < width; col++ )
            {
                int iR = byteBuffer.get( q++ );
                int iG = byteBuffer.get( q++ );
                int iB = byteBuffer.get( q++ );
                int iA = byteBuffer.get( q++ );
                
                int pixelInt = ( ( 0xFF000000 ) | ( ( iA & 0xFF ) << 24 ) | ( ( iR & 0xFF ) << 16 ) | ( ( iG & 0xFF ) << 8 ) | ( iB & 0xFF ) );
                
                image.setRGB( col, row, pixelInt );
                
                //pixelInts[ i++ ] = pixelInt;
            }
        }
        
        // Set the data for the BufferedImage
        //image.setRGB( 0, 0, image.getWidth(), image.getHeight(), pixelInts, 0, image.getWidth() );
        
        return ( image );
    }
    
    public void run()
    {
        if ( format == Format.RGB )
            convertRGB();
        else
            //if (format == Format.RGBA)
            convertRGBA();
        
        if ( targetFile != null )
        {
            try
            {
                ImageIO.write( image, "PNG", targetFile );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Creates a screenshot from a ByteBuffer
     */
    public BufferedImage createScreenshot()
    {
        if ( targetFile == null )
        {
            if ( format == Format.RGB )
                return ( convertRGB() );
            
            return ( convertRGBA() );
        }
        
//        new Thread( this , "Create screenshot").start();

		this.run();
        return ( null );
    }
    
    /**
     * Creates a screenshot from a ByteBuffer (asynchronously)
     * 
     * @param targetFile the file to save the shot to
     */
    public void createScreenshot( File targetFile )
    {
        this.targetFile = targetFile;
        
        new Thread( this , "Create screenshot").start();
    }
    
    /**
     * Starts a new Thread that waits until the time is right to create a new screenshot.
     * 
     * @param width the image's width
     * @param height the image's height
     * @param format RGB or RGBA
     * @param targetFile the file to save the shot to
     */
    public ScreenshotCreator( int width, int height, Format format, File targetFile )
    {
        this.format = format;
        
        int bytesPerPixel = 0;
        if ( format == Format.RGB )
            bytesPerPixel = 3;
        else if ( format == Format.RGBA )
            bytesPerPixel = 4;
        else
            throw new IllegalArgumentException( "Either RGB or RGBA are allowed as format." );
        
        this.byteBuffer = BufferUtils.createByteBuffer( width * height * bytesPerPixel );
        this.image = new BufferedImage( width, height, format.getBufferedImageFormat() );
        
        this.targetFile = targetFile;
    }
    
    /**
     * Starts a new Thread that waits until the time is right to create a new screenshot.
     * 
     * @param width the image's width
     * @param height the image's height
     * @param format RGB or RGBA
     */
    public ScreenshotCreator( int width, int height, Format format )
    {
        this( width, height, format, null );
    }
}
