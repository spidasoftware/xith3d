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
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.SliderListener;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;

/**
 * A simple Slider implementation used to select a certain value from a range.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Slider extends Widget
{
    /**
     * This class is used to describe a (set of) Slider Widget(s). You can
     * pass it to the Slider constructor. Modifications on the used instance
     * after creating the Slider Widget won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends Widget.DescriptionBase
    {
        private Texture2D leftTexture;
        private Texture2D bodyTexture;
        private Texture2D valueMarkTexture;
        private Texture2D rightTexture;
        private int height;
        
        private Texture2D handleTexture;
        private int handleYOffset;
        
        private boolean smoothSliding;
        
        /**
         * Sets the height.
         * 
         * @param height the new height
         */
        public void setHeight( int height )
        {
            this.height = height;
        }
        
        /**
         * @return the height
         */
        public final int getHeight()
        {
            return ( height );
        }
        
        /**
         * Sets the texture to use for the left end.
         * 
         * @param texture the texture
         */
        public void setLeftTexture( Texture2D texture )
        {
            this.leftTexture = texture;
        }
        
        /**
         * Sets the texture to use for the left end.
         * 
         * @param texture the texture
         */
        public void setLeftTexture( String texture )
        {
            setLeftTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the texture to use for the left end.
         */
        public final Texture2D getLeftTexture()
        {
            return ( leftTexture );
        }
        
        /**
         * Sets the texture to use for the right end.
         * 
         * @param texture the texture
         */
        public void setRightTexture( Texture2D texture )
        {
            this.rightTexture = texture;
        }
        
        /**
         * Sets the texture to use for the right end.
         * 
         * @param texture the texture
         */
        public void setRightTexture( String texture )
        {
            setRightTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the texture to use for the left end.
         */
        public final Texture2D getRightTexture()
        {
            return ( rightTexture );
        }
        
        /**
         * Sets the texture to use for the body.
         * 
         * @param texture the texture
         */
        public void setBodyTexture( Texture2D texture )
        {
            this.bodyTexture = texture;
        }
        
        /**
         * Sets the texture to use for the body.
         * 
         * @param texture the texture
         */
        public void setBodyTexture( String texture )
        {
            setBodyTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the texture to use for the body.
         */
        public final Texture2D getBodyTexture()
        {
            return ( bodyTexture );
        }
        
        /**
         * Sets the texture to use for the value mark.
         * 
         * @param texture the texture
         */
        public void setValueMarkTexture( Texture2D texture )
        {
            this.valueMarkTexture = texture;
        }
        
        /**
         * Sets the texture to use for the value mark.
         * 
         * @param texture the texture
         */
        public void setValueMarkTexture( String texture )
        {
            setValueMarkTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the texture to use for the value mark.
         */
        public final Texture2D getValueMarkTexture()
        {
            return ( valueMarkTexture );
        }
        
        /**
         * Sets the texture to use for the handle.
         * 
         * @param texture the texture
         */
        public void setHandleTexture( Texture2D texture )
        {
            this.handleTexture = texture;
        }
        
        /**
         * Sets the texture to use for the handle.
         * 
         * @param texture the texture
         */
        public void setHandleTexture( String texture )
        {
            setHandleTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        /**
         * @return the texture to use for the handle.
         */
        public final Texture2D getHandleTexture()
        {
            return ( handleTexture );
        }
        
        /**
         * Sets the handle button's y-offset.
         * 
         * @param yOffset the y-offset of the handle button
         */
        public void setHandleButtonYOffset( int yOffset )
        {
            this.handleYOffset = yOffset;
        }
        
        /**
         * @return the handle button's y-offset
         */
        public final int getHandleButtonYOffset()
        {
            return ( handleYOffset );
        }
        
        /**
         * If true, the handle doesn't snap to discrete positiones depending on
         * the available scroll values.
         * 
         * @param b enable/disable
         */
        public void setSmoothSliding( boolean b )
        {
            this.smoothSliding = b;
        }
        
        /**
         * @return true, if the handle doesn't snap to discrete positiones depending on
         * the available scroll values.
         */
        public final boolean getSmoothSliding()
        {
            return ( smoothSliding );
        }
        
        /**
         * Sets all values of this Description to the values of the given
         * Description.
         * 
         * @param desc the original to be duplicated
         */
        public void set( Description desc )
        {
            this.height = desc.height;
            
            this.leftTexture = desc.leftTexture;
            this.bodyTexture = desc.bodyTexture;
            this.valueMarkTexture = desc.valueMarkTexture;
            this.rightTexture = desc.rightTexture;
            
            this.handleTexture = desc.handleTexture;
            this.handleYOffset = desc.handleYOffset;
            
            this.smoothSliding = desc.smoothSliding;
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
        private Description( Description desc )
        {
            this.set( desc );
        }
        
        /**
         * Creates a new Slider.Description.
         * 
         * @param height the height
         * @param leftTexture the texture to use for the left end
         * @param rightTexture the texture to use for the right end
         * @param bodyTexture the texture to use for the body (tiled)
         * @param valueMarkTexture the texture to use for the value mark (repeated)
         * @param handleTexture the handle texture to use
         * @param handleYOffset the y-offset for the handle button
         * @param smoothSliding use smooth sliding?
         */
        public Description( int height, Texture2D leftTexture, Texture2D rightTexture, Texture2D bodyTexture, Texture2D valueMarkTexture, Texture2D handleTexture, int handleYOffset, boolean smoothSliding )
        {
            this.height = height;
            
            this.leftTexture = leftTexture;
            this.bodyTexture = bodyTexture;
            this.rightTexture = rightTexture;
            this.valueMarkTexture = valueMarkTexture;
            
            this.handleTexture = handleTexture;
            this.handleYOffset = handleYOffset;
            
            this.smoothSliding = smoothSliding;
        }
        
        /**
         * Creates a new Slider.Description.
         * 
         * @param height the height
         * @param leftTexture the texture to use for the left end
         * @param rightTexture the texture to use for the right end
         * @param bodyTexture the texture to use for the body (tiled)
         * @param valueMarkTexture the texture to use for the value mark (repeated)
         * @param handleTexture the handle texture to use
         * @param handleYOffset the y-offset for the handle button
         * @param smoothSliding use smooth sliding?
         */
        public Description( int height, String leftTexture, String rightTexture, String bodyTexture, String valueMarkTexture, String handleTexture, int handleYOffset, boolean smoothSliding )
        {
            this( height, HUDTextureUtils.getTexture( leftTexture, true ), HUDTextureUtils.getTexture( rightTexture, true ), HUDTextureUtils.getTexture( bodyTexture, true ), HUDTextureUtils.getTexture( valueMarkTexture, true ), HUDTextureUtils.getTexture( handleTexture, true ), handleYOffset, smoothSliding );
        }
    }
    
    private int heightInPx;
    
    private Texture2D leftTex;
    private Texture2D rightTex;
    private Texture2D bodyTex;
    private Texture2D valueMarkTex;
    private Texture2D handleTex;
    private int handleYOffset;
    
    private int currentHandlePosPx = -1;
    private int forcedHandlePosPx = -1;
    
    private boolean isSliding = false;
    private float slideStartMousePos;
    private int slideStartHandlePos;
    
    private int minValue;
    private int maxValue;
    private int value;
    
    private boolean smoothSliding;
    
    private final ArrayList<SliderListener> sliderListeners = new ArrayList<SliderListener>();
    
    public void setHandleYOffset( int yOffset )
    {
        this.handleYOffset = yOffset;
        
        setTextureDirty();
    }
    
    public final int getHandleYOffset()
    {
        return ( handleYOffset );
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
        int halfHandleSize = HUDTextureUtils.getTextureWidth( handleTex ) / 2;
        
        if ( mouseX < currentHandlePosPx - halfHandleSize )
            return ( -1 );
        
        if ( mouseX > currentHandlePosPx + halfHandleSize )
            return ( +1 );
        
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
            Dim2i buffer = Dim2i.fromPool();
            getSizeHUD2Pixels_( x, y, buffer );
            int localX = buffer.getWidth();
            int localY = buffer.getHeight();
            Dim2i.toPool( buffer );
            
            if ( getMousePosition( currentHandlePosPx, localX, localY ) == 0 )
            {
                bindToGlobalMouseMovement();
                
                slideStartHandlePos = currentHandlePosPx;
                slideStartMousePos = x;
                
                this.isSliding = true;
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
        
        this.isSliding = false;
        
        if ( getSmoothSliding() )
        {
            setTextureDirty();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseMoved( float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseMoved( x, y, buttonsState, when, isTopMost, hasFocus );
        
        if ( isSliding && ( getMaxValue() > getMinValue() ) )
        {
            int oldForcedHandlePosPx = forcedHandlePosPx;
            int newValue;
            
            float dx = x - slideStartMousePos;
            
            int leftWidth = HUDTextureUtils.getTextureWidth( leftTex );
            int rightWidth = HUDTextureUtils.getTextureWidth( rightTex );
            Dim2i buffer = Dim2i.fromPool();
            getSizeHUD2Pixels_( this.getWidth(), 0, buffer );
            int widgetWidthPx = buffer.getWidth();
            getSizeHUD2Pixels_( slideStartHandlePos + dx, 0, buffer );
            int slideHandlePos = buffer.getWidth();
            Dim2i.toPool( buffer );
            
            int barRestWidth = widgetWidthPx - leftWidth - rightWidth;
            
            forcedHandlePosPx = Math.max( leftWidth, Math.min( slideHandlePos, barRestWidth + leftWidth ) );
            
            newValue = Math.round( ( ( forcedHandlePosPx - leftWidth ) * ( getMaxValue() - getMinValue() ) ) / (float)barRestWidth ) + getMinValue();
            
            if ( !getSmoothSliding() )
            {
                forcedHandlePosPx = leftWidth + barRestWidth * ( newValue - getMinValue() ) / ( getMaxValue() - getMinValue() );
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
     * Sets the lower bound of scroll values.
     * 
     * @param minValue
     * @param maxValue
     * @param value
     */
    public void setMinMaxAndValue( int minValue, int maxValue, int value )
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
        
        setTextureDirty();
    }
    
    /**
     * @param oldValue
     * @param newValue
     */
    protected void onSliderValueChanged( int oldValue, int newValue )
    {
        for ( int i = 0; i < sliderListeners.size(); i++ )
        {
            sliderListeners.get( i ).onSliderValueChanged( this, newValue );
        }
    }
    
    /**
     * Sets the current slide value
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
        
        onSliderValueChanged( oldValue, value );
        
        return ( true );
    }
    
    /**
     * @return the current slide value
     */
    public final int getValue()
    {
        return ( value );
    }
    
    /**
     * Adds a SliderListener to the List to be notified, when the value has
     * changed.
     * 
     * @param l the new SliderListener
     */
    public void addSliderListener( SliderListener l )
    {
        sliderListeners.add( l );
    }
    
    /**
     * Removes a SliderListener from the List.
     * 
     * @param l the SliderListener to be removed
     */
    public boolean removeSliderListener( SliderListener l )
    {
        return ( sliderListeners.remove( l ) );
    }
    
    /**
     * If true, the handle doesn't snap to discrete positiones depending on the
     * available slide values.
     * 
     * @param b enable/disable
     */
    public void setSmoothSliding( boolean b )
    {
        this.smoothSliding = b;
    }
    
    /**
     * @return true, if the handle doesn't snap to discrete positiones depending on the
     * available slide values.
     */
    public final boolean getSmoothSliding()
    {
        return ( smoothSliding );
    }
    
    protected int drawHandle( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, Texture2D handleTex, int forcedHandlePos )
    {
        int handlePos;
        
        int handleSize = HUDTextureUtils.getTextureWidth( handleTex );
        
        if ( forcedHandlePos >= 0 )
            handlePos = forcedHandlePos;
        else
            handlePos = width * ( getValue() - getMinValue() ) / ( getMaxValue() - getMinValue() );
        
        DrawUtils.drawImage( null, handleTex, null, texCanvas, offsetX + handlePos - ( handleSize / 2 ), offsetY, handleSize, height );
        
        return ( handlePos );
    }
    
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        if ( drawsSelf )
            texCanvas.getImage().clear( Colorf.BLACK_TRANSPARENT, offsetX, offsetY, width, height );
        
        int leftWidth = HUDTextureUtils.getTextureWidth( leftTex );
        int rightWidth = HUDTextureUtils.getTextureWidth( rightTex );
        int bodyWidth = width - leftWidth - rightWidth;
        
        DrawUtils.drawImage( null, leftTex, null, texCanvas, offsetX, offsetY, width, height );
        DrawUtils.drawImage( null, bodyTex, TileMode.TILE_X, texCanvas, offsetX + leftWidth, offsetY, bodyWidth, height );
        DrawUtils.drawImage( null, rightTex, null, texCanvas, offsetX + width - rightWidth, offsetY, rightWidth, height );
        
        if ( valueMarkTex != null )
        {
            int valueMarkWidth = HUDTextureUtils.getTextureWidth( valueMarkTex );
            int valueMarkHeight = HUDTextureUtils.getTextureHeight( valueMarkTex );
            
            int n = getMaxValue() - getMinValue();
            for ( int i = 0; i <= n; i++ )
            {
                int pos = bodyWidth * i / n;
                
                DrawUtils.drawImage( null, valueMarkTex, null, texCanvas, offsetX + leftWidth + pos - ( valueMarkWidth / 2 ) - 1, offsetY + height - valueMarkHeight, valueMarkWidth, valueMarkHeight );
            }
        }
        
        if ( getMaxValue() > getMinValue() )
        {
            this.currentHandlePosPx = leftWidth + drawHandle( texCanvas, offsetX + leftWidth, offsetY, width - leftWidth - rightWidth, height, handleTex, isSliding ? forcedHandlePosPx - leftWidth : -1 );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        if ( heightInPx > 0 )
        {
            final Dim2f buffer = Dim2f.fromPool();
            getSizePixels2HUD_( 0, heightInPx, buffer );
            
            setSize( this.getWidth(), buffer.getHeight(), true );
            
            Dim2f.toPool( buffer );
            
            setTextureDirty();
        }
    }
    
    /**
     * Creates a new Slider.
     * 
     * @param width the width of the Scrollbar
     * @param height the height of the Scrollbar
     * @param desc a Slider.Description instance holding information about
     *             this new Slider
     */
    public Slider( float width, float height, Description desc )
    {
        super( false, false, width, height );
        
        if ( height < 0f )
            this.heightInPx = desc.getHeight();
        else
            this.heightInPx = -1;
        
        this.leftTex = desc.getLeftTexture();
        this.rightTex = desc.getRightTexture();
        this.bodyTex = desc.getBodyTexture();
        this.valueMarkTex = desc.getValueMarkTexture();
        this.handleTex = desc.getHandleTexture();
        
        this.handleYOffset = desc.getHandleButtonYOffset();
        
        this.minValue = 0;
        this.maxValue = 100;
        this.value = 0;
        this.smoothSliding = desc.getSmoothSliding();
    }
    
    /**
     * Creates a new Slider.
     * 
     * @param width the width of the Slider
     * @param desc a Slider.Description instance holding information about
     *             this new Slider
     */
    public Slider( float width, Description desc )
    {
        this( width, -1f, desc );
    }
    
    /**
     * Creates a new Slider.
     * 
     * @param width the width of the Slider
     * @param height the height of the Slider
     */
    public Slider( float width, float height )
    {
        this( width, height, HUD.getTheme().getSliderDescription() );
    }
    
    /**
     * Creates a new Slider.
     * 
     * @param width the width of the slider
     */
    public Slider( float width )
    {
        this( width, HUD.getTheme().getSliderDescription() );
    }
}
