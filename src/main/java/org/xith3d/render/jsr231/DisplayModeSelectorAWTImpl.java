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
package org.xith3d.render.jsr231;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.OpenGLLayer;

/**
 * DisplayModeSelector implementation for JOGL_AWT.<br>
 * If you want to know, which DisplayModes are awailable on your System and
 * for a specific OpenGLLayer, make use of it.<br>
 * <br>
 * Instantiate it by invoking the static getImplementation() method.
 * 
 * @see DisplayModeSelector#getImplementation(org.xith3d.render.config.OpenGLLayer)
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DisplayModeSelectorAWTImpl extends DisplayModeSelector
{
    private static final OpenGLLayer OPENGL_LAYER = OpenGLLayer.JOGL_AWT;
    
    private static DisplayMode[] cachedModes = null;
    
    /**
     * If no "real" fullscreen mode is supported, "artificial" DisplayModes
     * need to be generated. They will all have getNativeMode() = null.
     * 
     * @return an array of "artificial" generated DisplayModes
     */
    private DisplayMode[] getFallbackModes()
    {
        GraphicsDevice graphDev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        final java.awt.DisplayMode awtMode = graphDev.getDisplayMode();
        final int bpp = awtMode.getBitDepth();
        final int freq = awtMode.getRefreshRate();
        
        DisplayMode[] modes = new DisplayMode[ 11 ];
        
        modes[ 0 ] = new DisplayMode( OPENGL_LAYER, null, 320, 240, bpp, freq );
        modes[ 1 ] = new DisplayMode( OPENGL_LAYER, null, 640, 480, bpp, freq );
        modes[ 2 ] = new DisplayMode( OPENGL_LAYER, null, 800, 600, bpp, freq );
        modes[ 3 ] = new DisplayMode( OPENGL_LAYER, null, 1024, 768, bpp, freq );
        modes[ 4 ] = new DisplayMode( OPENGL_LAYER, null, 1152, 864, bpp, freq );
        modes[ 5 ] = new DisplayMode( OPENGL_LAYER, null, 1280, 960, bpp, freq );
        modes[ 6 ] = new DisplayMode( OPENGL_LAYER, null, 1280, 1024, bpp, freq );
        modes[ 7 ] = new DisplayMode( OPENGL_LAYER, null, 1480, 925, bpp, freq );
        modes[ 8 ] = new DisplayMode( OPENGL_LAYER, null, 1600, 1200, bpp, freq );
        modes[ 9 ] = new DisplayMode( OPENGL_LAYER, null, 1680, 1050, bpp, freq );
        modes[ 10 ] = new DisplayMode( OPENGL_LAYER, null, 1920, 1200, bpp, freq );
        
        return ( modes );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DisplayMode[] getAvailableModes()
    {
        if ( cachedModes != null )
            return ( cachedModes );
        
        GraphicsDevice graphDev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        java.awt.DisplayMode[] awtModes = graphDev.getDisplayModes();
        
        if ( ( awtModes.length <= 1 ) || ( !graphDev.isFullScreenSupported() ) )
            return ( getFallbackModes() );
        
        DisplayMode[] modes = new DisplayMode[ awtModes.length ];
        
        for ( int i = 0; i < awtModes.length; i++ )
        {
            modes[ i ] = new DisplayMode( OPENGL_LAYER, awtModes[ i ], awtModes[ i ].getWidth(), awtModes[ i ].getHeight(), awtModes[ i ].getBitDepth(), awtModes[ i ].getRefreshRate() );
        }
        
        sortModes( modes );
        
        cachedModes = modes;
        
        return ( modes );
    }
}
