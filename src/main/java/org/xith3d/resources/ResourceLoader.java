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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * The ResourceLoader loads requested resources and can update a progress.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ResourceLoader
{
    private static class RequestsSorter implements Comparator<ResourceRequest>
    {
        public static final RequestsSorter INSTANCE = new RequestsSorter();
        
        public int compare( ResourceRequest o1, ResourceRequest o2 )
        {
            return ( o1.getClass().getSimpleName().compareTo( o2.getClass().getSimpleName() ) );
        }
        
        public void sort( ArrayList<ResourceRequest> requests )
        {
            Collections.sort( requests, this );
        }
    }
    
    private ResourceLocator resLoc;
    
    private final ArrayList<ResourceRequest> requests = new ArrayList<ResourceRequest>();
    
    private final ArrayList<ResourceLoaderListener> listeners = new ArrayList<ResourceLoaderListener>();
    
    /**
     * Sets the ResourceLocator to be used for resource location.
     * 
     * @param resLoc
     */
    public void setResourceLocator( ResourceLocator resLoc )
    {
        this.resLoc = resLoc;
    }
    
    /**
     * @return the ResourceLocator to be used for resource location
     */
    public ResourceLocator getResourceLocator()
    {
        return ( resLoc );
    }
    
    public void addResourceLoaderListener( ResourceLoaderListener l )
    {
        listeners.add( l );
    }
    
    public void removeResourceLoaderListener( ResourceLoaderListener l )
    {
        listeners.remove( l );
    }
    
    /**
     * Adds a ResourceRequest to the queue.
     * 
     * @param resReq
     */
    public void addRequest( ResourceRequest resReq )
    {
        requests.add( resReq );
    }
    
    /**
     * @return the number of ResourceRequests queued in this ResourceLoader
     */
    public final int numRequests()
    {
        return ( requests.size() );
    }
    
    /**
     * Loads one Resource and adds it to the ResourceBag.
     * 
     * @throws Throwable
     */
    protected boolean loadResource( ResourceRequest resReq, ResourceBag resBag )
    {
        boolean success = true;
        
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).beforeResourceLoaded( this, resReq );
        }
        
        Object resource = null;
        
        try
        {
            resource = resReq.loadResource( resLoc, resBag );
        }
        catch ( Throwable t )
        {
            success = false;
            
            t.printStackTrace();
        }
        
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).afterResourceLoaded( this, resReq, resource );
        }
        
        return ( success );
    }
    
    /**
     * Loads all queued resources and puts them into a new ResourceBag.
     * 
     * @param lsu the LoadingScreenUpdater to notify of progress update
     * 
     * @return the filled ResourceBag
     */
    public ResourceBag loadResources( LoadingScreenUpdater lsu )
    {
        if ( lsu instanceof ResourceLoaderListener )
        {
            this.addResourceLoaderListener( (ResourceLoaderListener)lsu );
        }
        
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).beforeAnyResourceLoaded( this );
        }
        
        ResourceBag resBag = new ResourceBag();
        
        if ( lsu != null )
        {
            lsu.init( -numRequests(), null, (String)null );
        }
        
        if ( !requests.isEmpty() )
        {
            RequestsSorter.INSTANCE.sort( requests );
            
            Class<? extends ResourceRequest> lastRequestType = null;
            
            for ( int i = 0; i < requests.size(); i++ )
            {
                if ( requests.get( i ).getClass() != lastRequestType )
                {
                    for ( int j = 0; j < listeners.size(); j++ )
                    {
                        listeners.get( j ).beforeResourceBundleLoaded( this, requests.get( i ).getClass() );
                    }
                    
                    lastRequestType = requests.get( i ).getClass();
                }
                
                if ( lsu != null )
                    lsu.updateOnly( "Loading Texture resource \"" + requests.get( i ).getBagName() + "\"" );
                loadResource( requests.get( i ), resBag );
                if ( lsu != null )
                    lsu.update();
                
                if ( ( requests.size() > i + 1 ) && ( requests.get( i + 1 ).getClass() != lastRequestType ) )
                {
                    for ( int j = 0; j < listeners.size(); j++ )
                    {
                        listeners.get( j ).afterResourceBundleLoaded( this, requests.get( i + 1 ).getClass() );
                    }
                }
            }
        }
        
        if ( lsu != null )
            lsu.updateOnly( "ready" );
        
        for ( int i = 0; i < listeners.size(); i++ )
        {
            listeners.get( i ).afterAllResourceLoaded( this );
        }
        
        if ( lsu instanceof ResourceLoaderListener )
        {
            this.removeResourceLoaderListener( (ResourceLoaderListener)lsu );
        }
        
        return ( resBag );
    }
    
    /**
     * Loads all queued resources and puts them into a new ResourceBag.
     * 
     * @return the filled ResourceBag
     */
    public ResourceBag loadResources()
    {
        return ( loadResources( null ) );
    }
    
    public ResourceLoader( ResourceLocator resLoc )
    {
        this.resLoc = resLoc;
    }
    
    public ResourceLoader()
    {
        this( ResourceLocator.getInstance() );
    }
}
