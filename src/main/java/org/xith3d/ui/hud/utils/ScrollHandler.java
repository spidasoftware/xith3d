/**
 * Copyright (c) 2003-2008, Xith3D Project Group all rights reserved.
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
package org.xith3d.ui.hud.utils;

import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2f;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetAssembler;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;
import org.xith3d.ui.hud.listeners.ScrollbarListener;
import org.xith3d.ui.hud.listeners.WidgetContainerListener;
import org.xith3d.ui.hud.listeners.WidgetLocationListener;
import org.xith3d.ui.hud.listeners.WidgetMouseListener;
import org.xith3d.ui.hud.listeners.WidgetSizeListener;
import org.xith3d.ui.hud.widgets.Image;
import org.xith3d.ui.hud.widgets.Scrollbar;

/**
 * A ScrollHandler is capable of managing communication between a scrollable Widget
 * and the ScrollBars as well as properly adding the ScrollBars to the Widget
 * on demand.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ScrollHandler implements ScrollbarListener, WidgetContainerListener, WidgetLocationListener, WidgetSizeListener, WidgetMouseListener
{
    private final Widget widget;
    private final WidgetAssembler widgetAssembler;
    
    private float maxRight = 0f, maxBottom = 0f;
    
    private ScrollMode mode = ScrollMode.AUTO;
    private Scrollbar scrollbarH = null, scrollbarV = null;
    private Image spacerImage;
    
    private int numIgnoredEvents_H = 0;
    private int numIgnoredEvents_V = 0;
    
    public abstract void onScrolled( Scrollbar.Direction direction, int newValue );
    
    /**
     * {@inheritDoc}
     */
    public void onScrollbarValueChanged( Scrollbar scrollbar, int newValue )
    {
        if ( ( scrollbar.getDirection() == Scrollbar.Direction.HORIZONTAL ) && ( numIgnoredEvents_H > 0 ) )
        {
            numIgnoredEvents_H--;
            return;
        }
        
        if ( ( scrollbar.getDirection() == Scrollbar.Direction.VERTICAL ) && ( numIgnoredEvents_V > 0 ) )
        {
            numIgnoredEvents_V--;
            return;
        }
        
        onScrolled( scrollbar.getDirection(), newValue );
    }
    
    public void setScrollMode( ScrollMode mode )
    {
        this.mode = mode;
    }
    
    public final ScrollMode getScrollMode()
    {
        return ( mode );
    }
    
    public void setLineHeight( int lineHeight )
    {
        if ( scrollbarV == null )
            return;
        
        scrollbarV.setSmallIncrement( lineHeight );
    }
    
    public final int getLineHeight()
    {
        if ( scrollbarV == null )
            return ( 0 );
        
        return ( scrollbarV.getSmallIncrement() );
    }
    
    public void setScrollHValue( int value )
    {
        if ( scrollbarH == null )
            return;
        
        numIgnoredEvents_H++;
        scrollbarH.setValue( value );
    }
    
    public void setScrollVValue( int value )
    {
        if ( scrollbarV == null )
            return;
        
        numIgnoredEvents_V++;
        scrollbarV.setValue( value );
    }
    
    private final void mergeWidgetIntoBounds( Widget widget )
    {
        if ( widget.getLeft() + widget.getWidth() > maxRight )
        {
            maxRight = widget.getLeft() + widget.getWidth();
        }
        
        if ( widget.getTop() + widget.getHeight() > maxBottom )
        {
            maxBottom = widget.getTop() + widget.getHeight();
        }
    }
    
    private void updateScrollbarsFromBounds()
    {
        final boolean scrollHForced = ( ( mode == ScrollMode.ALWAYS ) || ( mode == ScrollMode.ALWAYS_HORIZONTAL ) );
        final boolean scrollVForced = ( ( mode == ScrollMode.ALWAYS ) || ( mode == ScrollMode.ALWAYS_VERTICAL ) );
        
        if ( scrollbarH != null )
            scrollbarH.setVisible( scrollHForced );
        if ( scrollbarV != null )
            scrollbarV.setVisible( scrollVForced );
        if ( spacerImage != null )
            spacerImage.setVisible( false );
        
        widgetAssembler.setAdditionalContentSize( 0, 0, 0, 0 );
        
        if ( mode != ScrollMode.NEVER )
        {
            float contentWidth = widget.getContentWidth();
            float contentHeight = widget.getContentHeight();
            
            int acw = 0;
            int ach = 0;
            
            boolean scrollV = false;
            boolean scrollH = false;
            
            if ( ( scrollbarV != null ) && ( scrollVForced || ( maxBottom >  contentHeight ) ) )
            {
                scrollV = true;
                
                scrollbarV.setVisible( true );
                
                acw = scrollbarV.getWidthOrHeightInPixels();
                
                widgetAssembler.setAdditionalContentSize( 0, 0, -acw, -ach );
                contentWidth = widget.getContentWidth();
                contentHeight = widget.getContentHeight();
            }
            
            if ( ( scrollbarH != null ) && ( scrollHForced || ( maxRight >  contentWidth ) ) )
            {
                scrollH = true;
                
                scrollbarH.setVisible( true );
                
                ach = scrollbarH.getWidthOrHeightInPixels();
                
                widgetAssembler.setAdditionalContentSize( 0, 0, -acw, -ach );
                contentWidth = widget.getContentWidth();
                contentHeight = widget.getContentHeight();
            }
            
            if ( ( scrollbarV != null ) && !scrollbarV.isVisible() && ( scrollbarH != null ) && scrollbarH.isVisible() && ( scrollVForced || ( maxBottom >  contentHeight ) ) )
            {
                scrollV = true;
                
                scrollbarV.setVisible( true );
                
                acw = scrollbarV.getWidthOrHeightInPixels();
                
                widgetAssembler.setAdditionalContentSize( 0, 0, -acw, -ach );
                contentWidth = widget.getContentWidth();
                contentHeight = widget.getContentHeight();
            }
            
            
            if ( ( scrollbarV != null ) && ( maxBottom >  contentHeight ) )
            {
                int maxValue = (int)Math.ceil( maxBottom - contentHeight );
                int value = Math.min( scrollbarV.getValue(), maxValue );
                
                scrollbarV.setMinMaxAndValue( 0, maxValue, value, (int)contentHeight );
            }
            else
            {
                scrollbarV.setMinMaxAndValue( 0, 0, 0 );
            }
            
            if ( ( scrollbarH != null ) && ( maxRight >  contentWidth ) )
            {
                int maxValue = (int)Math.ceil( maxRight - contentWidth );
                int value = Math.min( scrollbarH.getValue(), maxValue );
                
                scrollbarH.setMinMaxAndValue( 0, maxValue, value, (int)contentWidth );
            }
            else
            {
                scrollbarH.setMinMaxAndValue( 0, 0, 0 );
            }
            
            //if ( ( spacerImage != null ) && ( scrollbarH != null ) && scrollbarH.isVisible() && ( scrollbarV != null ) && scrollbarV.isVisible() )
            if ( ( spacerImage != null ) && scrollH && scrollV )
            {
                spacerImage.setVisible( true );
            }
        }
    }
    
    private final void updateBounds()
    {
        if ( ( !( widget instanceof WidgetContainer ) ) || ( widget.getHUD() == null ) )
            return;
        
        WidgetContainer wc = (WidgetContainer)widget;
        
        maxRight = 0f;
        maxBottom = 0f;
        
        for ( int i = 0; i < wc.getWidgetsCount(); i++ )
        {
            Widget w = wc.getWidget( i );
            
            mergeWidgetIntoBounds( w );
        }
        
        updateScrollbarsFromBounds();
    }
    
    public void setBounds( float maxRight, float maxBottom )
    {
        this.maxRight = maxRight;
        this.maxBottom = maxBottom;
        
        updateScrollbarsFromBounds();
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetAttachedToContainer( Widget widget, WidgetContainer container )
    {
        if ( widget != this.widget )
        {
            widget.addSizeListener( this );
            widget.addLocationListener( this );
            
            mergeWidgetIntoBounds( widget );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetDetachedFromContainer( Widget widget, WidgetContainer container )
    {
        if ( widget != this.widget )
        {
            widget.removeSizeListener( this );
            widget.removeLocationListener( this );
            
            mergeWidgetIntoBounds( widget );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetAttachedToHUD( Widget widget, HUD hud )
    {
        if ( widget == this.widget )
        {
            updateScrollbarSizesToMatchWidget();
            //updateScrollbarsFromBounds();
            updateBounds();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetDetachedFromHUD( Widget widget, HUD hud )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetDragStarted( Widget widget )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetDragStopped( Widget widget )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetLocationChanged( Widget widget, float oldLeft, float oldTop, float newLeft, float newTop )
    {
        updateBounds();
    }
    
    /**
     * {@inheritDoc}
     */
    public void onWidgetSizeChanged( Widget widget, float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        if ( widget == this.widget )
        {
            if ( widget.getHUD() != null )
                updateScrollbarSizesToMatchWidget();
        }
        else 
        {
            updateBounds();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonPressed( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseMoved( Widget widget, float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseStopped( Widget widget, float x, float y, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseWheelMoved( Widget widget, int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost )
    {
        if ( ( scrollbarV != null ) && scrollbarV.isVisible() )
        {
            scrollbarV.setValue( scrollbarV.getValue() - ( delta * scrollbarV.getSmallIncrement() ) );
            
            return;
        }
        
        if ( ( scrollbarH != null ) && scrollbarH.isVisible() )
        {
            scrollbarH.setValue( scrollbarH.getValue() - ( delta * scrollbarH.getSmallIncrement() ) );
            
            return;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseEntered( Widget widget, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseExited( Widget widget, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onInputStateChanged( Widget widget, DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * Creates the horizontal {@link Scrollbar}.
     * 
     * @param width the width to cover
     * 
     * @return the horizontal {@link Scrollbar}.
     */
    protected Scrollbar createHorizontalScrollbar( float width )
    {
        return ( new Scrollbar( width, Scrollbar.Direction.HORIZONTAL ) );
    }
    
    /**
     * Creates the vertical {@link Scrollbar}.
     * 
     * @param height the height to cover
     * 
     * @return the vertical {@link Scrollbar}.
     */
    protected Scrollbar createVerticalScrollbar( float height )
    {
        return ( new Scrollbar( height, Scrollbar.Direction.VERTICAL ) );
    }
    
    private void updateScrollbarSizesToMatchWidget()
    {
        float left = 0f;
        float top = 0f;
        float width = widget.getWidth();
        float height = widget.getHeight();
        
        if ( widget.getBorder() != null )
        {
            Dim2f buffer1 = Dim2f.fromPool();
            Dim2f buffer2 = Dim2f.fromPool();
            
            __HUD_base_PrivilegedAccess.getBorderSizeInHUDSpace( widget, buffer1, buffer2 );
            
            left += buffer1.getWidth();
            top += buffer1.getHeight();
            width -= buffer1.getWidth() + buffer2.getWidth();
            height -= buffer1.getHeight() + buffer2.getHeight();
            
            Dim2f.toPool( buffer2 );
            Dim2f.toPool( buffer1 );
        }
        
        final boolean scrollHorizontal = ( ( scrollbarH != null ) && scrollbarH.isVisible() );
        final boolean scrollVertical = ( ( scrollbarV != null ) && scrollbarV.isVisible() );
        int sbW = 0, sbH = 0;
        
        if ( scrollHorizontal )
        {
            scrollbarH.setSize( width - ( scrollVertical ? scrollbarV.getWidth() : 0f ), scrollbarH.getHeight() );
            widgetAssembler.reposition( scrollbarH, left, top + height - scrollbarH.getHeight() );
            
            sbH = scrollbarH.getWidthOrHeightInPixels();
        }
        
        if ( scrollVertical )
        {
            scrollbarV.setSize( scrollbarV.getWidth(), height - ( scrollHorizontal ? scrollbarH.getHeight() : 0f ) );
            widgetAssembler.reposition( scrollbarV, left + width - scrollbarV.getWidth(), top );
            
            sbW = scrollbarV.getWidthOrHeightInPixels();
        }
        
        if ( scrollHorizontal && scrollVertical )
        {
            this.spacerImage.setSize( scrollbarV.getWidth(), scrollbarH.getHeight() );
            widgetAssembler.reposition( spacerImage, left + width - scrollbarV.getWidth(), top + height - scrollbarH.getHeight() );
        }
        
        widgetAssembler.setAdditionalContentSize( 0, 0, -sbW, -sbH );
    }
    
    public ScrollHandler( Widget widget, WidgetAssembler widgetAssembler, boolean scrollHorizontal, boolean scrollVertical )
    {
        if ( !scrollHorizontal && !scrollVertical )
            throw new IllegalArgumentException( "A ScrollHandler is useless without any scroll direction being handled." );
        
        this.widget = widget;
        this.widgetAssembler = widgetAssembler;
        
        widgetAssembler.setPickDispatched( true );
        
        float left = 0f;
        float top = 0f;
        float width = widget.getWidth();
        float height = widget.getHeight();
        
        final float defaultSBSize = 16f;
        
        if ( scrollHorizontal )
        {
            this.scrollbarH = createHorizontalScrollbar( width - ( scrollVertical ? defaultSBSize : 0f ) );
            scrollbarH.addScrollbarListener( this );
            widgetAssembler.addWidget( scrollbarH, left, top + height - defaultSBSize );
        }
        
        if ( scrollVertical )
        {
            this.scrollbarV = createVerticalScrollbar( height - ( scrollHorizontal ? defaultSBSize : 0f ) );
            scrollbarV.addScrollbarListener( this );
            widgetAssembler.addWidget( scrollbarV, left + width - defaultSBSize, top );
        }
        
        if ( scrollHorizontal && scrollVertical )
        {
            this.spacerImage = new Image( false, defaultSBSize, defaultSBSize, HUD.getTheme().getScrollPanelSpaceTexture() );
            widgetAssembler.addWidget( spacerImage, left + width - defaultSBSize, top + height - defaultSBSize );
        }
        
        widget.addSizeListener( this );
        widget.addContainerListener( this );
        widget.addMouseListener( this );
    }
}
