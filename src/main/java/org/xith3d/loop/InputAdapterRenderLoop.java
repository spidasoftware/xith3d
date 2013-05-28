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
package org.xith3d.loop;

import org.jagatoo.input.InputSystem;
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
import org.jagatoo.input.managers.SimpleInputActionListener;
import org.jagatoo.input.managers.SimpleInputActionManager;

import org.xith3d.base.Xith3DEnvironment;

/**
 * This loop renders the scene in the same or a separate Thread.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class InputAdapterRenderLoop extends RenderLoop implements InputListener, InputStateListener, SimpleInputActionListener
{
    /**
     * {@inheritDoc}
     */
    public void onKeyPressed( KeyPressedEvent e, Key key ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onKeyReleased( KeyReleasedEvent e, Key key ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onKeyStateChanged( KeyStateEvent e, Key key, boolean state ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onKeyTyped( KeyTypedEvent e, char keyChar ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonPressed( MouseButtonPressedEvent e, MouseButton button ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonReleased( MouseButtonReleasedEvent e, MouseButton button ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonClicked( MouseButtonClickedEvent e, MouseButton button, int clickCount ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonStateChanged( MouseButtonEvent e, MouseButton button, boolean state ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onMouseMoved( MouseMovedEvent e, int x, int y, int dx, int dy ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onMouseWheelMoved( MouseWheelEvent e, int wheelDelta ) {}
    
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
    public void onMouseStopped( MouseStoppedEvent e, int x, int y ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onControllerAxisChanged( ControllerAxisChangedEvent e, ControllerAxis axis, float axisDelta ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onControllerButtonPressed( ControllerButtonPressedEvent e, ControllerButton button ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onControllerButtonReleased( ControllerButtonReleasedEvent e, ControllerButton button ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onControllerButtonStateChanged( ControllerButtonEvent e, ControllerButton button, boolean state ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onInputStateChanged( InputEvent e, DeviceComponent comp, int delta, int state ) {}
    
    /**
     * {@inheritDoc}
     */
    public void onActionInvoked( Object action, int delta, int state ) {}
    
    /**
     * Creates a new instance.
     * 
     * @param x3dEnv the {@link Xith3DEnvironment} to be linked with this RenderLoop.
     * @param maxFPS the maximum FPS to render at
     */
    public InputAdapterRenderLoop( Xith3DEnvironment x3dEnv, float maxFPS )
    {
        super( x3dEnv, maxFPS );
        
        InputSystem.getInstance().addInputListener( this );
        InputSystem.getInstance().addInputStateListener( this );
        SimpleInputActionManager.getInstance().addActionListener( this );
    }
    
    /**
     * Creates a new instance.
     * 
     * @param maxFPS the maximum FPS to render at
     */
    public InputAdapterRenderLoop( float maxFPS )
    {
        this( null, maxFPS );
    }
    
    /**
     * Creates a new instance.
     * 
     * @param x3dEnv the {@link Xith3DEnvironment} to be linked with this RenderLoop.
     */
    public InputAdapterRenderLoop( Xith3DEnvironment x3dEnv )
    {
        this( x3dEnv, Float.MAX_VALUE );
    }
    
    /**
     * Creates a new instance.
     */
    public InputAdapterRenderLoop()
    {
        this( Float.MAX_VALUE );
    }
    
    @Override
    protected void destroy()
    {
        super.destroy();
        
        if ( InputSystem.hasInstance() )
        {
            InputSystem.getInstance().removeInputListener( this );
            InputSystem.getInstance().removeInputStateListener( this );
            SimpleInputActionManager.getInstance().removeActionListener( this );
        }
    }
}
