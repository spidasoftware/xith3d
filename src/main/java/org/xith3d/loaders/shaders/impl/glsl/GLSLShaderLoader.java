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
package org.xith3d.loaders.shaders.impl.glsl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.xith3d.loaders.shaders.base.ShaderLoader;
import org.xith3d.scenegraph.Shader.ShaderType;
import org.xith3d.scenegraph.GLSLFragmentShader;
import org.xith3d.scenegraph.GLSLShader;
import org.xith3d.scenegraph.GLSLVertexShader;
import org.xith3d.utility.logging.X3DLog;

/**
 * Loads a GLSL shaders.
 * 
 * @author Florian Hofmann (ok ... i copied most from Matthias Mann)
 * @author Marvin Froehlich
 *
 * 14.12.2006 - fhofmann shader source is now displayed correctly in error messages
 */
public class GLSLShaderLoader extends ShaderLoader< GLSLShader >
{
    private static final GLSLShaderLoader instance = new GLSLShaderLoader();
    
    /**
     * This defines a mapping for placeholders in a shader source,
     * that are replaced by the provided values.
     * The value is converted to a String through the toString() method.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static final class InlineVariableMapping
    {
        private final String variableName;
        private final String searchString;
        private final Object value;
        private final String strValue;
        
        /**
         * Returns the variable name.
         * 
         * @return the variable name.
         */
        public final String getVariableName()
        {
            return ( variableName );
        }
        
        /**
         * Returns ${variableName}.
         * 
         * @return ${variableName}.
         */
        public final String getSearchString()
        {
            return ( searchString );
        }
        
        /**
         * Returns the raw value.
         * 
         * @return the raw value.
         */
        public final Object getValue()
        {
            return ( value );
        }
        
        /**
         * Returns the value converted to a String.
         * 
         * @return the value converted to a String.
         */
        public final String getValueAsString()
        {
            return ( strValue );
        }
        
        /**
         * Creates a new mapping.
         * 
         * @param variableName the name of the variabled as noted in the shader source.
         *        If this name is e.g. MY_VARIABLE, it is searched for as ${MY_VARIABLE}.
         *        Hence you must never use ${} for this name at this place.
         * @param value the raw value, which is converted to a String through the toString() method.
         */
        public InlineVariableMapping( String variableName, Object value )
        {
            if ( ( variableName == null ) || ( value == null ) )
            {
                throw new IllegalArgumentException( "Neither variableName nor value must be null." );
            }
            
            this.variableName = variableName;
            this.searchString = "${" + variableName + "}";
            this.value = value;
            this.strValue = value.toString();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public GLSLShader loadShader( Reader reader, ShaderType type, InlineVariableMapping... inlineVariables ) throws IOException
    {
        BufferedReader buffReader;
        
        if ( reader instanceof BufferedReader )
            buffReader = (BufferedReader)reader;
        else
            buffReader = new BufferedReader( reader );
        
        GLSLShader shader = null;
        
        // get shader source
        StringBuilder shaderSource = new StringBuilder();
        String line;
        try
        {
            while ( ( line = buffReader.readLine() ) != null )
            {
                int offset = 0;
                if ( inlineVariables != null )
                {
                    for ( int i = 0; i < inlineVariables.length; i++ )
                    {
                        InlineVariableMapping mapping = inlineVariables[i];
                        
                        int pos = line.indexOf( mapping.getSearchString(), offset );
                        if ( pos >= 0 )
                        {
                            shaderSource.append( line, offset, pos );
                            shaderSource.append( mapping.getValueAsString() );
                            offset = pos + mapping.getSearchString().length();
                        }
                    }
                }
                
                if ( line.indexOf( "${", offset ) >= 0 )
                    throw new Error( "Found unmapped inline variabled remaining in the shader source." );
                
                if ( offset > 0 )
                    shaderSource.append( line, offset, line.length() );
                else
                    shaderSource.append( line );
                
                shaderSource.append( '\n' );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        
        // check if we got a (more or less) valid file
        if ( shaderSource.length() == 0 )
        {
            X3DLog.exception( "failed to load shader \"...\"" );
            return ( null );
        }
        
        // create a shader from this source
        if ( type == ShaderType.FRAGMENT )
            shader = new GLSLFragmentShader( shaderSource.toString() );
        else
            shader = new GLSLVertexShader( shaderSource.toString() );
        
        // check again if we got something
        if ( shader == null )
        {
            X3DLog.exception( "failed to load shader \"...\"" );
            return ( null );
        }
        
        // return the shader
        return ( shader );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GLSLShader loadShader( Reader reader, ShaderType type ) throws IOException
    {
        return ( loadShader( reader, type, (InlineVariableMapping[])null ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public GLSLShader loadShader( InputStream in, ShaderType type, InlineVariableMapping... inlineVariables ) throws IOException
    {
        return ( loadShader( new InputStreamReader( in ), type, inlineVariables ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GLSLShader loadShader( InputStream in, ShaderType type ) throws IOException
    {
        return ( loadShader( new InputStreamReader( in ), type, (InlineVariableMapping[])null ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public GLSLShader loadShader( URL url, ShaderType type, InlineVariableMapping... inlineVariables ) throws IOException
    {
        return ( loadShader( url.openStream(), type, inlineVariables ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GLSLShader loadShader( URL url, ShaderType type ) throws IOException
    {
        return ( loadShader( url.openStream(), type, (InlineVariableMapping[])null ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GLSLShader loadShader( String name, ShaderType type ) throws IOException
    {
        // create a cache tag
        String cacheTag = type + name;
        
        // look up this shader in the cache
        GLSLShader shader = getFromCache( cacheTag );
        
        // if we have found a shader we just return it
        if ( shader != null )
            return ( shader );
        
        File shaderFile = new File( name );
        if ( shaderFile.exists() )
        {
            shader = loadShader( new FileReader( name ), type );
        }
        else
        {
            if ( getBaseURL() != null )
                shader = loadShader( new URL( getBaseURL(), name ).openStream(), type );
            else if ( getBasePath() != null )
                shader = loadShader( new FileReader( new File( getBasePath(), name ) ), type );
        }
        
        if ( shader == null )
        {
            X3DLog.error( "failed to load shader \"", name, "\"" );
            return ( null );
        }
        
        // store the shader in our cache
        cacheShader( cacheTag, shader );
        
        return ( shader );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param url
     *            The url of the Shader.
     *            
     * @return The Shader object
     */
    public GLSLVertexShader loadVertexShader( URL url, InlineVariableMapping... inlineVariables ) throws IOException
    {
        return ( (GLSLVertexShader)loadShader( url, ShaderType.VERTEX, inlineVariables ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param url
     *            The url of the Shader.
     *            
     * @return The Shader object
     */
    public GLSLVertexShader loadVertexShader( URL url ) throws IOException
    {
        return ( (GLSLVertexShader)loadShader( url, ShaderType.VERTEX, (InlineVariableMapping[])null ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param name
     *            The name of the Shader.
     *            
     * @return The Shader object
     */
    public GLSLVertexShader loadVertexShader( String name ) throws IOException
    {
        return ( (GLSLVertexShader)loadShader( name, ShaderType.VERTEX ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param url
     *            The url of the Shader.
     *            
     * @return The Shader object
     */
    public GLSLFragmentShader loadFragmentShader( URL url, InlineVariableMapping... inlineVariables ) throws IOException
    {
        return ( (GLSLFragmentShader)loadShader( url, ShaderType.FRAGMENT, inlineVariables ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param url
     *            The url of the Shader.
     *            
     * @return The Shader object
     */
    public GLSLFragmentShader loadFragmentShader( URL url ) throws IOException
    {
        return ( (GLSLFragmentShader)loadShader( url, ShaderType.FRAGMENT, (InlineVariableMapping[])null ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param name
     *            The name of the Shader.
     *            
     * @return The Shader object
     */
    public GLSLFragmentShader loadFragmentShader( String name ) throws IOException
    {
        return ( (GLSLFragmentShader)loadShader( name, ShaderType.FRAGMENT ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public GLSLShader loadShaderFromString( String source, ShaderType typ, InlineVariableMapping... inlineVariables )
    {
        if ( ( inlineVariables == null ) || ( inlineVariables.length == 0 ) || ( source.indexOf( "${" ) < 0 ) )
        {
            if ( typ == ShaderType.FRAGMENT )
                return ( new GLSLFragmentShader( source ) );
            
            return ( new GLSLVertexShader( source ) );
        }
        
        try
        {
            return ( loadShader( new StringReader( source ), typ, inlineVariables ) );
        }
        catch ( IOException e )
        {
            throw new Error( e );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GLSLShader loadShaderFromString( String source, ShaderType typ )
    {
        return ( loadShaderFromString( source, typ, (InlineVariableMapping[])null ) );
    }
    
    /**
     * Creates a Shader from the given String. The generated Shader is
     * not cached.
     * 
     * @param source
     *            The String that should get parsed
     *
     * @return Shader A Shader object that is based on the current content of
     *         the given String
     */
    public GLSLVertexShader loadVertexShaderFromString( String source, InlineVariableMapping... inlineVariables )
    {
        return ( (GLSLVertexShader)loadShaderFromString( source, ShaderType.VERTEX, inlineVariables ) );
    }
    
    /**
     * Creates a Shader from the given String. The generated Shader is
     * not cached.
     * 
     * @param source
     *            The String that should get parsed
     *
     * @return Shader A Shader object that is based on the current content of
     *         the given String
     */
    public GLSLVertexShader loadVertexShaderFromString( String source )
    {
        return ( (GLSLVertexShader)loadShaderFromString( source, ShaderType.VERTEX, (InlineVariableMapping[])null ) );
    }
    
    /**
     * Creates a Shader from the given String. The generated Shader is
     * not cached.
     * 
     * @param source
     *            The String that should get parsed
     *
     * @return Shader A Shader object that is based on the current content of
     *         the given String
     */
    public GLSLFragmentShader loadFragmentShaderFromString( String source, InlineVariableMapping... inlineVariables )
    {
        return ( (GLSLFragmentShader)loadShaderFromString( source, ShaderType.FRAGMENT, inlineVariables ) );
    }
    
    /**
     * Creates a Shader from the given String. The generated Shader is
     * not cached.
     * 
     * @param source
     *            The String that should get parsed
     *
     * @return Shader A Shader object that is based on the current content of
     *         the given String
     */
    public GLSLFragmentShader loadFragmentShaderFromString( String source )
    {
        return ( (GLSLFragmentShader)loadShaderFromString( source, ShaderType.FRAGMENT, (InlineVariableMapping[])null ) );
    }
    
    /**
     * Constructs a Loader with the specified baseURL.
     * 
     * @param baseURL the new baseURL to take resources from
     */
    public GLSLShaderLoader( URL baseURL )
    {
        super( baseURL );
    }
    
    /**
     * Constructs a Loader with the specified basePath.
     * 
     * @param basePath the new basePath to take resources from
     */
    public GLSLShaderLoader( String basePath )
    {
        super( basePath );
    }
    
    /**
     * Constructs a Loader with default values for all variables.
     */
    public GLSLShaderLoader()
    {
        super();
    }
    
    /**
     * @return the singleton instance of the TextureLoader
     */
    public static GLSLShaderLoader getInstance()
    {
        return ( instance );
    }
}
