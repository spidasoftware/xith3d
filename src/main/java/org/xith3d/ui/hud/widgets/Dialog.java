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
package org.xith3d.ui.hud.widgets;

import java.util.ArrayList;

import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.base.WindowHeaderWidget;
import org.xith3d.ui.hud.listeners.DialogListener;

public class Dialog extends Frame
{
    private Object closeCommand = null;
    
    private final ArrayList<DialogListener> listeners = new ArrayList<DialogListener>();
    
    public void setCloseCommand( Object closeCommand )
    {
        this.closeCommand = closeCommand;
    }
    
    public Object getCloseCommand()
    {
        return ( closeCommand );
    }
    
    /**
     * Adds a new DialogListener
     * 
     * @param l the new DialogListener
     */
    public void addDialogListener( DialogListener l )
    {
        listeners.add( l );
    }
    
    /**
     * Removes a DialogListener
     * 
     * @param l the DialogListener to remove
     */
    public void removeDialogListener( DialogListener l )
    {
        listeners.remove( l );
    }
    
    /**
     * Removes all registered {@link DialogListener}s from this {@link Dialog}.
     */
    public void removeAllDialogListeners()
    {
        listeners.clear();
    }
    
    /**
     * Use this method to notify all registered DialogListeners with a command String.
     * 
     * @param command the fired command
     */
    protected void notifyDialogListeners( Object command )
    {
        for ( DialogListener l: listeners )
        {
            l.onDialogClosed( this, command );
        }
    }
    
    /**
     * Use this method to notify all registered DialogListeners.
     */
    protected final void notifyDialogListeners()
    {
        notifyDialogListeners( getCloseCommand() );
    }
    
    /**
     * Waits in a separate Thread until this Dialog is set invisible
     * or removed from its parent.
     */
    public void waitForClose()
    {
        while ( Dialog.this.isVisible() )
        {
            Thread.yield();
        }
    }
    
    @Override
    public void onDetachedFromHUD( HUD hud )
    {
        super.onDetachedFromHUD( hud );
        
        notifyDialogListeners();
    }
    
    /**
     * Creates a new Dialog.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param headerDesc the description of this Dialog's header (or <i>null</i> for an undecorated Dialog)
     * @param title this Dialog's title
     */
    public Dialog( WidgetContainer contentPane, WindowHeaderWidget.Description headerDesc, String title )
    {
        super( contentPane, headerDesc, title );
    }
    
    /**
     * Creates a new Dialog.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param headerWidget a Widget, that defines this Window's header (or <i>null</i> for an undecorated Window)
     */
    public Dialog( WidgetContainer contentPane, WindowHeaderWidget headerWidget )
    {
        super( contentPane, headerWidget );
    }
    
    /**
     * Creates a new Dialog.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param title this Dialog's title
     */
    public Dialog( WidgetContainer contentPane, String title )
    {
        super( contentPane, title );
    }
    
    /**
     * Creates a new Dialog.
     * 
     * @param contentPane the Widget that visually defines the Window.
     */
    public Dialog( WidgetContainer contentPane )
    {
        super( contentPane );
    }
    
    /**
     * Creates a new Dialog.
     * 
     * @param width the width of the Window
     * @param height the height of the Window
     * @param title this Dialog's title
     */
    public Dialog( float width, float height, String title )
    {
        super( width, height, title );
    }
    
    /**
     * Creates a new Dialog.
     * 
     * @param width the width of the Window
     * @param height the height of the Window
     */
    public Dialog( float width, float height )
    {
        super( width, height );
    }
}
