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
package org.xith3d.loaders.sound.impl.wav;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.xith3d.loaders.sound.SoundLoader;
import org.xith3d.sound.BufferFormat;

/**
 * This is a SoundLoader implementation for Wave sounds (.wav).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class WaveLoader extends SoundLoader
{
    /**
     * The default extension to assume for Wave files.
     */
    public static final String DEFAULT_EXTENSION = "wav";
    
    private static WaveLoader singletonInstance = null;
    
    /**
     * @return the WaveLoader instance to use as singleton.
     */
    public static WaveLoader getInstance()
    {
        if ( singletonInstance == null )
            singletonInstance = new WaveLoader();
        
        return ( singletonInstance );
    }
    
    /**
     * This method loads audio data into a WAVData object.
     * 
     * @author Athomas Goldberg
     * @author Yuri Vl. Gushchin
     * 
     * @param aIn AudioInputStream that contains audio data to load
     * @return a WAVData object containing the audio data
     * @throws UnsupportedAudioFileException if the format of the audio if not
     *             supported.
     * @throws IOException If the file can no be found or some other IO error
     *             occurs
     */
    private static WaveData loadFromAudioInputStream( AudioInputStream aIn ) throws UnsupportedAudioFileException, IOException
    {
        WaveData result = null;
        ReadableByteChannel aChannel = Channels.newChannel( aIn );
        AudioFormat fmt = aIn.getFormat();
        int numChannels = fmt.getChannels();
        int bits = fmt.getSampleSizeInBits();
        
        BufferFormat format = null;
        try
        {
            format = BufferFormat.getFromValues( bits, numChannels );
        }
        catch ( IllegalArgumentException e )
        {
            e.printStackTrace();
            format = BufferFormat.MONO8;
        }
        
        int freq = Math.round( fmt.getSampleRate() );
        int size = aIn.available();
        ByteBuffer buffer = ByteBuffer.allocateDirect( size );
        aChannel.read( buffer );
        result = new WaveData( buffer, format, size, freq, false );
        aIn.close();
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public WaveSoundContainer loadSound( InputStream in ) throws IOException
    {
        try
        {
            WaveData wd = loadFromAudioInputStream( AudioSystem.getAudioInputStream( in ) );
            
            WaveSoundContainer container = new WaveSoundContainer( wd );
            
            return ( container );
        }
        catch ( UnsupportedAudioFileException e )
        {
            IOException e2 = new IOException();
            e2.initCause( e );
            throw e2;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public WaveSoundContainer loadSound( URL url ) throws IOException
    {
        try
        {
            WaveData wd = loadFromAudioInputStream( AudioSystem.getAudioInputStream( url ) );
            
            WaveSoundContainer container = new WaveSoundContainer( wd );
            
            return ( container );
        }
        catch ( UnsupportedAudioFileException e )
        {
            IOException e2 = new IOException();
            e2.initCause( e );
            throw e2;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public WaveSoundContainer loadSound( String filename ) throws IOException
    {
        try
        {
            WaveData wd = loadFromAudioInputStream( AudioSystem.getAudioInputStream( new File( filename ) ) );
            
            WaveSoundContainer container = new WaveSoundContainer( wd );
            
            return ( container );
        }
        catch ( UnsupportedAudioFileException e )
        {
            IOException e2 = new IOException();
            e2.initCause( e );
            throw e2;
        }
    }
}
