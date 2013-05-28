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

import java.util.ArrayList;
import java.util.Collections;

import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Tuple2i;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.LocalZIndexComparator;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;

/**
 * An instance of this class is hold by each Widget.
 * It can be used to add Widgets to to create a new
 * Widget from existing ones.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class WidgetAssembler
{
    private static final LocalZIndexComparator Z_INDEX_COMPARATOR = new LocalZIndexComparator();
    
    private final Widget owner;
    private final ArrayList<Widget> managedWidgets = new ArrayList<Widget>();
    private final ArrayList<Widget> widgets = new ArrayList<Widget>();
    private boolean widgetsSorted = true;
    private int additionalContentLeft = 0, additionalContentTop = 0, additionalContentWidth = 0, additionalContentHeight = 0;
    private ArrayList<HUDPickResult> pickedWidgets = null;
    private Widget currentHoveredWidget = null;
    private Widget currentFocussedWidget = null;
    private boolean keyEventsDispatched = false;
    private boolean pickDispatched = false;
    
    final void setHUD( HUD hud )
    {
        for ( int i = 0; i < widgets.size(); i++ )
        {
            widgets.get( i ).setHUD( hud );
        }
    }
    
    final void setContainer( WidgetContainer container )
    {
        for ( int i = 0; i < widgets.size(); i++ )
        {
            widgets.get( i ).setContainer( container, owner );
        }
    }
    
    public void setAdditionalContentSize( int additionalContentLeft, int additionalContentTop, int additionalContentWidth, int additionalContentHeight )
    {
        this.additionalContentLeft = additionalContentLeft;
        this.additionalContentTop = additionalContentTop;
        this.additionalContentWidth = additionalContentWidth;
        this.additionalContentHeight = additionalContentHeight;
        
        owner.updateSizeFactors();
    }
    
    public final int getAdditionalContentLeft()
    {
        return ( additionalContentLeft );
    }
    
    public final int getAdditionalContentTop()
    {
        return ( additionalContentTop );
    }
    
    public final int getAdditionalContentWidth()
    {
        return ( additionalContentWidth );
    }
    
    public final int getAdditionalContentHeight()
    {
        return ( additionalContentHeight );
    }
    
    public final Widget getCurrentFocussedWidget()
    {
        return ( currentFocussedWidget );
    }
    
    /**
     * Sets the whole Widget's Transparency.
     * 
     * @param transparency
     * @param childrenToo
     */
    public void setTransparency( float transparency, boolean childrenToo )
    {
        for ( int i = 0; i < widgets.size(); i++ )
        {
            final Widget widget = widgets.get( i );
            
            if ( widget instanceof WidgetContainer )
                ( (WidgetContainer)widget ).setTransparency( transparency, childrenToo );
            else
                widget.setTransparency( transparency );
        }
    }
    
    /**
     * Enables or disables dispatching of key events to the assemble Widgets.
     * 
     * @param b
     */
    public void setKeyEventsDispatched( boolean b )
    {
        this.keyEventsDispatched = b;
    }
    
    /**
     * @return whether dispatching of key events to the assemble Widgets is
     * enabled.
     */
    public final boolean areKeyEventsDispatched()
    {
        return ( keyEventsDispatched );
    }
    
    /**
     * This event is fired, when a key is pressed on a focused Widget.
     * 
     * @param key the key that was pressed
     * @param modifierMask the mask of modifier keys
     * @param when the keyevent's timestamp
     */
    public void onKeyPressed( Key key, int modifierMask, long when )
    {
        if ( areKeyEventsDispatched() && ( currentFocussedWidget != null ) )
        {
            currentFocussedWidget.onKeyPressed( key, modifierMask, when );
        }
    }
    
    /**
     * This event is fired, when a key is released on a focused Widget.
     * 
     * @param key the key that was released
     * @param modifierMask the mask of modifier keys
     * @param when the keyevent's timestamp
     */
    public void onKeyReleased( Key key, int modifierMask, long when )
    {
        if ( areKeyEventsDispatched() && ( currentFocussedWidget != null ) )
        {
            currentFocussedWidget.onKeyReleased( key, modifierMask, when );
        }
    }
    
    /**
     * This event is fired when a key is typed on the keyboard.
     * 
     * @param ch the typed key's character
     * @param modifierMask the mask of modifier keys
     * @param when the keyevent's timestamp
     */
    public void onKeyTyped( char ch, int modifierMask, long when )
    {
        if ( areKeyEventsDispatched() && ( currentFocussedWidget != null ) )
        {
            currentFocussedWidget.onKeyTyped( ch, modifierMask, when );
        }
    }
    
    /**
     * This event is fired when a ControllerButton has been pressed
     * and this Widget is the currently focussed one.
     * 
     * @param button the pressed button
     * @param when
     */
    public void onControllerButtonPressed( ControllerButton button, long when )
    {
        if ( areKeyEventsDispatched() && ( currentFocussedWidget != null ) )
        {
            currentFocussedWidget.onControllerButtonPressed( button, when );
        }
    }
    
    /**
     * This event is fired when a ControllerButton has been released
     * and this Widget is the currently focussed one.
     * 
     * @param button the released button
     * @param when
     */
    public void onControllerButtonReleased( ControllerButton button, long when )
    {
        if ( areKeyEventsDispatched() && ( currentFocussedWidget != null ) )
        {
            currentFocussedWidget.onControllerButtonReleased( button, when );
        }
    }
    
    /**
     * This event is fired when a ControllerAxis has changed
     * and this Widget is the currently focussed one.
     * 
     * @param axis the changed axis
     * @param axisDelta
     * @param when
     */
    public void onControllerAxisChanged( ControllerAxis axis, int axisDelta, long when )
    {
        if ( areKeyEventsDispatched() && ( currentFocussedWidget != null ) )
        {
            currentFocussedWidget.onControllerAxisChanged( axis, axisDelta, when );
        }
    }
    
    /**
     * This event is fired when the state of any DeviceComponent has changed.
     * 
     * @param comp
     * @param delta
     * @param state
     * @param when
     * @param isTopMost
     * @param hasFocus
     */
    public void onInputStateChanged( DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
        if ( areKeyEventsDispatched() && ( currentFocussedWidget != null ) )
        {
            currentFocussedWidget.onInputStateChanged( comp, delta, state, when, isTopMost, hasFocus );
        }
    }
    
    /**
     * Enables or disables dispatching of pickings to the assemble Widgets.
     * 
     * @param b
     */
    public void setPickDispatched( boolean b )
    {
        this.pickDispatched = b;
    }
    
    /**
     * @return whether dispatching of pickings to the assemble Widgets is
     * enabled.
     */
    public final boolean isPickingDispatched()
    {
        return ( pickDispatched );
    }
    
    /**
     * Dispatches the picking to the assembling Widgets.
     * 
     * @param canvasX the absolute canvas-x-position of the picking
     * @param canvasY the absolute canvas-y-position of the picking
     * @param widgetX
     * @param widgetY
     * @param pickReason the reson of this picking
     * @param button the mouse-button, that caused the picking
     * @param when
     * @param meta
     * @param flags
     * 
     * @return the picked Widget or null
     */
    public boolean pick( int canvasX, int canvasY, float widgetX, float widgetY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        ensureWidgetsSortedByZIndex();
        
        //final boolean isInternal = ( flags & HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL ) != 0;
        final boolean eventsSuppressed = ( flags & HUDPickResult.HUD_PICK_FLAG_EVENTS_SUPPRESSED ) != 0;
        
        HUDPickResult tmpHPR = null;
        HUDPickResult topMost = null;
        
        if ( pickedWidgets != null )
            pickedWidgets.clear();
        else
            pickedWidgets = new ArrayList<HUDPickResult>();
        
        for ( int i = managedWidgets.size() - 1; i >= 0; i-- )
        {
            final Widget widget = managedWidgets.get( i );
            if ( widget.isVisible() && widget.isPickable() )
            {
                tmpHPR = widget.pick( canvasX, canvasY, pickReason, button, when, meta, flags );
                if ( tmpHPR != null )
                {
                    pickedWidgets.add( tmpHPR );
                    
                    if ( ( topMost == null ) || ( topMost.getWidget().getZIndex() <= tmpHPR.getWidget().getZIndex() ) )
                    {
                        topMost = tmpHPR;
                    }
                }
            }
        }
        
        if ( ( currentHoveredWidget != null ) && ( ( topMost == null ) || ( topMost.getWidget() != currentHoveredWidget ) ) )
        {
            currentHoveredWidget.onMouseExited( true, false );
            currentHoveredWidget = null;
        }
        
        if ( !eventsSuppressed )
        {
            for ( int i = 0; i < pickedWidgets.size(); i++ )
            {
                final HUDPickResult hpr = pickedWidgets.get( i );
                final Widget pickedWidget = hpr.getWidget();
                final boolean isTopMost = ( pickedWidget == topMost.getWidget() );
                boolean hasFocus = ( pickedWidget == currentFocussedWidget );
                
                float pickXHUD_ = widgetX - pickedWidget.getLeft() + owner.getLeft();
                float pickYHUD_ = widgetY - pickedWidget.getTop() + owner.getTop();
                
                switch ( pickReason )
                {
                    case BUTTON_PRESSED:
                        if ( isTopMost )
                        {
                            if ( ( currentFocussedWidget != pickedWidget ) && pickedWidget.isFocussable() )
                            {
                                if ( currentFocussedWidget != null )
                                    currentFocussedWidget.onFocusLost();
                                
                                currentFocussedWidget = pickedWidget;
                                currentFocussedWidget.onFocusGained();
                                hasFocus = true;
                            }
                        }
                        
                        pickedWidget.onMouseButtonPressed( button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        
                        break;
                    
                    case BUTTON_RELEASED:
                        pickedWidget.onMouseButtonReleased( button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        
                        break;
                    
                    case MOUSE_MOVED:
                        pickedWidget.onMouseMoved( pickXHUD_, pickYHUD_, (int)meta, when, isTopMost, hasFocus );
                        
                        if ( ( currentHoveredWidget == null ) && ( isTopMost ) )
                        {
                            currentHoveredWidget = pickedWidget;
                            currentHoveredWidget.onMouseEntered( isTopMost, hasFocus );
                        }
                        break;
                    
                    case MOUSE_WHEEL_MOVED_UP:
                        pickedWidget.onMouseWheelMoved( +1, meta != 0L, pickXHUD_, pickYHUD_, when, isTopMost );
                        break;
                    
                    case MOUSE_WHEEL_MOVED_DOWN:
                        pickedWidget.onMouseWheelMoved( -1, meta != 0L, pickXHUD_, pickYHUD_, when, isTopMost );
                        break;
                }
                
                //if ( !isTopMost )
                    HUDPickResult.toPool( hpr );
            }
        }
        else
        {
            for ( int i = 0; i < pickedWidgets.size(); i++ )
            {
                HUDPickResult.toPool( pickedWidgets.get( i ) );
            }
        }
        
        pickedWidgets.clear();
        
        return ( topMost != null );
    }
    
    /**
     * Retrieves the SubWidget which is actually picked.
     * 
     * @param relX the mouse-x-position relative to the owner Widget's position
     * @param relY the mouse-y-position relative to the owner Widget's position
     */
    public Widget pick( float relX, float relY )
    {
        ensureWidgetsSortedByZIndex();
        
        Widget topMost = null;
        
        float ownerLeft = owner.getLeft();
        float ownerTop = owner.getTop();
        
        for ( int i = 0; i < managedWidgets.size(); i++ )
        {
            final Widget widget = managedWidgets.get( i );
            if ( widget.isVisible() && widget.isPickable() )
            {
                if ( ( ( widget.getLeft() - ownerLeft <= relX ) && ( relX <= widget.getLeft() - ownerLeft + widget.getWidth() ) ) && ( ( widget.getTop() - ownerTop <= relY ) && ( relY <= widget.getTop() - ownerTop + widget.getHeight() ) ) )
                {
                    //if ( ( topMost == null ) || ( topMost.getSGZPosition() <= widget.getSGZPosition() ) )
                    if ( ( topMost == null ) || ( topMost.getZIndex() <= widget.getZIndex() ) )
                    {
                        topMost = widget;
                    }
                }
            }
        }
        
        return ( topMost );
    }
    
    /**
     * Repositions the Widget relative to the owner Widget
     * 
     * @param widget the Widget to reposition
     * @param posX the new x-position
     * @param posY the new y-position
     */
    public void reposition( Widget widget, float posX, float posY )
    {
        widget.setLocation( owner.getLeft() + posX, owner.getTop() + posY );
    }
    
    /**
     * Called by the owner when its location has changed.
     * 
     * @param deltaX
     * @param deltaY
     */
    public void onOwnerMoved( float deltaX, float deltaY, boolean needsTextureRefresh )
    {
        for ( int i = 0; i < managedWidgets.size(); i++ )
        {
            Widget widget = managedWidgets.get( i );
            
            widget.setLocation( widget.getLeft() + deltaX, widget.getTop() + deltaY, false, needsTextureRefresh );
        }
    }
    
    /**
     * @return the x-position relative to the owner Widget
     * 
     * @param widget the Widget in question
     */
    public final float getPositionX( Widget widget )
    {
        return ( widget.getLeft() - owner.getLeft() );
    }
    
    /**
     * @return the y-position relative to the owner Widget
     * 
     * @param widget the Widget in question
     */
    public final float getPositionY( Widget widget )
    {
        return ( widget.getTop() - owner.getTop() );
    }
    
    /**
     * @return <i>true</i>, if the Widget is in the List of Assemble-Widgets
     */
    public boolean contains( Widget widget )
    {
        return ( widgets.contains( widget ) );
    }
    
    private void ensureWidgetsSortedByZIndex()
    {
        if ( widgetsSorted )
            return;
        
        Collections.sort( widgets, Z_INDEX_COMPARATOR );
        
        widgetsSorted = true;
    }
    
    /**
     * Adds a Widget to this assembler.
     * 
     * @param widget the Widget to add
     * @param locX the x-location to add the Widget at (relative to the owner's position)
     * @param locY the y-location to add the Widget at (relative to the owner's position)
     */
    public void addWidget( Widget widget, float locX, float locY )
    {
        if ( widget.isHeavyWeight() )
            throw new IllegalArgumentException( "You cannot add a heavyweight Widget to the WidgetAssembler." );
        
        widgets.add( widget );
        managedWidgets.add( widget );
        
        reposition( widget, locX, locY );
        
        widget.setContainer( owner.getContainer(), owner );
        widget.setHUD( owner.getHUD() );
        widget.setHostWidget( owner );
        
        widgetsSorted = false;
    }
    
    /**
     * Adds a Widget to this assembler at location(0, 0).
     * 
     * @param widget the Widget to add
     */
    public final void addWidget( Widget widget )
    {
        addWidget( widget, widget.getLeft(), widget.getTop() );
    }
    
    /**
     * Adds an unmanaged Widget to this assembler.
     * 
     * @param widget the Widget to add
     */
    public void addUnmanagedWidget( Widget widget )
    {
        if ( widget.isHeavyWeight() )
            throw new IllegalArgumentException( "You cannot add a heavyweight Widget to the WidgetAssembler." );
        
        widgets.add( widget );
        
        widget.setPassive( true );
        
        widget.setContainer( owner.getContainer(), owner );
        widget.setHUD( owner.getHUD() );
    }
    
    /**
     * Removes a Widget from this assembler.
     * 
     * @param widget the Widget to be removed
     */
    public void removeWidget( Widget widget )
    {
        if ( widget.getAssembly() != owner )
            throw new Error( "The given Widget doesn't belong to this owner Widget." );
        
        managedWidgets.remove( widget );
        widgets.remove( widget );
        
        widget.setPassive( false );
        
        widget.setHostWidget( null );
        widget.setHUD( null );
        widget.setContainer( null, null );
    }
    
    public void update()
    {
        for ( int i = 0; i < widgets.size(); i++ )
        {
            //widgets.get( i ).setContainer( owner.getContainer() );
            widgets.get( i ).update();
        }
        
        //updateLocations( owner.getLeft(), owner.getTop() );
    }
    
    final void setWidgetsPassive( boolean passive )
    {
        for ( int i = 0; i < widgets.size(); i++ )
        {
            widgets.get( i ).setPassive( passive );
        }
    }
    
    protected void setWidgetsDirty()
    {
        for ( int i = 0; i < widgets.size(); i++ )
        {
            //if ( !widgets.get( i ).isHeavyWeight() )
                widgets.get( i ).setWidgetDirty();
        }
    }
    
    protected void draw( Texture2DCanvas texCanvas, int offsetX, int offsetY )
    {
        ensureWidgetsSortedByZIndex();
        
        Tuple2i tmpLoc = Tuple2i.fromPool();
        Dim2i tmpSize = Dim2i.fromPool();
        
        float ownerLeft = owner.getLeft();
        float ownerTop = owner.getTop();
        
        for ( int i = 0; i < managedWidgets.size(); i++ )
        {
            Widget widget = managedWidgets.get( i );
            
            if ( widget.isVisible() )
            {
                widget.getRelLocationHUD2Pixels_( widget.getLeft() - ownerLeft, widget.getTop() - ownerTop, tmpLoc );
                widget.getSizeHUD2Pixels_( widget.getWidth(), widget.getHeight(), tmpSize );
                
                widget.drawAndUpdateWidget( texCanvas, offsetX + tmpLoc.getX(), offsetY + tmpLoc.getY(), tmpSize.getWidth(), tmpSize.getHeight(), false );
            }
        }
        
        Dim2i.toPool( tmpSize );
        Tuple2i.toPool( tmpLoc );
    }
    
    public WidgetAssembler( Widget owner )
    {
        this.owner = owner;
    }
}
