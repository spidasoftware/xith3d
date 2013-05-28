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

import java.util.ArrayList;
import java.util.List;

import org.openmali.vecmath2.Tuple3f;

import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundDriver;
import org.xith3d.sound.SoundException;
import org.xith3d.sound.SoundSource;
import org.xith3d.utility.logging.X3DLog;

/**
 * JavaSound sound driver implementation.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class SoundDriverImpl implements SoundDriver
{
    private List< SoundSource > sources;
    private List< SoundSource > availableSources;
    
    private boolean isOnline;
    
    /**
     * {@inheritDoc}
     */
    public void newFrameSync()
    {
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
    }
    
    /**
     * {@inheritDoc}
     */
    public void setListenerPosition( Tuple3f position )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void setListenerOrientation( Tuple3f direction, Tuple3f up )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void setListenerVolume( float gain )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public float getListenerVolume()
    {
        return ( 1.0f );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDopplerVelocity( float velocity )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public float getDopplerVelocity()
    {
        return ( 1.0f );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDopplerFactor( float factor )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public float getDopplerFactor()
    {
        return ( 0 );
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
        
        for ( int j = 0; j < sources.size(); j++ )
        {
            SoundSourceImpl ss = (SoundSourceImpl)sources.get( j );
            ss.close();
        }
        
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
        sources = new ArrayList< SoundSource >();
        availableSources = new ArrayList< SoundSource >();
        
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
        
        X3DLog.debug( "JavaSound sound driver initialized with ", availableSources.size(), " available sources" );
    }
}
