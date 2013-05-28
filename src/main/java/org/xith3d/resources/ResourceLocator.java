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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jagatoo.loaders.textures.locators.TextureStreamLocator;
import org.jagatoo.loaders.textures.locators.TextureStreamLocatorURL;
import org.xith3d.loaders.texture.TextureLoader;

/**
 * The ResourceLocator serves as an abstraction layer to locate resources from
 * a base folder or base URL.<br>
 * Use the {@link #create(String)} method to create an instance and the {@link #getResource(String)}
 * or {@link #getResourceAsStream(String)} method to get child resources.<br>
 * You can also create sub-ResourceLocators to handle sub-locations more easily.<br>
 * If you want to use a specific instance as a singleton, you the {@link #setSingletonInstance(ResourceLocator)}
 * or {@link #useAsSingletonInstance()} method. Then you can also use the {@link ResLoc}
 * class to have shorter commands in your coding.
 * 
 * @see ResLoc
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ResourceLocator
{
    private static ResourceLocator singletonInstance = null;
    
    private URL baseURL;
    
    /**
     * If you want to use one ResourceLocator as a singleton, first invoke this
     * and then use the getInstance() method to access the instance everywhere.
     * 
     * @param resLoc the ResourceLocator instance to use as the singleton instance
     * 
     * @see #useAsSingletonInstance()
     * @see #getInstance()
     * @see ResLoc
     */
    public static void setSingletonInstance( ResourceLocator resLoc )
    {
        singletonInstance = resLoc;
    }
    
    /**
     * If you want to use one ResourceLocator as a singleton, first invoke this
     * and then use the getInstance() method to access the instance everywhere.
     * 
     * @see #setSingletonInstance(ResourceLocator)
     * @see #getInstance()
     * @see ResLoc
     */
    public void useAsSingletonInstance()
    {
        singletonInstance = this;
    }
    
    /**
     * If you want to use one ResourceLocator as a singleton, use this.
     * But remember to first (once) invoke the setSingletonInstance() method.
     * 
     * @see #setSingletonInstance(ResourceLocator)
     * @see ResLoc
     * 
     * @return the singleton instance (if already set)
     */
    public static ResourceLocator getInstance()
    {
        return ( singletonInstance );
    }
    
    /**
     * Sets the base-URL of this ResourceLocator.
     */
    protected void setBaseURL( URL baseURL )
    {
        this.baseURL = baseURL;
    }
    
    /**
     * Sets the baseURL. The given baseFolder is converted to
     * a URL and is taken as the base-URL of the new instance.
     * 
     * @param baseFolder
     * 
     * @throws FileNotFoundException if the folder does not exist
     * @throws IllegalArgumentException if the baseFolder is not a directory
     */
    protected void setBaseURL( File baseFolder ) throws FileNotFoundException, IllegalArgumentException
    {
        if ( !baseFolder.exists() )
            throw new FileNotFoundException( "The base-folder does not exist (\"" + baseFolder + "\")." );
        
        if ( !baseFolder.isDirectory() )
            throw new IllegalArgumentException( "The given base-folder is not a directory (\"" + baseFolder + "\")." );
        
        try
        {
            setBaseURL( baseFolder.toURI().toURL() );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the baseURL. The given baseFolder is converted to
     * a URL and is taken as the base-URL of the new instance.
     * 
     * @param foldername
     * 
     * @throws FileNotFoundException if the folder does not exist
     * @throws IllegalArgumentException if the baseFolder is not a directory
     */
    protected void setBaseURL( String foldername ) throws FileNotFoundException, IllegalArgumentException
    {
        setBaseURL( new File( foldername ) );
    }
    
    /**
     * @return the base-URL of this ResourceLocator
     */
    public URL getBaseURL()
    {
        return ( baseURL );
    }
    
    /**
     * Creates a new ResourceLocator instance with the sub-URL as its baseURL.
     * 
     * @param subResource the relative resource to complete with this
     *                    instance's baseURL to the new instance's baseURL
     * @return the new sub-ResourceLocator
     * 
     * @throws MalformedURLException if something was wrong with the resource
     */
    public ResourceLocator getSubLocator( String subResource ) throws MalformedURLException
    {
        subResource = subResource.replace( '\\', '/' );
        
        if ( !subResource.endsWith( "/" ) )
            subResource += "/";
        
        URL subURL = new URL( getBaseURL(), subResource );
        
        return ( new ResourceLocator( subURL ) );
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
    public URL getResource( String name ) throws MalformedURLException
    {
        name = name.replace( '\\', '/' );
        
        return ( new URL( getBaseURL(), name ) );
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
    public InputStream getResourceAsStream( String name ) throws MalformedURLException, IOException
    {
        URL resource = getResource( name );
        
        return ( resource.openStream() );
    }
    
    private final int findAllResources( JarEntry[] entries, int i0, String basePath, String extension, boolean recursively, boolean foldersToo, List< URL > resources )
    {
        for ( int i = i0; i < entries.length; i++ )
        {
            final JarEntry entry = entries[ i ];
            
            final boolean isCandidate;
            if ( recursively )
            {
                isCandidate = entry.getName().startsWith( basePath );
            }
            else if ( entry.isDirectory() )
            {
                isCandidate = entry.getName().equals( basePath );
            }
            else
            {
                final int lastSlashPos = entry.getName().lastIndexOf( '/' );
                
                isCandidate = entry.getName().substring( lastSlashPos ).equals( basePath );
            }
            
            if ( isCandidate )
            {
                if ( ( extension == null ) || ( entry.getName().endsWith( extension ) ) )
                {
                    if ( !entry.isDirectory() || foldersToo )
                    {
                        resources.add( ResourceLocator.class.getClassLoader().getResource( entry.getName() ) );
                    }
                }
                
                if ( entry.isDirectory() && recursively )
                {
                    i = findAllResources( entries, i + 1, entry.getName(), extension, recursively, foldersToo, resources );
                }
            }
            else if ( resources.size() > 0 )
            {
                return ( i + 1 );
            }
        }
        
        return ( Integer.MAX_VALUE - 100 );
    }
    
    private final void findAllResources( JarFile jarFile, String basePath, String extension, boolean recursively, boolean foldersToo, List< URL > resources )
    {
        final JarEntry[] entries = new JarEntry[ jarFile.size() ];
        
        Enumeration< JarEntry > jarEntries = jarFile.entries();
        int i = 0;
        while ( jarEntries.hasMoreElements() )
        {
            entries[ i++ ] = jarEntries.nextElement();
        }
        
        findAllResources( entries, 0, basePath, extension, recursively, foldersToo, resources );
    }
    
    private final void findAllResources( File baseFolder, String extension, boolean recursively, boolean foldersToo, List< URL > resources )
    {
        for ( File file: baseFolder.listFiles() )
        {
            if ( ( extension == null ) || ( file.getName().endsWith( extension ) ) )
            {
                if ( !file.isDirectory() || foldersToo )
                {
                    try
                    {
                        resources.add( file.toURI().toURL() );
                    }
                    catch ( MalformedURLException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
            
            if ( file.isDirectory() && recursively )
            {
                findAllResources( file, extension, recursively, foldersToo, resources );
            }
        }
    }
    
    /**
     * Searches for all child resources in this ResourceLoator.
     * 
     * @param extension
     * @param recursively
     * @param foldersToo
     * 
     * @return a List of all found resources
     */
    public List< URL > findAllResources( String extension, boolean recursively, boolean foldersToo )
    {
        ArrayList< URL > resources = new ArrayList< URL >();
        
        if ( this.getBaseURL().getProtocol().equals( "jar" ) )
        {
            if ( this.getBaseURL().getPath().startsWith( "file:" ) )
            {
                String jarFileName = this.getBaseURL().getPath().substring( 5, this.getBaseURL().getPath().indexOf( ".jar!" ) + 4 );
                String basePath = this.getBaseURL().getPath().substring( this.getBaseURL().getPath().indexOf( ".jar!" ) + 6 );
                
                try
                {
                    findAllResources( new JarFile( jarFileName ), basePath, extension, recursively, foldersToo, resources );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
        if ( this.getBaseURL().getProtocol().equals( "file" ) )
        {
            try
            {
                findAllResources( new File( this.getBaseURL().toURI() ), extension, recursively, foldersToo, resources );
            }
            catch ( URISyntaxException e )
            {
                e.printStackTrace();
            }
        }
        
        return ( resources );
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
    public TextureStreamLocatorURL getTSL( String resourceName ) throws MalformedURLException
    {
        resourceName = resourceName.replace( '\\', '/' );
        
        if ( !resourceName.endsWith( "/" ) )
            resourceName += "/";
        
        URL resource = getResource( resourceName );
        
        return ( new TextureStreamLocatorURL( resource ) );
    }
    
    /**
     * Creates a TextureStreamLocator to be added to the TextureLoader.
     * The result of getBaseURL() is taken as the constructor's argument for
     * the TextureStreamLocatorURL.
     * 
     * @return the created TextureStreamLocatorURL to be added to the TextureLoader
     */
    public TextureStreamLocatorURL getTSL()
    {
        return ( new TextureStreamLocatorURL( getBaseURL() ) );
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
    public TextureStreamLocator createAndAddTSL( String resourceName ) throws MalformedURLException
    {
        resourceName = resourceName.replace( '\\', '/' );
        
        if ( !resourceName.endsWith( "/" ) )
            resourceName += "/";
        
        URL resource = getResource( resourceName );
        
        TextureStreamLocator tsl = new TextureStreamLocatorURL( resource );
        
        TextureLoader.getInstance().addTextureStreamLocator( tsl );
        
        return ( tsl );
    }
    
    /**
     * Creates a TextureStreamLocator and adds it to the TextureLoader.
     * The result of getBaseURL() is taken as the constructor's argument for
     * the TextureStreamLocatorURL.
     */
    public TextureStreamLocator createAndAddTSL()
    {
        TextureStreamLocator tsl = new TextureStreamLocatorURL( getBaseURL() );
        
        TextureLoader.getInstance().addTextureStreamLocator( tsl );
        
        return ( tsl );
    }
    
    /**
     * Creates a new ResourceLocator instance with the given base-URL.<br>
     * No check for availability is done on the baseURL.
     * 
     * @param baseURL
     */
    public ResourceLocator( URL baseURL )
    {
        setBaseURL( baseURL );
    }
    
    /**
     * Creates a new ResourceLocator instance. The given baseFolder is converted to
     * a URL and is taken as the base-URL of the new instance.
     * 
     * @param baseFolder
     * 
     * @throws FileNotFoundException if the folder does not exist
     * @throws IllegalArgumentException if the baseFolder is not a directory
     */
    protected ResourceLocator( File baseFolder ) throws FileNotFoundException, IllegalArgumentException
    {
        setBaseURL( baseFolder );
    }
    
    /**
     * Creates a new ResourceLocator instance. The given baseFolder is converted to
     * a URL and is taken as the base-URL of the new instance.
     * 
     * @param foldername
     * 
     * @throws FileNotFoundException if the folder does not exist
     * @throws IllegalArgumentException if the baseFolder is not a directory
     */
    protected ResourceLocator( String foldername ) throws FileNotFoundException, IllegalArgumentException
    {
        setBaseURL( foldername );
    }
    
    /**
     * Creates a new ResourceLocator instance.
     * The given baseResource is first converted to a slash-only form.
     * If it doesn't end with '/', one slash is appended.
     * If a resource with this name can be retrieved from the ClassLoader, it
     * is taken as the baseURL for the new instance.
     * If the resource couldn't be retrieved, a file with that name is checked
     * for existance and for being a directory. If this is a hit, it is
     * converted to a URL and taken as the baseURL for the new instance.
     * 
     * Otherwise an IllegalArgumentException is thrown with more info.
     * 
     * @param dummy
     * @param baseResource
     * 
     * @throws IllegalArgumentException if something is wrong with the given baseResource
     */
    protected ResourceLocator( Object dummy, String baseResource )
    {
        baseResource = baseResource.replace( '\\', '/' );
        if ( !baseResource.endsWith( "/" ) )
            baseResource += "/";
         
        try
        {
            URL baseURL = ResourceLocator.class.getResource( baseResource );
            if ( baseURL != null )
            {
                setBaseURL( baseURL );
                return;
            }
            
            baseURL = ResourceLocator.class.getClassLoader().getResource( baseResource );
            if ( baseURL != null )
            {
                setBaseURL( baseURL );
                return;
            }
            
            File baseFile = new File( baseResource );
            if ( baseFile.exists() )
            {
                if ( !baseFile.isDirectory() )
                {
                    setBaseURL( new File( baseResource ).getParentFile() );
                    return;
                }
                
                setBaseURL( baseFile );
                return;
            }
            
            baseFile = new File( System.getProperty( "user.dir" ), baseResource );
            
            if ( baseFile.exists() )
            {
                if ( !baseFile.isDirectory() )
                {
                    setBaseURL( new File( baseResource ).getParentFile() );
                    return;
                }
                
                setBaseURL( new File( baseResource ) );
                return;
            }
            
            throw new IllegalArgumentException( "Resource not found \"" + baseResource + "\"" );
        }
        catch ( IllegalArgumentException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Resource not found \"" + baseResource + "\"", e );
        }
    }
    
    /**
     * Creates a new ResourceLocator instance.
     * The given baseResource is first converted to a slash-only form.
     * If it doesn't end with '/', one slash is appended.
     * If a resource with this name can be retrieved from the ClassLoader, it
     * is taken as the baseURL for the new instance.
     * If the resource couldn't be retrieved, a file with that name is checked
     * for existance and for being a directory. If this is a hit, it is
     * converted to a URL and taken as the baseURL for the new instance.
     * 
     * Otherwise an IllegalArgumentException is thrown with more info.
     * 
     * @param baseResource
     * 
     * @return the new created ResourceLocator instance
     * 
     * @throws IllegalArgumentException if something is wrong with the given baseResource
     */
    public static ResourceLocator create( String baseResource )
    {
        return ( new ResourceLocator( null, baseResource ) );
    }
}
