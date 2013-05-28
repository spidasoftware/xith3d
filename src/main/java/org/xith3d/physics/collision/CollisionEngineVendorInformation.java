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
package org.xith3d.physics.collision;

import java.net.URL;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.util.versioning.Version;

/**
 * This class provides getters for the basic information about the
 * CollisionEngine's vendor information.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CollisionEngineVendorInformation implements NamedObject
{
    private final String name;
    private final URL url;
    private final Version version;
    private final String info;
    
    /**
     * @return the name of the CollisionEngine
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * @return the URL of the CollisionEngine's website (if any)
     */
    public final URL getURL()
    {
        return ( url );
    }
    
    /**
     * @return the version information of the CollisionEngine
     */
    public final Version getVersion()
    {
        return ( version );
    }
    
    /**
     * @return some info about this CollisionEngine,
     * maybe a description, something...
     */
    public final String getInfo()
    {
        return ( info );
    }
    
    public CollisionEngineVendorInformation( String name, URL url, Version version, String info )
    {
        this.name = name;
        this.url = url;
        this.version = version;
        this.info = info;
    }
}
