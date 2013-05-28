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
package org.xith3d.utility.characters;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * This implementation stores only the printable characters in an array to
 * minimize memory consumption as well as GC overhead.  To calculate the
 * index, it executes a binary search; therefore, the index value is
 * calculated in O( log n ).
 * 
 * @author Kevin Finley (aka horati)
 */
class BinarySearchCharIndex extends CharIndex
{
    private final Charset charset;
    private final char[] sortedPrintableCharacters;
    private final int definedCharacters;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Charset getCharset()
    {
        return ( charset );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex( char ch )
    {
        int offset = Arrays.binarySearch( sortedPrintableCharacters, ch );
        return ( Math.max( -1, offset ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryStrategy getMemoryStrategy()
    {
        return ( MemoryStrategy.BINSEARCH_OPTIMIZED  );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfPrintableChars()
    {
        return ( sortedPrintableCharacters.length  );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalNumberOfCharacters()
    {
        return ( definedCharacters );
    }
    
    public BinarySearchCharIndex( Charset charset )
    {
        super();
        
        final char max = (char)0xffff; // use this value instead of '\uffff'
        // due to crashes on some platforms
        int definedCharacters = 0;
        int printableCharacters = 0;
        char[] chars = new char[ max + 1 ];
        
        for ( char ch = 0; ch <= Short.MAX_VALUE; ch++ )
        {
            if ( Character.isDefined( ch ) )
            {
                definedCharacters++;
                if ( CharIndex.isPrintable( ch, charset ) )
                {
                    chars[ printableCharacters++ ] = ch;
                }
            }
        }
        char[] result = new char[ printableCharacters ];
        System.arraycopy( chars, 0, result, 0, printableCharacters );
        
        this.charset = charset;
        this.definedCharacters = definedCharacters;
        this.sortedPrintableCharacters = result;
    }
}
