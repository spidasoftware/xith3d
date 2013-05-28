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

import org.jagatoo.datatypes.NamableObject;
import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.opengl.enums.TestFunction;
import org.openmali.types.primitives.MutableLong;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Positioned2f;
import org.openmali.types.twodee.Rect2i;
import org.openmali.types.twodee.Sized2f;
import org.openmali.types.twodee.Sized2fRO;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple2i;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.Texture2DCanvas.DrawCallback2D;
import org.xith3d.scenegraph.primitives.DrawRectangle;
import org.xith3d.scenegraph.utils.ShapeUtils;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.__HUD_PrivilegedAccess;
import org.xith3d.ui.hud.borders.BorderFactory;
import org.xith3d.ui.hud.contextmenu.ContextMenu;
import org.xith3d.ui.hud.listeners.WidgetContainerListener;
import org.xith3d.ui.hud.listeners.WidgetControllerListener;
import org.xith3d.ui.hud.listeners.WidgetFocusListener;
import org.xith3d.ui.hud.listeners.WidgetInputListener;
import org.xith3d.ui.hud.listeners.WidgetKeyboardListener;
import org.xith3d.ui.hud.listeners.WidgetLocationListener;
import org.xith3d.ui.hud.listeners.WidgetMouseListener;
import org.xith3d.ui.hud.listeners.WidgetSizeListener;
import org.xith3d.ui.hud.listeners.WidgetVisibilityListener;
import org.xith3d.ui.hud.utils.Cursor;
import org.xith3d.ui.hud.utils.DrawUtils;
import org.xith3d.ui.hud.utils.DropShadowFactory;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;

/**
 * All Widgets to be added to a HUD must extend this class.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Widget implements Positioned2f, Sized2f, NamableObject
{
    protected static abstract class DescriptionBase
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            String s = this.getClass().getName() + "\n{\n";
            
            //final java.lang.reflect.Field[] fields = this.getClass().getDeclaredFields();
            final java.lang.reflect.Method[] methods = this.getClass().getMethods();
            
            for ( int i = 0; i < methods.length; i++ )
            {
                String name = methods[ i ].getName();
                
                if ( name.startsWith( "get" ) )
                    name = name.substring( 3 );
                else if ( name.startsWith( "is" ) )
                    name = name.substring( 2 );
                else
                    continue;
                
                String value;
                try
                {
                    Object valueObj = methods[ i ].invoke( this, (Object[])null );
                    
                    if ( valueObj == null )
                        value = null;
                    else
                        value = valueObj.toString();
                }
                catch ( Throwable t )
                {
                    //t.printStackTrace();
                    value = "[N/A]";
                }
                
                s += "    " + name + " = " + value + "\n";
            }
            
            s += "}";
            
            return ( s );
        }
    }
    
    private static final MutableLong MIN_UPDATE_DELAY = new MutableLong( 50000000L );
    
    private long forcedUpdateDelay = -1L;
    
    private static final RenderingAttributes RENDERING_ATTRIBUTES = new RenderingAttributes();
    static
    {
        RENDERING_ATTRIBUTES.setDepthTestFunction( TestFunction.ALWAYS );
    }
    
    private final boolean isHeavyWeight;
    private Widget hostWidget = null;
    private boolean isPassive = false;
    private Boolean hierarchyOK = null;
    private final TransformGroup transformGroup;
    private final WidgetAssembler widgetAssembler;
    private String name = "";
    private Object userObject = null;
    private Widget assembly = null;
    private Border border = null;
    private int widthPX = -1, heightPX = -1;
    private int contentWidthPX = -1, contentHeightPX = -1;
    protected float transformWidth_Pixels2HUD = -1f, transformHeight_Pixels2HUD = -1f;
    private HUD hud = null;
    private WidgetContainer container = null;
    private ContextMenu contextMenu = null;
    private String tooltip = null;
    private Widget cachedTooltipWidget = null;
    private final Dim2f size = new Dim2f( 0f, 0f );
    private final Tuple2f location = new Point2f( 0f, 0f );
    private int zIndex = 0;
    private boolean hovered = false;
    private boolean isVisible = true;
    private boolean isClickable = true;
    private boolean isPickable = true;
    private boolean isDraggable;
    private boolean isInitialized = false;
    private boolean isInitializing = false;
    
    private Cursor.Type cursorType = null;
    
    private Point2f dragStart = null;
    private final Point2f tmpDragStart = new Point2f();
    private Tuple2f dragStartWidget = null;
    private final Tuple2f tmpDragStartWidget = new Point2f();
    
    private boolean focussable = true;
    private boolean focusRequested = false;
    
    private float transparency = 0.0f;
    
    private DrawRectangle shape = null;
    private final DrawCallback2D drawCallback;
    boolean isThisWidgetDirty = true;
    private boolean isAHostedWidgetDirty = true;
    
    private boolean hasDropShadow = false;
    
    private final Rect2i oldClip = new Rect2i();
    private final Rect2i clip = new Rect2i();
    
    private final ArrayList<WidgetKeyboardListener> keyboardListeners = new ArrayList<WidgetKeyboardListener>( 1 );
    private final ArrayList<WidgetMouseListener> mouseListeners = new ArrayList<WidgetMouseListener>( 1 );
    private final ArrayList<WidgetControllerListener> controllerListeners = new ArrayList<WidgetControllerListener>( 1 );
    private final ArrayList<WidgetFocusListener> focusListeners = new ArrayList<WidgetFocusListener>( 1 );
    private final ArrayList<WidgetLocationListener> locationListeners = new ArrayList<WidgetLocationListener>( 1 );
    private final ArrayList<WidgetSizeListener> sizeListeners = new ArrayList<WidgetSizeListener>( 1 );
    private final ArrayList<WidgetVisibilityListener> visibilityListeners = new ArrayList<WidgetVisibilityListener>( 1 );
    private final ArrayList<WidgetContainerListener> containerListeners = new ArrayList<WidgetContainerListener>( 1 );
    
    /**
     * Returns true, if the widget has a TransformGroup and DrawTexture.<br />
     * Lightweight Widgets can only be use to add to heavyweight WidgetContainers or to containers,
     * that are added to heavyweight containers, etc.
     * 
     * @return is this Widget heavyweight or lightweight. 
     */
    public final boolean isHeavyWeight()
    {
        return ( isHeavyWeight );
    }
    
    /**
     * @return this Widget's Node to be added to the scenegraph
     */
    final Node getSGNode()
    {
        return ( transformGroup );
    }
    
    /**
     * Gets, the Window, of which this is the content pane.
     * 
     * @return the parent Window.
     */
    public Window getParentWindow()
    {
        if ( getContainer() == null )
            return ( null );
        
        return ( getContainer().getParentWindow() );
    }
    
    /**
     * Sets this Widget's name
     */
    public void setName( String name )
    {
        this.name = name;
        
        if ( transformGroup != null )
            transformGroup.setName( name );
    }
    
    /**
     * @return this widget's name
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * Sets this Widget's user-Object.
     * 
     * @param userObject the new user-Object
     */
    public void setUserObject( Object userObject )
    {
        this.userObject = userObject;
    }
    
    /**
     * @return this Widget's user-Object
     */
    public final Object getUserObject()
    {
        return ( userObject );
    }
    
    /**
     * Sets the ContextMenu for this Widget and inherits it to all children,
     * if this is a container.
     * 
     * @param contextMenu
     */
    public void setContextMenu( ContextMenu contextMenu )
    {
        this.contextMenu = contextMenu;
    }
    
    /**
     * @return the (inherited) ContextMenu.
     */
    public ContextMenu getContextMenu()
    {
        if ( contextMenu != null )
        {
            if ( contextMenu.getHUD() == null )
            {
                contextMenu.setHUD( this.getHUD() );
            }
            
            return ( contextMenu );
        }
        
        final WidgetContainer container = getContainer();
        
        if ( container != null )
        {
            /*
            if ( container instanceof HUD )
                return ( null );
            */
            
            return ( container.getContextMenu() );
        }
        
        return ( null );
    }
    
    /**
     * Sets the tooltip to be displayed when the mouse stopps over this Widget.
     * Please see {@link HUD#setToolTipFactory(org.xith3d.ui.hud.utils.ToolTipFactory)}
     * and {@link HUD#getToolTipFactory()}.
     * 
     * @param tooltip
     */
    public void setToolTip( String tooltip )
    {
        this.tooltip = tooltip;
        this.cachedTooltipWidget = null;
    }
    
    /**
     * @return the tooltip to be displayed when the mouse stopps over this Widget.
     * Please see {@link HUD#setToolTipFactory(org.xith3d.ui.hud.utils.ToolTipFactory)}
     * and {@link HUD#getToolTipFactory()}.
     */
    public final String getToolTip()
    {
        return ( tooltip );
    }
    
    /**
     * @return whether this Widget has a tooltip (!= null and not empty String).
     */
    public final boolean hasToolTip()
    {
        return ( ( tooltip != null ) && ( tooltip.length() > 0 ) );
    }
    
    void setCachedToolTipWidget( Widget tooltipWidget )
    {
        this.cachedTooltipWidget = tooltipWidget;
    }
    
    final Widget getCachedToolTipWidget()
    {
        return ( cachedTooltipWidget );
    }
    
    /**
     * Sets, if this Widgets has a drop shadow.
     * 
     * @see DropShadowFactory
     * @see HUD#setDropShadowFactory(DropShadowFactory)
     * @see HUD#getDropShadowFactory()
     * 
     * @param b
     */
    public void setHasDropShadow( boolean b )
    {
        if ( this.hasDropShadow == b )
            return;
        
        this.hasDropShadow = b;
        
        this.setSize( getWidth(), getHeight(), true );
    }
    
    /**
     * Gets, if this Widget has a drop shadow.
     * 
     * @see DropShadowFactory
     * @see HUD#setDropShadowFactory(DropShadowFactory)
     * @see HUD#getDropShadowFactory()
     * 
     * @return if this Widget has a drop shadow.
     */
    public final boolean hasDropShadow()
    {
        return ( hasDropShadow );
    }
    
    /**
     * @return the WidgetAssembler attached to this Widget
     */
    protected final WidgetAssembler getWidgetAssembler()
    {
        return ( widgetAssembler );
    }
    
    /**
     * @return a width that's visually equal to the given height
     * 
     * @param height the height to calculate a visually equal height
     */
    protected final float getEqualWidth_( float height )
    {
        if ( getContainer() != null )
            return ( getContainer().getEqualWidth( height ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getEqualWidth( height ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }
    
    /**
     * Calculates a height that's visually equal to the given width.
     * 
     * @param width the width to calculate a visually equal height
     * 
     * @return the buffer back again
     */
    protected final float getEqualHeight_( float width )
    {
        if ( getContainer() != null )
            return ( getContainer().getEqualHeight( width ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getEqualHeight( width ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }
    
    /**
     * Calculates HUD size from these pixel-values.
     * 
     * @param x the canvas-x-value to transform
     * @param y the canvas-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizePixels2HUD_( int x, int y, Dim2f_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getSizePixels2HUD( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getSizePixels2HUD( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
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
    protected final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationPixels2HUD_( int x, int y, Tuple2f_ buffer )
    {
        x -= getContentLeftPX();
        y -= getContentTopPX();
        
        if ( getContainer() != null )
        {
            getContainer().getLocationPixels2HUD( x, y, buffer );
            
            buffer.sub( getLeft(), getTop() );
            
            return ( buffer );
        }
        
        if ( getHUD() != null )
        {
            getHUD().getCoordinatesConverter().getLocationPixels2HUD( x, y, buffer );
            
            buffer.sub( getLeft(), getTop() );
            
            return ( buffer );
        }
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }
    
    /**
     * Calculates pixel size from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2i_ extends Dim2i> Dim2i_ getSizeHUD2Pixels_( float x, float y, Dim2i_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getSizeHUD2Pixels( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getSizeHUD2Pixels( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }
    
    /**
     * Calculates pixel size from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Tuple2i_ extends Tuple2i> Tuple2i_ getRelLocationHUD2Pixels_( float x, float y, Tuple2i_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getRelLocationHUD2Pixels( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getLocationHUD2Pixels( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
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
    protected final <Tuple2i_ extends Tuple2i> Tuple2i_ getLocationHUD2Pixels_( float x, float y, Tuple2i_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getLocationHUD2Pixels( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getLocationHUD2Pixels( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }
    
    /**
     * Calculates scenegraph width and height from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizeHUD2SG_( float x, float y, Dim2f_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getSizeHUD2SG( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getSizeHUD2SG( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
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
    protected final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationHUD2SG_( float x, float y, Tuple2f_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getLocationHUD2SG( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getLocationHUD2SG( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }
    
    /**
     * Calculates HUD size from these scenegraph-values.
     * 
     * @param x the scenegraph-x-value to transform
     * @param y the scenegraph-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizeSG2HUD_( float x, float y, Dim2f_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getSizeSG2HUD( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getSizeSG2HUD( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
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
    protected final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationSG2HUD_( float x, float y, Tuple2f_ buffer )
    {
        if ( getContainer() != null )
        {
            getContainer().getLocationSG2HUD( x, y, buffer );
            
            /*
            if ( getAssembly() != null )
            {
                buffer.sub( getAssembly().getLeft(), getAssembly().getTop() );
            }
            */
            
            return ( buffer );
        }
        
        if ( getHUD() != null )
        {
            getHUD().getCoordinatesConverter().getLocationSG2HUD( x, y, buffer );
            
            /*
            if ( getAssembly() != null )
            {
                buffer.sub( getAssembly().getLeft(), getAssembly().getTop() );
            }
            */
            
            return ( buffer );
        }
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
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
    protected final <Dim2f_ extends Dim2f> Dim2f_ getSizeOfPixels_( int x, int y, Dim2f_ buffer )
    {
        if ( getContainer() != null )
            return ( getContainer().getSizeOfPixels( x, y, buffer ) );
        
        if ( getHUD() != null )
            return ( getHUD().getCoordinatesConverter().getSizeOfPixels( x, y, buffer ) );
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }
    
    /**
     * Computes the absolute position of the given Widget on the HUD.
     * 
     * @param buffer
     */
    protected final <Tuple2f_ extends Tuple2f> Tuple2f_ getAbsoluteLocationOnHUD_( Tuple2f_ buffer )
    {
        if ( getHUD() != null )
        {
            getHUD().getCoordinatesConverter().getAbsoluteLocationOnHUD( this, buffer );
            
            return ( buffer );
        }
        
        throw new Error( "This method can't be executed on a widget, that is not attached to the HUD." );
    }

    /**
     * Adds a new {@link WidgetKeyboardListener}.
     * 
     * @param l
     */
    public void addKeyboardListener( WidgetKeyboardListener l )
    {
        keyboardListeners.add( l );
    }
    
    /**
     * Removes a {@link WidgetKeyboardListener}.
     * 
     * @param l
     */
    public void removeKeyboardListener( WidgetKeyboardListener l )
    {
        keyboardListeners.remove( l );
    }
    
    /**
     * Adds a new {@link WidgetMouseListener}.
     * 
     * @param l
     */
    public void addMouseListener( WidgetMouseListener l )
    {
        mouseListeners.add( l );
    }
    
    /**
     * Removes a {@link WidgetMouseListener}.
     * 
     * @param l
     */
    public void removeMouseListener( WidgetMouseListener l )
    {
        mouseListeners.remove( l );
    }
    
    /**
     * Adds a new {@link WidgetControllerListener}.
     * 
     * @param l
     */
    public void addControllerListener( WidgetControllerListener l )
    {
        controllerListeners.add( l );
    }
    
    /**
     * Removes a {@link WidgetControllerListener}.
     * 
     * @param l
     */
    public void removeControllerListener( WidgetControllerListener l )
    {
        controllerListeners.remove( l );
    }
    
    /**
     * Adds a new WidgetInputListener.
     * 
     * @param l
     */
    public final void addInputListener( WidgetInputListener l )
    {
        addKeyboardListener( l );
        addMouseListener( l );
        addControllerListener( l );
    }
    
    /**
     * Removes a WidgetInputListener.
     * 
     * @param l
     */
    public final void removeInputListener( WidgetInputListener l )
    {
        removeKeyboardListener( l );
        removeMouseListener( l );
        removeControllerListener( l );
    }
    
    /**
     * Adds a new WidgetFocusListener.
     * 
     * @param l
     */
    public void addFocusListener( WidgetFocusListener l )
    {
        focusListeners.add( l );
    }
    
    /**
     * Removes a WidgetFocusListener.
     * 
     * @param l
     */
    public void removeFocusListener( WidgetFocusListener l )
    {
        focusListeners.remove( l );
    }
    
    /**
     * Adds a new WidgetLocationListener.
     * 
     * @param l
     */
    public void addLocationListener( WidgetLocationListener l )
    {
        locationListeners.add( l );
    }
    
    /**
     * Removes a WidgetFocusListener.
     * 
     * @param l
     */
    public void removeLocationListener( WidgetLocationListener l )
    {
        locationListeners.remove( l );
    }
    
    /**
     * Adds a new WidgetSizeListener.
     * 
     * @param l
     */
    public void addSizeListener( WidgetSizeListener l )
    {
        sizeListeners.add( l );
    }
    
    /**
     * Removes a WidgetSizeListener.
     * 
     * @param l
     */
    public void removeSizeListener( WidgetSizeListener l )
    {
        sizeListeners.remove( l );
    }
    
    /**
     * Adds a new WidgetVisibilityListener.
     * 
     * @param l
     */
    public void addVisibilityListener( WidgetVisibilityListener l )
    {
        visibilityListeners.add( l );
    }
    
    /**
     * Removes a WidgetVisibilityListener.
     * 
     * @param l
     */
    public void removeVisibilityListener( WidgetVisibilityListener l )
    {
        visibilityListeners.remove( l );
    }
    
    /**
     * Adds a new WidgetContainerListener.
     * 
     * @param l
     */
    public void addContainerListener( WidgetContainerListener l )
    {
        containerListeners.add( l );
    }
    
    /**
     * Removes a WidgetContainerListener.
     * 
     * @param l
     */
    public void removeContainerListener( WidgetContainerListener l )
    {
        containerListeners.remove( l );
    }
    
    void notifyContainerListenersAboutAttachedWidget( Widget widget, WidgetContainer container )
    {
        for ( int i = 0; i < containerListeners.size(); i++ )
            containerListeners.get( i ).onWidgetAttachedToContainer( widget, container );
    }
    
    void notifyContainerListenersAboutDetachedWidget( Widget widget, WidgetContainer container )
    {
        for ( int i = 0; i < containerListeners.size(); i++ )
            containerListeners.get( i ).onWidgetDetachedFromContainer( widget, container );
    }
    
    /**
     * Sets whether this Widget can get the focus or not.
     * 
     * @param focussable
     */
    public void setFocussable( boolean focussable )
    {
        this.focussable = focussable;
    }
    
    /**
     * @return whether this Widget can get the focus or not.
     */
    public final boolean isFocussable()
    {
        return ( focussable );
    }
    
    /**
     * Requests the focus from the HUD system.<br>
     * The focus will be gained on next loop iteration.
     */
    public void requestFocus()
    {
        if ( !isFocussable() )
        {
            return;
        }
        
        if ( isInitialized() && ( getHUD() != null ) )
        {
            if ( getContainer() != null )
                getContainer().focus( this );
            else
                __HUD_PrivilegedAccess.focus( getHUD(), this );
        }
        else
        {
            focusRequested = true;
        }
    }
    
    /**
     * This event is fired, when the focus is gained to a Widget.
     */
    protected void onFocusGained()
    {
        for ( int i = 0; i < focusListeners.size(); i++ )
            focusListeners.get( i ).onFocusGained( this );
    }
    
    /**
     * This event is fired, when the focus is lost by a Widget.
     */
    protected void onFocusLost()
    {
        for ( int i = 0; i < focusListeners.size(); i++ )
            focusListeners.get( i ).onFocusLost( this );
    }
    
    /**
     * Is this Widget focused?
     * 
     * @param testLeaf only returns true, if this Widget is focused and is a Leaf
     */
    public final boolean hasFocus( boolean testLeaf )
    {
        if ( getHUD() != null )
        {
            final Widget cfw = __HUD_PrivilegedAccess.getCurrentFocusedWidget( getHUD(), testLeaf );
            
            if ( cfw == null )
                return ( false );
            
            if ( cfw == this )
                return ( true );
            
            if ( ( this.getAssembly() == cfw ) && ( getAssembly().getWidgetAssembler().getCurrentFocussedWidget() == this ) )
                return ( true );
        }
        
        return ( false );
    }
    
    /**
     * Is this Widget focused?
     */
    public final boolean hasFocus()
    {
        return ( hasFocus( false ) );
    }
    
    /**
     * Checks, if this Widget blocks the given DeviceComponent for focus-moves.
     * 
     * @param dc
     * 
     * @return Widget blocks the queried DeviceComponent?
     */
    protected boolean blocksFocusMoveDeviceComponent( DeviceComponent dc )
    {
        return ( false );
    }
    
    protected final void bindToGlobalMouseMovement()
    {
        __HUD_PrivilegedAccess.bindMouseMovement( getHUD(), this );
    }
    
    /**
     * This method is called when the mouse entered the Widget area.
     * 
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    protected void onMouseEntered( boolean isTopMost, boolean hasFocus )
    {
        if ( isTopMost )
        {
            this.hovered = true;
        }
        
        for ( int i = 0; i < mouseListeners.size(); i++ )
            mouseListeners.get( i ).onMouseEntered( this, isTopMost, hasFocus );
    }
    
    /**
     * This method is called when the mouse exited the Widget area.
     * 
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        if ( isTopMost )
        {
            this.hovered = false;
        }
        
        for ( int i = 0; i < mouseListeners.size(); i++ )
            mouseListeners.get( i ).onMouseExited( this, isTopMost, hasFocus );
    }
    
    /**
     * 
     * @param canvasX
     * @param canvasY
     * @param widgetX
     * @param widgetY
     */
    protected void startDragging( int canvasX, int canvasY, float widgetX, float widgetY )
    {
        /*
        Tuple2f pos = getContainer().getLocationCanvas2HUD( x, y );
        pos.sub( this.getLocation() );
        */
        
        dragStart = tmpDragStart;
        
        Tuple2f tmp = Tuple2f.fromPool();
        getAbsoluteLocationOnHUD_( tmp );
        dragStart.set( tmp.getX() + widgetX, tmp.getY() + widgetY );
        Tuple2f.toPool( tmp );
        
        tmpDragStartWidget.set( getLeft(), getTop() );
        dragStartWidget = tmpDragStartWidget;
        
        bindToGlobalMouseMovement();
        
        for ( int i = 0; i < locationListeners.size(); i++ )
            locationListeners.get( i ).onWidgetDragStarted( this );
    }
    
    /**
     * This event is fired, when a mouse button is pressed on a focused Widget.
     * 
     * @param button the button that was pressed
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param when
     * @param lastWhen
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     * 
     * @see net.jtank.input.MouseCode
     */
    protected void onMouseButtonPressed( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        for ( int i = 0; i < mouseListeners.size(); i++ )
            mouseListeners.get( i ).onMouseButtonPressed( this, button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isDraggable() && isTopMost )
        {
            Tuple2i tmp = Tuple2i.fromPool();
            getLocationHUD2Pixels_( x, y, tmp );
            int xpx = tmp.getX();
            int ypx = tmp.getY();
            Tuple2i.toPool( tmp );
            
            startDragging( xpx, ypx, x, y );
        }
    }
    
    /**
     * This method is called to notify all atteched {@link WidgetLocationListener}s about this event.
     */
    protected void notifyOnDragStopped()
    {
        for ( int i = 0; i < locationListeners.size(); i++ )
            locationListeners.get( i ).onWidgetDragStopped( this );
    }
    
    /**
     * This event is fired, when a mouse button is released on a focused Widget.
     * 
     * @param button the button that was released
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param when
     * @param lastWhen
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     * 
     * @see net.jtank.input.MouseCode
     */
    protected void onMouseButtonReleased( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        for ( int i = 0; i < mouseListeners.size(); i++ )
            mouseListeners.get( i ).onMouseButtonReleased( this, button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        final boolean wasDragging = ( dragStart != null );
        
        dragStart = null;
        dragStartWidget = null;
        
        if ( wasDragging )
        {
            notifyOnDragStopped();
        }
    }
    
    /**
     * This event is fired, when the mouse is moved on a Widget.
     * 
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param buttonsState
     * @param when
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    protected void onMouseMoved( float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
        for ( int i = 0; i < mouseListeners.size(); i++ )
            mouseListeners.get( i ).onMouseMoved( this, x, y, buttonsState, when, isTopMost, hasFocus );
        
        if ( dragStart != null )
        {
            Tuple2f tmp = Tuple2f.fromPool();
            getAbsoluteLocationOnHUD_( tmp );
            float absX = tmp.getX() + x;
            float absY = tmp.getY() + y;
            Tuple2f.toPool( tmp );
            
            float widgetPosX = dragStartWidget.getX() + ( absX - dragStart.getX() );
            float widgetPosY = dragStartWidget.getY() + ( absY - dragStart.getY() );
            
            float parentResX = 0f;
            float parentResY = 0f;
            if ( getContainer() != null )
            {
                parentResX = getContainer().getResX();
                parentResY = getContainer().getResY();
            }
            else if ( getHUD() != null )
            {
                parentResX = getHUD().getResX();
                parentResY = getHUD().getResY();
            }
            
            // it must not be possible to drag the Window out of the viewport
            widgetPosX = Math.max( widgetPosX, 0f - this.getWidth() );
            widgetPosX = Math.min( widgetPosX, parentResX );
            widgetPosY = Math.max( widgetPosY, 0f );
            widgetPosY = Math.min( widgetPosY, parentResY );
            
            this.setLocation( widgetPosX, widgetPosY );
        }
    }
    
    /**
     * This event is fired, when the mouse position has not been changed on this
     * Widget for a certain amount of time.
     * 
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param when
     * @param isTopMost is this Widget topMost
     * @param hasFocus is this Widget focused
     */
    protected void onMouseStopped( float x, float y, long when, boolean isTopMost, boolean hasFocus )
    {
        for ( int i = 0; i < mouseListeners.size(); i++ )
            mouseListeners.get( i ).onMouseStopped( this, x, y, when, isTopMost, hasFocus );
    }
    
    /**
     * This event is fired, when the mouse wheel is moved on a Widget.
     * 
     * @param delta a positive value when the wheel was moved up
     * @param isPageMove
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param when
     * @param isTopMost is this Widget topMost
     */
    protected void onMouseWheelMoved( int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost )
    {
        for ( int i = 0; i < mouseListeners.size(); i++ )
            mouseListeners.get( i ).onMouseWheelMoved( this, delta, isPageMove, x, y, when, isTopMost );
    }
    
    /**
     * This event is fired, when a key is pressed on a focused Widget.
     * 
     * @param key the key that was pressed
     * @param modifierMask the mask of modifier keys
     * @param when the keyevent's timestamp
     */
    protected void onKeyPressed( Key key, int modifierMask, long when )
    {
        for ( int i = 0; i < keyboardListeners.size(); i++ )
            keyboardListeners.get( i ).onKeyPressed( this, key, modifierMask, when );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().onKeyPressed( key, modifierMask, when );
    }
    
    /**
     * This event is fired, when a key is released on a focused Widget.
     * 
     * @param key the key that was released
     * @param modifierMask the mask of modifier keys
     * @param when the keyevent's timestamp
     */
    protected void onKeyReleased( Key key, int modifierMask, long when )
    {
        for ( int i = 0; i < keyboardListeners.size(); i++ )
            keyboardListeners.get( i ).onKeyReleased( this, key, modifierMask, when );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().onKeyReleased( key, modifierMask, when );
    }
    
    /**
     * This event is fired when a key is typed on the keyboard.
     * 
     * @param ch the typed key's character
     * @param modifierMask the mask of modifier keys
     * @param when the keyevent's timestamp
     */
    protected void onKeyTyped( char ch, int modifierMask, long when )
    {
        for ( int i = 0; i < keyboardListeners.size(); i++ )
            keyboardListeners.get( i ).onKeyTyped( this, ch, modifierMask, when );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().onKeyTyped( ch, modifierMask, when );
    }
    
    /**
     * This event is fired when a ControllerButton has been pressed
     * and this Widget is the currently focussed one.
     * 
     * @param button the pressed button
     * @param when the gameTime of the event
     */
    protected void onControllerButtonPressed( ControllerButton button, long when )
    {
        for ( int i = 0; i < controllerListeners.size(); i++ )
            controllerListeners.get( i ).onControllerButtonPressed( this, button, when );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().onControllerButtonPressed( button, when );
    }
    
    /**
     * This event is fired when a ControllerButton has been released
     * and this Widget is the currently focussed one.
     * 
     * @param button the released button
     * @param when the gameTime of the event
     */
    protected void onControllerButtonReleased( ControllerButton button, long when )
    {
        for ( int i = 0; i < controllerListeners.size(); i++ )
            controllerListeners.get( i ).onControllerButtonReleased( this, button, when );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().onControllerButtonReleased( button, when );
    }
    
    /**
     * This event is fired when a ControllerAxis has changed
     * and this Widget is the currently focussed one.
     * 
     * @param axis the changed axis
     * @param axisDelta
     * @param when the gameTime of the event
     */
    protected void onControllerAxisChanged( ControllerAxis axis, int axisDelta, long when )
    {
        for ( int i = 0; i < controllerListeners.size(); i++ )
            controllerListeners.get( i ).onControllerAxisChanged( this, axis, axisDelta, when );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().onControllerAxisChanged( axis, axisDelta, when );
    }
    
    /**
     * This event is fired when the state of any DeviceComponent has changed.
     * 
     * @param comp
     * @param delta
     * @param state
     * @param when the gameTime of the event
     * @param isTopMost
     * @param hasFocus
     */
    protected void onInputStateChanged( DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
        // If comp is null, we assume a mouse moved event.
        if ( ( comp == null ) || ( comp.getType().isMouseComponent() ) )
        {
            for ( int i = 0; i < mouseListeners.size(); i++ )
                mouseListeners.get( i ).onInputStateChanged( this, comp, delta, state, when, isTopMost, hasFocus );
        }
        else if ( comp.getType().isKeyboardComponent() )
        {
            for ( int i = 0; i < keyboardListeners.size(); i++ )
                keyboardListeners.get( i ).onInputStateChanged( this, comp, delta, state, when, isTopMost, hasFocus );
        }
        else if ( comp.getType().isControllerComponent() )
        {
            for ( int i = 0; i < controllerListeners.size(); i++ )
                controllerListeners.get( i ).onInputStateChanged( this, comp, delta, state, when, isTopMost, hasFocus );
        }
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().onInputStateChanged( comp, delta, state, when, isTopMost, hasFocus );
    }
    
    protected final boolean isHovered()
    {
        return ( hovered );
    }
    
    /**
     * This method is triggered when the visibility state has eeffectively changed.
     * 
     * @param visible
     */
    protected void onVisibilityChanged( boolean visible )
    {
    }
    
    /**
     * Sets wheather this Widget is visible or not
     * 
     * @param visible visible?
     */
    public final void setVisible( boolean visible )
    {
        if ( visible == isVisible() )
            return;
        
        this.isVisible = visible;
        
        if ( getSGNode() != null )
            this.getSGNode().setRenderable( visible );
        
        onVisibilityChanged( visible );
        
        if ( !isHeavyWeight() )
            setTextureDirty();
        
        for ( int i = 0; i < visibilityListeners.size(); i++ )
        {
            visibilityListeners.get( i ).onWidgetVisibilityChanged( this, visible );
        }
    }
    
    /**
     * @return wheather this Widget is visible or not
     */
    public final boolean isVisible()
    {
        return ( isVisible );
    }
    
    /**
     * Sets the whole Widget's transparency.
     * 
     * @param transparency
     * @param childrenToo
     */
    protected void setTransparency( float transparency, boolean childrenToo )
    {
        if ( shape != null )
            ShapeUtils.setShapesTransparency( shape, transparency, false, false );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().setTransparency( transparency, childrenToo );
        
        this.transparency = transparency;
    }
    
    /**
     * Sets the whole Widget's transparency.
     * 
     * @param transparency
     */
    public final void setTransparency( float transparency )
    {
        setTransparency( transparency, true );
    }
    
    /**
     * Gets the whole Widget's transparency.
     * 
     * @return the transparency.
     */
    public final float getTransparency()
    {
        return ( transparency );
    }
    
    /**
     * Sets wheather this Widget is clickable.
     * If it is not clickable, clicks are sent to
     * the next Widget under this one, if any.
     */
    public void setClickable( boolean isClickable )
    {
        this.isClickable = isClickable;
    }
    
    /**
     * @return wheather this Widget is clickable.
     * If it is not clickable, clicks are sent to
     * the next Widget under this one, if any.
     */
    public final boolean isClickable()
    {
        return ( isClickable );
    }
    
    /**
     * Sets wheather this Widget is clickable.
     * If it is not clickable, clicks are sent to
     * the next Widget under this one, if any.
     */
    public void setDraggable( boolean draggable )
    {
        this.isDraggable = draggable;
    }
    
    /**
     * @return wheather this Widget is draggable.
     */
    public final boolean isDraggable()
    {
        return ( isDraggable );
    }
    
    /**
     * Sets wheather this Widget is pickable.
     * If it is not pickable, the pick() method
     * will always return <i>null</i>.
     */
    public void setPickable( boolean isPickable )
    {
        this.isPickable = isPickable;
    }
    
    /**
     * @return wheather this Widget is pickable.
     * If it is not pickable, the pick() method
     * will always return <i>null</i>.
     */
    public final boolean isPickable()
    {
        return ( isPickable );
    }
    
    /**
     * Sets the Cursor type to be used when the cursor is over this Widget
     * and which is inherited to the Children, if this is a container.
     * 
     * @param cursor
     */
    public final void setCursor( Cursor.Type cursor )
    {
        this.cursorType = cursor;
    }
    
    /**
     * @return the Cursor type to be used when the cursor is over this Widget
     * and which is inherited to the Children, if this is a container.
     */
    public final Cursor.Type getCursor()
    {
        return ( cursorType );
    }
    
    /**
     * @return the "computed" Cursor type to be used when the cursor is over
     *         this Widget.
     */
    final Cursor.Type getInheritedCursor()
    {
        if ( this.cursorType != null )
            return ( this.cursorType );
        
        if ( getContainer() != null )
            return ( getContainer().getInheritedCursor() );
        
        if ( getHUD() != null )
            //return ( getHUD().getInheritedCursor() );
            return ( Cursor.Type.POINTER1 );
        
        return ( null );
    }
    
    /**
     * Sets the Border to use for this BorderSettable Widget.
     * 
     * @param border the new Border (<i>null</i> for no border)
     */
    public void setBorder( Border border )
    {
        if ( this.border == border )
            return;
        
        this.border = border;
        
        updateSizeFactors();
        setTextureDirty();
    }
    
    /**
     * Creates a new Border from the given Border.Desctiption and invokes
     * setBorder(Border).
     * 
     * @see #setBorder(Border)
     * 
     * @param borderDesc the Border.Description to create the new Border from (<i>null</i> for no border)
     */
    public final void setBorder( Border.Description borderDesc )
    {
        if ( borderDesc == null )
            setBorder( (Border)null );
        else
            setBorder( BorderFactory.createBorder( borderDesc ) );
    }
    
    /**
     * @return the Border used for this BorderSettable Widget
     */
    public final Border getBorder()
    {
        return ( border );
    }
    
    /**
     * Gets the left coordinate of where content (0, 0) is (in pixels).
     * 
     * @return the content left.
     */
    protected int getContentLeftPX()
    {
        int result = 0;
        
        if ( this instanceof PaddingSettable )
        {
            result += ( (PaddingSettable)this ).getPaddingLeft();
        }
        
        if ( getBorder() != null )
        {
            result += getBorder().getLeftWidth();
        }
        
        if ( getWidgetAssembler() != null )
        {
            result += getWidgetAssembler().getAdditionalContentLeft();
        }
        
        return ( result );
    }
    
    /**
     * Gets the top coordinate of where content (0, 0) is (in pixels).
     * 
     * @return the content left.
     */
    protected int getContentTopPX()
    {
        int result = 0;
        
        if ( this instanceof PaddingSettable )
        {
            result += ( (PaddingSettable)this ).getPaddingTop();
        }
        
        if ( getBorder() != null )
        {
            result += getBorder().getTopHeight();
        }
        
        if ( getWidgetAssembler() != null )
        {
            result += getWidgetAssembler().getAdditionalContentTop();
        }
        
        return ( result );
    }
    
    protected final <Dim2f_ extends Dim2f> Dim2f_ getContentOffset( Dim2f_ buffer )
    {
        return ( getSizePixels2HUD_( getContentLeftPX(), getContentTopPX(), buffer ) );
    }
    
    /**
     * Gets the width of the widget's content area (minus border and padding) (in pixels).
     * 
     * @return the content width.
     */
    protected int getContentWidthPX()
    {
        if ( getHUD() == null )
            throw new Error( "This widget is not attached to the HUD." );
        
        return ( contentWidthPX );
    }
    
    /**
     * Gets the height of the widget's content area (minus border and padding) (in pixels).
     * 
     * @return the content width.
     */
    protected int getContentHeightPX()
    {
        if ( getHUD() == null )
            throw new Error( "This widget is not attached to the HUD." );
        
        return (  contentHeightPX );
    }
    
    /**
     * @return the width without the Border (if any) and padding
     */
    public final float getContentWidth()
    {
        /*
        int contentWidthPX = getContentWidthPX();
        
        if ( contentWidthPX == -1 )
            throw new Error();
        
        return ( transformWidth_Pixels2HUD * contentWidthPX );
        */
        
        int nonContentWidthPX = 0;
        
        if ( getBorder() != null )
        {
            nonContentWidthPX += getBorder().getLeftWidth() + getBorder().getRightWidth();
        }
        
        if ( this instanceof PaddingSettable )
        {
            PaddingSettable ps = (PaddingSettable)this;
            
            nonContentWidthPX += ps.getPaddingLeft() + ps.getPaddingRight();
        }
        
        if ( getWidgetAssembler() != null )
        {
            nonContentWidthPX -= getWidgetAssembler().getAdditionalContentWidth();
        }
        
        Dim2f buffer = Dim2f.fromPool();
        
        getSizePixels2HUD_( nonContentWidthPX, 0, buffer );
        
        float contentWidth = getWidth() - buffer.getWidth();
        
        Dim2f.toPool( buffer );
        
        return ( contentWidth );
    }
    
    /**
     * @return the height without the Border (if any) and padding
     */
    public final float getContentHeight()
    {
        /*
        int contentHeightPX = getContentHeightPX();
        
        if ( contentHeightPX == -1 )
            throw new Error();
        
        return ( transformHeight_Pixels2HUD * contentHeightPX );
        */
        
        int nonContentHeightPX = 0;
        
        if ( getBorder() != null )
        {
            nonContentHeightPX += getBorder().getTopHeight() + getBorder().getBottomHeight();
        }
        
        if ( this instanceof PaddingSettable )
        {
            PaddingSettable ps = (PaddingSettable)this;
            
            nonContentHeightPX += ps.getPaddingTop() + ps.getPaddingBottom();
        }
        
        if ( getWidgetAssembler() != null )
        {
            nonContentHeightPX -= getWidgetAssembler().getAdditionalContentHeight();
        }
        
        Dim2f buffer = Dim2f.fromPool();
        
        getSizePixels2HUD_( 0, nonContentHeightPX, buffer );
        
        float contentHeight = getHeight() - buffer.getHeight();
        
        Dim2f.toPool( buffer );
        
        return ( contentHeight );
    }
    
    /**
     * @return the aspect ratio of the inner size
     */
    public final float getContentAspect()
    {
        return ( getContentWidth() / getContentHeight() );
    }
    
    protected final int getLevel()
    {
        if ( getContainer() == null )
            return ( 0 );
        
        return ( getContainer().getLevel() + 1 );
    }
    
    /**
     * Updates the Widget's internals.<br>
     * Called by the Widget system and can be called from outside.
     */
    public void update()
    {
        this.setLocation( getLeft(), getTop(), true, true );
        this.setSize( getWidth(), getHeight(), true );
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().update();
    }
    
    private static final class ZIndexComparable implements Comparable<Object>
    {
        private int[] zIndices = new int[ 1 ];
        
        public final void update( Widget widget )
        {
            int level = widget.getLevel();
            
            if ( zIndices.length != level + 1 )
            {
                zIndices = new int[ level + 1 ];
            }
            
            while ( widget != null )
            {
                zIndices[ level-- ] = widget.getZIndex();
                
                widget = widget.getContainer();
            }
        }
        
        public int compareTo( Object o2 )
        {
            if ( !( o2 instanceof ZIndexComparable ) )
                return ( +1 );
            
            ZIndexComparable that = (ZIndexComparable)o2;
            
            int n = Math.min( this.zIndices.length, that.zIndices.length );
            for ( int i = 0; i < n; i++ )
            {
                if ( this.zIndices[i] < that.zIndices[i] )
                    return ( -1 );
                
                if ( this.zIndices[i] > that.zIndices[i] )
                    return ( +1 );
            }
            
            if ( this.zIndices.length < that.zIndices.length )
                return ( -1 );
            
            if ( this.zIndices.length > that.zIndices.length )
                return ( +1 );
            
            return ( 0 );
        }
    }
    
    private final ZIndexComparable zIndexComparable = new ZIndexComparable();
    
    protected void updateAbsZIndex()
    {
        //if ( shape != null )
        {
            zIndexComparable.update( this );
        }
    }
    
    /**
     * @param contentWidth
     */
    protected float calculateTransformWidth_Pixels2HUD( float contentWidth )
    {
        return ( 1f );
    }
    
    /**
     * @param contentHeight
     */
    protected float calculateTransformHeight_Pixels2HUD( float contentHeight )
    {
        return ( 1f );
    }
    
    protected void updateSizeFactors()
    {
        if ( getHUD() == null )
            return;
        
        int nonContentWidthPX = 0;
        int nonContentHeightPX = 0;
        
        if ( getBorder() != null )
        {
            nonContentWidthPX += getBorder().getLeftWidth() + getBorder().getRightWidth();
            nonContentHeightPX += getBorder().getTopHeight() + getBorder().getBottomHeight();
        }
        
        if ( this instanceof PaddingSettable )
        {
            PaddingSettable ps = (PaddingSettable)this;
            
            nonContentWidthPX += ps.getPaddingLeft() + ps.getPaddingRight();
            nonContentHeightPX += ps.getPaddingTop() + ps.getPaddingBottom();
        }
        
        if ( getWidgetAssembler() != null )
        {
            nonContentWidthPX -= getWidgetAssembler().getAdditionalContentWidth();
            nonContentHeightPX -= getWidgetAssembler().getAdditionalContentHeight();
        }
        
        Dim2f buffer = Dim2f.fromPool();
        
        getSizePixels2HUD_( nonContentWidthPX, nonContentHeightPX, buffer );
        
        float contentWidth = getWidth() - buffer.getWidth();
        float contentHeight = getHeight() - buffer.getHeight();
        
        Dim2f.toPool( buffer );
        
        Dim2i buffer2 = Dim2i.fromPool();
        
        getSizeHUD2Pixels_( contentWidth, contentHeight, buffer2 );
        
        contentWidthPX = buffer2.getWidth();
        contentHeightPX = buffer2.getHeight();
        //if ( this instanceof TextField )
            //Thread.dumpStack();
            //System.out.println( contentWidthPX + ", " + contentHeightPX + ", " + contentWidth + ", " + contentHeight );
        
        getSizeHUD2Pixels_( getWidth(), getHeight(), buffer2 );
        
        widthPX = buffer2.getWidth();
        heightPX = buffer2.getHeight();
        
        Dim2i.toPool( buffer2 );
        
        transformWidth_Pixels2HUD = calculateTransformWidth_Pixels2HUD( contentWidth );
        transformHeight_Pixels2HUD = calculateTransformHeight_Pixels2HUD( contentHeight );
    }
    
    /**
     * Effectively changes the translation of this Widget (location and z-index)
     */
    protected void updateTranslation()
    {
        if ( getHUD() == null )
            return;
        
        updateAbsZIndex();
        
        if ( transformGroup == null )
            return;
        
        final Transform3D t3d = transformGroup.getTransform();
        final Tuple2f loc2 = Tuple2f.fromPool();
        getLocationHUD2SG_( getLeft(), getTop(), loc2 );
        t3d.setTranslation( loc2.getX(), loc2.getY(), 0f );
        Tuple2f.toPool( loc2 );
        transformGroup.setTransform( t3d );
    }
    
    /**
     * 
     * @param oldLeft
     * @param oldTop
     * @param newLeft
     * @param newTop
     */
    protected void onLocationChanged( float oldLeft, float oldTop, float newLeft, float newTop )
    {
    }
    
    protected final boolean setLocation( float locX, float locY, boolean forced, boolean needsTextureRefresh )
    {
        final float oldLeft = getLeft();
        final float oldTop = getTop();
        
        final boolean result = ( ( oldLeft != locX ) || ( oldTop != locY ) );
        
        if ( !result && !forced )
        {
            return ( false );
        }
        
        if ( result || forced )
        {
            this.location.set( locX, locY );
            
            if ( getHUD() != null )
            {
                updateTranslation();
            }
            
            if ( getWidgetAssembler() != null )
                getWidgetAssembler().onOwnerMoved( locX - oldLeft, locY - oldTop, needsTextureRefresh );
        }
        
        if ( result )
        {
            if ( !isHeavyWeight() && needsTextureRefresh )
            {
                setTextureDirty();
                Widget rootHost = getRootHostWidget();
                if ( rootHost != null )
                    rootHost.setWidgetDirty();
            }
            
            onLocationChanged( oldLeft, oldTop, locX, locY );
            
            for ( int i = 0; i < locationListeners.size(); i++ )
            {
                locationListeners.get( i ).onWidgetLocationChanged( this, oldLeft, oldTop, locX, locY );
            }
            
            if ( getContainer() != null )
            {
                getContainer().onChildMovedOrResized( this );
            }
        }
        
        return ( result );
    }
    
    /**
     * Sets this Widget's location relative to the upper-left corner of it's WidgetContainer
     * 
     * @param locX the new x-location
     * @param locY the new y-location
     * 
     * @return true, if the location actually has changed
     */
    public final Widget setLocation( float locX, float locY )
    {
        setLocation( locX, locY, false, true );
        
        return ( this );
    }
    
    /**
     * Sets this Widget's location relative to the upper-left corner of it's WidgetContainer
     * 
     * @param loc the new location
     * 
     * @return true, if the location actually has changed
     */
    public final Widget setLocation( Tuple2f loc )
    {
        return ( setLocation( loc.getX(), loc.getY() ) );
    }
    
    /**
     * @return this Widget's location relative to the upper-left corner of it's WidgetContainer
     */
    public final Tuple2f getLocation()
    {
        return ( location.getReadOnly() );
    }
    
    /**
     * @return this Widget's location relative to the upper-left corner of it's WidgetContainer
     */
    public <Tuple2f_ extends Tuple2f> Tuple2f_ getLocation( Tuple2f_ loc )
    {
        location.get( loc );
        
        return ( loc );
    }
    
    /**
     * @return this Widget's left location relative to the left borderline of it's WidgetContainer
     */
    public final float getLeft()
    {
        return ( location.getX() );
    }
    
    /**
     * @return this Widget's top location relative to the upper borderline of it's WidgetContainer
     */
    public final float getTop()
    {
        return ( location.getY() );
    }
    
    protected float getMinWidth()
    {
        return ( -1f );
    }
    
    protected float getMinHeight()
    {
        return ( -1f );
    }
    
    /**
     * 
     * @param oldWidth
     * @param oldHeight
     * @param newWidth
     * @param newHeight
     */
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        updateSizeFactors();
    }
    
    protected final boolean setSize( float width, float height, boolean forced )
    {
        final float oldWidth = getWidth();
        final float oldHeight = getHeight();
        
        width = Math.max( getMinWidth(), width );
        height = Math.max( getMinHeight(), height );
        
        final boolean result = ( ( oldWidth != width ) || ( oldHeight != height ) );
        
        if ( !result && !forced )
        {
            return ( false );
        }
        
        if ( result || forced )
        {
            this.size.set( width, height );
            
            if ( ( shape != null ) && ( getHUD() != null ) )
            {
                float shapeWidth = width;
                float shapeHeight = height;
                
                Dim2f s = Dim2f.fromPool();
                DropShadowFactory dsf = getHUD().getDropShadowFactory();
                if ( ( dsf != null ) && hasDropShadow() )
                {
                    getSizePixels2HUD_( dsf.getDropShadowWidth(), dsf.getDropShadowHeight(), s );
                    shapeWidth += s.getWidth();
                    shapeHeight += s.getHeight();
                }
                getSizeHUD2SG_( shapeWidth, shapeHeight, s );
                shape.resize( s.getWidth(), s.getHeight() );
                Dim2f.toPool( s );
            }
        //}
        
        //if ( result )
        //{
            setTextureDirty();
            if ( getContainer() != null )
            {
                getContainer().setTextureDirty();
            }
            
            onSizeChanged( oldWidth, oldHeight, width, height );
            
            for ( int i = 0; i < sizeListeners.size(); i++ )
            {
                sizeListeners.get( i ).onWidgetSizeChanged( this, oldWidth, oldHeight, width, height );
            }
            
            if ( getContainer() != null )
            {
                getContainer().onChildMovedOrResized( this );
            }
        }
        
        return ( result );
    }
    
    /**
     * Resizes this Widget to the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * 
     * @return true, if the size actually has changed
     */
    public final Widget setSize( float width, float height )
    {
        setSize( width, height, false );
        
        return ( this );
    }
    
    /**
     * Resizes this Widget to the given width and height.
     * 
     * @param size the new size of this Widget
     * 
     * @return true, if the size actually has changed
     */
    public final Widget setSize( Sized2fRO size )
    {
        return ( setSize( size.getWidth(), size.getHeight() ) );
    }
    
    /**
     * Resizes this Widget to the given width and height.
     * 
     * @param size the new size of this Widget
     * 
     * @return true, if the size actually has changed
     */
    public final Widget setSize( Tuple2f size )
    {
        return ( setSize( size.getX(), size.getY() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setWidth( float width )
    {
        setSize( width, getHeight() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setHeight( float height )
    {
        setSize( getWidth(), height );
    }
    
    /**
     * @return this Widget's width
     */
    public final float getWidth()
    {
        return ( size.getWidth() );
    }
    
    /**
     * Gets the width of the widget's area (in pixels).
     * 
     * @return the width.
     */
    protected final int getWidthPX()
    {
        if ( getHUD() == null )
            throw new Error( "This widget is not attached to the HUD." );
        
        return ( widthPX );
    }
    
    /**
     * @return this Widget's height
     */
    public final float getHeight()
    {
        return ( size.getHeight() );
    }
    
    /**
     * Gets the height of the widget's area (in pixels).
     * 
     * @return the width.
     */
    protected final int getHeightPX()
    {
        if ( getHUD() == null )
            throw new Error( "This widget is not attached to the HUD." );
        
        return ( heightPX );
    }
    
    /**
     * @return the size of this Widget (in container meatures)
     */
    public final Sized2fRO getSize()
    {
        return ( new Dim2f( size ) );
    }
    
    /**
     * @return the aspect ratio (width / height) of this Widget
     */
    public final float getAspect()
    {
        if ( size.getHeight() == 0f )
            return ( 0f );
        
        return ( size.getWidth() / size.getHeight() );
    }
    
    /**
     * Sets the z-index of this Widget.
     * Larger values bring are nearer.
     */
    public void setZIndex( int zIndex )
    {
        if ( zIndex == this.zIndex )
            return;
        
        this.zIndex = zIndex;
        
        if ( getContainer() != null )
        {
            getContainer().setZIndexSortingDirty();
            
            if ( !isHeavyWeight() )
                getContainer().setTextureDirty();
        }
        
        updateTranslation();
    }
    
    /**
     * @return the z-index of this Widget.
     * Larger values bring are nearer.
     */
    public final int getZIndex()
    {
        return ( zIndex );
    }
    
    public final int compareAbsZIndex( Widget widget2 )
    {
        return ( this.zIndexComparable.compareTo( widget2.zIndexComparable ) );
    }
    
    /**
     * @return the width on which to pick. By default this is exactly getWidth().
     */
    protected float getPickWidth()
    {
        return ( getWidth() );
    }
    
    /**
     * @return the height on which to pick. By default this is exactly getHeight().
     */
    protected float getPickHeight()
    {
        return ( getHeight() );
    }
    
    protected boolean pickConditionsMatch( HUDPickReason pickReason )
    {
        if ( getHUD() == null )
            return ( false );
        
        if ( !isPickable() || !isVisible() )
            return ( false );
        
        if ( ( ( pickReason == HUDPickReason.BUTTON_PRESSED ) || ( pickReason == HUDPickReason.BUTTON_RELEASED ) ) && !isClickable() )
            return ( false );
        
        return ( true );
    }
    
    /**
     * Dispatches the picking to the WidgetAssembler.
     * 
     * @return the pickresult of the WidgetAssembler or null
     */
    private boolean pickWidgetAssembler( int canvasX, int canvasY, float widgetX, float widgetY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        if ( ( getWidgetAssembler() == null ) || !getWidgetAssembler().isPickingDispatched() )
            return ( false );
        
        return ( getWidgetAssembler().pick( canvasX, canvasY, widgetX, widgetY, pickReason, button, when, meta, flags ) );
    }
    
    /**
     * Tests whether a Widget is under the cursor and runs the approriate methods if true.
     * 
     * @param canvasX the x position of the mouse on the Canvas3D
     * @param canvasY the y position of the mouse on the Canvas3D
     * @param pickReason the action which caused this pick operation
     * @param button the mouse-button, that caused the picking
     * @param when the timestamp of the picking
     * @param meta this could be either the lastPressTime, lastReleaseTime, buttonsState mask or the page-move-boolean. (depends on the pickReason)
     * @param flags
     * 
     * @return an instance of HUDPickResult holding the picked Widget and absolute and relative picking positions or null.
     */
    protected HUDPickResult pick( int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        if ( !pickConditionsMatch( pickReason ) )
            return ( null );
        
        final Tuple2f locP = Tuple2f.fromPool();
        getLocationPixels2HUD_( canvasX, canvasY, locP );
        float pickXHUD = locP.getX();
        float pickYHUD = locP.getY();
        Tuple2f.toPool( locP );
        
        if ( ( 0f > pickXHUD ) || ( pickXHUD > getPickWidth() ) || ( 0f > pickYHUD ) || ( pickYHUD > getPickHeight() ) )
            return ( null );
        
        HUDPickResult hpr = HUDPickResult.fromPool();
        hpr.set( this, this.getCursor(), pickXHUD + getLeft(), pickYHUD + getTop(), pickXHUD, pickYHUD, pickReason, button );
        
        if ( ( flags & HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL ) != 0 ) // TODO: Check, if this is the correct condition!
        {
            pickWidgetAssembler( canvasX, canvasY, pickXHUD, pickYHUD, pickReason, button, when, meta, flags );
        }
        
        return ( hpr );
    }
    
    /**
     * Is the init method currently being executed?
     */
    protected final boolean isInitializing()
    {
        return ( isInitializing );
    }
    
    /**
     * Has the init method been executed once?
     */
    protected final boolean isInitialized()
    {
        return ( isInitialized );
    }
    
    protected void initSize()
    {
    }
    
    protected void createShape()
    {
        if ( !isHeavyWeight )
            return;
        
        final Dim2f size2 = Dim2f.fromPool();
        
        float shapeWidth = getWidth();
        float shapeHeight = getHeight();
        
        DropShadowFactory dsf = getHUD().getDropShadowFactory();
        if ( ( dsf != null ) && hasDropShadow() )
        {
            getSizePixels2HUD_( dsf.getDropShadowWidth(), dsf.getDropShadowHeight(), size2 );
            shapeWidth += size2.getWidth();
            shapeHeight += size2.getHeight();
        }
        getSizeHUD2SG_( shapeWidth, shapeHeight, size2 );
        
        Node.pushGlobalIgnoreBounds( true );
        this.shape = new DrawRectangle( size2.getWidth(), size2.getHeight(), false, true, true );
        Node.popGlobalIgnoreBounds();
        shape.getAppearance().setRenderingAttributes( RENDERING_ATTRIBUTES );
        shape.setCustomComparable( zIndexComparable );
        shape.getGeometry().setOptimization( Optimization.USE_VBOS );
        transformGroup.addChild( shape );
        
        Dim2f.toPool( size2 );
        
        shape.getTextureCanvas().addDrawCallback( drawCallback );
    }
    
    /**
     * This method is called when the WidgetContainer is set.
     */
    protected abstract void init();
    
    boolean checkHiearchyIntegrity()
    {
        if ( hierarchyOK == Boolean.TRUE )
            return ( true );
        
        if ( isHeavyWeight() )
        {
            hierarchyOK = Boolean.TRUE;
            return ( true );
        }
        
        if ( getContainer() == null )
        {
            hierarchyOK = ( getAssembly() != null );
        }
        else
        {
            hierarchyOK = getContainer().checkHiearchyIntegrity();
        }
        
        if ( !hierarchyOK )
            throw new Error( "The hiearchy of a Widget is not ok. Any lightweight Widget must be in a hierarchy, where a (grand-)parent Widget is heavyweight. Check the HUD's ContentPane for being heavyweight." );
        
        return ( true );
    }
    
    /**
     * This event is fired, when this Widget is added to the HUD live Widget hierarchy.
     * 
     * @param hud the HUD, the Widget is added to
     */
    protected void onAttachedToHUD( HUD hud )
    {
        checkHiearchyIntegrity();
        
        updateSizeFactors();
        
        updateTranslation();
        if ( !isInitialized() )
        {
            isInitializing = true;
            initSize();
            createShape();
            init();
            isInitializing = false;
            if ( getWidgetAssembler() != null )
            {
                getWidgetAssembler().update();
            }
            
            isInitialized = true;
        }
        setSize( getWidth(), getHeight(), true );
        
        //if ( getWidgetAssembler() != null )
        //    getWidgetAssembler().updateLocations( getLeft(), getTop() );
        
        if ( focusRequested )
        {
            container.focus( this );
            focusRequested = false;
        }
        
        for ( int i = 0; i < containerListeners.size(); i++ )
            containerListeners.get( i ).onWidgetAttachedToHUD( this, hud );
    }
    
    /**
     * This event is fired, when this Widget is removed from the HUD live Widget hierarchy.
     * 
     * @param hud the HUD, the Widget is removed from
     */
    protected void onDetachedFromHUD( HUD hud )
    {
        hierarchyOK = null;
        
        for ( int i = 0; i < containerListeners.size(); i++ )
            containerListeners.get( i ).onWidgetDetachedFromHUD( this, hud );
    }
    
    void setHUD( HUD hud )
    {
        if ( this.hud == hud )
            return;
        
        HUD oldHUD = this.hud;
        this.hud = hud;
        
        if ( getWidgetAssembler() != null )
            getWidgetAssembler().setHUD( hud );
        
        if ( hud != null )
            onAttachedToHUD( hud );
        else
            onDetachedFromHUD( oldHUD );
    }
    
    /**
     * @return the HUD this Widget belongs to
     */
    public final HUD getHUD()
    {
        return ( hud );
    }
    
    /**
     * This event is fired, when this Widget is added to a WidgetContainer.
     * 
     * @param container the WidgetContainer, the Widget is added to
     */
    protected void onAttachedToContainer( WidgetContainer container )
    {
        if ( ( container.getSGGroup() != null ) && ( this.getSGNode() != null ) )
        {
            container.getSGGroup().addChild( this.getSGNode() );
        }
        
        container.notifyContainerListenersAboutAttachedWidget( this, container );
        this.notifyContainerListenersAboutAttachedWidget( this, container );
    }
    
    /**
     * This event is fired, when this Widget is removed from a WidgetContainer.
     * 
     * @param container the WidgetContainer, the Widget is removed from
     */
    protected void onDetachedFromContainer( WidgetContainer container )
    {
        if ( ( container.getSGGroup() != null ) && ( this.getSGNode() != null ) )
        {
            container.getSGGroup().removeChild( this.getSGNode() );
        }
        
        container.notifyContainerListenersAboutDetachedWidget( this, container );
        this.notifyContainerListenersAboutDetachedWidget( this, container );
    }
    
    /**
     * Sets this Widget's container.
     * 
     * @param container the new Container
     * @param assembly
     */
    final void setContainer( WidgetContainer container, Widget assembly )
    {
        boolean containerChanged = ( this.container != container );
        boolean assemblyChanged = ( this.assembly != assembly );
        
        if ( !containerChanged && !assemblyChanged )
            return;
        
        WidgetContainer oldContainer = this.container;
        this.container = container;
        this.assembly = assembly;
        
        if ( containerChanged )
        {
            if ( getWidgetAssembler() != null )
                getWidgetAssembler().setContainer( container );
            
            if ( container != null )
                onAttachedToContainer( container );
            else
                onDetachedFromContainer( oldContainer );
        }
    }
    
    /**
     * The Container which contains this Widget
     */
    public final WidgetContainer getContainer()
    {
        return ( container );
    }
    
    /**
     * @return the Widget, which uses this one to assemle itself, if any.
     */
    protected final Widget getAssembly()
    {
        return ( assembly );
    }
    
    /**
     * Removes the Widget from its Container.
     */
    public void detach()
    {
        if ( getAssembly() != null )
        {
            getAssembly().getWidgetAssembler().removeWidget( this );
        }
        else if ( getContainer() != null )
        {
            getContainer().removeWidget( this );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( this instanceof TextWidget )
        {
            String text = String.valueOf( ( (TextWidget)this ).getText() ).replaceAll( "\n", "\\\\n" );
            
            if ( ( getName() != null ) && ( getName().length() > 0 ) )
                return ( this.getClass().getSimpleName() + "( \"" + getName() + "\", " + text + "\" )" );
            
            return ( this.getClass().getSimpleName() + "( \"" + text + "\" )" );
        }
        
        if ( ( getName() != null ) && ( getName().length() > 0 ) )
        {
            return ( this.getClass().getSimpleName() + "( \"" + getName() + "\" )" );
        }
        
        return ( super.toString() );
    }
    
    /**
     * Returns the untilized {@link Shape3D} to display the {@link Widget}.<br>
     * For most widget types, this will be a {@link DrawRectangle}.<br>
     * If this Widget is lightweight, this method will return null.
     * 
     * @return the untilized {@link Shape3D} to display the {@link Widget}.
     */
    public Shape3D getShape()
    {
        return ( shape );
    }
    
    protected void setHostWidget( Widget widget )
    {
        this.hostWidget = widget;
    }
    
    /**
     * Returns the Widget, that this lightweight Widget draws on.
     * 
     * @return the host Widget.
     */
    protected final Widget getRootHostWidget()
    {
        if ( hostWidget == null )
        {
            if ( isHeavyWeight() )
                return ( this );
            
            return ( null );
        }
        
        return ( hostWidget.getRootHostWidget() );
    }
    
    final void setPassive( boolean passive )
    {
        this.isPassive = passive;
        
        if ( getWidgetAssembler() != null )
        {
            getWidgetAssembler().setWidgetsPassive( passive );
        }
    }
    
    protected void setHostedWidgetDirty()
    {
        isAHostedWidgetDirty = true;
    }
    
    protected void setWidgetDirty()
    {
        isThisWidgetDirty = true;
        
        if ( getWidgetAssembler() != null )
        {
            getWidgetAssembler().setWidgetsDirty();
        }
    }
    
    protected void resetWidgetDirty()
    {
        this.isThisWidgetDirty = false;
        this.isAHostedWidgetDirty = false;
    }
    
    protected final boolean isThisWidgetDirty()
    {
        return ( isThisWidgetDirty );
    }
    
    protected final boolean isAHostedWidgetDirty()
    {
        return ( isAHostedWidgetDirty );
    }
    
    /**
     * 
     * @param flags
     */
    protected void setHostTextureDirty( int flags )
    {
        if ( isPassive )
            return;
        
        setHostedWidgetDirty();
        
        if ( isHeavyWeight() )
        {
            this.drawCallback.setDirty( true );
            return;
        }
        
        Widget hostWidget = this.hostWidget;
        
        if ( hostWidget != null )
        {
            hostWidget.setHostTextureDirty( flags );
            return;
        }
        
        /*
        if ( getHUD() != null )
        {
            throw new Error( "A lightweight Widget must have a (grand-, ...)parent heavyweight Widget!" );
        }
        */
    }
    
    protected final void setHostTextureDirty()
    {
        setHostTextureDirty( 0 );
    }
    
    /**
     * 
     * @param flags
     */
    protected void setTextureDirty( int flags )
    {
        if ( isPassive )
            return;
        
        if ( getAssembly() != null )
        {
            getAssembly().setTextureDirty( flags );
            return;
        }
        
        setWidgetDirty();
        
        if ( isHeavyWeight() )
        {
            this.drawCallback.setDirty( true );
            return;
        }
        
        //Widget hostWidget = getHostWidget();
        Widget hostWidget = this.hostWidget;
        
        if ( hostWidget != null )
        {
            //hostWidget.setTextureDirty();
            hostWidget.setHostTextureDirty( flags );
            return;
        }
        
        /*
        if ( getHUD() != null )
        {
            throw new Error( "A lightweight Widget must have a (grand-, ...)parent heavyweight Widget!" );
        }
        */
    }
    
    protected final void setTextureDirty()
    {
        setTextureDirty( 0 );
    }
    
    /**
     * Draws the Widget's background.
     * 
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     * @param needsClearForNullBackground
     */
    protected void drawBackground( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        if ( isHeavyWeight() )
        {
            DrawUtils.clearImage( Colorf.BLACK_TRANSPARENT, null, null, texCanvas, offsetX, offsetY, width, height );
        }
        else if ( ( getContainer() != null ) && !getContainer().isThisWidgetDirty() && ( getAssembly() == null ) )
        {
            DropShadowFactory dsf = getHUD().getDropShadowFactory();
            if ( ( dsf == null ) || !hasDropShadow() )
            {
                getContainer().drawParentBackground( this, texCanvas, offsetX, offsetY, width, height );
            }
            else
            {
                getContainer().drawParentBackground( this, texCanvas, offsetX, offsetY, width + dsf.getDropShadowWidth(), height + dsf.getDropShadowHeight() );
            }
        }
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
    protected abstract void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf );
    
    /**
     * Draws the part of the Widget, that needs to be drawn after the WidgetAssembler.
     * 
     * @param texCanvas
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     * @param drawsSelf
     */
    protected void drawWidgetAfterWidgetAssembler( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
    }
    
    protected void drawBorder( Border border, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        border.drawBorder( texCanvas, offsetX, offsetY, width, height, this );
    }
    
    protected void setContentClipRect( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        texCanvas.setClip( offsetX, offsetY, width, height );
    }
    
    protected void drawWidgetContents( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        int contentLeft = offsetX + getContentLeftPX();
        int contentTop = offsetY + getContentTopPX();
        int contentWidth = getContentWidthPX();
        int contentHeight = getContentHeightPX();
        
        clip.set( contentLeft, contentTop, contentWidth, contentHeight );
        clip.clamp( oldClip );
        setContentClipRect( texCanvas, clip.getLeft(), clip.getTop(), clip.getWidth(), clip.getHeight() );
        
        drawWidget( texCanvas, contentLeft, contentTop, contentWidth, contentHeight, drawsSelf );
        
        if ( getWidgetAssembler() != null )
        {
            clip.set( offsetX, offsetY, width, height );
            clip.clamp( oldClip );
            texCanvas.setClip( clip );
            
            getWidgetAssembler().draw( texCanvas, offsetX, offsetY );
            
            clip.set( contentLeft, contentTop, contentWidth, contentHeight );
            clip.clamp( oldClip );
            setContentClipRect( texCanvas, clip.getLeft(), clip.getTop(), clip.getWidth(), clip.getHeight() );
        }
        
        drawWidgetAfterWidgetAssembler( texCanvas, contentLeft, contentTop, contentWidth, contentHeight, drawsSelf );
    }
    
    public void drawAndUpdateWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        if ( isThisWidgetDirty || isPassive )
        {
            texCanvas.getClip( oldClip );
            
            DropShadowFactory dsf = getHUD().getDropShadowFactory();
            if ( ( dsf == null ) || !hasDropShadow() )
            {
                clip.set( offsetX, offsetY, width, height );
                clip.clamp( oldClip );
                texCanvas.setClip( clip );
                
                texCanvas.beginUpdateRegion( offsetX, offsetY, width, height );
            }
            else
            {
                clip.set( offsetX, offsetY, width + dsf.getDropShadowWidth(), height + dsf.getDropShadowHeight() );
                clip.clamp( oldClip );
                texCanvas.setClip( clip );
                
                texCanvas.beginUpdateRegion( offsetX, offsetY, width + dsf.getDropShadowWidth(), height + dsf.getDropShadowHeight() );
                dsf.drawDropShadow( offsetX + width, offsetY + height, width, height, getZIndex(), texCanvas );
                
                clip.set( offsetX, offsetY, width, height );
                clip.clamp( oldClip );
                texCanvas.setClip( clip );
            }
            
            drawBackground( texCanvas, offsetX, offsetY, width, height );
            
            drawWidgetContents( texCanvas, offsetX, offsetY, width, height, drawsSelf );
            
            if ( getBorder() != null )
            {
                clip.set( offsetX, offsetY, width, height );
                clip.clamp( oldClip );
                texCanvas.setClip( clip );
                
                drawBorder( getBorder(), texCanvas, offsetX, offsetY, width, height );
            }
            
            texCanvas.finishUpdateRegion();
            texCanvas.setClip( oldClip );
        }
        else if ( isAHostedWidgetDirty )
        {
            drawWidgetContents( texCanvas, offsetX, offsetY, width, height, drawsSelf );
        }
        
        resetWidgetDirty();
    }
    
    /**
     * Sets the maximum frequency, at which a Widget can be redrawn.
     * 
     * @param freq frequency in Hz
     */
    public static final void setMaxRedrawFrequency( float freq )
    {
        MIN_UPDATE_DELAY.setValue( (long)( 1000000000f / freq ) );
    }
    
    /**
     * Gets the maximum frequency, at which a Widget can be redrawn.
     * 
     * @return the frequency in Hz.
     */
    public static final float getMaxRedrawFrequency()
    {
        return ( 1000000000f / MIN_UPDATE_DELAY.floatValue() );
    }
    
    /**
     * Sets the forced frequency, at which a Widget is redrawn.
     * 
     * @param freq frequency in Hz (-1 for off)
     */
    public void setForcedRedrawFrequency( float freq )
    {
        if ( !isHeavyWeight() )
            throw new Error( "A lightweight Widget cannot be forced redrawn." );
        
        if ( freq <= 0f )
        {
            this.forcedUpdateDelay = -1L;
            return;
        }
        
        this.forcedUpdateDelay = (long)( 1000000000f / freq );
    }
    
    /**
     * Gets the forced frequency, at which a Widget is redrawn.
     * 
     * @return the frequency in Hz (-1 for off).
     */
    public final float getForcedRedrawFrequency()
    {
        if ( forcedUpdateDelay <= 0L )
            return ( -1f );
        
        return ( 1000000000f / forcedUpdateDelay );
    }
    
    /**
     * Creates a new Widget.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     */
    protected Widget( boolean isHeavyWeight, boolean hasWidgetAssembler )
    {
        this.isHeavyWeight = isHeavyWeight;
        
        //if ( isHeavyWeight )
        {
            Node.pushGlobalIgnoreBounds( true );
            this.transformGroup = new TransformGroup();
            Node.popGlobalIgnoreBounds();
        }
        /*
        else
        {
            this.transformGroup = null;
        }
        */
        
        this.widgetAssembler = hasWidgetAssembler ? new WidgetAssembler( this ) : null;
        //this.setVisible( true );
        
        if ( isHeavyWeight )
        {
            this.drawCallback = new DrawCallback2D()
            {
                private boolean dirty = true;
                private long nextAllowedRedrawTime = -1L;
                private long nextForcedRedrawTime = -1L;
                
                @Override
                public void setDirty( boolean dirty )
                {
                    this.dirty = dirty;
                }
                
                @Override
                public boolean needsRedraw( long nanoTime )
                {
                    //if ( true ) { isThisWidgetDirty = true; return ( true ); }
                    if ( dirty )
                    {
                        if ( nanoTime < nextAllowedRedrawTime )
                            return ( false );
                        
                        nextAllowedRedrawTime += MIN_UPDATE_DELAY.longValue();
                    }
                    else if ( ( forcedUpdateDelay > 0L ) && ( nanoTime >= nextForcedRedrawTime ) )
                    {
                        nextForcedRedrawTime += forcedUpdateDelay;
                        setWidgetDirty();
                        return ( true );
                    }
                    
                    boolean result = dirty;
                    dirty = false;
                    
                    return ( result );
                }
                
                @Override
                public void drawTexture( Texture2DCanvas texCanvas, int texWidth, int texHeight )
                {
                    texCanvas.setClip( 0, 0, texWidth, texHeight );
                    
                    DropShadowFactory dsf = getHUD().getDropShadowFactory();
                    if ( ( dsf != null ) && hasDropShadow() )
                    {
                        texWidth -= dsf.getDropShadowWidth();
                        texHeight -= dsf.getDropShadowHeight();
                    }
                    
                    drawAndUpdateWidget( texCanvas, 0, 0, texWidth, texHeight, true );
                }
            };
        }
        else
        {
            this.drawCallback = null;
        }
    }
    
    /**
     * Creates a new Widget with the given width and height.
     * 
     * @param isHeavyWeight
     * @param hasWidgetAssembler
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param zIndex the z-index of this Widget
     */
    protected Widget( boolean isHeavyWeight, boolean hasWidgetAssembler, float width, float height )
    {
        this( isHeavyWeight, hasWidgetAssembler );
        
        this.size.set( Math.max( getMinWidth(), width ), Math.max( getMinHeight(), height ) );
    }
}
