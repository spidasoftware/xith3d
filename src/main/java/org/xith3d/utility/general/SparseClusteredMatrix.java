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

import java.util.ArrayList;

/**
 * Implements a sparse matrix interface optimized for data that is likely to be
 * clustered in 2dimensional space, where most of the empty space is surrounding
 * a cluster of elements.
 * 
 * 
 * :Id: SparseClusteredMatrix.java,v 1.5 2003/02/24 00:13:51 wurp Exp $
 * 
 * :Log: SparseClusteredMatrix.java,v $
 * Revision 1.5  2003/02/24 00:13:51  wurp
 * Formatted all java code for cvs (strictSunConvention.xml)
 * 
 * Revision 1.4  2001/06/20 04:05:42  wurp
 * added log4j.
 * 
 * Revision 1.3  2001/01/28 07:52:20  wurp
 * Removed <dollar> from Id and Log in log comments.
 * Added several new commands to AdminApp
 * Unfortunately, several other changes that I have lost track of.  Try diffing this
 * version with the previous one.
 * 
 * Revision 1.2  2000/12/16 22:07:33  wurp
 * Added Id and Log to almost all of the files that didn't have it.  It's
 * possible that the script screwed something up.  I did a commit and an update
 * right before I ran the script, so if a file is screwed up you should be able
 * to fix it by just going to the version before this one.
 * 
 * @author David Yazel
 */
public class SparseClusteredMatrix< T > implements SparseMatrixInterface< T >
{
    private SparseVector< SparseVector< T >> v; // vector of sparse vectors
    
    public SparseClusteredMatrix()
    {
        v = new SparseVector< SparseVector< T >>();
    }
    
    /**
     * Inserts an object into the matrix at the coordinates specified.
     */
    public void insertAt( int x, int z, T obj )
    {
        SparseVector< T > col = v.elementAt( x );
        
        if ( col == null )
        {
            col = new SparseVector< T >();
            v.insertAt( x, col );
        }
        
        col.insertAt( z, obj );
    }
    
    /**
     * Remove the object at the specified location.  If there is no object there
     * then nothing happens.
     */
    public void removeAt( int x, int z )
    {
        SparseVector< T > col = v.elementAt( x );
        
        if ( col == null )
        {
            return;
        }
        
        col.removeAt( z );
    }
    
    /**
     * @return the object at the location specicied.  If no object is there then
     * null is returned;
     */
    public T elementAt( int x, int z )
    {
        SparseVector< T > col = v.elementAt( x );
        
        if ( col == null )
        {
            return null;
        }
        
        return ( col.elementAt( z ) );
    }
    
    /**
     * Fills two ArrayLists, one with elements within certain bounds, the other with the ones
     * that are within the bounds.
     */
    public void elementsWithin( ArrayList< T > within, ArrayList< T > without, int x1, int z1, int x2, int z2 )
    {
        within = new ArrayList< T >();
        without = new ArrayList< T >();
        
        // first step is to sort the columns
        ArrayList< SparseVector< T >> colWithin = new ArrayList< SparseVector< T >>();
        ArrayList< SparseVector< T >> colWithout = new ArrayList< SparseVector< T >>();
        v.sortElements( colWithin, colWithout, x1, x2 );
        
        // now step through all the "within" columns and union their objects
        for ( int i = 0; i < colWithin.size(); i++ )
            colWithin.get( i ).sortElements( within, without, z1, z2 );
        
        // now step through all the "without" columns and union their objects
        for ( int i = 0; i < colWithout.size(); i++ )
            colWithout.get( i ).sortElements( within, without, z1, z2 );
    }
}
