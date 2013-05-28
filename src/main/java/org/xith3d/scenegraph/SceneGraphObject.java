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
package org.xith3d.scenegraph;

import java.util.HashMap;
import java.util.Map;

import org.jagatoo.datatypes.NamableObject;

/**
 * The base class for all objects contained in a scene graph.
 * 
 * @author Scott Shaver
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SceneGraphObject implements NamableObject
{
    /**
     * True if the object is live in a scene graph
     */
    private boolean live = false;
    
    /**
     * User specific data for this SceneGraphObject. It is not used by the
     * scene graph system.
     */
    private Map< Object, Object > userData = null;
    
    /**
     * This is the key used to store the user data from the setUserData(Object)
     * method. Used to maintain Java3D compatability. 
     *
     * Note that Object-type key is used to guarantee that conventional 
     * (old) user data namespace will never intersect with other user-defined namespaces.
     */
    protected static final Object XITH3D_USERDATAKEY_OLDUSERDATA = new Object();
    
    private String name = "";
    
    /**
     * Sets the live state of the object
     * 
     * @param live
     * 
     * @return <code>true</code>, if the state has changed
     */
    protected boolean setLive( boolean live )
    {
        if ( live == this.live )
            return ( false );
        
        this.live = live;
        
        return ( true );
    }
    
    /**
     * Is the object live in a scene graph.
     */
    public final boolean isLive()
    {
        return ( live );
    }
    
    /**
     * Get the Map of meta-data for the object.
     * 
     * @return The Map of meta-data for this object.
     */
    public Map< Object, Object > getUserDataMap()
    {
        if ( userData == null )
        {
            userData = new HashMap< Object, Object >();
        }
        
        return ( userData );
    }
    
    /**
     * Set a meta-data value into the user data container.
     * 
     * @param key The key to use to lookup the value.
     * @param value The value associated with the key.
     */
    public void setUserData( Object key, Object value )
    {
        if ( userData == null )
            userData = new HashMap< Object, Object >();
        
        userData.put( key, value );
    }
    
    /**
     * Set a meta-data value into the user data container, recursively
     * into all its children
     * 
     * @param key The key to use to lookup the value.
     * @param value The value associated with the key.
     */
    public static void setUserDataRecursive( SceneGraphObject object, Object key, Object value )
    {
        object.setUserData( key, value );
        
        if ( object instanceof GroupNode )
        {
            final GroupNode group = (GroupNode)object;
            final int n = group.numChildren();
            for ( int i = 0; i < n; i++ )
            {
                setUserDataRecursive( group.getChild( i ), key, value );
            }
        }
    }
    
    /**
     * Sets a meta-data value into the user data container, recursively
     * into all its children
     * 
     * @param key The key to use to lookup the value.
     * @param value The value associated with the key.
     */
    public void setUserDataRecursive( Object key, Object value )
    {
        setUserDataRecursive( this, key, value );
    }
    
    /**
     * Gets the Object associated with the given key.
     * 
     * @return The object associated with the given key or null if not found.
     */
    public Object getUserData( Object key )
    {
        if ( userData == null )
            return ( null );
        
        return ( userData.get( key ) );
    }
    
    /**
     * Set user specific data for this SceneGraphObject.
     */
    public void setUserData( Object userData )
    {
        setUserData( XITH3D_USERDATAKEY_OLDUSERDATA, userData );
    }
    
    /**
     * Get user specific data for this SceneGraphObject.
     */
    public Object getUserData()
    {
        return ( getUserData( XITH3D_USERDATAKEY_OLDUSERDATA ) );
    }
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( ( getName() != null ) && ( getName().length() > 0 ) )
            return ( this.getClass().getName() + " \"" + getName() + "\"" );
        
        return ( super.toString() );
    }
    
    /**
     * Constructs a new SceneGraphObject.
     */
    public SceneGraphObject()
    {
    }
}
