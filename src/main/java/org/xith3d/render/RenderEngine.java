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

import org.xith3d.loop.opscheduler.OperationScheduler;

/**
 * This interface is implemented by Xith3DEnvironment.
 * 
 * @see org.xith3d.base.Xith3DEnvironment
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface RenderEngine
{
    /**
     * Checks if everything is ok for the first rendering.
     * Never call this method directly. It is called by the RenderLoop.
     */
    public void checkRenderPreferences();
    
    /**
     * Renderes all Canvas3Ds in the list that are alive.
     * This method is usually called by the RenderLoop thread.
     * 
     * @param nanoGameTime the current game time in nanosecods
     * @param nanoFrameTime nanosecods needed to render the last frame
     */
    public void render( long nanoGameTime, long nanoFrameTime );
    
    /**
     * Clears the screen (BLACK) and destroys the display.
     * 
     * @see org.xith3d.render.CanvasPeer#destroy()
     */
    public void destroy();
    
    /**
     * Sets this RenderEngine's OperationScheduler.
     * Normally this will be the ExtRenderLoop instance.
     * 
     * @param opScheder the OperationScheduler instance
     */
    public void setOperationScheduler( OperationScheduler opScheder );
    
    /**
     * @return this RenderEngine's OperationScheduler.
     * Normally this will be the ExtRenderLoop instance.
     */
    public OperationScheduler getOperationScheduler();
}
