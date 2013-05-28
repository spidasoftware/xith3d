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
 * Group is a general purpose grouping node. Group nodes have
 * exactly one parent and an arbitrary number of children.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class Group extends GroupNode
{
    /**
     * Moves the specifed Node from its old location
     * in the scene graph to the end of this group.
     */
    public void moveTo( Node node )
    {
        node.detach();
        this.addChild( node );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Group newInstance()
    {
        boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        Group g = new Group();
        Node.globalIgnoreBounds = gib;
        
        return ( g );
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
        if ( !callback.traversalOperationCommon( this ) )
            return ( false );
        if ( !callback.traversalOperation( this ) )
            return ( false );
        
        if ( callback.traversalCheckGroupCommon( this ) && callback.traversalCheckGroup( this ) )
        {
            final int numChildren = numChildren();
            for ( int i = 0; i < numChildren; i++ )
            {
                if ( !getChild( i ).traverse( callback ) )
                    return ( false );
            }
        }
        
        return ( callback.traversalOperationCommonAfter( this ) && callback.traversalOperationAfter( this ) );
    }
    
    /**
     * Constructs a new Group object.
     */
    public Group()
    {
        super();
    }
    
    /**
     * Constructs a new Group object.
     */
    public Group( Node firstChild )
    {
        super();
        
        addChild( firstChild );
    }
}
