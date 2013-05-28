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

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;

/**
 * This class implements a base for rectangular Widgets, which are BackgroundSettable
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class BackgroundSettableWidget extends Widget
{
    protected abstract static class BackgroundSettableDescriptionBase extends Widget.DescriptionBase
    {
        private Colorf backgroundColor;
        private Texture2D backgroundTexture;
        
        /**
         * Sets the background color of this Widget.
         * 
         * @param color the color to use
         */
        public void setBackgroundColor( Colorf color )
        {
            backgroundColor = color;
        }
        
        /**
         * Returns the background color of this Widget.
         * 
         * @return the background color of this Widget.
         */
        public final Colorf getBackgroundColor()
        {
            return ( backgroundColor );
        }
        
        /**
         * Sets the background texture of this Widget.
         * 
         * @param texture the texture to use
         */
        public void setBackgroundTexture( Texture2D texture )
        {
            backgroundTexture = texture;
        }
        
        /**
         * Sets the background texture of this Widget.
         * 
         * @param texture the texture resource to use
         */
        public final void setBackgroundTexture( String texture )
        {
            if ( texture != null )
                setBackgroundTexture( HUDTextureUtils.getTexture( texture, true ) );
            else
                setBackgroundTexture( (Texture2D)null );
        }
        
        /**
         * Returns the background texture of this Widget.
         * 
         * @return the background texture of this Widget.
         */
        public final Texture2D getBackgroundTexture()
        {
            return ( backgroundTexture );
        }
        
        /**
         * Clon-Constructor
         * 
         * @param desc the original to clone
         */
        protected void setBgSDB( BackgroundSettableDescriptionBase desc )
        {
            this.backgroundColor = desc.backgroundColor;
            this.backgroundTexture = desc.backgroundTexture;
        }
        
        /**
         * Returns a clone of this instance.
         * 
         * @return a clone of this instance.
         */
        @Override
        public abstract BackgroundSettableDescriptionBase clone();
        
        /**
         * Creates a new Label.Description.
         * 
         * @param font the Font to be used for the text
         * @param color the color to be used
         * @param alignment the horizontal and vertical alignment
         * @param bgTex the background texture
         */
        public BackgroundSettableDescriptionBase( Colorf backgroundColor, Texture2D backgroundTexture )
        {
            this.backgroundColor = backgroundColor;
            this.backgroundTexture = backgroundTexture;
        }
    }
    
    private Colorf backgroundColor;
    private Texture2D backgroundTexture;
    private TileMode bgTileMode = TileMode.STRETCH;
    
    /**
     * Sets the background color of the Widget.
     * 
     * @param color the color to use
     */
    public void setBackgroundColor( Colorf color )
    {
        this.backgroundColor = color;
        //this.backgroundTexture = null;
        setTextureDirty();
    }
    
    /**
     * @return the background color of the Widget.
     */
    public final Colorf getBackgroundColor()
    {
        return ( backgroundColor );
    }
    
    /**
     * Sets the background texture of the Widget.
     * 
     * @param texture the texture resource to use
     */
    public void setBackgroundTexture( Texture2D texture )
    {
        //this.backgroundColor = null;
        this.backgroundTexture = texture;
        setTextureDirty();
    }
    
    /**
     * Sets the background texture of the Widget.
     * 
     * @param texture the texture resource to use
     * @param tileMode
     */
    public void setBackgroundTexture( Texture2D texture, TileMode tileMode )
    {
        //this.backgroundColor = null;
        this.backgroundTexture = texture;
        this.bgTileMode = tileMode;
        setTextureDirty();
    }
    
    /**
     * Sets the background Texture of the Widget.
     * 
     * @param texture the texture resource to use
     */
    public final void setBackgroundTexture( String texture )
    {
        setBackgroundTexture( HUDTextureUtils.getTexture( texture, true ) );
    }
    
    /**
     * Sets the background Texture of the Widget.
     * 
     * @param texture the texture resource to use
     */
    public final void setBackgroundTexture( String texture, TileMode tileMode )
    {
        setBackgroundTexture( HUDTextureUtils.getTexture( texture, true ), tileMode );
    }
    
    /**
     * Sets background color and texture at once.
     * 
     * @param color
     * @param texture
     * @param tileMode
     */
    public final void setBackground( Colorf color, String texture, TileMode tileMode )
    {
        setBackgroundColor( color );
        setBackgroundTexture( texture );
        setBackgroundTileMode( tileMode );
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
        setBackgroundColor( color );
        setBackgroundTexture( texture );
        setBackgroundTileMode( tileMode );
    }
    
    /**
     * Sets background color and texture at once.
     * 
     * @param color
     * @param texture
     */
    public final void setBackground( Colorf color, String texture )
    {
        setBackgroundColor( color );
        setBackgroundTexture( texture );
    }
    
    /**
     * Sets background color and texture to nothing at once.
     */
    public final void setNoBackground()
    {
        setBackgroundColor( null );
        setBackgroundTexture( (Texture2D)null );
    }
    
    /**
     * @return the background Texture of the Widget.
     */
    public final Texture2D getBackgroundTexture()
    {
        return ( backgroundTexture );
    }
    
    /**
     * Sets the background tile mode (null for no tiling)
     * 
     * @param mode
     */
    public void setBackgroundTileMode( TileMode mode )
    {
        this.bgTileMode = mode;
        setTextureDirty();
    }
    
    /**
     * @return the background tile mode (null for no tiling).
     */
    public final TileMode getBackgroundTileMode()
    {
        return ( bgTileMode );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawBackground( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        //super.drawBackground( texCanvas, offsetX, offsetY, width, height );
        
        if ( isHeavyWeight() )
        {
            if ( ( getBackgroundColor() == null ) && ( getBackgroundTexture() == null ) )
                DrawUtils.clearImage( Colorf.BLACK_TRANSPARENT, null, null, texCanvas, offsetX, offsetY, width, height );
            else
                DrawUtils.clearImage( getBackgroundColor(), getBackgroundTexture(), getBackgroundTileMode(), texCanvas, offsetX, offsetY, width, height );
        }
        else
        {
            if ( ( getBackgroundColor() == null ) && ( getBackgroundTexture() == null ) )
            {
                if ( ( getContainer() != null ) && !getContainer().isThisWidgetDirty() && ( getAssembly() == null ) )
                    getContainer().drawParentBackground( this, texCanvas, offsetX, offsetY, width, height );
            }
            else if ( ( ( getBackgroundColor() != null ) && !getBackgroundColor().hasAlpha() ) || ( ( getBackgroundTexture() != null ) && !getBackgroundTexture().getFormat().hasAlpha() ) )
            {
                DrawUtils.clearImage( getBackgroundColor(), getBackgroundTexture(), getBackgroundTileMode(), texCanvas, offsetX, offsetY, width, height );
            }
            else
            {
                if ( ( getContainer() != null ) && !getContainer().isThisWidgetDirty() && ( getAssembly() == null ) )
                    getContainer().drawParentBackground( this, texCanvas, offsetX, offsetY, width, height );
                
                DrawUtils.drawImage( getBackgroundColor(), getBackgroundTexture(), getBackgroundTileMode(), texCanvas, offsetX, offsetY, width, height );
            }
        }
    }
    
    /**
     * Creates a new BackgroundSettableWidget.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param backgroundColor
     * @param backgroundTexture
     * @param tileMode
     */
    protected BackgroundSettableWidget( boolean isHeavyWeight, boolean hasWidgetAssembler, Colorf backgroundColor, Texture2D backgroundTexture, TileMode tileMode )
    {
        super( isHeavyWeight, hasWidgetAssembler );
        
        setBackground( backgroundColor, backgroundTexture, tileMode );
    }
    
    /**
     * Creates a new BackgroundSettableWidget with the given width and height.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor
     * @param backgroundTexture
     * @param tileMode
     */
    protected BackgroundSettableWidget( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height, Colorf backgroundColor, Texture2D backgroundTexture, TileMode tileMode )
    {
        super( isHeavyWeight, hasWidgetAssembler, width, height );
        
        setBackground( backgroundColor, backgroundTexture, tileMode );
    }
}
