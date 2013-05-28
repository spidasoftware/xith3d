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
package org.xith3d.ui.hud.menusystem;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.hud.widgets.Button;
import org.xith3d.ui.hud.widgets.Panel;

/**
 * A MenuSystemWidget is a Widget, that handles the visualization part of
 * a {@link MenuGroup}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class MenuGroupWidget extends Panel
{
    public abstract WidgetContainer getAccessorPanel();
    
    public abstract WidgetContainer getMenuPanel();
    
    public abstract Button addAccessorWidget( String caption, Button.Description buttonDesc );
    
    public abstract void addMenu( MenuSystem menuSystem, String caption, Menu menu );
    
    public abstract void setMenuVisible( Menu menu, boolean visible );
    
    public abstract boolean isMenuVisible( Menu menu );
    
    public abstract void setMenuGroup( MenuGroup menuGroup );
    
    public abstract MenuGroup getMenuGroup();
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public MenuGroupWidget( boolean isHeavyWeight, float width, float height, Colorf backgroundColor, Texture2D backgroundTex )
    {
        super( isHeavyWeight, false, width, height, backgroundColor, backgroundTex, TileMode.TILE_BOTH );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public MenuGroupWidget( boolean isHeavyWeight, float width, float height, Colorf backgroundColor )
    {
        this( isHeavyWeight, width, height, backgroundColor, (Texture2D)null );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public MenuGroupWidget( boolean isHeavyWeight, float width, float height, Texture2D backgroundTex )
    {
        this( isHeavyWeight, width, height, null, backgroundTex );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public MenuGroupWidget( boolean isHeavyWeight, float width, float height, String backgroundTex )
    {
        this( isHeavyWeight, width, height, null, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public MenuGroupWidget( boolean isHeavyWeight, float width, float height )
    {
        this( isHeavyWeight, width, height, null, (Texture2D)null );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public MenuGroupWidget( float width, float height, Colorf backgroundColor, Texture2D backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, backgroundTex );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public MenuGroupWidget( float width, float height, Colorf backgroundColor, String backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public MenuGroupWidget( float width, float height, Colorf backgroundColor )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, (Texture2D)null );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public MenuGroupWidget( float width, float height, Texture2D backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, backgroundTex );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public MenuGroupWidget( float width, float height, String backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new MenuGroupWidget with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public MenuGroupWidget( float width, float height )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, (Texture2D)null );
    }
}
