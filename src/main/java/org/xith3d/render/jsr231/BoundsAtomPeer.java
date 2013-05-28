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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.spatial.bounds.BoundsType;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderAtomPeer;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.View;

import com.sun.opengl.util.BufferUtil;

/**
 * The Bounds Atom peer (renders bounds around atom nodes)
 * 
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 */
public class BoundsAtomPeer extends RenderAtomPeer
{
    private static final Matrix4f TEMP_MAT = new Matrix4f();
    private static final Colorf boundsColor_Shape = new Colorf( 1.0f, 0.0f, 0.0f, 0.0f );
    private static final Colorf boundsColor_Group = new Colorf( 0.0f, 1.0f, 0.0f, 0.0f );
    private static BoundingSphere boundingSphere = new BoundingSphere();
    private static BoundingBox boundingBox = new BoundingBox();
    private static GLUquadric boundsQuadric = null;
    
    private static final IntBuffer tmpIntBuffer = BufferUtil.newIntBuffer( 1 );
    private static FloatBuffer colorBuffer = BufferUtil.newFloatBuffer( 4 );
    
    /**
     * Draws bounds around shapes as a debugging aid
     * 
     * @param bounds
     */
    protected static final int drawBounds( GL gl, Bounds bounds, Colorf color, OpenGLStatesCache statesCache )
    {
        final GLU glu = GLUSingleton.instance();
        
        switch ( bounds.getType() )
        {
            case SPHERE:
            {
                if ( boundsQuadric == null )
                {
                    boundsQuadric = glu.gluNewQuadric();
                    glu.gluQuadricDrawStyle( boundsQuadric, GLU.GLU_LINE );
                }
                //gl.glPushMatrix();
                //gl.glLoadIdentity();
                if ( !statesCache.enabled || statesCache.blendingEnabled )
                    gl.glDisable( GL.GL_BLEND );
                statesCache.blendingEnabled = false;
                if ( !statesCache.enabled || statesCache.alphaTestEnabled )
                    gl.glDisable( GL.GL_ALPHA_TEST );
                statesCache.alphaTestEnabled = false;
                colorBuffer.rewind();
                gl.glGetFloatv( GL.GL_CURRENT_COLOR, colorBuffer );
                if ( color == null )
                    gl.glColor4f( boundsColor_Group.getRed(), boundsColor_Group.getGreen(), boundsColor_Group.getBlue(), boundsColor_Group.getAlpha() );
                else
                    gl.glColor4f( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
                BoundingSphere s = (BoundingSphere)bounds;
                gl.glTranslatef( s.getCenterX(), s.getCenterY(), s.getCenterZ() );
                glu.gluSphere( boundsQuadric, s.getRadius(), 12, 12 );
                gl.glTranslatef( -s.getCenterX(), -s.getCenterY(), -s.getCenterZ() );
                gl.glColor4fv( colorBuffer );
                if ( !statesCache.enabled || statesCache.blendingEnabled )
                    gl.glEnable( GL.GL_BLEND );
                if ( !statesCache.enabled || statesCache.alphaTestEnabled )
                    gl.glEnable( GL.GL_ALPHA_TEST );
                //gl.glPopMatrix();
                
                return ( 0 ); // (actually this is not 0, but it's nto important.)
            }
            case AABB:
            {
                if ( !statesCache.enabled || statesCache.alphaTestEnabled )
                    gl.glDisable( GL.GL_ALPHA_TEST );
                statesCache.alphaTestEnabled = false;
                colorBuffer.rewind();
                gl.glGetFloatv( GL.GL_CURRENT_COLOR, colorBuffer );
                gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
                if ( color == null )
                    gl.glColor4f( boundsColor_Group.getRed(), boundsColor_Group.getGreen(), boundsColor_Group.getBlue(), 0.05f );
                else
                    gl.glColor4f( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
                
                BoundingBox b = (BoundingBox)bounds;
                final Tuple3f l = b.getLower();
                final Tuple3f u = b.getUpper();
                final float size = Math.max( Math.max( u.getX() - l.getX(), u.getY() - l.getY() ), u.getZ() - l.getZ() );
                final float tx = l.getX() + ( size / 2.0f );
                final float ty = l.getY() + ( size / 2.0f );
                final float tz = l.getZ() + ( size / 2.0f );
                gl.glTranslatef( tx, ty, tz );
                
                gl.glDepthMask( false );
                
                gl.glColor4f( boundsColor_Group.getRed(), boundsColor_Group.getGreen(), boundsColor_Group.getBlue(), 0.1f );
                gl.glEnable( GL.GL_BLEND );
                GLUSingleton.getGLUT().glutSolidCube( size );
                
                gl.glDepthMask( true );
                
                tmpIntBuffer.clear();
                gl.glGetIntegerv( GL.GL_LINE_WIDTH, tmpIntBuffer );
                final int LINE_WIDTH = tmpIntBuffer.get();
                
                gl.glColor4f( 1.0f, 0.0f, 0.0f, 0.0f );
                gl.glLineWidth( 3f );
                gl.glDisable( GL.GL_BLEND );
                GLUSingleton.getGLUT().glutWireCube( size );
                gl.glLineWidth( LINE_WIDTH );
                
                gl.glTranslatef( -tx, -ty, -tz );
                gl.glColor4fv( colorBuffer );
                if ( !statesCache.enabled || statesCache.blendingEnabled )
                    gl.glEnable( GL.GL_BLEND );
                else
                    gl.glDisable( GL.GL_BLEND );
                if ( !statesCache.enabled || statesCache.alphaTestEnabled )
                    gl.glEnable( GL.GL_ALPHA_TEST );
                else
                    gl.glDisable( GL.GL_ALPHA_TEST );
                
                gl.glDepthMask( statesCache.depthWriteMask );
                
                return ( 0 ); // (actually this is not 0, but it's not important.)
            }
        }
        
        return ( 0 ); // (actually this is not 0, but it's not important.)
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int renderAtom( RenderAtom< ? > atom, Object glObj, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        if ( renderMode != RenderMode.NORMAL )
            return ( 0 );
        
        final GL gl = (GL)glObj;
        final OpenGLStatesCache statesCache = renderPeer.getStatesCache();
        
        // disable color-material
        if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
        {
            gl.glDisable( GL.GL_COLOR_MATERIAL );
            statesCache.colorMaterialEnabled = false;
        }
        
        // disable lighting
        if ( !statesCache.enabled || statesCache.lightingEnabled )
        {
            gl.glDisable( GL.GL_LIGHTING );
            statesCache.lightingEnabled = false;
        }
        
        ShapeAtomPeer.setMatrix( gl, view, Transform3D.IDENTITY,//atom.getTransform().getMatrix4f(),
                                 false, true );
        
        Bounds bounds;
        if ( atom.getNode().getBounds().getType() == BoundsType.AABB )
            bounds = boundingBox;
        else
            bounds = boundingSphere;
        
        bounds.set( atom.getNode().getBounds() );
        bounds.transform( atom.getNode().getWorldTransform().getMatrix4f() );
        if ( atom.getNode() instanceof TransformGroup )
        {
            TEMP_MAT.set( ( (TransformGroup)atom.getNode() ).getTransform().getMatrix4f() );
            TEMP_MAT.invert();
            bounds.transform( TEMP_MAT );
        }
        
        return ( drawBounds( gl, bounds, boundsColor_Shape, statesCache ) );
    }
}
