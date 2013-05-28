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
import org.jagatoo.input.devices.components.Key;
import org.xith3d.ui.hud.base.Widget;

/**
 * A WidgetKeyboardListener is notified of Widget-Keyboard interaction events.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface WidgetKeyboardListener
{
    /**
     * This event is fired, when a key is pressed on a focused Widget.
     * 
     * @param widget the Widget for which this event was fired
     * @param key the key that was pressed
     * @param modifierMask the mask of modifier keys
     * @param when the gameTime of the key event
     */
    public void onKeyPressed( Widget widget, Key key, int modifierMask, long when );
    
    /**
     * This event is fired, when a key is released on a focused Widget.
     * 
     * @param widget the Widget for which this event was fired
     * @param key the key that was released
     * @param modifierMask the mask of modifier keys
     * @param when the gameTime of the key event
     */
    public void onKeyReleased( Widget widget, Key key, int modifierMask, long when );
    
    /**
     * This event is fired when a key is typed on the keyboard.
     * 
     * @param widget the Widget for which this event was fired
     * @param ch the typed key's character
     * @param modifierMask the mask of modifier keys
     * @param when the gameTime of the key event
     */
    public void onKeyTyped( Widget widget, char ch, int modifierMask, long when );
    
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
