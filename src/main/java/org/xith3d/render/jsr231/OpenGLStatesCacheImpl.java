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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.OpenGlExtensions;

import com.sun.opengl.util.BufferUtil;

/**
 * This extension is just used to initialize the states.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OpenGLStatesCacheImpl extends OpenGLStatesCache
{
    private static final FloatBuffer floatBuffer = BufferUtil.newFloatBuffer( 16 );
    private static final ByteBuffer byteBuffer = BufferUtil.newByteBuffer( 16 );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void update( Object glObj, OpenGLCapabilities glCaps )
    {
        final GL gl = (GL)glObj;
        
        final int oldSelectedServerTU = ( this.currentServerTextureUnit == -1 ) ? 0 : this.currentServerTextureUnit;
        final int oldSelectedClientTU = ( this.currentClientTextureUnit == -1 ) ? 0 : this.currentClientTextureUnit;
        
        int texCoordArrayEnabledMask = 0;
        final boolean[] texture1DEnabled = new boolean[ glCaps.getMaxTextureUnits() ];
        final boolean[] texture2DEnabled = new boolean[ glCaps.getMaxTextureUnits() ];
        final boolean[] texture3DEnabled = new boolean[ glCaps.getMaxTextureUnits() ];
        final boolean[] textureCMEnabled = new boolean[ glCaps.getMaxTextureUnits() ];
        final int[] texGenEnableMask = new int[ glCaps.getMaxTextureUnits() ];
        int maskValue = 1;
        for ( int i = 0; i < glCaps.getMaxTextureUnits(); i++ )
        {
            TextureUnitStateUnitPeer.selectServerTextureUnit( gl, i, this, true );
            
            if ( gl.glIsEnabled( GL.GL_TEXTURE_COORD_ARRAY ) )
                texCoordArrayEnabledMask |= maskValue;
            
            texture1DEnabled[ i ] = gl.glIsEnabled( GL.GL_TEXTURE_1D );
            texture2DEnabled[ i ] = gl.glIsEnabled( GL.GL_TEXTURE_2D );
            texture3DEnabled[ i ] = gl.glIsEnabled( GL.GL_TEXTURE_3D );
            textureCMEnabled[ i ] = gl.glIsEnabled( GL.GL_TEXTURE_CUBE_MAP );
            
            maskValue *= 2;
            
            texGenEnableMask[ i ] = 0;
            if ( gl.glIsEnabled( GL.GL_TEXTURE_GEN_S ) )
                texGenEnableMask[ i ] |= 1;
            if ( gl.glIsEnabled( GL.GL_TEXTURE_GEN_T ) )
                texGenEnableMask[ i ] |= 2;
            if ( gl.glIsEnabled( GL.GL_TEXTURE_GEN_R ) )
                texGenEnableMask[ i ] |= 4;
            if ( gl.glIsEnabled( GL.GL_TEXTURE_GEN_Q ) )
                texGenEnableMask[ i ] |= 8;
        }
        
        TextureUnitStateUnitPeer.selectServerTextureUnit( gl, oldSelectedServerTU, this, true );
        ShapeAtomPeer.selectClientTextureUnit( gl, oldSelectedClientTU, this, true );
        
        final long vertexAttribsEnableMask = 0L;
        if ( OpenGlExtensions.GL_CUSTOM_VERTEX_ATTRIBUTES )
        {
            for ( int i = 0; i < glCaps.getMaxVertexAttributes(); i++ )
            {
                gl.glDisableVertexAttribArray( i );
            }
        }
        
        byteBuffer.rewind();
        gl.glGetBooleanv( GL.GL_COLOR_WRITEMASK, byteBuffer );
        int colorWriteMask = 0;
        if ( byteBuffer.get( 0 ) == 1 )
            colorWriteMask |= 1;
        if ( byteBuffer.get( 1 ) == 1 )
            colorWriteMask |= 2;
        if ( byteBuffer.get( 2 ) == 1 )
            colorWriteMask |= 4;
        if ( byteBuffer.get( 3 ) == 1 )
            colorWriteMask |= 8;
        
        byteBuffer.rewind();
        gl.glGetBooleanv( GL.GL_DEPTH_WRITEMASK, byteBuffer );
        boolean depthWriteMask = ( byteBuffer.get( 0 ) == 1 );
        
        final int currentGLSLShaderProgram = GLSLShaderProgramStateUnitPeer.getCurrentShaderProgram( gl );
        
        final boolean[] clipPlaneEnabled = new boolean[ 6 ];
        for ( int i = 0; i < clipPlaneEnabled.length; i++ )
        {
            final int glPlane = RenderPeerImpl.translateClipPlaneIndex( i );
            
            clipPlaneEnabled[ i ] = gl.glIsEnabled( glPlane );
        }
        
        final float[] color = new float[ 4 ];
        floatBuffer.rewind();
        gl.glGetFloatv( GL.GL_CURRENT_COLOR, floatBuffer );
        color[ 0 ] = floatBuffer.get(); color[ 1 ] = floatBuffer.get(); color[ 2 ] = floatBuffer.get(); color[ 3 ] = 1f - floatBuffer.get();
        
        final boolean[] lightEnabled = new boolean[]
        {
            gl.glIsEnabled( GL.GL_LIGHT0 ),
            gl.glIsEnabled( GL.GL_LIGHT1 ),
            gl.glIsEnabled( GL.GL_LIGHT2 ),
            gl.glIsEnabled( GL.GL_LIGHT3 ),
            gl.glIsEnabled( GL.GL_LIGHT4 ),
            gl.glIsEnabled( GL.GL_LIGHT5 ),
            gl.glIsEnabled( GL.GL_LIGHT6 ),
            gl.glIsEnabled( GL.GL_LIGHT7 )
        };
        
        update( gl.glIsEnabled( GL.GL_VERTEX_ARRAY ),
                gl.glIsEnabled( GL.GL_NORMAL_ARRAY ),
                gl.glIsEnabled( GL.GL_COLOR_ARRAY ),
                texCoordArrayEnabledMask,
                texGenEnableMask,
                texture1DEnabled,
                texture2DEnabled,
                texture3DEnabled,
                textureCMEnabled,
                vertexAttribsEnableMask,
                oldSelectedServerTU,
                oldSelectedClientTU,
                this.maxUsedVertexAttrib,
                colorWriteMask,
                depthWriteMask,
                ( AssemblyShaderProgramStateUnitPeer.areARBShaderProgramsSupported( gl ) ? gl.glIsEnabled( GL.GL_VERTEX_PROGRAM_ARB ) : false ),
                ( AssemblyShaderProgramStateUnitPeer.areARBShaderProgramsSupported( gl ) ? gl.glIsEnabled( GL.GL_FRAGMENT_PROGRAM_ARB ) : false ),
                currentGLSLShaderProgram,
                gl.glIsEnabled( GL.GL_DEPTH_TEST ),
                gl.glIsEnabled( GL.GL_ALPHA_TEST ),
                gl.glIsEnabled( GL.GL_STENCIL_TEST ),
                gl.glIsEnabled( GL.GL_SCISSOR_TEST ),
                clipPlaneEnabled,
                gl.glIsEnabled( GL.GL_BLEND ),
                gl.glIsEnabled( GL.GL_POINT_SMOOTH ),
                gl.glIsEnabled( GL.GL_LINE_STIPPLE ),
                gl.glIsEnabled( GL.GL_LINE_SMOOTH ),
                gl.glIsEnabled( GL.GL_POLYGON_SMOOTH ),
                gl.glIsEnabled( GL.GL_POLYGON_OFFSET_POINT ),
                gl.glIsEnabled( GL.GL_POLYGON_OFFSET_LINE ),
                gl.glIsEnabled( GL.GL_POLYGON_OFFSET_FILL ),
                gl.glIsEnabled( GL.GL_CULL_FACE ),
                gl.glIsEnabled( GL.GL_NORMALIZE),
                gl.glIsEnabled( GL.GL_COLOR_MATERIAL ),
                gl.glIsEnabled( GL.GL_LIGHTING ),
                lightEnabled,
                gl.glIsEnabled( GL.GL_FOG ),
                color
              );
    }
    
    public OpenGLStatesCacheImpl()
    {
        super();
    }
}
