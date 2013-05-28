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
package org.xith3d.loaders.shaders.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import org.jagatoo.util.cache.ResourceCache;
import org.xith3d.scenegraph.Shader;
import org.xith3d.scenegraph.Shader.ShaderType;

/**
 * Loader base for Shader loaders.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ShaderLoader< T extends Shader >
{
    private ResourceCache< String, T > shaderCache;
    
    /** Stores the baseUrl for data files associated with the URL
     * passed into load(URL).*/
    private Stack< URL > baseURLs;
    
    /** Stores the basePath for data files associated with the file
     * passed into load(String).*/
    private Stack< String > basePaths;
    
    /**
     * This method sets the base URL name for data files associated with
     * the file.  The baseUrl should be null by default, which is an indicator
     * to the loader that it should look for any associated files starting
     * from the same place as the URL passed into the load(URL) method.
     * Note: Users of setBaseUrl() would then use load(URL)
     * as opposed to load(String).
     */
    public void setBaseURL( URL url )
    {
        if ( url == null )
        {
            baseURLs.clear();
        }
        else
        {
            baseURLs.push( url );
        }
    }
    
    protected void popBaseURL()
    {
        if ( !baseURLs.empty() )
        {
            baseURLs.pop();
        }
    }
    
    /**
     * @return the current base URL setting.
     */
    public URL getBaseURL()
    {
        if ( baseURLs.empty() )
            return ( null );
        
        return ( baseURLs.peek() );
    }
    
    /**
     * This method sets the base path name for data files associated with
     * the file.  The basePath should be null by default, which is an indicator
     * to the loader that it should look for any associated files starting
     * from the same directory as the file passed into the load(String)
     * method.
     * Note: Users of setBasePath() would then use load(String)
     * as opposed to load(URL).
     */
    public void setBasePath( String pathName )
    {
        if ( pathName == null )
        {
            basePaths.clear();
        }
        else
        {
            basePaths.push( pathName );
        }
    }
    
    protected void popBasePath()
    {
        if ( !basePaths.empty() )
        {
            basePaths.pop();
        }
    }
    
    /**
     * @return the current base path setting.
     */
    public String getBasePath()
    {
        if ( basePaths.empty() )
            return ( null );
        
        return ( basePaths.peek() );
    }
    
    protected boolean setBaseURLFromShaderURL( URL shaderURL ) throws MalformedURLException
    {
        URL oldBaseURL = getBaseURL();
        
        if ( oldBaseURL == null )
        {
            String resource = shaderURL.toExternalForm();
            int pos = resource.lastIndexOf( '/' );
            if ( pos >= 0 )
            {
                String baseResource = resource.substring( 0, pos + 1 );
                setBaseURL( new URL( baseResource ) );
                
                return ( true );
            }
        }
        
        return ( false );
    }
    
    protected boolean setBasePathFromShaderFile( String shaderFilename )
    {
        String oldBasePath = getBasePath();
        
        if ( oldBasePath == null )
        {
            int pos = shaderFilename.lastIndexOf( '/' );
            if ( pos < 0 )
                pos = shaderFilename.lastIndexOf( '\\' );
            if ( pos >= 0 )
            {
                String baseResource = shaderFilename.substring( 0, pos + 1 );
                setBasePath( baseResource );
                
                return ( true );
            }
        }
        
        return ( false );
    }
    
    /**
     * Retrieves the Shader from the cache, or <i>null</i>, if the Shader
     * was not cached.
     * 
     * @param cacheTag the tag to search the cache for
     */
    protected T getFromCache( String cacheTag )
    {
        return ( shaderCache.get( cacheTag ) );
    }
    
    /**
     * Stores the Shader into the cache by the given tag.
     * 
     * @param cacheTag the tag to assotiate the Shader within the cache
     */
    protected void cacheShader( String cacheTag, T shader )
    {
        shaderCache.put( cacheTag, shader );
    }
    
    /**
     * Retrives the Shader from the given reader.
     * 
     * @param reader the reader to load the Shader from
     * @param type the desired Shader type
     * 
     * @return the Shader object
     */
    public abstract T loadShader( Reader reader, ShaderType type ) throws IOException;
    
    /**
     * Retrives the Shader from the given InputStream.
     * 
     * @param in the InputStream to load the Shader from
     * @param type the desired Shader type
     * 
     * @return the Shader object
     */
    public abstract T loadShader( InputStream in, ShaderType type ) throws IOException;
    
    /**
     * Retrives the Shader from the given URL.
     * 
     * @param url the URL to load the Shader from
     * @param type the desired Shader type
     * 
     * @return the Shader object
     */
    public abstract T loadShader( URL url, ShaderType type ) throws IOException;
    
    /**
     * Retrives the Shader with the given name. Loading works in the following
     * order:<br>
     * If the Shader was already loaded and is still in the cache then the
     * existing Shader is returned. So don't modify Shaders returned by this
     * method.<br>
     * The Shader is loaded from the <i>basePath</i> or <i>basURL</i>.
     * 
     * @param name The name of the Shader
     * @param type The desired Shader type
     * 
     * @return the Shader object
     */
    public abstract T loadShader( String name, ShaderType type ) throws IOException;
    
    /**
     * Creates a Shader from the given String. The generated Shader is not
     * cached.
     * 
     * @param source The String that should get parsed
     * 
     * @return Shader A Shader object that is based on the current content of
     *         the given String
     */
    public abstract T loadShaderFromString( String source, ShaderType typ );
    
    protected ShaderLoader()
    {
        this.shaderCache = new ResourceCache< String, T >();
        this.baseURLs = new Stack< URL >();
        this.basePaths = new Stack< String >();
    }
    
    protected ShaderLoader( URL baseURL )
    {
        this();
        
        setBaseURL( baseURL );
    }
    
    protected ShaderLoader( String basePath )
    {
        this();
        
        setBasePath( basePath );
    }
}
