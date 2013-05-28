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
package org.xith3d.render;

import org.xith3d.scenegraph.*;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;

/**
 * The ScenegraphModificationsManager is responsible for handling any change in the Scenegraph.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
/*public */class ScenegraphModificationsManager implements ScenegraphModificationsListener
{
    private final Renderer renderer;
    private boolean anythingChanged = true;
    
    public final boolean hasAnythingChanged()
    {
        return ( anythingChanged );
    }
    
    public void resetAnythingChanged()
    {
        this.anythingChanged = false;
    }
    
    @SuppressWarnings( "unused" )
    private final void setRootDirty( Node node )
    {
        BranchGroup root = node.getRoot();
        
        if ( root != null )
        {
            _SG_PrivilegedAccess.forceRefill( root, true );
        }
    }
    
    /*
     * #####################################################################
     * ### Methods from ScenegraphModificationsListener
     * #####################################################################
     */

    public void onBranchGraphAdded( BranchGroup branchGraph )
    {
        _SG_PrivilegedAccess.forceRefill( branchGraph, true );
        
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onBranchGraphAdded( branchGraph );
        }
    }
    
    public void onBranchGraphRemoved( BranchGroup branchGraph )
    {
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onBranchGraphRemoved( branchGraph );
        }
    }
    
    public void onNodePropertyChanged( Node node, String property )
    {
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onNodePropertyChanged( node, property );
        }
    }
    
    public void onChildAddedToGroup( GroupNode group, Node child )
    {
        child.setModListener( this );
        
        anythingChanged = true;
        
        final BranchGroup branchGroup = group.getRoot();
        if ( branchGroup != null )
        {
            _SG_PrivilegedAccess.forceRecull( branchGroup, true );
            
            /*
            final List< RenderPass > renderPasses = renderer.getRenderPasses( branchGroup );
            final int n = renderPasses.size();
            
            if ( ( child instanceof Light ) || ( child instanceof Fog ) )
            {
                for ( int i = 0; i < n; i++ )
                {
                    final RenderBinProvider binProvider = renderPasses.get( i ).getRenderBinProvider();
                    atomsCollector.collectAtoms( group, binProvider, null, glCaps );
                }
            }
            else if ( child instanceof Leaf )
            {
                for ( int i = 0; i < n; i++ )
                {
                    final RenderBinProvider binProvider = renderPasses.get( i ).getRenderBinProvider();
                    atomsCollector.collectAtom( (Leaf)child, binProvider, null, glCaps );
                }
            }
            else if ( child instanceof GroupNode )
            {
                for ( int i = 0; i < n; i++ )
                {
                    final RenderBinProvider binProvider = renderPasses.get( i ).getRenderBinProvider();
                    atomsCollector.collectAtoms( (GroupNode)child, binProvider, null, glCaps );
                }
            }
            */
            
            if ( renderer.getScenegraphModificationListeners().size() > 0 )
            {
                for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                    renderer.getScenegraphModificationListeners().get( i ).onChildAddedToGroup( group, child );
            }
        }
    }
    
    public void onChildRemovedFromGroup( GroupNode group, Node child )
    {
        anythingChanged = true;
        
        final BranchGroup branchGroup = group.getRoot();
        
        if ( branchGroup != null )
        {
            _SG_PrivilegedAccess.forceRecull( branchGroup, true );
            
            /*
            if ( child instanceof Leaf )
            {
                atomsRemover.removeAtom( (Leaf)child, renderer, null );
            }
            else if ( child instanceof GroupNode )
            {
                atomsRemover.removeAtoms( (GroupNode)child, renderer, null );
            }
            */
            
            /*
            child.setBinProvider( null );
            child.setLayeredNode( null );
            child.setModListener( null );
            */

            if ( renderer.getScenegraphModificationListeners().size() > 0 )
            {
                for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                    renderer.getScenegraphModificationListeners().get( i ).onChildRemovedFromGroup( group, child );
            }
        }
    }
    
    public void onSwitchWhichChildChanged( Switch sw, int oldValue, int whichChild )
    {
        anythingChanged = true;
    }
    
    public void onStateModifierContainmentChanged( GroupNode group, boolean oldValue, boolean newValue )
    {
        // TODO: ?
        
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onStateModifierContainmentChanged( group, oldValue, newValue );
        }
    }
    
    public void onScissorRectChanged( GroupNode group, ScissorRect oldValue, ScissorRect newValue )
    {
        // TODO ?
        
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onScissorRectChanged( group, oldValue, newValue );
        }
    }
    
    public void onClipperChanged( GroupNode group, Clipper oldValue, Clipper newValue )
    {
        // TODO ?
        
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onClipperChanged( group, oldValue, newValue );
        }
    }
    
    public void onTransformChanged( TransformGroup tg, Transform3D transform )
    {
        //tg.updateWorldTransform();
        
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onTransformChanged( tg, transform );
        }
    }
    
    public void onNodeComponentChanged( NodeComponent comp )
    {
        anythingChanged = true;
        
        if ( renderer.getScenegraphModificationListeners().size() > 0 )
        {
            for ( int i = 0; i < renderer.getScenegraphModificationListeners().size(); i++ )
                renderer.getScenegraphModificationListeners().get( i ).onNodeComponentChanged( comp );
        }
    }
    
    public ScenegraphModificationsManager( Renderer renderer )
    {
        this.renderer = renderer;
    }
}
