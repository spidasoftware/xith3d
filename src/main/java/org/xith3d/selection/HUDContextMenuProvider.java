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
package org.xith3d.selection;

import java.util.ArrayList;
import java.util.List;

import org.openmali.vecmath2.Tuple2i;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.contextmenu.ContextMenu;
import org.xith3d.ui.hud.contextmenu.DefaultContextMenu;
import org.xith3d.ui.hud.contextmenu.DefaultContextMenuItem;

/**
 * HUD implementation of {@link ContextMenuProvider}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HUDContextMenuProvider implements ContextMenuProvider
{
    private static final String PROP_HUD_MENU_ITEM = "HUD_MENU_ITEM";
    
    public static final String PROP_MOUSE_COORDS = "mouseCoords";
    public static final String PROP_WORLD_COORDS = "worldCoords";
    public static final String PROP_SELECTION = "selection";
    public static final String PROP_SELECTED_CONTEXT = "selectedContext";
    
    private ContextMenu menu = new DefaultContextMenu();
    
    /**
     * {@inheritDoc}
     */
    public void showContextMenu( Tuple2i mouseCoords, Tuple3f worldCoords, List<Selectable> selection, List<Selectable> selectedContext )
    {
        if ( menu.isPoppedUp() )
            menu.popUp( false );
        
        menu.removeAllItems();
        
        ArrayList<MenuAction> result = new ArrayList<MenuAction>();
        
        for ( int i = 0; i < selection.size(); i++ )
        {
            Selectable selectable = selection.get( i );
            List<MenuAction> actions = selectable.lookup( MenuAction.class );
            result.addAll( actions );
        }
        
        for ( int i = 0; i < result.size(); i++ )
        {
            final MenuAction action = result.get( i );
            action.setProperty( PROP_MOUSE_COORDS, mouseCoords );
            action.setProperty( PROP_WORLD_COORDS, worldCoords );
            action.setProperty( PROP_SELECTION, selection );
            action.setProperty( PROP_SELECTED_CONTEXT, selectedContext );
            
            DefaultContextMenuItem hudItem = (DefaultContextMenuItem)action.getProperty( PROP_HUD_MENU_ITEM );
            if ( hudItem == null )
            {
                hudItem = new DefaultContextMenuItem( action.getName() )
                {
                    @Override
                    public void onItemClicked()
                    {
                        super.onItemClicked();
                        
                        action.onActionPerformed();
                    }
                };
                
                action.setProperty( PROP_HUD_MENU_ITEM, hudItem );
            }
            
            if ( action == MenuAction.SEPARATOR )
            {
                //menu.addSeparator();
            }
            else
            {
                menu.addItem( hudItem );
            }
        }
        
        menu.popUp( mouseCoords.getX(), mouseCoords.getY() );
    }
    
    public HUDContextMenuProvider( HUD hud )
    {
        this.menu = new DefaultContextMenu();
        
        hud.setContextMenu( menu );
    }
}
