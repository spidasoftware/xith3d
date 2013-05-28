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
package org.xith3d.ui.hud.listmodels;

import java.util.ArrayList;

import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.base.LabeledStateButton;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Checkbox;
import org.xith3d.ui.hud.widgets.List;

/**
 * The {@link TextListModel} converts all items to String through {@link String#valueOf(Object)}.<br>
 * It also honors the font and font-color properties of the {@link List}.<br>
 * A {@link LabeledStateButton} is used to render the items to the {@link List}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class StateButtonListModel extends TextListModel
{
    private ArrayList<Boolean> states = null;
    
    /**
     * Sets an item's state.
     * 
     * @param itemIndex
     * @param state the item's state.
     */
    public void setItemState( int itemIndex, boolean state )
    {
        if ( states == null )
        {
            this.states = new ArrayList<Boolean>();
        }
        
        if ( states.size() < getItemsCount() )
        {
            for ( int i = states.size(); i < getItemsCount(); i++ )
            {
                states.add( null );
            }
        }
        
        states.set( itemIndex, state );
    }
    
    /**
     * Gets an item's state.
     * 
     * @param itemIndex
     * @return the items state.
     */
    public final boolean getItemState( int itemIndex )
    {
        if ( ( states == null ) || ( itemIndex >= states.size() ) )
            return ( false );
        
        Boolean state = states.get( itemIndex );
        
        if ( state == null )
            return ( false );
        
        return ( state );
    }
    
    @Override
    protected void prepareWidgetImpl( float listContentWidth, Widget widget, Object item, int itemIndex )
    {
        LabeledStateButton sb = (LabeledStateButton)widget;
        
        if ( getList() instanceof List )
        {
            sb.setAlignment( ( (List)getList() ).getAlignment() );
        }
        
        Colorf textColor = this.getItemColor( itemIndex );
        HUDFont font = this.getItemFont( itemIndex );
        
        if ( textColor == null )
            textColor = ( (List)getList() ).getFontColor();
        
        if ( font == null )
            font = ( (List)getList() ).getFont();
        
        sb.setFontColor( textColor );
        sb.setFont( font );
        
        if ( isSelected( itemIndex ) )
        {
            if ( ( (List)getList() ).getSelectionFontColor() != null )
            {
                sb.setFontColor( ( (List)getList() ).getSelectionFontColor() );
            }
        }
        
        sb.setState( getItemState( itemIndex ) );
        
        applyValueToWidget( item, sb );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hoverNeedsRedraw()
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkOnMouseButtonPressedImpl( int itemIndex, float itemTop, float itemBottom, float mouseX, float mouseY )
    {
        LabeledStateButton sb = (LabeledStateButton)getWidgetImpl( itemIndex );
        
        if ( __HUD_base_PrivilegedAccess.isMouseOverStateImage( sb, mouseX, mouseY - itemTop ) )
        {
            setItemState( itemIndex, !getItemState( itemIndex ) );
            
            markListDirty();
            
            return ( false );
        }
        
        return ( true );
    }
    
    @Override
    protected float getItemHeightImpl( int itemIndex )
    {
        LabeledStateButton sb = (LabeledStateButton)getWidgetImpl( itemIndex );
        
        applyValueToWidget( String.valueOf( getItem( itemIndex ) ), sb );
        
        Dim2f buffer = Dim2f.fromPool();
        sb.getMinimalSize( buffer );
        float minHeight = buffer.getHeight();
        Dim2f.toPool( buffer );
        
        return ( minHeight );
    }
    
    @Override
    protected float getMinItemWidthImpl( int itemIndex )
    {
        LabeledStateButton sb = (LabeledStateButton)getWidgetImpl( itemIndex );
        
        applyValueToWidget( String.valueOf( getItem( itemIndex ) ), sb );
        
        Dim2f buffer = Dim2f.fromPool();
        sb.getMinimalSize( buffer );
        float minWidth = buffer.getWidth();
        Dim2f.toPool( buffer );
        
        return ( minWidth );
    }
    
    @Override
    protected LabeledStateButton createWidget()
    {
        Checkbox checkbox = new Checkbox( 100f, 16f, "StateButton" );
        checkbox.setNoBackground();
        
        return ( checkbox );
    }
    
    @Override
    protected void checkWidgetType( Widget widget )
    {
        if ( !( widget instanceof LabeledStateButton ) )
            throw new IllegalArgumentException( "The given Widget is not an instanceof LabeledStateButton." );
    }
    
    public StateButtonListModel( java.util.List<Object> items, LabeledStateButton widget )
    {
        super( items, widget );
    }
    
    public StateButtonListModel( java.util.List<Object> items )
    {
        this( items, null );
    }
    
    public StateButtonListModel()
    {
        this( new java.util.ArrayList<Object>() );
    }
}
