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
package org.xith3d.ui.hud.menusystem.menus.settings;

import org.xith3d.input.FirstPersonInputHandler;
import org.xith3d.ui.hud.base.StateButton;
import org.xith3d.ui.hud.layout.ListLayout;
import org.xith3d.ui.hud.listeners.SliderListener;
import org.xith3d.ui.hud.listeners.WidgetStateListener;
import org.xith3d.ui.hud.menusystem.menus.MenuBase;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Checkbox;
import org.xith3d.ui.hud.widgets.EmptyWidget;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.hud.widgets.Slider;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This is a settings menu for the {@link FirstPersonInputHandler}'s main settings.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHMainSettingsMenu extends MenuBase
{
    public static final String NAME = FPIHMainSettingsMenu.class.getSimpleName();
    
    private final Label mouseXSpeedCaption;
    private final Label mouseYSpeedCaption;
    private final Slider xSlider;
    private final Slider ySlider;
    private final Checkbox yFlipper;
    
    private class InputReceiver implements SliderListener, WidgetStateListener
    {
        public boolean ignore = false;
        
        public void onSliderValueChanged( Slider slider, int newValue )
        {
            if ( ( getMenuGroup() != null ) && !ignore )
                getMenuGroup().fireOnSettingChanged( FPIHMainSettingsMenu.this, "fpih_main_setting", (String)slider.getUserObject() + "=" + ( newValue / 10f ) );
        }
        
        public void onButtonStateChanged( StateButton stateButton, boolean state, Object userObject )
        {
            if ( ( getMenuGroup() != null ) && !ignore )
                getMenuGroup().fireOnSettingChanged( FPIHMainSettingsMenu.this, "fpih_main_setting", (String)userObject + "=" + state );
        }
    }
    
    private final InputReceiver inputReceiver = new InputReceiver();
    
    public void setMaxMouseXSpeed( float speed )
    {
        inputReceiver.ignore = true;
        xSlider.setMaxValue( (int)( speed * 10 ) );
        inputReceiver.ignore = false;
    }
    
    public float getMaxMouseXSpeed()
    {
        return ( xSlider.getMaxValue() / 10f );
    }
    
    public void setMinMouseXSpeed( float speed )
    {
        if ( speed <= 0f )
            throw new IllegalArgumentException( "You cannot set the min speed <= 0" );
        
        inputReceiver.ignore = true;
        xSlider.setMinValue( (int)( speed * 10 ) );
        inputReceiver.ignore = false;
    }
    
    public float getMinMouseXSpeed()
    {
        return ( xSlider.getMinValue() / 10f );
    }
    
    public void setMouseXSpeed( float speed )
    {
        inputReceiver.ignore = true;
        xSlider.setValue( (int)( speed * 10 ) );
        inputReceiver.ignore = false;
    }
    
    public float getMouseXSpeed()
    {
        return ( xSlider.getValue() / 10f );
    }
    
    public void setMaxMouseYSpeed( float speed )
    {
        inputReceiver.ignore = true;
        ySlider.setMaxValue( (int)( speed * 10 ) );
        inputReceiver.ignore = false;
    }
    
    public float getMaxMouseYSpeed()
    {
        return ( ySlider.getMaxValue() / 10f );
    }
    
    public void setMinMouseYSpeed( float speed )
    {
        if ( speed <= 0f )
            throw new IllegalArgumentException( "You cannot set the min speed <= 0" );
        
        inputReceiver.ignore = true;
        ySlider.setMinValue( (int)( speed * 10 ) );
        inputReceiver.ignore = false;
    }
    
    public float getMinMouseYSpeed()
    {
        return ( ySlider.getMinValue() / 10f );
    }
    
    public void setMouseYSpeed( float speed )
    {
        inputReceiver.ignore = true;
        ySlider.setValue( (int)( speed * 10 ) );
        inputReceiver.ignore = false;
    }
    
    public float getMouseYSpeed()
    {
        return ( ySlider.getValue() / 10f );
    }
    
    public void setMouseYAxisInverted( boolean inverted )
    {
        inputReceiver.ignore = true;
        yFlipper.setState( inverted );
        inputReceiver.ignore = false;
    }
    
    public boolean isMouseYAxisInverted()
    {
        return ( yFlipper.getState() );
    }
    
    /**
     * Applies the {@link FirstPersonInputHandler}'s relevant config to this config menu.
     * 
     * @param fpih
     */
    public void applyConfig( FirstPersonInputHandler fpih )
    {
        setMouseXSpeed( fpih.getMouseXSpeed() );
        setMouseYSpeed( Math.abs( fpih.getMouseYSpeed() ) );
        
        setMouseYAxisInverted( fpih.getMouseYSpeed() < 0f );
    }
    
    /**
     * Reads the relevant config from this config menu
     * and applies it to the {@link FirstPersonInputHandler}.
     * 
     * @param fpih
     */
    public void extractConfig( FirstPersonInputHandler fpih )
    {
        fpih.setMouseXSpeed( getMouseXSpeed() );
        
        if ( isMouseYAxisInverted() )
            fpih.setMouseYSpeed( -getMouseYSpeed() );
        else
            fpih.setMouseYSpeed( getMouseYSpeed() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initWidgets()
    {
        addWidget( mouseXSpeedCaption );
        addWidget( xSlider );
        
        addWidget( new EmptyWidget( 0f, 10f ) );
        
        addWidget( mouseYSpeedCaption );
        addWidget( ySlider );
        
        addWidget( new EmptyWidget( 0f, 10f ) );
        
        yFlipper.setSize( 0f, mouseXSpeedCaption.getHeight() );
        addWidget( yFlipper );
    }
    
    public FPIHMainSettingsMenu( float width, float height )
    {
        super( width, height, FPIHMainSettingsMenu.NAME, new String[] { "save" } );
        
        ListLayout layout = new ListLayout( ListLayout.Orientation.VERTICAL, 2f, 10f, 10f, 10f, 10f );
        layout.setAlignment( ListLayout.Alignment.CENTER_TOP );
        layout.setOtherSpanCalculated( true );
        this.setLayout( layout );
        
        HUDFont font = HUDFont.getFont( "Verdana", HUDFont.PLAIN, 12 );
        
        this.mouseXSpeedCaption = new Label( 0f, 0f, "Mouse-X-Speed", font, TextAlignment.BOTTOM_LEFT );
        
        this.xSlider = new Slider( 0f );
        xSlider.setUserObject( "mouse_x_speed" );
        xSlider.setMinAndMax( 0, 50 );
        
        this.mouseYSpeedCaption = new Label( 0f, 0f, "Mouse-Y-Speed", font, TextAlignment.BOTTOM_LEFT );
        
        this.ySlider = new Slider( 0f );
        ySlider.setUserObject( "mouse_y_speed" );
        ySlider.setMinAndMax( 0, 50 );
        
        this.yFlipper = new Checkbox( 0f, mouseXSpeedCaption.getHeight(), "Y-axis inverted", font, null );
        yFlipper.setUserObject( "mouse_y_flipped" );
        
        
        // Apply default settings...
        setMouseXSpeed( FirstPersonInputHandler.DEFAULT_MOUSE_X_SPEED );
        setMouseYSpeed( Math.abs( FirstPersonInputHandler.DEFAULT_MOUSE_Y_SPEED ) );
        
        setMouseYAxisInverted( FirstPersonInputHandler.DEFAULT_MOUSE_Y_SPEED < 0f );
        
        xSlider.addSliderListener( inputReceiver );
        ySlider.addSliderListener( inputReceiver );
        yFlipper.addStateListener( inputReceiver );
    }
    
    public FPIHMainSettingsMenu( float width )
    {
        this( width, 0f );
        
        setMinimalHeight();
    }
}
