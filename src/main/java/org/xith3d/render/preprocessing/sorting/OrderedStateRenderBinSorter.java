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
package org.xith3d.render.preprocessing.sorting;

import java.util.Comparator;

import org.xith3d.render.preprocessing.OrderedState;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.RenderBin.DynamicAtomArray;
import org.xith3d.render.states.StateSortable;
import org.xith3d.render.states.StateSortableMap;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.utility.comparator.Sorter;

/**
 * This sorter sorts RenderAtoms by StateUnits (state-sorting)
 * with respect to the Node's {@link OrderedState}.
 * 
 * @author YVG
 * @author Marvin Froehlich (aka Qudus)
 */
public class OrderedStateRenderBinSorter extends RenderBinSorter implements Comparator< RenderAtom< ? > >
{
    private final StatePriorities priorities;
    
    public final StatePriorities getPriorities()
    {
        return ( priorities );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compare( RenderAtom< ? > atom1, RenderAtom< ? > atom2 )
    {
        // In case of Shapes in a SharedGroup and Links...
        if ( atom1 == atom2 )
            return ( 0 );
        
        final int stateType1 = atom1.getStateType();
        final int stateType2 = atom2.getStateType();
        
        if ( stateType1 < stateType2 )
            return ( -1 );
        else if ( stateType1 > stateType2 )
            return ( 1 );
        
        if ( atom1.getSortableStates().hash == atom2.getSortableStates().hash )
            return ( 0 );
        
        /*
         * Check the ordered status.
         * This needs to be done outside the shader loop
         * or else it is doing a redundent check every iteration.
         */
        final OrderedState os1 = atom1.getOrderedState();
        final OrderedState os2 = atom2.getOrderedState();
        
        if ( os1 != os2 )
        {
            if ( os1 == null )
                return ( -1 );
            else if ( os2 == null )
                return ( +1 );
            
            // ok if we get there then both shapes are
            // in ordered groups, so we need to compare their
            // ordered states
            
            int index = 0;
            while ( ( index < os1.depth ) && ( index < os2.depth ) )
            {
                if ( os1.orderIds[ index ] < os2.orderIds[ index ] )
                    return ( -1 );
                else if ( os1.orderIds[ index ] > os2.orderIds[ index ] )
                    return ( +1 );
                
                index++;
            }
            
            // ok so all the states matched for the minimum depth
            // of their relative state, so the one with less depth
            // goes first
            
            if ( os1.depth < os2.depth )
                return ( -1 );
            else if ( os1.depth > os2.depth )
                return ( +1 );
            
            // if we get here then they are exactly the same but for
            // insane reason the OrderState objects are not the same...
            
            throw new Error( "Completely impossible order state condition (comparing " + os1 + " and " + os2 + ")" );
        }
        
        // if we get here then we need to sort based on the state priorities
        
        final StateSortableMap ssMap1 = atom1.getSortableStates();
        final StateSortableMap ssMap2 = atom2.getSortableStates();
        
        for ( int i = 0; i < priorities.numStatePriorities; i++ )
        {
            // check the states
            
            final int prio = priorities.statePriorities[ i ];
            
            final StateSortable ss1 = ssMap1.map[ prio ];
            final StateSortable ss2 = ssMap2.map[ prio ];
            
            if ( ss1 == ss2 )
                continue;
            if ( ss1 == null )
                return ( -1 );
            else if ( ss2 == null )
                return ( +1 );
            
            final long bid1 = ssMap1.mapID[ prio ];
            final long bid2 = ssMap2.mapID[ prio ];
            if ( bid1 < bid2 )
                return ( -1 );
            else if ( bid1 > bid2 )
                return ( +1 );
        }
        
        return ( 0 );
    }
    
    @Override
    public boolean equals( Object o )
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortRenderBin( RenderBin renderBin, Transform3D viewTransform )
    {
        final DynamicAtomArray atoms = renderBin.getAtoms();
        
        // Arrays.sort( atoms.getRawArray(), 0, atoms.size(), this );
        Sorter.quickSort( atoms.getRawArray(), 0, atoms.size() - 1, this );
        // head = Sorter.mergeSort( head, this );
    }
    
    public OrderedStateRenderBinSorter( StatePriorities priorities )
    {
        this.priorities = priorities;
    }
    
    public OrderedStateRenderBinSorter()
    {
        this( StatePriorities.getDefaultPriorities() );
    }
}
