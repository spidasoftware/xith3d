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
import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.PolygonAttribsStateUnit;
import org.xith3d.render.states.units.StateUnitPeer;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * Handles the setting of polygon attributes.
 * 
 * @author David Yazel
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 */
public class PolygonAttribsStateUnitPeer implements StateUnitPeer
{
    public static final void enablePolygonOffset( GL gl, DrawMode mode, OpenGLStatesCache statesCache, boolean enable )
    {
        if ( mode == DrawMode.FILL )
        {
            if ( enable && ( !statesCache.enabled || !statesCache.polygonOffsetFillEnabled ))
            {
                gl.glEnable( GL.GL_POLYGON_OFFSET_FILL );
                statesCache.polygonOffsetFillEnabled = true;
            }
            else if ( !enable && ( !statesCache.enabled || statesCache.polygonOffsetFillEnabled ) )
            {
                gl.glDisable( GL.GL_POLYGON_OFFSET_FILL );
                statesCache.polygonOffsetFillEnabled = false;
            }
        }
        else if ( mode == DrawMode.LINE )
        {
            if ( enable && ( !statesCache.enabled || !statesCache.polygonOffsetLineEnabled ) )
            {
                gl.glEnable( GL.GL_POLYGON_OFFSET_LINE );
                statesCache.polygonOffsetLineEnabled = true;
            }
            else if ( !enable && ( !statesCache.enabled || statesCache.polygonOffsetLineEnabled ) )
            {
                gl.glDisable( GL.GL_POLYGON_OFFSET_LINE );
                statesCache.polygonOffsetLineEnabled = false;
            }
        }
        else if ( mode == DrawMode.POINT )
        {
            if ( enable && ( !statesCache.enabled || !statesCache.polygonOffsetPointEnabled ) )
            {
                gl.glEnable( GL.GL_POLYGON_OFFSET_POINT );
                statesCache.polygonOffsetPointEnabled = true;
            }
            else if ( !enable && ( !statesCache.enabled || statesCache.polygonOffsetPointEnabled ) )
            {
                gl.glDisable( GL.GL_POLYGON_OFFSET_POINT );
                statesCache.polygonOffsetPointEnabled = false;
            }
        }
    }
    
    public static final void setCullMode( GL gl, OpenGLStatesCache statesCache, int mode, boolean enable, boolean isPickMode )
    {
        if ( isPickMode )
        {
            /*
             * In pick mode, calls to GL11.glDisable(GL.GL_CULL_FACE)
             * seem to be incompatible, so we let hit at least face side...
             */
            switch ( mode )
            {
                case GL.GL_FRONT:
                    gl.glCullFace( GL.GL_FRONT );
                    break;
                case GL.GL_BACK:
                    gl.glCullFace( GL.GL_BACK );
                    break;
                default:
                    break;
            }
        }
        else
        {
            switch ( mode )
            {
                case GL.GL_FRONT:
                    gl.glCullFace( GL.GL_FRONT );
                    break;
                case GL.GL_BACK:
                    gl.glCullFace( GL.GL_BACK );
                    break;
                default:
                    break;
            }
            
            if ( enable && ( !statesCache.enabled || !statesCache.cullFaceEnabled ) )
                gl.glEnable( GL.GL_CULL_FACE );
            else if ( !enable && ( !statesCache.enabled || statesCache.cullFaceEnabled ) )
                gl.glDisable( GL.GL_CULL_FACE );
            
            statesCache.cullFaceEnabled = enable;
        }
    }
    
    public static final boolean setCullMode( GL gl, OpenGLStatesCache statesCache, FaceCullMode mode, boolean autoEnable, boolean isPickMode )
    {
        if ( isPickMode )
        {
            /*
             * In pick mode, calls to GL11.glDisable(GL.GL_CULL_FACE)
             * seem to be incompatible, so we let hit at least face side...
             */
            switch ( mode )
            {
                case NONE:
                    gl.glCullFace( GL.GL_BACK );
                    break;
                case FRONT:
                    gl.glCullFace( GL.GL_FRONT );
                    break;
                case BACK:
                    gl.glCullFace( GL.GL_BACK );
                    break;
                default:
                    break;
            }
            
            return ( false );
        }
        
        boolean result = false;
        if ( autoEnable )
        {
            result = statesCache.cullFaceEnabled;
        }
        
        final Boolean enable;
        
        switch ( mode )
        {
            case NONE:
                enable = false;
                break;
            case FRONT:
                gl.glCullFace( GL.GL_FRONT );
                enable = true;
                break;
            case BACK:
                gl.glCullFace( GL.GL_BACK );
                enable = true;
                break;
            default:
                //enable = result;
                enable = null;
                break;
        }
        
        if ( enable != null )
        {
            if ( enable && ( !statesCache.enabled || !statesCache.cullFaceEnabled ) )
                gl.glEnable( GL.GL_CULL_FACE );
            else if ( !enable && ( !statesCache.enabled || statesCache.cullFaceEnabled ) )
                gl.glDisable( GL.GL_CULL_FACE );
            
            statesCache.cullFaceEnabled = enable;
        }
        
        return ( result );
    }
    
    public void apply( RenderAtom< ? > atom, StateUnit stateUnit, Object glObj, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, OpenGLStatesCache statesCache, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "PolygonAttribsStateUnitPeer::apply()" );
        
        final GL gl = (GL)glObj;
        
        final PolygonAttributes pa = ( (PolygonAttribsStateUnit)stateUnit ).getPolygonAttributes();
        
        final DrawMode mode;
        if ( options.isWireframeModeEnabled() )
            mode = DrawMode.LINE;
        else
            mode = pa.getDrawMode();
        
        gl.glPolygonMode( GL.GL_FRONT_AND_BACK, mode.toOpenGL() );
        
        final float pOfs = pa.getPolygonOffset();
        final float pOfsFactor = pa.getPolygonOffsetFactor();
        if ( ( pOfs == 0f ) && ( pOfsFactor == 0f ) )
        {
            enablePolygonOffset( gl, pa.getDrawMode(), statesCache, false );
        }
        else
        {
            gl.glPolygonOffset( pOfsFactor, pOfs );
            enablePolygonOffset( gl, pa.getDrawMode(), statesCache, true );
        }
        
        setCullMode( gl, statesCache, pa.getFaceCullMode(), true, renderMode == RenderMode.PICKING );
        
        if ( pa.isPolygonAntialiasingEnabled() && ( !statesCache.enabled || !statesCache.polygonSmoothEnabled ) )
            gl.glEnable( GL.GL_POLYGON_SMOOTH );
        else if ( !pa.isPolygonAntialiasingEnabled() && ( !statesCache.enabled || statesCache.polygonSmoothEnabled ) )
            gl.glDisable( GL.GL_POLYGON_SMOOTH );
        
        statesCache.polygonSmoothEnabled = pa.isPolygonAntialiasingEnabled();
        
        /**
         * commented out because most cards do NOT suport it float vector[] =
         * new float[4]; vector[0] = 0; vector[1] = 0; vector[2] = 0; vector[3] =
         * 1;
         * 
         * if (gl.glGetString(gl.GL_EXTENSIONS).indexOf("EXT_cull_vertex")>=0) {
         * if (firstTime) System.out.println("We have the cull vertex
         * extension"); firstTime =false;
         * gl.glCullParameterfvEXT(gl.GL_CULL_VERTEX_EYE_POSITION_EXT,vector);
         * gl.glEnable(gl.GL_CULL_VERTEX_EXT); } else { if (firstTime) {
         * System.out.println("We do not have the cull vertex extension");
         * System.out.println(gl.glGetString(gl.GL_EXTENSIONS)); firstTime
         * =false; } }
         */
        ProfileTimer.endProfile();
    }
}
