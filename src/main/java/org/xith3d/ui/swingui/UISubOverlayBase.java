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

import java.awt.Rectangle;

import org.jagatoo.opengl.enums.DrawMode;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.QuadArray;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.utility.logging.X3DLog;

/**
 * Insert package comments here
 * <p/>
 * Originally Coded by David Yazel on Nov 23, 2003 at 11:58:22 AM.
 *
 * @author David Yazel
 */
public class UISubOverlayBase
{
    protected static final Boolean DEBUG = null;
    protected static final boolean GRID = false;
    
    protected final int lx; // which part of the parent image does this represent
    protected final int ly; // which part of the parent image does this represent
    protected final int ux; // which part of the parent image does this represent
    protected final int uy; // which part of the parent image does this represent
    protected final int texWidth; // the width of the texture (power of 2)
    protected final int texHeight; // the height of the texture (power of 2)
    protected final int width; // width of the sub-overlay
    protected final int height; // height of the sub-overlay
    
    protected Shape3D shape; // textured quad used to hold geometry
    protected Appearance ap; // appearance for this sub-overlay
    protected UIOverlay overlay; // the owner of this sub-overlay
    protected Rectangle bounds;
    protected boolean dirty;
    
    protected Rectangle getBounds()
    {
        return ( bounds );
    }
    
    /**
     * Simple function to return the smallest power of 2 which
     * the value can be contained within
     */
    protected static int smallestPower( int value )
    {
        int n = 2;
        
        while ( n < value )
            n *= 2;
        
        return ( n );
    }
    
    /**
     * Build the quad for this overlay
     */
    protected void buildShape()
    {
        // now we need to calculate the tex coordinates for the upper bounds of
        // the sub-overlay.  This is because we probably had to build the texture
        // bigger than the actual sub-overlay
        float texX = (float)( width ) / (float)texWidth;
        float texY = (float)( height ) / (float)texHeight;
        
        float texLX = 0.0f;
        float texLY = 0.0f;
        
        /*
        float texX = ((float)width - 0.5f) / (float)texWidth;
        float texY = ((float)height - 0.5f) / (float)texHeight;
        float texLX = 0.5f / (float)texWidth;
        float texLY = 0.5f / (float)texHeight;
        */
        X3DLog.debug( "  sub overlay texX=", texX, ", texY=", texY );
        
        float highOffset = 1f;
        float lowOffset = 0f;
        
        float[] verts =
        {
            ux + highOffset, ly + lowOffset, 0.0f, ux + highOffset, uy + highOffset, 0.0f, lx + lowOffset, uy + highOffset, 0.0f, lx + lowOffset, ly + lowOffset, 0.0f
        };
        
        /*
        float[] colors =
        {
            0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f
        };
        */

        /*
        float[] tex =
        {
            texX, texY,
            texX, texLY,
            texLX, texLY,
            texLX, texY};
        */
        float[] tex =
        {
            texX, texLY,
            texX, texY,
            texLX, texY,
            texLX, texLY
        };
        
        QuadArray planeGeom = new QuadArray( 4 );
        
        planeGeom.setCoordinates( 0, verts );
        planeGeom.setTextureCoordinates( 0, 0, 2, tex );
        
        // assign the appearance
        ap = new Appearance();
        ap.setPolygonAttributes( overlay.getPolygonAttributes() );
        ap.setRenderingAttributes( overlay.getRenderingAttributes() );
        
        if ( GRID )
        {
            ap.getPolygonAttributes().setDrawMode( DrawMode.LINE );
        }
        else
        {
            ap.setTextureAttributes( overlay.getTextureAttributes() );
            ap.setTransparencyAttributes( overlay.getTransparencyAttributes() );
        }
        
        Material m = new Material();
        m.setLightingEnabled( false );
        ap.setMaterial( m );
        
        // create the Shape with the Geometry and Appearance
        shape = new Shape3D( planeGeom, ap );
        shape.setName( "Window" );
    }
    
    /**
     * Return the shape
     */
    public Shape3D getShape()
    {
        return ( shape );
    }
    
    public void swap()
    {
    }
    
    protected UISubOverlayBase( UIOverlay overlay, int lx, int ly, int ux, int uy )
    {
        this.overlay = overlay;
        
        // save the screen location
        this.lx = lx;
        this.ly = ly;
        this.ux = ux;
        this.uy = uy;
        
        // caclulate the sub overlay size
        width = ux - lx + 1;
        height = uy - ly + 1;
        
        bounds = new Rectangle( lx, overlay.height - uy, width, height );
        
        // now we need to calculate the texture size needed to contain this
        // size sub-overlay.  Find the smallest power of two that works
        texWidth = smallestPower( width );
        texHeight = smallestPower( height );
        
        // create the shape
        buildShape();
    }
}
