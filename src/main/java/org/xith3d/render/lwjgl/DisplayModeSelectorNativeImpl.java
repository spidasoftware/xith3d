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
package org.xith3d.render.lwjgl;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.OpenGLLayer;

/**
 * DisplayModeSelector implementation for LWJGL.<br>
 * If you want to know, which DisplayModes are awailable on your System and
 * for a specific OpenGLLayer, make use of it.<br>
 * <br>
 * Instantiate it by invoking the static getImplementation() method.
 * 
 * @see DisplayModeSelector#getImplementation(org.xith3d.render.config.OpenGLLayer)
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DisplayModeSelectorNativeImpl extends DisplayModeSelector
{
    private static final OpenGLLayer OPENGL_LAYER = OpenGLLayer.LWJGL;
    
    private static DisplayMode[] cachedModes = null;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DisplayMode[] getAvailableModes()
    {
        if ( cachedModes != null )
            return ( cachedModes );
        
        org.lwjgl.opengl.DisplayMode[] lwjglModes;
        try
        {
            lwjglModes = Display.getAvailableDisplayModes();
        }
        catch ( LWJGLException e )
        {
            return ( new DisplayMode[ 0 ] );
        }
        
        DisplayMode[] modes = new DisplayMode[ lwjglModes.length ];
        
        for ( int i = 0; i < lwjglModes.length; i++ )
        {
            modes[ i ] = new DisplayMode( OPENGL_LAYER, lwjglModes[ i ], lwjglModes[ i ].getWidth(), lwjglModes[ i ].getHeight(), lwjglModes[ i ].getBitsPerPixel(), lwjglModes[ i ].getFrequency() );
        }
        
        sortModes( modes );
        
        cachedModes = modes;
        
        return ( modes );
    }
}
