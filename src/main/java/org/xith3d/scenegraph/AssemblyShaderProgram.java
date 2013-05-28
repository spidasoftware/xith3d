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

/**
 * @author Abdul Bezrati
 * @author Marvin Froehlich (aka Qudus)
 */
public class AssemblyShaderProgram extends ShaderProgram< AssemblyShader >
{
    // ////////////////////////////////////////////////////////////////
    // ///////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    // ////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof AssemblyShaderProgram ) )
            return ( false );
        AssemblyShaderProgram lo = (AssemblyShaderProgram)o;
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo( ShaderProgram< AssemblyShader > o )
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        AssemblyShaderProgram o = (AssemblyShaderProgram)original;
        
        removeAllShaders();
        
        if ( forceDuplicate )
        {
            for ( int i = 0; i < o.getNumVertexShaders(); i++ )
            {
                final AssemblyShader shader = o.getVertexShader( i );
                
                AssemblyVertexShader s2 = new AssemblyVertexShader( shader.getShaderCode(), shader.shaderProgramParameters, shader.isEnabled() );
                addShader( s2 );
            }
            
            for ( int i = 0; i < o.getNumFragmentShaders(); i++ )
            {
                final AssemblyShader shader = o.getFragmentShader( i );
                
                AssemblyFragmentShader s2 = new AssemblyFragmentShader( shader.getShaderCode(), shader.shaderProgramParameters, shader.isEnabled() );
                addShader( s2 );
            }
        }
        else
        {
            for ( int i = 0; i < o.getNumVertexShaders(); i++ )
            {
                addShader( o.getVertexShader( i ) );
            }
            
            for ( int i = 0; i < o.getNumFragmentShaders(); i++ )
            {
                addShader( o.getFragmentShader( i ) );
            }
        }
    }
    
    @Override
    public AssemblyShaderProgram cloneNodeComponent( boolean forceDuplicate )
    {
        AssemblyShaderProgram clone = new AssemblyShaderProgram( this.isEnabled() );
        
        clone.duplicateNodeComponent( this, forceDuplicate );
        
        return ( clone );
    }
    
    public AssemblyShaderProgram getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    public AssemblyShaderProgram( boolean enabled )
    {
        super( enabled );
    }
    
    public AssemblyShaderProgram()
    {
        this( true );
    }
    
    public AssemblyShaderProgram( AssemblyVertexShader vertexShader, AssemblyFragmentShader fragmentShader, boolean enabled )
    {
        this( enabled );
        
        if ( vertexShader != null )
            addShader( vertexShader );
        
        if ( fragmentShader != null )
            addShader( fragmentShader );
    }
    
    public AssemblyShaderProgram( AssemblyVertexShader vertexShader, AssemblyFragmentShader fragmentShader )
    {
        this( vertexShader, fragmentShader, true );
    }
}
