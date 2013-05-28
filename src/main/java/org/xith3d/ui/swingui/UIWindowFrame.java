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

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This is a debugging frame used to test the UIWindow and its ability to
 * render components into a buffered image and its ability to process
 * key and mouse events.
 * <p>
 * Originally Coded by David Yazel on Sep 30, 2003 at 9:43:08 PM.
 * 
 * @author David Yazel
 */
public class UIWindowFrame extends JFrame implements MouseListener, KeyListener
{
    private class RenderPanel extends JPanel
    {
        private static final long serialVersionUID = -2499279490771568627L;
        
        @Override
        public void paint( Graphics g )
        {
            //window.renderToBuffer();
            g.drawImage( window.getBuffer(), 0, 0, window.getWidth(), window.getHeight(), 0, 0, window.getWidth(), window.getHeight(), null );
        }
    }
    
    private static final long serialVersionUID = 795759606248944799L;
    
    private UIWindow window;
    private RenderPanel panel;
    
    public void mouseClicked( MouseEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public void mouseEntered( MouseEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public void mouseExited( MouseEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public void mousePressed( MouseEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public void mouseReleased( MouseEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public void keyPressed( KeyEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public void keyReleased( KeyEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public void keyTyped( KeyEvent e )
    {
        window.dispatchEvent( e );
        panel.repaint();
    }
    
    public UIWindowFrame( UIWindow window ) throws HeadlessException
    {
        super();
        
        this.window = window;
        this.panel = new RenderPanel();
        //window.setParent( panel );
        this.getContentPane().add( panel );
        setSize( window.getWidth(), window.getHeight() );
        panel.addMouseListener( this );
        panel.addKeyListener( this );
        panel.setFocusable( true );
    }
    
    public static void testWindow( UIWindow window )
    {
        UIWindowFrame frame = new UIWindowFrame( window );
        frame.setVisible( true );
        try
        {
            Thread.sleep( 600000L );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }
}
