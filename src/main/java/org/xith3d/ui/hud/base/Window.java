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

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.AbstractButton.ButtonState;
import org.xith3d.ui.hud.borders.TexturedBorder;
import org.xith3d.ui.hud.listeners.ButtonListener;
import org.xith3d.ui.hud.listeners.WidgetMouseAdapter;
import org.xith3d.ui.hud.listeners.WindowListener;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;

/**
 * This class represents a simple Window above the HUD.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Window extends Widget
{
    /**
     * This enum can be used for the setCloseOperation() method of a Window.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static enum CloseOperation
    {
        /**
         * The Window is detached from the HUD, when the close Button is
         * clicked.<br>
         * <br>
         * This is the default.
         */
        DETACH,
        ;
    }
    
    private WindowHeaderWidget headerWidget;
    private boolean redrawHeaderWidgetOnly = false;
    private CloseOperation closeOperation = CloseOperation.DETACH;
    private WidgetContainer contentPane;
    private boolean keepContentPaneSize;
    private boolean paneDraggingEnabled = false;
    private Point2f dragStart = null;
    private final Point2f tmpDragStart = new Point2f();
    private Point2f dragStartWindow = null;
    private final Point2f tmpDragStartWindow = new Point2f();
    
    private final ArrayList<WindowListener> listeners = new ArrayList<WindowListener>();
    
    /**
     * @return the title bar's Widget
     */
    protected final WindowHeaderWidget getHeaderWidget()
    {
        return ( headerWidget );
    }
    
    /**
     * @return true, if this Window is decorated (has a title bar).
     */
    public final boolean isDecorated()
    {
        return ( headerWidget != null );
    }
    
    /**
     * Gets the height of the title-bar.
     * 
     * @return the header's height.
     */
    public float getHeaderHeight()
    {
        WindowHeaderWidget headerWidget = getHeaderWidget();
        
        if ( headerWidget == null )
            return ( 0f );
        
        return ( headerWidget.getHeight() );
    }
    
    /**
     * Sets the Window's title.
     * 
     * @param title
     */
    public void setTitle( String title )
    {
        if ( !isDecorated() )
            throw new Error( "This Window isn't decorated." );
        
        if ( title == null )
            throw new IllegalArgumentException( "You cannot set the window title to null." );
        
        headerWidget.setText( title );
        contentPane.setName( "ContentPane of Window: \"" + title + "\"" );
    }
    
    /**
     * Gets the Window's title.
     * 
     * @return the Window's title.
     */
    public final String getTitle()
    {
        if ( !isDecorated() )
            return ( null );
        
        return ( headerWidget.getText() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final WidgetContainer getContentPane()
    {
        return ( contentPane );
    }
    
    /**
     * @return the ContentPane's width.
     */
    @Override
    protected final int getContentWidthPX()
    {
        return ( getContentPane().getContentWidthPX() );
    }
    
    /**
     * @return the ContentPane's height.
     */
    @Override
    protected final int getContentHeightPX()
    {
        return ( getContentPane().getContentHeightPX() );
    }
    
    /**
     * Sets the size of the window, so that the content-pane is the given size.
     * 
     * @param contentWidth
     * @param contentHeight
     */
    public void setContentSize( float contentWidth, float contentHeight )
    {
        float headerHeight = getHeaderHeight();
        float borderWidth = 0f;
        float borderHeight = 0f;
        
        if ( getBorder() != null )
        {
            Dim2f buffer = Dim2f.fromPool();
            getSizePixels2HUD_( getBorder().getLeftWidth() + getBorder().getRightWidth(), getBorder().getTopHeight() + getBorder().getBottomHeight() + 1, buffer );
            
            borderWidth = buffer.getWidth();
            borderHeight = buffer.getHeight();
            
            Dim2f.toPool( buffer );
        }
        
        setSize( contentWidth + borderWidth, contentHeight + headerHeight + borderHeight );
    }
    
    /**
     * @return the ContentPane's x-resolution.
     */
    public final float getResX()
    {
        return ( getContentPane().getResX() );
    }
    
    /**
     * @return the ContentPane's y-resolution.
     */
    public final float getResY()
    {
        return ( getContentPane().getResY() );
    }
    
    /**
     * Sets the default (if not overridden) CloseOperation to be executed
     * when the close Button was clicked.
     * 
     * @param op the default close operation or <i>null</i> for no operation
     */
    public void setDefaultCloseOperation( CloseOperation op )
    {
        this.closeOperation = op;
    }
    
    /**
     * @return the default (if not overridden) CloseOperation to be executed
     * when the close Button was clicked.
     */
    public final CloseOperation getDefaultCloseOperation()
    {
        return ( this.closeOperation );
    }
    
    /**
     * Sets the close button visible of hidden.
     * 
     * @param visible
     * 
     * @throws UnsupportedOperationException if this is an undecorated Window
     */
    public void setCloseButtonVisible( boolean visible )
    {
        if ( headerWidget == null )
            throw new UnsupportedOperationException( "This method is not supported for undecorated Windows." );
        
        headerWidget.getCloseButton().setVisible( visible );
    }
    
    /**
     * @return if the close button is visible of hidden.
     * 
     * @throws UnsupportedOperationException if this is an undecorated Window
     */
    public boolean isCloseButtonVisible()
    {
        if ( headerWidget == null )
            throw new UnsupportedOperationException( "This method is not supported for undecorated Windows." );
        
        return ( headerWidget.getCloseButton().isVisible() );
    }
    
    /**
     * Sets pane-dragging enabled or disabled.
     * If pane-dragging is enabled, you can hold the mouse button on the content pane and drag the Frame.
     */
    public void setPaneDraggingEnabled( boolean enabled )
    {
        this.paneDraggingEnabled = enabled;
    }
    
    /**
     * @return true, if pane-dragging is enabled.
     * If pane-dragging is enabled, you can hold the mouse button on the content pane and drag the Frame.
     */
    public final boolean isPaneDraggingEnabled()
    {
        return ( paneDraggingEnabled );
    }
    
    /**
     * @return true, if pane-dragging is enabled.
     * If pane-dragging is enabled, you can hold the mouse button on the content pane and drag the Frame.
     */
    protected boolean checkDragStartCondition( float postionOnWindowX, float postionOnWindowY )
    {
        if ( paneDraggingEnabled )
            return ( true );
        
        if ( headerWidget == null )
            return ( false );
        
        return ( headerWidget.isMouseOverBar( postionOnWindowX, postionOnWindowY ) );
    }
    
    @Override
    protected void startDragging( int canvasX, int canvasY, float widgetX, float widgetY  )
    {
        if ( checkDragStartCondition( widgetX, widgetY ) )
        {
            dragStart = tmpDragStart;
            
            Tuple2f tmp = Tuple2f.fromPool();
            getAbsoluteLocationOnHUD_( tmp );
            dragStart.set( tmp.getX() + widgetX, tmp.getY() + widgetY );
            Tuple2f.toPool( tmp );
            
            dragStartWindow = tmpDragStartWindow;
            dragStartWindow.set( getLeft(), getTop() );
            
            bindToGlobalMouseMovement();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonPressed( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        if ( isDraggable() && isTopMost )
        {
            getContentPane().onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonReleased( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        super.onMouseButtonReleased( button, x, y, when, lastWhen, isTopMost, hasFocus );
        
        dragStart = null;
        dragStartWindow = null;
        
        if ( isTopMost )
        {
            getContentPane().onMouseButtonReleased( button, x, y, when, lastWhen, isTopMost, hasFocus );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseMoved( float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
        if ( dragStart != null )
        {
            Tuple2f tmp = Tuple2f.fromPool();
            getAbsoluteLocationOnHUD_( tmp );
            float absX = tmp.getX() + x;
            float absY = tmp.getY() + y;
            Tuple2f.toPool( tmp );
            
            float windowPosX = dragStartWindow.getX() + ( absX - dragStart.getX() );
            float windowPosY = dragStartWindow.getY() + ( absY - dragStart.getY() );
            
            float titleHeight = isDecorated() ? headerWidget.getHeight() : 0f;
            
            // it must not be possible to drag the Window out of the viewport
            windowPosX = Math.max( windowPosX, 0f - this.getWidth() + titleHeight );
            windowPosX = Math.min( windowPosX, getHUD().getResX() - titleHeight );
            windowPosY = Math.max( windowPosY, 0f );
            windowPosY = Math.min( windowPosY, getHUD().getResY() - titleHeight );
            
            this.setLocation( windowPosX, windowPosY, false, false );
        }
        
        if ( isTopMost )
        {
            getContentPane().onMouseMoved( x, y, buttonsState, when, isTopMost, hasFocus );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseWheelMoved( int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost )
    {
        super.onMouseWheelMoved( delta, isPageMove, x, y, when, isTopMost );
        
        if ( isTopMost )
        {
            getContentPane().onMouseWheelMoved( delta, isPageMove, x, y, when, isTopMost );
            
            /*
            if (delta > 0)
                getContentPane().pick( -x, -y, HUDPickReason.MOUSE_WHEEL_MOVED_UP );
            else if (delta < 0)
                getContentPane().pick( -x, -y, HUDPickReason.MOUSE_WHEEL_MOVED_DOWN );
            */
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusLost()
    {
        getContentPane().onFocusLost();
    }
    
    private static void forwardOnMouseExited( Widget widget, boolean isTopMost, boolean hasFocus )
    {
        if ( widget == null )
            return;
        
        if ( widget instanceof WidgetContainer )
        {
            forwardOnMouseExited( ( (WidgetContainer)widget ).getCurrentHoveredWidget(), isTopMost, hasFocus );
            widget.onMouseExited( isTopMost, false );
            ( (WidgetContainer)widget ).resetCurrentHoveredWidget();
        }
        else
        {
            widget.onMouseExited( isTopMost, false );
        }
    }
    
    private void forwardOnMouseExited( boolean isTopMost, boolean hasFocus )
    {
        forwardOnMouseExited( getContentPane().getCurrentHoveredWidget(), isTopMost, hasFocus );
        getContentPane().resetCurrentHoveredWidget();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        super.onMouseExited( isTopMost, hasFocus );
        
        if ( isDecorated() && ( getHeaderWidget().getCloseButton() != null ) )
        {
            getHeaderWidget().getCloseButton().onMouseExited( isTopMost, hasFocus );
        }
        
        forwardOnMouseExited( isTopMost, hasFocus );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyPressed( Key key, int modifierMask, long when )
    {
        getContentPane().onKeyPressed( key, modifierMask, when );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyReleased( Key key, int modifierMask, long when )
    {
        getContentPane().onKeyReleased( key, modifierMask, when );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onKeyTyped( char ch, int modifierMask, long when )
    {
        getContentPane().onKeyTyped( ch, modifierMask, when );
    }
    
    /**
     * Adds a new WindowListener.
     * 
     * @param l the new WindowListener
     */
    public void addWindowListener( WindowListener l )
    {
        listeners.add( l );
    }
    
    /**
     * Removes a WindowListener.
     * 
     * @param l the WindowListener to remove
     */
    public void removeWindowListener( WindowListener l )
    {
        listeners.remove( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void detach()
    {
        if ( getHUD() != null )
        {
            getHUD().removeWindow( this );
        }
    }
    
    /**
     * This event is fired, if the close button on the Header-Widget was
     * clicked.<br>
     * It simply calls detach() by default. Override it to change this
     * behaviour.
     * 
     * @see #detach()
     */
    protected void onCloseButtonClicked()
    {
        if ( getDefaultCloseOperation() == null )
            return;
        
        switch ( getDefaultCloseOperation() )
        {
            case DETACH:
                getHeaderWidget().getCloseButton().setButtonState( ButtonState.NORMAL );
                detach();
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onVisibilityChanged( boolean visible )
    {
        super.onVisibilityChanged( visible );
        
        for ( int i = 0; i < listeners.size(); i++ )
        {
            if ( visible )
                listeners.get( i ).onWindowShown( this );
            else
                listeners.get( i ).onWindowHidden( this );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        float headerHeight = 0f;
        float innerLeft = 0f;
        float innerTop = 0f;
        float innerWidth = newWidth;
        float innerHeight = newHeight;
        
        if ( isDecorated() && ( getHUD() != null ) )
        {
            WindowHeaderWidget headerWidget = getHeaderWidget();
            headerHeight = headerWidget.getHeight();
            
            headerWidget.setSize( newWidth, headerHeight );
            
            innerTop += headerHeight;
            innerHeight -= headerHeight;
            
            if ( getBorder() != null )
            {
                Dim2f buffer = Dim2f.fromPool();
                
                getSizePixels2HUD_( getBorder().getLeftWidth(), getBorder().getTopHeight(), buffer );
                innerLeft += buffer.getWidth();
                innerTop += buffer.getHeight();
                innerWidth -= buffer.getWidth();
                innerHeight -= buffer.getHeight();
                
                getSizePixels2HUD_( getBorder().getRightWidth(), getBorder().getBottomHeight(), buffer );
                innerWidth -= buffer.getWidth();
                innerHeight -= buffer.getHeight();
                
                Dim2f.toPool( buffer );
            }
        }
        
        if ( getWidgetAssembler().contains( contentPane ) )
        {
            contentPane.setSize( innerWidth, innerHeight );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected HUDPickResult pick( int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        HUDPickResult result = super.pick( canvasX, canvasY, pickReason, button, when, meta, flags );
        
        if ( result == null )
            return ( null );
        
        final Widget contentPane = getContentPane();
        
        final boolean isCPPickable = contentPane.isPickable();
        contentPane.setPickable( true );
        
        result.setSubResult( contentPane.pick( canvasX, canvasY, pickReason, button, when, meta, flags ) );
        
        contentPane.setPickable( isCPPickable );
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromHUD( HUD hud )
    {
        getContentPane().onDetachedFromHUD( hud );
        
        super.onDetachedFromHUD( hud );
        
        for ( int i = 0; i < listeners.size(); i++ )
            listeners.get( i ).onWindowClosed( this );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void setHUD( HUD hud )
    {
        super.setHUD( hud );
        
        getContentPane().setHUD( hud );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( isDecorated() )
            return ( this.getClass().getSimpleName() + "(\"" + headerWidget.getText() + "\")" );
        
        return ( super.toString() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setHostTextureDirty( int flags )
    {
        if ( !isAHostedWidgetDirty() )
            redrawHeaderWidgetOnly = ( ( flags & WindowHeaderWidget.FLAG_ONLY_WINDOW_HEADER_WIDGET ) != 0 );
        
        super.setHostTextureDirty( flags & ~WindowHeaderWidget.FLAG_ONLY_WINDOW_HEADER_WIDGET );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setTextureDirty( int flags )
    {
        if ( !isThisWidgetDirty() )
            redrawHeaderWidgetOnly = ( ( flags & WindowHeaderWidget.FLAG_ONLY_WINDOW_HEADER_WIDGET ) != 0 );
        
        super.setTextureDirty( flags & ~WindowHeaderWidget.FLAG_ONLY_WINDOW_HEADER_WIDGET );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawBorder( Border border, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        if ( isDecorated() )
        {
            Dim2i d = Dim2i.fromPool();
            getSizeHUD2Pixels_( 0f, getHeaderHeight(), d );
            int headerHeight = d.getHeight();
            Dim2i.toPool( d );
            
            super.drawBorder( border, texCanvas, offsetX, offsetY + headerHeight, width, height - headerHeight );
        }
        else
        {
            super.drawBorder( border, texCanvas, offsetX, offsetY, width, height );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void drawAndUpdateWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        if ( redrawHeaderWidgetOnly )
        {
            Dim2i tmp = Dim2i.fromPool();
            getSizeHUD2Pixels_( 0, getHeaderHeight(), tmp );
            height = tmp.getHeight();
            Dim2i.toPool( tmp );
            
            texCanvas.beginUpdateRegion( offsetX, offsetY, width, height );
            
            headerWidget.drawAndUpdateWidget( texCanvas, offsetX, offsetY, width, height, false );
            
            texCanvas.finishUpdateRegion();
            
            texCanvas.finishUpdateRegion();
            
            redrawHeaderWidgetOnly = false;
            resetWidgetDirty();
        }
        else
        {
            super.drawAndUpdateWidget( texCanvas, offsetX, offsetY, width, height, drawsSelf );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        float innerLeft = 0f;
        float innerTop = 0f;
        
        if ( isDecorated() )
        {
            float innerWidth = getWidth();
            float innerHeight = getHeight();
            float outerWidth = getWidth();
            float outerHeight = getHeight();
            
            WindowHeaderWidget headerWidget = getHeaderWidget();
            getWidgetAssembler().addWidget( headerWidget );
            
            innerTop += headerWidget.getHeight();
            innerHeight -= headerWidget.getHeight();
            outerHeight += headerWidget.getHeight();
            
            if ( getBorder() != null )
            {
                Dim2f buffer = Dim2f.fromPool();
                
                getSizePixels2HUD_( getBorder().getLeftWidth(), getBorder().getTopHeight(), buffer );
                innerLeft += buffer.getWidth();
                innerTop += buffer.getHeight();
                innerWidth -= buffer.getWidth();
                innerHeight -= buffer.getHeight();
                outerWidth += buffer.getWidth();
                outerHeight += buffer.getHeight();
                
                getSizePixels2HUD_( getBorder().getRightWidth(), getBorder().getBottomHeight(), buffer );
                innerWidth -= buffer.getWidth();
                innerHeight -= buffer.getHeight();
                outerWidth += buffer.getWidth();
                outerHeight += buffer.getHeight();
                
                Dim2f.toPool( buffer );
            }
            
            if ( keepContentPaneSize )
                this.setSize( outerWidth, outerHeight );
            else
                contentPane.setSize( innerWidth, innerHeight );
        }
        
        getWidgetAssembler().addWidget( contentPane, innerLeft, innerTop );
        
        contentPane.setPickable( false );
    }
    
    /**
     * Creates the header Widget for this decorated Window.
     * It is only called, if the Window is decorated.
     * It MUST NOT return null. It MUST NOT add it to the WidgetAssembler.
     * 
     * @param headerDesc the description for the header Widget
     * @param title the Window's title
     * 
     * @return the create WindowHeaderWidget instance
     */
    protected abstract WindowHeaderWidget createHeaderWidget( WindowHeaderWidget.Description headerDesc, String title );
    
    protected abstract WidgetContainer createContentPane( float width, float height );
    
    /**
     * Creates a new Window.
     * 
     * @param width the width of the Window
     * @param height the height of the Window
     * @param keepContentPaneSize
     * @param contentPane the Widget that visually defines the Window.
     * @param headerDesc the description of this Window's header (or <i>null</i> for an undecorated Window)
     * @param title this Window's title
     * @param headerWidget a Widget, that defines this Window's header
     */
    private Window( float width, float height, boolean keepContentPaneSize, WidgetContainer contentPane, WindowHeaderWidget.Description headerDesc, String title, WindowHeaderWidget headerWidget )
    {
        super( true, true, width, height );
        
        if ( headerWidget == null )
        {
            if ( title == null )
            {
                this.headerWidget = null;
            }
            else
            {
                this.headerWidget = createHeaderWidget( headerDesc, title );
                if ( this.headerWidget == null )
                    throw new Error( "Window.createHeaderWidget() MUST NOT return null." );
                if ( getWidgetAssembler().contains( this.headerWidget ) )
                    throw new Error( "Window.createHeaderWidget() MUST NOT put the header Widget to the WidgetAssembler." );
            }
        }
        else
        {
            this.headerWidget = headerWidget;
        }
        
        if ( isDecorated() )
        {
            if ( this.headerWidget.getCloseButton() != null )
            {
                this.headerWidget.getCloseButton().addButtonListener( new ButtonListener()
                {
                    public void onButtonClicked( AbstractButton button, Object userObject )
                    {
                        onCloseButtonClicked();
                    }
                } );
            }
            
            this.headerWidget.addMouseListener( new WidgetMouseAdapter()
            {
                @Override
                public void onMouseEntered( Widget widget, boolean isTopMost, boolean hasFocus )
                {
                    Window.this.forwardOnMouseExited( isTopMost, hasFocus );
                }
            } );
            
            this.setBorder( new TexturedBorder( HUD.getTheme().getFrameBorderDescription() ) );
        }
        
        this.keepContentPaneSize = keepContentPaneSize;
        
        if ( contentPane == null )
        {
            this.contentPane = createContentPane( width, height );
        }
        else
        {
            this.contentPane = contentPane;
        }
        
        if ( isDecorated() )
            this.contentPane.setName( "ContentPane of Window: \"" + getTitle() + "\"" );
        else
            this.contentPane.setName( "ContentPane of an undecorated Window" );
        
        this.contentPane.setContentPaneOf( this );
        
        this.setHasDropShadow( true );
        this.setDraggable( true );
        
        getWidgetAssembler().setPickDispatched( true );
    }
    
    /**
     * Creates a new Window.
     * 
     * @param width the width of the Window
     * @param height the height of the Window
     * @param keepContentPaneSize
     * @param contentPane the Widget that visually defines the Window.
     * @param headerDesc the description of this Window's header (or <i>null</i> for an undecorated Window)
     * @param title this Window's title
     */
    protected Window( float width, float height, boolean keepContentPaneSize, WidgetContainer contentPane, WindowHeaderWidget.Description headerDesc, String title )
    {
        this( width, height, keepContentPaneSize, contentPane, headerDesc, title, null );
    }
    
    /**
     * Creates a new Window.
     * 
     * @param width the width of the Window
     * @param height the height of the Window
     * @param keepContentPaneSize
     * @param contentPane the Widget that visually defines the Window.
     * @param headerWidget a Widget, that defines this Window's header (or <i>null</i> for an undecorated Window)
     */
    protected Window( float width, float height, boolean keepContentPaneSize, WidgetContainer contentPane, WindowHeaderWidget headerWidget )
    {
        this( width, height, keepContentPaneSize, contentPane, null, null, headerWidget );
    }
    
    /**
     * Creates a new Window.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param headerDesc the description of this Window's header (or <i>null</i> for an undecorated Window)
     * @param title this Window's title
     */
    public Window( WidgetContainer contentPane, WindowHeaderWidget.Description headerDesc, String title )
    {
        this( contentPane.getWidth(), contentPane.getHeight(), true, contentPane, headerDesc, title, null );
    }
    
    /**
     * Creates a new Window.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param headerWidget a Widget, that defines this Window's header (or <i>null</i> for an undecorated Window)
     */
    public Window( WidgetContainer contentPane, WindowHeaderWidget headerWidget )
    {
        this( contentPane.getWidth(), contentPane.getHeight(), true, contentPane, null, null, headerWidget );
    }
    
    /**
     * Creates a new Window.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param title this Window's header (or <i>null</i> for an undecorated Window)
     */
    public Window( WidgetContainer contentPane, String title )
    {
        this( contentPane, ( title != null ? HUD.getTheme().getWindowHeaderDescription() : null ), title );
    }
    
    /**
     * Creates a new undecorated Window.
     * 
     * @param contentPane the Widget that visually defines the Window.
     */
    public Window( WidgetContainer contentPane )
    {
        this( contentPane, (String)null );
    }
}
