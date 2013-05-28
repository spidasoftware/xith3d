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
package org.xith3d.render;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import org.xith3d.render.config.CanvasConstructionInfo;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;

/**
 * This Panel is used to integrate a Canvas3D easily into Swing or AWT.
 * All event listeners added to this Panel are added to the canvas, too.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Canvas3DJPanel extends JPanel implements Canvas3DWrapper
{
    private static final long serialVersionUID = 9086864231543663085L;
    
    private Canvas3D canvas = null;
    private boolean isInitialized = false;
    
    /**
     * @return the contained Canavas3D
     */
    public final Canvas3D getCanvas()
    {
        return ( canvas );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setOpaque( boolean isOpaque )
    {
        super.setOpaque( isOpaque );
        
        if ( ( canvas != null ) && ( canvas.getPeer() != null ) )
        {
            if ( canvas.getPeer().getComponent() instanceof JComponent )
            {
                ( (JComponent)canvas.getPeer().getComponent() ).setOpaque( isOpaque );
            }
        }
    }
    
    /**
     * No other Layout can be used!
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public void setLayout( LayoutManager mgr )
    {
        if ( isInitialized )
        {
        }
//            throw new UnsupportedOperationException( "You cannot change the LayoutManager of a Canvas3DPanel." );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocusable( boolean b )
    {
        super.setFocusable( b );
        ( (Component)canvas.getPeer().getComponent() ).setFocusable( b );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestFocus()
    {
        ( (Component)canvas.getPeer().getComponent() ).requestFocus();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addKeyListener( KeyListener l )
    {
        //super.addKeyListener( l );
        ( (Component)canvas.getPeer().getComponent() ).addKeyListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeKeyListener( KeyListener l )
    {
        //super.removeKeyListener( l );
        ( (Component)canvas.getPeer().getComponent() ).removeKeyListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addMouseListener( MouseListener l )
    {
        //super.addMouseListener( l );
        ( (Component)canvas.getPeer().getComponent() ).addMouseListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMouseListener( MouseListener l )
    {
        //super.removeMouseListener( l );
        ( (Component)canvas.getPeer().getComponent() ).removeMouseListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addMouseMotionListener( MouseMotionListener l )
    {
        //super.addMouseMotionListener( l );
        ( (Component)canvas.getPeer().getComponent() ).addMouseMotionListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMouseMotionListener( MouseMotionListener l )
    {
        //super.removeMouseMotionListener( l );
        ( (Component)canvas.getPeer().getComponent() ).removeMouseMotionListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addMouseWheelListener( MouseWheelListener l )
    {
        //super.addMouseWheelListener( l );
        ( (Component)canvas.getPeer().getComponent() ).addMouseWheelListener( l );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMouseWheelListener( MouseWheelListener l )
    {
        super.removeMouseWheelListener( l );
        ( (Component)canvas.getPeer().getComponent() ).removeMouseWheelListener( l );
    }
    
    /**
     * Sets this Canvas3D's RenderOptions
     */
    public void setRenderOptions( RenderOptions ro )
    {
        getCanvas().setRenderOptions( ro );
    }
    
    /**
     * @return this Canvas3D's RenderOptions
     */
    public final RenderOptions getRenderOptions()
    {
        return ( getCanvas().getRenderOptions() );
    }
    
    /**
     * Enables or disables wireframe mode
     * 
     * @param enable if true, wireframe mode will be enabled
     */
    public void setWireframeMode( boolean enable )
    {
        getCanvas().setWireframeMode( enable );
    }
    
    /**
     * @return if wireframe mode is enabled or disabled
     */
    public final boolean isWireframeMode()
    {
        return ( getCanvas().isWireframeMode() );
    }
    
    /**
     * Switches wireframe mode.
     * 
     * @return the new state.
     */
    public boolean switchWireframeMode()
    {
        return ( getCanvas().switchWireframeMode() );
    }
    
    /**
     * enables lighting on this Canvas3D
     */
    public void enableLighting()
    {
        getCanvas().enableLighting();
    }
    
    /**
     * disables lighting on this Canvas3D
     */
    public void disableLighting()
    {
        getCanvas().disableLighting();
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     * 
     * @param layer the OpenGLLayer to use (can only be an AWT or SWING one)
     * @param bpp the bits per pixel (color depth) for rendering
     * @param fsaa full scene anti aliasing mode
     * @param vsync
     */
    public Canvas3DJPanel( OpenGLLayer layer, int bpp, FSAA fsaa, boolean vsync )
    {
        super( true );
        
        if ( ( layer != OpenGLLayer.JOGL_AWT ) && ( layer != OpenGLLayer.JOGL_SWING ) && ( layer != OpenGLLayer.LWJGL_AWT ) )
            throw new IllegalArgumentException( "You can only use JOGL_AWT, JOGL_SWING or LWJGL_AWT as OpenGLLayer" );
        
        super.setLayout( new GridLayout() );
        super.setBackground( Color.BLACK );
        
        this.canvas = Canvas3DFactory.create( layer, 800, 600, bpp, DisplayMode.WINDOWED, vsync, fsaa, this );
        
        this.setFocusable( true );
        
        isInitialized = true;
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     * 
     * @param layer the OpenGLLayer to use (can only be an AWT or SWING one)
     * @param bpp the bits per pixel (color depth) for rendering
     * @param fsaa full scene anti aliasing mode
     */
    public Canvas3DJPanel( OpenGLLayer layer, int bpp, FSAA fsaa )
    {
        this( layer, bpp, fsaa, true );
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     * 
     * @param layer the OpenGLLayer to use (can only be an AWT or SWING one)
     * @param bpp the bits per pixel (color depth) for rendering
     */
    public Canvas3DJPanel( OpenGLLayer layer, int bpp )
    {
        this( layer, bpp, FSAA.OFF );
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     * 
     * @param layer the OpenGLLayer to use (can only be an AWT or SWING one)
     */
    public Canvas3DJPanel( OpenGLLayer layer )
    {
        this( layer, DisplayMode.getDefaultBPP() );
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     * 
     * @param bpp the bits per pixel (color depth) for rendering
     * @param fsaa full scene anti aliasing mode
     */
    public Canvas3DJPanel( int bpp, FSAA fsaa )
    {
        this( OpenGLLayer.JOGL_SWING, bpp, fsaa );
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     * 
     * @param fsaa full scene anti aliasing mode
     */
    public Canvas3DJPanel( FSAA fsaa )
    {
        this( DisplayMode.getDefaultBPP(), fsaa );
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     * 
     * @param bpp the bits per pixel (color depth) for rendering
     */
    public Canvas3DJPanel( int bpp )
    {
        this( bpp, FSAA.OFF );
    }
    
    /**
     * Creates a new javax.swing.JPanel containing a Canvas3D covering all of it at each time.
     */
    public Canvas3DJPanel()
    {
        this( DisplayMode.getDefaultBPP() );
    }
    
    /**
     * Creates a new java.awt.Panel containing a Canvas3D covering all of it at each time.
     * 
     * @param canvasInfo the CanvasConstructionInfo holding all necessary information to create the new Canvas3D
     */
    public Canvas3DJPanel( CanvasConstructionInfo canvasInfo )
    {
        this( canvasInfo.getOpenGLLayer(), canvasInfo.getDisplayMode().getBPP(), canvasInfo.getFSAAMode() );
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
    }
}
