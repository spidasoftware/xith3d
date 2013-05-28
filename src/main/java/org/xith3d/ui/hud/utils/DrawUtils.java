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

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.scenegraph.TextureImage2D;

/**
 * Keeps static methods for Widget drawing.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DrawUtils
{
    /**
     * Renders the given image to the given area and mixes the pixel colors, if the image has an alpha channel.
     * 
     * @param color
     * @param texture
     * @param tileMode
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     */
    public static void drawImage( Colorf color, Texture2D texture, TileMode tileMode, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        final TextureImage2D image = texCanvas.getTexture().getImage0();
        
        if ( color != null )
        {
            image.fillRectangle( color, offsetX, offsetY, width, height );
        }
        
        if ( texture != null )
        {
            TextureImage2D img = texture.getImage0();
            
            if ( tileMode != null )
            {
                int imgWidth = HUDTextureUtils.getTextureWidth( texture );
                int imgHeight = HUDTextureUtils.getTextureHeight( texture );
                
                switch ( tileMode )
                {
                    case STRETCH:
                        if ( ( width == imgWidth ) && ( height == imgHeight ) )
                            image.drawImage( img, offsetX, offsetY );
                        else
                            texCanvas.drawImage( img.getBufferedImage(), offsetX, offsetY + height - 1, offsetX + width - 1, offsetY, 0, 0, imgWidth, imgHeight );
                        break;
                    case TILE_X:
                        image.drawImage( img, 0, 0, imgWidth, imgHeight, offsetX, offsetY, width, imgHeight );
                        break;
                    case TILE_Y:
                        image.drawImage( img, 0, 0, imgWidth, imgHeight, offsetX, offsetY, imgWidth, height );
                        break;
                    case TILE_BOTH:
                        image.drawImage( img, 0, 0, imgWidth, imgHeight, offsetX, offsetY, width, height );
                        break;
                }
            }
            else
            {
                image.drawImage( img, offsetX, offsetY );
            }
        }
    }
    
    /**
     * Clears the given area with the given image.
     * 
     * @param color
     * @param texture
     * @param tileMode
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     */
    public static void clearImage( Colorf color, Texture2D texture, TileMode tileMode, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        final TextureImage2D image = texCanvas.getTexture().getImage0();
        
        if ( color != null )
        {
            image.clear( color, offsetX, offsetY, width, height );
        }
        
        if ( texture != null )
        {
            TextureImage2D img = texture.getImage0();
            
            if ( tileMode != null )
            {
                int imgWidth = HUDTextureUtils.getTextureWidth( texture );
                int imgHeight = HUDTextureUtils.getTextureHeight( texture );
                
                switch ( tileMode )
                {
                    case STRETCH:
                        if ( ( width == imgWidth ) && ( height == imgHeight ) )
                        {
                            image.clear( img, offsetX, offsetY );
                        }
                        else
                        {
                            image.clear( Colorf.BLACK_TRANSPARENT, offsetX, offsetY, width, height );
                            texCanvas.drawImage( img.getBufferedImage(), offsetX, offsetY + height - 1, offsetX + width - 1, offsetY, 0, 0, imgWidth, imgHeight );
                        }
                        break;
                    case TILE_X:
                        image.clear( img, 0, 0, imgWidth, imgHeight, offsetX, offsetY, width, imgHeight );
                        break;
                    case TILE_Y:
                        image.clear( img, 0, 0, imgWidth, imgHeight, offsetX, offsetY, imgWidth, height );
                        break;
                    case TILE_BOTH:
                        image.clear( img, 0, 0, imgWidth, imgHeight, offsetX, offsetY, width, height );
                        break;
                }
            }
            else
            {
                image.clear( img, offsetX, offsetY );
            }
        }
    }
}
