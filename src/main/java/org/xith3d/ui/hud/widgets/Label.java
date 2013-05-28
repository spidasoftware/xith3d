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

import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.util.image.ImageUtility;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Rect2i;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.AutoSizable;
import org.xith3d.ui.hud.base.BackgroundSettableWidget;
import org.xith3d.ui.hud.base.PaddingSettable;
import org.xith3d.ui.hud.base.TextWidget;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.MultilineText;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * A Label displays text content. The new implementation is very highly optimized
 * for both static and dynamic text.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Label extends BackgroundSettableWidget implements TextWidget, PaddingSettable, Enableable, AutoSizable
{
    /**
     * This class is used to describe a (set of) Label Widget(s).
     * You can pass it to the Label constructor.
     * Modifications on the used instance after creating the Label Widget
     * won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends Widget.DescriptionBase
    {
        private Colorf backgroundColor;
        private Texture2D backgroundTexture;
        private TextAlignment alignment;
        private HUDFont font;
        private HUDFont font_disabled;
        private Colorf fontColor;
        private Colorf fontColor_disabled;
        
        /**
         * @return the background texture of this label
         */
        public final Texture2D getBackgroundTexture()
        {
            return ( backgroundTexture );
        }
        
        /**
         * Sets the background texture of this label
         * 
         * @param texture the texture to use
         */
        public void setBackgroundTexture( Texture2D texture )
        {
            backgroundTexture = texture;
        }
        
        /**
         * Sets the background color of this label
         * 
         * @param color the color to use
         */
        public void setBackgroundColor( Colorf color )
        {
            backgroundColor = color;
        }
        
        /**
         * @return the background color of this label
         */
        public Colorf getBackgroundColor()
        {
            return ( backgroundColor );
        }
        
        /**
         * Sets the background texture of this label
         * 
         * @param texture the texture resource to use
         */
        public void setBackgroundTexture( String texture )
        {
            if ( texture != null )
                setBackgroundTexture( HUDTextureUtils.getTexture( texture, true ) );
            else
                setBackgroundTexture( (Texture2D)null );
        }
        
        /**
         * Sets the horizontal and vertical alignment of the text
         */
        public void setAlignment( TextAlignment alignment )
        {
            this.alignment = alignment;
        }
        
        /**
         * @return the horizontal and vertical alignment of the text
         */
        public TextAlignment getAlignment()
        {
            return ( alignment );
        }
        
        /**
         * Sets the new Font to be used
         * 
         * @param font the new Font
         * @param disabled
         */
        public void setFont( HUDFont font, boolean disabled )
        {
            if ( disabled )
                this.font_disabled = font;
            else
                this.font = font;
        }
        
        /**
         * @return the used Font
         */
        public HUDFont getFont( boolean disabled )
        {
            if ( disabled )
                return ( font_disabled );
            
            return ( font );
        }
        
        /**
         * Sets the new color to be used
         * 
         * @param color the new color
         * @param disabled
         */
        public void setFontColor( Colorf color, boolean disabled )
        {
            if ( disabled )
                this.fontColor_disabled = color;
            else
                this.fontColor = color;
        }
        
        /**
         * @return the used color
         */
        public Colorf getFontColor( boolean disabled )
        {
            if ( disabled )
            {
                if ( fontColor_disabled == null )
                    this.fontColor_disabled = HUD.getTheme().getLabelDescription().getFontColor( true );
                
                return ( fontColor_disabled );
            }
            
            return ( fontColor );
        }
        
        public static HUDFont deriveDisabledFont( HUDFont enabledFont )
        {
            return ( HUDFont.getFont( enabledFont.getName(), enabledFont.getStyle().makeItalic(), enabledFont.getSize() ) );
        }
        
        /**
         * Clon-Constructor
         * 
         * @param desc the original to clone
         */
        public void set( Description desc )
        {
            this.backgroundTexture = desc.backgroundTexture;
            this.backgroundColor = desc.backgroundColor;
            this.alignment = desc.alignment;
            this.font = desc.font;
            this.font_disabled = desc.font_disabled;
            this.fontColor = desc.fontColor;
            this.fontColor_disabled = desc.fontColor_disabled;
        }
        
        /**
         * @return a clone of this instance
         */
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        /**
         * Clon-Constructor
         * 
         * @param desc the original to clone
         */
        protected Description( Description desc )
        {
            this.set( desc );
        }
        
        /**
         * Creates a new Label.Description.
         * 
         * @param backgroundColor
         * @param backgroundTexture the background texture
         * @param font_enabled the Font to be used for the text
         * @param font_disabled the Font to be used for the text
         * @param color_enabled the color to be used
         * @param color_disabled the color to be used
         * @param alignment the horizontal and vertical alignment
         */
        public Description( Colorf backgroundColor, Texture2D backgroundTexture, HUDFont font_enabled, HUDFont font_disabled, Colorf color_enabled, Colorf color_disabled, TextAlignment alignment )
        {
            this.backgroundColor = backgroundColor;
            this.backgroundTexture = backgroundTexture;
            
            if ( font_enabled == null )
                this.font = HUD.getTheme().getFont( false );
            else
                this.font = font_enabled;
            
            if ( font_disabled == null )
                this.font_disabled = deriveDisabledFont( this.font );
            else
                this.font_disabled = font_disabled;
            
            if ( color_enabled == null )
                this.fontColor = HUD.getTheme().getFontColor( false );
            else
                this.fontColor = color_enabled;
            
            this.fontColor_disabled = color_disabled;
            
            if ( alignment == null )
                this.alignment = TextAlignment.TOP_LEFT;
            else
                this.alignment = alignment;
        }
        
        /**
         * Creates a new Label.Description.
         * 
         * @param backgroundColor
         * @param backgroundTexture the background texture
         * @param font_enabled the Font to be used for the text
         * @param font_disabled the Font to be used for the text
         * @param color_enabled the color to be used
         * @param color_disabled the color to be used
         * @param alignment the horizontal and vertical alignment
         */
        public Description( Colorf backgroundColor, String backgroundTexture, HUDFont font_enabled, HUDFont font_disabled, Colorf color_enabled, Colorf color_disabled, TextAlignment alignment )
        {
            this( backgroundColor, HUDTextureUtils.getTextureOrNull( backgroundTexture, true ), font_enabled, font_disabled, color_enabled, color_disabled, alignment );
        }
    }
    
    protected static final boolean DEFAULT_HEAVYWEIGHT = false;
    
    private boolean autoSize = false;
    
    private int paddingBottom = 0;
    private int paddingRight = 0;
    private int paddingTop = 0;
    private int paddingLeft = 0;
    private int textOffsetX = 0;
    private int textOffsetY = 0;
    
    private TextAlignment alignment;
    
    private String text;
    private final MultilineText multiLineText = new MultilineText();
    
    private HUDFont font;
    private HUDFont font_disabled;
    private Colorf fontColor;
    private Colorf fontColor_disabled;
    
    private Texture2D icon = null;
    private int iconGap = 2;
    
    private boolean enabled = true;
    
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
        
        this.multiLineText.setPositionDirty();
        
        updateSizeFactors();
        setTextureDirty();
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean setPadding( int padding )
    {
        return ( setPadding( padding, padding, padding, padding ) );
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
     * Enables or disables auto-sizing.<br>
     * If enabled, the Label's size will always be the minimal size to wrap
     * the whole text content.
     * 
     * @param enabled
     */
    public void setAutoSizeEnabled( boolean enabled )
    {
        this.autoSize = enabled;
    }
    
    /**
     * @return if auto-sizing is enabled.<br>
     * If enabled, the Label's size will always be the minimal size to wrap
     * the whole text content.
     */
    public final boolean isAutoSizeEnabled()
    {
        return ( autoSize );
    }
    
    protected void updateText()
    {
        if ( autoSize && isInitialized() && ( getHUD() != null ) )
        {
            setMinimalSize();
        }
        
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setText( String text )
    {
        if ( text == null )
            throw new NullPointerException( "text must not be null" );
        
        if ( ( this.text == null ) || !this.text.equals( text ) )
        {
            this.text = text;
            
            updateText();
            
            this.multiLineText.setText( getDisplayedText() );
        }
    }
    
    /**
     * Sets the text from a float.
     * 
     * @param prefix null for no prefix
     * @param value the value
     * @param decimalSep '\0' for no decimal places
     * @param decPlaces
     * @param postfix null for no postfix
     */
    public void setText( String prefix, float value, char decimalSep, int decPlaces, String postfix )
    {
        String text;
        if ( ( decimalSep == '\0' ) || ( decPlaces <= 0 ) )
        {
            text = String.valueOf( (int)value );
        }
        else if ( decimalSep == '.' )
        {
            float p = (float)Math.pow( 10, decPlaces );
            
            text = String.valueOf( (int)( value * p ) / p );
        }
        else
        {
            float p = (float)Math.pow( 10, decPlaces );
            
            text = String.valueOf( (int)value ) + decimalSep + String.valueOf( (int)( ( value - (int)value ) * p ) );
        }
        
        if ( ( prefix == null ) || ( prefix.length() == 0 ) )
        {
            if ( ( postfix == null ) || ( postfix.length() == 0 ) )
                setText( text );
            else
                setText( text + postfix );
        }
        else if ( ( postfix == null ) || ( postfix.length() == 0 ) )
        {
            setText( prefix + text );
        }
        else
        {
            setText( prefix + text + postfix );
        }
    }
    
    /**
     * Sets the text from a float.<br>
     * The value will be formatted with a dot ('.') as decimal separator and 2 decimal places.
     * 
     * @param value the value
     */
    public final void setText( float value )
    {
        setText( null, value, '.', 2, null );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return ( text );
    }
    
    /**
     * @return the text, that is actually displayed on the Label.
     */
    protected String getDisplayedText()
    {
        return ( getText() );
    }
    
    /**
     * Sets the horizontal and vertical alignment of the text
     */
    public void setAlignment( TextAlignment alignment )
    {
        if ( this.alignment != alignment )
        {
            this.alignment = alignment;
            
            this.multiLineText.setPositionDirty();
            setTextureDirty();
        }
    }
    
    /**
     * @return the horizontal and vertical alignment of the text
     */
    public TextAlignment getAlignment()
    {
        return ( alignment );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFont( HUDFont font )
    {
        this.font = font;
        
        if ( isEnabled() )
        {
            this.multiLineText.setPositionDirty();
            setTextureDirty();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final HUDFont getFont()
    {
        return ( font );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFontDisabled( HUDFont font )
    {
        this.font_disabled = font;
        
        if ( !isEnabled() )
        {
            this.multiLineText.setPositionDirty();
            setTextureDirty();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final HUDFont getFontDisabled()
    {
        return ( font_disabled );
    }
    
    protected HUDFont getFont( boolean disabled )
    {
        if ( disabled )
            return ( getFontDisabled() );
        
        return ( getFont() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFontColor( Colorf color )
    {
        if ( !this.fontColor.equals( color ) )
        {
            this.fontColor = color;
            
            if ( isEnabled() )
            {
                setTextureDirty();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final Colorf getFontColor()
    {
        return ( fontColor.getReadOnly() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFontColorDisabled( Colorf color )
    {
        if ( !this.fontColor_disabled.equals( color ) )
        {
            this.fontColor_disabled = color;
            
            if ( !isEnabled() )
            {
                setTextureDirty();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final Colorf getFontColorDisabled()
    {
        return ( fontColor_disabled.getReadOnly() );
    }
    
    protected Colorf getFontColor( boolean disabled )
    {
        if ( disabled )
            return ( getFontColorDisabled() );
        
        return ( getFontColor() );
    }
    
    /**
     * Sets the icon for this Label.
     * 
     * @param icon
     */
    public void setIcon( Texture2D icon )
    {
        if ( this.icon == icon )
            return;
        
        this.icon = icon;
        
        setTextureDirty();
    }
    
    /**
     * Sets the icon for this Label.
     * 
     * @param icon
     */
    public final void setIcon( String icon )
    {
        setIcon( HUDTextureUtils.getTextureOrNull( icon, true ) );
    }
    
    /**
     * Gets the icon of this Label.
     * 
     * @return the icon of this Label.
     */
    public final Texture2D getIcon()
    {
        return ( icon );
    }
    
    /**
     * Sets the gap between the icon and the text.
     * 
     * @param gap the gap in pixels
     */
    public void setIconGap( int gap )
    {
        if ( this.iconGap == gap )
            return;
        
        this.iconGap = gap;
        
        setTextureDirty();
    }
    
    /**
     * Gets the gap between the icon and the text.
     * 
     * @return gap the gap in pixels
     */
    public final int getIconGap()
    {
        return ( iconGap );
    }
    
    /**
     * 
     * @param enabled
     */
    protected void setEnabledImpl( boolean enabled )
    {
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setEnabled( boolean enabled )
    {
        if ( enabled == this.enabled )
            return;
        
        this.enabled = enabled;
        
        setEnabledImpl( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        multiLineText.setPositionDirty();
        
        if ( getHUD() != null )
        {
            updateTranslation();
        }
    }
    
    @Override
    protected void onAttachedToHUD( HUD hud )
    {
        super.onAttachedToHUD( hud );
    }
    
    /**
     * Calculates the minimum Size needed to contain the whole caption.
     */
    public <Dim2f_ extends Dim2f> Dim2f_ getMinimalSize( Dim2f_ buffer )
    {
        double totalWidth = 0.0;
        double totalHeight = 0.0;
        
        if ( ( multiLineText.getNumLines() > 1 ) || ( ( multiLineText.getNumLines() == 1 ) && ( multiLineText.getLine( 0 ).length() > 0 ) ) )
        {
            HUDFont fnt = isEnabled() ? font : font_disabled;
            
            FontMetrics metrics = fnt.getFontMetrics( getHUD() );
            
            for ( int i = 0; i < multiLineText.getNumLines(); i++ )
            {
                Rectangle2D bounds = metrics.getStringBounds( multiLineText.getLine( i ), null );
                totalWidth = Math.max( totalWidth, bounds.getWidth() );
                totalHeight += bounds.getHeight();
            }
            
            if ( getIcon() != null )
            {
                totalWidth += getIconGap();
            }
        }
        
        if ( getIcon() != null )
        {
            totalWidth += HUDTextureUtils.getTextureWidth( getIcon() );
            totalHeight = Math.max( totalHeight, (double)HUDTextureUtils.getTextureHeight( getIcon() ) );
        }
        
        totalWidth += getPaddingLeft() + getPaddingRight();
        totalHeight += getPaddingTop() + getPaddingBottom();
        
        if ( getBorder() != null )
        {
            totalWidth += getBorder().getLeftWidth() + getBorder().getRightWidth();
            totalHeight += getBorder().getTopHeight() + getBorder().getBottomHeight();
        }
        
        if ( getHUD() != null )
            getSizePixels2HUD_( (int)totalWidth, (int)totalHeight, buffer );
        else
            buffer.set( (int)totalWidth, (int)totalHeight );
        
        return ( buffer );
    }
    
    /**
     * Resizes this Label to the minimum Size needed to contain the whole caption.
     */
    public final void setMinimalSize()
    {
        //if ( getHUD() == null )
        //    throw new IllegalStateException( "You cannot call this method, if the Widget is not added to the HUD." );
        
        final Dim2f newSize = Dim2f.fromPool();
        
        getMinimalSize( newSize );
        
        setSize( newSize );
        
        Dim2f.toPool( newSize );
    }
    
    /**
     * Gets the Label's minimum width.
     * 
     * @return the minimum width.
     */
    public final float getMinimalWidth()
    {
        Dim2f buffer = Dim2f.fromPool();
        
        getMinimalSize( buffer );
        
        float minWidth = buffer.getWidth();
        
        Dim2f.toPool( buffer );
        
        return ( minWidth );
    }
    
    /**
     * Gets the Label's minimum height.
     * 
     * @return the minimum height.
     */
    public final float getMinimalHeight()
    {
        Dim2f buffer = Dim2f.fromPool();
        
        getMinimalSize( buffer );
        
        float minHeight = buffer.getHeight();
        
        Dim2f.toPool( buffer );
        
        return ( minHeight );
    }
    
    public void setTextOffset( int textOffsetX, int textOffsetY )
    {
        if ( ( textOffsetX == this.textOffsetX ) && ( textOffsetY == this.textOffsetY ) )
            return;
        
        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;
        
        setTextureDirty();
    }
    
    public int getTextOffsetX()
    {
        return ( textOffsetX );
    }
    
    public int getTextOffsetY()
    {
        return ( textOffsetY );
    }
    
    private static Texture2D textImage = null;
    private static Texture2DCanvas textGraphics = null;
    private static Rect2i currClip = new Rect2i();
    
    protected void prepareText( MultilineText multiLineText, Texture2DCanvas texCanvas, int width, int height )
    {
        if ( isEnabled() )
        {
            texCanvas.setColor( getFontColor() );
            texCanvas.setFont( getFont().getAWTFont( getHUD() ) );
        }
        else
        {
            texCanvas.setColor( getFontColorDisabled() );
            texCanvas.setFont( getFontDisabled().getAWTFont( getHUD() ) );
        }
        
        int padLeft = getContentLeftPX();
        int padTop = getContentTopPX();
        Dim2i tmp = Dim2i.fromPool();
        getSizeHUD2Pixels_( getWidth(), getHeight(), tmp );
        int padRight = tmp.getWidth() - getContentLeftPX() - getContentWidthPX();
        int padBottom = tmp.getHeight() - getContentTopPX() - getContentHeightPX();
        Dim2i.toPool( tmp );
        
        multiLineText.update( texCanvas, width, height, padLeft, padRight, padTop, padBottom, getAlignment() );
    }
    
    protected void drawIcon( Texture2D icon, Texture2DCanvas texCanvas, int x, int y )
    {
        DrawUtils.drawImage( null, icon, null, texCanvas, x, y, HUDTextureUtils.getTextureWidth( icon ), HUDTextureUtils.getTextureHeight( icon ) );
    }
    
    /**
     * Draws the text on the Label.
     * 
     * @param multiLineText
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     */
    protected void drawText( MultilineText multiLineText, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        offsetX += getTextOffsetX();
        offsetY += getTextOffsetY();
        
        int textX0 = multiLineText.getMinPosX();
        int textY0 = multiLineText.getPosY( 0 ) + multiLineText.getLineOffsetY( 0 );
        int textTotalWidth = multiLineText.getTotalWidth();
        int textTotalHeight = multiLineText.getTotalHeight();
        
        texCanvas.getClip( currClip );
        
        /*
         * TODO: remove the "if (false &&)"
         * 
         * As long as DirectbufferedImage drawing is so slow, we can savely use the workaround always.
         * The workaround will always be faster. But this should be fixed one day in DirectBufferedImage resp. in JDK.
         */
        
        if ( false && currClip.covers( offsetX + textX0, offsetY + textY0, textTotalWidth, textTotalHeight ) )
        {
            texCanvas.setClip( (Rect2i)null );
            
            for ( int i = 0; i < multiLineText.getNumLines(); i++ )
            {
                texCanvas.drawString( multiLineText.getLine( i ), offsetX + multiLineText.getPosX( i ), offsetY + multiLineText.getPosY( i ), multiLineText.getWidth( i ), multiLineText.getHeight( i ) );
            }
            
            texCanvas.setClip( currClip );
        }
        else
        {
            // Since clipping doesn't work without corrupting the text on a DirectBufferedImage
            // doe to a bug in all JDKs (at least it looks like this),
            // we have to use this ugly workaround.
            
            //System.out.println( multiLineText.getLine( 0 ) + ", " + currClip + ", " + ( offsetX + textX0 ) + ", " + ( offsetY + textY0 ) + ", " + textTotalWidth + ", " + textTotalHeight );
            
            if ( ( textImage == null ) || ( textImage.getWidth() < textX0 + textTotalWidth ) || ( textImage.getHeight() < textTotalHeight ) )
            {
                int w = ImageUtility.roundUpPower2( textX0 + textTotalWidth );
                int h = ImageUtility.roundUpPower2( textTotalHeight );
                
                textImage = Texture2D.createDrawTexture( TextureFormat.RGBA, w, h, false );
                
                textGraphics = textImage.getTextureCanvas();
            }
            
            textGraphics.getImage().clear( Colorf.BLACK_TRANSPARENT );
            textGraphics.setColor( texCanvas.getColor() );
            textGraphics.setFont( texCanvas.getFont() );
            
            for ( int i = 0; i < multiLineText.getNumLines(); i++ )
            {
                textGraphics.drawString( multiLineText.getLine( i ), multiLineText.getPosX( i ), multiLineText.getPosY( i ) - textY0 );
            }
            
            texCanvas.getImage().drawImage( textImage.getImage0(), 0, 0, textX0 + textTotalWidth, textTotalHeight, offsetX, offsetY + textY0 );
        }
    }
    
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        prepareText( multiLineText, texCanvas, width, height );
        
        Texture2D icon = getIcon();
        
        if ( ( multiLineText.getNumLines() > 1 ) || ( ( multiLineText.getNumLines() == 1 ) && ( multiLineText.getLine( 0 ).length() > 0 ) ) )
        {
            int orgTextOffsetX = textOffsetX;
            int orgTextOffsetY = textOffsetY;
            
            if ( icon != null )
            {
                int iconWidth = HUDTextureUtils.getTextureWidth( icon );
                int iconHeight = HUDTextureUtils.getTextureHeight( icon );
                
                int gap = getIconGap();
                if ( getAlignment().isLeftAligned() )
                    textOffsetX += iconWidth + gap;
                else if ( getAlignment().isHCenterAligned() )
                    textOffsetX += ( iconWidth + gap ) / 2;
                
                int textX0 = multiLineText.getMinPosX();
                int textY0 = multiLineText.getPosY( 0 ) + multiLineText.getLineOffsetY( 0 );
                int textTotalHeight = multiLineText.getTotalHeight();
                
                if ( iconHeight > textTotalHeight )
                {
                    if ( getAlignment().isTopAligned() )
                        textOffsetY += ( iconHeight - textTotalHeight ) / 2;
                    else if ( getAlignment().isBottomAligned() )
                        textOffsetY -= ( iconHeight - textTotalHeight ) / 2;
                }
                
                int y = offsetY + textOffsetY + textY0 + ( ( textTotalHeight - iconHeight ) / 2 );
                
                switch ( getAlignment() )
                {
                    case TOP_LEFT:
                        drawIcon( icon, texCanvas, offsetX, y );
                        break;
                    case TOP_CENTER:
                        drawIcon( icon, texCanvas, offsetX + textX0 - ( iconWidth + gap ) / 2, y );
                        break;
                    case TOP_RIGHT:
                        drawIcon( icon, texCanvas, offsetX + textX0 - ( iconWidth + gap ), y );
                        break;
                    case CENTER_LEFT:
                        drawIcon( icon, texCanvas, offsetX, y );
                        break;
                    case CENTER_CENTER:
                        drawIcon( icon, texCanvas, offsetX + textX0 - ( iconWidth + gap ) / 2, y );
                        break;
                    case CENTER_RIGHT:
                        drawIcon( icon, texCanvas, offsetX + textX0 - ( iconWidth + gap ), y );
                        break;
                    case BOTTOM_LEFT:
                        drawIcon( icon, texCanvas, offsetX, y );
                        break;
                    case BOTTOM_CENTER:
                        drawIcon( icon, texCanvas, offsetX + textX0 - ( iconWidth + gap ) / 2, y );
                        break;
                    case BOTTOM_RIGHT:
                        drawIcon( icon, texCanvas, offsetX + textX0 - ( iconWidth + gap ), y );
                        break;
                }
            }
            
            drawText( multiLineText, texCanvas, offsetX, offsetY, width, height );
            
            if ( icon != null )
            {
                textOffsetX = orgTextOffsetX;
                textOffsetY = orgTextOffsetY;
            }
        }
        else if ( icon != null )
        {
            int iconWidth = HUDTextureUtils.getTextureWidth( icon );
            int iconHeight = HUDTextureUtils.getTextureHeight( icon );
            
            switch ( getAlignment() )
            {
                case TOP_LEFT:
                    drawIcon( icon, texCanvas, offsetX, offsetY );
                    break;
                case TOP_CENTER:
                    drawIcon( icon, texCanvas, offsetX + ( width - iconWidth ) / 2, offsetY );
                    break;
                case TOP_RIGHT:
                    drawIcon( icon, texCanvas, offsetX + width - iconWidth, offsetY );
                    break;
                case CENTER_LEFT:
                    drawIcon( icon, texCanvas, offsetX, offsetY + ( height - iconHeight ) / 2 );
                    break;
                case CENTER_CENTER:
                    drawIcon( icon, texCanvas, offsetX + ( width - iconWidth ) / 2, offsetY + ( height - iconHeight ) / 2 );
                    break;
                case CENTER_RIGHT:
                    drawIcon( icon, texCanvas, offsetX + width - iconWidth, offsetY + ( height - iconHeight ) / 2 );
                    break;
                case BOTTOM_LEFT:
                    drawIcon( icon, texCanvas, offsetX, offsetY + height - iconHeight );
                    break;
                case BOTTOM_CENTER:
                    drawIcon( icon, texCanvas, offsetX + ( width - iconWidth ) / 2, offsetY + height - iconHeight );
                    break;
                case BOTTOM_RIGHT:
                    drawIcon( icon, texCanvas, offsetX + width - iconWidth, offsetY + height - iconHeight );
                    break;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSize()
    {
        if ( autoSize )
        {
            setMinimalSize();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        /*
        if ( autoSize )
        {
            setMinimalSize();
        }
        */
        
        updateTranslation();
        
        setTransparency( getTransparency() );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font_enabled
     * @param font_disabled
     * @param fontColor_enabled
     * @param fontColor_disabled
     * @param alignment
     * @param backgroundColor
     * @param backgroundTexture
     */
    protected Label( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height, String text, HUDFont font_enabled, HUDFont font_disabled, Colorf fontColor_enabled, Colorf fontColor_disabled, TextAlignment alignment, Colorf backgroundColor, Texture2D backgroundTexture )
    {
        super( isHeavyWeight, hasWidgetAssembler, Math.max( 0f, width ), Math.max( 0f, height ), backgroundColor, backgroundTexture, TileMode.STRETCH );
        
        if ( text == null )
            throw new NullPointerException( "text must not be null" );
        
        this.autoSize = ( ( width <= 0f ) || ( height <= 0f ) );
        
        this.alignment = alignment;
        if ( font_enabled == null )
            this.font = HUD.getTheme().getFont( false );
        else
            this.font = font_enabled;
        if ( font_disabled == null )
            this.font_disabled = HUD.getTheme().getFont( true );
        else
            this.font_disabled = font_disabled;
        if ( fontColor_enabled == null )
            this.fontColor = HUD.getTheme().getFontColor( false );
        else
            this.fontColor = fontColor_enabled;
        if ( fontColor_disabled == null )
            this.fontColor_disabled = HUD.getTheme().getFontColor( true );
        else
            this.fontColor_disabled = fontColor_disabled;
        
        this.setText( text );
        
        this.setFocussable( false );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font_enabled
     * @param font_disabled
     * @param fontColor_enabled
     * @param fontColor_disabled
     * @param alignment
     * @param backgroundColor
     * @param backgroundTexture
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, HUDFont font_enabled, HUDFont font_disabled, Colorf fontColor_enabled, Colorf fontColor_disabled, TextAlignment alignment, Colorf backgroundColor, Texture2D backgroundTexture )
    {
        this( isHeavyWeight, false, width, height, text, font_enabled, font_disabled, fontColor_enabled, fontColor_disabled, alignment, backgroundColor, backgroundTexture );
    }
    
    private static final Description ld( Description desc )
    {
        if ( desc == null )
            return ( HUD.getTheme().getLabelDescription() );
        
        return ( desc );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param labelDesc a LabelDescription instance holding information about font, color, alignment and background-texture
     */
    protected Label( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height, String text, Description labelDesc )
    {
        this( isHeavyWeight, hasWidgetAssembler, width, height, text,
              ld( labelDesc ).getFont( false ), ld( labelDesc ).getFont( true ),
              ld( labelDesc ).getFontColor( false ), ld( labelDesc ).getFontColor( true ),
              ld( labelDesc ).getAlignment(),
              ld( labelDesc ).getBackgroundColor(), ld( labelDesc ).getBackgroundTexture()
            );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param labelDesc a LabelDescription instance holding information about font, color, alignment and background-texture
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, Description labelDesc )
    {
        this( isHeavyWeight, width, height, text,
              ld( labelDesc ).getFont( false ), ld( labelDesc ).getFont( true ),
              ld( labelDesc ).getFontColor( false ), ld( labelDesc ).getFontColor( true ),
              ld( labelDesc ).getAlignment(),
              ld( labelDesc ).getBackgroundColor(), ld( labelDesc ).getBackgroundTexture()
            );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param color the color to be used
     * @param alignment the horizontal and vertical alignment
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, HUDFont font, Colorf color, TextAlignment alignment )
    {
        this( isHeavyWeight, width, height, text,
              font, ( font == null ) ? Label.Description.deriveDisabledFont( HUD.getTheme().getFont( false ) ) : Label.Description.deriveDisabledFont( font ),
              color, null,
              alignment,
              null, null
            );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param alignment the horizontal and vertical alignment
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, TextAlignment alignment )
    {
        this( isHeavyWeight, width, height, text, null, null, alignment );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param color the color to be used
     * @param alignment the horizontal and vertical alignment
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, Colorf color, TextAlignment alignment )
    {
        this( isHeavyWeight, width, height, text, null, color, alignment );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param color the color to be used
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, HUDFont font, Colorf color )
    {
        this( isHeavyWeight, width, height, text, font, color, TextAlignment.TOP_LEFT );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param color the color to be used
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, Colorf color )
    {
        this( isHeavyWeight, width, height, text, null, color, TextAlignment.TOP_LEFT );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, HUDFont font )
    {
        this( isHeavyWeight, width, height, text, font, null, TextAlignment.TOP_LEFT );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param alignment the horizontal and vertical alignment
     */
    public Label( boolean isHeavyWeight, float width, float height, String text, HUDFont font, TextAlignment alignment )
    {
        this( isHeavyWeight, width, height, text, font, null, alignment );
    }
    
    /**
     * Creates a new Label with the given width and height and no text initially.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     */
    public Label( boolean isHeavyWeight, float width, float height )
    {
        this( isHeavyWeight, width, height, "", (HUDFont)null, (Colorf)null );
    }
    
    /**
     * Creates a new Label with the given width and height and no text initially.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     */
    public Label( boolean isHeavyWeight, float width, float height, String text )
    {
        this( isHeavyWeight, width, height, text, (HUDFont)null, (Colorf)null );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param labelDesc a LabelDescription instance holding information about font, color, alignment and background-texture
     */
    public Label( float width, float height, String text, Description labelDesc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, labelDesc );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param alignment the horizontal and vertical alignment
     */
    public Label( float width, float height, String text, TextAlignment alignment )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, alignment );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param color the color to be used
     * @param alignment the horizontal and vertical alignment
     */
    public Label( float width, float height, String text, Colorf color, TextAlignment alignment )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, color, alignment );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param color the color to be used
     * @param alignment the horizontal and vertical alignment
     */
    public Label( float width, float height, String text, HUDFont font, Colorf color, TextAlignment alignment )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, font, color, alignment );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param color the color to be used
     */
    public Label( float width, float height, String text, HUDFont font, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, font, color );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param color the color to be used
     */
    public Label( float width, float height, String text, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, color );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     */
    public Label( float width, float height, String text, HUDFont font )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, font );
    }
    
    /**
     * Creates a new Label with the given width and height and a z-index of 0.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param alignment the horizontal and vertical alignment
     */
    public Label( float width, float height, String text, HUDFont font, TextAlignment alignment )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, font, alignment );
    }
    
    /**
     * Creates a new Label with the given width and height and no text initially.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     */
    public Label( float width, float height )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height );
    }
    
    /**
     * Creates a new Label with the given width and height and no text initially.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     */
    public Label( float width, float height, String text )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text );
    }
}
