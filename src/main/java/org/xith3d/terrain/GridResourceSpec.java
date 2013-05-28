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
package org.xith3d.terrain;

import java.net.URL;

/**
 * @author Mathias 'cylab' Henze
 */
public class GridResourceSpec<Type extends GridResource>
{
    private int detail;
    private int refCount;
    private URL[] locations;
    private float s1;
    private float t1;
    private float s2;
    private float t2;
    private float min;
    private float max;
    private Type cachedResource;

    public GridResourceSpec( int detail, URL location, float s1, float t1, float s2, float t2 )
    {
        this(detail,new URL[]{location}, s1, t1, s2, t2);
    }
    
    public GridResourceSpec( int detail, URL location, float s1, float t1, float s2, float t2, float min, float max )
    {
        this(detail,new URL[]{location}, s1, t1, s2, t2, min, max);
    }

    public GridResourceSpec( int detail, URL[] locations, float s1, float t1, float s2, float t2 )
    {
        this(detail, locations, s1, t1, s2,t2, Float.MAX_VALUE, Float.MIN_VALUE);
    }
    
    public GridResourceSpec( int detail, URL[] locations, float s1, float t1, float s2, float t2, float min, float max )
    {
        super();
        this.locations = locations;
        this.detail = detail;
        this.s1 = s1;
        this.t1 = t1;
        this.s2 = s2;
        this.t2 = t2;
        this.min = min;
        this.max = max;
        this.refCount = 0;
        this.cachedResource = null;
    }

    public void setDetail( int detail )
    {
        this.detail = detail;
    }

    public int getDetail()
    {
        return detail;
    }

    public URL[] getLocations()
    {
        return locations;
    }

    public int getRefCount()
    {
        return refCount;
    }

    public float getS1()
    {
        return s1;
    }

    public float getS2()
    {
        return s2;
    }

    public float getT1()
    {
        return t1;
    }

    public float getT2()
    {
        return t2;
    }

    public float getMax()
    {
        return max;
    }

    public float getMin()
    {
        return min;
    }

    public Type getCachedResource()
    {
        return cachedResource;
    }

    public void setCachedResource( Type cachedResource )
    {
        this.cachedResource = cachedResource;
    }

    public void addReference()
    {
        refCount++;
    }

    public boolean release()
    {
        refCount--;
        if( refCount < 0 )
        {
            throw new IllegalStateException( "Refcounter underflow! Probably a severe problem with the terrain resource handling!" );
        }
        if(refCount == 0)
        {
            cachedResource= null;
            return true;
        }
        return false;
    }
}
