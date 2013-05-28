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

import org.jagatoo.datatypes.Enableable;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.SceneGraphOpenGLReferences;

/**
 * Base for any Shader.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Shader implements Comparable< Shader >, Enableable
{
    public static enum ShaderType
    {
        VERTEX,
        FRAGMENT
    }
    
    private final ShaderType shadertype;
    
    private boolean enabled;
    private boolean dirty;
    
    private String shaderCode;
    
    private final SceneGraphOpenGLReferences openGLReferences = new SceneGraphOpenGLReferences( 1 );
    
    public final SceneGraphOpenGLReferences getOpenGLReferences()
    {
        return ( openGLReferences );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize()
    {
        openGLReferences.prepareObjectForDestroy();
    }
    
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( openGLReferences.referenceExists( canvasPeer ) )
            openGLReferences.prepareObjectForDestroy( canvasPeer );
    }
    
    public final ShaderType getType()
    {
        return ( shadertype );
    }
    
    public final void setEnabled( boolean programState )
    {
        enabled = programState;
    }
    
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    final void setDirty( boolean dirty )
    {
        this.dirty = dirty;
    }
    
    final boolean isDirty()
    {
        return ( dirty );
    }
    
    public final void setShaderCode( String shaderCode )
    {
        if ( ( this.shaderCode != null ) && ( this.shaderCode.equals( shaderCode ) ) )
            return;
        
        this.shaderCode = shaderCode;
        this.dirty = true;
    }
    
    public final String getShaderCode()
    {
        return ( shaderCode );
    }
    
    /**
     * {@inheritDoc}
     */
    // will be overridden by GLSL vertex and fragment shader
    public abstract int compareTo( Shader o );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals( Object obj );
    
    public Shader( ShaderType type, String shaderCode, boolean enabled )
    {
        this.shadertype = type;
        this.shaderCode = shaderCode;
        this.enabled = enabled;
        this.dirty = true;
    }
}
