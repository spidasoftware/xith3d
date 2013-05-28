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

import java.util.BitSet;

import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;
import org.xith3d.scenegraph.traversal.TraversalCallback;

/**
 * Switch group nodes allow an application to choose dynamically
 * amoung a number of subgraphs. The Switch node contains an ordered
 * list of children and a switch value. The switch value determines
 * which child or children will be rendered. The index order of children
 * is only used for selecting the appropriate child or children,
 * it does not specify rendering order.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Switch extends Group
{
    /**
     * indicates that no children are rendered
     */
    public static final int CHILD_NONE = -1;
    
    /**
     * indicates that all children are rendered, switch node acts
     * as an oridinary Group node.
     */
    public static final int CHILD_ALL = -2;
    
    /**
     * indicates that the childMask BitSet is used to select
     * the children that are rendered
     */
    public static final int CHILD_MASK = -3;
    
    private static boolean ignoreMaskForTraversal = true;
    
    /**
     * the BitSet defining the children to render
     */
    private BitSet childMask;
    
    /**
     * the index of a specific child to render
     */
    private int whichChild;
    
    /**
     * Sets the index of the child to render for this object. The value
     * may be a non-negative integer, indicating a specific child,
     * or it may be one of the following constants: CHILD_NONE,
     * CHILD_ALL or CHILD_MASK.
     */
    public void setWhichChild( int whichChild )
    {
        final int oldValue = this.whichChild;
        
        this.whichChild = whichChild;
        
        if ( ( getModListener() != null ) && ( whichChild != oldValue ) )
            getModListener().onSwitchWhichChildChanged( this, oldValue, whichChild );
    }
    
    /**
     * Gets the index of the child to render for this object.
     */
    public final int getWhichChild()
    {
        return ( whichChild );
    }
    
    /**
     * Sets the child mask for this object.
     */
    public final void setChildMask( BitSet childMask )
    {
        //BitSet oldValue = this.childMask;
        
        this.childMask = childMask;
        
        /*
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
            getChild( i ).setLive( childMask.get( i ) );
        */
        
        /*
        if (getModListener() != null)
            getModListener().onSwitchMaskChanged( this, oldValue, childMask );
        */
    }
    
    /**
     * Gets the child mask for this object
     */
    public final BitSet getChildMask()
    {
        return ( childMask );
    }
    
    /**
     * Gets the curently selected child. If whichChild is out
     * of range or is set to CHILD_MASK, CHILD_ALL or CHILD_NONE
     * then null is returned.
     */
    public Node getCurrentChild()
    {
        if ( ( whichChild < 0 ) || ( whichChild > numChildren() - 1 ) )
            return ( null );
        
        return ( getChild( whichChild ) );
    }
    
    /**
     * Tests if specified child is visible according current switch settings.
     * @return true if child visible, false if not visible or child is not a member of this switch
     */
    public final boolean isVisible( Node child )
    {
        if ( whichChild == CHILD_NONE )
            return ( false );
        
        final int idx = indexOf( child );
        if ( idx < 0 )
            return ( false );
        
        if ( whichChild == CHILD_ALL )
            return ( true );
        
        if ( whichChild == CHILD_MASK )
        {
            if ( childMask == null )
                return ( false );
            
            return ( childMask.get( idx ) );
        }
        
        return ( idx == whichChild );
    }
    
    /**
     * If this is true, all children will be traversed by the traverse() method.<br>
     * If this is false, only the unmasked children will be traversed by the traverse() method.
     * 
     * @param ignore
     * @return the previous value
     */
    public static boolean setIgnoreMaskForTraversal( boolean ignore )
    {
        final boolean b = ignoreMaskForTraversal;
        ignoreMaskForTraversal = ignore;
        return ( b );
    }
    
    /**
     * If this is true, all children will be traversed by the traverse() method.<br>
     * If this is false, only the unmasked children will be traversed by the traverse() method.
     */
    public static boolean getIgnoreMaskForTraversal()
    {
        return ( ignoreMaskForTraversal );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Switch newInstance()
    {
        boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        Switch s = new Switch( this.whichChild, this.childMask );
        Node.globalIgnoreBounds = gib;
        
        return ( s );
    }
    
    /**
     * Traverses the scenegraph from this node on.
     * If this Node is a Group it will recusively run through each child.<br>
     * <br>
     * @see #getIgnoreMaskForTraversal()
     * 
     * @param listener the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    @Override
    public boolean traverse( TraversalCallback listener )
    {
        if ( !listener.traversalOperation( this ) )
            return ( false );
        
        if ( listener.traversalCheckGroup( this ) )
        {
            if ( ( ignoreMaskForTraversal ) || ( whichChild == Switch.CHILD_ALL ) )
            {
                final int n = numChildren();
                for ( int i = 0; i < n; i++ )
                {
                    if ( !getChild( i ).traverse( listener ) )
                        return ( false );
                }
            }
            else
            {
                switch ( whichChild )
                {
                    case Switch.CHILD_MASK:
                    {
                        final int n = numChildren();
                        for ( int i = childMask.nextSetBit( 0 ); i >= 0 && i < n; i = childMask.nextSetBit( i + 1 ) )
                        {
                            if ( !getChild( i ).traverse( listener ) )
                                return ( false );
                        }
                        break;
                    }
                    case Switch.CHILD_NONE:
                    {
                        break;
                    }
                    default:
                    {
                        return ( getChild( whichChild ).traverse( listener ) );
                    }
                }
            }
        }
        
        return ( true );
    }
    
    /**
     * Traverses the scenegraph from this node on.
     * If this Node is a Group it will recusively run through every child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     */
    @Override
    public boolean traverse( DetailedTraversalCallback callback )
    {
        if ( !callback.traversalOperationCommon( this ) )
            return ( false );
        if ( !callback.traversalOperation( this ) )
            return ( false );
        
        if ( callback.traversalCheckGroup( this ) )
        {
            if ( ( ignoreMaskForTraversal ) || ( whichChild == Switch.CHILD_ALL ) )
            {
                final int n = numChildren();
                for ( int i = 0; i < n; i++ )
                {
                    if ( !getChild( i ).traverse( callback ) )
                        return ( false );
                }
                
            }
            else
            {
                switch ( whichChild )
                {
                    case Switch.CHILD_MASK:
                    {
                        final int n = numChildren();
                        for ( int i = childMask.nextSetBit( 0 ); i >= 0 && i < n; i = childMask.nextSetBit( i + 1 ) )
                        {
                            if ( !getChild( i ).traverse( callback ) )
                                return ( false );
                        }
                        break;
                    }
                    case Switch.CHILD_NONE:
                    {
                        break;
                    }
                    default:
                    {
                        if ( !getChild( whichChild ).traverse( callback ) )
                            return ( false );
                    }
                }
            }
        }
        
        return ( callback.traversalOperationAfter( this ) && callback.traversalOperationCommonAfter( this ) );
    }
    
    /**
     * Constructs a new Switch object with the specified childMask
     * and the specified value for whichChild.
     */
    public Switch( int whichChild, BitSet childMask )
    {
        super();
        
        this.childMask = childMask;
        this.whichChild = whichChild;
    }
    
    /**
     * Constructs a new Switch object with an empty childMask
     * and the specified value for whichChild.
     */
    public Switch( int whichChild )
    {
        this( whichChild, new BitSet() );
    }
    
    /**
     * Constructs a new Switch object with an empty childMask
     * and whichChild set to CHILD_NONE.
     */
    public Switch()
    {
        this( CHILD_NONE );
    }
}
