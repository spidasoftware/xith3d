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
package org.xith3d.render.preprocessing;

import org.openmali.FastMath;
import org.openmali.spatial.bodies.Classifier;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.render.ClipperInfo;
import org.xith3d.render.ScissorRect;
import org.xith3d.render.states.StateSortable;
import org.xith3d.render.states.StateSortableMap;
import org.xith3d.render.states.StateUnit;
import org.xith3d.scenegraph.Node;

/**
 * A render atom is a discrete chunk of geometry and shader that will be drawn.
 * Render atoms are submitted to a rendering peer which is API specific.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class RenderAtom< T extends Node > extends StateSortable
{
    private final T node;
    
    private final boolean hasOrderedState;
    
    private final StateUnit[] stateUnits;
    private int numStateUnits;
    
    //private Transform3D transform;
    private Classifier.Classification classify;
    private float squaredDistanceToView;
    private boolean hasPlainDistance = false;
    private float distanceToView;
    private float zValue;
    @SuppressWarnings("unchecked")
    private Comparable customComparable = null;
    
    protected boolean translucent = false;
    
    private StateSortableMap map = new StateSortableMap();
    
    /**
     * Sets the values to compare by.
     * 
     * @param squaredDistanceToView the absolute distance to the current View.
     * @param zValue the z-value of the absolute translation
     * @param customComparable the custom comparable object
     */
    @SuppressWarnings("unchecked")
    public final void setCompareIndicators( float squaredDistanceToView, float zValue, Comparable customComparable )
    {
        this.squaredDistanceToView = squaredDistanceToView;
        this.hasPlainDistance = false;
        this.zValue = zValue;
        this.customComparable = customComparable;
    }
    
    /**
     * @return the absolute (squared) distance to the current View.
     */
    public final float getSquaredDistanceToView()
    {
        return ( squaredDistanceToView );
    }
    
    /**
     * @return the absolute distance to the current View.
     */
    public final float getDistanceToView()
    {
        if ( !hasPlainDistance )
        {
            this.distanceToView = FastMath.sqrt( squaredDistanceToView );
            this.hasPlainDistance = true;
        }
        
        return ( distanceToView );
    }
    
    /**
     * @return the z-value of the absolute translation
     */
    public final float getZValue()
    {
        return ( zValue );
    }
    
    /**
     * Returns the custom comparable object.
     * 
     * @return the custom comparable object
     */
    @SuppressWarnings("unchecked")
    public final Comparable getCustomComparable()
    {
        return ( customComparable );
    }
    
    public void setClassification( Classifier.Classification classify )
    {
        this.classify = classify;
    }
    
    public Classifier.Classification getClassification()
    {
        return ( classify );
    }
    
    /**
     * Gets the position of the geometry (usually from transformed bounds
     * 
     * @param p
     */
    public abstract < Tup extends Tuple3f > Tup getPosition( Tup p );
    
    /**
     * @return the box, which is clipped using glScissor
     */
    public final ScissorRect getScissorRect()
    {
        return ( node.getInheritedNodeAttributes().getScissorRect() );
    }
    
    /**
     * @return the the ClipperInfo
     */
    public final ClipperInfo getClipper()
    {
        return ( node.getInheritedNodeAttributes().getClipper() );
    }
    
    public final T getNode()
    {
        return ( node );
    }
    
    public final int getNumStateUnits()
    {
        return ( numStateUnits );
    }
    
    /**
     * Removes all shaders from the list
     */
    public void clearStateUnits()
    {
        numStateUnits = 0;
    }
    
    public final void updateStateMap( StateUnit stateUnit )
    {
        map.map[ stateUnit.getStateType() ] = stateUnit;
        map.mapID[ stateUnit.getStateType() ] = stateUnit.getStateId();
        map.calcHash();
    }
    
    /**
     * updates a shader to the atom's list of shaders. Does not work well if
     * shader changes its translucent status.
     * 
     * @param stateUnit
     */
    public void updateStateUnit( StateUnit stateUnit )
    {
        final StateUnit oldShader = stateUnits[ stateUnit.getStateType() ];
        
        if ( oldShader == stateUnit )
            return;
        
        if ( oldShader == null )
        {
            stateUnits[ stateUnit.getStateType() ] = stateUnit;
            
            if ( stateUnit.isTranslucent() )
                translucent = true;
        }
        else
        {
            if ( oldShader.isTranslucent() != stateUnit.isTranslucent() )
            {
                throw new UnsupportedOperationException("Updating StateUnits that change translucent status not implemented");
            }
            
            stateUnits[ stateUnit.getStateType() ] = stateUnit;
        }
        
        updateStateMap( stateUnit );
    }
    
    public final StateSortableMap getSortableStates()
    {
        return ( map );
    }
    
    public final StateUnit[] getStateUnits()
    {
        return ( stateUnits );
    }
    
    public final StateUnit getStateUnit( int index )
    {
        return ( stateUnits[ index ] );
    }
    
    public boolean isTranslucent()
    {
        return ( translucent );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long getStateId()
    {
        return ( -1L );
    }
    
    /**
     * @return the ordered state for this render atom. If this is not in an
     *         ordered group then this returns null. The ordered state is
     */
    public final OrderedState getOrderedState()
    {
        if ( !hasOrderedState || ( getNode() == null ) )
            return ( null );
        
        return ( getNode().getOrderedState() );
    }
    
    public RenderAtom( int stateType, T node, boolean hasOrderedState )
    {
        super( stateType );
        
        this.node = node;
        this.hasOrderedState = hasOrderedState;
        this.stateUnits = new StateUnit[ StateUnit.MAX_STATE_TYPES ];
        this.numStateUnits = 0;
        
        updateCachedStateId();
    }
}
