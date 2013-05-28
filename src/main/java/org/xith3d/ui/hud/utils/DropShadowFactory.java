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
package org.xith3d.ui.hud.utils;

import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.base.Widget;

/**
 * the {@link DropShadowFactory} is capable of drawing dropshadows around {@link Widget}s
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class DropShadowFactory
{
    private final int width;
    private final int height;
    
    /**
     * Gets the drow shadow width in pixels.
     * 
     * @return the drop shadow's width in pixels.
     */
    public final int getDropShadowWidth()
    {
        return ( width );
    }
    
    /**
     * Gets the drow shadow height in pixels.
     * 
     * @return the drop shadow's height in pixels.
     */
    public final int getDropShadowHeight()
    {
        return ( height );
    }
    
    /**
     * Draws a dropshadow for a {@link Widget}.
     * 
     * @param widgetRight the right coordinate of the widget in texture space
     * @param widgetBottom the bottom coordinate of the widget in texture space
     * @param widgetWidth the width coordinate of the widget in texture space
     * @param widgetHeight the height coordinate of the widget in texture space
     * @param zIndex the widget's z-index
     * @param texCanvas the texture canvas to traw on
     */
    public abstract void drawDropShadow( int widgetRight, int widgetBottom, int widgetWidth, int widgetHeight, int zIndex, Texture2DCanvas texCanvas );
    
    protected DropShadowFactory( int width, int height )
    {
        this.width = width;
        this.height = height;
    }
}
