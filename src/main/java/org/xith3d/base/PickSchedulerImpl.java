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
package org.xith3d.base;

import java.util.List;

import org.jagatoo.input.devices.components.MouseButton;
import org.xith3d.loop.opscheduler.PickScheduler;
import org.xith3d.loop.opscheduler.util.SchedOpsPool;
import org.xith3d.picking.AllPickListener;
import org.xith3d.picking.NearestPickListener;
import org.xith3d.picking.ScheduledPicker;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DWrapper;
import org.xith3d.scenegraph.GroupNode;

/**
 * Simple implementation of the PickScheduler interface.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
class PickSchedulerImpl implements PickScheduler
{
    private final Xith3DEnvironment env;
    
    private void pick( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, Object pl, Object userObject, boolean pickAll )
    {
        if ( env.getOperationScheduler() == null )
            throw new NullPointerException( "Your Xith3DEnvironment doesn't have an attached OperationScheduler." );
        
        final ScheduledPicker sp = SchedOpsPool.allocateScheduledPicker();
        if ( !pickAll )
            sp.init( groups, canvas, button, mouseX, mouseY, (NearestPickListener)pl, userObject );
        else
            sp.init( groups, canvas, button, mouseX, mouseY, (AllPickListener)pl, userObject );
        env.getOperationScheduler().scheduleOperation( sp );
    }
    
    private void pick( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, Object pl, Object userObject, boolean pickAll )
    {
        List< GroupNode > groupList = SchedOpsPool.allocateGroupList();
        
        groupList.add( group );
        
        pick( groupList, canvas, button, mouseX, mouseY, pl, userObject, pickAll );
        
        SchedOpsPool.deallocateGroupList( groupList );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        pick( groups, canvas, button, mouseX, mouseY, pl, userObject, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        pick( group, canvas, button, mouseX, mouseY, pl, userObject, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        pick( groups, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, userObject, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        pick( group, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, userObject, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        pick( groups, canvas, button, mouseX, mouseY, pl, null, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        pick( group, canvas, button, mouseX, mouseY, pl, null, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        pick( groups, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, null, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        pick( group, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, null, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( groups, env.getCanvas(), button, mouseX, mouseY, pl, userObject, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( GroupNode group, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( group, env.getCanvas(), button, mouseX, mouseY, pl, userObject, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( groups, env.getCanvas(), button, mouseX, mouseY, pl, null, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickAll( GroupNode group, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( group, env.getCanvas(), button, mouseX, mouseY, pl, null, true );
    }
    
    
    
    
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        pick( groups, canvas, button, mouseX, mouseY, pl, userObject, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        pick( group, canvas, button, mouseX, mouseY, pl, userObject, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        pick( groups, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, userObject, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        pick( group, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, userObject, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        pick( groups, canvas, button, mouseX, mouseY, pl, null, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        pick( group, canvas, button, mouseX, mouseY, pl, null, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( List< ? extends GroupNode > groups, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        pick( groups, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, null, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( GroupNode group, Canvas3DWrapper canvasWrapper, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        pick( group, canvasWrapper.getCanvas(), button, mouseX, mouseY, pl, null, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( groups, env.getCanvas(), button, mouseX, mouseY, pl, userObject, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( GroupNode group, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( group, env.getCanvas(), button, mouseX, mouseY, pl, userObject, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( groups, env.getCanvas(), button, mouseX, mouseY, pl, null, false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void pickNearest( GroupNode group, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the environment!" );
        
        pick( group, env.getCanvas(), button, mouseX, mouseY, pl, null, false );
    }
    
    
    
    PickSchedulerImpl( Xith3DEnvironment env )
    {
        this.env = env;
    }
}
