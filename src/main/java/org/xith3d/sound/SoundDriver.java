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

import org.openmali.vecmath2.Tuple3f;

/**
 * Implementations of a sound driver must implement this interface. This
 * represents the various capabilities of the sound system.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public interface SoundDriver
{
    /**
     * Call this method once a frame to check and possibly load the next buffer
     * from all the streaming sources, as well as dequeue all processed buffers.
     * This will operate synchronously, so it will not return until the work is
     * complete.
     */
    public void newFrameSync();
    
    /**
     * Call this method once a frame to check and possibly load the next buffer
     * from all the streaming sources, as well as dequeue all processed buffers.
     * This will operate asynchronously and will return immediately. If it is
     * already processing from the last frame then it will skip this frame. The
     * thread used is a high priority thread so that it can complete its task in
     * as little time as possible while still reducing frame stutter. This is
     * because this is mostly I/O bound and will enter wait states, thus freeing
     * CPU for rendering.
     */
    public void newFrameAsync();
    
    public void setListenerVelocity( Tuple3f velocity );
    
    public void setListenerPosition( Tuple3f position );
    
    public void setListenerOrientation( Tuple3f direction, Tuple3f up );
    
    public void setListenerVolume( float gain );
    
    public float getListenerVolume();
    
    public void setDopplerVelocity( float velocity );
    
    public float getDopplerVelocity();
    
    public void setDopplerFactor( float factor );
    
    public float getDopplerFactor();
    
    public SoundSource allocateSoundSource() throws SoundException;
    
    public SoundBuffer allocateSoundBuffer();
    
    public void delete( SoundSource source );
    
    public void delete( SoundBuffer buffer );
    
    public int getNumAvailableSources();
    
    public int getNumSources();
    
    public void shutdown();
    
    public boolean isOnline();
}
