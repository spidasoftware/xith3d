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
package org.xith3d.ui.hud;

import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.events.ControllerAxisChangedEvent;
import org.jagatoo.input.events.ControllerButtonEvent;
import org.jagatoo.input.events.ControllerButtonPressedEvent;
import org.jagatoo.input.events.ControllerButtonReleasedEvent;
import org.jagatoo.input.events.InputEvent;
import org.jagatoo.input.events.KeyPressedEvent;
import org.jagatoo.input.events.KeyReleasedEvent;
import org.jagatoo.input.events.KeyStateEvent;
import org.jagatoo.input.events.KeyTypedEvent;
import org.jagatoo.input.events.MouseButtonClickedEvent;
import org.jagatoo.input.events.MouseButtonEvent;
import org.jagatoo.input.events.MouseButtonPressedEvent;
import org.jagatoo.input.events.MouseButtonReleasedEvent;
import org.jagatoo.input.events.MouseMovedEvent;
import org.jagatoo.input.events.MouseStoppedEvent;
import org.jagatoo.input.events.MouseWheelEvent;
import org.jagatoo.input.listeners.InputListener;
import org.jagatoo.input.listeners.InputStateListener;

/**
 * This class simply listens for events from the InputSystem
 * and forwards the to the HUD.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
class HUDInputSystemConnection implements InputListener, InputStateListener
{
    private static final int MOUSE_Y_CORRECTION = -3; // TODO: This bug needs to get fixed!!!
    
    private final HUD hud;
    
    long nextAcceptedMouseEventTime = -1L;
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonPressed( MouseButtonPressedEvent e, MouseButton button )
    {
        if ( !hud.isVisible() )
            return;
        
        if ( !e.getMouse().isAbsolute() )
            return;
        
        if ( System.currentTimeMillis() < nextAcceptedMouseEventTime )
            return;
        
        hud.onMouseButtonPressed( button, e.getX(), e.getY() + MOUSE_Y_CORRECTION, e.getWhen(), e.getLastWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonReleased( MouseButtonReleasedEvent e, MouseButton button )
    {
        if ( !hud.isVisible() )
            return;
        
        if ( !e.getMouse().isAbsolute() )
            return;
        
        if ( System.currentTimeMillis() < nextAcceptedMouseEventTime )
            return;
        
        hud.onMouseButtonReleased( button, e.getX(), e.getY() + MOUSE_Y_CORRECTION, e.getWhen(), e.getLastWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonClicked( MouseButtonClickedEvent e, MouseButton button, int clickCount )
    {
    }
    
    public void onMouseButtonStateChanged( MouseButtonEvent e, MouseButton button, boolean state )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseMoved( MouseMovedEvent e, int x, int y, int dx, int dy )
    {
        if ( !hud.isVisible() )
            return;
        
        if ( !e.getMouse().isAbsolute() )
            return;
        
        if ( System.currentTimeMillis() < nextAcceptedMouseEventTime )
            return;
        
        hud.onMouseMoved( x, y + MOUSE_Y_CORRECTION, e.getMouse().getButtonsState(), e.getWhen(), e.getLastWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public long getMouseStopDelay()
    {
        return ( 500000000L );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseStopped( MouseStoppedEvent e, int x, int y )
    {
        if ( !hud.isVisible() )
            return;
        
        if ( !e.getMouse().isAbsolute() )
            return;
        
        if ( System.currentTimeMillis() < nextAcceptedMouseEventTime )
            return;
        
        hud.onMouseStopped( x, y + MOUSE_Y_CORRECTION, e.getMouse().getButtonsState(), e.getWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseWheelMoved( MouseWheelEvent e, int wheelDelta )
    {
        if ( !hud.isVisible() )
            return;
        
        if ( !e.getMouse().isAbsolute() )
            return;
        
        if ( System.currentTimeMillis() < nextAcceptedMouseEventTime )
            return;
        
        hud.onMouseWheelMoved( wheelDelta, e.getMouse().getCurrentX(), e.getMouse().getCurrentY() + MOUSE_Y_CORRECTION, e.getWhen(), e.isPageMove() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onKeyPressed( KeyPressedEvent e, Key key )
    {
        if ( !hud.isVisible() )
            return;
        
        hud.onKeyPressed( key, e.getModifierMask(), e.getWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onKeyReleased( KeyReleasedEvent e, Key key )
    {
        if ( !hud.isVisible() )
            return;
        
        hud.onKeyReleased( key, e.getModifierMask(), e.getWhen() );
    }
    
    public void onKeyStateChanged( KeyStateEvent e, Key key, boolean state )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onKeyTyped( KeyTypedEvent e, char keyChar )
    {
        if ( !hud.isVisible() )
            return;
        
        hud.onKeyTyped( keyChar, e.getModifierMask(), e.getWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onControllerButtonPressed( ControllerButtonPressedEvent e, ControllerButton button )
    {
        if ( !hud.isVisible() )
            return;
        
        hud.onControllerButtonPressed( button, e.getWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onControllerButtonReleased( ControllerButtonReleasedEvent e, ControllerButton button )
    {
        if ( !hud.isVisible() )
            return;
        
        hud.onControllerButtonReleased( button, e.getWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onControllerButtonStateChanged( ControllerButtonEvent e, ControllerButton button, boolean state )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onControllerAxisChanged( ControllerAxisChangedEvent e, ControllerAxis axis, float axisDelta )
    {
        if ( !hud.isVisible() )
            return;
        
        hud.onControllerAxisChanged( axis, axisDelta, e.getWhen() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onInputStateChanged( InputEvent e, DeviceComponent comp, int delta, int state )
    {
        if ( !hud.isVisible() )
            return;
        
        if ( ( e.getType() == InputEvent.Type.MOUSE_EVENT ) && ( System.currentTimeMillis() < nextAcceptedMouseEventTime ) )
            return;
        
        hud.onInputStateChanged( comp, delta, state, e.getWhen() );
    }
    
    HUDInputSystemConnection( HUD hud )
    {
        this.hud = hud;
    }
}
