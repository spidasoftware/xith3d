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

import java.util.ArrayList;
import java.util.HashMap;

import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.input.managers.InputBindingsManager;
import org.jagatoo.input.managers.InputBindingsSet;
import org.openmali.vecmath2.Colorf;
import org.xith3d.input.FirstPersonInputHandler;
import org.xith3d.input.modules.fpih.FPIHInputAction;
import org.xith3d.input.modules.fpih.FPIHInputBindingsManager;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.borders.ColoredBorder;
import org.xith3d.ui.hud.layout.FillLayout;
import org.xith3d.ui.hud.layout.GridLayout;
import org.xith3d.ui.hud.layout.LayoutManager;
import org.xith3d.ui.hud.listeners.WidgetInputAdapter;
import org.xith3d.ui.hud.listeners.WidgetInputListener;
import org.xith3d.ui.hud.listeners.WidgetMouseAdapter;
import org.xith3d.ui.hud.listeners.WidgetMouseListener;
import org.xith3d.ui.hud.menusystem.menus.MenuBase;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.hud.widgets.Panel;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This is a settings menu for the {@link FirstPersonInputHandler}'s input bindings.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHInputBindingsSettingsMenu extends MenuBase
{
    public static final String NAME = FPIHInputBindingsSettingsMenu.class.getSimpleName();
    
    private final HashMap< FPIHInputAction, Label[] > actionLabelMap = new HashMap< FPIHInputAction, Label[] >();
    private final HashMap< Label, FPIHInputAction > labelActionMap = new HashMap< Label, FPIHInputAction >();
    
    private final InputBindingsManager< FPIHInputAction > inputBindings = new InputBindingsManager< FPIHInputAction >( FPIHInputAction.values().length );
    
    private Label bindingQuery;
    private Label lastClickedLabel = null;
    
    public void setBinding( DeviceComponent comp, FPIHInputAction action, InputBindingsSet set )
    {
        FPIHInputAction bound = inputBindings.getBoundAction( comp );
        if ( bound != null )
        {
            Label[] boundLabels = actionLabelMap.get( bound );
            if ( boundLabels[ 0 ].getText().equals( comp.getName() ) )
                boundLabels[ 0 ].setText( "" );
            else if ( boundLabels[ 1 ].getText().equals( comp.getName() ) )
                boundLabels[ 1 ].setText( "" );
        }
        
        final Label targetLabel;
        if ( set == InputBindingsSet.PRIMARY )
            targetLabel = actionLabelMap.get( action )[ 0 ];
        else //if ( set == BindingSet.SECONDARY )
            targetLabel = actionLabelMap.get( action )[ 1 ];
        
        inputBindings.bind( comp, action, set );
        targetLabel.setText( comp.getLocalizedName() );
    }
    
    public void resetBinding( FPIHInputAction action, InputBindingsSet set )
    {
        if ( inputBindings.unbind( action ) != null )
        {
            final Label targetLabel;
            if ( set == InputBindingsSet.PRIMARY )
                targetLabel = actionLabelMap.get( action )[ 0 ];
            else //if ( set == BindingSet.SECONDARY )
                targetLabel = actionLabelMap.get( action )[ 1 ];
            
            targetLabel.setText( "" );
        }
    }
    
    public FPIHInputAction getBinding( DeviceComponent comp )
    {
        return ( inputBindings.getBoundAction( comp ) );
    }
    
    public DeviceComponent getBoundComponent( FPIHInputAction action, InputBindingsSet set )
    {
        return ( inputBindings.getBoundComponent( action, set ) );
    }
    
    public void clearBindings()
    {
        inputBindings.unbindAll();
        
        for ( Label label: labelActionMap.keySet() )
        {
            label.setText( "" );
        }
    }
    
    public void setBindings( InputBindingsManager< FPIHInputAction > inputBindings )
    {
        clearBindings();
        
        for ( FPIHInputAction action: FPIHInputAction.values() )
        {
            for ( InputBindingsSet set: InputBindingsSet.values() )
            {
                final DeviceComponent comp = inputBindings.getBoundComponent( action, set );
                
                if ( comp != null )
                {
                    setBinding( comp, action, set );
                }
            }
        }
    }
    
    public InputBindingsManager< FPIHInputAction > getBindings()
    {
        return ( inputBindings );
    }
    
    private WidgetInputListener bindingSetListener = new WidgetInputAdapter()
    {
        private final void handleInput( DeviceComponent comp )
        {
            bindingQuery.removeInputListener( bindingSetListener );
            
            if ( lastClickedLabel == null )
                return;
            
            bindingQuery.setClickable( false );
            bindingQuery.setPickable( false );
            bindingQuery.setVisible( false );
            
            final FPIHInputAction action = labelActionMap.get( lastClickedLabel );
            setBinding( comp, action, (InputBindingsSet)lastClickedLabel.getUserObject() );
            getMenuGroup().fireOnSettingChanged( FPIHInputBindingsSettingsMenu.this, "key_binding", comp.getName() + "=>" + action );
            
            lastClickedLabel = null;
        }
        
        @Override
        public void onKeyPressed( Widget widget, Key key, int modifierMask, long when )
        {
            handleInput( key );
        }
        
        @Override
        public void onMouseWheelMoved( Widget widget, int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost )
        {
            if ( delta > 0 )
                handleInput( MouseButtons.WHEEL_UP );
            else if ( delta < 0 )
                handleInput( MouseButtons.WHEEL_DOWN );
        }
    };
    
    private WidgetMouseListener bindingInputListener = new WidgetMouseAdapter()
    {
        @Override
        public void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
        {
            lastClickedLabel = (Label)widget;
            
            if ( ( lastWhen != -1L ) && ( ( when - lastWhen ) < 300000000L ) )
            {
                float parentLeft = 0f;
                float parentTop = 0f;
                float parentResX = 0f;
                float parentResY = 0f;
                if ( widget.getContainer() != null )
                {
                    parentLeft = widget.getContainer().getLeft();
                    parentTop = widget.getContainer().getTop();
                    parentResX = widget.getContainer().getResX();
                    parentResY = widget.getContainer().getResY();
                }
                else if ( widget.getHUD() != null )
                {
                    parentResX = widget.getHUD().getResX();
                    parentResY = widget.getHUD().getResY();
                }
                
                bindingQuery.setLocation( parentLeft + 2f, parentTop + 2f );
                bindingQuery.setSize( parentResX - 4f, parentResY - 4f );
                bindingQuery.setClickable( true );
                bindingQuery.setPickable( true );
                bindingQuery.setVisible( true );
                bindingQuery.setFocussable( true );
                bindingQuery.requestFocus();
                
                bindingQuery.addInputListener( bindingSetListener );
            }
        }
    };
    
    private ArrayList< Widget > earlyWidgets;
    
    /**
     * Applies the {@link FirstPersonInputHandler}'s relevant config to this config menu.
     * 
     * @param fpih
     */
    public void applyConfig( FirstPersonInputHandler fpih )
    {
        setBindings( fpih.getBindingsManager() );
    }
    
    /**
     * Reads the relevant config from this config menu
     * and applies it to the {@link FirstPersonInputHandler}.
     * 
     * @param fpih
     * @param clearFPIHBefore
     */
    public void extractConfig( FirstPersonInputHandler fpih, boolean clearFPIHBefore )
    {
        fpih.getBindingsManager().set( inputBindings, clearFPIHBefore );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initWidgets()
    {
        if ( earlyWidgets != null )
        {
            for ( int i = 0; i < earlyWidgets.size(); i++ )
            {
                addWidget( earlyWidgets.get( i ) );
            }
            
            earlyWidgets = null;
        }
    }
    
    private Panel createMiniPanel( float height, int borderBottom, int borderRight, int borderTop, int borderLeft, Colorf backgroundColor )
    {
        Panel panel = new Panel( 200f, height, backgroundColor );
        panel.setLayout( new FillLayout() );
        
        panel.setBorder( new ColoredBorder( borderBottom, borderRight, borderTop, borderLeft, Colorf.DARK_GRAY ) );
        
        return ( panel );
    }
    
    private void addHeaderLine( HUDFont font )
    {
        Panel panel1 = createMiniPanel( 30f, 1, 1, 1, 1, null );
        Label label1 = new Label( 0f, 0f, "Command", font, Colorf.BLACK, TextAlignment.CENTER_LEFT );
        label1.setPadding( 0, 3, 0, 3 );
        panel1.addWidget( label1 );
        earlyWidgets.add( panel1 );
        
        Panel panel2 = createMiniPanel( 30f, 1, 1, 1, 0, null );
        Label label2 = new Label( 0f, 0f, "Key 1", font, Colorf.BLACK, TextAlignment.CENTER_CENTER );
        panel2.addWidget( label2 );
        earlyWidgets.add( panel2 );
        
        Panel panel3 = createMiniPanel( 30f, 1, 1, 1, 0, null );
        Label label3 = new Label( 0f, 0f, "Key 2", font, Colorf.BLACK, TextAlignment.CENTER_CENTER );
        panel3.addWidget( label3 );
        earlyWidgets.add( panel3 );
        
        Panel panel4 = createMiniPanel( 5f, 0, 1, 0, 1, null );
        earlyWidgets.add( panel4 );
        
        Panel panel5 = createMiniPanel( 5f, 0, 1, 0, 0, null );
        earlyWidgets.add( panel5 );
        
        Panel panel6 = createMiniPanel( 5f, 0, 1, 0, 0, null );
        earlyWidgets.add( panel6 );
    }
    
    private void addBindingLine( String caption, FPIHInputAction action, HUDFont font )
    {
        final int borderTop = ( actionLabelMap.size() == 0 ) ? 1: 0;
        
        Panel panel1 = createMiniPanel( 30f, 1, 1, borderTop, 1, null );
        Label label1 = new Label( 1f, 1f, caption, font, Colorf.BLACK, TextAlignment.CENTER_LEFT );
        label1.setPadding( 0, 3, 0, 3 );
        panel1.addWidget( label1 );
        earlyWidgets.add( panel1 );
        
        Panel panel2 = createMiniPanel( 30f, 1, 1, borderTop, 0, Colorf.BLACK );
        Label label2 = new Label( 1f, 1f, "", font, Colorf.WHITE, TextAlignment.CENTER_CENTER );
        label2.setUserObject( InputBindingsSet.PRIMARY );
        label2.addMouseListener( bindingInputListener );
        panel2.addWidget( label2 );
        earlyWidgets.add( panel2 );
        
        Panel panel3 = createMiniPanel( 30f, 1, 1, borderTop, 0, Colorf.BLACK );
        Label label3 = new Label( 1f, 1f, "", font, Colorf.WHITE, TextAlignment.CENTER_CENTER );
        label3.setUserObject( InputBindingsSet.SECONDARY );
        label3.addMouseListener( bindingInputListener );
        panel3.addWidget( label3 );
        earlyWidgets.add( panel3 );
        
        actionLabelMap.put( action, new Label[] { label2, label3 } );
        labelActionMap.put( label2, action );
        labelActionMap.put( label3, action );
    }
    
    public FPIHInputBindingsSettingsMenu( float width, float height )
    {
        super( width, height, FPIHInputBindingsSettingsMenu.NAME, new String[] { "save" } );
        
        this.setLayout( new GridLayout( 0, 3, 0f, 0f, 10f, 10f, 10f, 10f ) );
        
        HUDFont font = HUDFont.getFont( "Verdana", HUDFont.PLAIN, 12 );
        
        earlyWidgets = new ArrayList< Widget >();
        
        addHeaderLine( font.derive( HUDFont.BOLD ) );
        
        addBindingLine( FPIHInputAction.WALK_FORWARD.getLocalizedText(),      FPIHInputAction.WALK_FORWARD,      font );
        addBindingLine( FPIHInputAction.WALK_BACKWARD.getLocalizedText(),     FPIHInputAction.WALK_BACKWARD,     font );
        addBindingLine( FPIHInputAction.STRAFE_LEFT.getLocalizedText(),       FPIHInputAction.STRAFE_LEFT,       font );
        addBindingLine( FPIHInputAction.STRAFE_RIGHT.getLocalizedText(),      FPIHInputAction.STRAFE_RIGHT,      font );
        addBindingLine( FPIHInputAction.TURN_LEFT.getLocalizedText(),         FPIHInputAction.TURN_LEFT,         font );
        addBindingLine( FPIHInputAction.TURN_RIGHT.getLocalizedText(),        FPIHInputAction.TURN_RIGHT,        font );
        addBindingLine( FPIHInputAction.AIM_UP.getLocalizedText(),            FPIHInputAction.AIM_UP,            font );
        addBindingLine( FPIHInputAction.AIM_DOWN.getLocalizedText(),          FPIHInputAction.AIM_DOWN,          font );
        addBindingLine( FPIHInputAction.JUMP.getLocalizedText(),              FPIHInputAction.JUMP,              font );
        addBindingLine( FPIHInputAction.CROUCH.getLocalizedText(),            FPIHInputAction.CROUCH,            font );
        addBindingLine( FPIHInputAction.DISCRETE_ZOOM_IN.getLocalizedText(),  FPIHInputAction.DISCRETE_ZOOM_IN,  font );
        addBindingLine( FPIHInputAction.DISCRETE_ZOOM_OUT.getLocalizedText(), FPIHInputAction.DISCRETE_ZOOM_OUT, font );
        addBindingLine( FPIHInputAction.ZOOM_IN.getLocalizedText(),           FPIHInputAction.ZOOM_IN,           font );
        addBindingLine( FPIHInputAction.ZOOM_OUT.getLocalizedText(),          FPIHInputAction.ZOOM_OUT,          font );
        
        this.bindingQuery = new Label( 0f, 0f, "PRESS_KEY", font.derive( HUDFont.BOLD ), Colorf.GREEN, TextAlignment.CENTER_CENTER );
        this.bindingQuery.setZIndex( 10 );
        bindingQuery.setBackgroundColor( Colorf.BLACK );
        this.addWidget( bindingQuery, 100f, 100f, LayoutManager.IGNORED_BY_LAYOUT );
        bindingQuery.setVisible( false );
        bindingQuery.setClickable( false );
        bindingQuery.setPickable( false );
        
        
        // Apply default settings...
        setBindings( FPIHInputBindingsManager.DEFAULT_BINDINGS );
    }
    
    public FPIHInputBindingsSettingsMenu( float width )
    {
        this( width, 0f );
        
        setMinimalHeight();
    }
}
