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
package org.xith3d.sound.drivers.joal;

import org.openmali.vecmath2.Tuple3f;

import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundContainer;
import org.xith3d.sound.SoundDriver;
import org.xith3d.sound.SoundSource;
import org.xith3d.sound.SoundState;
import org.xith3d.utility.logging.X3DLog;

import net.java.games.joal.AL;

/**
 * JOAL implementation of SoundSource.
 * 
 * @author David Yazel
 */
public class SoundSourceImpl implements SoundSource
{
    static int count = 0;
    int handle;
    
    private SoundDriverImpl driver;
    
    public SoundSourceImpl( SoundDriverImpl driver )
    {
        this.driver = driver;
        int[] ret = new int[ 1 ];
        int error = driver.getAL().alGetError();
        driver.getAL().alGenSources( 1, ret, 0 );
        error = driver.getAL().alGetError();
        if ( error != AL.AL_NO_ERROR )
            throw new Error( "no more sources available : " + driver.decodeSoundError( error ) );
        handle = ret[ 0 ];
        X3DLog.debug( "Created new source.. handle = ", handle );
        setRolloffFactor( 0.5f );
        setMaxDistance( 30 );
        setReferenceDistance( 5 );
        setMinVolume( 0 );
        setMaxVolume( 1 );
    }
    
    public void queueBuffer( SoundBuffer buffer )
    {
        driver.getAL().alSourceQueueBuffers( handle, 1, new int[]
        {
            ( (SoundBufferImpl)buffer ).handle
        }, 0 );
        driver.checkError();
    }
    
    public int[] unqueueProcessedBuffers()
    {
        int ret[] = new int[ 1 ];
        driver.getAL().alGetSourcei( handle, AL.AL_BUFFERS_PROCESSED, ret, 0 );
        int num = ret[ 0 ];
        int buffers[] = new int[ num ];
        driver.getAL().alSourceUnqueueBuffers( handle, num, buffers, 0 );
        driver.checkError();
        return ( buffers );
        
    }
    
    public void setBuffer( SoundBuffer buffer )
    {
        driver.getAL().alSourcei( handle, AL.AL_BUFFER, ( (SoundBufferImpl)buffer ).handle );
        driver.checkError();
    }
    
    public void setContainer( SoundContainer container )
    {
        SoundBuffer b = container.getData( driver );
        if ( b != null )
            setBuffer( b );
    }
    
    public void setVolume( float gain )
    {
        driver.getAL().alSourcef( handle, AL.AL_GAIN, gain );
        driver.checkError();
        
    }
    
    public void play()
    {
        driver.getAL().alSourcePlay( handle );
        driver.checkError();
    }
    
    public void pause()
    {
        driver.getAL().alSourcePause( handle );
        driver.checkError();
    }
    
    public void rewind()
    {
        driver.getAL().alSourceRewind( handle );
        driver.checkError();
    }
    
    public void stop()
    {
        driver.getAL().alSourceStop( handle );
        driver.checkError();
    }
    
    public boolean isPlaying()
    {
        return ( getState() == SoundState.PLAYING );
    }
    
    private float[] pos = new float[ 3 ];
    private boolean hasCachedPosition = false;
    
    void releaseCachedResources()
    {
        hasCachedPosition = false;
    }
    
    public void setPosition( float posX, float posY, float posZ )
    {
        if ( hasCachedPosition )
        {
            if ( ( pos[ 0 ] == posX ) && ( pos[ 1 ] == posY ) && ( pos[ 2 ] == posZ ) )
                return;
            
            hasCachedPosition = true;
        }
        
        pos[ 0 ] = posX;
        pos[ 1 ] = posY;
        pos[ 2 ] = posZ;
        driver.getAL().alSourcefv( handle, AL.AL_POSITION, pos, 0 );
        driver.checkError();
        //if (((++count) % 200) == 0) System.out.println( "Position for source" + handle + " is " + position );
    }
    
    public void setPosition( Tuple3f position )
    {
        setPosition( position.getX(), position.getY(), position.getZ() );
    }
    
    public void setVelocity( float veloX, float veloY, float veloZ )
    {
        driver.getAL().alSource3f( handle, AL.AL_VELOCITY, veloX, veloY, veloZ );
        driver.checkError();
    }
    
    public void setVelocity( Tuple3f velocity )
    {
        setVelocity( velocity.getX(), velocity.getY(), velocity.getZ() );
    }
    
    public void setDirection( float dirX, float dirY, float dirZ )
    {
    }
    
    public void setDirection( Tuple3f direction )
    {
    }
    
    public SoundState getState()
    {
        int ret[] = new int[ 1 ];
        driver.getAL().alGetSourcei( handle, AL.AL_SOURCE_STATE, ret, 0 );
        int state = ret[ 0 ];
        switch ( state )
        {
            case AL.AL_PLAYING:
                return ( SoundState.PLAYING  );
            case AL.AL_PAUSED:
                return ( SoundState.PAUSED  );
            case AL.AL_INITIAL:
                return ( SoundState.INITIAL  );
            case AL.AL_STOPPED:
                return ( SoundState.STOPPED  );
            default:
                throw new Error( "Illegal OpenAL state found" );
                
        }
    }
    
    public void setReferenceDistance( float refDistance )
    {
        driver.getAL().alSourcef( handle, AL.AL_REFERENCE_DISTANCE, refDistance );
        driver.checkError();
    }
    
    /**
     * Set to zero if this is an unattenuated sound, 1 would be normal otherwise
     * 
     * @param factor
     */
    public void setRolloffFactor( float factor )
    {
        driver.getAL().alSourcef( handle, AL.AL_ROLLOFF_FACTOR, factor );
        driver.checkError();
        
    }
    
    public void setRelative( boolean relative )
    {
        driver.getAL().alSourcei( handle, AL.AL_SOURCE_RELATIVE, relative ? 1 : 0 );
        driver.checkError();
    }
    
    public void setLoop( boolean loop )
    {
        driver.getAL().alSourcei( handle, AL.AL_LOOPING, loop ? 1 : 0 );
    }
    
    public void setMaxVolume( float maxVolume )
    {
        driver.getAL().alSourcef( handle, AL.AL_MAX_GAIN, maxVolume );
        driver.checkError();
    }
    
    public void setMinVolume( float minVolume )
    {
        driver.getAL().alSourcef( handle, AL.AL_MIN_GAIN, minVolume );
        driver.checkError();
    }
    
    public void setMaxDistance( float maxDistance )
    {
        driver.getAL().alSourcef( handle, AL.AL_MAX_DISTANCE, maxDistance );
        driver.checkError();
    }
    
    public void setMaxTime( long ms )
    {
    }
    
    public SoundDriver getSoundDriver()
    {
        return ( driver );
    }
}
