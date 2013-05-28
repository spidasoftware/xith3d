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
 * A BranchGroup is the root of a SceneGraph. More exactly it is the branch
 * group of a RenderPass. It is not intended to be used as a child of another
 * Group.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public class BranchGroup extends GroupNode
{
    private SceneGraph sceneGraph = null;
    
    private boolean refillForeced = true;
    private boolean recullForeced = true;
    
    /**
     * Marks this BranchGroup as refill-forced.<br>
     * If the BranchGroup is marked refill-foreced, it will be completely retraversed
     * in the next rendering pass.
     * 
     * @param force
     */
    final void forceRefill( boolean force )
    {
        this.refillForeced = force;
        
        /*
        if ((force) && (locale != null))
            locale.forceRefill( true );
        */
    }
    
    /**
     * @return this BranchGroup's refill-forced flag state.<br>
     * If the BranchGroup is marked refill-foreced, it will be completely retraversed
     * in the next rendering pass.
     */
    final boolean isRefillForeced()
    {
        return ( refillForeced );
    }
    
    /**
     * Sets this BranchGroup's recull-forced flag state.<br>
     * If the BranchGroup is marked recull-forced, it will be reculled
     * in the next rendering pass.
     * 
     * @param force
     */
    final void forceRecull( boolean force )
    {
        this.recullForeced = force;
        
        /*
        if ((force) && (locale != null))
            locale.forceRecull( true );
        */
    }
    
    /**
     * @return this BranchGroup's recull-forced flag state.<br>
     * If the BranchGroup is marked recull-forced, it will be reculled
     * in the next rendering pass.
     */
    final boolean isRecullForeced()
    {
        return ( recullForeced );
    }
    
    final void setSceneGraph( SceneGraph sceneGraph )
    {
        this.sceneGraph = sceneGraph;
        
        this.setLive( this.sceneGraph != null );
    }
    
    public final SceneGraph getSceneGraph()
    {
        return ( sceneGraph );
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
     * {@inheritDoc}
     */
    @Override
    protected BranchGroup newInstance()
    {
        boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        BranchGroup bg = new BranchGroup();
        Node.globalIgnoreBounds = gib;
        
        return ( bg );
    }
    
    /**
     * Constructs a new BranchGroup instance and directly
     * adds the given child Node to it.<br>
     * <br>
     * <i>This is a convenience method.</i>
     */
    protected BranchGroup( GroupNode hostGroup, Node firstChild )
    {
        super( hostGroup );
        
        if ( firstChild != null )
            this.addChild( firstChild );
    }
    
    /**
     * Constructs a new BranchGroup instance and directly
     * adds the given child Node to it.<br>
     * <br>
     * <i>This is a convenience method.</i>
     * 
     * @param firstChild
     */
    public BranchGroup( Node firstChild )
    {
        this( null, firstChild );
    }
    
    /**
     * Constructs a new BranchGroup instance.
     */
    public BranchGroup()
    {
        this( null, null );
    }
}
