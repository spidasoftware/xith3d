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

import java.util.ArrayList;
import java.util.HashMap;

import org.jagatoo.datatypes.NamedObject;
import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.base.AbstractButton;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.ButtonListener;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Button;

/**
 * The {@link MenuGroup} manages {@link Menu} items and notifies
 * {@link MenuSystemListener}s of state changes.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class MenuGroup
{
    private final String name;
    
    private MenuGroupWidget menuGroupWidget = null;
    private Menu currentActiveMenu = null;
    
    private boolean visible = false;
    
    private ArrayList< Object[] > earlyObjects = new ArrayList< Object[] >();
    
    private final ArrayList< Button > menuAccessors = new ArrayList< Button >();
    private final ArrayList< Menu > menus = new ArrayList< Menu >();
    private final HashMap< String, Menu > menuMap = new HashMap< String, Menu >();
    private final HashMap< Button, Menu > accessorMenuMap = new HashMap< Button, Menu >();
    private final HashMap< Menu, Button > menuAccessorMap = new HashMap< Menu, Button >();
    
    private MenuSystem menuSystem = null;
    
    private MenuGroup prevMenuGroup = null;
    
    public final String getName()
    {
        return ( name );
    }
    
    public final MenuSystem getMenuSystem()
    {
        return ( menuSystem );
    }
    
    public void setPreviousMenuGroup( MenuGroup menuGroup )
    {
        if ( menuGroup == null )
        {
            throw new IllegalArgumentException( "the given MenuGroup must not be null" );
        }
        
        if ( menuGroup == this )
        {
            throw new IllegalArgumentException( "the given MenuGroup must be a different one than this" );
        }
        
        this.prevMenuGroup = menuGroup;
    }
    
    public MenuGroup getPreviousMenuGroup()
    {
        return ( prevMenuGroup );
    }
    
    public void fireOnSettingChanged( Menu menu, String setting, Object value )
    {
        if ( getMenuSystem() != null )
        {
            getMenuSystem().fireOnSettingChanged( menu, setting, value );
        }
        
        if ( getMenuGroupWidget() instanceof ButtonsLeftMenusCenterMenuGroupWidget )
        {
            Button saveButton = ( (ButtonsLeftMenusCenterMenuGroupWidget)getMenuGroupWidget() ).getSaveButton();
            if ( saveButton != null )
            {
                saveButton.setVisible( true );
            }
        }
    }
    
    public boolean fireOnMenuActionPerformed( String action )
    {
        if ( getCurrentActiveMenu() == null )
        {
            if ( getMenuSystem() != null )
            {
                getMenuSystem().fireOnMenuActionPerformed( this, this.getCurrentActiveMenu(), action );
                
                return ( true );
            }
        }
        else
        {
            if ( getCurrentActiveMenu().prepareAction( action ) )
            {
                if ( getMenuSystem() != null )
                {
                    if ( getMenuSystem().fireOnMenuActionPerformed( this, this.getCurrentActiveMenu(), action ) )
                    {
                        return ( getCurrentActiveMenu().onActionConsumed( action ) );
                    }
                }
            }
        }
        
        return ( false );
    }
    
    /**
     * Sets the current active Menu.
     */
    public void setCurrentActiveMenu( Menu menu )
    {
        if ( currentActiveMenu != menu )
        {
            if ( currentActiveMenu != null )
            {
                menuGroupWidget.setMenuVisible( currentActiveMenu, false );
            }
            
            menuGroupWidget.setMenuVisible( menu, true );
            
            if ( menu != null )
            {
                ( (Widget)menu ).requestFocus();
            }
            
            if ( menuGroupWidget.getMenuPanel().getLayout() != null )
                menuGroupWidget.getMenuPanel().getLayout().doLayout( menuGroupWidget.getMenuPanel() );
        }
        
        currentActiveMenu = menu;
    }
    
    /**
     * @return the current active Menu.
     */
    public final Menu getCurrentActiveMenu()
    {
        return ( currentActiveMenu );
    }
    
    private static long lastButtonClick = 0L;
    
    private final ButtonListener accessorListener = new ButtonListener()
    {
        public void onButtonClicked( AbstractButton button, Object userObject )
        {
            final long t = System.currentTimeMillis();
            final long dt = t - lastButtonClick;
            lastButtonClick = t;
            
            /*
             * For some very strange reason the Button, that takes the clicked
             * Button's place in the folowing menu, is sometimes also clicked.
             * This is an ugly workaround to prevent this.
             */
            if ( dt > 30L )
            {
                getMenuSystem().onOtherMenuRequested( userObject );
            }
        }
    };
    
    /**
     * @return the {@link MenuGroupWidget}, that handles the layout of this {@link MenuGroup}.
     */
    public final MenuGroupWidget getMenuGroupWidget()
    {
        return ( menuGroupWidget );
    }
    
    /**
     * Adds a {@link Menu} item and an accessor Widget (like a button)
     * to the {@link MenuGroup}.
     * 
     * @param accessorCaption
     * @param caption
     * @param menu
     * 
     * @return the accessor {@link Button}.
     */
    protected Button addMenuImpl( String accessorCaption, String caption, Menu menu )
    {
        Button accessor = menuGroupWidget.addAccessorWidget( accessorCaption, getMenuSystem().getAccessorDescription() );
        menuGroupWidget.addMenu( getMenuSystem(), caption, menu );
        accessor.setUserObject( menu );
        accessor.addButtonListener( accessorListener );
        
        if ( getMenusCount() == 1 )
        {
            this.currentActiveMenu = getMenu( 0 );
        }
        
        return ( accessor );
    }
    
    /**
     * Adds a {@link Menu} item and an accessor Widget (like a {@link Button})
     * to the {@link MenuGroup}.
     * 
     * @param accessorCaption
     * @param caption
     * @param menu
     */
    public final void addMenu( String accessorCaption, String caption, Menu menu )
    {
        if ( !( menu instanceof Widget ) )
            throw new IllegalArgumentException( "A Menu must be an instance if Widget." );
        
        menus.add( menu );
        menuMap.put( menu.getName(), menu );
        menu.setMenuGroup( this );
        
        if ( menuGroupWidget == null )
        {
            earlyObjects.add( new Object[] { accessorCaption, caption, menu } );
            
            return;
        }
        
        final Button accessor = addMenuImpl( accessorCaption, caption, menu );
        
        if ( accessor != null )
        {
            menuAccessors.add( accessor );
            accessorMenuMap.put( accessor, menu );
            menuAccessorMap.put( menu, accessor );
        }
        
        menu.setMenuSystem( menuSystem );
    }
    
    /**
     * Adds a {@link Button} to the {@link MenuGroup}.
     * 
     * @param caption the caption of the accessor {@link Button}
     * @param target the processed value of target is either
     *               its name, if it is a {@link NamedObject},
     *               or the value of the toString() method.
     */
    public void addActionButton( String caption, Object target )
    {
        if ( menuGroupWidget == null )
        {
            earlyObjects.add( new Object[] { caption, target } );
            
            return;
        }
        
        Button button = menuGroupWidget.addAccessorWidget( caption, getMenuSystem().getAccessorDescription() );
        button.setUserObject( target );
        button.addButtonListener( accessorListener );
    }
    
    public final java.util.List< Menu > getMenus()
    {
        return ( menus );
    }
    
    public final Menu getMenu( int index )
    {
        return ( menus.get( index ) );
    }
    
    public final Menu getMenu( String name )
    {
        return ( menuMap.get( name ) );
    }
    
    public final int getMenusCount()
    {
        return ( menus.size() );
    }
    
    public void setVisible( boolean visible )
    {
        this.visible = visible;
        
        if ( menuGroupWidget != null )
        {
            menuGroupWidget.setVisible( visible );
            menuGroupWidget.getAccessorPanel().requestFocus();
        }
    }
    
    public final boolean isVisible()
    {
        return ( visible );
    }
    
    protected abstract MenuGroupWidget createMenuSystemWidget( float width, float height, float resolutionX, float resolutionY );
    
    /**
     * 
     * @param accessorDesc
     * @param menuBGColor
     * @param menuBorderColor
     * @param menuCaptionFont
     * @param menuFontColor
     */
    protected void initMenus( Button.Description accessorDesc, Colorf menuBGColor, Colorf menuBorderColor, HUDFont menuCaptionFont, Colorf menuFontColor )
    {
    }
    
    protected void init( MenuSystem menuSystem )
    {
        this.menuSystem = menuSystem;
        
        Dim2f size = Dim2f.fromPool();
        menuSystem.getHUD().getCoordinatesConverter().getSizePixels2HUD( (int)menuSystem.getWidth(), (int)menuSystem.getHeight(), size );
        this.menuGroupWidget = createMenuSystemWidget( size.getWidth(), size.getHeight(), menuSystem.getWidth(), menuSystem.getHeight() );
        menuGroupWidget.setZIndex( menuSystem.getMenuZIndex() );
        Dim2f.toPool( size );
        
        menuGroupWidget.setVisible( this.isVisible() );
        
        menuGroupWidget.setMenuGroup( this );
        menuSystem.getContainer().addWidgetCentered( menuGroupWidget );
        
        menus.clear();
        for ( int i = 0; i < earlyObjects.size(); i++ )
        {
            final Object[] eo = earlyObjects.get( i );
            
            if ( eo.length == 3 )
                addMenu( (String)eo[ 0 ], (String)eo[ 1 ], (Menu)eo[ 2 ] );
            else if ( eo.length == 2 )
                addActionButton( (String)eo[ 0 ], eo[ 1 ] );
        }
        earlyObjects = null;
        
        initMenus( menuSystem.getAccessorDescription(), menuSystem.getMenuBackgroundColor(), menuSystem.getMenuBorderColor(), menuSystem.getMenuCaptionFont(), menuSystem.getMenuCaptionFontColor() );
    }
    
    /**
     * Creates a new {@link MenuGroup}.
     * 
     * @param name
     * @param previousMenuGroup
     */
    public MenuGroup( String name, MenuGroup previousMenuGroup )
    {
        this.name = name;
        
        if ( previousMenuGroup != null )
            setPreviousMenuGroup( previousMenuGroup );
    }
    
    /**
     * Creates a new {@link MenuGroup}.
     * 
     * @param name
     */
    public MenuGroup( String name )
    {
        this( name, null );
    }
}
