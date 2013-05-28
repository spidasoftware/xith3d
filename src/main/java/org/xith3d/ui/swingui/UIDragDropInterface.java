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

import java.awt.event.MouseEvent;

/**
 * Insert package comments here
 * 
 * Originally Coded by David Yazel on Oct 4, 2003 at 11:55:20 PM.
 */
public interface UIDragDropInterface
{
    /**
     * Called when the overlay manager starts a drag action.  If null is returned
     * then the dragging action is cancelled.  otherwise the icon specified
     * in the DraggingInformation will be moved around the screen until the
     * mouse is released.
     * 
     * @param mouseEvent
     * 
     * @return the UIDraggingInformation
     */
    UIDraggingInformation startDrag( MouseEvent mouseEvent );
    
    /**
     * Once a legitimate dragging operation is started then windows which implement
     * this interface will get this callback as the icon is moved over their surface.
     * 
     * @param mouseEvent
     * 
     * @param info
     */
    boolean dragging( MouseEvent mouseEvent, UIDraggingInformation info );
    
    /**
     * When the mouse is released the dragging operation will stop.  If there is
     * a window or an object underneath the mouse and it implements this
     * interface then this method will be called. Returning false means that
     * the target is rejecting the drop.
     * 
     * @param mouseEvent
     * 
     * @param info
     */
    boolean dropped( MouseEvent mouseEvent, UIDraggingInformation info );
}
