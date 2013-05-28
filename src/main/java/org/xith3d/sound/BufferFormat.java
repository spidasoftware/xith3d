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
package org.xith3d.sound;

/**
 * This is an abstraction for common sound formats.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public enum BufferFormat
{
    MONO8( 8, 1 ),
    MONO16( 16, 1 ),
    STEREO8( 8, 2 ),
    STEREO16( 8, 2 ),
    ;
    
    private final int bits;
    private final int channels;
    
    /**
     * The number of bits for this format.
     * @return the number of bits for this format.
     */
    public final int getBits()
    {
        return ( bits );
    }
    
    /**
     * The number of channels for this format.
     * @return the number of channels for this format.
     */
    public final int getNumChannels()
    {
        return ( channels );
    }
    
    /**
     * Selects the correct BufferFormat from int values.
     * 
     * @param bits
     * @param channels
     * 
     * @return the selected format.
     */
    public static final BufferFormat getFromValues( int bits, int channels )
    {
        if ( bits == 8 )
        {
            if ( channels == 1 )
                return ( MONO8 );
            
            if ( channels == 2 )
                return ( STEREO8 );
        }
        
        if ( bits == 16 )
        {
            if ( channels == 1 )
                return ( MONO16 );
            
            if ( channels == 2 )
                return ( STEREO16 );
        }
        
        throw new IllegalArgumentException( "Unsupported format (bits = " + bits + ", channels = " + channels + ")" );
    }
    
    private BufferFormat( int bits, int channels )
    {
        this.bits = bits;
        this.channels = channels;
    }
}
