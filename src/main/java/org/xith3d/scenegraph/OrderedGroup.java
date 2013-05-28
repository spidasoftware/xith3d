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
 * OrderedGroup node guarantees that its children will be rendered
 * in their index order.
 *
 * <p> Note that actual order of shape rendering can be affected by setting 
 * TransparencyAttributes in appearance.
 * 
 * <p>1. All transparent shapes are drawn in a seperate pass and drawn back 
 * to front ignoring ordering in ordered groups.
 * <p>2. If a shape is transparent, but marked with setSortEnabled(false) then 
 * the shape will be drawn in the opaque pass without respect to its back-to-front order.
 * <p>3. If a shape is transparent, marked for non-sorting with setSortEnabled(false) and a child in an 
 * ordered group then the ordering of the shapes will be first by ordered group 
 * and second by standard rendering attributes.
 * 
 * @author David Yazel
 *
 * @see org.xith3d.scenegraph.TransparencyAttributes#setSortEnabled(boolean)
 * @see org.xith3d.scenegraph.TransparencyAttributes#setEnabled(boolean)
 */
public class OrderedGroup extends Group
{
    private static long ORDERED_ID = 0;
    
    protected long allocateOrderedId()
    {
        return ( ORDERED_ID++ );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected OrderedGroup newInstance()
    {
        boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        OrderedGroup g = new OrderedGroup();
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
     * Constructs a new OrderedGroup object.
     */
    public OrderedGroup()
    {
        super();
    }
}
