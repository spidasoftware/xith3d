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
package org.xith3d.utility.memory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.xith3d.utility.cache.Cachable;
import org.xith3d.utility.cache.Cache;
import org.xith3d.utility.cache.CacheMatchInterface;
import org.xith3d.utility.logging.X3DLog;

/**
 * Cached NIO buffer system.
 * 
 * @author David Yazel
 */
public class NioMemoryBuffer< T > implements Cachable< T >
{
    static class Matcher implements CacheMatchInterface< NioMemoryBuffer< ? >>
    {
        int size;
        
        public Matcher( int size )
        {
            this.size = size;
        }
        
        public float match( NioMemoryBuffer< ? > o )
        {
            NioMemoryBuffer< ? > m = (NioMemoryBuffer< ? >)o;
            
            if ( m.size() == size )
                return ( 1 );
            else if ( m.size() < size )
                return ( 0 );
            else if ( m.size() >= ( size * 2 ) )
                return ( 0 );
            else
            {
                float f = 1 - ( ( m.size() - size ) / size );
                
                return f;
            }
        }
    }
    
    private static Cache< NioMemoryBuffer< ? >> cache = new Cache< NioMemoryBuffer< ? >>( "NioMemoryBuffers", false );
    private byte[] byteBuffer = null;
    private FloatBuffer floatBuffer = null;
    private ByteBuffer bBuffer = null;
    private IntBuffer intBuffer = null;
    
    private NioMemoryBuffer( int size )
    {
        this.byteBuffer = new byte[ size ];
        this.bBuffer = ByteBuffer.wrap( byteBuffer );
        this.intBuffer = bBuffer.asIntBuffer();
        this.floatBuffer = bBuffer.asFloatBuffer();
        
        X3DLog.debug( "NioMemoryBuffer : ", size );
        X3DLog.debug( "  byteBuffer size = ", byteBuffer.length );
        X3DLog.debug( "  bBuffer capacity = ", bBuffer.capacity() );
        X3DLog.debug( "  intBuffer capacity = ", intBuffer.capacity() );
        X3DLog.debug( "  floatBuffer capacity = ", floatBuffer.capacity() );
    }
    
    @SuppressWarnings( "unchecked" )
    public static NioMemoryBuffer< ? > getInstance( int size )
    {
        //return new NioMemoryBuffer(size);
        NioMemoryBuffer< ? > m = cache.getBest( new Matcher( size ) );
        
        if ( m != null )
            return ( m );
        
        return ( new NioMemoryBuffer( size ) );
    }
    
    public void returnInstance()
    {
        cache.put( this );
    }
    
    public int size()
    {
        return ( byteBuffer.length  );
    }
    
    public byte[] getByteArray()
    {
        return ( byteBuffer );
    }
    
    public FloatBuffer getFloatBuffer()
    {
        return ( floatBuffer );
    }
    
    public IntBuffer getIntBuffer()
    {
        return ( intBuffer );
    }
    
    /**
     * @return an approximate amount of memory being used by the cached item.
     */
    public long memoryUsed()
    {
        return ( byteBuffer.length  );
    }
    
    /**
     * Called if the cache determines that the object should be flushed through
     * non-use.  This should only be called be the caching system if check in and
     * check out are strictly managed.  The the implementation does nothing in this
     * function then the object will be garbaged collected.
     * 
     * @param o
     */
    public void flush( T o )
    {
    }
    
    /**
     * @return the name of the cached object. Returning NULL is acceptable. This
     * is primarily used for reporting and tracking cached items.
     */
    public String getName()
    {
        return ( "NioMemoryBuffer" );
    }
}
