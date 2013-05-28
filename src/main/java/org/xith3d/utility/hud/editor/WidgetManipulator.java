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
package org.xith3d.utility.hud.editor;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;

import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.listeners.WidgetEventsReceiverAdapter;
import org.xith3d.ui.hud.widgets.Image;

/**
 * Wraps a Widget and adds handles to resize the Widget. It also makes the
 * Widget draggable.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class WidgetManipulator
{
    private Image nwHandle, neHandle, swHandle, seHandle;
    private final int HANDLE_X_SIZE = 10;
    private final int HANDLE_Y_SIZE = 10;
    protected Widget widget;
    private Tuple2f old_nwHandle = null;
    private Tuple2f old_neHandle = null;
    private Tuple2f old_swHandle = null;
    private Tuple2f old_seHandle = null;
    
    private boolean isEventResizing = false;
    private boolean isManipulatorReady = false;
    private StackTraceElement stackTraceElement;
    private WERA wera;
    
    private class WERA extends WidgetEventsReceiverAdapter
    {
        @Override
        public void onWidgetLocationChanged( Widget widget, float oldLeft, float oldTop, float newLeft, float newTop )
        {
            if ( ( !isEventResizing ) && ( widget.getHUD() != null ) )
            {
                isEventResizing = true; // avoid deadlock or stack-overflow
                
                if ( widget == WidgetManipulator.this.widget )
                {
                    repositionHandles();
                }
                else if ( widget == nwHandle )
                {
                    resizeWidget();
                }
                else if ( widget == neHandle )
                {
                    resizeWidget();
                }
                else if ( widget == swHandle )
                {
                    resizeWidget();
                }
                else if ( widget == seHandle )
                {
                    resizeWidget();
                }
                
                isEventResizing = false; // avoid deadlock or stack-overflow
            }
        }
        
        @Override
        public void onWidgetAttachedToContainer( Widget widget, WidgetContainer container )
        {
            if ( widget == WidgetManipulator.this.widget )
            {
                widget.getContainer().addWidget( nwHandle );
                widget.getContainer().addWidget( neHandle );
                widget.getContainer().addWidget( swHandle );
                widget.getContainer().addWidget( seHandle );
            }
        }
        
        @Override
        public void onWidgetAttachedToHUD( Widget widget, HUD hud )
        {
            if ( widget == WidgetManipulator.this.widget )
            {
                // hud.getOperationScheduler().scheduleOperation( WidgetManipulator.this );
            }
        }
        
        @Override
        public void onWidgetDetachedFromContainer( Widget widget, WidgetContainer container )
        {
            // System.out.println( "onWidgetDetachedFromContainer" );
        }
        
        @Override
        public void onWidgetDetachedFromHUD( Widget widget, HUD hud )
        {
            // System.out.println( "onWidgetDetachedFromHUD" );
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void onMouseButtonPressed( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
        {
            if ( widget == WidgetManipulator.this.widget && button == MouseButtons.MIDDLE_BUTTON )
            {
                StringBuffer buff = new StringBuffer();
                String name = WidgetManipulator.this.widget.getClass().getSimpleName().toLowerCase() + ".";
                buff.append( name );
                buff.append( "setLocation(" );
                buff.append( widget.getLeft() );
                buff.append( "f," );
                buff.append( widget.getTop() );
                buff.append( "f);\n" );
                buff.append( name );
                buff.append( "setSize(" );
                buff.append( widget.getWidth() );
                buff.append( "f," );
                buff.append( widget.getHeight() );
                buff.append( "f);" );
                
                StringSelection stringSelection = new StringSelection( buff.toString() );
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents( stringSelection, null );
                
                System.err.println( "Clicked : " + stackTraceElement );
            }
        }
    }
    
    public WidgetManipulator( final Widget widget )
    {
        stackTraceElement = Thread.currentThread().getStackTrace()[ 2 ];
        
        wera = new WERA();
        
        this.widget = widget;
        
        widget.setDraggable( true );
        widget.addLocationListener( wera );
        
        Texture2D tex = new Texture2D( TextureFormat.RGB );
        tex.setImage( 0, new TextureImage2D( TextureImageFormat.LUMINANCE, 8, 8, new byte[]
        {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0,
            0, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0,
            -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, -1,
            -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1,
            -1
        } ) );
        
        final int handleZIndex = widget.getZIndex() + 1;
        nwHandle = new Image( HANDLE_X_SIZE, HANDLE_Y_SIZE, tex );
        nwHandle.setZIndex( handleZIndex );
        nwHandle.setDraggable( true );
        nwHandle.addLocationListener( wera );
        if ( widget.getContainer() != null )
        {
            widget.getContainer().addWidget( nwHandle );
        }
        
        neHandle = new Image( HANDLE_X_SIZE, HANDLE_Y_SIZE, tex );
        neHandle.setZIndex( handleZIndex );
        neHandle.setDraggable( true );
        neHandle.addLocationListener( wera );
        if ( widget.getContainer() != null )
        {
            widget.getContainer().addWidget( neHandle );
        }
        
        swHandle = new Image( HANDLE_X_SIZE, HANDLE_Y_SIZE, tex );
        swHandle.setZIndex( handleZIndex );
        swHandle.setDraggable( true );
        swHandle.addLocationListener( wera );
        if ( widget.getContainer() != null )
        {
            widget.getContainer().addWidget( swHandle );
        }
        
        seHandle = new Image( HANDLE_X_SIZE, HANDLE_Y_SIZE, tex );
        seHandle.setZIndex( handleZIndex );
        seHandle.setDraggable( true );
        seHandle.addLocationListener( wera );
        if ( widget.getContainer() != null )
        {
            widget.getContainer().addWidget( seHandle );
        }
        
        repositionHandles();
        
        if ( widget.getContainer() == null )
        {
            widget.addContainerListener( wera );
        }
        
        widget.addMouseListener( wera );
        
        this.isManipulatorReady = true;
    }
    
    private void repositionHandles()
    {
        if ( old_nwHandle != null )
            old_nwHandle.set( nwHandle.getLeft(), nwHandle.getTop() );
        nwHandle.setLocation( widget.getLeft() - HANDLE_X_SIZE / 2f, widget.getTop() - HANDLE_Y_SIZE / 2f );
        if ( old_nwHandle == null )
            old_nwHandle = new Point2f( nwHandle.getLeft(), nwHandle.getTop() );
        
        if ( old_neHandle != null )
            old_neHandle.set( neHandle.getLeft(), neHandle.getTop() );
        neHandle.setLocation( widget.getLeft() + widget.getWidth() - HANDLE_X_SIZE / 2f, widget.getTop() - HANDLE_Y_SIZE / 2f );
        if ( old_neHandle == null )
            old_neHandle = new Point2f( neHandle.getLeft(), neHandle.getTop() );
        
        if ( old_swHandle != null )
            old_swHandle.set( swHandle.getLeft(), swHandle.getTop() );
        swHandle.setLocation( widget.getLeft() - HANDLE_X_SIZE / 2f, widget.getTop() + widget.getHeight() - HANDLE_Y_SIZE / 2f );
        if ( old_swHandle == null )
            old_swHandle = new Point2f( swHandle.getLeft(), swHandle.getTop() );
        
        if ( old_seHandle != null )
            old_seHandle.set( seHandle.getLeft(), seHandle.getTop() );
        seHandle.setLocation( widget.getLeft() + widget.getWidth() - HANDLE_X_SIZE / 2f, widget.getTop() + widget.getHeight() - HANDLE_Y_SIZE / 2f );
        if ( old_seHandle == null )
            old_seHandle = new Point2f( seHandle.getLeft(), seHandle.getTop() );
    }
    
    private void resizeWidget()
    {
        if ( isManipulatorReady )
        {
            boolean anyChange = false;
            
            if ( ( old_nwHandle.getX() != nwHandle.getLeft() ) || ( old_nwHandle.getY() != nwHandle.getTop() ) )
            {
                widget.setLocation( widget.getLeft() + nwHandle.getLeft() - old_nwHandle.getX(), widget.getTop() + nwHandle.getTop() - old_nwHandle.getY() );
                widget.setSize( widget.getWidth() - nwHandle.getLeft() + old_nwHandle.getX(), widget.getHeight() - nwHandle.getTop() + old_nwHandle.getY() );
                old_nwHandle.set( nwHandle.getLeft(), nwHandle.getTop() );
                
                anyChange = true;
            }
            else if ( ( old_neHandle.getX() != neHandle.getLeft() ) || ( old_neHandle.getY() != neHandle.getTop() ) )
            {
                widget.setLocation( widget.getLeft(), widget.getTop() + neHandle.getTop() - old_neHandle.getY() );
                widget.setSize( widget.getWidth() + neHandle.getLeft() - old_neHandle.getX(), widget.getHeight() - neHandle.getTop() + old_neHandle.getY() );
                old_neHandle.set( neHandle.getLeft(), neHandle.getTop() );
                
                anyChange = true;
            }
            else if ( ( old_swHandle.getX() != swHandle.getLeft() ) || ( old_swHandle.getY() != swHandle.getTop() ) )
            {
                widget.setLocation( widget.getLeft() + swHandle.getLeft() - old_swHandle.getX(), widget.getTop() );
                widget.setSize( widget.getWidth() - swHandle.getLeft() + old_swHandle.getX(), widget.getHeight() + swHandle.getTop() - old_swHandle.getY() );
                old_swHandle.set( swHandle.getLeft(), swHandle.getTop() );
                
                anyChange = true;
            }
            else if ( ( old_seHandle.getX() != seHandle.getLeft() ) || ( old_seHandle.getY() != seHandle.getTop() ) )
            {
                widget.setSize( widget.getWidth() + seHandle.getLeft() - old_seHandle.getX(), widget.getHeight() + seHandle.getTop() - old_seHandle.getY() );
                old_seHandle.set( seHandle.getLeft(), seHandle.getTop() );
                
                anyChange = true;
            }
            
            if ( anyChange )
            {
                repositionHandles();
                
                old_nwHandle.set( nwHandle.getLeft(), nwHandle.getTop() );
                old_neHandle.set( neHandle.getLeft(), neHandle.getTop() );
                old_swHandle.set( swHandle.getLeft(), swHandle.getTop() );
                old_seHandle.set( seHandle.getLeft(), seHandle.getTop() );
            }
        }
    }
}
