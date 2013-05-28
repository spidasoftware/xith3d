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
package org.xith3d.ui.hud.utils;

import org.openmali.FastMath;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.scenegraph.TextureImage2D;

/**
 * Default implementation of the {@link DropShadowFactory}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DefaultDropShadowFactory extends DropShadowFactory
{
    private int offsetX = 0;
    private int offsetY = 0;
    private Colorf startColor = new Colorf( 0f, 0f, 0f, 0f );
    
    private byte[] pixelLine = null;
    
    protected int getOffsetX()
    {
        return ( offsetX );
    }
    
    protected int getOffsetY()
    {
        return ( offsetY );
    }
    
    protected Colorf getStartColor()
    {
        return ( startColor );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void drawDropShadow( int widgetRight, int widgetBottom, int widgetWidth, int widgetHeight, int zIndex, Texture2DCanvas texCanvas )
    {
        TextureImage2D image = texCanvas.getImage();
        
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        int shadowWidth = getDropShadowWidth();
        int shadowHeight = getDropShadowHeight();
        
        int bufferSize = ( widgetWidth + getDropShadowWidth() ) * 4;
        if ( ( pixelLine == null ) || ( pixelLine.length < bufferSize ) )
        {
            pixelLine = new byte[ bufferSize ];
        }
        
        Colorf startColor = getStartColor();
        byte r0 = startColor.getRedByte();
        byte g0 = startColor.getGreenByte();
        byte b0 = startColor.getBlueByte();
        int a0 = startColor.getAlphaInt();
        
        // upper-right shadow corner
        
        for ( int j = 0; j < shadowWidth; j++ )
        {
            for ( int i = 0; i < shadowWidth; i++ )
            {
                int dist = Math.min( (int)FastMath.sqrt( i * i + ( shadowWidth - j ) * ( shadowWidth - j ) ), shadowWidth );
                
                pixelLine[i * 4 + 0] = r0;
                pixelLine[i * 4 + 1] = g0;
                pixelLine[i * 4 + 2] = b0;
                pixelLine[i * 4 + 3] = (byte)( ( shadowWidth - dist ) * ( 255 - a0 ) / shadowWidth );
            }
            
            image.drawPixelLine( pixelLine, 4, widgetRight, widgetBottom - widgetHeight + offsetY + j, shadowWidth );
        }
        
        // right shadow
        
        for ( int j = offsetY + shadowWidth; j < widgetHeight; j++ )
        {
            for ( int i = 0; i < shadowWidth; i++ )
            {
                pixelLine[i * 4 + 0] = r0;
                pixelLine[i * 4 + 1] = g0;
                pixelLine[i * 4 + 2] = b0;
                pixelLine[i * 4 + 3] = (byte)( ( shadowWidth - i ) * ( 255 - a0 ) / shadowWidth );
            }
            
            image.drawPixelLine( pixelLine, 4, widgetRight, widgetBottom - widgetHeight + j, shadowWidth );
        }
        
        // lower-left shadow corner
        
        for ( int j = 0; j < shadowHeight; j++ )
        {
            for ( int i = 0; i < shadowHeight; i++ )
            {
                int dist = Math.min( (int)FastMath.sqrt( ( shadowHeight - i ) * ( shadowHeight - i ) + j * j ), shadowHeight );
                
                pixelLine[i * 4 + 0] = r0;
                pixelLine[i * 4 + 1] = g0;
                pixelLine[i * 4 + 2] = b0;
                pixelLine[i * 4 + 3] = (byte)( ( shadowHeight - dist ) * ( 255 - a0 ) / shadowHeight );
            }
            
            image.drawPixelLine( pixelLine, 4, widgetRight - widgetWidth + offsetX, widgetBottom + j, shadowHeight );
        }
        
        // bottom shadow
        
        for ( int j = 0; j < shadowHeight; j++ )
        {
            for ( int i = 0; i < widgetWidth - offsetX - shadowHeight; i++ )
            {
                pixelLine[i * 4 + 0] = r0;
                pixelLine[i * 4 + 1] = g0;
                pixelLine[i * 4 + 2] = b0;
                pixelLine[i * 4 + 3] = (byte)( ( shadowHeight - j ) * ( 255 - a0 ) / shadowWidth );
            }
            
            image.drawPixelLine( pixelLine, 4, widgetRight - widgetWidth + offsetX + shadowHeight, widgetBottom + j, widgetWidth - offsetX - shadowHeight );
        }
        
        // lower-right shadow
        
        int m = (int)( ( shadowWidth + shadowHeight ) / 2f );
        
        for ( int j = 0; j < shadowHeight; j++ )
        {
            for ( int i = 0; i < shadowWidth; i++ )
            {
                int dist = Math.min( (int)FastMath.sqrt( i * i + j * j ), m );
                
                pixelLine[i * 4 + 0] = r0;
                pixelLine[i * 4 + 1] = g0;
                pixelLine[i * 4 + 2] = b0;
                pixelLine[i * 4 + 3] = (byte)( ( m - dist ) * ( 255 - a0 ) / m );
            }
            
            image.drawPixelLine( pixelLine, 4, widgetRight, widgetBottom + j, shadowWidth );
        }
    }
    
    public DefaultDropShadowFactory( int width, int height )
    {
        super( width, height );
    }
    
    public DefaultDropShadowFactory()
    {
        this( 20, 20 );
    }
}
