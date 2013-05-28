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

import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This class serves as a base for all StateButtons with an Image and a Label.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class LabeledStateButton extends StateButton implements TextWidget, AutoSizable
{
    protected static final boolean DEFAULT_HEAVYWEIGHT = false;
    
    /**
     * This class is used to describe a LabeledStateButton Widget.
     * You can pass it to the constructor.
     * Modifications on the used instance after creating the Widget
     * won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends Widget.DescriptionBase
    {
        private Texture2D texDeactivatedNormal;
        private Texture2D texDeactivatedHovered;
        private Texture2D texActivatedNormal;
        private Texture2D texActivatedHovered;
        
        private int space;
        
        private Label.Description labelDesc;
        
        public void setTexture( boolean state, boolean hovered, Texture2D texture )
        {
            if ( state )
            {
                if ( hovered )
                    this.texActivatedNormal = texture;
                else
                    this.texActivatedHovered = texture;
            }
            else
            {
                if ( hovered )
                    this.texDeactivatedNormal = texture;
                else
                    this.texDeactivatedHovered = texture;
            }
        }
        
        public void setTexture( boolean state, boolean hovered, String name )
        {
            setTexture( state, hovered, HUDTextureUtils.getTextureOrNull( name, true ) );
        }
        
        public final Texture2D getTexture( boolean state, boolean hovered )
        {
            if ( state )
            {
                if ( hovered )
                    return ( texActivatedHovered );
                
                return ( texActivatedNormal );
            }
            
            if ( hovered )
                return ( texDeactivatedHovered );
            
            return ( texDeactivatedNormal );
        }
        
        public void setSpace( int space )
        {
            this.space = space;
        }
        
        public final int getSpace()
        {
            return ( space );
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
         */
        public void set( Description desc )
        {
            this.texDeactivatedNormal = desc.texDeactivatedNormal;
            this.texDeactivatedHovered = desc.texDeactivatedHovered;
            this.texActivatedNormal = desc.texActivatedNormal;
            this.texActivatedHovered = desc.texActivatedHovered;
            
            this.space = desc.space;
            
            this.labelDesc = desc.labelDesc.clone();
        }
        
        /**
         * Clones LabeledStateButton.Description
         */
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        /**
         * Clone-Constructor
         */
        public Description( Description desc )
        {
            this.set( desc );
        }
        
        /**
         * Create a new LabeledStateButton.Description
         * 
         * @param texDeactivatedNormal Texture for DEACTIVATED_NORMAL state
         * @param texDeactivatedHovered Texture for DEACTIVATED_HOVERED state
         * @param texActivatedNormal Texture for ACTIVATED_NORMAL state
         * @param texActivatedHovered Texture for ACTIVATED_HOVERED state
         * @param imageSize size of the state images
         * @param space space between the Image and the Label
         * @param labelDesc description of the Label
         */
        public Description( Texture2D texDeactivatedNormal, Texture2D texDeactivatedHovered, Texture2D texActivatedNormal, Texture2D texActivatedHovered, int space, Label.Description labelDesc )
        {
            this.texDeactivatedNormal = texDeactivatedNormal;
            this.texDeactivatedHovered = texDeactivatedHovered;
            this.texActivatedNormal = texActivatedNormal;
            this.texActivatedHovered = texActivatedHovered;
            
            this.space = space;
            
            this.labelDesc = labelDesc;
        }
        
        /**
         * Create a new LabeledStateButton.Description
         * 
         * @param texDeactivatedNormal Texture for DEACTIVATED_NORMAL state
         * @param texDeactivatedHovered Texture for DEACTIVATED_HOVERED state
         * @param texActivatedNormal Texture for ACTIVATED_NORMAL state
         * @param texActivatedHovered Texture for ACTIVATED_HOVERED state
         * @param imageSize size of the state images
         * @param space space between the Image and the Label
         * @param labelDesc description of the Label
         */
        public Description( String texDeactivatedNormal, String texDeactivatedHovered, String texActivatedNormal, String texActivatedHovered, int space, Label.Description labelDesc )
        {
            this( HUDTextureUtils.getTexture( texDeactivatedNormal, true ), HUDTextureUtils.getTexture( texDeactivatedHovered, true ), HUDTextureUtils.getTexture( texActivatedNormal, true ), HUDTextureUtils.getTexture( texActivatedHovered, true ), space, labelDesc );
        }
    }
    
    private Description desc;
    private Texture2D buttonTex;
    private Label label;
    
    private boolean autoSize = false;
    
    /**
     * Sets the background color of the Widget.
     * 
     * @param color the color to use
     */
    public void setBackgroundColor( Colorf color )
    {
        label.setBackgroundColor( color );
    }
    
    /**
     * @return the background color of the Widget.
     */
    public final Colorf getBackgroundColor()
    {
        return ( label.getBackgroundColor() );
    }
    
    /**
     * Sets the background texture of the Widget.
     * 
     * @param texture the texture resource to use
     */
    public void setBackgroundTexture( Texture2D texture )
    {
        label.setBackgroundTexture( texture );
    }
    
    /**
     * Sets the background Texture of the Widget.
     * 
     * @param texture the texture resource to use
     */
    public void setBackgroundTexture( String texture )
    {
        label.setBackgroundTexture( texture );
    }
    
    /**
     * Sets background color and texture at once.
     * 
     * @param color
     * @param texture
     * @param tileMode
     */
    public final void setBackground( Colorf color, Texture2D texture, TileMode tileMode )
    {
        label.setBackground( color, texture, tileMode );
    }
    
    /**
     * Sets background color and texture at once.
     * 
     * @param color
     * @param texture
     */
    public final void setBackground( Colorf color, String texture )
    {
        label.setBackground( color, texture );
    }
    
    /**
     * Sets background color and texture to nothing at once.
     */
    public final void setNoBackground()
    {
        label.setNoBackground();
    }
    
    /**
     * @return the background Texture of the Widget.
     */
    public final Texture2D getBackgroundTexture()
    {
        return ( label.getBackgroundTexture() );
    }
    
    /**
     * Sets the background tile mode (null for no tiling)
     * 
     * @param mode
     */
    public void setBackgroundTileMode( TileMode mode )
    {
        label.setBackgroundTileMode( mode );
    }
    
    /**
     * @return the background tile mode (null for no tiling).
     */
    public final TileMode getBackgroundTileMode()
    {
        return ( label.getBackgroundTileMode() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setText( String text )
    {
        label.setText( text );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getText()
    {
        return ( label.getText() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setAlignment( TextAlignment alignment )
    {
        label.setAlignment( alignment );
    }
    
    /**
     * {@inheritDoc}
     */
    public final TextAlignment getAlignment()
    {
        return ( label.getAlignment() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFont( HUDFont font )
    {
        label.setFont( font );
    }
    
    /**
     * {@inheritDoc}
     */
    public final HUDFont getFont()
    {
        return ( label.getFont() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFontDisabled( HUDFont font )
    {
        label.setFontDisabled( font );
    }
    
    /**
     * {@inheritDoc}
     */
    public final HUDFont getFontDisabled()
    {
        return ( label.getFontDisabled() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFontColor( Colorf color )
    {
        label.setFontColor( color );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Colorf getFontColor()
    {
        return ( label.getFontColor() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFontColorDisabled( Colorf color )
    {
        label.setFontColorDisabled( color );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Colorf getFontColorDisabled()
    {
        return ( label.getFontColorDisabled() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEnabledImpl( boolean enabled )
    {
        label.setEnabled( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onExtendedStateChanged( boolean state, boolean hovered )
    {
        this.buttonTex = desc.getTexture( state, hovered );
        
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        label.setSize( newWidth, newHeight );
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
    
    /**
     * {@inheritDoc}
     */
    public final <Dim2f_ extends Dim2f> Dim2f_ getMinimalSize( Dim2f_ buffer )
    {
        float minWidth = 0f;
        float minHeight = 0f;
        
        if ( buttonTex != null )
        {
            getSizePixels2HUD_( HUDTextureUtils.getTextureWidth( buttonTex ), HUDTextureUtils.getTextureHeight( buttonTex ), buffer );
            minWidth += buffer.getWidth();
            minHeight = Math.max( minHeight, buffer.getHeight() );
            
            if ( label != null )
            {
                getSizePixels2HUD_( desc.getSpace(), 0, buffer );
                minWidth += buffer.getWidth();
            }
        }
        
        if ( label != null )
        {
            label.getMinimalSize( buffer );
            minWidth += buffer.getWidth();
            minHeight = Math.max( minHeight, buffer.getHeight() );
        }
        
        buffer.set( minWidth, minHeight );
        
        return ( buffer );
    }
    
    /**
     * Resizes this LabeledStateButton to the minimum Size needed to contain the whole caption.
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
     * Gets the state-button's minimum width.
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
     * Gets the state-button's minimum height.
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
    
    protected boolean isMouseOverStateImage( float mouseX, float mouseY )
    {
        Dim2f buffer2 = Dim2f.fromPool();
        getSizePixels2HUD_( HUDTextureUtils.getTextureWidth( buttonTex ), HUDTextureUtils.getTextureHeight( buttonTex ), buffer2 );
        float iconWidth = buffer2.getWidth();
        float iconHeight = buffer2.getHeight();
        Dim2f.toPool( buffer2 );
        
        float iconTop = ( getContentHeight() - iconHeight ) / 2f;
        
        if ( ( mouseX < 0f ) || ( 0f + iconWidth < mouseX ) )
            return ( false );
        
        if ( ( mouseY < iconTop ) || ( iconTop + iconHeight < mouseY ) )
            return ( false );
        
        return ( true );
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
    protected void drawWidgetAfterWidgetAssembler( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        super.drawWidgetAfterWidgetAssembler( texCanvas, offsetX, offsetY, width, height, drawsSelf );
        
        int y = ( height - HUDTextureUtils.getTextureHeight( buttonTex ) ) / 2;
        DrawUtils.drawImage( null, buttonTex, null, texCanvas, offsetX, offsetY + y, width, height - y );
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
        if ( label != null )
        {
            //getWidgetAssembler().addWidget( label, 0f, 0f );
            getWidgetAssembler().reposition( label, 0f, 0f );
        }
    }
    
    /**
     * Creates a new LabeledStateButton
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param desc the Description object for this Widget
     * @param text the text to be displayed
     */
    public LabeledStateButton( boolean isHeavyWeight, float width, float height, String text, Description desc )
    {
        super( isHeavyWeight, true, Math.max( 0f, width ), Math.max( 0f, height ) );
        
        this.autoSize = ( ( width <= 0f ) || ( height <= 0f ) );
        
        this.desc = desc.clone();
        
        this.buttonTex = desc.getTexture( getState(), isHovered() );
        
        if ( ( desc.getLabelDescription() != null ) && ( text != null ) )
            this.label = new Label( false, Math.max( 0.1f, width ), Math.max( 0.1f, height ), text, desc.getLabelDescription() );
        else
            this.label = new Label( false, Math.max( 0.1f, width ), Math.max( 0.1f, height ), "" );
        
        getWidgetAssembler().addWidget( label, 0f, 0f );
        label.setTextOffset( HUDTextureUtils.getTextureWidth( desc.getTexture( false, false ) ) + desc.getSpace(), 0 );
    }
}
