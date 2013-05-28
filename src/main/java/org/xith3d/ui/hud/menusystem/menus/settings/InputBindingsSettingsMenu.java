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

import org.jagatoo.commands.CommandsRegistry;
import org.jagatoo.input.actions.LabeledInputAction;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.input.managers.InputBindingsManager;
import org.jagatoo.input.managers.InputBindingsSet;
import org.openmali.vecmath2.Colorf;
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
 * This is a settings menu for general key-bindings
 * to bind keys to {@link LabeledInputAction}s.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class InputBindingsSettingsMenu extends MenuBase
{
    public static final String NAME = InputBindingsSettingsMenu.class.getSimpleName();
    
    private final LabeledInputAction[] actions;
    
    private final HashMap< LabeledInputAction, Label[] > actionLabelMap = new HashMap< LabeledInputAction, Label[] >();
    private final HashMap< Label, LabeledInputAction > labelActionMap = new HashMap< Label, LabeledInputAction >();
    
    private final InputBindingsManager< LabeledInputAction > inputBindings;
    
    private Label bindingQuery;
    private Label lastClickedLabel = null;
    
    public void setBinding( DeviceComponent comp, LabeledInputAction action, InputBindingsSet set )
    {
        LabeledInputAction bound = inputBindings.getBoundAction( comp );
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
        else //if ( set == InputBindingSet.SECONDARY )
            targetLabel = actionLabelMap.get( action )[ 1 ];
        
        inputBindings.bind( comp, action, set );
        targetLabel.setText( comp.getLocalizedName() );
    }
    
    public void resetBinding( LabeledInputAction action, InputBindingsSet set )
    {
        if ( inputBindings.unbind( action ) != null )
        {
            final Label targetLabel;
            if ( set == InputBindingsSet.PRIMARY )
                targetLabel = actionLabelMap.get( action )[ 0 ];
            else //if ( set == InputBindingSet.SECONDARY )
                targetLabel = actionLabelMap.get( action )[ 1 ];
            
            targetLabel.setText( "" );
        }
    }
    
    public LabeledInputAction getBinding( DeviceComponent comp )
    {
        return ( inputBindings.getBoundAction( comp ) );
    }
    
    public DeviceComponent getBoundComponent( LabeledInputAction action, InputBindingsSet set )
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
    
    @SuppressWarnings( "unchecked" )
    public < A extends LabeledInputAction > void setBindings( InputBindingsManager< A > inputBindings )
    {
        clearBindings();
        
        for ( LabeledInputAction action: actions )
        {
            for ( InputBindingsSet set: InputBindingsSet.values() )
            {
                final DeviceComponent comp = inputBindings.getBoundComponent( (A)action, set );
                
                if ( comp != null )
                {
                    setBinding( comp, action, set );
                }
            }
        }
    }
    
    public InputBindingsManager< LabeledInputAction > getBindings()
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
            
            final LabeledInputAction action = labelActionMap.get( lastClickedLabel );
            setBinding( comp, action, (InputBindingsSet)lastClickedLabel.getUserObject() );
            getMenuGroup().fireOnSettingChanged( InputBindingsSettingsMenu.this, "input_binding", comp.getName() + "=>" + action.getLocalizedText() );
            
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
            else
                handleInput( MouseButtons.WHEEL_DOWN );
        }
        
        @Override
        public void onMouseButtonPressed( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
        {
            handleInput( button );
        }
    };
    
    private WidgetMouseListener bindingInputListener = new WidgetMouseAdapter()
    {
        @Override
        public void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
        {
            lastClickedLabel = (Label)widget;
            
            if ( ( lastWhen != -1L ) && ( ( when - lastWhen ) < 300L ) )
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
                bindingQuery.requestFocus();
                
                bindingQuery.addInputListener( bindingSetListener );
            }
        }
    };
    
    private ArrayList< Widget > earlyWidgets;
    
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
    
    private void addBindingLine( String caption, LabeledInputAction command, HUDFont font )
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
        
        actionLabelMap.put( command, new Label[] { label2, label3 } );
        labelActionMap.put( label2, command );
        labelActionMap.put( label3, command );
    }
    
    private InputBindingsSettingsMenu( float width, float height, LabeledInputAction[] commands, Object[] commandObjs )
    {
        super( width, height, InputBindingsSettingsMenu.NAME, new String[] { "save" } );
        
        if ( commands == null )
        {
            this.actions = new LabeledInputAction[ commandObjs.length ];
            for ( int i = 0; i < commandObjs.length; i++ )
            {
                this.actions[ i ] = (LabeledInputAction)commandObjs[ i ];
            }
        }
        else
        {
            this.actions = commands;
        }
        this.inputBindings = new InputBindingsManager< LabeledInputAction >( this.actions.length );
        
        this.setLayout( new GridLayout( 0, 3, 0f, 0f, 10f, 10f, 10f, 10f ) );
        
        HUDFont font = HUDFont.getFont( "Verdana", HUDFont.PLAIN, 12 );
        
        earlyWidgets = new ArrayList< Widget >();
        
        addHeaderLine( font.derive( HUDFont.BOLD ) );
        
        for ( int i = 0; i < this.actions.length; i++ )
        {
            final LabeledInputAction command = this.actions[ i ];
            
            addBindingLine( command.getLocalizedText(), command, font );
        }
        
        this.bindingQuery = new Label( 0f, 0f, "PRESS_KEY", font.derive( HUDFont.BOLD ), Colorf.GREEN, TextAlignment.CENTER_CENTER );
        this.bindingQuery.setZIndex( 10 );
        bindingQuery.setBackgroundColor( Colorf.BLACK );
        this.addWidget( bindingQuery, 100f, 100f, LayoutManager.IGNORED_BY_LAYOUT );
        bindingQuery.setVisible( false );
        bindingQuery.setClickable( false );
        bindingQuery.setPickable( false );
    }
    
    public InputBindingsSettingsMenu( float width, float height, LabeledInputAction[] commands )
    {
        this( width, height, commands, null );
    }
    
    public InputBindingsSettingsMenu( float width, float height, CommandsRegistry< ? extends LabeledInputAction > commandsReg )
    {
        this( width, height, null, commandsReg.values() );
    }
    
    public InputBindingsSettingsMenu( float width, LabeledInputAction[] commands )
    {
        this( width, 0f, commands );
        
        setMinimalHeight();
    }
    
    public InputBindingsSettingsMenu( float width, CommandsRegistry< ? extends LabeledInputAction > commandsReg )
    {
        this( width, 0f, commandsReg );
        
        setMinimalHeight();
    }
}
