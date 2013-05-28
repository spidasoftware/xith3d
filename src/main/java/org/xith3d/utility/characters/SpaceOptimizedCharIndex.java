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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * Provides methods to get information about printable characters.
 * Any printable character is assotiated with an index to be used in an array
 * of the size [total number of printable chars].
 * 
 * This CharIndex works without any caching and does everything in O(n).
 * 
 * @author Kevin Finley (aka horati)
 * @author Marvin Froehlich (aka Qudus)
 */
class SpaceOptimizedCharIndex extends CharIndex
{
    private Charset charset;
    private CharsetEncoder encoder;
    private CharBuffer charBuffer;
    private ByteBuffer byteBuffer;
    
    private int numPrintables = -1;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryStrategy getMemoryStrategy()
    {
        return ( MemoryStrategy.SPACE_OPTIMIZED  );
    }
    
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
    public int getTotalNumberOfCharacters()
    {
        return ( Short.MAX_VALUE + 1 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfPrintableChars()
    {
        if ( numPrintables >= 0 )
            return ( numPrintables );
        
        numPrintables = 0;
        
        final int n = getTotalNumberOfCharacters();
        for ( char i = 0; i <= n; i++ )
        {
            if ( isPrintable( i, encoder, charBuffer, byteBuffer ) )
            {
                numPrintables++;
            }
            
            charBuffer.clear();
            byteBuffer.clear();
        }
        
        return ( numPrintables );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex( char ch )
    {
        int result = -1;
        
        for ( char i = 0; i <= ch; i++ )
        {
            if ( isPrintable( i, encoder, charBuffer, byteBuffer ) )
            {
                result++;
            }
            
            charBuffer.clear();
            byteBuffer.clear();
        }
        
        return ( result );
    }
    
    public SpaceOptimizedCharIndex( Charset charset )
    {
        super();
        
        this.charset = charset;
        this.encoder = charset.newEncoder();
        this.encoder.onUnmappableCharacter( CodingErrorAction.REPORT );
        this.encoder.onMalformedInput( CodingErrorAction.REPORT );
        this.charBuffer = CharBuffer.allocate( 1 );
        this.byteBuffer = ByteBuffer.allocate( 2 );
    }
}