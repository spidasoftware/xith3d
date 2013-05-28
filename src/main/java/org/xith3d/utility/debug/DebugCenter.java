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
package org.xith3d.utility.debug;

import java.awt.Window;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Keys;
import org.jagatoo.input.events.InputEvent;
import org.jagatoo.input.listeners.InputStateListener;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.Updater;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.utility.sgtree.SGTree;
import org.xith3d.utility.timing.PerformanceStats;

/**
 * The Debug Center allows you to dynamically launch a range of utilities on
 * your game to easily debug it.
 * 
 * TODO : Add features :)
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class DebugCenter implements Updatable, InputStateListener {
    
    private Xith3DEnvironment env;
    
    // Sub-systems
    private PerformanceStats perf = null;
    private SGTree sgTree = null;
    
    // Flags
    private boolean sgTreeUpdateScheduled = false;
    private boolean controlPressed;
    
    // Constants
    private String appendString = " - Xith3D Debug Center Active (Press Ctrl+F1 for help)";
    private Window help;
    
    /**
     * @param env
     * @param loop
     */
    public DebugCenter(Xith3DEnvironment env, Updater updater) {
        
        // References
        this.env = env;
        // this.loop = loop;
        updater.addUpdatable(this);
        
        // Events
        //InputSystem.getInstance().registerNewKeyboardAndMouse(env.getCanvas().getPeer());
        InputSystem.getInstance().addInputStateListener(this);
        
        // Performance Stats
        updater.addUpdatable(this.perf = new PerformanceStats(false));
        
        // SGTree
        sgTree = new SGTree();
        
    }
    
    /**
     * 
     */
    private void switchHelp() {

        if (help == null) {
            help = new HelpWindow();
        }

        help.setVisible(!help.isVisible());

    }
    
    /**
     * {@inheritDoc}
     */
    public void update(long gameTime, long frameTime, TimingMode timingMode) {
        
        if (!env.getCanvas().getTitle().endsWith(appendString)) {
            env.getCanvas().setTitle(env.getCanvas().getTitle() + appendString);
        }
        
        // Update sg tree if needed
        if (sgTreeUpdateScheduled) {
            sgTreeUpdateScheduled = false;
            assert sgTree != null;
            sgTree.updateNodes(env);
        }
        
        // Allow Swing to paint its components
        Thread.yield();
        
    }
    
    public void onInputStateChanged(InputEvent e, DeviceComponent comp, int delta, int state) {
        if (comp == Keys.LEFT_CONTROL) {
            controlPressed = (state > 0);
            return;
        }
        
        if (controlPressed) {
            if (comp == Keys.T) {
                sgTreeUpdateScheduled = true;
            } else if (comp == Keys.F1) {
                switchHelp();
            } else if (comp == Keys.S) {
                perf.print();
            }
        }
    }
    
}
