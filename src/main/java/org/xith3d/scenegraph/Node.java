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

import java.util.Stack;

import org.jagatoo.logging.ProfileTimer;
import org.openmali.spatial.SpatialNode;
import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.bounds.BoundingPolytope;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.spatial.bounds.BoundsType;
import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.shadows.ShadowFactory;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.Clipper;
import org.xith3d.render.ClipperInfo;
import org.xith3d.render.ScissorRect;
import org.xith3d.render.preprocessing.OrderedState;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;
import org.xith3d.scenegraph.traversal.TraversalCallback;
import org.xith3d.scenegraph.utils.CopyListener;
import org.xith3d.utility.logging.X3DLog;

/**
 * Node is the base class for all node objects in a scene graph.
 * 
 * @author Scott Shaver
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Node extends SceneGraphObject implements SpatialNode
{
    private final boolean is_billboard = ( this instanceof Billboard );
    private final boolean is_updatableNode = ( this instanceof UpdatableNode );
    
    private static boolean defaultPickable = true;
    
    private transient boolean pickable = defaultPickable;
    
    private transient boolean renderable = true;
    
    /**
     * The parent of this node.
     */
    private transient GroupNode parent = null;
    
    protected transient boolean boundsAutoCompute = true;
    
    private transient boolean ignoreBounds = false;
    
    /**
     * Transform group for this node. The universe maintains this transform when
     * the graph is parsed. We are putting a pointer here so that we do not need
     * to replicate a transform3d all over the place
     */
    protected TransformGroup transformGroup = null;
    
    private ScenegraphModificationsListener modListener = null;
    
    /**
     * The following two pointers help us maintain information regarding the
     * ordered state of this node. The ordered child pointers travel back up
     * through parents linked from ordered group to ordered group. Each child of
     * an ordered group has a unique OrderedState.
     */
    private Node orderedChild = null;
    
    private OrderedState orderedState = null;
    
    /**
     * Defines an occluder for this portion this sub-scene on down. If this node
     * is within view and an occluder is set then it will be passed to the
     * renderer for shadow rendering.
     */
    private boolean isOccluder = false;
    private Object shadowAttachment = null;
    
    private final InheritedNodeAttributes inheritedNodeAttribs = new InheritedNodeAttributes();
    
    /**
     * The bounds of this object.
     */
    protected Bounds bounds;
    protected Bounds untransformedBounds;
    private BoundsType boundsType = null;
    
    private static BoundsTypeHint boundsTypeHint = BoundsTypeHint.SPHERE;
    protected BoundsTypeHint instanceBoundsTypeHint;
    
    private Bounds worldBounds;
    
    private boolean showBounds = false;
    
    protected boolean boundsDirty = false;
    
    protected static boolean globalIgnoreBounds = false;
    private static Stack< Boolean > globalIgnoreBoundsStack = new Stack< Boolean >();
    static
    {
        globalIgnoreBoundsStack.push( globalIgnoreBounds );
    }
    
    private Object treeCell = null;
    
    public void setTreeCell( Object treeCell )
    {
        this.treeCell = treeCell;
    }
    
    public Object getTreeCell()
    {
        return ( treeCell );
    }
    
    public static void setBoundsTypeHint( BoundsTypeHint bth )
    {
        if ( bth == null )
            throw new IllegalArgumentException( "BoundsTypeHint must not be null" );
        
        boundsTypeHint = bth;
    }
    
    public static BoundsTypeHint getBoundsTypeHint()
    {
        return ( boundsTypeHint );
    }
    
    public final boolean isBillboard()
    {
        return ( is_billboard );
    }
    
    public final boolean isUpdatableNode()
    {
        return ( is_updatableNode );
    }
    
    /**
     * Is a Node object pickable when created?
     */
    public static boolean getDefaultPickable()
    {
        return ( defaultPickable );
    }
    
    /**
     * Sets whether this Node is attended by picking algorithms.
     * 
     * @param value if false, this Node is ignored by picking algorithms
     */
    public void setPickable( boolean value )
    {
        pickable = value;
        
        if ( modListener != null )
            modListener.onNodePropertyChanged( this, "Pickable" );
    }
    
    /**
     * @return whether this Node is attended by picking algorithms
     */
    public final boolean isPickable()
    {
        return ( pickable );
    }
    
    /**
     * Sets whether this Node is attended by picking algorithms, and all
     * its children
     * 
     * @param pickable if false, this Node and all its children, recursively,
     *                 are ignored by picking algorithms
     */
    public final static void setPickableRecursive( Node node, boolean pickable )
    {
        node.setPickable( pickable );
        
        if ( node instanceof GroupNode )
        {
            final GroupNode group = (GroupNode)node;
            final int numChildren = group.numChildren();
            for ( int i = 0; i < numChildren; i++ )
            {
                Node child = group.getChild( i );
                setPickableRecursive( child, pickable );
            }
        }
    }
    
    /**
     * Sets whether this Node is attended by picking algorithms, and all
     * its children.
     * 
     * @param pickable if false, this Node and all its children, recursively
     *                 are ignored by picking algorithms
     */
    public final void setPickableRecursive( boolean pickable )
    {
        setPickableRecursive( this, pickable );
    }
    
    /**
     * Sets whether this Node is excluded from rendering or not.
     * 
     * @param value if false, this Node is excluded from rendering
     */
    public final void setRenderable( boolean value )
    {
        renderable = value;
        
        if ( modListener != null )
            modListener.onNodePropertyChanged( this, "Renderable" );
    }
    
    /**
     * @return false, if this Node is excluded from rendering
     */
    public final boolean isRenderable()
    {
        return ( renderable );
    }
    
    public final InheritedNodeAttributes getInheritedNodeAttributes()
    {
        return ( inheritedNodeAttribs );
    }
    
    public void setModListener( ScenegraphModificationsListener modListener )
    {
        this.modListener = modListener;
    }
    
    public final ScenegraphModificationsListener getModListener()
    {
        return ( modListener );
    }
    
    public void setIgnoreBounds( boolean ignoreBounds )
    {
        if ( ignoreBounds == this.ignoreBounds )
            return;
        
        this.ignoreBounds = ignoreBounds;
        
        //setBoundsDirty();
    }
    
    public final boolean isIgnoreBounds()
    {
        return ( ignoreBounds );
    }
    
    public static void setGlobalIgnoreBounds( boolean val )
    {
        globalIgnoreBounds = val;
    }
    
    public static void pushGlobalIgnoreBounds( boolean val )
    {
        globalIgnoreBoundsStack.push( val );
        globalIgnoreBounds = val;
    }
    
    public static boolean popGlobalIgnoreBounds()
    {
        if ( globalIgnoreBoundsStack.size() > 1 ) // leave at least one element on the Stack
            globalIgnoreBoundsStack.pop();
        /*
        else
            globalIgnoreBoundsStack.peek();
        */

        globalIgnoreBounds = globalIgnoreBoundsStack.peek();
        
        return ( globalIgnoreBounds );
    }
    
    protected void mergeInheritedNodes( InheritedNodeAttributes in )
    {
        if ( in != this.getInheritedNodeAttributes() )
            this.getInheritedNodeAttributes().merge( in );
    }
    
    protected void unmergeInheritedNodes( InheritedNodeAttributes in )
    {
        if ( in != this.getInheritedNodeAttributes() )
            this.getInheritedNodeAttributes().unmerge( in );
    }
    
    protected void unmergeInheritedLight( Light light )
    {
        this.getInheritedNodeAttributes().removeLight( light );
    }
    
    protected void unmergeInheritedFog( Fog fog )
    {
        this.getInheritedNodeAttributes().removeFog( fog );
    }
    
    protected void mergeInheritedScissorRect( ScissorRect scissorRect )
    {
        this.getInheritedNodeAttributes().setScissorRect( scissorRect );
    }
    
    /**
     * 
     * @param clipper
     * @param clipperInfo
     */
    protected void mergeInheritedClipper( Clipper clipper, ClipperInfo clipperInfo )
    {
        this.getInheritedNodeAttributes().setClipper( clipperInfo );
    }
    
    public final void setBoundsAutoCompute( boolean autocompute )
    {
        this.boundsAutoCompute = autocompute;
    }
    
    public final boolean getBoundsAutoCompute()
    {
        return ( boundsAutoCompute );
    }
    
    public final void getWorldTransform( Transform3D transform3D )
    {
        if ( transformGroup != null )
        {
            transform3D.set( transformGroup.getWorldTransform() );
        }
        else
        {
            transform3D.set( Transform3D.IDENTITY );
        }
    }
    
    public Transform3D getWorldTransform()
    {
        if ( transformGroup == null )
            return ( Transform3D.IDENTITY );
        
        return ( transformGroup.getInlinedWorldTransform() );
    }
    
    public final boolean getShowBounds()
    {
        return ( showBounds );
    }
    
    public void setShowBounds( boolean show )
    {
        showBounds = show;
    }
    
    /**
     * Sets the bounds for this object.
     */
    public void setBounds( Bounds bounds )
    {
        /*
        if (this.bounds != null)
        {
            if (bounds != null)
                this.bounds.set( bounds );
            else
                this.bounds = null;
        }
        else if (bounds != null)
        {
            if (bounds instanceof BoundingBox)
                this.bounds = new BoundingBox( bounds );
            else
                this.bounds = new BoundingSphere( bounds );
        }
        */
        this.bounds = bounds;
        
        if ( bounds != null )
        {
            if ( untransformedBounds == null )
            {
                if ( bounds instanceof BoundingBox )
                    untransformedBounds = new BoundingBox( bounds );
                else if ( bounds instanceof BoundingPolytope )
                    untransformedBounds = new BoundingPolytope( bounds );
                else
                    untransformedBounds = new BoundingSphere( bounds );
            }
            else
            {
                if ( bounds instanceof BoundingBox )
                {
                    if ( untransformedBounds instanceof BoundingBox )
                        untransformedBounds.set( bounds );
                    else
                        untransformedBounds = new BoundingBox( bounds );
                }
                else if ( bounds instanceof BoundingPolytope )
                {
                    if ( untransformedBounds instanceof BoundingPolytope )
                        untransformedBounds.set( bounds );
                    else
                        untransformedBounds = new BoundingPolytope( bounds );
                }
                else
                //if (bounds instanceof BoundingSphere)
                {
                    //if (untransformedBounds instanceof BoundingSphere)
                        untransformedBounds.set( bounds );
                    //else
                    //    untransformedBounds = new BoundingSphere( bounds );
                }
            }
            
            if ( worldBounds == null )
            {
                if ( bounds instanceof BoundingBox )
                    worldBounds = new BoundingBox( bounds );
                else if ( bounds instanceof BoundingPolytope )
                    worldBounds = new BoundingPolytope( bounds );
                else
                    worldBounds = new BoundingSphere( bounds );
            }
            else
            {
                if ( bounds instanceof BoundingBox )
                {
                    if ( worldBounds instanceof BoundingBox )
                        worldBounds.set( bounds );
                    else
                        worldBounds = new BoundingBox( bounds );
                }
                else if ( bounds instanceof BoundingPolytope )
                {
                    if ( worldBounds instanceof BoundingPolytope )
                        worldBounds.set( bounds );
                    else
                        worldBounds = new BoundingPolytope( bounds );
                }
                else //if (bounds instanceof BoundingSphere)
                {
                    //if (worldBounds instanceof BoundingSphere)
                        worldBounds.set( bounds );
                    //else
                    //    worldBounds = new BoundingSphere( bounds );
                }
            }
        }
        
        if ( bounds == null )
            boundsType = null;
        else if ( bounds instanceof BoundingBox )
            boundsType = BoundsType.AABB;
        else if ( bounds instanceof BoundingPolytope )
            boundsType = BoundsType.POLYTOPE;
        else
            boundsType = BoundsType.SPHERE;
        
        
        this.setBoundsDirtyUpward();
        
        //if ( setBoundsTriggersUpdate )
        {
            updateBounds( true );
        }
    }
    
    public final BoundsType getBoundsType()
    {
        return ( boundsType );
    }
    
    public final Bounds getWorldBounds()
    {
        return ( worldBounds );
    }
    
    /**
     * @return the bounds for this object.
     */
    public final Bounds getBounds()
    {
        return ( bounds );
    }
    
    /**
     * @return the parent of this Node or returns null if there is no parent.
     */
    public final GroupNode getParent()
    {
        return ( parent );
    }
    
    /**
     * @return the root BranchGroup of this Node.
     */
    public final BranchGroup getRoot()
    {
        if ( getParent() != null )
        {
            return ( getParent().getRoot() );
        }
        else if ( this instanceof BranchGroup )
        {
            return ( (BranchGroup)this );
        }
        else
        {
            return ( null );
        }
    }
    
    public final Node getOrderedChild()
    {
        return ( orderedChild );
    }
    
    public final void setOrderedChild( Node orderedChild )
    {
        this.orderedChild = orderedChild;
    }
    
    public final OrderedState getOrderedState()
    {
        if ( orderedState != null )
        {
            return ( orderedState );
        }
        else if ( orderedChild != null )
        {
            return orderedChild.getOrderedState();
        }
        else
        {
            return ( null );
        }
    }
    
    protected final void setTransformGroup( TransformGroup tg )
    {
        transformGroup = tg;
    }
    
    /**
     * Returns the TransformGroup, which defines the Transform of this Node. So
     * it does <b>not</b> return itself, if it is a TransformGroup.
     * 
     * @return the TransformGroup which defines this nodes Transform, null if no
     *         TransformGroup is its parent
     */
    public final TransformGroup getTransformGroup()
    {
        return ( transformGroup );
    }
    
    protected void setBoundsDirty()
    {
        setBoundsDirtyUpward();
    }
    
    protected void setBoundsDirtyUpward()
    {
        boundsDirty = true;
        if ( parent != null )
        {
            parent.setBoundsDirtyUpward();
        }
    }
    
    /**
     * Detaches the Node from its parent and sets the node to not live.
     */
    public final void detach()
    {
        if ( SceneGraph.CHECK_FOR_ILLEGAL_MODIFICATION )
            SceneGraph.checkForIllegalModification( this );
        
        GroupNode parent = getParent();
        
        if ( parent != null )
            parent.removeChild( this );
    }
    
    /**
     * Sets the parent for this Node object. If the parent is live then this
     * node and any children it has are made live. If the parent is null then
     * this node and any children it has are made not live.
     * 
     * @throws IllegalSceneGraphOperation if the Node already has a parent
     */
    protected void setParent( GroupNode parent )
    {
        // Checks for violation of the scenegraph's directed acyclic graph
        // constraint (setting the parent of a node when it already has one)
        if ( ( this.parent != null ) && ( parent != null ) )
        {
            throw new IllegalSceneGraphOperation( "Illegal attempt to set the parent of this Node (name: '" + this.getName() + "', " + this + ") as it already has a parent (existing parent name: '" + this.parent.getName() + "', " + this.parent + ") and this would violate the directed acyclic graph constraint of the scenegraph." );
        }
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "Node::setParent()" );
        this.parent = parent;
        if ( ( parent == null ) || !parent.isLive() )
        {
            this.setLive( false );
        }
        else if ( parent.isLive() )
        {
            this.setLive( true );
        }
        
        // update the transformations and bounds
        
        this.updateTransformGroup();
        this.updateWorldTransform();
        this.updateOrderedChild();
        
        if ( parent != null )
        {
            if ( !parent.boundsDirty )
                parent.expandBounds( this, true );
            else
                parent.updateBoundsCheap( true, false, true, false );
        }
        
        ProfileTimer.endProfile();
    }
    
    public void printBounds( boolean childrenToo )
    {
        printBounds( childrenToo, 0 );
    }
    
    private void printBounds( boolean childrenToo, int indent )
    {
        for ( int i = 0; i < indent; i++ )
        {
            System.out.print( "   " );
        }
        System.out.println( getName() + " " + this.getWorldBounds() );
        
        if ( isLive() )
            System.out.println( " (live)" );
        if ( childrenToo )
        {
            if ( this instanceof GroupNode )
            {
                final GroupNode group = (GroupNode)this;
                final int numChildren = group.numChildren();
                
                Bounds childrenBounds[] = new Bounds[ numChildren ];
                for ( int i = 0; i < numChildren; i++ )
                {
                    Node n = group.getChild( i );
                    n.printBounds( childrenToo, indent + 1 );
                    childrenBounds[ i ] = n.getBounds();
                }
                
                bounds.set( childrenBounds );
            }
        }
    }
    
    /**
     * When autocompute is turned off we need to take the bounds and assign
     * them to all the children.
     * 
     * @param b the Bounds to assign recursively to all children
     */
    private final void setAllWorldBounds( Bounds b )
    {
        worldBounds.set( b );
    }
    
    /**
     * Update bounds sets the bounds and virutal world bounds of all nodes
     * recursively.
     * 
     * @param onlyDirty Only update those nodes which have been marked as dirty.
     * @param childrenToo if false, the children bounds are not updated
     * @param parentToo if false, the parent Node will not be updated
     * @param onlyWorld if true, only the vworld bounds are updated
     */
    protected void updateBoundsCheap( boolean onlyDirty, boolean childrenToo, boolean parentToo, boolean onlyWorld )
    {
        // if we already have the bounds then return
        if ( ( isIgnoreBounds() ) || ( !boundsDirty && onlyDirty ) )
        {
            return;
        }
        
        worldBounds.set( untransformedBounds );
        worldBounds.transform( getWorldTransform().getMatrix4f() );
        
        if ( !boundsAutoCompute )
        {
            if ( this instanceof GroupNode )
            {
                final GroupNode group = (GroupNode)this;
                final int numChildren = group.numChildren();
                for ( int i = 0; i < numChildren; i++ )
                {
                    group.getChild( i ).setAllWorldBounds( worldBounds );
                }
            }
        }
        
        boundsDirty = false;
        
        final GroupNode parent = getParent();
        if ( ( parentToo ) && ( parent != null ) )
        {
            parent.boundsDirty = true;
            parent.updateBoundsCheap( onlyDirty, false, parentToo, onlyWorld );
        }
    }
    
    /**
     * Update bounds sets the bounds and virutal world bounds of all nodes
     * recursively.
     * 
     * @param onlyDirty Only update those nodes which have been marked as dirty.
     */
    public void updateBounds( boolean onlyDirty )
    {
        updateBoundsCheap( onlyDirty, true, true, false );
    }
    
    public final void setIsOccluder( boolean isOccluder )
    {
        final boolean changed = this.isOccluder != isOccluder;
        this.isOccluder = isOccluder;
        
        if ( changed )
        {
            final EffectFactory effFact = EffectFactory.getInstance();
            if ( effFact != null )
            {
                final ShadowFactory shadowFact = effFact.getShadowFactory();
                if ( shadowFact != null )
                    shadowFact.onOccluderStateChanged( this, isOccluder );
            }
        }
    }
    
    public final boolean isOccluder()
    {
        return ( isOccluder );
    }
    
    public final void setShadowAttachment( Object shadowAttachment )
    {
        this.shadowAttachment = shadowAttachment;
    }
    
    public final Object getShadowAttachment()
    {
        return ( shadowAttachment );
    }
    
    /**
     * If this is a TansformGroup, then it will multiply the transform against
     * the parent transform and store it into the world-transform matrix.
     * 
     * <b>Never use this method on your own! It's just for internal use.</b>
     */
    public void updateWorldTransform()
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "Node::updateWorldTransform" );
        
        if ( this instanceof TransformGroup )
        {
            final TransformGroup tg = (TransformGroup)this;
            final Transform3D t = tg.getWorldTransform();
            
            if ( getTransformGroup() == null )
            {
                t.set( tg.getTransform() );
            }
            else
            {
                //getTransformGroup().getWorldTransform().transform( tg.getTransform(), t );
                t.set( tg.getTransform() );
                getTransformGroup().getWorldTransform().transform( t );
            }
        }
        
        if ( this instanceof GroupNode )
        {
            final GroupNode g = (GroupNode)this;
            final int n = g.numChildren();
            for ( int i = 0; i < n; i++ )
            {
                g.getChild( i ).updateWorldTransform();
            }
        }
        
        ProfileTimer.endProfile();
    }
    
    /**
     * <b>Never use this method on your own! It's just for internal use.</b>
     */
    public final void updateTransformGroup()
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "Node::updateTransformGroup" );
        
        if (parent == null)
        {
            setTransformGroup( null );
        }
        else if (parent instanceof TransformGroup)
        {
            setTransformGroup( (TransformGroup)parent );
        }
        else
        {
            setTransformGroup( parent.getTransformGroup() );
        }
        
        /*
         * if (transformGroup==null) System.out.println("node "+getName()+" has
         * no tg"); else System.out.println("node "+getName()+" has tg
         * "+transformGroup.getName());
         */
        if ( this instanceof GroupNode )
        {
            final GroupNode g = (GroupNode)this;
            final int n = g.numChildren();
            for ( int i = 0; i < n; i++ )
            {
                g.getChild( i ).updateTransformGroup();
            }
        }
        
        ProfileTimer.endProfile();
    }
    
    /**
     * Steps down from the nodes.
     */
    protected final void updateOrderedChild()
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "Node::updateOrderedChild" );
        
        if ( parent == null )
        {
            setOrderedChild( null );
        }
        else if ( parent instanceof OrderedGroup )
        {
            // this is an ordered child, so we need to
            // build a new ordered state down one depth
            
            OrderedState os = parent.getOrderedState();
            if ( os == null )
            {
                os = new OrderedState();
            }
            else
            {
                os = (OrderedState)os.clone();
            }
            
            // drop down a depth of one
            os.addDepth( ( (OrderedGroup)parent ).allocateOrderedId() );
            
            // Set newly created ordered state as the ordered state for this node
            
            orderedState = os;
            setOrderedChild( this );
        }
        else
        {
            // otherwise just take the ordered child of the parent
            setOrderedChild( parent.getOrderedChild() );
        }
        if ( this instanceof GroupNode )
        {
            // if this is a group, step through the children
            final GroupNode group = (GroupNode)this;
            final int numChildren = group.numChildren();
            for ( int i = 0; i < numChildren; i++ )
            {
                group.getChild( i ).updateOrderedChild();
            }
        }
        
        ProfileTimer.endProfile();
    }
    
    /**
     * Creates a new instance of the Node.
     */
    public Node cloneNode( boolean forceDuplicate )
    {
        try
        {
            Node node = (Node)getClass().newInstance();
            
            node.duplicateNode( this, forceDuplicate );
            
            return ( node );
        }
        catch ( Exception ex )
        {
            RuntimeException runtimeException = new RuntimeException();
            
            runtimeException.initCause( ex );
            
            throw runtimeException;
        }
    }
    
    /**
     * Copies all the node information from the originalNode into the current
     * node. This method is called from the cloneNode method which is called
     * from the cloneTree method. This method is empty - it doesn't do anything
     * 
     * @param originalNode
     * @param forceDuplicate
     */
    public void duplicateNode( Node originalNode, boolean forceDuplicate )
    {
    }
    
    /**
     * Creates a shared copy of this Node. A shared copy is one where the
     * geometry and appearance is shared, but everything else is copied. This
     * is a replacement for shared groups because of performance considerations.
     * If you are loading the same model many times then this can save on memory
     * and load times. The only allowable within the subtree are groups and
     * shapes. This also copies a shapes bounds and turns autocomute off so that
     * it is fast to insert the model into the scene.
     * 
     * @param listener
     * 
     * @return a shared copy of this Node
     * 
     * @see #absorbDetails(Node)
     */
    public Node sharedCopy( CopyListener listener )
    {
        throw new UnsupportedOperationException( "sharedCopy is not yet implemented for " + this.getClass().getName() );
    }
    
    /**
     * Creates a shared copy of this Node. A shared copy is one where the
     * geometry and appearance is shared, but everything else is copied. This
     * is a replacement for shared groups because of performance considerations.
     * If you are loading the same model many times then this can save on memory
     * and load times. The only allowable within the subtree are groups and
     * shapes. This also copies a shapes bounds and turns autocomute off so that
     * it is fast to insert the model into the scene.
     * 
     * @return a shared copy of this Node
     * 
     * @see #absorbDetails(Node)
     */
    public Node sharedCopy()
    {
        return ( sharedCopy( (CopyListener)null ) );
    }
    
    /**
     * Turns the receiver into a shared copy of the node parameter. This is
     * precisely the inverse operation of sharedCopy; i.e., shared copy creates
     * a new node, but absorbDetails turns the current node into a copy exactly
     * equivalent to what would be returned, if you created a new copy via the
     * sharedCopy() method.
     * 
     * @param node the node to copy.
     * 
     * @see #sharedCopy()
     */
    public void absorbDetails( Node node )
    {
        throw new UnsupportedOperationException( "absorbDetails is not yet implemented for " + this.getClass().getName() );
    }
    
    /**
     * Each Node object will be pickable by default following this static flag.
     * 
     * @param value pickable by default?
     */
    public static void setDefaultPickable( boolean value )
    {
        defaultPickable = value;
    }
    
    /**
     * This method frees OpenGL resources (names) for all Nodes in the traversal
     * of this Node(-Group).
     * 
     * @param canvasPeer
     */
    public abstract void freeOpenGLResources( CanvasPeer canvasPeer );
    
    /**
     * This method frees OpenGL resources (names) for all Nodes in the traversal
     * of this Node(-Group).
     * 
     * @param canvas
     */
    public final void freeOpenGLResources( Canvas3D canvas )
    {
        if ( canvas.getPeer() == null )
            throw new Error( "The given Canvas3D is not linked to a CanvasPeer." );
        
        freeOpenGLResources( canvas.getPeer() );
    }
    
    protected final String getIndentString( int indent )
    {
        char[] spaces = new char[ indent * 2 ];
        for ( int i = 0; i < spaces.length; i++ )
        {
            spaces[ i ] = ' ';
        }
        
        return ( new String( spaces ) );
    }
    
    protected abstract void dump( int indent );
    
    /**
     * Traverses the scenegraph from this node on. If this Node is a Group it
     * will recusively run through each child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    public boolean traverse( TraversalCallback callback )
    {
        return ( callback.traversalOperation( this ) );
    }
    
    /**
     * Traverses the scenegraph from this node on. If this Node is a Group it
     * will recusively run through each child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    public abstract boolean traverse( DetailedTraversalCallback callback );
    
    /**
     * Constructs a new Node object.
     */
    protected Node( boolean initializeBounds )
    {
        super();
        
        if ( initializeBounds )
        {
            instanceBoundsTypeHint = boundsTypeHint;
            
            ignoreBounds = globalIgnoreBounds;
            if ( !globalIgnoreBounds )
            {
                switch ( instanceBoundsTypeHint )
                {
                    case AABB:
                        bounds = new BoundingBox();
                        untransformedBounds = new BoundingBox();
                        worldBounds = new BoundingBox();
                        boundsType = BoundsType.AABB;
                        break;
                    
                    case POLYTOPE:
                        throw new Error( "BoundingPolytopes are not yet supported" );
                        
                    case NONE:
                        bounds = null;
                        untransformedBounds = null;
                        worldBounds = null;
                        boundsType = BoundsType.POLYTOPE;
                        break;
                    
                    case SPHERE:
                    default:
                        bounds = new BoundingSphere();
                        untransformedBounds = new BoundingSphere();
                        worldBounds = new BoundingSphere();
                        boundsType = BoundsType.SPHERE;
                }
            }
        }
        else
        {
            instanceBoundsTypeHint = null;
            ignoreBounds = true;
            bounds = null;
            untransformedBounds = null;
            worldBounds = null;
            boundsType = null;
        }
    }
    
    /**
     * Constructs a new Node object.
     */
    public Node()
    {
        this( true );
    }
}
