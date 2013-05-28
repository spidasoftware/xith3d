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

import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * TransformGroup node specifies a single spatial transformation,
 * via a Transform3D object, that can position, orient and scale
 * all of its children. The effects of transformations in the scene
 * graph are cumulative.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class TransformGroup extends Group implements Transformable
{
    /**
     * The transform for this object.
     */
    private final Transform3D worldTransform = new Transform3D();
    private final Transform3D transform = new Transform3D();
    private final Point3f position = new Point3f();
    
    protected final void onTransformChanged()
    {
        if ( getModListener() != null )
            getModListener().onTransformChanged( this, transform );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setTransform( Transform3D t )
    {
        if ( SceneGraph.CHECK_FOR_ILLEGAL_MODIFICATION )
            SceneGraph.checkForIllegalModification( this );
        
        if ( ( transform != t ) && ( t != null ) )
        {
            transform.set( t );
        }
        
        //updateTransformGroup();
        updateWorldTransform();
        boundsDirty = true;
        if ( ( getParent() != null ) || ( numChildren() > 0 ) )
            updateBoundsCheap( false, true, true, true );
        
        /*
        updateBounds();
        updateWorldBounds();
        */

        if ( transform != null )
            transform.transformGroup = this;
        
        if ( getModListener() != null )
            getModListener().onTransformChanged( this, transform );
    }
    
    /**
     * Just reapplies this {@link TransformGroup}'s Transform to mark it dirty.
     */
    public final void updateTransform()
    {
        this.setTransform( this.getTransform() );
    }
    
    /**
     * @see #setTransform(Transform3D)
     * 
     * @param t
     */
    public final void setLocalTransform( Transform3D t )
    {
        setTransform( t );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Transform3D getTransform()
    {
        return ( transform );
    }
    
    /**
     * @see #getTransform()
     */
    public final Transform3D getLocalTransform()
    {
        return ( transform );
    }
    
    public final void getTransform( Transform3D t )
    {
        t.set( transform.getMatrix4f() );
    }
    
    /**
     * @see #getTransform(Transform3D)
     * 
     * @param t
     */
    public final void getLocalTransform( Transform3D t )
    {
        t.set( transform.getMatrix4f() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPosition( float posX, float posY, float posZ )
    {
        transform.setTranslation( posX, posY, posZ );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPosition( Tuple3f position )
    {
        setPosition( position.getX(), position.getY(), position.getZ() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getPosition( Tuple3f position )
    {
        transform.getTranslation( position );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Point3f getPosition()
    {
        transform.getTranslation( position );
        
        return ( position );
    }
    
    final Transform3D getInlinedWorldTransform()
    {
        return ( worldTransform );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Transform3D getWorldTransform()
    {
        return ( worldTransform );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected TransformGroup newInstance()
    {
        boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        final TransformGroup newTG = new TransformGroup();
        Node.globalIgnoreBounds = gib;
        
        newTG.setTransform( this.transform );
        
        return ( newTG );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean traverse( DetailedTraversalCallback callback )
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
     * Constructs a new TransformGroup object with the specified transform.
     */
    public TransformGroup( Transform3D t )
    {
        super();
        
        setTransform( t );
    }
    
    /**
     * Constructs a new TransformGroup object with the specified transform.
     */
    public TransformGroup( Matrix4f transform )
    {
        super();
        
        getTransform().set( transform );
    }
    
    /**
     * Constructs a new TransformGroup object.
     */
    public TransformGroup()
    {
        this( (Transform3D)null );
    }
    
    public TransformGroup( float translationX, float translationY, float translationZ )
    {
        this();
        
        this.getTransform().setTranslation( translationX, translationY, translationZ );
        this.updateTransform();
    }
    
    public TransformGroup( Tuple3f translation )
    {
        this( translation.getX(), translation.getY(), translation.getZ() );
    }
}
