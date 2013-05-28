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
package org.xith3d.effects.atmosphere;

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.Updater;
import org.xith3d.scenegraph.PointLight;
import org.xith3d.scenegraph.primitives.Sphere;

/**
 * This factory generates atmospheric effects for spheres.
 * 
 * @author Yoann Meste (aka Mancer)
 */
public abstract class AtmosphereFactory implements Updatable
{
    private Sphere sphere;
	private PointLight light;
    
    private Tuple3f wavelength3 = new Tuple3f( 0.65f, 0.570f, 0.475f );
	
    public void setSphere( Sphere sphere )
    {
        this.sphere = sphere;
    }
    
    public final Sphere getSphere()
    {
        return ( sphere );
    }
    
    public void setLight( PointLight light )
    {
        this.light = light;
    }
    
    public final PointLight getLight()
    {
        return ( light );
    }
    
    protected final Point3f getLightPos( Point3f lightPos )
    {
        light.getComputedLocation( lightPos );
        
        return ( lightPos );
    }
    
    public void setWavelength3( Tuple3f wavelength3 )
    {
        this.wavelength3 = wavelength3;
    }
    
    public final Tuple3f getWavelength3()
    {
        return ( wavelength3 );
    }
    
    protected abstract void prepareAtmosphere( Sphere sphere, float atmosphereRadius, PointLight light );
    
    public final void prepareAtmosphere( Sphere sphere, float atmosphereRadius, PointLight light, Updater updater )
    {
        setSphere( sphere );
        setLight( light );
        
        prepareAtmosphere( sphere, atmosphereRadius, light );
        
        updater.addUpdatable( this );
    }
    
    protected AtmosphereFactory()
    {
    }
}
