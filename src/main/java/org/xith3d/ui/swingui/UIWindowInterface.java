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

import java.awt.Cursor;
import java.awt.Rectangle;

/**
 * Interface used to define overlays that act as windows.  This is an extension of the
 * overlay interface.
 * <p>
 * Originally Coded by David Yazel on Oct 26, 2003 at 11:24:19 AM.
 */
public interface UIWindowInterface extends UIOverlayInterface
{
    /**
     * Called by the window manager when a window is given focus.  It is up to the window
     * to determine how best to indicate that it has focus.
     * 
     * @param focus
     */
    void setFocus( boolean focus );
    
    /**
     * @return the location of the window which can be used to drag the window.  Often this is
     * a rectangle in the upper left hand corner of the window.
     */
    Rectangle getWindowDragHotspot();
    
    /**
     * @return the location of the window which can be used to close the window.  Often this is
     * a rectangle in the upper right hand corner of the window.
     */
    Rectangle getWindowCloseHotspot();
    
    /**
     * @return the location of the window which can be used to close the window.  Often this is
     * a rectangle in the lower right hand corner of the window.
     */
    Rectangle getWindowResizeHotspot();
    
    /**
     * @return the current cursor for the window.
     */
    Cursor getCursor();
}
