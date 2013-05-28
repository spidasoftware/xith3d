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

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.base.Window;
import org.xith3d.ui.hud.base.WindowHeaderWidget;

/**
 * This class represents a simple Window above the HUD.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Frame extends Window
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected WindowHeaderWidget createHeaderWidget( WindowHeaderWidget.Description headerDesc, String title )
    {
        return ( new WindowHeaderWidget( getWidth(), title, headerDesc ) );
    }
    
    public static Panel createDefaultContentPane( float width, float height )
    {
        Colorf backgroundColor = HUD.getTheme().getContentPaneBackgroundColor();
        Texture2D backgroundTexture = HUD.getTheme().getContentPaneBackgroundTexture();
        
        return ( new Panel( false, width, height, backgroundColor, backgroundTexture ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected WidgetContainer createContentPane( float width, float height )
    {
        return ( createDefaultContentPane( width, height ) );
    }
    
    /**
     * Creates a new Frame.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param headerDesc the description of this Frame's header (or <i>null</i> for an undecorated Frame)
     * @param title this Frame's title
     */
    public Frame( WidgetContainer contentPane, WindowHeaderWidget.Description headerDesc, String title )
    {
        super( contentPane, headerDesc, title );
    }
    
    /**
     * Creates a new Frame.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param headerWidget a Widget, that defines this Window's header (or <i>null</i> for an undecorated Window)
     */
    public Frame( WidgetContainer contentPane, WindowHeaderWidget headerWidget )
    {
        super( contentPane, headerWidget );
    }
    
    /**
     * Creates a new Frame.
     * 
     * @param contentPane the Widget that visually defines the Window.
     * @param title this Frame's title
     */
    public Frame( WidgetContainer contentPane, String title )
    {
        super( contentPane, ( title != null ) ? HUD.getTheme().getWindowHeaderDescription() : null, title );
    }
    
    /**
     * Creates a new Frame.
     * 
     * @param contentPane the Widget that visually defines the Window.
     */
    public Frame( WidgetContainer contentPane )
    {
        this( contentPane, (String)null );
    }
    
    /**
     * Creates a new Frame.
     * 
     * @param width the width of the Window
     * @param height the height of the Window
     * @param title this Frame's title
     */
    public Frame( float width, float height, String title )
    {
        super( width, height, false, (WidgetContainer)null, HUD.getTheme().getWindowHeaderDescription(), title );
    }
    
    /**
     * Creates a new Frame.
     * 
     * @param width the width of the Window
     * @param height the height of the Window
     */
    public Frame( float width, float height )
    {
        this( width, height, (String)null );
    }
}
