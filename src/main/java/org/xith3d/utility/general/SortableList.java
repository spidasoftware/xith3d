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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This is a java.util.List implementation, that provides <b>insitu</b> sorting
 * of the elements.<br>
 * This is very useful to work GC-friendly with Lists.<br>
 * It also has singleton Iterators.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class SortableList< E extends Comparable< E >> implements List< E >
{
    private class SLIterator implements Iterator< E >
    {
        private int cursor = -1;
        
        public boolean hasNext()
        {
            return ( cursor < size - 1 );
        }
        
        public E next()
        {
            return ( get( ++cursor ) );
        }
        
        public void remove()
        {
            SortableList.this.remove( cursor );
        }
        
        public void reset()
        {
            this.cursor = -1;
        }
    }
    
    private class SLListIterator implements ListIterator< E >
    {
        private int cursor = 0;
        private int lastCursor = -1;
        
        public boolean hasNext()
        {
            return ( cursor < size );
        }
        
        public boolean hasPrevious()
        {
            return ( cursor > 0 );
        }
        
        public E next()
        {
            lastCursor = cursor;
            return ( get( cursor++ ) );
        }
        
        public int nextIndex()
        {
            return ( cursor );
        }
        
        public E previous()
        {
            lastCursor = cursor - 1;
            
            try
            {
                return ( get( --cursor ) );
            }
            catch ( ArrayIndexOutOfBoundsException e )
            {
                cursor++;
                lastCursor = cursor;
                
                throw e;
            }
        }
        
        public int previousIndex()
        {
            return ( cursor - 1 );
        }
        
        public void add( E e )
        {
            SortableList.this.add( cursor, e );
            cursor++;
        }
        
        public void remove()
        {
            SortableList.this.remove( cursor );
        }
        
        public void set( E e )
        {
            SortableList.this.set( lastCursor, e );
        }
        
        public void reset()
        {
            this.cursor = 0;
            this.lastCursor = -1;
        }
    }
    
    private Object[] array;
    private int size;
    private SLIterator iterator = new SLIterator();
    private SLListIterator listIterator = new SLListIterator();
    
    /**
     * Increases the capacity of this <tt>SortableList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity( int minCapacity )
    {
        final int oldCapacity = array.length;
        
        if ( minCapacity > oldCapacity )
        {
            final Object[] oldArray = array;
            final int newCapacity = ( oldCapacity * 3 ) / 2 + 1;
            array = new Object[ newCapacity ];
            System.arraycopy( oldArray, 0, array, 0, oldCapacity );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return ( size );
    }
    
    /**
     * {@inheritDoc}
     */
    public void add( int index, E element )
    {
        ensureCapacity( index + 1 );
        
        if ( index == size )
        {
            array[ index ] = element;
        }
        else if ( index > size )
        {
            Arrays.fill( array, size, index - 1, null );
            array[ index ] = element;
        }
        else
        {
            System.arraycopy( array, index, array, index + 1, size - index - 1 );
            array[ index ] = element;
        }
        
        size = index + 1;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean add( E element )
    {
        add( size, element );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public boolean addAll( Collection< ? extends E > coll )
    {
        ensureCapacity( size + coll.size() );
        
        for ( Object o: coll )
        {
            add( (E)o );
        }
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public boolean addAll( int index, Collection< ? extends E > coll )
    {
        ensureCapacity( size + coll.size() );
        
        if ( index >= size )
        {
            if ( index > size )
            {
                Arrays.fill( array, size, index - 1, null );
                
                size = index;
            }
            
            for ( Object o: coll )
            {
                array[ size++ ] = (E)o;
            }
        }
        else
        {
            System.arraycopy( array, index, array, index + coll.size(), size - index - 1 );
            
            for ( Object o: coll )
            {
                array[ index++ ] = (E)o;
            }
            
            size += coll.size();
        }
        
        return ( coll.size() > 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        size = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean contains( Object o )
    {
        for ( int i = 0; i < size; i++ )
        {
            if ( array[ i ] == o )
                return ( true );
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsAll( Collection< ? > coll )
    {
        int result = 0;
        
        for ( Object o: coll )
        {
            if ( contains( o ) )
                result++;
        }
        
        return ( result == coll.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public E get( int index )
    {
        if ( index >= size )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return ( (E)array[ index ] );
    }
    
    /**
     * {@inheritDoc}
     */
    public int indexOf( Object o )
    {
        if ( o == null )
        {
            for ( int i = 0; i < size; i++ )
            {
                if ( array[ i ] == null )
                    return ( i );
            }
        }
        else
        {
            for ( int i = 0; i < size; i++ )
            {
                if ( o.equals( array[ i ] ) )
                    return ( i );
            }
        }
        
        return ( -1 );
    }
    
    /**
     * {@inheritDoc}
     */
    public int lastIndexOf( Object o )
    {
        if ( o == null )
        {
            for ( int i = size - 1; i >= 0; i-- )
            {
                if ( array[ i ] == null )
                    return ( i );
            }
        }
        else
        {
            for ( int i = size - 1; i >= 0; i-- )
            {
                if ( o.equals( array[ i ] ) )
                    return ( i );
            }
        }
        
        return ( -1 );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return ( size == 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    public Iterator< E > iterator()
    {
        this.iterator.reset();
        
        return ( iterator );
    }
    
    /**
     * {@inheritDoc}
     */
    public ListIterator< E > listIterator( int index )
    {
        this.listIterator.reset();
        
        return ( listIterator );
    }
    
    /**
     * {@inheritDoc}
     */
    public ListIterator< E > listIterator()
    {
        return ( listIterator( 0 ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public E remove( int index )
    {
        if ( index >= size )
            throw new ArrayIndexOutOfBoundsException( index );
        
        final E old = get( index );
        
        if ( index < size - 1 )
            System.arraycopy( array, index + 1, array, index, size - index - 1 );
        
        size--;
        
        return ( old );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean remove( Object o )
    {
        final int index = indexOf( o );
        
        if ( index >= 0 )
        {
            remove( index );
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean removeAll( Collection< ? > coll )
    {
        boolean result = false;
        
        for ( Object o: coll )
        {
            if ( remove( o ) )
                result = true;
        }
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean retainAll( Collection< ? > coll )
    {
        boolean result = false;
        
        for ( int i = size - 1; i >= 0; i-- )
        {
            if ( !coll.contains( array[ i ] ) )
            {
                remove( array[ i ] );
                result = true;
            }
        }
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    public E set( int index, E element )
    {
        if ( index >= size )
            throw new ArrayIndexOutOfBoundsException( index );
        
        final E old = get( index );
        
        array[ index ] = element;
        
        return ( old );
    }
    
    /**
     * {@inheritDoc}
     */
    public List< E > subList( int fromIndex, int toIndex )
    {
        throw new UnsupportedOperationException( "not yet implemented." );
    }
    
    /**
     * {@inheritDoc}
     */
    public Object[] toArray()
    {
        //E[] result = ( E[] ) Array.newInstance( array.getClass().getComponentType(), size );
        Object[] result = new Object[ size ];
        
        System.arraycopy( array, 0, result, 0, size() );
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public < T > T[] toArray( T[] a )
    {
        if ( a.length < size )
            a = (T[])Array.newInstance( a.getClass().getComponentType(), size );
        
        System.arraycopy( array, 0, a, 0, size );
        if ( a.length > size )
            Arrays.fill( a, size, a.length, null );
        
        return ( a );
    }
    
    /**
     * Returns a shallow copy of this <tt>SortableList</tt> instance. 
     * (The elements themselves are not copied.)
     *
     * @return a clone of this <tt>SortableList</tt> instance
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public Object clone()
    {
        try
        {
            SortableList< E > clone = (SortableList< E >)super.clone();
            clone.array = (E[])Array.newInstance( array.getClass().getComponentType(), array.length );
            System.arraycopy( array, 0, clone.array, 0, array.length );
            return ( clone );
        }
        catch ( CloneNotSupportedException e )
        {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
    
    /**
     * Sets the list <b>insutu</b> using a mergesort.
     * 
     * @see Arrays#sort(Object[])
     */
    public void sort()
    {
        if ( size > 1 )
        {
            // Waaahh! Why inclusive/exclusive? Blame on sun here! (size is needed instead of (size-1))
            Arrays.sort( array, 0, size );
        }
    }
    
    /**
     * Creates a new SortableList with an initial size of <tt>initialSize</tt>.
     * 
     * @param initialCapacity
     */
    public SortableList( int initialCapacity )
    {
        this.array = new Object[ initialCapacity ];
        this.size = 0;
    }
    
    /**
     * Creates a new SortableList with an initial size of 16.
     */
    public SortableList()
    {
        this( 16 );
    }
}
