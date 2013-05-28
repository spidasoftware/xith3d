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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jagatoo.loaders.textures.locators.TextureStreamLocator;
import org.jagatoo.loaders.textures.locators.TextureStreamLocatorURL;

/**
 * This is a "shortcut" for the singleton instance of ResourceLocator.
 * 
 * @see ResourceLocator
 * @see ResourceLocator#setSingletonInstance(ResourceLocator)
 * @see ResourceLocator#getInstance()
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ResLoc
{
    /**
     * @return the base-URL of this ResourceLocator
     */
    public static URL getBaseURL()
    {
        return ( ResourceLocator.getInstance().getBaseURL() );
    }
    
    /**
     * Creates a resource as a URL from this ResourceLocator. The given
     * resource-name must be relative to this instance's baseURL.
     * 
     * @param name the relative resource to complete with this
     *             instance's baseURL to an absolute one
     * @return the resource
     * 
     * @throws MalformedURLException if something was wrong with the resource
     */
    public static URL getResource( String name ) throws MalformedURLException
    {
        return ( ResourceLocator.getInstance().getResource( name ) );
    }
    
    /**
     * Creates a resource as a URL from this ResourceLocator. The given
     * resource-name must be relative to this instance's baseURL.
     * 
     * @param name the relative resource to complete with this
     *             instance's baseURL to an absolute one
     * @return an InputStream from the resource
     * 
     * @throws MalformedURLException if something was wrong with the resource
     * @throws IOException when the InputStream could not be created
     */
    public static InputStream getResourceAsStream( String name ) throws MalformedURLException, IOException
    {
        return ( ResourceLocator.getInstance().getResourceAsStream( name ) );
    }
    /**
     * Searches for all child resources in the current singleton ResourceLoator instance.
     * 
     * @param extension
     * @param recursively
     * @param foldersToo
     * 
     * @return a List of all found resources
     */
    public static List< URL > findAllResources( String extension, boolean recursively, boolean foldersToo )
    {
        return ( ResourceLocator.getInstance().findAllResources( extension, recursively, foldersToo ) );
    }
    
    /**
     * Creates a TextureStreamLocator to be added to the TextureLoader.
     * 
     * @param resourceName the relative resource to complete with this
     *                     instance's baseURL to an absolute one
     *                     It is taken as the constructor's argument for the TextureStreamLocatorURL.
     * @return the created TextureStreamLocatorURL to be added to the TextureLoader
     * 
     * @throws MalformedURLException if something was wrong with the resource
     */
    public static TextureStreamLocatorURL getTSL( String resourceName ) throws MalformedURLException
    {
        return ( ResourceLocator.getInstance().getTSL( resourceName ) );
    }
    
    /**
     * Creates a TextureStreamLocator to be added to the TextureLoader.
     * The result of getBaseURL() is taken as the constructor's argument for
     * the TextureStreamLocatorURL.
     * 
     * @return the created TextureStreamLocatorURL to be added to the TextureLoader
     */
    public static TextureStreamLocatorURL getTSL()
    {
        return ( ResourceLocator.getInstance().getTSL() );
    }
    
    /**
     * Creates a TextureStreamLocator and adds it to the TextureLoader.
     * 
     * @param resourceName the relative resource to complete with this
     *                     instance's baseURL to an absolute one
     *                     It is taken as the constructor's argument for the TextureStreamLocatorURL.
     * 
     * @throws MalformedURLException if something was wrong with the resource
     */
    public static TextureStreamLocator createAndAddTSL( String resourceName ) throws MalformedURLException
    {
        return ( ResourceLocator.getInstance().createAndAddTSL( resourceName ) );
    }
    
    /**
     * Creates a TextureStreamLocator and adds it to the TextureLoader.
     * The result of getBaseURL() is taken as the constructor's argument for
     * the TextureStreamLocatorURL.
     */
    public static TextureStreamLocator createAndAddTSL()
    {
        return ( ResourceLocator.getInstance().createAndAddTSL() );
    }
}
