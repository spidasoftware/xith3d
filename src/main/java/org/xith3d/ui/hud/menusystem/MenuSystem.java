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
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.actions.AbstractInvokableInputAction;
import org.jagatoo.input.actions.InvokableInputAction;
import org.jagatoo.input.devices.InputDevice;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.DigitalDeviceComponent;
import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Button;

/**
 * The {@link MenuSystem} is a manager class, that manages instances of
 * {@link MenuGroup}, which manage instances of {@link Menu}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MenuSystem
{
    private Button.Description accessorDesc = null;
    private Colorf backgroundColor = Colorf.parseColor( "#846D2F" );
    private Colorf borderColor = Colorf.DARK_GRAY;
    private HUDFont captionFont = HUDFont.getFont( "Baveuse", HUDFont.PLAIN, 24 );
    private Colorf captionFontColor = Colorf.ORANGE;
    
    private final HUD hud;
    private final WidgetContainer parentContainer;
    private final float width;
    private final float height;
    private final int menuZIndex;
    private boolean visible = false;
    
    private final ArrayList< MenuGroup > menuGroups = new ArrayList< MenuGroup >();
    private final HashMap< String, MenuGroup > menuGroupsMap = new HashMap< String, MenuGroup >();
    
    private MenuGroup currentMenuGroup = null;
    
    private final ArrayList< MenuSystemListener > listeners = new ArrayList< MenuSystemListener >();
    
    private DigitalDeviceComponent globalMenuSystemAccessor = null;
    
    private final InvokableInputAction menuAccessAction = new AbstractInvokableInputAction( 0 )
    {
        public String invokeAction( InputDevice device, DeviceComponent comp, int delta, int state, long nanoTime ) throws InputSystemException
        {
            if ( state == 0 )
                return ( null );
            
            if ( MenuSystem.this.isVisible() )
            {
                if ( ( getCurrentMenuGroup() != null ) && ( getCurrentMenuGroup().getPreviousMenuGroup() != null ) )
                {
                    setCurrentMenuGroup( getCurrentMenuGroup().getPreviousMenuGroup() );
                }
                else
                {
                    setVisible( false );
                }
            }
            else
            {
                setVisible( true );
            }
            
            return ( null );
        }
    };
    
    public void setMenuSystemAccessor( DigitalDeviceComponent accessor )
    {
        if ( globalMenuSystemAccessor != null )
            globalMenuSystemAccessor.unbindAction( menuAccessAction );
        
        this.globalMenuSystemAccessor = accessor;
        
        if ( globalMenuSystemAccessor != null )
            globalMenuSystemAccessor.bindAction( menuAccessAction );
    }
    
    public final DigitalDeviceComponent getMenuSystemAccessor()
    {
        return ( globalMenuSystemAccessor );
    }
    
    /**
     * @return the assotiated {@link HUD}.
     */
    public final HUD getHUD()
    {
        return ( hud );
    }
    
    /**
     * @return the {@link WidgetContainer}, the {@link MenuGroup}'s {@link Menu}s
     * are added to. This might be the HUD itself!
     */
    public final WidgetContainer getContainer()
    {
        return ( parentContainer );
    }
    
    public final float getWidth()
    {
        return ( width );
    }
    
    public final float getHeight()
    {
        return ( height );
    }
    
    public final int getMenuZIndex()
    {
        return ( menuZIndex );
    }
    
    public void setVisible( boolean visible )
    {
        final boolean wasVisible = this.visible;
        
        this.visible = visible;
        
        if ( !wasVisible && visible )
        {
            fireOnMenuSystemEntered();
        }
        else if ( wasVisible && !visible )
        {
            fireOnMenuSystemExited();
        }
        
        if ( currentMenuGroup != null )
        {
            currentMenuGroup.setVisible( visible );
        }
        
        if ( visible )
        {
            hud.disposeFocus();
            
            if ( currentMenuGroup != null )
            {
                currentMenuGroup.getMenuGroupWidget().getAccessorPanel().requestFocus();
            }
        }
    }
    
    public final boolean isVisible()
    {
        return ( visible );
    }
    
    public final Colorf getMenuCaptionFontColor()
    {
        return ( captionFontColor );
    }
    
    public final void setAccessorDescription( Button.Description desc )
    {
        this.accessorDesc = desc;
    }
    
    public final void setMenuBackgroundColor( Colorf color )
    {
        this.backgroundColor = color;
    }
    
    public final Colorf getMenuBackgroundColor()
    {
        return ( backgroundColor );
    }
    
    public final void setMenuBorderColor( Colorf color )
    {
        this.borderColor = color;
    }
    
    public final Colorf getMenuBorderColor()
    {
        return ( borderColor );
    }
    
    public final void setMenuCaptionFont( HUDFont font )
    {
        this.captionFont = font;
    }
    
    public final HUDFont getMenuCaptionFont()
    {
        return ( captionFont );
    }
    
    public final void setMenuCaptionFontColor( Colorf color )
    {
        this.captionFontColor = color;
    }
    
    public final Button.Description getAccessorDescription()
    {
        return ( accessorDesc );
    }
    
    public void addMenuGroup( MenuGroup group )
    {
        menuGroups.add( group );
        menuGroupsMap.put( group.getName(), group );
        
        if ( menuGroups.size() == 1 )
            setCurrentMenuGroup( group );
        
        group.init( this );
    }
    
    public final MenuGroup getMenuGroup( int index )
    {
        return ( menuGroups.get( index ) );
    }
    
    public final MenuGroup getMenuGroup( String name )
    {
        return ( menuGroupsMap.get( name ) );
    }
    
    public final int getMenuGroupsCount()
    {
        return ( menuGroups.size() );
    }
    
    public void setCurrentMenuGroup( MenuGroup menuGroup )
    {
        if ( menuGroup == null )
        {
            throw new IllegalArgumentException( "menuGroup must not be" );
        }
        
        this.currentMenuGroup = menuGroup;
        
        for ( int i = 0; i < menuGroups.size(); i++ )
        {
            menuGroups.get( i ).setVisible( false );
        }
        
        menuGroup.setVisible( this.isVisible() );
    }
    
    public MenuGroup setCurrentMenuGroup( String menuGroupName )
    {
        final MenuGroup menuGroup = menuGroupsMap.get( menuGroupName );
        
        if ( menuGroup == null )
        {
            throw new IllegalArgumentException( "MenuGroup \"" + menuGroupName + "\" not found!" );
        }
        
        setCurrentMenuGroup( menuGroup );
        
        return ( menuGroup );
    }
    
    public final MenuGroup getCurrentMenuGroup()
    {
        return ( currentMenuGroup );
    }
    
    public MenuGroup findMenuGroup( Object menuID )
    {
        if ( menuID == null )
            return ( null );
        
        for ( int i = 0; i < menuGroups.size(); i++ )
        {
            final MenuGroup mg = menuGroups.get( i );
            
            if ( mg.equals( menuID ) )
            {
                return ( mg );
            }
            /*
            else if ( mg.equals( String.valueOf( menuID ) ) )
            {
                return ( mg );
            }
            */
            else if ( ( menuID instanceof NamedObject ) && ( mg.getName().equals( menuID ) ) )
            {
                return ( mg );
            }
            else if ( mg.getName().equals( String.valueOf( menuID ) ) )
            {
                return ( mg );
            }
            
            for ( int j = 0; j < mg.getMenusCount(); j++ )
            {
                final Menu menu = mg.getMenu( j );
                
                if ( menu.equals( menuID ) )
                {
                    return ( mg );
                }
                /*
                else if ( menu.equals( String.valueOf( menuID ) ) )
                {
                    return ( mg );
                }
                */
                else if ( ( menuID instanceof NamedObject ) && ( menu.getName().equals( menuID ) ) )
                {
                    return ( mg );
                }
                else if ( menu.getName().equals( String.valueOf( menuID ) ) )
                {
                    return ( mg );
                }
            }
        }
        
        return ( null );
    }
    
    public Menu findMenu( Object menuID )
    {
        if ( menuID == null )
            return ( null );
        
        for ( int i = 0; i < menuGroups.size(); i++ )
        {
            final MenuGroup mg = menuGroups.get( i );
            
            for ( int j = 0; j < mg.getMenusCount(); j++ )
            {
                final Menu menu = mg.getMenu( j );
                
                if ( menu.equals( menuID ) )
                {
                    return ( menu );
                }
                else if ( menu.equals( menuID.toString() ) )
                {
                    return ( menu );
                }
                else if ( ( menuID instanceof NamedObject ) && ( menu.getName().equals( menuID ) ) )
                {
                    return ( menu );
                }
                else if ( menu.getName().equals( menuID.toString() ) )
                {
                    return ( menu );
                }
            }
        }
        
        return ( null );
    }
    
    /**
     * Adds a listner to the MenuSystem, that is notified of MenuSystem state changes.
     * 
     * @param l
     */
    public final void addMenuSystemListener( MenuSystemListener l )
    {
        listeners.add( l );
    }
    
    public final void removeMenuSystemListener( MenuSystemListener l )
    {
        listeners.remove( l );
    }
    
    protected boolean fireBeforeMenuSystemStateChanged( String currentMenu, Object target )
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            if ( listeners.get( i ).beforeMenuStateChanged( this, currentMenu, target ) )
                return ( true );
        }
        
        return ( false );
    }
    
    protected void fireOnMenuSystemStateChanged( String oldMenu, Object target )
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onMenuStateChanged( this, oldMenu, target );
        }
    }
    
    protected void fireOnSettingChanged( Menu menu, String setting, Object value )
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onSettingChanged( this, menu, setting, value );
        }
    }
    
    protected boolean fireOnMenuActionPerformed( MenuGroup menuGroup, Menu menu, String action )
    {
        boolean consumed = false;
        
        for ( int i = 0; i < listeners.size(); i++ )
        {
            consumed = listeners.get( i ).onMenuActionPerformed( this, menuGroup, menu, action ) || consumed;
        }
        
        return ( consumed );
    }
    
    protected void fireOnMenuSystemEntered()
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onMenuSystemEntered( this );
        }
    }
    
    protected void fireOnMenuSystemExited()
    {
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).onMenuSystemExited( this );
        }
    }
    
    protected void onOtherMenuRequested( Object menuID )
    {
        if ( menuID == null )
        {
            setVisible( false );
            return;
        }
        
        final MenuGroup currentMG = getCurrentMenuGroup();
        final Menu currentMenu = ( currentMG != null ) ? currentMG.getCurrentActiveMenu() : null;
        
        final MenuGroup mg = findMenuGroup( menuID );
        final Menu menu = findMenu( menuID );
        
        // fire before-event
        if ( ( currentMG != mg ) || ( currentMenu != menu ) )
        {
            if ( ( mg == null ) && ( menu == null ) )
            {
                if ( ( currentMenu == null ) && ( fireBeforeMenuSystemStateChanged( currentMG.getName(), menuID ) ) )
                    return;
                else if ( ( currentMenu != null ) && ( fireBeforeMenuSystemStateChanged( currentMenu.getName(), menuID ) ) )
                    return;
            }
            else if ( menu == null )
            {
                if ( currentMenu != mg.getCurrentActiveMenu() )
                {
                    if ( ( currentMenu == null ) && ( fireBeforeMenuSystemStateChanged( currentMG.getName(), mg.getName() ) ) )
                        return;
                    else if ( ( currentMenu != null ) && ( fireBeforeMenuSystemStateChanged( currentMenu.getName(), mg.getName() ) ) )
                        return;
                }
            }
            else
            {
                if ( currentMenu != menu )
                {
                    if ( ( currentMenu == null ) && ( fireBeforeMenuSystemStateChanged( currentMG.getName(), menu.getName() ) ) )
                        return;
                    else if ( ( currentMenu != null ) && ( fireBeforeMenuSystemStateChanged( currentMenu.getName(), menu.getName() ) ) )
                        return;
                }
            }
        }
        
        // fire state-change-event
        if ( mg == null )
        {
            if ( currentMenu == null )
                fireOnMenuSystemStateChanged( currentMG.getName(), menuID );
            else
                fireOnMenuSystemStateChanged( currentMenu.getName(), menuID );
        }
        else
        {
            if ( ( currentMG != mg ) || ( currentMenu != menu ) )
            {
                if ( menu == null )
                {
                    currentMG.setCurrentActiveMenu( null );
                    mg.setCurrentActiveMenu( null );
                    this.setCurrentMenuGroup( mg );
                    
                    if ( currentMenu != mg.getCurrentActiveMenu() )
                    {
                        if ( currentMenu == null )
                            fireOnMenuSystemStateChanged( currentMG.getName(), mg.getName() );
                        else
                            fireOnMenuSystemStateChanged( currentMenu.getName(), mg.getName() );
                    }
                }
                else
                {
                    currentMG.setCurrentActiveMenu( null );
                    mg.setCurrentActiveMenu( menu );
                    this.setCurrentMenuGroup( mg );
                    
                    if ( currentMenu != menu )
                    {
                        if ( currentMenu == null )
                            fireOnMenuSystemStateChanged( currentMG.getName(), menu.getName() );
                        else
                            fireOnMenuSystemStateChanged( currentMenu.getName(), menu.getName() );
                    }
                }
            }
        }
    }
    
    /**
     * Creates a new {@link MenuGroup}.
     * 
     * @param hud the assotiated {@link HUD}
     * @param parentContainer the {@link WidgetContainer}, the {@link MenuGroup}'s {@link Menu}s
     *                        are added to. This might be the HUD itself!
     * @param width the design with of the {@link Menu}s
     * @param height the design height of the {@link Menu}s
     * @param menuZIndex
     */
    public MenuSystem( HUD hud, WidgetContainer parentContainer, float width, float height, int menuZIndex )
    {
        if ( ( hud == null ) || ( parentContainer == null ) )
            throw new IllegalArgumentException( "Neither hud nor parentContainer must be null" );
        
        this.hud = hud;
        this.parentContainer = parentContainer;
        this.width = width;
        this.height = height;
        this.menuZIndex = menuZIndex;
    }
    
    /**
     * Creates a new {@link MenuGroup}.
     * 
     * @param hud the assotiated {@link HUD}
     * @param width the design with of the {@link Menu}s
     * @param height the design height of the {@link Menu}s
     * @param menuZIndex
     */
    public MenuSystem( HUD hud, float width, float height, int menuZIndex )
    {
        this( hud, hud.getContentPane(), width, height, menuZIndex );
    }
    
    /**
     * Creates a new {@link MenuGroup}.
     * 
     * @param hud the assotiated {@link HUD}
     * @param width the design with of the {@link Menu}s
     * @param height the design height of the {@link Menu}s
     */
    public MenuSystem( HUD hud, float width, float height )
    {
        this( hud, hud.getContentPane(), width, height, 1000 );
    }
    
    /**
     * Creates a new {@link MenuGroup}.
     * 
     * @param hud the assotiated {@link HUD}
     */
    public MenuSystem( HUD hud )
    {
        this( hud, hud.getContentPane(), hud.getResX(), hud.getResY(), 1000 );
    }
}
