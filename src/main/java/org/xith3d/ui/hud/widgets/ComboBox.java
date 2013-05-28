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

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point2f;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.__HUD_PrivilegedAccess;
import org.xith3d.ui.hud.base.AbstractButton;
import org.xith3d.ui.hud.base.AbstractList;
import org.xith3d.ui.hud.base.ListModel;
import org.xith3d.ui.hud.base.TextWidget;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;
import org.xith3d.ui.hud.listeners.ListSelectionListener;
import org.xith3d.ui.hud.listeners.WidgetEventsReceiverAdapter;
import org.xith3d.ui.hud.listmodels.TextListModel;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.PopUpable;
import org.xith3d.ui.hud.utils.ScrollMode;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * A ComboBox is a bordered Label with a Button on the right, that pops up a
 * List widget and displays the current selected Item's text.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ComboBox extends AbstractList implements TextWidget, Enableable, PopUpable
{
    private static final boolean DEFAULT_HEAVYWEIGHT = false;
    
    public static class Description extends Widget.DescriptionBase
    {
        private TextField.Description textFieldDesc;
        private List.Description listDesc;
        private Texture2D buttonSymbol;
        
        public void setTextFieldDescription( TextField.Description textFieldDesc )
        {
            this.textFieldDesc = textFieldDesc;
        }
        
        public TextField.Description getTextFieldDescription()
        {
            return ( textFieldDesc );
        }
        
        public void setListDescription( List.Description listDesc )
        {
            this.listDesc = listDesc;
        }
        
        public List.Description getListDescription()
        {
            return ( listDesc );
        }
        
        public Texture2D getButtonSymbol()
        {
            return ( buttonSymbol );
        }
        
        public void setButtonSymbol( Texture2D texture )
        {
            buttonSymbol = texture;
        }
        
        public void setButtonSymbol( String texture )
        {
            setButtonSymbol( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        public void set( Description template )
        {
            this.textFieldDesc = template.textFieldDesc.clone();
            this.listDesc = template.listDesc.clone();
            this.buttonSymbol = template.buttonSymbol;
        }
        
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        private Description( Description template )
        {
            this.set( template );
        }
        
        public Description( TextField.Description textFieldDesc, List.Description listDesc, Texture2D buttonSymbol )
        {
            this.textFieldDesc = textFieldDesc;
            this.listDesc = listDesc;
            this.buttonSymbol = buttonSymbol;
        }
        
        public Description( TextField.Description textFieldDesc, List.Description listDesc, String buttonSymbol )
        {
            this( textFieldDesc, listDesc, HUDTextureUtils.getTexture( buttonSymbol, true ) );
        }
    }
    
    private class AssemblerEventsReceiver extends WidgetEventsReceiverAdapter
    {
        public boolean selectionChanged = false;
        
        @Override
        public void onButtonClicked( AbstractButton button, Object userObject )
        {
            if ( ( button == ComboBox.this.button ) && ComboBox.this.isEnabled() )
            {
                popUp( !isPoppedUp() );
            }
        }
        
        @Override
        public void onListSelectionChanged( AbstractList list, Object oldSelectedItem, Object newSelectedItem, int oldSelectedIndex, int newSelectedIndex )
        {
            if ( textField != null )
            {
                textField.setText( ( newSelectedItem != null ) ? String.valueOf( newSelectedItem ) : "" );
            }
            
            selectionChanged = true;
            
            for ( int i = 0; i < selectionListeners.size(); i++ )
                selectionListeners.get( i ).onListSelectionChanged( ComboBox.this, oldSelectedItem, newSelectedItem, oldSelectedIndex, newSelectedIndex );
        }
        
        @Override
        public void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
        {
            if ( selectionChanged )
                popUp( false );
        }
        
        @Override
        public void onListItemClicked( AbstractList list, Object item, int itemIndex )
        {
            for ( int i = 0; i < selectionListeners.size(); i++ )
                selectionListeners.get( i ).onListItemClicked( ComboBox.this, item, itemIndex );
        }
    }
    
    private final TextField textField;
    private final Button button;
    private final List list;
    
    private int maxListHeightByItems = -1;
    
    private final AssemblerEventsReceiver assemblerEventsReceiver = new AssemblerEventsReceiver();
    private final ArrayList<ListSelectionListener> selectionListeners = new ArrayList<ListSelectionListener>( 1 );
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected float getMinWidth()
    {
        return ( 50f );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected float getMinHeight()
    {
        return ( 10f );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setModel( ListModel model )
    {
        if ( list != null )
            list.setModel( model );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ListModel getModel()
    {
        return ( list.getModel() );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setPadding( int paddingBottom, int paddingRight, int paddingTop, int paddingLeft )
    {
        boolean b1 = textField.setPadding( 0, paddingRight, 0, paddingLeft );
        boolean b2 = list.setPadding( paddingBottom, paddingRight, paddingTop, paddingLeft );
        
        return ( b1 || b2 );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingBottom()
    {
        return ( list.getPaddingBottom() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingRight()
    {
        return ( textField.getPaddingRight() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingTop()
    {
        return ( list.getPaddingTop() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getPaddingLeft()
    {
        return ( textField.getPaddingLeft() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setHoverBackgroundColor( Colorf color )
    {
        list.setHoverBackgroundColor( color );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Colorf getHoverBackgroundColor()
    {
        return ( list.getHoverBackgroundColor() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionBackgroundColor( Colorf color )
    {
        list.setSelectionBackgroundColor( color );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Colorf getSelectionBackgroundColor()
    {
        return ( list.getSelectionBackgroundColor() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlignment( TextAlignment alignment )
    {
        textField.setAlignment( alignment );
        list.setAlignment( alignment );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final TextAlignment getAlignment()
    {
        return ( textField.getAlignment() );
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectedIndex( int itemIndex )
    {
        super.setSelectedIndex( itemIndex );
        
        textField.setText( String.valueOf( getSelectedItem() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddItemSetsSelectedItem( boolean b )
    {
        list.setAddItemSetsSelectedItem( b );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean addItemSetsSelectedItem()
    {
        return ( list.addItemSetsSelectedItem() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object addItem( int index, Object item )
    {
        list.addItem( index, item );
        
        if ( addItemSetsSelectedItem() )
            textField.setText( String.valueOf( item ) );
        
        setMaxListHeightByItems( getMaxListHeightByItems() );
        
        return ( item );
    }
    
    public void addItem( int index, String text, HUDFont font, Colorf color )
    {
        list.addItem( index, text, font, color );
        
        setMaxListHeightByItems( getMaxListHeightByItems() );
    }
    
    public final void addItem( String text, HUDFont font, Colorf color )
    {
        addItem( getItemsCount(), text, font, color );
    }
    
    public final void addItem( int index, String text, Colorf color )
    {
        addItem( index, text, null, color );
    }
    
    public final void addItem( String text, Colorf color )
    {
        addItem( getItemsCount(), text, null, color );
    }
    
    public final void addItem( int index, String text )
    {
        addItem( index, text, null, null );
    }
    
    public final void addItem( String text )
    {
        addItem( getItemsCount(), text, null, null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String removeItem( int index )
    {
        String item = (String)super.removeItem( index );
        
        setMaxListHeightByItems( getMaxListHeightByItems() );
        
        return ( item );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public void addItems( java.util.List items )
    {
        super.addItems( items );
        
        setMaxListHeightByItems( getMaxListHeightByItems() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addItems( Object[] items )
    {
        super.addItems( items );
        
        setMaxListHeightByItems( getMaxListHeightByItems() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        super.clear();
        
        textField.setText( "" );
        
        setMaxListHeightByItems( getMaxListHeightByItems() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTopIndex( int topIndex )
    {
        list.setTopIndex( topIndex );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopIndex()
    {
        return ( list.getTopIndex() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getBottomIndex()
    {
        return ( list.getBottomIndex() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollSelectedItemIntoView()
    {
        list.scrollSelectedItemIntoView();
    }
    
    /**
     * Sets the list's height by items count.
     * 
     * @param numItems the new height by items unit
     */
    public void setListHeightByItems( int numItems )
    {
        list.setHeightByItems( numItems );
    }
    
    /**
     * Gets the list's height by items count.
     * 
     * @return the height by items count.
     */
    public int getListHeightByItems()
    {
        return ( list.getHeightByItems() );
    }
    
    /**
     * Sets the maximum list's height by items.<br>
     * If the list contains less items, it is sized appropriately.
     * 
     * @param maxHeight
     */
    public void setMaxListHeightByItems( int maxHeight )
    {
        this.maxListHeightByItems = maxHeight;
        
        if ( ( maxListHeightByItems > 0 ) && ( getHUD() != null ) )
        {
            setListHeightByItems( Math.min( getItemsCount(), maxListHeightByItems ) );
        }
    }
    
    /**
     * Gets the maximum list's height by items.
     * 
     * @return the maximum list's height by items.
     */
    public int getMaxListHeightByItems()
    {
        return ( maxListHeightByItems );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean blocksFocusMoveDeviceComponent( DeviceComponent dc )
    {
        return ( textField.blocksFocusMoveDeviceComponent( dc ) || list.blocksFocusMoveDeviceComponent( dc ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setText( String text )
    {
        throw new UnsupportedOperationException( "You cannot set the Text of a ComboBox." );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return ( textField.getText() );
    }
    
    /**
     * {@inheritDoc}
     */
    public HUDFont getFont()
    {
        return ( textField.getFont() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFont( HUDFont font )
    {
        textField.setFont( font );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFontColor( Colorf color )
    {
        textField.setFontColor( color );
    }
    
    /**
     * {@inheritDoc}
     */
    public Colorf getFontColor()
    {
        return ( textField.getFontColor() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setEnabled( boolean enabled )
    {
        textField.setEnabled( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isEnabled()
    {
        return ( textField.isEnabled() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        final float buttonWidth = ( button != null ) ? newHeight : 0f;
        
        if ( textField != null )
            textField.setSize( newWidth - buttonWidth, newHeight );
        if ( button != null )
            button.setSize( buttonWidth, newHeight );
        list.setSize( newWidth, list.getHeight() );
        
        if ( button != null )
            getWidgetAssembler().reposition( button, newWidth - buttonWidth, 0f );
    }
    
    /**
     * @return the height on which to pick. By default this is exactly getHeight().
     */
    @Override
    protected float getPickHeight()
    {
        /*
        if ( isPoppedUp() )
            return ( super.getPickHeight() + list.getHeight() );
        else
        */
            return ( super.getPickHeight() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void popUp( boolean p )
    {
        if ( p == isPoppedUp() )
            return;
        
        final HUD hud = getHUD();
        
        if ( hud == null )
            return;
        
        if ( p )
        {
            Point2f listPos = Point2f.fromPool();
            getAbsoluteLocationOnHUD_( listPos );
            listPos.addY( this.getHeight() );
            
            __HUD_PrivilegedAccess.addVolatilePopup( hud, list, this, listPos.getX(), listPos.getY() );
            
            Point2f.toPool( listPos );
            
            assemblerEventsReceiver.selectionChanged = false;
        }
        else if ( list.getHUD() != null )
        {
            __HUD_PrivilegedAccess.removeVolatilePopup( hud );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isPoppedUp()
    {
        //return ( list.isVisible() );
        //return ( list.getContainer() != null );
        return ( list.getHUD() != null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onFocusLost()
    {
        super.onFocusLost();
        
        /*
        if ( isPoppedUp() )
            popUp( false );
        */
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseExited( isTopMost, hasFocus );
        
        __HUD_base_PrivilegedAccess.onMouseExited( button, isTopMost, hasFocus );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttachedToHUD( HUD hud )
    {
        super.onAttachedToHUD( hud );
        
        setMaxListHeightByItems( maxListHeightByItems );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        if ( getSelectedIndex() != -1 )
        {
            textField.setText( String.valueOf( getSelectedItem() ) );
        }
        
        getWidgetAssembler().addWidget( this.textField, 0f, 0f );
        
        getWidgetAssembler().addWidget( button, this.textField.getWidth(), 0f );
    }
    
    /**
     * Creates a new ComboBox.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     * @param desc
     */
    public ComboBox( boolean isHeavyWeight, float width, float height, ListModel listModel, Description desc )
    {
        super( isHeavyWeight, width, height, null, null, null, listModel );
        
        TextField tf = new TextField( getWidth() - getHeight(), getHeight(), "", desc.getTextFieldDescription() );
        tf.setAlignment( TextAlignment.CENTER_LEFT );
        tf.setEditable( false );
        this.textField = tf;
        
        this.button = new Button( getHeight(), getHeight(), "" );
        button.setIcon( desc.buttonSymbol );
        button.addButtonListener( assemblerEventsReceiver );
        
        this.list = new List( true, getWidth(), getHeight() * 5f, listModel, desc.getListDescription() );
        list.addSelectionListener( this.assemblerEventsReceiver );
        list.addMouseListener( assemblerEventsReceiver );
        
        getWidgetAssembler().setKeyEventsDispatched( true );
        getWidgetAssembler().setPickDispatched( true );
        
        setMaxListHeightByItems( 5 );
        list.setScrollMode( ScrollMode.AUTO );
    }
    
    /**
     * Creates a new ComboBox.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     */
    public ComboBox( boolean isHeavyWeight, float width, float height, ListModel listModel )
    {
        this( isHeavyWeight, width, height, listModel, HUD.getTheme().getComboBoxDescription() );
    }
    
    /**
     * Creates a new ComboBox.
     * 
     * @param width the new width
     * @param model the ListModel (null for auto-generation)
     * @param height the new height
     * @param desc
     */
    public ComboBox( float width, float height, ListModel listModel, Description desc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, listModel, desc );
    }
    
    /**
     * Creates a new ComboBox.
     * 
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     */
    public ComboBox( float width, float height, ListModel listModel )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, listModel );
    }
    
    /**
     * Creates a new ComboBox with a {@link TextListModel}.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     * @param desc
     */
    public static final ComboBox newTextCombo( boolean isHeavyWeight, float width, float height, Description desc )
    {
        return ( new ComboBox( isHeavyWeight, width, height, new TextListModel(), desc ) );
    }
    
    /**
     * Creates a new ComboBox with a {@link TextListModel}.
     * 
     * @param isHeavyWeight
     * @param width the new width
     * @param height the new height
     */
    public static final ComboBox newTextCombo( boolean isHeavyWeight, float width, float height )
    {
        return ( new ComboBox( isHeavyWeight, width, height, new TextListModel() ) );
    }
    
    /**
     * Creates a new ComboBox with a {@link TextListModel}.
     * 
     * @param width the new width
     * @param model the ListModel (null for auto-generation)
     * @param height the new height
     * @param desc
     */
    public static final ComboBox newTextCombo( float width, float height, Description desc )
    {
        return ( new ComboBox( width, height, new TextListModel(), desc ) );
    }
    
    /**
     * Creates a new ComboBox with a {@link TextListModel}.
     * 
     * @param width the new width
     * @param height the new height
     * @param model the ListModel (null for auto-generation)
     */
    public static final ComboBox newTextCombo( float width, float height )
    {
        return ( new ComboBox( width, height, new TextListModel() ) );
    }
}
