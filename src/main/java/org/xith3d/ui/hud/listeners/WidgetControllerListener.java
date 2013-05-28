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

import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.xith3d.ui.hud.base.Widget;

/**
 * A WidgetControllerListener is notified of Widget-Controller interaction events.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface WidgetControllerListener
{
    /**
     * This event is fired when a ControllerButton has been pressed
     * and this Widget is the currently focussed one.
     * 
     * @param widget the Widget for which this event was fired
     * @param button the pressed button
     * @param when the gameTime of the event
     */
    public void onControllerButtonPressed( Widget widget, ControllerButton button, long when );
    
    /**
     * This event is fired when a ControllerButton has been released
     * and this Widget is the currently focussed one.
     * 
     * @param widget the Widget for which this event was fired
     * @param button the released button
     * @param when the gameTime of the event
     */
    public void onControllerButtonReleased( Widget widget, ControllerButton button, long when );
    
    /**
     * This event is fired when a ControllerAxis has changed
     * and this Widget is the currently focussed one.
     * 
     * @param widget the Widget for which this event was fired
     * @param axis the changed axis
     * @param axisDelta
     * @param when the gameTime of the event
     */
    public void onControllerAxisChanged( Widget widget, ControllerAxis axis, float axisDelta, long when );
    
    /**
     * This event is fired when the state of any DeviceComponent has changed.
     * 
     * @param widget the Widget for which this event was fired
     * @param comp
     * @param delta
     * @param state
     * @param when the gameTime of the event
     * @param isTopMost
     * @param hasFocus
     */
    public void onInputStateChanged( Widget widget, DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus );
}
