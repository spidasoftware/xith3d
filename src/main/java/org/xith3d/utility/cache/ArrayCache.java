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
package org.xith3d.utility.cache;

import java.lang.reflect.Array;

/**
 * Simple cache to handle arrays.
 * 
 * @author David Yazel
 */
public class ArrayCache extends Cache< Array >
{
    public ArrayCache( String name, boolean shareable )
    {
        super( name, shareable );
    }
    
    /**
     * 
     * @param className
     * @param length
     * @return the cached array
     */
    public static Array getArray( Class< ? > className, int length )
    {
        return null;
    }
    
    class ArrayCacheNode implements Cachable< Array >, CacheMatchInterface< Array >
    {
        private Array array;
        
        public ArrayCacheNode( Array data )
        {
            array = data;
        }
        
        /**
         * @return an approximate amount of memory being used by the cached item.
         */
        public long memoryUsed()
        {
            return Array.getLength( array );
        }
        
        /**
         * Called if the cache determines that the object should be flushed through
         * non-use.  This should only be called be the caching system if check in and
         * check out are strictly managed.  The the implementation does nothing in this
         * function then the object will be garbaged collected.
         * 
         * @param o
         */
        public void flush( Array o )
        {
        }
        
        /**
         * @return the name of the cached object. Returning NULL is acceptable. This
         * is primarily used for reporting and tracking cached items.
         */
        public String getName()
        {
            return null;
        }
        
        /**
         * @param o The object in the cache
         * 
         * @return the match against the specified cache item.
         *   The percent that this is a "good" match. zero indicates it
         *   is not an acceptable match. 1 means it is a perfect match. All other
         *   values between 0 and 1 means it is an acceptable match, but the closer
         *   to 1, the better it is.<br>
         *   <br>
         *   An example would be a system which cached NIO byte buffers. If these
         *   buffers are used briefly and then returned you can save a lot of garbage collection,
         *   especially if these are of substantial size (images). The implementation of the
         *   catch match interface might look for a cached buffer that was big enough, but
         *   which was not overly big.
         */
        public float match( Array o )
        {
            if ( ( Array.getLength( o ) == Array.getLength( array ) ) )
            {
                return ( 1f );
            }
            
            return ( 0f );
        }
    }
}
