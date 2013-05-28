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
package org.xith3d.picking;

import java.util.List;

import org.jagatoo.input.devices.components.MouseButton;
import org.xith3d.scenegraph.GroupNode;

/**
 * A class implementing this interface is able to pick Nodes in the scenegraph.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface PickEngine
{
    /**
     * Picks all Nodes in the given Groups.
     * 
     * @param groups the Groups to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     * @param userObject the user-object to be passed back to the PickListener
     */
    public void pickAll( List< ? extends GroupNode > groups, MouseButton button, int x, int y, AllPickListener pl, Object userObject );
    
    /**
     * Picks all Nodes in the given Groups.
     * 
     * @param groups the Groups to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     */
    public void pickAll( List< ? extends GroupNode > groups, MouseButton button, int x, int y, AllPickListener pl );
    
    /**
     * Picks the all Nodes in the given Groups and finds the closest one to the View.
     * 
     * @param groups the Groups to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     * @param userObject the user-object to be passed back to the PickListener
     */
    public void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int x, int y, NearestPickListener pl, Object userObject );
    
    /**
     * Picks the all Nodes in the given Groups and finds the closest one to the View.
     * 
     * @param groups the Groups to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     */
    public void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int x, int y, NearestPickListener pl );
    
    /**
     * Picks all Nodes in the given Group.
     * 
     * @param group the Group to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     * @param userObject the user-object to be passed back to the PickListener
     */
    public void pickAll( GroupNode group, MouseButton button, int x, int y, AllPickListener pl, Object userObject );
    
    /**
     * Picks all Nodes in the given Group.
     * 
     * @param group the Group to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     */
    public void pickAll( GroupNode group, MouseButton button, int x, int y, AllPickListener pl );
    
    /**
     * Picks the all Nodes in the given Group and finds the closest one to the View.
     * 
     * @param group the Group to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     * @param userObject the user-object to be passed back to the PickListener
     */
    public void pickNearest( GroupNode group, MouseButton button, int x, int y, NearestPickListener pl, Object userObject );
    
    /**
     * Picks the all Nodes in the given Group and finds the closest one to the View.
     * 
     * @param group the Group to do the picking on
     * @param button the mouse button, that was clicked to initiate the picking
     * @param x the mouse-x-position where the picking is to be done
     * @param y the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified of the pick results
     */
    public void pickNearest( GroupNode group, MouseButton button, int x, int y, NearestPickListener pl );
}
