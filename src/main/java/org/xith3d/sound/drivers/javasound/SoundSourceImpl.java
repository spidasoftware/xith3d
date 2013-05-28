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

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundContainer;
import org.xith3d.sound.SoundDriver;
import org.xith3d.sound.SoundSource;
import org.xith3d.sound.SoundState;

/**
 * JavaSound implementation of the SoundSource.
 * 
 * @author David Yazel
 */
public class SoundSourceImpl implements SoundSource
{
    private List< SoundBuffer > queue = new ArrayList< SoundBuffer >();
    private Clip clip;
    private SoundDriver driver;
    private SoundState state;
    private Point3f position = new Point3f();
    private int loopCount = 0;
    
    public SoundSourceImpl( SoundDriver driver )
    {
        this.driver = driver;
        state = SoundState.INITIAL;
    }
    
    public Mixer getMixer()
    {
        // TODO (yvg): Select compatible non-buggy Mixer (if we found one)
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for ( int i = 0; i < mixers.length; i++ )
        {
            if ( mixers[ i ].getName().equals( "Java Sound Audio Engine" ) )
            {
                return ( AudioSystem.getMixer( mixers[ i ] ) );
            }
        }
        
        return ( null );
    }
    
    public void setBuffer( SoundBuffer buffer )
    {
        try
        {
            SoundBufferImpl sb = (SoundBufferImpl)buffer;
            Mixer mix = getMixer();
            // Use selected (compatible) Mixer, or fall back to default system Mixer
            // if no compatible one found
            if ( mix != null )
                clip = (Clip)mix.getLine( ( (SoundBufferImpl)buffer ).getInfo() );
            else
                clip = (Clip)AudioSystem.getLine( ( (SoundBufferImpl)buffer ).getInfo() );
            clip.open( sb.af, sb.data, 0, sb.size );
        }
        catch ( LineUnavailableException e )
        {
            e.printStackTrace(); // To change body of catch statement use
            // Options | File Templates.
            throw new Error( e );
        }
    }
    
    public void close()
    {
        if ( clip != null )
        {
            clip.close();
            clip = null;
        }
        
        state = SoundState.INITIAL;
    }
    
    public void queueBuffer( SoundBuffer buffer )
    {
        queue.add( buffer );
    }
    
    public void setContainer( SoundContainer container )
    {
        SoundBuffer sb = container.getData( driver );
        
        if ( sb != null )
        {
            setBuffer( sb );
        }
    }
    
    public void play()
    {
        if ( clip == null )
        {
            if ( queue.size() > 0 )
            {
                SoundBufferImpl sb = (SoundBufferImpl)queue.remove( 0 );
                setBuffer( sb );
            }
            else
                return;
        }
        // Clip.loop() also starts clip playback
        // clip.start();
        clip.loop( loopCount );
        state = SoundState.PLAYING;
    }
    
    public void pause()
    {
        if ( clip != null )
            clip.stop();
        
        state = SoundState.PAUSED;
    }
    
    public void rewind()
    {
        if ( clip != null )
            clip.setFramePosition( 0 );
    }
    
    public void stop()
    {
        if ( clip != null )
            clip.stop();
        
        state = SoundState.STOPPED;
    }
    
    public boolean isPlaying()
    {
        return ( getState() == SoundState.PLAYING );
    }
    
    public void setPosition( float posX, float posY, float posZ )
    {
        this.position.set( posX, posY, posZ );
    }
    
    public void setPosition( Tuple3f position )
    {
        this.position.set( position );
    }
    
    public void setVelocity( float veloX, float veloY, float veloZ )
    {
    }
    
    public void setVelocity( Tuple3f velocity )
    {
    }
    
    public void setDirection( float dirX, float dirY, float dirZ )
    {
    }
    
    public void setDirection( Tuple3f direction )
    {
    }
    
    public SoundState getState()
    {
        return ( state );
    }
    
    public void setVolume( float gain )
    {
    }
    
    public void setReferenceDistance( float refDistance )
    {
    }
    
    public void setRolloffFactor( float factor )
    {
    }
    
    public void setRelative( boolean relative )
    {
    }
    
    public void setLoop( boolean loop )
    {
        if ( clip != null )
        {
            loopCount = ( loop ? Clip.LOOP_CONTINUOUSLY : 0 );
            // Defer setting of clip loop because of loop also starts playback
            // in JavaSound
            if ( isPlaying() )
                clip.loop( loopCount );
        }
    }
    
    public void setMaxVolume( float maxVolume )
    {
    }
    
    public void setMinVolume( float minVolume )
    {
    }
    
    public void setMaxDistance( float maxDistance )
    {
    }
    
    public void setMaxTime( long ms )
    {
    }
    
    public SoundDriver getSoundDriver()
    {
        return ( driver );
    }
}
