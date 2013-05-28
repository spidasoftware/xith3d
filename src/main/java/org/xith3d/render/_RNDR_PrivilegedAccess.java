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

import org.xith3d.scenegraph.BranchGroup;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class _RNDR_PrivilegedAccess
{
    public static final int getRenderersWorking()
    {
        return ( Renderer.renderersWorking );
    }
    
    public static final void addCanvas3D( Canvas3D canvas, Renderer renderer )
    {
        renderer.addCanvas3D( canvas );
    }
    
    public static final void removeCanvas3D( Canvas3D canvas, Renderer renderer )
    {
        renderer.removeCanvas3D( canvas );
    }
    
    public static final Canvas3D removeCanvas3D( int i, Renderer renderer )
    {
        return ( renderer.removeCanvas3D( i ) );
    }
    
    public static final RenderPass addRenderPass( RenderPass renderPass, Renderer renderer )
    {
        return ( renderer.addRenderPass( renderPass ) );
    }
    
    public static final RenderPass addRenderPass( int index, RenderPass renderPass, Renderer renderer )
    {
        return ( renderer.addRenderPass( index, renderPass ) );
    }
    
    public static final boolean removeRenderPasses( BranchGroup branchGroup, Renderer renderer )
    {
        return ( renderer.removeRenderPasses( branchGroup ) );
    }
    
    public static final boolean removeRenderPass( RenderPass renderPass, Renderer renderer )
    {
        return ( renderer.removeRenderPass( renderPass ) );
    }
    
    public static final boolean removeRenderPass( int index, Renderer renderer )
    {
        return ( renderer.removeRenderPass( index ) );
    }
    
    public static final void removeAllRenderPasses( Renderer renderer )
    {
        renderer.removeAllRenderPasses();
    }
}
