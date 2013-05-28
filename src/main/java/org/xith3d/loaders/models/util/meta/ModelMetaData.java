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
package org.xith3d.loaders.models.util.meta;

import org.openmali.vecmath2.AxisAngle3f;
import org.xith3d.loaders.models.ModelLoader;

/**
 * Contains the meta data that can be associated with a model.
 * 
 * @author Andrew Hanson (aka Patheros)
 */
public class ModelMetaData
{
    protected static class Resource
    {
        protected String name;
        protected Type type = Type.relative;
        
        protected static enum Type
        {
            base, relative
        }
    }
    
    protected static class LoadingFlags
    {
        protected boolean lightNodes;
        protected boolean fogNodes;
        protected boolean backgroundNodes;
        protected boolean behaviorNodes;
        protected boolean viewGroups;
        protected boolean soundNodes;
        protected boolean useDisplayLists;
        
        public int getFlags()
        {
            int retVal = 0;
            retVal |= lightNodes ? ModelLoader.LOAD_LIGHT_NODES : 0;
            retVal |= fogNodes ? ModelLoader.LOAD_FOG_NODES : 0;
            //retVal |= backgroundNodes ? ModelLoader.LOAD_BACKGROUND_NODES : 0;
            //retVal |= behaviorNodes ? ModelLoader.LOAD_BEHAVIOR_NODES : 0;
            retVal |= viewGroups ? ModelLoader.LOAD_CAMERAS : 0;
            retVal |= soundNodes ? ModelLoader.LOAD_SOUND_NODES : 0;
            //retVal |= useDisplayLists ? ModelLoader.USE_DISPLAY_LISTS : 0;
            
            return retVal;
        }
        
        public void setFlag( int loadFlags )
        {
            lightNodes = ( loadFlags & ModelLoader.LOAD_LIGHT_NODES ) > 0;
            fogNodes = ( loadFlags & ModelLoader.LOAD_FOG_NODES ) > 0;
            //backgroundNodes = ( loadFlags & ModelLoader.LOAD_BACKGROUND_NODES ) > 0;
            //behaviorNodes = ( loadFlags & ModelLoader.LOAD_BEHAVIOR_NODES ) > 0;
            viewGroups = ( loadFlags & ModelLoader.LOAD_CAMERAS ) > 0;
            soundNodes = ( loadFlags & ModelLoader.LOAD_SOUND_NODES ) > 0;
            //useDisplayLists = ( loadFlags & ModelLoader.USE_DISPLAY_LISTS ) > 0;
        }
    }
    
    private Resource resource = new Resource();
    private AxisAngle3f rotation;
    private float scaling;
    private LoadingFlags loadingFlags = new LoadingFlags();
    
    // TODO Add skining
    
    /**
     * Sets the name of the resource to create a URL from.
     * 
     * @param resourceName
     */
    void setResourceName( String resourceName )
    {
        this.resource.name = resourceName;
    }
    
    /**
     * @return the name of the resource to create a URL from.
     */
    public final String getResourceName()
    {
        return ( resource.name );
    }
    
    void setResourceRefrenceBase()
    {
        this.resource.type = Resource.Type.base;
    }
    
    public final boolean isResourceRefrenceBase()
    {
        return ( resource.type == Resource.Type.base );
    }
    
    void setResourceRefrenceRelative()
    {
        this.resource.type = Resource.Type.relative;
    }
    
    public final boolean isResourceRefrenceRelative()
    {
        return ( resource.type == Resource.Type.relative );
    }
    
    public void setRotation( AxisAngle3f rotation )
    {
        this.rotation = rotation;
    }
    
    public final AxisAngle3f getRotation()
    {
        return ( rotation );
    }
    
    void setScaling( float scaling )
    {
        this.scaling = scaling;
    }
    
    public final float getScaling()
    {
        return ( scaling );
    }
    
    public final LoadingFlags getLoadingFlags()
    {
        return ( loadingFlags );
    }
}
