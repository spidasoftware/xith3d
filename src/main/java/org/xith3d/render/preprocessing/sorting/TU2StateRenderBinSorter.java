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

import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.RenderBin.DynamicAtomArray;
import org.xith3d.render.states.StateSortable;
import org.xith3d.render.states.StateSortableMap;
import org.xith3d.scenegraph.Transform3D;

/**
 * This sorter sorts RenderAtoms by TU0 and 1 only (state-sorting)
 * and assumes, that only ShapeAtoms are being sorted.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TU2StateRenderBinSorter extends RenderBinSorter
{
    private static final void swap( RenderAtom<?>[] a, int i, int j )
    {
        RenderAtom<?> tmp = a[ i ];
        a[ i ] = a[ j ];
        a[ j ] = tmp;
    }
    
    private final void quickSort( RenderAtom<?>[] a, int lo0, int hi0 )
    {
        int lo = lo0;
        int hi = hi0;
        RenderAtom<?> mid;
        
        if ( hi0 > lo0 )
        {
            /* Arbitrarily establishing partition element as the midpoint of
             * the array.
             */
            mid = a[ ( lo0 + hi0 ) >>> 1 ];
            
            // loop through the array until indices cross
            while ( lo <= hi )
            {
                /* find the first element that is greater than or equal to
                 * the partition element starting from the left Index.
                 */
                while ( ( lo < hi0 ) && ( compare( a[ lo ], mid ) < 0 ) )
                {
                    ++lo;
                }
                
                /* find an element that is smaller than or equal to
                 * the partition element starting from the right Index.
                 */
                while ( ( hi > lo0 ) && ( compare( a[ hi ], mid ) > 0 ) )
                {
                    --hi;
                }
                
                // if the indexes have not crossed, swap
                if ( lo <= hi )
                {
                    swap( a, lo, hi );
                    
                    ++lo;
                    --hi;
                }
            }
            
            /* If the right index has not reached the left side of array
             * must now sort the left partition.
             */
            if ( lo0 < hi )
                quickSort( a, lo0, hi );
            
            /* If the left index has not reached the right side of array
             * must now sort the right partition.
             */
            if ( lo < hi0 )
                quickSort( a, lo, hi0 );
        }
    }
    
    private static final int compare( RenderAtom< ? > atom1, RenderAtom< ? > atom2 )
    {
        // In case of Shapes in a SharedGroup and Links...
        if ( atom1 == atom2 )
            return ( 0 );
        
        /*
        final int stateType1 = atom1.getStateType();
        final int stateType2 = atom2.getStateType();
        
        if ( stateType1 < stateType2 )
            return ( -1 );
        else if ( stateType1 > stateType2 )
            return ( +1 );
        */
        
        if ( atom1.getSortableStates().hash == atom2.getSortableStates().hash )
            return ( 0 );
        
        
        // if we get here then we need to sort based on the state priorities
        
        final StateSortableMap ssMap1 = atom1.getSortableStates();
        final StateSortableMap ssMap2 = atom2.getSortableStates();
        
        /*
         * Sort by TU 0...
         */
        
        StateSortable ss1 = ssMap1.map[ 2 ];
        StateSortable ss2 = ssMap2.map[ 2 ];
        
        if ( ss1 != ss2 )
        {
            if ( ss1 == null )
                return ( -1 );
            else if ( ss2 == null )
                return ( +1 );
            
            long stateID1 = ssMap1.mapID[ 2 ];
            long stateID2 = ssMap2.mapID[ 2 ];
            if ( stateID1 < stateID2 )
                return ( -1 );
            else if ( stateID1 > stateID2 )
                return ( +1 );
            
            /*
             * Sort by TU 1...
             */
            
            ss1 = ssMap1.map[ 3 ];
            ss2 = ssMap2.map[ 3 ];
            
            if ( ss1 != ss2 )
                return ( 0 );
            if ( ss1 == null )
                return ( -1 );
            else if ( ss2 == null )
                return ( +1 );
            
            stateID1 = ssMap1.mapID[ 2 ];
            stateID2 = ssMap2.mapID[ 2 ];
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
        
        quickSort( atoms.getRawArray(), 0, atoms.size() - 1 );
    }
    
    public TU2StateRenderBinSorter()
    {
    }
}
