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

import java.nio.IntBuffer;

import javax.media.opengl.GL;

import org.jagatoo.logging.ProfileTimer;
import org.openmali.vecmath2.Vector4f;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.SceneGraphOpenGLReference;
import org.xith3d.render.SceneGraphOpenGLReferences;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.scenegraph.AssemblyFragmentShader;
import org.xith3d.scenegraph.AssemblyShaderProgram;
import org.xith3d.scenegraph.AssemblyShaderProgramContext;
import org.xith3d.scenegraph.AssemblyVertexShader;
import org.xith3d.scenegraph._SG_PrivilegedAccess;
import org.xith3d.utility.logging.X3DLog;

import com.sun.opengl.util.BufferUtil;

/**
 * @author Abdul Bezrati
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 */
public class AssemblyShaderProgramStateUnitPeer
{
    private static final IntBuffer tmpIntBuffer = BufferUtil.newIntBuffer( 1 );
    private static final IntBuffer handleBuffer = BufferUtil.newIntBuffer( 1 );
    
    private static SceneGraphOpenGLReferences.Provider shaderProgramNameProvider = new SceneGraphOpenGLReferences.Provider()
    {
        public SceneGraphOpenGLReference newReference( CanvasPeer canvasPeer, SceneGraphOpenGLReferences references, int numNamesPerContext )
        {
            return ( new SceneGraphOpenGLReference( canvasPeer, references, numNamesPerContext )
            {
                @Override
                public void prepareObjectForDestroy()
                {
                    SceneGraphOpenGLReference ref = getReferences().removeReference( getContext().getCanvasID() );
                    
                    ( (CanvasPeerImplBase)getContext() ).addDestroyableObject( ref );
                }
                
                @Override
                public void destroyObject( int index, int name )
                {
                    final GL gl = ( (CanvasPeerImplBase)getContext() ).getGL();
                    
                    tmpIntBuffer.clear();
                    tmpIntBuffer.put( name );
                    tmpIntBuffer.rewind();
                    gl.glDeleteProgramsARB( 1, tmpIntBuffer );
                }
            } );
        }
    };
    
    private static boolean ARB_shader_programs_supported = false;
    
    private static boolean checkedOnce = false;
    
    private static final void checkOnce( GL gl )
    {
        String extensions = gl.glGetString( GL.GL_EXTENSIONS );
        if ( ( extensions.indexOf( "GL_ARB_vertex_program" ) != -1 ) && ( extensions.indexOf( "GL_ARB_fragment_program" ) != -1 ) )
            ARB_shader_programs_supported = true;
        
        checkedOnce = true;
        
        if ( !ARB_shader_programs_supported )
        {
            //javax.swing.JOptionPane.showMessageDialog( null, "No Vertex/Fragment shaders support, skipping", "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
            //X3DLog.debug( "No Fragment Program support, skipping" );
        }
    }
    
    protected static final boolean areARBShaderProgramsSupported( GL gl )
    {
        if ( !checkedOnce )
            checkOnce( gl );
        
        return ( ARB_shader_programs_supported );
    }
    
    private static final void defineVertexProgram( GL gl, AssemblyVertexShader shaderProgram, SceneGraphOpenGLReference openGLRef )
    {
        handleBuffer.rewind();
        gl.glGenProgramsARB( 1, handleBuffer );
        final int spHandle = handleBuffer.get();
        openGLRef.setName( spHandle );
        
        gl.glBindProgramARB( GL.GL_VERTEX_PROGRAM_ARB, spHandle );
        gl.glProgramStringARB( GL.GL_VERTEX_PROGRAM_ARB, GL.GL_PROGRAM_FORMAT_ASCII_ARB, shaderProgram.getShaderCode().length(), shaderProgram.getShaderCode() );
        
        String programErrorString = gl.glGetString( GL.GL_PROGRAM_ERROR_STRING_ARB );
        handleBuffer.rewind();
        gl.glGetIntegerv( GL.GL_PROGRAM_ERROR_POSITION_ARB, handleBuffer );
        int errorPos = handleBuffer.get();
        if ( errorPos != -1 )
        {
            //javax.swing.JOptionPane.showMessageDialog( null, "Error String:" + programErrorString, "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
            System.err.println( "VP Error String:" + programErrorString );
        }
        
        X3DLog.debug( "Binding Shader Program to handle ", spHandle );
    }
    
    private static final void defineFragmentProgram( GL gl, AssemblyFragmentShader shaderProgram, SceneGraphOpenGLReference openGLRef )
    {
        handleBuffer.clear();
        //handleBuffer.rewind();
        gl.glGenProgramsARB( 1, handleBuffer );
        final int spHandle = handleBuffer.get();
        openGLRef.setName( spHandle );
        
        gl.glBindProgramARB( GL.GL_FRAGMENT_PROGRAM_ARB, spHandle );
        gl.glProgramStringARB( GL.GL_FRAGMENT_PROGRAM_ARB, GL.GL_PROGRAM_FORMAT_ASCII_ARB, shaderProgram.getShaderCode().length(), shaderProgram.getShaderCode() );
        
        String programErrorString = gl.glGetString( GL.GL_PROGRAM_ERROR_STRING_ARB );
        IntBuffer errorPos = BufferUtil.newIntBuffer( 1 );
        gl.glGetIntegerv( GL.GL_PROGRAM_ERROR_POSITION_ARB, errorPos );
        
        if ( errorPos.get() != -1 )
        {
            //javax.swing.JOptionPane.showMessageDialog( null, "Error String:" + programErrorString, "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
            System.err.println( "FP Error String:" + programErrorString );
        }

        X3DLog.debug( "Binding Shader Program to handle ", spHandle );
    }
    
    private static final void bindVertexProgram( GL gl, OpenGLStatesCache statesCache, AssemblyVertexShader shaderProgram, CanvasPeer canvasPeer )
    {
        final SceneGraphOpenGLReference openGLRef = shaderProgram.getOpenGLReferences().getReference( canvasPeer, shaderProgramNameProvider );
        
        if ( !statesCache.enabled || !statesCache.assemblyVertexShadersEnabled )
        {
            gl.glEnable( GL.GL_VERTEX_PROGRAM_ARB );
            statesCache.assemblyVertexShadersEnabled = true;
        }
        
        int spHandle = openGLRef.getName();
        
        if ( ( spHandle != -1 ) && _SG_PrivilegedAccess.isDirty( shaderProgram ) )
        {
            handleBuffer.rewind();
            handleBuffer.put( spHandle );
            handleBuffer.rewind();
            gl.glDeleteProgramsARB( 1, handleBuffer );
            _SG_PrivilegedAccess.setDirty( shaderProgram, false );
            
            spHandle = openGLRef.deleteName();
        }
        
        if ( spHandle != -1 )
        {
            X3DLog.debug( "Already cached, so binding Shader Program" );
            gl.glBindProgramARB( GL.GL_VERTEX_PROGRAM_ARB, spHandle );
        }
        else
        {
            defineVertexProgram( gl, shaderProgram, openGLRef );
        }
        
        final Vector4f[] parameters = shaderProgram.getParameters();
        if ( parameters != null )
        {
            for ( int i = 0; i < parameters.length; i++ )
            {
                gl.glProgramEnvParameter4fARB( GL.GL_VERTEX_PROGRAM_ARB, i, parameters[ i ].getX(), parameters[ i ].getY(), parameters[ i ].getZ(), parameters[ i ].getW() );
            }
        }
    }
    
    private static final void bindFragmentProgram( GL gl, OpenGLStatesCache statesCache, AssemblyFragmentShader shaderProgram, CanvasPeer canvasPeer )
    {
        final SceneGraphOpenGLReference openGLRef = shaderProgram.getOpenGLReferences().getReference( canvasPeer, shaderProgramNameProvider );
        
        if ( !statesCache.enabled || !statesCache.assemblyFragmentShadersEnabled )
        {
            gl.glEnable( GL.GL_FRAGMENT_PROGRAM_ARB );
            statesCache.assemblyFragmentShadersEnabled = true;
        }
        
        int spHandle = openGLRef.getName();
        
        if ( ( spHandle != -1 ) && _SG_PrivilegedAccess.isDirty( shaderProgram ) )
        {
            handleBuffer.clear();
            //handleBuffer.rewind();
            handleBuffer.put( spHandle );
            handleBuffer.rewind();
            gl.glDeleteProgramsARB( 1, handleBuffer );
            _SG_PrivilegedAccess.setDirty( shaderProgram, false );
            
            spHandle = openGLRef.deleteName();
        }
        
        if ( spHandle != -1 )
        {
            X3DLog.debug( "Already cached, so binding Shader Program" );
            gl.glBindProgramARB( GL.GL_FRAGMENT_PROGRAM_ARB, spHandle );
        }
        else
        {
            defineFragmentProgram( gl, shaderProgram, openGLRef );
        }
        
        final Vector4f[] parameters = shaderProgram.getParameters();
        if ( parameters != null )
        {
            for ( int i = 0; i < parameters.length; i++ )
            {
                gl.glProgramEnvParameter4fARB( GL.GL_FRAGMENT_PROGRAM_ARB, i, parameters[ i ].getX(), parameters[ i ].getY(), parameters[ i ].getZ(), parameters[ i ].getW() );
            }
        }
    }
    
    private static final void setVertexProgramState( GL gl, OpenGLStatesCache statesCache, AssemblyVertexShader shaderProgram, CanvasPeer canvasPeer )
    {
        if ( !checkedOnce )
        {
            checkOnce( gl );
        }
        
        if ( ARB_shader_programs_supported )
        {
            if ( shaderProgram == null || !shaderProgram.isEnabled() || shaderProgram.getShaderCode() == null )
            {
                if ( !statesCache.enabled || statesCache.assemblyVertexShadersEnabled )
                {
                    gl.glDisable( GL.GL_VERTEX_PROGRAM_ARB );
                    statesCache.assemblyVertexShadersEnabled = false;
                }
            }
            else //if ( shaderProgram.getType() == ShaderType.VERTEX )
            {
                bindVertexProgram( gl, statesCache, shaderProgram, canvasPeer );
            }
        }
    }
    
    private static final void setFragmentProgramState( GL gl, OpenGLStatesCache statesCache, AssemblyFragmentShader shaderProgram, CanvasPeer canvasPeer )
    {
        if ( !checkedOnce )
        {
            checkOnce( gl );
        }
        
        if ( ARB_shader_programs_supported )
        {
            if ( shaderProgram == null || !shaderProgram.isEnabled() || shaderProgram.getShaderCode() == null )
            {
                if ( !statesCache.enabled || statesCache.assemblyFragmentShadersEnabled )
                {
                    gl.glDisable( GL.GL_FRAGMENT_PROGRAM_ARB );
                    statesCache.assemblyFragmentShadersEnabled = false;
                }
            }
            else //if ( shaderProgram.getType() == ShaderType.FRAGMENT )
            {
                bindFragmentProgram( gl, statesCache, shaderProgram, canvasPeer );
            }
        }
    }
    
    protected static final void disableAssemblyShaders( GL gl, OpenGLStatesCache statesCache )
    {
        if ( statesCache.enabled && ( !statesCache.assemblyVertexShadersEnabled && !statesCache.assemblyFragmentShadersEnabled ) )
            return;
        
        if ( !checkedOnce )
        {
            checkOnce( gl );
        }
        
        if ( ARB_shader_programs_supported )
        {
            gl.glDisable( GL.GL_VERTEX_PROGRAM_ARB );
            gl.glDisable( GL.GL_FRAGMENT_PROGRAM_ARB );
        }
        
        statesCache.assemblyVertexShadersEnabled = false;
        statesCache.assemblyFragmentShadersEnabled = false;
    }
    
    public static final void apply( Object glObj, AssemblyShaderProgramContext shaderProgram, CanvasPeer canvasPeer, OpenGLStatesCache statesCache, RenderMode renderMode )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "ShaderProgramPeer::apply()" );
        
        final GL gl = (GL)glObj;
        
        if ( ( renderMode == RenderMode.NORMAL ) && ( shaderProgram.isEnabled() ) )
        {
            final AssemblyShaderProgram program = shaderProgram.getProgram();
            
            AssemblyVertexShader vertexProgram = null;
            if ( program.getNumVertexShaders() > 0 )
                vertexProgram = (AssemblyVertexShader)program.getVertexShader( 0 );
            
            AssemblyFragmentShader fragmentProgram = null;
            if ( program.getNumFragmentShaders() > 0 )
                fragmentProgram = (AssemblyFragmentShader)program.getFragmentShader( 0 );
            
            setVertexProgramState( gl, statesCache, vertexProgram, canvasPeer );
            setFragmentProgramState( gl, statesCache, fragmentProgram, canvasPeer );
        }
        else
        {
            disableAssemblyShaders( gl, statesCache );
        }
        
        ProfileTimer.endProfile();
    }
}
