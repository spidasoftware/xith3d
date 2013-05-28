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

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.AbstractButton;
import org.xith3d.ui.hud.base.BackgroundSettableWidget;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.ScrollbarListener;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;

/**
 * A simple Scrollbar implementation used to scroll content on the HUD.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Scrollbar extends BackgroundSettableWidget
{
    /**
     * Scrolldirection of the Scrollbar
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public enum Direction
    {
        HORIZONTAL,
        VERTICAL,
        ;
    }
    
    /**
     * This class is used to describe a (set of) Scrollbar Widget(s). You can
     * pass it to the Scrollbar constructor. Modifications on the used instance
     * after creating the Scrollbar Widget won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends Widget.DescriptionBase
    {
        private Direction direction;
        
        private Texture2D backgroundTexture;
        
        private Texture2D decTexture;
        
        private Texture2D incTexture;
        
        private Texture2D handleLTTexture;
        private Texture2D handleRBTexture;
        private Texture2D handleBodyTexture;
        private Texture2D handleDecoTexture;
        
        private boolean smoothScrolling;
        
        /**
         * Sets the direction in which the Scrollbar scrolls.
         */
        public void setDirection( Direction direc )
        {
            this.direction = direc;
        }
        
        /**
         * @return the direction in which the Scrollbar scrolls.
         */
        public final Direction getDirection()
        {
            return ( direction );
        }
        
        /**
         * Sets the background texture to use.
         * 
         * @param texture the texture
         */
        public void setBackgroundTexture( Texture2D texture )
        {
            this.backgroundTexture = texture;
        }
        
        /**
         * Sets the background texture to use.
         * 
         * @param texture the texture
         */
        public final void setBackgroundTexture( String texture )
        {
            setBackgroundTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the background texture to use
         */
        public final Texture2D getBackgroundTexture()
        {
            return ( backgroundTexture );
        }
        
        /**
         * Sets the texture to use for the increment button.
         * 
         * @param texture the texture
         */
        public void setIncrementTexture( Texture2D texture )
        {
            if ( texture == null )
                throw new IllegalArgumentException( "texture must not be null" );
            
            this.incTexture = texture;
        }
        
        /**
         * Sets the texture to use for the increment button.
         * 
         * @param texture the texture
         */
        public final void setIncrementTexture( String texture )
        {
            setIncrementTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the texture to use for the increment button
         */
        public final Texture2D getIncrementTexture()
        {
            return ( incTexture );
        }
        
        /**
         * Sets the texture to use for the decrement button.
         * 
         * @param texture the texture
         */
        public void setDecrementTexture( Texture2D texture )
        {
            if ( texture == null )
                throw new IllegalArgumentException( "texture must not be null" );
            
            this.decTexture = texture;
        }
        
        /**
         * Sets the texture to use for the decrement button.
         * 
         * @param texture the texture
         */
        public final void setDecrementTexture( String texture )
        {
            setDecrementTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the texture to use for the decrement button
         */
        public final Texture2D getDecrementTexture()
        {
            return ( decTexture );
        }
        
        /**
         * Sets the textures to use for the handle.
         * 
         * @param handleLTTexture;
         * @param handleRBTexture;
         * @param handleBodyTexture;
         * @param handleDecoTexture;
         */
        public void setHandleTextures( Texture2D handleLTTexture, Texture2D handleRBTexture, Texture2D handleBodyTexture, Texture2D handleDecoTexture )
        {
            this.handleLTTexture = handleLTTexture;
            this.handleRBTexture = handleRBTexture;
            this.handleBodyTexture = handleBodyTexture;
            this.handleDecoTexture = handleDecoTexture;
        }
        
        /**
         * Sets the textures to use for the handle.
         * 
         * @param handleLTTexture;
         * @param handleRBTexture;
         * @param handleBodyTexture;
         * @param handleDecoTexture;
         */
        public final void setHandleTextures( String handleLTTexture, String handleRBTexture, String handleBodyTexture, String handleDecoTexture )
        {
            setHandleTextures( HUDTextureUtils.getTextureOrNull( handleLTTexture, true ),
                               HUDTextureUtils.getTextureOrNull( handleRBTexture, true ),
                               HUDTextureUtils.getTextureOrNull( handleBodyTexture, true ),
                               HUDTextureUtils.getTextureOrNull( handleDecoTexture, true )
                             );
        }
        
        /**
         * @return the texture to use for the left or top part of the handle.
         */
        public final Texture2D getHandleLeftTopTexture()
        {
            return ( handleLTTexture );
        }
        
        /**
         * @return the texture to use for the right or bottom part of the handle.
         */
        public final Texture2D getHandleRightBottomTexture()
        {
            return ( handleRBTexture );
        }
        
        /**
         * @return the texture to use for the body part of the handle.
         */
        public final Texture2D getHandleBodyTexture()
        {
            return ( handleBodyTexture );
        }
        
        /**
         * @return the texture to use for the handle decoration.
         */
        public final Texture2D getHandleDecoTexture()
        {
            return ( handleDecoTexture );
        }
        
        /**
         * If true, the handle doesn't snap to discrete positiones depending on
         * the available scroll values.
         * 
         * @param b enable/disable
         */
        public void setSmoothScrolling( boolean b )
        {
            this.smoothScrolling = b;
        }
        
        /**
         * @return true, if the handle doesn't snap to discrete positiones depending on
         * the available scroll values.
         */
        public final boolean getSmoothScrolling()
        {
            return ( smoothScrolling );
        }
        
        /**
         * Clone-Constructor.
         * 
         * @param desc the original to be duplicated
         */
        public void set( Description desc )
        {
            this.direction = desc.direction;
            
            this.backgroundTexture = desc.backgroundTexture;
            
            this.decTexture = desc.decTexture;
            
            this.incTexture = desc.incTexture;
            
            this.handleLTTexture = desc.handleLTTexture;
            this.handleRBTexture = desc.handleRBTexture;
            this.handleBodyTexture = desc.handleBodyTexture;
            this.handleDecoTexture = desc.handleDecoTexture;
            
            this.smoothScrolling = desc.smoothScrolling;
        }
        
        /**
         * @return a Clone of this Scrollbar.Description.
         */
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        /**
         * Clone-Constructor.
         * 
         * @param desc the original to be duplicated
         */
        public Description( Description desc )
        {
            this.set( desc );
        }
        
        /**
         * Creates a new Scrollbar.Description.
         * 
         * @param direction the direction in which the Scrollbar scrolls
         * @param backgroundTexture the background texture to use
         * @param handleLTTexture the handle texture to use
         * @param handleRBTexture the handle texture to use
         * @param handleBodyTexture the handle texture to use
         * @param handleDecoTexture the handle texture to use
         * @param decTexture the decrement button texture to use
         * @param incTexture the decrement button texture to use
         * @param smoothScrolling use smooth scrolling?
         */
        public Description( Direction direction, Texture2D backgroundTexture, Texture2D handleLTTexture, Texture2D handleRBTexture, Texture2D handleBodyTexture, Texture2D handleDecoTexture, Texture2D decTexture, Texture2D incTexture, boolean smoothScrolling )
        {
            this.direction = direction;
            
            this.backgroundTexture = backgroundTexture;
            
            this.decTexture = decTexture;
            this.incTexture = incTexture;
            
            this.handleLTTexture = handleLTTexture;
            this.handleRBTexture = handleRBTexture;
            this.handleBodyTexture = handleBodyTexture;
            this.handleDecoTexture = handleDecoTexture;
            
            this.smoothScrolling = smoothScrolling;
        }
        
        /**
         * Creates a new Scrollbar.Description.
         * 
         * @param direction the direction in which the Scrollbar scrolls
         * @param backgroundTexture the background texture to use
         * @param handleLTTexture the handle texture to use
         * @param handleRBTexture the handle texture to use
         * @param handleBodyTexture the handle texture to use
         * @param handleDecoTexture the handle texture to use
         * @param decTexture the decrement button texture to use
         * @param incTexture the decrement button texture to use
         * @param smoothScrolling use smooth scrolling?
         */
        public Description( Direction direction, String backgroundTexture, String handleLTTexture, String handleRBTexture, String handleBodyTexture, String handleDecoTexture, String decTexture, String incTexture, boolean smoothScrolling )
        {
            this( direction, HUDTextureUtils.getTexture( backgroundTexture, true ), HUDTextureUtils.getTexture( handleLTTexture, true ), HUDTextureUtils.getTexture( handleRBTexture, true ), HUDTextureUtils.getTexture( handleBodyTexture, true ), HUDTextureUtils.getTextureOrNull( handleDecoTexture, true ), HUDTextureUtils.getTexture( decTexture, true ), HUDTextureUtils.getTexture( incTexture, true ), smoothScrolling );
        }
    }
    
    private Direction direction;
    
    private final AbstractButton decButton;
    private final AbstractButton incButton;
    
    private Texture2D handleLTTex;
    private Texture2D handleRBTex;
    private Texture2D handleBodyTex;
    private Texture2D handleDecoTex;
    
    private int sizePX;
    
    private int currentHandlePosPx = -1;
    private int forcedHandlePosPx = -1;
    
    private boolean isScrolling = false;
    private float scrollStartMousePos;
    private int scrollStartHandlePos;
    
    private int minValue = 0;
    private int maxValue = 100;
    private int pageSize = 1;
    private int smallIncrement = 1;
    private int value = 0;
    
    private boolean smoothScrolling;
    private boolean needsRedrawAfterMouseButtonReleased = false;
    
    private final ArrayList<ScrollbarListener> scrollbarListeners = new ArrayList<ScrollbarListener>();
    
    /**
     * @return the direction in which the Scrollbar scrolls
     */
    public final Direction getDirection()
    {
        return ( direction );
    }
    
    private int getHandleSize()
    {
        Dim2i buffer = Dim2i.fromPool();
        getSizeHUD2Pixels_( decButton.getWidth(), decButton.getHeight(), buffer );
        int decBtnWidth = buffer.getWidth();
        int decBtnHeight = buffer.getHeight();
        getSizeHUD2Pixels_( incButton.getWidth(), incButton.getHeight(), buffer );
        int incBtnWidth = buffer.getWidth();
        int incBtnHeight = buffer.getHeight();
        Dim2i.toPool( buffer );
        
        int pageSize = getPageSize();//Math.max( 1, Math.min( getPageSize(), getMaxValue() - getMinValue() ) );
        float total = pageSize + getMaxValue() - getMinValue();
        
        int handleSize = 32;
        
        if ( getDirection() == Direction.HORIZONTAL )
        {
            float areaWidth = getWidthPX() - decBtnWidth - incBtnWidth;
            
            int handleLeftSize = HUDTextureUtils.getTextureWidth( handleLTTex );
            int handleRightSize = HUDTextureUtils.getTextureWidth( handleRBTex );
            
            handleSize = Math.max( handleLeftSize + handleRightSize, (int)( pageSize * areaWidth / total ) );
        }
        else
        {
            float areaHeight = getHeightPX() - decBtnHeight - incBtnHeight;
            
            int handleTopSize = HUDTextureUtils.getTextureHeight( handleLTTex );
            int handleBottomSize = HUDTextureUtils.getTextureHeight( handleRBTex );
            
            handleSize = Math.max( handleTopSize + handleBottomSize, (int)( pageSize * areaHeight / total ) );
        }
        
        return ( handleSize );
    }
    
    /**
     * Checks, if the mouse is over the handle.
     * 
     * @param currentHandlePosPx
     * @param mouseX widget local mouse-x
     * @param mouseY widget local mouse-y
     * 
     * @return -1, if the mouse is on the smaller side of the handle, +1, if it is on the greater side, 0 if it is over the handle.
     */
    protected int getMousePosition( int currentHandlePosPx, int mouseX, int mouseY )
    {
        if ( getDirection() == Direction.HORIZONTAL )
        {
            if ( mouseX < currentHandlePosPx )
                return ( -1 );
            
            if ( mouseX > currentHandlePosPx + getHandleSize() )
                return ( +1 );
        }
        else// if ( getDirection() == Direction.VERTICAL )
        {
            if ( mouseY < currentHandlePosPx )
                return ( -1 );
            
            if ( mouseY > currentHandlePosPx + getHandleSize() )
                return ( +1 );
        }
        
        return ( 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonPressed( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isTopMost && ( getMaxValue() > getMinValue() ) )
        {
            Widget picked = getWidgetAssembler().pick( x, y );
            
            if ( picked == decButton )
            {
                // decrement the value
                setValue( getValue() - getSmallIncrement() );
            }
            else if ( picked == incButton )
            {
                // increment the value
                setValue( getValue() + getSmallIncrement() );
            }
            else
            {
                Dim2i buffer = Dim2i.fromPool();
                getSizeHUD2Pixels_( x, y, buffer );
                int localX = buffer.getWidth();
                int localY = buffer.getHeight();
                Dim2i.toPool( buffer );
                
                if ( getMousePosition( currentHandlePosPx, localX, localY ) == 0 )
                {
                    bindToGlobalMouseMovement();
                    
                    scrollStartHandlePos = currentHandlePosPx;
                    
                    if ( getDirection() == Direction.HORIZONTAL )
                    {
                        scrollStartMousePos = x;
                    }
                    else if ( getDirection() == Direction.VERTICAL )
                    {
                        scrollStartMousePos = y;
                    }
                    
                    this.isScrolling = true;
                    
                    this.needsRedrawAfterMouseButtonReleased = true;
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonReleased( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonReleased( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        this.isScrolling = false;
        
        if ( getSmoothScrolling() )
        {
            if ( needsRedrawAfterMouseButtonReleased )
                setTextureDirty();
            
            this.needsRedrawAfterMouseButtonReleased = false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseMoved( float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseMoved( x, y, buttonsState, when, isTopMost, hasFocus );
        
        if ( isScrolling && ( getMaxValue() > getMinValue() ) )
        {
            int oldForcedHandlePosPx = forcedHandlePosPx;
            int newValue;
            
            if ( getDirection() == Direction.HORIZONTAL )
            {
                float dx = x - scrollStartMousePos;
                
                Dim2i buffer = Dim2i.fromPool();
                getSizeHUD2Pixels_( decButton.getWidth(), decButton.getHeight(), buffer );
                int decBtnWidth = buffer.getWidth();
                getSizeHUD2Pixels_( incButton.getWidth(), incButton.getHeight(), buffer );
                int incBtnWidth = buffer.getWidth();
                getSizeHUD2Pixels_( this.getWidth(), 0, buffer );
                int widgetWidthPx = buffer.getWidth();
                getSizeHUD2Pixels_( scrollStartHandlePos + dx, 0, buffer );
                int scrollHandlePos = buffer.getWidth();
                Dim2i.toPool( buffer );
                
                int handleSize = getHandleSize();
                int barRestWidth = widgetWidthPx - decBtnWidth - incBtnWidth - handleSize;
                
                forcedHandlePosPx = Math.max( decBtnWidth, Math.min( scrollHandlePos, barRestWidth + decBtnWidth ) );
                
                newValue = Math.round( ( ( forcedHandlePosPx - decBtnWidth ) * ( getMaxValue() - getMinValue() ) ) / (float)barRestWidth ) + getMinValue();
                
                if ( !getSmoothScrolling() )
                {
                    forcedHandlePosPx = decBtnWidth + barRestWidth * ( newValue - getMinValue() ) / ( getMaxValue() - getMinValue() );
                }
            }
            else// if ( getDirection() == Direction.VERTICAL )
            {
                float dy = y - scrollStartMousePos;
                
                Dim2i buffer = Dim2i.fromPool();
                getSizeHUD2Pixels_( decButton.getWidth(), decButton.getHeight(), buffer );
                int decBtnHeight = buffer.getHeight();
                getSizeHUD2Pixels_( incButton.getWidth(), incButton.getHeight(), buffer );
                int incBtnHeight = buffer.getHeight();
                getSizeHUD2Pixels_( 0, this.getHeight(), buffer );
                int widgetHeightPx = buffer.getHeight();
                getSizeHUD2Pixels_( scrollStartHandlePos + dy, 0, buffer );
                int scrollHandlePos = buffer.getWidth();
                Dim2i.toPool( buffer );
                
                int handleSize = getHandleSize();
                int barRestHeight = widgetHeightPx - decBtnHeight - incBtnHeight - handleSize;
                
                forcedHandlePosPx = Math.max( decBtnHeight, Math.min( scrollHandlePos, barRestHeight + decBtnHeight ) );
                
                newValue = Math.round( ( ( forcedHandlePosPx - decBtnHeight ) * ( getMaxValue() - getMinValue() ) ) / (float)barRestHeight ) + getMinValue();
                
                if ( !getSmoothScrolling() )
                {
                    forcedHandlePosPx = decBtnHeight + barRestHeight * ( newValue - getMinValue() ) / ( getMaxValue() - getMinValue() );
                }
            }
            
            if ( newValue != getValue() )
            {
                setValue( newValue );
            }
            else if ( forcedHandlePosPx != oldForcedHandlePosPx )
            {
                setTextureDirty();
            }
        }
    }
    
    /**
     * Sets the lower bound of scroll values.
     */
    public void setMinValue( int minValue )
    {
        if ( minValue > maxValue )
            throw new IllegalArgumentException( "minValue must never be greater than maxValue." );
        
        if ( this.minValue == minValue )
            return;
        
        this.minValue = minValue;
        
        setTextureDirty();
    }
    
    /**
     * @return the lower bound of scroll values
     */
    public final int getMinValue()
    {
        return ( minValue );
    }
    
    /**
     * Sets the upper bound of scroll values.
     */
    public void setMaxValue( int maxValue )
    {
        if ( maxValue < minValue )
            throw new IllegalArgumentException( "maxValue must never be less than minValue." );
        
        if ( this.maxValue == maxValue )
            return;
        
        this.maxValue = maxValue;
        
        setTextureDirty();
    }
    
    /**
     * @return the upper bound of scroll values
     */
    public final int getMaxValue()
    {
        return ( maxValue );
    }
    
    /**
     * Sets the lower bound of scroll values.
     * 
     * @param minValue
     * @param maxValue
     */
    public void setMinAndMax( int minValue, int maxValue )
    {
        if ( minValue > maxValue )
            throw new IllegalArgumentException( "minValue must be <= maxValue." );
        
        if ( ( minValue == this.minValue ) && ( maxValue == this.maxValue ) )
            return;
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        
        setTextureDirty();
    }
    
    /**
     * Sets the page size, which indirectly defines the size of the handle.
     * 
     * @param pageSize
     */
    public void setPageSize( int pageSize )
    {
        this.pageSize = pageSize;
    }
    
    /**
     * Gets the page size, which indirectly defines the size of the handle.
     * 
     * @return  the page size.
     */
    public final int getPageSize()
    {
        return ( pageSize );
    }
    
    /**
     * Sets the step to increment the Scrollbar's value by when the increment
     * button is clicked.
     */
    public void setSmallIncrement( int value )
    {
        this.smallIncrement = value;
    }
    
    /**
     * @return the step to increment the Scrollbar's value by when the increment
     *         button is clicked
     */
    public final int getSmallIncrement()
    {
        return ( smallIncrement );
    }
    
    /**
     * Sets the lower bound of scroll values.
     * 
     * @param minValue
     * @param maxValue
     * @param value
     * @param pageSize
     */
    public void setMinMaxAndValue( int minValue, int maxValue, int value, int pageSize )
    {
        if ( minValue > maxValue )
            throw new IllegalArgumentException( "minValue must be <= maxValue." );
        
        if ( ( value < minValue ) || ( value > maxValue ) )
            throw new IllegalArgumentException( "value must be >= minValue and <= maxValue." );
        
        if ( ( minValue == this.minValue ) && ( maxValue == this.maxValue ) )
            return;
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        
        this.pageSize = pageSize;//Math.max( 1, Math.min( pageSize, maxValue - minValue ) );
        
        setTextureDirty();
    }
    
    /**
     * Sets the lower bound of scroll values.
     * 
     * @param minValue
     * @param maxValue
     * @param value
     */
    public final void setMinMaxAndValue( int minValue, int maxValue, int value )
    {
        setMinMaxAndValue( minValue, maxValue, value, ( maxValue - minValue ) / 5 );
    }
    
    /**
     * @param oldValue
     * @param newValue
     */
    protected void onScrollbarValueChanged( int oldValue, int newValue )
    {
        //final Direction direction = this.getDirection();
        
        for ( int i = 0; i < scrollbarListeners.size(); i++ )
        {
            scrollbarListeners.get( i ).onScrollbarValueChanged( this, newValue );
        }
    }
    
    /**
     * Sets the current scroll value
     * 
     * @param value
     */
    public boolean setValue( int value )
    {
        value = Math.max( getMinValue(), Math.min( value, getMaxValue() ) );
        
        if ( value == this.value )
            return ( false );
        
        int oldValue = this.value;
        this.value = value;
        
        setTextureDirty();
        
        onScrollbarValueChanged( oldValue, value );
        
        return ( true );
    }
    
    /**
     * @return the current scroll value
     */
    public final int getValue()
    {
        return ( value );
    }
    
    /**
     * Adds a ScrollbarListener to the List to be notified, when the value has
     * changed.
     * 
     * @param l the new ScrollbarListener
     */
    public void addScrollbarListener( ScrollbarListener l )
    {
        scrollbarListeners.add( l );
    }
    
    /**
     * Removes a ScrollbarListener from the List.
     * 
     * @param l the ScrollbarListener to be removed
     */
    public boolean removeScrollbarListener( ScrollbarListener l )
    {
        return ( scrollbarListeners.remove( l ) );
    }
    
    /**
     * If true, the handle doesn't snap to discrete positiones depending on the
     * available scroll values.
     * 
     * @param b enable/disable
     */
    public void setSmoothScrolling( boolean b )
    {
        this.smoothScrolling = b;
    }
    
    /**
     * @return true, if the handle doesn't snap to discrete positiones depending on the
     * available scroll values.
     */
    public final boolean getSmoothScrolling()
    {
        return ( smoothScrolling );
    }
    
    /**
     * If this Scrollbar's Direction is Horizontal, the height in pixels is returned.
     * If this Scrollbar's Direction is Vertical, the width in pixels is returned.
     * 
     * @return the width or height in pixels.
     */
    public final int getWidthOrHeightInPixels()
    {
        return ( sizePX );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        if ( getDirection() == Direction.HORIZONTAL )
        {
            if ( decButton != null )
            {
                decButton.setSize( decButton.getWidth(), newHeight );
                if ( getWidgetAssembler().contains( decButton ) )
                    getWidgetAssembler().reposition( decButton, 0f, 0f );
            }
            
            if ( incButton != null )
            {
                incButton.setSize( incButton.getWidth(), newHeight );
                if ( getWidgetAssembler().contains( incButton ) )
                    getWidgetAssembler().reposition( incButton, newWidth - incButton.getWidth(), 0f );
            }
        }
        else //if (getDirection() == Direction.VERTICAL)
        {
            if ( decButton != null )
            {
                decButton.setSize( newWidth, decButton.getHeight() );
                if ( getWidgetAssembler().contains( decButton ) )
                    getWidgetAssembler().reposition( decButton, 0f, 0f );
            }
            
            if ( incButton != null )
            {
                incButton.setSize( newWidth, incButton.getHeight() );
                if ( getWidgetAssembler().contains( incButton ) )
                    getWidgetAssembler().reposition( incButton, 0f, newHeight - incButton.getHeight() );
            }
        }
        
        // unnecessary! already done in setSize().
        //setTextureDirty();
    }
    
    protected int drawHandle( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, Texture2D handleLTTex, Texture2D handleRBTex, Texture2D handleBodyTex, Texture2D handleDecoTex, int forcedHandlePos )
    {
        int handlePos;
        
        int handleDecoratorWidth = 0;
        int handleDecoratorHeight = 0;
        if ( handleDecoTex != null )
        {
            handleDecoratorWidth = HUDTextureUtils.getTextureWidth( handleDecoTex );
            handleDecoratorHeight = HUDTextureUtils.getTextureWidth( handleDecoTex );
        }
        
        if ( getDirection() == Direction.HORIZONTAL )
        {
            int handleLeftSize = HUDTextureUtils.getTextureWidth( handleLTTex );
            int handleRightSize = HUDTextureUtils.getTextureWidth( handleRBTex );
            int handleSize = getHandleSize();
            
            if ( forcedHandlePos >= 0 )
                handlePos = forcedHandlePos;
            else
                handlePos = ( width - handleSize ) * ( getValue() - getMinValue() ) / ( getMaxValue() - getMinValue() );
            
            DrawUtils.drawImage( null, handleLTTex, null, texCanvas, offsetX + handlePos, offsetY, handleLeftSize, height );
            DrawUtils.drawImage( null, handleRBTex, null, texCanvas, offsetX + handlePos + handleSize - handleRightSize, offsetY, handleRightSize, height );
            DrawUtils.drawImage( null, handleBodyTex, TileMode.TILE_X, texCanvas, offsetX + handlePos + handleLeftSize, offsetY, handleSize - handleLeftSize - handleRightSize, height );
            
            if ( ( handleDecoTex != null ) && ( handleSize - handleLeftSize - handleRightSize >= handleDecoratorWidth ) )
                DrawUtils.drawImage( null, handleDecoTex, null, texCanvas, offsetX + handlePos + ( ( handleSize - handleDecoratorWidth ) / 2 ), offsetY + ( ( height - handleDecoratorHeight ) / 2 ), handleDecoratorWidth, handleDecoratorHeight );
        }
        else
        {
            int handleTopSize = HUDTextureUtils.getTextureHeight( handleLTTex );
            int handleBottomSize = HUDTextureUtils.getTextureHeight( handleRBTex );
            int handleSize = getHandleSize();
            
            if ( forcedHandlePos >= 0 )
                handlePos = forcedHandlePos;
            else
                handlePos = ( height - handleSize ) * ( getValue() - getMinValue() ) / ( getMaxValue() - getMinValue() );
            
            DrawUtils.drawImage( null, handleLTTex, null, texCanvas, offsetX, offsetY + handlePos, width, handleTopSize );
            DrawUtils.drawImage( null, handleRBTex, null, texCanvas, offsetX, offsetY + handlePos + handleSize - handleBottomSize, width, handleBottomSize );
            DrawUtils.drawImage( null, handleBodyTex, TileMode.TILE_Y, texCanvas, offsetX, offsetY + handlePos + handleTopSize, width, handleSize - handleTopSize - handleBottomSize );
            
            if ( ( handleDecoTex != null ) && ( handleSize - handleTopSize - handleBottomSize >= handleDecoratorHeight ) )
                DrawUtils.drawImage( null, handleDecoTex, null, texCanvas, offsetX + ( ( width - handleDecoratorWidth ) / 2 ), offsetY + handlePos + ( ( handleSize - handleDecoratorHeight ) / 2 ), handleDecoratorWidth, handleDecoratorHeight );
        }
        
        return ( handlePos );
    }
    
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        if ( getMaxValue() > getMinValue() )
        {
            Dim2i buffer = Dim2i.fromPool();
            getSizeHUD2Pixels_( decButton.getWidth(), decButton.getHeight(), buffer );
            int decBtnWidth = buffer.getWidth();
            int decBtnHeight = buffer.getHeight();
            getSizeHUD2Pixels_( incButton.getWidth(), incButton.getHeight(), buffer );
            int incBtnWidth = buffer.getWidth();
            int incBtnHeight = buffer.getHeight();
            Dim2i.toPool( buffer );
            
            if ( getDirection() == Direction.HORIZONTAL )
            {
                this.currentHandlePosPx = decBtnWidth + drawHandle( texCanvas, offsetX + decBtnWidth, offsetY, width - decBtnWidth - incBtnWidth, height, handleLTTex, handleRBTex, handleBodyTex, handleDecoTex, isScrolling ? forcedHandlePosPx - decBtnWidth : -1 );
            }
            else
            {
                this.currentHandlePosPx = decBtnHeight + drawHandle( texCanvas, offsetX, offsetY + decBtnHeight, width, height - decBtnHeight - incBtnHeight, handleLTTex, handleRBTex, handleBodyTex, handleDecoTex, isScrolling ? forcedHandlePosPx - decBtnHeight : -1 );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        final Dim2f buffer = Dim2f.fromPool();
        
        getSizePixels2HUD_( sizePX, sizePX, buffer );
        
        if ( this.getWidth() < 0f )
        {
            // vertical
            
            super.setSize( buffer.getWidth(), this.getHeight() );
        }
        else if ( this.getHeight() < 0f )
        {
            // horizontal
            
            setSize( this.getWidth(), buffer.getHeight() );
        }
        
        if ( getDirection() == Direction.HORIZONTAL )
        {
            getSizePixels2HUD_( HUDTextureUtils.getTextureWidth( ( (Button)decButton ).getTextureNormal() ), 0, buffer );
            decButton.setSize( buffer.getWidth(), this.getHeight() );
            getWidgetAssembler().addWidget( decButton, 0f, 0f );
            
            getSizePixels2HUD_( HUDTextureUtils.getTextureWidth( ( (Button)incButton ).getTextureNormal() ), 0, buffer );
            incButton.setSize( buffer.getWidth(), this.getHeight() );
            getWidgetAssembler().addWidget( incButton, getWidth() - incButton.getWidth(), 0f );
        }
        else if ( getDirection() == Direction.VERTICAL )
        {
            getSizePixels2HUD_( 0, HUDTextureUtils.getTextureHeight( ( (Button)decButton ).getTextureNormal() ), buffer );
            decButton.setSize( this.getWidth(), buffer.getHeight() );
            getWidgetAssembler().addWidget( decButton, 0f, 0f );
            
            getSizePixels2HUD_( 0, HUDTextureUtils.getTextureHeight( ( (Button)incButton ).getTextureNormal() ), buffer );
            incButton.setSize( this.getWidth(), buffer.getHeight() );
            getWidgetAssembler().addWidget( incButton, 0f, getHeight() - incButton.getHeight() );
        }
        
        Dim2f.toPool( buffer );
        
        setSize( this.getWidth(), this.getHeight(), true );
        
        setTextureDirty();
    }
    
    /**
     * Creates a new Scrollbar.
     * 
     * @param width the width of the Scrollbar
     * @param height the height of the Scrollbar
     * @param sbDesc a Scrollbar.Description instance holding information about
     *            this new Scrollbar
     */
    public Scrollbar( float width, float height, Description sbDesc )
    {
        super( false, true, width, height, null, sbDesc.getBackgroundTexture(), TileMode.TILE_BOTH );
        
        this.direction = sbDesc.getDirection();
        
        this.decButton = new Button( false, HUDTextureUtils.getTextureWidth( sbDesc.decTexture ), HUDTextureUtils.getTextureHeight( sbDesc.decTexture ), sbDesc.decTexture, null, null );
        this.incButton = new Button( false, HUDTextureUtils.getTextureWidth( sbDesc.incTexture ), HUDTextureUtils.getTextureHeight( sbDesc.incTexture ), sbDesc.incTexture, null, null );
        
        this.handleLTTex = sbDesc.getHandleLeftTopTexture();
        this.handleRBTex = sbDesc.getHandleRightBottomTexture();
        this.handleBodyTex = sbDesc.getHandleBodyTexture();
        this.handleDecoTex = sbDesc.getHandleDecoTexture();
        
        if ( getDirection() == Direction.HORIZONTAL )
        {
            if ( getBackgroundTexture() != null )
                this.sizePX = HUDTextureUtils.getTextureHeight( getBackgroundTexture() );
            else if ( sbDesc.getDecrementTexture() != null )
                this.sizePX = HUDTextureUtils.getTextureHeight( sbDesc.getDecrementTexture() );
            else if ( sbDesc.getIncrementTexture() != null )
                this.sizePX = HUDTextureUtils.getTextureHeight( sbDesc.getIncrementTexture() );
            else
                this.sizePX = 16; // fallback!
        }
        else if ( getDirection() == Direction.VERTICAL )
        {
            if ( getBackgroundTexture() != null )
                this.sizePX = HUDTextureUtils.getTextureWidth( getBackgroundTexture() );
            else if ( sbDesc.getDecrementTexture() != null )
                this.sizePX = HUDTextureUtils.getTextureWidth( sbDesc.getDecrementTexture() );
            else if ( sbDesc.getIncrementTexture() != null )
                this.sizePX = HUDTextureUtils.getTextureWidth( sbDesc.getIncrementTexture() );
            else
                this.sizePX = 16; // fallback!
        }
        
        this.smoothScrolling = sbDesc.getSmoothScrolling();
    }
    
    /**
     * Creates a new Scrollbar.
     * 
     * @param length the length of the Scrollbar
     * @param zIndex the z-index of the Scrollbar
     * @param sbDesc a Scrollbar.Description instance holding information about
     *               this new Scrollbar
     */
    public Scrollbar( float length, Description sbDesc )
    {
        this( ( sbDesc.direction == Direction.HORIZONTAL ) ? length : -1f, ( sbDesc.direction == Direction.HORIZONTAL ) ? -1f : length, sbDesc );
    }
    
    private static Description getThemedDesc( Direction direction )
    {
        if ( direction == Direction.HORIZONTAL )
            return ( HUD.getTheme().getScrollbarDescriptionHorizontal() );
        
        return ( HUD.getTheme().getScrollbarDescriptionVertical() );
    }
    
    /**
     * Creates a new Scrollbar.
     * 
     * @param width the width of the Scrollbar
     * @param height the height of the Scrollbar
     * @param direction the scroll-direction
     */
    public Scrollbar( float width, float height, Direction direction )
    {
        this( width, height, getThemedDesc( direction ) );
    }
    
    /**
     * Creates a new Scrollbar.
     * 
     * @param length if (direction == HORIZONTAL), this is the width of the
     *            Scrollbar. if (direction == VERTICAL), this is the height of
     *            the Scrollbar.
     * @param direction the scroll-direction
     */
    public Scrollbar( float length, Direction direction )
    {
        this( length, getThemedDesc( direction ) );
    }
}
