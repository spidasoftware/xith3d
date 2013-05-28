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
package org.xith3d.ui.hud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.Mouse;
import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.DigitalDeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.Keys;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.util.arrays.ArrayUtils;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.ExtSized2i;
import org.openmali.types.twodee.Sized2fRO;
import org.openmali.types.twodee.Sized2i;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.types.twodee.util.ResizeListener2i;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Point2i;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple2i;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.OperationSchedulerImpl;
import org.xith3d.render.ForegroundRenderPass;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.render.preprocessing.sorting.CustomRenderBinSorter;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.SceneGraph;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.View.CameraMode;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.base.Window;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;
import org.xith3d.ui.hud.contextmenu.ContextMenu;
import org.xith3d.ui.hud.listeners.HUDPickMissedListener;
import org.xith3d.ui.hud.listeners.WidgetContainerListener;
import org.xith3d.ui.hud.listeners.WidgetInputListener;
import org.xith3d.ui.hud.theming.FallbackTheme;
import org.xith3d.ui.hud.theming.WidgetTheme;
import org.xith3d.ui.hud.utils.Cursor;
import org.xith3d.ui.hud.utils.CursorSet;
import org.xith3d.ui.hud.utils.DefaultDropShadowFactory;
import org.xith3d.ui.hud.utils.DefaultToolTipFactory;
import org.xith3d.ui.hud.utils.DropShadowFactory;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.utils.TileMode;
import org.xith3d.ui.hud.utils.ToolTipFactory;
import org.xith3d.ui.hud.utils.WidgetZIndexGroup;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;
import org.xith3d.ui.hud.widgets.Dialog;
import org.xith3d.ui.hud.widgets.Image;
import org.xith3d.ui.hud.widgets.Panel;

/**
 * This is a 3D HUD implementation. A HUD can contain WidgetContainers and Widgets.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HUD
{
    public static enum FocusMoveDirection
    {
        UP,
        LEFT,
        RIGHT,
        DOWN,
        NEXT,
        ;
    }
    
    /**
     * Since the inner size of a decorated Canvas3D window is not correct
     * before the first frame, the HUD size needs to be updated after the
     * first frame.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    private class HUDCanvas3DConnection implements ResizeListener2i
    {
        public void onObjectResized( Sized2i object, int oldWidth, int oldHeight, int newWidth, int newHeight )
        {
            HUD.this.setSize( newWidth, newHeight, -1f, -1f );
            
            fixRenderPass();
        }
    }
    
    public static final int WINDOW_BASE_Z_INDEX = (int)Short.MAX_VALUE + 2;
    public static final int POPUP_Z_INDEX = Integer.MAX_VALUE - 100;
    public static final int TOOLTIP_Z_INDEX = Integer.MAX_VALUE - 90;
    public static final int CROSSHAIR_Z_INDEX = (int)Short.MAX_VALUE + 1;
    public static final int CURSOR_Z_INDEX = Integer.MAX_VALUE - 1;
    
    private ForegroundRenderPass renderPass = null;
    private final BranchGroup branchGroup;
    private Dim2f size;
    private HUDCanvas3DConnection canvasConnection = null;
    private HUDInputSystemConnection inputSystemConnection = null;
    private Dim2f resolution = null;
    private final HUDCoordinatesConverter coordinatesConverter = new HUDCoordinatesConverter( this );
    private WidgetContainer contentPane;
    private final ArrayList<Window> windows = new ArrayList<Window>();
    private final WidgetZIndexGroup framesZIGroup = new WidgetZIndexGroup( 1 );
    private final Stack<Dialog> dialogs = new Stack<Dialog>();
    private Widget currentVolatilePopup = null;
    private Widget currentVolatilePopupAssembly = null;
    private final Tuple2f lastMousePos;
    private CursorSet cursorSet = null;
    private Cursor.Type currentCursorType = Cursor.Type.POINTER1;
    private Cursor currentCursor = null;
    private Image cursorImage = null;
    private Image crosshairImage = null;
    private int crosshairHotspotX = -2, crosshairHotspotY = -2;
    private Widget currentHoveredWidget = null;
    private Widget currentFocusedWidget = null;
    private Widget mouseBoundWidget = null;
    private final ArrayList<HUDPickResult> pickedWidgets = new ArrayList< HUDPickResult >();
    private final ArrayList<WidgetContainerListener> globalContainerListeners = new ArrayList<WidgetContainerListener>( 1 );
    private final ArrayList<WidgetInputListener> globalInputListeners = new ArrayList<WidgetInputListener>( 1 );
    private final ArrayList<HUDPickMissedListener> pickMissedListeners = new ArrayList<HUDPickMissedListener>( 1 );
    private final ArrayList<Integer> pickMissedMasks = new ArrayList<Integer>( 1 );
    
    private ContextMenu contextMenu = null;
    private ContextMenu currentlyDisplayedContextMenu = null;
    private boolean useVoidContextMenu = true;
    
    private ToolTipFactory tooltipFactory = new DefaultToolTipFactory();
    
    private Widget currentlyTooltippedWidget = null;
    private Point2i tooltipStartPoint = new Point2i();
    private Widget currentlyDisplayedTooltipWidget = null;
    
    private DropShadowFactory dropshadowFactory = new DefaultDropShadowFactory();
    
    private final OperationScheduler opScheduler = new OperationSchedulerImpl();
    
    private transient static WidgetTheme theme = null;
    
    private DeviceComponent[] focusMoveUpAccessors = new DeviceComponent[] { Keys.UP };
    private DeviceComponent[] focusMoveLeftAccessors = new DeviceComponent[] { Keys.LEFT };
    private DeviceComponent[] focusMoveRightAccessors = new DeviceComponent[] { Keys.RIGHT };
    private DeviceComponent[] focusMoveDownAccessors = new DeviceComponent[] { Keys.DOWN };
    private DeviceComponent[] focusMoveNextAccessors = new DeviceComponent[] { Keys.TAB };
    
    private final void fixRenderPass()
    {
        renderPass.getConfig().setScreenScale( getWidth() / 2.0f );
        renderPass.getConfig().setCenterOfView( new Point2f( -getWidth() / 2.0f, getHeight() / 2.0f ) );
    }
    
    private final ForegroundRenderPass createRenderPass( BranchGroup bg )
    {
        this.renderPass = ForegroundRenderPass.createParallel( bg );
        
        final RenderPassConfig passConfig = renderPass.getConfig();
        
        passConfig.setCameraMode( CameraMode.VIEW_FIXED );
        
        passConfig.setOpaqueSorter( new CustomRenderBinSorter() );
        passConfig.setTransparentSorter( new CustomRenderBinSorter() );
        passConfig.setFrontClipDistance( -1.0f );
        passConfig.setBackClipDistance( +1.0f );
        //passConfig.setFieldOfView( fov );
        
        fixRenderPass();
        
        //renderPass.setClipperEnabled( false );
        renderPass.setScissorEnabled( true );
        //renderPass.setFrustumCullingEnabled( false );
        
        return ( renderPass );
    }
    
    /**
     * Walks up the Scenegraph to the root BranchGroup. If there is one the
     * assotiated RenderPass is searched for and returned.
     * If this HUD is not attached to a BranchGroup or the BranchGroup
     * is not assitiated with a RenderPass they are created.
     * 
     * @return the RenderPass to add to the Renderer or Xith3DEnvironment
     */
    public final ForegroundRenderPass getRenderPass()
    {
        if ( this.renderPass == null )
        {
            this.renderPass = createRenderPass( branchGroup );
        }
        
        return ( this.renderPass );
    }
    
    /**
     * {@inheritDoc}
     */
    public final BranchGroup getSGGroup()
    {
        return ( branchGroup );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void detach()
    {
        BranchGroup bg = getSGGroup();
        
        if ( bg == null )
            return;
        
        SceneGraph sg = bg.getSceneGraph();
        
        if ( sg == null )
            return;
        
        sg.removeHUD( this );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setName( String name )
    {
        getSGGroup().setName( name );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getName()
    {
        return ( getSGGroup().getName() );
    }
    
    /**
     * Sets the WidgetTheme to use for default textures and some default properties
     * 
     * @param theme the new WidgetTheme to use
     */
    public static void setTheme( WidgetTheme theme )
    {
        HUD.theme = theme;
    }
    
    /**
     * Sets the built-in WidgetTheme to use for default textures and some default properties
     * 
     * @param theme the new WidgetTheme to use
     */
    public static void setTheme( String theme ) throws IOException
    {
        HUD.theme = new WidgetTheme( theme );
    }
    
    /**
     * @return the WidgetTheme to use for default textures and some default properties
     */
    public static final WidgetTheme getTheme()
    {
        if ( HUD.theme == null )
        {
            final String THEME_NAME = "GTK";
            
            try
            {
                HUD.theme = new WidgetTheme( THEME_NAME );
            }
            catch ( IOException e )
            {
                System.err.println( "WARNING: Theme \"" + THEME_NAME + "\" could not get loaded. Using Fallback-Theme" );
                HUD.theme = new FallbackTheme();
            }
        }
        
        return ( HUD.theme  );
    }
    
    /**
     * Adds a new HUDPickMissedListener to be notified of a pick event, that
     * didn't hit any Widget.
     * 
     * @see HUDPickReason#getMaskValue()
     * 
     * @param mask a bitmask to define, which {@link HUDPickReason}s cause the listener to be notified.
     * @param l
     */
    public void addPickMissedListener( int mask, HUDPickMissedListener l )
    {
        this.pickMissedListeners.add( l );
        this.pickMissedMasks.add( mask );
    }
    
    /**
     * Removes a HUDPickMissedListener.
     * 
     * @param l
     */
    public void removePickMissedListener( HUDPickMissedListener l )
    {
        int index = pickMissedListeners.indexOf( l );
        
        if ( index < 0 )
            return;
        
        this.pickMissedListeners.remove( index );
        this.pickMissedMasks.remove( index );
    }
    
    /**
     * Adds a new WidgetInputListener to be notified of global widget input events.
     * 
     * @param l
     */
    public void addGlobalInputListener( WidgetInputListener l )
    {
        this.globalInputListeners.add( l );
    }
    
    /**
     * Removes a global WidgetInputListener.
     * 
     * @param l
     */
    public void removeGlobalInputListener( WidgetInputListener l )
    {
        this.globalInputListeners.remove( l );
    }
    
    /**
     * Adds a new WidgetContainerListener to be notified of global widget container events.
     * 
     * @param l
     */
    public void addGlobalContainerListener( WidgetContainerListener l )
    {
        this.globalContainerListeners.add( l );
    }
    
    /**
     * Removes a global WidgetContainerListener.
     * 
     * @param l
     */
    public void removeGlobalContainerListener( WidgetContainerListener l )
    {
        this.globalContainerListeners.remove( l );
    }
    
    /**
     * Unfocusses all Widgets.
     */
    public void disposeFocus()
    {
        if ( currentFocusedWidget != null )
            __HUD_base_PrivilegedAccess.onFocusLost( currentFocusedWidget );
        
        currentFocusedWidget = null;
    }
    
    /**
     * Sets the whole HUD visible/invisible.
     * 
     * @param visible
     */
    public void setVisible( boolean visible )
    {
        this.getSGGroup().setRenderable( visible );
    }
    
    /**
     * Gets, if the whole HUD is visible/invisible.
     * 
     * @return the whole HUD's visibility.
     */
    public final boolean isVisible()
    {
        return ( this.getSGGroup().isRenderable() );
    }
    
    /**
     * Sets all Widgets' transparency.
     * 
     * @param transparency
     */
    public void setTransparency( float transparency )
    {
        for ( int i = 0; i < windows.size(); i++ )
        {
            windows.get( i ).setTransparency( transparency );
        }
        
        contentPane.setTransparency( transparency, true );
    }
    
    /**
     * Gets the content pane's transparency.
     * 
     * @return the transparency.
     */
    public final float getTransparency()
    {
        return ( contentPane.getTransparency() );
    }
    
    /**
     * @return this HUD's width
     */
    public final float getWidth()
    {
        if ( size != null )
            return ( size.getWidth() );
        
        return ( -1.0f );
    }
    
    /**
     * @return this HUD's height
     */
    public final float getHeight()
    {
        if ( size != null )
            return ( size.getHeight() );
        
        return ( -1.0f );
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getAspect()
    {
        //if ( getHeight() == 0f )
        //    return ( 0f );
        
        return ( getWidth() / getHeight() );
    }
    
    /**
     * @return this HUD's size
     */
    public final Sized2fRO getSize()
    {
        return ( size );
    }
    
    /**
     * Returns whether a custom resolution is defined on the hud.
     * 
     * @return whether a custom resolution is defined on the hud.
     */
    public final boolean hasCustomResolution()
    {
        return ( resolution != null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getResX()
    {
        if ( resolution == null )
            return ( getWidth() );
        
        return ( resolution.getWidth() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getResY()
    {
        if ( resolution == null )
            return ( getHeight() );
        
        return ( resolution.getHeight() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Tuple2f getResolution()
    {
        return ( new Tuple2f( getResX(), getResY() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getResAspect()
    {
        if ( getResY() == 0f )
            return ( 0f );
        
        return ( getResX() / getResY() );
    }
    
    /**
     * Returns a converter utility to convert from different coorcinate spaces.
     * 
     * @return a converter utility to convert from different coorcinate spaces.
     */
    public final HUDCoordinatesConverter getCoordinatesConverter()
    {
        return ( coordinatesConverter );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update()
    {
        contentPane.update();
        
        for ( int i = 0; i < windows.size(); i++ )
        {
            windows.get( i ).update();
        }
        
        if ( currentVolatilePopup != null )
            currentVolatilePopup.update();
        
        if ( currentlyDisplayedTooltipWidget != null )
            currentlyDisplayedTooltipWidget.update();
    }
    
    /**
     * Resizes this HUD's coordinate system to the given width and height.
     * 
     * @param resX the new width of this HUD's coordinate system
     * @param resY the new height of this HUD's coordinate system
     * @param forced
     */
    protected boolean setSize( float resX, float resY, boolean forced )
    {
        final boolean result = true;
        
        boolean contentPaneHasFullSize = ( contentPane.getWidth() == this.getResX() ) && ( contentPane.getHeight() == this.getResY() );
        
        if ( ( resY >= 0f ) && ( resY >= 0f ) )
        {
            this.resolution = new Dim2f( resX, resY );
        }
        
        if ( contentPaneHasFullSize )
        {
            contentPane.setSize( this.getResX(), this.getResY() );
        }
        
        update();
        
        return ( result );
    }
    
    /**
     * Resizes this HUD's coordinate system to the given width and height.
     * 
     * @param resX the new width of this HUD's coordinate system
     * @param resY the new height of this HUD's coordinate system
     */
    public final HUD setSize( float resX, float resY )
    {
        setSize( resX, resY, false );
        
        return ( this );
    }
    
    /**
     * Resizes this HUD's coordinate system to the given width and height.
     * 
     * @param resolution the new resolution of this HUD's coordinate system
     */
    public final HUD setSize( Sized2fRO resolution )
    {
        return ( setSize( resolution.getWidth(), resolution.getHeight() ) );
    }
    
    /**
     * Resizes this HUD's coordinate system to the given width and height.
     * 
     * @param resolution the new resolution of this HUD's coordinate system
     */
    public final HUD setSize( Tuple2f resolution )
    {
        return ( setSize( resolution.getX(), resolution.getY() ) );
    }
    
    /**
     * Creates a new coordinate system on the HUD.
     * It doesn't actually resize the HUD.
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resX the view width of the coordinate system
     * @param resY the view height of the coordinate system
     */
    public final void setSize( int canvasWidth, int canvasHeight, float resX, float resY )
    {
        if ( this.size == null )
            this.size = new Dim2f();
        this.size.set( canvasWidth, (float)canvasHeight );
        
        //final boolean result = ( ( this.resolution == null ) || ( this.resolution.getWidth() != resX ) || ( this.resolution.getHeight() != resY ) );
        setSize( resX, resY );
        
        this.update();
        
        //return ( result );
    }
    
    /**
     * Creates a new coordinate system on the HUD.
     * It doesn't actually resize the HUD.
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resX the view width of the coordinate system
     * @param resY the view height of the coordinate system
     */
    public final void setSize( Sized2iRO canvas, float resX, float resY )
    {
        setSize( canvas.getWidth(), canvas.getHeight(), resX, resY );
    }
    
    /**
     * Creates a new coordinate system on the HUD.
     * It doesn't actually resize the HUD.
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resolution the resolution of the coordinate system
     */
    public final void setSize( int canvasWidth, int canvasHeight, Sized2fRO resolution )
    {
        if ( resolution != null )
            setSize( canvasWidth, canvasHeight, resolution.getWidth(), resolution.getHeight() );
        else
            setSize( canvasWidth, canvasHeight, -1f, -1f );
    }
    
    /**
     * Creates a new coordinate system on the HUD.
     * It doesn't actually resize the HUD.
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resolution the resolution of the coordinate system
     */
    public final void setSize( Sized2iRO canvas, Sized2fRO resolution )
    {
        if ( resolution != null )
            setSize( canvas, resolution.getWidth(), resolution.getHeight() );
        else
            setSize( canvas, -1f, -1f );
    }
    
    /**
     * Creates a new coordinate system on the HUD.
     * It doesn't actually resize the HUD.
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     */
    public final void setSize( int canvasWidth, int canvasHeight )
    {
        setSize( canvasWidth, canvasHeight, canvasWidth, canvasHeight );
    }
    
    /**
     * Creates a new coordinate system on the HUD.
     * It doesn't actually resize the HUD.
     * 
     * @param canvas the canvas to take the resolution and aspect from
     */
    public final void setSize( Sized2iRO canvas )
    {
        setSize( canvas, (Sized2fRO)null );
    }
    
    public final void setWidth( float width )
    {
        setSize( width, getResX() );
    }
    
    public final void setHeight( float height )
    {
        setSize( getResX(), height );
    }
    
    /**
     * {@inheritDoc}
     */
    public Window addWindow( Window window, float locX, float locY )
    {
        if ( window.getHUD() != null )
        {
            throw new Error( "This Widget is already added to the HUD." );
        }
        
        int maxZIndex = WINDOW_BASE_Z_INDEX - 1;
        for ( int i = 0; i < windows.size(); i++ )
        {
            final Window wnd = windows.get( i );
            
            if ( wnd.getZIndex() > maxZIndex )
                maxZIndex = wnd.getZIndex();
        }
        
        window.setZIndex( maxZIndex + 1 );
        framesZIGroup.add( window );
        
        windows.add( window );
        
        currentFocusedWidget = window;
        
        if ( inputSystemConnection != null )
            inputSystemConnection.nextAcceptedMouseEventTime = System.currentTimeMillis() + 200L;
        
        if ( window instanceof Dialog )
        {
            dialogs.push( (Dialog)window );
        }
        
        window.setLocation( locX, locY );
        __HUD_base_PrivilegedAccess.setHUD( this, window );
        
        branchGroup.addChild( __HUD_base_PrivilegedAccess.getSGNode( window ) );
        
        for ( int i = 0; i < globalContainerListeners.size(); i++ )
        {
            globalContainerListeners.get( i ).onWidgetAttachedToHUD( window, this );
        }
        
        return ( window );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Window addWindow( Window window )
    {
        return ( addWindow( window, window.getLeft(), window.getTop() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Window addWindowCentered( Window window )
    {
        float posUpperLeftX = ( this.getResX() - window.getWidth() ) / 2.0f;
        float posUpperLeftY = ( this.getResY() - window.getHeight() ) / 2.0f;
        
        posUpperLeftX = Math.round( posUpperLeftX );
        posUpperLeftY = Math.round( posUpperLeftY );
        
        return ( addWindow( window, posUpperLeftX, posUpperLeftY ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeWindow( Window window )
    {
        if ( window.getHUD() != this )
            throw new Error( "the given Widget is not held in this Container." );
        
        branchGroup.removeChild( __HUD_base_PrivilegedAccess.getSGNode( window ) );
        
        if ( window instanceof Dialog )
        {
            dialogs.remove( window );
        }
        
        windows.remove( window );
        
        framesZIGroup.remove( window );
        
        __HUD_base_PrivilegedAccess.setHUD( null, window );
        
        for ( int i = 0; i < globalContainerListeners.size(); i++ )
        {
            globalContainerListeners.get( i ).onWidgetDetachedFromHUD( window, this );
        }
    }
    
    /**
     * Adds an internally managed widget.
     * 
     * @param widget
     * @param locX use -1 for center
     * @param locY use -1 for center
     * @param zIndex
     */
    private void addInternalWidget( Widget widget, float locX, float locY, int zIndex )
    {
        if ( ( widget.getContainer() != null ) || ( widget.getHUD() != null ) )
        {
            throw new Error( "This Widget is already added to the HUD." );
        }
        
        if ( locX < 0f )
            locX = Math.round( ( this.getResX() - widget.getWidth() ) / 2.0f );
        if ( locY < 0f )
            locY = Math.round( ( this.getResY() - widget.getHeight() ) / 2.0f );
        widget.setLocation( locX, locY );
        widget.setZIndex( zIndex );
        __HUD_base_PrivilegedAccess.setHUD( this, widget );
        
        branchGroup.addChild( __HUD_base_PrivilegedAccess.getSGNode( widget ) );
    }
    
    void addVolatilePopup( Widget widget, Widget assembly, float locX, float locY )
    {
        if ( !widget.isHeavyWeight() )
            throw new IllegalArgumentException( "A volatile popup must be heavyweight." );
        
        if ( currentVolatilePopup != null )
        {
            removeVolatilePopup();
        }
        
        addInternalWidget( widget, locX, locY, POPUP_Z_INDEX );
        
        this.currentVolatilePopup = widget;
        this.currentVolatilePopupAssembly = assembly;
    }
    
    void removeVolatilePopup()
    {
        removeInternalWidget( currentVolatilePopup );
        
        currentVolatilePopup = null;
        currentVolatilePopupAssembly = null;
    }
    
    final Widget getCurrentVolatilePopup()
    {
        return ( currentVolatilePopup );
    }
    
    private void removeInternalWidget( Widget widget )
    {
        if ( ( widget.getContainer() != null ) || ( widget.getHUD() != this ) )
            throw new Error( "the given Widget is not held in this Container." );
        
        branchGroup.removeChild( __HUD_base_PrivilegedAccess.getSGNode( widget ) );
        
        __HUD_base_PrivilegedAccess.setHUD( null, widget );
    }
    
    /**
     * Sets the content pane, where all other Widgets (except for windows) must be added to.
     * 
     * @param contentPane
     */
    public void setContentPane( WidgetContainer contentPane )
    {
        if ( contentPane == null )
            throw new IllegalArgumentException( "contentPane must not be null." );
        
        if ( contentPane == this.contentPane )
            return;
        
        if ( this.contentPane != null )
            removeInternalWidget( this.contentPane );
        
        addInternalWidget( contentPane, contentPane.getLeft(), contentPane.getTop(), 0 );
        this.contentPane = contentPane;
    }
    
    /**
     * Returns the content pane, where all other Widgets (except for windows) must be added to.
     * 
     * @return  the content pane, where all other Widgets (except for windows) must be added to.
     */
    public final WidgetContainer getContentPane()
    {
        return ( contentPane );
    }
    
    /**
     * @return the currently visible dialog, if any or null
     */
    private final Dialog getCurrentDialog()
    {
        for ( int i = dialogs.size() - 1; i >= 0; i-- )
        {
            Dialog dialog = dialogs.get( i );
            if ( dialog.isVisible() && ( dialog.getHUD() != null ) )
            {
                return ( dialogs.get( i ) );
            }
        }
        
        return ( null );
    }
    
    Widget focus( Widget widget )
    {
        final Widget cfw = currentFocusedWidget;
        
        if ( widget instanceof WidgetContainer )
        {
            // We need to check, if the container is a content pane of a window.
            if ( ( (WidgetContainer)widget ).isContentPane() )
                widget = ( (WidgetContainer)widget ).getParentWindow();
        }
        
        if ( ( cfw == widget ) || !widget.isFocussable() )
            return ( cfw );
        
        if ( currentFocusedWidget != null )
            __HUD_base_PrivilegedAccess.onFocusLost( currentFocusedWidget );
        
        currentFocusedWidget = widget;
        __HUD_base_PrivilegedAccess.onFocusGained( currentFocusedWidget );
        
        return ( cfw );
    }
    
    final Widget getCurrentFocusedWidget( boolean getLeaf )
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
    
    private final Widget getCurrentFocusedWidget()
    {
        return ( getCurrentFocusedWidget( false ) );
    }
    
    final Widget getCurrentHoveredWidget( boolean getLeaf )
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
    
    private final Widget getCurrentHoveredWidget()
    {
        return ( currentHoveredWidget );
    }
    
    /**
     * Bind mouse movement to a Widget.
     * onMouseMoved will be called on this Widget,
     * even if the mouse is not over it.
     * 
     * @param widget the Widget to bind
     */
    void bindMouseMovement( Widget widget )
    {
        mouseBoundWidget = widget;
    }
    
    private final Widget getCurrentMouseBoundWidget()
    {
        return ( mouseBoundWidget );
    }
    
    /**
     * Sets the {@link ToolTipFactory} to be used to generate tooltip-Widgets.
     * 
     * @param ttf
     */
    public void setToolTipFactory( ToolTipFactory ttf )
    {
        this.tooltipFactory = ttf;
    }
    
    /**
     * @return the {@link ToolTipFactory} to be used to generate tooltip-Widgets.
     */
    public final ToolTipFactory getToolTipFactory()
    {
        return ( tooltipFactory );
    }
    
    private void disposeToolTip()
    {
        if ( currentlyDisplayedTooltipWidget == null )
            return;
        
        removeInternalWidget( currentlyDisplayedTooltipWidget );
        currentlyDisplayedTooltipWidget = null;
        currentlyTooltippedWidget = null;
    }
    
    private void displayToolTipIfNeeded( Widget leafHoveredWidget, int canvasX, int canvasY )
    {
        if ( !leafHoveredWidget.hasToolTip() || ( getToolTipFactory() == null ) )
            return;
        
        if ( leafHoveredWidget == currentlyTooltippedWidget )
            return;
        
        Widget tooltip = __HUD_base_PrivilegedAccess.getCachedToolTipWidget( leafHoveredWidget );
        
        if ( tooltip == null )
        {
            tooltip = getToolTipFactory().createToolTip( leafHoveredWidget.getToolTip() );
            
            if ( tooltip == null )
            {
                throw new Error( "ToolTipFactory " + getToolTipFactory().getClass() + " returns null. This is illegal!" );
            }
            
            if ( !tooltip.isHeavyWeight() )
            {
                throw new Error( "A tooltip must be heavyweight!" );
            }
            
            __HUD_base_PrivilegedAccess.setCachedToolTipWidget( leafHoveredWidget, tooltip );
        }
        
        disposeToolTip();
        
        this.currentlyDisplayedTooltipWidget = tooltip;
        
        Tuple2f buffer = Tuple2f.fromPool();
        coordinatesConverter.getLocationPixels2HUD( canvasX, canvasY, buffer );
        final float posX = buffer.getX();
        final float posY = buffer.getY();
        Tuple2f.toPool( buffer );
        
        addInternalWidget( tooltip, posX, posY, TOOLTIP_Z_INDEX );
        
        currentlyTooltippedWidget = leafHoveredWidget;
        tooltipStartPoint.set( canvasX, canvasY );
    }
    
    /**
     * Sets the {@link DropShadowFactory} to be used to render drop shadows.
     * 
     * @param ttf
     */
    public void setDropShadowFactory( DropShadowFactory dsf )
    {
        this.dropshadowFactory = dsf;
    }
    
    /**
     * @return the {@link DropShadowFactory} to be used to render drop shadows.
     */
    public final DropShadowFactory getDropShadowFactory()
    {
        return ( dropshadowFactory );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setContextMenu( ContextMenu contextMenu )
    {
        if ( contextMenu == this.contextMenu )
        {
            return;
        }
        
        if ( this.contextMenu != null )
        {
            this.contextMenu.setHUD( null );
        }
        
        this.contextMenu = contextMenu;
        
        contextMenu.setHUD( this );
    }
    
    /**
     * {@inheritDoc}
     */
    public ContextMenu getContextMenu()
    {
        return ( contextMenu );
    }
    
    /**
     * Defines, if a ContextMenu is used, if the mouse was clicked in the empty
     * space (where no Widget is displayed).
     * 
     * @param use
     */
    public void setUseVoidContextMenu( boolean use )
    {
        this.useVoidContextMenu = use;
    }
    
    /**
     * @return if a ContextMenu is used, if the mouse was clicked in the empty
     * space (where no Widget is displayed).
     */
    public final boolean useVoidContextMenu()
    {
        return ( useVoidContextMenu );
    }
    
    private void popUpContextMenu( ContextMenu contextMenu, float posX, float posY )
    {
        if ( contextMenu == null )
            return;
        
        contextMenu.popUp( posX, posY );
    }
    
    private void checkPopupMenu( HUDPickResult pickResult, MouseButton button, float posX, float posY, HUDPickReason pickReason )
    {
        Widget pickedWidget = ( pickResult == null ) ? null : pickResult.getLeafResult().getWidget();
        
        ContextMenu contextMenu = null;
        if ( pickedWidget != null )
        {
            contextMenu = pickedWidget.getContextMenu();
        }
        if ( contextMenu == null )
        {
            contextMenu = this.getContextMenu();
        }
        
        if ( contextMenu != null )
        {
            if ( currentlyDisplayedContextMenu != null )
            {
                if ( ( pickedWidget != null ) || ( useVoidContextMenu() ) )
                {
                    if ( pickedWidget != currentlyDisplayedContextMenu.getMenuItemsContainer() )
                    {
                        if ( button == MouseButtons.RIGHT_BUTTON )
                        {
                            if ( contextMenu != currentlyDisplayedContextMenu )
                            {
                                currentlyDisplayedContextMenu.popUp( false );
                                currentlyDisplayedContextMenu = null;
                            }
                            
                            if ( pickReason == HUDPickReason.BUTTON_RELEASED )
                            {
                                popUpContextMenu( contextMenu, posX, posY );
                                this.currentlyDisplayedContextMenu = contextMenu;
                            }
                        }
                        else
                        {
                            if ( ( pickResult == null ) || ( pickResult.getWidget() != contextMenu.getMenuItemsContainer() ) )
                            {
                                currentlyDisplayedContextMenu.popUp( false );
                                currentlyDisplayedContextMenu = null;
                            }
                        }
                    }
                }
            }
            else if ( ( button == MouseButtons.RIGHT_BUTTON ) && ( pickReason == HUDPickReason.BUTTON_RELEASED ) )
            {
                if ( ( pickedWidget != null ) || useVoidContextMenu() )
                {
                    popUpContextMenu( contextMenu, posX, posY );
                    this.currentlyDisplayedContextMenu = contextMenu;
                }
            }
        }
        else if ( currentlyDisplayedContextMenu != null )
        {
            currentlyDisplayedContextMenu.popUp( false );
            currentlyDisplayedContextMenu = null;
        }
    }
    
    private Widget pick( int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        HUDPickResult tmpHPR = null;
        HUDPickResult topMost = null;
        pickedWidgets.clear();
        
        /*
        if ( ( topMost == null ) && ( currentlyDisplayedTooltipWidget != null ) )
        {
            tmpHPR = __HUD_base_PrivilegedAccess.pick( currentlyDisplayedTooltipWidget, canvasX, canvasY, pickReason, button, when, meta, flags | HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL | HUDPickResult.HUD_PICK_FLAG_EVENTS_SUPPRESSED );
            if ( tmpHPR != null )
            {
                pickedWidgets.add( tmpHPR );
                
                topMost = tmpHPR;
            }
        }
        */
        
        if ( ( topMost == null ) && ( getCurrentVolatilePopup() != null ) )
        {
            tmpHPR = __HUD_base_PrivilegedAccess.pick( getCurrentVolatilePopup(), canvasX, canvasY, pickReason, button, when, meta, flags );
            if ( tmpHPR != null )
            {
                pickedWidgets.add( tmpHPR );
                
                topMost = tmpHPR;
            }
        }
        
        Dialog currDialog = getCurrentDialog();
        
        if ( topMost == null )
        {
            if ( currDialog != null )
            {
                if ( currDialog.isPickable() )
                {
                    tmpHPR = __HUD_base_PrivilegedAccess.pick( currDialog, canvasX, canvasY, pickReason, button, when, meta, flags );
                    if ( tmpHPR != null )
                    {
                        pickedWidgets.add( tmpHPR );
                        
                        topMost = tmpHPR;
                    }
                }
            }
            else
            {
                // check, if there's a Window under the cursor
                for ( int i = 0; i < windows.size(); i++ )
                {
                    final Window window = windows.get( i );
                    if ( window.isVisible() && window.isPickable() )
                    {
                        //tmpHPR = __HUD_base_PrivilegedAccess.pick( window, canvasX, canvasY, pickReason, button, when, meta, flags | HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL | HUDPickResult.HUD_PICK_FLAG_EVENTS_SUPPRESSED );
                        tmpHPR = __HUD_base_PrivilegedAccess.pick( window, canvasX, canvasY, pickReason, button, when, meta, flags | HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL );
                        if ( tmpHPR != null )
                        {
                            pickedWidgets.add( tmpHPR );
                            
                            if ( ( topMost == null ) || ( topMost.compareTo( tmpHPR ) <= 0 ) )
                            {
                                topMost = tmpHPR;
                            }
                        }
                    }
                }
                
                if ( ( topMost == null ) && contentPane.isVisible() && contentPane.isPickable() )
                {
                    tmpHPR = __HUD_base_PrivilegedAccess.pick( contentPane, canvasX, canvasY, pickReason, button, when, meta, flags );
                    if ( tmpHPR != null )
                    {
                        pickedWidgets.add( tmpHPR );
                        
                        topMost = tmpHPR;
                    }
                }
            }
        }
        
        final boolean justTest = ( flags & HUDPickResult.HUD_PICK_FLAG_JUST_TEST_AND_DO_NOTHING ) == HUDPickResult.HUD_PICK_FLAG_JUST_TEST_AND_DO_NOTHING;
        final boolean isInternal = justTest || ( flags & HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL ) != 0;
        final boolean eventsSuppressed = justTest || ( flags & HUDPickResult.HUD_PICK_FLAG_EVENTS_SUPPRESSED ) != 0;
        
        if ( !justTest && ( currentHoveredWidget != null ) && ( ( topMost == null ) || ( currentHoveredWidget != topMost.getWidget() ) ) )
        {
            // How can this be??? But we will check it to avoid problems...
            if ( currentHoveredWidget.getHUD() != null )
            {
                __HUD_base_PrivilegedAccess.onMouseExited( currentHoveredWidget, true, false );
                
                for ( int j = 0; j < globalInputListeners.size(); j++ )
                {
                    globalInputListeners.get( j ).onMouseExited( getCurrentHoveredWidget( true ), true, false );
                }
            }
            currentHoveredWidget = null;
        }
        
        final Tuple2f locP = Tuple2f.fromPool();
        getCoordinatesConverter().getLocationPixels2HUD( canvasX, canvasY, locP );
        float pickXHUD = locP.getX();
        float pickYHUD = locP.getY();
        Tuple2f.toPool( locP );
        
        if ( ( topMost != null ) && isInternal && !eventsSuppressed )
        {
            for ( int i = 0; i < pickedWidgets.size(); i++ )
            {
                final HUDPickResult hpr = pickedWidgets.get( i );
                final Widget pickedWidget = hpr.getWidget();
                final boolean isTopMost = ( pickedWidget == topMost.getWidget() ); // && topMost.isLeafResult();
                boolean hasFocus = ( pickedWidget == getCurrentFocusedWidget() );
                
                float pickXHUD_ = pickXHUD - pickedWidget.getLeft();
                float pickYHUD_ = pickYHUD - pickedWidget.getTop();
                
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
                        
                        __HUD_base_PrivilegedAccess.onMouseButtonPressed( pickedWidget, button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        
                        for ( int j = 0; j < globalInputListeners.size(); j++ )
                        {
                            globalInputListeners.get( j ).onMouseButtonPressed( hpr.getLeafResult().getWidget(), button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        }
                        
                        break;
                    
                    case BUTTON_RELEASED:
                        __HUD_base_PrivilegedAccess.onMouseButtonReleased( pickedWidget, button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        
                        for ( int j = 0; j < globalInputListeners.size(); j++ )
                        {
                            globalInputListeners.get( j ).onMouseButtonReleased( hpr.getLeafResult().getWidget(), button, pickXHUD_, pickYHUD_, when, meta, isTopMost, hasFocus );
                        }
                        
                        break;
                    
                    case MOUSE_MOVED:
                        __HUD_base_PrivilegedAccess.onMouseMoved( pickedWidget, pickXHUD_, pickYHUD_, (int)meta, when, isTopMost, hasFocus );
                        
                        if ( ( currentHoveredWidget == null ) && isTopMost )
                        {
                            currentHoveredWidget = pickedWidget;
                            __HUD_base_PrivilegedAccess.onMouseEntered( currentHoveredWidget, isTopMost, hasFocus );
                            
                            for ( int j = 0; j < globalInputListeners.size(); j++ )
                            {
                                globalInputListeners.get( j ).onMouseEntered( getCurrentHoveredWidget( true ), isTopMost, hasFocus );
                            }
                        }
                        
                        for ( int j = 0; j < globalInputListeners.size(); j++ )
                        {
                            globalInputListeners.get( j ).onMouseMoved( hpr.getLeafResult().getWidget(), pickXHUD_, pickYHUD_, (int)meta, when, isTopMost, hasFocus );
                        }
                        
                        break;
                        
                    case MOUSE_STOPPED:
                        //__HUD_base_PrivilegedAccess.onMouseStopped( pickedWidget, pickXHUD_, pickYHUD_, when, isTopMost, hasFocus );
                        
                        if ( isTopMost )
                        {
                            displayToolTipIfNeeded( hpr.getLeafResult().getWidget(), canvasX, canvasY );
                        }
                        
                        /*
                        for ( int j = 0; j < globalInputListeners.size(); j++ )
                        {
                            globalInputListeners.get( j ).onMouseStopped( hpr.getLeafResult().getWidget(), pickXHUD_, pickYHUD_, when, isTopMost, hasFocus );
                        }
                        */
                        
                        break;
                    
                    case MOUSE_WHEEL_MOVED_UP:
                        __HUD_base_PrivilegedAccess.onMouseWheelMoved( pickedWidget, +1, ( meta != 0L ), pickXHUD_, pickYHUD_, when, isTopMost );
                        
                        for ( int j = 0; j < globalInputListeners.size(); j++ )
                        {
                            globalInputListeners.get( j ).onMouseWheelMoved( hpr.getLeafResult().getWidget(), +1, ( meta != 0L ), pickXHUD_, pickYHUD_, when, isTopMost );
                        }
                        
                        break;
                    
                    case MOUSE_WHEEL_MOVED_DOWN:
                        __HUD_base_PrivilegedAccess.onMouseWheelMoved( pickedWidget, -1, ( meta != 0L ), pickXHUD_, pickYHUD_, when, isTopMost );
                        
                        for ( int j = 0; j < globalInputListeners.size(); j++ )
                        {
                            globalInputListeners.get( j ).onMouseWheelMoved( hpr.getLeafResult().getWidget(), -1, ( meta != 0L ), pickXHUD_, pickYHUD_, when, isTopMost );
                        }
                        
                        break;
                }
            }
        }
        
        if ( !justTest )
        {
            // notify pick-missed listeners in case...
            if ( pickedWidgets.size() == 0 )
            {
                if ( !eventsSuppressed )
                {
                    if ( currDialog == null )
                    {
                        for ( int i = 0; i < pickMissedListeners.size(); i++ )
                        {
                            if ( ( pickMissedMasks.get( i ) & pickReason.getMaskValue() ) > 0 )
                                pickMissedListeners.get( i ).onHUDPickMissed( button, canvasX, canvasY, pickReason, when, meta );
                        }
                    }
                }
                
                checkPopupMenu( null, button, pickXHUD, pickYHUD, pickReason );
            }
            
            // change the Cursor texture, if necessary...
            if ( topMost != null )
            {
                final Cursor.Type newCursorType = topMost.getInheritedCursorType();
                if ( newCursorType != currentCursorType )
                {
                    if ( ( newCursorType != null ) || ( currentCursorType != null ) )
                    {
                        if ( newCursorType == null )
                            setCursorInternal( getCursorSet().get( Cursor.Type.POINTER1 ), Cursor.Type.POINTER1, false );
                        else
                            setCursorInternal( getCursorSet().get( newCursorType ), newCursorType, false );
                    }
                }
                
                if ( ( pickReason == HUDPickReason.BUTTON_PRESSED ) || ( pickReason == HUDPickReason.BUTTON_RELEASED ) )
                {
                    checkPopupMenu( topMost, button, pickXHUD, pickYHUD, pickReason );
                }
            }
            else if ( currentCursorType != Cursor.Type.POINTER1 )
            {
                setCursorInternal( getCursorSet().get( Cursor.Type.POINTER1 ), Cursor.Type.POINTER1, false );
            }
        }
        
        if ( topMost != null )
        {
            if ( pickReason == HUDPickReason.BUTTON_PRESSED )
            {
                if ( ( getCurrentVolatilePopup() != null ) && ( topMost.getWidget() != getCurrentVolatilePopup() ) && ( topMost.getLeafResult().getWidget() != currentVolatilePopupAssembly ) )
                {
                    removeVolatilePopup();
                }
            }
        }
        else if ( ( pickReason == HUDPickReason.BUTTON_PRESSED ) && ( getCurrentVolatilePopup() != null ) )
        {
            removeVolatilePopup();
        }
        
        Widget topmostWidget = ( topMost != null ) ? topMost.getWidget() : null;
        
        for ( int i = 0; i < pickedWidgets.size(); i++ )
        {
            HUDPickResult.toPool( pickedWidgets.get( i ) );
        }
        
        pickedWidgets.clear();
        
        return ( topmostWidget );
    }
    
    void onMouseButtonPressed( MouseButton button, int x, int y, long when, long lastWhen )
    {
        pick( x, y, HUDPickReason.BUTTON_PRESSED, button, when, lastWhen, HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL );
    }
    
    void onMouseButtonReleased( MouseButton button, int x, int y, long when, long lastWhen )
    {
        if ( getCurrentMouseBoundWidget() == null )
        {
            pick( x, y, HUDPickReason.BUTTON_RELEASED, button, when, lastWhen, HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL );
        }
        else
        {
            final Tuple2f locP = Tuple2f.fromPool();
            //getCoordinatesConverter().getLocationPixels2HUD( x, y, locP );
            __HUD_base_PrivilegedAccess.getLocationPixels2HUD_( mouseBoundWidget, x, y, locP );
            float pickXHUD = locP.getX();
            float pickYHUD = locP.getY();
            Tuple2f.toPool( locP );
            
            Widget mbw = mouseBoundWidget;
            mouseBoundWidget = null;
            __HUD_base_PrivilegedAccess.onMouseButtonReleased( mbw, button, pickXHUD, pickYHUD, when, lastWhen, true, mbw.isFocussable() );
        }
    }
    
    void onMouseMoved( int x, int y, int buttonsState, long when, long lastWhen )
    {
        if ( currentlyDisplayedTooltipWidget != null )
        {
            Widget tmp = currentlyDisplayedTooltipWidget;
            currentlyDisplayedTooltipWidget = null;
            Widget pr = pick( x, y, HUDPickReason.MOUSE_MOVED, null, when, lastWhen, HUDPickResult.HUD_PICK_FLAG_JUST_TEST_AND_DO_NOTHING );
            currentlyDisplayedTooltipWidget = tmp;
            
            if ( ( pr == null ) || ( pr != currentlyDisplayedTooltipWidget ) )
            {
                final int minMoveX = (int)( this.getWidth() / 16f );
                final int minMoveY = (int)( this.getHeight() / 12f );
                
                if ( ( Math.abs( x - tooltipStartPoint.getX() ) > minMoveX ) || ( Math.abs( y - tooltipStartPoint.getY() ) > minMoveY ) )
                {
                    disposeToolTip();
                }
            }
        }
        
        lastMousePos.set( x * getResX() / getWidth(), y * getResY() / getHeight() );
        
        if ( ( cursorImage != null ) && ( currentCursor != null ) )
        {
            if ( __HUD_base_PrivilegedAccess.getSGNode( cursorImage ).getParent() == null )
            {
                branchGroup.addChild( __HUD_base_PrivilegedAccess.getSGNode( cursorImage ) );
                __HUD_base_PrivilegedAccess.setHUD( this, cursorImage );
            }
            
            float zpx = currentCursor.getZeroPointX() * getResX() / getWidth();
            float zpy = currentCursor.getZeroPointY() * getResY() / getHeight();
            
            cursorImage.setLocation( lastMousePos.getX() - zpx, lastMousePos.getY() - zpy );
        }
        
        Widget mouseBound = getCurrentMouseBoundWidget();
        if ( ( mouseBound != null ) && ( getCurrentHoveredWidget() != mouseBound ) )
        {
            final Tuple2f locP = Tuple2f.fromPool();
            //getCoordinatesConverter().getLocationPixels2HUD( x, y, locP );
            __HUD_base_PrivilegedAccess.getLocationPixels2HUD_( mouseBound, x, y, locP );
            float pickXHUD = locP.getX();
            float pickYHUD = locP.getY();
            Tuple2f.toPool( locP );
            
            __HUD_base_PrivilegedAccess.onMouseMoved( mouseBound, pickXHUD, pickYHUD, buttonsState, when, true, mouseBound.hasFocus() );
        }
        else
        {
            pick( x, y, HUDPickReason.MOUSE_MOVED, null, when, (long)buttonsState, HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL );
        }
    }
    
    void onMouseStopped( int x, int y, int buttonsState, long when )
    {
        if ( currentHoveredWidget != null )
        {
            final Tuple2f locP = Tuple2f.fromPool();
            getCoordinatesConverter().getLocationPixels2HUD( x, y, locP );
            float pickXHUD = locP.getX();
            float pickYHUD = locP.getY();
            Tuple2f.toPool( locP );
            
            __HUD_base_PrivilegedAccess.onMouseStopped( currentHoveredWidget, pickXHUD, pickYHUD, when, true, currentHoveredWidget.hasFocus() );
        }
        
        /*Widget pickedWidget = */pick( x, y, HUDPickReason.MOUSE_STOPPED, null, when, buttonsState, HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL );
        
        /*
        if ( pickedWidget == null )
        {
            displayToolTipIfNeeded( null, x, y );
        }
        */
    }
    
    void onMouseWheelMoved( int wheelDelta, int x, int y, long when, boolean isPageMove )
    {
        final HUDPickReason pickReason;
        if ( wheelDelta > 0 )
            pickReason = HUDPickReason.MOUSE_WHEEL_MOVED_UP;
        else if ( wheelDelta < 0 )
            pickReason = HUDPickReason.MOUSE_WHEEL_MOVED_DOWN;
        else
            pickReason = null;
        
        if ( pickReason != null )
            pick( x, y, pickReason, null, when, isPageMove ? 1L : 0L, HUDPickResult.HUD_PICK_FLAG_IS_INTERNAL );
    }
    
    void onKeyPressed( Key key, int modifierMask, long when )
    {
        if ( currentFocusedWidget != null )
        {
            __HUD_base_PrivilegedAccess.onKeyPressed( currentFocusedWidget, key, modifierMask, when );
            
            if ( !globalInputListeners.isEmpty() )
            {
                Widget leafFocussedWidget = getCurrentFocusedWidget( true );
                for ( int i = 0; i < globalInputListeners.size(); i++ )
                {
                    globalInputListeners.get( i ).onKeyPressed( leafFocussedWidget, key, modifierMask, when );
                }
            }
        }
    }
    
    void onKeyReleased( Key key, int modifierMask, long when )
    {
        if ( currentFocusedWidget != null )
        {
            __HUD_base_PrivilegedAccess.onKeyReleased( currentFocusedWidget, key, modifierMask, when );
            
            if ( !globalInputListeners.isEmpty() )
            {
                Widget leafFocussedWidget = getCurrentFocusedWidget( true );
                for ( int i = 0; i < globalInputListeners.size(); i++ )
                {
                    globalInputListeners.get( i ).onKeyReleased( leafFocussedWidget, key, modifierMask, when );
                }
            }
        }
    }
    
    void onKeyTyped( char keyChar, int modifierMask, long when )
    {
        if ( currentFocusedWidget != null )
        {
            __HUD_base_PrivilegedAccess.onKeyTyped( currentFocusedWidget, keyChar, modifierMask, when );
            
            if ( !globalInputListeners.isEmpty() )
            {
                Widget leafFocussedWidget = getCurrentFocusedWidget( true );
                for ( int i = 0; i < globalInputListeners.size(); i++ )
                {
                    globalInputListeners.get( i ).onKeyTyped( leafFocussedWidget, keyChar, modifierMask, when );
                }
            }
        }
    }
    
    void onControllerButtonPressed( ControllerButton button, long when )
    {
        final Widget currentFocusedWidget = getCurrentFocusedWidget( true );
        
        if ( currentFocusedWidget != null )
        {
            __HUD_base_PrivilegedAccess.onControllerButtonPressed( currentFocusedWidget, button, when );
            
            if ( !globalInputListeners.isEmpty() )
            {
                Widget leafFocussedWidget = getCurrentFocusedWidget( true );
                for ( int i = 0; i < globalInputListeners.size(); i++ )
                {
                    globalInputListeners.get( i ).onControllerButtonPressed( leafFocussedWidget, button, when );
                }
            }
        }
    }
    
    void onControllerButtonReleased( ControllerButton button, long when )
    {
        final Widget currentFocusedWidget = getCurrentFocusedWidget( true );
        
        if ( currentFocusedWidget != null )
        {
            __HUD_base_PrivilegedAccess.onControllerButtonReleased( currentFocusedWidget, button, when );
            
            if ( !globalInputListeners.isEmpty() )
            {
                Widget leafFocussedWidget = getCurrentFocusedWidget( true );
                for ( int i = 0; i < globalInputListeners.size(); i++ )
                {
                    globalInputListeners.get( i ).onControllerButtonReleased( leafFocussedWidget, button, when );
                }
            }
        }
    }
    
    void onControllerAxisChanged( ControllerAxis axis, float axisDelta, long when )
    {
        final Widget currentFocusedWidget = getCurrentFocusedWidget( true );
        
        if ( currentFocusedWidget != null )
        {
            __HUD_base_PrivilegedAccess.onControllerAxisChanged( currentFocusedWidget, axis, (int)( axisDelta * axis.getScale() ), when );
            
            if ( !globalInputListeners.isEmpty() )
            {
                Widget leafFocussedWidget = getCurrentFocusedWidget( true );
                for ( int i = 0; i < globalInputListeners.size(); i++ )
                {
                    globalInputListeners.get( i ).onControllerAxisChanged( leafFocussedWidget, axis, axisDelta, when );
                }
            }
        }
    }
    
    void onInputStateChanged( DeviceComponent comp, int delta, int state, long when )
    {
        final Widget leafFocussedWidget = getCurrentFocusedWidget( true );
        
        if ( leafFocussedWidget == null )
            return;
        
        
        __HUD_base_PrivilegedAccess.onInputStateChanged( leafFocussedWidget, comp, delta, state, when, true, true );
        
        boolean doFocusManagement = true;
        if ( comp instanceof DigitalDeviceComponent )
        {
            if ( delta <= 0 )
                doFocusManagement = false;
        }
        /*
        else if ( comp instanceof MouseWheel )
        {
            //delta = Math.abs( delta );
        }
        */
        else
        {
            doFocusManagement = false;
        }
        
        if ( doFocusManagement && ( leafFocussedWidget.getContainer() != null ) && ( !__HUD_base_PrivilegedAccess.widgetBlocksFocusMoveDeviceComponent( leafFocussedWidget, comp ) ) )
        {
            FocusMoveDirection fmd = null;
            
            if ( ( fmd == null ) && ( focusMoveUpAccessors != null ) )
            {
                for ( int i = 0; i < focusMoveUpAccessors.length; i++ )
                {
                    if ( focusMoveUpAccessors[ i ] == comp )
                    {
                        fmd = FocusMoveDirection.UP;
                        break;
                    }
                }
            }
            
            if ( ( fmd == null ) && ( focusMoveDownAccessors != null ) )
            {
                for ( int i = 0; i < focusMoveDownAccessors.length; i++ )
                {
                    if ( focusMoveDownAccessors[ i ] == comp )
                    {
                        fmd = FocusMoveDirection.DOWN;
                        break;
                    }
                }
            }
            
            if ( ( fmd == null ) && ( focusMoveLeftAccessors != null ) )
            {
                for ( int i = 0; i < focusMoveLeftAccessors.length; i++ )
                {
                    if ( focusMoveLeftAccessors[ i ] == comp )
                    {
                        fmd = FocusMoveDirection.LEFT;
                        break;
                    }
                }
            }
            
            if ( ( fmd == null ) && ( focusMoveRightAccessors != null ) )
            {
                for ( int i = 0; i < focusMoveRightAccessors.length; i++ )
                {
                    if ( focusMoveRightAccessors[ i ] == comp )
                    {
                        fmd = FocusMoveDirection.RIGHT;
                        break;
                    }
                }
            }
            
            if ( ( fmd == null ) && ( focusMoveNextAccessors != null ) )
            {
                for ( int i = 0; i < focusMoveNextAccessors.length; i++ )
                {
                    if ( focusMoveNextAccessors[ i ] == comp )
                    {
                        fmd = FocusMoveDirection.NEXT;
                        break;
                    }
                }
            }
            
            if ( ( fmd != null ) && ( leafFocussedWidget.getContainer() != null ) )
            {
                __HUD_base_PrivilegedAccess.moveFocus( leafFocussedWidget.getContainer(), fmd );
            }
        }
        
        for ( int i = 0; i < globalInputListeners.size(); i++ )
        {
            globalInputListeners.get( i ).onInputStateChanged( leafFocussedWidget, comp, delta, state, when, true, true );
        }
    }
    
    /**
     * Binds a DeviceComponent to this HUD, that works as a focus-move-accessor
     * for a given direction.
     * 
     * @param comp
     * @param direction
     */
    public void bindFocusMoveAccessor( DeviceComponent comp, FocusMoveDirection direction )
    {
        DeviceComponent[] accessors;
        switch ( direction )
        {
            case UP:
                accessors = focusMoveUpAccessors;
                break;
            case DOWN:
                accessors = focusMoveDownAccessors;
                break;
            case LEFT:
                accessors = focusMoveLeftAccessors;
                break;
            case RIGHT:
                accessors = focusMoveRightAccessors;
                break;
            case NEXT:
                accessors = focusMoveNextAccessors;
                break;
            default:
                throw new Error( "Unsupported direction " + direction );
        }
        
        if ( ( accessors == null ) || ( accessors.length == 0 ) )
        {
            accessors = new DeviceComponent[] { comp };
        }
        else
        {
            DeviceComponent[] newArray = new DeviceComponent[ accessors.length + 1 ];
            System.arraycopy( accessors, 0, newArray, 0, accessors.length );
            newArray[ newArray.length - 1 ] = comp;
            accessors = newArray;
        }
        
        switch ( direction )
        {
            case UP:
                focusMoveUpAccessors = accessors;
                break;
            case DOWN:
                focusMoveDownAccessors = accessors;
                break;
            case LEFT:
                focusMoveLeftAccessors = accessors;
                break;
            case RIGHT:
                focusMoveRightAccessors = accessors;
                break;
            case NEXT:
                focusMoveNextAccessors = accessors;
                break;
        }
    }
    
    /**
     * Unbinds a focus-move-accessor DeviceComponent from this HUD for a given direction.
     * 
     * @param comp
     * @param direction
     */
    public void unbindFocusMoveAccessor( DeviceComponent comp, FocusMoveDirection direction )
    {
        DeviceComponent[] accessors;
        switch ( direction )
        {
            case UP:
                accessors = focusMoveUpAccessors;
                break;
            case DOWN:
                accessors = focusMoveDownAccessors;
                break;
            case LEFT:
                accessors = focusMoveLeftAccessors;
                break;
            case RIGHT:
                accessors = focusMoveRightAccessors;
                break;
            case NEXT:
                accessors = focusMoveNextAccessors;
                break;
            default:
                throw new Error( "Unsupported direction " + direction );
        }
        
        if ( accessors == null )
        {
            return;
        }
        
        final int index = ArrayUtils.indexOf( accessors, comp, true );
        
        if ( index < 0 )
        {
            return;
        }
        
        if ( accessors.length == 1 )
        {
            accessors = null;
            
            return;
        }
        
        
        DeviceComponent[] newArray = new DeviceComponent[ accessors.length - 1 ];
        System.arraycopy( accessors, 0, newArray, 0, index );
        System.arraycopy( accessors, index + 1, newArray, index, accessors.length - index - 1 );
        accessors = newArray;
        
        switch ( direction )
        {
            case UP:
                focusMoveUpAccessors = accessors;
                break;
            case DOWN:
                focusMoveDownAccessors = accessors;
                break;
            case LEFT:
                focusMoveLeftAccessors = accessors;
                break;
            case RIGHT:
                focusMoveRightAccessors = accessors;
                break;
            case NEXT:
                focusMoveNextAccessors = accessors;
                break;
        }
    }
    
    /**
     * Sets the CursorSet with standard cursor types.
     * 
     * @param cursorSet
     */
    public void setCursorSet( CursorSet cursorSet )
    {
        if ( cursorSet == null )
            throw new IllegalArgumentException( "cursorSet cannot be null." );
        
        this.cursorSet = cursorSet;
    }
    
    /**
     * @return the CursorManager to manager the standard cursor types.
     */
    public final CursorSet getCursorSet()
    {
        if ( this.cursorSet == null )
        {
            this.cursorSet = getTheme().getCursorSet();
        }
        
        return ( cursorSet );
    }
    
    /**
     * Sets the {@link Cursor} for the mouse cursor.<br>
     * Use <i>null</i> for cursor to remove the cursor.
     * 
     * @param cursor the new cursor for the cursor Image (or null for no cursor)
     * @param type
     */
    private final void setCursorInternal( Cursor cursor, Cursor.Type type, boolean allowDetach )
    {
        if ( cursor != null )
        {
            float texWidth = cursor.getTexture().getOriginalWidth() * getResX() / getWidth();
            float texHeight = cursor.getTexture().getOriginalHeight() * getResY() / getHeight();
            
            Dim2f tmp = Dim2f.fromPool();
            getCoordinatesConverter().getSizePixels2HUD( cursor.getZeroPointX(), cursor.getZeroPointY(), tmp );
            float zpx = tmp.getWidth();
            float zpy = tmp.getHeight();
            Dim2f.toPool( tmp );
            
            if ( this.cursorImage == null )
            {
                cursorImage = new Image( true, texWidth, texHeight, cursor.getTexture(), TileMode.TILE_BOTH );
                cursorImage.setLocation( lastMousePos.getX() - zpx, lastMousePos.getY() - zpy );
                cursorImage.setZIndex( CURSOR_Z_INDEX );
                
                branchGroup.addChild( __HUD_base_PrivilegedAccess.getSGNode( cursorImage ) );
                __HUD_base_PrivilegedAccess.setHUD( this, cursorImage );
            }
            else
            {
                if ( cursorImage.getTexture() != cursor.getTexture() )
                    cursorImage.setTexture( cursor.getTexture() );
                
                if ( ( cursorImage.getWidth() != texWidth ) || ( cursorImage.getHeight() != texHeight ) )
                {
                    cursorImage.setSize( texWidth, texHeight );
                }
                
                cursorImage.setLocation( lastMousePos.getX() - zpx, lastMousePos.getY() - zpy );
            }
            
            cursorImage.setVisible( true );
            
            currentCursorType = type;
            currentCursor = cursor;
        }
        else if ( allowDetach )
        {
            if ( __HUD_base_PrivilegedAccess.getSGNode( cursorImage ).getParent() != null )
                __HUD_base_PrivilegedAccess.getSGNode( cursorImage ).detach();
            
            __HUD_base_PrivilegedAccess.setHUD( null, cursorImage );
            
            cursorImage = null;
            currentCursorType = null;
            currentCursor = null;
        }
        else
        {
            if ( cursorImage != null )
            {
                cursorImage.setVisible( false );
            }
        }
    }
    
    /**
     * This is a convenience- and backwards-compatiblity-method to set the {@link Cursor}.
     * This sets the Cursor.Type#POINTER1 entry in the current {@link CursorSet}.
     * 
     * @param pointer1 the new Cursor to use for pointer1 (or null for no cursor)
     */
    public void setCursor( Cursor pointer1 )
    {
        getCursorSet().setPointer1( pointer1 );
        
        if ( currentCursorType == Cursor.Type.POINTER1 )
        {
            setCursorInternal( pointer1, Cursor.Type.POINTER1, true );
        }
    }
    
    /**
     * This is a convenience- and backwards-compatiblity-method to set the {@link Cursor}.
     * This sets the Cursor.Type#POINTER1 entry in the current {@link CursorSet}.
     * 
     * @param pointer1 the new Cursor to use for pointer1 (or null for no cursor)
     */
    public void setCursor( String pointer1 )
    {
        if ( getCursorSet().getPointer1() == null )
            getCursorSet().setPointer1( new Cursor( pointer1 ) );
        else
            getCursorSet().getPointer1().setTexture( pointer1 );
        
        if ( currentCursorType == Cursor.Type.POINTER1 )
        {
            setCursorInternal( getCursorSet().getPointer1(), Cursor.Type.POINTER1, true );
        }
    }
    
    /**
     * Sets the cursor's visibility.<br>
     * If no cursor image has been set, this call will have no effect!
     * 
     * @param visible
     */
    public void setCursorVisible( boolean visible )
    {
        if ( cursorImage != null )
            cursorImage.setVisible( visible );
    }
    
    /**
     * @return the cursor's current visibility.
     */
    public final boolean isCursorVisible()
    {
        if ( cursorImage == null )
            return ( false );
        
        return ( cursorImage.isVisible() );
    }
    
    private void setCrosshairLocation( Image crosshairImage, int hotspotX, int hotspotY, float imgWidth, float imgHeight )
    {
        if ( ( hotspotX < 0 ) || ( hotspotY < 0 ) )
        {
            final float posX = ( getResX() - imgWidth ) / 2.0f;
            final float posY = ( getResY() - imgHeight ) / 2.0f;
            
            crosshairImage.setLocation( posX, posY );
        }
        else
        {
            Dim2f tmp = Dim2f.fromPool();
            getCoordinatesConverter().getSizePixels2HUD( hotspotX, hotspotY, tmp );
            float posX = ( getResX() - imgWidth ) / 2.0f + hotspotX;
            float posY = ( getResY() - imgHeight ) / 2.0f + hotspotY;
            Dim2f.toPool( tmp );
            
            crosshairImage.setLocation( posX, posY );
        }
        
        this.crosshairHotspotX = hotspotX;
        this.crosshairHotspotY = hotspotY;
    }
    
    /**
     * Sets the Texture for the crosshair.<br>
     * Use <i>null</i> for texture to remove the crosshair.
     * 
     * @param texture the new texture for the crosshair Image (or null for no crosshair)
     * @param hotspotX texture-relative location of the crossheir's hotspot (x)
     * @param hotspotY texture-relative location of the crossheir's hotspot (y)
     */
    public void setCrosshair( Texture2D texture, int hotspotX, int hotspotY )
    {
        if ( texture != null )
        {
            Dim2f tmp = Dim2f.fromPool();
            getCoordinatesConverter().getSizePixels2HUD( HUDTextureUtils.getTextureWidth( texture ), HUDTextureUtils.getTextureHeight( texture ), tmp );
            float imgWidth = tmp.getWidth();
            float imgHeight = tmp.getHeight();
            Dim2f.toPool( tmp );
            
            if ( this.crosshairImage == null )
            {
                crosshairImage = new Image( true, imgWidth, imgHeight, texture, TileMode.TILE_BOTH );
                crosshairImage.setZIndex( CROSSHAIR_Z_INDEX );
                setCrosshairLocation( crosshairImage, hotspotX, hotspotY, imgWidth, imgHeight );
                
                branchGroup.addChild( __HUD_base_PrivilegedAccess.getSGNode( crosshairImage ) );
                __HUD_base_PrivilegedAccess.setHUD( this, crosshairImage );
            }
            else
            {
                crosshairImage.setTexture( texture );
                if ( ( crosshairImage.getWidth() != imgWidth ) || ( crosshairImage.getHeight() != imgHeight ) )
                {
                    crosshairImage.setSize( imgWidth, imgHeight );
                    
                    setCrosshairLocation( crosshairImage, hotspotX, hotspotY, imgWidth, imgHeight );
                }
                else if ( ( hotspotX != this.crosshairHotspotX ) || ( hotspotY != this.crosshairHotspotY ) )
                {
                    setCrosshairLocation( crosshairImage, hotspotX, hotspotY, imgWidth, imgHeight );
                }
            }
        }
        else
        {
            if ( __HUD_base_PrivilegedAccess.getSGNode( crosshairImage ).getParent() != null )
                branchGroup.removeChild( __HUD_base_PrivilegedAccess.getSGNode( crosshairImage ) );
            
            crosshairImage = null;
        }
    }
    
    /**
     * Sets the Texture for the crosshair.<br>
     * Use <i>null</i> for texture to remove the crosshair.
     * 
     * @param texture the new texture for the crosshair Image (or null for no crosshair)
     * @param hotspot texture-relative location of the crossheir's hotspot
     */
    public final void setCrosshair( Texture2D texture, Tuple2i hotspot )
    {
        setCrosshair( texture, hotspot.getX(), hotspot.getY() );
    }
    
    /**
     * Sets the Texture for the corsshair. The texture MUST support an alpha channel.<br>
     * Use <i>null</i> for texture to remove the crosshair.
     * 
     * @param texture the new texture for the crosshair Image (or null for no crosshair)
     * @param hotspotX texture-relative location of the crossheir's hotspot (x)
     * @param hotspotY texture-relative location of the crossheir's hotspot (y)
     */
    public final void setCrosshair( String texture, int hotspotX, int hotspotY )
    {
        setCrosshair( HUDTextureUtils.getTexture( texture, true ), hotspotX, hotspotY );
    }
    
    /**
     * Sets the Texture for the corsshair. The texture MUST support an alpha channel.<br>
     * Use <i>null</i> for texture to remove the crosshair.
     * 
     * @param texture the new texture for the crosshair Image (or null for no crosshair)
     * @param hotspot texture-relative location of the crossheir's hotspot
     */
    public final void setCrosshair( String texture, Tuple2i hotspot )
    {
        setCrosshair( texture, hotspot.getX(), hotspot.getY() );
    }
    
    /**
     * Sets the Texture for the crosshair.<br>
     * Use <i>null</i> for texture to remove the crosshair.
     * 
     * @param texture the new texture for the crosshair Image (or null for no crosshair)
     */
    public final void setCrosshair( Texture2D texture )
    {
        setCrosshair( texture, -1, -1 );
    }
    
    /**
     * Sets the Texture for the corsshair. The texture MUST support an alpha channel.<br>
     * Use <i>null</i> for texture to remove the crosshair.
     * 
     * @param texture the new texture for the crosshair Image (or null for no crosshair)
     */
    public final void setCrosshair( String texture )
    {
        setCrosshair( texture, -1, -1 );
    }
    
    /**
     * Sets the crosshair's visibility.<br>
     * If no crosshair image has been set, this call will have no effect!
     * 
     * @param visible
     */
    public void setCrosshairVisible( boolean visible )
    {
        if ( crosshairImage != null )
            crosshairImage.setVisible( visible );
    }
    
    /**
     * @return the crosshair's current visibility.
     */
    public final boolean isCrosshairVisible()
    {
        if ( crosshairImage == null )
            return ( false );
        
        return ( crosshairImage.isVisible() );
    }
    
    /**
     * @return this HUD's OperationScheduler
     */
    public final OperationScheduler getOperationScheduler()
    {
        return ( opScheduler );
    }
    
    final void updateOperations( long nanoGameTime, long nanoFrameTime )
    {
        opScheduler.update( nanoGameTime, nanoFrameTime, TimingMode.NANOSECONDS );
    }
    
    /**
     * Connects this HUD to the given Sized2i instance (e.g. a Canvas3D)
     * and listenes for its resized event.
     * 
     * @param canvas
     */
    public void connect( ExtSized2i canvas )
    {
        if ( isConnected() )
            return;
        
        this.canvasConnection = new HUDCanvas3DConnection();
        canvas.addResizeListener( canvasConnection );
        
        // when the HUD is first added to the scenegraph, the Widgets need to be updated.
        update();
    }
    
    /**
     * Connects this HUD to the given InputSystem.
     * 
     * @param inputSystem
     */
    public void connect( InputSystem inputSystem )
    {
        if ( inputSystem != null )
        {
            this.inputSystemConnection = new HUDInputSystemConnection( this );
            
            inputSystem.addInputListener( inputSystemConnection );
            inputSystem.addInputStateListener( inputSystemConnection );
            
            if ( inputSystem.hasMouse() )
            {
                final Mouse mouse = inputSystem.getMouse();
                
                lastMousePos.set( mouse.getCurrentX(), mouse.getCurrentY() );
                
                mouse.addMouseStopListener( inputSystemConnection );
            }
        }
    }
    
    /**
     * Disconnects this HUD from the given Sized2i instance (e.g. a Canvas3D)
     * and does not listen for its resized event anymore.
     * 
     * @param canvas
     */
    public void disconnect( ExtSized2i canvas )
    {
        if ( canvasConnection != null )
        {
            canvas.removeResizeListener( canvasConnection );
            canvasConnection = null;
        }
    }
    
    /**
     * Disconnects this HUD from the given InputSystem.
     * 
     * @param inputSystem
     */
    public void disconnect( InputSystem inputSystem )
    {
        if ( ( inputSystem != null ) && ( inputSystemConnection != null ) )
        {
            inputSystem.removeInputListener( inputSystemConnection );
            inputSystem.removeInputStateListener( inputSystemConnection );
            
            if ( inputSystem.hasMouse() )
            {
                final Mouse mouse = inputSystem.getMouse();
                
                mouse.removeMouseStopListener( inputSystemConnection );
            }
            
            this.inputSystemConnection = null;
        }
    }
    
    /**
     * @return if the HUD is currently connected with the {@link SceneGraph}.
     */
    public final boolean isConnected()
    {
        return ( canvasConnection != null );
    }
    
    protected WidgetContainer createDefaultContentPane( boolean heavyWeight, float resX, float resY )
    {
        Panel p = new Panel( heavyWeight, resX, resY, null, null );
        p.setClippingEnabled( false );
        
        p.setName( "HUD ContentPane" );
        
        return ( p );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resolution the resolution of the coordinate system
     * @param contentPane
     * @param createHeavyWeightContentPane
     */
    protected HUD( int canvasWidth, int canvasHeight, Sized2fRO resolution, WidgetContainer contentPane, boolean createHeavyWeightContentPane )
    {
        super();
        
        Node.pushGlobalIgnoreBounds( true );
        this.branchGroup = new BranchGroup();
        Node.popGlobalIgnoreBounds();
        
        float resX = ( resolution == null ) ? canvasWidth : resolution.getWidth();
        float resY = ( resolution == null ) ? canvasHeight: resolution.getHeight();
        
        if ( contentPane == null )
            setContentPane( createDefaultContentPane( createHeavyWeightContentPane, resX, resY ) );
        else
            setContentPane( contentPane );
        
        setSize( canvasWidth, canvasHeight, resolution );
        
        this.lastMousePos = new Tuple2f( getResX() / 2f, getResY() / 2f );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resolution the resolution of the coordinate system
     * @param contentPane
     */
    public HUD( int canvasWidth, int canvasHeight, Sized2fRO resolution, WidgetContainer contentPane )
    {
        this( canvasWidth, canvasHeight, resolution, contentPane, true );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resolution the resolution of the coordinate system
     */
    public HUD( int canvasWidth, int canvasHeight, Sized2fRO resolution )
    {
        this( canvasWidth, canvasHeight, resolution, null );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resX the new width of the coordinate system
     * @param resY the new height of the coordinate system
     */
    public HUD( int canvasWidth, int canvasHeight, float resX, float resY )
    {
        this( canvasWidth, canvasHeight, new Dim2f( resX, resY ) );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resX the new width of the coordinate system
     * @param resY the new height of the coordinate system
     * @param contentPane
     */
    public HUD( int canvasWidth, int canvasHeight, float resX, float resY, WidgetContainer contentPane )
    {
        this( canvasWidth, canvasHeight, new Dim2f( resX, resY ), contentPane );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resolution the resolution of the coordinate system
     */
    public HUD( Sized2iRO canvas, Sized2fRO resolution )
    {
        this( canvas.getWidth(), canvas.getHeight(), resolution );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resolution the resolution of the coordinate system
     * @param contentPane
     */
    public HUD( Sized2iRO canvas, Sized2fRO resolution, WidgetContainer contentPane )
    {
        this( canvas.getWidth(), canvas.getHeight(), resolution, contentPane );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resX the x-resolution of the coordinate system. The y-resolution is calculateed by (resX / canvasAspect).
     */
    public HUD( Sized2iRO canvas, float resX )
    {
        this( canvas.getWidth(), canvas.getHeight(), resX, resX / canvas.getAspect() );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resX the x-resolution of the coordinate system. The y-resolution is calculateed by (resX / canvasAspect).
     * @param createHeavyWeightContentPane
     */
    public HUD( Sized2iRO canvas, float resX, boolean createHeavyWeightContentPane )
    {
        this( canvas.getWidth(), canvas.getHeight(), new Dim2f( resX, resX / canvas.getAspect() ), null, createHeavyWeightContentPane );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resX the x-resolution of the coordinate system. The y-resolution is calculateed by (resX / canvasAspect).
     * @param contentPane
     */
    public HUD( Sized2iRO canvas, float resX, WidgetContainer contentPane )
    {
        this( canvas.getWidth(), canvas.getHeight(), resX, resX / canvas.getAspect(), contentPane );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resX the x-resolution of the coordinate system. The y-resolution is calculateed by (resX / canvasAspect).
     */
    public HUD( int canvasWidth, int canvasHeight, float resX )
    {
        this( canvasWidth, canvasHeight, new Dim2f( resX, resX / ( (float)canvasWidth / (float)canvasHeight ) ) );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     * @param resX the x-resolution of the coordinate system. The y-resolution is calculateed by (resX / canvasAspect).
     * @param contentPane
     */
    public HUD( int canvasWidth, int canvasHeight, float resX, WidgetContainer contentPane )
    {
        this( canvasWidth, canvasHeight, new Dim2f( resX, resX / ( (float)canvasWidth / (float)canvasHeight ) ), contentPane );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resX the new width of the coordinate system
     * @param resY the new height of the coordinate system
     */
    public HUD( Sized2iRO canvas, float resX, float resY )
    {
        this( canvas, new Dim2f( resX, resY ) );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resX the new width of the coordinate system
     * @param resY the new height of the coordinate system
     * @param createHeavyWeightContentPane
     */
    public HUD( Sized2iRO canvas, float resX, float resY, boolean createHeavyWeightContentPane )
    {
        this( canvas.getWidth(), canvas.getHeight(), new Dim2f( resX, resY ), null, createHeavyWeightContentPane );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the aspect ratio from
     * @param resX the new width of the coordinate system
     * @param resY the new height of the coordinate system
     * @param contentPane
     */
    public HUD( Sized2iRO canvas, float resX, float resY, WidgetContainer contentPane )
    {
        this( canvas, new Dim2f( resX, resY ), contentPane );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvasWidth the width of the Canvas3D to take the resolution and aspect from
     * @param canvasHeight the width of the Canvas3D to take the resolution and aspect from
     */
    public HUD( int canvasWidth, int canvasHeight )
    {
        this( canvasWidth, canvasHeight, (Sized2fRO)null );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the resolution and aspect ratio from
     */
    public HUD( Sized2iRO canvas )
    {
        this( canvas, (Sized2fRO)null );
    }
    
    /**
     * Creates a new HUD with a coordinate system with the given width and height
     * 
     * @param canvas the Canvas3D to take the resolution and aspect ratio from
     */
    public HUD( Sized2iRO canvas, boolean createHeavyWeightContentPane )
    {
        this( canvas.getWidth(), canvas.getHeight(), (Sized2fRO)null, null, createHeavyWeightContentPane );
    }
}
