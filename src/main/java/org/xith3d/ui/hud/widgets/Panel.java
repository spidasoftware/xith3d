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
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;

public class Panel extends WidgetContainer
{
    protected static final boolean DEFAULT_HEAVYWEIGHT = false;
    
    private Texture2D foregroundImage = null;
    
    /**
     * Sets the foreground texture of the WidgetContainer.
     * It MUST always have an alpha channel!
     * 
     * @param texture the texture resource to use
     */
    public void setForegroundTexture( Texture2D texture )
    {
        this.foregroundImage = texture;
    }
    
    /**
     * Sets the foreground texture of the WidgetContainer.
     * It MUST always have an alpha channel!
     * 
     * @param texture the texture resource to use
     */
    public final void setForegroundTexture( String texture )
    {
        setForegroundTexture( HUDTextureUtils.getTexture( texture, true ) );
    }
    
    /**
     * @return the foreground Texture of the WidgetContainer.
     */
    public final Texture2D getForegroundTexture()
    {
        return ( foregroundImage );
    }
    
    protected void drawForeground( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        if ( getForegroundTexture() == null )
            return;
        
        DrawUtils.drawImage( null, getForegroundTexture(), TileMode.STRETCH, texCanvas, offsetX, offsetY, width, height );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        super.drawWidget( texCanvas, offsetX, offsetY, width, height, drawsSelf );
        
        drawForeground( texCanvas, offsetX, offsetY, width, height );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    protected Panel( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height, Colorf backgroundColor, Texture2D backgroundTex, TileMode backgroundTileMode )
    {
        super( isHeavyWeight, hasWidgetAssembler, width, height, backgroundColor, backgroundTex, backgroundTileMode );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public Panel( boolean isHeavyWeight, float width, float height, Colorf backgroundColor, Texture2D backgroundTex, TileMode backgroundTileMode )
    {
        this( isHeavyWeight, false, width, height, backgroundColor, backgroundTex, backgroundTileMode );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public Panel( boolean isHeavyWeight, float width, float height, Colorf backgroundColor, Texture2D backgroundTex )
    {
        this( isHeavyWeight,  width, height, backgroundColor, backgroundTex, TileMode.TILE_BOTH );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public Panel( boolean isHeavyWeight, float width, float height, Colorf backgroundColor )
    {
        this( isHeavyWeight, width, height, backgroundColor, (Texture2D)null );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public Panel( boolean isHeavyWeight, float width, float height, Texture2D backgroundTex )
    {
        this( isHeavyWeight, width, height, null, backgroundTex );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public Panel( boolean isHeavyWeight, float width, float height, String backgroundTex )
    {
        this( isHeavyWeight, width, height, null, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public Panel( boolean isHeavyWeight, float width, float height )
    {
        this( isHeavyWeight, width, height, null, (Texture2D)null );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public Panel( float width, float height, Colorf backgroundColor, Texture2D backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, backgroundTex );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public Panel( float width, float height, Colorf backgroundColor, String backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public Panel( float width, float height, Colorf backgroundColor )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, (Texture2D)null );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public Panel( float width, float height, Texture2D backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, backgroundTex );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public Panel( float width, float height, String backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public Panel( float width, float height )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, (Texture2D)null );
    }
    
    /**
     * Creates a new Panel with the given width and height.<br>
     * The Panel will have the background defined in the current theme.<br>
     * If scrollable is true, a {@link ScrollPanel} will be created and returned.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param scrollable if true, a {@link ScrollPanel} will be created and returned
     * 
     * @return a {@link Panel} or {@link ScrollPanel} (depending on the scrollable parameter).
     */
    public static final Panel createContentPane( float width, float height, boolean scrollable )
    {
        if ( scrollable )
            return ( new ScrollPanel( false, width, height, HUD.getTheme().getContentPaneBackgroundColor(), HUD.getTheme().getContentPaneBackgroundTexture(), TileMode.TILE_BOTH ) );
        
        return ( new Panel( false, width, height, HUD.getTheme().getContentPaneBackgroundColor(), HUD.getTheme().getContentPaneBackgroundTexture(), TileMode.TILE_BOTH ) );
    }
    
    /**
     * Creates a new non scrollable Panel with the given width and height.<br>
     * The Panel will have the background defined in the current theme.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * 
     * @return a {@link Panel}.
     */
    public static Panel createContentPane( float width, float height )
    {
        return ( createContentPane( width, height, false ) );
    }
}
