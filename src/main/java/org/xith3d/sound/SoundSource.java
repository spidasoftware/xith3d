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
 * A Soundsource represents a single source of sound to be played.
 * 
 * @author David Yazel
 */
public interface SoundSource
{
    /**
     * Description: Specify the current Buffer object, which means the head
     * entry in its queue. Using BUFFER with the Source command on a STOPPED or
     * INITIAL Source empties the entire queue, then appends the one Buffer
     * specified.
     * <p>
     * For a PLAYING or PAUSED Source, using the Source command with BUFFER is
     * an INVALID_OPERATION. It can be applied to INITIAL and STOPPED Sources
     * only. Specifying an invalid bufferName will result in an INVALID_VALUE
     * error while specifying an invalid sourceName results in an INVALID_NAME
     * error.
     */
    void queueBuffer( SoundBuffer buffer );
    
    /**
     * An alternate to explicitly providing the buffers via the queueBuffer
     * method is to provide a sound data container which will generate the data
     * needed to create the buffers. All the loaders provided in
     * xith3d.sound.loaders create SoundDataContainers. Setting a sound
     * container is the only way to play streaming sounds as the sound manager
     * will make sure to continue to request sounds from the container.
     * 
     * @param container
     */
    void setContainer( SoundContainer container );
    
    void play();
    
    void pause();
    
    void rewind();
    
    void stop();
    
    boolean isPlaying();
    
    void setPosition( float posX, float posY, float posZ );
    
    void setPosition( Tuple3f position );
    
    void setVelocity( float veloX, float veloY, float veloZ );
    
    void setVelocity( Tuple3f velocity );
    
    /**
     * Description: If DIRECTION does not equal the zero vector, the Source is
     * directional. The sound emission is presumed to be symmetric around the
     * direction vector (cylinder symmetry). Sources are not oriented in full 3
     * degrees of freedom, only two angles are effectively needed.
     * 
     * The zero vector is default, indicating that a Source is not directional.
     * Specifying a non-zero vector will make the Source directional. Specifying
     * a zero vector for a directional Source will effectively mark it as
     * nondirectional.
     */
    void setDirection( float dirX, float dirY, float dirZ );
    
    /**
     * Description: If DIRECTION does not equal the zero vector, the Source is
     * directional. The sound emission is presumed to be symmetric around the
     * direction vector (cylinder symmetry). Sources are not oriented in full 3
     * degrees of freedom, only two angles are effectively needed.
     * 
     * The zero vector is default, indicating that a Source is not directional.
     * Specifying a non-zero vector will make the Source directional. Specifying
     * a zero vector for a directional Source will effectively mark it as
     * nondirectional.
     */
    void setDirection( Tuple3f direction );
    
    SoundState getState();
    
    void setVolume( float gain );
    
    /**
     * REFERENCE_DISTANCE is the distance at which the Listener will experience
     * GAIN (unless the implementation had to clamp effective GAIN to the
     * available dynamic range).
     * 
     * @param refDistance
     */
    void setReferenceDistance( float refDistance );
    
    /**
     * ROLLOFF_FACTOR is per-Source parameter the application can use to
     * increase or decrease the range of a source by decreasing or increasing
     * the attenuation, respectively. The default value is 1. The implementation
     * is free to optimize for a ROLLOFF_FACTOR value of 0, which indicates that
     * the application does not wish any distance attenuation on the respective
     * Source.
     * 
     * @param factor
     */
    void setRolloffFactor( float factor );
    
    /**
     * SOURCE_RELATIVE set to TRUE indicates that the values specified by
     * POSITION are to be interpreted relative to the listener position
     * 
     * @param relative
     */
    void setRelative( boolean relative );
    
    /**
     * LOOPING is a flag that indicates that the Source will not be in STOPPED
     * state once it reaches the end of last buffer in the buffer queue.
     * Instead, the Source will immediately promote to INITIAL and PLAYING. The
     * default value is FALSE. LOOPING can be changed on a Source in any
     * execution state. In particular, it can be changed on a PLAYING Source
     * 
     * @param loop
     */
    void setLoop( boolean loop );
    
    void setMaxVolume( float maxVolume );
    
    void setMinVolume( float minVolume );
    
    void setMaxDistance( float maxDistance );
    
    void setMaxTime( long ms );
    
    SoundDriver getSoundDriver();
}
