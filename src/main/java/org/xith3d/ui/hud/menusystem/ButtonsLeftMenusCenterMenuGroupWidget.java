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
package org.xith3d.ui.hud.menusystem;

import java.util.HashMap;

import org.xith3d.ui.hud.base.AbstractButton;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.borders.ColoredBorder;
import org.xith3d.ui.hud.layout.BorderLayout;
import org.xith3d.ui.hud.layout.CenterLayout;
import org.xith3d.ui.hud.layout.HullLayout;
import org.xith3d.ui.hud.layout.LayoutManager;
import org.xith3d.ui.hud.layout.ListLayout;
import org.xith3d.ui.hud.listeners.ButtonListener;
import org.xith3d.ui.hud.widgets.Button;
import org.xith3d.ui.hud.widgets.EmptyWidget;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.hud.widgets.Panel;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This is a {@link MenuGroupWidget} implementation, that places the
 * accessor Buttons in the WEST area of a {@link BorderLayout} and the
 * Menus in the CENTER area.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ButtonsLeftMenusCenterMenuGroupWidget extends MenuGroupWidget
{
    private MenuGroup menuGroup = null;
    private final Panel accessorPanel;
    private final Panel menuPanel;
    
    private Button saveButton;
    private boolean waitingForSaveButton;
    
    private Panel westPanel = null;
    private ListLayout.Alignment buttonsAlignment = null;
    
    private final HashMap< Menu, Widget > menuWrapperMap = new HashMap< Menu, Widget >();
    
    private final ButtonListener buttonListener = new ButtonListener()
    {
        public void onButtonClicked( AbstractButton button, Object userObject )
        {
            if ( menuGroup != null )
            {
                if ( menuGroup.fireOnMenuActionPerformed( (String)userObject ) )
                {
                    button.setVisible( false );
                }
            }
        }
    };
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final WidgetContainer getAccessorPanel()
    {
        return ( accessorPanel );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final WidgetContainer getMenuPanel()
    {
        return ( menuPanel );
    }
    
    /**
     * @return the Button, that indicates a save-operation.
     */
    public Button getSaveButton()
    {
        return ( saveButton );
    }
    
    public Button createAccessorWidget( String caption, Button.Description buttonDesc )
    {
        final Button button;
        if ( buttonDesc == null )
            button = new Button( 100f, 40f, caption );
        else
            button = new Button( 100f, 40f, caption, buttonDesc );
        
        button.setFocusResponsive( true );
        
        return ( button );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Button addAccessorWidget( String caption, Button.Description buttonDesc )
    {
        final Button button = createAccessorWidget( caption, buttonDesc );
        accessorPanel.addWidget( button );
        
        if ( accessorPanel.getWidgets().size() == 1 )
        {
            button.requestFocus();
        }
        
        return ( button );
    }
    
    private Widget createMenuWrapper( MenuSystem menuSystem, String caption, Menu menu )
    {
        ( (Widget)menu ).setBorder( new ColoredBorder( 2, menuSystem.getMenuBorderColor() ) );
        
        Panel panel = new Panel( 0f, 0f, menuSystem.getMenuBackgroundColor() );
        
        panel.setLayout( new HullLayout( new CenterLayout( CenterLayout.Orientation.VERTICAL ), 15f ) );
        panel.setBorder( new ColoredBorder( 4, menuSystem.getMenuBorderColor() ) );
        
        Label captionLabel = new Label( 0f, 0f, caption,
                                                menuSystem.getMenuCaptionFont(),
                                                menuSystem.getMenuCaptionFontColor(),
                                                TextAlignment.CENTER_CENTER );
        
        panel.addWidget( captionLabel );
        
        panel.addWidget( new EmptyWidget( 0f, 30f ) );
        
        panel.addWidget( ( (Widget)menu ) );
        
        return ( panel );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addMenu( MenuSystem menuSystem, String caption, Menu menu )
    {
        Widget wrapper = createMenuWrapper( menuSystem, caption, menu );
        menuWrapperMap.put( menu, wrapper );
        
        wrapper.setVisible( menuPanel.getWidgets().size() == 0 );
        menuPanel.addWidget( wrapper );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMenuVisible( Menu menu, boolean visible )
    {
        if ( menu == null )
            return;
        
        final Widget widget = menuWrapperMap.get( menu );
        
        if ( widget == null )
            return;
        
        widget.setVisible( visible );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMenuVisible( Menu menu )
    {
        final Widget widget = menuWrapperMap.get( menu );
        
        if ( widget == null )
            return ( false );
        
        return ( widget.isVisible() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMenuGroup( MenuGroup menuGroup )
    {
        this.menuGroup = menuGroup;
        
        if ( ( menuGroup != null ) && waitingForSaveButton )
        {
            this.saveButton = createAccessorWidget( "Save", getMenuGroup().getMenuSystem().getAccessorDescription() );
            saveButton.setUserObject( "save" );
            saveButton.setVisible( false );
            if ( buttonsAlignment.isBottom() )
                westPanel.addWidget( saveButton, BorderLayout.Area.NORTH );
            else
                westPanel.addWidget( saveButton, BorderLayout.Area.SOUTH );
            
            saveButton.addButtonListener( buttonListener );
            
            waitingForSaveButton = false;
            westPanel = null;
            buttonsAlignment = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MenuGroup getMenuGroup()
    {
        return ( menuGroup );
    }
    
    protected BorderLayout createMainBorderLayout()
    {
        return ( new BorderLayout( 0f, 10f ) );
    }
    
    protected LayoutManager createWestLayout()
    {
        return ( new BorderLayout( 50f, 0f, 50f, 50f ) );
    }
    
    protected LayoutManager createAccessorLayout( ListLayout.Alignment buttonsAlignment, boolean withSaveButton )
    {
        ListLayout layout;
        
        if ( withSaveButton )
        {
            layout = new ListLayout( ListLayout.Orientation.VERTICAL, 10f );
        }
        else
        {
            layout = new ListLayout( ListLayout.Orientation.VERTICAL, 10f, 50f, 0f, 50f, 50f );
        }
        
        layout.setAlignment( buttonsAlignment );
        
        return ( layout );
    }
    
    public ButtonsLeftMenusCenterMenuGroupWidget( float width, float height, float resolutionX, float resolutionY, ListLayout.Alignment buttonsAlignment, boolean withSaveButton )
    {
        super( false, width, height );
        
        setResolution( resolutionX, resolutionY );
        
        BorderLayout layout = createMainBorderLayout();
        this.setLayout( layout );
        
        if ( withSaveButton )
        {
            Panel westPanel = new Panel( false, 200f, height );
            westPanel.setLayout( createWestLayout() );
            
            this.accessorPanel = new Panel( false, 0f, 0f );
            accessorPanel.setLayout( createAccessorLayout( buttonsAlignment, withSaveButton ) );
            
            westPanel.addWidget( accessorPanel, BorderLayout.Area.CENTER );
            this.addWidget( westPanel, BorderLayout.Area.WEST );
            
            this.waitingForSaveButton = true;
            this.westPanel = westPanel;
            this.buttonsAlignment = buttonsAlignment;
        }
        else
        {
            this.accessorPanel = new Panel( false, 200f, height );
            accessorPanel.setLayout( createAccessorLayout( buttonsAlignment, withSaveButton ) );
            
            this.addWidget( accessorPanel, BorderLayout.Area.WEST );
            
            this.saveButton = null;
            this.waitingForSaveButton = false;
        }
        
        this.menuPanel = new Panel( false, 0f, 0f );
        menuPanel.setLayout( new CenterLayout() );
        
        this.addWidget( menuPanel, BorderLayout.Area.CENTER );
    }
}
