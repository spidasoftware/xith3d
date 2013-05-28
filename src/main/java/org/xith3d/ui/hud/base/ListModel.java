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


/**
 * A {@link ListModel} holds data for a List Widget.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ListModel
{
    private AbstractList list;
    
    private boolean usesFixedHeight = true;
    private float fixedItemHeight = -1f;
    
    private int selectedIndex = -1;
    
    void setList( AbstractList list )
    {
        this.list = list;
        
        markListDirty();
    }
    
    /**
     * Gets the List Widget, that this model is used for (can be null).
     * 
     * @return  the List Widget, that this model is used for.
     */
    protected final AbstractList getList()
    {
        return ( list );
    }
    
    /**
     * Gets an array of all Widgets, that are used by this model.
     * 
     * @return an array of all Widgets, that are used by this model.
     */
    protected abstract Widget[] getUsedWidgets();
    
    /**
     * Adds a new Item at the given position.
     * 
     * @param index
     * @param item
     */
    protected abstract void addItemImpl( int index, Object item );
    
    /**
     * Adds a new Item at the given position.
     * 
     * @param index
     * @param item
     */
    public final void addItem( int index, Object item )
    {
        if ( ( index < 0 ) || ( index > getItemsCount() ) )
            throw new IllegalArgumentException( "index must be in range [0, itemsCount]." );
        
        addItemImpl( index, item );
    }
    
    /**
     * Adds a new Item at the end of the list.
     * 
     * @param item
     * 
     * @return the index, at which the item has been added.
     */
    public final int addItem( Object item )
    {
        int index = getItemsCount();
        addItemImpl( index, item );
        
        return ( index );
    }
    
    /**
     * Adds all items from the given List to this ListModel.
     * 
     * @param items
     */
    @SuppressWarnings( "unchecked" )
    public void addItems( java.util.List items )
    {
        for ( Object item : items )
        {
            addItem( item );
        }
    }
    
    /**
     * Adds all items from the given array to this ListModel.
     * 
     * @param items
     */
    public void addItems( Object[] items )
    {
        for ( Object item : items )
        {
            addItem( item );
        }
    }
    
    /**
     * Sets the item at the given position.
     * 
     * @param index
     * @param item
     */
    protected abstract void setItemImpl( int index, Object item );
    
    /**
     * Sets the item at the given position.
     * 
     * @param index
     * @param item
     */
    public final void setItem( int index, Object item )
    {
        if ( ( index < 0 ) || ( index >= getItemsCount() ) )
            throw new IllegalArgumentException( "index must be in range [0, itemsCount)." );
        
        setItemImpl( index, item );
    }
    
    /**
     * Removes an Item at the given position.
     * 
     * @param index
     * 
     * @return the removed Item or null, if there was no item at the given index.
     */
    protected abstract Object removeItemImpl( int index );
    
    /**
     * Removes an Item at the given position.
     * 
     * @param index
     * 
     * @return the removed Item or null, if there was no item at the given index.
     */
    public final Object removeItem( int index )
    {
        if ( ( index < 0 ) || ( index >= getItemsCount() ) )
            throw new IllegalArgumentException( "index must be in range [0, itemsCount)." );
        
        return ( removeItemImpl( index ) );
    }
    
    /**
     * Clears the list.
     */
    public abstract void clear();
    
    /**
     * Gets the number of items in the list.
     * 
     * @return the number of items in the list.
     */
    public abstract int getItemsCount();
    
    /**
     * Gets the item at index index. This item must be handleable by the used Widget.
     * 
     * @param index
     * 
     * @return the index'th item.
     */
    protected abstract Object getItemImpl( int index );
    
    /**
     * Gets the item at index index. This item must be handleable by the used Widget.
     * 
     * @param index
     * 
     * @return the index'th item.
     */
    public Object getItem( int index )
    {
        if ( ( index < 0 ) || ( index >= getItemsCount() ) )
            return ( null );
        
        return ( getItemImpl( index ) );
    }
    
    /**
     * Gets the Widget, that is used to render the specified item.
     * This method should not do anything to the returned Widget.
     * All setup stuff is done in the {@link #prepareWidget(Widget, Object)} method.
     * 
     * @param itemIndex
     * 
     * @return the raw Widget.
     */
    protected abstract Widget getWidgetImpl( int itemIndex );
    
    /**
     * This method is called right before the Widget is used to render a specific item.<br>
     * It must only return the Widget.
     * 
     * @param listContentWidth
     * @param widget
     * @param item
     * @param itemIndex
     */
    protected abstract void prepareWidgetImpl( float listContentWidth, Widget widget, Object item, int itemIndex );
    
    /**
     * This method is called right before the Widget is used to render a specific item.
     * 
     * @param listContentWidth
     * @param widget
     * @param item
     * @param itemIndex
     */
    protected void prepareWidget( float listContentWidth, Widget widget, Object item, int itemIndex )
    {
        if ( getUsesFixedHeight() )
            widget.setSize( listContentWidth, getItemHeight( 0 ) );
        else
            widget.setSize( listContentWidth, getItemHeight( itemIndex ) );
        
        prepareWidgetImpl( listContentWidth, widget, item, itemIndex );
    }
    
    /**
     * Gets the Widget to render the specified item.
     * The Widget will be fully setup to be rendered to the list.
     * 
     * @param listContentWidth
     * @param itemIndex
     * 
     * @return the fully setup Widget.
     */
    final Widget getWidget( float listContentWidth, int itemIndex )
    {
        Object item = getItem( itemIndex );
        
        Widget widget = getWidgetImpl( itemIndex );
        
        prepareWidget( listContentWidth, widget, item, itemIndex );
        
        return ( widget );
    }
    
    /**
     * Gets the Widget to render the specified item.
     * The Widget will be fully setup to be rendered to the list.
     * 
     * @param itemIndex
     * 
     * @return the fully setup Widget.
     */
    protected final Widget getWidget( int itemIndex )
    {
        return ( getWidget( getList().getContentWidth(), itemIndex ) );
    }
    
    /**
     * Checks, whether hovering the items needs the List to be redrawn.
     * 
     * @return if hovering the items needs a redraw.
     */
    public boolean hoverNeedsRedraw()
    {
        return ( false );
    }
    
    /**
     * Checks, if the item should be selected after it was clicked at the specified location.
     * 
     * @param itemIndex
     * @param itemTop
     * @param itemBottom
     * @param mouseX
     * @param mouseY
     * 
     * @return should the item get selected?
     */
    protected boolean checkOnMouseButtonPressedImpl( int itemIndex, float itemTop, float itemBottom, float mouseX, float mouseY )
    {
        return ( true );
    }
    
    /**
     * Checks, if the item should be selected after it was clicked at the specified location.
     * 
     * @param itemIndex
     * @param itemLeft
     * @param itemTop
     * @param itemBottom
     * @param mouseX
     * @param mouseY
     * 
     * @return should the item get selected?
     */
    public final boolean checkOnMouseButtonPressed( int itemIndex, float itemLeft, float listContentWidth, float itemTop, float itemBottom, float mouseX, float mouseY )
    {
        Widget widget = getWidgetImpl( itemIndex );
        
        getList().getWidgetAssembler().reposition( widget, itemLeft, itemTop );
        widget.setSize( listContentWidth, itemBottom - itemTop );
        
        return ( checkOnMouseButtonPressedImpl( itemIndex, itemTop, itemBottom, mouseX, mouseY ) );
    }
    
    /**
     * Sets, whether all items have the same height.
     * If true, getItemHeight(0) is used for all items.
     * Default: true.
     * 
     * @param usesFixedHeight
     */
    public void setUsesFixedHeight( boolean usesFixedHeight )
    {
        if ( usesFixedHeight == this.usesFixedHeight )
            return;
        
        this.usesFixedHeight = usesFixedHeight;
        
        this.fixedItemHeight = -1f;
    }
    
    /**
     * Returns, whether all items have the same height.
     * If true, getItemHeight(0) is used for all items.
     * Default: true.
     * 
     * @return whether all items have the same height.
     */
    public final boolean getUsesFixedHeight()
    {
        return ( usesFixedHeight );
    }
    
    /**
     * Gets the item's height in HUD space. If getUsesFixedHeight() is true, only the value of the first item is used for all.
     * 
     * @param itemIndex
     * 
     * @return the item's height.
     */
    protected abstract float getItemHeightImpl( int itemIndex );
    
    /**
     * Gets the item's height in HUD space. If getUsesFixedHeight() is true, only the value of the first item is used for all.
     * 
     * @param itemIndex
     * 
     * @return the item's height.
     */
    public final float getItemHeight( int itemIndex )
    {
        if ( itemIndex < 0 )
            return ( -1f );
        
        if ( itemIndex >= getItemsCount() )
            return ( -1f );
        
        if ( getUsesFixedHeight() )
        {
            if ( fixedItemHeight < 0f )
            {
                fixedItemHeight = getItemHeightImpl( itemIndex );
            }
            
            return ( fixedItemHeight );
        }
        
        return ( getItemHeightImpl( itemIndex ) );
    }
    
    /**
     * Gets the item's minimum width in HUD space.
     * 
     * @param itemIndex
     * 
     * @return the item's minimum height.
     */
    protected abstract float getMinItemWidthImpl( int itemIndex );
    
    /**
     * Gets the item's minimum width in HUD space.
     * 
     * @param itemIndex
     * 
     * @return the item's minimum height.
     */
    public final float getMinItemWidth( int itemIndex )
    {
        if ( itemIndex < 0 )
            return ( -1f );
        
        if ( itemIndex >= getItemsCount() )
            return ( -1f );
        
        return ( getMinItemWidthImpl( itemIndex ) );
    }
    
    /**
     * Sets the selected index.
     * 
     * @param index
     */
    public void setSelectedIndex( int index )
    {
        this.selectedIndex = index;
    }
    
    /**
     * Gets the selected index.
     * This method is not used by the (default) list implementation. {@link #isSelected(int)} is used instead, which is more flexible.
     * 
     * @return the selected index.
     */
    public int getSelectedIndex()
    {
        return ( selectedIndex );
    }
    
    /**
     * Gets the selected item.
     * 
     * @return the selected item.
     */
    public Object getSelectedItem()
    {
        int selIdx = getSelectedIndex();
        
        if ( ( selIdx < 0 ) || ( selIdx >= getItemsCount() ) )
            return ( null );
        
        return ( getItem( selIdx ) );
    }
    
    /**
     * Queries, whether the specified index is selected.
     * 
     * @param index
     * 
     * @return index selected?
     */
    public boolean isSelected( int index )
    {
        return ( index == selectedIndex );
    }
    
    /**
     * Notifies the List be be redrawn.
     */
    public void markListDirty()
    {
        if ( list != null )
            list.updateSizesAndMarkDirty();
    }
}
