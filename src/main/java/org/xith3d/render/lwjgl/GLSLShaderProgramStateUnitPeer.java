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
import java.nio.IntBuffer;

import org.jagatoo.logging.ProfileTimer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.OpenGlExtensions;
import org.xith3d.render.SceneGraphOpenGLReference;
import org.xith3d.render.SceneGraphOpenGLReferences;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.scenegraph.GLSLContext;
import org.xith3d.scenegraph.GLSLFragmentShader;
import org.xith3d.scenegraph.GLSLParameters;
import org.xith3d.scenegraph.GLSLShader;
import org.xith3d.scenegraph.GLSLShaderProgram;
import org.xith3d.scenegraph.GLSLVertexShader;
import org.xith3d.scenegraph._SG_PrivilegedAccess;
import org.xith3d.scenegraph.Shader.ShaderType;
import org.xith3d.utility.debug.DebugStrings;
import org.xith3d.utility.logging.X3DLog;

/**
 * Created on Jul 7, 2006 by florian for project 'xith3d_glsl_shader_support'
 * 
 * @author Florian Hofmann (aka Goliat)
 * @author Marvin Froehlich (aka Qudus)
 */
public class GLSLShaderProgramStateUnitPeer
{
    private static boolean ARB_shader_objects_supported = false;
    
    private static ByteBuffer tmpByteBuffer = BufferUtils.createByteBuffer( 128 );
    private static FloatBuffer tmpFloatBuffer = BufferUtils.createFloatBuffer( 128 );
    private static IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer( 128 );
    
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
                    ARBShaderObjects.glDeleteObjectARB( name );
                }
            } );
        }
    };
    
    private static SceneGraphOpenGLReferences.Provider shaderNameProvider = new SceneGraphOpenGLReferences.Provider()
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
                    //ARBShaderObjects.glDeleteObjectARB( name );
                }
            } );
        }
    };
    
    private static boolean checkedOnce = false;
    
    /**
     * check if GL_ARB_fragment_shader and GL_ARB_vertex_shader are supported
     * 
     * @param gl
     */
    private static void checkOnce()
    {
        String extensions = GL11.glGetString( GL11.GL_EXTENSIONS );
        
        if ( extensions.indexOf( "GL_ARB_shader_objects" ) != -1 )
        {
            ARB_shader_objects_supported = true;
        }
        else
        {
            X3DLog.error( "GL_ARB_fragment_shader and/or GL_ARB_vertex_shader not supported, skipping" );
        }
        
        checkedOnce = true;
    }
    
    protected static final boolean areARBShaderObjectsSupported()
    {
        if ( !checkedOnce )
            checkOnce();
        
        return ( ARB_shader_objects_supported );
    }
    
    private static void compileShader( GLSLShader s, SceneGraphOpenGLReference shaderOpenGLRef )
    {
        // check if we tried to compile this before and failed
        if ( s.hasCompilationError() )
            return;
        
        X3DLog.debug( "compiling Shader:\n", s.getShaderCode(), "\n" );
        
        // create shader object
        final int shaderHandle;
        if ( s.getType() == ShaderType.VERTEX )
            shaderHandle = ARBShaderObjects.glCreateShaderObjectARB( ARBVertexShader.GL_VERTEX_SHADER_ARB );
        else
            shaderHandle = ARBShaderObjects.glCreateShaderObjectARB( ARBFragmentShader.GL_FRAGMENT_SHADER_ARB );
        
        // set glHandle
        shaderOpenGLRef.setName( shaderHandle );
        
        // create byte buffer for source
        if ( tmpByteBuffer.limit() < s.getShaderCode().length() )
        {
            tmpByteBuffer = BufferUtils.createByteBuffer( (int)( s.getShaderCode().length() * 1.5 ) );
        }
        tmpByteBuffer.clear();
        tmpByteBuffer.put( s.getShaderCode().getBytes() );
        tmpByteBuffer.flip();
        
        // set source for shader
        ARBShaderObjects.glShaderSourceARB( shaderHandle, tmpByteBuffer );
        
        // compile this shader
        ARBShaderObjects.glCompileShaderARB( shaderHandle );
        
        // check if everything went right:
        tmpIntBuffer.clear();
        ARBShaderObjects.glGetObjectParameterARB( shaderHandle, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB, tmpIntBuffer );
        
        if ( tmpIntBuffer.get() != GL11.GL_TRUE )
        {
            if ( GLSLContext.isDebuggingEnabled() )
            {
                tmpIntBuffer.clear();
                ARBShaderObjects.glGetObjectParameterARB( shaderHandle, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, tmpIntBuffer );
                final int length = tmpIntBuffer.get();
                
                if ( tmpByteBuffer.limit() < length )
                {
                    tmpByteBuffer = BufferUtils.createByteBuffer( (int)( length * 1.5 ) );
                }
                
                tmpIntBuffer.flip();
                tmpByteBuffer.clear();
                ARBShaderObjects.glGetInfoLogARB( shaderHandle, tmpIntBuffer, tmpByteBuffer );
                byte[] infoBytes = new byte[ length ];
                tmpByteBuffer.get( infoBytes );
                String errMsg = new String( infoBytes );
                String message = "Failed to compile GLSL Shader.\n" + "Message:\n" + errMsg + "\n" + "+++++++++++++++++++++++++++++++++++\n" + "Source:\n" + "+++++++++++++++++++++++++++++++++++\n" + DebugStrings.numerateLines( s.getShaderCode(), 1, 3 ) + "\n" + "+++++++++++++++++++++++++++++++++++\n";
                System.err.println( message );
                X3DLog.error( message );
            }
            
            s.setCompilationError( true );
            
            return;
        }
        
        X3DLog.debug( "done glHandle: ", shaderHandle );
        
        // set the compiled flag in the shader
        //s.setCompiled( true );
        _SG_PrivilegedAccess.setDirty( s, false );
    }
    
    private static void mapAttributes( GLSLShaderProgram shaderProgram, SceneGraphOpenGLReference shaderProgOpenGLRef )
    {
        if ( !OpenGlExtensions.GL_CUSTOM_VERTEX_ATTRIBUTES )
            return;
        
        for ( int i = 0; i < shaderProgram.getNumVertexShaders(); i++ )
        {
            GLSLVertexShader shader = (GLSLVertexShader)shaderProgram.getVertexShader( i );
            
            final int numAttribs = shader.getVertexAttributesCount();
            
            for ( int j = 0; j < numAttribs; j++ )
            {
                final int index = shader.getNthVertexAttributeIndex( j );
                final String name = shader.getVertexAttributeMapping( index );
                
                setupTempByteBuffer( name.length() + 1 );
                tmpByteBuffer.put( name.getBytes() );
                tmpByteBuffer.put( (byte)0 );
                tmpByteBuffer.flip();
                GL20.glBindAttribLocation( shaderProgOpenGLRef.getName(), index, tmpByteBuffer );
            }
        }
    }
    
    private static void linkShaderProgram( GLSLShaderProgram shaderProgram, CanvasPeer canvasPeer )
    {
        // check if we have tried to link this before
        if ( shaderProgram.hasLinkingError() )
            return;
        
        X3DLog.debug( "linking GLSL shader program: ", shaderProgram.getName() );
        
        final SceneGraphOpenGLReference shaderProgOpenGLRef = shaderProgram.getOpenGLReferences().getReference( canvasPeer, shaderProgramNameProvider );
        
        // create a program container
        final int glHandle = ARBShaderObjects.glCreateProgramObjectARB();
        
        // set the handle in the program
        shaderProgOpenGLRef.setName( glHandle );
        
        // attach all shaders (which were compiled before)
        for ( int i = 0; i < shaderProgram.getNumVertexShaders(); i++ )
        {
            GLSLVertexShader s = (GLSLVertexShader)shaderProgram.getVertexShader( i );
            
            // attach shader to program
            ARBShaderObjects.glAttachObjectARB( glHandle, s.getOpenGLReferences().getReference( canvasPeer, shaderNameProvider ).getName() );
        }
        for ( int i = 0; i < shaderProgram.getNumFragmentShaders(); i++ )
        {
            GLSLFragmentShader s = (GLSLFragmentShader)shaderProgram.getFragmentShader( i );
            
            // attach shader to program
            ARBShaderObjects.glAttachObjectARB( glHandle, s.getOpenGLReferences().getReference( canvasPeer, shaderNameProvider ).getName() );
        }
        
        // map the Vertex-Shader's Vertex-Attribute names to their indices.
        mapAttributes( shaderProgram, shaderProgOpenGLRef );
        
        // now we link the program
        ARBShaderObjects.glLinkProgramARB( glHandle );
        
        // check if we linked correctly
        //IntBuffer params = BufferUtils.createIntBuffer( 1 );
        tmpIntBuffer.clear();
        ARBShaderObjects.glGetObjectParameterARB( glHandle, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB, tmpIntBuffer );
        
        if ( tmpIntBuffer.get() != GL11.GL_TRUE )
        {
            X3DLog.error( "failed to link glsl program shader id:", glHandle );
            _SG_PrivilegedAccess.setGLSLShaderProgramLinkError( shaderProgram, true );
            
            return;
        }
        
        // give out some debuggin stuff
        X3DLog.debug( "done ... glHandle: ", glHandle );
        
        // set this to linked
        _SG_PrivilegedAccess.setGLSLShaderProgramLinked( shaderProgram, true );
    }
    
    private static void setupTempByteBuffer( int minCap )
    {
        if ( tmpByteBuffer.capacity() < minCap )
        {
            tmpByteBuffer = BufferUtils.createByteBuffer( (int)( minCap * 1.5 ) );
        }
        
        tmpByteBuffer.position( 0 );
        tmpByteBuffer.limit( tmpByteBuffer.capacity() );
    }
    
    private static void setupTempFloatBuffer( int minCap )
    {
        if ( tmpFloatBuffer.capacity() < minCap )
        {
            tmpFloatBuffer = BufferUtils.createFloatBuffer( (int)( minCap * 1.5 ) );
        }
        
        tmpFloatBuffer.position( 0 );
        tmpFloatBuffer.limit( tmpFloatBuffer.capacity() );
    }
    
    private static void setupTempIntBuffer( int minCap )
    {
        if ( tmpIntBuffer.capacity() < minCap )
        {
            tmpIntBuffer = BufferUtils.createIntBuffer( (int)( minCap * 1.5 ) );
        }
        
        tmpIntBuffer.position( 0 );
        tmpIntBuffer.limit( tmpIntBuffer.capacity() );
    }
    
    private static void applyUniformVariables( GLSLContext shaderProgram, CanvasPeer canvasPeer )
    {
        final GLSLShaderProgram program = shaderProgram.getProgram();
        final GLSLParameters params = shaderProgram.getUniformParameters();
        
        // check if we have tried to link this before
        if ( program.hasLinkingError() )
            return;
        
        if ( !params.hasUniformVars() )
            return;
        
        final SceneGraphOpenGLReference shaderProgOpenGLRef = program.getOpenGLReferences().getReference( canvasPeer, shaderProgramNameProvider );
        
        final int numFloatUniformVars = params.getNumUniformVarsFloat();
        
        // loop through the keys
        for ( int k = 0; k < numFloatUniformVars; k++ )
        {
            final String key = params.getFloatUniformVarName( k );
            
            // create a location for this key
            setupTempByteBuffer( key.length() + 1 );
            tmpByteBuffer.put( key.getBytes() );
            tmpByteBuffer.put( (byte)0 );
            tmpByteBuffer.flip();
            final int location = ARBShaderObjects.glGetUniformLocationARB( shaderProgOpenGLRef.getName(), tmpByteBuffer );
            
            // get value
            final float[] value = params.getUniformVarValueFloat( k );
            
            // send uniform var
            setupTempFloatBuffer( value.length );
            
            tmpFloatBuffer.put( value );
            tmpFloatBuffer.flip();
            
            switch ( params.getUniformVarBaseSizeFloat( k ) )
            {
                case 1:
                    ARBShaderObjects.glUniform1ARB( location, tmpFloatBuffer );
                    break;
                case 2:
                    ARBShaderObjects.glUniform2ARB( location, tmpFloatBuffer );
                    break;
                case 3:
                    ARBShaderObjects.glUniform3ARB( location, tmpFloatBuffer );
                    break;
                case 4:
                    ARBShaderObjects.glUniform4ARB( location, tmpFloatBuffer );
                    break;
                case 9:
                    ARBShaderObjects.glUniformMatrix3ARB( location, true, tmpFloatBuffer );
                    break;
                case 16:
                    ARBShaderObjects.glUniformMatrix4ARB( location, true, tmpFloatBuffer );
                    break;
            }
        }
        
        final int numIntUniformVars = params.getNumUniformVarsInt();
        
        // loop through the keys
        for ( int k = 0; k < numIntUniformVars; k++ )
        {
            final String key = params.getIntUniformVarName( k );
            
            // create a location for this key
            setupTempByteBuffer( key.length() + 1 );
            tmpByteBuffer.put( key.getBytes() );
            tmpByteBuffer.put( (byte)0 );
            tmpByteBuffer.flip();
            final int location = ARBShaderObjects.glGetUniformLocationARB( shaderProgOpenGLRef.getName(), tmpByteBuffer );
            
            // get value
            final int[] value = params.getUniformVarValueInt( k );
            
            // send uniform var
            setupTempIntBuffer( value.length );
            
            tmpIntBuffer.put( value );
            tmpIntBuffer.flip();
            
            switch ( params.getUniformVarBaseSizeInt( k ) )
            {
                case 1:
                    ARBShaderObjects.glUniform1ARB( location, tmpIntBuffer );
                    break;
                case 2:
                    ARBShaderObjects.glUniform2ARB( location, tmpIntBuffer );
                    break;
                case 3:
                    ARBShaderObjects.glUniform3ARB( location, tmpIntBuffer );
                    break;
                case 4:
                    ARBShaderObjects.glUniform4ARB( location, tmpIntBuffer );
                    break;
            }
        }
    }
    
    protected static final int getCurrentShaderProgram()
    {
        if ( !checkedOnce )
            checkOnce();
        
        if ( !ARB_shader_objects_supported )
        {
            return ( 0 );
        }
        
        return ( ARBShaderObjects.glGetHandleARB( ARBShaderObjects.GL_PROGRAM_OBJECT_ARB ) );
    }
    
    protected static final void disableGLSLShaders( OpenGLStatesCache statesCache )
    {
        if ( statesCache.enabled && statesCache.currentGLSLShaderProgram == 0 )
            return;
        
        if ( !checkedOnce )
            checkOnce();
        
        if ( ARB_shader_objects_supported )
        {
            ARBShaderObjects.glUseProgramObjectARB( 0 );
        }
        
        statesCache.currentGLSLShaderProgram = 0;
    }
    
    public static final void apply( GLSLContext shaderProgram, CanvasPeer canvasPeer, OpenGLStatesCache statesCache, RenderMode renderMode )
    {
        // start profiling
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "GLSLShaderProgramStateUnitPeer::apply()" );
        
        if ( renderMode != RenderMode.NORMAL )
        {
            disableGLSLShaders( statesCache );
            return;
        }
        
        // check if shaders are supported
        if ( !checkedOnce )
            checkOnce();
        
        final GLSLShaderProgram program = shaderProgram.getProgram();
        
        // check if this program was linked
        if ( shaderProgram.isEnabled() && !program.isLinked() )
        {
            // we have to link it ...
            // but first we have to check that all shaders got compiled
            for ( int i = 0; i < program.getNumVertexShaders(); i++ )
            {
                final GLSLShader s = program.getVertexShader( i );
                final SceneGraphOpenGLReference shaderOpenGLRef = s.getOpenGLReferences().getReference( canvasPeer, shaderNameProvider );
                
                // compile if necessary
                //if ( !s.isCompiled() )
                if ( _SG_PrivilegedAccess.isDirty( s ) )
                    compileShader( s, shaderOpenGLRef );
                
            }
            
            for ( int i = 0; i < program.getNumFragmentShaders(); i++ )
            {
                final GLSLShader s = program.getFragmentShader( i );
                final SceneGraphOpenGLReference shaderOpenGLRef = s.getOpenGLReferences().getReference( canvasPeer, shaderNameProvider );
                
                // compile if necessary
                //if ( !s.isCompiled() )
                if ( _SG_PrivilegedAccess.isDirty( s ) )
                    compileShader( s, shaderOpenGLRef );
                
            }
            
            // now we can link the shader program
            linkShaderProgram( program, canvasPeer );
        }
        
        if ( shaderProgram.isEnabled() )
        {
            // check if this program is linked
            // in cases of failed linking this can happen
            if ( program.isLinked() && ARB_shader_objects_supported )
            {
                final int shaderProgGLHandle = program.getOpenGLReferences().getReference( canvasPeer, shaderProgramNameProvider ).getName();
                X3DLog.debug( "Use GLSL program id ", shaderProgGLHandle );
                
                if ( !statesCache.enabled || statesCache.currentGLSLShaderProgram != shaderProgGLHandle )
                {
                    ARBShaderObjects.glUseProgramObjectARB( shaderProgGLHandle );
                    statesCache.currentGLSLShaderProgram = shaderProgGLHandle;
                }
                
                // apply uniform variables
                applyUniformVariables( shaderProgram, canvasPeer );
            }
        }
        else
        {
            disableGLSLShaders( statesCache );
        }
        
        // end profile
        ProfileTimer.endProfile();
    }
}
