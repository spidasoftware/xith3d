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
package org.xith3d.effects.shadows;

import org.jagatoo.datatypes.Enableable;
import org.openmali.types.twodee.Sized2iRO;
import org.xith3d.render.RenderPass;
import org.xith3d.render.preprocessing.FrustumCuller;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.ShadowAtom;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.View;

/**
 * The ShadowFactory is capable if creating the shadow system environment.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ShadowFactory implements Enableable
{
    public static final class ShadowFactoryIdentifier
    {
    }
    
    private int shadowTextureUnit = 3;
    
    private int shadowQuality = 10;
    private int shadowSoftness = 0;
    
    private boolean enabled = true;
    
    /**
     * @return the (partially) unique id,
     *         that maps the shadow-factory to the render-code.
     */
    public abstract ShadowFactoryIdentifier getShadowFactoryId();
    
    /**
     * {@inheritDoc}
     */
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * Sets the texture-unit to use for the shadow map.
     * 
     * @param unit
     */
    public void setShadowTextureUnit( int unit )
    {
        this.shadowTextureUnit = unit;
    }
    
    /**
     * @return the texture-unit to use for the shadow map.
     */
    public final int getShadowTextureUnit()
    {
        return ( shadowTextureUnit );
    }
    
    public void setShadowQuality( int quality )
    {
        this.shadowQuality = quality;
    }
    
    public final int getShadowQuality()
    {
        return ( shadowQuality );
    }
    
    public abstract Sized2iRO getLightViewport();
    
    public void setShadowSoftness( int softness )
    {
        this.shadowSoftness = softness;
    }
    
    public final int getShadowSoftness()
    {
        return ( shadowSoftness );
    }
    
    /**
     * Checks, if the Light is accepted as "shadow caster".
     * throws an ecception, if it is not accepted.
     * 
     * @param light
     */
    public abstract void verifyLight( Light light );
    
    /**
     * @return whether this ShadowFactory needs per-light-frustum-culling.
     */
    public abstract boolean needsPerLightCulling();
    
    public abstract RenderPass setupRenderPass( View view, Light light, float viewportAspect, RenderBin shadowBin, long frameId, boolean justForCulling );
    
    /**
     * This is called by the {@link FrustumCuller}, if a Node with occluder
     * has been detected within the frustum.
     * 
     * @param node
     */
    public abstract ShadowAtom getShadowAtom( Node node );
    
    /**
     * This must be called from the scenegraph when a node's occluder state
     * is changed.
     * 
     * @param node
     * @param isOccluder
     */
    public abstract void onOccluderStateChanged( Node node, boolean isOccluder );
    
    /**
     * This must be called from the scenegraph when a shape's shadow-receiver state
     * is changed.
     * 
     * @param shape
     * @param isShadowReceiver
     */
    public abstract void onShadowReceiverStateChanged( Shape3D shape, boolean isShadowReceiver );
}
