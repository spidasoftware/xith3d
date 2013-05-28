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
import org.jagatoo.opengl.enums.LinePattern;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.LineAttribsStateUnit;
import org.xith3d.render.states.units.StateUnitPeer;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * <p>
 * Handles the setting of line attributes, specifically the line width, pattern
 * and antialiasing.
 * <p>
 * 
 * @author Yuri Vl. Gushchin
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 * 
 * @version 1.0
 */
public class LineAttribsStateUnitPeer implements StateUnitPeer
{
    private static final int PATTERN_SOLID_MASK = 0xFFFF;
    
    private static final int PATTERN_DASH_MASK = 0x00FF;
    
    private static final int PATTERN_DOT_MASK = 0x0101;
    
    private static final int PATTERN_DASH_DOT_MASK = 0x087F;
    
    public void apply( RenderAtom< ? > atom, StateUnit stateUnit, Object glObj, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, OpenGLStatesCache statesCache, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "LineAttribsStateUnitPeer::apply()" );
        
        final GL gl = (GL)glObj;
        
        LineAttributes la = ( (LineAttribsStateUnit)stateUnit ).getLineAttributes();
        
        gl.glLineWidth( la.getLineWidth() );
        LinePattern pattern = la.getLinePattern();
        if ( pattern == LineAttributes.PATTERN_SOLID )
        {
            if ( !statesCache.enabled || statesCache.lineStippleEnabled )
            {
                gl.glDisable( GL.GL_LINE_STIPPLE );
                statesCache.lineStippleEnabled = false;
            }
        }
        else
        {
            if ( !statesCache.enabled || !statesCache.lineStippleEnabled )
            {
                gl.glEnable( GL.GL_LINE_STIPPLE );
                statesCache.lineStippleEnabled = true;
            }
            
            int mask = la.getPatternMask();
            switch ( pattern )
            {
                case SOLID:
                    mask = PATTERN_SOLID_MASK;
                    break;
                case DASHED:
                    mask = PATTERN_DASH_MASK;
                    break;
                case DOTTED:
                    mask = PATTERN_DOT_MASK;
                    break;
                case DASHED_DOTTED:
                    mask = PATTERN_DASH_DOT_MASK;
            }
            gl.glLineStipple( la.getPatternScaleFactor(), (short)mask );
        }
        
        if ( la.isLineAntialiasingEnabled() && ( !statesCache.enabled || !statesCache.lineSmoothEnabled ) )
            gl.glEnable( GL.GL_LINE_SMOOTH );
        else if ( !la.isLineAntialiasingEnabled() && ( !statesCache.enabled || statesCache.lineSmoothEnabled ) )
            gl.glDisable( GL.GL_LINE_SMOOTH );
        
        statesCache.lineSmoothEnabled = la.isLineAntialiasingEnabled();
        
        ProfileTimer.endProfile();
    }
}
