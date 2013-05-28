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

import org.xith3d.render.Canvas3D;
import org.xith3d.scenegraph.View;

/**
 * A class implementing this interface can hold several Canvas3Ds.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface CanvasBag
{
    /**
     * Removes a Canvas3D from the list of canveses. It won't be rendered anymore.
     * 
     * @param canvas the Canvas3D to be removed
     */
    public void removeCanvas( Canvas3D canvas );
    
    /**
     * Removes a Canvas3D from the list of canveses. It won't be rendered anymore.
     * 
     * @param canvasWrapper the Canvas3D to be removed
     */
    public void removeCanvas( Canvas3DWrapper canvasWrapper );
    
    /**
     * Removes all Canvas3Ds from the View
     */
    public void removeAllCanvas3Ds();
    
    /**
     * Adds a Canvas3D to the list of canveses. It will be rendered from now on.
     * 
     * @param canvas the Canvas3D to be added
     * @param view the view to add the Canvas3D to
     * @return the given Canvas3D back again
     */
    public Canvas3D addCanvas( Canvas3D canvas, View view );
    
    /**
     * Adds a Canvas3D to the list of canveses. It will be rendered from now on.
     * 
     * @param canvas the Canvas3D to be added
     * @return the given Canvas3D back again
     */
    public Canvas3D addCanvas( Canvas3D canvas );
    
    /**
     * Adds a Canvas3D to the list of canveses. It will be rendered from now on.
     * 
     * @param canvasWrapper the Canvas3D to be added
     * @param view the view to add the Canvas3D to
     * @return the given Canvas3DWrapper back again
     */
    public Canvas3DWrapper addCanvas( Canvas3DWrapper canvasWrapper, View view );
    
    /**
     * Adds a Canvas3D to the list of canveses. It will be rendered from now on.
     * 
     * @param canvasWrapper the Canvas3D to be added
     * @return the given Canvas3DWrapper back again
     */
    public Canvas3DWrapper addCanvas( Canvas3DWrapper canvasWrapper );
    
    /**
     * @return the Canvas3D added first to the Xith3DEnvironment or null
     */
    public Canvas3D getCanvas();
    
    /**
     * @param index the desired Canvas3D's index
     * @return the Canvas3D added first to the Xith3DEnvironment or null
     */
    public Canvas3D getCanvas( int index );
    
    /**
     * Suspends a Canvas3D do not be rendered for the moment.
     * 
     * @param canvas the arguable Canvas3D
     */
    public void suspendCanvas( Canvas3D canvas );
    
    /**
     * Suspends a Canvas3D do not be rendered for the moment.
     * 
     * @param canvasWrapper the arguable Canvas3D containing Canvas3DWrapper
     */
    public void suspendCanvas( Canvas3DWrapper canvasWrapper );
    
    /**
     * (Re-)activates a Canvas3D to be rendered by the RenderLoop
     * 
     * @param canvas the arguable Canvas3D
     */
    public void reviveCanvas( Canvas3D canvas );
    
    /**
     * (Re-)activates a Canvas3D to be rendered by the RenderLoop
     * 
     * @param canvasWrapper the arguable Canvas3D containing Canvas3DWrapper
     */
    public void reviveCanvas( Canvas3DWrapper canvasWrapper );
    
    /**
     * Checkes if a Canvas3D is currently to be rendered.
     * 
     * @param canvas the arguable Canvas3D
     * 
     * @return true, if the Canvas3D is currently rendered.
     */
    public boolean isCanvasAlive( Canvas3D canvas );
    
    /**
     * Checkes if a Canvas3DPanel is currently to be rendered.
     * 
     * @param canvasWrapper the arguable Canvas3DWrapper
     * 
     * @return true, if the Canvas3D is currently rendered.
     */
    public boolean isCanvasAlive( Canvas3DWrapper canvasWrapper );
}
