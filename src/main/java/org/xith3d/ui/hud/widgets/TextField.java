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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.Keys;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Colorf;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Interval;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.Border;
import org.xith3d.ui.hud.listeners.TextFieldListener;
import org.xith3d.ui.hud.utils.Cursor;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * A TextField is a Widget that allows for editing a single line of text.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextField extends Label
{
    public static class Description extends Label.Description
    {
        private Border.Description borderDesc;
        
        private Texture2D caretTexture;
        
        public void setBorderDescription( Border.Description borderDesc )
        {
            this.borderDesc = borderDesc;
        }
        
        public final Border.Description getBorderDescription()
        {
            return ( borderDesc );
        }
        
        public void setCaretTexture( Texture2D texture )
        {
            this.caretTexture = texture;
        }
        
        public void setCaretTexture( String texture )
        {
            setCaretTexture( HUD.getTheme().getTextCaretTexture( texture ) );
        }
        
        public final Texture2D getCaretTexture()
        {
            return ( caretTexture );
        }
        
        public void set( Description template )
        {
            super.set( template );
            
            this.borderDesc = template.borderDesc.clone();
            this.caretTexture = template.caretTexture;
        }
        
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        protected Description( Description template )
        {
            super( template );
            
            this.set( template );
        }
        
        public Description( Label.Description labelDesc, Border.Description borderDesc, Texture2D caretTexture )
        {
            super( labelDesc );
            
            this.borderDesc = borderDesc;
            this.caretTexture = caretTexture;
        }
        
        public Description( Label.Description labelDesc, Border.Description borderDesc, String caretTexture )
        {
            this( labelDesc, borderDesc, HUDTextureUtils.getTexture( caretTexture, true ) );
        }
        
        public Description( Label.Description labelDesc, Border.Description borderDesc )
        {
            this( labelDesc, borderDesc, (Texture2D)null );
        }
    }
    
    private static class CaretBlinker extends Interval
    {
        private TextField currentTextField = null;
        private final Image caretImage;
        
        @Override
        protected void onIntervalHit( long gameTime, long frameTime, TimingMode timingMode )
        {
            super.onIntervalHit( gameTime, frameTime, timingMode );
            
            caretImage.setVisible( currentTextField.isEditable() && currentTextField.hasFocus( true ) && !caretImage.isVisible() );
            
            currentTextField.setTextureDirty();
        }
        
        private void init()
        {
            /*
            Dim2f buffer = Dim2f.fromPool();
            
            currentTextField.getSizePixels2HUD_( HUDTextureUtils.getTextureWidth( currentTextField.caretTex ), 0, buffer );
            float caretWidth = buffer.getWidth();
            
            currentTextField.getSizePixels2HUD_( 1, 1, buffer );
            float caretHeight = currentTextField.getContentHeight() - 2f * buffer.getHeight();
            
            Dim2f.toPool( buffer );
            
            caretImage.setSize( caretWidth, caretHeight );
            caretImage.setTexture( currentTextField.caretTex );
            
            currentTextField.getWidgetAssembler().addWidget( caretImage );
            caretImage.setZIndex( currentTextField.getZIndex() + 1 );
            */
            
            currentTextField.updateCaretPosition();
            
            caretImage.setVisible( currentTextField.isEditable() && currentTextField.hasFocus( true ) );
            
            currentTextField.setTextureDirty();
        }
        
        public void start( TextField textField, OperationScheduler opScheder )
        {
            if ( ( caretImage != null ) && ( caretImage.getContainer() != null ) )
            {
                caretImage.detach();
            }
            
            this.currentTextField = textField;
            
            init();
            
            if ( !this.isAlive() )
                opScheder.addInterval( this );
        }
        
        public void stop()
        {
            super.kill();
            
            if ( caretImage != null )
            {
                caretImage.setVisible( false );
                caretImage.detach();
            }
        }
        
        public CaretBlinker( long rate )
        {
            super( rate );
            
            this.caretImage = new Image( 1f, 16f, HUD.getTheme().getTextCaretTexture( "black" ), TileMode.STRETCH );
            caretImage.setVisible( false );
            
            stop();
        }
    }
    
    private String dispText = "";
    
    private static final CaretBlinker blinker = new CaretBlinker( 500000L );
    private final Texture2D caretTex;
    private int caretPosByChars;
    private float caretLeftHUD;
    
    private boolean editable = true;
    
    private char echoChar = '*';
    private boolean echoMode = false;
    
    private char[] ignoredChars = new char[] { '^' };
    
    private final ArrayList< TextFieldListener > listeners = new ArrayList< TextFieldListener >();
    
    /**
     * Adds a listener, that is notified of special TextField events.
     * 
     * @param l
     */
    public void addTextFieldListener( TextFieldListener l )
    {
        listeners.add( l );
    }
    
    /**
     * Removes a TextFieldListener from the list.
     * 
     * @param l
     */
    public void removeTextFieldListener( TextFieldListener l )
    {
        listeners.remove( l );
    }
    
    /**
     * Enables or disables the editability of this TextField. If not editable,
     * no key-events will be processed and the carret won't be visible.
     * 
     * @param editable
     */
    public void setEditable( boolean editable )
    {
        if ( editable == this.editable )
            return;
        
        this.editable = editable;
        
        if ( editable && this.hasFocus( true ) )
        {
            blinker.start( this, getHUD().getOperationScheduler() );
        }
    }
    
    /**
     * @return the editability of this TextField. If not editable, no key-events
     *         will be processed and the carret won't be visible.
     */
    public boolean isEditable()
    {
        return ( editable );
    }
    
    // TODO: This should be removed, since direct texture drawing already has a context!
    private static Graphics2D graphics = new BufferedImage( 16, 16, BufferedImage.TYPE_3BYTE_BGR ).createGraphics();
    
    private final float getCharWidth( char ch )
    {
        FontMetrics metrics = graphics.getFontMetrics( getFont( !isEnabled() ).getAWTFont( getHUD() ) );
        
        Rectangle2D bounds = metrics.getStringBounds( String.valueOf( ch ), graphics );
        
        return ( (float)bounds.getWidth() );
    }
    
    private final float getEchoCharWidth()
    {
        return ( getCharWidth( getEchoChar() ) );
    }
    
    private float getCharWidth( int i )
    {
        if ( i >= getDisplayedText().length() )
            return ( 0 );
        
        return ( getCharWidth( getDisplayedText().charAt( i ) ) );
    }
    
    private final float computeCaretPos()
    {
        FontMetrics metrics = graphics.getFontMetrics( getFont( !isEnabled() ).getAWTFont( getHUD() ) );
        
        Rectangle2D bounds = metrics.getStringBounds( getDisplayedText().substring( 0, caretPosByChars ), graphics );
        
        return ( (float)bounds.getWidth() );
    }
    
    private final void resetTextOffset()
    {
        setTextOffset( 0, 0 );
    }
    
    private void updateCaretPosition()
    {
        if ( getHUD() == null )
            return;
        
        caretLeftHUD = 0f;
        if ( isEchoMode() )
        {
            caretLeftHUD += caretPosByChars * getEchoCharWidth();
        }
        else
        {
            caretLeftHUD += computeCaretPos();
        }
        
        if ( getDisplayedText().length() == 0 )
        {
            resetTextOffset();
        }
        else
        {
            int textOffset = getTextOffsetX();
            int offsetIdx = -1;
            while ( textOffset + caretLeftHUD < 0 )
            {
                textOffset += getCharWidth( ++offsetIdx );
            }
            
            int widthPX = getContentWidthPX();
            
            offsetIdx = getText().length();
            while ( textOffset + caretLeftHUD > widthPX )
            {
                textOffset -= getCharWidth( --offsetIdx );
            }
            
            setTextOffset( textOffset, getTextOffsetY() );
        }
        
        //System.out.println( getTextOffsetX() );
        
        caretLeftHUD += getTextOffsetX();
        
        final Dim2f buffer = Dim2f.fromPool();
        
        caretLeftHUD = getSizePixels2HUD_( (int)caretLeftHUD, 0, buffer ).getWidth();
        
        Dim2f pixel = buffer;
        getSizeOfPixels_( 1, 1, pixel );
        caretLeftHUD += getPaddingLeft();
        if ( getBorder() != null )
            caretLeftHUD += getBorder().getLeftWidth();
        //getWidgetAssembler().reposition( blinker.caretImage, caretLeft, ( getContentTopPX() + 1 ) * pixel.getHeight() );
        
        Dim2f.toPool( buffer );
        
        updateTranslation();
    }
    
    public void setCaretPosition( int pos )
    {
        this.caretPosByChars = pos;
        
        caretPosByChars = Math.max( caretPosByChars, 0 );
        caretPosByChars = Math.min( caretPosByChars, getDisplayedText().length() );
        
        updateCaretPosition();
    }
    
    public final int getCaretPosition()
    {
        return ( caretPosByChars );
    }
    
    /**
     * Sets the character to replace each character of the TextField's text with
     * when in echo-mode.
     * 
     * @param echoChar
     */
    public void setEchoChar( char echoChar )
    {
        this.echoChar = echoChar;
    }
    
    /**
     * @return the character to replace each character of the TextField's text with
     * when in echo-mode.
     */
    public final char getEchoChar()
    {
        return ( echoChar );
    }
    
    /**
     * Enables/Disables the echo-mode.<br>
     * In echo-mode each character of the TextField's text will be replaced with
     * the echo char.
     * 
     * @param echoMode
     * @param echoChar
     */
    public void setEchoMode( boolean echoMode, char echoChar )
    {
        this.echoMode = echoMode;
        setEchoChar( echoChar );
        
        updateText();
    }
    
    /**
     * Enables/Disables the echo-mode.<br>
     * In echo-mode each character of the TextField's text will be replaced with
     * the echo char.
     * 
     * @param echoMode
     */
    public void setEchoMode( boolean echoMode )
    {
        setEchoMode( echoMode, getEchoChar() );
    }
    
    /**
     * @return the echo-mode.<br>
     * In echo-mode each character of the TextField's text will be replaced with
     * the echo char.
     */
    public boolean isEchoMode()
    {
        return ( echoMode );
    }
    
    /**
     * Sets the array of chars ignored by this TextField.
     * 
     * @param ignoredChars
     */
    public void setIgnoredChars( char... ignoredChars )
    {
        this.ignoredChars = ignoredChars;
    }
    
    /**
     * @return the array of chars ignored by this TextField.
     */
    public char[] getIgnoredChars()
    {
        return ( ignoredChars );
    }
    
    private void updateDisplayedText()
    {
        final String text = getText();
        
        if ( isEchoMode() )
        {
            char[] chars = new char[ text.length() ];
            for ( int i = 0; i < chars.length; i++ )
                chars[i] = getEchoChar();
            
            this.dispText = new String( chars );
        }
        else
        {
            this.dispText = text;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayedText()
    {
        return ( dispText );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateText()
    {
        updateDisplayedText();
        
        super.updateText();
    }
    
    private void setTextInternal( String text )
    {
        super.setText( text );
        
        updateCaretPosition();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setText( String text )
    {
        //this.caretPosByChars = text.length();
        this.caretPosByChars = 0;
        resetTextOffset();
        
        setTextInternal( text );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFont( HUDFont font )
    {
        super.setFont( font );
        
        updateCaretPosition();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean blocksFocusMoveDeviceComponent( DeviceComponent dc )
    {
        if ( dc == Keys.LEFT )
            return ( true );
        
        if ( dc == Keys.RIGHT )
            return ( true );
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyPressed( Key key, int modifierMask, long when )
    {
        super.onKeyPressed( key, modifierMask, when );
        
        if ( !isEditable() || !isEnabled() )
        {
            return;
        }
        
        switch ( key.getKeyID() )
        {
            case LEFT:
                caretPosByChars = Math.max( 0, caretPosByChars - 1 );
                updateCaretPosition();
                setTextureDirty();
                
                break;
            
            case RIGHT:
                caretPosByChars = Math.min( caretPosByChars + 1, getDisplayedText().length() );
                updateCaretPosition();
                setTextureDirty();
                
                break;
            
            case HOME:
                caretPosByChars = 0;
                updateCaretPosition();
                setTextureDirty();
                
                break;
            
            case END:
                caretPosByChars = getDisplayedText().length();
                updateCaretPosition();
                setTextureDirty();
                
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyTyped( char ch, int modifierMask, long when )
    {
        super.onKeyTyped( ch, modifierMask, when );
        
        if ( !isEditable() || !isEnabled() )
        {
            return;
        }
        
        if ( ch == '\b' )
        {
            if ( caretPosByChars > 0 )
            {
                final String leftPart = ( caretPosByChars > 0 ) ? getText().substring( 0, caretPosByChars - 1 ) : "";
                final String rightPart = ( caretPosByChars < getText().length() ) ? getText().substring( caretPosByChars ) : "";
                caretPosByChars -= 1;
                setTextInternal( leftPart + rightPart );
                setTextureDirty();
            }
            
            for ( int i = 0; i < listeners.size(); i++ )
            {
                listeners.get( i ).onCharDeleted( this );
            }
        }
        else if ( ch == (char)127 ) // delete
        {
            if ( caretPosByChars < getText().length() )
            {
                final String leftPart = ( caretPosByChars > 0 ) ? getText().substring( 0, caretPosByChars ) : "";
                final String rightPart = ( caretPosByChars < getText().length() ) ? getText().substring( caretPosByChars + 1 ) : "";
                setTextInternal( leftPart + rightPart );
                setTextureDirty();
            }
            
            for ( int i = 0; i < listeners.size(); i++ )
            {
                listeners.get( i ).onCharDeleted( this );
            }
        }
        else if ( ch == (char)27 ) // ESCAPE
        {
            for ( int i = 0; i < listeners.size(); i++ )
            {
                listeners.get( i ).onEscapeHit( this );
            }
        }
        else if ( ch == '\t' )
        {
            for ( int i = 0; i < listeners.size(); i++ )
            {
                listeners.get( i ).onTabHit( this );
            }
        }
        else if ( ( ch == 10 ) || ( ch == 13 ) ) // ENTER
        {
            for ( int i = 0; i < listeners.size(); i++ )
            {
                listeners.get( i ).onEnterHit( this );
            }
        }
        else
        {
            boolean ignored = false;
            if ( ignoredChars != null )
            {
                for ( int i = 0; i < ignoredChars.length; i++ )
                {
                    if ( ignoredChars[ i ] == ch )
                    {
                        ignored = true;
                        break;
                    }
                }
            }
            
            if ( !ignored )
            {
                final String leftPart = ( caretPosByChars > 0 ) ? getText().substring( 0, caretPosByChars ) : "";
                final String rightPart = ( caretPosByChars < getText().length() ) ? getText().substring( caretPosByChars ) : "";
                caretPosByChars += 1;
                setTextInternal( leftPart + ch + rightPart );
                //setTextureDirty();
                
                for ( int i = 0; i < listeners.size(); i++ )
                {
                    listeners.get( i ).onCharTyped( this, ch );
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        updateCaretPosition();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusGained()
    {
        super.onFocusGained();
        
        if ( isEditable() )
        {
            blinker.start( this, getHUD().getOperationScheduler() );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusLost()
    {
        super.onFocusLost();
        
        blinker.stop();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToHUD( HUD hud )
    {
        super.onAttachedToHUD( hud );
        
        updateCaretPosition();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromHUD( HUD hud )
    {
        super.onDetachedFromHUD( hud );
        
        if ( blinker != null )
        {
            blinker.stop();
        }
    }
    
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        super.drawWidget( texCanvas, offsetX, offsetY, width, height, drawsSelf );
        
        if ( blinker.caretImage.isVisible() )
        {
            Dim2i buffer = Dim2i.fromPool();
            getSizeHUD2Pixels_( caretLeftHUD, 0, buffer );
            int caretLeftPX = buffer.getWidth();
            Dim2i.toPool( buffer );
            
            int caretWidth = HUDTextureUtils.getTextureWidth( caretTex );
            
            DrawUtils.drawImage( null, caretTex, TileMode.STRETCH, texCanvas, offsetX + caretLeftPX - getPaddingLeft(), offsetY + 1, caretWidth, getContentHeightPX() - 2 );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        super.init();
        
        updateDisplayedText();
        setText( getText() );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param textFieldDesc a LabelDescription instance holding information
     *                      about font, color, alignment and background-texture
     */
    public TextField( boolean isHeavyWeight, float width, float height, String text, Description textFieldDesc )
    {
        super( isHeavyWeight, true, width, height, text, textFieldDesc );
        
        this.setFocussable( true );
        
        //setPadding( 0, 0, 0, 2 );
        setBorder( textFieldDesc.getBorderDescription() );
        
        this.caretTex = ( textFieldDesc.getCaretTexture() != null ) ? textFieldDesc.getCaretTexture() : HUD.getTheme().getTextFieldDescription().getCaretTexture();
        
        //this.caretPosByChars = getText().length();
        this.caretPosByChars = 0;
        
        resetTextOffset();
        
        this.setCursor( Cursor.Type.TEXT );
    }
    
    private static Description getDesc( HUDFont font, Colorf color, TextAlignment alignment )
    {
        Description desc = HUD.getTheme().getTextFieldDescription();
        
        if ( font != null )
        {
            desc.setFont( font, false );
            desc.setFont( Label.Description.deriveDisabledFont( font ), false );
        }
        
        if ( color != null )
            desc.setFontColor( color, false );
        
        if ( alignment != null )
            desc.setAlignment( alignment );
        
        return ( desc );
    }
    
    /**
     * Creates a new TextField with the given width, height and z-index.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param color the color to be used
     * @param alignment the horizontal and vertical alignment
     */
    public TextField( boolean isHeavyWeight, float width, float height, String text, HUDFont font, Colorf color, TextAlignment alignment )
    {
        this( isHeavyWeight, width, height, text, getDesc( font, color, alignment ) );
    }
    
    /**
     * Creates a new TextField with the given width and height and no text
     * initially.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     */
    public TextField( boolean isHeavyWeight, float width, float height, String text )
    {
        this( isHeavyWeight, width, height, text, HUD.getTheme().getTextFieldDescription() );
    }
    
    /**
     * Creates a new TextField with the given width and height and no text
     * initially.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     */
    public TextField( boolean isHeavyWeight, float width, float height )
    {
        this( isHeavyWeight, width, height, "", HUD.getTheme().getTextFieldDescription() );
    }
    
    /**
     * Creates a new Label with the given width, height and z-index.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param textFieldDesc a LabelDescription instance holding information
     *                      about font, color, alignment and background-texture
     */
    public TextField( float width, float height, String text, Description textFieldDesc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, textFieldDesc );
    }
    
    /**
     * Creates a new TextField with the given width, height and z-index.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     * @param font the Font to be used for the text
     * @param color the color to be used
     * @param alignment the horizontal and vertical alignment
     */
    public TextField( float width, float height, String text, HUDFont font, Colorf color, TextAlignment alignment )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, getDesc( font, color, alignment ) );
    }
    
    /**
     * Creates a new TextField with the given width and height and no text
     * initially.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display in this TextWidget
     */
    public TextField( float width, float height, String text )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, HUD.getTheme().getTextFieldDescription() );
    }
    
    /**
     * Creates a new TextField with the given width and height and no text
     * initially.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     */
    public TextField( float width, float height )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, "", HUD.getTheme().getTextFieldDescription() );
    }
}
