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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point2i;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple2i;
import org.openmali.vecmath2.Vector2f;
import org.openmali.vecmath2.Vector2i;
import org.xith3d.render.ScissorRect;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.__HUD_PrivilegedAccess;
import org.xith3d.ui.hud.HUD.FocusMoveDirection;
import org.xith3d.ui.hud.layout.LayoutManager;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.LocalZIndexComparator;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;

/**
 * A WidgetContainer is a Widget, that can hold arbitrary Widgets.
 * It can have it's own coordinate system and the contained Widget's
 * transformations are relative to it.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class WidgetContainer extends BackgroundSettableWidget implements PaddingSettable
{
    private static final LocalZIndexComparator Z_INDEX_COMPARATOR = new LocalZIndexComparator();
    
    private final GroupNode childrenGroup;
    
    private Tuple2f resolution = null;
    protected final Vector2f childrenOffset_HUD = new Vector2f( 0f, 0f );
    protected final Vector2i childrenOffset_PX = new Vector2i( 0, 0 );
    
    private Window parentWindow = null;
    
    private final ArrayList<Widget> widgets = new ArrayList<Widget>();
    private boolean widgetsSorted = true;
    private Boolean hasOverlappingWidgets = null;
    
    private Widget topMostWidget = null;
    private Widget currentHoveredWidget = null;
    private Widget currentFocusedWidget = null;
    
    private final ArrayList<HUDPickResult> pickedWidgets = new ArrayList<HUDPickResult>();
    
    private LayoutManager layoutManager = null;
    private boolean layoutDirty = false;
    
    private int paddingBottom = 0, paddingRight = 0, paddingTop = 0, paddingLeft = 0;
    
    /**
     * @return the scenegraph Group to add children to
     */
    protected final GroupNode getSGGroup()
    {
        return ( childrenGroup );
    }
    
    void setContentPaneOf( Window window )
    {
        this.parentWindow = window;
    }
    
    /**
     * Gets, if this WidgetContainer is a content pane of a Window.
     * 
     * @return true, if this container is the contentpane of a Window.
     */
    public final boolean isContentPane()
    {
        return ( parentWindow != null );
    }
    
    /**
     * Gets, the Window, of which this is the content pane.
     * 
     * @return the parent Window.
     */
    @Override
    public final Window getParentWindow()
    {
        if ( parentWindow != null )
            return ( parentWindow );
        
        if ( getContainer() == null )
            return ( null );
        
        return ( getContainer().getParentWindow() );
    }
    
    /**
     * @return a height that's visually equal to the given width
     * 
     * @param height the height to calculate a visually equal width
     */
    protected final float getEqualHeight( float width )
    {
        float resAspect = getResAspect();
        if ( resAspect == 0f )
            return ( 0f );
        
        return ( width * this.getContentAspect() / resAspect );
    }
    
    /**
     * @return a width that's visually equal to the given height
     * 
     * @param height the height to calculate a visually equal height
     */
    protected final float getEqualWidth( float height )
    {
        float contentAspect = getContentAspect();
        if ( contentAspect == 0f )
            return ( 0f );
        
        return ( height * getResAspect() / contentAspect );
    }
    
    /**
     * Calculates HUD size from these pixel-values.
     * 
     * @param w the canvas-x-value to transform
     * @param h the canvas-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizePixels2HUD( int w, int h, Dim2f_ buffer )
    {
        if ( getContainer() != null )
            getContainer().getSizePixels2HUD( w, h, buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getSizePixels2HUD( w, h, buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        buffer.scale( transformWidth_Pixels2HUD, transformHeight_Pixels2HUD );
        
        return ( buffer );
    }
    
    /**
     * Calculates HUD location from these pixel-values.
     * 
     * @param x the canvas-x-value to transform
     * @param y the canvas-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationPixels2HUD( int x, int y, Tuple2f_ buffer )
    {
        x -= getContentLeftPX();
        y -= getContentTopPX();
        
        if ( getContainer() != null )
        {
            getContainer().getLocationPixels2HUD( x, y, buffer );
        }
        else if ( getHUD() != null )
        {
            /*
            if ( isContentPane() )
            {
                Window window = getParentWindow();
                
                x -= window.getLeft();
                y -= window.getTop() + window.getHeaderHeight();
                
                if ( window.getBorder() != null )
                {
                    x -= window.getBorder().getLeftWidth();
                    y -= window.getBorder().getTopHeight();
                }
            }
            */
            
            getHUD().getCoordinatesConverter().getLocationPixels2HUD( x, y, buffer );
        }
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        if ( getContentWidthPX() == 0 )
        {
            buffer.setX( 0f );
        }
        else
        {
            buffer.subX( getLeft() );
            buffer.mulX( getResX() / getContentWidth() );
        }
        
        if ( getContentHeightPX() == 0 )
        {
            buffer.setY( 0f );
        }
        else
        {
            buffer.subY( getTop() );
            buffer.mulY( ( getResY() / getContentHeight() ) );
        }
        
        buffer.add( -childrenOffset_HUD.getX(), childrenOffset_HUD.getY() );
        
        return ( buffer );
    }
    
    /**
     * Calculates pixel size from these HUD-values.
     * 
     * @param w the HUD-x-value to transform
     * @param h the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2i_ extends Dim2i> Dim2i_ getSizeHUD2Pixels( float w, float h, Dim2i_ buffer )
    {
        if ( getHUD() == null )
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        if ( ( transformWidth_Pixels2HUD == 0f ) || ( transformHeight_Pixels2HUD == 0f ) )
        {
            w = 0f;
            h = 0f;
        }
        else
        {
            w /= transformWidth_Pixels2HUD;
            h /= transformHeight_Pixels2HUD;
        }
        
        if ( getContainer() != null )
            getContainer().getSizeHUD2Pixels( w, h, buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getSizeHUD2Pixels( w, h, buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        return ( buffer );
    }
    
    /**
     * Calculates container relative pixel location from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Tuple2i_ extends Tuple2i> Tuple2i_ getRelLocationHUD2Pixels( float x, float y, Tuple2i_ buffer )
    {
        if ( getResX() == 0f )
            x = 0f;
        else
            x *= ( getContentWidth() / getResX() );
        if ( getResY() == 0f )
            y = 0f;
        else
            y *= ( getContentHeight() / getResY() );
        
        if ( getContainer() != null )
            getContainer().getRelLocationHUD2Pixels( x, y, buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getLocationHUD2Pixels( x, y, buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        return ( buffer );
    }
    
    /**
     * Calculates pixel location from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Tuple2i_ extends Tuple2i> Tuple2i_ getLocationHUD2Pixels( float x, float y, Tuple2i_ buffer )
    {
        if ( getContainer() != null )
            getContainer().getLocationHUD2Pixels( getLeft(), getTop(), buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getLocationHUD2Pixels( getLeft(), getTop(), buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        final float left = buffer.getX();
        final float top = buffer.getY();
        
        //x += getChildrenOffsetX();
        //y += getChildrenOffsetY();
        
        if ( getBorder() != null )
            x += getBorder().getLeftWidth();
        x += getPaddingLeft();
        if ( getResX() == 0f )
            x = 0f;
        else
            x *= ( getContentWidth() / getResX() );
        if ( getBorder() != null )
            y += getBorder().getTopHeight();
        y += getPaddingTop();
        if ( getResY() == 0f )
            y = 0f;
        else
            y *= ( getContentHeight() / getResY() );
        
        Dim2i dim = Dim2i.fromPool();
        if ( getContainer() != null )
            getContainer().getSizeHUD2Pixels( x, y, dim );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getSizeHUD2Pixels( x, y, dim );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        buffer.set( dim.getWidth(), dim.getHeight() );
        Dim2i.toPool( dim );
        buffer.addX( (int)left );
        buffer.addY( (int)top );
        
        return ( buffer );
    }
    
    /**
     * Calculates scenegraph width and height from these HUD-values.
     * 
     * @param w the HUD-x-value to transform
     * @param h the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizeHUD2SG( float w, float h, Dim2f_ buffer )
    {
        if ( getHUD() == null )
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        if ( ( transformWidth_Pixels2HUD == 0f ) || ( transformHeight_Pixels2HUD == 0f ) )
        {
            w = 0f;
            h = 0f;
        }
        else
        {
            w /= transformWidth_Pixels2HUD;
            h /= transformHeight_Pixels2HUD;
        }
        
        if ( getContainer() != null )
            getContainer().getSizeHUD2SG( w, h, buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getSizeHUD2SG( w, h, buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        return ( buffer );
    }
    
    /**
     * Calculates scenegraph location from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationHUD2SG( float x, float y, Tuple2f_ buffer )
    {
        if ( getResX() == 0f )
            x = 0f;
        else
            x *= ( getContentWidth() / getResX() );
        if ( getResY() == 0f )
            y = 0f;
        else
            y *= ( getContentHeight() / getResY() );
        x += getLeft();
        y += getTop();
        if ( getBorder() != null )
        {
            x += getBorder().getLeftWidth();
            y += getBorder().getTopHeight();
        }
        x += getPaddingLeft();
        y += getPaddingTop();
        
        if ( getContainer() != null )
            getContainer().getLocationHUD2SG( x, y, buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getLocationHUD2SG( x, y, buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        buffer.subX( ( (TransformGroup)getSGNode() ).getTransform().getMatrix4f().m03() );
        buffer.subY( ( (TransformGroup)getSGNode() ).getTransform().getMatrix4f().m13() );
        
        return ( buffer );
    }
    
    /**
     * Calculates HUD size from these scenegraph-values.
     * 
     * @param w the scenegraph-x-value to transform
     * @param h the scenegraph-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizeSG2HUD( float w, float h, Dim2f_ buffer )
    {
        if ( getContainer() != null )
            getContainer().getSizeSG2HUD( w, h, buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getSizeSG2HUD( w, h, buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        if ( getContentWidthPX() == 0 )
            buffer.setWidth( 0f );
        else
            buffer.scale( getResX() / getContentWidth(), 1f );
        if ( getContentHeightPX() == 0 )
            buffer.setHeight( 0f );
        else
            buffer.scale( 1f, getResY() / getContentHeight() );
        
        return ( buffer );
    }
    
    /**
     * Calculates HUD location from these scenegraph-values.
     * 
     * @param x the scenegraph-x-value to transform
     * @param y the scenegraph-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationSG2HUD( float x, float y, Tuple2f_ buffer )
    {
        x += ( (TransformGroup)getSGNode() ).getTransform().getMatrix4f().m03();
        y += ( (TransformGroup)getSGNode() ).getTransform().getMatrix4f().m13();
        
        if ( getContainer() != null )
            getContainer().getLocationSG2HUD( x, y, buffer );
        else if ( getHUD() != null )
            getHUD().getCoordinatesConverter().getLocationSG2HUD( x, y, buffer );
        else
            throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
        
        buffer.subX( getLeft() );
        buffer.subY( getTop() );
        if ( getBorder() != null )
        {
            buffer.subX( getBorder().getLeftWidth() );
            buffer.subY( getBorder().getTopHeight() );
        }
        buffer.subX( getPaddingLeft() );
        buffer.subY( getPaddingTop() );
        
        if ( getResX() == 0f )
            buffer.setX( 0f );
        else
            buffer.divX( getContentWidth() / getResX() );
        if ( getResY() == 0f )
            buffer.setY( 0f );
        else
            buffer.divY( getContentHeight() / getResY() );
        
        return ( buffer );
    }
    
    /**
     * Retrieves the size these pixels have on this WidgetContainer.
     * 
     * @param x the x-count of pixels 
     * @param y the y-count of pixels
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizeOfPixels( int x, int y, Dim2f_ buffer )
    {
        getSizeSG2HUD( x, y, buffer );
        
        return ( buffer );
    }
    
    /**
     * Sets the container's resolution.<br>
     * Set one of the values to -1 to use not custom resolution.
     * 
     * @param resX
     * @param resY
     */
    public void setResolution( float resX, float resY )
    {
        if ( ( resX < 0f ) || ( resY < 0f ) )
        {
            if ( this.resolution == null )
                return;
            
            this.resolution = null;
        }
        
        if ( this.resolution == null )
            this.resolution = new Tuple2f( resX, resY );
        else
            this.resolution.set( resX, resY );
        
        update();
    }
    
    /**
     * @return the x-resolution of the WidgetContainer.
     */
    public final float getResX()
    {
        if ( resolution == null )
        {
            if ( getHUD() == null )
            {
                // This is a fallback!
                
                float resX = getWidth();
                if ( getBorder() != null )
                    resX -= getBorder().getLeftWidth() - getBorder().getRightWidth();
                resX -= getPaddingLeft() + getPaddingRight();
                
                return ( resX );
            }
            
            return ( getContentWidth() );
        }
        
        return ( resolution.getX() );
    }
    
    /**
     * @return the y-resolution of the WidgetContainer.
     */
    public final float getResY()
    {
        if ( resolution == null )
        {
            if ( getHUD() == null )
            {
                // This is a fallback!
                
                float resY = getHeight();
                if ( getBorder() != null )
                    resY -= getBorder().getTopHeight() - getBorder().getBottomHeight();
                resY -= getPaddingTop() + getPaddingBottom();
                
                return ( resY );
            }
            
            return ( getContentHeight() );
        }
        
        return ( resolution.getY() );
    }
    
    /**
     * @return this WidgetContainer's resolution.
     * By default this is equal to width and height.
     */
    public final Tuple2f getResolution()
    {
        return ( new Tuple2f( getResX(), getResY() ) );
    }
    
    /**
     * @return the aspect ratio of the resolution of the WidgetContainer.
     */
    public final float getResAspect()
    {
        if ( getResY() == 0f )
            return ( 0f );
        
        return ( getResX() / getResY() );
    }
    
    /**
     * Returns whether a custom resolution is defined on the widget container.
     * 
     * @return whether a custom resolution is defined on the widget container.
     */
    public final boolean hasCustomResolution()
    {
        return ( resolution != null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        layoutDirty = true;
        
        updateLight( true );
    }
    
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
        
        this.update();
        
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
     * Sets which LayoutManager to use for the child Widgets of this Container.
     * 
     * @param layout the new LayoutManager to use
     */
    public void setLayout( LayoutManager layout )
    {
        if ( this.layoutManager == layout )
        {
            return;
        }
        
        if ( this.layoutManager != null )
        {
            this.layoutManager.clear();
        }
        
        this.layoutManager = layout;
        
        if ( layoutManager != null )
        {
            layoutManager.clear();
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                layoutManager.addWidget( widgets.get( i ), null );
            }
            
            final HUD hud = getHUD();
            if ( ( layoutManager != null ) && layoutDirty && isVisible() && ( hud != null ) && hud.isVisible() && hud.isConnected() )
            {
                layoutManager.doLayout( this );
                layoutDirty = false;
            }
            else
            {
                layoutDirty = ( widgets.size() > 0 );
            }
        }
        else
        {
            layoutDirty = false;
        }
    }
    
    /**
     * @return the currently used LayoutManager
     */
    public final LayoutManager getLayout()
    {
        return ( layoutManager );
    }
    
    /**
     * 
     * @param child
     */
    void onChildMovedOrResized( Widget child )
    {
        this.hasOverlappingWidgets = null;
    }
    
    final boolean hasOverlappingWidgets()
    {
        if ( hasOverlappingWidgets == null )
        {
            hasOverlappingWidgets = Boolean.FALSE;
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                Widget w0 = widgets.get( i );
                
                for ( int j = i + 1; j < widgets.size(); j++ )
                {
                    Widget w1 = widgets.get( j );
                    
                    if ( w1.getLeft() + w1.getWidth() < w0.getLeft() )
                    {
                        // no overlapping!
                    }
                    else if ( w1.getTop() + w1.getHeight() < w0.getTop() )
                    {
                        // no overlapping!
                    }
                    else if ( w1.getLeft() > w0.getLeft() + w0.getWidth() )
                    {
                        // no overlapping!
                    }
                    else if ( w1.getTop() > w0.getTop() + w0.getHeight() )
                    {
                        // no overlapping!
                    }
                    else
                    {
                        hasOverlappingWidgets = Boolean.TRUE;
                        return ( true );
                    }
                }
            }
        }
        
        return ( hasOverlappingWidgets.booleanValue() );
    }
    
    protected void setZIndexSortingDirty()
    {
        this.widgetsSorted = false;
    }
    
    private void ensureWidgetsSortedByZIndex()
    {
        if ( widgetsSorted )
            return;
        
        Collections.sort( widgets, Z_INDEX_COMPARATOR );
        
        widgetsSorted = true;
    }
    
    /**
     * Adds a Widget to this container at the given location.
     * 
     * @param widget the Widget to add
     * @param locX the x-location to add the Widget at
     * @param locY the y-location to add the Widget at
     * @param zIndex the new Widget's z-index
     * @param constraints the contraints to use for this Widget in the LayoutManager
     */
    public Widget addWidget( Widget widget, float locX, float locY, int zIndex, Object constraints )
    {
        if ( ( widget.getContainer() != null ) || ( widget.getHUD() != null ) )
        {
            throw new Error( "This Widget is already added to the HUD." );
        }
        
        widgets.add( widget );
        widgetsSorted = false;
        
        final HUD hud = getHUD();
        
        widget.setLocation( locX, locY );
        widget.setZIndex( zIndex );
        widget.setContainer( this, null );
        if ( !widget.isHeavyWeight() )
        {
            //widget.setHostWidget( getHostWidget() );
            widget.setHostWidget( this );
            
            this.setTextureDirty();
        }
        widget.setHUD( hud );
        
        //if ( ( topMostWidget == null ) || ( topMostWidget.getSGZPosition() <= widget.getSGZPosition() ) )
        if ( ( topMostWidget == null ) || ( topMostWidget.getZIndex() <= widget.getZIndex() ) )
        {
            topMostWidget = widget;
        }
        
        if ( ( constraints != LayoutManager.IGNORED_BY_LAYOUT ) && ( layoutManager != null ) )
        {
            layoutManager.addWidget( widget, constraints );
            
            if ( isVisible() && ( hud != null ) && hud.isVisible() && hud.isConnected() )
            {
                layoutManager.doLayout( this );
                layoutDirty = false;
            }
            else
            {
                layoutDirty = true;
            }
        }
        
        this.hasOverlappingWidgets = null;
        
        return ( widget );
    }
    
    /**
     * Adds a Widget to this container at the given location.
     * 
     * @param widget the Widget to add
     * @param locX the x-location to add the widget at
     * @param locY the y-location to add the widget at
     * @param zIndex the new Widget's z-index
     */
    public final Widget addWidget( Widget widget, float locX, float locY, int zIndex )
    {
        return ( addWidget( widget, locX, locY, zIndex, null ) );
    }
    
    /**
     * Adds a Widget to this container at the given location.
     * 
     * @param widget the Widget to add
     * @param locX the x-location to add the Widget at
     * @param locY the y-location to add the Widget at
     * @param constraints the contraints to use for this Widget in the LayoutManager
     */
    public final Widget addWidget( Widget widget, float locX, float locY, Object constraints )
    {
        return ( addWidget( widget, locX, locY, widget.getZIndex(), constraints ) );
    }
    
    /**
     * Adds a Widget to this container at the given location.
     * 
     * @param widget the Widget to add
     * @param locX the x-location to add the widget at
     * @param locY the y-location to add the widget at
     */
    public final Widget addWidget( Widget widget, float locX, float locY )
    {
        return ( addWidget( widget, locX, locY, widget.getZIndex(), null ) );
    }
    
    /**
     * Adds a Widget to this container at the Widget's location.
     * 
     * @param widget the widget to add
     * @param constraints the contraints to use for this Widget in the LayoutManager
     */
    public final Widget addWidget( Widget widget, Object constraints )
    {
        return ( addWidget( widget, widget.getLeft(), widget.getTop(), widget.getZIndex(), constraints ) );
    }
    
    /**
     * Adds a Widget to this container at the Widget's location.
     * 
     * @param widget the widget to add
     */
    public final Widget addWidget( Widget widget )
    {
        return ( addWidget( widget, widget.getLeft(), widget.getTop(), widget.getZIndex(), null ) );
    }
    
    /**
     * Adds a Widget to this container at the center.
     * 
     * @param widget the widget to add (centered)
     * @param zIndex the new Widget's z-index
     */
    public final Widget addWidgetCentered( Widget widget, int zIndex )
    {
        float posUpperLeftX;
        float posUpperLeftY;
        
        if ( ( getHUD() == null ) && !hasCustomResolution() )
        {
            posUpperLeftX = Math.round( ( this.getWidth() - widget.getWidth() ) / 2.0f );
            posUpperLeftY = Math.round( ( this.getHeight() - widget.getHeight() ) / 2.0f );
        }
        else
        {
            posUpperLeftX = Math.round( ( this.getResX() - widget.getWidth() ) / 2.0f );
            posUpperLeftY = Math.round( ( this.getResY() - widget.getHeight() ) / 2.0f );
        }
        
        return ( addWidget( widget, posUpperLeftX, posUpperLeftY, zIndex ) );
    }
    
    /**
     * Adds a Widget to this container at the center.
     * 
     * @param widget the widget to add (centered)
     */
    public final Widget addWidgetCentered( Widget widget )
    {
        return ( addWidgetCentered( widget, widget.getZIndex() ) );
    }
    
    /**
     * Removes a Widget from this container.
     * 
     * @param widget the widget to remove
     */
    public void removeWidget( Widget widget )
    {
        if ( widget.getContainer() != this )
            throw new Error( "the given Widget is not held in this Container." );
        
        HUD hud = getHUD();
        
        widgets.remove( widget );
        widget.setContainer( null, null );
        widget.setHUD( null );
        widget.setHostWidget( null );
        
        if ( topMostWidget == widget )
            topMostWidget = null;
        
        if ( layoutManager != null )
        {
            layoutManager.removeWidget( widget );
            
            if ( isVisible() && ( hud != null ) && hud.isVisible() && hud.isConnected() )
            {
                layoutManager.doLayout( this );
                layoutDirty = false;
            }
            else
            {
                layoutDirty = true;
            }
        }
        
        this.hasOverlappingWidgets = null;
        
        /*
        for ( int i = 0; i < containerListeners.size(); i++ )
        {
            containerListeners.get( i ).onWidgetDetachedFromContainer( this, widget );
        }
        */
    }
    
    /**
     * Removes all Widgets from this WidgetContainer.
     */
    public void clear()
    {
        /*
        Widget[] tmpWidgets = new Widget[ widgets.size() ];
        
        int i = 0;
        for ( Widget widget: widgets )
        {
            tmpWidgets[ i++ ] = widget;
        }
        
        for ( i = 0; i < tmpWidgets.length; i++ )
        {
            removeWidget( tmpWidgets[ i ] );
        }
        */
        
        for ( int i = widgets.size() - 1; i >= 0; i-- )
        {
            removeWidget( widgets.get( i ) );
        }
    }
    
    /**
     * Gets the number of {@link Widget}s on this container.
     * 
     * @return the number of contained {@link Widget}s.
     */
    public final int getWidgetsCount()
    {
        return ( widgets.size() );
    }
    
    /**
     * Gets the index'th Widget on this Container.
     * The order may change depending on the z-index.
     * 
     * @param index
     * 
     * @return te index'th Widget.
     */
    public final Widget getWidget( int index )
    {
        ensureWidgetsSortedByZIndex();
        
        return ( widgets.get( index ) );
    }
    
    /**
     * @return a List of all Widgets contained by this WidgetContainer.
     * The returned Set is unmodifiable.
     */
    public final List< Widget > getWidgets()
    {
        return ( Collections.unmodifiableList( widgets ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onVisibilityChanged( boolean visible )
    {
        super.onVisibilityChanged( visible );
        
        if ( visible )
        {
            if ( ( layoutManager != null ) && layoutDirty && ( getHUD() != null ) && getHUD().isVisible() && getHUD().isConnected() )
            {
                layoutManager.doLayout( this );
                layoutDirty = false;
                
                /*
                for ( int i = 0; i < widgets.size(); i++ )
                {
                    widgets.get( i ).setVisible( widgets.get( i ).isVisible() );
                }
                */
            }
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                widgets.get( i ).update();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransparency( float transparency, boolean childrenToo )
    {
        super.setTransparency( transparency, childrenToo );
        
        if ( childrenToo )
        {
            final java.util.List< Widget > children = getWidgets();
            for ( int i = 0; i < children.size(); i++ )
            {
                final Widget child = children.get( i );
                
                if ( child instanceof WidgetContainer )
                    ( (WidgetContainer)child ).setTransparency( transparency, childrenToo );
                else
                    child.setTransparency( transparency );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected HUDPickResult pick( int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        if ( !pickConditionsMatch( pickReason ) )
            return ( null );
        
        final Tuple2f locP = Tuple2f.fromPool();
        getLocationPixels2HUD_( canvasX, canvasY, locP );
        float pickXHUD = locP.getX();
        float pickYHUD = locP.getY();
        Tuple2f.toPool( locP );
        
        final boolean isInternal = ( flags & HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL ) != 0;
        final boolean eventsSuppressed = ( flags & HUDPickResult.HUD_PICK_FLAG_EVENTS_SUPPRESSED ) != 0;
        
        HUDPickResult thisPick = super.pick( canvasX, canvasY, pickReason, button, when, meta, flags );
        
        // We don't need to further test the children, if this container hasn't been picked!
        if ( thisPick == null )
            return ( null );
        
        HUDPickResult topMost = thisPick;
        HUDPickResult tmpHPR = null;
        
        ensureWidgetsSortedByZIndex();
        pickedWidgets.clear();
        
        for ( int i = widgets.size() - 1; i >= 0; i-- )
        {
            final Widget widget = widgets.get( i );
            if ( widget.isVisible() && widget.isPickable() )
            {
                tmpHPR = widget.pick( canvasX, canvasY, pickReason, button, when, meta, flags );
                if ( tmpHPR != null )
                {
                    pickedWidgets.add( tmpHPR );
                    
                    if ( ( topMost == null ) || ( topMost.compareTo( tmpHPR ) <= 0 ) )
                    {
                        topMost = tmpHPR;
                    }
                    
                    if ( !hasOverlappingWidgets() )
                    {
                        break;
                    }
                }
            }
        }
        
        //if ( ( currentHoveredWidget != null ) && ( ( currentHoveredWidget != topMost.getWidget() ) || ( topMost == thisPick ) ) )
        if ( ( currentHoveredWidget != null ) && ( currentHoveredWidget != topMost.getWidget() ) )
        {
            // How can this be??? But we will check it to avoid problems...
            if ( currentHoveredWidget.getContainer() != null )
            {
                currentHoveredWidget.onMouseExited( true, false );
            }
            currentHoveredWidget = null;
        }
        
        for ( int i = 0; i < pickedWidgets.size(); i++ )
        {
            final HUDPickResult hpr = pickedWidgets.get( i );
            final Widget pickedWidget = hpr.getWidget();
            final boolean isTopMost = ( pickedWidget == topMost.getWidget() ); // && topMost.isLeafResult();
            boolean hasFocus = ( pickedWidget == currentFocusedWidget );
            
            float pickXHUD_ = pickXHUD - pickedWidget.getLeft();
            float pickYHUD_ = pickYHUD - pickedWidget.getTop();
            
            if ( isInternal && !eventsSuppressed )
            {
                switch ( pickReason )
                {
                    case BUTTON_PRESSED:
                        if ( isTopMost )
                        {
                            if ( currentFocusedWidget != pickedWidget )
                            {
                                focus( pickedWidget );
                                hasFocus = true;
                            }
                        }
                        
                        pickedWidget.onMouseButtonPressed( button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        
                        break;
                    
                    case BUTTON_RELEASED:
                        pickedWidget.onMouseButtonReleased( button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        
                        break;
                    
                    case MOUSE_MOVED:
                        pickedWidget.onMouseMoved( pickXHUD_, pickYHUD_, (int)meta, when, isTopMost, hasFocus );
                        
                        if ( ( currentHoveredWidget == null ) && isTopMost )
                        {
                            currentHoveredWidget = pickedWidget;
                            currentHoveredWidget.onMouseEntered( isTopMost, hasFocus );
                        }
                        break;
                    
                    case MOUSE_WHEEL_MOVED_UP:
                        pickedWidget.onMouseWheelMoved( +1, ( meta != 0L ), pickXHUD_, pickYHUD_, when, isTopMost );
                        break;
                    
                    case MOUSE_WHEEL_MOVED_DOWN:
                        pickedWidget.onMouseWheelMoved( -1, ( meta != 0L ), pickXHUD_, pickYHUD_, when, isTopMost );
                        break;
                }
            }
            
            if ( ( topMost != hpr ) && ( topMost != thisPick ) )
                HUDPickResult.toPool( hpr );
        }
        
        pickedWidgets.clear();
        
        if ( topMost != thisPick )
        {
            thisPick.setSubResult( topMost );
        }
        
        return ( thisPick );
    }
    
    /**
     * Focusses the given Widget (non thread-safe).
     * 
     * @param widget
     * 
     * @return the previously focussed Widget
     */
    public Widget focus( Widget widget )
    {
        final Widget pfw = currentFocusedWidget;
        
        if ( getContainer() != null )
            getContainer().focus( this );
        else
            __HUD_PrivilegedAccess.focus( getHUD(), widget );
        
        if ( ( pfw == widget ) || !widget.isFocussable() )
            return ( pfw );
        
        if ( ( currentFocusedWidget != null ) && ( currentFocusedWidget.getContainer() != null ) )
            currentFocusedWidget.onFocusLost();
        
        currentFocusedWidget = widget;
        currentFocusedWidget.onFocusGained();
        
        return ( pfw );
    }
    
    /**
     * @return the current focused Widget
     * 
     * @param getLeaf recursively searches for the focused leaf (Widget), if true
     */
    public final Widget getCurrentFocusedWidget( boolean getLeaf )
    {
        final Widget cfw = currentFocusedWidget;
        
        if ( getLeaf )
        {
            if ( ( cfw != null ) && ( cfw instanceof WidgetContainer ) )
            {
                final Widget cfw2 = ( (WidgetContainer)cfw ).getCurrentFocusedWidget( getLeaf );
                
                if ( cfw2 != null )
                    return ( cfw2 );
                
                return ( cfw );
            }
        }
        
        return ( cfw );
    }
    
    /**
     * @return the current focused Widget
     */
    public final Widget getCurrentFocusedWidget()
    {
        return ( currentFocusedWidget );
    }
    
    /**
     * @return the current hovered Widget
     * 
     * @param getLeaf recursively searches for the hovered leaf (Widget), if true
     */
    public final Widget getCurrentHoveredWidget( boolean getLeaf )
    {
        final Widget chw = currentHoveredWidget;
        
        if ( getLeaf )
        {
            if ( ( chw != null ) && ( chw instanceof WidgetContainer ) )
            {
                final Widget chw2 = ( (WidgetContainer)chw ).getCurrentHoveredWidget( getLeaf );
                
                if ( chw2 != null )
                    return ( chw2 );
                
                return ( chw );
            }
        }
        
        return ( chw );
    }
    
    protected final Widget getCurrentHoveredWidget()
    {
        return ( currentHoveredWidget );
    }
    
    protected final void resetCurrentHoveredWidget()
    {
        currentHoveredWidget = null;
    }
    
    /**
     * Moves the focus to the closest widget in the container
     * in the given direction.
     * 
     * @param direction
     * 
     * @return the newly focussed Widget.
     */
    Widget moveFocus( FocusMoveDirection direction )
    {
        if ( currentFocusedWidget == null )
            return ( currentFocusedWidget );
        
        if ( widgets.size() < 2 )
            return ( currentFocusedWidget );
        
        float currMidX = currentFocusedWidget.getLeft() + ( currentFocusedWidget.getWidth() / 2f );
        float currMidY = currentFocusedWidget.getTop() + ( currentFocusedWidget.getHeight() / 2f );
        
        float nextMidX = 0f;
        float nextMidY = 0f;
        
        Widget nextWidget = null;
        for ( int i = 0; i < widgets.size(); i++ )
        {
            final Widget widget = widgets.get( i );
            
            if ( !widget.isFocussable() )
                continue;
            
            if ( widget != currentFocusedWidget )
            {
                switch ( direction )
                {
                    case UP:
                    {
                        final float midY = widget.getTop() + ( widget.getHeight() / 2f);
                        if ( midY < currMidY )
                        {
                            if ( ( nextWidget == null ) || ( midY > nextMidY ) )
                            {
                                nextWidget = widget;
                                nextMidY = midY;
                            }
                        }
                        break;
                    }
                    case LEFT:
                    {
                        final float midX = widget.getLeft() + ( widget.getWidth() / 2f);
                        if ( midX < currMidX )
                        {
                            if ( ( nextWidget == null ) || ( midX > nextMidX ) )
                            {
                                nextWidget = widget;
                                nextMidY = midX;
                            }
                        }
                        break;
                    }
                    case RIGHT:
                    {
                        final float midX = widget.getLeft() + ( widget.getWidth() / 2f);
                        if ( midX > currMidX )
                        {
                            if ( ( nextWidget == null ) || ( midX < nextMidX ) )
                            {
                                nextWidget = widget;
                                nextMidY = midX;
                            }
                        }
                        break;
                    }
                    case DOWN:
                    {
                        final float midY = widget.getTop() + ( widget.getHeight() / 2f);
                        if ( midY > currMidY )
                        {
                            if ( ( nextWidget == null ) || ( midY < nextMidY ) )
                            {
                                nextWidget = widget;
                                nextMidY = midY;
                            }
                        }
                        break;
                    }
                    case NEXT:
                    {
                        final float midX = widget.getLeft() + ( widget.getWidth() / 2f);
                        if ( midX > currMidX )
                        {
                            if ( ( nextWidget == null ) || ( midX < nextMidX ) )
                            {
                                nextWidget = widget;
                                nextMidY = midX;
                            }
                        }
                        
                        if ( nextWidget == currentFocusedWidget )
                        {
                            currMidX = 0f;
                            final float midY = widget.getTop() + ( widget.getHeight() / 2f);
                            if ( midY > currMidY )
                            {
                                if ( ( nextWidget == null ) || ( midY < nextMidY ) )
                                {
                                    nextWidget = widget;
                                    nextMidY = midY;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        if ( nextWidget != null )
        {
            nextWidget.requestFocus();
        }
        
        return ( nextWidget );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusGained()
    {
        super.onFocusGained();
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onFocusGained();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusLost()
    {
        super.onFocusLost();
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onFocusLost();
        }
    }
    
    private static void forwardOnMouseLeft( Widget widget, boolean isTopMost, boolean hasFocus )
    {
        if ( widget == null )
            return;
        
        if ( widget instanceof WidgetContainer )
        {
            forwardOnMouseLeft( ( (WidgetContainer)widget ).getCurrentHoveredWidget(), isTopMost, hasFocus );
        }
        
        widget.onMouseExited( isTopMost, false );
        
        if ( widget instanceof WidgetContainer )
        {
            ( (WidgetContainer)widget ).resetCurrentHoveredWidget();
        }
    }
    
    @Override
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        forwardOnMouseLeft( currentHoveredWidget, isTopMost, hasFocus );
        resetCurrentHoveredWidget();
        
        super.onMouseExited( isTopMost, hasFocus );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyPressed( Key key, int modifierMask, long when )
    {
        super.onKeyPressed( key, modifierMask, when );
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onKeyPressed( key, modifierMask, when );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyReleased( Key key, int modifierMask, long when )
    {
        super.onKeyReleased( key, modifierMask, when );
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onKeyReleased( key, modifierMask, when );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyTyped( char ch, int modifierMask, long when )
    {
        super.onKeyTyped( ch, modifierMask, when );
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onKeyTyped( ch, modifierMask, when );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onControllerButtonPressed( ControllerButton button, long when )
    {
        super.onControllerButtonPressed( button, when );
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onControllerButtonPressed( button, when );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onControllerButtonReleased( ControllerButton button, long when )
    {
        super.onControllerButtonReleased( button, when );
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onControllerButtonReleased( button, when );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onControllerAxisChanged( ControllerAxis axis, int axisDelta, long when )
    {
        super.onControllerAxisChanged( axis, axisDelta, when );
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onControllerAxisChanged( axis, axisDelta, when );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInputStateChanged( DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
        super.onInputStateChanged( comp, delta, state, when, isTopMost, hasFocus );
        
        if ( currentFocusedWidget != null )
        {
            currentFocusedWidget.onInputStateChanged( comp, delta, state, when, isTopMost, hasFocus );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateAbsZIndex()
    {
        super.updateAbsZIndex();
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            widgets.get( i ).updateAbsZIndex();
        }
    }
    
    private final void updateLight( boolean updateChildren )
    {
        updateSizeFactors();
        
        final HUD hud = getHUD();
        
        if ( hud != null )
        {
            final Tuple2i loc = Tuple2i.fromPool();
            
            getLocationHUD2Pixels( 0f, 0f, loc );
            
            // OpenGL wants the ScissorBox to be flipped upside down!
            loc.setY( (int)hud.getHeight() - loc.getY() - getContentHeightPX() );
            
            if ( childrenGroup.getScissorRect() != null )
                childrenGroup.getScissorRect().init( loc.getX(), loc.getY(), getContentWidthPX(), getContentHeightPX() );
            //System.out.println( childrenGroup.getScissorBox() );
            //childrenGroup.setScissorRect( null );
            
            Tuple2i.toPool( loc );
        }
        
        if ( updateChildren )
        {
            for ( int i = 0; i < widgets.size(); i++ )
            {
                widgets.get( i ).update();
            }
        }
    }
    
    /**
     * Enables or disables clipping for this WidgetContainer.
     * 
     * @param clippingEnabled
     */
    public void setClippingEnabled( boolean clippingEnabled )
    {
        if ( clippingEnabled == isClippingEnbaled() )
            return;
        
        if ( clippingEnabled )
        {
            childrenGroup.setScissorRect( new ScissorRect( 0, 0, 0, 0 ) );
            updateLight( false );
        }
        else
        {
            childrenGroup.setScissorRect( null );
        }
    }
    
    /**
     * @return whether clipping is enabled or disabled for this WidgetContainer.
     */
    public final boolean isClippingEnbaled()
    {
        return ( childrenGroup.getScissorRect() != null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected float calculateTransformWidth_Pixels2HUD( float contentWidth )
    {
        if ( hasCustomResolution() )
            return ( getResX() / contentWidth );
        
        return ( super.calculateTransformWidth_Pixels2HUD( contentWidth ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected float calculateTransformHeight_Pixels2HUD( float contentHeight )
    {
        if ( hasCustomResolution() )
            return ( getResY() / contentHeight );
        
        return ( super.calculateTransformHeight_Pixels2HUD( contentHeight ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTranslation()
    {
        super.updateTranslation();
        
        updateLight( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update()
    {
        super.update();
        
        //updateLight( true );
        
        if ( ( getHUD() != null ) && ( layoutManager != null ) )
        {
            layoutManager.doLayout( this );
            layoutDirty = false;
        }
        
        // We don't need to explicitly update the children, since this is already done by the setSize() implementation.
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToHUD( HUD hud )
    {
        super.onAttachedToHUD( hud );
        
        if ( hud.isVisible() )
        {
            update();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void setHUD( HUD hud )
    {
        super.setHUD( hud );
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            widgets.get( i ).setHUD( hud );
        }
    }
    
    @Override
    protected void setWidgetDirty()
    {
        super.setWidgetDirty();
        setHostedWidgetDirty();
        
        if ( widgets != null )
        for ( int i = 0; i < widgets.size(); i++ )
        {
            if ( !widgets.get( i ).isHeavyWeight() )
                widgets.get( i ).setWidgetDirty();
        }
    }
    
    /**
     * Draws the (parent-)background for a child Widget.
     * 
     * @param forWidget
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     */
    void drawParentBackground( Widget forWidget, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        //int ox = forWidget.getContentLeftPX();
        //int oy = forWidget.getContentTopPX();
        int ox = 0;
        int oy = 0;
        
        Dim2i tmp = Dim2i.fromPool();
        getSizeHUD2Pixels( forWidget.getLeft(), forWidget.getTop(), tmp );
        ox += tmp.getWidth();
        oy += tmp.getHeight();
        Dim2i.toPool( tmp );
        
        offsetX -= ox;
        offsetY -= oy;
        //width += ox;
        //height += oy;
        width = getWidthPX();
        height = getHeightPX();
        
        drawBackground( texCanvas, offsetX, offsetY, width, height );
    }
    
    /**
     * 
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     * @param drawsSelf
     */
    protected void drawChildWidgets( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        if ( !isAHostedWidgetDirty() )
            return;
        
        Point2i p = Point2i.fromPool();
        Dim2i d = Dim2i.fromPool();
        
        ensureWidgetsSortedByZIndex();
        
        //offsetX += getPaddingLeft();
        //offsetY += getPaddingTop();
        
        offsetX += childrenOffset_PX.getX();
        offsetY -= childrenOffset_PX.getY();
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            Widget widget = widgets.get( i );
            if ( !widget.isHeavyWeight() && widget.isVisible() )
            {
                getRelLocationHUD2Pixels_( widget.getLeft(), widget.getTop(), p );
                getSizeHUD2Pixels( widget.getWidth(), widget.getHeight(), d );
                
                widget.drawAndUpdateWidget( texCanvas, offsetX + p.getX(), offsetY + p.getY(), d.getWidth(), d.getHeight(), false );
            }
        }
        
        Dim2i.toPool( d );
        Point2i.toPool( p );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        drawChildWidgets( texCanvas, offsetX, offsetY, width, height, drawsSelf );
    }
    
    protected GroupNode createChildrenGroup()
    {
        return ( new Group() );
    }
    
    /**
     * Creates a new WidgetContainer with the given width, height and z-index.
     * The WidgetContainer will have a differen coordinate system then it's parent WidgetContainer.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundColor the background color
     * @param backgroundTex the background texture
     * @param backgroundTileMode
     */
    protected WidgetContainer( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height, Colorf backgroundColor, Texture2D backgroundTex, TileMode backgroundTileMode )
    {
        super( isHeavyWeight, hasWidgetAssembler, width, height, backgroundColor, backgroundTex, backgroundTileMode );
        
        Node.pushGlobalIgnoreBounds( true );
        this.childrenGroup = createChildrenGroup();
        Node.popGlobalIgnoreBounds();
        ( (TransformGroup)getSGNode() ).addChild( childrenGroup );
        
        childrenGroup.setScissorRect( new ScissorRect( 0, 0, 0, 0 ) );
    }
}
