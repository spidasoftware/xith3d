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
import java.util.HashMap;
import java.util.Map;

import org.xith3d.loaders.sound.impl.midi.MidiLoader;
import org.xith3d.loaders.sound.impl.ogg.OggLoader;
import org.xith3d.loaders.sound.impl.wav.WaveLoader;
import org.xith3d.sound.SoundContainer;

/**
 * This SoundLoader utilizes the SoundLoader implementation, that is assotiated
 * with the requested Sound resource's extension.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ExtensionSoundLoader extends SoundLoader
{
    private static ExtensionSoundLoader singletonInstance = null;
    
    /**
     * @return the ExtensionSoundLoader instance to use as singleton.
     */
    public static ExtensionSoundLoader getInstance()
    {
        if ( singletonInstance == null )
            singletonInstance = new ExtensionSoundLoader();
        
        return ( singletonInstance );
    }
    
    private Map< String, SoundLoader > extensionMap;
    
    /**
     * Sets the Map, that maps extensions to SoundLoaders.
     */
    public void setExtensionMap( Map< String, SoundLoader > extensionMap )
    {
        this.extensionMap = extensionMap;
    }
    
    /**
     * @return the Map, that maps extensions to SoundLoaders.
     */
    public Map< String, SoundLoader > getExtensionMap()
    {
        return ( extensionMap );
    }
    
    /**
     * Maps the lowercase of the extension to the given SoundLoader.
     * 
     * @param extension
     * @param loader
     */
    public void mapExtension( String extension, SoundLoader loader )
    {
        extensionMap.put( extension.toLowerCase(), loader );
    }
    
    /**
     * @param extension
     * 
     * @return the SoundLoader mapped to the lowercase of the given extension.
     */
    public SoundLoader getMappedSoundLoader( String extension )
    {
        return ( extensionMap.get( extension.toLowerCase() ) );
    }
    
    private static String getExtension( String resource )
    {
        if ( ( resource == null ) || ( ( ( !resource.equals( "." ) ) && ( resource.length() < 2 ) ) ) )
            throw new IllegalArgumentException( "Illegal Sound resource \"" + resource + "\"" );
        
        final int lastDotPos = resource.lastIndexOf( '.' );
        if ( lastDotPos >= 0 )
            return ( resource.substring( lastDotPos + 1 ) );
        
        return ( resource );
    }
    
    /**
     * This method loads the Sound from an InputStream.
     * 
     * @param in the InputStream to load the Sound from. It isn't necessary
     *           to explicitly pass an instance of BufferedInputStream, since it is created on demand.
     */
    @Override
    public SoundContainer loadSound( InputStream in ) throws IOException
    {
        throw new UnsupportedOperationException( "You cannot load directly from an InputStream with the ExtensionSoundLoader." );
    }
    
    /**
     * This method loads the Sound from a URL.
     * 
     * @param url the URL to load the Sound from.
     */
    @Override
    public SoundContainer loadSound( URL url ) throws IOException
    {
        SoundLoader loader = getMappedSoundLoader( getExtension( url.toString() ) );
        
        if ( loader == null )
            throw new UnsupportedOperationException( "There's no (registered) SoundLoader implementation for \"" + getExtension( url.toString() ) + "\"" );
        
        return ( loader.loadSound( url ) );
    }
    
    /**
     * This method loads the Scene from a file.
     * 
     * @param filename the file's name to load the Sound from.
     */
    @Override
    public SoundContainer loadSound( String filename ) throws IOException
    {
        SoundLoader loader = getMappedSoundLoader( getExtension( filename ) );
        
        if ( loader == null )
            throw new UnsupportedOperationException( "There's no (registered) SoundLoader implementation for \"" + getExtension( filename ) + "\"" );
        
        return ( loader.loadSound( filename ) );
    }
    
    public ExtensionSoundLoader( Map< String, SoundLoader > extensionMap )
    {
        setExtensionMap( extensionMap );
    }
    
    private static Map< String, SoundLoader > createDefaultExtMap()
    {
        Map< String, SoundLoader > extensionMap = new HashMap< String, SoundLoader >();
        
        extensionMap.put( WaveLoader.DEFAULT_EXTENSION, WaveLoader.getInstance() );
        extensionMap.put( OggLoader.DEFAULT_EXTENSION, OggLoader.getInstance() );
        extensionMap.put( MidiLoader.DEFAULT_EXTENSION, MidiLoader.getInstance() );
        
        return ( extensionMap );
    }
    
    public ExtensionSoundLoader()
    {
        this( createDefaultExtMap() );
    }
}
