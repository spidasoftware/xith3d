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
package org.xith3d.ui.hud.menusystem.menus;

import org.xith3d.ui.hud.widgets.Panel;

import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.layout.BorderSettableLayoutManager;
import org.xith3d.ui.hud.menusystem.Menu;
import org.xith3d.ui.hud.menusystem.MenuGroup;
import org.xith3d.ui.hud.menusystem.MenuSystem;

/**
 * A base implementation of the {@link Menu} interface
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class MenuBase extends Panel implements Menu
{
    private final String[] acceptedActions;
    
    protected boolean minimalHeightSet = false;
    
    private MenuSystem menuSystem = null;
    private MenuGroup menuGroup = null;
    
    protected final String[] getAcceptedActions()
    {
        return ( acceptedActions );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setMenuSystem( MenuSystem menuSystem )
    {
        this.menuSystem = menuSystem;
    }
    
    /**
     * {@inheritDoc}
     */
    public final MenuSystem getMenuSystem()
    {
        return ( menuSystem );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setMenuGroup( MenuGroup menuGroup )
    {
        this.menuGroup = menuGroup;
    }
    
    /**
     * {@inheritDoc}
     */
    public final MenuGroup getMenuGroup()
    {
        return ( menuGroup );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean prepareAction( String action )
    {
        for ( int i = 0; i < acceptedActions.length; i++ )
        {
            if ( action.equals( acceptedActions[ i ] ) )
            {
                return ( true );
            }
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean onActionConsumed( String action )
    {
        for ( int i = 0; i < acceptedActions.length; i++ )
        {
            if ( action.equals( acceptedActions[ i ] ) )
            {
                return ( true );
            }
        }
        
        return ( false );
    }
    
    public void setMinimalHeight()
    {
        if ( !isInitialized() && !isInitializing() )
        {
            minimalHeightSet = true;
            return;
        }
        
        getContainer().update();
        
        final float borderHeight = ( getBorder() != null ) ? getBorder().getTopHeight() + getBorder().getBottomHeight() : 0f;
        final float paddingHeight = ( ( getLayout() != null ) && ( getLayout() instanceof BorderSettableLayoutManager ) ) ? ( (BorderSettableLayoutManager)getLayout() ).getBorderBottom() : 0f;
        final Widget lastWidget = getWidgets().get( getWidgets().size() - 1 );
        
        setSize( getWidth(), lastWidget.getTop() + lastWidget.getHeight() + borderHeight + paddingHeight );
    }
    
    protected abstract void initWidgets();
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        super.init();
        
        initWidgets();
        
        if ( minimalHeightSet )
        {
            minimalHeightSet = false;
            setMinimalHeight();
        }
    }
    
    public MenuBase( float width, float height, String name, String[] acceptedActions )
    {
        super( false, width, height );
        
        this.setName( name );
        
        this.acceptedActions = acceptedActions;
    }
    
    public MenuBase( float width, String name, String[] acceptedActions )
    {
        this( width, 0f, name, acceptedActions );
        
        setMinimalHeight();
    }
}
