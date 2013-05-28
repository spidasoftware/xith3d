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

import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.util.specific.MD3Tools;

/**
 * This is a ResourceRequest for an MD3 Model from multiple parts
 * loaded with ModelLoader.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MultipartMD3ModelResourceRequest implements ResourceRequest
{
    private final String nameHead;
    private final String nameTorso;
    private final String nameLegs;
    private final String mountHead;
    private final String mountTorso;
    private final String bagName;
    private final URL baseURL;
    private final String skin;
    private final float scale;
    private final int flags;
    
    /**
     * {@inheritDoc}
     */
    public final String getName()
    {
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getBagName()
    {
        return ( bagName );
    }
    
    /**
     * @return the base URL to use while loading (or <i>null</i> to get from model URL).
     */
    public final URL getBaseURL()
    {
        return ( baseURL );
    }
    
    /**
     * @return the name of the skin to be loaded.
     */
    public final String getSkin()
    {
        return ( skin );
    }
    
    /**
     * @return the scale, the Model will be loaded with.
     */
    public final float getScale()
    {
        return ( scale );
    }
    
    /**
     * @return the load flags to be used to load this Model.
     */
    public final int getLoadFlags()
    {
        return ( flags );
    }
    
    /**
     * {@inheritDoc}
     */
    public Object loadResource( ResourceLocator resLoc, ResourceBag resBag ) throws IOException
    {
        URL legs = new URL( getBaseURL(), nameLegs );
        URL torso = new URL( getBaseURL(), nameTorso );
        URL head = new URL( getBaseURL(), nameHead );
        
        Model model = MD3Tools.loadMultipartModel( legs, mountTorso, torso, mountHead, head, null, getSkin(), getScale() );
        
        resBag.addModel( getBagName(), model );
        
        return ( model );
    }
    
    /**
     * Creates a new ModelResourceRequest.
     * 
     * @param nameLegs the name of the requested Model resource
     * @param mountTorso the name of the requested Model resource
     * @param nameTorso the name of the requested Model resource
     * @param mountHead the name of the requested Model resource
     * @param nameHead the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param baseURL the base URL to use while loading (or <i>null</i> to get from model URL)
     * @param flags the load flags to be used to load this Model
     * @param skin the name of the skin to be loaded
     * @param scale the scale to load the Model with
     */
    public MultipartMD3ModelResourceRequest( String nameLegs, String mountTorso, String nameTorso, String mountHead, String nameHead, String bagName, URL baseURL, int flags, String skin, float scale )
    {
        this.nameHead = nameHead;
        this.nameTorso = nameTorso;
        this.nameLegs = nameLegs;
        this.mountHead = mountHead;
        this.mountTorso = mountTorso;
        this.bagName = bagName;
        this.baseURL = baseURL;
        this.flags = flags;
        this.skin = skin;
        this.scale = scale;
    }
    
    /**
     * Creates a new ModelResourceRequest.
     * 
     * @param nameLegs the name of the requested Model resource
     * @param mountTorso the name of the requested Model resource
     * @param nameTorso the name of the requested Model resource
     * @param mountHead the name of the requested Model resource
     * @param nameHead the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param baseURL the base URL to use while loading (or <i>null</i> to get from model URL)
     * @param flags the load flags to be used to load this Model
     * @param skin the name of the skin to be loaded
     */
    public MultipartMD3ModelResourceRequest( String nameLegs, String mountTorso, String nameTorso, String mountHead, String nameHead, String bagName, URL baseURL, int flags, String skin )
    {
        this( nameLegs, mountTorso, nameTorso, mountHead, nameHead, bagName, baseURL, flags, skin, 1.0f );
    }
    
    /**
     * Creates a new ModelResourceRequest.
     * 
     * @param nameLegs the name of the requested Model resource
     * @param mountTorso the name of the requested Model resource
     * @param nameTorso the name of the requested Model resource
     * @param mountHead the name of the requested Model resource
     * @param nameHead the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param baseURL the base URL to use while loading (or <i>null</i> to get from model URL)
     * @param flags the load flags to be used to load this Model
     * @param scale the scale to load the Model with
     */
    public MultipartMD3ModelResourceRequest( String nameLegs, String mountTorso, String nameTorso, String mountHead, String nameHead, String bagName, URL baseURL, int flags, float scale )
    {
        this( nameLegs, mountTorso, nameTorso, mountHead, nameHead, bagName, baseURL, flags, null, scale );
    }
    
    /**
     * Creates a new ModelResourceRequest.
     * 
     * @param nameLegs the name of the requested Model resource
     * @param mountTorso the name of the requested Model resource
     * @param nameTorso the name of the requested Model resource
     * @param mountHead the name of the requested Model resource
     * @param nameHead the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param baseURL the base URL to use while loading (or <i>null</i> to get from model URL)
     * @param flags the load flags to be used to load this Model
     */
    public MultipartMD3ModelResourceRequest( String nameLegs, String mountTorso, String nameTorso, String mountHead, String nameHead, String bagName, URL baseURL, int flags )
    {
        this( nameLegs, mountTorso, nameTorso, mountHead, nameHead, bagName, baseURL, flags, null );
    }
    
    /**
     * Creates a new ModelResourceRequest.
     * 
     * @param nameLegs the name of the requested Model resource
     * @param mountTorso the name of the requested Model resource
     * @param nameTorso the name of the requested Model resource
     * @param mountHead the name of the requested Model resource
     * @param nameHead the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param baseURL the base URL to use while loading (or <i>null</i> to get from model URL)
     */
    public MultipartMD3ModelResourceRequest( String nameLegs, String mountTorso, String nameTorso, String mountHead, String nameHead, String bagName, URL baseURL )
    {
        this( nameLegs, mountTorso, nameTorso, mountHead, nameHead, bagName, baseURL, -1, null );
    }
    
    /**
     * Creates a new ModelResourceRequest.
     * 
     * @param nameLegs the name of the requested Model resource
     * @param mountTorso the name of the requested Model resource
     * @param nameTorso the name of the requested Model resource
     * @param mountHead the name of the requested Model resource
     * @param nameHead the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     * @param scale the scale to load the Model with
     */
    public MultipartMD3ModelResourceRequest( String nameLegs, String mountTorso, String nameTorso, String mountHead, String nameHead, String bagName, float scale )
    {
        this( nameLegs, mountTorso, nameTorso, mountHead, nameHead, bagName, null, -1, null, scale );
    }
    
    /**
     * Creates a new ModelResourceRequest.
     * 
     * @param nameLegs the name of the requested Model resource
     * @param mountTorso the name of the requested Model resource
     * @param nameTorso the name of the requested Model resource
     * @param mountHead the name of the requested Model resource
     * @param nameHead the name of the requested Model resource
     * @param bagName the name of the requested resource, that it will carry in the ResourceBag
     */
    public MultipartMD3ModelResourceRequest( String nameLegs, String mountTorso, String nameTorso, String mountHead, String nameHead, String bagName )
    {
        this( nameLegs, mountTorso, nameTorso, mountHead, nameHead, bagName, null, -1, null );
    }
}
