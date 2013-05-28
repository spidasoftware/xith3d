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
package org.xith3d.ui.hud.listeners;

import org.xith3d.ui.hud.base.Widget;

/**
 * A WidgetLocationListener is notified of any Widget location change of the
 * assotiated Widget.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface WidgetLocationListener
{
    /**
     * This event is fired when a Widget is started to be dragged.
     * 
     * @param widget the Widget, which is dragged
     */
    public void onWidgetDragStarted( Widget widget );
    
    /**
     * This event is fired when a Widget is stopped to be dragged.
     * 
     * @param widget the Widget, which was dragged
     */
    public void onWidgetDragStopped( Widget widget );
    
    /**
     * This event is fired when a Widget's location has changed.
     * 
     * @param widget the Widget, which's location has changed
     * @param oldLeft the old left coordinate of the Widget
     * @param oldTop the old top coordinate of the Widget
     * @param newLeft the new left coordinate of the Widget
     * @param newTop the new top coordinate of the Widget
     */
    public void onWidgetLocationChanged( Widget widget, float oldLeft, float oldTop, float newLeft, float newTop );
}
