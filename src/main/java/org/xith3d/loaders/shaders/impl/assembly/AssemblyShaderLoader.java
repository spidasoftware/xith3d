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
package org.xith3d.loaders.shaders.impl.assembly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.xith3d.loaders.shaders.base.ShaderLoader;
import org.xith3d.scenegraph.Shader.ShaderType;
import org.xith3d.scenegraph.AssemblyFragmentShader;
import org.xith3d.scenegraph.AssemblyShader;
import org.xith3d.scenegraph.AssemblyVertexShader;
import org.xith3d.utility.logging.X3DLog;

/**
 * Loads a assembly shaders.
 * 
 * @author Marvin Froehlich
 */
public class AssemblyShaderLoader extends ShaderLoader< AssemblyShader >
{
    private static final AssemblyShaderLoader instance = new AssemblyShaderLoader();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AssemblyShader loadShader( Reader reader, ShaderType type ) throws IOException
    {
        BufferedReader buffReader;
        
        if ( reader instanceof BufferedReader )
            buffReader = (BufferedReader)reader;
        else
            buffReader = new BufferedReader( reader );
        
        AssemblyShader shader = null;
        
        // get shader source
        StringBuffer shaderSource = new StringBuffer();
        String line;
        try
        {
            while ( ( line = buffReader.readLine() ) != null )
            {
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
            shader = new AssemblyFragmentShader( shaderSource.toString() );
        else
            shader = new AssemblyVertexShader( shaderSource.toString() );
        
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
    public AssemblyShader loadShader( InputStream in, ShaderType type ) throws IOException
    {
        return ( loadShader( new InputStreamReader( in ), type ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AssemblyShader loadShader( URL url, ShaderType type ) throws IOException
    {
        //boolean baseURLWasNull = setBaseURLFromShaderURL( url );
        
        AssemblyShader shader = loadShader( url.openStream(), type );
        
        /*
        if (baseURLWasNull)
        {
            popBaseURL();
        }
        */
        
        return ( shader );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AssemblyShader loadShader( String name, ShaderType type ) throws IOException
    {
        // create a cache tag
        String cacheTag = type + name;
        
        // look up this shader in the cache
        AssemblyShader shader = getFromCache( cacheTag );
        
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
            X3DLog.exception( "failed to load shader \"", name, "\"" );
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
    public AssemblyVertexShader loadVertexShader( URL url ) throws IOException
    {
        return ( (AssemblyVertexShader)loadShader( url, ShaderType.VERTEX ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param name
     *            The name of the Shader.
     *            
     * @return The Shader object
     */
    public AssemblyVertexShader loadVertexShader( String name ) throws IOException
    {
        return ( (AssemblyVertexShader)loadShader( name, ShaderType.VERTEX ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param url
     *            The url of the Shader.
     *            
     * @return The Shader object
     */
    public AssemblyFragmentShader loadFragmentShader( URL url ) throws IOException
    {
        return ( (AssemblyFragmentShader)loadShader( url, ShaderType.FRAGMENT ) );
    }
    
    /**
     * Retrives the Shader with the given name.
     * 
     * @param name
     *            The name of the Shader.
     *            
     * @return The Shader object
     */
    public AssemblyFragmentShader loadFragmentShader( String name ) throws IOException
    {
        return ( (AssemblyFragmentShader)loadShader( name, ShaderType.FRAGMENT ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AssemblyShader loadShaderFromString( String source, ShaderType typ )
    {
        if ( typ == ShaderType.FRAGMENT )
            return ( new AssemblyFragmentShader( source ) );
        
        return ( new AssemblyVertexShader( source ) );
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
    public AssemblyVertexShader loadVertexShaderFromString( String source )
    {
        return ( (AssemblyVertexShader)loadShaderFromString( source, ShaderType.VERTEX ) );
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
    public AssemblyFragmentShader loadFragmentShaderFromString( String source )
    {
        return ( (AssemblyFragmentShader)loadShaderFromString( source, ShaderType.FRAGMENT ) );
    }
    
    /**
     * Constructs a Loader with the specified baseURL.
     * 
     * @param baseURL the new baseURL to take resources from
     */
    public AssemblyShaderLoader( URL baseURL )
    {
        super( baseURL );
    }
    
    /**
     * Constructs a Loader with the specified basePath.
     * 
     * @param basePath the new basePath to take resources from
     */
    public AssemblyShaderLoader( String basePath )
    {
        super( basePath );
    }
    
    /**
     * Constructs a Loader with default values for all variables.
     */
    public AssemblyShaderLoader()
    {
        super();
    }
    
    /**
     * @return the singleton instance of the TextureLoader
     */
    public static AssemblyShaderLoader getInstance()
    {
        return ( instance );
    }
}
