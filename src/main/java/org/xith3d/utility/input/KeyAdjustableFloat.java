/**
 * "Digibots" sourcecode is licenced under the
 * CeCILL license
 * 
 * Made early 2007 by Amos Wenger, Olivier Charvet 
 * and Quentin Merleau
 */
package org.xith3d.utility.input;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.Keyboard;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.Keys;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;

/**
 * A float value adjustable by keyboard events.
 * You can adjust the keys which are used
 * to increase/decrease the value.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class KeyAdjustableFloat extends ScheduledOperationImpl {

    private final Keyboard keyboard;
    
    private final Key vkDown;
    private final Key vkUp;
    private final Key vkDisp;
    
    private final float speed;
    private float value;
    
    /**
     * New KeyAdjustableFloat
     * @param vkDown
     * @param vkUp
     * @param speed
     * @param inputManager The input manager used
     */
    public KeyAdjustableFloat(Key vkDown, Key vkUp, float speed,
            OperationScheduler opSched) {

        this(vkDown, vkUp, Keys.ESCAPE, speed, opSched);
        
    }
    
    /**
     * New KeyAdjustableFloat
     * @param vkDown
     * @param vkUp
     * @param speed
     * @param inputManager The input manager used
     */
    public KeyAdjustableFloat(Key vkDown, Key vkUp, Key vkDisp, float speed,
            OperationScheduler opSched) {

        super(true);
        
        if (!InputSystem.hasInstance()) {
            throw new Error("No InputSystem registered.");
        }
        
        this.keyboard = InputSystem.getInstance().getKeyboard();
        
        if (keyboard == null) {
            throw new Error("No Keyboard registered at the InputSystem.");
        }
        
        this.vkDown = vkDown;
        this.vkUp = vkUp;
        this.vkDisp = vkDisp;
        this.speed = speed;
        
        opSched.scheduleOperation(this);
        
    }

    public void update(long gameTime, long frameTime, TimingMode timingMode) {
        
        float diff = timingMode.getSecondsAsFloat(frameTime) * speed;
        
        if(keyboard.isKeyPressed(vkDown)) {
            value -= diff;
        }
        
        if(keyboard.isKeyPressed(vkUp)) {
            value += diff;
        }
        
        if(keyboard.isKeyPressed(vkDisp)) {
            System.out.println("Value = "+value);
        }

    }
    
    public void setValue(float value) {
        this.value = value;
    }
    
    public void setValue(Float value) {
        this.value = value;
    }
    
    public final float getValue() {
        return value;
    }

}
