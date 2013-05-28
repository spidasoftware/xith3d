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
package org.xith3d.loaders.sound;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xith3d.scenegraph.BackgroundSound;
import org.xith3d.scenegraph.PointSound;
import org.xith3d.sound.SoundContainer;

/**
 * This is a base for other SoundLoader implementations for different formats.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SoundLoader
{
    private static SoundLoader singletonInstance = null;
    
    /**
     * @return the ExtensionSoundLoader instance to use as singleton.
     */
    public static SoundLoader getInstance()
    {
        if ( singletonInstance == null )
            singletonInstance = new ExtensionSoundLoader();
        
        return ( singletonInstance );
    }
    
    /**
     * This method loads the Sound from an InputStream.
     * 
     * @param in the InputStream to load the Sound from. It isn't necessary
     *           to explicitly pass an instance of BufferedInputStream, since it is created on demand.
     */
    public abstract SoundContainer loadSound( InputStream in ) throws IOException;
    
    /**
     * This method loads the Sound from a URL.
     * 
     * @param url the URL to load the Sound from.
     */
    public abstract SoundContainer loadSound( URL url ) throws IOException;
    
    /**
     * This method loads the Sound from a file.
     * 
     * @param filename the file's name to load the Sound from.
     */
    public abstract SoundContainer loadSound( String filename ) throws IOException;
    
    /**
     * This method loads a PointSound from an InputStream.
     * 
     * @param in the InputStream to load the Sound from. It isn't necessary
     *           to explicitly pass an instance of BufferedInputStream, since it is created on demand.
     */
    public PointSound loadPointSound( InputStream in, float gain ) throws IOException
    {
        SoundContainer container = loadSound( in );
        
        return ( new PointSound( container, gain ) );
    }
    
    /**
     * This method loads a PointSound from a URL.
     * 
     * @param url the URL to load the Sound from.
     */
    public PointSound loadPointSound( URL url, float gain ) throws IOException
    {
        SoundContainer container = loadSound( url );
        
        return ( new PointSound( container, gain ) );
    }
    
    /**
     * This method loads a PointSound from a file.
     * 
     * @param filename the file's name to load the Sound from.
     */
    public PointSound loadPointSound( String filename, float gain ) throws IOException
    {
        SoundContainer container = loadSound( filename );
        
        return ( new PointSound( container, gain ) );
    }
    
    /**
     * This method loads a BackgroundSound from an InputStream.
     * 
     * @param in the InputStream to load the Sound from. It isn't necessary
     *           to explicitly pass an instance of BufferedInputStream, since it is created on demand.
     */
    public BackgroundSound loadBackgroundSound( InputStream in, float gain ) throws IOException
    {
        SoundContainer container = loadSound( in );
        
        return ( new BackgroundSound( container, gain ) );
    }
    
    /**
     * This method loads a BackgroundSound from a URL.
     * 
     * @param url the URL to load the Sound from.
     */
    public BackgroundSound loadBackgroundSound( URL url, float gain ) throws IOException
    {
        SoundContainer container = loadSound( url );
        
        return ( new BackgroundSound( container, gain ) );
    }
    
    /**
     * This method loads a BackgroundSound from a file.
     * 
     * @param filename the file's name to load the Sound from.
     */
    public BackgroundSound loadBackgroundSound( String filename, float gain ) throws IOException
    {
        SoundContainer container = loadSound( filename );
        
        return ( new BackgroundSound( container, gain ) );
    }
}
