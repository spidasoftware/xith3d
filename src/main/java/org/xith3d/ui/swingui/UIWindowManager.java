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
package org.xith3d.ui.swingui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.openmali.FastMath;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderPass;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.OrderedGroup;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * The overlay manager keeps track of all the overlay's on the screen and
 * makes sure they are updated with the view transform once a frame.  The
 * Overlay manager should be placed into the XithBackground in the last
 * ordered group to ensure that it is rendered last.
 * <p>
 * 
 * Originally Coded by David Yazel on Oct 4, 2003 at 11:29:28 PM.
 */
public class UIWindowManager extends RenderPass
{
    private static float consoleZ = 7f;
    
    //private static float consoleZ = 4f;
    //private static int MAX_OVERLAYS = 20;
    private static int OM_NONE = 0;
    private static int OM_MOVE_WINDOW = 1;
    private static int OM_DRAGGING = 2;
    private static int OM_DRAGGING_NONE = 3;
    //private static int OM_DRAGGING_MAYBE = 4;
    
    private TransformGroup consoleTG; // transform group for the image plate
    private Group windows; // un-ordered group of UIPositionedWindow
    private OrderedGroup orderedWindows; // ordered group of UIPositionedWindow
    public Transform3D planeOffset; // transform of image plate
    public Transform3D worldTransform;
    public Transform3D lastTransform;
    private ArrayList< UIPositionedWindow > windowList = null;
    private float consoleWidth; // width of console
    private float consoleHeight; // height of console
    private float scale; // scale into world coordinates
    private Dimension canvasDim; // the dimensions of the Canvas3d
    private Dimension checkDim; // used to check for dimension changes
    private int mode = OM_NONE; // the mode we are in with the window
    private UIPositionedWindow focus; // current focused window.
    private UIPositionedWindow lastWindow = null; // used for fast checking mouse events
    private Canvas3D c3d;
    private boolean isDirectX = false;
    private int checks = 0;
    //private long dragStart;
    
    // objects needed for drag and drop support
    private UIDraggingInformation draggingInfo = null;
    private UIPositionedWindow dragWindow = null;
    
    // used for picking into the world and finding objects
    private boolean mouseClicked = false;
    
    /**
     * @return the first window that contains the specified point.  If the
     * windows are stacked then the top most window will be picked.
     */
    private UIPositionedWindow getWindow( Point p )
    {
        if ( lastWindow != null )
        {
            if ( lastWindow.isVisible() && lastWindow.contains( p ) )
            {
                return ( lastWindow );
            }
        }
        
        UIPositionedWindow w = null;
        int n = windowList.size();
        
        for ( int i = 0; i < n; i++ )
        {
            w = windowList.get( i );
            
            if ( w != dragWindow )
            {
                if ( w.isVisible() && w.contains( p ) )
                {
                    lastWindow = w;
                    
                    return ( w );
                }
            }
        }
        
        return ( null );
    }
    
    public void calcImagePlate()
    {
        // get the field of view and then calculate the width in meters of the
        // screen
        canvasDim.height = c3d.getHeight();
        canvasDim.width = c3d.getWidth();
        
        float aspect = (float)canvasDim.width / (float)canvasDim.height;
        
        float fovy = c3d.getView().getFieldOfView();
        
        //float xmin;
        float xmax;
        //float ymin;
        float ymax;
        
        ymax = consoleZ * FastMath.tan( fovy );
        //ymin = -ymax;
        //xmin = ymin * aspect;
        xmax = ymax * aspect;
        
        scale = ( ymax * 2 ) / canvasDim.height;
        
        consoleWidth = xmax * 2;
        consoleHeight = ymax * 2;
        
        //float screenScale = c3d.getView().getScreenScale();
        //consoleHeight = (FastMath.tan( fovy / 2.0 ) * consoleZ) * 2.0f;
        //consoleWidth = consoleHeight * aspect;
        //scale = consoleHeight / canvasDim.height;
        X3DLog.debug( "Building overlay mapping" );
        
        //X3DLog.println( LogType.EXHAUSTIVE, "  Field of view up and down is : " + fov + " or "+ FastMath.toDeg( fov )+" degrees" );
        //X3DLog.println( LogType.EXHAUSTIVE, "  Screen scale : " + screenScale );
        //get the canvas width in pixels, then calculate the scale from
        // pixels to meters.  Then calculate the height of the screen in meters
        //c3d.getSize( canvasDim );
        if ( canvasDim.width != 0 )
        {
            // build the plane offset
            X3DLog.debug( "  Overlay scale : ", scale );
            X3DLog.debug( "  aspect is : ", aspect );
            X3DLog.debug( "  Screen size is : ", canvasDim );
            X3DLog.debug( "  console width (meters) : ", consoleWidth );
            X3DLog.debug( "  console height (meters) : ", consoleHeight );
            
            float texelOffset = 0;
            
            if ( isDirectX )
            {
                texelOffset = scale / 2.0f;
            }
            
            X3DLog.debug( "  Driver is : ", ( isDirectX ? "Direct X" : "OpenGL" ) );
            X3DLog.debug( "  Texel Offset : ", texelOffset );
            
            planeOffset.setTranslation( new Vector3f( ( -consoleWidth / 2.0f ) - texelOffset, ( -consoleHeight / 2.0f ) - texelOffset, -consoleZ ) );
            planeOffset.setScale( scale );
        }
        else
        {
            X3DLog.error( "Cannot calculate image plate" );
        }
        
        checkDim.setSize( canvasDim );
    }
    
    /**
     * Called each frame prior to rendering to update the overlays.
     * Convenience method, calls <code>newFrame(view.getTransform());</code>
     * 
     * @param view the view who's transform is used to update the overlays.
     */
    public void newFrame( View view )
    {
        newFrame( view.getTransform() );
    }
    
    /**
     * Called once a frame to update the different overlays.
     */
    public void newFrame( Transform3D viewTransform )
    {
        /*
        if ( !this.isLive() )
        {
            return;
        }
        */
        if ( windowList.size() == 0 )
        {
            return;
        }
        
        if ( ( checks % 100 ) == 0 )
        {
            //checkScreenSize();
        }
        
        //Vector3f location = new Vector3f();
        
        worldTransform.set( viewTransform );
        
        //worldTransform.invert();
        //worldTransform.get( location );
        
        //Log.log.println( LogType.EXHAUSTIVE, "  View location is  : " + location );
        worldTransform.mul( planeOffset );
        consoleTG.setTransform( worldTransform );
        
        // now update all the visible, assigned overlays
        final int n = windowList.size();
        for ( int i = 0; i < n; i++ )
        {
            final UIPositionedWindow w = windowList.get( i );
            
            if ( w.isAssigned() )
            {
                if ( w.isVisible() )
                {
                    w.getOverlay().update();
                }
            }
        }
    }
    
    /**
     * This adds an overlay into the overlay manager system.  This finds a free spot
     * in the list of overlays and inserts it, then pops it to the front.
     */
    public UIPositionedWindow addOverlay( UIOverlayInterface overlay )
    {
        UIPositionedWindow w = new UIPositionedWindow();
        w.assign( overlay );
        if ( overlay instanceof UIWindow )
            ( (UIWindow)overlay ).setWindowManager( this );
        
        if ( overlay instanceof UIWindow )
            w.setDraggable( ( (UIWindow)overlay ).isDraggable() );
        
        if ( overlay.isOpaque() )
        {
            orderedWindows.addChild( w );
        }
        else
        {
            windows.addChild( w );
        }
        
        windowList.add( w );
        
        return ( w );
    }
    
    /**
     * This removes the overlay from the overlay system.  The underlying
     * resources will be released.
     */
    public void removeOverlay( UIOverlayInterface overlay )
    {
        UIPositionedWindow w = getWindow( overlay );
        
        if ( w != null )
        {
            w.empty();
        }
    }
    
    /**
     * Sets the visibility of the window
     */
    public void setVisible( UIOverlayInterface overlay, boolean show )
    {
        UIPositionedWindow w = getWindow( overlay );
        
        if ( w != null )
        {
            w.setVisible( show );
        }
    }
    
    public boolean isVisible( UIOverlayInterface overlay )
    {
        UIPositionedWindow w = getWindow( overlay );
        
        if ( w == null )
            return ( false );
        
        return ( w.isVisible() );
    }
    
    /**
     * This finds the current window associated with an overlay.
     */
    private UIPositionedWindow getWindow( UIOverlayInterface overlay )
    {
        UIPositionedWindow w = null;
        int n = windowList.size();
        
        for ( int i = 0; i < n; i++ )
        {
            w = windowList.get( i );

            if ( w.getOverlay() == overlay )
            {
                return ( w );
            }
        }
        
        X3DLog.error( "Unknown overlay window referenced" );
        
        return ( null );
    }
    
    /**
     * Sets the position of the specified overlay
     */
    public void setPosition( UIOverlayInterface overlay, int x, int y )
    {
        UIPositionedWindow w = getWindow( overlay );
        
        if ( w != null )
        {
            w.setPosition( x, y, canvasDim );
            
            /*
            Dimension d = new Dimension();
            w.getOverlay().getSize(d);
            w.setPosition( (float)x, (float)canvasDim.height - y - d. height );
            */
        }
    }
    
    public int getX( UIOverlayInterface overlay )
    {
        UIPositionedWindow w = getWindow( overlay );
        
        if ( w != null )
            return ( w.getX() );
        
        return ( -1 );
    }
    
    public int getY( UIOverlayInterface overlay )
    {
        UIPositionedWindow w = getWindow( overlay );
        
        if ( w != null )
            return ( w.getY() );
        
        return ( -1 );
    }
    
    public void processEvent( AWTEvent e )
    {
        if ( e instanceof MouseEvent )
        {
            MouseEvent mev = (MouseEvent)e;
            processMouseEvent( new MouseEvent( (Component)mev.getSource(), mev.getID(), mev.getWhen(), mev.getModifiers(), mev.getX(), mev.getY(), mev.getClickCount(), false ) );
        }
        else if ( e instanceof KeyEvent )
        {
            if ( focus != null )
            {
                Object o = focus.getOverlay();
                
                if ( o instanceof UIWindow )
                {
                    ( (UIWindow)o ).dispatchEvent( e );
                }
            }
            //else Log.print( "there is no focused window for key event " + e );
        }
    }
    
    public void setOverlayFocus( UIOverlayInterface overlay )
    {
        UIPositionedWindow w = getWindow( overlay );
        setOverlayFocus( w );
    }
    
    public void setOverlayFocus( UIPositionedWindow w )
    {
        if ( w == focus )
            return;
        
        boolean focusFalse = true;
        
        if ( focus != null )
        {
            if ( focus.getOverlay() instanceof UIWindow )
            {
                UIWindow uiw = (UIWindow)focus.getOverlay();
                
                if ( uiw.last != null )
                {
                    uiw.last.dispatchEvent( new MouseEvent( uiw.last, MouseEvent.MOUSE_EXITED, 0, MouseEvent.BUTTON1_MASK, uiw.last.getLocation().x, uiw.last.getLocation().y, 0, false ) );
                    
                    uiw.last = null;
                }
                
                if ( !( uiw.textComponentFocus ) || mouseClicked )
                    uiw.setFocus( false );
                else
                    focusFalse = false;
            }
        }
        
        if ( focusFalse )
        {
            focus = w;
            if ( w != null )
            {
                if ( focus.getOverlay() instanceof UIWindow )
                    ( (UIWindow)focus.getOverlay() ).setFocus( true );
            }
        }
    }
    
    public synchronized void processMouseEvent( MouseEvent me )
    {
        //boolean rightButton = ((MouseEvent.BUTTON3_MASK & me.getModifiers()) != 0);
        boolean leftButton = ( ( InputEvent.BUTTON1_MASK & me.getModifiers() ) != 0 );
        
        UIPositionedWindow w = getWindow( me.getPoint() );
        
        boolean eventConsumed = false;
        
        if ( me.getID() == MouseEvent.MOUSE_MOVED )
        {
            if ( mode == OM_MOVE_WINDOW )
            {
                focus.setPosition( me.getX(), me.getY(), canvasDim );
                eventConsumed = true;
            } //else if (w == null) setOverlayFocus( (UIPositionedWindow)null );
        }
        
        if ( me.getID() == MouseEvent.MOUSE_DRAGGED )
        {
            //Log.log.println( LogType.EXHAUSTIVE, "Mouse dragged at " + me.getPoint() );
            if ( mode == OM_NONE )
            {
                mode = OM_DRAGGING_NONE;
                
                if ( w != null )
                {
                    Object o = w.getOverlay();
                    
                    X3DLog.debug( "Overlay type is ", o.getClass().getSimpleName() );
                    
                    if ( o instanceof UIDragDropInterface )
                    {
                        me.translatePoint( -(int)w.getRectangle().getX(), -(int)w.getRectangle().getY() );
                        draggingInfo = ( (UIDragDropInterface)o ).startDrag( me );
                        
                        if ( draggingInfo != null )
                        {
                            X3DLog.debug( "Mouse dragging object!" );
                            
                            // let a click go through for this since we are
                            // dragging it anyway
                            if ( o instanceof UIWindow )
                            {
                                MouseEvent mec = new MouseEvent( me.getComponent(), MouseEvent.MOUSE_CLICKED, me.getWhen(), me.getModifiers(), me.getX(), me.getY(), me.getClickCount(), false );
                                
                                ( (UIWindow)o ).dispatchEvent( mec );
                            }
                            
                            mode = OM_DRAGGING;
                            
                            // if the drag overlay has not been created yet go ahead
                            // and make it
                            if ( dragWindow == null )
                            {
                                UIDraggingWindow dw = new UIDraggingWindow();
                                dragWindow = addOverlay( dw );
                            }
                            
                            // give the drag window the dragging information so it knows
                            // how to draw itself
                            ( (UIDraggingWindow)dragWindow.getOverlay() ).setDi( draggingInfo );
                            
                            // translate the mouse event back to original screen position
                            // and then set the dragging window position to the mouse position
                            me.translatePoint( (int)w.getRectangle().getX(), (int)w.getRectangle().getY() );
                            dragWindow.setPosition( me.getX(), me.getY(), canvasDim );
                            
                            // make the drag window visible
                            dragWindow.setVisible( true );
                            eventConsumed = true;
                        }
                        else
                        {
                            // translate back
                            me.translatePoint( (int)w.getRectangle().getX(), (int)w.getRectangle().getY() );
                        }
                    }
                }
            }
            else if ( mode == OM_DRAGGING )
            {
                dragWindow.setPosition( me.getX(), me.getY(), canvasDim );
                
                // if there is an overlay underneath the icon, and it
                // is drag and drop enabled then check to see if this could
                // receive it.
                boolean canReceive = false;
                
                if ( w != null )
                {
                    Object o = w.getOverlay();
                    
                    if ( o instanceof UIDragDropInterface )
                    {
                        me.translatePoint( -(int)w.getRectangle().getX(), -(int)w.getRectangle().getY() );
                        canReceive = ( (UIDragDropInterface)o ).dragging( me, draggingInfo );
                    }
                }
                eventConsumed = true;
                ( (UIDraggingWindow)dragWindow.getOverlay() ).setCanDrop( canReceive );
            }
        }
        
        if ( me.getID() == MouseEvent.MOUSE_RELEASED )
        {
            if ( mode == OM_DRAGGING_NONE )
            {
                X3DLog.debug( "Mouse done dragging none" );
                mode = OM_NONE;
            }
            else if ( mode == OM_DRAGGING )
            {
                mode = OM_NONE;
                dragWindow.setVisible( false );
                
                boolean dropped = false;
                if ( w != null )
                {
                    X3DLog.debug( "Mouse drop on window" );
                    
                    Object o = w.getOverlay();
                    
                    if ( draggingInfo != null )
                    {
                        if ( o instanceof UIDragDropInterface )
                        {
                            // translate into coordinate space of window
                            me.translatePoint( -(int)w.getRectangle().getX(), -(int)w.getRectangle().getY() );
                            
                            dropped = ( (UIDragDropInterface)o ).dropped( me, draggingInfo );
                            
                            // translate back
                            me.translatePoint( +(int)w.getRectangle().getX(), +(int)w.getRectangle().getY() );
                        }
                    }
                }
                
                // if dropped failed then callback
                if ( !dropped )
                {
                    if ( ( draggingInfo != null ) && ( draggingInfo.getFailure() != null ) )
                    {
                        UIOverlayInterface o = null;
                        if ( w != null )
                            o = w.getOverlay();
                        draggingInfo.getFailure().dropFailed( me.getX(), me.getY(), draggingInfo, o );
                    }
                }
                draggingInfo = null;
                eventConsumed = true;
            }
        }
        
        if ( me.getID() == MouseEvent.MOUSE_CLICKED )
        {
            X3DLog.debug( "Mouse clicked at ", me.getPoint() );
            if ( w != null )
                setOverlayFocus( w );
            
            // if we are moving the window then we don't care if there is a current window
            // under the cursor
            if ( ( mode == OM_MOVE_WINDOW ) && leftButton )
            {
                mode = OM_NONE;
                X3DLog.debug( "Done moving window" );
                focus.setPosition( me.getX(), me.getY(), canvasDim );
            }
            else if ( w != null )
            {
                X3DLog.debug( "Mouse clicked in window " );
                
                // if the mouse is being left clicked in the upper left hand corner of
                // the window then go into the window movement mode.
                if ( w.isDraggable() )
                {
                    if ( leftButton && ( mode == OM_NONE ) )
                    {
                        Rectangle bounds = new Rectangle( w.getRectangle() );
                        bounds.setSize( 15, 15 );
                        
                        if ( bounds.contains( me.getPoint() ) )
                        {
                            X3DLog.debug( "Begin moving window" );
                            if ( w != null )
                                setOverlayFocus( w );
                            mode = OM_MOVE_WINDOW;
                        }
                    }
                }
            }
            else
            {
                // if we get here then the click was not in a window, so we need to
                // do picking against the 3d scene to find the object, if there is one.
                
                setOverlayFocus( (UIPositionedWindow)null );
                
                /*
                Log.print( "Attempting to find object at pick point" );
                SynchronizedObject o = getPickedObject( me, mobiles );
                if (o == null)
                {
                    o = getPickedObject( me, structures );
                }
                if (o != null)
                {
                    Log.print( "Found picked object : " + o.getObjectName() );
                    CMM.post( new CMTarget( o ) );
                }
                */
            }
        }
        
        // if we get here, but we have not consumed the mouse message then
        // determine if the mouse event should be sent
        if ( !eventConsumed )
        {
            if ( w != null )
            {
                Object o = w.getOverlay();
                
                if ( o instanceof UIWindow )
                {
                    if ( ( (UIWindow)o ).isDisabled() )
                        return;
                    
                    me.translatePoint( -(int)w.getRectangle().getX(), -(int)w.getRectangle().getY() );
                    ( (UIWindow)o ).dispatchEvent( me );
                }
            }
        }
    }
    
    public UIWindowManager( Canvas3D c )
    {
        super( new BaseRenderPassConfig() );
        
        this.c3d = c;
        
        //check to see if we are using direct x or OpenGL
        /*
        Map m = c.queryProperties();
        String nativeVersion = (String)m.get( "native.version" );
        if (nativeVersion.startsWith( "DirectX" ))
        {
            isDirectX = true;
        }
        */
        windowList = new ArrayList< UIPositionedWindow >( 20 );
        
        BranchGroup root = this.getBranchGroup();
        root.setPickable( true );
        consoleTG = new TransformGroup();
        root.addChild( consoleTG );
        
        consoleTG.setTransform( new Transform3D() );
        
        // define the dimensions and transforms used by the overlay manager
        canvasDim = new Dimension();
        checkDim = new Dimension();
        planeOffset = new Transform3D();
        worldTransform = new Transform3D();
        lastTransform = new Transform3D();
        
        calcImagePlate();
        
        /*
        Group p = XithEngine.getEngine().getUniverse().getPlatformGeometry();
        Group pg = new Group();
        pg.addChild(getTestShapeOpenGL( 0, 0, 64, 64, Color.red ) );
        pg.addChild(getTestShapeOpenGL( 64, 0, 64, 64, Color.blue ) );
        pg.addChild(getTestShapeOpenGL( 0, 64, 64, 64, Color.green ) );
        pg.addChild(getTestShapeOpenGL( 64, 64, 64, 64, Color.magenta ) );
        pg.addChild(getTestShapeOpenGL( 128, 128, 64, 64, Color.gray ) );
        pg.addChild(getTestShapeOpenGL( 128 + 64, 128, 16, 64, Color.yellow ) );
        p.addChild( pg );
        */
        //addTestShape( 0, 0, 100, 100);
        //addTestShape( 100, 0, 100, 100);
        // define the ordered group that will have all the UIPositionedWindow's. They
        // are placed in an ordered group so that we can control window stacking.
        windows = new Group();
        windows.setIgnoreBounds( true );
        
        consoleTG.addChild( windows );
        
        orderedWindows = new OrderedGroup();
        consoleTG.addChild( orderedWindows );
        consoleTG.setTransform( planeOffset );
        
        //c.setWindowManager( this );
    }
}
