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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.text.Caret;

import org.xith3d.scenegraph.Node;
import org.xith3d.utility.logging.X3DLog;

/**
 * A UI window is a special container for Swing components which can be rendered
 * on a Xith3D canvas.
 * 
 * @author David Yazel
 */
public class UIWindow implements UIOverlayInterface, UIDragDropInterface
{
    private BufferedImage buffer;
    
    private int width;
    private int height;
    private boolean clipAlpha;
    private boolean blendAlpha;
    private Frame window;
    private Component focusedComponent = null;
    private JComponent root;
    private static UIRepaintManager repaintMgr = new UIRepaintManager();
    //private RepaintManager defaultMgr ;
    private UIOverlay overlay;
    private boolean packed = false;
    private UIWindowManager manager = null;
    
    private boolean disabled = false;
    private boolean draggable = false;
    boolean textComponentFocus = false;
    Component last = null;
    
    public int getWidth()
    {
        return ( width );
    }
    
    public int getHeight()
    {
        return ( height );
    }
    
    public void setWindowManager( UIWindowManager manager )
    {
        this.manager = manager;
    }
    
    public UIWindowManager getWindowManager()
    {
        return ( manager );
    }
    
    public void setRoot( JComponent root )
    {
        window.add( root );
        this.root = root;
        pack();
        pack();
        repaintMgr.markCompletelyDirty( root );
    }
    
    private void constructBuffer()
    {
        if ( clipAlpha || blendAlpha )
            buffer = new BufferedImage( width + 50, height + 50, BufferedImage.TYPE_INT_ARGB );
        //buffer = DirectBufferedImage.getDirectImageRGBA( width + 50, height + 50 );
        else
            buffer = new BufferedImage( width + 50, height + 50, BufferedImage.TYPE_INT_RGB );
        //buffer = DirectBufferedImage.getDirectImageRGB( width + 50, height + 50 );
        //buffer = new BufferedImage( width + 50, height + 50, BufferedImage.TYPE_INT_RGB );
    }
    
    public BufferedImage getBuffer()
    {
        return ( buffer );
    }
    
    public UIOverlay getOverlay()
    {
        return ( overlay );
    }
    
    /*
    private void renderToBuffer(Point curPoint, Component c, Graphics g)
    {
        System.out.println( "rendering " + c.getClass().getName() + " at " + c.getLocation() + " size " + c.getSize() );
        Point p = new Point( c.getLocation() );
        //p.x += curPoint.x;
        //p.y += curPoint.y;
        g.translate( p.x, p.y );
        //g.setClip( p.x, p.y, c.getSize().width, c.getSize().height );
        g.setClip( 0, 0, c.getSize().width, c.getSize().height );
        g.setFont( c.getFont() );
        g.setColor( c.getForeground() );
        c.paint( g );
        c.paintAll( g );
        
        if (c instanceof Container)
        {
            Container ca = (Container)c;
            Component cs[] = ca.getComponents();
            for (int i = 0; i < cs.length; i++)
            {
                renderToBuffer( p, cs[ i ], g );
            }
        }
        g.translate( -p.x, -p.y );
    }
    */
    
    /*
    private MouseEvent adjustMouse(Component c, MouseEvent me)
    {
        if (c == null)
            return me;
        
        Point mousePoint = me.getPoint();
        Point compPoint = new Point( c.getLocation() );
        Point newPoint = new Point( mousePoint.x - compPoint.x, mousePoint.y - compPoint.y );
        MouseEvent newEvent = new MouseEvent( c, me.getID(), me.getWhen(), me.getModifiers(), newPoint.x, newPoint.y, me.getClickCount(), false, me.getButton() );
        
        return ( newEvent );
    }
    */
    
    private Component dispatchMouseEvent( int index, Component c, MouseEvent me )
    {
        /*
        for (int i = 0; i < index; i++)
            System.out.print( "  " );
        */
        //System.out.println( "checking " + c.getClass().getName() + " for mouse " + me.getPoint() + " against " + c.getLocation() );
        Point mousePoint = me.getPoint();
        Component retValue = null;
        if ( c instanceof Container )
        {
            Container ca = (Container)c;
            Component cs[] = ca.getComponents();
            
            for ( int i = 0; i < cs.length; i++ )
            {
                if ( cs[ i ].getBounds().contains( mousePoint ) && cs[ i ].isVisible() )
                {
                    //System.out.println( "container hit : " + cs[i].getClass().getName() );
                    Point compPoint = new Point( cs[ i ].getLocation() );
                    Point newPoint = new Point( mousePoint.x - compPoint.x, mousePoint.y - compPoint.y );
                    MouseEvent newEvent = new MouseEvent( cs[ i ], me.getID(), me.getWhen(), me.getModifiers(), newPoint.x, newPoint.y, me.getClickCount(), false, me.getButton() );
                    Component focus = dispatchMouseEvent( index + 1, cs[ i ], newEvent );
                    if ( retValue == null && focus != null )
                        retValue = focus;
                    
                    if ( me.isConsumed() )
                    {
                        System.out.println( "consumed" );
                        return ( cs[ i ] );
                    }
                }
            }
        }
        
        //System.out.println( "dispatching event to " + c.getClass().getName() );
        
        if ( !c.isVisible() )
            return ( null );
        if ( !c.isEnabled() )
            return ( null );
        c.dispatchEvent( me );
        
        // if we already have a new focus then keep that one
        if ( retValue != null )
            return ( retValue );
        
        // check to see if this can have focus
        if ( c.isFocusable() )
            return ( c );
        
        return ( null );
    }
    
    /**
     * Finds the deepest component that will accept the dragging request.
     * 
     * @return null if there is no such component.
     */
    private UIDraggingInformation startDrag( int index, Component c, MouseEvent me )
    {
        Point mousePoint = me.getPoint();
        UIDraggingInformation retValue = null;
        X3DLog.debug( "c is of type : ", c.getClass().getSimpleName() );
        if ( c instanceof Container )
        {
            Container ca = (Container)c;
            Component cs[] = ca.getComponents();
            
            for ( int i = 0; i < cs.length; i++ )
            {
                if ( cs[ i ].getBounds().contains( mousePoint ) && cs[ i ].isVisible() )
                {
                    Point compPoint = new Point( cs[ i ].getLocation() );
                    me.translatePoint( -compPoint.x, -compPoint.y );
                    UIDraggingInformation focus = startDrag( index + 1, cs[ i ], me );
                    me.translatePoint( compPoint.x, compPoint.y );
                    if ( retValue == null && focus != null )
                        retValue = focus;
                }
            }
        }
        
        if ( !c.isVisible() )
        {
            X3DLog.debug( "return null" );
            return ( null );
        }
        
        // if we already have a new focus then keep that one
        if ( retValue != null )
        {
            X3DLog.debug( "return retValue" );
            return ( retValue );
        }
        
        // check to see if this can have focus
        if ( c instanceof UIDragDropInterface )
        {
            return ( ( (UIDragDropInterface)c ).startDrag( me ) );
        }
        
        X3DLog.debug( "return null cause no focus" );
        return ( null );
    }
    
    /**
     * Finds the deepest component that will accept the dragging request.
     * 
     * @return null if there is no such component.
     */
    public boolean dragging( int index, Component c, MouseEvent me, UIDraggingInformation info )
    {
        Point mousePoint = me.getPoint();
        boolean retValue = false;
        if ( c instanceof Container )
        {
            Container ca = (Container)c;
            Component cs[] = ca.getComponents();
            
            for ( int i = 0; i < cs.length; i++ )
            {
                if ( cs[ i ].getBounds().contains( mousePoint ) && cs[ i ].isVisible() )
                {
                    Point compPoint = new Point( cs[ i ].getLocation() );
                    me.translatePoint( -compPoint.x, -compPoint.y );
                    boolean focus = dragging( index + 1, cs[ i ], me, info );
                    me.translatePoint( compPoint.x, compPoint.y );
                    if ( retValue == false && focus == true )
                        retValue = focus;
                }
            }
        }
        
        if ( !c.isVisible() )
            return ( false );
        
        // if we already have a new focus then keep that one
        if ( retValue != false )
            return ( retValue );
        
        // check to see if this can have focus
        if ( c instanceof UIDragDropInterface )
            return ( ( (UIDragDropInterface)c ).dragging( me, info ) );
        
        return ( false );
    }
    
    /**
     * Finds the deepest component that will accept the drop request.
     * 
     * @return false if there is no such component.
     */
    public boolean drop( int index, Component c, MouseEvent me, UIDraggingInformation info )
    {
        Point mousePoint = me.getPoint();
        boolean retValue = false;
        if ( c instanceof Container )
        {
            Container ca = (Container)c;
            Component cs[] = ca.getComponents();
            
            for ( int i = 0; i < cs.length; i++ )
            {
                if ( cs[ i ].getBounds().contains( mousePoint ) && cs[ i ].isVisible() )
                {
                    Point compPoint = new Point( cs[ i ].getLocation() );
                    me.translatePoint( -compPoint.x, -compPoint.y );
                    boolean focus = drop( index + 1, cs[ i ], me, info );
                    me.translatePoint( compPoint.x, compPoint.y );
                    if ( retValue == false && focus == true )
                        retValue = focus;
                }
            }
        }
        
        if ( !c.isVisible() )
            return ( false );
        
        // if we already have a new focus then keep that one
        if ( retValue != false )
            return ( retValue );
        
        // check to see if this can have focus
        if ( c instanceof UIDragDropInterface )
            return ( ( (UIDragDropInterface)c ).dropped( me, info ) );
        
        return ( false );
    }
    
    private void getDirtyAreas( Point curPoint, JComponent c, ArrayList< UIDirtyRegion > areas )
    {
        if ( !c.isVisible() )
            return;
        
        //System.out.println( "rendering " + c.getClass().getName() + " at " + c.getLocation() + " size " + c.getSize() );
        Point p = new Point( c.getLocation() );
        p.x += curPoint.x;
        p.y += curPoint.y;
        
        if ( c instanceof Container )
        {
            Container ca = c;
            Component cs[] = ca.getComponents();
            for ( int i = 0; i < cs.length; i++ )
            {
                if ( cs[ i ] instanceof JComponent )
                    getDirtyAreas( p, (JComponent)cs[ i ], areas );
            }
        }
        
        // get the dirty rectangle, if there is one.
        UIDirtyRegion dr = repaintMgr.getUIDirtyRegion( c );
        if ( dr != null )
        {
            Rectangle absR = new Rectangle( dr.getCompDirty() );
            Point newPoint = new Point( absR.x + p.x, absR.y + p.y );
            absR.setLocation( newPoint );
            dr.setAbsDirty( absR );
            areas.add( dr );
            repaintMgr.removeUIDirtyRegion( c );
        }
    }
    
    public ArrayList< ? > getDirtyAreas()
    {
        ArrayList< UIDirtyRegion > list = new ArrayList< UIDirtyRegion >();
        getDirtyAreas( new Point(), (JComponent)getRootComponent(), list );
        
        return ( list );
    }
    
    public void pack()
    {
        if ( root instanceof JInternalFrame )
            ( (JInternalFrame)root ).pack();
        
        pack( getRootComponent() );
    }
    
    private void pack( Component c )
    {
        if ( c instanceof Container )
        {
            Container ca = (Container)c;
            Component cs[] = ca.getComponents();
            
            for ( int i = 0; i < cs.length; i++ )
            {
                pack( cs[ i ] );
            }
        }
        
        // absolutely critical... the lightweight peer is not assigned until
        // this is done... took 6 hours to figure this out - yazel
        if ( c.getParent() != null )
            c.addNotify();
        
        if ( c instanceof JComponent )
        {
            ( (JComponent)c ).doLayout();
            ( (JComponent)c ).revalidate();
        }
        
        c.setVisible( true );
        
        if ( c instanceof JTextField )
        {
            Caret caret = ( (JTextField)c ).getCaret();
            caret.setBlinkRate( 0 );
        }
    }
    
    private Component getRootComponent()
    {
        return ( root );
    }
    
    public void setComponentFocus( Component focus )
    {
        boolean same = ( focus == focusedComponent );
        if ( focusedComponent != null )
        {
            if ( focusedComponent instanceof JTextField )
            {
                textComponentFocus = false;
                
                Caret caret = ( (JTextField)focusedComponent ).getCaret();
                if ( caret.isVisible() )
                {
                    caret.setBlinkRate( 0 );
                    caret.setVisible( false );
                }
            }
            
            if ( !same )
            {
                //System.out.println( "setting component to focus lost" );
                if ( focusedComponent instanceof FocusListener )
                {
                    //FocusListener listener = (FocusListener)focusedComponent;
                    //listener.focusGained( new FocusEvent( focusedComponent, FocusEvent.FOCUS_LOST ) );
                }
                //focusedComponent.dispatchEvent( new FocusEvent( focusedComponent, FocusEvent.FOCUS_LOST ) );
            }
        }
        
        if ( focus != null && focus.isEnabled() )
        {
            focusedComponent = focus;
            getWindowManager().setOverlayFocus( this );
            
            if ( focusedComponent instanceof JTextField )
            {
                textComponentFocus = true;
                
                /*
                int x = focusedComponent.getLocation().x + 1;
                int y = focusedComponent.getLocation().y + 1;
                
                // this stupid thing is to force focus
                
                focusedComponent.dispatchEvent( new MouseEvent( focusedComponent, MouseEvent.MOUSE_PRESSED,
                                                                0, MouseEvent.BUTTON1_MASK, x, y, 1, false ) );
                focusedComponent.dispatchEvent( new MouseEvent( focusedComponent, MouseEvent.MOUSE_RELEASED,
                                                                0, MouseEvent.BUTTON1_MASK, x, y, 1, false ) );
                focusedComponent.dispatchEvent( new MouseEvent( focusedComponent, MouseEvent.MOUSE_CLICKED,
                                                                0, MouseEvent.BUTTON1_MASK, x, y, 1, false ) );
                
                // this is to show
                Caret caret = ((JTextField)focusedComponent).getCaret();
                caret.setBlinkRate( 0 );
                caret.setVisible( true );
                */
            }
            
            if ( !same )
            {
                //System.out.println( "setting component to focus gained" );
                if ( focusedComponent instanceof FocusListener )
                {
                    //FocusListener listener = (FocusListener)focusedComponent;
                    //listener.focusGained( new FocusEvent( focusedComponent, FocusEvent.FOCUS_GAINED ) );
                }
            }
            //focusedComponent.dispatchEvent( new FocusEvent( focusedComponent, FocusEvent.FOCUS_GAINED ) );
        }
        else if ( focus == null )
        {
            focusedComponent = null;
        }
    }
    
    public void setVisible( boolean visible )
    {
        getWindowManager().setVisible( this, visible );
    }
    
    public void dispatchEvent( AWTEvent e )
    {
        //RepaintManager.setCurrentManager( repaintMgr );
        
        if ( e instanceof MouseEvent )
        {
            Component focus = dispatchMouseEvent( 0, getRootComponent(), (MouseEvent)e /*adjustMouse( parent, (MouseEvent)e )*/);
            
            if ( focus != null )
                focus.dispatchEvent( new MouseEvent( focus, MouseEvent.MOUSE_ENTERED, 0, MouseEvent.BUTTON1_MASK, focus.getLocation().x, focus.getLocation().y, 0, false ) );
            
            if ( focus != null && last == null )
                last = focus;
            
            else if ( last != null && last != focus )
            {
                last.dispatchEvent( new MouseEvent( last, MouseEvent.MOUSE_EXITED, 0, MouseEvent.BUTTON1_MASK, last.getLocation().x, last.getLocation().y, 0, false ) );
                
                last = focus;
            }
            
            if ( ( focus != focusedComponent ) && ( focus != null ) && ( (MouseEvent)e ).getID() == MouseEvent.MOUSE_PRESSED )
            {
                setComponentFocus( focus );
            }
        }
        else if ( e instanceof KeyEvent )
        {
            if ( focusedComponent != null )
            {
                focusedComponent.dispatchEvent( e );
            }
        }
        
        // get all the components which have been marked as dirty
        //RepaintManager.setCurrentManager( defaultMgr );
    }
    
    /*
    public void processWindowEvent( WindowEvent event )
    {
    }
    */
    
    public synchronized void renderToBuffer( ArrayList< ? > dirtyList )
    {
        if ( !packed )
        {
            pack();
            packed = true;
        }
        
        final Graphics g = buffer.getGraphics();
        
        if ( blendAlpha )
        {
            Graphics2D g2 = (Graphics2D)g;
            g2.setComposite( AlphaComposite.Src );
            g.setColor( new Color( 0, 0, 0, 0 ) );
            g.fillRect( 0, 0, width, height );
            g2.setComposite( AlphaComposite.SrcOver );
        }
        
        //Component c = getRootComponent();
        
        // print the component using the swing thread to stop deadlocks from
        // happening in some cases.
        
        try
        {
            SwingUtilities.invokeAndWait( new Runnable()
            {
                public void run()
                {
                    root.print( g );
                }
            } );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        catch ( InvocationTargetException e )
        {
            e.printStackTrace();
        }
        g.dispose();
        
        overlay.repaintChanged( buffer, dirtyList );
    }
    
    public void getSize( Dimension dim )
    {
        dim.setSize( width, height );
    }
    
    public void update()
    {
        if ( !repaintMgr.isDirty() )
            return;
        
        ArrayList< UIDirtyRegion > dirtyList = new ArrayList< UIDirtyRegion >();
        getDirtyAreas( new Point(), (JComponent)getRootComponent(), dirtyList );
        
        if ( dirtyList.size() > 0 )
        {
            //if (repaintMgr.isDirty()) {
            renderToBuffer( dirtyList );
            overlay.update();
            //repaintMgr.clear();
        }
    }
    
    public Node getRoot()
    {
        return ( overlay.getRoot() );
    }
    
    public boolean isOpaque()
    {
        return ( overlay.isOpaque() );
    }
    
    public UIDraggingInformation startDrag( MouseEvent me )
    {
        return ( startDrag( 0, getRootComponent(), me ) );
    }
    
    public boolean dragging( MouseEvent me, UIDraggingInformation info )
    {
        return ( dragging( 0, getRootComponent(), me, info ) );
    }
    
    public boolean dropped( MouseEvent me, UIDraggingInformation info )
    {
        RepaintManager.setCurrentManager( repaintMgr );
        boolean val = drop( 0, getRootComponent(), me, info );
        //RepaintManager.setCurrentManager( defaultMgr );
        
        return ( val );
    }
    
    public void setDisabled( boolean b )
    {
        this.disabled = b;
        
        overlay.getRoot().setPickable( !b );
    }
    
    public boolean isDisabled()
    {
        return ( disabled );
    }
    
    public void setDraggable( boolean b )
    {
        draggable = b;
    }
    
    public boolean isDraggable()
    {
        return ( draggable );
    }
    
    public int getX()
    {
        return ( getWindowManager().getX( this ) );
    }
    
    public int getY()
    {
        return ( getWindowManager().getY( this ) );
    }
    
    public void setPosition( int x, int y )
    {
        getWindowManager().setPosition( this, x, y );
    }
    
    public void setFocus( boolean hasFocus )
    {
        X3DLog.debug( "setting window focus to ", hasFocus );
        if ( !hasFocus && focusedComponent != null )
            setComponentFocus( null );
    }
    
    public UIWindow( int width, int height, boolean clipAlpha, boolean blendAlpha )
    {
        this.width = width;
        this.height = height;
        this.clipAlpha = clipAlpha;
        this.blendAlpha = blendAlpha;
        
        constructBuffer();
        
        /*
         * it is unfortunate that we need this at all, since all we care about
         * is the lightweight swing components... but the mishmash disaster that is called
         * AWT/Swing just can't handle not having a heavyweight parent
         */
        window = new Frame( GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration() );
        window.setEnabled( true );
        window.setSize( width, height );
        window.setUndecorated( true );
        window.addNotify();
        
        //defaultMgr = new RepaintManager();
        RepaintManager.setCurrentManager( repaintMgr );
        
        // now build an overlay which matches the size of the window.
        overlay = new UIOverlay( width, height, clipAlpha, blendAlpha, false );
    }
    
    public UIWindow( JComponent root, int width, int height, boolean clipAlpha, boolean blendAlpha )
    {
        this( width, height, clipAlpha, blendAlpha );
        
        setRoot( root );
    }
}
