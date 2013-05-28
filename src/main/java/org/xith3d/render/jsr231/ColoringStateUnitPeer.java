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

import javax.media.opengl.GL;

import org.jagatoo.logging.ProfileTimer;
import org.openmali.vecmath2.Colorf;

import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.ColoringStateUnit;
import org.xith3d.render.states.units.StateUnitPeer;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * Handles the shading for coloring attributes.
 * 
 * @author Yuri Vl. Gushchin
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 */
public class ColoringStateUnitPeer implements StateUnitPeer
{
    public void apply( RenderAtom< ? > atom, StateUnit stateUnit, Object glObj, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, OpenGLStatesCache statesCache, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        if ( renderMode != RenderMode.NORMAL )
            return;
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "ColoringStateUnitPeer::apply()" );
        
        final GL gl = (GL)glObj;
        
        final ColoringAttributes ca = ( (ColoringStateUnit)stateUnit ).getColoringAttributes();
        final Colorf color = ca.getColor();
        final TransparencyAttributes m = ( (ColoringStateUnit)stateUnit ).getTransparencyAttributes();
        
        if ( ( m.getMode() == TransparencyAttributes.BLENDED ) && m.isEnabled() )
        {
            if ( !statesCache.enabled || !statesCache.blendingEnabled )
            {
                X3DLog.debug( "Blending enabled" );
                gl.glEnable( GL.GL_BLEND );
                statesCache.blendingEnabled = true;
            }
            gl.glBlendFunc( m.getSrcBlendFunction().toOpenGL(), m.getDstBlendFunction().toOpenGL() );
            
            // The glColor4 variants specify all four color components explicitly
            gl.glColor4f( color.getRed(), color.getGreen(), color.getBlue(), 1f - m.getTransparency() );
            statesCache.color.set( color );
        }
        else
        {
            if ( !statesCache.enabled || statesCache.blendingEnabled )
            {
                X3DLog.debug( "Blending disabled" );
                gl.glDisable( GL.GL_BLEND );
                statesCache.blendingEnabled = false;
            }
            
            // The glColor3 variants specify new red, green, and blue values
            // explicitly, and set the current alpha value to 1.0 implicitly.
            gl.glColor3f( color.getRed(), color.getGreen(), color.getBlue() );
            statesCache.color.set( color.getRed(), color.getGreen(), color.getBlue() );
        }
        
        gl.glShadeModel( ca.getShadeModel().toOpenGL() );
        ProfileTimer.endProfile();
    }
}
