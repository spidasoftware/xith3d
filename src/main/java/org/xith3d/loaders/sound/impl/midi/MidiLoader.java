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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xith3d.loaders.sound.SoundLoader;

/**
 * This is a SoundLoader implementation for Midi sounds (.mid).
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MidiLoader extends SoundLoader
{
    /**
     * The default extension to assume for Wave files.
     */
    public static final String DEFAULT_EXTENSION = "mid";
    
    private static MidiLoader singletonInstance = null;
    
    /**
     * @return the MidiLoader instance to use as singleton.
     */
    public static MidiLoader getInstance()
    {
        if ( singletonInstance == null )
            singletonInstance = new MidiLoader();
        
        return ( singletonInstance );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MidiSoundContainer loadSound( InputStream in ) throws IOException
    {
        try
        {
            return ( MidiSoundContainer.load( in ) );
        }
        catch ( Exception e )
        {
            IOException e2 = new IOException( e.getMessage() );
            e2.initCause( e );
            
            throw e2;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MidiSoundContainer loadSound( URL url ) throws IOException
    {
        return ( loadSound( url.openStream() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MidiSoundContainer loadSound( String filename ) throws IOException
    {
        return ( loadSound( new BufferedInputStream( new FileInputStream( filename ) ) ) );
    }
}
