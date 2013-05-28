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
package org.xith3d.ui.swingui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.jagatoo.image.DirectBufferedImage;
import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFilter;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.utility.logging.X3DLog;

/**
 * A SubOverlay is one of the pieces which displays a portion of the
 * overlay.  This is used internally by UIOverlay and should not be referenced
 * directly.
 * <p>
 * Originally Coded by David Yazel on Oct 4, 2003 at 10:02:32 PM.
 * 
 * @author David Yazel
 */
public class UISubOverlay extends UISubOverlayBase
{
    private BufferedImage a; // buffered image used for icA
    private BufferedImage b; // buffered image used for icB
    private TextureImage2D icA; // one of the double buffers
    private TextureImage2D icB; // the other one of the double buffers
    private Texture2D tex; // texture mapped to icA or icB
    private boolean frontBuffer; // true if icA is the current texture, false if its icB
    
    //private int[] transferBuffer; // used for transferring scan lines from main image to sub-image
    //private int[] compareBuffer; // used for transferring scan lines from main image to sub-image
    
    protected void updateBackBufferByDraw( BufferedImage fullImage )
    {
        BufferedImage backBuffer;
        
        if ( frontBuffer )
        {
            backBuffer = b;
        }
        else
        {
            backBuffer = a;
        }
        
        dirty = true;
        
        Graphics g = backBuffer.getGraphics();
        g.drawImage( fullImage, 0, 0, width, height, lx, overlay.height - ly, ux + 1, overlay.height - uy - 1, null );
    }
    
    /**
     * Transfers the scan lines from the full image to the sub-image. The
     * scan lines are applied in reverse order so that it is compatible with
     * that silly Y-up thing (grrr).
     */
    public void updateBackBuffer( DirectBufferedImage fullImage )
    {
        BufferedImage backBuffer;
        //BufferedImage otherBuffer;
        
        if ( frontBuffer )
        {
            backBuffer = b;
            //otherBuffer = a;
        }
        else
        {
            backBuffer = a;
            //otherBuffer = b;
        }
        
        dirty = true;
        
        int w = fullImage.getWidth();
        
        ByteBuffer fullBytes = fullImage.getByteBuffer();
        ByteBuffer backBytes = ( (DirectBufferedImage)backBuffer ).getByteBuffer();
        final int size = fullImage.getDirectType() == DirectBufferedImage.Type.DIRECT_RGBA ? 4 : 3;
        
        byte[] buffer = new byte[ width * size ];
        
        for ( int i = 0; i < height; i++ )
        {
            int y = ( overlay.height - uy + i ) - 1;
            int yy = texHeight - i - 1 - ( texHeight - height );
            
            //System.arraycopy( fullBytes, ( w * y + lx ) * size, backBytes, yy * texWidth * size, width * size );
            
            fullBytes.get( buffer, ( w * y + lx ) * size, width * size );
            backBytes.put( buffer, yy * texWidth * size, width * size );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void swap()
    {
        if ( frontBuffer )
        {
            tex.setImage( 0, icB );
        }
        else
        {
            tex.setImage( 0, icA );
        }
        
        frontBuffer = !frontBuffer;
        dirty = false;
    }
    
    /**
     * Creates the sub overlay for the specified region.
     */
    protected UISubOverlay( UIOverlay overlay, int lx, int ly, int ux, int uy )
    {
        super( overlay, lx, ly, ux, uy );
        
        // create the two buffers
        boolean hasAlpha = ( overlay.getBlendAlpha() || overlay.getClipAlpha() );
        
        if ( hasAlpha )
        {
            a = DirectBufferedImage.makeDirectImageRGBA( texWidth, texHeight );
            b = DirectBufferedImage.makeDirectImageRGBA( texWidth, texHeight );
        }
        else
        {
            a = DirectBufferedImage.makeDirectImageRGB( texWidth, texHeight );
            b = DirectBufferedImage.makeDirectImageRGB( texWidth, texHeight );
        }
        
        // determine a compatible by-ref image type
        TextureImageFormat tiFormat;
        
        if ( hasAlpha )
        {
            tiFormat = TextureImageFormat.RGBA;
        }
        else
        {
            tiFormat = TextureImageFormat.RGB;
        }
        
        // create two by-reference image components
        icA = new TextureImage2D( tiFormat, a, true );
        icB = new TextureImage2D( tiFormat, b, true );
        
        // create a transfer buffer for one scan line
        //transferBuffer = new int[ texWidth ];
        //compareBuffer = new int[ texWidth ];
        
        X3DLog.debug( "Sub-overlay : ", width, ",", height, " -> ", texWidth, ",", texHeight, " : ", lx, "," + ly, " - ", ux, ",", uy );
        
        tex = new Texture2D( hasAlpha ? TextureFormat.RGBA : TextureFormat.RGB );
        
        //tex.setBoundaryModes( TextureBoundaryMode.CLAMP, TextureBoundaryMode.CLAMP );
        tex.setBoundaryModes( TextureBoundaryMode.CLAMP_TO_EDGE, TextureBoundaryMode.CLAMP_TO_EDGE );
        
        tex.setFilter( TextureFilter.POINT );
        
        tex.setImage( 0, icA );
        
        if ( !GRID )
        {
            ap.setTexture( tex );
        }
        
        frontBuffer = true;
    }
}
