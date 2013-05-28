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
import java.util.HashMap;

/**
 * Provides methods to get information about printable characters.
 * Any printable character is assotiated with an index to be used in an array
 * of the size [total number of printable chars].
 * 
 * This CharIndex caches the printables in a HashMap and uses GC-optimized
 * hashing to do everything in O(1).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
class HashOptimizedCharIndex extends CharIndex
{
    private static class SingletonCharacter
    {
        private char ch;
        
        public void setChar( char ch )
        {
            this.ch = ch;
        }
        
        public char getChar()
        {
            return ( ch );
        }
        
        /**
         * Returns a hash code for this <code>Character</code>.
         * @return  a hash code value for this object.
         */
        @Override
        public int hashCode()
        {
            return ( (int)ch );
        }
        
        /**
         * Compares this object against the specified object.
         * The result is <code>true</code> if and only if the argument is not
         * <code>null</code> and is a <code>Character</code> object that
         * represents the same <code>char</code> value as this object.
         *
         * @param   obj   the object to compare with.
         * @return  <code>true</code> if the objects are the same;
         *          <code>false</code> otherwise.
         */
        @Override
        public boolean equals( Object obj )
        {
            if ( obj instanceof Character )
            {
                return ( ch == ( (Character)obj ).charValue() );
            }
            else if ( obj instanceof SingletonCharacter )
            {
                return ( ch == ( (SingletonCharacter)obj ).getChar() );
            }
            
            return ( false );
        }
        
        public SingletonCharacter( char ch )
        {
            setChar( ch );
        }
    }
    
    private Charset charset;
    private HashMap< Object, Integer > hashCache;
    private SingletonCharacter singChar = new SingletonCharacter( '\0' );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryStrategy getMemoryStrategy()
    {
        return ( MemoryStrategy.HASH_OPTIMIZED  );
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
        return ( hashCache.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex( char ch )
    {
        singChar.setChar( ch );
        
        final Integer index = hashCache.get( singChar );
        
        if ( index == null )
            return ( -1 );
        
        return ( index.intValue() );
    }
    
    /**
     * Computes and returns a HashMap of Characters mapped to Integers.<br>
     * Any printable char will be contained an this Map.
     * 
     * @param charset the charset to use
     * 
     * @return the cache-HashMap
     */
    private HashMap< Object, Integer > createHashCache( Charset charset )
    {
        final int n = getTotalNumberOfCharacters();
        final HashMap< Object, Integer > result = new HashMap< Object, Integer >();
        final CharBuffer charBuffer = CharBuffer.allocate( 1 );
        final ByteBuffer byteBuffer = ByteBuffer.allocate( 2 );
        final CharsetEncoder encoder = charset.newEncoder();
        int numPrintables = 0;
        
        for ( char ch = 0; ch < n; ch++ )
        {
            if ( isPrintable( ch, encoder, charBuffer, byteBuffer ) )
            {
                result.put( new Character( ch ), new Integer( numPrintables++ ) );
            }
            
            charBuffer.clear();
            byteBuffer.clear();
        }
        
        return ( result );
    }
    
    public HashOptimizedCharIndex( Charset charset )
    {
        super();
        
        this.charset = charset;
        this.hashCache = createHashCache( charset );
    }
}
