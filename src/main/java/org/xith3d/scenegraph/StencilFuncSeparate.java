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
import org.jagatoo.opengl.enums.TestFunction;

/**
 * This is an abstraction of the OpenGL function glStencilFuncSeparate().
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class StencilFuncSeparate
{
    private StencilFace face;
    private TestFunction func = TestFunction.ALWAYS;
    private int ref = 0;
    private int mask = ~0;
    
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
     * Specifies the test function.
     * The initial value is ALWAYS.
     * 
     * @param func
     */
    public final void setTestFunction( TestFunction func )
    {
        if ( func == null )
            throw new NullPointerException( "func must not be null" );
        
        this.func = func;
    }
    
    /**
     * Specifies the test function.
     * The initial value is ALWAYS.
     */
    public final TestFunction getTestFunction()
    {
        return ( func );
    }
    
    /**
     * Specifies the reference value for the stencil test.
     * ref is clamped to the range [2, 2^n - 1],
     * where n is the number of bitplanes in the stencil buffer.
     * The initial value is 0.
     * 
     * @param ref
     */
    public final void setRef( int ref )
    {
        if ( ref < 2 )
            throw new IllegalArgumentException( "ref must be in range [2, 2^n - 1]." );
        
        this.ref = ref;
    }
    
    /**
     * Specifies the reference value for the stencil test.
     * ref is clamped to the range [2, 2^n - 1],
     * where n is the number of bitplanes in the stencil buffer.
     * The initial value is 0.
     */
    public final int getRef()
    {
        return ( ref );
    }
    
    /**
     * Specifies a mask that is ANDed with both the reference value
     * and the stored stencil value when the test is done.
     * The initial value is all 1's.
     * 
     * @param mask
     */
    public final void setMask( int mask )
    {
        this.mask = mask;
    }
    
    public final int getMask()
    {
        return ( mask );
    }
    
    
    /**
     * Creates a new {@link StencilFuncSeparate}.
     * 
     * @param face
     */
    public StencilFuncSeparate( StencilFace face )
    {
        this.setFace( face );
    }
    
    /**
     * Creates a new {@link StencilFuncSeparate}.
     * 
     * @param face
     * @param func
     */
    public StencilFuncSeparate( StencilFace face, TestFunction func )
    {
        this( face );
        
        this.setTestFunction( func );
    }
    
    /**
     * Creates a new {@link StencilFuncSeparate}.
     * 
     * @param face
     * @param func
     * @param ref
     */
    public StencilFuncSeparate( StencilFace face, TestFunction func, int ref )
    {
        this( face, func );
        
        this.setRef( ref );
    }
    
    /**
     * Creates a new {@link StencilFuncSeparate}.
     * 
     * @param face
     * @param func
     * @param ref
     * @param mask
     */
    public StencilFuncSeparate( StencilFace face, TestFunction func, int ref, int mask )
    {
        this( face, func, ref );
        
        this.setMask( mask );
    }
}
