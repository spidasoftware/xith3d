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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Insert package comments here
 * <p>
 * Originally Coded by David Yazel on Oct 5, 2003 at 11:45:49 AM.
 */
public class UIEventAdapter implements MouseListener, KeyListener, MouseMotionListener
{
    private UIWindowManager windowMgr;
    
    public void mouseClicked( MouseEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void mouseEntered( MouseEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void mouseExited( MouseEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void mousePressed( MouseEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void mouseReleased( MouseEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void keyPressed( KeyEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void keyReleased( KeyEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void keyTyped( KeyEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void mouseDragged( MouseEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public void mouseMoved( MouseEvent e )
    {
        windowMgr.processEvent( e );
    }
    
    public UIEventAdapter( UIWindowManager windowMgr )
    {
        this.windowMgr = windowMgr;
    }
}
