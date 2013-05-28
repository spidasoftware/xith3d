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
 * A WidgetMouseListener is notified of Widget-Mouse interaction events.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface WidgetMouseListener
{
    /**
     * This event is fired, when a mouse button is pressed on a focused Widget.
     * 
     * @param widget the Widget for which this event was fired
     * @param button the button that was pressed
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param when the gameTime of the mouse event
     * @param lastWhen the milli-time when the button has last been pressed
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    public void onMouseButtonPressed( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus );
    
    /**
     * This event is fired, when a mouse button is released on a focused Widget.
     * 
     * @param widget the Widget for which this event was fired
     * @param button the button that was released
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param when the gameTime of the mouse event
     * @param lastWhen the milli-time when the button has last been released
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    public void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus );
    
    /**
     * This event is fired, when the mouse is moved on a Widget.
     * 
     * @param widget the Widget for which this event was fired
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param buttonsState the buttons' state bitmask
     * @param when the gameTime of the mouse event
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    public void onMouseMoved( Widget widget, float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus );
    
    /**
     * This event is fired, when the mouse position has not been changed on this
     * Widget for a certain amount of time.
     * 
     * @param widget the Widget for which this event was fired
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param when the gameTime of the mouse event
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    public void onMouseStopped( Widget widget, float x, float y, long when, boolean isTopMost, boolean hasFocus );
    
    /**
     * This event is fired, when the mouse wheel is moved on a Widget.
     * 
     * @param widget the Widget for which this event was fired
     * @param delta a positive value when the wheel was moved up
     * @param isPageMove true, if whole pages are to be scrolled with this wheel move
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param when the gameTime of the mouse event
     * @param isTopMost is this Widget topMost
     */
    public void onMouseWheelMoved( Widget widget, int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost );
    
    /**
     * This method is called when the mouse entered the Widget area.
     * 
     * @param widget the Widget for which this event was fired
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    public void onMouseEntered( Widget widget, boolean isTopMost, boolean hasFocus );
    
    /**
     * This method is called when the mouse exited the Widget area.
     * 
     * @param widget the Widget for which this event was fired
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    public void onMouseExited( Widget widget, boolean isTopMost, boolean hasFocus );
    
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
