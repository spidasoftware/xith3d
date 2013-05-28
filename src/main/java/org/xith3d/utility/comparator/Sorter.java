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
package org.xith3d.utility.comparator;

import java.util.Comparator;

import org.jagatoo.datatypes.Chainable;
import org.jagatoo.datatypes.DoublyChainable;

/**
 * Holds static methods to sort lists
 * 
 * @author Gosling, Lilian (c_lilian) [quickSort]
 * @author Marvin Froehlich (aka Qudus) [mergeSort]
 */
public final class Sorter
{
    private static < E > void swap( E[] a, int i, int j )
    {
        E tmp = a[ i ];
        a[ i ] = a[ j ];
        a[ j ] = tmp;
    }
    
    /**
     * This is a generic version of C.A.R Hoare's Quick Sort
     * algorithm.  This will handle arrays that are already
     * sorted, and arrays with duplicate keys.<BR>
     *
     * If you think of a one dimensional array as going from
     * the lowest index on the left to the highest index on the right
     * then the parameters to this function are lowest index or
     * left and highest index or right.  The first time you call
     * this function it will be with the parameters 0, a.length - 1.
     *
     * @param a       an object array
     * @param lo0     left boundary of array partition
     * @param hi0     right boundary of array partition
     * @param comp    the comparator used
     */
    public static < E > void quickSort( E[] a, int lo0, int hi0, Comparator< E > comp )
    {
        int lo = lo0;
        int hi = hi0;
        E mid;
        
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
                while ( ( lo < hi0 ) && ( comp.compare( a[ lo ], mid ) < 0 ) )
                {
                    ++lo;
                }
                
                /* find an element that is smaller than or equal to
                 * the partition element starting from the right Index.
                 */
                while ( ( hi > lo0 ) && ( comp.compare( a[ hi ], mid ) > 0 ) )
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
                quickSort( a, lo0, hi, comp );
            
            /* If the left index has not reached the right side of array
             * must now sort the right partition.
             */
            if ( lo < hi0 )
                quickSort( a, lo, hi0, comp );
        }
    }
    
    /**
     * This is a generic merge sort algorithm, that sorts a linked list.<br>
     * <br>
     * It is a ported version of the one that I found on
     * http://www.chiark.greenend.org.uk/~sgtatham/algorithms/listsort.html
     * 
     * @param <E> the type of the elements to be sorted
     * 
     * @param head the head of the linked list
     * @param comp the Comparator to compare the elements
     * @return the new head of the linked list
     */
    public static < E extends Chainable< E >> E mergeSort( E head, Comparator< E > comp )
    {
        final boolean isCircular = false;
        
        if ( head == null )
            return ( null );
        
        E p, q, e, tail, oldHead;
        int inSize, merges, pSize, qSize, i;
        
        inSize = 1;
        
        while ( true )
        {
            p = head;
            oldHead = head; // only used for circular linkage
            head = null;
            tail = null;
            
            merges = 0; // count of merges we do in this pass
            
            while ( p != null )
            {
                merges++; // there exists a merge to be done
                // step inSize places along from p
                q = p;
                pSize = 0;
                for ( i = 0; i < inSize; i++ )
                {
                    pSize++;
                    
                    if ( isCircular )
                        q = ( q.getNext() == oldHead ? null : q.getNext() );
                    else
                        q = q.getNext();
                    
                    if ( q == null )
                        break;
                }
                
                // if q hasn't fallen off end, we have two lists to merge
                qSize = inSize;
                
                /* now we have two lists; merge them */
                while ( ( pSize > 0 ) || ( ( qSize > 0 ) && ( q != null ) ) )
                {
                    // decide whether next element of merge comes from p or q
                    if ( pSize == 0 )
                    {
                        // p is empty; e must come from q.
                        e = q;
                        q = q.getNext();
                        qSize--;
                        
                        if ( ( isCircular ) && ( q == oldHead ) )
                            q = null;
                    }
                    else if ( ( qSize == 0 ) || ( q == null ) )
                    {
                        // q is empty; e must come from p.
                        e = p;
                        p = p.getNext();
                        pSize--;
                        
                        if ( ( isCircular ) && ( p == oldHead ) )
                            p = null;
                    }
                    else if ( comp.compare( p, q ) <= 0 )
                    {
                        // First element of p is lower or equal --> e must come from p.
                        e = p;
                        p = p.getNext();
                        pSize--;
                        
                        if ( ( isCircular ) && ( p == oldHead ) )
                            p = null;
                    }
                    else
                    {
                        /* First element of q is lower; e must come from q. */
                        e = q;
                        q = q.getNext();
                        qSize--;
                        
                        if ( ( isCircular ) && ( q == oldHead ) )
                            q = null;
                    }
                    
                    // add the next element to the merged list
                    if ( tail != null )
                    {
                        tail.setNext( e );
                    }
                    else
                    {
                        head = e;
                    }
                    
                    if ( oldHead instanceof DoublyChainable )
                    {
                        // Maintain reverse pointers in a doubly linked list.
                        ( (DoublyChainable< E >)e ).setPrevious( tail );
                    }
                    
                    tail = e;
                }
                
                // now p has stepped `insize' places along, and q has too
                p = q;
            }
            
            if ( isCircular )
            {
                tail.setNext( head );
                if ( oldHead instanceof DoublyChainable )
                {
                    // Maintain reverse pointers in a doubly linked list.
                    ( (DoublyChainable< E >)head ).setPrevious( tail );
                }
            }
            else
                tail.setNext( null );
            
            // If we have done only one merge, we're finished.
            if ( merges <= 1 ) // allow for nmerges==0, the empty list case
                return head;
            
            // Otherwise repeat, merging lists twice the size
            inSize *= 2;
        }
    }
    
    private Sorter()
    {
    }
}
