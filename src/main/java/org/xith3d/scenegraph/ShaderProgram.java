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

import java.util.ArrayList;

import org.jagatoo.datatypes.Enableable;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.scenegraph.Shader.ShaderType;

/**
 * @author Abdul Bezrati
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ShaderProgram< S extends Shader > extends NodeComponent implements Enableable, StateTrackable< ShaderProgram< S > >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    private boolean dirty = false;
    private boolean enabled = true;
    
    // these vectors hold the vertex and fragment shaders 
    private final ArrayList< S > vertexShaders = new ArrayList< S >( 1 );
    private final ArrayList< S > fragmentShaders = new ArrayList< S >( 1 );
    
    public final void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
    
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    protected final void setDirty( boolean dirty )
    {
        this.dirty = dirty;
    }
    
    final boolean isDirty()
    {
        return ( dirty );
    }
    
    public final void addShader( S shader )
    {
        if ( shader.getType() == ShaderType.VERTEX )
            vertexShaders.add( shader );
        else if ( shader.getType() == ShaderType.FRAGMENT )
            fragmentShaders.add( shader );
    }
    
    public final void removeShader( S shader )
    {
        if ( shader.getType() == ShaderType.VERTEX )
            vertexShaders.remove( shader );
        else if ( shader.getType() == ShaderType.FRAGMENT )
            fragmentShaders.remove( shader );
    }
    
    public final void removeAllShaders()
    {
        vertexShaders.clear();
        fragmentShaders.clear();
    }
    
    public final int getNumVertexShaders()
    {
        return ( vertexShaders.size() );
    }
    
    public final int getNumFragmentShaders()
    {
        return ( fragmentShaders.size() );
    }
    
    public final int getNumShaders()
    {
        return ( getNumVertexShaders() + getNumFragmentShaders() );
    }
    
    public final S getVertexShader( int index )
    {
        return ( vertexShaders.get( index ) );
    }
    
    public final S getFragmentShader( int index )
    {
        return ( fragmentShaders.get( index ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        for ( int i = 0; i < vertexShaders.size(); i++ )
        {
            vertexShaders.get( i ).freeOpenGLResources( canvasPeer );
        }
        
        for ( int i = 0; i < fragmentShaders.size(); i++ )
        {
            fragmentShaders.get( i ).freeOpenGLResources( canvasPeer );
        }
    }
    
    // ////////////////////////////////////////////////////////////////
    // ///////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    // ////////////////////////////////////////////////////////////////
    
    public final void setStateNode( StateNode node )
    {
        this.stateNode = node;
        this.stateId = node.getId();
    }
    
    public final StateNode getStateNode()
    {
        return ( stateNode );
    }
    
    public final long getStateId()
    {
        return ( stateId );
    }
    
    /**
     * {@inheritDoc}
     */
    public abstract int compareTo( ShaderProgram< S > o );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals( Object obj );
    
    @SuppressWarnings( "unchecked" )
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        ShaderProgram<S> orgShaderProg = (ShaderProgram<S>)original;
        
        this.enabled = orgShaderProg.enabled;
        
        this.setDirty( true );
    }
    
    public ShaderProgram( boolean enabled )
    {
        super( false );
        
        this.enabled = enabled;
        this.dirty = true;
    }
    
    public ShaderProgram()
    {
        this( true );
    }
}
