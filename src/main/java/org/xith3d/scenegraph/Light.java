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

import org.jagatoo.datatypes.Enableable;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.vecmath2.Colorf;

import org.xith3d.render.CanvasPeer;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * Light defines a common set of attributes that control lights. A light has
 * associated with it a color, a state (on/off), and a Bounds object that
 * specifies the region of influence for the light.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Light extends Leaf implements Enableable
{
    private static int nextLightID = 1;
    
    public static final Colorf DEFAULT_COLOR = Colorf.WHITE;
    
    private final int lightID = nextLightID++;
    
    /**
     * The influencing bounds for this object.
     */
    private Bounds influencingBounds = null;
    
    /**
     * The influencing bounding leaf for this object.
     */
    private BoundingLeaf boundingLeaf = null;
    
    /**
     * The color for this object.
     */
    private Colorf color = new Colorf();
    
    /**
     * The state for this object.
     */
    private boolean enabled;
    
    public final int getLightID()
    {
        return ( lightID );
    }
    
    /**
     * Sets the state for this object.
     */
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
    
    /**
     * Gets the state for this object.
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * This is a Light. You cannot mark a light pickable!
     */
    @Override
    public final void setPickable( boolean value )
    {
    }
    
    /**
     * Sets the color for this object.
     */
    public void setColor( Colorf color )
    {
        if ( color == null )
            throw new IllegalArgumentException( "color must not be null" );
        
        this.color.set( color );
    }
    
    /**
     * Sets the color for this object.
     */
    public void setColor( float r, float g, float b )
    {
        this.color.set( r, g, b );
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
    public void setInfluencingBounds( Bounds region )
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
    public void setInfluencingBoundingLeaf( BoundingLeaf boundingLeaf )
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
     * Traverses the scenegraph from this node on. If this Node is a Group it
     * will recusively run through each child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    @Override
    public boolean traverse( DetailedTraversalCallback callback )
    {
        return ( callback.traversalOperationCommon( this ) && callback.traversalOperation( this ) && callback.traversalOperationAfter( this ) && callback.traversalOperationCommonAfter( this ) );
    }
    
    protected static final Colorf getCol( Colorf col )
    {
        return ( ( col == null ) ?  DEFAULT_COLOR :  col );
    }
    
    /**
     * Constructs a new Light object with the specified color.
     * 
     * @param enabled
     * @param colorR
     * @param colorG
     * @param colorB
     */
    public Light( boolean enabled, float colorR, float colorG, float colorB )
    {
        super();
        
        this.color.set( colorR, colorG, colorB );
        this.enabled = enabled;
        
        super.setPickable( false );
    }
    
    /**
     * Constructs a new Light object with the specified color.
     * 
     * @param colorR
     * @param colorG
     * @param colorB
     */
    public Light( float colorR, float colorG, float colorB )
    {
        this( true, colorR, colorG, colorB );
    }
    
    /**
     * Constructs a new Light object with a default color of white.
     * 
     * @param enabled
     */
    public Light( boolean enabled )
    {
        this( enabled, DEFAULT_COLOR.getRed(), DEFAULT_COLOR.getGreen(), DEFAULT_COLOR.getBlue() );
    }
    
    /**
     * Constructs a new Light object with a default color of white.
     */
    public Light()
    {
        this( true );
    }
}
