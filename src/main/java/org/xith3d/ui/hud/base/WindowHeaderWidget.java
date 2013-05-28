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

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;
import org.xith3d.ui.hud.widgets.Button;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * Represents a Window's header bar's Widget.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class WindowHeaderWidget extends Label //BackgroundSettableWidget implements TextWidget
{
    public static final int FLAG_ONLY_WINDOW_HEADER_WIDGET = 1;
    
    public static class Description extends Label.Description
    {
        private Button.Description closeButtonDesc;
        
        public void setCloseButtonDescription( Button.Description desc )
        {
            this.closeButtonDesc = desc;
        }
        
        public final Button.Description getCloseButtonDescription()
        {
            return ( closeButtonDesc );
        }
        
        /**
         * Clone-Constructor
         */
        public void set( Description desc )
        {
            super.set( desc );
            
            if ( desc.closeButtonDesc == null )
                this.closeButtonDesc = null;
            else
                this.closeButtonDesc = desc.closeButtonDesc.clone();
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
         */
        public Description( Description desc )
        {
            super( desc );
            
            this.set( desc );
        }
        
        /**
         * Full-Constructor with Texture
         * 
         * @param backgroundTexture
         * @param font
         * @param color
         * @param alignment
         * @param closeButtonDesc
         */
        public Description( Texture2D backgroundTexture, HUDFont font, Colorf color, TextAlignment alignment, Button.Description closeButtonDesc )
        {
            super( null, backgroundTexture, font, null, color, null, alignment );
            
            this.closeButtonDesc = closeButtonDesc;
        }
        
        /**
         * Full-Constructor with Texture
         * 
         * @param backgroundTexture
         * @param font
         * @param color
         * @param alignment
         * @param closeButtonDesc
         */
        public Description( String backgroundTexture, HUDFont font, Colorf color, TextAlignment alignment, Button.Description closeButtonDesc )
        {
            this( HUDTextureUtils.getTexture( backgroundTexture, true ), font, color, alignment, closeButtonDesc );
        }
    }
    
    private Description desc;
    private AbstractButton closeButton;
    
    /**
     * @return the Button, which closes the Window
     */
    public final AbstractButton getCloseButton()
    {
        return ( closeButton );
    }
    
    /**
     * Checks, if the mouse is over the bar (not over any Button, etc.).
     * 
     * @param relX
     * @param relY
     */
    public final boolean isMouseOverBar( float relX, float relY )
    {
        if ( relY > this.getHeight() )
            return ( false );
        
        final Widget test = getWidgetAssembler().pick( relX, relY );
        
        return ( test == null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected HUDPickResult pick( int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        flags &= ~HUDPickResult.HUD_PICK_FLAG_EVENTS_SUPPRESSED;
        
        return ( super.pick( canvasX, canvasY, pickReason, button, when, meta, flags ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        if ( ( closeButton != null ) && getWidgetAssembler().contains( closeButton ) )
        {
            final Dim2f threePixels = Dim2f.fromPool();
            getSizeOfPixels_( 3, 3, threePixels );
            
            getWidgetAssembler().reposition( closeButton, this.getWidth() - threePixels.getWidth() - closeButton.getWidth(), ( this.getHeight() - closeButton.getHeight() ) / 2f );
            
            Dim2f.toPool( threePixels );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setHostTextureDirty( int flags )
    {
        super.setHostTextureDirty( flags | FLAG_ONLY_WINDOW_HEADER_WIDGET );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setTextureDirty( int flags )
    {
        super.setTextureDirty( flags | FLAG_ONLY_WINDOW_HEADER_WIDGET );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        final Dim2f size2 = Dim2f.fromPool();
        
        if ( closeButton != null )
        {
            int wpx = HUDTextureUtils.getTextureWidth( desc.closeButtonDesc.getTextureNormal() );
            int hpx = HUDTextureUtils.getTextureHeight( desc.closeButtonDesc.getTextureNormal() );
            
            getSizePixels2HUD_( wpx, hpx, size2 );
            closeButton.setSize( size2 );
            
            getWidgetAssembler().addWidget( closeButton ); // The button will be correctly positioned when the widget is resized.
        }
        
        getSizePixels2HUD_( 0, HUDTextureUtils.getTextureHeight( getBackgroundTexture() ), size2 );
        this.setSize( this.getWidth(), size2.getHeight(), true );
        
        Dim2f.toPool( size2 );
        
        getWidgetAssembler().setPickDispatched( true );
    }
    
    /**
     * This method creates the close button for the Window.
     * It may return null, if you want.
     * 
     * @param desc the WindowHeaderWidget.Description
     * 
     * @return the created close Button
     */
    protected AbstractButton createCloseButton( Description desc )
    {
        Texture2D texNormal = desc.getCloseButtonDescription().getTextureNormal();
        Texture2D texHovered = desc.getCloseButtonDescription().getTextureHovered();
        Texture2D texPressed = desc.getCloseButtonDescription().getTexturePressed();
        
        Button btn = new Button( false, HUDTextureUtils.getTextureWidth( texNormal ), HUDTextureUtils.getTextureHeight( texNormal ), texNormal, texHovered, texPressed );
        
        return ( btn );
    }
    
    /**
     * Creates a new WindowHeaderWidget.
     * 
     * @param width
     * @param title
     * @param desc
     */
    public WindowHeaderWidget( float width, String title, Description desc )
    {
        super( false, true, width, HUDTextureUtils.getTextureHeight( desc.getBackgroundTexture() ), title, desc );
        
        this.desc = desc;
        
        this.closeButton = createCloseButton( desc );
    }
    
    /**
     * Creates a new WindowHeaderWidget.
     */
    public WindowHeaderWidget( float width, String title )
    {
        this( width, title, HUD.getTheme().getWindowHeaderDescription() );
    }
}
