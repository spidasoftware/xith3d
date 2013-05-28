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
package org.xith3d.ui.hud.widgets;

import java.util.ArrayList;

import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.DigitalDeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.Keys;
import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector2f;
import org.openmali.vecmath2.Vector2i;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.AbstractList;
import org.xith3d.ui.hud.base.Border;
import org.xith3d.ui.hud.base.ListModel;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;
import org.xith3d.ui.hud.listeners.ListSelectionListener;
import org.xith3d.ui.hud.listmodels.TextListModel;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.ScrollHandler;
import org.xith3d.ui.hud.utils.ScrollMode;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.hud.widgets.Scrollbar.Direction;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * A Scrollable List Widget that renders the contents of a ListModel.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class List extends AbstractList
{
    /**
     * This class is used to describe a List Widget.
     * You can pass it to the List constructor.
     * Modifications on the used instance after creating the List Widget
     * won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends Widget.DescriptionBase
    {
        private Colorf backgroundColor;
        private Texture2D backgroundTexture;
        
        private Border.Description borderDesc;
        
        private int paddingBottom, paddingRight, paddingTop, paddingLeft;
        
        private Colorf hoverBackgroundColor;
        private Colorf hoverFontColor;
        
        private Colorf selectionBackgroundColor;
        private Colorf selectionFontColor;
        
        public void setBorderDescription( Border.Description borderDesc )
        {
            this.borderDesc = borderDesc;
        }
        
        public Border.Description getBorderDescription()
        {
            return ( borderDesc );
        }
        
        public void setPadding( int bottom, int right, int top, int left )
        {
            this.paddingBottom = bottom;
            this.paddingRight = right;
            this.paddingTop = top;
            this.paddingLeft = left;
        }
        
        public void setPadding( int padding )
        {
            setPadding( padding, padding, padding, padding );
        }
        
        public int getPaddingBottom()
        {
            return ( paddingBottom );
        }
        
        public int getPaddingRight()
        {
            return ( paddingRight );
        }
        
        public int getPaddingTop()
        {
            return ( paddingTop );
        }
        
        public int getPaddingLeft()
        {
            return ( paddingLeft );
        }
        
        public void setBackgroundTexture( Texture2D texture )
        {
            this.backgroundTexture = texture;
        }
        
        public void setBackgroundTexture( String texture )
        {
            this.backgroundTexture = HUDTextureUtils.getTexture( texture, true );
        }
        
        public Texture2D getBackgroundTexture()
        {
            return ( backgroundTexture );
        }
        
        public void setBackgroundColor( Colorf color )
        {
            this.backgroundColor = color;
        }
        
        public Colorf getBackgroundColor()
        {
            return ( backgroundColor );
        }
        
        public Colorf getFontColor()
        {
            return ( HUD.getTheme().getLabelDescription().getFontColor( false ) );
        }
        
        public HUDFont getFont()
        {
            return ( HUD.getTheme().getLabelDescription().getFont( false ) );
        }
        
        public void setHoverBackgroundColor( Colorf color )
        {
            this.hoverBackgroundColor = color;
        }
        
        public Colorf getHoverBackgroundColor()
        {
            return ( hoverBackgroundColor );
        }
        
        public void setHoverFontColor( Colorf color )
        {
            this.hoverFontColor = color;
        }
        
        public Colorf getHoverFontColor()
        {
            return ( hoverFontColor );
        }
        
        public void setSelectionBackgroundColor( Colorf color )
        {
            this.selectionBackgroundColor = color;
        }
        
        public Colorf getSelectionBackgroundColor()
        {
            return ( selectionBackgroundColor );
        }
        
        public void setSelectionFontColor( Colorf color )
        {
            this.selectionFontColor = color;
        }
        
        public Colorf getSelectionFontColor()
        {
            return ( selectionFontColor );
        }
        
        public void set( Description template )
        {
            this.backgroundTexture = template.backgroundTexture;
            this.backgroundColor = template.backgroundColor;
            this.borderDesc = template.borderDesc;
            this.paddingBottom = template.paddingBottom;
            this.paddingRight = template.paddingRight;
            this.paddingTop = template.paddingTop;
            this.paddingLeft = template.paddingLeft;
            
            this.hoverBackgroundColor = template.hoverBackgroundColor;
            this.hoverFontColor = template.hoverFontColor;
            
            this.selectionBackgroundColor = template.selectionBackgroundColor;
            this.selectionFontColor = template.selectionFontColor;
        }
        
        /**
         * Clones this instance and returns the clone.
         */
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        /**
         * Clone-Constructor.
         * 
         * @param template the template Description to copy the values from
         */
        private Description( Description template )
        {
            this.set( template );
        }
        
        /**
         * Creates a new Image.Description
         * 
         * @param 
         */
        public Description( int paddingBottom, int paddingRight, int paddingTop, int paddingLeft, Border.Description borderDesc, Colorf backgroundColor, Texture2D backgroundTexture, Colorf hoveredBackgroundColor, Colorf hoveredFontColor, Colorf selectedBackgroundColor, Colorf selectedFontColor )
        {
            this.paddingBottom = paddingBottom;
            this.paddingRight = paddingRight;
            this.paddingTop = paddingTop;
            this.paddingLeft = paddingLeft;
            
            this.backgroundColor = backgroundColor;
            this.backgroundTexture = backgroundTexture;
            
            this.borderDesc = borderDesc;
            
            this.hoverBackgroundColor = hoveredBackgroundColor;
            this.hoverFontColor = hoveredFontColor;
            
            this.selectionBackgroundColor = selectedBackgroundColor;
            this.selectionFontColor = selectedFontColor;
        }
        
        /**
         * Creates a new Image.Description
         * 
         * @param 
         */
        public Description( int paddingBottom, int paddingRight, int paddingTop, int paddingLeft, Border.Description borderDesc, Colorf backgroundColor, String backgroundTexture, Colorf hoveredBackgroundColor, Colorf hoveredFontColor, Colorf selectedBackgroundColor, Colorf selectedFontColor )
        {
            this( paddingBottom, paddingRight, paddingTop, paddingLeft, borderDesc, backgroundColor, HUDTextureUtils.getTexture( backgroundTexture, true ), hoveredBackgroundColor, hoveredFontColor, selectedBackgroundColor, selectedFontColor );
        }
    }
    
    protected final Vector2f childrenOffset_HUD = new Vector2f( 0f, 0f );
    protected final Vector2i childrenOffset_PX = new Vector2i( 0, 0 );
    
    private int paddingBottom = 0;
    private int paddingRight = 3;
    private int paddingTop = 0;
    private int paddingLeft = 3;
    
    private int heightByItems = -1;
    
    private Colorf fontColor;
    private Colorf selectionFontColor = null;
    
    private HUDFont font;
    
    private Colorf hoverBackgroundColor = null;
    private Colorf selectionBackgroundColor = null;
    
    private int currentHoveredItem = -1;
    private int lastDrawnHoveredItem = -1;
    
    private TextAlignment alignment = TextAlignment.CENTER_LEFT;
    
    private ScrollHandler scrollHandler;
    
    private boolean isFixedToBottom = false;
    
    private boolean addItemSetsSelected = false;
    
    private final ArrayList<ListSelectionListener> selectionListeners = new ArrayList<ListSelectionListener>();
    
    private DigitalDeviceComponent[] upComponents = { Keys.UP };
    private DigitalDeviceComponent[] downComponents = { Keys.DOWN };
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterModelSetWidthItems( ListModel model )
    {
        super.afterModelSetWidthItems( model );
        
        scrollHandler.setLineHeight( (int)model.getItemHeight( 0 ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setPadding( int paddingBottom, int paddingRight, int paddingTop, int paddingLeft )
    {
        if ( ( this.paddingBottom == paddingBottom ) &&
             ( this.paddingRight == paddingRight ) &&
             ( this.paddingTop == paddingTop ) &&
             ( this.paddingLeft == paddingLeft ) )
        {
            return ( false );
        }
        
        this.paddingBottom = paddingBottom;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingLeft = paddingLeft;
        
        updateSizeFactors();
        setTextureDirty();
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingBottom()
    {
        return ( paddingBottom );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingRight()
    {
        return ( paddingRight );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingTop()
    {
        return ( paddingTop );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingLeft()
    {
        return ( paddingLeft );
    }
    
    /**
     * Sets the text-color of non-selected Items.
     * 
     * @param color
     */
    public void setFontColor( Colorf color )
    {
        this.fontColor = color;
        
        setTextureDirty();
    }
    
    /**
     * Gets the text-color of non-selected Items.
     * 
     * @return the text-color of non-selected Items.
     */
    public final Colorf getFontColor()
    {
        return ( fontColor );
    }
    
    /**
     * Sets the Items' font.
     * 
     * @param font
     */
    public void setFont( HUDFont font )
    {
        this.font = font;
        
        setTextureDirty();
    }
    
    /**
     * Gets the Items' font.
     * 
     * @return the Items' font.
     */
    public final HUDFont getFont()
    {
        return ( font );
    }
    
    /**
     * Sets the text-color of the selected Item.
     * 
     * @param color
     */
    public void setSelectionFontColor( Colorf color )
    {
        this.selectionFontColor = color;
        
        setTextureDirty();
    }
    
    /**
     * Gets the text-color of the selected Item.
     * 
     * @return the text-color of the selected Item.
     */
    public final Colorf getSelectionFontColor()
    {
        return ( selectionFontColor );
    }
    
    public Object addItem( int index, Object item, HUDFont font, Colorf color )
    {
        Object result = super.addItem( index, item );
        
        ListModel model = getModel();
        if ( model instanceof TextListModel )
        {
            ( (TextListModel)model ).setItemFont( index, font );
            ( (TextListModel)model ).setItemColor( index, color );
        }
        
        return ( result );
    }
    
    public final Object addItem( Object item, HUDFont font, Colorf color )
    {
        return ( addItem( getItemsCount(), item, font, color ) );
    }
    
    public final Object addItem( int index, Object item, Colorf color )
    {
        return ( addItem( index, item, null, color ) );
    }
    
    public final Object addItem( Object item, Colorf color )
    {
        return ( addItem( getItemsCount(), item, null, color ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Object addItem( int index, Object item )
    {
        return ( addItem( index, item, null, null ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setHoverBackgroundColor( Colorf color )
    {
        this.hoverBackgroundColor = color;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Colorf getHoverBackgroundColor()
    {
        return ( hoverBackgroundColor );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionBackgroundColor( Colorf color )
    {
        this.selectionBackgroundColor = color;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Colorf getSelectionBackgroundColor()
    {
        return ( selectionBackgroundColor );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlignment( TextAlignment alignment )
    {
        this.alignment = alignment;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final TextAlignment getAlignment()
    {
        return ( alignment );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionListener( ListSelectionListener l )
    {
        selectionListeners.add( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionListener( ListSelectionListener l )
    {
        selectionListeners.remove( l );
    }
    
    protected void notifyOnSelectionChanged( Object oldSelObj, Object newSelObj, int oldSelIdx, int newSelIdx )
    {
        for ( int i = 0; i < selectionListeners.size(); i++ )
        {
            selectionListeners.get( i ).onListSelectionChanged( this, oldSelObj, newSelObj, oldSelIdx, newSelIdx );
        }
    }
    
    protected void notifyOnItemClicked( Object item, int index )
    {
        for ( int i = 0; i < selectionListeners.size(); i++ )
        {
            selectionListeners.get( i ).onListItemClicked( this, item, index );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddItemSetsSelectedItem( boolean b )
    {
        this.addItemSetsSelected = b;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean addItemSetsSelectedItem()
    {
        return ( addItemSetsSelected );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollSelectedItemIntoView()
    {
        int selIndex = getSelectedIndex();
        
        if ( ( selIndex < 0 ) || ( selIndex >= getItemsCount() ) )
            return;
        
        ListModel model = getModel();
        
        float selTop = 0f;
        if ( model.getUsesFixedHeight() )
        {
            selTop = selIndex * model.getItemHeight( 0 );
        }
        else
        {
            for ( int i = 0; i < selIndex; i++ )
            {
                selTop += model.getItemHeight( i );
            }
        }
        
        if ( selTop + childrenOffset_HUD.getY() < 0f )
        {
            setChildrenOffset( false, 0f, true, -selTop, true );
        }
        else
        {
            float selBottom = selTop + ( model.getUsesFixedHeight() ? model.getItemHeight( 0 ) : model.getItemHeight( selIndex ) );
            
            float contentHeight = getContentHeight();
            if ( selBottom + childrenOffset_HUD.getY() > contentHeight )
            {
                setChildrenOffset( false, 0f, true, contentHeight - selBottom, true );
            }
        }
    }
    
    @Override
    protected void afterItemAddedToEnd()
    {
        super.afterItemAddedToEnd();
        
        if ( isFixedToBottom() )
            scrollToBottom();
    }
    
    @Override
    protected void afterFirstItemAdded()
    {
        super.afterFirstItemAdded();
        
        scrollHandler.setLineHeight( (int)getModel().getItemHeight( 0 ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTopIndex( int topIndex )
    {
        if ( ( topIndex < 0 ) || ( topIndex >= getItemsCount() ) )
            throw new IllegalArgumentException( "topIndex out of range" );
        
        ListModel model = getModel();
        
        float topTop = 0f;
        if ( model.getUsesFixedHeight() )
        {
            topTop = topIndex * model.getItemHeight( 0 );
        }
        else
        {
            for ( int i = 0; i < topIndex; i++ )
            {
                topTop += model.getItemHeight( i );
            }
        }
        
        int n = getItemsCount();
        
        float bottomBottom = topTop;
        if ( model.getUsesFixedHeight() )
        {
            bottomBottom += ( n - topIndex ) * model.getItemHeight( 0 );
        }
        else
        {
            for ( int i = topIndex; i < n; i++ )
            {
                bottomBottom += model.getItemHeight( i );
            }
        }
        
        topTop = Math.max( 0f, Math.min( topTop, bottomBottom - getContentHeight() ) );
        
        setChildrenOffset( false, 0f, true, -topTop, true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopIndex()
    {
        if ( getItemsCount() == 0 )
            return ( -1 );
        
        ListModel model = getModel();
        
        if ( model.getUsesFixedHeight() )
        {
            return ( (int)Math.ceil( -childrenOffset_HUD.getY() / model.getItemHeight( 0 ) ) );
        }
        
        float tmp = -childrenOffset_HUD.getY();
        int i = 0;
        while ( tmp > 0f )
        {
            tmp -= model.getItemHeight( i++ );
        }
        
        return ( i );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getBottomIndex()
    {
        if ( getItemsCount() == 0 )
            return ( -1 );
        
        ListModel model = getModel();
        
        if ( model.getUsesFixedHeight() )
        {
            return ( (int)Math.ceil( ( -childrenOffset_HUD.getY() + getContentHeight() ) / model.getItemHeight( 0 ) ) - 1 );
        }
        
        float tmp = -childrenOffset_HUD.getY() + getContentHeight();
        int i = 0;
        while ( tmp > 0f )
        {
            tmp -= model.getItemHeight( i++ );
        }
        
        return ( i - 1 );
    }
    
    /**
     * Sets the height by items count.
     * 
     * @param numItems the new height by items unit
     */
    public void setHeightByItems( int numItems )
    {
        if ( getHUD() == null )
        {
            this.heightByItems = numItems;
        }
        else
        {
            float height2 = 0f;
            
            int paddingAndBorder = getPaddingTop() + getPaddingBottom();
            if ( getBorder() != null )
            {
                paddingAndBorder += getBorder().getTopHeight() + getBorder().getBottomHeight();
            }
            
            if ( paddingAndBorder > 0 )
            {
                Dim2f buffer = Dim2f.fromPool();
                getSizePixels2HUD_( 0, paddingAndBorder, buffer );
                height2 += buffer.getHeight();
                Dim2f.toPool( buffer );
            }
            
            //if ( getItemsCount() > 0 )
            {
                ListModel model = getModel();
                
                if ( model.getUsesFixedHeight() )
                {
                    height2 += numItems * Math.max( 0f, model.getItemHeight( 0 ) );
                }
                else
                {
                    int i0 = getTopIndex();
                    for ( int i = i0; i < i0 + numItems; i++ )
                    {
                        height2 += Math.max( 0f, model.getItemHeight( i ) );
                    }
                }
                
                setSize( getWidth(), height2 );
            }
        }
    }
    
    /**
     * Gets the height by items count.
     * 
     * @return the height by items count.
     */
    public int getHeightByItems()
    {
        if ( getItemsCount() == 0 )
            return ( -1 );
        
        ListModel model = getModel();
        if ( model.getUsesFixedHeight() )
        {
            return ( (int)Math.floor( getHeight() / model.getItemHeight( 0 ) ) );
        }
        
        int i = getTopIndex();
        int c = 0;
        float height2 = getContentHeight();
        while ( height2 > 0f )
        {
            height2 -= model.getItemHeight( i++ );
            c++;
        }
        
        return ( c );
    }
    
    /**
     * Sets the ScrollBar's ScrollMode.<br>
     * <br>
     * The ScrollBar is visible, if (||)
     * <ul>
     *   <li>more items are in the list, than the list can display at once und ScrollMode is AUTO</li>
     *   <li>ScrollMode is ALWAYS</li>
     * </ul>
     * 
     * @param mode the ScrollBar's ScrollMode
     */
    public void setScrollMode( ScrollMode mode )
    {
        scrollHandler.setScrollMode( mode );
        
        setTextureDirty();
    }
    
    /**
     * Returns the ScrollBar's ScrollMode.<br>
     * <br>
     * The ScrollBar is visible, if (||)
     * <ul>
     *   <li>more items are in the list, than the list can display at once und ScrollMode is AUTO</li>
     *   <li>ScrollMode is ALWAYS</li>
     * </ul>
     * 
     * @return the ScrollBar's ScrollMode.
     */
    public ScrollMode getScrollMode()
    {
        return ( scrollHandler.getScrollMode() );
    }
    
    /**
     * @return true, if the List is currently scrollt to the bottom-most item.
     */
    public boolean isScrolledToBottom()
    {
        return ( getBottomIndex() == getItemsCount() - 1 );
    }
    
    /**
     * Scrolls the list to the bottom-most item.
     */
    public void scrollToBottom()
    {
        ListModel model = getModel();
        
        int n = model.getItemsCount();
        
        if ( n == 0 )
            return;
        
        float bottomBottom = 0f;
        
        if ( model.getUsesFixedHeight() )
        {
            bottomBottom = n * model.getItemHeight( 0 );
        }
        else
        {
            for ( int i = 0; i < n; i++ )
            {
                bottomBottom += model.getItemHeight( i );
            }
        }
        
        bottomBottom -= getContentHeight();
        
        setChildrenOffset( false, 0f, true, -Math.max( 0f, bottomBottom ), true );
    }
    
    /**
     * Fixes or releases this List to always scroll to the lowest item,
     * when a new Item is added and the List is currently scrolled to bottom.
     * 
     * @param fixed
     */
    public void setFixedToBottom( boolean fixed )
    {
        this.isFixedToBottom = fixed;
    }
    
    /**
     * If true, the List always scrolls to the lowest item,
     * when a new Item is added and the List is currently scrolled to bottom.
     */
    public boolean isFixedToBottom()
    {
        return ( isFixedToBottom );
    }
    
    /**
     * Gets the minimum list width, that is able to fully display all items.
     * 
     * @return the minimum width.
     */
    public float getMinWidthThatFitsItems()
    {
        float minWidth = 0f;
        
        ListModel model = getModel();
        
        final int n = model.getItemsCount();
        for ( int i = 0; i < n; i++ )
        {
            float minItemWidth = model.getMinItemWidth( i );
            
            if ( minItemWidth > minWidth )
                minWidth = minItemWidth;
        }
        
        Dim2f buffer = Dim2f.fromPool();
        getSizePixels2HUD_( getPaddingLeft() + getPaddingRight(), 0, buffer );
        minWidth += buffer.getWidth();
        if ( getBorder() != null )
        {
            getSizePixels2HUD_( getBorder().getLeftWidth() + getBorder().getRightWidth(), 0, buffer );
            minWidth += buffer.getWidth();
        }
        Dim2f.toPool( buffer );
        
        return ( minWidth );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean blocksFocusMoveDeviceComponent( DeviceComponent dc )
    {
        if ( upComponents != null )
        {
            for ( int i = 0; i < upComponents.length; i++ )
            {
                if ( upComponents[i] == dc )
                    return ( true );
            }
        }
        
        if ( downComponents != null )
        {
            for ( int i = 0; i < downComponents.length; i++ )
            {
                if ( downComponents[i] == dc )
                    return ( true );
            }
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyPressed( Key key, int modifierMask, long when )
    {
        super.onKeyPressed( key, modifierMask, when );
        
        for ( int i = 0; i < upComponents.length; i++ )
        {
            if ( key == upComponents[i] )
            {
                selectPreviousItem();
                scrollSelectedItemIntoView();
                
                return;
            }
        }
        
        for ( int i = 0; i < downComponents.length; i++ )
        {
            if ( key == downComponents[i] )
            {
                selectNextItem();
                scrollSelectedItemIntoView();
                
                return;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseExited( isTopMost, hasFocus );
        
        currentHoveredItem = -1;
        
        if ( currentHoveredItem != lastDrawnHoveredItem )
        {
            getModel().markListDirty();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseMoved( float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseMoved( x, y, buttonsState, when, isTopMost, hasFocus );
        
        if ( isTopMost )
        {
            float contentWidth = getContentWidth();
            float contentHeight = getContentHeight();
            
            if ( ( x >= 0f ) && ( y >= 0f ) && ( x < contentWidth ) && ( y < contentHeight ) )
            {
                final ListModel model = getModel();
                final boolean fixed = model.getUsesFixedHeight();
                final float itemHeight = model.getItemHeight( 0 );
                final int n = getItemsCount();
                
                float itemTop = childrenOffset_HUD.getY() + getPaddingTop();
                float itemBottom = itemTop + itemHeight;
                int i = 0;
                while ( ( itemBottom < y ) && ( i < n ) )
                {
                    itemTop = itemBottom;
                    itemBottom += ( fixed ? itemHeight : model.getItemHeight( i ) );
                    i++;
                }
                
                if ( i < n )
                {
                    currentHoveredItem = i;
                }
                else
                {
                    currentHoveredItem = -1;
                }
                
                if ( currentHoveredItem != lastDrawnHoveredItem )
                {
                    if ( ( getHoverBackgroundColor() != null ) || model.hoverNeedsRedraw() )
                    {
                        model.markListDirty();
                    }
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonPressed( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isTopMost )
        {
            final Dim2f siz = Dim2f.fromPool();
            
            float mouseX = x;
            float mouseY = y;
            
            float contentLeft = 0f;
            if ( getBorder() != null )
            {
                getSizePixels2HUD_( getBorder().getLeftWidth(), getBorder().getTopHeight(), siz );
                //mouseX -= siz.getWidth();
                //mouseY -= siz.getHeight();
                
                getSizePixels2HUD_( getContentLeftPX(), 0, siz );
                contentLeft = siz.getWidth();
            }
            
            Dim2f.toPool( siz );
            
            float contentWidth = getContentWidth();
            float contentHeight = getContentHeight();
            
            if ( ( mouseX >= 0f ) && ( mouseY >= 0f ) && ( mouseX < contentWidth ) && ( mouseY < contentHeight ) )
            {
                final ListModel model = getModel();
                final boolean fixed = model.getUsesFixedHeight();
                final float itemHeight = model.getItemHeight( 0 );
                final int n = getItemsCount();
                
                float itemTop = childrenOffset_HUD.getY() + getPaddingTop();
                float itemBottom = itemTop + itemHeight;
                int i = 0;
                while ( ( itemBottom < mouseY ) && ( i < n ) )
                {
                    itemTop = itemBottom;
                    itemBottom += ( fixed ? itemHeight : model.getItemHeight( i ) );
                    i++;
                }
                
                if ( i < n )
                {
                    int oldSelIdx = getSelectedIndex();
                    Object oldSelObj = getSelectedItem();
                    
                    if ( model.checkOnMouseButtonPressed( i, contentLeft, contentWidth, itemTop, itemBottom, x, y ) )
                    {
                        setSelectedIndex( i );
                        
                        Object newSelObj = getSelectedItem();
                        
                        notifyOnSelectionChanged( oldSelObj, newSelObj, oldSelIdx, i );
                        notifyOnItemClicked( newSelObj, i );
                    }
                    else
                    {
                        notifyOnItemClicked( getItem( i ), i );
                    }
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateSizesAndMarkDirty()
    {
        super.updateSizesAndMarkDirty();
        
        if ( getHUD() != null )
        {
            ListModel model = getModel();
            
            float maxRight = 0f;
            float maxBottom = 0f;
            
            float firstHeight = model.getItemHeight( 0 );
            
            int n = getItemsCount();
            for ( int i = 0; i < n; i++ )
            {
                /*
                if ( widget.getLeft() + widget.getWidth() > maxRight )
                {
                    maxRight = widget.getLeft() + widget.getWidth();
                }
                */
                
                if ( model.getUsesFixedHeight() )
                    maxBottom += firstHeight;
                else
                    maxBottom += model.getItemHeight( i );
            }
            
            scrollHandler.setBounds( maxRight, maxBottom );
            
            if ( model.getItemsCount() > 0 )
            {
                scrollHandler.setLineHeight( (int)model.getItemHeight( 0 ) );
            }
        }
        
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToHUD( HUD hud )
    {
        super.onAttachedToHUD( hud );
        
        if ( heightByItems >= 0 )
        {
            setHeightByItems( heightByItems );
            
            heightByItems = -1;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setContentClipRect( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        texCanvas.setClip( offsetX - getPaddingLeft(), offsetY - getPaddingTop(), width + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom() );
    }
    
    /**
     * Draws the background of a hovered item.
     * 
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     * 
     * @return true, if a hovered background has been drawn.
     */
    protected boolean drawHoveredItemBackground( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        Colorf color = getHoverBackgroundColor();
        
        if ( color == null )
            return ( false );
        
        if ( color.hasAlpha() )
            DrawUtils.drawImage( color, null, null, texCanvas, offsetX, offsetY, width, height );
        else
            DrawUtils.clearImage( color, null, null, texCanvas, offsetX, offsetY, width, height );
        
        return ( true );
    }
    
    /**
     * Draws the background of a selected item.
     * 
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     * 
     * @return true, if a selected background has been drawn.
     */
    protected boolean drawSelectedItemBackground( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        Colorf color = getSelectionBackgroundColor();
        
        if ( color == null )
            return ( false );
        
        if ( color.hasAlpha() )
            DrawUtils.drawImage( color, null, null, texCanvas, offsetX, offsetY, width, height );
        else
            DrawUtils.clearImage( color, null, null, texCanvas, offsetX, offsetY, width, height );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        ListModel model = getModel();
        
        float offsetY_HUD = childrenOffset_HUD.getY();
        float contentHeight = getContentHeight();
        
        offsetX -= getPaddingLeft();
        //width += getPaddingLeft() + getPaddingRight();
        
        offsetY += childrenOffset_PX.getY();
        //offsetY += getPaddingTop();
        
        float contentWidth = getContentWidth();
        int contentWidthPX = getContentWidthPX();
        int itemBGWidth = contentWidthPX + getPaddingLeft() + getPaddingRight();
        
        int i0 = 0;
        
        if ( model.getUsesFixedHeight() )
        {
            float itemHeight = model.getItemHeight( 0 );
            Dim2i buffer = Dim2i.fromPool();
            getSizeHUD2Pixels_( 0f, itemHeight, buffer );
            int itemHeight_PX = buffer.getHeight();
            Dim2i.toPool( buffer );
            
            i0 = (int)Math.floor( -childrenOffset_HUD.getY() / itemHeight );
            offsetY_HUD += i0 * itemHeight;
            offsetY += i0 * itemHeight_PX;
        }
        
        int n = model.getItemsCount();
        for ( int i = i0; i < n; i++ )
        {
            Widget w = getWidget( contentWidth, i );
            
            if ( offsetY_HUD >= contentHeight )
                break;
            
            int itemHeightPX = __HUD_base_PrivilegedAccess.getHeightPX( w );
            
            if ( offsetY_HUD + w.getHeight() > 0f )
            {
                boolean bg = true;
                
                if ( bg && ( i == currentHoveredItem ) )
                {
                    bg = !drawHoveredItemBackground( texCanvas, offsetX, offsetY, itemBGWidth, itemHeightPX );
                }
                
                if ( bg && model.isSelected( i ) )
                {
                    bg = !drawSelectedItemBackground( texCanvas, offsetX, offsetY, itemBGWidth, itemHeightPX );
                }
                
                if ( i == currentHoveredItem )
                {
                    __HUD_base_PrivilegedAccess.onMouseEntered( w, true, hasFocus( true ) );
                }
                
                w.drawAndUpdateWidget( texCanvas, offsetX + getPaddingLeft(), offsetY, contentWidthPX, itemHeightPX, false );
                
                if ( i == currentHoveredItem )
                {
                    __HUD_base_PrivilegedAccess.onMouseExited( w, true, hasFocus( true ) );
                }
            }
            
            offsetY += itemHeightPX;
            offsetY_HUD += w.getHeight();
        }
        
        lastDrawnHoveredItem = currentHoveredItem;
    }
    
    /**
     * Sets the display offset for the child-Widgets.
     * 
     * @param changeX
     * @param offsetX the new offset
     * @param changeY
     * @param offsetY the new offset
     * @param updateScrollbars
     */
    private void setChildrenOffset( boolean changeX, float offsetX, boolean changeY, float offsetY, boolean updateScrollbars )
    {
        if ( getHUD() == null )
            return;
        
        Dim2i buffer2 = Dim2i.fromPool();
        getSizeHUD2Pixels_( offsetX, offsetY, buffer2 );
        
        if ( changeX )
        {
            childrenOffset_PX.setX( buffer2.getWidth() );
            childrenOffset_HUD.setX( offsetX );
        }
        
        if ( changeY )
        {
            childrenOffset_PX.setY( buffer2.getHeight() );
            childrenOffset_HUD.setY( offsetY );
        }
        
        Dim2i.toPool( buffer2 );
        
        setTextureDirty();
        
        if ( updateScrollbars )
        {
            if ( changeX )
                scrollHandler.setScrollHValue( -(int)offsetX );
            
            if ( changeY )
                scrollHandler.setScrollVValue( -(int)offsetY );
        }
    }
    
    private void initScrollHandler()
    {
        this.scrollHandler = new ScrollHandler( this, getWidgetAssembler(), true, true )
        {
            @Override
            public void onScrolled( Direction direction, int newValue )
            {
                if ( direction == Direction.VERTICAL )
                    setChildrenOffset( false, 0, true, -newValue, false );
                else
                    setChildrenOffset( true, -newValue, false, 0, false );
            }
        };
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
    }
    
    /**
     * Creates a new List Widget.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     * @param listDesc the List.Description to describe this new List Widget
     */
    public List( boolean isHeavyWeight, float width, float height, ListModel model, Description listDesc )
    {
        super( isHeavyWeight, width, height, listDesc.getBackgroundColor(), listDesc.getBackgroundTexture(), TileMode.TILE_BOTH, model );
        
        initScrollHandler();
        
        this.setBorder( listDesc.getBorderDescription() );
        
        this.setPadding( listDesc.getPaddingBottom(), listDesc.getBorderDescription().getRightWidth(), listDesc.getPaddingTop(), listDesc.getBorderDescription().getLeftWidth() );
        
        setFontColor( listDesc.getFontColor() );
        setSelectionFontColor( listDesc.getSelectionFontColor() );
        setFont( listDesc.getFont() );
        
        setHoverBackgroundColor( listDesc.getHoverBackgroundColor() );
        setSelectionBackgroundColor( listDesc.getSelectionBackgroundColor() );
    }
    
    /**
     * Creates a new List Widget.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     */
    public List( boolean isHeavyWeight, float width, float height, ListModel model )
    {
        this( isHeavyWeight, width, height, model, HUD.getTheme().getListDescription() );
    }
    
    /**
     * Creates a new List Widget.
     * 
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     * @param listDesc the List.Description to describe this new List Widget
     */
    public List( float width, float height, ListModel model, Description listDesc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, model, listDesc );
    }
    
    /**
     * Creates a new List Widget.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     */
    public List( float width, float height, ListModel model )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, model );
    }
    
    /**
     * Creates a new List Widget with a {@link TextListModel}.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     * @param listDesc the List.Description to describe this new List Widget
     */
    public static final List newTextList( boolean isHeavyWeight, float width, float height, Description listDesc )
    {
        return ( new List( isHeavyWeight, width, height, new TextListModel(), listDesc ) );
    }
    
    /**
     * Creates a new List Widget with a {@link TextListModel}.
     * 
     * @param width the new width
     * @param height the new height
     * @param listDesc the List.Description to describe this new List Widget
     */
    public static final List newTextList( float width, float height, Description listDesc )
    {
        return ( new List( width, height, new TextListModel(), listDesc ) );
    }
    
    /**
     * Creates a new List Widget with a {@link TextListModel}.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     */
    public static final List newTextList( boolean isHeavyWeight, float width, float height )
    {
        return ( new List( isHeavyWeight, width, height, new TextListModel() ) );
    }
    
    /**
     * Creates a new List Widget with a {@link TextListModel}.
     * 
     * @param width the new width
     * @param height the new height
     */
    public static final List newTextList( float width, float height )
    {
        return ( new List( width, height, new TextListModel() ) );
    }
}
