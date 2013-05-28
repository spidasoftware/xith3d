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
package org.xith3d.effects;

import org.xith3d.effects.atmosphere.AtmosphereFactory;
import org.xith3d.effects.atmosphere.GLSLAtmosphereFactory;
import org.xith3d.effects.bloom.BloomFactory;
import org.xith3d.effects.bloom.GLSLBloomFactory;
import org.xith3d.effects.bumpmapping.BumpMappingFactory;
import org.xith3d.effects.bumpmapping.GLSLBumpMappingFactory;
import org.xith3d.effects.celshading.AssemblyCelShadingFactory;
import org.xith3d.effects.celshading.CelShadingFactory;
import org.xith3d.effects.shadows.GLSLShadowMappingFactory;
import org.xith3d.effects.shadows.ShadowFactory;
import org.xith3d.effects.textureprojection.FixedFuncTextureProjectionFectory;
import org.xith3d.effects.textureprojection.TextureProjectionFactory;

/**
 * The {@link EffectFactory} is a registry class, where you can register
 * certain known factories for certain known effects.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class EffectFactory
{
    private static EffectFactory INSTANCE = new EffectFactory();
    
    private BumpMappingFactory bumpMappingFactory = null;
    private CelShadingFactory celShadingFactory = null;
    private TextureProjectionFactory texProjFactory = null;
    private ShadowFactory shadowFactory = null;
    private AtmosphereFactory atmosphereFactory = null;
    private BloomFactory bloomFactory = null;
    
    public static final void setInstance( EffectFactory factory )
    {
        INSTANCE = factory;
    }
    
    public static final EffectFactory getInstance()
    {
        return ( INSTANCE );
    }
    
    public void registerBumpMappingFactory( BumpMappingFactory factory )
    {
        this.bumpMappingFactory = factory;
    }
    
    public final BumpMappingFactory getBumpMappingFactory()
    {
        return ( bumpMappingFactory );
    }
    
    public void registerCelShadingFactory( CelShadingFactory factory )
    {
        this.celShadingFactory = factory;
    }
    
    public final CelShadingFactory getCelShadingFactory()
    {
        return ( celShadingFactory );
    }
    
    public void registerTextureProjectionFactory( TextureProjectionFactory factory )
    {
        this.texProjFactory = factory;
    }
    
    public final TextureProjectionFactory getTextureProjectionFactory()
    {
        return ( texProjFactory );
    }
    
    public void registerShadowFactory( ShadowFactory factory )
    {
        this.shadowFactory = factory;
    }
    
    public final ShadowFactory getShadowFactory()
    {
        return ( shadowFactory );
    }
    
    public void registerAtmosphereFactory( AtmosphereFactory factory )
    {
        this.atmosphereFactory = factory;
    }
    
    public AtmosphereFactory getAtmosphereFactory()
    {
		return ( atmosphereFactory );
	}
    
    public void registerBloomFactory( BloomFactory factory )
    {
        this.bloomFactory = factory;
    }
    
    public BloomFactory getBloomFactory()
    {
        return ( bloomFactory );
    }
    
    public EffectFactory()
    {
        registerBumpMappingFactory( new GLSLBumpMappingFactory() );
        registerCelShadingFactory( new AssemblyCelShadingFactory() );
        registerTextureProjectionFactory( new FixedFuncTextureProjectionFectory() );
        registerShadowFactory( new GLSLShadowMappingFactory() );
        registerAtmosphereFactory( new GLSLAtmosphereFactory() );
        registerBloomFactory( new GLSLBloomFactory() );
    }
}
