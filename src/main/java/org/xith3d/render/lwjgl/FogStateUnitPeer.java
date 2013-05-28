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

import java.nio.FloatBuffer;

import org.openmali.vecmath2.Colorf;

import org.jagatoo.logging.ProfileTimer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.FogStateUnit;
import org.xith3d.render.states.units.StateUnitPeer;
import org.xith3d.scenegraph.ExponentialFog;
import org.xith3d.scenegraph.Fog;
import org.xith3d.scenegraph.LinearFog;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * Handles the rendering of fog.
 * 
 * @author Benjamin Winters - based on LightingShaderPeer class by David Yazel
 */
public class FogStateUnitPeer implements StateUnitPeer
{
    private FloatBuffer float4 = BufferUtils.createFloatBuffer( 4 );
    
    public void apply( RenderAtom< ? > atom, StateUnit stateUnit, Object glObj, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, OpenGLStatesCache statesCache, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        if ( renderMode != RenderMode.NORMAL )
            return;
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "FogStateUnitPeer::apply()" );
        
        FogStateUnit fsu = (FogStateUnit)stateUnit;
        
        if ( fsu.numFogs() == 0 )
        {
            if ( !statesCache.enabled || statesCache.fogEnabled )
            {
                GL11.glDisable( GL11.GL_FOG );
                statesCache.fogEnabled = false;
            }
            
            ProfileTimer.endProfile();
            return;
        }
        
        // enable and set all of the fogs
        
        // draw some fog
        for ( int i = 0; i < fsu.numFogs(); i++ )
        {
            // get the current Fog node
            final Fog fog = fsu.getFog( i );
            
            // get the color of the fog
            Colorf color = fog.getColor();
            float4.put( color.getRed() ).put( color.getGreen() ).put( color.getBlue() ).put( 1.0f ).position( 0 );
            
            if ( fog instanceof LinearFog )
            {
                // set up linear fog
                final LinearFog linearFog = (LinearFog)fog;
                
                GL11.glFogi( GL11.GL_FOG_MODE, GL11.GL_LINEAR ); // fog mode
                // set here (linear)
                GL11.glFog( GL11.GL_FOG_COLOR, float4 );
                GL11.glHint( GL11.GL_FOG_HINT, GL11.GL_NICEST );
                GL11.glFogf( GL11.GL_FOG_START, linearFog.getFrontDistance() );
                GL11.glFogf( GL11.GL_FOG_END, linearFog.getBackDistance() );
                
                if ( !statesCache.enabled || !statesCache.fogEnabled )
                {
                    GL11.glEnable( GL11.GL_FOG );
                    statesCache.fogEnabled = true;
                }
            }
            else if ( fog instanceof ExponentialFog )
            {
                // set up exponential fog
                final ExponentialFog expFog = (ExponentialFog)fog;
                
                switch ( expFog.getFogMode() )
                {
                    case EXP:
                        // fog mode set here (exponential)
                        GL11.glFogi( GL11.GL_FOG_MODE, GL11.GL_EXP );
                        break;
                    case EXP2:
                        // fog mode set here (exponential 2)
                        GL11.glFogi( GL11.GL_FOG_MODE, GL11.GL_EXP2 );
                        break;
                    default:
                        throw new Error( "Unknown/Unsupported FogMode" );
                }
                
                GL11.glFog( GL11.GL_FOG_COLOR, float4 );
                GL11.glFogf( GL11.GL_FOG_DENSITY, expFog.getDensity() );
                GL11.glHint( GL11.GL_FOG_HINT, GL11.GL_NICEST );
                
                if ( !statesCache.enabled || !statesCache.fogEnabled )
                {
                    GL11.glEnable( GL11.GL_FOG );
                    statesCache.fogEnabled = true;
                }
            }
        }
        
        ProfileTimer.endProfile();
    }
}
