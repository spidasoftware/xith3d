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
package org.xith3d.io;

import java.io.IOException;
import java.util.Random;

/**
 * This is a simple test object which creates a buffer of random data. When the
 * data is saved some checksums are stored so that when the data is read back we
 * can determine that it is exactly correct. This class is designed to test the
 * archives.
 * 
 * @author David Yazel
 */
public class TestScribable implements Scribable
{
    private byte[] buffer;
    private long checksum;
    private int size;
    
    public long getCheckSum()
    {
        return ( checksum );
    }
    
    public void save( ScribeOutputStream out ) throws IOException
    {
        out.writeInt( size );
        out.write( buffer );
        out.writeLong( checksum );
    }
    
    public void load( ScribeInputStream in ) throws IOException
    {
        size = in.readInt();
        buffer = new byte[ size ];
        in.read( buffer );
        checksum = in.readLong();
        
        long cs = 0;
        
        for ( int i = 0; i < size; i++ )
        {
            cs += buffer[ i ];
        }
        
        if ( cs != checksum )
        {
            throw new Error( "corrupted scribable object" );
        }
    }
    
    public TestScribable( int size ) throws IOException
    {
        buffer = new byte[ size ];
        this.size = size;
        
        Random r = new Random( 1289428 );
        checksum = 0;
        
        for ( int i = 0; i < size; i++ )
        {
            buffer[ i ] = (byte)r.nextInt( 255 );
            checksum += buffer[ i ];
        }
    }
    
    public TestScribable()
    {
    }
}
