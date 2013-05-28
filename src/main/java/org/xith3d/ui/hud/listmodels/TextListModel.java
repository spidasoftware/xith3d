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
import org.xith3d.ui.hud.base.TextWidget;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.hud.widgets.List;

/**
 * The {@link TextListModel} converts all items to String through {@link String#valueOf(Object)}.<br>
 * It also honors the font and font-color properties of the {@link List}.<br>
 * A {@link Label} is used to render the items to the {@link List}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextListModel extends DefaultAbstractListModel
{
    private final Widget widget;
    
    //private final ArrayList<MultilineText> multiLines = new ArrayList<MultilineText>();
    private ArrayList<Colorf> itemColors = null;
    private ArrayList<HUDFont> itemFonts = null;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Widget[] getUsedWidgets()
    {
        return ( new Widget[] { widget } );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Widget getWidgetImpl( int itemIndex )
    {
        return ( widget );
    }
    
    /**
     * Sets an item's color.
     * 
     * @param itemIndex
     * @param color null for list default.
     */
    public void setItemColor( int itemIndex, Colorf color )
    {
        if ( itemColors == null )
        {
            if ( color == null )
                return;
            
            this.itemColors = new ArrayList<Colorf>();
        }
        
        if ( itemColors.size() < getItemsCount() )
        {
            for ( int i = itemColors.size(); i < getItemsCount(); i++ )
            {
                itemColors.add( null );
            }
        }
        
        itemColors.set( itemIndex, color );
    }
    
    /**
     * Gets an item's color.
     * 
     * @param itemIndex
     * @return the items color (null for list default).
     */
    public final Colorf getItemColor( int itemIndex )
    {
        if ( ( itemColors == null ) || ( itemIndex >= itemColors.size() ) )
            return ( null );
        
        return ( itemColors.get( itemIndex ) );
    }
    
    /**
     * Sets an item's font.
     * 
     * @param itemIndex
     * @param font null for list default.
     */
    public void setItemFont( int itemIndex, HUDFont font )
    {
        if ( itemFonts == null )
        {
            if ( font == null )
                return;
            
            this.itemFonts = new ArrayList<HUDFont>();
        }
        
        if ( itemFonts.size() < getItemsCount() )
        {
            for ( int i = itemFonts.size(); i < getItemsCount(); i++ )
            {
                itemFonts.add( null );
            }
        }
        
        itemFonts.set( itemIndex, font );
    }
    
    /**
     * Gets an item's font.
     * 
     * @param itemIndex
     * @return the items font (null for list default).
     */
    public final HUDFont getItemFont( int itemIndex )
    {
        if ( ( itemFonts == null ) || ( itemIndex >= itemFonts.size() ) )
            return ( null );
        
        return ( itemFonts.get( itemIndex ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        super.clear();
        
        if ( itemColors != null )
            itemColors.clear();
        
        if ( itemFonts != null )
            itemFonts.clear();
    }
    
    protected void applyValueToWidget( Object value, TextWidget widget )
    {
        widget.setText( String.valueOf( value ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareWidgetImpl( float listContentWidth, Widget widget, Object item, int itemIndex )
    {
        Label lbl = (Label)widget;
        
        if ( getList() instanceof List )
        {
            lbl.setAlignment( ( (List)getList() ).getAlignment() );
        }
        
        Colorf textColor = this.getItemColor( itemIndex );
        HUDFont font = this.getItemFont( itemIndex );
        
        if ( textColor == null )
            textColor = ( (List)getList() ).getFontColor();
        
        if ( font == null )
            font = ( (List)getList() ).getFont();
        
        lbl.setFontColor( textColor );
        lbl.setFont( font );
        
        if ( isSelected( itemIndex ) )
        {
            if ( ( (List)getList() ).getSelectionFontColor() != null )
            {
                lbl.setFontColor( ( (List)getList() ).getSelectionFontColor() );
            }
        }
        
        applyValueToWidget( item, lbl );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected float getItemHeightImpl( int itemIndex )
    {
        Label lbl = (Label)getWidgetImpl( itemIndex );
        
        applyValueToWidget( getItem( itemIndex ), lbl );
        
        Dim2f buffer = Dim2f.fromPool();
        lbl.getMinimalSize( buffer );
        float minHeight = buffer.getHeight();
        Dim2f.toPool( buffer );
        
        return ( minHeight );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected float getMinItemWidthImpl( int itemIndex )
    {
        Label lbl = (Label)getWidgetImpl( itemIndex );
        
        applyValueToWidget( getItem( itemIndex ), lbl );
        
        Dim2f buffer = Dim2f.fromPool();
        lbl.getMinimalSize( buffer );
        float minWidth = buffer.getWidth();
        Dim2f.toPool( buffer );
        
        return ( minWidth );
    }
    
    protected Widget createWidget()
    {
        Label label = new Label( 100f, 16f );
        label.setNoBackground();
        
        return ( label );
    }
    
    /**
     * @param widget
     */
    protected void checkWidgetType( Widget widget )
    {
        /*
        if ( !( widget instanceof Label ) )
            throw new IllegalArgumentException( "The given Widget is not an instanceof Label." );
        */
    }
    
    protected TextListModel( java.util.List<Object> items, Widget widget )
    {
        super( items );
        
        if ( widget == null )
            this.widget = createWidget();
        else
            this.widget = widget;
        
        if ( !( this.widget instanceof TextWidget ) )
            throw new IllegalArgumentException( "The given Widget is not an instanceof TextWidget." );
        
        checkWidgetType( this.widget );
    }
    
    public TextListModel( java.util.List<Object> items )
    {
        this( items, null );
    }
    
    public TextListModel()
    {
        this( new java.util.ArrayList<Object>() );
    }
}
