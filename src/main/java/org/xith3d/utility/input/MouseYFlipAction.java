/**
 * Copyright (c) 2007-2008, JAGaToo Project Group all rights reserved.
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
package org.xith3d.utility.input;

import org.jagatoo.input.actions.AbstractLabeledInvokableInputAction;
import org.jagatoo.input.devices.InputDevice;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.xith3d.input.FirstPersonInputHandler;

/**
 * This LabeledInvokableInputAction is capable of flipping the mouse movement y axis of a {@link FirstPersonInputHandler}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MouseYFlipAction extends AbstractLabeledInvokableInputAction
{
    private final FirstPersonInputHandler fpih;
    
    private final boolean keyStateDependent;
    
    /**
     * {@inheritDoc}
     */
    public String invokeAction( InputDevice device, DeviceComponent comp, int delta, int state, long nanoTime )
    {
        if ( keyStateDependent )
        {
            final boolean bool = ( state > 0 );
            
            final float absYSpeed = Math.abs( fpih.getMouseYSpeed() );
            if ( bool )
                fpih.setMouseYSpeed( absYSpeed * -1f );
            else
                fpih.setMouseYSpeed( absYSpeed );
            
            return ( bool ? "inverted" : "non-inverted" );
        }
        
        if ( delta <= 0 )
            return ( null );
        
        fpih.flipMouseYAxis();
        
        return ( "flipped" );
    }
    
    public MouseYFlipAction( int ordinal, String text, FirstPersonInputHandler fpih, boolean keyStateDependent )
    {
        super( ordinal, text );
        
        if ( fpih == null )
            throw new NullPointerException( "fpih must not be null" );
        
        this.fpih = fpih;
        this.keyStateDependent = keyStateDependent;
    }
    
    public MouseYFlipAction( FirstPersonInputHandler fpih, boolean keyStateDependent )
    {
        this( -1, "Flip Mouse Y", fpih, keyStateDependent );
    }
    
    public MouseYFlipAction( int ordinal, String text, FirstPersonInputHandler fpih )
    {
        this( ordinal, text, fpih, false );
    }
    
    public MouseYFlipAction( FirstPersonInputHandler fpih )
    {
        this( -1, "Flip Mouse Y", fpih );
    }
}
