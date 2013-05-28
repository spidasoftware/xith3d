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

import java.util.ArrayList;
import java.util.List;

import org.openmali.vecmath2.Tuple3f;

import net.java.games.joal.AL;
import net.java.games.joal.ALC;
import net.java.games.joal.ALCcontext;
import net.java.games.joal.ALCdevice;
import net.java.games.joal.ALFactory;

import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundDriver;
import org.xith3d.sound.SoundException;
import org.xith3d.sound.SoundSource;
import org.xith3d.utility.logging.X3DLog;

/**
 * Sound Driver Implementation for JOAL (Java Open Audio Library, OpenAL
 * binding).
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class SoundDriverImpl implements SoundDriver
{
    private AL al;
    private ALC alc;
    
    private List< SoundSource > sources;
    private List< SoundBufferImpl > buffers;
    private ALCdevice device;
    private ALCcontext context;
    private List< SoundSource > availableSources;
    
    private float listenerVolume = 1.0f;
    private float dopplerVelocity = 1.0f;
    private float dopplerFactor = 0.0f;
    
    private boolean isOnline;
    
    protected AL getAL()
    {
        return ( al );
    }
    
    protected String decodeSoundError( int error )
    {
        switch ( error )
        {
            case AL.AL_NO_ERROR:
                return ( "NO ERROR" );
            case AL.AL_INVALID_ENUM:
                return ( "INVALID ENUM" );
            case AL.AL_INVALID_VALUE:
                return ( "INVALID VALUE" );
            case AL.AL_INVALID_NAME:
                return ( "INVALID NAME" );
            case AL.AL_INVALID_OPERATION:
                return ( "INVALID OPERATION" );
            case AL.AL_OUT_OF_MEMORY:
                return ( "OUT OF MEMORY" );
            default:
                return ( "UNKNOWN ERROR" );
        }
    }
    
    protected void checkError()
    {
        int error = getAL().alGetError();
        if ( error != AL.AL_NO_ERROR )
            throw new Error( decodeSoundError( error ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public void newFrameSync()
    {
        // step through and remove all the processed buffers
        
        int sourceList[] = new int[ sources.size() ];
        for ( int i = 0; i < sourceList.length; i++ )
        {
            SoundSourceImpl ss = (SoundSourceImpl)sources.get( i );
            sourceList[ i ] = ss.handle;
            ss.stop();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void newFrameAsync()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void setListenerVelocity( Tuple3f velocity )
    {
        al.alListener3f( AL.AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ() );
        checkError();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setListenerPosition( Tuple3f position )
    {
        al.alListenerfv( AL.AL_POSITION,
                         new float[]
                         {
                             position.getX(),
                             position.getY(),
                             position.getZ()
                         },
                         0
                       );
        checkError();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setListenerOrientation( Tuple3f direction, Tuple3f up )
    {
        al.alListenerfv( AL.AL_ORIENTATION,
                         new float[]
                         {
                             direction.getX(),
                             direction.getY(),
                             direction.getZ(),
                             up.getX(),
                             up.getY(),
                             up.getZ()
                         },
                         0
                       );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setListenerVolume( float gain )
    {
        al.alListenerf( AL.AL_GAIN, gain );
        checkError();
        
        this.listenerVolume = gain;
    }
    
    /**
     * {@inheritDoc}
     */
    public float getListenerVolume()
    {
        return ( listenerVolume );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDopplerVelocity( float velocity )
    {
        al.alDopplerVelocity( velocity );
        checkError();
        
        this.dopplerVelocity = velocity;
    }
    
    /**
     * {@inheritDoc}
     */
    public float getDopplerVelocity()
    {
        return ( dopplerVelocity );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDopplerFactor( float factor )
    {
        al.alDopplerFactor( factor );
        
        this.dopplerFactor = factor;
    }
    
    /**
     * {@inheritDoc}
     */
    public float getDopplerFactor()
    {
        return ( dopplerFactor );
    }
    
    /**
     * {@inheritDoc}
     */
    public SoundSource allocateSoundSource() throws SoundException
    {
        if ( availableSources.size() == 0 )
            throw new SoundException( "no sound sources available" );
        
        SoundSource s = availableSources.remove( availableSources.size() - 1 );
        sources.add( s );
        
        return ( s );
    }
    
    /**
     * {@inheritDoc}
     */
    public SoundBuffer allocateSoundBuffer()
    {
        return ( new SoundBufferImpl( this ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public void delete( SoundSource source )
    {
        sources.remove( source );
        ( (SoundSourceImpl)source ).releaseCachedResources();
        availableSources.add( source );
    }
    
    /**
     * {@inheritDoc}
     */
    public void delete( SoundBuffer buffer )
    {
        // TODO
    }
    
    /**
     * {@inheritDoc}
     */
    public int getNumAvailableSources()
    {
        return ( availableSources.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    public int getNumSources()
    {
        return ( sources.size() + availableSources.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown()
    {
        if ( !isOnline() )
        {
            return;
        }
        
        // stop all the sources
        
        int[] sourceList = new int[ sources.size() + availableSources.size() ];
        int i = 0;
        for ( int j = 0; j < sources.size(); j++ )
        {
            SoundSourceImpl ss = (SoundSourceImpl)sources.get( j );
            sourceList[ i++ ] = ss.handle;
            ss.stop();
        }
        
        for ( int j = 0; j < availableSources.size(); j++ )
        {
            SoundSourceImpl ss = (SoundSourceImpl)availableSources.get( j );
            sourceList[ i++ ] = ss.handle;
            ss.stop();
        }
        
        // destroy the sources
        
        if ( sourceList.length > 0 )
        {
            al.alDeleteSources( sourceList.length, sourceList, 0 );
            checkError();
        }
        
        // delete all the buffers
        
        int[] bufferList = new int[ buffers.size() ];
        for ( int j = 0; i < bufferList.length; j++ )
        {
            SoundBufferImpl ss = buffers.get( j );
            bufferList[ j ] = ss.handle;
        }
        
        if ( bufferList.length > 0 )
        {
            al.alDeleteBuffers( bufferList.length, bufferList, 0 );
            checkError();
        }
        
        X3DLog.debug( "Making sound context current" );
        alc.alcMakeContextCurrent( context );
        X3DLog.debug( "Destroying context" );
        alc.alcDestroyContext( context );
        X3DLog.debug( "Closing device" );
        alc.alcCloseDevice( device );
        
        this.isOnline = false;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isOnline()
    {
        return ( isOnline );
    }
    
    public SoundDriverImpl()
    {
        try
        {
            al = ALFactory.getAL();
            alc = ALFactory.getALC();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        
        try
        {
            device = alc.alcOpenDevice( System.getProperty( "XITH3D_OPENAL_DEVICE" ) );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        if ( device == null )
            throw new Error( "No sound device found" );
        
        context = alc.alcCreateContext( device, null );
        alc.alcMakeContextCurrent( context );
        checkError();
        
        sources = new ArrayList< SoundSource >();
        buffers = new ArrayList< SoundBufferImpl >();
        availableSources = new ArrayList< SoundSource >();
        
        al.alDistanceModel( AL.AL_INVERSE_DISTANCE );
        checkError();
        
        // allocate all available sound sources
        
        boolean done = false;
        int n = 0;
        while ( !done )
        {
            try
            {
                SoundSourceImpl ss = new SoundSourceImpl( this );
                availableSources.add( ss );
                if ( n++ == 60 )
                    break;
            }
            catch ( Error e )
            {
                X3DLog.print( e );
                done = true;
            }
        }
        setListenerVolume( 1.0f );
        
        this.isOnline = true;
        
        X3DLog.debug( "OpenAL sound driver initialized with ", availableSources.size(), " available sources" );
    }
}
