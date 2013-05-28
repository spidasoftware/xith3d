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
package org.xith3d.ui.hud.contextmenu;

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.widgets.Label;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DefaultContextMenuItem extends Label implements ContextMenuItem
{
    private Colorf hoveredColor = Colorf.parseColor( "6E6EFF" );
    
    private ContextMenu contextMenu = null;
    
    public void setHoveredColor( Colorf hoveredColor )
    {
        this.hoveredColor = hoveredColor;
    }
    
    public Colorf getHoveredColor()
    {
        return ( hoveredColor );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean clickCausesMenuHide()
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setContextMenu( ContextMenu contextMenu )
    {
        this.contextMenu = contextMenu;
        
        if ( contextMenu == null )
        {
            this.detach();
        }
        else
        {
            contextMenu.getMenuItemsContainer().addWidget( this );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final ContextMenu getContextMenu()
    {
        return ( contextMenu );
    }
    
    protected void markHovered()
    {
        this.setBackgroundColor( getHoveredColor() );
    }
    
    protected void markFree()
    {
        this.setBackgroundColor( null);
    }
    
    @Override
    protected void onMouseEntered( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseEntered( isTopMost, hasFocus );
        
        if ( isTopMost )
            markHovered();
    }
    
    @Override
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseExited( isTopMost, hasFocus );
        
        //if ( isTopMost )
            markFree();
    }
    
    protected void onItemClicked()
    {
        contextMenu.onItemClicked( this );
    }
    
    @Override
    protected void onMouseButtonReleased( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonReleased( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isTopMost )
        {
            onItemClicked();
            
            markFree();
        }
    }
    
    public DefaultContextMenuItem( String text, Label.Description labelDesc, Object userObject )
    {
        super( 100f, 15f, text, labelDesc );
        
        this.setUserObject( userObject );
    }
    
    public DefaultContextMenuItem( String text, Object userObject )
    {
        this( text, null, userObject );
    }
    
    public DefaultContextMenuItem( String text, Label.Description labelDesc )
    {
        this( text, labelDesc, null );
    }
    
    public DefaultContextMenuItem( String text )
    {
        this( text, null, null );
    }
}
