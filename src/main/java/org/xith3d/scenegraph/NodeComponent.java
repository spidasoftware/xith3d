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

import org.xith3d.render.Canvas3D;
import org.xith3d.render.CanvasPeer;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;

/**
 * NodeComponent is the base class for all node component objects in
 * scene graph Node objects.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class NodeComponent extends SceneGraphObject
{
    /**
     * controls whether a NodeComponent object is dupicated
     * or referenced on a call to cloneTree.
     */
    private boolean duplicateOnCloneTree = false;
    
    private boolean changed = true;
    
    private final boolean isGlobalOptionsRelevant;
    
    private short lastKnownGlobalOptionsChangeID = (short)( GlobalOptions.getInstance().getChangeID() - 1 );
    
    private ScenegraphModificationsListener modListener = null;
    
    public void setModListener( ScenegraphModificationsListener modListener )
    {
        this.modListener = modListener;
    }
    
    public final ScenegraphModificationsListener getModListener()
    {
        return ( modListener );
    }
    
    protected void setChanged( boolean changed )
    {
        if ( !changed )
        {
            lastKnownGlobalOptionsChangeID = GlobalOptions.getInstance().getChangeID();
        }
        
        if ( changed == this.changed )
            return;
        
        this.changed = changed;
        
        if ( changed && ( modListener != null ) )
            modListener.onNodeComponentChanged( this );
    }
    
    public boolean isChanged()
    {
        return ( changed );
    }
    
    public boolean isChanged2()
    {
        return ( changed || ( isGlobalOptionsRelevant && ( lastKnownGlobalOptionsChangeID < GlobalOptions.getInstance().getChangeID() ) ) );
    }
    
    /**
     * controls whether a NodeComponent object is dupicated
     * or referenced on a call to cloneTree. by default this flag
     * is set to false. This means that the NodeComponent object will
     * not be duplicated on a call to cloneTree, newly creted leaf
     * nodes will refer to the original NodComponent object instead.<p>
     *
     * If the cloneTree method is called with forceDuplicate parameter
     * set to true the duplicationOnCloneTree flag is ignored and the
     * entire scene graph is duplicated.
     */
    public final void setDuplicateOnCloneTree( boolean b )
    {
        duplicateOnCloneTree = b;
    }
    
    /**
     * @return the state of the duplicateOnCloneTree flag.
     */
    public final boolean getDuplicateOnCloneTree()
    {
        return duplicateOnCloneTree;
    }
    
    /**
     * 
     * @param original
     * @param forceDuplicate
     */
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        duplicateOnCloneTree = original.getDuplicateOnCloneTree();
        // live = false;
        setName( original.getName() );
        // what with userData ?? - how to duplicate it on force ?
        setUserData( original.getUserData() );
    }
    
    public abstract NodeComponent cloneNodeComponent( boolean forceDuplicate );
    
    /**
     * This method frees OpenGL resources (names) for all this NodeComponent and
     * all child-components.
     * 
     * @param canvasPeer
     */
    public abstract void freeOpenGLResources( CanvasPeer canvasPeer );
    
    /**
     * This method frees OpenGL resources (names) for all this NodeComponent and
     * all child-components.
     * 
     * @param canvas
     */
    public final void freeOpenGLResources( Canvas3D canvas )
    {
        if ( canvas.getPeer() == null )
            throw new Error( "The given Canvas3D is not linked to a CanvasPeer." );
        
        freeOpenGLResources( canvas.getPeer() );
    }
    
    /**
     * Constructs a new NodeComponent object.
     */
    public NodeComponent( boolean isGlobalOptionsRelevant )
    {
        super();
        
        this.isGlobalOptionsRelevant = isGlobalOptionsRelevant;
    }
}
