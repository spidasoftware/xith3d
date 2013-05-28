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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.openmali.vecmath2.Vector3f;

import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * Insert package comments here
 * <p>
 * Originally Coded by David Yazel on Oct 4, 2003 at 10:51:03 PM.
 */
public class UIPositionedWindow extends Group
{
    private Group root = null; // root of the overlay
    private TransformGroup posTG = null; // position transform group for the overlay in screen coordinates
    private UIOverlayInterface data = null;
    private Rectangle bounds = null;
    
    private int posX = -1;
    private int posY = -1;
    private boolean draggable = false;
    
    /**
     * This method builds the root and the transform group for controlling
     * its position.  This is called the first time we attach an overlay to the
     * window.
     */
    private void buildRoot()
    {
        root = new Group();
        
        addChild( root );
        
        // build the position transform group used to move the
        // overlay to a location on the screen
        posTG = new TransformGroup();
        
        root.addChild( posTG );
    }
    
    /**
     * Get the bounds
     */
    public Rectangle getRectangle()
    {
        return ( bounds );
    }
    
    /**
     * @return true if the point passed in is contained within the
     * bounds of the window.
     */
    public boolean contains( Point p )
    {
        if ( bounds == null )
        {
            return ( false );
        }
        
        return ( bounds.contains( p ) );
    }
    
    /**
     * Sets the position of the window using the position transformation
     */
    public void setPosition( int x, int y, Dimension canvasDim )
    {
        posX = x;
        posY = y;
        
        if ( root != null )
        {
            Dimension d = new Dimension();
            data.getSize( d );
            
            Transform3D t = new Transform3D();
            t.setTranslation( new Vector3f( x, (float)canvasDim.height - y - d.height, 0 ) );
            posTG.setTransform( t );
            bounds = new Rectangle( new Point( x, y ), d );
        }
    }
    
    public void setDraggable( boolean b )
    {
        draggable = b;
    }
    
    public boolean isDraggable()
    {
        return ( draggable );
    }
    
    public int getX()
    {
        return ( posX );
    }
    
    public int getY()
    {
        return ( posY );
    }
    
    /**
     * Used to free the overlay from the overlay window.
     */
    public void empty()
    {
        if ( root != null )
        {
            if ( root.isLive() )
            {
                root.detach();
            }
            
            root.detach();
            root = null;
            posTG = null;
            data = null;
            bounds = null;
        }
    }
    
    /**
     * @return true of the ovwelay window is assigned to an overlay
     */
    public boolean isAssigned()
    {
        return ( root != null );
    }
    
    /**
     * This attaches an overlay to this window.  If there is already one assigned
     * then it will be released.
     */
    public void assign( UIOverlayInterface overlay )
    {
        if ( root != null )
        {
            empty();
        }
        
        buildRoot();
        posTG.addChild( overlay.getRoot() );
        data = overlay;
    }
    
    /**
     * @return true if the overlay is visible
     */
    public boolean isVisible()
    {
        if ( root == null )
            return ( false );
        
        if ( root.getParent() == null )
            return ( false );
        
        //if ( root.isLive() )
        //    return ( true );
        
        return ( true );
    }
    
    /**
     * @return the assigned overlay
     */
    public UIOverlayInterface getOverlay()
    {
        return ( data );
    }
    
    /**
     * Sets the overlay visible if the specified value is true
     */
    public void setVisible( boolean show )
    {
        if ( root != null )
        {
            if ( show && !isVisible() )
            {
                addChild( root );
            }
            else if ( !show && isVisible() )
            {
                root.detach();
            }
        }
        
        //Log.log.println( LogType.EXHAUSTIVE, "Set overlay visible : " + show );
    }
    
    public UIPositionedWindow()
    {
    }
}
