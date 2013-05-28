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
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;

/**
 * @author Abdul Bezrati
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ShaderProgramContext< SP extends ShaderProgram< ? > > extends NodeComponent implements Enableable, StateTrackable< ShaderProgramContext< SP > >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    private SP program;
    
    public final SP getProgram()
    {
        return ( program );
    }
    
    public final void setEnabled( boolean enabled )
    {
        program.setEnabled( enabled );
    }
    
    public final boolean isEnabled()
    {
        return ( program.isEnabled() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        program.freeOpenGLResources( canvasPeer );
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
    public abstract int compareTo( ShaderProgramContext< SP > o );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals( Object obj );
    
    protected abstract SP newProgramInstance();
    
    @SuppressWarnings( "unchecked" )
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        ShaderProgramContext<SP> orgShaderProg = (ShaderProgramContext<SP>)original;
        
        if ( forceDuplicate )
        {
            this.program = newProgramInstance();
            program.duplicateNodeComponent( orgShaderProg.getProgram(), forceDuplicate );
        }
        else
        {
            this.program = orgShaderProg.program;
        }
    }
    
    public ShaderProgramContext( SP program )
    {
        super( false );
        
        this.program = program;
    }
}
