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
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.utility.comparator.Sorter;

/**
 * This sorter sorts the RenderBin from closest to furthest using bounding shpere center
 * as reference point.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class OrderedBackToFrontRenderBinSorter extends RenderBinSorter implements Comparator< RenderAtom< ? > >
{
    /**
     * {@inheritDoc}
     */
    public int compare( RenderAtom< ? > atom1, RenderAtom< ? > atom2 )
    {
        // check the ordered status
        
        OrderedState os1 = atom1.getOrderedState();
        OrderedState os2 = atom2.getOrderedState();
        
        if ( os1 != os2 )
        {
            if ( os1 == null )
                return ( -1 );
            else if ( os2 == null )
                return ( 1 );
            
            // ok if we get there then both shapes are
            // in ordered groups, so we need to compare their
            // ordered states
            
            int index = 0;
            while ( ( index < os1.depth ) && ( index < os2.depth ) )
            {
                if ( os1.orderIds[ index ] < os2.orderIds[ index ] )
                    return ( -1 );
                else if ( os1.orderIds[ index ] > os2.orderIds[ index ] )
                    return ( 1 );
                
                index++;
            }
            
            // ok so all the states matched for the minimum depth
            // of their relative state, so the one with less depth
            // goes first
            
            if ( os1.depth < os2.depth )
                return ( -1 );
            else if ( os1.depth > os2.depth )
                return ( 1 );
            
            // if we get here then they are exactly the same but for
            // insane reason the OrderState objects are not the same...
            
            throw new Error( "Completely impossible order state condition (comparing " + os1 + " and " + os2 + ")" );
        }
        
        // check the distances
        if ( atom1.getSquaredDistanceToView() > atom2.getSquaredDistanceToView() )
            return ( -1 );
        else if ( atom1.getSquaredDistanceToView() < atom2.getSquaredDistanceToView() )
            return ( 1 );
        else
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
        updateDistancesToView( renderBin, null );
        
        final DynamicAtomArray atoms = renderBin.getAtoms();
        
        //Arrays.sort( atoms.getRawArray(), 0, atoms.size(), this );
        Sorter.quickSort( atoms.getRawArray(), 0, atoms.size() - 1, this );
        //head = Sorter.mergeSort( head, this );
    }
    
    public OrderedBackToFrontRenderBinSorter()
    {
    }
}
