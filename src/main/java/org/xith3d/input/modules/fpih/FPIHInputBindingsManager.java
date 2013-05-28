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
package org.xith3d.input.modules.fpih;

import org.jagatoo.input.devices.components.Keys;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.input.managers.InputBindingsManager;
import org.xith3d.input.FirstPersonInputHandler;

/**
 * A special {@link InputBindingsManager} for the {@link FirstPersonInputHandler}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHInputBindingsManager extends InputBindingsManager< FPIHInputAction >
{
    public static final InputBindingsManager< FPIHInputAction > DEFAULT_BINDINGS = new InputBindingsManager< FPIHInputAction >( FPIHInputAction.values().length );
    static
    {
        DEFAULT_BINDINGS.unbindAll();
        
        DEFAULT_BINDINGS.bind( Keys.W,                   FPIHInputAction.WALK_FORWARD );
        DEFAULT_BINDINGS.bind( Keys.UP,                  FPIHInputAction.WALK_FORWARD );
        DEFAULT_BINDINGS.bind( Keys.S,                   FPIHInputAction.WALK_BACKWARD );
        DEFAULT_BINDINGS.bind( Keys.DOWN,                FPIHInputAction.WALK_BACKWARD );
        DEFAULT_BINDINGS.bind( Keys.A,                   FPIHInputAction.STRAFE_LEFT );
        DEFAULT_BINDINGS.bind( Keys.LEFT,                FPIHInputAction.STRAFE_LEFT );
        DEFAULT_BINDINGS.bind( Keys.D,                   FPIHInputAction.STRAFE_RIGHT );
        DEFAULT_BINDINGS.bind( Keys.RIGHT,               FPIHInputAction.STRAFE_RIGHT );
        DEFAULT_BINDINGS.bind( Keys.LEFT_SHIFT,          FPIHInputAction.JUMP );
        DEFAULT_BINDINGS.bind( Keys.SPACE,               FPIHInputAction.CROUCH );
        DEFAULT_BINDINGS.bind( Keys.NUMPAD_ADD,          FPIHInputAction.ZOOM_OUT );
        DEFAULT_BINDINGS.bind( Keys.NUMPAD_SUBTRACT,     FPIHInputAction.ZOOM_IN );
        DEFAULT_BINDINGS.bind( MouseButtons.WHEEL_UP,    FPIHInputAction.DISCRETE_ZOOM_OUT );
        DEFAULT_BINDINGS.bind( MouseButtons.WHEEL_DOWN,  FPIHInputAction.DISCRETE_ZOOM_IN );
    }
    
    /**
     * Creates the default key bindings.
     * You can override them by invoking the bindKey() method.
     * 
     * {@link #bind(org.jagatoo.input.devices.components.DeviceComponent, FPIHInputAction, org.jagatoo.input.managers.InputBindingsSet)}
     * 
     * @param clearBefore if true, unbindKeys() is called before
     */
    public void createDefaultBindings( boolean clearBefore )
    {
        set( DEFAULT_BINDINGS, clearBefore );
    }
    
    /**
     * Creates the default key bindings.
     * You can override them by invoking the bindKey() method.
     * This method clears all key bindings before.
     * 
     * {@link #bind(org.jagatoo.input.devices.components.DeviceComponent, FPIHInputAction, org.jagatoo.input.managers.InputBindingsSet)}
     */
    public void createDefaultBindings()
    {
        createDefaultBindings( true );
    }
    
    public FPIHInputBindingsManager()
    {
        super( FPIHInputAction.values().length );
    }
}
