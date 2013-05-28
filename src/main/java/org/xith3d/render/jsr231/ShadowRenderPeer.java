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
package org.xith3d.render.jsr231;

import java.util.HashMap;

import javax.media.opengl.GL;

import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.shadows.GLSLShadowMappingFactory;
import org.xith3d.effects.shadows.ShadowFactory;
import org.xith3d.effects.shadows.VolumeShadowFactory;
import org.xith3d.effects.shadows.ShadowFactory.ShadowFactoryIdentifier;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;

/**
 * Handles shadow rendering.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ShadowRenderPeer
{
    public static interface ShadowRenderPeerInterface
    {
        int initShadows( GL gl, View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId );
        
        int drawShadows( GL gl, View view, Light light, RenderBin shadowBin, RenderPeerImpl renderPeer, long frameId );
    }
    
    
    private RenderPeerImpl renderPeer;
    
    private final HashMap< ShadowFactoryIdentifier, ShadowRenderPeerInterface > peerMap = new HashMap< ShadowFactoryIdentifier, ShadowRenderPeerInterface >();
    
    public RenderPeerImpl getRenderPeer()
    {
        return ( renderPeer );
    }
    
    private static final ShadowFactory getShadowFactory()
    {
        final EffectFactory effFact = EffectFactory.getInstance();
        if ( effFact == null )
            return ( null );
        
        return ( effFact.getShadowFactory() );
    }
    
    public final int initShadows( GL gl, View view, Light light, RenderBin shadowBin, long frameId )
    {
        final ShadowFactory shadowFact = getShadowFactory();
        
        if ( ( shadowFact == null ) || !shadowFact.isEnabled() )
            return ( 0 );
        
        final ShadowRenderPeerInterface peer = peerMap.get( shadowFact.getShadowFactoryId() );
        
        return ( peer.initShadows( gl, view, light, shadowBin, renderPeer, frameId ) );
    }
    
    public final int drawShadows( GL gl, View view, Light light, RenderBin shadowBin, long frameId )
    {
        final ShadowFactory shadowFact = getShadowFactory();
        
        if ( ( shadowFact == null ) || !shadowFact.isEnabled() )
            return ( 0 );
        
        final ShadowRenderPeerInterface peer = peerMap.get( shadowFact.getShadowFactoryId() );
        
        return ( peer.drawShadows( gl, view, light, shadowBin, renderPeer, frameId ) );
    }
    
    public ShadowRenderPeer( RenderPeerImpl renderPeer )
    {
        this.renderPeer = renderPeer;
        
        peerMap.put( VolumeShadowFactory.SHADOW_FACTORY_ID, new VolumeShadowRenderPeer() );
        peerMap.put( GLSLShadowMappingFactory.SHADOW_FACTORY_ID, new ShadowMappingRenderPeer() );
    }
}
