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
 * A Buffer encapsulates state related to storing sample data. The application can
 * request and release Buffer objects, and fill them with data. Data can be supplied
 * compressed and encoded as long as the format is supported. Buffers can, internally,
 * contain waveform data as uncompressed or compressed samples,
 *
 * Unlike Sources and Listener, Buffer Objects can be shared among contexts. Buffers are
 * referenced by Sources. A single Buffer can be referred to by multiple Sources. This
 * separation allows driver and hardware to optimize storage and processing where applicable.
 *
 * The simplest supported format for buffer data is PCM.
 * The Buffer state is dependent on the state of all Sources that is has been
 * queued for. A single queue occurrence of a Buffer propagates the Buffer state
 * (over all Sources) from UNUSED to PROCESSED or higher. Sources that are STOPPED or
 * INITIAL still have queue entries that cause Buffers to be PROCESSED.
 *
 * A single queue entry with a single Source for which the Buffer is not yet
 * PROCESSED propagates the buffer's queueing state to PENDING.
 *
 * Buffers that are PROCESSED for a given Source can be unqueued from that
 * Source's queue. Buffers that have been unqueued from all Sources are UNUSED.
 * Buffers that are UNUSED can be deleted, or changed by BufferData commands.
 * 
 * @author David Yazel
 */
public enum BufferState
{
    /**
     * the Buffer is no included in any queue for any Source. In particular, the Buffer is
     * neither pending nor current for any Source. The Buffer name can be deleted at this time.
     */
    UNUSED,
    
    /**
     * the Buffer is listed in the queue of at least one Source, but is neither
     * pending nor current for any Source. The Buffer can be deleted as soon as
     * it has been unqueued for all Sources it is queued with.
     */
    PROCESSED,
    
    /**
     * there is at least one Source for which the Buffer has been queued, for
     * which the Buffer data has not yet been dereferenced. The Buffer can only be
     * unqueued for those Sources which have dereferenced the data in the Buffer in its
     * entirety, and can not be deleted or changed
     */
    PENDING;
}
