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
package org.xith3d.schedops.values;

import org.jagatoo.input.devices.Keyboard;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.Keys;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.utility.properties.FloatProperty;

/**
 * Add comment here...
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class KeyFloatAdjuster extends Adjuster {
    
    private FloatProperty data;
    
    private Key ADD_KEY;
    
    private Key SUB_KEY;
    
    private Key PRINT_KEY;
    
    private boolean ADD_PRESSED;
    
    private boolean SUB_PRESSED;
    
    private float speed;
    
    private Mode mode;
    
    private Keyboard keyboard;
    
    /**
     * @param name Name
     * @param data FloatProperty
     * @param speed Speed, in units/second
     * @param mode Mode.SEQUENTIAL or Mode.CONTINUOUS
     * @param keyboard Keyboard
     */
    public KeyFloatAdjuster(String name, FloatProperty data, float speed,
                            Mode mode, Keyboard keyboard) {
        
        this(name, data, speed, mode, keyboard,
             Keys.NUMPAD_ADD, Keys.NUMPAD_SUBTRACT, Keys.NUMPAD_ENTER);
        
    }
    
    /**
     * @param name Name
     * @param data FloatProperty
     * @param speed Speed, in units/second
     * @param mode Mode.SEQUENTIAL or Mode.CONTINUOUS
     * @param keyboard Keyboard
     * @param ADD_KEY Key code for add
     * @param SUB_KEY Key code for sub
     * @param DISP_KEY Key code for print
     */
    public KeyFloatAdjuster(String name, FloatProperty data, float speed,
            Mode mode, Keyboard keyboard,
            Key ADD_KEY, Key SUB_KEY, Key DISP_KEY) {
        
        this.name = name;
        this.data = data;
        this.speed = speed;
        this.mode = mode;
        
        this.keyboard = keyboard;
        
        this.ADD_KEY = ADD_KEY;
        this.SUB_KEY = SUB_KEY;
        this.PRINT_KEY = DISP_KEY;
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void update(long gameTime, long frameTime, TimingMode timingMode) {
        
        changed = false;
        
        float delta = (timingMode.getMilliSeconds(frameTime)) / Adjuster.RESOLUTION * speed;
        
        if(keyboard.isKeyPressed(PRINT_KEY)) {
            getState();
        }
        
        switch (mode) {
        case SEQUENTIAL:
            
            if(ADD_PRESSED) {
                if(!keyboard.isKeyPressed(ADD_KEY)) {
                    add(delta); changed = true;
                    ADD_PRESSED = false;
                }
            } else {
                if(!keyboard.isKeyPressed(ADD_KEY)) {
                    ADD_PRESSED = true;
                }
            }
            
            if(SUB_PRESSED) {
                if(!keyboard.isKeyPressed(SUB_KEY)) {
                    add(delta); changed = true;
                    SUB_PRESSED = false;
                }
            } else {
                if(!keyboard.isKeyPressed(SUB_KEY)) {
                    SUB_PRESSED = true;
                }
            }
            
            break;
            
        case CONTINUOUS:
            
            if(keyboard.isKeyPressed(ADD_KEY)) {
                add(delta); changed = true;
            }
            
            if(keyboard.isKeyPressed(SUB_KEY)) {
                sub(delta); changed = true;
            }
            
            break;
            
        default:
            break;
        }
        
    }
    
    private void sub(float delta) {
        
        data.setFloat(data.getFloat() - delta);
        
    }
    
    private void add(float delta) {
        
        data.setFloat(data.getFloat() + delta);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        
        return data.toString();
        
    }
    
    /**
     * @return the adjuster speed, in units/second
     */
    public float getSpeed() {
        
        return speed;
        
    }
    
    /**
     * Adjust the speed
     * @param speed New speed, in units/second
     */
    public void setSpeed(float speed) {
        
        this.speed = speed;
        
    }

}
