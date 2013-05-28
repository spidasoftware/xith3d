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
 * Leaf is an abstract class for all scene graph nodes that have no children.
 * Leaf nodes specify lights, geometry, sounds, etc.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Leaf extends Node
{
    private GroupNode pickHost = null;
    
    protected void setPickHost( GroupNode pickHost )
    {
        this.pickHost = pickHost;
    }
    
    /**
     * @return the pick-host GroupNode of this Shape3D
     *         This is the deepest GroupNode, which is a (grand)parent of this
     *         Shape3D and which is makred to be a pick-host
     * 
     * @see GroupNode#setPickHost(boolean)
     */
    public final GroupNode getPickHost()
    {
        return ( pickHost );
    }
    
    /**
     * This is a separate duplicate method of {@link Node#getWorldTransform()}
     * to ensure, a call to this method gets inlined by teh JIT-Compiler.
     * 
     * @return the Leaf's world-transform.
     */
    final Transform3D getLeafWorldTransform()
    {
        if ( transformGroup == null )
            return ( Transform3D.IDENTITY );
        
        return ( transformGroup.getInlinedWorldTransform() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void dump( int indent )
    {
        System.out.println( getIndentString( indent ) + this );
    }
    
    /**
     * Traverses the scenegraph from this node on.
     * If this Node is a Group it will recusively run through each child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    @Override
    public boolean traverse( DetailedTraversalCallback callback )
    {
        return ( callback.traversalOperationCommon( this ) && callback.traversalOperation( this ) && callback.traversalOperationAfter( this ) && callback.traversalOperationCommonAfter( this ) );
    }
    
    /**
     * Constructs a new Leaf object.
     */
    protected Leaf( boolean initializeBounds )
    {
        super( initializeBounds );
    }
    
    /**
     * Constructs a new Leaf object.
     */
    public Leaf()
    {
        this( true );
    }
}
