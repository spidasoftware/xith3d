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

/**
 * Compares two Strings ignoring case.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public final class IgnoreCaseComparator implements Comparator< String >
{
    public static final IgnoreCaseComparator INSTANCE = new IgnoreCaseComparator();
    
    public static final boolean equalsIC( String string1, String string2 )
    {
        if ( string2.length() != string1.length() )
            return ( false );
        
        for ( int i = 0; i < string2.length(); i++ )
        {
            if ( Character.toLowerCase( string1.charAt( i ) ) != Character.toLowerCase( string2.charAt( i ) ) )
                return ( false );
        }
        
        return ( true );
    }
    
    /**
     * Compares two Strings ignoring case.
     * 
     * @param string1
     * @param string2
     */
    public static final int compareIC( String string1, String string2 )
    {
        final int n = Math.min( string1.length(), string2.length() );
        
        for ( int i = 0; i < n; i++ )
        {
            final char ch1 = Character.toLowerCase( string1.charAt( i ) );
            final char ch2 = Character.toLowerCase( string2.charAt( i ) );
            
            if ( ch1 < ch2 )
                return ( -1 );
            else if ( ch1 > ch2 )
                return ( +1 );
        }
        
        if ( string1.length() < string2.length() )
            return ( -1 );
        
        if ( string1.length() > string2.length() )
            return ( +1 );
        
        return ( 0 );
    }
    
    /**
     * Compares two Strings ignoring case.
     * 
     * @param string1
     * @param string2
     */
    public final int compare( String string1, String string2 )
    {
        return ( compareIC( string1, string2 ) );
    }
    
    private IgnoreCaseComparator()
    {
    }
}
