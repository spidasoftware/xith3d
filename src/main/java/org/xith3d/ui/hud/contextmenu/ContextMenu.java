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
package org.xith3d.ui.hud.contextmenu;

import java.util.ArrayList;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.Mouse;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.__HUD_PrivilegedAccess;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.utils.PopUpable;

/**
 * This is the root of any Xith3D HUD context menu.<br>
 * You can add instances of {@link ContextMenuItem}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ContextMenu implements PopUpable
{
    private HUD hud;
    private final WidgetContainer menuItemsContainer;
    
    private final ArrayList< ContextMenuItem > items = new ArrayList< ContextMenuItem >();
    
    private final ArrayList< ContextMenuListener > listeners = new ArrayList< ContextMenuListener >();
    
    private boolean poppedUp = false;
    private long nextAllowedPopUpTime = -1L;
    
    public void setHUD( HUD hud )
    {
        this.hud = hud;
    }
    
    public final HUD getHUD()
    {
        return ( hud );
    }
    
    public final WidgetContainer getMenuItemsContainer()
    {
        return ( menuItemsContainer );
    }
    
    public void addContextMenuListener( ContextMenuListener l )
    {
        if ( listeners.contains( l ) )
            return;
        
        listeners.add( l );
    }
    
    public void removeContextMenuListener( ContextMenuListener l )
    {
        listeners.remove( l );
    }
    
    /**
     * 
     * @param item
     * @param menuItemsContainer
     */
    protected void onItemAdded( ContextMenuItem item, WidgetContainer menuItemsContainer )
    {
    }
    
    public void addItem( ContextMenuItem item )
    {
        if ( isPoppedUp() )
            throw new Error( "The ContextMenu must be invisible to be manipulated!" );
        
        if ( item.getContextMenu() != null )
            throw new Error( "This item is already attached to a ContextMenu." );
        
        items.add( item );
        item.setContextMenu( this );
        
        onItemAdded( item, menuItemsContainer );
    }
    
    /**
     * 
     * @param item
     * @param menuItemsContainer
     */
    protected void onItemRemoved( ContextMenuItem item, WidgetContainer menuItemsContainer )
    {
    }
    
    public void removeItem( ContextMenuItem item )
    {
        if ( isPoppedUp() )
            throw new Error( "The ContextMenu must be invisible to be manipulated!" );
        
        if ( item.getContextMenu() != this )
            throw new Error( "This item is not attached to this ContextMenu." );
        
        items.remove( item );
        item.setContextMenu( null );
        
        onItemRemoved( item, menuItemsContainer );
    }
    
    public final void removeAllItems()
    {
        for ( int i = items.size() -1; i >= 0; i-- )
        {
            removeItem( items.get( i ) );
        }
    }
    
    public void popUp( float posX, float posY )
    {
        if ( !poppedUp && ( System.currentTimeMillis() < nextAllowedPopUpTime ) )
            return;
        
        if ( menuItemsContainer.getHUD() == null )
            __HUD_PrivilegedAccess.addVolatilePopup( hud, menuItemsContainer, null, posX, posY );
        else
            menuItemsContainer.setLocation( posX, posY );
        
        this.poppedUp = true;
    }
    
    public void popUp( int canvasX, int canvasY )
    {
        Tuple2f buffer = Tuple2f.fromPool();
        hud.getCoordinatesConverter().getLocationPixels2HUD( canvasX, canvasY, buffer );
        final float posX = buffer.getX();
        final float posY = buffer.getY();
        Tuple2f.toPool( buffer );
        
        popUp( posX, posY );
    }
    
    /**
     * {@inheritDoc}
     */
    public void popUp( boolean p )
    {
        if ( p == poppedUp )
            return;
        
        if ( !p )
        {
            this.poppedUp = false;
            this.nextAllowedPopUpTime = System.currentTimeMillis() + 20L;
            
            //menuItemsContainer.detach();
            __HUD_PrivilegedAccess.removeVolatilePopup( hud );
        }
        
        if ( !InputSystem.hasInstance() )
        {
            popUp( 0f, 0f );
            
            return;
        }
        
        Mouse mouse = InputSystem.getInstance().getMouse();
        
        if ( mouse == null )
        {
            popUp( 0f, 0f );
            
            return;
        }
        
        Tuple2f buffer = Tuple2f.fromPool();
        hud.getCoordinatesConverter().getLocationPixels2HUD( mouse.getCurrentX(), mouse.getCurrentY(), buffer );
        final float posX = buffer.getX();
        final float posY = buffer.getY();
        Tuple2f.toPool( buffer );
        
        popUp( posX, posY );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isPoppedUp()
    {
        return ( poppedUp );
    }
    
    // TODO: Make protected!
    public void onItemClicked( ContextMenuItem item )
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onContextMenuItemClicked( item );
        }
        
        if ( item.clickCausesMenuHide() )
        {
            popUp( false );
        }
    }
    
    protected WidgetContainer createMenuItemsContainer()
    {
        throw new Error( "You must implement createMenuItemsContainer, if a null value is passed to the Constructor!" );
    }
    
    public ContextMenu( HUD hud, WidgetContainer menuItemsContainer )
    {
        this.hud = hud;
        
        if ( menuItemsContainer == null )
            this.menuItemsContainer = createMenuItemsContainer();
        else
            this.menuItemsContainer = menuItemsContainer;
        this.menuItemsContainer.setName( "Context Menu" );
    }
}
