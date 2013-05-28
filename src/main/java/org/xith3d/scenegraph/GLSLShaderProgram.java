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
import org.xith3d.render.SceneGraphOpenGLReferences;

/**
 * Created on Jul 7, 2006 by florian for project 'xith3d_glsl_shader_support'
 * 
 * @author Florian Hofmann (aka Goliat)
 * @author Marvin Froehlich (aka Qudus)
 */
public class GLSLShaderProgram extends ShaderProgram< GLSLShader >
{
    // should be true if this program got linked
    protected boolean linked = false;
    
    // will be true if we got a linking error
    protected boolean linkingError = false;
    
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( openGLReferences.referenceExists( canvasPeer ) )
            openGLReferences.prepareObjectForDestroy( canvasPeer );
        
        super.freeOpenGLResources( canvasPeer );
    }
    
    final void setLinked( boolean linked )
    {
        this.linked = linked;
    }
    
    public final boolean isLinked()
    {
        return ( linked );
    }
    
    final void setLinkingError( boolean linkingError )
    {
        this.linkingError = linkingError;
    }
    
    public final boolean hasLinkingError()
    {
        return ( linkingError );
    }
    
    
    //////////////////////////////////////////////////////////////////
    /////////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    //////////////////////////////////////////////////////////////////
    
    public GLSLShaderProgram getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        GLSLShaderProgram orgSP = (GLSLShaderProgram)original;
        
        removeAllShaders();
        
        if ( forceDuplicate )
        {
            for ( int i = 0; i < getNumVertexShaders(); i++ )
            {
                this.addShader( orgSP.getVertexShader( i ) );
            }
            for ( int i = 0; i < getNumFragmentShaders(); i++ )
            {
                this.addShader( orgSP.getFragmentShader( i ) );
            }
        }
        else
        {
            this.openGLReferences.set( orgSP.openGLReferences );
            for ( int i = 0; i < getNumVertexShaders(); i++ )
            {
                this.addShader( orgSP.getVertexShader( i ) );
            }
            for ( int i = 0; i < getNumFragmentShaders(); i++ )
            {
                this.addShader( orgSP.getFragmentShader( i ) );
            }
            this.linked = orgSP.linked;
        }
    }
    
    @Override
    public GLSLShaderProgram cloneNodeComponent( boolean forceDuplicate )
    {
        GLSLShaderProgram clone = new GLSLShaderProgram( this.isEnabled() );
        
        clone.duplicateNodeComponent( this, forceDuplicate );
        
        return ( clone );
    }
    
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof GLSLShaderProgram ) )
            return ( false );
        GLSLShaderProgram lo = (GLSLShaderProgram)o;
        if ( this.isEnabled() != lo.isEnabled() )
            return ( false );
        if ( this.getNumVertexShaders() != lo.getNumVertexShaders() )
            return ( false );
        if ( this.getNumFragmentShaders() != lo.getNumFragmentShaders() )
            return ( false );
        for ( int i = 0; i < getNumVertexShaders(); i++ )
        {
            if ( !this.getVertexShader( i ).equals( lo.getVertexShader( i ) ) )
                return ( false );
        }
        for ( int i = 0; i < getNumFragmentShaders(); i++ )
        {
            if ( !this.getFragmentShader( i ).equals( lo.getFragmentShader( i ) ) )
                return ( false );
        }
        
        return ( true );
    }
    
    @Override
    public int compareTo( ShaderProgram< GLSLShader > o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.getNumVertexShaders() > o.getNumVertexShaders() )
            return ( 1 );
        else if ( this.getNumVertexShaders() < o.getNumVertexShaders() )
            return ( -1 );
        else if ( this.getNumFragmentShaders() > o.getNumFragmentShaders() )
            return ( 1 );
        else if ( this.getNumFragmentShaders() < o.getNumFragmentShaders() )
            return ( -1 );
        
        int result = 0;
        for ( int i = 0; i < getNumVertexShaders(); i++ )
        {
            result += this.getVertexShader( i ).compareTo( o.getVertexShader( i ) );
        }
        for ( int i = 0; i < getNumFragmentShaders(); i++ )
        {
            result += this.getFragmentShader( i ).compareTo( o.getFragmentShader( i ) );
        }
        
        if ( result > 0 )
            return ( +1 );
        else if ( result < 0 )
            return ( -1 );
        else
            return ( 0 );
    }
    
    public GLSLShaderProgram( boolean enabled )
    {
        super( enabled );
    }
    
    public GLSLShaderProgram()
    {
        this( true );
    }
    
    public GLSLShaderProgram( GLSLVertexShader vertexShader, GLSLFragmentShader fragmentShader, boolean enabled )
    {
        this( enabled );
        
        if ( vertexShader != null )
            addShader( vertexShader );
        
        if ( fragmentShader != null )
            addShader( fragmentShader );
    }
    
    public GLSLShaderProgram( GLSLVertexShader vertexShader, GLSLFragmentShader fragmentShader )
    {
        this( vertexShader, fragmentShader, true );
    }
}
