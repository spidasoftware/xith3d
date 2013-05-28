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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentProgram;
import org.lwjgl.opengl.ARBVertexProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.OpenGlExtensions;

/**
 * This extension is just used to initialize the states.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OpenGLStatesCacheImpl extends OpenGLStatesCache
{
    private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer( 16 );
    private static final ByteBuffer byteBuffer = BufferUtils.createByteBuffer( 16 );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void update( Object glObj, OpenGLCapabilities glCaps )
    {
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
            TextureUnitStateUnitPeer.selectServerTextureUnit( i, this, true );
            
            if ( GL11.glIsEnabled( GL11.GL_TEXTURE_COORD_ARRAY ) )
                texCoordArrayEnabledMask |= maskValue;
            
            texture1DEnabled[ i ] = GL11.glIsEnabled( GL11.GL_TEXTURE_1D );
            texture2DEnabled[ i ] = GL11.glIsEnabled( GL11.GL_TEXTURE_2D );
            texture3DEnabled[ i ] = GL11.glIsEnabled( GL12.GL_TEXTURE_3D );
            textureCMEnabled[ i ] = GL11.glIsEnabled( GL13.GL_TEXTURE_CUBE_MAP );
            
            maskValue *= 2;
            
            texGenEnableMask[ i ] = 0;
            if ( GL11.glIsEnabled( GL11.GL_TEXTURE_GEN_S ) )
                texGenEnableMask[ i ] |= 1;
            if ( GL11.glIsEnabled( GL11.GL_TEXTURE_GEN_T ) )
                texGenEnableMask[ i ] |= 2;
            if ( GL11.glIsEnabled( GL11.GL_TEXTURE_GEN_R ) )
                texGenEnableMask[ i ] |= 4;
            if ( GL11.glIsEnabled( GL11.GL_TEXTURE_GEN_Q ) )
                texGenEnableMask[ i ] |= 8;
        }
        
        TextureUnitStateUnitPeer.selectServerTextureUnit( oldSelectedServerTU, this, true );
        ShapeAtomPeer.selectClientTextureUnit( oldSelectedClientTU, this, true );
        
        final long vertexAttribsEnableMask = 0L;
        if ( OpenGlExtensions.GL_CUSTOM_VERTEX_ATTRIBUTES )
        {
            for ( int i = 0; i < glCaps.getMaxVertexAttributes(); i++ )
            {
                GL20.glDisableVertexAttribArray( i );
            }
        }
        
        byteBuffer.rewind();
        GL11.glGetBoolean( GL11.GL_COLOR_WRITEMASK, byteBuffer );
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
        GL11.glGetBoolean( GL11.GL_DEPTH_WRITEMASK, byteBuffer );
        boolean depthWriteMask = ( byteBuffer.get( 0 ) == 1 );
        
        final int currentGLSLShaderProgram = GLSLShaderProgramStateUnitPeer.getCurrentShaderProgram();
        
        final boolean[] clipPlaneEnabled = new boolean[ 6 ];
        for ( int i = 0; i < clipPlaneEnabled.length; i++ )
        {
            final int glPlane = RenderPeerImpl.translateClipPlaneIndex( i );
            
            clipPlaneEnabled[ i ] = GL11.glIsEnabled( glPlane );
        }
        
        final float[] color = new float[ 4 ];
        floatBuffer.rewind();
        GL11.glGetFloat( GL11.GL_CURRENT_COLOR, floatBuffer );
        color[ 0 ] = floatBuffer.get(); color[ 1 ] = floatBuffer.get(); color[ 2 ] = floatBuffer.get(); color[ 3 ] = 1f - floatBuffer.get();
        
        final boolean[] lightEnabled = new boolean[]
        {
            GL11.glIsEnabled( GL11.GL_LIGHT0 ),
            GL11.glIsEnabled( GL11.GL_LIGHT1 ),
            GL11.glIsEnabled( GL11.GL_LIGHT2 ),
            GL11.glIsEnabled( GL11.GL_LIGHT3 ),
            GL11.glIsEnabled( GL11.GL_LIGHT4 ),
            GL11.glIsEnabled( GL11.GL_LIGHT5 ),
            GL11.glIsEnabled( GL11.GL_LIGHT6 ),
            GL11.glIsEnabled( GL11.GL_LIGHT7 )
        };
        
        update( GL11.glIsEnabled( GL11.GL_VERTEX_ARRAY ),
                GL11.glIsEnabled( GL11.GL_NORMAL_ARRAY ),
                GL11.glIsEnabled( GL11.GL_COLOR_ARRAY ),
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
                ( AssemblyShaderProgramStateUnitPeer.areARBShaderProgramsSupported() ? GL11.glIsEnabled( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB ) : false ),
                ( AssemblyShaderProgramStateUnitPeer.areARBShaderProgramsSupported() ? GL11.glIsEnabled( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB ) : false ),
                currentGLSLShaderProgram,
                GL11.glIsEnabled( GL11.GL_DEPTH_TEST ),
                GL11.glIsEnabled( GL11.GL_ALPHA_TEST ),
                GL11.glIsEnabled( GL11.GL_STENCIL_TEST ),
                GL11.glIsEnabled( GL11.GL_SCISSOR_TEST ),
                clipPlaneEnabled,
                GL11.glIsEnabled( GL11.GL_BLEND ),
                GL11.glIsEnabled( GL11.GL_POINT_SMOOTH ),
                GL11.glIsEnabled( GL11.GL_LINE_STIPPLE ),
                GL11.glIsEnabled( GL11.GL_LINE_SMOOTH ),
                GL11.glIsEnabled( GL11.GL_POLYGON_SMOOTH ),
                GL11.glIsEnabled( GL11.GL_POLYGON_OFFSET_POINT ),
                GL11.glIsEnabled( GL11.GL_POLYGON_OFFSET_LINE ),
                GL11.glIsEnabled( GL11.GL_POLYGON_OFFSET_FILL ),
                GL11.glIsEnabled( GL11.GL_CULL_FACE ),
                GL11.glIsEnabled( GL11.GL_NORMALIZE),
                GL11.glIsEnabled( GL11.GL_COLOR_MATERIAL ),
                GL11.glIsEnabled( GL11.GL_LIGHTING ),
                lightEnabled,
                GL11.glIsEnabled( GL11.GL_FOG ),
                color
              );
    }
    
    public OpenGLStatesCacheImpl()
    {
        super();
    }
}
