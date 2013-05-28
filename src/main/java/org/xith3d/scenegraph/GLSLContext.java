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

import org.xith3d.render.CanvasPeer;

/**
 * Created on Jul 7, 2006 by florian for project 'xith3d_glsl_shader_support'
 * 
 * @author Florian Hofmann (aka Goliat)
 * @author Marvin Froehlich (aka Qudus)
 */
public class GLSLContext extends ShaderProgramContext< GLSLShaderProgram >
{
    private static boolean debuggingEnabled = false;
    
    private final GLSLParameters parameters = new GLSLParameters();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        super.freeOpenGLResources( canvasPeer );
        
        parameters.freeOpenGLResources( canvasPeer );
    }
    
    public static void setDebuggingEnabled( boolean debuggingEnabled )
    {
        GLSLContext.debuggingEnabled = debuggingEnabled;
    }
    
    public static boolean isDebuggingEnabled()
    {
        return ( GLSLContext.debuggingEnabled  );
    }
    
    public final GLSLParameters getUniformParameters()
    {
        return ( parameters );
    }
    
    
    //////////////////////////////////////////////////////////////////
    /////////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    //////////////////////////////////////////////////////////////////
    
    public GLSLContext getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected GLSLShaderProgram newProgramInstance()
    {
        return ( new GLSLShaderProgram() );
    }
    
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        GLSLContext orgSP = (GLSLContext)original;
        
        parameters.duplicateNodeComponent( orgSP.getUniformParameters(), forceDuplicate );
    }
    
    @Override
    public GLSLContext cloneNodeComponent( boolean forceDuplicate )
    {
        GLSLContext clone = new GLSLContext( null );
        
        clone.duplicateNodeComponent( this, forceDuplicate );
        
        return ( clone );
    }
    
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof GLSLContext ) )
            return ( false );
        
        GLSLContext sp = (GLSLContext)o;
        
        return ( getProgram().equals( sp.getProgram() ) && getUniformParameters().equals( sp.getUniformParameters() ) );
    }
    
    @Override
    public int compareTo( ShaderProgramContext< GLSLShaderProgram > o )
    {
        if ( this == o )
            return ( 0 );
        
        return ( getProgram().compareTo( o.getProgram() ) );
    }
    
    public GLSLContext( GLSLShaderProgram program )
    {
        super( program );
    }
}
