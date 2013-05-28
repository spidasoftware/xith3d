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

import org.openmali.vecmath2.Vector4f;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * @author Abdul Bezrati
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class AssemblyShader extends Shader
{
    protected Vector4f[] shaderProgramParameters = null;
    
    public final void setParameters( Vector4f[] parameters )
    {
        shaderProgramParameters = new Vector4f[ parameters.length ];
        System.arraycopy( parameters, 0, shaderProgramParameters, 0, parameters.length );
    }
    
    public final void setParameter( int index, Vector4f parameter )
    {
        shaderProgramParameters[ index ] = new Vector4f( parameter );
    }
    
    public final Vector4f[] getParameters()
    {
        return ( shaderProgramParameters );
    }
    
    public final Vector4f getParameter( int index )
    {
        return ( shaderProgramParameters[ index ] );
    }
    
    public final int getNumParameters()
    {
        return ( shaderProgramParameters.length );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof AssemblyShader ) )
            return ( false );
        AssemblyShader as = (AssemblyShader)o;
        if ( this.getType() != as.getType() )
            return ( false );
        if ( this.isEnabled() != as.isEnabled() )
            return ( false );
        if ( ComparatorHelper.compare( this.getShaderCode(), as.getShaderCode() ) != 0 )
            return ( false );
        if ( ComparatorHelper.compare( this.shaderProgramParameters, as.shaderProgramParameters ) != 0 )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo( Shader o )
    {
        if ( this == o )
            return ( 0 );
        if ( !( o instanceof AssemblyShader ) )
            return ( -1 );
        if ( this.getType().ordinal() < o.getType().ordinal() )
            return ( -1 );
        if ( this.getType().ordinal() > o.getType().ordinal() )
            return ( 1 );
        
        AssemblyShader as2 = (AssemblyShader)o;
        
        int val = ComparatorHelper.compareBoolean( this.isEnabled(), as2.isEnabled() );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( this.getShaderCode(), as2.getShaderCode() );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( this.shaderProgramParameters, as2.shaderProgramParameters );
        if ( val != 0 )
            return ( val );
        
        return ( 0 );
    }
    
    public AssemblyShader( ShaderType type, String shaderCode, Vector4f[] parameters, boolean enabled )
    {
        super( type, shaderCode, enabled );
        
        if ( parameters != null )
        {
            this.shaderProgramParameters = new Vector4f[ parameters.length ];
            System.arraycopy( parameters, 0, shaderProgramParameters, 0, parameters.length );
        }
    }
    
    public AssemblyShader( ShaderType type, String shaderCode, Vector4f[] parameters )
    {
        this( type, shaderCode, parameters, true );
    }
    
    public AssemblyShader( ShaderType type, String shaderCode )
    {
        this( type, shaderCode, null, true );
    }
    
    public AssemblyShader( ShaderType type )
    {
        this( type, null, null, true );
    }
}
