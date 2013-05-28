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
package org.xith3d.scenegraph;

import org.xith3d.sound.SoundContainer;

/**
 * PointSounds are those sounds which exist in the point in space within the
 * transform groups it exists. It is assumed that the sound exists within its
 * local coordinate system at (0, 0, 0).<br>
 * <br>
 * PointSounds have their volume attenuated based on an inverse distance formula
 * which is acoustically similar to that of the human ear. This is not linear,
 * but rather logarithmic in nature.<br>
 * <br>
 * 
 * @author David Yazel
 */
public class PointSound extends Sound
{
    private float minVolume = 0f;
    private float maxVolume = 1f;
    private float maxDistance = -1f;
    private float rolloffFactor = 1f;
    private float referenceDistance = -1f;
    
    /**
     * Sets the minimum volume, even at max distance.
     * 
     * @param minVolume
     */
    public void setMinVolume( float minVolume )
    {
        this.minVolume = minVolume;
    }
    
    /**
     * @return the minimum volume for this sournd, even at max distance
     */
    public final float getMinVolume()
    {
        return ( minVolume );
    }
    
    /**
     * Sets the max volume for this sound.
     * 
     * @param maxVolume
     */
    public void setMaxVolume( float maxVolume )
    {
        this.maxVolume = maxVolume;
    }
    
    /**
     * @return the maximum volume of this sound
     */
    public final float getMaxVolume()
    {
        return ( maxVolume );
    }
    
    /**
     * Sets the max distance before the sound is a min volume. If this is set to
     * -1 then the View's sound activation radius will be used.
     * 
     * @param maxDistance
     */
    public void setMaxDistance( float maxDistance )
    {
        this.maxDistance = maxDistance;
    }
    
    /**
     * @return the max distance before this sound is at min volume
     */
    public final float getMaxDistance()
    {
        return ( maxDistance );
    }
    
    /**
     * The rollover-factor compresses or expands the range this sound can be
     * heard. 0 will make the sound unattenuated (uneffected by distance). 1 is
     * the normal setting. 0.5 will make the sound appear to be twice as close
     * as it really is, while 2 will make it sound twice as far away as it
     * really is.
     */
    public void setRolloffFactor( float rolloffFactor )
    {
        this.rolloffFactor = rolloffFactor;
    }
    
    /**
     * The rollover-factor compresses or expands the range this sound can be
     * heard. 0 will make the sound unattenuated (uneffected by distance). 1 is
     * the normal setting. 0.5 will make the sound appear to be twice as close
     * as it really is, while 2 will make it sound twice as far away as it
     * really is.
     * 
     * @return the current rollover-factor
     */
    public final float getRolloffFactor()
    {
        return ( rolloffFactor );
    }
    
    /**
     * The reference distance is the distance from the view that the sound will
     * become at maximum volume. This is usually good to set within a few units
     * of the view so that you don't have scaling in the last few units. If the
     * reference distance is -1 then the 0.17 of the max distance will be used.
     */
    public void setReferenceDistance( float referenceDistance )
    {
        this.referenceDistance = referenceDistance;
    }
    
    /**
     * The reference distance is the distance from the view that the sound will
     * become at maximum volume. This is usually good to set within a few units
     * of the view so that you don't have scaling in the last few units.
     * 
     * @return the current reference-distance
     */
    public final float getReferenceDistance()
    {
        return ( referenceDistance );
    }
    
    // Constructors
    
    public PointSound( SoundContainer soundContainer, float gain )
    {
        super( soundContainer, gain );
    }
    
    public PointSound( SoundContainer soundContainer )
    {
        this( soundContainer, 0.0f );
    }
}
