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
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * Provides methods to get information about printable characters.
 * Any printable character is assotiated with an index to be used in an array
 * of the size [total number of printable chars].
 * 
 * @author Kevin Finley (aka horati)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class CharIndex
{
    /**
     * For expressing values within the MemoryStrategy:
     * <ul>
     *     <li>p is the number of printable characters</li>
     *     <li>a is the number of all possible characters</li>
     * </ul>
     */
    public static enum MemoryStrategy
    {
        /**
         * This strategy does no pre-collections of printable chars.<br>
         * getIndex() is executed in O(a).<br>
         * getNumberOfPrintableChars() is executed in O(a) the first time and then in O(1).<br>
         * Memory consumption is O(1).
         */
        SPACE_OPTIMIZED,
        
        /**
         * This strategy uses BitSets to determine the information.<br>
         * getIndex() is executed in O(1) if the character is not found or (cheap) O(a) if the character is found.<br>
         * getNumberOfPrintableChars() is executed in O(1).<br>
         * Memory consumption is O(a/8).
         */
        BITSET_OPTIMIZED,
        
        /**
         * This is a GC optimized detection way.<br>
         * getIndex() is executed in O(1).<br>
         * getNumberOfPrintableChars() is executed in O(1).<br>
         * Memory consumption is O(a*6).
         */
        GC_OPTIMIZED,
        
        /**
         * This strategy uses GC-optimized hashing to retrieve and store
         * the information.<br>
         * getIndex() is executed in O(1).<br>
         * getNumberOfPrintableChars() is executed in O(1).<br>
         * Memory consumption is O(p*25).
         */
        HASH_OPTIMIZED,
        
        /**
         * This strategy uses GC-optimized binary searching to retrieve and store
         * the information.<br>
         * getIndex() is executed in O(log p).<br>
         * getNumberOfPrintableChars() is executed in O(1).<br>
         * Memory consumption is O(p*2).
         */
        BINSEARCH_OPTIMIZED;
    }
    
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    private static Charset charset = null;
    private static CharsetEncoder encoder = null;
    private static CharBuffer charBuffer = null;
    private static ByteBuffer byteBuffer = null;
    
    protected static boolean isPrintable( char ch, CharsetEncoder encoder, CharBuffer charBuffer, ByteBuffer byteBuffer )
    {
        boolean result = false;
        
        if ( ch == ' ' )
        {
            result = true;
        }
        else
        {
            switch ( Character.getType( ch ) )
            {
                case Character.CONTROL:
                case Character.FORMAT:
                case Character.LINE_SEPARATOR:
                case Character.MODIFIER_LETTER:
                case Character.PARAGRAPH_SEPARATOR:
                case Character.PRIVATE_USE:
                case Character.SPACE_SEPARATOR:
                case Character.SURROGATE:
                case Character.UNASSIGNED:
                    // leave it false
                    break;
                case Character.COMBINING_SPACING_MARK:
                case Character.CONNECTOR_PUNCTUATION:
                case Character.CURRENCY_SYMBOL:
                case Character.DASH_PUNCTUATION:
                case Character.DECIMAL_DIGIT_NUMBER:
                case Character.ENCLOSING_MARK:
                case Character.END_PUNCTUATION:
                case Character.FINAL_QUOTE_PUNCTUATION:
                case Character.INITIAL_QUOTE_PUNCTUATION:
                case Character.LETTER_NUMBER:
                case Character.LOWERCASE_LETTER:
                case Character.MATH_SYMBOL:
                case Character.MODIFIER_SYMBOL:
                case Character.NON_SPACING_MARK:
                case Character.OTHER_LETTER:
                case Character.OTHER_NUMBER:
                case Character.OTHER_PUNCTUATION:
                case Character.OTHER_SYMBOL:
                case Character.START_PUNCTUATION:
                case Character.TITLECASE_LETTER:
                case Character.UPPERCASE_LETTER:
                    result = true;
                    break;
                default:
                    assert false : "Has the Unicode specification been updated since this code was written?";
            }
            
            if ( result )
            {
                charBuffer.clear();
                byteBuffer.clear();
                charBuffer.append( ch );
                charBuffer.rewind();
                CoderResult cr = encoder.encode( charBuffer, byteBuffer, true );
                if ( cr.isUnmappable() )
                {
                    result = false;
                }
            }
        }
        
        return ( result );
    }
    
    /**
     * Simply determines, if the given char is printable without any optimizations.
     * 
     * @param ch the questionary char
     * @param charset the Charset to use
     * 
     * @return true, if the char is printable
     */
    public static boolean isPrintable( char ch, Charset charset )
    {
        if ( charset == null )
        {
            if ( CharIndex.charset == null )
                charset = DEFAULT_CHARSET;
            else
                charset = CharIndex.charset;
            
            if ( !CharIndex.charset.equals( charset ) )
                encoder = null;
        }
        else if ( CharIndex.charset != null )
        {
            if ( !CharIndex.charset.equals( charset ) )
                encoder = null;
        }
        
        if ( encoder == null )
        {
            CharIndex.charset = charset;
            CharIndex.encoder = charset.newEncoder();
            CharIndex.encoder.onUnmappableCharacter( CodingErrorAction.REPORT );
            CharIndex.encoder.onMalformedInput( CodingErrorAction.REPORT );
            CharIndex.charBuffer = CharBuffer.allocate( 1 );
            CharIndex.byteBuffer = ByteBuffer.allocate( 2 );
        }
        
        return ( isPrintable( ch, encoder, charBuffer, byteBuffer ) );
    }
    
    /**
     * Simply determines, if the given char is printable without any optimizations.
     * 
     * @param ch the questionary char
     * @param charset the Charset to use
     * 
     * @return true, if the char is printable
     */
    public static boolean isPrintable( char ch, String charset )
    {
        return ( isPrintable( ch, Charset.forName( charset ) ) );
    }
    
    /**
     * Simply determines, if the given char is printable without any optimizations.
     * 
     * @param ch the questionary char
     * 
     * @return true, if the char is printable
     */
    public static boolean isPrintable( char ch )
    {
        return ( isPrintable( ch, ( CharIndex.charset != null ) ? CharIndex.charset : DEFAULT_CHARSET ) );
    }
    
    /**
     * @return the MemoryStrategy used by this CharIndex
     */
    public abstract MemoryStrategy getMemoryStrategy();
    
    /**
     * @return the Charset used by this CharIndex
     */
    public abstract Charset getCharset();
    
    /**
     * @return the total number of chars, that is cared of.
     */
    public abstract int getTotalNumberOfCharacters();
    
    /**
     * @return the number of printable chars in this CharIndex.
     */
    public abstract int getNumberOfPrintableChars();
    
    /**
     * If you want to allocate a smaller array [size: getNumberOfPrintableChars()],
     * You can use the int returned by this method as the index in this array.
     * 
     * @param ch the questionary char
     * 
     * @return the index in a smaller array
     * 
     * @see #getNumberOfPrintableChars()
     */
    public abstract int getIndex( char ch );
    
    protected CharIndex()
    {
    }
    
    /**
     * Creates a space-optimized CharIndex.
     * 
     * @see MemoryStrategy#SPACE_OPTIMIZED
     */
    public static CharIndex createSpaceOptimizedCharIndex( Charset charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( new SpaceOptimizedCharIndex( charset ) );
    }
    
    /**
     * Creates a space-optimized CharIndex.
     * 
     * @see MemoryStrategy#SPACE_OPTIMIZED
     */
    public static CharIndex createSpaceOptimizedCharIndex( String charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( createSpaceOptimizedCharIndex( Charset.forName( charset ) ) );
    }
    
    /**
     * Creates a space-optimized CharIndex.
     * 
     * @see MemoryStrategy#SPACE_OPTIMIZED
     */
    public static CharIndex createSpaceOptimizedCharIndex()
    {
        return ( createSpaceOptimizedCharIndex( Charset.defaultCharset() ) );
    }
    
    /**
     * Creates a BitSet-optimized CharIndex.
     * 
     * @see MemoryStrategy#BITSET_OPTIMIZED
     */
    public static CharIndex createBitSetOptimizedCharIndex( Charset charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( new BitSetOptimizedCharIndex( charset ) );
    }
    
    /**
     * Creates a BitSet-optimized CharIndex.
     * 
     * @see MemoryStrategy#BITSET_OPTIMIZED
     */
    public static CharIndex createBitSetOptimizedCharIndex( String charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( createBitSetOptimizedCharIndex( Charset.forName( charset ) ) );
    }
    
    /**
     * Creates a BitSet-optimized CharIndex.
     * 
     * @see MemoryStrategy#BITSET_OPTIMIZED
     */
    public static CharIndex createBitSetOptimizedCharIndex()
    {
        return ( createBitSetOptimizedCharIndex( Charset.defaultCharset() ) );
    }
    
    /**
     * Creates a GC-optimized CharIndex.
     * 
     * @see MemoryStrategy#GC_OPTIMIZED
     */
    public static CharIndex createGCOptimizedCharIndex( Charset charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( new GCOptimizedCharIndex( charset ) );
    }
    
    /**
     * Creates a GC-optimized CharIndex.
     * 
     * @see MemoryStrategy#GC_OPTIMIZED
     */
    public static CharIndex createGCOptimizedCharIndex( String charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( createGCOptimizedCharIndex( Charset.forName( charset ) ) );
    }
    
    /**
     * Creates a GC-optimized CharIndex.
     * 
     * @see MemoryStrategy#GC_OPTIMIZED
     */
    public static CharIndex createGCOptimizedCharIndex()
    {
        return ( createGCOptimizedCharIndex( Charset.defaultCharset() ) );
    }
    
    /**
     * Creates a hash-optimized CharIndex.
     * 
     * @see MemoryStrategy#HASH_OPTIMIZED
     */
    public static CharIndex createHashOptimizedCharIndex( Charset charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( new HashOptimizedCharIndex( charset ) );
    }
    
    /**
     * Creates a hash-optimized CharIndex.
     * 
     * @see MemoryStrategy#HASH_OPTIMIZED
     */
    public static CharIndex createHashOptimizedCharIndex( String charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( createHashOptimizedCharIndex( Charset.forName( charset ) ) );
    }
    
    /**
     * Creates a hash-optimized CharIndex.
     * 
     * @see MemoryStrategy#HASH_OPTIMIZED
     */
    public static CharIndex createHashOptimizedCharIndex()
    {
        return ( createHashOptimizedCharIndex( Charset.defaultCharset() ) );
    }
    
    /**
     * Creates a search-optimized CharIndex.
     * 
     * @see MemoryStrategy#BINSEARCH_OPTIMIZED
     */
    public static CharIndex createBinSearchOptimizedCharIndex( Charset charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( new BinarySearchCharIndex( charset ) );
    }
    
    /**
     * Creates a search-optimized CharIndex.
     * 
     * @see MemoryStrategy#BINSEARCH_OPTIMIZED
     */
    public static CharIndex createBinSearchOptimizedCharIndex( String charset )
    {
        assert ( charset != null ) : "Charset must not be null.";
        
        return ( createBinSearchOptimizedCharIndex( Charset.forName( charset ) ) );
    }
    
    /**
     * Creates a search-optimized CharIndex.
     * 
     * @see MemoryStrategy#BINSEARCH_OPTIMIZED
     */
    public static CharIndex createBinSearchOptimizedCharIndex()
    {
        return ( createBinSearchOptimizedCharIndex( Charset.defaultCharset() ) );
    }
    
    /*
    public static void main( String[] args )
    {
        final long t0 = System.currentTimeMillis();
        //CharIndex ci = CharIndex.createSpaceOptimizedCharIndex( "ISO-8859-1" );
        //CharIndex ci = CharIndex.createBitSetOptimizedCharIndex( "ISO-8859-1" );
        //CharIndex ci = CharIndex.createGCOptimizedCharIndex( "ISO-8859-1" );
        CharIndex ci = CharIndex.createHashOptimizedCharIndex( "ISO-8859-1" );
        //CharIndex ci = CharIndex.createSearchOptimizedCharIndex( "ISO-8859-1" );
        final long t1 = System.currentTimeMillis();
        for ( int i = 0; i < 10000; i++ )
        {
            ci.getIndex( 'Z' );
        }
        final long t2 = System.currentTimeMillis();
        
        System.out.printf( "Construction %d ms\tExecution %d ms\tPer operation %f ms\n", t1 - t0, t2 - t1, ( t2 - t1 ) / 10000. );
        System.out.println( ci.getIndex( 'A' ) );
        System.out.println( ci.getNumberOfPrintableChars() );
    }
    */
}
