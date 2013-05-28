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
package org.xith3d.input.modules.orih;

import org.jagatoo.input.devices.components.Keys;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.input.managers.InputBindingsManager;
import org.xith3d.input.ObjectRotationInputHandler;

/**
 * A special {@link InputBindingsManager} for the {@link ObjectRotationInputHandler}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ORIHInputBindingsManager extends InputBindingsManager< ORIHInputAction >
{
    public static final InputBindingsManager< ORIHInputAction > DEFAULT_KEY_BINDINGS = new InputBindingsManager< ORIHInputAction >( ORIHInputAction.values().length );
    static
    {
        DEFAULT_KEY_BINDINGS.unbindAll();
        
        DEFAULT_KEY_BINDINGS.bind( Keys.A,                   ORIHInputAction.ROTATE_LEFT );
        DEFAULT_KEY_BINDINGS.bind( Keys.LEFT,                ORIHInputAction.ROTATE_LEFT );
        DEFAULT_KEY_BINDINGS.bind( Keys.D,                   ORIHInputAction.ROTATE_RIGHT );
        DEFAULT_KEY_BINDINGS.bind( Keys.RIGHT,               ORIHInputAction.ROTATE_RIGHT );
        DEFAULT_KEY_BINDINGS.bind( Keys.W,                   ORIHInputAction.ROTATE_UP );
        DEFAULT_KEY_BINDINGS.bind( Keys.UP,                  ORIHInputAction.ROTATE_UP );
        DEFAULT_KEY_BINDINGS.bind( Keys.S,                   ORIHInputAction.ROTATE_DOWN );
        DEFAULT_KEY_BINDINGS.bind( Keys.DOWN,                ORIHInputAction.ROTATE_DOWN );
        DEFAULT_KEY_BINDINGS.bind( Keys.NUMPAD_ADD,          ORIHInputAction.ZOOM_OUT );
        DEFAULT_KEY_BINDINGS.bind( Keys.NUMPAD_SUBTRACT,     ORIHInputAction.ZOOM_IN );
        DEFAULT_KEY_BINDINGS.bind( MouseButtons.WHEEL_UP,    ORIHInputAction.DISCRETE_ZOOM_OUT );
        DEFAULT_KEY_BINDINGS.bind( MouseButtons.WHEEL_DOWN,  ORIHInputAction.DISCRETE_ZOOM_IN );
    }
    
    /**
     * Creates the default key bindings.
     * You can override them by invoking the bindKey() method.
     * 
     * @param clearBefore if true, unbindKeys() is called before
     */
    public void createDefaultBindings( boolean clearBefore )
    {
        set( DEFAULT_KEY_BINDINGS, clearBefore );
    }
    
    /**
     * Creates the default key bindings.
     * You can override them by invoking the bindKey() method.
     * This method clears all key bindings before.
     * 
     * @see #createDefaultBindings(boolean)
     */
    public void createDefaultBindings()
    {
        createDefaultBindings( true );
    }
    
    public ORIHInputBindingsManager()
    {
        super( ORIHInputAction.values().length );
    }
}
