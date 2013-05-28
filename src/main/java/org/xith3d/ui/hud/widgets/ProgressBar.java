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

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.BackgroundSettableWidget;
import org.xith3d.ui.hud.base.Border;
import org.xith3d.ui.hud.borders.BorderFactory;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;

public class ProgressBar extends BackgroundSettableWidget
{
    protected static final boolean DEFAULT_HEAVYWEIGHT = false;
    
    /**
     * This class is used to describe a ProgressBar Widget.
     * You can pass it to the ProgressBar constructor.
     * Modifications on the used instance after creating the ProgressBar Widget
     * won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends BackgroundSettableWidget.BackgroundSettableDescriptionBase
    {
        private int leftWidth;
        private int rightWidth;
        private int topHeight;
        private int bottomHeight;
        
        private Texture2D barTexture;
        
        private Border.Description borderDesc;
        
        private Label.Description labelDesc;
        
        
        public void setBottomHeight( int bh )
        {
            this.bottomHeight = bh;
        }
        
        public final int getBottomHeight()
        {
            return ( bottomHeight );
        }
        
        public void setRightWidth( int rw )
        {
            this.rightWidth = rw;
        }
        
        public final int getRightWidth()
        {
            return ( rightWidth );
        }
        
        public void setTopHeight( int th )
        {
            this.topHeight = th;
        }
        
        public final int getTopHeight()
        {
            return ( topHeight );
        }
        
        public void setLeftWidth( int lw )
        {
            this.leftWidth = lw;
        }
        
        public final int getLeftWidth()
        {
            return ( leftWidth );
        }
        
        public void setBarTexture( Texture2D texture )
        {
            this.barTexture = texture;
        }
        
        public void setBarTexture( String texture )
        {
            setBarTexture( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        public final Texture2D getBarTexture()
        {
            return ( barTexture );
        }
        
        public void setBorderDescription( Border.Description borderDesc )
        {
            this.borderDesc = borderDesc;
        }
        
        public final Border.Description getBorderDescription()
        {
            return ( borderDesc );
        }
        
        public void setLabelDescription( Label.Description labelDesc )
        {
            this.labelDesc = labelDesc;
        }
        
        public final Label.Description getLabelDescription()
        {
            return ( labelDesc );
        }
        
        /**
         * Clone-Constructor
         * 
         * @param desc the original to be duplicated
         */
        public void set( Description desc )
        {
            super.setBgSDB( desc );
            
            this.leftWidth = desc.leftWidth;
            this.rightWidth = desc.rightWidth;
            this.topHeight = desc.topHeight;
            this.bottomHeight = desc.bottomHeight;
            
            this.barTexture = desc.barTexture;
            
            this.borderDesc = desc.borderDesc.clone();
            this.labelDesc = desc.labelDesc.clone();
        }
        
        /**
         * @return a clone of this ProgressBar.Description.
         */
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        /**
         * Clone-Constructor
         * 
         * @param desc the original to be duplicated
         */
        private Description( Description desc )
        {
            super( desc.getBackgroundColor(), desc.getBackgroundTexture() );
            
            this.set( desc );
        }
        
        public Description( int bottom, int right, int top, int left, Colorf backgroundColor, Texture2D backgroundTexture, Texture2D barTex, Border.Description borderDesc, Label.Description labelDesc )
        {
            super( backgroundColor, backgroundTexture );
            
            this.bottomHeight = Math.max( 0, bottom );
            this.rightWidth = Math.max( 0, right );
            this.topHeight = Math.max( 0, top );
            this.leftWidth = Math.max( 0, left );
            
            this.barTexture = barTex;
            
            this.borderDesc = borderDesc;
            if ( borderDesc == null )
            {
                this.borderDesc = HUD.getTheme().getProgressBarBorderDesc();
            }
            
            this.labelDesc = labelDesc;
            if ( labelDesc == null )
            {
                this.labelDesc = HUD.getTheme().getProgressbarLabelDescription();
            }
        }
        
        public Description( int bottom, int right, int top, int left, Colorf backgroundColor, String backgroundTexture, String barTex, Border.Description borderDesc, Label.Description labelDesc )
        {
            this( bottom, right, top, left, backgroundColor, HUDTextureUtils.getTexture( backgroundTexture, true ), HUDTextureUtils.getTexture( barTex, true ), borderDesc, labelDesc );
        }
    }
    
    private int leftWidth;
    private int rightWidth;
    private int topHeight;
    private int bottomHeight;
    
    private Texture2D barTex;
    
    private Label label;
    
    private int minValue;
    private int maxValue;
    private int value;
    
    /**
     * Sets the Font to use for the percent value in the center of the ProgressBar.
     * 
     * @param font the Font
     */
    public void setFont( HUDFont font )
    {
        if ( label != null )
        {
            label.setFont( font );
        }
    }
    
    /**
     * @return the Font to use for the percent value in the center of the ProgressBar.
     */
    public final HUDFont getFont()
    {
        if ( label == null )
            return ( null );
        
        return ( label.getFont() );
    }
    
    /**
     * Sets the Font color to use for the percent value in the center of the ProgressBar.
     * 
     * @param color the Color
     */
    public void setFontColor( Colorf color )
    {
        if ( label != null )
        {
            label.setFontColor( color );
        }
    }
    
    /**
     * @return the Font color to use for the percent value in the center of the ProgressBar.
     */
    public final Colorf getFontColor()
    {
        if ( label == null )
            return ( null );
        
        return ( label.getFontColor() );
    }
    
    /**
     * Sets the Font to use for the percent value in the center of the ProgressBar.
     * 
     * @param font the Font
     * @param color the font-color
     */
    public final void setFont( HUDFont font, Colorf color )
    {
        setFont( font );
        setFontColor( color );
    }
    
    /**
     * Sets the minimum value this ProgressBar can take.
     * Default: 0
     * 
     * @param minValue the new minimum value
     */
    public void setMinValue( int minValue )
    {
        if ( this.minValue == minValue )
            return;
        
        this.minValue = minValue;
        
        setValue( getValue(), true );
        
        //setTextureDirty();
    }
    
    /**
     * @return the minimum value this ProgressBar can take.
     * Default: 0
     */
    public final int getMinValue()
    {
        return ( minValue );
    }
    
    /**
     * Sets the maximum value this ProgressBar can take.
     * Default: 100
     * 
     * @param maxValue the new minimum value
     */
    public void setMaxValue( int maxValue )
    {
        if ( this.maxValue == maxValue )
            return;
        
        this.maxValue = maxValue;
        
        setValue( getValue(), true );
        
        //setTextureDirty();
    }
    
    /**
     * @return the maximum value this ProgressBar can take.
     * Default: 100
     */
    public final int getMaxValue()
    {
        return ( maxValue );
    }
    
    public final void setMinAndMaxValue( int minValue, int maxValue )
    {
        if ( ( this.minValue == minValue ) && ( this.maxValue == maxValue ) )
            return;
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        
        setValue( getValue(), true );
        
        //setTextureDirty();
    }
    
    public final void setMinMaxAndValue( int minValue, int maxValue, int value )
    {
        if ( ( this.minValue == minValue ) && ( this.maxValue == maxValue ) && ( this.value == value ) )
            return;
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        
        setValue( value, true );
        
        //setTextureDirty();
    }
    
    /**
     * Creates the ProgressBar's percentage text from the current value.
     * 
     * @return the current percentage text.
     */
    protected String makeText()
    {
        return ( String.valueOf( ( getValue() - getMinValue() ) * 100 / ( getMaxValue() - getMinValue() ) ) + "%" );
    }
    
    /**
     * Sets the current value of this ProgressBar.
     * Default: 0
     * 
     * @param value the new value
     * @param forced
     */
    protected void setValue( int value, boolean forced )
    {
        if ( ( value == this.value ) && !forced )
            return;
        
        this.value = Math.max( minValue, Math.min( value, maxValue ) );
        
        if ( label != null )
        {
            label.setText( makeText() );
        }
        
        setTextureDirty();
    }
    
    /**
     * Sets the current value of this ProgressBar.
     * Default: 0
     * 
     * @param value the new value
     */
    public final void setValue( int value )
    {
        setValue( value, false );
    }
    
    /**
     * @return the current value of this ProgressBar.
     * Default: 0
     */
    public final int getValue()
    {
        return ( value );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        /*
        if ( barLeftTex != null )
        {
            int leftWidth = HUDTextureUtils.getTextureWidth( barLeftTex );
            DrawUtils.drawImage( null, barLeftTex, null, texCanvas, offsetX, offsetY, leftWidth, height );
            
            offsetX += leftWidth;
            width -= leftWidth;
        }
        
        int rightWidth = 0;
        if ( barRightTex != null )
            rightWidth = HUDTextureUtils.getTextureWidth( barRightTex );
        
        int barSpace = width - rightWidth;
        int barWidth = barSpace * ( getValue() - getMinValue() ) / ( getMaxValue() - getMinValue() );
        
        DrawUtils.drawImage( null, barBodyTex, TileMode.TILE_BOTH, texCanvas, offsetX, offsetY, barWidth, height );
        
        if ( barRightTex != null )
        {
            DrawUtils.drawImage( null, barRightTex, null, texCanvas, offsetX + barWidth, offsetY, rightWidth, height );
        }
        */
        
        TextureImage2D btexImg = barTex.getImage0();
        int srcOrgW = HUDTextureUtils.getTextureWidth( barTex );
        int srcOrgH = HUDTextureUtils.getTextureHeight( barTex );
        
        int srcBottomH = bottomHeight;
        int srcRightW = rightWidth;
        int srcTopH = topHeight;
        int srcLeftW = leftWidth;
        
        int srcRightLeft = srcOrgW - srcRightW;
        int srcBottomTop = srcOrgH - srcBottomH;
        int trgRightLeft = width - srcRightW;
        int trgBottomTop = height - srcBottomH;
        
        int srcBodyWidth = srcOrgW - srcLeftW - srcRightW;
        int srcBodyHeight = srcOrgH - srcTopH - srcBottomH;
        int trgBodyWidth = ( width - srcLeftW - srcRightW ) * ( getValue() - getMinValue() ) / ( getMaxValue() - getMinValue() );
        int trgBodyHeight = height - srcTopH - srcBottomH;
        
        TextureImage2D ti = texCanvas.getImage();
        
        // render corners...
        if ( ( srcLeftW > 0 ) && ( srcBottomH > 0 ) )
            ti.drawImage( btexImg, 0, srcBottomTop, srcLeftW, srcBottomH, offsetX + 0, offsetY + trgBottomTop );
        if ( ( srcRightW > 0 ) && ( srcBottomH > 0 ) )
            ti.drawImage( btexImg, srcRightLeft, srcBottomTop, srcRightW, srcBottomH, offsetX + trgRightLeft, offsetY + trgBottomTop );
        if ( ( srcRightW > 0 ) && ( srcTopH > 0 ) )
            ti.drawImage( btexImg, srcRightLeft, 0, srcRightW, srcTopH, offsetX + trgRightLeft, offsetY );
        if ( ( srcLeftW > 0 ) && ( srcTopH > 0 ) )
            ti.drawImage( btexImg, 0, 0, srcLeftW, srcTopH, offsetX, offsetY );
        
        // render edges...
        if ( srcBottomH > 0 )
            ti.drawImage( btexImg, srcLeftW, srcBottomTop, srcOrgW - srcLeftW - srcRightW, srcBottomH, offsetX + srcLeftW, offsetY + trgBottomTop, trgBodyWidth, srcBottomH );
        if ( srcRightW > 0 )
            ti.drawImage( btexImg, srcRightLeft, srcTopH, srcRightW, srcOrgH - srcTopH - srcBottomH, offsetX + trgRightLeft, offsetY + srcTopH, srcRightW, trgBodyHeight );
        if ( srcTopH > 0 )
            ti.drawImage( btexImg, srcLeftW, 0, srcOrgW - srcLeftW - srcRightW, srcTopH, offsetX + srcLeftW, offsetY, trgBodyWidth, srcTopH );
        if ( srcLeftW > 0 )
            ti.drawImage( btexImg, 0, srcTopH, srcLeftW, srcOrgH - srcTopH - srcBottomH, offsetX, offsetY + srcTopH, srcLeftW, trgBodyHeight );
        
        // render body...
        //texCanvas.drawImage( btexImg.getBufferedImage(), offsetX + srcLeftW, offsetY + trgBottomTop, offsetX + trgRightLeft, offsetY + srcTopH, srcLeftW, srcTopH, srcBodyWidth, srcBodyHeight );
        
        int parts = trgBodyWidth / srcBodyWidth;
        for ( int i = 0; i < parts; i++ )
        {
            texCanvas.drawImage( btexImg.getBufferedImage(), offsetX + srcLeftW, offsetY + trgBottomTop, offsetX + srcLeftW + srcBodyWidth, offsetY + srcTopH, srcLeftW, srcTopH, srcBodyWidth, srcBodyHeight );
            offsetX += srcBodyWidth;
        }
        
        int lastPartWidth = trgBodyWidth % srcBodyWidth;
        if ( lastPartWidth > 0 )
        {
            texCanvas.drawImage( btexImg.getBufferedImage(), offsetX + srcLeftW, offsetY + trgBottomTop, offsetX + srcLeftW + lastPartWidth, offsetY + srcTopH, srcLeftW, srcTopH, lastPartWidth, srcBodyHeight );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
    }
    
    /**
     * Creates a new ProgressBar.
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param minValue the initial minimum value
     * @param maxValue the initial maximum value
     * @param desc the ProgressBar.Description to describe this ProgressBar
     */
    public ProgressBar( boolean isHeavyWeight, float width, float height, int minValue, int maxValue, Description desc )
    {
        super( isHeavyWeight, true, width, height, null, null, TileMode.STRETCH );
        
        if ( desc == null )
            desc = HUD.getTheme().getProgressBarDescription();
        
        this.leftWidth = desc.getLeftWidth();
        this.rightWidth = desc.getRightWidth();
        this.topHeight = desc.getTopHeight();
        this.bottomHeight = desc.getBottomHeight();
        
        this.barTex = desc.getBarTexture();
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = minValue;
        
        if ( desc.getLabelDescription() != null )
        {
            this.label = new Label( false, width, height, "0%", desc.getLabelDescription() );
            label.setBackground( null, (Texture2D)null, null );
            getWidgetAssembler().addWidget( label );
        }
        
        if ( desc.getBorderDescription() != null )
        {
            this.setBorder( BorderFactory.createBorder( desc.getBorderDescription() ) );
        }
        
        this.setFocussable( false );
    }
    
    /**
     * Creates a new ProgressBar.
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param minValue the initial minimum value
     * @param maxValue the initial maximum value
     */
    public ProgressBar( boolean isHeavyWeight, float width, float height, int minValue, int maxValue )
    {
        this( isHeavyWeight, width, height, minValue, maxValue, null );
    }
    
    /**
     * Creates a new ProgressBar.
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     */
    public ProgressBar( boolean isHeavyWeight, float width, float height )
    {
        this( isHeavyWeight, width, height, 0, 100, null );
    }
    
    /**
     * Creates a new ProgressBar.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param minValue the initial minimum value
     * @param maxValue the initial maximum value
     * @param desc the ProgressBar.Description to describe this ProgressBar
     */
    public ProgressBar( float width, float height, int minValue, int maxValue, Description desc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, minValue, maxValue, desc );
    }
    
    /**
     * Creates a new ProgressBar.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param minValue the initial minimum value
     * @param maxValue the initial maximum value
     */
    public ProgressBar( float width, float height, int minValue, int maxValue )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, minValue, maxValue, null );
    }
    
    /**
     * Creates a new ProgressBar.
     * 
     * @param width the desired width
     * @param height the desired height
     */
    public ProgressBar( float width, float height )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, 0, 100, null );
    }
}
