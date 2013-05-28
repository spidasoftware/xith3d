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

import java.util.ArrayList;
import java.util.List;

import org.jagatoo.input.devices.components.MouseButton;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.loop.opscheduler.util.SchedOpsPool;

import org.xith3d.render.Canvas3D;
import org.xith3d.scenegraph.GroupNode;

/**
 * Since picking is done by the render thread (RenderLoop) an instance of this class
 * is put into the RenderLoop's scheduler. When picking is done, the PickListener is notified.
 * 
 * @see org.xith3d.picking.PickListener
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ScheduledPicker extends ScheduledOperationImpl
{
    private List< GroupNode > groups;
    private Canvas3D canvas;
    private MouseButton button;
    private int posX;
    private int posY;
    
    private AllPickListener allPickListener;
    private NearestPickListener nearestPickListener;
    
    private Object userObject;
    
    private boolean pickAll;
    
    /**
     * @return the user object assotiated with this instance if any
     */
    public Object getUserObject()
    {
        return ( userObject );
    }
    
    /**
     * Assotiotes a new user object with this instance.
     */
    public void setUserObject( Object userObject )
    {
        this.userObject = userObject;
    }
    
    /**
     * @return the Listener for the picking result
     */
    public AllPickListener getAllPickListener()
    {
        return ( allPickListener );
    }
    
    /**
     * @return the Listener for the picking result
     */
    public NearestPickListener getNearestPickListener()
    {
        return ( nearestPickListener );
    }
    
    /**
     * @return the Canvas3D-instance on which the picking was done
     */
    public Canvas3D getCanvas()
    {
        return ( canvas );
    }
    
    /**
     * @return the mouse-x coordinate whre the picking should be made
     */
    public int getMouseX()
    {
        return ( posX );
    }
    
    /**
     * @return the mouse-x coordinate whre the picking should be made
     */
    public int getMouseY()
    {
        return ( posY );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( !pickAll )
        {
            PickingLibrary.pickNearest( groups, canvas, button, getMouseX(), getMouseY(), nearestPickListener, getUserObject() );
        }
        else
        {
            PickingLibrary.pickAll( groups, canvas, button, getMouseX(), getMouseY(), allPickListener, getUserObject() );
        }
        
        SchedOpsPool.deallocateScheduledPicker( this );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param groups the List of GroupNodes to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onNodePicked() method
     */
    public void init( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        this.groups.clear();
        for ( int i = 0; i < groups.size(); i++ )
            this.groups.add( groups.get( i ) );
        
        this.canvas = canvas;
        this.button = button;
        
        this.posX = mouseX;
        this.posY = mouseY;
        
        this.allPickListener = pl;
        setUserObject( userObject );
        
        this.pickAll = true;
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param groups the List of GroupNodes to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onNodePicked() method
     */
    public void init( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        this.groups.clear();
        for ( int i = 0; i < groups.size(); i++ )
            this.groups.add( groups.get( i ) );
        
        this.canvas = canvas;
        this.button = button;
        
        this.posX = mouseX;
        this.posY = mouseY;
        
        this.nearestPickListener = pl;
        setUserObject( userObject );
        
        this.pickAll = false;
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     */
    public ScheduledPicker()
    {
        super( false );
        
        this.groups = createGsList( null );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param groups the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onNodePicked() method
     */
    public ScheduledPicker( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        super( false );
        
        init( groups, canvas, button, mouseX, mouseY, pl, userObject );
    }
    
    private static List< GroupNode > createGsList( GroupNode group )
    {
        final List< GroupNode > groups = new ArrayList< GroupNode >();
        
        if ( group != null )
            groups.add( group );
        
        return ( groups );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param group the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public ScheduledPicker( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl, Object userObject )
    {
        this( createGsList( group ), canvas, button, mouseX, mouseY, pl, userObject );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param groups the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     */
    public ScheduledPicker( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        this( groups, canvas, button, mouseX, mouseY, pl, (Object)null );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param group the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     */
    public ScheduledPicker( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, AllPickListener pl )
    {
        this( group, canvas, button, mouseX, mouseY, pl, (Object)null );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param groups the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onNodePicked() method
     */
    public ScheduledPicker( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        super( false );
        
        init( groups, canvas, button, mouseX, mouseY, pl, userObject );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param group the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onObjectPicked() method of the PickListener
     */
    public ScheduledPicker( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl, Object userObject )
    {
        this( createGsList( group ), canvas, button, mouseX, mouseY, pl, userObject );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param groups the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     */
    public ScheduledPicker( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        this( groups, canvas, button, mouseX, mouseY, pl, (Object)null );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param group the Groups to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     */
    public ScheduledPicker( GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, NearestPickListener pl )
    {
        this( group, canvas, button, mouseX, mouseY, pl, (Object)null );
    }
}
