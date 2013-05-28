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
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;

/**
 * A widget representing an image.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Image extends Widget
{
    private Colorf color;
    private Texture2D texture;
    private TileMode tileMode = TileMode.STRETCH;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransparency( float transparency, boolean childrenToo )
    {
        super.setTransparency( transparency, childrenToo );
        
        if ( color != null )
        {
            color.setAlpha( transparency );
            
            setTextureDirty();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setTexture( Texture2D texture )
    {
        if ( texture == this.texture )
            return;
        
        this.texture = texture;
        
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setTexture( String texture )
    {
        setTexture( HUDTextureUtils.getTexture( texture, true ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Texture2D getTexture()
    {
        return ( texture );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setTileMode( TileMode mode )
    {
        if ( mode == this.tileMode )
            return;
        
        this.tileMode = mode;
        
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    public final TileMode getTileMode()
    {
        return ( tileMode );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setColor( Colorf color )
    {
        if ( color == this.color )
            return;
        
        this.color = color;
        
        setTextureDirty();
    }
    
    /**
     * {@inheritDoc}
     */
    public final Colorf getColor()
    {
        if ( color == null )
            return ( null );
        
        return ( color.getReadOnly() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( ( getColor() != null ) && ( getTexture() != null ) )
            return ( "Image ( " + getColor() + ", \"" + getTexture().getResourceName() + "\" )" );
        
        if ( getColor() != null )
            return ( "Image ( " + getColor() + " )" );
        
        if ( getTexture() != null )
            return ( "Image ( \"" + getTexture().getResourceName() + "\" )" );
        
        return ( super.toString() );
    }
    
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        if ( isHeavyWeight() )
            DrawUtils.clearImage( getColor(), getTexture(), getTileMode(), texCanvas, offsetX, offsetY, width, height );
        else
            DrawUtils.drawImage( getColor(), getTexture(), getTileMode(), texCanvas, offsetX, offsetY, width, height );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
    }
    
    /**
     * Creates a new Image with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param color the color to use for the Image
     * @param texture the Texture to use for this Image
     */
    public Image( boolean isHeavyWeight, float width, float height, Colorf color, Texture2D texture, TileMode tileMode )
    {
        super( isHeavyWeight, false, width, height );
        
        this.color = color;
        this.texture = texture;
        this.tileMode = ( tileMode != null ) ? tileMode : TileMode.STRETCH;
        
        this.setFocussable( false );
    }
    
    /**
     * Creates a new Image with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param color the color to use for the Image
     */
    public Image( boolean isHeavyWeight, float width, float height, Colorf color )
    {
        this( isHeavyWeight, width, height, color, null, null );
    }
    
    /**
     * Creates a new Image with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texture the Texture to use for this Image
     * @param tileMode
     */
    public Image( boolean isHeavyWeight, float width, float height, Texture2D texture, TileMode tileMode )
    {
        this( isHeavyWeight, width, height, null, texture, tileMode );
    }
    
    /**
     * Creates a new Image with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texture the Texture to use for this Image
     */
    public Image( boolean isHeavyWeight, float width, float height, Texture2D texture )
    {
        this( isHeavyWeight, width, height, null, texture, null );
    }
    
    /**
     * Creates a new Image with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texture the Texture resource to use for this Image
     */
    public Image( boolean isHeavyWeight, float width, float height, String texture )
    {
        this( isHeavyWeight, width, height, null, HUDTextureUtils.getTexture( texture, true ), null );
    }
    
    /**
     * Creates a new <b>lightweight</b> Image with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param color the color to use for the Image
     * @param texture the Texture to use for this Image
     */
    public Image( float width, float height, Colorf color, Texture2D texture )
    {
        this( false, width, height, color, texture, null );
    }
    
    /**
     * Creates a new <b>lightweight</b> Image with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param color the color to use for the Image
     */
    public Image( float width, float height, Colorf color )
    {
        this( false, width, height, color );
    }
    
    /**
     * Creates a new <b>lightweight</b> Image with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texture the Texture to use for this Image
     * @param tileMode
     */
    public Image( float width, float height, Texture2D texture, TileMode tileMode )
    {
        this( false, width, height, texture, tileMode );
    }
    
    /**
     * Creates a new <b>lightweight</b> Image with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texture the Texture to use for this Image
     */
    public Image( float width, float height, Texture2D texture )
    {
        this( false, width, height, texture );
    }
    
    /**
     * Creates a new <b>lightweight</b> Image with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texture the Texture resource to use for this Image
     */
    public Image( float width, float height, String texture )
    {
        this( false, width, height, texture );
    }
}
