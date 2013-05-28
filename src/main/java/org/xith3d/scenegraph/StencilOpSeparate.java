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
package org.xith3d.scenegraph;

import org.jagatoo.opengl.enums.StencilFace;
import org.jagatoo.opengl.enums.StencilOperation;

/**
 * This is an abstraction of the OpenGL function glStencilOpSeparate().
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class StencilOpSeparate
{
    private StencilFace face;
    private StencilOperation sfail = StencilOperation.KEEP;
    private StencilOperation dpfail = StencilOperation.KEEP;
    private StencilOperation dppass = StencilOperation.KEEP;
    
    /**
     * Specifies whether front and/or back stencil state is updated.
     * 
     * @param face
     */
    public final void setFace( StencilFace face )
    {
        if ( face == null )
            throw new NullPointerException( "face must not be null" );
        
        this.face = face;
    }
    
    /**
     * Specifies whether front and/or back stencil state is updated.
     */
    public final StencilFace getFace()
    {
        return ( face );
    }
    
    /**
     * Specifies the action to take when the stencil test fails.
     * The initial value is KEEP.
     * 
     * @param sfail
     */
    public final void setSFail( StencilOperation sfail )
    {
        if ( sfail == null )
            throw new NullPointerException( "func must not be null" );
        
        this.sfail = sfail;
    }
    
    /**
     * Specifies the action to take when the stencil test fails.
     * The initial value is KEEP.
     */
    public final StencilOperation getSFail()
    {
        return ( sfail );
    }
    
    /**
     * Specifies the stencil action when the stencil test passes,
     * but the depth test fails.
     * The initial value is KEEP.
     * 
     * @param dpfail
     */
    public final void setDPFail( StencilOperation dpfail )
    {
        if ( dpfail == null )
            throw new NullPointerException( "func must not be null" );
        
        this.dpfail = dpfail;
    }
    
    /**
     * Specifies the stencil action when the stencil test passes,
     * but the depth test fails.
     * The initial value is KEEP.
     */
    public final StencilOperation getDPFail()
    {
        return ( dpfail );
    }
    
    /**
     * Specifies the stencil action when both the stencil test and the depth
     * test pass, or when the stencil test passes and either there is no
     * depth buffer or depth testing is not enabled.
     * The initial value is KEEP.
     * 
     * @param dppass
     */
    public final void setDPPass( StencilOperation dppass )
    {
        if ( dppass == null )
            throw new NullPointerException( "func must not be null" );
        
        this.dppass = dppass;
    }
    
    /**
     * Specifies the stencil action when both the stencil test and the depth
     * test pass, or when the stencil test passes and either there is no
     * depth buffer or depth testing is not enabled.
     * The initial value is KEEP.
     */
    public final StencilOperation getDPPass()
    {
        return ( dppass );
    }
    
    
    /**
     * Creates a new {@link StencilOpSeparate}.
     * 
     * @param face
     */
    public StencilOpSeparate( StencilFace face )
    {
        this.setFace( face );
    }
    
    /**
     * Creates a new {@link StencilOpSeparate}.
     * 
     * @param face
     * @param sfail
     */
    public StencilOpSeparate( StencilFace face, StencilOperation sfail )
    {
        this( face );
        
        this.setSFail( sfail );
    }
    
    /**
     * Creates a new {@link StencilOpSeparate}.
     * 
     * @param face
     * @param sfail
     * @param dpfail
     */
    public StencilOpSeparate( StencilFace face, StencilOperation sfail, StencilOperation dpfail )
    {
        this( face, sfail );
        
        this.setDPFail( dpfail );
    }
    
    /**
     * Creates a new {@link StencilOpSeparate}.
     * 
     * @param face
     * @param sfail
     * @param dpfail
     * @param dppass
     */
    public StencilOpSeparate( StencilFace face, StencilOperation sfail, StencilOperation dpfail, StencilOperation dppass )
    {
        this( face, sfail, dpfail );
        
        this.setDPPass( dppass );
    }
}
