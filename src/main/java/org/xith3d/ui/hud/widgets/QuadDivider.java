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

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.QuadDividerListener;
import org.xith3d.ui.hud.listeners.WidgetLocationListener;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;

/**
 * The QuadDivider is used to resize four Widgets in
 * north-west, north-east, south-west and south-east.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class QuadDivider extends Widget
{
    public static final float DIVIDER_WIDTH = 10f;
    
    public static enum QuadPanels
    {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT,
        ;
    }
    
    private class EventEngine extends ScheduledOperationImpl implements WidgetLocationListener
    {
        private boolean eventBlocked = false;
        
        public void onWidgetDragStarted( Widget widget )
        {
        }
        
        public void onWidgetDragStopped( Widget widget )
        {
        }
        
        public void onWidgetLocationChanged( Widget widget, float oldLeft, float oldTop, float newLeft, float newTop )
        {
            if ( eventBlocked )
                return;
            
            eventBlocked = true;
            if ( ( widget == horizontalDivider ) && ( widget.getLeft() != 0f ) )
                widget.setLocation( 0f, widget.getTop() );
            if ( ( widget == verticalDivider ) && ( widget.getTop() != 0f ) )
                widget.setLocation( widget.getLeft(), 0f );
            
            eventBlocked = false;
            
            final HUD hud = QuadDivider.this.getHUD();
            
            if ( hud != null )
            {
                hud.getOperationScheduler().scheduleOperation( this );
            }
        }
        
        public void update( long gameTime, long frameTime, TimingMode timingMode )
        {
            updatePanelSizes();
            
            for ( int i = 0; i < listeners.size(); i++ )
            {
                listeners.get( i ).onDividerResized( QuadDivider.this, leftWidth, rightWidth, topHeight, bottomHeight );
            }
        }
        
        public EventEngine()
        {
            super( false );
        }
    }
    
    private Colorf earlyDivColor;
    
    private float earlyVertDivLeft = -1f;
    private float earlyHorizDivTop = -1f;
    
    private Image horizontalDivider;
    private Image verticalDivider;
    private EventEngine eventEngine = new EventEngine();
    
    private float leftWidth = -1f;
    private float rightWidth = -1f;
    private float topHeight = -1f;
    private float bottomHeight = -1f;
    
    private float rightLeft = -1f;
    private float bottomTop = -1f;
    
    private final ArrayList< QuadDividerListener > listeners = new ArrayList< QuadDividerListener >( 1 );
    
    protected void updatePanelSizes()
    {
        final float horizDivTop;
        final float vertDivLeft;
        if ( isInitialized() )
        {
            horizDivTop = horizontalDivider.getTop();
            vertDivLeft = verticalDivider.getLeft();
        }
        else
        {
            if ( earlyHorizDivTop < 0f )
                horizDivTop = ( getHeight() / 2f ) - ( DIVIDER_WIDTH / 2f );
            else
                horizDivTop = earlyHorizDivTop;
            
            if ( earlyVertDivLeft < 0f )
                vertDivLeft = ( getWidth() / 2f ) - ( DIVIDER_WIDTH / 2f );
            else
                vertDivLeft = earlyVertDivLeft;
        }
        
        leftWidth = vertDivLeft;
        rightWidth = getWidth() - ( vertDivLeft + DIVIDER_WIDTH );
        topHeight = horizDivTop;
        bottomHeight = getHeight() - ( horizDivTop + DIVIDER_WIDTH );
        
        rightLeft = vertDivLeft + DIVIDER_WIDTH;
        bottomTop = horizDivTop + DIVIDER_WIDTH;
    }
    
    public void setDividerColor( Colorf color )
    {
        if ( isInitialized() )
        {
            horizontalDivider.setColor( color );
            verticalDivider.setColor( color );
        }
        else
        {
            earlyDivColor = color;
        }
    }
    
    /**
     * Sets the left-position of the vertical diviver.
     * 
     * @param vertLeft
     */
    public void setVerticalDividerLeft( float vertLeft )
    {
        if ( !isInitialized() )
        {
            earlyVertDivLeft = vertLeft;
        }
        else
        {
            verticalDivider.setLocation( vertLeft, 0f );
        }
        
        updatePanelSizes();
    }
    
    /**
     * Sets the top-position of the horizontal divider.
     * 
     * @param horizTop
     */
    public void setHorizontalDividerTop( float horizTop )
    {
        if ( !isInitialized() )
        {
            earlyHorizDivTop = horizTop;
        }
        else
        {
            horizontalDivider.setLocation( 0f, horizTop );
        }
        
        updatePanelSizes();
    }
    
    /**
     * @return the width of the left-hand panels.
     */
    public final float getLeftWidth()
    {
        return ( leftWidth );
    }
    
    /**
     * @return the width of the right-hand panels.
     */
    public final float getRightWidth()
    {
        return ( rightWidth );
    }
    
    /**
     * @return the height of the top panels.
     */
    public final float getTopHeight()
    {
        return ( topHeight );
    }
    
    /**
     * @return the height of the bottom panels.
     */
    public final float getBottomHeight()
    {
        return ( bottomHeight );
    }
    
    /**
     * @return the left-position of the right-hand panels.
     */
    public final float getRightLeft()
    {
        return ( rightLeft );
    }
    
    /**
     * @return the top-position of the bottom panels.
     */
    public final float getBottomTop()
    {
        return ( bottomTop );
    }
    
    /**
     * Returns a constant indicating the "panel" under the mouse cursor.
     * 
     * @param canvasX
     * @param canvasY
     * 
     * @return the "panel". (or null, if the widget has not yet been initialized or the mouse is not over one of the panels)
     */
    public QuadPanels getPanelUnderMouse( int canvasX, int canvasY )
    {
        if ( getHUD() == null )
            return ( null );
        
        Point2f p = Point2f.fromPool();
        
        getLocationPixels2HUD_( canvasX, canvasY, p );
        
        QuadPanels qp = null;
        if ( ( p.getX() >= 0f ) && ( p.getX() <= getLeftWidth() ) )
        {
            if ( ( p.getY() >= 0f ) && ( p.getY() <= getTopHeight() ) )
            {
                qp = QuadPanels.UPPER_LEFT;
            }
            else if ( ( p.getY() >= getBottomTop() ) && ( p.getY() <= getHeight() ) )
            {
                qp = QuadPanels.LOWER_LEFT;
            }
        }
        else if ( ( p.getX() >= getRightLeft() ) && ( p.getX() <= getWidth() ) )
        {
            if ( ( p.getY() >= 0f ) && ( p.getY() <= getTopHeight() ) )
            {
                qp = QuadPanels.UPPER_RIGHT;
            }
            else if ( ( p.getY() >= getBottomTop() ) && ( p.getY() <= getHeight() ) )
            {
                qp = QuadPanels.LOWER_RIGHT;
            }
        }
        
        Point2f.toPool( p );
        
        return ( qp );
    }
    
    /**
     * Adds a new QuadDividerListener.
     * 
     * @param l
     */
    public void addQuadDividerListener( QuadDividerListener l )
    {
        listeners.add( l );
    }
    
    /**
     * Removes a QuadDividerListener.
     * 
     * @param l
     */
    public void removeQuadDividerListener( QuadDividerListener l )
    {
        listeners.remove( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public HUDPickResult pick( int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        final Tuple2f locP = Tuple2f.fromPool();
        getLocationPixels2HUD_( canvasX, canvasY, locP );
        float pickXHUD = locP.getX();
        float pickYHUD = locP.getY();
        Tuple2f.toPool( locP );
        
        getWidgetAssembler().pick( canvasX, canvasY, pickXHUD, pickYHUD, pickReason, button, when, meta, flags );
        
        return ( null );
    }
    
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        texCanvas.getImage().clear( Colorf.BLACK_TRANSPARENT );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        this.horizontalDivider = new Image( getWidth(), DIVIDER_WIDTH, earlyDivColor );
        this.verticalDivider = new Image( DIVIDER_WIDTH, getHeight(), earlyDivColor );
        earlyDivColor = null;
        
        horizontalDivider.setDraggable( true );
        verticalDivider.setDraggable( true );
        
        horizontalDivider.addLocationListener( eventEngine );
        verticalDivider.addLocationListener( eventEngine );
        
        if ( earlyHorizDivTop < 0f )
            earlyHorizDivTop = ( getHeight() / 2f ) - ( DIVIDER_WIDTH / 2f );
        if ( earlyVertDivLeft < 0f )
            earlyVertDivLeft = ( getWidth() / 2f ) - ( DIVIDER_WIDTH / 2f );
        
        getWidgetAssembler().addWidget( horizontalDivider, 0f, earlyHorizDivTop );
        getWidgetAssembler().addWidget( verticalDivider, earlyVertDivLeft, 0f );
        
        updatePanelSizes();
    }
    
    public QuadDivider( float width, float height, Colorf dividerColor )
    {
        super( true, true, width, height );
        
        this.setFocussable( false );
        
        earlyDivColor = dividerColor;
        
        updatePanelSizes();
    }
    
    public QuadDivider( float width, float height )
    {
        this( width, height, Colorf.PINK );
    }
}
