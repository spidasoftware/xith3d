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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.xith3d.scenegraph.Node;

/**
 * Insert package comments here
 * <p/>
 * Originally Coded by David Yazel on Nov 24, 2003 at 12:17:37 PM.
 */
public class UIDraggingWindow implements UIOverlayInterface
{
    private UIDraggingInformation di = null;
    private UIOverlay overlay;
    boolean dirty = false;
    boolean canDrop = false;
    private BufferedImage image = new BufferedImage( 32, 32, BufferedImage.TYPE_INT_ARGB );
    
    public void getSize( Dimension dim )
    {
        dim.setSize( 32, 32 );
    }
    
    public UIDraggingInformation getDraggingInterface()
    {
        return ( di );
    }
    
    public void setDi( UIDraggingInformation di )
    {
        this.di = di;
        this.dirty = true;
    }
    
    public void update()
    {
        if ( dirty )
        {
            if ( di != null )
            {
                Graphics g = image.getGraphics();
                g.drawImage( di.getIconBackground(), 0, 0, null );
                if ( di.getIcon() != null )
                    g.drawImage( di.getIcon(), 0, 0, null );
                if ( canDrop )
                    g.drawImage( di.getIconCanDrop(), 0, 0, null );
                g.dispose();
            }
            else
            {
                Graphics g = image.getGraphics();
                g.setColor( Color.black );
                g.fillRect( 0, 0, 32, 32 );
                g.setColor( Color.yellow );
                g.drawRect( 0, 0, 32, 32 );
            }
            
            overlay.repaint( image );
            dirty = false;
        }
    }
    
    public void setCanDrop( boolean drop )
    {
        canDrop = drop;
        dirty = true;
    }
    
    public Node getRoot()
    {
        return ( overlay.getRoot() );
    }
    
    public boolean isOpaque()
    {
        return ( false );
    }
    
    public UIDraggingWindow()
    {
        this.overlay = new UIOverlay( 32, 32, false, true );
    }
}
