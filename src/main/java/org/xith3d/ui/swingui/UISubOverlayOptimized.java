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

import java.awt.AlphaComposite;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFilter;
import org.jagatoo.opengl.enums.TextureFormat;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
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
public class UISubOverlayOptimized extends UISubOverlayBase
{
    private final Texture2D tex; // texture mapped to icA or icB
    
    /**
     * Creates the sub overlay for the specified region.
     */
    protected UISubOverlayOptimized( UIOverlay overlay, int lx, int ly, int ux, int uy )
    {
        super( overlay, lx, ly, ux, uy );
        
        X3DLog.debug( "Sub-overlay : ", width, ",", height, " -> ", texWidth, ",", texHeight, " : ", lx, ",", ly, " - ", ux, ",", uy );
        
        // create the two buffers
        boolean hasAlpha = ( overlay.getBlendAlpha() || overlay.getClipAlpha() );
        
        this.tex = Texture2D.createDrawTexture( hasAlpha ? TextureFormat.RGBA : TextureFormat.RGB, texWidth, texHeight, true, true );
        //ti = tex.getImage0();
        
        //tex.setBoundaryModes( TextureBoundaryMode.CLAMP, TextureBoundaryMode.CLAMP );
        tex.setBoundaryModes( TextureBoundaryMode.CLAMP_TO_EDGE, TextureBoundaryMode.CLAMP_TO_EDGE );
        
        tex.setFilter( TextureFilter.POINT );
        
        if ( !GRID )
        {
            ap.setTexture( tex );
        }
    }
    
    protected void update( BufferedImage fullImage, Rectangle updateRect )
    {
        dirty = true;
        Rectangle r = updateRect.intersection( bounds );
        Texture2DCanvas tc = tex.getTextureCanvas();
        
        int destLX = r.x - bounds.x;
        int destLY = height - ( r.y - bounds.y ) - r.height;
        //int destLY = r.y - bounds.y;
        int destUX = destLX + r.width;
        int destUY = destLY + r.height;
        
        // updated to move the rectangle we're copying from up one pixel 
        r.y -= 1;
        
        int srcLX = r.x;
        int srcUY = r.y;
        int srcUX = r.x + r.width;
        int srcLY = r.y + r.height;
        
        tc.setComposite( AlphaComposite.Src );
        //g2.setColor( new Color( 0, 0, 0, 0 ) );
        //g.fillRect( destLX, destLY, r.width - 1, r.height - 1 );
        tc.drawImage( fullImage, destLX, destLY, destUX, destUY, srcLX, srcLY, srcUX, srcUY );
        //g.drawImage( fullImage, 0, 0, width, height, lx, overlay.height - ly, ux + 1, overlay.height - uy - 1, null );
        //g.setColor( Color.red );
        //g.drawRect( destLX, destLY, r.width - 1, r.height - 1 );
        //System.out.println( "marking update " + r + " for bounds " + bounds );
        //ic.update( new Rect2i( destLX, destLY, r.width, r.height ) );
        //tex.setHasUpdateList( true );
        
        //ic.update( new Rectangle( destLX, destLY, r.width, r.height ) );
    }
    
    /**
     * Swaps the buffers
     */
    @Override
    public void swap()
    {
        dirty = false;
    }
}
