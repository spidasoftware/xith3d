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
 * A sound data container is what acts as a generator for the actual sound data
 * which will be played. Containers load all their data into memory, or they can
 * load the data in chunks. Chunked data is called by the sound driver on
 * demand, allowing for data to be decompressed in a double buffered stream.<br>
 * <br>
 * Important note: Streamable data containers cannot be attached to more than
 * one source at a time. In general you only need to stream the very large files
 * and they would use a fairly small amount of memory.
 * 
 * @author David Yazel
 */
public interface SoundContainer
{
    /**
     * @return if there might be more than one sound buffer available.
     */
    boolean isStreaming();
    
    /**
     * Gets a buffer to be played.
     * 
     * @return The buffer ready to be played. Null indicates there is no more
     *         data to be played.
     */
    SoundBuffer getData( SoundDriver driver );
    
    /**
     * Called by the sound system to indicate that it has finished playing the
     * buffer. In a streaming implementation this is an opportunity to decode the
     * next chunk into this released buffer.
     * 
     * @param buffer
     */
    void returnData( SoundDriver driver, SoundBuffer buffer );
    
    /**
     * Generally only valid for streaming sound containers. Sets up for a call
     * to the getData() method. The next call to getData() should return the
     * first buffer in the sound source.
     * 
     * @param driver
     */
    void rewind( SoundDriver driver );
}
