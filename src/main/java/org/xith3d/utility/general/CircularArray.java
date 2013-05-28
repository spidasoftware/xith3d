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
package org.xith3d.utility.general;

/**
 * Java's internal LinkedList implementation is extremely GC expensive.
 * This Array-embedded implemention is better.<br>
 * The CircularArray is a special kind of a Stack. Elements are dropped in (push)
 * in the array at a certain position and are removed (pop) at another certain position.
 * These position are generally not the backing array's first and last positions,
 * but can be anywhere. When the array's last element has been written, the array's
 * first element will be the next and so forth.
 * So the position of an element in the backing array is (i % array.length).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CircularArray< T >
{
    private final T[] array;
    private int top;
    private int end;
    private int size;
    
    /**
     * @return the backing array's length.
     */
    public final int size()
    {
        return ( size );
    }
    
    /**
     * @return <code>true</code>, if the CircularArray holds at least one Element.
     */
    public final boolean isEmpty()
    {
        return ( size == 0 );
    }
    
    /**
     * Stores the object at the next position. This might overwrite an existing
     * Element, if the backing array is not big enough. (No auto-growing)
     * 
     * @param o
     */
    public final void push( T o )
    {
        if ( size == array.length )
            throw new ArrayIndexOutOfBoundsException( "The CircularArray is full" );
        
        final int pos = ++end % array.length;
        array[ pos ] = o;
    }
    
    /**
     * Sneaks the CircularArray, if there is any Element to be retrieved by pop(),
     * but does not remove it.
     * 
     * @see #pop()
     * 
     * @return the element to be retrieved by pop(), but does not remove it.
     */
    public final T peek()
    {
        if ( isEmpty() )
            return ( null );
        
        return ( array[ top ] );
    }
    
    /**
     * Retrieves and removes the next element in question. This is the first
     * element element added and still remaining.
     * 
     * @see #peek()
     */
    public final T pop()
    {
        if ( isEmpty() )
            return ( null );
        
        final T o = peek();
        
        top--;
        if ( top < 0 )
            top = array.length - 1;
        
        return ( o );
    }
    
    /**
     * Creates a new CircularArray with a backing array of the specified size.
     * If (size + 1) elements are stored without removing any, the last element
     * will overwrite the first one.
     * 
     * @param size
     */
    @SuppressWarnings( "unchecked" )
    public CircularArray( int size )
    {
        this.array = (T[])new Object[ size ];
        this.top = 0;
        this.end = -1;
        this.size = 0;
    }
}
