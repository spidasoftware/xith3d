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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mathias 'cylab' Henze
 */
@SuppressWarnings("unchecked")
public class DefaultGridResourceProvider implements GridResourceProvider
{
	private List<GridResourceSpec> samplerSpecs;
    private List<GridResourceSpec> surfaceSpecs;
    
    protected DefaultGridResourceProvider()
    {
        
    }

    public DefaultGridResourceProvider(List<GridResourceSpec<GridSampler>> samplerSpecs, List<GridResourceSpec<GridSurface>> surfaceSpecs)
    {
        this.samplerSpecs= new ArrayList<GridResourceSpec>(samplerSpecs);
        this.surfaceSpecs= new ArrayList<GridResourceSpec>(surfaceSpecs);
    }

    protected void setSamplerSpecs( List<GridResourceSpec> samplerSpecs )
    {
        this.samplerSpecs = samplerSpecs;
    }

    protected void setSurfaceSpecs( List<GridResourceSpec> surfaceSpecs )
    {
        this.surfaceSpecs = surfaceSpecs;
    }

    
    public final GridSampler findSampler( float s1, float t1, float s2, float t2, int detail )
    {
        GridResourceSpec<GridSampler> spec = findResourceSpec(samplerSpecs, s1, t1, s2, t2, detail);
        // if a specific detail level was loaded, we assume it is loaded by the Terrain LOD-System, so we need to adjust the refCount
        if (detail != -1)
        {
            spec.addReference();
        }
        GridSampler sampler = spec.getCachedResource();
        if (sampler == null)
        {
            sampler = createSampler(spec);
            spec.setCachedResource(sampler);
        }
        return sampler;
    }

    public void releaseSampler( GridSampler sampler )
    {
        for(int i = 0; i < samplerSpecs.size(); i++)
        {
            GridResourceSpec spec= samplerSpecs.get(i);
            // only release the sampler, when the spec is unreferenced (release() return true)
            if(spec.getCachedResource() == sampler && spec.release())
            {
                sampler.release();
                return;
            }
        }
    }

    public final GridSurface findSurface( float s1, float t1, float s2, float t2, int detail )
    {
        GridResourceSpec<GridSurface> spec = findResourceSpec(surfaceSpecs, s1, t1, s2, t2, detail);
        // if a specific detail level was loaded, we assume it is loaded by the Terrain LOD-System, so we need to adjust the refCount
        if (detail != -1)
        {
            spec.addReference();
        }
        GridSurface surface = spec.getCachedResource();
        if (surface == null)
        {
            surface = createSurface(spec);
            spec.setCachedResource(surface);
        }
        return surface;
    }

    public void releaseSurface( GridSurface surface )
    {
        for(int i = 0; i < surfaceSpecs.size(); i++)
        {
            GridResourceSpec spec= surfaceSpecs.get(i);
            // only release the surface, when the spec is unreferenced (release() return true)
            if(spec.getCachedResource() == surface && spec.release())
            {
                surface.release();
                return;
            }
        }
    }
    
    protected GridResourceSpec findResourceSpec(List<GridResourceSpec> specs, float s1, float t1, float s2, float t2, int detail)
    {
        int handle= 0; // if no matching spec is found, just use the first one
        int matches[]= new int[32];
        if(detail>31) detail= 31;
        // first find matching ResurceSpec regardless of the detail level
        for(int i = 0; i < specs.size(); i++)
        {
            GridResourceSpec spec= specs.get(i);
            if(s1>=spec.getS1() && s2<=spec.getS2() && t1>=spec.getT1() && t2<=spec.getT2())
            {
                if(detail==-1 && spec.getCachedResource()==null) continue;
                int specDetail= spec.getDetail();
                if(specDetail>31) specDetail= 31;
                matches[spec.getDetail()]= i+1; // mark handle valid by avoiding 0
            }
        }
        // find the best valid spec by backward searching through the valid detail levels
        for(int i = detail==-1?31:detail; i >= 0; i--)
        {
            if( matches[i] > 0 )
            {
                handle = matches[i] - 1; // remove the validity mark
                break;
            }
        }
        return specs.get(handle);
    }
    
    protected GridSampler createSampler(GridResourceSpec<GridSampler> spec)
    {
        try
        {
            return new HeightMapSampler( spec );
        }
        catch( Exception ex )
        {
            Logger.getLogger( DefaultGridResourceProvider.class.getName()  ).log( Level.SEVERE, null, ex );
        }
        return null;
    }
    
    protected GridSurface createSurface(GridResourceSpec<GridSurface> spec)
    {
        try
        {
            return new TextureSurface(spec);
        }
        catch( Exception ex )
        {
            Logger.getLogger( DefaultGridResourceProvider.class.getName()  ).log( Level.SEVERE, null, ex );
        }
        return null;
    }
}
