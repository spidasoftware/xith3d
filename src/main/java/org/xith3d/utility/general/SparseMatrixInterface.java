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
 * Interface for sparse matrices
 *
 * :Id: SparseMatrixInterface.java,v 1.3 2003/02/24 00:13:51 wurp Exp $
 *
 * :Log: SparseMatrixInterface.java,v $
 * Revision 1.3  2003/02/24 00:13:51  wurp
 * Formatted all java code for cvs (strictSunConvention.xml)
 *
 * Revision 1.2  2001/06/20 04:05:42  wurp
 * added log4j.
 *
 * Revision 1.1  2000/12/05 20:49:14  wizofid
 * Committing new engine code
 *
 * @author David Yazel
 */
public interface SparseMatrixInterface< T >
{
    /**
     * Inserts an object into the matrix at the coordinates specified.
     */
    void insertAt( int x, int z, T obj );
    
    /**
     * Remove the object at the specified location.  If there is no object there
     * then nothing happens.
     */
    void removeAt( int x, int z );
    
    /**
     * @return the object at the location specicied.  If no object is there then
     * null is returned;
     */
    T elementAt( int x, int z );
}
