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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.openmali.vecmath2.Tuple2i;
import org.openmali.vecmath2.Tuple3f;

/**
 * @author Mathias Henze (aka cylab)
 */
public class AWTContextMenuProvider implements ContextMenuProvider
{
    private static final String PROP_SWING_ACTION = "SWING_ACTION";
    
    public static final String PROP_MOUSE_COORDS = "mouseCoords";
    public static final String PROP_WORLD_COORDS = "worldCoords";
    public static final String PROP_SELECTION = "selection";
    public static final String PROP_SELECTED_CONTEXT = "selectedContext";
    
    private JPopupMenu menu = new JPopupMenu();
    private Component component;

    public void showContextMenu( Tuple2i mouseCoords, Tuple3f worldCoords, List<Selectable> selection, List<Selectable> selectedContext )
    {
        menu.removeAll();
        
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
            
            AbstractAction swingAction = (AbstractAction)action.getProperty( PROP_SWING_ACTION );
            if ( swingAction == null )
            {
                swingAction = new AbstractAction( action.getName() )
                {
                    private static final long serialVersionUID = 1L;
                    
                    public void actionPerformed( ActionEvent e )
                    {
                        action.onActionPerformed();
                    }
                };
                
                action.setProperty( PROP_SWING_ACTION, swingAction );
            }
            
            if ( action == MenuAction.SEPARATOR )
                menu.addSeparator();
            else
                menu.add( swingAction );
        }
        
        menu.show( component, mouseCoords.getX(), mouseCoords.getY() );
    }

    public AWTContextMenuProvider( Component component )
    {
        this.component = component;
    }
}
