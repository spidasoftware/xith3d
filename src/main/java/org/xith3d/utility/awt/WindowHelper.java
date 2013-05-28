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
package org.xith3d.utility.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * This class may help you to adjust your Window (Frame, JFrame, etc.).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public final class WindowHelper
{
    /**
     * @return the coordinates for the upper-left corner of you window
     * so that it is centered
     */
    public static Point getCenterCoordinates( Dimension d )
    {
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        
        Point ul = new Point();
        ul.x = ( s.width - d.width ) / 2;
        ul.y = ( s.height - d.height ) / 2;
        
        return ( ul );
    }
    
    /**
     * Centers the given window on the screen
     * 
     * @param f the window to be centered
     */
    public static void center( Component f )
    {
        f.setLocation( getCenterCoordinates( f.getSize() ) );
    }
    
    /**
     * Centers the given window on the screen
     * 
     * @param f the window to be centered
     * @param screen the screen to center on
     */
    public static void center( Frame f, GraphicsConfiguration screen )
    {
        Point p = getCenterCoordinates( f.getSize() );
        
        p.translate( screen.getBounds().x, screen.getBounds().y );
        
        f.setLocation( p );
    }
    
    private WindowHelper()
    {
    }
}
