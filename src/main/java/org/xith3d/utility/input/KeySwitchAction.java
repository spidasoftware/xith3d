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
package org.xith3d.utility.input;

import java.lang.reflect.Method;

import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.actions.AbstractInvokableInputAction;
import org.jagatoo.input.devices.InputDevice;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.xith3d.utility.classes.beans.BeanUtil;
import org.xith3d.utility.logging.X3DLog;

/**
 * Associate a key with
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class KeySwitchAction extends AbstractInvokableInputAction {
    
    private Object object;
    private Method getter;
    private Method setter;
    
    public KeySwitchAction(Object object, String member) {
        
        this(-1, object, member);
        
    }
    
    public KeySwitchAction(int ordinal, Object object, String member) {
        
        super(ordinal);
        
        try {
            getter = BeanUtil.getGetter(object, member);
            setter = BeanUtil.getSetter(object, member);
        } catch (Exception e) {
            X3DLog.print(e);
            throw new Error(e);
        }
        this.object = object;
        
    }
    
    /**
     * {@inheritDoc}
     */
    public String invokeAction(InputDevice device, DeviceComponent comp, int delta, int state, long nanoTime) throws InputSystemException {
        if(delta <= 0) {
            try {
                setter.invoke(this.object, !((Boolean) getter.invoke(this.object)).booleanValue());
            } catch (Throwable t) {
                throw new InputSystemException(t);
            }
        }
        
        return "ok";
    }
}
