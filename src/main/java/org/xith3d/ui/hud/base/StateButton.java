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
package org.xith3d.ui.hud.base;

import java.util.ArrayList;

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.input.devices.components.MouseButton;
import org.xith3d.ui.hud.listeners.WidgetStateListener;
import org.xith3d.ui.hud.utils.ButtonGroup;

/**
 * This class is a base for all state capable Buttons on a HUD.
 * You can add WidgetActionListeners to it to get
 * notified of a click event.
 * 
 * @see org.xith3d.ui.hud.listeners.WidgetStateListener
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class StateButton extends Widget implements Enableable
{
    private boolean state = false;
    
    private final ArrayList<WidgetStateListener> listeners = new ArrayList<WidgetStateListener>( 3 );
    
    private ButtonGroup stateGroup = null;
    
    private boolean enabled = true;
    
    /**
     * Assotiates this StateButton with a StateGroup
     * 
     * @param sg the StateGroup to assotiate this StateButton with
     */
    public void setStateGroup( ButtonGroup sg )
    {
        this.stateGroup = sg;
    }
    
    /**
     * @return the StateGroup this StateButton is assotiated with
     */
    public final ButtonGroup getStateGroup()
    {
        return ( stateGroup );
    }
    
    /**
     * Notifies all added listeners about a state event
     */
    protected final void notifyListeners()
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onButtonStateChanged( this, state, getUserObject() );
        }
    }
    
    protected abstract void onExtendedStateChanged( boolean state, boolean hovered );
    
    /**
     * Sets the current state of this StateButton
     * 
     * @param state
     */
    public void setState( boolean state )
    {
        if ( this.state == state )
            return;
        
        this.state = state;
        
        if ( ( getStateGroup() != null ) && state )
            getStateGroup().onButtonActivated( this );
        
        onExtendedStateChanged( this.state, this.isHovered() );
        notifyListeners();
    }
    
    /**
     * @return the current state of this StateButton
     */
    public final boolean getState()
    {
        return ( state );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseEntered( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseEntered( isTopMost, hasFocus );
        
        if ( isTopMost )
        {
            onExtendedStateChanged( state, isHovered() );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseExited( isTopMost, hasFocus );
        
        if ( isTopMost )
        {
            onExtendedStateChanged( state, isHovered() );
        }
    }
    
    protected void cycleState()
    {
        setState( !getState() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonPressed( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isTopMost && isEnabled() )
        {
            cycleState();
        }
    }
    
    /**
     * Add a StateListener
     */
    public void addStateListener( WidgetStateListener listener )
    {
        listeners.add( listener );
    }
    
    /**
     * Remove a StateListener
     */
    public void removeStateListener( WidgetStateListener listener )
    {
        listeners.remove( listener );
    }
    
    protected abstract void setEnabledImpl( boolean enabled );
    
    /**
     * {@inheritDoc}
     */
    public final void setEnabled( boolean enabled )
    {
        if ( enabled == this.enabled )
            return;
        
        this.enabled = enabled;
        
        setEnabledImpl( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * Creates a new StateButton.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the desired width
     * @param height the desired height
     */
    public StateButton( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height )
    {
        super( isHeavyWeight, hasWidgetAssembler, width, height );
    }
}
