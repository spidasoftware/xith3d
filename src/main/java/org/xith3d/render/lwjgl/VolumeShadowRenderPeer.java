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

import java.nio.FloatBuffer;

import org.jagatoo.logging.ProfileTimer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
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
 * @author Marvin Froehlich (aka Qudus)
 */
public class VolumeShadowRenderPeer implements ShadowRenderPeer.ShadowRenderPeerInterface
{
    private final FloatBuffer floatBuffer4x4 = BufferUtils.createFloatBuffer( 16 );
    
    private float[] trans = new float[ 16 ];
    
    public final int initShadows( View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId )
    {
        return ( 0 );
    }
    
    private final int drawObjectShadow( OpenGLStatesCache statesCache, OpenGLCapabilities glCaps, Occluder occluder )
    {
        occluder.getWorldTransform().getColumnMajor( trans );
        floatBuffer4x4.clear();
        floatBuffer4x4.put( trans ).flip();
        floatBuffer4x4.rewind();
        GL11.glLoadMatrix( floatBuffer4x4 );
        
        if ( !statesCache.enabled || statesCache.normalsArrayEnabled )
        {
            GL11.glDisableClientState( GL11.GL_NORMAL_ARRAY );
            statesCache.normalsArrayEnabled = false;
        }
        if ( !statesCache.enabled || statesCache.colorsArrayEnabled )
        {
            GL11.glDisableClientState( GL11.GL_COLOR_ARRAY );
            statesCache.colorsArrayEnabled = false;
        }
        for ( int i = 0; i < glCaps.getMaxTextureUnits(); i++ )
        {
            final int tuMaskValue = FastMath.pow( 2, i );
            if ( !statesCache.enabled || ( statesCache.texCoordArraysEnableMask & tuMaskValue ) != 0 )
            {
                ShapeAtomPeer.selectClientTextureUnit( i, statesCache, false );
                GL11.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                statesCache.texCoordArraysEnableMask &= ~tuMaskValue;
            }
        }
        if ( !statesCache.enabled || !statesCache.coordsArrayEnabled )
        {
            GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
            statesCache.coordsArrayEnabled = true;
        }
        
        GL11.glVertexPointer( 3, 0, occluder.getBuffer().getCoordinatesData().getBuffer() );
        GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, occluder.getBuffer().getValidVertexCount() );
        
        return ( occluder.getBuffer().getValidVertexCount() / 3 );
    }
    
    /**
     * Draws the shadow volumes in the ShadowBin.
     * 
     * @param view
     * @param light
     * @param shadowBin
     * @param renderPeer
     * @param frameId
     * 
     * @return the number of triangles rendered
     */
    public final int drawShadows( View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId )
    {
        final OpenGLStatesCache statesCache = renderPeer.getStatesCache();
        final OpenGLCapabilities glCaps = renderPeer.getCanvasPeer().getOpenGLCapabilities();
        
        final VolumeShadowFactory shadowFactory = (VolumeShadowFactory)EffectFactory.getInstance().getShadowFactory();
        
        final Point3f lightSourcePos = shadowFactory.getLightSourcePosition();
        
        int numTriangles = 0;
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "ShadowShaderPeer::drawShadows" );
        
        final int FULLMASK = 0xffffffff;
        final int STENCIL_VAL = 128;
        
        final RenderBin bin = shadowBin;
        
        // determine edges
        for ( int i = 0; i < bin.size(); i++ )
        {
            final Node node = bin.getAtom( i ).getNode();
            final Occluder occluder = (Occluder)node.getShadowAttachment();
            occluder.determineVisibleEdges( lightSourcePos );
        }
        
        GL11.glPushAttrib( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_POLYGON_BIT | GL11.GL_STENCIL_BUFFER_BIT );
        
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable( GL11.GL_LIGHTING ); // turn off Lighting
        statesCache.lightingEnabled = false;
        GL11.glDisable( GL11.GL_TEXTURE_2D );
        statesCache.texture2DEnabled[ statesCache.currentServerTextureUnit ] = false;
        GL11.glDisable( GL12.GL_TEXTURE_3D );
        statesCache.texture3DEnabled[ statesCache.currentServerTextureUnit ] = false;
        GL11.glDisable( GL11.GL_BLEND );
        statesCache.blendingEnabled = false;
        GL11.glStencilFunc( GL11.GL_NEVER, 0xff, FULLMASK );
        
        GL11.glDepthMask( true );
        GL11.glDepthFunc( GL11.GL_LEQUAL );
        GL11.glEnable( GL11.GL_DEPTH_TEST );
        statesCache.depthTestEnabled = true;
        
        GL11.glPopMatrix();
        
        GL11.glColor3f( 1f, 0f, 0f );
        GL11.glClearStencil( STENCIL_VAL );
        GL11.glClear( GL11.GL_STENCIL_BUFFER_BIT );
        
        GL11.glDisable( GL11.GL_LIGHTING ); // turn off Lighting
        statesCache.lightingEnabled = false;
        
        GL11.glFrontFace( GL11.GL_CCW );
        GL11.glEnable( GL11.GL_CULL_FACE );
        statesCache.cullFaceEnabled = true;;
        GL11.glCullFace( GL11.GL_FRONT );
        
        GL11.glDepthMask( false ); // turn off writing to the Depth-Buffer
        GL11.glDepthFunc( GL11.GL_LEQUAL );
        GL11.glEnable( GL11.GL_STENCIL_TEST ); // turn on Stencil-Buffer testing
        statesCache.stencilTestEnabled = true;
        GL11.glColorMask( false, false, false, false ); // don't draw into the Color-Buffer
        GL11.glStencilFunc( GL11.GL_ALWAYS, STENCIL_VAL, FULLMASK );
        
        // First Pass. Increase Stencil Value In The Shadow
        GL11.glStencilOp( GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR );
        
        for ( int i = 0; i < bin.size(); i++ )
        {
            final Node node = bin.getAtom( i ).getNode();
            final Occluder occluder = (Occluder)node.getShadowAttachment();
            numTriangles += drawObjectShadow( statesCache, glCaps, occluder );
        }
        
        // Second Pass. Decrease Stencil Value In The Shadow
        GL11.glCullFace( GL11.GL_BACK );
        GL11.glStencilOp( GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR );
        
        GL11.glColor3f( 0.5f, 0.5f, 0f );
        for ( int i = 0; i < bin.size(); i++ )
        {
            final Node node = bin.getAtom( i ).getNode();
            final Occluder occluder = (Occluder)node.getShadowAttachment();
            drawObjectShadow( statesCache, glCaps, occluder );
        }
        
        GL11.glColorMask( ( statesCache.colorWriteMask & 1 ) != 0,
                          ( statesCache.colorWriteMask & 2 ) != 0,
                          ( statesCache.colorWriteMask & 4 ) != 0,
                          ( statesCache.colorWriteMask & 8 ) != 0
                        ); // reset color-mask
        
        // Draw A Shadowing RectanGL11.gle Covering The Entire Screen
        GL11.glColor4f( 0.0f, 0.0f, 0.0f, 0.4f );
        GL11.glEnable( GL11.GL_BLEND );
        statesCache.blendingEnabled = true;
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        
        // get rid of the view matrix, only have the projection matrix
        
        GL11.glMatrixMode( GL11.GL_PROJECTION );
        GL11.glLoadIdentity();
        view.getProjection().getColumnMajor( trans );
        floatBuffer4x4.clear();
        floatBuffer4x4.put( trans ).flip();
        floatBuffer4x4.rewind();
        GL11.glLoadMatrix( floatBuffer4x4 );
        
        GL11.glMatrixMode( GL11.GL_MODELVIEW );
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
        GL11.glDepthMask( false );
        
        //GL11.glEnable( GL11.GL_STENCIL_TEST ); // turn on Stencil-Buffer testing
        GL11.glStencilFunc( GL11.GL_NOTEQUAL, STENCIL_VAL, FULLMASK );
        GL11.glStencilOp( GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP );
        GL11.glCullFace( GL11.GL_BACK );
        GL11.glDisable( GL11.GL_CULL_FACE );
        statesCache.cullFaceEnabled = false;
        GL11.glAlphaFunc( GL11.GL_ALWAYS, 0f );
        
        GL11.glBegin( GL11.GL_TRIANGLE_STRIP );
        GL11.glVertex3f( -100f, 100f, -2f );
        GL11.glVertex3f( -100f, -1000f, -2f );
        GL11.glVertex3f( +100f, 100f, -2f );
        GL11.glVertex3f( +100f, -1000f, -2f );
        GL11.glEnd();
        
        GL11.glPopMatrix();
        
        GL11.glPopAttrib();
        ProfileTimer.endProfile();
        
        return ( numTriangles );
    }
}
