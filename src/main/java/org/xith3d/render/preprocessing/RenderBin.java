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

import org.openmali.spatial.bodies.Classifier;

import org.xith3d.render.states.StateSortable;
import org.xith3d.scenegraph.Node;

/**
 * A collection of RenderAtoms that will be prioritized and sorted for
 * rendering.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public final class RenderBin
{
    public static final class DynamicAtomArray
    {
        private RenderAtom< ? >[] atoms;
        private int lastSize;
        private int size;
        
        public final RenderAtom< ? >[] getRawArray()
        {
            return ( atoms );
        }
        
        /**
         * @return the actual current size of the dynamic array
         */
        public final int size()
        {
            return ( size );
        }
        
        /**
         * Clears the array by resetting the size indicator to "0".
         */
        public final void clear()
        {
            lastSize = size;
            for ( int i = 0; i < size; i++ ) atoms[i] = null;
            size = 0;
        }
        
        /**
         * Ensures that all atoms ever been in the render bin are freed for garbage collection
         */
        public final void cleanUp()
        {
            if ( lastSize <= size )
                return;
            
            //for ( int i = size; i < lastSize; i++ ) atoms[i] = null;
        }
        
        private final void ensureCapacity( int cap )
        {
            final int oldCapacity = atoms.length;
            if ( cap > oldCapacity )
            {
                final RenderAtom< ? >[] oldData = atoms;
                final int newCapacity = ( oldCapacity * 3 ) / 2 + 1;
                atoms = new RenderAtom[ newCapacity ];
                System.arraycopy( oldData, 0, atoms, 0, oldCapacity );
            }
        }
        
        /**
         * Appends the specified element to the array and increases the size
         * indicator.
         * 
         * @return the same given element back again (convenience)
         */
        public final RenderAtom< ? > append( RenderAtom< ? > atom )
        {
            ensureCapacity( size + 1 );
            
            atoms[ size ] = atom;
            //atom.setLocalIndex( size );
            
            size++;
            
            return ( atom );
        }
        
        /**
         * @param index
         * 
         * @return the element at the specified index.
         */
        public final RenderAtom< ? > get( int index )
        {
            //if ( index >= size )
            //    throw new ArrayIndexOutOfBoundsException( String.valueOf( index ) ) );
            
            return ( atoms[ index ] );
        }
        
        public DynamicAtomArray( int initialCapacity )
        {
            this.atoms = new RenderAtom[ initialCapacity ];
            this.size = 0;
        }
    }
    
    private final RenderBinType type;
    private final String name;
    
    private final DynamicAtomArray atoms;
    private int iterationPointer = 0;
    
    public final RenderBinType getType()
    {
        return ( type );
    }
    
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * @return the DynamicAtomArray containing all RenderBuckets in this RenderBin.
     */
    public final DynamicAtomArray getAtoms()
    {
        return ( atoms );
    }
    
    /**
     * @return the number of currently contained RenderAtoms
     */
    public final int size()
    {
        return ( atoms.size() );
    }
    
    /**
     * Removes all Atoms from the RenderBin.
     */
    public final void clear()
    {
        atoms.clear();
    }
    
    /**
     * Removes unused atoms from the RenderBin
     */
    public final void shrink()
    {
        atoms.cleanUp();
    }

    /**
     * @return the RenderAtom with the specified index in the list.
     * 
     * @param index the deserved Atom's RenderBucket's index.
     */
    public final RenderAtom< ? extends Node > getAtom( int index )
    {
        return ( atoms.get( index ) );
    }
    
    /**
     * Must be called at the beginning when the Atoms list is to be iterated. 
     */
    public final void resetIterationPointer()
    {
        iterationPointer = 0;
    }
    
    /**
     * @return the next RenderAtom in the iteration.<br>
     *         Returns <i>null</i>, if no more atoms are available.
     */
    public final RenderAtom< ? extends Node > getNextAtom()
    {
        if ( iterationPointer >= atoms.size() )
            return ( null );
        
        return ( atoms.get( iterationPointer++ ) );
    }
    
    /**
     * Adds a RenderAtom to the chain of RenderAtoms. Atoms in this chain
     * (similar to a LinkedList) will be rendered.
     * 
     * @param atom the RenderAtom to be added
     * @param classify
     * @param frameId
     */
    public final void addAtom( RenderAtom< ? extends Node > atom, Classifier.Classification classify, long frameId )
    {
        atoms.append( atom );
        atom.setClassification( classify );
    }
    
    public void dump( org.xith3d.render.preprocessing.sorting.StatePriorities priorities )
    {
        System.out.println( "" );
        
        RenderAtom< ? extends Node > atom;
        for ( int j = 0; j < atoms.size(); j++ )
        {
            System.out.print( "Atom " + j + ": " );
            
            atom = atoms.get( j );
            
            for ( int i = 0; i < priorities.numStatePriorities; i++ )
            {
                StateSortable ss1 = atom.getSortableStates().map[ priorities.statePriorities[ i ] ];
                if ( ss1 == null )
                    System.out.print( " " + priorities.statePriorities[ i ] + ":-1" );
                else
                {
                    long bid1 = atom.getSortableStates().map[ priorities.statePriorities[ i ] ].getStateId();
                    System.out.print( " " + priorities.statePriorities[ i ] + ":" + bid1 );
                }
            }
            System.out.println( "" );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( this.getClass().getSimpleName() + " \"" + getName() + "\" with " + size() + " atoms" );
    }
    
    public RenderBin( RenderBinType type, String name, int initialCapacity )
    {
        super();
        
        this.type = type;
        this.name = name;
        
        this.atoms = new DynamicAtomArray( initialCapacity );
    }
}
