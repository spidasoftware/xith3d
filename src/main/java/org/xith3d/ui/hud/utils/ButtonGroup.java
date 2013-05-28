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
package org.xith3d.ui.hud.utils;

import java.util.ArrayList;
import java.util.HashSet;

import org.xith3d.ui.hud.base.StateButton;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.WidgetStateListener;

/**
 * This class can be used to group {@link StateButton} {@link Widget}s.
 * Only one {@link StateButton} in the group can have an ACTIVATED state.<br>
 * The attached {@link WidgetStateListener}s are only notified for the
 * {@link StateButton}, that has been activated, but not for the one, that
 * has been deactivated (automatically).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ButtonGroup
{
    private final HashSet<StateButton> buttons = new HashSet<StateButton>();
    private StateButton currentActivated = null;
    
    private final ArrayList<WidgetStateListener> listeners = new ArrayList<WidgetStateListener>();
    
    /**
     * Adds a {@link WidgetStateListener}.
     */
    public void addStateListener( WidgetStateListener listener )
    {
        listeners.add( listener );
    }
    
    /**
     * Removes a {@link WidgetStateListener}.
     */
    public void removeStateListener( WidgetStateListener listener )
    {
        listeners.remove( listener );
    }
    
    /**
     * Notifies all added listeners about a state event.
     * 
     * @param button
     */
    protected final void notifyListeners( StateButton button )
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onButtonStateChanged( button, button.getState(), button.getUserObject() );
        }
    }
    
    /**
     * This method is invoked by the appropriate event in the {@link StateButton} class.
     * It will deactivate the current activated {@link StateButton}.
     * 
     * @param button the new activated {@link StateButton}
     */
    public void onButtonActivated( StateButton button )
    {
        if ( buttons.contains( button ) )
        {
            if ( currentActivated != null )
                currentActivated.setState( false );
            
            currentActivated = button;
            
            notifyListeners( currentActivated );
        }
    }
    
    /**
     * Adds a new {@link StateButton} to this group.
     * 
     * @param button the {@link StateButton} to add
     */
    public void addStateButton( StateButton button )
    {
        buttons.add( button );
        button.setStateGroup( this );
    }
    
    /**
     * Removes a {@link StateButton} from this group.
     * 
     * @param button the {@link StateButton} to remove
     */
    public void removeStateButton( StateButton button )
    {
        if ( buttons.remove( button ) )
            button.setStateGroup( null );
    }
    
    /**
     * Creates a new {@link ButtonGroup}.
     */
    public ButtonGroup()
    {
    }
}
