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

import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.MouseButton;
import org.xith3d.ui.hud.base.Widget;

/**
 * If a Widget makes use of other Widgets to be built and these Widgets produce
 * events, that are to be catched be the Widget only, then you should create an
 * inner class and let it extend this class to catch them, since it already
 * implements all known Widget-Listeners (with empty method stubs).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class WidgetMouseAdapter implements WidgetMouseListener
{
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonPressed( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseMoved( Widget widget, float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseStopped( Widget widget, float x, float y, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseWheelMoved( Widget widget, int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseEntered( Widget widget, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseExited( Widget widget, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onInputStateChanged( Widget widget, DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
    }
}
