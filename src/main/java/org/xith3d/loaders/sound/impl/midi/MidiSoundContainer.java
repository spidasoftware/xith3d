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
package org.xith3d.loaders.sound.impl.midi;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import org.xith3d.sound.SingletonSoundContainer;
import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundDriver;

/**
 * SoundContainer implementation for Wave sounds.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MidiSoundContainer implements SingletonSoundContainer
{
    private Sequencer sequencer;
    
    /**
     * {@inheritDoc}
     */
    public void setEnabled( boolean enabled )
    {
        if ( enabled && !sequencer.isRunning() )
        {
            sequencer.start();
        }
        else if ( !enabled && sequencer.isRunning() )
        {
            sequencer.stop();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPaused( boolean paused )
    {
        setEnabled( !paused );
    }
    
    /**
     * {@inheritDoc}
     */
    public long getDuration()
    {
        return ( sequencer.getMicrosecondLength() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize()
    {
        // Close the MidiDevice & free resources
        sequencer.stop();
        sequencer.close();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isStreaming()
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public SoundBuffer getData( SoundDriver driver )
    {
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    public void returnData( SoundDriver driver, SoundBuffer buffer )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void rewind( SoundDriver driver )
    {
    }
    
    private MidiSoundContainer( Sequencer sequencer )
    {
        this.sequencer = sequencer;
        sequencer.start();
    }
    
    static MidiSoundContainer load( InputStream inputStream ) throws MidiUnavailableException, InvalidMidiDataException, IOException
    {
        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.setSequence( MidiSystem.getSequence( inputStream ) );
        sequencer.open();
        
        return ( new MidiSoundContainer( sequencer ) );
    }
}
