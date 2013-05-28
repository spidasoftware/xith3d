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
package org.xith3d.render;

import java.util.ArrayList;

import org.xith3d.render.config.OpenGLLayer;

/**
 * A RenderCallback can be attached to a RenderPass.<br>
 * Before the assotiated RenderPass'es Nodes are rendered, the callback method
 * is invoked.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface RenderCallback
{
    /**
     * This method is invoked by the Renderer from the render thread.
     * When this method is invoked, the RenderPass is about to be processed (e.g. shapes being culled).
     * 
     * @param renderPass
     */
    public void beforeRenderPassIsProcessed( RenderPass renderPass );
    
    /**
     * This method is invoked by the Renderer from the render thread.
     * When this method is invoked, the RenderPass is processed (e.g. shapes being culled).
     * 
     * @param renderPass
     */
    public void afterRenderPassIsProcessed( RenderPass renderPass );
    
    /**
     * This method is invoked by the Renderer from the render thread.
     * When this method is invoked, the RenderPass is about to be prepared.
     * 
     * @param renderPass
     * @param openGLLayer the used OpenGLLayer
     * @param glObj In JOGL mode, this is the GLCanvas, in LWJGL this is null
     */
    public void beforeRenderPassIsRendered( RenderPass renderPass, OpenGLLayer openGLLayer, Object glObj );
    
    /**
     * This method is invoked before the given RenderTarget is being activated.
     * 
     * @param renderPass
     * @param renderTarget
     * @param openGLLayer the used OpenGLLayer
     * @param glObj
     */
    public void beforeRenderTargetIsActivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj );
    
    /**
     * This method is invoked after the given RenderTarget is being activated.
     * 
     * @param renderPass
     * @param renderTarget
     * @param openGLLayer the used OpenGLLayer
     * @param glObj
     */
    public void afterRenderTargetIsActivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj );
    
    /**
     * This method is invoked by the Renderer from the render thread.
     * When this method is invoked, the RenderPass is fully set up.
     * 
     * @param renderPass
     * @param openGLLayer the used OpenGLLayer
     * @param glObj In JOGL mode, this is the GLCanvas, in LWJGL this is null
     */
    public void afterRenderPassIsSetUp( RenderPass renderPass, OpenGLLayer openGLLayer, Object glObj );
    
    /**
     * This method is invoked before the given RenderTarget is being deactivated.
     * 
     * @param renderPass
     * @param renderTarget
     * @param openGLLayer the used OpenGLLayer
     * @param glObj
     */
    public void beforeRenderTargetIsDeactivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj );
    
    /**
     * This method is invoked after the given RenderTarget is being deactivated.
     * 
     * @param renderPass
     * @param renderTarget
     * @param openGLLayer the used OpenGLLayer
     * @param glObj
     */
    public void afterRenderTargetIsDeactivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj );
    
    /**
     * This method is invoked by the Renderer from the render thread.
     * When this method is invoked, the RenderPass is fully set up.
     * 
     * @param renderPass
     * @param openGLLayer the used OpenGLLayer
     * @param glObj In JOGL mode, this is the GLCanvas, in LWJGL this is null
     */
    public void afterRenderPassCompleted( RenderPass renderPass, OpenGLLayer openGLLayer, Object glObj );
    
    
    public static final class RenderCallbackNotifier
    {
        private final ArrayList< RenderCallback > callbacks;
        
        public final void notifyBeforeRenderPassIsProcessed( final RenderPass renderPass )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).beforeRenderPassIsProcessed( renderPass );
            }
        }
        
        public final void notifyAfterRenderPassIsProcessed( final RenderPass renderPass )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).afterRenderPassIsProcessed( renderPass );
            }
        }
        
        public final void notifyBeforeRenderPassIsRendered( RenderPass renderPass, OpenGLLayer openGLLayer, Object glObj )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).beforeRenderPassIsRendered( renderPass, openGLLayer, glObj );
            }
        }
        
        public final void notifyBeforeRenderTargetIsActivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).beforeRenderTargetIsActivated( renderPass, renderTarget, openGLLayer, glObj );
            }
        }
        
        public final void notifyAfterRenderTargetIsActivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).afterRenderTargetIsActivated( renderPass, renderTarget, openGLLayer, glObj );
            }
        }
        
        public final void notifyAfterRenderPassIsSetUp( RenderPass renderPass, OpenGLLayer openGLLayer, Object glObj )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).afterRenderPassIsSetUp( renderPass, openGLLayer, glObj );
            }
        }
        
        public final void notifyBeforeRenderTargetIsDeactivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).beforeRenderTargetIsDeactivated( renderPass, renderTarget, openGLLayer, glObj );
            }
        }
        
        public final void notifyAfterRenderTargetIsDeactivated( RenderPass renderPass, RenderTarget renderTarget, OpenGLLayer openGLLayer, Object glObj )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).afterRenderTargetIsDeactivated( renderPass, renderTarget, openGLLayer, glObj );
            }
        }
        
        public final void notifyAfterRenderPassCompleted( RenderPass renderPass, OpenGLLayer openGLLayer, Object glObj )
        {
            for ( int i = 0; i < callbacks.size(); i++ )
            {
                callbacks.get( i ).afterRenderPassCompleted( renderPass, openGLLayer, glObj );
            }
        }
        
        
        public RenderCallbackNotifier( ArrayList< RenderCallback > callbacks )
        {
            this.callbacks = callbacks;
        }
    }
}
