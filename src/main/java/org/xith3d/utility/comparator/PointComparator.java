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

import org.openmali.vecmath2.Tuple3f;

/**
 * @author David Yazel
 */
public class PointComparator implements Comparator< Tuple3f >
{
    public PointComparator()
    {
    }
    
    public static int comparePoints( Tuple3f p1, Tuple3f p2 )
    {
        if ( p1.equals( p2 ) )
        {
            return ( 0 );
        }
        
        if ( p1.getX() < p2.getX() )
        {
            return ( -1 );
        }
        else if ( p1.getX() > p2.getX() )
        {
            return ( 1 );
        }
        else
        {
            if ( p1.getY() < p2.getY() )
            {
                return ( -1 );
            }
            else if ( p1.getY() > p2.getY() )
            {
                return ( 1 );
            }
            else
            {
                if ( p1.getZ() < p2.getZ() )
                {
                    return ( -1 );
                }
                else if ( p1.getZ() > p2.getZ() )
                {
                    return ( 1 );
                }
                else
                {
                    return ( 0 );
                }
            }
        }
    }
    
    public static int comparePoints( Tuple3f p1, Tuple3f p2, float epsilon )
    {
        if ( p1.epsilonEquals( p2, epsilon ) )
            return ( 0 );
        
        return ( comparePoints( p1, p2 ) );
    }
    
    public int compare( Tuple3f o1, Tuple3f o2 )
    {
        return ( comparePoints( o1, o2 ) );
    }
}
