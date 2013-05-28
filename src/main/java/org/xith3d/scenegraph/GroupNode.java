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
import java.util.List;

import org.jagatoo.logging.ProfileTimer;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.Clipper;
import org.xith3d.render.ClipperInfo;
import org.xith3d.render.ScissorRect;
import org.xith3d.render.preprocessing.ShadowAtom;
import org.xith3d.scenegraph.traversal.TraversalCallback;
import org.xith3d.scenegraph.utils.CopyListener;
import org.xith3d.utility.logging.X3DLog;

/**
 * A Node, that can hold a list of child Nodes.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class GroupNode extends Node
{
    protected final GroupNode hostGroup;
    
    /**
     * The list of children nodes in this Group.
     */
    protected Node[] children = null;
    protected int numChildren = 0;
    
    protected long totalNumChildren = 0L;
    protected long totalNumShapes = 0L;
    
    private ShadowAtom shadowAtom = null;
    
    private ScissorRect scissorRect = null;
    private Clipper clipper = null;
    
    private static final Bounds EMPTY_BOUNDS = new BoundingSphere();
    
    private static BoundingSphere tmpBounds = new BoundingSphere();
    
    private GroupNode pickHost = null;
    private boolean isPickHost = false;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean setLive( boolean live )
    {
        if ( !super.setLive( live ) )
            return ( false );
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            getChild( i ).setLive( live );
        }
        
        return ( true );
    }
    
    /**
     * Recursively searches for (grand-)children to set the pick-host property.
     * Stops the seach, if a child is a GroupNode and the pick-host flag is
     * set.
     * 
     * @param pickHost the GroupNode, which serves as the pick-host
     */
    protected void setPickHost( GroupNode pickHost )
    {
        if ( !isPickHost() )
        {
            this.pickHost = pickHost;
            
            final int n = numChildren();
            for ( int i = 0; i < n; i++ )
            {
                final Node child = getChild( i );
                
                if ( child instanceof Leaf )
                {
                    ( (Leaf)child ).setPickHost( pickHost );
                }
                else if ( child instanceof GroupNode )
                {
                    ( (GroupNode)child ).setPickHost( pickHost );
                }
            }
        }
    }
    
    /**
     * @return this GroupNode's pick-host
     */
    public final GroupNode getPickHost()
    {
        return ( pickHost );
    }
    
    /**
     * Sets this Group to be a pick-host or not.
     * A Shape3D provides a method getPickHost(), which will return the
     * (deepest) GroupNode, which is a pick-host.
     * 
     * @see Leaf#getPickHost()
     * 
     * @param isPickHost
     */
    public void setPickHost( boolean isPickHost )
    {
        if ( this.isPickHost == isPickHost )
            return;
        
        this.isPickHost = isPickHost;
        
        final GroupNode ph;
        if ( isPickHost() )
            ph = this;
        else
            ph = getPickHost();
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            final Node child = getChild( i );
            
            if ( child instanceof Leaf )
            {
                ( (Leaf)child ).setPickHost( ph );
            }
            else if ( child instanceof GroupNode )
            {
                final GroupNode g = (GroupNode)child;
                if ( !g.isPickHost() )
                    g.setPickHost( ph );
            }
        }
    }
    
    /**
     * @return the pick-host flag of this GroupNode.
     * A Shape3D provides a method getPickHost(), which will return the
     * (deepest) GroupNode, which is a pick-host.
     * 
     * @see Shape3D#getPickHost()
     */
    public boolean isPickHost()
    {
        return ( isPickHost );
    }
    
    final void setAtom( ShadowAtom shadowAtom )
    {
        this.shadowAtom = shadowAtom;
    }
    
    final ShadowAtom getAtom()
    {
        return ( shadowAtom );
    }
    
    protected final void checkChild( Node child, int index )
    {
        if ( child == this )
            throw new IllegalSceneGraphOperation( "You cannot add a Group to itself." );
        else if ( child == null )
            throw new IllegalSceneGraphOperation( "You cannot add null to a group." );
        else if ( child.getParent() != null )
            throw new IllegalSceneGraphOperation( "This Node already has a parent." );
        else if ( child instanceof BranchGroup )
            throw new IllegalSceneGraphOperation( "You cannot add a (root) BranchGroup to another group." );
        else if ( ( index > numChildren ) || ( index < 0 ) )
            throw new ArrayIndexOutOfBoundsException( "Illegal index " + index );
    }
    
    @Override
    protected void mergeInheritedNodes( InheritedNodeAttributes in )
    {
        super.mergeInheritedNodes( in );
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            getChild( i ).mergeInheritedNodes( in );
        }
    }
    
    @Override
    protected void unmergeInheritedNodes( InheritedNodeAttributes in )
    {
        super.unmergeInheritedNodes( in );
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            getChild( i ).unmergeInheritedNodes( in );
        }
    }
    
    @Override
    protected void unmergeInheritedLight( Light light )
    {
        super.unmergeInheritedLight( light );
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            getChild( i ).unmergeInheritedLight( light );
        }
    }
    
    @Override
    protected void unmergeInheritedFog( Fog fog )
    {
        super.unmergeInheritedFog( fog );
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            getChild( i ).unmergeInheritedFog( fog );
        }
    }
    
    protected boolean ensureCapacity( int minCapacity )
    {
        if ( children == null )
        {
            children = new Node[ Math.max( minCapacity, 8 ) ];
            
            return ( true );
        }
        
        final int oldCapacity = children.length;
        
        if ( minCapacity > oldCapacity )
        {
            final int newCapacity = ( oldCapacity * 3 ) / 2 + 1;
            final Node[] newArray = new Node[ newCapacity ];
            System.arraycopy( children, 0, newArray, 0, oldCapacity );
            children = newArray;
            
            return ( true );
        }
        
        return ( false );
    }
    
    void addTotalNumChildrenFromChild( long additionalTotalNumChildren, long additionalTotalNumShapes )
    {
        this.totalNumChildren += additionalTotalNumChildren;
        this.totalNumShapes += additionalTotalNumShapes;
        
        if ( getParent() != null )
            getParent().addTotalNumChildrenFromChild( additionalTotalNumChildren, additionalTotalNumShapes );
    }
    
    /**
     * Insert a child at the specified index. The parent of the child is set to
     * this object. If this object is live then the child is set live.
     */
    public void addChild( Node child, int index )
    {
        if ( hostGroup != null )
        {
            hostGroup.addChild( child, index );
            return;
        }
        
        checkChild( child, index );
        
        if ( SceneGraph.CHECK_FOR_ILLEGAL_MODIFICATION )
            SceneGraph.checkForIllegalModification( this );
        
        ensureCapacity( index + 1 );
        
        if ( index < numChildren )
        {
            System.arraycopy( children, index, children, index + 1, numChildren - index );
        }
        children[ index ] = child;
        numChildren++;
        
        child.setParent( this ); // this will make the child tree live/not
        
        final long tnc = totalNumChildren;
        final long tns = totalNumShapes;
        
        if ( child instanceof GroupNode )
        {
            final GroupNode childGroup = (GroupNode)child;
            
            childGroup.setPickHost( this.isPickHost() ? this : getPickHost() );
            
            totalNumChildren += 1 + childGroup.totalNumChildren;
            totalNumShapes += childGroup.totalNumShapes;
        }
        else if ( child instanceof Leaf )
        {
            totalNumChildren += 1;
            
            if ( child instanceof Shape3D )
            {
                totalNumShapes += 1;
            }
            
            ( (Leaf)child ).setPickHost( this.isPickHost() ? this : getPickHost() );
        }
        
        if ( this.getParent() != null )
        {
            this.getParent().addTotalNumChildrenFromChild( totalNumChildren - tnc, totalNumShapes - tns );
        }
        
        if ( child instanceof Light )
        {
            getInheritedNodeAttributes().addLight( (Light)child );
            mergeInheritedNodes( this.getInheritedNodeAttributes() );
        }
        else if ( child instanceof Fog )
        {
            getInheritedNodeAttributes().addFog( (Fog)child );
            mergeInheritedNodes( this.getInheritedNodeAttributes() );
        }
        else
        {
            child.mergeInheritedNodes( this.getInheritedNodeAttributes() );
        }
        
        if ( getModListener() != null )
            getModListener().onChildAddedToGroup( this, child );
    }
    
    /**
     * Add a child to the group as the last child in the group. The parent of
     * the child is set to this object. If this object is live then the child is
     * made live.
     */
    public final void addChild( Node child )
    {
        addChild( child, numChildren() );
    }
    
    /**
     * Remove the child at the specified index. The parent of the child is set
     * to null. The child is made not live.
     */
    public Node removeChild( int index )
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.removeChild( index ) );
        }
        
        if ( SceneGraph.CHECK_FOR_ILLEGAL_MODIFICATION )
            SceneGraph.checkForIllegalModification( this );
        
        if ( index >= numChildren )
            throw new IllegalArgumentException( "This child does not exist in this group" );
        
        final Node child = children[ index ];
        
        if ( child == null )
            throw new IllegalArgumentException( "This child does not exist in this group" );
        
        if ( getModListener() != null )
            getModListener().onChildRemovedFromGroup( this, child );
        
        children[ index ] = null;
        System.arraycopy( children, index + 1, children, index, numChildren - index - 1 );
        numChildren--;
        
        child.setParent( null ); // this will make the child tree not
        // live
        child.setModListener( null );
        
        final long tnc = totalNumChildren;
        final long tns = totalNumShapes;
        
        if ( child instanceof GroupNode )
        {
            final GroupNode childGroup = (GroupNode)child;
            
            ( (GroupNode)child ).setPickHost( (GroupNode)null );
            
            totalNumChildren -= 1 + childGroup.totalNumChildren;
        }
        else if ( child instanceof Leaf )
        {
            totalNumChildren -= 1;
            
            if ( child instanceof Shape3D )
            {
                totalNumShapes -= 1;
            }
            
            ( (Leaf)child ).setPickHost( (GroupNode)null );
        }
        
        if ( this.getParent() != null )
        {
            this.getParent().addTotalNumChildrenFromChild( totalNumChildren - tnc, totalNumShapes - tns );
        }
        
        if ( child instanceof Light )
        {
            unmergeInheritedLight( (Light)child );
            child.getInheritedNodeAttributes().removeLight( (Light)child );
        }
        else if ( child instanceof Fog )
        {
            unmergeInheritedFog( (Fog)child );
            child.getInheritedNodeAttributes().removeFog( (Fog)child );
        }
        else
        {
            child.unmergeInheritedNodes( this.getInheritedNodeAttributes() );
        }
        
        return ( child );
    }
    
    /**
     * @param child
     * 
     * @return the index of the given child Node in the list of this Group's children.
     */
    public final int indexOf( Node child )
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.indexOf( child ) );
        }
        
        if ( child == null )
            throw new NullPointerException( "child is null" );
        
        for ( int i = 0; i < numChildren; i++ )
        {
            if ( child.equals( children[ i ] ) )
                return ( i );
        }
        
        return ( -1 );
    }
    
    /**
     * Remove a child from the group. The parent of the child is set to null.
     * The child is made not live.
     */
    public final int removeChild( Node child )
    {
        final int index = indexOf( child );
        if ( index == -1 )
            throw new IllegalArgumentException( "This child does not exist in this group" );
        
        removeChild( index );
        
        return ( index );
    }
    
    /**
     * Removes all the children of this group.
     */
    public void removeAllChildren()
    {
        final int n = numChildren();
        for ( int i = n - 1; i >= 0; i-- )
            removeChild( i );
    }
    
    /**
     * Set the child at the specified index. If an existing child is at that
     * location then it is removed, its parent is set to null and it is set not
     * live. If this object is live then the child is set live.
     * 
     * @param child
     * @param index
     * 
     * @return the Node, that previously lived at the given position
     */
    public Node setChild( Node child, int index )
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.setChild( child, index ) );
        }
        
        checkChild( child, index );
        
        final Node prevChild;
        if ( index == numChildren )
            prevChild = null;
        else
            prevChild = removeChild( index );
        
        addChild( child, index );
        
        return ( prevChild );
    }
    
    /**
     * @return the child at the specified index.
     */
    public final Node getChild( int index )
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.getChild( index ) );
        }
        
        if ( ( index < 0 ) || ( index >= numChildren ) )
            //throw new ArrayIndexOutOfBoundsException( "index " + index + " is greater than max-index " + ( numChildren - 1 ) ) );
            return ( null );
        
        return ( children[ index ] );
    }
    
    /**
     * Get the number of children in this group.
     */
    public final int numChildren()
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.numChildren() );
        }
        
        return ( numChildren );
    }
    
    /**
     * @return the total number of children in this group and its subgroups.
     */
    public final long getTotalNumChildren()
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.getTotalNumChildren() );
        }
        
        return ( totalNumChildren );
    }
    
    /**
     * @return the total number of shapes in this group and its subgroups.
     */
    public final long getTotalNumShapes()
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.getTotalNumShapes() );
        }
        
        return ( totalNumShapes );
    }
    
    /**
     * Fills this group's children into the given list.
     * Note: The list is not cleared before.
     * 
     * @param list
     */
    public final <L extends List<Node>> L getChildren( L list )
    {
        if ( hostGroup != null )
        {
            return ( hostGroup.getChildren( list ) );
        }
        
        for ( int i = 0; i < numChildren; i++ )
        {
            list.add( children[ i ] );
        }
        
        return ( list );
    }
    
    /**
     * @return a read-only List of all contained children
     * 
     * @deprecated use {@link #numChildren()} and {@link #getChild(int)} instead.
     */
    @Deprecated
    public ArrayList< Node > getChildren()
    {
        return ( getChildren( new ArrayList< Node >( numChildren() ) ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setBoundsDirtyUpward()
    {
        boundsDirty = true;
        if ( getParent() != null )
        {
            getParent().setBoundsDirtyUpward();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setBoundsDirty()
    {
        setBoundsDirtyUpward();
    }
    
    /**
     * 
     * @param node
     * @param forceNodeUpdate
     */
    protected final void expandBounds( Node node, boolean forceNodeUpdate )
    {
        // if we already have the bounds then return
        
        if ( isIgnoreBounds() || node.isIgnoreBounds() || !this.boundsAutoCompute )
        {
            return;
        }
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "Node::expandBounds" );
        
        //System.out.println( node + ", " + node.getWorldBounds() );
        //if (forceNodeUpdate && (this.getTransformGroup() != null))
        node.updateBoundsCheap( false, true, false, true );
        //System.out.println( node + ", " + node.getWorldBounds() );
        
        final int num = this.numChildren();
        if ( ( num == 0 ) || ( ( num == 1 ) && ( this.getChild( 0 ) == node ) ) )
            untransformedBounds.set( node.getBounds() );
        else
            untransformedBounds.combine( node.getBounds() );
        
        bounds.set( untransformedBounds );
        if ( this instanceof TransformGroup )
            bounds.transform( ( (TransformGroup)this ).getTransform().getMatrix4f() );
        
        getWorldBounds().set( untransformedBounds );
        getWorldBounds().transform( getWorldTransform().getMatrix4f() );
        
        final GroupNode parent = getParent();
        if ( parent != null )
        {
            if ( !parent.boundsDirty )
                parent.expandBounds( this, false );
            else
                parent.updateBoundsCheap( true, false, true, false );
        }
        
        ProfileTimer.endProfile();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateBoundsCheap( boolean onlyDirty, boolean childrenToo, boolean parentToo, boolean onlyWorld )
    {
        // if we already have the bounds then return
        if ( ( isIgnoreBounds() ) || ( !boundsDirty && onlyDirty ) )
        {
            return;
        }
        
        if ( boundsAutoCompute )
        {
            if ( numChildren() == 0 )
            {
                untransformedBounds.set( EMPTY_BOUNDS );
                bounds.set( EMPTY_BOUNDS );
                getWorldBounds().set( untransformedBounds );
                if ( getWorldTransform() != null )
                    getWorldBounds().transform( getWorldTransform().getMatrix4f() );
            }
            else
            {
                boolean firstValidBounds = true;
                final int n = numChildren();
                for ( int i = 0; i < n; i++ )
                {
                    final Node node = getChild( i );
                    if ( !node.isIgnoreBounds() )
                    {
                        if ( childrenToo )
                        {
                            node.updateBoundsCheap( onlyDirty, childrenToo, false, onlyWorld );
                        }
                        
                        if ( !onlyWorld )
                        {
                            tmpBounds.set( node.untransformedBounds );
                            if ( this instanceof TransformGroup )
                                tmpBounds.transform( ( (TransformGroup)this ).getTransform().getMatrix4f() );
                        }
                        
                        if ( firstValidBounds )
                        {
                            firstValidBounds = false;
                            
                            if ( !onlyWorld )
                            {
                                untransformedBounds.set( node.getBounds() );
                            }
                            
                            getWorldBounds().set( node.getWorldBounds() );
                        }
                        else
                        {
                            if ( !onlyWorld )
                            {
                                untransformedBounds.combine( node.getBounds() );
                            }
                            
                            getWorldBounds().combine( node.getWorldBounds() );
                        }
                    }
                }
                
                //if (!onlyWorld)
                {
                    bounds.set( untransformedBounds );
                    if ( this instanceof TransformGroup )
                        bounds.transform( ( (TransformGroup)this ).getTransform().getMatrix4f() );
                }
            }
            
            boundsDirty = false;
            
            if ( parentToo )
            {
                final GroupNode parent = getParent();
                if ( parent != null )
                {
                    parent.boundsDirty = true;
                    parent.updateBoundsCheap( onlyDirty, false, parentToo, onlyWorld );
                }
            }
        }
        else
        {
            super.updateBoundsCheap( onlyDirty, childrenToo, parentToo, onlyWorld );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBounds( boolean onlyDirty )
    {
        updateBoundsCheap( onlyDirty, true, true, false );
    }
    
    public void setShowBounds( boolean show, boolean childrenToo )
    {
        super.setShowBounds( show );
        
        if ( childrenToo )
        {
            final int numChildren = numChildren();
            for ( int i = 0; i < numChildren; i++ )
            {
                final Node child = getChild( i );
                
                if ( child instanceof GroupNode )
                    ( (GroupNode)child ).setShowBounds( show, childrenToo );
                else
                    child.setShowBounds( show );
            }
        }
    }
    
    @Override
    protected void mergeInheritedScissorRect( ScissorRect scissorRect )
    {
        if ( ( this.scissorRect != null ) && ( this.scissorRect != scissorRect ) )
            return;
        
        super.mergeInheritedScissorRect( scissorRect );
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            getChild( i ).mergeInheritedScissorRect( scissorRect );
        }
    }
    
    /**
     * Sets this Group's ScissorRect
     * 
     * @param scissorRect the new ScissorRect
     */
    public void setScissorRect( ScissorRect scissorRect )
    {
        if ( this.scissorRect == scissorRect )
            return;
        
        final ScissorRect oldValue = this.scissorRect;
        
        this.scissorRect = scissorRect;
        
        mergeInheritedScissorRect( scissorRect );
        
        if ( getModListener() != null )
            getModListener().onScissorRectChanged( this, oldValue, scissorRect );
    }
    
    /**
     * @return this Group's ScissorBox
     */
    public final ScissorRect getScissorRect()
    {
        return ( scissorRect );
    }
    
    @Override
    protected void mergeInheritedClipper( Clipper clipper, ClipperInfo clipperInfo )
    {
        if ( ( this.clipper != null ) && ( this.clipper != clipper ) )
            return;
        
        if ( ( clipper != null ) && ( clipperInfo == null ) )
        {
            TransformGroup tg = this.getTransformGroup();
            clipperInfo = new ClipperInfo( clipper, ( tg == null ) ? null : tg.getTransform() );
        }
        
        super.mergeInheritedClipper( clipper, clipperInfo );
        
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            getChild( i ).mergeInheritedClipper( clipper, clipperInfo );
        }
    }
    
    /**
     * Sets this Group's Clipper
     * 
     * @param clipper the new Clipper
     */
    public void setClipper( Clipper clipper )
    {
        if ( this.clipper == clipper )
            return;
        
        Clipper oldValue = this.clipper;
        
        this.clipper = clipper;
        
        mergeInheritedClipper( clipper, null );
        
        if ( getModListener() != null )
            getModListener().onClipperChanged( this, oldValue, clipper );
    }
    
    /**
     * @return this Group's Clipper
     */
    public final Clipper getClipper()
    {
        return ( clipper );
    }
    
    /**
     * @return a new instance of this class. This is invoked by the sharedCopy() method.
     * 
     * @see #sharedCopy(CopyListener)
     */
    protected abstract GroupNode newInstance();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GroupNode sharedCopy( CopyListener listener )
    {
        final GroupNode newGroup = newInstance();
        
        final int numChildren = numChildren();
        for ( int i = 0; i < numChildren; i++ )
        {
            newGroup.addChild( getChild( i ).sharedCopy( listener ) );
        }
        
        /*
        newGroup.setBoundsAutoCompute( false );
        newGroup.setBounds( node.getBounds() );
        */
        if ( this.isOccluder() )
            newGroup.setIsOccluder( true );
        newGroup.boundsDirty = true;
        newGroup.updateBounds( true );
        newGroup.setPickable( this.isPickable() );
        newGroup.setRenderable( this.isRenderable() );
        newGroup.setName( this.getName() );
        
        if ( listener != null )
        {
            listener.onNodeCopied( this, newGroup, true );
        }
        
        return ( newGroup );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GroupNode sharedCopy()
    {
        return ( (GroupNode)super.sharedCopy() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void absorbDetails( Node node )
    {
        assert ( node instanceof GroupNode ) : "The given node is not a GroupNode.";
        
        final GroupNode group = (GroupNode)node;
        
        removeAllChildren();
        final int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            addChild( getChild( i ).sharedCopy() );
        }
        
        /*
        newGroup.setBoundsAutoCompute( false );
        newGroup.setBounds( node.getBounds() );
        */
        setIsOccluder( group.isOccluder() );
        this.boundsDirty = true;
        updateBounds( true );
        setPickable( group.isPickable() );
        setPickable( group.isRenderable() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void dump( int indent )
    {
        System.out.println( getIndentString( indent ) + this );
        
        for ( int i = 0; i < numChildren(); i++ )
        {
            getChild( i ).dump( indent + 1 );
        }
    }
    
    /**
     * Dumps this Group and it's children to stdout.
     */
    public void dump()
    {
        System.out.println( "dumping group " + this.toString() );
        
        dump( 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        for ( int i = 0; i < numChildren(); i++ )
        {
            getChild( i ).freeOpenGLResources( canvasPeer );
        }
    }
    
    /**
     * Traverses the scenegraph from this node on. If this Node is a Group it
     * will recusively run through each child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    @Override
    public boolean traverse( TraversalCallback callback )
    {
        if ( !callback.traversalOperation( this ) )
            return ( false );
        
        if ( callback.traversalCheckGroup( this ) )
        {
            final int numChildren = numChildren();
            for ( int i = 0; i < numChildren; i++ )
            {
                if ( !getChild( i ).traverse( callback ) )
                    return ( false );
            }
        }
        
        return ( true );
    }
    
    /**
     * @param name the name to be searched for
     * @return the first child named "name", or <i>null</i> if not foud
     */
    public Node findFirst( final String name )
    {
        class MyTraversalCallback implements TraversalCallback
        {
            public Node foundNode = null;
            
            public boolean traversalCheckGroup( GroupNode group )
            {
                // Explorate each group (we never know..)
                return ( true );
            }
            
            public boolean traversalOperation( Node node )
            {
                if ( ( ( name == null && node.getName() == null ) ) || ( node.getName().equals( name ) ) )
                {
                    foundNode = node;
                    return ( false );
                }
                
                return ( true );
            }
        }
        
        MyTraversalCallback tcb = new MyTraversalCallback();
        this.traverse( tcb );
        
        return ( tcb.foundNode  );
    }
    
    /**
     * @param name the name to be searched for
     * @return all the found children named "name", or <i>null</i> if none foud
     */
    public List< Node > findAll( final String name )
    {
        class MyTraversalCallback implements TraversalCallback
        {
            public ArrayList< Node > foundNodes = null;
            
            public boolean traversalCheckGroup( GroupNode group )
            {
                // Explorate each group (we never know..)
                return ( true );
            }
            
            public boolean traversalOperation( Node node )
            {
                if ( ( ( name == null && node.getName() == null ) ) || ( node.getName().equals( name ) ) )
                {
                    if ( foundNodes == null )
                    {
                        foundNodes = new ArrayList< Node >();
                    }
                    foundNodes.add( node );
                }
                
                return ( true );
            }
            
        }
        
        MyTraversalCallback tcb = new MyTraversalCallback();
        this.traverse( tcb );
        
        return ( tcb.foundNodes  );
    }
    
    /**
     * @param searchedClass the class to search for
     * @return a list of the first node of the specified class in this group, or
     *         <i>null</i> if not found
     */
    public < NT extends Node > NT findFirst( final Class< NT > searchedClass )
    {
        class MyTraversalCallback implements TraversalCallback
        {
            public NT foundNode = null;
            
            public boolean traversalCheckGroup( GroupNode group )
            {
                // Explorate each group (we never know..)
                return ( true );
            }
            
            @SuppressWarnings( "unchecked" )
            public boolean traversalOperation( Node node )
            {
                if ( searchedClass.isAssignableFrom( node.getClass() ) )
                {
                    foundNode = (NT)node;
                    return ( false );
                }
                
                return ( true );
            }
        }
        
        MyTraversalCallback tcb = new MyTraversalCallback();
        this.traverse( tcb );
        
        return ( tcb.foundNode  );
    }
    
    /**
     * @param searchedClass the class to search for
     * @return a list of all nodes of the specified class found in this group or
     *         <i>null</i> if none found
     */
    public < NT extends Node > List< NT > findAll( final Class< NT > searchedClass )
    {
        class MyTraversalCallback implements TraversalCallback
        {
            public ArrayList< NT > foundNodes = null;
            
            public boolean traversalCheckGroup( GroupNode group )
            {
                // Explorate each group (we never know..)
                return ( true );
            }
            
            @SuppressWarnings( "unchecked" )
            public boolean traversalOperation( Node node )
            {
                if ( searchedClass.isAssignableFrom( node.getClass() ) )
                {
                    if ( foundNodes == null )
                    {
                        foundNodes = (ArrayList< NT >)new ArrayList();
                    }
                    foundNodes.add( (NT)node );
                    return ( true );
                }
                
                return ( true );
            }
        }
        
        MyTraversalCallback tcb = new MyTraversalCallback();
        this.traverse( tcb );
        
        return ( tcb.foundNodes  );
    }
    
    /**
     * Constructs a new Group object.
     * 
     * @param hostGroup the group to be forwarded all the add/remove methods.
     */
    public GroupNode( GroupNode hostGroup )
    {
        super();
        
        this.hostGroup = hostGroup;
        
        if ( hostGroup == null )
        {
            this.children = null;
            this.numChildren = 0;
        }
        else
        {
            this.children = new Node[] { hostGroup };
            this.numChildren = 1;
        }
    }
    
    /**
     * Constructs a new Group object.
     */
    public GroupNode()
    {
        this( null );
    }
}
