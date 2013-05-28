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
import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderPass;
import org.xith3d.scenegraph.GroupNode;

/**
 * An instance of this class hold all information to perform a picking.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class PickRequest
{
    private final ArrayList< RenderPass > renderPasses;
    private final ArrayList< GroupNode > groups;
    private Canvas3D canvas;
    private MouseButton button;
    private int posX;
    private int posY;
    
    private AllPickListener allPickListener;
    private NearestPickListener nearestPickListener;
    
    private Object userObject;
    
    private boolean pickAll;
    
    public List< RenderPass > getRenderPasses()
    {
        return ( renderPasses );
    }
    
    public List< GroupNode > getGroups()
    {
        return ( groups );
    }
    
    /**
     * @return the user object assotiated with this instance if any
     */
    public final Object getUserObject()
    {
        return ( userObject );
    }
    
    /**
     * Assotiotes a new user object with this instance.
     */
    public final void setUserObject( Object userObject )
    {
        this.userObject = userObject;
    }
    
    /**
     * @return the Listener for the picking result
     */
    public final AllPickListener getAllPickListener()
    {
        return ( allPickListener );
    }
    
    /**
     * @return the Listener for the picking result
     */
    public final NearestPickListener getNearestPickListener()
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
    
    public final MouseButton getButton()
    {
        return ( button );
    }
    
    /**
     * @return the mouse-x coordinate whre the picking should be made
     */
    public final int getMouseX()
    {
        return ( posX );
    }
    
    /**
     * @return the mouse-x coordinate whre the picking should be made
     */
    public final int getMouseY()
    {
        return ( posY );
    }
    
    public final boolean getPickAll()
    {
        return ( pickAll );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param renderPasses
     * @param groups the List of GroupNodes to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onNodePicked() method
     */
    public void init( List< RenderPass > renderPasses, List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, Object pl, Object userObject, boolean pickAll )
    {
        this.renderPasses.clear();
        for ( int i = 0; i < renderPasses.size(); i++ )
            this.renderPasses.add( renderPasses.get( i ) );
        
        this.groups.clear();
        for ( int i = 0; i < groups.size(); i++ )
            this.groups.add( groups.get( i )  );
        
        this.canvas = canvas;
        this.button = button;
        
        this.posX = mouseX;
        this.posY = mouseY;
        
        if ( pickAll )
            this.allPickListener = (AllPickListener)pl;
        else
            this.nearestPickListener = (NearestPickListener)pl;
        setUserObject( userObject );
        
        this.pickAll = pickAll;
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     * 
     * @param renderPass
     * @param group the List of GroupNodes to do the picking on
     * @param canvas the canvas to do the picking on
     * @param button the mouse button, that was clicked
     * @param mouseX the x-position of the mouse on the canvas
     * @param mouseY the y-position of the mouse on the canvas
     * @param pl the picklistener to use for callback when picking is done
     * @param userObject this user object is passed back to the onNodePicked() method
     * @param pickAll if true, all picked shapes are returned as results, but not only the nearest one
     */
    public void init( RenderPass renderPass, GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, Object pl, Object userObject, boolean pickAll )
    {
        this.renderPasses.clear();
        this.renderPasses.add( renderPass );
        
        this.groups.clear();
        this.groups.add( group );
        
        this.canvas = canvas;
        this.button = button;
        
        this.posX = mouseX;
        this.posY = mouseY;
        
        if ( pickAll )
            this.allPickListener = (AllPickListener)pl;
        else
            this.nearestPickListener = (NearestPickListener)pl;
        setUserObject( userObject );
        
        this.pickAll = pickAll;
    }
    
    private static ArrayList< RenderPass > createRPList( RenderPass renderPass )
    {
        final ArrayList< RenderPass > passes = new ArrayList< RenderPass >();
        
        if ( renderPass != null )
            passes.add( renderPass );
        
        return ( passes );
    }
    
    private static ArrayList< GroupNode > createGsList( GroupNode group )
    {
        final ArrayList< GroupNode > groups = new ArrayList< GroupNode >();
        
        if ( group != null )
            groups.add( group );
        
        return ( groups );
    }
    
    /**
     * Generates a new ScheduledPicker instance.
     */
    public PickRequest()
    {
        this.renderPasses = createRPList( null );
        this.groups = createGsList( null );
    }
}
