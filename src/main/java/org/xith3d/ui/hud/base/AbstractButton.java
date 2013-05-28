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
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.DigitalDeviceComponent;
import org.jagatoo.input.devices.components.Keys;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.util.arrays.ArrayUtils;
import org.openmali.types.twodee.Dim2f;
import org.xith3d.ui.hud.listeners.ButtonListener;

/**
 * This class is a base for all Buttons on a HUD. You can add
 * WidgetActionListeners to it to get notified of a click event.
 * 
 * @see org.xith3d.ui.hud.listeners.ButtonListener
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class AbstractButton extends Widget implements Enableable
{
    public enum ButtonState
    {
        NORMAL,
        HOVERED,
        PRESSED,
        ;
    }
    
    protected ButtonState buttonState;
    
    protected boolean isStateChangable = true;
    
    private boolean enabled = true;
    
    private final ArrayList<ButtonListener> listeners = new ArrayList<ButtonListener>();
    
    private static DeviceComponent[] accessors = new DeviceComponent[] { Keys.ENTER, Keys.SPACE };
    
    private static boolean isDefaultFocusResponsive = false;
    
    private boolean isFocusResponsive = isDefaultFocusResponsive;
    
    /**
     * Sets whether the Button Widget is focus-responsive by default.
     * 
     * @see #setFocusResponsive(boolean)
     * 
     * @param resp
     */
    public static final void setDefaultFocusResponsive( boolean resp )
    {
        isDefaultFocusResponsive = resp;
    }
    
    /**
     * @return whether the Button Widget is focus-responsive by default.
     * 
     * @see #isFocusResponsive()
     */
    public static final boolean isDefaultFocusResponsive()
    {
        return ( isDefaultFocusResponsive );
    }
    
    /**
     * Sets whether this Button Widget is focus-responsive.
     * This means, that it displays the HOVERED-state-picture
     * when it holds the focus.
     * 
     * @see #isFocusResponsive()
     * @see #setDefaultFocusResponsive(boolean)
     * @see #isDefaultFocusResponsive()
     * 
     * @param resp
     */
    public final void setFocusResponsive( boolean resp )
    {
        this.isFocusResponsive = resp;
    }
    
    /**
     * @return whether this Button Widget is focus-responsive.
     * This means, that it displays the HOVERED-state-picture
     * when it holds the focus.
     * 
     * @see #setFocusResponsive(boolean)
     * @see #setDefaultFocusResponsive(boolean)
     * @see #isDefaultFocusResponsive()
     */
    public final boolean isFocusResponsive()
    {
        return ( isFocusResponsive );
    }
    
    /**
     * Binds a DeviceComponent to ALL Buttons, that works as an accessor.
     * This means, that a focussed Button is pressed on a positive state-change
     * on this DeviceComponent.
     * 
     * @param comp
     */
    public static void bindAccessor( DeviceComponent comp )
    {
        if ( ( accessors == null ) || ( accessors.length == 0 ) )
        {
            accessors = new DeviceComponent[] { comp };
        }
        else
        {
            DeviceComponent[] newArray = new DeviceComponent[ accessors.length + 1 ];
            System.arraycopy( accessors, 0, newArray, 0, accessors.length );
            newArray[ newArray.length - 1 ] = comp;
            accessors = newArray;
        }
    }
    
    /**
     * Unbinds a DeviceComponent from ALL Buttons.
     * 
     * @param comp
     */
    public static void unbindAccessor( DeviceComponent comp )
    {
        if ( accessors == null )
        {
            return;
        }
        
        final int index = ArrayUtils.indexOf( accessors, comp, true );
        
        if ( index < 0 )
        {
            return;
        }
        
        if ( accessors.length == 1 )
        {
            accessors = null;
            
            return;
        }
        
        
        DeviceComponent[] newArray = new DeviceComponent[ accessors.length - 1 ];
        System.arraycopy( accessors, 0, newArray, 0, index );
        System.arraycopy( accessors, index + 1, newArray, index, accessors.length - index - 1 );
        accessors = newArray;
    }
    
    /**
     * Adds a ButtonListener.
     */
    public void addButtonListener( ButtonListener l )
    {
        listeners.add( l );
    }
    
    /**
     * Removes a ButtonListener.
     */
    public void removeButtonListener( ButtonListener l )
    {
        listeners.remove( l );
    }
    
    protected void fireButtonClickedEvent()
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onButtonClicked( this, this.getUserObject() );
        }
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
     * Sets the new {@link ButtonState} for this Button.
     * 
     * @param buttonState
     * 
     * @return has state changed?
     */
    public boolean setButtonState( ButtonState buttonState )
    {
        if ( buttonState == null )
            throw new IllegalArgumentException( "buttonState must not be null" );
        
        if ( buttonState == this.buttonState )
            return ( false );
        
        this.buttonState = buttonState;
        
        return ( true );
    }
    
    /**
     * @return the current ButtonState of this Button
     */
    public final ButtonState getButtonState()
    {
        return ( buttonState );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseEntered( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseEntered( isTopMost, hasFocus );
        
        if ( isTopMost && isStateChangable && isEnabled() )
            setButtonState( ButtonState.HOVERED );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseExited( isTopMost, hasFocus );
        
        if ( isTopMost && isStateChangable && isEnabled() )
            setButtonState( ButtonState.NORMAL );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonPressed( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isTopMost && isStateChangable && isEnabled() )
            setButtonState( ButtonState.PRESSED );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonReleased( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonReleased( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isTopMost && isStateChangable && isEnabled() )
        {
            setButtonState( ButtonState.HOVERED );
            
            fireButtonClickedEvent();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInputStateChanged( DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
        super.onInputStateChanged( comp, delta, state, when, isTopMost, hasFocus );
        
        if ( comp instanceof DigitalDeviceComponent )
        {
            if ( delta <= 0 )
                return;
        }
        /*
        else if ( comp instanceof MouseWheel )
        {
            //delta = Math.abs( delta );
        }
        */
        else
        {
            return;
        }
        
        if ( accessors != null )
        {
            for ( int i = 0; i < accessors.length; i++ )
            {
                if ( accessors[ i ] == comp )
                {
                    fireButtonClickedEvent();
                    return;
                }
            }
        }
    }
    
    /**
     * Calculates implementation dependent optimal size for this Button.
     * 
     * @param buffer
     * 
     * @return the buffer back again.
     */
    public abstract <Dim2f_ extends Dim2f> Dim2f_ getOptimalSize( Dim2f_ buffer );
    
    /**
     * Creates a new Button for the HUD.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the desired width
     * @param height the desired height
     */
    public AbstractButton( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height )
    {
        super( isHeavyWeight, hasWidgetAssembler, width, height );
        
        this.buttonState = ButtonState.NORMAL;
    }
}
