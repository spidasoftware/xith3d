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

import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.RenderBin.DynamicAtomArray;
import org.xith3d.render.states.StateSortable;
import org.xith3d.render.states.StateSortableMap;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.utility.comparator.Sorter;

/**
 * This sorter sorts RenderAtoms by StateUnits (state-sorting).
 * 
 * @author YVG
 * @author Marvin Froehlich (aka Qudus)
 */
public class StateRenderBinSorter extends RenderBinSorter implements Comparator< RenderAtom< ? > >
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
            return ( +1 );
        
        if ( atom1.getSortableStates().hash == atom2.getSortableStates().hash )
            return ( 0 );
        
        
        // if we get here then we need to sort based on the state priorities
        
        final StateSortableMap ssMap1 = atom1.getSortableStates();
        final StateSortableMap ssMap2 = atom2.getSortableStates();
        
        for ( int i = 0; i < priorities.numStatePriorities; i++ )
        {
            // check the states
            final int prio = priorities.statePriorities[ i ];
            
            final StateSortable ss1 = ssMap1.map[ prio ];
            final StateSortable ss2 = ssMap2.map[ prio ];
            
            //System.out.println( ss1 + ", " + ss2 );
            
            if ( ss1 == ss2 )
                continue;
            if ( ss1 == null )
                return ( -1 );
            else if ( ss2 == null )
                return ( +1 );
            
            final long stateID1 = ssMap1.mapID[ prio ];
            final long stateID2 = ssMap2.mapID[ prio ];
            if ( stateID1 < stateID2 )
                return ( -1 );
            else if ( stateID1 > stateID2 )
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
    
    public StateRenderBinSorter( StatePriorities priorities )
    {
        this.priorities = priorities;
    }
    
    public StateRenderBinSorter()
    {
        this( StatePriorities.getDefaultPriorities() );
    }
}
