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

import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.AbstractButton;
import org.xith3d.ui.hud.base.TextWidget;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * A simple button Widget. You may specify an image for each state (normal,
 * hover, pressed). You may specify a text.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Button extends AbstractButton implements TextWidget
{
    protected static final boolean DEFAULT_HEAVYWEIGHT = false;
    
    /**
     * This class is used to describe a Button Widget. You can pass it to the
     * Button constructor. Modifications on the used instance after creating the
     * Button Widget won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends Widget.DescriptionBase
    {
        private int leftWidth;
        private int rightWidth;
        private int topHeight;
        private int bottomHeight;
        
        private Texture2D texNormal;
        private Texture2D texHovered;
        private Texture2D texPressed;
        
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
        
        public void setTextureNormal( Texture2D texture )
        {
            if ( texture == null )
                throw new IllegalArgumentException( "NORMAL texture must not be null." );
            
            this.texNormal = texture;
        }
        
        public final void setTextureNormal( String texture )
        {
            if ( texture == null )
                setTextureNormal( (Texture2D)null );
            else
                setTextureNormal( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        public final Texture2D getTextureNormal()
        {
            return ( texNormal );
        }
        
        public void setTextureHovered( Texture2D texture )
        {
            this.texHovered = texture;
        }
        
        public final void setTextureHovered( String texture )
        {
            if ( texture == null )
                setTextureHovered( (Texture2D)null );
            else
                setTextureHovered( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        public final Texture2D getTextureHovered()
        {
            return ( texHovered );
        }
        
        public void setTexturePressed( Texture2D texture )
        {
            this.texPressed = texture;
        }
        
        public final void setTexturePressed( String texture )
        {
            if ( texture == null )
                setTexturePressed( (Texture2D)null );
            else
                setTexturePressed( HUDTextureUtils.getTexture( texture, true ) );
        }
        
        public final Texture2D getTexturePressed()
        {
            return ( texPressed );
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
            this.leftWidth = desc.leftWidth;
            this.rightWidth = desc.rightWidth;
            this.topHeight = desc.topHeight;
            this.bottomHeight = desc.bottomHeight;
            
            this.texNormal = desc.texNormal;
            this.texHovered = desc.texHovered;
            this.texPressed = desc.texPressed;
            
            this.labelDesc = desc.labelDesc.clone();
        }
        
        /**
         * @return a clone of this instance.
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
            this.set( desc );
        }
        
        public Description( int bottom, int right, int top, int left, Texture2D texNormal, Texture2D texHovered, Texture2D texPressed )
        {
            //HUD.getTheme().getButtonDescription( this );
            
            this.bottomHeight = Math.max( 0, bottom );
            this.rightWidth = Math.max( 0, right );
            this.topHeight = Math.max( 0, top );
            this.leftWidth = Math.max( 0, left );
            
            setTextureNormal( texNormal );
            setTextureHovered( texHovered );
            setTexturePressed( texPressed );
            
            this.labelDesc = HUD.getTheme().getLabelDescription();
        }
        
        public Description( int bottom, int right, int top, int left, String texNormal, String texHovered, String texPressed )
        {
            this( bottom, right, top, left, HUDTextureUtils.getTextureOrNull( texNormal, true ), HUDTextureUtils.getTextureOrNull( texHovered, true ), HUDTextureUtils.getTextureOrNull( texPressed, true ) );
        }
        
        public Description()
        {
            HUD.getTheme().getButtonDescription( this );
        }
        
        public Description( Texture2D texNormal, Texture2D texHovered, Texture2D texPressed )
        {
            this();
            
            this.setTextureNormal( texNormal );
            this.setTextureHovered( texHovered );
            this.setTexturePressed( texPressed );
        }
        
        public Description( String texNormal, String texHovered, String texPressed )
        {
            this();
            
            this.setTextureNormal( texNormal );
            this.setTextureHovered( texHovered );
            this.setTexturePressed( texPressed );
        }
        
        public Description( int bottom, int right, int top, int left, Texture2D texNormal, Texture2D texHovered, Texture2D texPressed, HUDFont font, Colorf fontColor )
        {
            this( bottom, right, top, left, texNormal, texHovered, texPressed );
            
            this.labelDesc.setFont( font, false );
            this.labelDesc.setFont( ( font != null ) ? Label.Description.deriveDisabledFont( font ): font, true );
            this.labelDesc.setFontColor( fontColor, false );
            this.labelDesc.setFontColor( HUD.getTheme().getLabelDescription().getFontColor( true ), true );
            this.labelDesc.setAlignment( TextAlignment.CENTER_CENTER );
        }
        
        public Description( int bottom, int right, int top, int left, String texNormal, String texHovered, String texPressed, HUDFont font, Colorf fontColor )
        {
            this( bottom, right, top, left, HUDTextureUtils.getTextureOrNull( texNormal, true ), HUDTextureUtils.getTextureOrNull( texHovered, true ), HUDTextureUtils.getTextureOrNull( texPressed, true ), font, fontColor );
        }
    }
    
    private int leftWidth;
    private int rightWidth;
    private int topHeight;
    private int bottomHeight;
    
    private Texture2D texNormal;
    private Texture2D texHovered;
    private Texture2D texPressed;
    
    private final Label caption;
    
    /**
     * Sets the Texture to be used for NORMAL ButtonState.
     * 
     * @param texture the new Texture to use
     */
    public void setTextureNormal( Texture2D texture )
    {
        if ( texture == null )
            throw new IllegalArgumentException( "NORMAL texture must not be null." );
        
        this.texNormal = texture;
        
        if ( getButtonState() == ButtonState.NORMAL )
            setTextureDirty();
    }
    
    /**
     * Sets the Texture by resource to be used for NORMAL ButtonState.
     * 
     * @param resource the texture resource to use
     */
    public final void setTextureNormal( String resource )
    {
        setTextureNormal( HUDTextureUtils.getTexture( resource, true ) );
    }
    
    /**
     * @return the Texture used for NORMAL ButtonState.
     */
    public final Texture2D getTextureNormal()
    {
        return ( texNormal );
    }
    
    /**
     * Sets the Texture to be used for HOVERED ButtonState.
     * 
     * @param texture the new Texture to use
     */
    public void setTextureHovered( Texture2D texture )
    {
        this.texHovered = texture;
        
        if ( getButtonState() == ButtonState.HOVERED )
            setTextureDirty();
    }
    
    /**
     * Sets the Texture by resource to be used for HOVERED ButtonState.
     * 
     * @param resource the texture resource to use
     */
    public final void setTextureHovered( String resource )
    {
        setTextureHovered( HUDTextureUtils.getTexture( resource, true ) );
    }
    
    /**
     * @return the Texture used for HOVERED ButtonState.
     */
    public final Texture2D getTextureHovered()
    {
        return ( texHovered );
    }
    
    /**
     * Sets the Texture to be used for PRESSED ButtonState.
     * 
     * @param texture the new Texture to use
     */
    public void setTexturePressed( Texture2D texture )
    {
        this.texPressed = texture;
        
        if ( getButtonState() == ButtonState.PRESSED )
            setTextureDirty();
    }
    
    /**
     * Sets the Texture by resource to be used for PRESSED ButtonState.
     * 
     * @param resource the texture resource to use
     */
    public final void setTexturePressed( String resource )
    {
        setTexturePressed( HUDTextureUtils.getTexture( resource, true ) );
    }
    
    /**
     * @return the Texture used for PRESSED ButtonState.
     */
    public final Texture2D getTexturePressed()
    {
        return ( texPressed );
    }
    
    /**
     * Gets the texture for the current buttonstate.
     */
    protected Texture2D getCurrentTexture()
    {
        if ( isFocusResponsive() && ( getTextureHovered() != null ) && hasFocus( true ) )
            return ( getTextureHovered() );
        
        if ( ( getButtonState() == ButtonState.HOVERED ) && ( getTextureHovered() != null ) )
        {
            return ( getTextureHovered() );
        }
        
        if ( ( getButtonState() == ButtonState.PRESSED ) && ( getTexturePressed() != null ) )
        {
            return ( getTexturePressed() );
        }
        
        return ( getTextureNormal() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusGained()
    {
        super.onFocusGained();
        
        if ( isFocusResponsive() && hasFocus( true ) && ( getButtonState() != ButtonState.HOVERED ) )
            setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusLost()
    {
        super.onFocusLost();
        
        if ( isFocusResponsive() && hasFocus( true ) && ( getButtonState() != ButtonState.HOVERED ) )
            setTextureDirty();
    }
    
    /**
     * Updates the Button's images.
     */
    @Override
    public void update()
    {
        super.update();
        
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setButtonState( ButtonState buttonState )
    {
        boolean result = super.setButtonState( buttonState );
        
        if ( !result )
            return ( false );
        
        if ( caption != null )
        {
            if ( getButtonState() == ButtonState.PRESSED )
                caption.setTextOffset( 1, 1 );
            else
                caption.setTextOffset( 0, 0 );
        }
        
        setTextureDirty();
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        if ( caption != null )
            caption.setSize( newWidth, newHeight );
    }
    
    /**
     * Returns whether this Button has a text.
     * 
     * @return whether this Button has a text.
     */
    public final boolean hasText()
    {
        return ( caption != null );
    }
    
    /**
     * Sets this Button's text content.
     */
    public void setText( String text )
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        caption.setText( text );
    }
    
    /**
     * @return this Button's text content
     */
    public final String getText()
    {
        if ( caption == null )
            return ( null );
        
        return ( caption.getText() );
    }
    
    /**
     * Sets the Font of the Button's text.
     * 
     * @param font the new Font
     */
    public void setFont( HUDFont font )
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        caption.setFont( font );
    }
    
    /**
     * @return the Font of the Button's text
     */
    public final HUDFont getFont()
    {
        if ( caption == null )
            return ( null );
        
        return ( caption.getFont() );
    }
    
    /**
     * Sets the font-color of the Button's text.
     * 
     * @param color the new Font
     */
    public void setFontColor( Colorf color )
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        caption.setFontColor( color );
    }
    
    /**
     * @return the font-color of the Button's text
     */
    public final Colorf getFontColor()
    {
        if ( caption == null )
            return ( null );
        
        return ( caption.getFontColor() );
    }
    
    /**
     * Sets the text alignment of the Button's text.
     * 
     * @param align the new alignment
     */
    public void setAlignment( TextAlignment align )
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        caption.setAlignment( align );
    }
    
    /**
     * @return the text alignment of the Button's text
     */
    public final TextAlignment getAlignment()
    {
        if ( caption == null )
            return ( null );
        
        return ( caption.getAlignment() );
    }
    
    /**
     * Sets the icon for this Button.
     * 
     * @param icon
     */
    public void setIcon( Texture2D icon )
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        caption.setIcon( icon );
    }
    
    /**
     * Sets the icon for this Button.
     * 
     * @param icon
     */
    public void setIcon( String icon )
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        caption.setIcon( icon );
    }
    
    /**
     * Gets the icon of this Button.
     * 
     * @return the icon of this Button.
     */
    public final Texture2D getIcon()
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        return ( caption.getIcon() );
    }
    
    /**
     * Sets the gap between the icon and the text.
     * 
     * @param gap the gap in pixels
     */
    public void setIconGap( int gap )
    {
        if ( caption == null )
            throw new Error( "This Button doesn't have a text." );
        
        caption.setIconGap( gap );
    }
    
    /**
     * Gets the gap between the icon and the text.
     * 
     * @return gap the gap in pixels
     */
    public final int getIconGap()
    {
        if ( caption == null )
            return ( 0 );
        
        return ( caption.getIconGap() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEnabledImpl( boolean enabled )
    {
        if ( caption != null )
        {        
            caption.setEnabled( enabled );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        Texture2D btex = getCurrentTexture();
        TextureImage2D btexImg = btex.getImage0();
        int srcOrgW = HUDTextureUtils.getTextureWidth( btex );
        int srcOrgH = HUDTextureUtils.getTextureHeight( btex );
        
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
        int trgBodyWidth = width - srcLeftW - srcRightW;
        int trgBodyHeight = height - srcTopH - srcBottomH;
        
        TextureImage2D ti = texCanvas.getImage();
        
        if ( drawsSelf )
            ti.clear( Colorf.BLACK_TRANSPARENT, offsetX, offsetY, width, height );
        
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
        //ti.drawImage( btex, srcLeftW, srcTopH, srcBodyWidth, srcBodyHeight, offsetX + srcLeftW, offsetY + srcTopH, trgBodyWidth, trgBodyHeight );
        texCanvas.drawImage( btexImg.getBufferedImage(), offsetX + srcLeftW, offsetY + trgBottomTop, offsetX + trgRightLeft, offsetY + srcTopH, srcLeftW, srcTopH, srcBodyWidth, srcBodyHeight );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <Dim2f_ extends Dim2f> Dim2f_ getOptimalSize( Dim2f_ buffer )
    {
        return ( caption.getMinimalSize( buffer ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( getText() == null )
        {
            return ( getClass().getSimpleName() + "( texture: \"" + getTextureNormal().getName() + "\" )" );
        }
        
        return ( super.toString() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
    }
    
    protected Label createCaptionWidget( String text, Label.Description labelDesc )
    {
        Label label = new Label( false, getWidth(), getHeight(), text, labelDesc );
        label.setNoBackground();
        label.setPickable( false );
        label.setClickable( false );
        
        return ( new Label( false, getWidth(), getHeight(), text, labelDesc ) );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param desc Button.Description
     */
    public Button( boolean isHeavyWeight, float width, float height, String text, Description desc )
    {
        super( isHeavyWeight, true, width, height );
        
        this.bottomHeight = desc.getBottomHeight();
        this.rightWidth = desc.getRightWidth();
        this.topHeight = desc.getTopHeight();
        this.leftWidth = desc.getLeftWidth();
        
        this.texNormal = desc.getTextureNormal();
        this.texHovered = desc.getTextureHovered();
        this.texPressed = desc.getTexturePressed();
        
        if ( text != null )
        {
            this.caption = createCaptionWidget( text, desc.getLabelDescription() );
            getWidgetAssembler().addWidget( caption );
        }
        else
        {
            this.caption = null;
        }
    }
    
    /**
     * Creates a new Button.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     */
    public Button( boolean isHeavyWeight, float width, float height, String text )
    {
        this( isHeavyWeight, width, height, text, HUD.getTheme().getButtonDescription() );
    }
    
    protected static Button.Description deriveDesc( HUDFont font, Colorf color )
    {
        Button.Description desc = HUD.getTheme().getButtonDescription();
        
        if ( font != null )
            desc.getLabelDescription().setFont( font, false );
        if ( color != null )
            desc.getLabelDescription().setFontColor( color, false );
        
        return ( desc );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public Button( boolean isHeavyWeight, float width, float height, String text, HUDFont font, Colorf color )
    {
        this( isHeavyWeight, width, height, text, deriveDesc( font, color ) );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     */
    public Button( boolean isHeavyWeight, float width, float height, String text, HUDFont font )
    {
        this( isHeavyWeight, width, height, text, deriveDesc( font, null ) );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param color
     */
    public Button( boolean isHeavyWeight, float width, float height, String text, Colorf color )
    {
        this( isHeavyWeight, width, height, text, deriveDesc( null, color ) );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param desc Button.Description
     */
    public Button( float width, float height, String text, Description desc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, desc );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     */
    public Button( float width, float height, String text )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public Button( float width, float height, String text, HUDFont font, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, font, color );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     */
    public Button( float width, float height, String text, HUDFont font )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, font );
    }
    
    /**
     * Creates a new Button.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param color
     */
    public Button( float width, float height, String text, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, color );
    }
    
    protected static Button.Description createImageButtonDesc( Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture, HUDFont font, Colorf color )
    {
        Button.Description desc = new Button.Description(
            0, 0, 0, 0,
            normalTexture, hoveredTexture, pressedTexture,
            font, color
        );
        
        if ( font != null )
            desc.getLabelDescription().setFont( font, false );
        if ( color != null )
            desc.getLabelDescription().setFontColor( color, false );
        
        return ( desc );
    }
    
    protected static Button.Description createImageButtonDesc( String normalTexture, String hoveredTexture, String pressedTexture, HUDFont font, Colorf color )
    {
        Button.Description desc = new Button.Description(
            0, 0, 0, 0,
            normalTexture, hoveredTexture, pressedTexture,
            font, color
        );
        
        if ( font != null )
            desc.getLabelDescription().setFont( font, false );
        if ( color != null )
            desc.getLabelDescription().setFontColor( color, false );
        
        return ( desc );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public Button( boolean isHeavyWeight, float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( isHeavyWeight, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public Button( boolean isHeavyWeight, float width, float height, String normalTexture, String hoveredTexture, String pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( isHeavyWeight, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public Button( boolean isHeavyWeight, float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture )
    {
        this( isHeavyWeight, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public Button( boolean isHeavyWeight, float width, float height, String normalTexture, String hoveredTexture, String pressedTexture )
    {
        this( isHeavyWeight, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public Button( float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public Button( float width, float height, String normalTexture, String hoveredTexture, String pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public Button( float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
    
    /**
     * Creates a new Image Button (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public Button( float width, float height, String normalTexture, String hoveredTexture, String pressedTexture )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
}
