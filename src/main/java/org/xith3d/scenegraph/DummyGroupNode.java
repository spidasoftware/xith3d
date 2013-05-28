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

import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * The DummyGroupNode is meant to be used, if Nodes must be grouped even if they
 * are already in other Groups.
 * PickingLibrary accepts instances of DummyGroupNode.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DummyGroupNode extends GroupNode
{
    private Node[] childNodes;
    
    @Override
    public void setParent( GroupNode parent )
    {
        if ( parent != null )
        {
            parent.removeChild( this );
            
            throw new IllegalArgumentException( "You cannot add a " + this.getClass().getName() + " to the SceneGraph." );
        }
    }
    
    @Override
    protected boolean ensureCapacity( int minCapacity )
    {
        final boolean result = super.ensureCapacity( minCapacity );
        
        if ( result )
            this.childNodes = super.children;
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild( Node child, int index )
    {
        if ( index > childNodes.length )
            throw new ArrayIndexOutOfBoundsException( index );
        
        ensureCapacity( index + 1 );
        
        childNodes[ index ] = child;
        numChildren++;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node setChild( Node child, int index )
    {
        if ( index >= childNodes.length )
            throw new ArrayIndexOutOfBoundsException( index );
        
        final Node old = childNodes[ index ];
        
        childNodes[ index ] = child;
        
        return ( old );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node removeChild( int index )
    {
        if ( index >= childNodes.length )
            throw new ArrayIndexOutOfBoundsException( index );
        
        final Node old = childNodes[ index ];
        childNodes[ index ] = null;
        System.arraycopy( children, index + 1, children, index, numChildren - index - 1 );
        numChildren--;
        
        return ( old );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBounds( boolean onlyDirty )
    {
        for ( int i = 0; i < numChildren(); i++ )
        {
            getChild( i ).updateBounds( onlyDirty );
        }
    }
    
    @Override
    protected DummyGroupNode newInstance()
    {
        throw new UnsupportedOperationException( "DummyGroupNode dos not support newInstance()." );
    }
    
    @Override
    public boolean traverse( DetailedTraversalCallback callback )
    {
        boolean result = false;
        
        for ( int i = 0; i < numChildren(); i++ )
        {
            if ( getChild( i ).traverse( callback ) )
                result = true;
        }
        
        return ( result );
    }
    
    public DummyGroupNode()
    {
        super();
        
        this.childNodes = super.children;
    }
}
