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
import java.nio.IntBuffer;

import org.jagatoo.logging.ProfileTimer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentProgram;
import org.lwjgl.opengl.ARBVertexProgram;
import org.lwjgl.opengl.GL11;
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

/**
 * @author Abdul Bezrati
 * @author Marvin Froehlich (aka Qudus)
 */
public class AssemblyShaderProgramStateUnitPeer
{
    private static final IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer( 16 );
    
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
                    tmpIntBuffer.clear();
                    tmpIntBuffer.put( 0, name );
                    ARBVertexProgram.glDeleteProgramsARB( tmpIntBuffer );
                }
            } );
        }
    };
    
    private static boolean ARB_shader_programs_supported = false;
    
    private static boolean checkedOnce = false;
    
    private static final void checkOnce()
    {
        String extensions = GL11.glGetString( GL11.GL_EXTENSIONS );
        if ( ( extensions.indexOf( "GL_ARB_vertex_program" ) != -1 ) && ( extensions.indexOf( "GL_ARB_fragment_program" ) != -1 ) )
            ARB_shader_programs_supported = true;
        
        checkedOnce = true;
        
        if ( !ARB_shader_programs_supported )
        {
            //javax.swing.JOptionPane.showMessageDialog( null, "No Vertex/Fragment shaders support, skipping", "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
            //System.err.println( "No Vertex Program support, skipping" );
        }
    }
    
    protected static final boolean areARBShaderProgramsSupported()
    {
        if ( !checkedOnce )
            checkOnce();
        
        return ( ARB_shader_programs_supported );
    }
    
    private static final int defineVertexProgram( AssemblyVertexShader shaderProgram, SceneGraphOpenGLReference openGLRef )
    {
        tmpIntBuffer.clear();
        ARBVertexProgram.glGenProgramsARB( tmpIntBuffer );
        final int spHandle = tmpIntBuffer.get( 0 );
        openGLRef.setName( spHandle );
        
        ARBVertexProgram.glBindProgramARB( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB, spHandle );
        String program = shaderProgram.getShaderCode();
        final byte[] programBytes = program.getBytes();
        ByteBuffer programBuffer = BufferUtils.createByteBuffer( programBytes.length );
        programBuffer.put( programBytes ).flip();
        ARBVertexProgram.glProgramStringARB( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB, ARBVertexProgram.GL_PROGRAM_FORMAT_ASCII_ARB, programBuffer );
        
        String programErrorString = GL11.glGetString( ARBVertexProgram.GL_PROGRAM_ERROR_STRING_ARB );
        tmpIntBuffer.clear();
        GL11.glGetInteger( ARBVertexProgram.GL_PROGRAM_ERROR_POSITION_ARB, tmpIntBuffer );
        
        if ( tmpIntBuffer.get( 0 ) != -1 )
        {
            //javax.swing.JOptionPane.showMessageDialog( null, "Error String:" + programErrorString, "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
            X3DLog.error( "VP Error String:", programErrorString );
        }
        
        X3DLog.debug( "Binding Shader Program to handle " + spHandle );
        
        return ( spHandle );
    }
    
    private static final int defineFragmentProgram( AssemblyFragmentShader shaderProgram, SceneGraphOpenGLReference openGLRef )
    {
        tmpIntBuffer.clear();
        ARBFragmentProgram.glGenProgramsARB( tmpIntBuffer );
        final int spHandle = tmpIntBuffer.get( 0 );
        openGLRef.setName( spHandle );
        
        ARBFragmentProgram.glBindProgramARB( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, spHandle );
        String program = shaderProgram.getShaderCode();
        final byte[] programBytes = program.getBytes();
        ByteBuffer programBuffer = BufferUtils.createByteBuffer( programBytes.length );
        programBuffer.put( programBytes ).flip();
        ARBFragmentProgram.glProgramStringARB( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, ARBFragmentProgram.GL_PROGRAM_FORMAT_ASCII_ARB, programBuffer );
        
        String programErrorString = GL11.glGetString( ARBFragmentProgram.GL_PROGRAM_ERROR_STRING_ARB );
        tmpIntBuffer.clear();
        GL11.glGetInteger( ARBFragmentProgram.GL_PROGRAM_ERROR_POSITION_ARB, tmpIntBuffer );
        
        if ( tmpIntBuffer.get( 0 ) != -1 )
        {
            //javax.swing.JOptionPane.showMessageDialog( null, "Error String:" + programErrorString, "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
            System.err.println( "FP Error String:" + programErrorString );
        }

        X3DLog.debug( "Binding Shader Program to handle ", spHandle );
        
        return ( spHandle );
    }
    
    private static final void bindVertexProgram( OpenGLStatesCache statesCache, AssemblyVertexShader shaderProgram, CanvasPeer canvasPeer )
    {
        final SceneGraphOpenGLReference openGLRef = shaderProgram.getOpenGLReferences().getReference( canvasPeer, shaderProgramNameProvider );
        
        if ( !statesCache.enabled || !statesCache.assemblyVertexShadersEnabled )
        {
            GL11.glEnable( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB );
            statesCache.assemblyVertexShadersEnabled = true;
        }
        
        int spHandle = openGLRef.getName();
        
        if ( ( spHandle != -1 ) && _SG_PrivilegedAccess.isDirty( shaderProgram ) )
        {
            tmpIntBuffer.clear();
            tmpIntBuffer.put( 0, spHandle );
            ARBVertexProgram.glDeleteProgramsARB( tmpIntBuffer );
            _SG_PrivilegedAccess.setDirty( shaderProgram, false );
            
            spHandle = openGLRef.deleteName();
        }
        
        if ( spHandle != -1 )
        {
            X3DLog.debug( "Already cached, so binding Shader Program" );
            
            ARBVertexProgram.glBindProgramARB( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB, spHandle );
        }
        else
        {
            spHandle = defineVertexProgram( shaderProgram, openGLRef );
        }
        
        final Vector4f[] parameters = shaderProgram.getParameters();
        if ( parameters != null )
        {
            for ( int i = 0; i < parameters.length; i++ )
            {
                ARBVertexProgram.glProgramEnvParameter4fARB( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB, i, parameters[ i ].getX(), parameters[ i ].getY(), parameters[ i ].getZ(), parameters[ i ].getW() );
            }
        }
    }
    
    private static final void bindFragmentProgram( OpenGLStatesCache statesCache, AssemblyFragmentShader shaderProgram, CanvasPeer canvasPeer )
    {
        final SceneGraphOpenGLReference openGLRef = shaderProgram.getOpenGLReferences().getReference( canvasPeer, shaderProgramNameProvider );
        
        if ( !statesCache.enabled || !statesCache.assemblyFragmentShadersEnabled )
        {
            GL11.glEnable( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB );
            statesCache.assemblyFragmentShadersEnabled = true;
        }
        
        int spHandle = openGLRef.getName();
        
        if ( ( spHandle != -1 ) && _SG_PrivilegedAccess.isDirty( shaderProgram ) )
        {
            tmpIntBuffer.clear();
            tmpIntBuffer.put( 0, spHandle );
            ARBFragmentProgram.glDeleteProgramsARB( tmpIntBuffer );
            _SG_PrivilegedAccess.setDirty( shaderProgram, false );
            
            spHandle = openGLRef.deleteName();
        }
        
        if ( spHandle != -1 )
        {
            X3DLog.debug( "Already cached, so binding Shader Program" );
            
            ARBFragmentProgram.glBindProgramARB( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, spHandle );
        }
        else
        {
            spHandle = defineFragmentProgram( shaderProgram, openGLRef );
        }
        
        final Vector4f[] parameters = shaderProgram.getParameters();
        if ( parameters != null )
        {
            for ( int i = 0; i < parameters.length; i++ )
            {
                ARBFragmentProgram.glProgramEnvParameter4fARB( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, i, parameters[ i ].getX(), parameters[ i ].getY(), parameters[ i ].getZ(), parameters[ i ].getW() );
            }
        }
    }
    
    private static final void setVertexProgramState( OpenGLStatesCache statesCache, AssemblyVertexShader shaderProgram, CanvasPeer canvasPeer )
    {
        if ( !checkedOnce )
        {
            checkOnce();
        }
        
        if ( ARB_shader_programs_supported )
        {
            if ( shaderProgram == null || !shaderProgram.isEnabled() || shaderProgram.getShaderCode() == null )
            {
                if ( !statesCache.enabled || statesCache.assemblyVertexShadersEnabled )
                {
                    GL11.glDisable( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB );
                    statesCache.assemblyVertexShadersEnabled = false;
                }
            }
            else //if ( shaderProgram.getType() == ShaderType.VERTEX )
            {
                bindVertexProgram( statesCache, shaderProgram, canvasPeer );
            }
        }
    }
    
    private static final void setFragmentProgramState( OpenGLStatesCache statesCache, AssemblyFragmentShader shaderProgram, CanvasPeer canvasPeer )
    {
        if ( !checkedOnce )
        {
            checkOnce();
        }
        
        if ( ARB_shader_programs_supported )
        {
            if ( shaderProgram == null || !shaderProgram.isEnabled() || shaderProgram.getShaderCode() == null )
            {
                if ( !statesCache.enabled || statesCache.assemblyFragmentShadersEnabled )
                {
                    GL11.glDisable( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB );
                    statesCache.assemblyFragmentShadersEnabled = false;
                }
            }
            else //if ( shaderProgram.getType() == ShaderType.FRAGMENT )
            {
                bindFragmentProgram( statesCache, shaderProgram, canvasPeer );
            }
        }
    }
    
    protected static final void disableAssemblyShaders( OpenGLStatesCache statesCache )
    {
        if ( statesCache.enabled && ( !statesCache.assemblyVertexShadersEnabled && !statesCache.assemblyFragmentShadersEnabled ) )
            return;
        
        if ( !checkedOnce )
            checkOnce();
        
        
        if ( ARB_shader_programs_supported )
        {
            GL11.glDisable( ARBVertexProgram.GL_VERTEX_PROGRAM_ARB );
            GL11.glDisable( ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB );
        }
        
        statesCache.assemblyVertexShadersEnabled = false;
        statesCache.assemblyFragmentShadersEnabled = false;
    }
    
    public static final void apply( AssemblyShaderProgramContext shaderProgram, CanvasPeer canvasPeer, OpenGLStatesCache statesCache, RenderMode renderMode )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "AssemblyShaderProgramStateUnitPeer::apply()" );
        
        if ( !checkedOnce )
        {
            checkOnce();
        }
        
        
        if ( ( renderMode == RenderMode.NORMAL ) && ( shaderProgram.isEnabled() ) )
        {
            final AssemblyShaderProgram program = shaderProgram.getProgram();
            
            AssemblyVertexShader vertexProgram = null;
            if ( program.getNumVertexShaders() > 0 )
                vertexProgram = (AssemblyVertexShader)program.getVertexShader( 0 );
            
            AssemblyFragmentShader fragmentProgram = null;
            if ( program.getNumFragmentShaders() > 0 )
                fragmentProgram = (AssemblyFragmentShader)program.getFragmentShader( 0 );
            
            setVertexProgramState( statesCache, vertexProgram, canvasPeer );
            setFragmentProgramState( statesCache, fragmentProgram, canvasPeer );
        }
        else
        {
            disableAssemblyShaders( statesCache );
        }
        
        ProfileTimer.endProfile();
    }
}
