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
import org.openmali.FastMath;
import org.openmali.vecmath2.Point3f;
import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.shadows.VolumeShadowFactory;
import org.xith3d.effects.shadows.occluder.Occluder;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * Handles volume shadow rendering.
 * 
 * @author David Yazel
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 */
public class VolumeShadowRenderPeer implements ShadowRenderPeer.ShadowRenderPeerInterface
{
    private float[] openGLProjMatrix = new float[ 16 ];
    private float[] viewTrans = new float[ 16 ];
    
    public final int initShadows( GL gl, View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId )
    {
        return ( 0 );
    }
    
    private final int drawObjectShadow( GL gl, OpenGLStatesCache statesCache, OpenGLCapabilities glCaps, Occluder object )
    {
        final boolean statesCacheDisabled = !statesCache.enabled;
        
        gl.glPushMatrix();
        
        gl.glLoadMatrixf( viewTrans, 0 );
        
        object.getWorldTransform().getColumnMajor( openGLProjMatrix );
        
        gl.glMultMatrixf( openGLProjMatrix, 0 );
        
        if ( statesCacheDisabled || statesCache.colorsArrayEnabled )
        {
            gl.glDisableClientState( GL.GL_COLOR_ARRAY );
            statesCache.colorsArrayEnabled = false;
        }
        if ( statesCacheDisabled || statesCache.normalsArrayEnabled )
        {
            gl.glDisableClientState( GL.GL_NORMAL_ARRAY );
            statesCache.normalsArrayEnabled = false;
        }
        for ( int i = 0; i < glCaps.getMaxTextureUnits(); i++ )
        {
            final int tuMaskValue = FastMath.pow( 2, i );
            if ( statesCacheDisabled || ( statesCache.texCoordArraysEnableMask & tuMaskValue ) != 0 )
            {
                ShapeAtomPeer.selectClientTextureUnit( gl, i, statesCache, false );
                gl.glDisableClientState( GL.GL_TEXTURE_COORD_ARRAY );
                statesCache.texCoordArraysEnableMask &= ~tuMaskValue;
            }
        }
        if ( statesCacheDisabled || !statesCache.coordsArrayEnabled )
        {
            gl.glEnableClientState( GL.GL_VERTEX_ARRAY );
            statesCache.coordsArrayEnabled = true;
        }
        
        gl.glVertexPointer( 3, GL.GL_FLOAT, 0, object.getBuffer().getCoordinatesData().getBuffer().rewind() );
        gl.glDrawArrays( GL.GL_TRIANGLES, 0, object.getBuffer().getValidVertexCount() );
        
        gl.glPopMatrix();
        
        return ( object.getBuffer().getValidVertexCount() / 3 );
    }
    
    /**
     * Draws the shadow volumes in the ShadowBin.
     * 
     * @param gl
     * @param view
     * @param light
     * @param shadowBin
     * @param renderPeer
     * @param frameId
     */
    public final int drawShadows( GL gl, View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId )
    {
        final OpenGLStatesCache statesCache = renderPeer.getStatesCache();
        final OpenGLCapabilities glCaps = renderPeer.getCanvasPeer().getOpenGLCapabilities();
        
        final VolumeShadowFactory shadowFactory = (VolumeShadowFactory)EffectFactory.getInstance().getShadowFactory();
        
        final Point3f lightSourcePos = shadowFactory.getLightSourcePosition();
        
        int numTriangles = 0;
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "ShadowShaderPeer::drawShadows" );
        //final int FULLMASK = 0xffffffff;
        //final int STENCIL_VAL = 128;
        
        // determine edges
        
        final RenderBin bin = shadowBin;
        
        for ( int i = 0; i < bin.size(); i++ )
        {
            final Node node = bin.getAtom( i ).getNode();
            final Occluder occluder = (Occluder)node.getShadowAttachment();
            occluder.determineVisibleEdges( lightSourcePos );
        }
        
        gl.glDisable( GL.GL_LIGHTING ); // turn off lighting
        statesCache.lightingEnabled = true;
        
        gl.glEnable( GL.GL_CULL_FACE );
        statesCache.cullFaceEnabled = true;
        gl.glCullFace( GL.GL_BACK );
        
        gl.glDepthMask( false ); // turn off writing to the depth-buffer
        gl.glDepthFunc( GL.GL_LEQUAL );
        
        gl.glEnable( GL.GL_STENCIL_TEST ); // turn on stencil buffer testing
        statesCache.stencilTestEnabled = true;
        gl.glColorMask( false, false, false, false ); // don't draw into the color buffer
        gl.glStencilFunc( GL.GL_ALWAYS, 1, 0xffffffff );
        gl.glFrontFace( GL.GL_CCW );
        
        // frst pass: increase stencil value in the shadow
        
        gl.glStencilOp( GL.GL_KEEP, GL.GL_KEEP, GL.GL_INCR );
        
        for ( int i = 0; i < bin.size(); i++ )
        {
            final Node node = bin.getAtom( i ).getNode();
            final Occluder occluder = (Occluder)node.getShadowAttachment();
            numTriangles += drawObjectShadow( gl, statesCache, glCaps, occluder );
        }
        
        // second pass: decrease stencil value in the shadow
        
        gl.glFrontFace( GL.GL_CW );
        gl.glStencilOp( GL.GL_KEEP, GL.GL_KEEP, GL.GL_DECR );
        
        gl.glColor3f( 0f, 0f, 0f );
        for ( int i = 0; i < bin.size(); i++ )
        {
            final Node node = bin.getAtom( i ).getNode();
            final Occluder occluder = (Occluder)node.getShadowAttachment();
            drawObjectShadow( gl, statesCache, glCaps, occluder );
        }
        
        gl.glFrontFace( GL.GL_CCW );
        gl.glColorMask( ( statesCache.colorWriteMask & 1 ) != 0,
                        ( statesCache.colorWriteMask & 2 ) != 0,
                        ( statesCache.colorWriteMask & 4 ) != 0,
                        ( statesCache.colorWriteMask & 8 ) != 0
                      ); // reset color-mask
        
        // draw a shadowing rectangle covering the entire screen
        gl.glColor4f( 0.0f, 0.0f, 0.0f, 0.2f );
        gl.glEnable( GL.GL_BLEND );
        statesCache.blendingEnabled = true;
        gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
        gl.glStencilFunc( GL.GL_NOTEQUAL, 0, 0xffffffff );
        gl.glStencilOp( GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP );
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glBegin( GL.GL_TRIANGLE_STRIP );
        gl.glVertex3f( -1f, +1f, -0.20f );
        gl.glVertex3f( -1f, -1f, -0.20f );
        gl.glVertex3f( +1f, +1f, -0.20f );
        gl.glVertex3f( +1f, -1f, -0.20f );
        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable( GL.GL_BLEND );
        statesCache.blendingEnabled = false;
        gl.glDepthFunc( GL.GL_LEQUAL );
        gl.glDepthMask( true );
        gl.glEnable( GL.GL_LIGHTING );
        statesCache.lightingEnabled = true;
        gl.glDisable( GL.GL_STENCIL_TEST );
        statesCache.stencilTestEnabled = false;
        gl.glShadeModel( GL.GL_SMOOTH );
        
        ProfileTimer.endProfile();
        
        return ( numTriangles );
    }
}
