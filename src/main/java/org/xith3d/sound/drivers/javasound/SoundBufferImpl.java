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
package org.xith3d.sound.drivers.javasound;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.xith3d.sound.BufferFormat;
import org.xith3d.sound.SoundBuffer;
import org.xith3d.utility.logging.X3DLog;

/**
 * JavaSound implementation of the SoundBuffer.
 * 
 * @author David Yazel
 */
public class SoundBufferImpl implements SoundBuffer
{
    private DataLine.Info info;
    protected AudioFormat af;
    protected byte data[];
    protected int size;
    
    public SoundBufferImpl( SoundDriverImpl driver )
    {
    }
    
    public void setData( BufferFormat format, int size, int frequency, ByteBuffer data )
    {
        X3DLog.debug( "buffer is size=", size, ", freq=", frequency, ", format", format );
        
        int bits = 16;
        int channels = 1;
        boolean signed = true;
        
        if ( format == BufferFormat.MONO8 )
        {
            bits = 8;
            channels = 1;
            signed = false;
        }
        else if ( format == BufferFormat.MONO16 )
        {
            bits = 16;
            channels = 1;
            signed = true;
        }
        else if ( format == BufferFormat.STEREO8 )
        {
            bits = 8;
            channels = 2;
            signed = false;
        }
        else if ( format == BufferFormat.STEREO16 )
        {
            bits = 16;
            channels = 2;
            signed = true;
        }
        
        af = new AudioFormat( frequency, bits, channels, signed, false );
        info = new DataLine.Info( Clip.class, af, size );
        data.rewind();
        this.data = new byte[ size ];
        data.get( this.data );
        this.size = size;
    }
    
    public DataLine.Info getInfo()
    {
        return ( info );
    }
}
