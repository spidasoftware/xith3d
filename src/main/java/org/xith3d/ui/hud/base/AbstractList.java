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
package org.xith3d.ui.hud.base;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.listeners.ListSelectionListener;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This is the base implementation for a List Widget.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class AbstractList extends BackgroundSettableWidget implements PaddingSettable
{
    protected static final boolean DEFAULT_HEAVYWEIGHT = false;
    
    private ListModel model;
    
    /**
     * @param model
     */
    protected void afterModelSetWidthItems( ListModel model )
    {
    }
    
    /**
     * Sets the underlying model.
     * 
     * @param model
     */
    public void setModel( ListModel model )
    {
        if ( model == null )
            throw new IllegalArgumentException( "model must not be null" );
        
        if ( model == this.model )
            return;
        
        if ( this.model != null )
        {
            for ( Widget widget : this.model.getUsedWidgets() )
            {
                getWidgetAssembler().removeWidget( widget );
            }
            
            this.model.setList( null );
        }
        
        this.model = model;
        
        this.model.setList( this );
        
        for ( Widget widget : model.getUsedWidgets() )
        {
            getWidgetAssembler().addUnmanagedWidget( widget );
        }
        
        if ( ( getHUD() != null ) && ( model.getItemsCount() > 0 ) )
        {
            afterModelSetWidthItems( model );
        }
    }
    
    /**
     * Gets the underlying model.
     * 
     * @return the underlying model.
     */
    public ListModel getModel()
    {
        return ( model );
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean setPadding( int padding )
    {
        return ( setPadding( padding, padding, padding, padding ) );
    }
    
    /**
     * Sets the background-color of the hovered Item.
     * 
     * @param color
     */
    public abstract void setHoverBackgroundColor( Colorf color );
    
    /**
     * @return the background-color of the hovered Item.
     */
    public abstract Colorf getHoverBackgroundColor();
    
    /**
     * Sets the background-color of the selected Item.
     * 
     * @param color
     */
    public abstract void setSelectionBackgroundColor( Colorf color );
    
    /**
     * @return the background-color of the selected Item.
     */
    public abstract Colorf getSelectionBackgroundColor();
    
    /**
     * This alignment is used to render the items, if possible.
     * 
     * @param alignment
     */
    public abstract void setAlignment( TextAlignment alignment );
    
    /**
     * This alignment is used to render the items, if possible.
     * 
     * @return the TextAlignment.
     */
    public abstract TextAlignment getAlignment();
    
    /**
     * Adds a new ListSelectionListener.
     * 
     * @param l the new listener
     */
    public abstract void addSelectionListener( ListSelectionListener l );
    
    /**
     * Removes a ListSelectionListener.
     * 
     * @param l the listener to be removed
     */
    public abstract void removeSelectionListener( ListSelectionListener l );
    
    /**
     * Sets the currently selected Item.
     * 
     * @param itemIndex
     */
    public void setSelectedIndex( int itemIndex )
    {
        ListModel model = getModel();
        
        model.setSelectedIndex( itemIndex );
        
        model.markListDirty();
    }
    
    /**
     * @return the selected Item's index or <i>null</i>
     */
    public final int getSelectedIndex()
    {
        return ( getModel().getSelectedIndex() );
    }
    
    /**
     * Returns the currently selected Item.
     * 
     * @return the currently selected Item.
     */
    public Object getSelectedItem()
    {
        return ( getModel().getSelectedItem() );
    }
    
    /**
     * Finds the first item, which's equals() method returns true for the given value.
     * 
     * @param item
     * 
     * @return the found item's index (or -1)
     */
    public int findItem( Object item )
    {
        ListModel model = getModel();
        
        int n = model.getItemsCount();
        
        for ( int i = 0; i < n; i++ )
        {
            if ( model.getItem( i ).equals( item ) )
                return ( i );
        }
        
        return ( -1 );
    }
    
    /**
     * Sets the selected item to the previous one of the currently selected item.
     * 
     * @return the new selected index
     */
    public final int selectPreviousItem()
    {
        int selIndex = getSelectedIndex();
        
        if ( selIndex > 0 )
        {
            setSelectedIndex( --selIndex );
        }
        
        return ( selIndex );
    }
    
    /**
     * Sets the selected item to the following one of the currently selected item.
     * 
     * @return the new selected index
     */
    public final int selectNextItem()
    {
        int selIndex = getSelectedIndex();
        
        if ( selIndex < getItemsCount() - 1 )
        {
            setSelectedIndex( ++selIndex );
        }
        
        return ( selIndex );
    }
    
    /**
     * If set to true, the addItem() method sets the selected item to the added one.
     * 
     * @param b
     */
    public abstract void setAddItemSetsSelectedItem( boolean b );
    
    /**
     * If set to true, the addItem() method sets the selected item to the added one.
     */
    public abstract boolean addItemSetsSelectedItem();
    
    /**
     * Scrolls the list, so that the selected item is in the content area.
     */
    public abstract void scrollSelectedItemIntoView();
    
    protected void afterItemAddedToEnd()
    {
    }
    
    protected void afterFirstItemAdded()
    {
    }
    
    /**
     * Adds the given Item to the List (at the given position).
     * 
     * @param index the position to add the Item at
     * @param item the new Item to add to the List
     */
    public Object addItem( int index, Object item )
    {
        ListModel model = getModel();
        
        model.addItem( index, item );
        
        if ( addItemSetsSelectedItem() )
            setSelectedIndex( index );
        else if ( getSelectedIndex() >= index )
            setSelectedIndex( getSelectedIndex() + 1 );
        
        if ( index == model.getItemsCount() - 2 )
            afterItemAddedToEnd();
        
        model.markListDirty();
        
        if ( ( getHUD() != null ) && ( model.getItemsCount() == 1 ) )
        {
            afterFirstItemAdded();
        }
        
        return ( item );
    }
    
    /**
     * Adds the given Item to the List (at the end).
     * 
     * @param item the new Item to add to the List
     * 
     * @return the index, at which the item has been added.
     */
    public final int addItem( Object item )
    {
        int index = getItemsCount();
        
        addItem( index, item );
        
        return ( index );
    }
    
    /**
     * Adds all items from the given List to this List.
     * 
     * @param items
     */
    @SuppressWarnings( "unchecked" )
    public void addItems( java.util.List items )
    {
        ListModel model = getModel();
        
        int index = model.getItemsCount();
        
        model.addItems( items );
        
        if ( addItemSetsSelectedItem() )
            setSelectedIndex( index );
        //else if ( getSelectedIndex() >= index )
        //    setSelectedIndex( getSelectedIndex() + 1 );
        
        //if ( index == model.getItemsCount() - 2 )
            afterItemAddedToEnd();
        
        model.markListDirty();
        
        if ( ( getHUD() != null ) && ( model.getItemsCount() == items.size() ) )
        {
            afterFirstItemAdded();
        }
    }
    
    /**
     * Adds all items from the given array to this List.
     * 
     * @param items
     */
    public void addItems( Object[] items )
    {
        ListModel model = getModel();
        
        int index = model.getItemsCount();
        
        model.addItems( items );
        
        if ( addItemSetsSelectedItem() )
            setSelectedIndex( index );
        //else if ( getSelectedIndex() >= index )
        //    setSelectedIndex( getSelectedIndex() + 1 );
        
        //if ( index == model.getItemsCount() - 2 )
            afterItemAddedToEnd();
        
        model.markListDirty();
        
        if ( ( getHUD() != null ) && ( model.getItemsCount() == items.length ) )
        {
            afterFirstItemAdded();
        }
    }
    
    /**
     * Removes the given Item from the List.
     * 
     * @param index the position of the Item to be removed
     * 
     * @return the removed Item or null, if there was no item at the given index.
     */
    public Object removeItem( int index )
    {
        ListModel model = getModel();
        
        Object item = model.removeItem( index );
        
        model.markListDirty();
        
        return ( item );
    }
    
    /**
     * Removes all items from the List.
     */
    public void clear()
    {
        ListModel model = getModel();
        
        model.clear();
        
        model.markListDirty();
    }
    
    /**
     * @return the number of Items in this List
     */
    public final int getItemsCount()
    {
        return ( getModel().getItemsCount() );
    }
    
    protected final Widget getWidget( float contentWidth, int itemIndex )
    {
        return ( getModel().getWidget( contentWidth, itemIndex ) );
    }
    
    /**
     * Gets the item from the specified index.
     * 
     * @param index the index to get the Item from
     * 
     * @return the Item at the given index.
     */
    public Object getItem( int index )
    {
        return ( getModel().getItem( index ) );
    }
    
    /**
     * Scrolls the list, so that the given index is the top item's index (if possible).
     * 
     * @param topIndex
     */
    public abstract void setTopIndex( int topIndex );
    
    /**
     * The top-most item's index visible in the List (or -1, if the List is empty)
     * 
     * @return the top index.
     */
    public abstract int getTopIndex();
    
    /**
     * The bottom-most item's index visible in the List (or -1, if the List is empty)
     * 
     * @return the bottom index.
     */
    public abstract int getBottomIndex();
    
    protected void updateSizesAndMarkDirty()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToHUD( HUD hud )
    {
        super.onAttachedToHUD( hud );
        
        updateSizesAndMarkDirty();
    }
    
    /**
     * Creates a new AbstractList.
     * 
     * @param isHeavyWeight
     * @param backgroundColor
     * @param backgroundTexture
     * @param tileMode
     * @param model the ListModel (null for auto-generation)
     */
    protected AbstractList( boolean isHeavyWeight, Colorf backgroundColor, Texture2D backgroundTexture, TileMode tileMode, ListModel model )
    {
        super( isHeavyWeight, true, backgroundColor, backgroundTexture, tileMode );
        
        if ( model == null )
            throw new IllegalArgumentException( "model must not be null" );
        
        setBackground( backgroundColor, backgroundTexture, tileMode );
        
        setModel( model );
        
        getWidgetAssembler().setPickDispatched( true );
    }
    
    /**
     * Creates a new AbstractList with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor
     * @param backgroundTexture
     * @param tileMode
     * @param model the ListModel (null for auto-generation)
     */
    protected AbstractList( boolean isHeavyWeight, float width, float height, Colorf backgroundColor, Texture2D backgroundTexture, TileMode tileMode, ListModel model )
    {
        super( isHeavyWeight, true, width, height, backgroundColor, backgroundTexture, tileMode );
        
        if ( model == null )
            throw new IllegalArgumentException( "model must not be null" );
        
        setBackground( backgroundColor, backgroundTexture, tileMode );
        
        setModel( model );
        
        getWidgetAssembler().setPickDispatched( true );
    }
}
