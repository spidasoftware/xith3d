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

import org.openmali.spatial.bounds.Bounds;
import org.openmali.vecmath2.Colorf;

import org.xith3d.render.CanvasPeer;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * Fog defines a common set of attributes that contorl fog, or depth
 * cueing, in the scene.
 * 
 * @author David Yazel
 */
public abstract class Fog extends Leaf
{
    private static int nextFogID = 1;
    
    private final int fogID = nextFogID++;
    
    /**
     * The color for this object.
     */
    private final Colorf color = new Colorf();
    
    /**
     * The influencing bounds for this object.
     */
    private Bounds influencingBounds = null;
    
    /**
     * The application bounding leaf for this object.
     */
    private BoundingLeaf boundingLeaf = null;
    
    public final int getFogID()
    {
        return ( fogID );
    }
    
    /**
     * Sets the color for this object.
     */
    public final void setColor( Colorf color )
    {
        this.color.set( color );
    }
    
    /**
     * Sets the color for this object.
     */
    public final Colorf getColor( Colorf color )
    {
        color.set( this.color );
        
        return ( color );
    }
    
    /**
     * Gets the color for this object.
     */
    public final Colorf getColor()
    {
        return ( color.getReadOnly() );
    }
    
    /**
     * Sets the influence bounding region for this object.
     */
    public final void setInfluencingBounds( Bounds region )
    {
        this.influencingBounds = region;
    }
    
    /**
     * Gets the influence bounding region for this object.
     */
    public final Bounds getInfluencingBounds()
    {
        return ( influencingBounds );
    }
    
    /**
     * Sets the influence bounding leaf for this object.
     */
    public final void setInfluencingBoundingLeaf( BoundingLeaf boundingLeaf )
    {
        this.boundingLeaf = boundingLeaf;
    }
    
    /**
     * Sets the influence bounding leaf for this object.
     */
    public final BoundingLeaf getInfluencingBoundingLeaf()
    {
        return ( boundingLeaf );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
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
     * Constructs a new Fog object with a default color of black.
     */
    public Fog()
    {
        super();
    }
    
    /**
     * Constructs a new Fog object with the specified color.
     */
    public Fog( Colorf color )
    {
        this();
        
        this.color.set( color );
    }
}
