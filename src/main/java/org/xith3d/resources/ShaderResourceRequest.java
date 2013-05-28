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
package org.xith3d.resources;

import java.io.IOException;
import java.net.URL;

import org.xith3d.loaders.shaders.base.ShaderLoader;
import org.xith3d.scenegraph.Shader;
import org.xith3d.scenegraph.Shader.ShaderType;

/**
 * This is a ResourceRequest for a Shader resource to be loaded with
 * ShaderLoader.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ShaderResourceRequest implements ResourceRequest
{
    private final String name;
    private final String bagName;
    private final ShaderType type;
    private final URL baseURL;
    private final ShaderLoader< ? > loader;
    
    /**
     * {@inheritDoc}
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getBagName()
    {
        return ( bagName );
    }
    
    /**
     * @return the load flags to be used to load this Model
     */
    public final ShaderType getType()
    {
        return ( type );
    }
    
    /**
     * @return the base URL to use while loading (or <i>null</i> to get from model URL)
     */
    public final URL getBaseURL()
    {
        return ( baseURL );
    }
    
    /**
     * @return the ShaderLoader used to load this Shader resource
     */
    public final ShaderLoader< ? > getShaderLoader()
    {
        return ( loader );
    }
    
    /**
     * {@inheritDoc}
     */
    public Object loadResource( ResourceLocator resLoc, ResourceBag resBag ) throws IOException
    {
        ShaderLoader< ? > loader = getShaderLoader();
        
        final URL tmpURL = loader.getBaseURL();
        if ( getBaseURL() != null )
            loader.setBaseURL( getBaseURL() );
        
        Shader shader = loader.loadShader( resLoc.getResource( getName() ), getType() );
        
        resBag.addShader( getBagName(), shader );
        
        if ( getBaseURL() != null )
            loader.setBaseURL( tmpURL );
        
        return ( shader );
    }
    
    /**
     * Creates a new ShaderResourceRequest.
     * 
     * @param name the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param type the ShaderType to use
     * @param baseURL the base URL to use while loading (or <i>null</i> to get from model URL)
     * @param loader the ShaderLoader used to load this Sahder resource (or <i>null</i> to use the ExtensionLoader)
     */
    public ShaderResourceRequest( String name, String bagName, ShaderType type, URL baseURL, ShaderLoader< ? > loader )
    {
        this.name = name;
        this.bagName = bagName;
        this.type = type;
        this.baseURL = baseURL;
        this.loader = loader;
    }
    
    /**
     * Creates a new ShaderResourceRequest.
     * 
     * @param name the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param type the ShaderType to use
     * @param loader the ShaderLoader used to load this Sahder resource (or <i>null</i> to use the ExtensionLoader)
     */
    public ShaderResourceRequest( String name, String bagName, ShaderType type, ShaderLoader< ? > loader )
    {
        this( name, bagName, type, null, loader );
    }
}
