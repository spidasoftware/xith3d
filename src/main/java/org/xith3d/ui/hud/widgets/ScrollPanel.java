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

import java.util.ArrayList;

import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.ui.hud.listeners.ContainerScrollListener;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.ScrollHandler;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.hud.widgets.Scrollbar.Direction;

public class ScrollPanel extends Panel
{
    private final ScrollHandler scrollHandler;
    
    private final ArrayList<ContainerScrollListener> scrollListeners = new ArrayList<ContainerScrollListener>();
    
    /**
     * Adds a {@link ContainerScrollListener} to the list of notified objects
     * when the children-offset has changed.
     * 
     * @param l
     */
    public final void addContainerScrollListener( ContainerScrollListener l )
    {
        scrollListeners.add( l );
    }
    
    /**
     * Removes a {@link ContainerScrollListener} from the list of notified objects
     * when the children-offset has changed.
     * 
     * @param l
     */
    public final void removeContainerScrollListener( ContainerScrollListener l )
    {
        scrollListeners.add( l );
    }
    
    public void setLineHeight( float lineHeight )
    {
        scrollHandler.setLineHeight( (int)lineHeight );
    }
    
    public final float getLineHeight()
    {
        return ( scrollHandler.getLineHeight() );
    }
    
    /**
     * Sets the display offset for the child-Widgets.
     * 
     * @param changeX
     * @param offsetX the new offset
     * @param changeY
     * @param offsetY the new offset
     */
    private void setChildrenOffset( boolean changeX, int offsetX, boolean changeY, int offsetY )
    {
        if ( getHUD() == null )
            return;
        
        TransformGroup childrenTG = (TransformGroup)getSGGroup();
        
        Matrix4f m = childrenTG.getTransform().getMatrix4f();
        float tx = m.m03();
        float ty = m.m13();
        
        Dim2i buffer2 = Dim2i.fromPool();
        getSizeHUD2Pixels( offsetX, offsetY, buffer2 );
        
        if ( changeX )
        {
            childrenOffset_PX.setX( buffer2.getWidth() );
            childrenOffset_HUD.setX( offsetX );
        }
        
        if ( changeY )
        {
            childrenOffset_PX.setY( buffer2.getHeight() );
            childrenOffset_HUD.setY( offsetY );
        }
        
        Dim2i.toPool( buffer2 );
        
        Dim2f buffer = Dim2f.fromPool();
        getSizeHUD2SG( offsetX, offsetY, buffer );
        
        if ( changeX )
        {
            tx = buffer.getWidth();
        }
        
        if ( changeY )
        {
            ty = buffer.getHeight();
        }
        
        Dim2f.toPool( buffer );
        
        m.m03( tx );
        m.m13( ty );
        childrenTG.updateTransform();
        
        setTextureDirty();
        
        for ( int i = 0 ; i < scrollListeners.size(); i++ )
        {
            scrollListeners.get( i ).onContainerScrolled( this, offsetX, offsetY );
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
     * {@inheritDoc}
     */
    @Override
    protected GroupNode createChildrenGroup()
    {
        return ( new TransformGroup() );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     * @param backgroundTileMode
     */
    public ScrollPanel( boolean isHeavyWeight, float width, float height, Colorf backgroundColor, Texture2D backgroundTex, TileMode backgroundTileMode )
    {
        super( isHeavyWeight, true, width, height, backgroundColor, backgroundTex, backgroundTileMode );
        
        this.scrollHandler = new ScrollHandler( this, getWidgetAssembler(), true, true )
        {
            @Override
            public void onScrolled( Direction direction, int newValue )
            {
                if ( direction == Direction.VERTICAL )
                    setChildrenOffset( false, 0, true, newValue );
                else
                    setChildrenOffset( true, -newValue, false, 0 );
            }
        };
        
        setLineHeight( 16f );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public ScrollPanel( boolean isHeavyWeight, float width, float height, Colorf backgroundColor, Texture2D backgroundTex )
    {
        this( isHeavyWeight, width, height, backgroundColor, backgroundTex, TileMode.TILE_BOTH );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public ScrollPanel( boolean isHeavyWeight, float width, float height, Colorf backgroundColor, String backgroundTex )
    {
        this( isHeavyWeight, width, height, backgroundColor, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public ScrollPanel( boolean isHeavyWeight, float width, float height, Colorf backgroundColor )
    {
        this( isHeavyWeight, width, height, backgroundColor, (Texture2D)null );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public ScrollPanel( boolean isHeavyWeight, float width, float height, Texture2D backgroundTex )
    {
        this( isHeavyWeight, width, height, null, backgroundTex );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public ScrollPanel( boolean isHeavyWeight, float width, float height, String backgroundTex )
    {
        this( isHeavyWeight, width, height, null, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public ScrollPanel( boolean isHeavyWeight, float width, float height )
    {
        this( isHeavyWeight, width, height, null, (Texture2D)null );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public ScrollPanel( float width, float height, Colorf backgroundColor, Texture2D backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, backgroundTex );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     */
    public ScrollPanel( float width, float height, Colorf backgroundColor, String backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public ScrollPanel( float width, float height, Colorf backgroundColor )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, backgroundColor, (Texture2D)null );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public ScrollPanel( float width, float height, Texture2D backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, backgroundTex );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTex the background texture
     */
    public ScrollPanel( float width, float height, String backgroundTex )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, HUDTextureUtils.getTexture( backgroundTex, true ) );
    }
    
    /**
     * Creates a new Panel with the given width and height.
     * The Panel will can a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     */
    public ScrollPanel( float width, float height )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, null, (Texture2D)null );
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
    public static final ScrollPanel createContentPane( float width, float height )
    {
        return ( (ScrollPanel)createContentPane( width, height, true ) );
    }
}
