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
package org.xith3d.render.lwjgl;

import org.openmali.types.twodee.Sized2iRO;
import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.shadows.ShadowMappingFactory;
import org.xith3d.render.RenderPass;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;

/**
 * Handles GLSL shadow-mapping.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ShadowMappingRenderPeer implements ShadowRenderPeer.ShadowRenderPeerInterface
{
    /**
     * Draws the shadows into the shadow map.
     * 
     * @param view
     * @param light
     * @param shadowBin
     * @param renderPeer
     * @param frameId
     */
    public final int initShadows( View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId )
    {
        final ShadowMappingFactory shadowFactory = (ShadowMappingFactory)EffectFactory.getInstance().getShadowFactory();
        
        Sized2iRO viewport = ( (CanvasPeerImplBase)renderPeer.getCanvasPeer() ).getCurrentViewport();
        if ( viewport == null )
            viewport = renderPeer.getCanvasPeer().getCanvas3D(); // fallback!
        final float viewportAspect = viewport.getAspect();
        
        RenderPass generationPass = shadowFactory.setupRenderPass( view, light, viewportAspect, shadowBin, frameId, false );
        
        int triangles = 0;
        if ( generationPass != null )
        {
            try
            {
                triangles = renderPeer.renderRenderPass( null, renderPeer.getCanvasPeer().getOpenGLCapabilities(), renderPeer.getStatesCache(), null, generationPass, 0, true, RenderMode.SHADOW_MAP_GENERATION, view, true, frameId, 0L, 0L, null, 0 );
            }
            catch ( Throwable t )
            {
                t.printStackTrace();
            }
        }
        
        return ( triangles );
    }
    
    public final int drawShadows( View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId )
    {
        return ( 0 );
    }
}
