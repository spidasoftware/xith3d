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
package org.xith3d.loop.opscheduler;

import java.util.List;

import org.jagatoo.input.devices.components.MouseButton;
import org.xith3d.picking.AllPickListener;
import org.xith3d.picking.NearestPickListener;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DWrapper;
import org.xith3d.scenegraph.GroupNode;

/**
 * Thsi interface allows for scheduled picking. So you don't need to
 * synchronize anything at all.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface PickScheduler
{
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickAll( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickAll( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickAll( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickAll( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickAll( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickAll( GroupNode group, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickAll( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, AllPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickAll( GroupNode group, MouseButton button, int mouseX, int mouseY, AllPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickNearest( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickNearest( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the Picking on
     * @param canvas the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickNearest( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param canvasWrapper the Canvas3D to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickNearest( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public void pickNearest( GroupNode group, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param groups the List of GroupNodes to do the Picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, NearestPickListener pl );
    
    /**
     * Schedules a new picking operation in the {@link OperationScheduler}.
     * 
     * @param group the GroupNode to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the mouse-x-position where the picking is to be done
     * @param mouseY the mouse-y-position where the picking is to be done
     * @param pl the PickListener to be notified when picking is done
     */
    public void pickNearest( GroupNode group, MouseButton button, int mouseX, int mouseY, NearestPickListener pl );
}
