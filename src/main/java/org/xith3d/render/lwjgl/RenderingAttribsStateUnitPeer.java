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

import org.jagatoo.logging.ProfileTimer;
import org.jagatoo.opengl.enums.TestFunction;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.RenderingAttribsStateUnit;
import org.xith3d.render.states.units.StateUnitPeer;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.StencilFuncSeparate;
import org.xith3d.scenegraph.StencilMaskSeparate;
import org.xith3d.scenegraph.StencilOpSeparate;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * Hendles the setting of rendering attributes, specifically the depth test and
 * alpha test. This will be expanded to also support stencil tests in the
 * future.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class RenderingAttribsStateUnitPeer implements StateUnitPeer
{
    public void apply( RenderAtom< ? > atom, StateUnit stateUnit, Object glObj, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, OpenGLStatesCache statesCache, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "RenderingAttribsStateUnitPeer::apply()" );
        
        RenderingAttributes ra = ( (RenderingAttribsStateUnit)stateUnit ).getRenderingAttributes();
        
        if ( !statesCache.enabled || statesCache.depthWriteMask != ra.isDepthBufferWriteEnabled() )
        {
            GL11.glDepthMask( ra.isDepthBufferWriteEnabled() );
            statesCache.depthWriteMask = ra.isDepthBufferWriteEnabled();
        }
        
        if ( ra.hasColorWriteMask() && ( !statesCache.enabled || statesCache.colorWriteMask != ra.getColorWriteMask() ) )
        {
            GL11.glColorMask( ra.isRedWriteEnabled(), ra.isGreenWriteEnabled(), ra.isBlueWriteEnabled(), ra.isAlphaWriteEnabled() );
            statesCache.colorWriteMask = ra.getColorWriteMask();
        }
        
        final boolean depthTest;
        
        if ( ra.isDepthBufferEnabled() )
        {
            GL11.glDepthFunc( ra.getDepthTestFunction().toOpenGL() );
            if ( ra.getDepthTestFunction() == TestFunction.ALWAYS )
                depthTest = false;
            else
                depthTest = true;
        }
        else
        {
            depthTest = false;
        }
        
        if ( depthTest && ( !statesCache.enabled || !statesCache.depthTestEnabled ) )
            GL11.glEnable( GL11.GL_DEPTH_TEST );
        else if ( !depthTest && ( !statesCache.enabled || statesCache.depthTestEnabled ) )
            GL11.glDisable( GL11.GL_DEPTH_TEST );
        
        statesCache.depthTestEnabled = depthTest;
        
        final boolean alphaTest;
        
        if ( !statesCache.enabled || statesCache.blendingEnabled )
        {
            GL11.glAlphaFunc( ra.getAlphaTestFunction().toOpenGL(), ra.getAlphaTestValue() );
            if ( ra.getAlphaTestFunction() == TestFunction.ALWAYS )
                alphaTest = false;
            else
                alphaTest = true;
        }
        else
        {
            // no need to have alpha test enabled if we are not blending
            alphaTest = false;
        }
        
        if ( alphaTest && ( !statesCache.enabled || !statesCache.alphaTestEnabled ) )
            GL11.glEnable( GL11.GL_ALPHA_TEST );
        else if ( !alphaTest && ( !statesCache.enabled || statesCache.alphaTestEnabled ) )
            GL11.glDisable( GL11.GL_ALPHA_TEST );
        
        statesCache.alphaTestEnabled = alphaTest;
        
        if ( ra.isStencilEnabled() )
        {
            if ( !statesCache.enabled || !statesCache.stencilTestEnabled )
                GL11.glEnable( GL11.GL_STENCIL_TEST );
            GL11.glStencilFunc( ra.getStencilTestFunction().toOpenGL(), ra.getStencilRef(), ra.getStencilMask() );
            GL11.glStencilOp( ra.getStencilOpFail().toOpenGL(), ra.getStencilOpZFail().toOpenGL(), ra.getStencilOpZPass().toOpenGL() );
            
            if ( renderPeer.getCanvasPeer().getOpenGLInfo().getVersionMajor() >= 2 )
            {
                final StencilFuncSeparate funcSep = ra.getStencilFuncSeparate();
                if ( funcSep != null )
                {
                    GL20.glStencilFuncSeparate( funcSep.getFace().toOpenGL(), funcSep.getTestFunction().toOpenGL(), funcSep.getRef(), funcSep.getMask() );
                }
                
                final StencilOpSeparate opSep = ra.getStencilOpSeparate();
                if ( opSep != null )
                {
                    GL20.glStencilOpSeparate( opSep.getFace().toOpenGL(), opSep.getSFail().toOpenGL(), opSep.getDPFail().toOpenGL(), opSep.getDPPass().toOpenGL() );
                }
                
                final StencilMaskSeparate maskSep = ra.getStencilMaskSeparate();
                if ( maskSep != null )
                {
                    GL20.glStencilMaskSeparate( maskSep.getFace().toOpenGL(), maskSep.getMask() );
                }
            }
        }
        else
        {
            if ( !statesCache.enabled || statesCache.stencilTestEnabled )
                GL11.glDisable( GL11.GL_STENCIL_TEST );
        }
        
        statesCache.stencilTestEnabled = ra.isStencilEnabled();
        
        ProfileTimer.endProfile();
    }
}
